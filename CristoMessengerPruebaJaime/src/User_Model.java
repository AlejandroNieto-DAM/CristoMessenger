
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
     
    User_Model(){
        
        super();
       
    }
    
    public void setQuery(String query){
        this.query = query;
    }
    
    public String getQuery(){
        return this.query;
    }
    
    public void viewTable(Connection con, String dbName, String query, ArrayList<User> usuarios) throws SQLException {

        try (Statement stmt = con.createStatement()) {
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
        } catch (SQLException e ) {
            CristoMessenger.returnException(e.toString());
        }
    } 

    public void getUsuariosLoginPasswd(ArrayList<User> usuarios){
        
        this.setQuery( "select id_user, name, password, surname1, surname2, state " + "from " + this.getDBName() + ".user"); 
        
        try {
            this.viewTable(this.getConnector(), this.getDBName(), this.getQuery(), usuarios);
        } catch (SQLException ex) {
            CristoMessenger.returnException(ex.toString());
        } 
        
    }
    
    public void insertUser(User user){
        
        try{
         
          String query = " insert into user (id_user, name, password, surname1, surname2, photo, state)"
            + " values (?, ?, ?, ?, ?, ?, ?)";

          PreparedStatement preparedStmt = this.getConnector().prepareStatement(query);
          preparedStmt.setString (1, user.getLogin());
          preparedStmt.setString (2, user.getNombreUsuario());
          preparedStmt.setString (3, user.getPasswd());
          preparedStmt.setString (4, user.getApellido1());
          preparedStmt.setString (5, user.getApellido2());
          preparedStmt.setString (6, "eyeyey");
          preparedStmt.setInt (7, 0);
          
         
          
            
          preparedStmt.execute();

          this.getConnector().close();
          
          CristoMessenger.returnException("Usuario introducido correctamente!!");
        }
        catch (Exception e){
          CristoMessenger.returnException("Got an exception!");
          CristoMessenger.returnException(e.getMessage());
        }
    }
    
    public void getUser(User auxiliar) throws SQLException{
        
        this.setQuery( "select * " + "from " + this.getDBName() + ".user where id_user = '" + auxiliar.getLogin() + "'"); 
        
        try (Statement stmt = this.getConnector().createStatement()) {
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
        } catch (SQLException e ) {
            CristoMessenger.returnException(e.toString());
        }
        
         
    }
}
