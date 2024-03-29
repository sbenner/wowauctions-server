package com.heim.wowauctions.common.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 5/8/15
 * Time: 2:04 AM
 */

@Component
public class HttpReqHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpReqHandler.class);
    @Value("${wow.items.url}")
    String itemsUrl;
    @Autowired
    private RestTemplate customRestTemplate;

    @Value("${token.url}")
    String tokenUrl;

    @Value("${client.id}")
    String clientId;
    @Value("${client.secret}")
    String clientSecret;

    @Value("${server.list.url}")
    String serverListUrl;

    volatile OAuth2AccessToken token;


    @PostConstruct
    void initToken() {
        obtainToken();
    }


    public Map<String, Integer> getServers() {
        Map<String, Integer> serverList = new HashMap<>();

        Document doc = null;
        try {
            doc = Jsoup.connect(serverListUrl).get();

            Elements usServers = doc.select("#realm_list_table").select("tr");

            for (Element e : usServers) {
                if (!e.select("td").isEmpty()) {

                    String realm = e.select("a.realm").text().toLowerCase();
                    int population = Integer.parseInt(e.select("span[class=num]").text());

                    serverList.put(realm, population);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return serverList;
    }

    public String getItemsUrl(long itemId) {
        return String.format(itemsUrl, itemId);
    }

    @SuppressWarnings("unchecked")
    public ResponseEntity<?> getData(String url) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
            HttpEntity<String> requestEntity = new HttpEntity<String>("params", headers);

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(url).queryParam("access_token", token.getValue());

            return
                    customRestTemplate.
                            exchange(builder.build().encode().toUri(),
                                    HttpMethod.GET,
                                    requestEntity,
                                    String.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;

    }

    @SuppressWarnings("unchecked")
    public HttpHeaders getHeadData(String url) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(url).queryParam("access_token", token.getValue());

            return
                    customRestTemplate.
                            headForHeaders(builder.build().encode().toUri());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;

    }

    @Scheduled(fixedRate = 180000, initialDelay = 180000)
    public void obtainToken() {

        if (token == null || token.isExpired()) {

            ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
            resourceDetails.setClientSecret(clientSecret);
            resourceDetails.setClientId(clientId);
            resourceDetails.setAccessTokenUri(tokenUrl);
            resourceDetails.setGrantType("client_credentials");

            OAuth2RestTemplate oAuthRestTemplate = new OAuth2RestTemplate(resourceDetails);
            this.token = oAuthRestTemplate.getAccessToken();
        }
    }

    private String getToken() {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "client_credentials");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String enc = clientId + ":" + clientSecret;
        String encoded = Base64.getEncoder().encodeToString(enc.getBytes());
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encoded);

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(tokenUrl);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return
                customRestTemplate.
                        exchange(builder.build().encode().toUri(),
                                HttpMethod.POST,
                                request,
                                String.class).getBody();
    }


    private static String parseId(String fileName) {
        return fileName.substring(fileName.indexOf("-") + 1, fileName.indexOf("."));
    }


}
