package Models;


import Classes.Message;
import DBConnetion.ConnectToBD;
import cristoserver.CristoServer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
public class Message_Model extends ConnectToBD{
    
    
    private String query;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Message_Model(){
        
        super();
       
    }
    
    public void setQuery(String query){
        this.query = query;
    }
    
    public String getQuery(){
        return this.query;
    }
    
    public void viewTable(Connection con, String dbName, String query, ArrayList<Message> messages) throws SQLException {

        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String login = rs.getString("id_user_orig");
                String login2 = rs.getString("id_user_dest");
                String date = rs.getString("datetime");
                int read = rs.getInt("read");
                int sent = rs.getInt("sent");
                String text = rs.getString("text");
                
                
                Message auxiliar = new Message();

                auxiliar.setId_user_orig(login);
                auxiliar.setId_user_dest(login2);
                auxiliar.setDate(date);
                auxiliar.setRead(read);
                auxiliar.setSent(sent);
                auxiliar.setText(text);

                messages.add(auxiliar);


            }
        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
    } 

    public void getMessages(ArrayList<Message> messages, String login){
        
        this.setQuery( "select * " + "from " + this.getDBName() + ".message WHERE id_user_orig = '" + login + "' or id_user_dest = '" + login + "'"); 
        
        
        try {
            this.viewTable(this.getConnector(), this.getDBName(), this.getQuery(), messages);
        } catch (SQLException ex) {
            CristoServer.debug(ex.toString());
        } 
        
    }
    
    public int getTotalMessagesOfAConversation(String login_orig, String login_dest){
        int totalMensajes = 0;
        this.setQuery( "select * " + "from " + this.getDBName() + ".message WHERE (id_user_orig = '" + login_orig + "' and id_user_dest = '" + login_dest + "') or " + "(id_user_orig = '" + login_dest + "' and id_user_dest = '" + login_orig + "')"); 
        try (Statement stmt = this.getConnector().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                totalMensajes++;
            }
        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        return totalMensajes;
    }
    
    public void getMessages1(ArrayList<Message> messages, String login_orig, String login_dest, String previousDate){
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        this.setQuery( "select * " + "from " + this.getDBName() + ".message WHERE ((id_user_orig = '" + login_orig + "' and id_user_dest = '" + login_dest + "') or " + "(id_user_orig = '" + login_dest + "' and id_user_dest = '" + login_orig + "')) and datetime BETWEEN '" + previousDate + "' and '" + sdf.format(timestamp) + "'"); 
        
        
        try (Statement stmt = this.getConnector().createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String login = rs.getString("id_user_orig");
                String login2 = rs.getString("id_user_dest");
                String date = rs.getString("datetime");
                int read = rs.getInt("read");
                int sent = rs.getInt("sent");
                String text = rs.getString("text");
                
                
                Message auxiliar = new Message();

                auxiliar.setId_user_orig(login);
                auxiliar.setId_user_dest(login2);
                auxiliar.setDate(date);
                auxiliar.setRead(read);
                auxiliar.setSent(sent);
                auxiliar.setText(text);

                messages.add(auxiliar);


            }
        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        
    }

    public void insertMessage(String login, String login_dest, String text){
       
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        try{
           String query = " insert into message values (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStmt = this.getConnector().prepareStatement(query);
            preparedStmt.setString (1, login);
            preparedStmt.setString (2, login_dest);
            preparedStmt.setString (3, sdf.format(timestamp));
            preparedStmt.setInt (4, 0);
            preparedStmt.setInt (5, 1);
            preparedStmt.setString (6, text);
            
            System.out.println("LA QUERYYYY --> " + query);
           
          preparedStmt.execute();

          this.getConnector().close();
          
          //CristoServer.debug("Usuario introducido correctamente!!");
        } catch (Exception e) {
            CristoServer.debug("Got an exception!");
            CristoServer.debug(e.getMessage());
        }
    }


}
