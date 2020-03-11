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

    public void getMessages(ArrayList<Message> messages, String login) throws SQLException{
        
        this.setQuery( "select * " + "from " + this.getDBName() + ".message WHERE id_user_orig = '" + login + "' or id_user_dest = '" + login + "'"); 
        
        Statement stmt = this.getConnector().createStatement();
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String login_orig = rs.getString("id_user_orig");
                String login2 = rs.getString("id_user_dest");
                String date = rs.getString("datetime");
                int read = rs.getInt("read_msg");
                int sent = rs.getInt("sent");
                String text = rs.getString("text");
                
                
                Message auxiliar = new Message();

                auxiliar.setId_user_orig(login_orig);
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
        
        stmt.close();
        this.getConnector().close();
        
        
    }
    
    public int getTotalMessagesOfAConversation(String login_orig, String login_dest) throws SQLException{
        int totalMensajes = 0;
        this.setQuery( "select * " + "from " + this.getDBName() + ".message WHERE (id_user_orig = '" + login_orig + "' and id_user_dest = '" + login_dest + "') or " + "(id_user_orig = '" + login_dest + "' and id_user_dest = '" + login_orig + "')"); 
        
        Statement stmt = this.getConnector().createStatement();
        
        try  {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                totalMensajes++;
            }
            
            

        } catch (SQLException e ) {
            CristoServer.debug(e.toString());
        }
        
        stmt.close();
        this.getConnector().close();
        
        return totalMensajes;
    }
    
    public void getMessages1(ArrayList<Message> messages, String login_orig, String login_dest, String previousDate) throws SQLException{
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        this.setQuery( "select * " + "from " + this.getDBName() + ".message WHERE ((id_user_orig = '" + login_orig + "' and id_user_dest = '" + login_dest + "') or " + "(id_user_orig = '" + login_dest + "' and id_user_dest = '" + login_orig + "')) and datetime BETWEEN '" + previousDate + "' and '" + sdf.format(timestamp) + "'"); 
        
        Statement stmt = this.getConnector().createStatement();
        
        try  {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {

                String login = rs.getString("id_user_orig");
                String login2 = rs.getString("id_user_dest");
                String date = rs.getString("datetime");
                int read = rs.getInt("read_msg");
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
        
        stmt.close();
        this.getConnector().close();
        
        for(int i = 0; i < messages.size(); i++){
            if(messages.get(i).getRead() == false && messages.get(i).getId_user_orig().equals(login_dest)){
                                
                String query = "update " + this.getDBName() + ".message "
                        + "set read_msg = 1 "
                        + "where datetime = '" + messages.get(i).getDate() + "' "
                        + "and id_user_orig = '" + login_dest + "' "
                        + "and id_user_dest = '" + login_orig + "'";
                
                messages.get(i).setRead(1);

                PreparedStatement preparedStmt = this.getConnector().prepareStatement(query);
                preparedStmt.executeUpdate();
                preparedStmt.close();
                this.getConnector().close();   
            }
        }      
    }

    public void insertMessage(String login, String login_dest, String text, String datetime) throws SQLException{
               
        PreparedStatement preparedStmt = this.getConnector().prepareStatement(query);
        try{
           String query = " insert into message values (?, ?, ?, ?, ?, ?)";
           
            preparedStmt.setString (1, login);
            preparedStmt.setString (2, login_dest);
            preparedStmt.setString (3, datetime);
            preparedStmt.setInt (4, 0);
            preparedStmt.setInt (5, 1);
            preparedStmt.setString (6, text);
                       
          preparedStmt.execute();
          
          
        } catch (Exception e) {
            CristoServer.debug("Got an exception!");
            CristoServer.debug(e.getMessage());
        }
        
        preparedStmt.close();
          
        this.getConnector().close();
    }


}
