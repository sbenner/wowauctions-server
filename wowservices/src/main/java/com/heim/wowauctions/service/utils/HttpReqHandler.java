package com.heim.wowauctions.service.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 5/8/15
 * Time: 2:04 AM
 */

@Component
public class HttpReqHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpReqHandler.class);

    @Autowired
    RestTemplate restTemplate;


    @Value("${apikey}")
    private String apikey;

    public static Map<String, Integer> getServers() {
        Map<String, Integer> serverList = new HashMap<String, Integer>();

        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.wowprogress.com/realms/rank/us").get();

            Elements usServers = doc.select("#realm_list_table").select("tr");

            for (Element e : usServers) {
                if (!e.select("td").isEmpty()) {

                    String realm = e.select("a.realm").text().toLowerCase();
                    int population = Integer.valueOf(e.select("span[class=num]").text());

                    serverList.put(realm, population);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return serverList;
    }

    @SuppressWarnings("unchecked")
    public String getData(String url) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("apikey", apikey);

            return
                    restTemplate.
                            exchange(builder.build().encode().toUri(),
                                    HttpMethod.GET,
                                    requestEntity,
                                    String.class).getBody();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;

    }


    private static String parseId(String fileName) {
        return fileName.substring(fileName.indexOf("-") + 1, fileName.indexOf("."));
    }


}
