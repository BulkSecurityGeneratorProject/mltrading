package com.mltrading.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mltrading.models.parser.ServiceParser;
import com.mltrading.models.parser.StockParser;
import com.mltrading.repository.StockRepository;
import com.mltrading.service.ExtractionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



/**
 * Created by gmo on 07/01/2016.
 */

@RestController
@RequestMapping("/api")
public class ExtractionResource {

    @javax.inject.Inject
    private StockRepository stockRepository;


    private static ExtractionService service = new ExtractionService();

    @RequestMapping(value = "/extractionAction",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public String getExtractionAction() {
        service.extractStock(stockRepository);
        return "ok";
    }


    @RequestMapping(value = "/extractionSeries",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public String getExtractionSeries() {
        service.extractFull();
        return "ok";
    }
}