package com.mltrading.ml;

import com.mltrading.models.stock.StockGeneral;
import com.mltrading.models.stock.StockHistory;
import com.mltrading.models.stock.StockPrediction;
import org.apache.spark.mllib.linalg.Vectors;

import java.io.Serializable;

/**
 * Created by gmo on 29/02/2016.
 */
public class MLPredictor  implements Serializable {

    public StockPrediction prediction(StockGeneral stock) {


        MLStocks s = CacheMLStock.getMLStockCache().get(stock.getCodif());
        if (s != null) {
            try {
                StockPrediction sp = new StockPrediction(stock.getCodif());
                String date = StockHistory.getLastDateHistory(stock.getCodif());
                FeaturesStock fs = FeaturesStock.createRT(stock, s.getValidator(PredictionPeriodicity.D1), date);
                sp.setPredictionD1(s.getModel(PredictionPeriodicity.D1).predict(Vectors.dense(fs.vectorize())));
                sp.setConfidenceD1(100 - (s.getStatus().getErrorRateD1() * 100 / s.getStatus().getCountD1()));

                fs = FeaturesStock.createRT(stock, s.getValidator(PredictionPeriodicity.D5), date);
                sp.setPredictionD5(s.getModel(PredictionPeriodicity.D5).predict(Vectors.dense(fs.vectorize())));
                sp.setConfidenceD5(100 - (s.getStatus().getErrorRateD5() * 100 / s.getStatus().getCountD5()));

                fs = FeaturesStock.createRT(stock, s.getValidator(PredictionPeriodicity.D20), date);
                sp.setPredictionD20(s.getModel(PredictionPeriodicity.D20).predict(Vectors.dense(fs.vectorize())));
                sp.setConfidenceD20(100 - (s.getStatus().getErrorRateD20() * 100 / s.getStatus().getCountD20()));

                fs = FeaturesStock.createRT(stock, s.getValidator(PredictionPeriodicity.D40), date);
                sp.setPredictionD40(s.getModel(PredictionPeriodicity.D40).predict(Vectors.dense(fs.vectorize())));
                sp.setConfidenceD40(100 - (s.getStatus().getErrorRate(PredictionPeriodicity.D40) * 100 / s.getStatus().getCountD40()));

                return sp;
            }catch (Exception e) {
                System.out.print(e.toString());
                return null;
            }
        }
        else {
            return null;
        }
    }

}
