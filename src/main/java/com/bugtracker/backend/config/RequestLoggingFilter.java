package com.bugtracker.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

/**
 * Filter to log incoming requests and their headers for debugging purposes.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Run right after CORS filter
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.info("\n=== Incoming Request ===\n" +
                "Method: {}\n" +
                "URI: {}\n" +
                "Query: {}\n" +
                "Origin: {}\n" +
                "Headers: {}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                httpRequest.getQueryString(),
                httpRequest.getHeader("Origin"),
                getRequestHeaders(httpRequest));

        // Log response headers after the request is processed
        chain.doFilter(request, response);

        log.info("\n=== Response ===\n" +
                "Status: {}\n" +
                "Headers: {}",
                httpResponse.getStatus(),
                getResponseHeaders(httpResponse));
    }

    private String getRequestHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.append(headerName).append(": ").append(request.getHeader(headerName));
            if (headerNames.hasMoreElements()) {
                headers.append(", ");
            }
        }
        return headers.toString();
    }

    private String getResponseHeaders(HttpServletResponse response) {
        StringBuilder headers = new StringBuilder();
        for (String headerName : response.getHeaderNames()) {
            headers.append(headerName).append(": ").append(response.getHeader(headerName));
            if (!headerName.equalsIgnoreCase(response.getHeaderNames().toArray()[response.getHeaderNames().size() - 1])) {
                headers.append(", ");
            }
        }
        return headers.toString();
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("RequestLoggingFilter initialized");
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
