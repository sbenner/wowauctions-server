package com.heim.wowauctions.common.utils;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/25/14
 * Time: 1:42 AM
 */

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.net.URLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Component
public class SignatureHelper {


    public static final String API_KEY = "RS00001";
    private static final Logger logger = LoggerFactory.getLogger(SignatureHelper.class);
    public static final String APIKEY_HEADER = "apikey";
    public static final String TIMESTAMP_HEADER = "timestamp";
    public static final String SIGNATURE_HEADER = "signature";
    private static final List<String> SIGNATURE_KEYWORDS = Arrays.asList(APIKEY_HEADER, TIMESTAMP_HEADER);

    private static final String ALGORITHM = "DSA";

    public static <T> String createSignature(T request, String privateKey) throws Exception {


        String sortedUrl = createSortedUrl(request);

        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(StringUtils.getBytesUtf8(privateKey)));

        Signature sig = Signature.getInstance(ALGORITHM);
        sig.initSign(keyFactory.generatePrivate(privateKeySpec));
        sig.update(StringUtils.getBytesUtf8(sortedUrl));

        return Base64.encodeBase64String(sig.sign());
    }

    public static boolean validateSignature(String url, String signatureString,String publicKey) throws Exception {

        logger.info("url to verify: "+url);
        logger.info("signatureString to verify: "+signatureString);
        //  String publicKey = getPublicKey();
        if (publicKey == null) {
            logger.error("public key is NULL!!!!");
            return false;
        }

        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initVerify(decodePublicKey(publicKey));
        signature.update(StringUtils.getBytesUtf8(url));

        try {

            return signature.verify(Base64.decodeBase64(signatureString));

        } catch (SignatureException e) {
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    public static boolean validateTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        if (timestamp > currentTime + 60000 ||
                timestamp < currentTime - 60000) {
            return false;
        }
        return true;
    }

    public static <T> String createSortedUrl(T request) {

        TreeMap<String, String> headersAndParams = new TreeMap<String, String>();

        List<String> e = new ArrayList<String>();
        if (request instanceof HttpServletRequest)
            e = Collections.list(((HttpServletRequest) request).getHeaderNames());
        if (request instanceof ClientHttpRequest)    //this one is needed for unit test
            e.addAll(((ClientHttpRequest) request).getHeaders().keySet());

        for (String key : e) {
            if (SIGNATURE_KEYWORDS.contains(key)) {

                if (request instanceof HttpServletRequest)
                    headersAndParams.put(key, ((HttpServletRequest) request).getHeader(key));
                if (request instanceof ClientHttpRequest)
                    headersAndParams.put(key, ((ClientHttpRequest) request).getHeaders().getFirst(key));

            }
        }
        URLCodec code = new URLCodec();
        if (request instanceof HttpServletRequest) {
            try {
                headersAndParams.put("query", code.decode(((HttpServletRequest) request).getQueryString()));
            } catch (DecoderException e1) {
                logger.error(e1.getMessage(),e1);
            }
            return createSortedUrl(
                    ((HttpServletRequest) request).getServletPath(),
                    headersAndParams);
        }
        if (request instanceof ClientHttpRequest) {
            try {
                headersAndParams.put("query", code.decode(((ClientHttpRequest) request).getURI().getQuery()));
            } catch (DecoderException e1) {
                logger.error(e1.getMessage(),e1);
            }
            return createSortedUrl(
                    ((ClientHttpRequest) request).getURI().getPath(),
                    headersAndParams);

        }
        return null;
    }

    public static String createSortedUrl(String url, TreeMap<String, String> headersAndParams) {
        // build the url with headers and parms sorted

        logger.info("url "+url);

        for(Map.Entry e: headersAndParams.entrySet()){
            logger.info("key "+e.getKey());
            logger.info("value "+e.getValue());
        }


        String params =
                headersAndParams.get("query") != null ? headersAndParams.get("query") : "";

        StringBuilder sb = new StringBuilder();
        if (!url.endsWith("?")) url += "?";

        sb.append(url);

        if (params.length() > 0)
            sb.append(params).append("?apikey=").append(headersAndParams.get("apikey"))
                    .append("@timestamp=").append(headersAndParams.get("timestamp"));


        return sb.toString();
    }

//    //test and generator
//    public static void main(String[] args) throws Exception {
//
//        // Generate a 1024-bit Digital Signature Algorithm (DSA) key pair
//        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
//        keyGen.initialize(1024);
//        KeyPair keypair = keyGen.genKeyPair();
//        PrivateKey privateKey = keypair.getPrivate();
//        PublicKey publicKey = keypair.getPublic();
//
//        // Get the bytes of the public and private keys (these go in the database with API Key)
//        byte[] privateKeyEncoded = privateKey.getEncoded();
//        byte[] publicKeyEncoded = publicKey.getEncoded();
//
//        System.out.println("Private Key: " + Base64.encodeBase64String(privateKeyEncoded));
//        System.out.println("Public Key: " + Base64.encodeBase64String(publicKeyEncoded));
//
//    }


    private static PublicKey decodePublicKey(String publicKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(StringUtils.getBytesUtf8(publicKey)));
        return keyFactory.generatePublic(publicKeySpec);
    }
}