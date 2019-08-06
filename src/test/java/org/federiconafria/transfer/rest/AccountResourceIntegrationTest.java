package org.federiconafria.transfer.rest;

import org.federiconafria.transfer.logic.entities.AccountBuilder;
import org.federiconafria.transfer.logic.services.AccountService;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.rest.AccountResource.AccountDTO;
import org.federiconafria.transfer.rest.AccountResource.NewAccountDTO;
import org.federiconafria.transfer.storage.AccountMemoryStorage;
import org.federiconafria.transfer.storage.MyIdProvider;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class AccountResourceIntegrationTest extends JerseyTest {

    private AccountService accountService;

    @Override
    protected Application configure() {
        accountService = new AccountService(new AccountMemoryStorage(), new MyIdProvider());
        try {
            accountService.createAccount(new AccountBuilder()
                    .setUser("testUser")
                    .setAmount("12.50")
                    .build());
        } catch (EntityCreationException e) {
            throw new IllegalArgumentException(e);
        }
        return new ResourceConfig(AccountResource.class)
                .register(new Binder());
    }

    @Test
    public void getAccount_NonExistentId_NOT_FOUNDResponse() {
        Response response = target("/accounts/9").request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getAccount_correctRequest_OKResponse() {
        Response response = target("/accounts/1").request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        AccountDTO output = response.readEntity(AccountDTO.class);
        assertEquals("testUser", output.user);
        assertEquals("12.50", output.amount);
        assertEquals(1, output.id);
    }

    @Test
    public void createAccount_correctRequest_OKResponse() {
        NewAccountDTO input = new NewAccountDTO();
        input.amount = "12.55";
        input.user = "testUser";
        Response response = target("/accounts/").request().post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("Http response should be 200: ", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("It should be JSON", MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        AccountDTO output = response.readEntity(AccountDTO.class);
        assertEquals(2, output.id);
    }

    @Test
    public void createAccount_IncorrectAmount_BadRequestResponse() {
        NewAccountDTO input = new NewAccountDTO();
        input.amount = "-12.55";
        input.user = "testUser";
        Response response = target("/accounts/").request().post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("Http response should be 400: ", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    class Binder extends AbstractBinder {
        @Override
        protected void configure() {
            bindFactory(new Factory<AccountService>() {
                @Override
                public AccountService provide() {
                    return accountService;
                }

                @Override
                public void dispose(AccountService instance) {

                }
            }).to(AccountService.class);
        }
    }

}