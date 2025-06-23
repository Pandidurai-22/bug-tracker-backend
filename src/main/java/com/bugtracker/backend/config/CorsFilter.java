package com.bugtracker.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    // List of allowed origins - add more as needed
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
        "https://bugtrackerclient-mu.vercel.app",
        "http://localhost:3000"  // For local development
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
        throws IOException, ServletException {
        
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        log.info("CORS Filter - Request from origin: {}", origin);
        
        // Validate origin
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            log.debug("CORS Filter - Origin {} is allowed", origin);
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, Referer, Connection, User-Agent, Authorization, authorization, token, X-XSRF-TOKEN, X-Requested-With");
            response.setHeader("Access-Control-Expose-Headers", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
        } else if (origin != null) {
            log.warn("CORS Filter - Origin not allowed: {}", origin);
        }

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("CORS Filter - Handling OPTIONS preflight request for: {} {}", 
                    request.getMethod(), request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        log.info("CORS Filter - Proceeding with request: {} {}", request.getMethod(), request.getRequestURI());
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("CORS Filter initialized");
    }

    @Override
    public void destroy() {
        log.info("CORS Filter destroyed");
    }
}
