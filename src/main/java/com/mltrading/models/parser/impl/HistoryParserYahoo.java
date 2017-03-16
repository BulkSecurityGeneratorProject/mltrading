package com.mltrading.models.parser.impl;


import com.google.inject.Singleton;

import com.mltrading.models.parser.HistoryParser;

import com.mltrading.models.stock.*;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;



/**
 * Created by gmo on 23/11/2015.
 */

@Singleton
@Deprecated
public class HistoryParserYahoo implements HistoryParser {
    @Override
    public void fetch() {
        throw new NotImplementedException();
    }

    @Override
    public void fetchSpecific(StockGeneral g) {
        throw new NotImplementedException();
    }

    @Override
    public void fetchCurrent(int period) {
        throw new NotImplementedException();
    }


    static String startUrl="https://fr.finance.yahoo.com/q/hp?s=";
    static String endUrl ="&a=00&b=3&c=2010&g=d&z=66&y=";
    static int PAGINATION = 66;
    static String refCode = "tbody";
    static int MAXPAGE = 1518;


    /**
     * not very nice .. code duplicate and exit not nice
     * @param range
     *
    public  void loaderFrom(int range) {

        int numPage;
        boolean retry = false;
        for (StockGeneral g : CacheStockGeneral.getIsinCache().values()) {
            Consensus cnote = ConsensusParserInvestir.fetchStock(g.getCode());

            String url = startUrl + g.getCodif() + "." + g.getPlaceCodif() + endUrl + 0;
            try {
                String text;
                int loopPage = 0;

                //inifinite loop
                do {
                    text = ParserCommon.loadUrl(new URL(url));
                    if (text == null) retry = true;
                    else retry = false;
                } while (retry);


                Document doc = Jsoup.parse(text);
                BatchPoints bp = InfluxDaoConnector.getBatchPoints();

                Elements links = doc.select(refCode);
                int count = 0;

                for (Element link : links) {

                    if (link.children().size() > 40) {
                        Elements sublinks = link.children().select("tr");
                        for (Element elt : sublinks) {
                            Elements t = elt.select("td");
                            if (t.size() > 3) {
                                loopPage++;
                                StockHistory hist = new StockHistory(g);
                                hist.setDayYahoo(t.get(0).text());
                                hist.setOpening(new Double(t.get(1).text().replace(",", ".")));
                                hist.setHighest(new Double(t.get(2).text().replace(",", ".")));
                                hist.setLowest(new Double(t.get(3).text().replace(",", ".")));
                                hist.setValue(new Double(t.get(4).text().replace(",", ".")));
                                hist.setVolume(new Double(t.get(5).text().replaceAll(" ", "")));
                                hist.setConsensusNote(cnote.getNotation(cnote.getIndice(loopPage + 0)).getAvg());
                                if (hist.getVolume() > 0)
                                    HistoryParser.saveHistory(bp, hist); //dont save no trading day
                                System.out.println(hist.toString());
                                if (count++ >= range)
                                    break;
                            }
                        }
                    }
                }
                InfluxDaoConnector.writePoints(bp);


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR for : " + g.getName());
            }
        }

    }

    public  void loaderSpecific(StockGeneral g) {

        int numPage;
        boolean retry = false;

        Consensus cnote = ConsensusParserInvestir.fetchStock(g.getCode());
        for(numPage =0; numPage <= MAXPAGE ; numPage += PAGINATION) {
            String url = startUrl + g.getCodif() +"." + g.getPlaceCodif() + endUrl+ numPage;
            try {
                String text;
                int loopPage = 0;

                //inifinite loop
                do {
                    text = ParserCommon.loadUrl(new URL(url));
                    if (text == null) retry = true;
                    else retry = false;
                } while (retry);


                Document doc = Jsoup.parse(text);
                BatchPoints bp = InfluxDaoConnector.getBatchPoints();

                Elements links = doc.select(refCode);

                for (Element link : links) {

                    if (link.children().size() > 40) {
                        Elements sublinks = link.children().select("tr");
                        for (Element elt : sublinks) {
                            Elements t = elt.select("td");
                            if (t.size() > 3) {
                                loopPage ++;
                                StockHistory hist = new StockHistory(g);
                                hist.setDayYahoo(t.get(0).text());
                                hist.setOpening(new Double(t.get(1).text().replace(",", ".")));
                                hist.setHighest(new Double(t.get(2).text().replace(",", ".")));
                                hist.setLowest(new Double(t.get(3).text().replace(",", ".")));
                                hist.setValue(new Double(t.get(4).text().replace(",", ".")));
                                hist.setVolume(new Double(t.get(5).text().replaceAll(" ", "")));
                                hist.setConsensusNote(cnote.getNotation(cnote.getIndice(loopPage+numPage)).getAvg());
                                if (hist.getVolume() > 0) HistoryParser.saveHistory(bp, hist); //dont save no trading day
                                System.out.println(hist.toString());
                            }
                        }
                    }
                }
                InfluxDaoConnector.writePoints(bp);


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("ERROR for : " + g.getName());
            }
        }
    }




    public  void loader() {

        int numPage;
        boolean retry = false;
        for (StockGeneral g: CacheStockGeneral.getIsinCache().values()) {
            Consensus cnote = ConsensusParserInvestir.fetchStock(g.getCode());
            for(numPage =0; numPage <= MAXPAGE ; numPage += PAGINATION) {
                String url = startUrl + g.getCodif() +"." + g.getPlaceCodif() + endUrl+ numPage;
                try {
                    String text;
                    int loopPage = 0;

                    //inifinite loop
                    do {
                        text = ParserCommon.loadUrl(new URL(url));
                        if (text == null) retry = true;
                        else retry = false;
                    } while (retry);


                    Document doc = Jsoup.parse(text);
                    BatchPoints bp = InfluxDaoConnector.getBatchPoints();

                    Elements links = doc.select(refCode);

                    for (Element link : links) {

                        if (link.children().size() > 40) {
                            Elements sublinks = link.children().select("tr");
                            for (Element elt : sublinks) {
                                Elements t = elt.select("td");
                                if (t.size() > 3) {
                                    loopPage ++;
                                    StockHistory hist = new StockHistory(g);
                                    hist.setDayYahoo(t.get(0).text());
                                    hist.setOpening(new Double(t.get(1).text().replace(",", ".")));
                                    hist.setHighest(new Double(t.get(2).text().replace(",", ".")));
                                    hist.setLowest(new Double(t.get(3).text().replace(",", ".")));
                                    hist.setValue(new Double(t.get(4).text().replace(",", ".")));
                                    hist.setVolume(new Double(t.get(5).text().replaceAll(" ", "")));
                                    hist.setConsensusNote(cnote.getNotation(cnote.getIndice(loopPage+numPage)).getAvg());
                                    if (hist.getVolume() > 0) HistoryParser.saveHistory(bp, hist); //dont save no trading day
                                    System.out.println(hist.toString());
                                }
                            }
                        }
                    }
                    InfluxDaoConnector.writePoints(bp);


                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("ERROR for : " + g.getName());
                }
            }
        }
    }*/
}
