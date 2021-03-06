package com.mltrading.models.parser;

import com.mltrading.models.stock.StockHistory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.concurrent.TimeUnit;

/**
 * Created by gmo on 03/12/2015.
 */
public interface VolatilityParser {

    void fetch();

    public void fetchCurrent(int period);


    public static void saveHistory(BatchPoints bp, StockHistory hist) {
        Point pt = Point.measurement(hist.getCode()).time(hist.getTimeInsert().getMillis(), TimeUnit.MILLISECONDS)
            .field("open", hist.getOpening())
            .field("high", hist.getHighest())
            .field("low",hist.getLowest())
            .field("volume", hist.getVolume())
            .field("value",hist.getValue())
            .build();
        bp.point(pt);
    }
}
