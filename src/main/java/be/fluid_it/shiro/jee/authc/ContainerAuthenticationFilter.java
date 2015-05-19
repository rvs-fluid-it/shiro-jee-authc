package be.fluid_it.shiro.jee.authc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;

public class ContainerAuthenticationFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ContainerAuthenticationRealm.class);
    private ContainerAuthenticationBridge containerAutcBridge;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.containerAutcBridge = new ContainerAuthenticationBridge();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.trace("Synchronize the Shiro subject with the JEE principal ...");
        String message = containerAutcBridge.synchronizeShiroSubjectWithJEEPrincipal(request);
        if (message != null) {
            logger.info(message);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    public static void registerFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic dynamic = servletContext.addFilter(ContainerAuthenticationFilter.class.getName(), ContainerAuthenticationFilter.class);
        dynamic.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}