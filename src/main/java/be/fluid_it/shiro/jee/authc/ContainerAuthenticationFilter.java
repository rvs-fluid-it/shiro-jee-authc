package be.fluid_it.shiro.jee.authc;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.DispatcherType;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.EnumSet;

public class ContainerAuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ContainerAuthenticationRealm.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isAuthenticated(request)) {
            logger.trace("Synchronize the Shiro subject bound to thread [" +
                Thread.currentThread().getId() +
                "] with the principal bound to request [" +
                request +
                "] ...");
            String message = new ContainerAuthenticationBridge().synchronizeShiroSubjectWithJEEPrincipal(request);
            if (message != null) {
                logger.info(message);
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            logger.trace("Cleaning up the thread context (including the Shiro subject)");
            ThreadContext.remove();
        }
    }

    @Override
    public void destroy() {
    }

    private boolean isAuthenticated(ServletRequest request) {
        return request instanceof HttpServletRequest && ((HttpServletRequest)request).getUserPrincipal() != null;
    }

    public static void registerFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic dynamic = servletContext.addFilter(ContainerAuthenticationFilter.class.getName(), ContainerAuthenticationFilter.class);
        dynamic.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}