/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.p.ticketsystemspark;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Salt {

    public static Salt ReadFromRS(ResultSet rs) throws SQLException {
        var temp = new Salt();
        temp.UserID = rs.getInt("UserID");
        temp.Salt = rs.getBytes("Salt");
        return temp;
    }

    public int UserID;
    //1024 bytes
    public byte[] Salt;
    public static Salt CreateNewSalt(int UserID)
    {
        var temp =new Salt();
        temp.Salt = SaltUtils.getNextSalt();
        temp.UserID=UserID;
        return temp;
    }

}
