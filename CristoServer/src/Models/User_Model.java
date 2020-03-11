package Models;


import Classes.User;
import DBConnetion.ConnectToBD;
import cristoserver.CristoServer;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class User_Model extends ConnectToBD{
    
    private String query;
    
    Connection myConnection;
     
    public User_Model(){
        
        super();
        try {
            myConnection = this.getConnector();
        } catch (SQLException ex) {
            Logger.getLogger(User_Model.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setQuery(String query){
        this.query = query;
    }
    
    public String getQuery(){
        return this.query;
    }
    
    public void viewTable(Connection con, String dbName, String query, ArrayList<User> usuarios) throws SQLException {

        Statement stmt = con.createStatement();
                
        try  {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String login = rs.getString("id_user");
                String passwd = rs.getString("password");
                String nombre = rs.getString("name");
                String surname1 = rs.getString("surname1");
                String surname2 = rs.getString("surname2");
                int stateInt = rs.getInt("state");
                
                User auxiliar = new User();

                auxiliar.setLogin(login);
                auxiliar.setNombreUsuario(nombre);
                auxiliar.setApellido1(surname1);
                auxiliar.setApellido2(surname2);
                auxiliar.setPasswd(passwd);
                auxiliar.setEstadoUsuario(stateInt);

                usuarios.add(auxiliar);

            }
            
            rs.close();
            
        } catch (SQLException e ) {
             CristoServer.debug(e.toString());
        }
        
        stmt.close();
        con.close();
    } 

    public void getUsuariosLoginPasswd(ArrayList<User> usuarios){
        
        this.setQuery( "select id_user, name, password, surname1, surname2, state " + "from " + this.getDBName() + ".user"); 
        
        try {
            this.viewTable(myConnection, this.getDBName(), this.getQuery(), usuarios);
        } catch (SQLException ex) {
            CristoServer.debug(ex.toString());
        } 
        
    }
    
    public void insertUser(User user) throws SQLException{
        
        PreparedStatement preparedStmt = myConnection.prepareStatement(query);
        
        try{
         
          String query = " insert into user (id_user, name, password, surname1, surname2, photo, state)"
            + " values (?, ?, ?, ?, ?, ?, ?)";

          
          preparedStmt.setString (1, user.getLogin());
          preparedStmt.setString (2, user.getNombreUsuario());
          preparedStmt.setString (3, user.getPasswd());
          preparedStmt.setString (4, user.getApellido1());
          preparedStmt.setString (5, user.getApellido2());
          preparedStmt.setString (6, "eyeyey");
          preparedStmt.setInt (7, 0);
          
   
          preparedStmt.execute();
          CristoServer.debug("Usuario introducido correctamente!!");
          
        } catch (Exception e){
          CristoServer.debug("Got an exception!");
          CristoServer.debug(e.getMessage());
        }
        
        preparedStmt.close();
          
    }
    
    public void getUser(User auxiliar) throws SQLException{
        
        this.setQuery( "select * " + "from " + this.getDBName() + ".user where id_user = '" + auxiliar.getLogin() + "'"); 
        
        Statement stmt = myConnection.createStatement();
        
        try  {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String passwd = rs.getString("password");
                String nombre = rs.getString("name");
                String surname1 = rs.getString("surname1");
                String surname2 = rs.getString("surname2");
                int stateInt = rs.getInt("state");
                
                auxiliar.setNombreUsuario(nombre);
                auxiliar.setApellido1(surname1);
                auxiliar.setApellido2(surname2);
                auxiliar.setPasswd(passwd);
                auxiliar.setEstadoUsuario(stateInt);
            
            }
            
            
            rs.close();
        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        stmt.close();
        
         
    }
    
    
    public int getExistUser(String login, String pass) throws SQLException{
        int existe = 0;
        this.setQuery( "select * " + "from " + this.getDBName() + ".user where id_user = '" + login + "' and password = '" + pass + "'"); 
        
        
        Statement stmt = myConnection.createStatement();
                
        try  {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                existe++;
            }
            
            rs.close();
            

        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        stmt.close();
        
        return existe;
    }
    
    public String getUserState(String login) throws SQLException{
        this.setQuery( "select state " + "from " + this.getDBName() + ".user where id_user = '" + login + "'");
        int state = 0;
        
        Statement stmt = myConnection.createStatement();
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                state = rs.getInt("state");
                   
            }
            
            rs.close();

        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        String userState = "";
        if(state == 1){
            userState = "CONNECTED";
        } else {
            userState = "NOT_CONNECTED";
        }
        
        stmt.close();
        
        return userState;
    }

    public void setConnected(String login) throws SQLException {
        String query = "update " + this.getDBName()+ ".user "
                + "set state = 1 "
                + "where id_user = '" + login + "'";

        PreparedStatement preparedStmt = myConnection.prepareStatement(query);
        
                
        preparedStmt.executeUpdate();
        
        preparedStmt.close();
        

    }
    
    public void setDisconnected(String login) throws SQLException{
        String query = "update " + this.getDBName()+ ".user "
                + "set state = 0 "
                + "where id_user = '" + login + "'";

        PreparedStatement preparedStmt = myConnection.prepareStatement(query);
        
        preparedStmt.executeUpdate();
        preparedStmt.close();
        
        

    }
    
    
    public Boolean findUser(String login) throws SQLException{
        this.setQuery( "select * " + "from " + this.getDBName() + ".user where id_user = '" + login + "'");
        int state = 0;
        Boolean existe = false;
        
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
        
        if(state != 0){
            existe = true;
        }
        
        
        stmt.close();
        
        return existe;

    }

    public String getUrlPhoto(String login) throws SQLException {
        this.setQuery( "select photo " + "from " + this.getDBName() + ".user where id_user = '" + login + "'");
        String url = "";
        
        Statement stmt = myConnection.createStatement();
        
        try  {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                url = rs.getString("photo");
            }
            
            rs.close();
            

        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        stmt.close();
        
 
        return url;
    }
    
    public void setDisconnectedOtherUsers(ArrayList conectados) throws SQLException{
        
        String query =  "update " + this.getDBName() + ".user set state = 0 where id_user not in (";
                
        for(int i = 0; i < conectados.size() - 1; i++){
            query += "'" + conectados.get(i) + "',";
        }
        
        query += "'" + conectados.get(conectados.size() - 1) + "')";
        
        PreparedStatement preparedStmt = myConnection.prepareStatement(query);
        
        
        preparedStmt.executeUpdate();
        preparedStmt.close();
        
        
        
        
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
