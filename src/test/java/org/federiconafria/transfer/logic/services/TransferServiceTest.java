package org.federiconafria.transfer.logic.services;

import org.federiconafria.transfer.logic.api.AccountStorage;
import org.federiconafria.transfer.logic.api.IdProvider;
import org.federiconafria.transfer.logic.api.TransferQueue;
import org.federiconafria.transfer.logic.api.TransferStorage;
import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.entities.TransferBuilder;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.services.TransferService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransferServiceTest {

    @Mock
    private TransferStorage storage;
    @Captor
    private ArgumentCaptor<Transfer> insertedTransfer;
    @Mock
    private TransferQueue queue;
    @Captor
    private ArgumentCaptor<Transfer> queuedTransfer;
    private TransferService service;
    @Mock
    private AccountStorage accountStorage;
    @Mock
    private IdProvider idProvider;

    @Before
    public void setUp() throws Exception {
        service = new TransferService(storage, queue, accountStorage, idProvider);
        when(accountStorage.getAccount(anyLong())).thenReturn(null);
        when(accountStorage.getAccount(eq(11L))).thenThrow(EntityNotFoundException.class);
        when(storage.getTransfer(eq(11L))).thenThrow(EntityNotFoundException.class);
        when(idProvider.generateNextId()).thenReturn(2L);
    }

    @Test
    public void createTransfer() throws EntityCreationException {
        Transfer newTransfer = makeTransfer(1L, 2L);

        Transfer createdTransfer = service.createTransfer(newTransfer);

        assertNotNull(createdTransfer);
        assertEquals(2L, (long) createdTransfer.getId());
        Mockito.verify(storage).insertTransfer(insertedTransfer.capture());
        assertNotSame(newTransfer, insertedTransfer.getValue());
        assertSame(createdTransfer, insertedTransfer.getValue());
        Mockito.verify(queue).queueTransfer(queuedTransfer.capture());
        assertSame(insertedTransfer.getValue(), queuedTransfer.getValue());
        Mockito.verify(idProvider).generateNextId();
    }

    private Transfer makeTransfer(long l, long l2) throws EntityCreationException {
        return new TransferBuilder().setIdSourceAccount(l).setIdDestinationAccount(l2).setAmount("55.50").build();
    }

    @Test(expected = EntityCreationException.class)
    public void createTransfer_InexistentSourceAccount_ShouldFAIL() throws EntityCreationException {
        Transfer newTransfer = makeTransfer(11L, 2L);

        try {
            service.createTransfer(newTransfer);
        } finally {
            Mockito.verify(storage, never()).insertTransfer(any());
            Mockito.verify(queue, never()).queueTransfer(any());
        }
    }

    @Test(expected = EntityCreationException.class)
    public void createTransfer_InexistentDestinationAccount_ShouldFAIL() throws EntityCreationException {
        Transfer newTransfer = makeTransfer(1L, 11L);

        try {
            service.createTransfer(newTransfer);
        } finally {
            Mockito.verify(storage, never()).insertTransfer(any());
            Mockito.verify(queue, never()).queueTransfer(any());
        }
    }

    @Test(expected = EntityCreationException.class)
    public void createTransfer_SameAccounts_ShouldFAIL() throws EntityCreationException {
        try {
            service.createTransfer(makeTransfer(1L, 1L));
        } finally {
            Mockito.verify(storage, never()).insertTransfer(any());
            Mockito.verify(queue, never()).queueTransfer(any());
        }
    }

    @Test(expected = EntityNotFoundException.class)
    public void getTransfer_InexistentID_ShouldFAIL() throws EntityNotFoundException {
        service.getTransfer(11L);
    }

    @Test
    public void getTransfer_CorrectID_ShouldReturnTransfer() throws EntityNotFoundException, EntityCreationException {
        Transfer storedTransfer = makeTransfer(5L, 2L);
        when(storage.getTransfer(eq(2L))).thenReturn(storedTransfer);

        Transfer retrievedTransfer = service.getTransfer(2L);

        assertEquals(storedTransfer, retrievedTransfer);
    }

}