/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amber;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author MRUDULA
 */

public class DBConn {

    // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost:3307/hindi";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "12345";
    
    Connection conn = null;
    Statement stmt1 = null;
    Statement stmt2 = null;
    
    
    public void getConnection() {
        
       
        try{                    
            System.out.println("registering jdbc driver.....");
        
            //STEP 2: Register JDBC driver
            Class<?> forName = Class.forName("com.mysql.jdbc.Driver");
            System.out.println("JDBC driver registerd SUCCESSFULLY !!");

             //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            System.out.println("Connection to database SUCCESSFUL !");
            
            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            
           
            
        } catch(SQLException | ClassNotFoundException se){}
        
        finally{
                    //finally block used to close resources
                    try{
                        if(stmt1!=null)
                            stmt1.close();
                    }catch(SQLException se2){
                     }// nothing we can do
                    try{
                        if(stmt2!=null)
                            stmt2.close();
                    }catch(SQLException se2){
                     }// nothing we can do
                    try{
                         if(conn!=null)
                             conn.close();
                        }catch(SQLException s){}
        }
    }   
    
    public void stmtConnection() {
        try{
            stmt1 = conn.createStatement();
        } catch(Exception e){}
        
    }
    
    public ResultSet runQuery(String synonym_query){
        try{
            ResultSet rs1 = stmt2.executeQuery(synonym_query);
            return rs1;
        } catch(Exception e){}
        return null;
    }

}

