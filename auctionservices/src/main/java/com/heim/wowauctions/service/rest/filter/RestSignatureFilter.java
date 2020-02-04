package com.heim.wowauctions.service.rest.filter;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/25/14
 * Time: 1:42 AM
 */


import com.heim.wowauctions.common.utils.SignatureHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//@WebFilter
public class RestSignatureFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(RestSignatureFilter.class);

    @Value("${public.key}")
    String publicKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if( request.getMethod().equals("OPTIONS")||
                (!StringUtils.isEmpty(request.getPathInfo())&&
                        (request.getPathInfo().contains("/web/")||
                request.getPathInfo().endsWith(".html")||
                                request.getPathInfo().endsWith(".png")||
                                request.getPathInfo().endsWith(".css")||
                                request.getPathInfo().endsWith(".js")))) {
            filterChain.doFilter(request, response);
            return;
        }

        String url = SignatureHelper.createSortedUrl(request);

        String signature = request.getHeader(SignatureHelper.SIGNATURE_HEADER);
        String apiKey = request.getHeader(SignatureHelper.APIKEY_HEADER);

        long timestamp = 0;
        try {
            timestamp = Long.parseLong(request.getHeader(SignatureHelper.TIMESTAMP_HEADER));

            if (!SignatureHelper.validateTimestamp(timestamp)) {
                logger.error("timestamp is wrong");
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            logger.error(e.getMessage(),e);
            return;
        }

        if (signature == null || apiKey == null) {
            logger.error("apikey or signature is null");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if (!SignatureHelper.validateSignature(url, signature,publicKey)) {
                logger.error("validateSignature returned false");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

}