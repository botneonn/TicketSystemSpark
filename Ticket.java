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
public class Ticket {
 public int TicketId;
 public String TicketTitle;
 public String TicketContent;
 public int UserId;
 public String TicketCategory;
 public static Ticket GetFromRs(ResultSet rs) throws SQLException{
    Ticket temp=new Ticket();
    temp.TicketId= rs.getInt("TicketId");
    temp.TicketContent=rs.getString("TicketContent");
    temp.UserId=rs.getInt("UserId");
    temp.TicketCategory=rs.getString("TicketCategory");    
    temp.TicketCategory=rs.getString("TicketTitle");
    return temp;
    }
}
