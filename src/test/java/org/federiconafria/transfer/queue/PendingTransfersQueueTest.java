package org.federiconafria.transfer.queue;

import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.entities.AccountBuilder;
import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.entities.TransferBuilder;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.services.TransferExecutionService;
import org.federiconafria.transfer.storage.AccountMemoryStorage;
import org.federiconafria.transfer.storage.MyIdProvider;
import org.federiconafria.transfer.storage.TransferMemoryStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PendingTransfersQueueTest {

    private final MyIdProvider transferId = new MyIdProvider();
    private final TransferMemoryStorage transferStorage = new TransferMemoryStorage();
    private final AccountMemoryStorage accountStorage = new AccountMemoryStorage();
    private final MyIdProvider accountId = new MyIdProvider();
    private PendingTransfersQueue queue;
    private Thread thread;
    private CountDownLatch latch;

    @Before
    public void setUp() throws Exception {
        TransferExecutionService service = new TransferExecutionService(transferStorage, accountStorage);
        Consumer<Long> consumer = f -> latch.countDown();
        queue = new PendingTransfersQueue(service, consumer, 200, 6);

        thread = new Thread(queue);
        thread.start();
        putAccount();
        putAccount();
    }

    @Test
    public void singleTransfer_Successful() throws EntityCreationException, InterruptedException, EntityNotFoundException {
        latch = new CountDownLatch(1);

        queue.queueTransfer(makeTransfer("10"));

        waitForTransfersCompletion();
        assertEquals("Transfer status", Transfer.Status.EXECUTED, getTransfer(1L).getStatus());
    }

    @Test
    public void singleTransfer_Failed() throws EntityCreationException, InterruptedException, EntityNotFoundException {
        latch = new CountDownLatch(1);

        queue.queueTransfer(makeTransfer("150"));

        waitForTransfersCompletion();
        assertEquals("Transfer status", Transfer.Status.FAILED, getTransfer(1L).getStatus());
    }

    @Test
    public void twoTransfers_BothSuccessful() throws EntityCreationException, InterruptedException, EntityNotFoundException {
        int amount = 2;
        latch = new CountDownLatch(amount);

        queue.queueTransfer(makeTransfer("10"));
        queue.queueTransfer(makeTransfer("10"));

        waitForTransfersCompletion();

        assertEquals("Pending transfers", 0, countByStatus(amount, Transfer.Status.PENDING));
        assertEquals("Pending transfers", 2, countByStatus(amount, Transfer.Status.EXECUTED));
        assertEquals("Pending transfers", 0, countByStatus(amount, Transfer.Status.FAILED));
    }

    @Test
    public void twoTransfers_OneSuccessful() throws EntityCreationException, InterruptedException, EntityNotFoundException {
        int amount = 2;
        latch = new CountDownLatch(amount);

        queue.queueTransfer(makeTransfer("55"));
        queue.queueTransfer(makeTransfer("55"));

        waitForTransfersCompletion();

        assertEquals("Pending transfers", 0, countByStatus(amount, Transfer.Status.PENDING));
        assertEquals("Executed transfers", 1, countByStatus(amount, Transfer.Status.EXECUTED));
        assertEquals("Failed transfers", 1, countByStatus(amount, Transfer.Status.FAILED));
    }

    @Test
    public void _100Transfers_50Successful() throws EntityCreationException, InterruptedException, EntityNotFoundException {
        int amount = 100;
        latch = new CountDownLatch(amount);

        IntStream.rangeClosed(1, 100).forEach((n) -> queue.queueTransfer(makeTransfer("2")));

        waitForTransfersCompletion();

        assertEquals("Pending transfers", 0, countByStatus(amount, Transfer.Status.PENDING));
        assertEquals("Executed transfers", 50, countByStatus(amount, Transfer.Status.EXECUTED));
        assertEquals("Failed transfers", 50, countByStatus(amount, Transfer.Status.FAILED));
    }

    private long countByStatus(int amount, Transfer.Status status) {
        return IntStream.rangeClosed(1, amount).asLongStream()
                .mapToObj(this::getTransfer)
                .filter(t -> t.getStatus() == status)
                .count();
    }

    private Transfer getTransfer(long id) {
        try {
            return transferStorage.getTransfer(id);
        } catch (EntityNotFoundException e) {
            fail(e.getMessage());
            return null;
        }
    }

    private void waitForTransfersCompletion() throws InterruptedException {
        latch.await(68, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() {
        thread.interrupt();
    }

    private void putAccount() throws EntityCreationException {
        long id = accountId.generateNextId();
        Account account = new Account(id, new AccountBuilder()
                .setUser("user" + id)
                .setAmount("100")
                .build());
        accountStorage.insertAccount(account);
    }

    private Transfer makeTransfer(String amount) {
        try {
            Transfer transfer = new Transfer(transferId.generateNextId(), new TransferBuilder()
                    .setAmount(amount)
                    .setIdSourceAccount(1L)
                    .setIdDestinationAccount(2L)
                    .build());
            transferStorage.insertTransfer(transfer);
            return transfer;
        } catch (EntityCreationException e) {
            throw new RuntimeException(e);
        }
    }
}