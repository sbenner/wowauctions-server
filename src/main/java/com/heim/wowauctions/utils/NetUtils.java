package com.heim.wowauctions.utils;



import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sergey
 * Date: 4/1/14
 * Time: 10:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetUtils {


    public static void getFileFromUrl(String url, String fileName) {

        try {

            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet getRequest = new HttpGet(url);
            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            FileOutputStream fos = new FileOutputStream(new File(fileName));
            byte[] buf = new byte[8192];
            int read;
            InputStream is = response.getEntity().getContent();

            int totalDownloaded = 0;
            while ((read = is.read(buf)) != -1) {
                totalDownloaded += read;
                System.out.print("\r bytes downloaded: " + totalDownloaded);

                fos.write(buf);

            }
            fos.close();
            System.out.println("");

        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }


    }



    public static Map<String,Integer> getServers()
    {
        Map<String,Integer> serverList = new HashMap<String,Integer>();

        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.wowprogress.com/realms/rank/us").get();

            Elements usServers = doc.select("#realm_list_table").select("tr");

            for(Element e : usServers)
            {
                if(!e.select("td").isEmpty()){

                String realm = e.select("a.realm").text().toLowerCase();
                int population = Integer.valueOf(e.select("span[class=num]").text());

                serverList.put(realm, population);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverList;
    }




    public static String getResourceFromUrl(String url) {

        String output = null;

        try {
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000)
                    .setConnectTimeout(5000)
                    .setConnectionRequestTimeout(5000)
                    .setStaleConnectionCheckEnabled(true)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .build();

            HttpGet getRequest = new HttpGet(url);

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());

            }

            output = IOUtils.toString(response.getEntity().getContent(), "UTF-8");


        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }


        return output;
    }


    public static void postToUrl(String url, String... params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(
                url);
        try {


            StringEntity input = new StringEntity("{qty:100,name:iPad 4}");
            input.setContentType("application/json");
            postRequest.setEntity(input);

            HttpResponse response = httpClient.execute(postRequest);

            if (response.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            httpClient.close();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
    }


}