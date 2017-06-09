package com.heim.wowauctions.spark.rest;

import com.heim.wowauctions.spark.SparkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by sbenner on 08/06/2017.
 */
@Controller
@RequestMapping("/sparkjobs")
public class SparkController {

    @Autowired
    private SparkService sparkService;

    @RequestMapping(method = RequestMethod.GET, value = "/start", produces = "application/json")
    @ResponseBody
    public DeferredResult<String> startJob(HttpServletResponse res) {


        DeferredResult<String> result = new DeferredResult<>(180000L);

        new Thread(() -> result.setResult("Result: " + sparkService.count())).start();


        return result;

        //return new ResponseEntity("OK", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/list", produces = "application/json")
    public
    @ResponseBody
    ResponseEntity listJobs(HttpServletResponse res) {
        return new ResponseEntity("OK", HttpStatus.OK);
    }


}
