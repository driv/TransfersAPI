package org.federiconafria.transfer.queue;

import org.federiconafria.transfer.logic.api.TransferQueue;
import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.services.TransferExecutionService;

import java.util.concurrent.*;
import java.util.function.Consumer;

public class PendingTransfersQueue implements TransferQueue, Runnable {
    private final BlockingQueue<Transfer> pendingTransfers;
    private final TransferExecutionService service;
    private final int threads;
    private final Consumer<Long> listener;

    public PendingTransfersQueue(TransferExecutionService service, Consumer<Long> listener, int capacity, int threads) {
        this.pendingTransfers = new ArrayBlockingQueue<>(capacity);
        this.service = service;
        this.threads = threads;
        this.listener = listener;
    }

    @Override
    public void queueTransfer(Transfer newTransfer) {
        pendingTransfers.add(newTransfer);
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        while (!Thread.interrupted()) {
            try {
                Long id = pendingTransfers.take().getId();
                Future<Long> future = pool.submit(() -> PendingTransfersQueue.this.executeTransfer(id), id);
                makeCompletableFuture(future).thenAccept(this.listener);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        pool.shutdown();
    }

    private void executeTransfer(Long id) {
        try {
            service.execute(id);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Long> makeCompletableFuture(Future<Long> submit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return submit.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
