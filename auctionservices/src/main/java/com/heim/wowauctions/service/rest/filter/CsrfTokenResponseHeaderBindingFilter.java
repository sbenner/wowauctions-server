package com.heim.wowauctions.service.rest.filter;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 10/23/17
 * Time: 4:43 AM
 */

import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter
public class CsrfTokenResponseHeaderBindingFilter extends OncePerRequestFilter {
     private static final String REQUEST_ATTRIBUTE_NAME = "_csrf";
     private static final String RESPONSE_HEADER_NAME = "X-CSRF-HEADER";
     private static final String RESPONSE_PARAM_NAME = "X-CSRF-PARAM";
     private static final String RESPONSE_TOKEN_NAME = "X-CSRF-TOKEN";
     private static final String EXPOSE_HEADERS= "Access-Control-Expose-Headers";

     private static final String DEFAULT_CSRF_TOKEN_ATTR_NAME = HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, javax.servlet.FilterChain filterChain) throws ServletException, IOException {
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "OPTIONS,GET,POST");

        //CsrfToken token = (CsrfToken) request.getSession().getAttribute(DEFAULT_CSRF_TOKEN_ATTR_NAME);
        CsrfToken token = (CsrfToken) request.getAttribute(REQUEST_ATTRIBUTE_NAME);
        if (token != null) {
            
            response.setHeader(RESPONSE_HEADER_NAME, token.getHeaderName());
            response.setHeader(RESPONSE_PARAM_NAME, token.getParameterName());
            response.setHeader(RESPONSE_TOKEN_NAME , token.getToken());
        }
        filterChain.doFilter(request, response);
    }
}