package org.federiconafria.transfer.logic;

import org.federiconafria.transfer.logic.entities.Account;
import org.federiconafria.transfer.logic.entities.AccountBuilder;
import org.federiconafria.transfer.logic.exceptions.EntityCreationException;
import org.federiconafria.transfer.logic.exceptions.EntityNotFoundException;
import org.federiconafria.transfer.logic.api.IdProvider;
import org.federiconafria.transfer.logic.api.AccountStorage;
import org.federiconafria.transfer.logic.services.AccountService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Mock
    AccountStorage storage;
    @Mock
    IdProvider idProvider;
    private AccountService service;
    @Captor
    private ArgumentCaptor<Account> insertedAccount;
    private Account correctAccount;

    @Before
    public void init() {
        service = new AccountService(storage, idProvider);
        Mockito.when(idProvider.generateNextId()).thenReturn(5L);
        try {
            correctAccount = new AccountBuilder().setUser("testUser").setAmount("5.00").build();
        } catch (EntityCreationException e) {
            throw new IllegalArgumentException(e);
        }
    }


    @Test(expected = EntityCreationException.class)
    public void createAccount_nullAccount_shouldThrowIllegalArgumentException() throws EntityCreationException {

        service.createAccount(null);

        Mockito.verify(storage, Mockito.never()).insertAccount(Mockito.any());
    }

    @Test(expected = EntityCreationException.class)
    public void createAccount_negativeAmount_shouldThrowException() throws EntityCreationException {
        service.createAccount(new AccountBuilder().setUser("testUser").setAmount("-5").build());

        Mockito.verify(storage, Mockito.never()).insertAccount(Mockito.any());
    }

    @Test
    public void createAccount_SUCCESS_shouldStoreAccount_withNewId() throws EntityCreationException {
        service.createAccount(correctAccount);

        Mockito.verify(storage).insertAccount(insertedAccount.capture());
        Assert.assertNotSame(correctAccount, insertedAccount.getValue());
        Assert.assertEquals(insertedAccount.getValue().getId(), 5L);
    }

    @Test(expected = EntityNotFoundException.class)
    public void retrieveAccount_NotFound() throws EntityNotFoundException {
        Mockito.when(storage.getAccount(Mockito.eq(3L)))
                .thenThrow(new EntityNotFoundException(""));
        service.getAccount(3L);
    }

    @Test
    public void retrieveAccount_Found() throws EntityNotFoundException {
        Account storageAccount = new Account(5L, correctAccount);
        Mockito.when(storage.getAccount(Mockito.eq(5L)))
                .thenReturn(storageAccount);

        Account retrieved = service.getAccount(5L);

        Assert.assertSame(storageAccount, retrieved);
    }

}