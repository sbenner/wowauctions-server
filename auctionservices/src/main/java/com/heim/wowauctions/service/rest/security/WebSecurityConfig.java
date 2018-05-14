package com.heim.wowauctions.service.rest.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 10/23/17
 * Time: 3:46 AM
 */
@EnableWebSecurity
public class WebSecurityConfig extends
        WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().requireCsrfProtectionMatcher(new RequestMatcher() {
            private Pattern allowedMethods = Pattern.compile("^(HEAD|TRACE|OPTIONS)$");
            private RegexRequestMatcher apiMatcher =
                    new RegexRequestMatcher("/v[0-9]*/web/.*", null);

            @Override
            public boolean matches(HttpServletRequest request) {
                // No CSRF due to allowedMethod
                if(StringUtils.isEmpty(request.getPathInfo())
                        ||
                        request.getPathInfo().contains("/mobi/"))return false;
              
                if(allowedMethods.matcher(request.getMethod()).matches()
                        ||(request.getPathInfo().endsWith(".html")||
                            request.getPathInfo().endsWith(".css")||
                        request.getPathInfo().endsWith(".png")||
                        request.getPathInfo().endsWith(".js")))
                    return false;

                // No CSRF due to api call
                if(apiMatcher.matches(request))
                    return false;

                // CSRF for everything else that is not an API call or an allowedMethod
                return true;
            }
        });
    }
}