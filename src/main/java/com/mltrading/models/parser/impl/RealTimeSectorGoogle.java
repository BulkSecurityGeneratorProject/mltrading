package com.mltrading.models.parser.impl;

import com.mltrading.models.parser.ParserCommon;
import com.mltrading.models.parser.RealTimeParser;
import com.mltrading.models.stock.CacheStockSector;
import com.mltrading.models.stock.StockSector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;

/**
 * Created by gmo on 16/01/2017.
 */
public class RealTimeSectorGoogle implements RealTimeParser {

    static String startUrl="https://www.google.com/finance?q=INDEXEURO%3A";
    static String refCode = "div.id-price-panel";

    public static void refreshCache() {
        CacheStockSector.getSectorCache().values().forEach(com.mltrading.models.parser.impl.RealTimeSectorGoogle::refreshCache);
        //CacheStockSector.getSectorCache().values().forEach(com.mltrading.models.parser.impl.RealTimeSectorGoogle::print);
    }


    private static void print(StockSector ss) {
        System.out.println("code: " + ss.getCode() +" value: " + ss.getValue());
    }

    private static int refreshCache(StockSector ss) {

        try {
            String text = ParserCommon.loadUrl(new URL(startUrl+ss.getCode()));

            Document doc = Jsoup.parse(text);

            Elements links = doc.select(refCode);

            String value = links.get(0).child(0).text();

            String values[] = value.split(" ");

            ss.setValue(new Double(values[0].replaceAll(",", "")));
            ss.setVariation(new Float(values[2].replaceAll("\\(", "").replaceAll("%\\)", "")));



        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


}