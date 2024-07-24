package ecommerce.accountmanagement.config;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Enumeration;

@Configuration
@Order(1)
public class RequestLoggingFilterConfig implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) {}

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        logRequestDetails(httpRequest);
        chain.doFilter(request, response);
    }

    private void logRequestDetails(final HttpServletRequest request) {
        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP Method: ").append(request.getMethod()).append("\n");
        sb.append("Request URI: ").append(request.getRequestURI()).append("\n");
        sb.append("Query Parameters: ").append(request.getQueryString()).append("\n");

        sb.append("Request Parameters: \n");
        final Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            final String paramName = parameterNames.nextElement();
            final String paramValue = request.getParameter(paramName);
            sb.append(paramName).append(": ").append(paramValue).append("\n");
        }

        System.out.println(sb);
    }

    @Override
    public void destroy() {}
}
