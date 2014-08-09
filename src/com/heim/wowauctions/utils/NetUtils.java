package com.heim.wowauctions.utils;



import java.io.*;
import java.net.MalformedURLException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.impl.client.HttpClients;


/**
 * Created with IntelliJ IDEA.
 * User: sergey
 * Date: 4/1/14
 * Time: 10:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetUtils {




    public static void getFileFromUrl(String url,String fileName){


        try {


            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet getRequest = new HttpGet(url);
            // getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            FileOutputStream fos = new FileOutputStream(new File(fileName));
            byte [] buf = new byte[8192];
            int read=0;
            InputStream is = response.getEntity().getContent();
            Console console = System.console();
            int totalDownloaded=0;
            while((read= is.read(buf))!=-1){
                totalDownloaded += read;
                 System.out.print("\r bytes downloaded: " + totalDownloaded);
                //}
                 fos.write(buf);

            }
            fos.close();
            System.out.println("");

//            fos.write(IOUtils.toByteArray(response.getEntity().getContent()));
 //
   //         fos.close();


        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }


    }


        public static String getResourceFromUrl(String url){

        String output=null;

        try {


            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet getRequest = new HttpGet(url);
          // getRequest.addHeader("accept", "application/json");

            HttpResponse response = httpClient.execute(getRequest);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }

            output = IOUtils.toString(response.getEntity().getContent());


        } catch (ClientProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }


    return   output;
    }


    public static void postToUrl(String url,String... params){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postRequest = new HttpPost(
                url);
       try
        {
        //StringEntity input = new StringEntity("{\"qty\":100,\"name\":\"iPad 4\"}");

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
