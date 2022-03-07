package com.cvc.consullthotels.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {


    @Autowired
    private TokenProvider jwtTokenUtil;

    @Value("${x.ily.api.appkey}")
    private String appKeyValue;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String jwtToken = jwtTokenUtil.readAuthorizationToken(request);

        if (jwtTokenUtil.isValidToken(jwtToken)) {
            ApiKeyAuthenticationToken apiToken = new ApiKeyAuthenticationToken(appKeyValue, AuthorityUtils.NO_AUTHORITIES);
                SecurityContextHolder.getContext().setAuthentication(apiToken);
        }

        chain.doFilter(request, response);
    }


}
