package be.fluid_it.shiro.jee.authc;

import org.apache.shiro.authc.AuthenticationToken;

import java.security.Principal;

public class ContainerAuthenticationToken implements AuthenticationToken {
    private final Principal userPrincipal;

    public ContainerAuthenticationToken(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    @Override
    public Object getPrincipal() {
        return userPrincipal.getName();
    }

    @Override
    public Object getCredentials() {
        return "jee-container-authenticated";
    }
}
