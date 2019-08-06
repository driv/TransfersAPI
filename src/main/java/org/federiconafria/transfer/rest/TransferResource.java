package org.federiconafria.transfer.rest;

import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.entities.TransferBuilder;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.services.TransferService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/transfers")
@Produces(MediaType.APPLICATION_JSON)
public class TransferResource {

    @Inject
    private TransferService service;

    @GET
    @Path("/{id}")
    public Response getTransfer(@PathParam("id") long id) {
        try {
            Transfer transfer = service.getTransfer(id);
            return Response.ok(makeOutputData(transfer)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response createTransfer(NewTransferDTO inputData) {
        try {
            Transfer newTransfer = new TransferBuilder()
                    .setAmount(inputData.amount)
                    .setIdSourceAccount(inputData.idSourceAccount)
                    .setIdDestinationAccount(inputData.idDestinationAccount)
                    .build();
            Transfer transfer = service.createTransfer(newTransfer);
            return Response.ok(makeOutputData(transfer)).build();
        } catch (EntityCreationException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    private TransferDTO makeOutputData(Transfer transfer) {
        TransferDTO outputData = new TransferDTO();
        outputData.id = transfer.getId();
        outputData.amount = transfer.getAmount().getAmount().toString();
        outputData.status = transfer.getStatus().name();
        outputData.idSourceAccount = transfer.getIdSourceAccount();
        outputData.idDestinationAccount = transfer.getIdDestinationAccount();
        return outputData;
    }

    public static class TransferDTO {
        public Long id;
        public String amount;
        public long idSourceAccount;
        public long idDestinationAccount;
        public String status;
    }

    public static class NewTransferDTO {
        public String amount;
        public long idSourceAccount;
        public long idDestinationAccount;
    }

}
