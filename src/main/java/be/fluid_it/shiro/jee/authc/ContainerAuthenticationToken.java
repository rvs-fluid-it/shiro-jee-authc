package be.fluid_it.shiro.jee.authc;

import org.apache.shiro.authc.AuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;

public class ContainerAuthenticationToken implements AuthenticationToken {
    private final Principal userPrincipal;
    private final Logger logger = LoggerFactory.getLogger(ContainerAuthenticationRealm.class);


    public ContainerAuthenticationToken(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    @Override
    public Object getPrincipal() {
        if (userPrincipal != null) {
            String name = userPrincipal.getName();
            if (name == null) {
                logger.info("A principal encountered without a name");
            }
            return name;
        } else {
            logger.info("A null principal encountered in a ContainerAuthenticationToken");
            return null;
        }
    }

    @Override
    public Object getCredentials() {
        return "jee-container-authenticated";
    }
}
