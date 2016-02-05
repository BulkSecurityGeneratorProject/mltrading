package com.mltrading.ml;


import com.mltrading.models.stock.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmo on 24/11/2015.
 */
public class FeaturesStock implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(FeaturesStock.class);

    private String date;

    private double resultValue;
    private double predictionValue;
    private double currentValue;

    private Double vector[];

    int currentVectorPos = 0;


    public FeaturesStock() {
        vector = new Double[20000];
    }

    public FeaturesStock(FeaturesStock fs, double predictRes) {
        this.date = fs.date;
        this.resultValue = fs.resultValue;
        this.currentValue = fs.getCurrentValue();
        this.currentVectorPos = fs.currentVectorPos;
        this.vector = fs.vector.clone();
        this.predictionValue = predictRes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double[] getVector() {
        return vector;
    }

    public int getCurrentVectorPos() {
        return currentVectorPos;
    }

    public void setPredictionValue(double predictionValue) {
        this.predictionValue = predictionValue;
    }


    public double getCurrentValue() {
        return currentValue;
    }

    public double getResultValue() {
        return resultValue;
    }

    public void setResultValue(double resultValue) {
        this.resultValue = resultValue;
    }

    public double getPredictionValue() {
        return predictionValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public static FeaturesStock transform(StockHistory sh,double value) {
        FeaturesStock fs = new FeaturesStock();

        fs.setPredictionValue(value);

        return fs;
    }

    public double[] vectorize() {

        double[] result = new double[currentVectorPos];

        for(int i = 0; i < currentVectorPos; ++i) {
            try {
                result[i] = vector[i].doubleValue();
            } catch (NullPointerException npe) {
                result[i] = 0;
            }
        }

        return result;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public  static List<FeaturesStock> transformList(List<StockHistory> shL) {
        List<FeaturesStock> fsL = new ArrayList<>();

        for (int i = 0; i< shL.size()-1; i++) {
            fsL.add(transform(shL.get(i), shL.get(i+1).getValue()));
        }

        return fsL;
    }

    public void linearize(StockHistory sh) {
        this.vector[currentVectorPos++] = sh.getValue();
        this.vector[currentVectorPos++] = sh.getConsensusNote();
        this.vector[currentVectorPos++] = sh.getVolume();
    }

    public void linearize(StockAnalyse sa) {
        this.vector[currentVectorPos++] = sa.getMme12();
        this.vector[currentVectorPos++] = sa.getMme26();
        this.vector[currentVectorPos++] = sa.getMomentum();
        this.vector[currentVectorPos++] = sa.getStdDev();
    }

    public void linearizeSH(List<StockHistory> shl) {
        for (StockHistory sh:shl)
            this.vector[currentVectorPos++] = sh.getValue();
    }

    public void linearizeSS(List<StockSector> shl) {
        for (StockSector sh:shl)
            this.vector[currentVectorPos++] = sh.getValue();
    }

    public void linearizeSI(List<StockIndice> shl) {
        for (StockIndice sh:shl)
            this.vector[currentVectorPos++] = sh.getValue();
    }

    static int OFFSET_BASE = 50;
    static int RANGE_MAX = 300;
    static int XT_PERIOD = 20;
    static int XT_OFFSET = 20;

    public  static List<FeaturesStock> create(Stock stock) {
        //Xt,Xt-1,...,Xn ,Consensus AT => StockHistory
        //Indice Xt,..Xn, AT => StockIndice
        //Secteur Xt,..Xn, AT => StockSecteur
        //Vola Cac Xt,..Xn, AT
        //indice etranger

        log.info("create FeaturesStock for: " + stock.getCodeif());

        List<FeaturesStock> fsL = new ArrayList<>();

        List<String> rangeDate = null;
        try {
            rangeDate = StockHistory.getDateHistoryListOffsetLimit(stock.getCode(), OFFSET_BASE, RANGE_MAX);
            if (rangeDate.size() < 200) {
                log.error("Cannot get date list for: " + stock.getCode() + " not enough element");
                return null;
            }
        } catch (Exception e) {
            log.error("Cannot get date list for: " + stock.getCode() + "  //exception:" + e);
            return null;
        }

        for (String date: rangeDate) {
            FeaturesStock fs = new FeaturesStock();
            fs.setDate(date);

            try {
            StockHistory  res = StockHistory.getStockHistoryDayAfter(stock.getCode(), date);
            fs.setResultValue(res.getValue());
            } catch (Exception e) {
                log.error("Cannot get date for: " + stock.getCode() + " and date: " + date + " //exception:" + e);
                continue;
            }

            /**
             * stock
             */
            try {
            List<StockHistory> sh = StockHistory.getStockHistoryDateInvert(stock.getCode(), date, XT_PERIOD);
            fs.linearizeSH(sh);
            StockHistory current = StockHistory.getStockHistory(stock.getCode(), date);
            fs.linearize(current);
            fs.setCurrentValue(current.getValue());

            } catch (Exception e) {
                log.error("Cannot get stock history for: " + stock.getCode() + " and date: " + date +  " //exception:" + e);
                continue;
            }


            try {
                StockAnalyse ash = StockAnalyse.getAnalyse(stock.getCode(), date);
                fs.linearize(ash);

            } catch (Exception e) {
                log.error("Cannot get analyse stock for: " + stock.getCode() + " and date: " + date +  " //exception:" + e);
                continue;
            }

            /**
             * sector
             */
            try {
                List<StockSector> ss = StockSector.getStockSectorDateInvert(stock.getSector(), date, XT_PERIOD);
                fs.linearizeSS(ss);
                StockAnalyse ass = StockAnalyse.getAnalyse(stock.getSector(), date);
                fs.linearize(ass);
            } catch (Exception e) {
                log.error("Cannot get sector/analyse stock for: " + stock.getSector() + " and date: " + date + " //exception:" + e);
                continue;
            }


            /**
             * indice
             */
            try {
                String codeIndice = StockIndice.translate(stock.getIndice());
                List<StockIndice> si = StockIndice.getStockIndiceDateInvert(codeIndice, date, XT_PERIOD);
                fs.linearizeSI(si);
                StockAnalyse asi = StockAnalyse.getAnalyse(codeIndice, date);
                fs.linearize(asi);
            } catch (Exception e) {
                log.error("Cannot get indice/analyse stock for: " + stock.getIndice() + " and date: " + date +  " //exception:" + e);
                continue;
            }


            /**
             * volatility cac
             */
            try {
                List<StockIndice> sVCac = StockIndice.getStockIndiceDateInvert("VCAC", date, XT_PERIOD);
                fs.linearizeSI(sVCac);
            } catch (Exception e) {
                log.error("Cannot get vcac stock for: " + stock.getCodeif() + " and date: " + date + " //exception:" + e);
                continue;
            }


            fsL.add(fs);
        }

        return fsL;
    }
}
