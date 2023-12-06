package deliverable;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.io.Serializable;

class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}

class SavingsAccount implements Serializable {
    private double balance;
    private double interestRate;

    public SavingsAccount(double balance, double interestRate) {
        this.balance = balance;
        this.interestRate = interestRate;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient funds in savings account");
        }
        balance -= amount;
    }

    public void applyInterest() {
        deposit(balance * interestRate);
    }
}

class CheckingAccount implements Serializable{
    private double balance;
    private double overdraftLimit;

    public CheckingAccount(double balance, double overdraftLimit) {
        this.balance = balance;
        this.overdraftLimit = overdraftLimit;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance + overdraftLimit) {
            throw new InsufficientFundsException("Exceeded overdraft limit in checking account");
        }
        balance -= amount;
    }
}

class Account implements Serializable {
    private String accountNumber;
    private String pin;
    private SavingsAccount savingsAccount;
    private CheckingAccount checkingAccount;

    public Account(String accountNumber, String pin, double savingsBalance, double savingsInterestRate,
                   double checkingBalance, double checkingOverdraftLimit) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.savingsAccount = new SavingsAccount(savingsBalance, savingsInterestRate);
        this.checkingAccount = new CheckingAccount(checkingBalance, checkingOverdraftLimit);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public SavingsAccount getSavingsAccount() {
        return savingsAccount;
    }

    public CheckingAccount getCheckingAccount() {
        return checkingAccount;
    }
}

class ATM {
    private ArrayList<Account> accounts;

    public ATM(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }
    
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public Account authenticate(String accountNumber, String pin) throws AuthenticationException {
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber) && account.getPin().equals(pin)) {
                return account;
            }
        }
        throw new AuthenticationException("Invalid account number or PIN");
    }
}

public class ATMSystemGUI extends Application {
    private ATM atm;
    private TextField accountNumberField;
    private PasswordField pinField;
    private TextField amountField;
    private TextArea resultTextArea;
    private ComboBox<String> transactionTypeComboBox;
    private ComboBox<String> accountTypeComboBox;

    public ATMSystemGUI() {
        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account("123456", "1234", 1000, 0.05, 2000, 500));
        accounts.add(new Account("789012", "5678", 1500, 0.03, 2500, 600));

        atm = new ATM(accounts);
    }
    
        private void saveAccountsToFile(ArrayList<Account> accounts, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(accounts);
            System.out.println("Accounts saved to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error saving accounts to file: " + e.getMessage());
        }
    }

    private ArrayList<Account> loadAccountsFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            Object obj = ois.readObject();
            if (obj instanceof ArrayList) {
                System.out.println("Accounts loaded from file: " + fileName);
                return (ArrayList<Account>) obj;
            }
        } catch (Exception e) {
            System.err.println("Error loading accounts from file: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("ATM System");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        // Account Number
        Label accountNumberLabel = new Label("Account Number:");
        GridPane.setConstraints(accountNumberLabel, 0, 0);
        accountNumberField = new TextField();
        GridPane.setConstraints(accountNumberField, 1, 0);

        // PIN
        Label pinLabel = new Label("PIN:");
        GridPane.setConstraints(pinLabel, 0, 1);
        pinField = new PasswordField();
        GridPane.setConstraints(pinField, 1, 1);

        // Transaction Type
        Label transactionTypeLabel = new Label("Transaction Type:");
        GridPane.setConstraints(transactionTypeLabel, 0, 2);
        transactionTypeComboBox = new ComboBox<>();
        transactionTypeComboBox.getItems().addAll("Withdraw", "Deposit");
        transactionTypeComboBox.setValue("Withdraw"); // Default to Withdraw
        GridPane.setConstraints(transactionTypeComboBox, 1, 2);

        // Account Type
        Label accountTypeLabel = new Label("Account Type:");
        GridPane.setConstraints(accountTypeLabel, 0, 3);
        accountTypeComboBox = new ComboBox<>();
        accountTypeComboBox.getItems().addAll("Savings", "Checking");
        accountTypeComboBox.setValue("Savings"); // Default to Savings
        GridPane.setConstraints(accountTypeComboBox, 1, 3);

        // Amount
        Label amountLabel = new Label("Amount:");
        GridPane.setConstraints(amountLabel, 0, 4);
        amountField = new TextField();
        GridPane.setConstraints(amountField, 1, 4);

        // Result
        resultTextArea = new TextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setWrapText(true);
        GridPane.setConstraints(resultTextArea, 0, 5, 2, 1);

        // Authenticate Button
        Button processButton = new Button("Process Transaction");
        processButton.setOnAction(e -> processTransaction());
        GridPane.setConstraints(processButton, 0, 6);

        grid.getChildren().addAll(accountNumberLabel, accountNumberField, pinLabel, pinField,
                transactionTypeLabel, transactionTypeComboBox, accountTypeLabel, accountTypeComboBox,
                amountLabel, amountField, resultTextArea, processButton);

        Scene scene = new Scene(grid, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void processTransaction() {
        String accountNumber = accountNumberField.getText();
        String pin = pinField.getText();
        String transactionType = transactionTypeComboBox.getValue();
        String accountType = accountTypeComboBox.getValue();
        String amountText = amountField.getText();

        try {
            double amount = Double.parseDouble(amountText);
            Account userAccount = atm.authenticate(accountNumber, pin);

            switch (transactionType) {
                case "Withdraw":
                    Withdrawal(userAccount, accountType, amount);
                    break;
                case "Deposit":
                    Deposit(userAccount, accountType, amount);
                    break;
                default:
                    throw new InvalidInputException("Invalid transaction type");
            }

        } catch (NumberFormatException | InvalidInputException | AuthenticationException | InsufficientFundsException ex) {
            resultTextArea.setText("Error: " + ex.getMessage());
        }
    }

    private void Withdrawal(Account userAccount, String accountType, double amount)
            throws InvalidInputException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidInputException("Invalid withdrawal amount");
        }

        switch (accountType) {
            case "Savings":
                userAccount.getSavingsAccount().withdraw(amount);
                resultTextArea.setText("Withdrawal from savings successful. New balance: " +
                        userAccount.getSavingsAccount().getBalance());
                break;
            case "Checking":
                userAccount.getCheckingAccount().withdraw(amount);
                resultTextArea.setText("Withdrawal from checking successful. New balance: " +
                        userAccount.getCheckingAccount().getBalance());
                break;
            default:
                throw new InvalidInputException("Invalid account type");
        }
    }

    private void Deposit(Account userAccount, String accountType, double amount)
            throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Invalid deposit amount");
        }

        switch (accountType) {
            case "Savings":
                userAccount.getSavingsAccount().deposit(amount);
                resultTextArea.setText("Deposit to savings successful. New balance: " +
                        userAccount.getSavingsAccount().getBalance());
                break;
            case "Checking":
                userAccount.getCheckingAccount().deposit(amount);
                resultTextArea.setText("Deposit to checking successful. New balance: " +
                        userAccount.getCheckingAccount().getBalance());
                break;
            default:
                throw new InvalidInputException("Invalid account type");
        }
    }

    public static void main(String[] args) {
        ATMSystemGUI atmSystemGUI = new ATMSystemGUI();
        ArrayList<Account> accounts = atmSystemGUI.loadAccountsFromFile("account.txt");

        if (accounts.isEmpty()) {
            // Default accounts if the file couldn't be loaded or is empty
            accounts.add(new Account("123456", "1234", 1000, 0.05, 2000, 500));
            accounts.add(new Account("789012", "5678", 1500, 0.03, 2500, 600));
        }

        atmSystemGUI.atm = new ATM(accounts);
        launch(args);
    }
    
    @Override
    public void stop() {
        saveAccountsToFile(atm.getAccounts(), "account.txt");
    }
    
}
