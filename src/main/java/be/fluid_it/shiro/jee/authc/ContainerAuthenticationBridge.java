package be.fluid_it.shiro.jee.authc;

import org.apache.shiro.SecurityUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class ContainerAuthenticationBridge {
    public String synchronizeShiroSubjectWithJEEPrincipal(ServletRequest request) {
        String message = null;
        if (isAuthenticatedInContainer(request)) {
            if (!isAuthenticatedInShiro()) {
                message = propagateJEEPrincipalToShiroSubjectMessage((HttpServletRequest) request);
                propagateUserPrincipalToShiro(request);
            } else if (isAuthenticatedInShiro()) {
                if (!isSameUserAuthenticatedInContainerAndShiro(request)) {
                    message = logoutFromShiro();
                    message += propagateUserPrincipalToShiro(request);
                }
            }
        } else {
            if (isAuthenticatedInShiro()) {
                message = logoutShiroSubjectMessage();
                logoutFromShiro();
            }
        }
        return message;
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
        SecurityUtils.getSubject().login(new ContainerAuthenticationToken(((HttpServletRequest) request).getUserPrincipal()));
        return propagateJEEPrincipalToShiroSubjectMessage(request);
    }

    private String propagateJEEPrincipalToShiroSubjectMessage(ServletRequest request) {
        return "JEE principal [" +
                ((HttpServletRequest)request).getUserPrincipal().getName() +
                "] => Shiro subject \n";
    }

    private String logoutFromShiro() {
        SecurityUtils.getSubject().logout();
        return logoutShiroSubjectMessage();
    }

    private String logoutShiroSubjectMessage() {
        return "Logout Shiro subject [" +
                SecurityUtils.getSubject().getPrincipal() +
                "]\n";
    }
}
