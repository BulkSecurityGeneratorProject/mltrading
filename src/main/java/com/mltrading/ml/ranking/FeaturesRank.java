package com.mltrading.ml.ranking;

import com.mltrading.ml.*;
import com.mltrading.ml.model.ModelType;
import com.mltrading.ml.util.MLMath;
import com.mltrading.models.stock.*;
import com.mltrading.models.stock.cache.CacheStockGeneral;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.mltrading.ml.MlForecast.updatePredictor;

public class FeaturesRank extends Feature implements Serializable{

    private static final Logger log = LoggerFactory.getLogger(FeaturesRank.class);

    private String codif;


    public FeaturesRank() {
        vector = new Double[20000];
    }

    public FeaturesRank(FeaturesRank fs, double pred, PredictionPeriodicity t) {
        this.currentDate = fs.getCurrentDate();
        this.setDate(fs.getDate(t),t);
        setResultValue(fs.getResultValue(PredictionPeriodicity.D1), PredictionPeriodicity.D1);
        setResultValue(fs.getResultValue(PredictionPeriodicity.D5), PredictionPeriodicity.D5);
        setResultValue(fs.getResultValue(PredictionPeriodicity.D20), PredictionPeriodicity.D20);
        setResultValue(fs.getResultValue(PredictionPeriodicity.D40), PredictionPeriodicity.D40);
        this.currentValue = fs.getCurrentValue();
        this.currentVectorPos = fs.currentVectorPos;
        this.vector = fs.vector.clone();
        setPredictionValue(pred, t);
    }

    public FeaturesRank(String codif) {
        this.codif = codif;
        vector = new Double[20000];
    }

    public String getCodif() {
        return codif;
    }

    public void setCodif(String codif) {
        this.codif = codif;
    }



    /**
     * create feature vector for ranking model training
     * @param l => list of stock
     * @param rangeMin value usable
     * @param rangeMax
     * @return FeaturesRank list
     */
    public  static List<FeaturesRank> create(List<StockGeneral> l, int rangeMin, int rangeMax) {
        List<FeaturesRank> frL = new ArrayList<>();

        if (rangeMin < rangeMax) {
            log.error("range born inferrior cannot be less than rangeMax");
            return null;

        }


        for (StockGeneral s : l) {

            String codif = s.getCodif();

            StockGeneral sg = CacheStockGeneral.getCache().get(s.getCode());


            List<String> rangeDate;

            /**
             * to extract data between range without refactoring cache, extract overall and reduce range
             */
            try {
                rangeDate = StockHistory.getDateHistoryListOffsetLimit(codif, rangeMin);
                rangeDate = rangeDate.subList(1, rangeMin - rangeMax);
            } catch (Exception e) {
                return null;
            }

            if (rangeDate == null) continue;

            MLStocks ref = CacheMLStock.getMLStockCache().get(codif);

            if ((ref == null) || ref.isEmtpyModel())
                continue;

            int index = 0;

            for (String date : rangeDate) {
                FeaturesRank fr = filledFeaturesRank(sg, date);
                if (fr == null)
                    continue;

                frL.add(fr);
                if (index > rangeMin)
                    break;
                index ++;
            }
        }

        return  frL;
    }

    public static int DAYS_BY_MONTH = 31;
    public static int DAYS_BY_YEAR = 365;
    private static int check_diff( DateTime timeInsert , DateTime timeNow ) {
        int shift = (timeNow.getYear() - timeInsert.getYear()) * DAYS_BY_YEAR;
        return (timeNow.getDayOfMonth()+timeNow.getMonthOfYear()*DAYS_BY_MONTH + shift) - (timeInsert.getDayOfMonth()+timeInsert.getMonthOfYear()*DAYS_BY_MONTH);
    }


    public void linearize(double value) {
        this.vector[currentVectorPos++] = value;
    }

    public void linearize(boolean value) {
        this.vector[currentVectorPos++] = value ==true ? 1.0 : 0;
    }



    public void linearize(StockHistory sh) {
       this.vector[currentVectorPos++] = sh.getValue();
    }


    private void linearize(StockAnalyse sa) {
        this.vector[currentVectorPos++] = sa.getMma20();
        this.vector[currentVectorPos++] = sa.getMma50();
        this.vector[currentVectorPos++] = sa.getMme12();
        this.vector[currentVectorPos++] = sa.getMme26();
        this.vector[currentVectorPos++] = sa.getMacd();
        this.vector[currentVectorPos++] = sa.getMomentum();
        this.vector[currentVectorPos++] = sa.getStdDev();

    }



    /**
     * Create a FeatureStock list for prediction
     * use each day to refresh data with current model
     * @param date
     * @return
     */
    public static FeaturesRank createRT(String codif, String date) {
        StockGeneral sg = CacheStockGeneral.getCache().get(CacheStockGeneral.getCode(codif));
        return filledFeaturesRank(sg, date);

    }


    private static FeaturesRank filledFeaturesRank(StockGeneral sg, String date) {

        String sector = sg.getSector();



        MLStocks ref = CacheMLStock.getMLStockCache().get(sg.getCodif());

        FeaturesRank fr = new FeaturesRank();
        fr.setCurrentDate(date);

        if (setResultYield(fr, PeriodicityList.periodicityLong, sg.getCodif(), date) == false) return null;
        StockGeneral sgSim = new StockGeneral(StockHistory.getStockHistory(sg.getCodif(), date), sg);
        updatePredictor(sgSim,false);
        final StockPrediction stockPrediction = sgSim.getPrediction();

        PeriodicityList.periodicityLong.forEach(p -> {

            double res = ref.getStatus(ModelType.ENSEMBLE).getAvg(p);
            fr.linearize(res);
            double error = ref.getStatus(ModelType.ENSEMBLE).getErrorRate(p);
            fr.linearize(error);

            /**
             * for training stockPrediction is null
             */
            if (stockPrediction == null) {
                fr.linearize(0);
            }
            else {
                double yieldPrediction =  MLMath.yield(stockPrediction.getPrediction(p), sgSim.getValue());
                fr.linearize(yieldPrediction);
            }

        });

        StockHistory si = StockHistory.getStockHistory("PX1", date);
        fr.linearize(si);
        StockAnalyse asi = StockAnalyse.getAnalyse("PX1", date);
        fr.linearize(asi);

        StockHistory siDJ = StockHistory.getStockHistory("DJI", date);
        fr.linearize(siDJ);
        StockAnalyse asiDJ = StockAnalyse.getAnalyse("DJI", date);
        fr.linearize(asiDJ);

        StockHistory siETOD= StockHistory.getStockHistory("ETOD", date);
        fr.linearize(siETOD);
        StockAnalyse asiETOD = StockAnalyse.getAnalyse("ETOD", date);
        fr.linearize(asiETOD);

        si = StockHistory.getStockHistory(sector, date);
        fr.linearize(si);
        asi = StockAnalyse.getAnalyse(sector, date);
        fr.linearize(asi);

        si = StockHistory.getStockHistory("VCAC", date);
        fr.linearize(si);
        asi = StockAnalyse.getAnalyse(sector, date);
        fr.linearize(asi);
        new DateTime(date);
        //TODO broken
        /*StockDocument sd = StockDocument.getNextStockDocument(sg.getRealCodif() + StockDocument.TYPE_DIARY, date);
        int diff = check_diff(new DateTime(date), new DateTime(sd.getDate()));
        fr.linearize(diff);*/

        return fr;
    }

}
