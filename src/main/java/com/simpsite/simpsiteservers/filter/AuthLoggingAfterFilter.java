package com.simpsite.simpsiteservers.Filter;

import jakarta.servlet.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.logging.Logger;

public class AuthLoggingAfterFilter implements Filter{
    private final Logger LOG = Logger.getLogger(AuthLoggingAfterFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(null != authentication){
            LOG.info("User: "+authentication.getName()+" is successfully authenticated.");
        }
        chain.doFilter(request,response);
    }

}
