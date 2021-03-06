package com.mltrading.models.parser.impl;


import com.mltrading.models.parser.ParserCommon;
import com.mltrading.models.parser.StockParser;
import com.mltrading.models.stock.cache.CacheStockGeneral;
import com.mltrading.models.stock.Stock;
import com.mltrading.models.stock.StockGeneral;
import com.mltrading.repository.StockRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmo on 05/01/2016.
 */
public class StockParserInvestir extends ParserCommon implements StockParser{

    //http://investir.lesechos.fr/cours/profil-societe-action-orange,xpar,ora,fr0000133308,isin.html
    //https://investir.lesechos.fr/cours/profil-societe-action-orange,xpar,ora,fr0000133308,isin.html
    //https://investir.lesechos.fr/cours/profil-societe-action-peugeot,xpar,ug,fr0000121501,isin.html
    static String base = "https://investir.lesechos.fr/cours/profil-societe-action-";
    static String sep = ",";
    static String end = ",isin.html";



    @Override
    public void fetch(StockRepository repository) {
        loader(repository);
    }

    @Override
    public void fetch(StockRepository repository, String codif) {
        StockGeneral sg = CacheStockGeneral.getIsinExCache().get("codif");

        loader(repository, sg);
    }


    private void loader(StockRepository repository) {
        for (StockGeneral g: CacheStockGeneral.getIsinCache().values()) {
            loader(repository, g);
        }

    }


    private void loader(StockRepository repository, StockGeneral g ){

            String url = base + CacheStockGeneral.getIsinCache().get(g.getCode()).getName().toLowerCase().replaceAll(" ","-") + sep + g.getPlace().toLowerCase() + sep  + g.getRealCodif().toLowerCase() + sep + g.getCode().toLowerCase() +end;


            try {
                String text;

                System.out.println(url);
                text = loadUrl(new URL(url));

                if (g.getCodif().toLowerCase().equals("urw")) {
                    Stock stock = new Stock("Compartiment A", "CAC 40", "URW", "FR0013326246", "NA", "NA", "SRD PEA", "138 296 941", "18 981 M€", "ND", "ND", "ND", "FRFIN");
                    repository.save(stock);
                    return;
                }

                if (g.getCodif().toLowerCase().equals("mt")) {
                    Stock stock = new Stock("Compartiment A", "CAC 40", "MT", "LU1598757687", "NA", "NA", "SRD PEA", "1 021 903 623", "13 756 M€", "ND", "ND", "ND", "FRBM");
                    repository.save(stock);
                    return;
                }

                if (text != null) {
                    Document doc = Jsoup.parse(text);

                    String sector=g.getSector();

                    String marche = doc.select("div.bloc-tabs2").get(0).getAllElements().get(3).text();
                    String indice = doc.select("div.bloc-tabs2").get(0).getAllElements().get(6).text();
                    String codeif = doc.select("div.bloc-tabs2").get(0).getAllElements().get(9).text();
                    String code = doc.select("div.bloc-tabs2").get(0).getAllElements().get(12).text();
                    String bloomberg = doc.select("div.bloc-tabs2").get(0).getAllElements().get(15).text();
                    String reuters = doc.select("div.bloc-tabs2").get(0).getAllElements().get(18).text();
                    String eligib = doc.select("div.bloc-tabs2").get(0).getAllElements().get(21).text();
                    String titleNb = doc.select("div.bloc-tabs2").get(0).getAllElements().get(24).text();
                    String capitalization = doc.select("div.bloc-tabs2").get(0).getAllElements().get(27).text();
                    String ownFounds = doc.select("div.bloc-tabs2").get(0).getAllElements().get(30).text();
                    String debt = doc.select("div.bloc-tabs2").get(0).getAllElements().get(33).text();
                    String netDebt="";
                    if (doc.select("div.bloc-tabs2").get(0).getAllElements().size() > 35) {
                        netDebt = doc.select("div.bloc-tabs2").get(0).getAllElements().get(36).text();
                    }

                    Elements dir = doc.select(".effectifs").toggleClass("flex-viewport").select("li");
                    List<String> peoples = new ArrayList<String>();
                    for (Element p:dir) {
                        String personn = p.text();
                        peoples.add(personn);
                    }

                    Stock stock = new Stock(marche, indice, codeif, code, bloomberg, reuters, eligib, titleNb, capitalization, ownFounds, debt, netDebt, sector);

                    repository.save(stock);
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR for : " + g.getName());
            }



        }



    }

