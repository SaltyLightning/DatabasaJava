package com.company;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.SimpleTimeZone;

public class XavierGrayAssignment4 {
    static Scanner sc;
    public static void main(String[] args) throws ClassNotFoundException {
        sc = new Scanner(System.in);
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
            while ((entry = Integer.parseInt(sc.nextLine())) != 7) {
//                if (sc.hasNext()) {
//                    sc.nextLine();
//                    System.out.println("Fail");
//                }
                switch (entry) {
                    case 1:
                        try {
                            AddCustomer(handle);
                            System.out.println("Successfully added customer to database.");
                        } catch (SQLException e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                        break;
                    case 2:
                        try {
                            AddOrder(handle);
                            System.out.println("Successfully added order to database.");
                        } catch (SQLException e) {
                            if (e instanceof SQLIntegrityConstraintViolationException)
                                System.out.println("You violated foreign key constraints");
                            System.out.println(e.getLocalizedMessage());
                        }
                        catch (ParseException e){
                            System.out.println(e.getLocalizedMessage());
                            System.out.println("(Pattern: \"mm-dd-yyyy\")");
                        }
                        break;
                    case 3:
                        RemoveOrder(handle);
                        break;
                    case 4:
                        ShipOrder(handle);
                        break;
                    case 5:
                        PrintPendingOrders(handle);
                        break;
                    case 6:
                        Restock(handle);
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

    private static void Restock(DatabaseHandle handle) {
    }

    private static void PrintPendingOrders(DatabaseHandle handle) {
    }

    private static void ShipOrder(DatabaseHandle handle) {
    }

    private static void RemoveOrder(DatabaseHandle handle) {
    }

    private static void AddOrder(DatabaseHandle handle) throws SQLException, ParseException {
        handle.sqlConnect.setAutoCommit(false);
        String insertString = "INSERT INTO orders(CustomerID, EmployeeID, OrderDate, RequiredDate, ShipVia, Freight, ShipName, ShipAddress, ShipCity, ShipRegion, ShipPostalCode, ShipCountry) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement insertStatement = handle.sqlConnect.prepareStatement(insertString, Statement.RETURN_GENERATED_KEYS);
        String cur;
        System.out.println("Please enter the CustomerID");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(1, cur);

        System.out.println("Please enter the EmployeeID");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(2, cur);

        System.out.println("Please enter the OrderDate");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        SimpleDateFormat dt = new SimpleDateFormat("mm-dd-yyyy");
        Date t = new Date(dt.parse(cur).getTime());
        insertStatement.setDate(3, t);

        System.out.println("Please enter the RequiredDate");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        t = new Date(dt.parse(cur).getTime());
        insertStatement.setDate(4, t);

        System.out.println("Please enter the ShipVia");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(5, cur);

        System.out.println("Please enter the Freight");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(6, cur);

        System.out.println("Please enter the ShipName");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(7, cur);

        System.out.println("Please enter the ShipAddress");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(8, cur);

        System.out.println("Please enter the ShipCity");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(9, cur);

        System.out.println("Please enter the ShipRegion");
        cur = sc.nextLine();
        insertStatement.setString(10, cur);

        System.out.println("Please enter the ShipPostalCode");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(11, cur);

        System.out.println("Please enter the ShipCountry");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(12, cur);

        int affectedRows = insertStatement.executeUpdate();
        handle.sqlConnect.commit();

        ResultSet keyResults = insertStatement.getGeneratedKeys();
    
        int orderId = keyResults.getInt(1);
        String insertString2 = "insert into orders_details(OrderID, ProductID, UnitPrice, Quantity, Discount) " +
                "values (?, ?, ?, ?, ?)";
        PreparedStatement insertStatement2 = handle.sqlConnect.prepareStatement(insertString2);

//        System.out.println("Please enter the OrderID");
//        while ((cur = sc.nextLine()).equals(""))
//            System.out.println("Null values are not allowed for this field.");

        insertStatement2.setInt(1, orderId);

        System.out.println("Please enter the ProductID");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement2.setInt(2, Integer.parseInt(cur));

        System.out.println("Please enter the UnitPrice");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement2.setDouble(3, Double.parseDouble(cur));

        System.out.println("Please enter the Quantity");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setShort(4, Short.parseShort(cur));

        System.out.println("Please enter the Discount");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        float temp = Float.parseFloat(cur);
        if (temp > 0)
            throw new SQLException("Discounts are not allowed");
        insertStatement2.setFloat(5, temp);

        insertStatement2.executeUpdate();

        handle.sqlConnect.commit();
        handle.sqlConnect.setAutoCommit(true);
    }
    private static void AddCustomer(DatabaseHandle handle) throws SQLException {
        String insertString = "insert into customers(CustomerID, CompanyName, ContactName, Address, City, Region, PostalCode, Country, Phone, Fax) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement insertStatement = handle.sqlConnect.prepareStatement(insertString);

        String cur;
        System.out.println("Please enter the CustomerID");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(1, cur);

        System.out.println("Please enter the CompanyName");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(2, cur);

        System.out.println("Please enter the ContactName");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(3, cur);

        System.out.println("Please enter the Address");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(4, cur);

        System.out.println("Please enter the City");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(5, cur);

        System.out.println("Please enter the Region");
        cur = sc.nextLine();
        insertStatement.setString(6, cur);

        System.out.println("Please enter the PostalCode");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(7, cur);

        System.out.println("Please enter the Country");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(8, cur);

        System.out.println("Please enter the Phone");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(9, cur);

        System.out.println("Please enter the Fax");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement.setString(10, cur);

        insertStatement.executeUpdate();
    }
}
