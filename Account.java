package BankAccSys;

import java.util.Scanner;

public class BankAccSys_Main {

    // Account class (for demo purpose)
    static class Account {
        private int accNo;
        private String holderName;
        private double balance;

        public Account(int accNo, String holderName, double balance) {
            this.accNo = accNo;
            this.holderName = holderName;
            this.balance = balance;
        }

        public void saveAccountToFile() {
            System.out.println("Account details saved to accounts.txt (demo).");
        }
    }

    // Bank class
    static class Bank {
        private String bankName;
        private Account account;   // ✅ Now Account, not boolean

        public Bank(String bankName) {
            this.bankName = bankName;
        }

        public void createAccount(Scanner sc) {
            if (account == null) {
                sc.nextLine(); // clear buffer
                System.out.print("Enter account holder name: ");
                String name = sc.nextLine();

                System.out.print("Enter account number: ");
                int accNo = sc.nextInt();

                account = new Account(accNo, name, 0.0);
                account.saveAccountToFile();

                System.out.println("✅ Account created successfully for "
                                   + name + " (Acc No: " + accNo + ")");
            } else {
                System.out.println("⚠ Account already exists!");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("************************************");
        System.out.println("Welcome to Central Bank");
        System.out.println("************************************");
        System.out.println("How may I assist You.....! Please select the operation to be performed");
        System.out.println("1.Union Bank");
        System.out.println("2.SBI");
        System.out.println("3.Kotak");
        System.out.println("4.Canara Bank ");
        System.out.println("5.Andhra Bank");

        Scanner s = new Scanner(System.in);
        System.out.println("Enter a bank Choice:");
        int choice = s.nextInt();

        switch (choice) {
            case 1:
                Bank unionBank = new Bank("Union Bank");
                System.out.println("---------------------------");
                System.out.println("Welcome to Union Bank\n");
                System.out.println("---------------------------");
                System.out.println("How may I assist You.....! Please select the operation to be performed");
                System.out.println("1.Account Creation");
                System.out.println("2.Deposit");
                System.out.println("3.Withdraw");
                System.out.println("4.Balance Enquiry");
                System.out.println("5.Transactions");
                System.out.println("6.Help");

                int selectionUnion = s.nextInt();
                switch (selectionUnion) {
                    case 1:
                        unionBank.createAccount(s);
                        break;
                    default:
                        System.out.println("Invalid option");
                }
                break;

            default:
                System.out.println("Other banks not yet implemented");
        }

        s.close();
    }
}
