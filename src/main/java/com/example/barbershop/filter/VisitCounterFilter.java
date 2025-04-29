package com.example.barbershop.filter;

import com.example.barbershop.service.VisitCounter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class VisitCounterFilter implements Filter {

    private final VisitCounter visitCounter;

    public VisitCounterFilter(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();

        if (requestUri.startsWith("/barbers")) {
            visitCounter.incrementVisit(requestUri);
        }
        if (requestUri.startsWith("/locations")) {
            visitCounter.incrementVisit(requestUri);
        }

        chain.doFilter(request, response);
    }
}