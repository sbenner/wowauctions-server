package com.heim.wowauctions.service.rest.filter;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/25/14
 * Time: 1:42 AM
 */

import com.heim.wowauctions.service.utils.SignatureHelper;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class RestSignatureFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String url = SignatureHelper.createSortedUrl(request);

        String signature = request.getHeader(SignatureHelper.SIGNATURE_HEADER);
        String apiKey = request.getHeader(SignatureHelper.APIKEY_HEADER);

        long timestamp = 0;
        try{
            timestamp = Long.parseLong(request.getHeader(SignatureHelper.TIMESTAMP_HEADER));

            if(!SignatureHelper.validateTimestamp(timestamp)){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
                return;
            }

        }   catch(Exception e){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        }

        if(signature==null||apiKey==null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        }

        try {
            if (!SignatureHelper.validateSignature(url, signature)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "REST signature failed validation.");
                return;
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request.");
            return;
        }

        filterChain.doFilter(request, response);
    }

}