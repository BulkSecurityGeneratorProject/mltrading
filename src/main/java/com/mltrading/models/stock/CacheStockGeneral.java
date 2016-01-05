package com.mltrading.models.stock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by gmo on 17/06/2015.
 */






public class CacheStockGeneral {

    private static final Map<String, StockGeneral> ISINMap;
    static {
        Map<String, StockGeneral> aMap = new HashMap<>();
        aMap.put("BE0003470755", new StockGeneral("BE0003470755","Solvay","SOLB","xbru","BR"));
        aMap.put("CH0012214059", new StockGeneral("CH0012214059","LafargeHolcim-ltd","LHN","xpar","PA"));
        aMap.put("FR0000045072", new StockGeneral("FR0000045072","Credit Agricole","ACA","xpar","PA"));
        aMap.put("FR0000073272", new StockGeneral("FR0000073272","Safran","SAF","xpar","PA"));
        aMap.put("FR0000120073", new StockGeneral("FR0000120073","Air Liquide","AI","xpar","PA"));
        aMap.put("FR0000120172", new StockGeneral("FR0000120172","Carrefour","CA","xpar","PA"));
        aMap.put("FR0000120271", new StockGeneral("FR0000120271","Total","FP","xpar","PA"));
        aMap.put("FR0000120321", new StockGeneral("FR0000120321","Loreal","OR","xpar","PA"));
        aMap.put("FR0000120404", new StockGeneral("FR0000120404", "Accor Hotels","AC","xpar","PA"));
        aMap.put("FR0000120503", new StockGeneral("FR0000120503","Bouygues","EN","xpar","PA"));
        aMap.put("FR0000120578", new StockGeneral("FR0000120578","Sanofi","SAN","xpar","PA"));
        aMap.put("FR0000120628", new StockGeneral("FR0000120628","Axa","CS","xpar","PA"));
        aMap.put("FR0000120644", new StockGeneral("FR0000120644","Danone","BN","xpar","PA"));
        aMap.put("FR0000120693", new StockGeneral("FR0000120693","Pernod Ricard","RI","xpar","PA"));
        aMap.put("FR0000121014", new StockGeneral("FR0000121014","Lvmh","MC","xpar","PA"));
        aMap.put("FR0000121261", new StockGeneral("FR0000121261","Michelin","ML","xpar","PA"));
        aMap.put("FR0000121485", new StockGeneral("FR0000121485","Kering","KER","xpar","PA"));
        aMap.put("FR0000121501", new StockGeneral("FR0000121501","Peugeot","UG","xpar","PA"));
        aMap.put("FR0000121667", new StockGeneral("FR0000121667","Essilor Intl","EI","xpar","PA"));
        aMap.put("FR0000121972", new StockGeneral("FR0000121972","Schneider Electric","SU","xpar","PA"));
        aMap.put("FR0000124141", new StockGeneral("FR0000124141","Veolia environnement","VIE","xpar","PA"));
        aMap.put("FR0000124711", new StockGeneral("FR0000124711","Unibail-Rodamco","UL","xswx","PA"));
        aMap.put("FR0000125007", new StockGeneral("FR0000125007","Saint Gobain","SGO","xpar","PA"));
        aMap.put("FR0000125338", new StockGeneral("FR0000125338","Cap Gemini","CAP","xpar","PA"));
        aMap.put("FR0000125486", new StockGeneral("FR0000125486","Vinci","DG","xpar","PA"));
        aMap.put("FR0000127771", new StockGeneral("FR0000127771","Vivendi","VIV","xpar","PA"));
        aMap.put("FR0000130007", new StockGeneral("FR0000130007","Alcatel-Lucent","ALU","xpar","PA"));
        aMap.put("FR0000130338", new StockGeneral("FR0000130338","Valeo","FR","xpar","PA"));
        aMap.put("FR0000130577", new StockGeneral("FR0000130577","Publicis Groupe","PUB","xpar","PA"));
        aMap.put("FR0000130809", new StockGeneral("FR0000130809","Societe Generale","GLE","xpar","PA"));
        aMap.put("FR0000131104", new StockGeneral("FR0000131104","Bnp Paribas","BNP","xpar","PA"));
        aMap.put("FR0000131708", new StockGeneral("FR0000131708","Technip","TEC","xpar","PA"));
        aMap.put("FR0000131906", new StockGeneral("FR0000131906","Renault","RNO","xpar","PA"));
        aMap.put("FR0000133308", new StockGeneral("FR0000133308","Orange","ORA","xpar","PA"));
        aMap.put("FR0010208488", new StockGeneral("FR0010208488","Engie","ENGI","xpar","PA"));
        aMap.put("FR0010220475", new StockGeneral("FR0010220475","Alstom","ALO","xpar","PA"));
        aMap.put("FR0010242511", new StockGeneral("FR0010242511","EDF","EDF","xpar","PA"));
        aMap.put("FR0010307819", new StockGeneral("FR0010307819","Legrand SA","LR","xpar","PA"));
        aMap.put("LU0323134006", new StockGeneral("LU0323134006","ArcelorMittal","MT","xams","PA"));
        aMap.put("NL0000235190", new StockGeneral("NL0000235190", "Airbus","AIR","xpar","PA"));

        ISINMap = Collections.unmodifiableMap(aMap);
    }



    private Map<String,StockGeneral> CSCache = new TreeMap<>();






    private CacheStockGeneral() {
    }

    private static class CacheStockGeneralHolder {
        /** Instance unique non pre-initialise */
        private final static CacheStockGeneral instance = new CacheStockGeneral();
    }


    public static Map<String,StockGeneral> getCache() {
        return CacheStockGeneralHolder.instance.CSCache;
    }

    public static Map<String,StockGeneral> getIsinCache() {
        return ISINMap;
    }

    public static String getCode(String codif) {
        for (StockGeneral g: CacheStockGeneral.getIsinCache().values()) {
            if (g.getCodif().equals(codif)) return g.getCode();
        }
        return null;
    }

    public static String getPlace(String codif) {
        for (StockGeneral g: CacheStockGeneral.getIsinCache().values()) {
            if (g.getCodif().equals(codif)) return g.getPlace();
        }
        return null;
    }

    public static void removeCache() {
        CacheStockGeneralHolder.instance.CSCache.clear();
    }

}
