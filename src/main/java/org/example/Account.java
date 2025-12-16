package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account implements AccountService {
    private final List<Transaction> transactions = new ArrayList<>();
    private final DateProvider dateProvider;
    private int balance = 0;

    public Account() {
        this.dateProvider = new DateProvider() {
            @Override
            public java.time.LocalDate today() {
                return java.time.LocalDate.now();
            }
        };
    }

    public Account(DateProvider dateProvider) {
        this.dateProvider = dateProvider;
    }

    @Override
    public synchronized void deposit(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        LocalDate date = dateProvider.today();
        balance += amount;
        transactions.add(new Transaction(date, amount, balance));
    }

    @Override
    public synchronized void withdraw(int amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient funds");
        LocalDate date = dateProvider.today();
        balance -= amount;
        transactions.add(new Transaction(date, -amount, balance));
    }

    @Override
    public synchronized void printStatement() {
        // print header
        System.out.println("Date || Amount || Balance");
        // print transactions in reverse chronological order
        List<Transaction> copy = new ArrayList<>(transactions);
        Collections.reverse(copy);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Transaction t : copy) {
            String date = t.getDate().format(fmt);
            System.out.printf("%s || %d || %d%n", date, t.getAmount(), t.getBalanceAfter());
        }
    }
}
