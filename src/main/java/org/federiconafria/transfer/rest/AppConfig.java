package org.federiconafria.transfer.rest;

import org.federiconafria.transfer.logic.services.AccountService;
import org.federiconafria.transfer.logic.services.TransferService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/accounts")
public class AppConfig extends ResourceConfig {

    public AppConfig(final AccountService accountService, final TransferService transferService) {
        packages("org.federiconafria.transfer.rest");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(accountService).to(AccountService.class);
            }
        });
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(transferService).to(TransferService.class);
            }
        });
    }

}
