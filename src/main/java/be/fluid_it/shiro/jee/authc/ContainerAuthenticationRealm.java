package be.fluid_it.shiro.jee.authc;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainerAuthenticationRealm implements Realm {
    private static final Logger log = LoggerFactory.getLogger(ContainerAuthenticationRealm.class);

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof ContainerAuthenticationToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if (supports(token)) {
            return new SimpleAccount(token.getPrincipal(), token.getCredentials(), getName());
        }
        return null;
    }
}
