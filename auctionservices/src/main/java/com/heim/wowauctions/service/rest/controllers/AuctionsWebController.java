package com.heim.wowauctions.service.rest.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "localhost", maxAge = 3600, exposedHeaders = "X-CSRF-TOKEN")
@RestController
@RequestMapping("/web/")
public class AuctionsWebController extends CommonController {
}
