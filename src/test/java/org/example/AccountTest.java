package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountTest {

    @Test
    void acceptanceTest_printsStatementWithAllTransactionsInReverseChronologicalOrder() throws Exception {
        // Arrange: create a date provider that returns specific dates in sequence
        LocalDate d1 = LocalDate.of(2012,1,10);
        LocalDate d2 = LocalDate.of(2012,1,13);
        LocalDate d3 = LocalDate.of(2012,1,14);

        DateProvider seq = new DateProvider() {
            int i = 0;
            @Override
            public LocalDate today() {
                i++;
                if (i == 1) return d1;
                if (i == 2) return d2;
                return d3;
            }
        };

        Account account = new Account(seq);

        // capture output
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(out));

        // Act
        account.deposit(1000);
        account.deposit(2000);
        account.withdraw(500);
        account.printStatement();

        // Restore
        System.setOut(old);

        String printed = out.toString();

        // Assert: contains header and lines in expected (reverse chrono) order
        assertTrue(printed.contains("Date || Amount || Balance"));
        assertTrue(printed.contains("14/01/2012 || -500 || 2500"));
        assertTrue(printed.contains("13/01/2012 || 2000 || 3000"));
        assertTrue(printed.contains("10/01/2012 || 1000 || 1000"));
    }
}
