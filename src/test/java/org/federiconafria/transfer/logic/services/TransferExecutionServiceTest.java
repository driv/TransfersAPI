package org.federiconafria.transfer.logic.services;

import org.federiconafria.transfer.logic.api.AccountStorage;
import org.federiconafria.transfer.logic.api.TransferStorage;
import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.entities.AccountBuilder;
import org.federiconafria.transfer.logic.entities.Transfer;
import org.federiconafria.transfer.logic.entities.TransferBuilder;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransferExecutionServiceTest {

    @Mock
    AccountStorage accountStorage;
    @Mock
    TransferStorage transferStorage;
    @Captor
    ArgumentCaptor<Transfer> transferCaptor;
    @Captor
    ArgumentCaptor<Account> accountCaptor;

    @Before
    public void init() throws EntityNotFoundException, EntityCreationException {
        when(accountStorage.getAccount(eq(1L)))
                .thenReturn(makeAccount("userFrom", "10.00", 1));
        when(accountStorage.getAccount(eq(2L)))
                .thenReturn(makeAccount("userTo", "10.00", 2));
    }

    @Test
    public void executeTransfer_SUCCESS() throws EntityNotFoundException, EntityCreationException {
        when(transferStorage.getTransfer(1L))
                .thenReturn(makeTransfer(1, "5.00"));

        new TransferExecutionService(transferStorage, accountStorage).execute(1L);

        verify(transferStorage).insertTransfer(transferCaptor.capture());
        Transfer updatedTransfer = transferCaptor.getValue();
        assertEquals("Transfer Id", 1L, (long) updatedTransfer.getId());
        assertEquals("Transfer Status", Transfer.Status.EXECUTED, updatedTransfer.getStatus());

        verify(accountStorage, times(2)).insertAccount(accountCaptor.capture());
        Map<Long, Account> accounts = mapAccounts();
        assertEquals(new BigDecimal("5.00"), accounts.get(1L).getBalance().getAmount());
        assertEquals(new BigDecimal("15.00"), accounts.get(2L).getBalance().getAmount());
    }

    @Test
    public void executeTransfer_NotEnoughBalance() throws EntityNotFoundException, EntityCreationException {
        when(transferStorage.getTransfer(1L))
                .thenReturn(makeTransfer(1, "15.00"));

        new TransferExecutionService(transferStorage, accountStorage).execute(1L);

        verify(accountStorage, never()).insertAccount(any());

        verify(transferStorage).insertTransfer(transferCaptor.capture());
        Transfer updatedTransfer = transferCaptor.getValue();
        assertEquals("Transfer Id", 1L, (long) updatedTransfer.getId());
        assertEquals("Transfer Status", Transfer.Status.FAILED, updatedTransfer.getStatus());
    }

    private Map<Long, Account> mapAccounts() {
        return accountCaptor.getAllValues().stream()
                .collect(Collectors.toMap(Account::getId, Function.identity()));
    }

    private Transfer makeTransfer(int id, String amount) throws EntityCreationException {
        return new Transfer(id, new TransferBuilder()
                .setAmount(amount)
                .setIdSourceAccount(1)
                .setIdDestinationAccount(2)
                .build());
    }

    private Account makeAccount(String user, String amount, long id) throws EntityCreationException {
        return new Account(id, new AccountBuilder()
                .setUser(user)
                .setAmount(amount).build());
    }
}