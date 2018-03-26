package com.company;

import java.util.InputMismatchException;
import java.util.Scanner;

public class XavierGrayAssignment4 {

    public static void main(String[] args) throws ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        int entry;
        DatabaseHandle handle = new DatabaseHandle();
        if (!handle.connect()) {
            System.exit(0);
        }
        else {
            System.out.println("Connected to localhost");
        }
        System.out.println("Thank you for connecting to the Northwind Database");
        System.out.println("What would you like to do today?");
        System.out.println("1) Add a customer\t2) Add an order");
        System.out.println("2) Remove an order\t3) Ship an order");
        System.out.println("4) Print pending orders\t6) Restock parts");
        System.out.println("7) Quit");
        try {
            while ((entry = sc.nextInt()) != 7) {
                switch (entry) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    default:
                        System.out.println("That is not an accepted response");
                }
                System.out.println("What would you like to do next?");
                System.out.println("1) Add a customer\t2) Add an order");
                System.out.println("2) Remove an order\t3) Ship an order");
                System.out.println("4) Print pending orders\t6) Restock parts");
                System.out.println("7) Quit");
            }
        }
        catch (InputMismatchException e) {
            System.out.println("Please only enter numbers next time!");
        }
        System.out.println("Thank you! Have a nice day!");
        sc.close();
    }
}
