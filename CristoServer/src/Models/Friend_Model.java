package Models;


import Classes.Friend;
import Classes.User;
import DBConnetion.ConnectToBD;
import cristoserver.CristoServer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class Friend_Model extends ConnectToBD{
    
    private String query;
    Connection myConnection;
    public Friend_Model(){
        
        super();
        try {
            myConnection = this.getConnector();
        } catch (SQLException ex) {
            Logger.getLogger(Friend_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    
    public void setQuery(String query){
        this.query = query;
    }
    
    public String getQuery(){
        return this.query;
    }
    
    public void getFriendsOf(ArrayList<User> amigos, String id_user) throws SQLException{
                
        //select * from user where id_user IN (select id_user_dest from friend where id_user_orig = '@alexinio');
        
        this.setQuery( "select id_user, state " + "from " + this.getDBName() + ".user where id_user IN ( select id_user_dest from " + this.getDBName() + ".friend where id_user_orig = '" + id_user + "')"); 
        
        Statement stmt = myConnection.createStatement();
        
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String login = rs.getString("id_user");
                int request = rs.getInt("state");

                User auxiliar = new User();

                auxiliar.setLogin(login);               
                auxiliar.setEstadoUsuario(request);
                amigos.add(auxiliar);

            }
          
            rs.close();
        } catch (SQLException e ) {

           CristoServer.debug(e.toString());

        } 
        
        stmt.close();
        
    } 

    public Boolean getRelation(String friend1, String friend2) throws SQLException {
        Boolean areFriends = false;
        this.setQuery( "select * " + "from " + this.getDBName() + ".friend where (id_user_orig = '" + friend1 + "' and id_user_dest = '" + friend2 + "') or (id_user_orig = '" + friend2 + "' and id_user_dest = '" + friend1 + "')");
        int state = 0;
        
        Statement stmt = myConnection.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                state++;                
            }
            
            rs.close();
            
        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        stmt.close();
        
        if(state == 2){
            areFriends = true;
        }
        
        return areFriends;
    }
    
    
    public void closeConnection(){
        try {
            this.myConnection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Friend_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.getConnector().close();
        } catch (SQLException ex) {
            Logger.getLogger(Friend_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
