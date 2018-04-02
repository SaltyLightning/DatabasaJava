package com.company;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;
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
        System.out.println("3) Remove an order\t4) Ship an order");
        System.out.println("5) Print pending orders\t6) Restock parts");
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
                        try {
                            RemoveOrder(handle);
                        } catch (SQLException e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                        break;
                    case 4:
                        try{
                            String orderid = ShipOrder(handle);
                            System.out.println("Order " + orderid + " shipped!");
                        }
                        catch (SQLException e){
                            System.out.println(e.getLocalizedMessage());
                        }
                        break;
                    case 5:
                        try {
                            PrintPendingOrders(handle);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 6:
                        try {
                            Restock(handle);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.printf(e.getLocalizedMessage());
                        }
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
        catch (NumberFormatException e){
            System.out.println("Please only enter numbers next time!");
        }
        System.out.println("Thank you! Have a nice day!");
        sc.close();
    }

    private static void Restock(DatabaseHandle handle) throws SQLException {
        String cur;
        System.out.print("Please enter the product name you would like to order: ");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        String selectString = "select ProductID, Discontinued, ReorderLevel from products where ProductName = ?";
        PreparedStatement selStatement = handle.sqlConnect.prepareStatement(selectString);
        selStatement.setString(1, cur);
        String productN = cur;
        ResultSet selSet = selStatement.executeQuery();
        if (!selSet.next()){
            String insertString = "INSERT INTO products(ProductName, SupplierID, CategoryID, QuantityPerUnit, " +
                    "UnitPrice, UnitsInStock, UnitsOnOrder, ReorderLevel, Discontinued) " +
                    "VALUES (?, ?, ?, ?, ?, 0, ?, ?, \'n\')";
            PreparedStatement insertStatement = handle.sqlConnect.prepareStatement(insertString);
            insertStatement.setString(1, cur);
            System.out.println("Looks like that's a new product. We'll have to ask for some more information.");

            System.out.print("What is the SupplierID for that product? ");
            while ((cur = sc.nextLine()).equals(""))
                System.out.println("Null values are not allowed for this field.");
            insertStatement.setInt(2, Integer.parseInt(cur));

            System.out.print("What is the CategoryID for that product? ");
            while ((cur = sc.nextLine()).equals(""))
                System.out.println("Null values are not allowed for this field.");
            insertStatement.setInt(3, Integer.parseInt(cur));

            System.out.print("What is the Quantity Per Unit for that product? ");
            while ((cur = sc.nextLine()).equals(""))
                System.out.println("Null values are not allowed for this field.");
            insertStatement.setString(4, cur);

            System.out.print("What is the Unit Price for that product? ");
            while ((cur = sc.nextLine()).equals(""))
                System.out.println("Null values are not allowed for this field.");
            insertStatement.setDouble(5, Double.parseDouble(cur));

            System.out.print("What is the Reorder Level for that product? ");
            while ((cur = sc.nextLine()).equals(""))
                System.out.println("Null values are not allowed for this field.");
            insertStatement.setInt(6, Integer.parseInt(cur));
            insertStatement.setInt(7, Integer.parseInt(cur));

            insertStatement.execute();
        }
        else{
            if (selSet.getString(2).equals("y")){
                System.out.println("That product is discontinued. You cannot restock it.");
                throw new SQLException();
            }
            String updateString = "UPDATE products SET UnitsInStock = UnitsInStock + UnitsOnOrder, " +
                    "UnitsOnOrder = ? WHERE ProductID = ?";
            PreparedStatement updateStatement = handle.sqlConnect.prepareStatement(updateString);
            updateStatement.setInt(2, selSet.getInt(1));
            int reorderAmount = selSet.getInt(3);
            if (reorderAmount == 0){
                System.out.print("How many of \"" + productN + "\" would you like to order? ");
                while ((cur = sc.nextLine()).equals(""))
                    System.out.println("Null values are not allowed for this field.");
                reorderAmount = Integer.parseInt(cur);
            }
            updateStatement.setInt(1, reorderAmount);
            updateStatement.execute();
        }
        System.out.println("Successfully restocked " + productN);
    }

    private static void PrintPendingOrders(com.company.DatabaseHandle handle) throws SQLException {
        String selectString = "select OrderID, OrderDate, RequiredDate, CustomerID " +
                "from orders where ShippedDate is NULL order by OrderDate ASC";
        PreparedStatement selStatement = handle.sqlConnect.prepareStatement(selectString);
        ResultSet selSet = selStatement.executeQuery();
        while (selSet.next()) {
            System.out.println(String.format("Order ID = %s, Order date = %s, Required date = %s" +
                            " Customer ID = %s", selSet.getString(1), selSet.getString(2),
                    selSet.getString(3), selSet.getString(4)));
        }
    }

    private static String ShipOrder(com.company.DatabaseHandle handle) throws SQLException {
        String cur;
        System.out.println("Please enter the order ID you would like to ship:");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");

        String selectString = "select ShipName, ShipAddress, ShipCity, ShipPostalCode, ShipCountry "+
                "from orders where OrderID = " + cur;

        PreparedStatement selStatement = handle.sqlConnect.prepareStatement(selectString);
        ResultSet selSet = selStatement.executeQuery();
        if (!selSet.next())
            throw new SQLException("Cannot find address information for that order id");
        if (selSet.getString(1).equals(""))
            throw new SQLException("Cannot find address information for that order id");
        if (selSet.getString(2).equals(""))
            throw new SQLException("Cannot find address information for that order id");
        if (selSet.getString(3).equals(""))
            throw new SQLException("Cannot find address information for that order id");
        if (selSet.getString(4).equals(""))
            throw new SQLException("Cannot find address information for that order id");
        if (selSet.getString(5).equals(""))
            throw new SQLException("Cannot find address information for that order id");
        String updateString = "update orders set ShippedDate = ?, ShipVia = ?, Freight = ? where OrderID = ?";

        PreparedStatement updateStatement = handle.sqlConnect.prepareStatement(updateString);

        updateStatement.setDate(1, new Date(new java.util.Date().getTime()));
        updateStatement.setInt(2, (new Random()).nextInt(2) + 1);
        updateStatement.setDouble(3, (new Random()).nextDouble() * new Random().nextInt(1000) + 0.2);

        updateStatement.setString(4, cur);
        updateStatement.execute();
        return cur;
    }

    private static void RemoveOrder(DatabaseHandle handle) throws SQLException {
        handle.sqlConnect.setAutoCommit(false);
        String deleteString = "DELETE FROM order_details WHERE OrderID = ?";
        String cur;

        PreparedStatement deleteStatement = handle.sqlConnect.prepareStatement(deleteString);

        System.out.println("Please enter the order ID you would like to delete");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        deleteStatement.setInt(1, Integer.parseInt(cur));

        deleteStatement.executeUpdate();

        String deleteString2 = "DELETE FROM orders WHERE OrderID = ?";
        deleteStatement = handle.sqlConnect.prepareStatement(deleteString2);
        deleteStatement.setInt(1, Integer.parseInt(cur));

        deleteStatement.executeUpdate();

        handle.sqlConnect.commit();
        handle.sqlConnect.setAutoCommit(true);
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
//        ResultSetMetaData metaData = keyResults.getMetaData();
        keyResults.last();
//        System.out.println(String.format("Column count: %d, Column 1 name: %s, Row count: %d",
//                metaData.getColumnCount(), metaData.getColumnName(1), keyResults.getRow()));
//        System.out.println("Generated key = " + keyResults.getLong(1));
        long l = keyResults.getLong(1);
        String insertString2 = "insert into orders_details(OrderID, ProductID, UnitPrice, Quantity, Discount) " +
                "values (?, ?, ?, ?, ?)";
        PreparedStatement insertStatement2 = handle.sqlConnect.prepareStatement(insertString2);

//        System.out.println("Please enter the OrderID");
//        while ((cur = sc.nextLine()).equals(""))
//            System.out.println("Null values are not allowed for this field.");

        insertStatement2.setLong(1, l);
        int pID = -1;
        while (pID == -1) {
            try {
                System.out.println("Please enter the ProductID");
                while ((cur = sc.nextLine()).equals(""))
                    System.out.println("Null values are not allowed for this field.");
                pID = Integer.parseInt(cur);
            } catch (NumberFormatException e) {
                System.out.println("Numbers only for this field");
            }
        }
        insertStatement2.setInt(2, pID);

        String selectString = "select UnitPrice from products where ProductID = " + pID;
        PreparedStatement selStatement = handle.sqlConnect.prepareStatement(selectString);
        ResultSet set = selStatement.executeQuery();

        insertStatement.setDouble(3, set.getDouble(1));
        System.out.println("Please enter the Quantity");
        while ((cur = sc.nextLine()).equals(""))
            System.out.println("Null values are not allowed for this field.");
        insertStatement2.setShort(4, Short.parseShort(cur));

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
