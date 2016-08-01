package be.fluid_it.shiro.jee.authc;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

public class ContainerAuthenticationBridge {
    private static final Logger logger = LoggerFactory.getLogger(ContainerAuthenticationRealm.class);
    public static final String SUBJECT_BOUND_TO_THREAD_ID_KEY = "subject.bound.to.thread.id";

    public String synchronizeShiroSubjectWithJEEPrincipal(ServletRequest request) {
        String message = null;
        if (isAuthenticatedInContainer(request)) {
            logger.trace("Request [" +
                request.hashCode() +
                "] is authenticated in container ...");
            if (!isAuthenticatedInShiro()) {
                logger.trace("Shiro subject is not bound to thread [" +
                    Thread.currentThread().getId() +
                    "]");
                message = propagateJEEPrincipalToShiroSubjectMessage(request);
                propagateUserPrincipalToShiro(request);
            } else if (isAuthenticatedInShiro()) {
                if (!isSameUserAuthenticatedInContainerAndShiro(request)) {
                    logger.info("Principal bound to Request[" + request +
                        "] and subject [" +
                        SecurityUtils.getSubject().getPrincipal().toString() +
                        "] bound to thread [" +
                        Thread.currentThread().getId() +
                        "] do not match");
                    message += propagateUserPrincipalToShiro(request);
                }
            }
        }
        return message + " in thread [" +
            Thread.currentThread().getId() +
            "] ...";
    }

    private boolean isAuthenticatedInContainer(ServletRequest request) {
        return ((HttpServletRequest) request).getUserPrincipal() != null;
    }

    private boolean isAuthenticatedInShiro() {
        return SecurityUtils.getSubject() != null && SecurityUtils.getSubject().isAuthenticated();
    }

    private boolean isSameUserAuthenticatedInContainerAndShiro(ServletRequest request) {
        return ((HttpServletRequest) request).getUserPrincipal().getName().equals(SecurityUtils.getSubject().getPrincipal().toString());
    }

    private String propagateUserPrincipalToShiro(ServletRequest request) {
        Principal userPrincipal = ((HttpServletRequest) request).getUserPrincipal();
        if (userPrincipal != null) {
            long threadId = Thread.currentThread().getId();
            logger.trace("Login to Shiro in thread [" +
                threadId +
                "] ...");
            SecurityUtils.getSubject().login(new ContainerAuthenticationToken(userPrincipal));
            request.setAttribute(SUBJECT_BOUND_TO_THREAD_ID_KEY, threadId);
            logger.trace("Logged in to Shiro in thread [" +
                threadId +
                "] ...");
        }
        return propagateJEEPrincipalToShiroSubjectMessage(request);
    }

    private String propagateJEEPrincipalToShiroSubjectMessage(ServletRequest request) {
        Principal userPrincipal = ((HttpServletRequest) request).getUserPrincipal();
        String name = userPrincipal != null ? userPrincipal.getName() : null;
        return "JEE principal [" + name + "] => Shiro subject \n";
    }
}
