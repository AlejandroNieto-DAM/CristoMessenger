
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
public class Message_Model extends ConnectToBD{
    
    
    private String query;
     
    Message_Model(){
        
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
                String date = rs.getString("date");
                String hour = rs.getString("hour");
                int read = rs.getInt("read");
                int sent = rs.getInt("sent");
                String text = rs.getString("text");
                
                
                Message auxiliar = new Message();

                auxiliar.setId_user_orig(login);
                auxiliar.setId_user_dest(login2);
                auxiliar.setDate(date);
                auxiliar.setHour(hour);
                auxiliar.setRead(read);
                auxiliar.setSent(sent);
                auxiliar.setText(text);

                messages.add(auxiliar);


            }
        } catch (SQLException e ) {
            CristoMessenger.returnException(e.toString());
        }
    } 

    public void getMessages(ArrayList<Message> messages){
        
        this.setQuery( "select * " + "from " + this.getDBName() + ".message"); 
        
        
        try {
            this.viewTable(this.getConnector(), this.getDBName(), this.getQuery(), messages);
        } catch (SQLException ex) {
            CristoMessenger.returnException(ex.toString());
        } 
        
    }
}
