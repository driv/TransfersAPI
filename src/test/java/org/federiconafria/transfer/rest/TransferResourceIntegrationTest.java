package org.federiconafria.transfer.rest;

import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.entities.AccountBuilder;
import org.federiconafria.transfer.logic.entities.TransferBuilder;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.services.TransferService;
import org.federiconafria.transfer.rest.AccountResource.AccountDTO;
import org.federiconafria.transfer.rest.TransferResource.NewTransferDTO;
import org.federiconafria.transfer.rest.TransferResource.TransferDTO;
import org.federiconafria.transfer.storage.AccountMemoryStorage;
import org.federiconafria.transfer.storage.MyIdProvider;
import org.federiconafria.transfer.storage.TransferMemoryQueue;
import org.federiconafria.transfer.storage.TransferMemoryStorage;
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

public class TransferResourceIntegrationTest extends JerseyTest {

    private TransferService transferService;

    @Override
    protected Application configure() {
        AccountMemoryStorage accountStorage = new AccountMemoryStorage();
        transferService = new TransferService(
                new TransferMemoryStorage(), new TransferMemoryQueue(), accountStorage, new MyIdProvider());
        try {
            Account sampleAccount = new AccountBuilder()
                    .setUser("tstUser")
                    .setAmount("55.55")
                    .build();
            accountStorage.insertAccount(new Account(1, sampleAccount));
            accountStorage.insertAccount(new Account(2, sampleAccount));
            transferService.createTransfer(new TransferBuilder()
                    .setAmount("10.00")
                    .setIdSourceAccount(1L)
                    .setIdDestinationAccount(2L)
                    .build());
        } catch (EntityCreationException e) {
            throw new IllegalArgumentException(e);
        }
        return new ResourceConfig(TransferResource.class).register(new Binder());
    }


    @Test
    public void getTransfer_NonExistentId_NOT_FOUNDResponse() {
        Response response = target("/transfers/9").request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getTransfer_correctRequest_OKResponse() {
        Response response = target("/transfers/1").request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        TransferDTO output = response.readEntity(TransferDTO.class);
        assertEquals("10.00", output.amount);
        assertEquals("PENDING", output.status);
        assertEquals(1L, (long) output.id);
    }

    @Test
    public void createTransfer_incorrectAmount_BadRequestResponse() {
        NewTransferDTO input = new NewTransferDTO();
        input.amount = "-10.00";
        input.idSourceAccount = 2L;
        input.idDestinationAccount = 1L;

        Response response = target("/transfers/").request().post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("Response code", Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void createTransfer_correctRequest_OKResponse() {
        NewTransferDTO input = new NewTransferDTO();
        input.amount = "10.00";
        input.idSourceAccount = 2L;
        input.idDestinationAccount = 1L;

        Response response = target("/transfers/").request().post(Entity.entity(input, MediaType.APPLICATION_JSON_TYPE));

        assertEquals("HTTP code:", Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Media Type", MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        TransferDTO output = response.readEntity(TransferDTO.class);
        assertEquals("ID", 2L, (long)output.id);
        assertEquals("Status", "PENDING", output.status);
    }

    private class Binder extends AbstractBinder {
        @Override
        protected void configure() {
            bindFactory(new Factory<TransferService>() {
                @Override
                public TransferService provide() {
                    return transferService;
                }

                @Override
                public void dispose(TransferService instance) {

                }
            }).to(TransferService.class);
        }
    }
}
