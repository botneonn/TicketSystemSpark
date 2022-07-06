/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.p.ticketsystemspark;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Praktikant
 */
public class User {

   
    public int UserID;
    public String UserName;
    public String UserEmail;
    public String UserMobilePhone;
    public byte[] UserPassword;
    public Boolean UserLockedOut;

    public static User GetFromRs(ResultSet rs) throws SQLException {
        User temp = new User();
        temp.UserID = rs.getInt("UserID");
        temp.UserName = rs.getString("UserName");
        temp.UserEmail = rs.getString("UserEmail");
        temp.UserMobilePhone = rs.getString("UserMobilePhone");
        temp.UserPassword = rs.getBytes("UserPassword");
        temp.UserLockedOut=rs.getBoolean("UserLockedOut");
        return temp;
    }

    public Salt GetUserSalt() throws SQLException, NotFoundException {
        var conn = DatabaseConnectionCreator.GetConnection();
        var ps = conn.prepareStatement("select * from d1.usersalts where UserID=" + this.UserID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return Salt.ReadFromRS(rs);
        } else {
            throw new NotFoundException();
        }
    }

    public static User GetFromUserName(String username) throws SQLException, NotFoundException  {
        var conn = DatabaseConnectionCreator.GetConnection();
        var ps = conn.prepareStatement("select * from d1.users where UserName=?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return User.GetFromRs(rs);
        } else {
            throw new NotFoundException();
        }
    }

    public static User CreateUser(String Username, String Password, String Email, String MobilePhone) throws InvalidUsernameException, InvalidEmailException, UnknownErrorException, SQLException {

        var temp = new User();
        if (Username.length() == 0 || Username.length() > 45) {
            throw new InvalidUsernameException("Username must be at least a character long and at most 45 characters");
        }
        temp.UserName = Username;

        if (!Regex.EmailPattern.matcher(Email).matches()) {
            throw new InvalidEmailException("Email is invalid");
        }
        temp.UserEmail = Email;
        //TODO: PHONE REGEX GOES HERE
        temp.UserMobilePhone = MobilePhone;

        var conn = DatabaseConnectionCreator.GetConnection();

        try {
            var ps = conn.prepareStatement("INSERT INTO `d1`.`users` ( `UserEmail`, `UserName`, `UserMobilePhone`) VALUES ( ?, ?, ?); ");
            ps.setString(1, Email);
            ps.setString(2, Username);
            ps.setString(3, MobilePhone);
            ps.execute();
            var ps2 = conn.prepareStatement("select last_insert_id() as last_id;");
            var a = ps2.executeQuery();
            a.next();
            var id = a.getInt("last_id");
            temp.UserID = id;
            var salt = Salt.CreateNewSalt(id);
            var pss = conn.prepareStatement("INSERT INTO `d1`.`usersalts` (`UserID`, `Salt`) VALUES (?, ?);");
            pss.setInt(1, salt.UserID);
            pss.setBytes(2, salt.Salt);
            pss.execute();

            var ups = conn.prepareStatement("UPDATE `d1`.`users` SET `UserPassword` = ? WHERE (`UserId` = ?);");
            var hash = SaltUtils.hash(Password.toCharArray(), salt.Salt);
            temp.UserPassword = hash;
            ups.setBytes(1, hash);
            ups.setInt(2, id);
            ups.execute();

            return temp;
        } finally {
            conn.close();
        }
    }
}
