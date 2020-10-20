/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author rmunene
 */
public class DBFunctions {

    public Map<String, String> fundsTransfers(String phonumber, String recepientNo, int amount) {
        Map<String, String> respMap = new HashMap<>();
        // check if recepient Exists
        boolean notExists = checkIfExist(recepientNo);
        if (notExists) {
            respMap.put("status", "0");
            respMap.put("message", "Account does not exist");
        } else {
            // check balance
            int currentBalance = checkCustomerBalance(phonumber);
            if (currentBalance >= amount) {
                // debit customer
                drCustomer(phonumber, currentBalance, amount);
                // credit recepient
                crRecepient(recepientNo, amount);
                // response
                respMap.put("status", "1");
                respMap.put("message", "Funds Transfer Succesful");
            } else {
                respMap.put("status", "0");
                respMap.put("message", "Insufficient Balance");
            }
        }
        return respMap;
    }

    private boolean checkIfExist(String recepientNo) {
        boolean exist = false;
        String sql = "SELECT ID FROM tbaccounts  WHERE PhoneNumber=?";
        try (Connection connection = DBconnection.Connect();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, recepientNo);
            ps.executeQuery();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    exist = true;
                }
            }
        } catch (Exception ex) {
            // log
        }
        return exist;
    }

    private int checkCustomerBalance(String phonenumber) {
        int balance = 0;
        String sql = "SELECT AvailableBal FROM tbaccounts  WHERE PhoneNumber=?";
        try (Connection connection = DBconnection.Connect();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phonenumber);
            ps.executeQuery();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    balance = Integer.valueOf(rs.getString("AvailableBal"));
                }
            }
        } catch (Exception ex) {
            // log
        }
        return balance;
    }

    private void drCustomer(String phonenumber, int availableAmount, int amount) {
        int debitAmount = availableAmount - amount;
        String sql = "update tbaccounts set AvailableBal=?,ActualBal=?  WHERE PhoneNumber=?";
        try (Connection connection = DBconnection.Connect();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, debitAmount);
            ps.setInt(2, debitAmount);
            ps.setString(3, phonenumber);
            ps.execute();
        } catch (Exception ex) {
            // log
        }
    }

    private void crRecepient(String recepientNo, int amount) {
        String sql = "update tbaccounts set AvailableBal=AvailableBal+?,ActualBal=ActualBal+?  WHERE PhoneNumber=?";
        try (Connection connection = DBconnection.Connect();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setInt(2, amount);
            ps.setString(3, recepientNo);
            ps.execute();
        } catch (Exception ex) {
            // log
        }
    }

}
