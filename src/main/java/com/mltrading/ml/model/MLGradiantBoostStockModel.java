package com.mltrading.ml.model;

import com.mltrading.ml.CacheMLStock;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.tree.model.GradientBoostedTreesModel;
import scala.Serializable;

public class MLGradiantBoostStockModel extends Model<GradientBoostedTreesModel> implements Serializable {
    GradientBoostedTreesModel model;

    public MLGradiantBoostStockModel(GradientBoostedTreesModel model) {
        super();
        this.model = model;
    }

    public MLGradiantBoostStockModel() {
        super();
    }

    public MLGradiantBoostStockModel(String path, String period, String codif, String modelExtendedPrefix) {
        super(period, codif, ModelType.GRADIANTBOOSTTREE, modelExtendedPrefix);
        load(path, period, codif, modelExtendedPrefix);
    }


    @Override
    public void setModel(GradientBoostedTreesModel model) {
        this.model = model;
    }

    @Override
    public GradientBoostedTreesModel getModel() {
        return this.model;
    }


    @Override
    public void load(String path, String period, String codif, String modelExtendedPrefix) {
        this.model = GradientBoostedTreesModel.load(CacheMLStock.getJavaSparkContext().sc(), path + "model/Model" + ModelType.code(ModelType.GRADIANTBOOSTTREE) + period + codif + modelExtendedPrefix);
        //this.validator.loadValidator(codif + ModelType.code(ModelType.GRADIANTBOOSTTREE) + period);
    }

    @Override
    public double predict(Vector vector) {
        return  model.predict(vector);
    }

    @Override
    public void save(String s) {
        this.model.save(CacheMLStock.getJavaSparkContext().sc(), s);
    }
}
