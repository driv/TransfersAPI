package org.federiconafria.transfer.rest;

import org.federiconafria.transfer.entities.Account;
import org.federiconafria.transfer.entities.AccountBuilder;
import org.federiconafria.transfer.services.AccountService;
import org.federiconafria.transfer.services.exceptions.AccountNotFoundException;

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
            AccountDTO outputData = new AccountDTO();
            outputData.id = account.getId();
            outputData.amount = account.getAmount().getAmount().toString();
            outputData.user = account.getUser();
            return Response.ok(outputData).build();
        } catch (AccountNotFoundException e) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @POST
    public Response createAccount(NewAccountDTO inputData) {
        Account newAccount = new AccountBuilder()
                .setAmount(inputData.amount)
                .setUser(inputData.user)
                .build();
        Account account = service.createAccount(newAccount);
        AccountDTO outputData = new AccountDTO();
        outputData.id = account.getId();
        outputData.user = account.getUser();
        outputData.amount = account.getAmount().getAmount().toString();
        return Response.ok(outputData).build();
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
