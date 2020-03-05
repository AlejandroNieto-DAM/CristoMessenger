/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import Classes.Message;
import Classes.User;
import Controllers.Friend_Controller;
import Controllers.Message_Controller;
import Controllers.User_Controller;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;

public class KnockKnockProtocol{
    
    private String cadenaPrincipal;
    
    private ArrayList<User> usuarios;
    private ArrayList<Message> messages;
    

    private String login_user;
    private String focusedFriend;
        
    private Message_Controller message_controller;
    private Friend_Controller friend_controller;
    private User_Controller user_controller;

    public int contadorMsg;
    
    File file;
    FileInputStream fin = null;
   
    int separador = 0;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
 
    KnockKnockProtocol(){
        
        cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
                
        usuarios = new ArrayList();
        messages = new ArrayList();
        
        user_controller = new User_Controller();
        friend_controller = new Friend_Controller();
        message_controller = new Message_Controller();
        
        
        login_user = "";
        focusedFriend = "";
        
        
        contadorMsg = 0;
    }
    
    public String processInput(String theInput) throws SQLException, IOException {
        String theOutput = null;

        this.usuarios.clear();
  
        if(theInput.startsWith(cadenaPrincipal)){
            
            if(theInput.contains("LOGIN") && this.contadorPaquetes(theInput) == 6){
                     
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
                 String state = "";
                 if(existe == 1){
                    state = user_controller.getUserState(login);
                 }
                  
                 if(existe == 1 && state.contains("NOT_CONNECTED")){
                    System.out.println("Este usuario existe.");
                    CristoServer.debug("Este usuario existe");
                    user_controller.setConnected(login);
                    this.login_user = login;
                    theOutput = getFriends(login);
                 } else {
                     System.out.println("Este usuario no existe.");
                     CristoServer.debug("Este usuario no existe");
                     theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#ERROR#BAD_LOGIN";
                 }
     
            } 
            
            if(theInput.contains("MSGS")){
                if(this.contadorPaquetes(theInput) == 7){
                    this.messages.clear();
                    theOutput = getTotalMsgs(theInput);  
                } else {
                    theOutput = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#BAD_MSGPKG";
                }   
            }
 
            if(theInput.contains("STATUS")){
                if(this.contadorPaquetes(theInput) == 6){
                    theOutput = this.getUserState(theInput); 
                } else {
                    theOutput = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#BAD_PKG";
                }                
            }
            
            if(theInput.contains("ALLDATA_USER")){
                if(this.contadorPaquetes(theInput) == 5){
                   theOutput = this.getAllDataUser(theInput); 
                } else {
                   theOutput = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#BAD_PKG"; 
                }
            }
    
        } else {
            
            CristoServer.debug("MENSAJE INVALIDO");  
            
        }

        CristoServer.debug(theOutput);
        return theOutput;
    }
    
    public void loadFile(String theInput) throws FileNotFoundException{
        String[] datos = theInput.split("#"); 
        String foto = this.user_controller.getUrlPhoto(datos[4]);
        try{
            file = new File(foto);
            fin = new FileInputStream(file);
        } catch(FileNotFoundException e){
            file = new File("data/defaultPhoto.jpeg");
            fin = new FileInputStream(file);
        }
        
        separador = (int)file.length();
    }
    
    public int getSeparador(){
        return separador;
    }
    
    public String getPhotoUser() throws FileNotFoundException, IOException{
        String cadena = "";

        int  i = 0;
        int contador = 0;
        int[] fileContent;
        int bytesPorLeer = 511;
        String toEncode = "";

        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#RESPONSE_MULTIMEDIA#" + 
                this.login_user + "#" + 
                (int)file.length() + "#";

        if(separador > bytesPorLeer){
            fileContent = new int[bytesPorLeer];
            cadena += bytesPorLeer + "#";
        } else {
            fileContent = new int[separador];
            bytesPorLeer = separador;
            cadena += separador + "#";
        }

        while(contador < bytesPorLeer){
            i = fin.read();
            fileContent[contador] = i;
            toEncode += (char)i;
            
            contador++;
        }

        String encodedString = Base64.getEncoder().encodeToString(toEncode.getBytes());
        
        cadena += encodedString;

        contador = 0;

        separador -= 512; 
        
        if(separador < 0){
            separador = 0;
            fin.close();
        }
  
        return cadena;
    }
    
    
    public String getAllDataUser(String theInput){
        String cadena = "";
        
        String datos[] = theInput.split("#");
        User focusFriend = new User();
        focusFriend.setLogin(datos[4]);
        try {
            user_controller.getUser(focusFriend);
            cadena += cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#ALLDATA_USER#" +  focusFriend.getLogin() + "#"
                    + focusFriend.getNombreUsuario() + "#" + focusFriend.getApellido1() + "#" + focusFriend.getApellido2();
        } catch (SQLException ex) {
            //Logger.getLogger(KnockKnockProtocol.class.getName()).log(Level.SEVERE, null, ex);
            cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#BAD_PKG";          
        }
  
        return cadena;
    }
    
    
    public String receiveMessage(String theInput) throws SQLException{
        String cadena = "";
        String[] datos = theInput.split("#");
        
        Boolean existeUser1 = user_controller.findUser(datos[4]);
        Boolean existeUser2 = user_controller.findUser(datos[5]); 
        Boolean areFriends = friend_controller.getRelation(datos[4], datos[5]);

       if(existeUser1 && existeUser2 && areFriends){
            message_controller.insertMessage(datos[4], datos[5], datos[6], datos[1]);
            cadena = "Bien";
       } else {
            cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#FORBIDDEN_CHAT";
       }
        
        return cadena;
    }
    
    
    public String getUserState(String theInput){
        
        String[] datos = theInput.split("#");
        
        String cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#STATUS#" + datos[5];
        
        String status = user_controller.getUserState(datos[5]);
        
        cadena += "#" + status;

        return cadena;
        
    }
    
    public String sendMsg(int i){
        
        String cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#MSGS";
        cadena += "#" + messages.get(i).getId_user_orig() + "#" + messages.get(i).getId_user_dest() + "#" + messages.get(i).getDate() + "#" + messages.get(i).getText(); 
        
        /*if(messages.get(i).getRead()){
            cadena += "#LEIDO";
        } else {
            cadena += "#NO_LEIDO";
        }*/
        
        return cadena;
        
    }
    
    public String getTotalMsgs(String theInput){
        
        String cadena = "";
        
        String[] receive = theInput.split("#");
        
        
        try {
            message_controller.getMessages1(messages, receive[4], receive[5], receive[6]);
            this.focusedFriend = receive[5];
        
            cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#MSGS#" + login_user + "#" + focusedFriend + "#" + message_controller.getTotalMessagesOfAConversation(receive[4], receive[5]) + "#" + messages.size();

            this.contadorMsg = messages.size();
            
        } catch (SQLException ex) {
            //Logger.getLogger(KnockKnockProtocol.class.getName()).log(Level.SEVERE, null, ex);
            cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#BAD_MSGPKG";
        }
        
        return cadena; 
        
    }
        
    public String getFriends(String login){
        
        String cadena = "";
        
        friend_controller.getFriendsOf(this.usuarios, login);

        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#SERVER#LOGIN_CORRECT#" + login + "#FRIENDS";
        
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
    
    
    public String getFriend(String theInput){
        String friend = "";
        
       String[] datos = theInput.split("#");
       friend = datos[5];
       
       return friend;
    }
    
    public int contadorPaquetes(String theInput){
       int contadorPaquetes = 0;
       String[] datos = theInput.split("#");
       
       for(String paquete : datos){
           contadorPaquetes++;
       }
       
       return contadorPaquetes;
    }
    
    public String startingMultimedia(){
        String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#STARTING_MULTIMEDIA_TRANSMISSION_TO#" + this.getLogin();
        return cadena;
    }
    
    public String endingMultimedia(){
        String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#ENDING_MULTIMEDIA_TRANSMISSION#" + this.getLogin();
        return cadena;
    }
    
    public String sendReceivedMessage(String theInput){
        String[] datos = theInput.split("#");
        String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#CHAT#" + datos[5] + "#" + this.getLogin() + "#MESSAGE_SUCCESFULLY_PROCESSED#" + sdf.format(timestamp);
        return cadena;
    }
    
    public String sendMessage(String theInput){
        String[] datos = theInput.split("#");  
        String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#SERVER#CHAT#" + datos[4] + "#" + datos[5] + "#" + datos[6] + "#";
        return cadena;
        
    }
}
