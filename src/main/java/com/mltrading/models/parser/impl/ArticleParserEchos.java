package com.mltrading.models.parser.impl;

import com.google.inject.Singleton;
import com.mltrading.models.parser.ArticleParser;
import com.mltrading.models.parser.ParserCommon;
import com.mltrading.models.stock.*;
import com.mltrading.models.stock.cache.CacheStockGeneral;
import com.mltrading.repository.ArticleRepository;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

/**
 * Created by gmo on 09/03/2016.
 */
@Singleton
public class ArticleParserEchos extends ParserCommon implements ArticleParser {

    private static final Logger log = LoggerFactory.getLogger(HistogramDocument.class);
    static String refCode = "div.contenu_article";

    HistogramDocument hd = new HistogramDocument();


    @Override
    public void fetch(ArticleRepository repository) {
        loader(repository);
    }

    @Override
    public void fetchCurrent(ArticleRepository repository) {
    //get article
        //si non present add

        for (StockGeneral g: CacheStockGeneral.getIsinCache().values()) {
            HistogramDocument hd = new HistogramDocument();
            String dateRef = hd.getLastDateHistory(g.getCodif() + "R");
            if (dateRef != null)
                loaderFrom(repository , g, dateRef);
        }

    }


    private void loaderFrom(ArticleRepository repository, StockGeneral g, String dateRef) {
        DateTime dref = new DateTime(dateRef);

        List<StockDocument> sds =  StockDocument.getStockDocumentInvert(g.getCodif(), StockDocument.TYPE_ARTICLE);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (StockDocument d: sds) {
            if (dref.isAfter(d.getTimeInsert())) {
                return;
            }
            String url = d.getRef();
            try {
                String text;

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!url.startsWith("http")) {
                    url = url.replace("href=\"http://", "http://").replaceAll("\"", "");
                    url = url.replace("href=", "http://investir.lesechos.fr").replaceAll("\"", "");
                }
                System.out.println(url);
                text = loadUrl(new URL(url));

                Document doc = Jsoup.parse(text);

                Article a = new Article();
                a.setKey(new ArticleKey(g.getCodif(), d.getDate()));
                a.setReference(d.getRef());

                /**
                 * Check balise number, if too much dont refer to action
                 */
                if (doc.select("div.bloc-tags").select("li").size() <5) {

                    String title = doc.select("h1").attr("itemprop","Headline").get(0).text();
                    a.setTitle(title);

                    Elements links = doc.select(refCode);

                    Elements sentences = links.select("p");

                    for (Element sentence : sentences) {
                        a.getLines().add(Jsoup.parse(sentence.toString()).text());

                    }

                    Elements kewWords = doc.select("div.bloc-tags").select("li");
                    for (Element kewWord : kewWords) {
                        a.getTopic().add(Jsoup.parse(kewWord.toString()).text());
                    }

                }

                repository.save(a);


            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("ERROR for : " + g.getName());
            }


        }
    }





    private void loader(ArticleRepository repository) {
        for (StockGeneral g : CacheStockGeneral.getIsinCache().values()) {

            List<StockDocument> sds =  StockDocument.getStockDocument(g.getCodif(), StockDocument.TYPE_ARTICLE);

            if (sds == null) continue;

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (StockDocument d: sds) {
                String url = d.getRef();
                try {
                    String text;

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!url.startsWith("http"))
                        url = url.replace("href=\"","https://investir.lesechos.fr").replaceAll("\"","");
                    System.out.println(url);
                    text = loadUrl(new URL(url));



                    Document doc = Jsoup.parse(text);

                    Article a = new Article();
                    a.setKey(new ArticleKey(g.getCodif(), d.getDate()));
                    a.setReference(d.getRef());

                    /**
                     * Check balise number, if too much dont refer to action
                     */
                    if (doc.select("div.bloc-tags").select("li").size() <5) {

                        String title = doc.select("h1").attr("itemprop","Headline").get(0).text();
                        a.setTitle(title);

                        Elements links = doc.select(refCode);

                        Elements sentences = links.select("p");

                        for (Element sentence : sentences) {
                            a.getLines().add(Jsoup.parse(sentence.toString()).text());

                        }

                        Elements kewWords = doc.select("div.bloc-tags").select("li");
                        for (Element kewWord : kewWords) {
                            a.getTopic().add(Jsoup.parse(kewWord.toString()).text());
                        }

                    }

                    repository.save(a);

                } catch (Exception e) {
                    //e.printStackTrace();
                    System.out.println("ERROR for : " + g.getName());
                }

            }
        }
    }


}
