package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starter code for the Online Store workshop.
 * Students will complete the TODO sections to make the program work.
 */
public class Store {

    public static void main(String[] args) {

        // Create lists for inventory and the shopping cart
        ArrayList<Product> inventory = new ArrayList<>();
        ArrayList<Product> cart = new ArrayList<>();

        // Load inventory from the data file (pipe-delimited: id|name|price)
        loadInventory("products.csv", inventory);

        // Main menu loop
        Scanner scanner = new Scanner(System.in);
        int choice = -1;
        while (choice != 3) {
            System.out.println("\nWelcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");
            System.out.print("Your choice: ");

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter 1, 2, or 3.");
                scanner.nextLine();                 // discard bad input
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine();                     // clear newline

            switch (choice) {
                case 1 -> displayProducts(inventory, cart, scanner);
                case 2 -> displayCart(cart, scanner);
                case 3 -> System.out.println("Thank you for shopping with us!");
                default -> System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    /**
     * Reads product data from a file and populates the inventory list.
     * File format (pipe-delimited):
     * id|name|price
     * <p>
     * Example line:
     * A17|Wireless Mouse|19.99
     */
    public static void loadInventory(String fileName, ArrayList<Product> inventory) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] productArray = line.split("\\|");
                inventory.add(new Product(productArray[0], productArray[1], Double.parseDouble(productArray[2])));
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Data Error: A line in the file is corrupt or incomplete.");
        } catch (java.io.FileNotFoundException e) {
            System.err.println("File is not found");
        } catch (java.io.IOException e){
            System.err.println("An unexpected error occurred while reading the file.");
        }
    }

    /**
     * Displays all products and lets the user add one to the cart.
     * Typing X returns to the main menu.
     */
    public static void displayProducts(ArrayList<Product> inventory,
                                       ArrayList<Product> cart,
                                       Scanner scanner) {
        try {
            boolean done = false;

                System.out.println("=========================================================================================");
                System.out.println("Id          | Name                               | Price                ");
                for (int i = inventory.size() - 1; i > 0; i--) {
                    PrintOut(inventory.get(i));
                }
                System.out.println("=========================================================================================");
                while (!done) {
                    System.out.println("If you want to buy something write its Id, otherwise write \"no\"");
                    String ans = scanner.nextLine();
                    if (ans.equalsIgnoreCase("no")){
                        done=true;
                    }else {
                        Product item = findProductById(ans.toUpperCase(),inventory);
                        if(item!=null) {
                            cart.add(item);
                            System.out.println(item.getName() + " has been added to cart.");
                        }
                        else {
                            System.out.println("Sorry! Id is not found. Please try again.");
                        }
                    }
                }
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    /**
     * Shows the contents of the cart, calculates the total,
     * and offers the option to check out.
     */
    public static void displayCart(ArrayList<Product> cart, Scanner scanner) {
        try {
            double total = 0;
            boolean done = false;
            System.out.println("=========================================================================================");
            System.out.println("Id          | Name                               | Price                ");
            for (int i = cart.size() - 1; i > 0; i--) {
                PrintOut(cart.get(i));
                total+=cart.get(i).getPrice();
            }
            System.out.println("Your total is: $"+total);
            System.out.println("=========================================================================================");

            while (!done) {
                System.out.println("Please choose your option: \"C\" to check out, \"X\" to return.");
                String ans = scanner.nextLine();
                if (ans.equalsIgnoreCase("X")){
                    done=true;
                }else if(ans.equalsIgnoreCase("C")){
                    checkOut(cart,total,scanner);
                    done = true;
                }else {
                    System.out.println("Invalid input, please try again");
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
    }

    /**
     * Handles the checkout process:
     * 1. Confirm that the user wants to buy.
     * 2. Accept payment and calculate change.
     * 3. Display a simple receipt.
     * 4. Clear the cart.
     */
    public static void checkOut(ArrayList<Product> cart,
                                double totalAmount,
                                Scanner scanner) {
        boolean isEnough = false;
        double change = 0;
        System.out.println("You are going to buy: ");
        for (int i = 0; i < cart.size();i++){
            System.out.println(cart.get(i).getName());
        }
        System.out.println("Your total is: $" + totalAmount);
        System.out.println("Do you want to finish?(yes/no)");
        String ans = scanner.nextLine();
        if(ans.equalsIgnoreCase("no")){
            return;
        }
        
        System.out.println("How would you pay?(card/cash)");
        ans = scanner.nextLine();

        while (!isEnough) {
            if (ans.equalsIgnoreCase("cash")) {
                System.out.println("Please inter your amount:");
                double cash = scanner.nextDouble();
                scanner.nextLine();
                if (cash < totalAmount) {
                    System.out.println("There is not isEnough, please try again");
                } else if (cash == totalAmount) {
                    System.out.println("Thank you for the exact amount");
                    isEnough = true;
                } else {
                    change = cash-totalAmount;
                    System.out.println("Your change is: $" + change);
                    isEnough = true;
                }
            }
        }
        System.out.println("Your receipt: ");
        System.out.println("=========================================================================================");
        System.out.println("Id          | Name                               | Price                ");
        for (int i = cart.size() - 1; i > 0; i--) {
            PrintOut(cart.get(i));
        }
        System.out.println("Your total is: $"+totalAmount);
        if(change!=0){
            System.out.println("Your change is: $" + change);
        }
        System.out.println("Thank you!");
        System.out.println("=========================================================================================");
    }

    /**
     * Searches a list for a product by its id.
     *
     * @return the matching Product, or null if not found
     */
    public static Product findProductById(String id, ArrayList<Product> inventory) {
        for (Product product : inventory) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
    private static void PrintOut(Product product){
        String formatString = "%-12s| %-35s| %-12s";
        System.out.printf((formatString) + "%n", product.getId(), product.getName(), product.getPrice());
    }
}

