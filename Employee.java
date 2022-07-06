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
public class Employee {
    public int Id;
    public String Name;
    public String Pass;
    public String Email;
    public Boolean Available;
    public String Team;

    
    public static Employee GetFromRs(ResultSet rs) throws SQLException{
    Employee temp=new Employee();
    temp.Id= rs.getInt("idEmployees");
    temp.Name=rs.getString("EmployeeName");
    temp.Pass=rs.getString("EmployeePassword");
    temp.Email=rs.getString("EmployeeEmail");
    temp.Available=rs.getBoolean("EmployeeAvailable");
    temp.Team = rs.getString("EmployeeTeam");
    return temp;
    }
}
