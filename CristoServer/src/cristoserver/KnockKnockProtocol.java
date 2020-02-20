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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;

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
    
    File file;
    FileInputStream fin = null;
   
    int separador = 0;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());


    
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
            
            if(theInput.contains("MSGS") && this.contadorPaquetes(theInput) == 7){
                this.messages.clear();
                theOutput = getTotalMsgs(theInput);                    
                //theOutput = "PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#BAD_MSGPKG";
            }
             
            if(theInput.contains("#CHAT") && this.contadorPaquetes(theInput) == 7){
                 theOutput = this.receiveMessage(theInput);
            }
 
            if(theInput.contains("STATUS") && this.contadorPaquetes(theInput) == 6){
                theOutput = this.getUserState(theInput); 
                //theOutput = "PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#BAD_PKG";
            }
            
            if(theInput.contains("ALLDATA_USER") && this.contadorPaquetes(theInput) == 5){
                theOutput = this.getAllDataUser(theInput);   
                //theOutput = "PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#BAD_PKG";
            }
            
            
            
        } else {
            CristoServer.debug("MENSAJE INVALIDO");  
        }

        CristoServer.debug(theOutput);
        return theOutput;
    }
    
    public void loadFile(String theInput) throws FileNotFoundException{
        file = new File("data/Alejandro_Muñoz/Alejandro_Muñoz.jpg");
        fin = new FileInputStream(file);
        
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
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#RESPONSE_MULTIMEDIA#<LOGIN_CLIENTE>#<TOTAL_BYTES_MULTIMEDIA>#<SIZE_PACKET_MULTIMEDIA>#@512BYTES_FOTO
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
    
    
    public String receiveMessage(String theInput) throws SQLException{
        String cadena = "ksfjhsdl";
        String[] datos = theInput.split("#");
        
        Boolean existeUser1 = user_controller.findUser(datos[4]);
        Boolean existeUser2 = user_controller.findUser(datos[5]); 
        Boolean areFriends = friend_controller.getRelation(datos[4], datos[5]);
        
        System.out.println("Existe 1 --> " + existeUser1);
        System.out.println("Existe 2 --> " + existeUser2);
        System.out.println("Son amigos --> " + areFriends);



       if(existeUser1 && existeUser2){
            message_controller.insertMessage(datos[4], datos[5], datos[6]);
            cadena = "Bien";
       } else {
            cadena = "PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#FORBIDDEN_CHAT";
       }
        
        return cadena;
    }
    
    
    public String getUserState(String theInput){
        
        String[] datos = theInput.split("#");
        
        String cadena = cadenaPrincipal + "#" + dateTime + "#SERVER#STATUS#" + datos[5];
        
        String status = user_controller.getUserState(datos[5]);
        
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
}
