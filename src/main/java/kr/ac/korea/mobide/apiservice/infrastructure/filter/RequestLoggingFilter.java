package kr.ac.korea.mobide.apiservice.infrastructure.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Koo Lee on 2014-08-29.
 */
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String query = request.getParameter("query");
        String k = request.getParameter("k");
        if (!StringUtils.isEmpty(query) && !StringUtils.isEmpty(k))
            logger.debug(query + "[" + k + "]");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
