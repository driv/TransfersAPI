package org.federiconafria.transfer.rest;

import org.federiconafria.transfer.services.AccountService;
import org.federiconafria.transfer.storage.AccountMemoryStorage;
import org.federiconafria.transfer.storage.MyAccountIdProvider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/accounts")
public class AppConfig extends ResourceConfig {

    public AppConfig() {
        packages("org.federiconafria.transfer.rest");
        AccountService accountService = new AccountService(new AccountMemoryStorage(), new MyAccountIdProvider());
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(accountService).to(AccountService.class);
            }
        });
    }

}
