package com.mltrading.ml;

import com.mltrading.dao.InfluxDaoConnector;
import com.mltrading.influxdb.dto.BatchPoints;
import com.mltrading.ml.FeaturesStock.PredictionPeriodicity;
import org.apache.spark.api.java.JavaRDD;

import org.apache.spark.mllib.tree.model.RandomForestModel;

import java.util.List;


public class MLStock {
    private String codif;
    private PredictionPeriodicity period;
    private RandomForestModel model;
    private List<Double> previsionList;
    private Validator validator;


    private MLStock() {
        validator = new Validator();
    }

    public MLStock(String codif, PredictionPeriodicity period) {
        this.period = period;
        this.codif = codif;
        validator = new Validator();
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public String getCodif() {
        return codif;
    }

    public void setCodif(String codif) {
        this.codif = codif;
    }

    public RandomForestModel getModel() {
        return this.model;
    }

    public void setModel(RandomForestModel model) {
        this.model = model;
    }

    public List<Double> getPrevisionList() {
        return previsionList;
    }

    public void setPrevisionList(List<Double> previsionList) {
        this.previsionList = previsionList;
    }



    public void saveModel() {
       this.model.save(CacheMLStock.getJavaSparkContext().sc(), "Model" + period.toString() + codif);
    }

    public void loadModel(PredictionPeriodicity t) {
        this.model = RandomForestModel.load(CacheMLStock.getJavaSparkContext().sc(), "Model"+period.toString()+codif);
    }

    /*public void savePerformance() {
        BatchPoints bp = InfluxDaoConnector.getBatchPoints();
        for (MLPerformances p : perfList) {
            p.savePerformance(bp, codif+"P");
        }
    }   */

    public void save() {
        saveModel();
        //savePerformance();
    }


}
