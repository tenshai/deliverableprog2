
package deliverable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class atmtest {

    private ATM atm;

    @BeforeEach
    public void setUp() {
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("123456", "1234", 1000, 0.05, 2000, 500));
        accounts.add(new Account("789012", "5678", 1500, 0.03, 2500, 600));
        atm = new ATM(accounts);
    }

    @Test
    public void testGetAccounts() {
        assertNotNull(atm.getAccounts(), "ATM accounts should not be null");
        assertEquals(2, atm.getAccounts().size(), "Number of accounts should match");
    }

    @Test
    public void testAuthenticateValid() {
        assertDoesNotThrow(() -> {
            Account authenticatedAccount = atm.authenticate("123456", "1234");
            assertNotNull(authenticatedAccount, "Authenticated account should not be null");
            assertEquals("123456", authenticatedAccount.getAccountNumber(), "Authenticated account number should match");
        });
    }

    @Test
    public void testAuthenticateInvalidPIN() {
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->{
            atm.authenticate("123456", "wrongpin");
        });

        assertEquals("Invalid account number or PIN", exception.getMessage(), "Exception message should match");
    }

    @Test
    public void testAuthenticateUnknownAccount() {
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            atm.authenticate("unknownaccount", "1234");
        });

        assertEquals("Invalid account number or PIN", exception.getMessage(), "Exception message should match");
    }
}
