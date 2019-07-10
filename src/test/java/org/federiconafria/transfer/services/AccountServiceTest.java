package org.federiconafria.transfer.services;

import org.federiconafria.transfer.entities.Account;
import org.federiconafria.transfer.entities.Currency;
import org.federiconafria.transfer.services.exceptions.AccountNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    AccountStorage storage;
    @Mock
    AccountIdProvider idProvider;
    private AccountService service;
    @Captor
    private ArgumentCaptor<Account> insertedAccount;
    private Account correctAccount = new Account("testUser", Currency.valueOf("5.00"));

    @Before
    public void init() {
        service = new AccountService(storage, idProvider);
        Mockito.when(idProvider.generateNextId()).thenReturn(5L);
    }


    @Test
    public void createAccount_nullAccount_shouldThrowIllegalArgumentException() {
        expectedException.expect(IllegalArgumentException.class);

        service.createAccount(null);

        Mockito.verify(storage, Mockito.never()).insertAccount(Mockito.any());
    }

    @Test
    public void createAccount_negativeAmount_shouldThrowException() {
        expectedException.expect(IllegalArgumentException.class);

        service.createAccount(new Account("testUser", Currency.valueOf("-5")));

        Mockito.verify(storage, Mockito.never()).insertAccount(Mockito.any());
    }

    @Test
    public void createAccount_SUCCESS_shouldStoreAccount_withNewId() {
        service.createAccount(correctAccount);

        Mockito.verify(storage).insertAccount(insertedAccount.capture());
        Mockito.verify(storage).insertAccount(Mockito.any());
        Assert.assertNotSame(correctAccount, insertedAccount.getValue());
        Assert.assertEquals(insertedAccount.getValue().getId(), 5L);
    }

    @Test
    public void retrieveAccount_NotFound() throws AccountNotFoundException {
        Mockito.when(storage.getAccount(Mockito.eq(3L)))
                .thenThrow(new AccountNotFoundException(""));
        expectedException.expect(AccountNotFoundException.class);
        service.getAccount(3L);
    }

    @Test
    public void retrieveAccount_Found() throws AccountNotFoundException {
        Account storageAccount = new Account(5L, correctAccount);
        Mockito.when(storage.getAccount(Mockito.eq(5L)))
                .thenReturn(storageAccount);

        Account retrieved = service.getAccount(5L);

        Assert.assertSame(storageAccount, retrieved);
    }

}