package com.mltrading.models.stock.cache;

/**
 * Created by gmo on 08/03/2017.
 */


import com.mltrading.dao.TimeSeriesDao.TimeSeriesDao;
import com.mltrading.dao.TimeSeriesDao.impl.TimeSeriesDaoInfluxImpl;
import com.mltrading.models.stock.StockHistory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * cache for improve I/O perfrormance with influxdb
 * refresh cache every day
 */
public class CacheStockHistory {

    private static TimeSeriesDao timeSeries = null;

    final static CacheStockHistory instance = new CacheStockHistory();


    private Map<String,Map<String, Integer>> indexCache = new HashMap<>();
    private Map<String,List<StockHistory>> historyCache = new HashMap<>();

    private CacheStockHistory() {
        timeSeries = new TimeSeriesDaoInfluxImpl();
    }

    public static CacheStockHistory CacheStockHistoryHolder() {
        /** Instance unique non pre-initialise */
        return instance;
    }



    /**
     * Use getStockHistory in memory
     * @param code
     * @param date
     * @return
     */
    public StockHistory getStockHistory(final String code, String date) {
        return getStockHistory(code,date,true);
    }


    /**
     *
     * @param code
     * @param date
     * @return
     */
    public StockHistory getStockHistory(final String code, String date, boolean inMemory) {

        if (inMemory == true) {

            List<StockHistory> list = getInCache(code);
            Integer index = getInCache(list, code, date);

            return findElement(list, index);
        }
        else
            return timeSeries.extractSpecific(code, date);
    }





    /**
     * Populate a list of date
     * @param code
     * @param max
     * @return
     */
    public List<String> getDateHistoryListOffsetLimit(final String code, int max) {
        List<String> dateList = new ArrayList<>();
        List<StockHistory> list = getInCache(code);

        if (list == null) return null;
        int size = list.size();
        if (size < max)
            return null;

        for (int i = size-max; i < size; i++) {
            dateList.add(list.get(i).getDay());
        }
        return dateList;
    }


    /**
     * get day of last element in cache
     * @param code
     * @return
     */
    public  String getLastDateHistory(final String code) {return getLastDateHistory(code,true);}


    /**
     * get day of last element in cache
     * @param code
     * @param inMemory
     * @return
     */
    public  String getLastDateHistory(final String code, boolean inMemory) {

        if (inMemory == true) {
            List<StockHistory> list = getInCache(code);
            if (list == null) return null;
            return list.get(list.size()-1).getDay();
        }
        else
            return timeSeries.extractLastHistory(code).getDay();

    }


    /**
     * return element a day after
     * @param code
     * @param date
     * @return
     */
    public StockHistory getStockHistoryDayAfter(final String code, String date) {
        return getStockHistoryDayAfter(code,date,true);
    }

    /**
     * return element a day after
     * @param code
     * @param date
     * @param inMemory
     * @return
     */
    public StockHistory getStockHistoryDayAfter(final String code, String date, boolean inMemory) {
        if (inMemory == true) {

            List<StockHistory> list = getInCache(code);
            Integer index = getInCache(list, code, date);

            /* order is most recent to oldest*/
            return findElement(list, index+1);
        }
        else
            return timeSeries.extractSpecificAfter(code, date);
    }


    public StockHistory getStockHistoryDayBefore(final String code, String date) {
        return getStockHistoryDayBefore(code, date, true);
    }

    /**
     * return element a day after
     * @param code
     * @param date
     * @param inMemory
     * @return
     */
    public StockHistory getStockHistoryDayBefore(final String code, String date, boolean inMemory) {
        if (inMemory == true) {

            List<StockHistory> list = getInCache(code);
            Integer index = getInCache(list, code, date);

            /* order is most recent to oldest*/
            return findElement(list, index-1);
        }
        else
            return timeSeries.extractSpecificBefore(code, date);
    }


    /**
     *
     * @param code
     * @param date
     * @param offset
     * @return
     */
    public StockHistory getStockHistoryDayOffset(final String code, String date, int offset) {
        return getStockHistoryDayOffset(code, date, offset, true);
    }


    /**
     *
     * @param code
     * @param date
     * @param offset
     * @param inMemory
     * @return
     */
    public StockHistory getStockHistoryDayOffset(final String code, String date, int offset, boolean inMemory) {
        if (inMemory == true) {

            List<StockHistory> list = getInCache(code);
            Integer index = getInCache(list,code, date);

            return findElement(list, index+offset);
        }
        else
            return timeSeries.extractSpecificOffset(code, date, offset);
    }


    /**
     *  get count last element
     * @param code
     * @param count
     * @return
     */
    public List<StockHistory> getStockHistoryLast(final String code, int count) {return getStockHistoryLast(code,count,true);}


    /**
     * get count last element
     * @param code
     * @param count
     * @param inMemory
     * @return
     */
    public List<StockHistory> getStockHistoryLast(final String code, int count, boolean inMemory) {
        if (inMemory == true) {
            List<StockHistory> list = getInCache(code);
            if (list == null) return null;
            return list.subList(0, count);
        }
        else
            return timeSeries.extractLasts(code, count);
    }


    /**
     *  get count last element
     * @param code
     * @param count
     * @return
     */
    public List<StockHistory> getStockHistoryLastInvert(final String code, int count) {return getStockHistoryLastInvert(code, count, true);}


    /**
     * get count last element
     * @param code
     * @param count
     * @param inMemory
     * @return
     */
    public List<StockHistory> getStockHistoryLastInvert(final String code, int count, boolean inMemory) {
        if (inMemory == true) {
            List<StockHistory> list = getInCache(code);
            if (list == null || list.size()-count < 0) return null;
            List<StockHistory> subList= new CopyOnWriteArrayList(list.subList(list.size() - count, list.size()));
            Collections.sort(subList);
            return(subList);
        }
        else
            throw  new NotImplementedException();

    }


    /**
     *
     * @param code
     * @param date
     * @param offset
     * @return
     */
    public  List<StockHistory> getStockHistoryDateInvert(final String code, final String date, int offset) { return getStockHistoryDateInvert(code, date,offset, true);}


    /**
     * return a sublist start on date t and for a specific size offset
     * order of list is reversed ... d1d2d3 => d3d2d1
     * @param code
     * @param date
     * @param offset
     * @param inMemory
     * @return
     */
    static int exclusiveOffset = 1;
    public  List<StockHistory> getStockHistoryDateInvert(final String code, final String date, int offset, boolean inMemory) {


        if (inMemory == true) {

            List<StockHistory> list = getInCache(code);
            Integer index = getInCache(list,code, date);

            if (list == null || index - offset < 0) return null;
            List<StockHistory> subList= new CopyOnWriteArrayList(list.subList(index - offset + exclusiveOffset, index + exclusiveOffset));

            Collections.sort(subList);

            return subList;
        }
        else
            return timeSeries.extractDateInvert(code, date, offset);

    }









    /**
     * return index and filled cache if empty
     * @param code
     * @return
     */
    private  Integer getInCache(List<StockHistory> list , String code, String date) {
        Map<String, Integer> indexMap = this.indexCache.get(code);
        if (indexMap == null) fillCache(code);
        Integer res =  this.indexCache.get(code).get(date);
        if (res == null) {
            int curser = 0;
            while (curser  < list.size()) {
                if (list.get(curser).getDay().compareTo(date) < 0) curser ++;
                else {
                    res = curser;
                    break;
                }
            }
        }

        return res;
    }


    /**
     * return list and filled cache if empty
     * @param code
     * @return
     */
    private  List<StockHistory> getInCache(String code) {
        List<StockHistory> list = this.historyCache.get(code);
        if (list == null)
            list = fillCache(code);

        return list;
    }


    /**
     * find element for a date
     * @param list
     * @param index
     * @return
     */

    private StockHistory findElement(List<StockHistory> list , Integer index) {
        if (list == null) return null;

        if (index != null)
            return index < list.size()  ? list.get(index) : null;

        return null;
    }


    /**
     * filled cache
     * @param code
     * @return
     */
    private synchronized List<StockHistory> fillCache(final String code) {

        return timeSeries.extract(code,this.indexCache, this.historyCache);
    }



}