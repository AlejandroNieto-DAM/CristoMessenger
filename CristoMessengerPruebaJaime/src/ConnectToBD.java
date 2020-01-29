
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class ConnectToBD {
    
    private String conector;
    private String port;
    private String ip;
    private String dbName;
    private String database;
    
    private String user;
    private String passwd;
    
    ConnectToBD(){
        conector = "jdbc";
        port = "3306";
        ip = "clasedam2.ddns.net";
        dbName = "cristomessenger";
        database = "mysql";
        user = "clasedam2";
        passwd = "root";
    }
    
    
    //******** SETS ********//
    
    public void setConector(String conector){
        this.conector = conector;
    }
    
    public void setPort(String port){
        this.port = port;
    }
    
    public void setIP(String ip){
        this.ip = ip;
    }
    
    public void setDBName(String dbname){
        this.dbName = dbname;
    }
    
    public void setDatabase(String database){
        this.database = database;
    }
    
    public void setUser(String user){
        this.user = user;
    }
    
    public void setPasswd(String passwd){
        this.passwd = passwd;
    }
    
    
    //******** GETS ********//
    
    public String getConectorName(){
        return conector;
    }
    
    public String getPort(){
        return port;
    }
    
    public String getIP(){
        return ip;
    }
    
    public String getDBName(){
        return dbName;
    }
    
    public String getDataBase(){
        return database;
    }
    
    public String getDBUser(){
        return user;
    }
    
    public String getDBPasswd(){
        return passwd;
    }
    
    public Connection getConnector() throws SQLException {

        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.getDBUser());
        connectionProps.put("password", this.getDBPasswd());

        if (this.getDataBase().equals("mysql")) {
            conn = DriverManager.getConnection("jdbc:" + this.getDataBase() + "://" + this.getIP() + ":" + this.getPort() + "/" + this.getDBName(), connectionProps);
        } else if (this.getDataBase().equals("derby")) {
            conn = DriverManager.getConnection("jdbc:" + this.getDataBase() + ":" + this.getDBName() + ";create=true",connectionProps);
        }
        
        CristoMessenger.returnException("Connected to database");
        return conn;
    }
        
}

    

