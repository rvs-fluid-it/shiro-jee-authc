package be.fluid_it.shiro.jee.authc.modules;

import be.fluid_it.guice.extensions.multibindings.shiro.realms.RealmKey;
import be.fluid_it.guice.extensions.multibindings.shiro.realms.modules.MultiRealmBinder;
import be.fluid_it.shiro.jee.authc.ContainerAuthenticationRealm;
import com.google.inject.Binder;
import com.google.inject.Module;

public class ContainerAuthenticationRealmModule implements Module {
    @Override
    public void configure(Binder binder) {
        MultiRealmBinder.newMultiRealmBinder(binder)
                .addBinding(new RealmKey(ContainerAuthenticationRealm.class))
                .to(ContainerAuthenticationRealm.class);
    }
}
