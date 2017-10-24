package com.heim.wowauctions.service.rest.controllers;

import com.heim.wowauctions.service.services.AuctionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


@Controller
@RequestMapping("/mobi/")
public class AuctionsMobiController {

    @Autowired
    AuctionsService service;

    @RequestMapping(method = RequestMethod.GET, value = "/items", produces = "application/json"
    )
    public
    @ResponseBody
    ResponseEntity getItem(HttpServletResponse res,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                           @RequestParam(value = "size", required = false, defaultValue = "20") Integer pageSize,
                           @RequestParam(value = "exact", required = false) boolean exact) throws IOException {

        if (name != null) {
            name = name.trim();
            name = name.replaceAll("[^a-zA-Z0-9 ']", "");
        }

        if (StringUtils.isEmpty(name) ||
                name.trim().length() <= 1) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity(service.getAuctions(name, pageSize, exact, page), HttpStatus.OK);
        }

    }

    @RequestMapping(method = RequestMethod.GET,
            value = "/itemchart", produces = "application/json"
    )
    public
    @ResponseBody
    ResponseEntity getItemChart(HttpServletResponse res, @RequestParam(value = "id", required = true) String id,
                                @RequestParam(value = "period", required = false) Integer period,
                                @RequestParam(value = "exact", required = false) boolean exact) throws IOException {

        long itemId;
        try {
            itemId = Long.parseLong(id);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if (itemId != 0) {
            Map<Long, Long> auctions = service.getItemChart(itemId);
            if (auctions != null && auctions.size() > 0) {
                return new ResponseEntity(auctions, HttpStatus.OK);
            } 
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);

    }


}