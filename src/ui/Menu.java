package ui;

import backend.CompanyEmailSender;
import backend.Database;
import backend.RegistrationSystem;
import boat.Boat;
import exceptions.FailedTransactionException;
import exceptions.NotFoundByGivenInfo;
import person.Client;
import person.Company;

import java.io.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class Menu implements Serializable {
    private static Scanner scanner = new Scanner(System.in);
    // !!!!!!!!!!!!!!!!!! To read the database
    static RegistrationSystem system = new RegistrationSystem();
    static ArrayList<Boat> boats = loadBoatsFromFile();
    static Database database = new Database(boats);
    static CompanyEmailSender companyEmailSender;


    public static void main(String[] args) throws ClassNotFoundException, NotFoundByGivenInfo, FailedTransactionException {
        //Initializing
        System.out.println("We are sending your information to our server, please wait a moment...");


        LocalTime constructedTime = LocalTime.now();
        /*try {
            companyEmailSender = new CompanyEmailSender();
            companyEmailSender.sent("System is working at " + getCurrentTime(), new Company(0, "zhaoq@kean.edu", "2232"));
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (InvalidEmailAddress e) {
            System.out.println("Something badly happen in our server, email notification is not available ");
            ;
        }*/


        //first, client log in their accounts
        System.out.println("Welcome to our company!");
        System.out.println("Time: " + getCurrentTime());

        a:
        while (true) {
            System.out.println("You can choose 1. register  2. login  3. quit ");
            int log = scanner.nextInt();
            switch (log) {
                case 1:
                    // Use the register method in the RegistrationSystem class to register a new client;
                    Map<String, Client> userDatabase = system.registerUser();
                    system.saveUserDatabaseToFile(userDatabase);
                    break;
                case 2:
                    system.loadUserDatabaseFromFile();
                    login();
                    saveBoatsToFile(boats);
                    System.out.println("..................................");
                    break a;
                case 3:
                    System.out.println("Thank you for your visit!");
                    return;
            }
        }
    }

    public static String getCurrentTime() {
        return java.time.LocalDateTime.now().toString();
    }

    public static void login() throws NotFoundByGivenInfo, FailedTransactionException {

        // to log in;
        System.out.print("Please enter your user name:");
        String name = scanner.next();
        scanner.nextLine();

        Map<String, Client> userDatabase = system.loadUserDatabaseFromFile();
        // Check if the username exists
        if (!userDatabase.containsKey(name)) {
            System.out.println("Username not found. Please register first.");
            return;
        }
        System.out.print("Enter your password: ");
        String enteredPassword = scanner.nextLine();
        System.out.println("\n\nEnter any key to continue...");
        scanner.nextLine();
        // Get the user info for the entered username

        String storedPassword = userDatabase.get(name).getPassword();


        // Check if the entered password matches the stored password
        while (true) {
            if (enteredPassword.equals(storedPassword)) {
                System.out.println("Login successfully!");
                System.out.println("We are sending your login information to your email, please wait a moment...");

/*                try {
                    if (companyEmailSender.isValidEmail(userDatabase.get(name).getEmail()) == -1) {
                        System.out.println("Invalid email address, so we cannot send email to you please register again");
                    }
                    companyEmailSender.sent("Someone is trying to log in your account at \n" + getCurrentTime(), userDatabase.get(name));
                } catch (InvalidEmailAddress e) {
                    System.out.println("Invalid email address, so we cannot send email to you please register again");
                }*/
                System.out.println("Login Information has sent to your email");
                System.out.println("----------------------------------------");

                // if login successful, then start to choose function
                fiveRequest(name);
                break;
                // Determine which module to enter based on the last input int
                //           scanner.close();
            } else {
                // If the passwords don't match, display an error message
                System.out.println("Invalid password. Please try again.");
                enteredPassword = scanner.nextLine();
            }
        }
    }

    // display basic function: choose borrow, buy or sell;
    public static void fiveRequest(String name) throws NotFoundByGivenInfo, FailedTransactionException {
        int input1;

        while (true) {

            System.out.println("What is your request?");
            System.out.println("1. borrow  2. buy  3. return  4. search appointment  5. display log");


            input1 = scanner.nextInt();
            switch (input1) {
                case 1:
                    borrowBoat(name);
                    break;
                case 2:
                    buyBoat(name);
                    break;
                case 3:
                    returnBoat(name);
                    break;
                case 4:
                    searchAppointment();
                    break;
                case 5:
                    displayLog(name);
                    break;
                default:
                    System.out.println("Invalid input.");
                    break;
            }
            if (input1 == 1 || input1 == 2 || input1 == 3 || input1 == 4 || input1 == 5) {
                break;
            }
            System.out.println("Please enter 1 or 2 or 3 or 4 or 5 ~");

        }
        scanner.nextLine();
    }

    public static void borrowBoat(String name) throws NotFoundByGivenInfo, FailedTransactionException {
        Boat boat = recommendBoat();
        borrowTransaction(name, boat);
    }

    public static void buyBoat(String name) throws NotFoundByGivenInfo, FailedTransactionException {
        Boat boat = recommendBoat();
        buyTransaction(name, boat);
    }

    public static void returnBoat(String name) {
        Map<String, Client> userDatabase = system.loadUserDatabaseFromFile();
        Client currentClient = userDatabase.get(name);
        System.out.println("user:" + currentClient.getName());
        System.out.println(currentClient.getUse());
        if (currentClient.getUse().size() == 0 || currentClient.getUse() == null) {
            System.out.println("no boat can return");
        } else {
            System.out.println(currentClient.getUse());
            System.out.print("Which boat do you want to return? input No.x");
        }

    }

    public static void searchAppointment() {

    }

    public static void displayLog(String name) {
        Map<String, Client> userDatabase = system.loadUserDatabaseFromFile();

    }

    public static Boat recommendBoat() throws NotFoundByGivenInfo, FailedTransactionException {
        System.out.println("Are there any requirements for the boat? ");
        System.out.println("You can select your boat's make, model, length, current docking area and year of production.");
        System.out.print("y/n : ");
        String input2;
        Boat borrowBoat = new Boat();
        while (true) {
            try {
                input2 = scanner.next();
                if (input2.equals("y") || input2.equals("n")) {
                    break;
                }
                System.out.println("Please enter y or n ~");
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Please enter y or n ~");
            }
        }
        if (input2.equals("y")) {
            database.show(scanner);
            scanner.nextLine();
            Boat currentBoat = Database.searchBoat(scanner);

            return currentBoat;
        }
        if (input2.equals("n")) {
            int i = 0;
            System.out.println("I will recommend any boat to you. The following is information about the boat:");
            for (Map.Entry<Double, ArrayList<Boat>> entry : database.getrPriceBoats().subMap(12000.0, 19000.0).entrySet()) {
                System.out.println("---> Price: " + entry.getKey());
                for (Boat boat : entry.getValue()) {
                    i++;
                    System.out.println("boat" + i + " : " + boat);
                }
                System.out.println();
            }
            System.out.print("Choose one: ");
            int count = scanner.nextInt();
            int j = 0;
            b:
            for (Map.Entry<Double, ArrayList<Boat>> entry : database.getrPriceBoats().subMap(12000.0, 19000.0).entrySet()) {
                for (Boat boat : entry.getValue()) {
                    j++;
                    if (j == count)
                        return boat;
                }
            }
        }
        return borrowBoat;
    }

    public static void borrowTransaction(String name, Boat boat) throws FailedTransactionException {
        System.out.println("Dealing.....\nPlease Wait a moment");
        Map<String, Client> userDatabase = system.loadUserDatabaseFromFile();
        Client currentClient = userDatabase.get(name);
        if (!(boat.getOwner() instanceof Company))
            throw new FailedTransactionException("The boat is sold out");
        else if (boat.getUser() instanceof Client)
            throw new FailedTransactionException("The boat is rented");
        boat.setUser(currentClient);
        boats.set(boat.getIndex(), boat);
        //Boat oldBoat=boats.get(boat.getIndex());
        boats.set(boat.getIndex(), boat);
        currentClient.getUse().add(boat);

        System.out.println("Now, you can use: " + currentClient.getUse());
        userDatabase.put(name, currentClient);
        system.saveUserDatabaseToFile(userDatabase);
    }

    public static void buyTransaction(String name, Boat boat) throws FailedTransactionException {
        System.out.println("Dealing.....\nPlease Wait a moment");
        Map<String, Client> userDatabase = system.loadUserDatabaseFromFile();
        Client currentClient = userDatabase.remove(name);
        if (!(boat.getOwner() instanceof Company))
            throw new FailedTransactionException("The boat is sold out");
        else if (boat.getUser() instanceof Client)
            throw new FailedTransactionException("The boat is rented");
        boat.setUser(currentClient);
        boat.setOwner(currentClient);
        boats.set(boat.getIndex(), boat);
        //saveBoatsToFile(boats);
        HashSet<Boat> set = currentClient.getOwn();
        set.add(boat);
        currentClient.getOwn().add(boat);
        currentClient.setOwn(set);
        System.out.println("Now, you own : " + currentClient.getUse());
        userDatabase.put(name, currentClient);
        system.saveUserDatabaseToFile(userDatabase);

        //currentClient.getTransaction().add(boat);
    }

    public static ArrayList<Boat> loadBoatsFromFile() {
        String filePath = "C:\\Users\\QinJian\\Desktop\\allBoats";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (ArrayList<Boat>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBoatsToFile(ArrayList<Boat> boats) {
        String filePath = "C:\\Users\\QinJian\\Desktop\\allBoats";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(boats);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


