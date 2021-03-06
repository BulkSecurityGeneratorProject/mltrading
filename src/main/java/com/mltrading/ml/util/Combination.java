package com.mltrading.ml.util;

import com.mltrading.ml.*;
import com.mltrading.ml.model.GradiantBoostStock;
import com.mltrading.ml.model.ModelType;
import com.mltrading.ml.model.RandomForestStock;
import com.mltrading.models.util.MLActivities;
import com.mltrading.web.rest.dto.ForecastDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by gmo on 15/06/2017.
 */




public class Combination extends Evaluate{

    private static final Random random = new Random(0);
    private static final Logger log = LoggerFactory.getLogger(Combination.class);

    MatrixValidator mv;

    public static Combination newInstance() {
        return new Combination();
    }


    public MatrixValidator getMv() {
        return mv;
    }

    public void setMv(MatrixValidator mv) {
        this.mv = mv;
    }

    public Combination(MatrixValidator mv) {
        this.mv = mv;
    }

    public Combination() {
        mv = new MatrixValidator();
        mv.generateRandomModel(0);
    }

    static int SCORENOTREACHABLE =  500;
    public double evaluate(String codif, PredictionPeriodicity p, ForecastDTO forecastDTO) {
        final ModelType type = ModelType.get(forecastDTO.getModelType());

        final MLStocks mls = MLStocks.newStock(codif, forecastDTO);

        CacheMLActivities.addActivities(new MLActivities("optimize", codif, "start", 0, 0, false));

        mls.getModel(p).setValidator(type,mv);



        if (type == ModelType.RANDOMFOREST) {
            RandomForestStock rfs = new RandomForestStock();
            rfs.processRFRef(codif, mls, false, p);
        }
        else {
            GradiantBoostStock rfs = new GradiantBoostStock();
            rfs.processRFRef(codif, mls, false, p);
        }


        if (null != mls) {
            mls.getStatus(type).calculeAvgPrd();

        } else {
            CacheMLActivities.addActivities(new MLActivities("optimize", codif, "failed", 0, 0, true));
        }

        //inverse score
        return SCORENOTREACHABLE - convert(mls.getStatus(type).getErrorRate(p),mls.getStatus(type).getAvg(p));
    }


    private double convert(int error, double stdDev) {
        int std = Math.abs((int) Math.rint(stdDev*1000));
        String convert = error+"."+std;
        return new Double(convert);
    }



    public Combination merge(Combination other) {
        this.mv.merge(other.mv);
        return new Combination(this.mv);
    }

    public Combination mutate() {
        return new Combination();
    }


    @Override
    public void updateEnsemble() {

    }
}
