package com.heim.wowauctions.service.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "localhost", maxAge = 3600, exposedHeaders = "X-CSRF-TOKEN")
@Controller
@RequestMapping("/web/")
public class AuctionsWebController extends CommonController {
}
