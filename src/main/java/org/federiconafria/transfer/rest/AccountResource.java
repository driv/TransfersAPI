package org.federiconafria.transfer.rest;

import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.entities.AccountBuilder;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.services.AccountService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
    @Inject
    private AccountService service;

    @GET
    @Path("/{id}")
    public Response getAccount(@PathParam("id") long id) {
        try {
            Account account = service.getAccount(id);
            return Response.ok(getOutputData(account)).build();
        } catch (EntityNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @POST
    public Response createAccount(NewAccountDTO inputData) {
        try {
            Account newAccount = new AccountBuilder()
                    .setAmount(inputData.amount)
                    .setUser(inputData.user)
                    .build();
            Account account = service.createAccount(newAccount);
            return Response.ok(getOutputData(account)).build();
        } catch (EntityCreationException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private AccountDTO getOutputData(Account account) {
        AccountDTO outputData = new AccountDTO();
        outputData.id = account.getId();
        outputData.user = account.getUser();
        outputData.amount = account.getAmount().getAmount().toString();
        return outputData;
    }

    public static class NewAccountDTO {
        public String user;
        public String amount;
    }

    public static class AccountDTO {
        public long id;
        public String user;
        public String amount;
    }

}
