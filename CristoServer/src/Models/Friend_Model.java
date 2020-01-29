package Models;


import Classes.Friend;
import DBConnetion.ConnectToBD;
import cristoserver.CristoServer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


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
     
    public Friend_Model(){
        
        super();
       
    }
    
    public void setQuery(String query){
        this.query = query;
    }
    
    public String getQuery(){
        return this.query;
    }
    
    public void viewTable(Connection con, String dbName, String query, ArrayList<Friend> amigos) throws SQLException {

        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String login = rs.getString("id_user_orig");
                String login2 = rs.getString("id_user_dest");
                int request = rs.getInt("accept_request");
           
                Friend auxiliar = new Friend();
                if(request == 1){
                    auxiliar.setLogin_orig(login);
                    auxiliar.setLogin_des(login2);
                    auxiliar.setAccept_request(request);

                    amigos.add(auxiliar);
                }
            }
        } catch (SQLException e ) {
            
           CristoServer.debug(e.toString());
           
        }
    } 

    public void getFriendsOf(ArrayList<Friend> amigos, String id_user){
        
        CristoServer.debug("Debug: getFriendsOf");
        
        this.setQuery( "select id_user_orig, id_user_dest, accept_request " + "from " + this.getDBName() + ".friend where id_user_orig = '" + id_user + "' or id_user_dest = '" + id_user + "'"); 
        
        try {
            this.viewTable(this.getConnector(), this.getDBName(), this.getQuery(), amigos);
        } catch (SQLException ex) {
            CristoServer.debug(ex.toString());
        } 
        
    } 
    
}
