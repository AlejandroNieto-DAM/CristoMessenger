/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import Classes.Friend;
import Classes.Message;
import Classes.User;
import Controllers.Friend_Controller;
import Controllers.Message_Controller;
import Controllers.User_Controller;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
 
public class KnockKnockProtocol{
    
    private String cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
    
    ArrayList<User> usuarios = new ArrayList();
    ArrayList<Friend> friends = new ArrayList();
    ArrayList<Message> messages = new ArrayList();
    User_Controller a = new User_Controller();
    Friend_Controller myController = new Friend_Controller();
    String login;
    
    LocalDateTime dateTime = LocalDateTime.now();
    Message_Controller myC = new Message_Controller();
               


 
 
    public String processInput(String theInput) throws SQLException {
        String theOutput = null;
        this.friends.clear();
        this.messages.clear();
        this.usuarios.clear();
        
        if(theInput.startsWith(cadenaPrincipal)){
            
            if(theInput.contains("LOGIN")){
                
                System.out.println("Entro login");
                
                String login = "";
                String pass = "";
                 int contador = 0;
                 for(int i = 0; i < theInput.length(); i++){
                     if(theInput.charAt(i) == '#'){
                         contador++; 
                     }

                     if(contador == 3){
                             login += theInput.charAt(i);
                     }

                     if(contador == 4){
                         pass += theInput.charAt(i);
                     }
                 }

                 login = login.substring(1, login.length());
                 pass = pass.substring(1, pass.length());

                 a.getUsuarios(usuarios);  
                 
                 boolean encontrado = false;

                 for(int i = 0; i < usuarios.size() && encontrado == false; i++){
                     
                     CristoServer.debug(usuarios.get(i).getLogin() + " " + usuarios.get(i).getPasswd() + " " + usuarios.get(i).getEstadoUsuario());
                     
                     if(usuarios.get(i).getLogin().equals(login) && usuarios.get(i).getPasswd().equals(pass)){
                         encontrado = true;
                     }
                     
                 }
                 
                 if(encontrado == true){
                    System.out.println("Este usuario existe.");
                    CristoServer.debug("Este usuario existe");
                    a.setConnected(login);
                    this.login = login;
                    theOutput = getFriends(login);
                 } else {
                     System.out.println("Este usuario no existe.");
                     CristoServer.debug("Este usuario no existe");
                     theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#SERVER#ERROR#BAD_LOGIN";
                 }
     
            } 
            
            if(theInput.contains("MSGS")){
  
                System.out.println("Entro msgs");
                theOutput = getMsgs(theInput); 
                CristoServer.debug(theOutput);
                
            }
            
        } else {
            CristoServer.debug("MENSAJE INVALIDO");  
        }

        CristoServer.debug(theOutput);
        return theOutput;
    }
    
    
    public String getMsgs(String theInput){
        
        String cadena = "";
        
        String[] receive = theInput.split("#");
        
        for(String a : receive){
            System.out.println(a);
        }
      
        myC.getMessages1(messages, receive[3], receive[4]);
        
        cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#MSGS#" + receive[3] + "#" + receive[4] + "#LIST";
        
        for(int i = 0; i < messages.size(); i++){
            cadena += "#" + messages.get(i).getId_user_orig() + "#" + messages.get(i).getDate() + "." + messages.get(i).getHour() + "#" + messages.get(i).getText() ;
        }
        
        cadena += "#END";
        
        return cadena;
    }
        
    public String getFriends(String login){
        
        
        
        String cadena = "";
        
        myController.getFriendsOf(this.friends, login);
        
        cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#LOGIN_CORRECT#" + login  + "#FRIENDS";
        
        int amigos = 0;
        String substringFriends = "";
        
        for(int i = 0; i < friends.size(); i++){ 
            
            if(friends.get(i).getLogin_orig().equals(login)){
                amigos++;
                
                for(int j = 0; j < usuarios.size(); j++){
                    
                    if(friends.get(i).getLogin_des().equals(usuarios.get(j).getLogin())){
                        substringFriends +=  "#" + usuarios.get(j).getLogin() + "#";
                        
                        if(usuarios.get(j).getEstadoUsuario().equals(true)){
                            substringFriends += "CONNECTED";
                        } else {
                            substringFriends += "NOT_CONNECTED";
                        }
                        
                    }
                    
                }  
                
            }
            
        }
        
        cadena += "#" + amigos + substringFriends;
        
        System.out.println("CADENA --> " + cadena);
        
        
        return cadena;
    }
    
    public void setDisconnected() throws SQLException{
        a.setDisconnected(login);
    }
}
