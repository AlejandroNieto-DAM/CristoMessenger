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
    String focusedFriend = "";
    LocalDateTime dateTime = LocalDateTime.now();
    Message_Controller myC = new Message_Controller();
    
    int contadorMsg = 0;
               


 
 
    public String processInput(String theInput) throws SQLException {
        String theOutput = null;
        this.friends.clear();
        
        this.usuarios.clear();
  
        if(theInput.startsWith(cadenaPrincipal)){
            
            if(theInput.contains("LOGIN")){
                
                
                String login = "";
                String pass = "";
                 int contador = 0;
                 for(int i = 0; i < theInput.length(); i++){
                     if(theInput.charAt(i) == '#'){
                         contador++; 
                     }

                     if(contador == 4){
                             login += theInput.charAt(i);
                     }

                     if(contador == 5){
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
                
                if(theInput.contains("OK_SEND")){
                    theOutput = sendMsg();
                    this.contadorMsg++;
    
                } else {
                   //System.out.println("Entro msgs");
                    this.messages.clear();
                    theOutput = getTotalMsgs(theInput); 
                    contadorMsg = 0;
                   
                }
                
            }
            
            if(theInput.contains(("ALL_RECEIVED"))){
                if(contadorMsg < this.messages.size()){
                        theOutput = sendMsg();
                        this.contadorMsg++;
                } else {
                    theOutput = "yeye";
                }
                
            }
            
            if(theInput.contains("STATUS")){
                theOutput = this.getUserState(theInput);
                
            }
            
        } else {
            CristoServer.debug("MENSAJE INVALIDO");  
        }

        CristoServer.debug(theOutput);
        return theOutput;
    }
    
    
    public String getUserState(String theInput){
        
        
        String cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#STATUS#" + focusedFriend;
        
        String status = a.getUserState(focusedFriend);
        
        cadena += "#" + status;

        return cadena;
        
    }
    
    public String sendMsg(){
        
        String cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#MSG";
        //myC.getMessages1(messages, login, this.focusedFriend);
        cadena += "#" + messages.get(contadorMsg).getId_user_orig() + "#" + messages.get(contadorMsg).getId_user_dest() + "#" + messages.get(contadorMsg).getDate() + "." + messages.get(contadorMsg).getHour() + "#" + messages.get(contadorMsg).getText() ;
        
        
        return cadena;
        
    }
    
    public String getTotalMsgs(String theInput){
        
        String cadena = "";
        
        String[] receive = theInput.split("#");
        
        myC.getMessages1(messages, receive[4], receive[5]);
        
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#MSGS#<LOGIN_CLIENT#<LOGIN_AMIGO>#N_MESSAGES#
        this.focusedFriend = receive[5];
        
        cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#MSGS#" + login + "#" + focusedFriend + "#" + messages.size();
        
        
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
        
        
        
        return cadena;
    }
    
    public void setDisconnected() throws SQLException{
        a.setDisconnected(login);
    }
}
