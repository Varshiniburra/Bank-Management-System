package BankAccSys;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class BankAccSys_Main 
{

    // =================== ACCOUNT CLASS ===================
    static class Account
    {
        private String accNo;
        private String holderName;
        private double balance;
        private String firstName;
        private String lastName;
        private long phone;
        private String address;
        private String dob;
        private String email;
        private String pan;
        private List<String> transactions = new ArrayList<>();
        private String bankPrefix;
        private int withdrawCountToday = 0;
        private String lastWithdrawDate = "";

        public Account(String accNo, String holderName, double balance,
                       String firstName, String lastName, long phone,
                       String address, String dob, String email, String pan, String bankPrefix) 
        {
            this.accNo = accNo;
            this.holderName = holderName;
            this.balance = balance;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.address = address;
            this.dob = dob;
            this.email = email;
            this.pan = pan;
            this.bankPrefix = bankPrefix;
            addTransaction("Account created with balance " + balance);
        }

        public String getAccNo() 
        { 
        	return accNo; 
        	}
        public String getHolderName() 
        { 
        	return holderName; 
        	}
        public double getBalance() 
        { 
        	return balance; 
        	}

        private void addTransaction(String detail) 
        {
            String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
            String record = timeStamp + " - " + detail;
            transactions.add(record);

            try (PrintWriter writer = new PrintWriter(new FileWriter(bankPrefix + "_txn.txt", true))) 
            {
                writer.println(accNo + " | " + record);
            } 
            catch (IOException e) 
            {
                System.out.println("Error saving transaction: " + e.getMessage());
            }
        }

        public void deposit(double amount) 
        {
            balance += amount;
            addTransaction("Deposited: " + amount + " | Balance: " + balance);
            System.out.println("💰 " + amount + " deposited successfully. New Balance: " + balance);
        }

        public boolean withdraw(double amount)
        {
            String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (!today.equals(lastWithdrawDate)) 
            {
                withdrawCountToday = 0;
                lastWithdrawDate = today;
            }
            if (withdrawCountToday >= 3) 
            {
                System.out.println("❌ Withdrawal limit reached! Only 3 withdrawals allowed per day.");
                return false;
            }
            if (amount > 100000) 
            {
                System.out.println("❌ Withdrawal limit exceeded! Max ₹1,00,000 per transaction.");
                return false;
            }
            if (amount <= balance) 
            {
                balance -= amount;
                withdrawCountToday++;
                addTransaction("Withdrew: " + amount + " | Balance: " + balance);
                System.out.println("💰 " + amount + " withdrawn successfully. New Balance: " + balance);
                return true;
            }
            else 
            {
                System.out.println("❌ Insufficient balance!");
                return false;
            }
        }

        public void balanceEnquiry() 
        {
            System.out.println("💰 Current Balance: " + balance);
        }

        public void showTransactions() 
        {
            System.out.println("📄 Transaction History for " + holderName + " (Acc: " + accNo + "):");
            for (String t : transactions) System.out.println(" - " + t);
        }

        public void saveToFile(PrintWriter pw) 
        {
            pw.printf("%-10s | %-10s | %-10s | %-10s | %-10d | %-25s | %-10s | %-15s | %-12s | %.2f\n",
                    accNo, firstName, lastName, dob, phone, email, pan, address, bankPrefix, balance);
        }

        @Override
        public String toString() 
        {
            return String.format("%s | %s | 💰 Balance: %.2f | 📧 %s | 🆔 %s | 📱 %d",
                    accNo, holderName, balance, email, pan, phone);
        }
    }

    // =================== BANK CLASS ===================
    static class Bank {
        public String bankName;
        private String bankPrefix;
        private Map<String, Account> accounts = new LinkedHashMap<>();
        private final String FILE_NAME;
        private int accCounter = 10000;

        public Bank(String bankName, String bankPrefix) 
        {
            this.bankName = bankName;
            this.bankPrefix = bankPrefix;
            this.FILE_NAME = bankPrefix + "_accounts.txt";
            loadAccounts();
        }

        private String generateAccNo() 
        { 
        	return bankPrefix + (++accCounter); 
        	}

        public void createAccount(Scanner sc) 
        {
            sc.nextLine();
            System.out.println("******************************************************");
            System.out.println("🏦  ACCOUNT CREATION - " + bankName);
            System.out.println("******************************************************");

            System.out.print("👤 First Name: ");
            String firstName = sc.nextLine().trim();

            System.out.print("👤 Last Name: ");
            String lastName = sc.nextLine().trim();

            System.out.print("📅 Date of Birth (dd-mm-yyyy): ");
            String dob = sc.nextLine().trim();

            System.out.print("📧 Email: ");
            String email = sc.nextLine().trim();
            
            if (!email.contains("@"))
            { 
            	System.out.println("❌ Invalid Email"); 
            	return; 
            	}

            System.out.print("🆔 PAN (10 chars): ");
            String pan = sc.nextLine().trim();
            
            if (pan.length() != 10) 
            { 
            	System.out.println("❌ Invalid PAN"); 
            	return; 
            	}

            System.out.print("📱 Phone (10 digits): ");
            String phoneStr = sc.nextLine().trim();
            
            if (!phoneStr.matches("\\d{10}")) 
            { 
            	System.out.println("❌ Invalid phone"); 
            	return; 
            	}
            long phone = Long.parseLong(phoneStr);

            System.out.print("🏠 Address: ");
            String address = sc.nextLine().trim();

            String accNo = generateAccNo();
            double balance = 1000;
            Account acc = new Account(accNo, firstName + " " + lastName, balance,
                    firstName, lastName, phone, address, dob, email, pan, bankPrefix);
            accounts.put(accNo, acc);
            saveAccounts();

            System.out.println("✅ ACCOUNT CREATED SUCCESSFULLY");
            System.out.println("------------------------------------------------------");
            System.out.println("👤 Name       : " + acc.getHolderName());
            System.out.println("📧 Email      : " + acc.email);
            System.out.println("🆔 PAN        : " + acc.pan);
            System.out.println("📱 Phone      : " + acc.phone);
            System.out.println("🏦 Bank       : " + bankName);
            System.out.println("💳 Account No : " + accNo);
            System.out.println("💰 Balance    : " + balance);
            System.out.println("******************************************************");
        }

        private Account getAccount(Scanner sc) 
        {
            System.out.print("💳 Enter Account Number: ");
            String accNo = sc.next().trim();
            Account acc = accounts.get(accNo);
            if (acc == null) System.out.println("❌ No account found!");
            return acc;
        }

        public void deposit(Scanner sc) 
        {
            Account acc = getAccount(sc);
            if (acc != null) 
            {
                System.out.print("💰 Enter deposit amount: ");
                double amt = sc.nextDouble();
                acc.deposit(amt);
                saveAccounts();
            }
        }

        public void withdraw(Scanner sc)
        {
            Account acc = getAccount(sc);
            if (acc != null) 
            {
                System.out.print("💰 Enter withdrawal amount: ");
                double amt = sc.nextDouble();
                acc.withdraw(amt);
                saveAccounts();
            }
        }

        public void balanceEnquiry(Scanner sc) 
        {
            Account acc = getAccount(sc);
            if (acc != null) acc.balanceEnquiry();
        }

        public void transactions(Scanner sc) 
        {
            Account acc = getAccount(sc);
            if (acc != null) acc.showTransactions();
        }

        public void listAllAccountsForEmployee() 
        {
            if (accounts.isEmpty()) 
            { 
            	System.out.println("No accounts."); 
            	return; 
            	}
            for (Account a : accounts.values()) System.out.println(a.toString());
        }

        private void saveAccounts() 
        {
            try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) 
            {
                // Heading
                pw.println("================================================================================================");
                pw.println("            Account File Database");
                pw.println("================================================================================================");

                // Header row
                pw.printf("%-10s | %-12s | %-12s | %-12s | %-12s | %-25s | %-12s | %-15s | %-12s | %s\n",
                        "Acc No", "First Name", "Last Name", "DOB", "Phone", "Email", "PAN", "Address", "Bank", "Balance");
                pw.println("---------------------------------------------------------------------------------------------------------------------");

                // Data rows
                for (Account acc : accounts.values()) acc.saveToFile(pw);

            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }


        private void loadAccounts() 
        {
            File file = new File(FILE_NAME);
            if (!file.exists()) 
            	return;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) 
            {
                String line; String accNo="", name="", first="", last="", dob="", email="", pan="", addr=""; long phone=0; double bal=0;
                while ((line=br.readLine())!=null) 
                {
                    if(line.startsWith("Account No:")) accNo=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("Name:")) name=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("First Name:")) first=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("Last Name:")) last=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("DOB:")) dob=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("Email:")) email=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("PAN:")) pan=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("Phone:")) phone=Long.parseLong(line.split(":",2)[1].trim());
                    
                    else if(line.startsWith("Address:")) addr=line.split(":",2)[1].trim();
                    
                    else if(line.startsWith("Balance:")) 
                    {
                        bal=Double.parseDouble(line.split(":",2)[1].trim());
                        Account acc = new Account(accNo,name,bal,first,last,phone,addr,dob,email,pan,bankPrefix);
                        accounts.put(accNo,acc);
                        try 
                        { 
                        	int num=Integer.parseInt(accNo.substring(bankPrefix.length())); if(num>accCounter) accCounter=num; 
                        	} 
                        catch(Exception ignored)
                        {
                        	//ignored
                        }
                    }
                }
            } catch (IOException e) 
            { 
            	e.printStackTrace(); 
            	}
        }
    }

    // =================== EMPLOYEE CREDENTIALS ===================
    private static final Map<String,String> EMP_CREDENTIALS = new HashMap<>();
    static 
    { 
    	EMP_CREDENTIALS.put("emp001","pass001");
    	EMP_CREDENTIALS.put("emp002","pass002"); 
    	EMP_CREDENTIALS.put("emp003","pass003"); 
    	}

    private static Map<Integer,Bank> initializeBanks() 
    {
        Map<Integer,Bank> banks = new LinkedHashMap<>();
        banks.put(1,new Bank("Union Bank","UNI"));
        banks.put(2,new Bank("SBI","SBI"));
        banks.put(3,new Bank("Kotak","KOT"));
        banks.put(4,new Bank("Canara Bank","CAN"));
        banks.put(5,new Bank("Andhra Bank","AND"));
        return banks;
    }

    // =================== CUSTOMER CARE RECOMMENDATION ===================
    private static void customerCare(Scanner sc) 
    {
        System.out.println("------------------------------------------------------");
        System.out.println("📞  CUSTOMER CARE ASSISTANCE");
        System.out.println("------------------------------------------------------");
        System.out.print("💬 Describe your problem (loan/savings/atm/interest): ");
        String issue = sc.next().toLowerCase();
        switch(issue)
        {
            case "loan": 
            	System.out.println("👉 Recommendation: 🏦 Andhra Bank has best loan schemes!"); 
            	break;
            case "savings": 
            	System.out.println("👉 Recommendation: 🏦 Union Bank is best for savings!"); 
            	break;
            case "atm": 
            	System.out.println("👉 Recommendation: 🏧 Canara Bank has the best ATM network!"); 
            	break;
            case "interest": 
            	System.out.println("👉 Recommendation: 🏦 SBI has lowest interest rates!"); 
            	break;
            default: 
            	System.out.println("👉 Recommendation: 🏦 Please visit any nearby bank for guidance."); 
            	break;
        }
        System.out.println("------------------------------------------------------");
    }

    // =================== MAIN METHOD ===================
    public static void main(String[] args)
    {
        Scanner sc = new Scanner(System.in);
        Map<Integer,Bank> banks = initializeBanks();

        while(true) 
        {
            System.out.println("******************************************************");
            System.out.println("🏦  Welcome to CENTRAL BANK");
            System.out.println("******************************************************");
            System.out.println("1️⃣  Bank Employee Login");
            System.out.println("2️⃣  Customer Care");
            System.out.println("3️⃣  Exit");
            System.out.println("******************************************************");
            System.out.print("👉 Please enter your choice: ");
            int choice = sc.nextInt();
            switch(choice) 
            {
                case 1:
                    sc.nextLine();
                    System.out.print("🆔 Employee ID: ");
                    String empId = sc.nextLine().trim();
                    System.out.print("🔑 Password: ");
                    String pass = sc.nextLine().trim();
                    if(EMP_CREDENTIALS.containsKey(empId) && EMP_CREDENTIALS.get(empId).equals(pass)) 
                    {
                        System.out.println("✅ Login Successful! Welcome, " + empId);
                        int empOption;
                        do {
                            System.out.println("\n******************************************************");
                            System.out.println("🏦 CENTRAL BANK - EMPLOYEE DASHBOARD");
                            System.out.println("******************************************************");
                            System.out.println("1️.  Select Bank");
                            System.out.println("2️.  View All Accounts");
                            System.out.println("3️.  Exit to Main Menu");
                            System.out.println("******************************************************");
                            System.out.print("👉 Enter choice: ");
                            empOption = sc.nextInt();
                            switch(empOption) 
                            {
                                case 1:
                                    for(Map.Entry<Integer,Bank> b : banks.entrySet()) 
                                    {
                                        System.out.println(b.getKey() + ". " + b.getValue().bankName);
                                    }
                                    System.out.print("Select Bank: ");
                                    int bChoice = sc.nextInt();
                                    Bank bank = banks.get(bChoice);
                                    if(bank==null)
                                    {
                                    	System.out.println("❌ Invalid bank!"); 
                                    	break;
                                    	}
                                    int bankOp;
                                    do {
                                        System.out.println("\n------------------------------------------------------");
                                        System.out.println("🏦  " + bank.bankName + " - OPERATIONS");
                                        System.out.println("------------------------------------------------------");
                                        System.out.println("1️.  Create Account");
                                        System.out.println("2️.  Deposit");
                                        System.out.println("3️.  Withdraw");
                                        System.out.println("4️.  Balance Enquiry");
                                        System.out.println("5️.  Transaction History");
                                        System.out.println("6️.  Back to Employee Menu");
                                        System.out.print("👉 Enter choice: ");
                                        bankOp = sc.nextInt();
                                        switch(bankOp)
                                        {
                                            case 1: 
                                            	bank.createAccount(sc); 
                                            	break;
                                            case 2: 
                                            	bank.deposit(sc); 
                                            	break;
                                            case 3: 
                                            	bank.withdraw(sc); 
                                            	break;
                                            case 4: 
                                            	bank.balanceEnquiry(sc); 
                                            	break;
                                            case 5: 
                                            	bank.transactions(sc); 
                                            	break;
                                            case 6: 
                                            	break;
                                            default: 
                                            	System.out.println("❌ Invalid choice!");
                                        }
                                    }while(bankOp!=6);
                                    break;
                                case 2:
                                    for(Bank bnk : banks.values())
                                    {
                                        System.out.println("🏦 "+bnk.bankName);
                                        bnk.listAllAccountsForEmployee();
                                    }
                                    break;
                                case 3: 
                                	break;
                                default: System.out.println("❌ Invalid choice!");
                            }
                        }while(empOption!=3);
                    } 
                    else 
                    { 
                    	System.out.println("❌ Access Denied! Invalid credentials."); 
                    	}
                    break;
                case 2: 
                	customerCare(sc); 
                	break;
                case 3: 
                	System.out.println("👋 Thank you for using Central Bank!"); 
                	System.exit(0);
                default: 
                	System.out.println("❌ Invalid choice!");
            }
        }
    }
}
