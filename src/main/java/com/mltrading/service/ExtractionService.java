package com.mltrading.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mltrading.models.parser.*;
import com.mltrading.models.stock.cache.*;
import com.mltrading.models.stock.StockGeneral;
import com.mltrading.models.stock.StockHistory;
import com.mltrading.models.stock.StockRawMat;
import com.mltrading.models.util.CsvFileReader;
import com.mltrading.repository.ArticleRepository;
import com.mltrading.repository.StockRepository;
import com.mltrading.web.rest.ExtractionResource;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * Created by gmo on 04/01/2016.
 */
@Service
public class ExtractionService {

    Injector injector = Guice.createInjector(new ServiceParser());
    RealTimeParser rtPrice = injector.getInstance( RealTimeParser.class );
    HistoryParser histParser = injector.getInstance(HistoryParser.class);
    HistoryRawMaterialsParser rawParser = injector.getInstance(HistoryRawMaterialsParser.class);
    HistoryIndiceParser indiceParser= injector.getInstance(HistoryIndiceParser.class);
    HistorySectorParser sectorParser = injector.getInstance(HistorySectorParser.class);
    VolatilityParser vola = injector.getInstance(VolatilityParser.class);
    StockParser stock = injector.getInstance(StockParser.class);
    ArticlesParser articles =  injector.getInstance(ArticlesParser.class);
    ArticleParser article =  injector.getInstance(ArticleParser.class);
    ArticleNotation notation =  injector.getInstance(ArticleNotation.class);
    DiaryParser diaryParser =  injector.getInstance(DiaryParser.class);


    public void extractStock(StockRepository stockRepository) {
        stock.fetch(stockRepository);
    }

    /**
     * Extract full data from web
     * complete with ddd import data from file
     * @param articleRepository
     */
    public void extractFull(ArticleRepository articleRepository) {
        histParser.fetch();
        indiceParser.fetch();
        sectorParser.fetch();
        vola.fetch();
        diaryParser.fetch();

        new CsvFileReader();
    }

    public void extractRawFull(String host) {
        rawParser.fetch(host);
        rawParser.fetchCurrent(30);
    }

    public void extractSectorFull() {
        sectorParser.fetch();
    }

    public void extractSectorPeriod(int period) {
        if (period == ExtractionResource.AUTO)
            period = getLastUpdateRefSector();
        sectorParser.fetchCurrent(period);
    }

    public void extractIndiceFull() {
        indiceParser.fetch();
    }

    public void extractIndicePeriod(int period) {
        if (period == ExtractionResource.AUTO)
            period = getLastUpdateRefIndice();
        indiceParser.fetchCurrent(period);
    }

    public void extractVcacFull() {
        vola.fetch();
    }

    public void extractArticlesFull() {
        articles.fetch();
    }

    public void extractArticleFull(ArticleRepository articleRepository) {
        article.fetch(articleRepository);
    }

    public void extractDiaryFull() {
        diaryParser.fetch();
    }

    public void extractDiaryPeriod(int period) {
        diaryParser.fetchCurrent(period);
    }


    public void extractionCurrent(ArticleRepository articleRepository,int period) throws InterruptedException {
        histParser.fetchCurrent(period);
        indiceParser.fetchCurrent(period);
        sectorParser.fetchCurrent(period);
        vola.fetchCurrent(period);
        rawParser.fetchCurrent(period);
        diaryParser.fetchCurrent(period);
        articles.fetchCurrent();
        article.fetchCurrent(articleRepository);
        diaryParser.fetchCurrent(period);

        CacheStockHistory.CacheStockHistoryHolder().clearCache();

        Analyse a = new Analyse();
        a.processDaily(period);


        CacheStockAnalyse.CacheStockAnalyseHolder().clearCache();
        CacheStockDiary.CacheStockHistoryHolder().clearCache();
    }
    //

    public  void extractionSpecific(String code) throws InterruptedException {
        StockGeneral g = CacheStockGeneral.getIsinCache().get(code);
        histParser.fetchSpecific(g);
        Analyse a = new Analyse();
        a.processAnalysisAll(g.getCode());
        //articles.fetchSpecific(g);
        //article.fetchSpecific(g);
    }

    public static int DAYS_BY_MONTH = 31;
    public static int DAYS_BY_YEAR = 365;

    public static int check_diff( DateTime timeInsert , DateTime timeNow ) {
        int shift = (timeNow.getYear() - timeInsert.getYear()) * DAYS_BY_YEAR;
        return (timeNow.getDayOfMonth()+timeNow.getMonthOfYear()*DAYS_BY_MONTH + shift) - (timeInsert.getDayOfMonth()+timeInsert.getMonthOfYear()*DAYS_BY_MONTH);
    }

    public int getLastUpdateRefSeries() {
        String l = StockHistory.getLastDateHistory("ORA");
        DateTime timeInsert = new DateTime(l);
        DateTime timeNow = new DateTime(System.currentTimeMillis());

        return check_diff(timeInsert,timeNow);
    }


    public int getLastUpdateRefSector() {
        String l = StockHistory.getLastDateHistory("FRIN");
        DateTime timeInsert = new DateTime(l);
        DateTime timeNow = new DateTime(System.currentTimeMillis());

        return check_diff(timeInsert,timeNow);
    }

    public int getLastUpdateRefIndice() {
        String l = StockHistory.getLastDateHistory("PX1");
        DateTime timeInsert = new DateTime(l);
        DateTime timeNow = new DateTime(System.currentTimeMillis());

        return check_diff(timeInsert,timeNow);
    }

    public int getLastUpdateRefRaw() {
        String l = StockHistory.getLastDateHistory("PETB");
        DateTime timeInsert = new DateTime(l);
        DateTime timeNow = new DateTime(System.currentTimeMillis());

        return check_diff(timeInsert,timeNow);
    }


    public int getLastUpdateRef() {
        String l = StockHistory.getLastDateHistory("ORA");
        DateTime timeInsert = new DateTime(l);
        DateTime timeNow = new DateTime(System.currentTimeMillis());

        int diff = check_diff(timeInsert,timeNow);


        l = StockHistory.getLastDateHistory("FRIN");
        timeInsert = new DateTime(l);
        diff = Math.max(diff,  check_diff(timeInsert,timeNow));

        l = StockHistory.getLastDateHistory("PX1");
        timeInsert = new DateTime(l);
        diff = Math.max(diff,  check_diff(timeInsert,timeNow));

        l = StockRawMat.getLastDateHistory("PETB");
        timeInsert = new DateTime(l);
        diff = Math.max(diff,  check_diff(timeInsert,timeNow));


        //diff -=1;

        return diff;
        //return 0;
    }


    public void processAT() throws InterruptedException {
        Analyse a = new Analyse();
        a.processAll();
    }

    public void processATPeriod(int period) throws InterruptedException {
        Analyse a = new Analyse();
        a.processDaily(period);
    }


    public void extractNotationFull(ArticleRepository articleRepository) {
        notation.fetch(articleRepository);
    }

    public void extractRawPeriod(int period) {
        if (period == ExtractionResource.AUTO)
            period = getLastUpdateRefRaw();
        rawParser.fetchCurrent(period);
    }


    public void extractVcacPeriod(int period) {

        vola.fetchCurrent(period);
    }


    public void extractArticlesPeriod() {
        articles.fetchCurrent();
    }

    public void extractArticlePeriod(ArticleRepository articleRepository) {
        article.fetchCurrent(articleRepository);
    }

    public void extractSeriesFull() {
        histParser.fetch();
    }

    public void extractSeriesPeriod(int period) {
        if (period == ExtractionResource.AUTO)
            period = getLastUpdateRefSeries();
        histParser.fetchCurrent(period);
    }
}
