package org.federiconafria.transfer;

import org.federiconafria.transfer.logic.services.AccountService;
import org.federiconafria.transfer.logic.services.TransferExecutionService;
import org.federiconafria.transfer.logic.services.TransferService;
import org.federiconafria.transfer.queue.PendingTransfersQueue;
import org.federiconafria.transfer.rest.AppConfig;
import org.federiconafria.transfer.storage.AccountMemoryStorage;
import org.federiconafria.transfer.storage.MyIdProvider;
import org.federiconafria.transfer.storage.TransferMemoryStorage;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

public class App {
    public static void main(String[] args) throws IOException {
        AccountMemoryStorage accountStorage = new AccountMemoryStorage();
        TransferMemoryStorage transferStorage = new TransferMemoryStorage();
        AccountService accountService = new AccountService(accountStorage, new MyIdProvider());
        TransferExecutionService transferExecutionService = new TransferExecutionService(transferStorage, accountStorage);
        PendingTransfersQueue transferQueue = new PendingTransfersQueue(transferExecutionService, (n) -> System.out.println("Transfer executed: " + n), 500, Runtime.getRuntime().availableProcessors() - 2);
        TransferService transferService = new TransferService(transferStorage, transferQueue, accountStorage, new MyIdProvider());

        AppConfig config = new AppConfig(accountService, transferService);
        String baseUri = "http://localhost:8080/api/";
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), config);
        System.out.println("Starting SERVER");
        server.start();

        System.out.println("Starting QUEUE");
        new Thread(transferQueue).start();
    }
}
