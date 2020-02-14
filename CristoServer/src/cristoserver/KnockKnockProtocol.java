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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
 
public class KnockKnockProtocol{
    
    private String cadenaPrincipal;
    
    private ArrayList<User> usuarios;
    private ArrayList<Friend> friends;
    private ArrayList<Message> messages;
    
    
    
    private String login_user;
    private String focusedFriend;
    
    private LocalDateTime dateTime;
    
    private Message_Controller message_controller;
    private Friend_Controller friend_controller;
    private User_Controller user_controller;
    
    private PrintWriter out;
    private BufferedReader in;

    public int contadorMsg;
    
    KnockKnockProtocol(){
        
        cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
                
        usuarios = new ArrayList();
        friends = new ArrayList();
        messages = new ArrayList();
        
        user_controller = new User_Controller();
        friend_controller = new Friend_Controller();
        message_controller = new Message_Controller();
        
        
        login_user = "";
        focusedFriend = "";
        
        dateTime = LocalDateTime.now();
        
        contadorMsg = 0;
    }
    
    
    public void setPrintWriter(PrintWriter a){
        this.out = a;
    }
    
    public void setBufferedReader(BufferedReader a){
        this.in = a;
    }
    
    public String processInput(String theInput) throws SQLException, IOException {
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

                 int existe = user_controller.getExistUser(login, pass);  
                  
                 if(existe == 1){
                    System.out.println("Este usuario existe.");
                    CristoServer.debug("Este usuario existe");
                    user_controller.setConnected(login);
                    this.login_user = login;
                    theOutput = getFriends(login);
                 } else {
                     System.out.println("Este usuario no existe.");
                     CristoServer.debug("Este usuario no existe");
                     theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#SERVER#ERROR#BAD_LOGIN";
                 }
     
            } 
            
            if(theInput.contains("MSGS")){
                //System.out.println("Entro msgs");
                 this.messages.clear();
                 theOutput = getTotalMsgs(theInput);                    
  
            }
            
            
            if(theInput.contains("#CHAT")){
                //System.out.println("Entro msgs");
                 theOutput = this.receiveMessage(theInput);
            }
            
            
            
            if(theInput.contains("STATUS")){
                theOutput = this.getUserState(theInput);
                
            }
            
            if(theInput.contains("ALLDATA_USER")){
                theOutput = this.getAllDataUser(theInput);
                
            }
            
        } else {
            CristoServer.debug("MENSAJE INVALIDO");  
        }

        CristoServer.debug(theOutput);
        return theOutput;
    }
    
    
    public String getAllDataUser(String theInput) throws SQLException{
        String cadena = "";
        
        String datos[] = theInput.split("#");
        User focusFriend = new User();
        focusFriend.setLogin(datos[4]);
        user_controller.getUser(focusFriend);
        
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#ALLDATA_USER#<LOGIN>#<NAME>#<SURNAME1>#<SURNAME2>

        cadena += cadenaPrincipal + "#" + dateTime + "#SERVER#ALLDATA_USER#" +  focusFriend.getLogin() + "#"
                    + focusFriend.getNombreUsuario() + "#" + focusFriend.getApellido1() + "#" + focusFriend.getApellido2();
          
        return cadena;
    }
    
    
    public String receiveMessage(String theInput){
        String cadena = "ksfjhsdl";
        //FILTRAR QUE SEAN AMIGOS Y ESTEN REGISTRADOS
        String[] datos = theInput.split("#");
        System.out.println("MEnsaje que se va a insertar --> " + datos[6]);
        message_controller.insertMessage(datos[4], datos[5], datos[6]);
        
        return cadena;
    }
    
    
    public String getUserState(String theInput){
        
        
        String cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#STATUS#" + focusedFriend;
        
        String status = user_controller.getUserState(focusedFriend);
        
        cadena += "#" + status;

        return cadena;
        
    }
    
    public String sendMsg(int i){
        
        String cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#MSGS";
        cadena += "#" + messages.get(i).getId_user_orig() + "#" + messages.get(i).getId_user_dest() + "#" + messages.get(i).getDate() + "#" + messages.get(i).getText() ; 
        return cadena;
        
    }
    
    public String getTotalMsgs(String theInput){
        
        String cadena = "";
        
        String[] receive = theInput.split("#");
        
        
        message_controller.getMessages1(messages, receive[4], receive[5], receive[6]);
        
        this.focusedFriend = receive[5];
        
        cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#MSGS#" + login_user + "#" + focusedFriend + "#" + message_controller.getTotalMessagesOfAConversation(receive[4], receive[5]) + "#" + messages.size();
        
        this.contadorMsg = messages.size();
        return cadena;
    }
        
    public String getFriends(String login){
        
        
        
        String cadena = "";
        
        friend_controller.getFriendsOf(this.usuarios, login);
        
        
        cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#LOGIN_CORRECT#" + login + "#FRIENDS";
        
        int amigos = 0;
        String substringFriends = "";
        
        
                
        for(int j = 0; j < usuarios.size(); j++){

            substringFriends +=  "#" + usuarios.get(j).getLogin() + "#";

            if(usuarios.get(j).getEstadoUsuario().equals(true)){
                substringFriends += "CONNECTED";
            } else {
                substringFriends += "NOT_CONNECTED";
            }
        }  

        cadena += "#" + usuarios.size() + substringFriends;

        return cadena;
    }
    
    public void setDisconnected() throws SQLException{
        user_controller.setDisconnected(login_user);
    }
    
    public String getLogin(){
        return this.login_user;
    }
}
