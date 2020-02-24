package Controladores;


import Classes.User;
import Classes.Message;
import Vista.CristoMessenger;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
public class ClientProtocol {
    
    private final String cadenaPrincipal;
    private final int LOGGING = 0;
    private final int LOGGED = 1;
    
    public int state = LOGGING;
    
    private String login;
    private String passwd;
        
    CristoMessenger myCristoMessenger;
    ArrayList<Message> msjs = new ArrayList();
    ArrayList<User> friendList = new ArrayList();
    
    LocalDateTime dateTime;
    
    int numeroMensajes;
    int totalNumeroMensajes;
    
    public int restar = 1;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

     
    ClientProtocol(String login, String pass, CristoMessenger a){
        cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
        this.login = login;
        this.passwd = pass;
        myCristoMessenger = a;
        msjs = new ArrayList();
        dateTime = LocalDateTime.now();
        numeroMensajes = 0;
        totalNumeroMensajes = 0;
    }
    
    public String processInput(String theInput){
        String theOutput = null;
          
        
        System.out.println("FROM SERVER --> " + theInput);
        
        if(state == LOGGING){
            theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#LOGIN#" + login + "#" + passwd;
            state = LOGGED;
            
        } else if (state == LOGGED){
            
            if(theInput.startsWith(cadenaPrincipal)){
                if(theInput.contains("LOGIN_CORRECT")){
                    leerAmigos(theInput);                    
                }
                
                if(theInput.contains("BAD_LOGIN")){
                    theOutput = null;
                }
                
                if(theInput.contains("#MSGS#")){
                    this.msjs.clear();
                    this.leerNumeroMensajes(theInput);
                    theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#MSGS#OK_SEND!";
                }

                if(theInput.contains("STATUS")){
                    String status = this.friendStatus(theInput);
                    this.myCristoMessenger.setFriendStatus(status);
                }
                
                if(theInput.contains("ALLDATA_USER")){
                    String nombre = this.userData(theInput);
                    this.myCristoMessenger.setFriendData(nombre);
                }
            }
        }
        
        return theOutput;
    }
    
    public String msgAllReceived(){
        return cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#ALL_RECEIVED";
    }
    
    public String getPhoto(){
        String cadena = "";
        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#GET_PHOTO#" + this.login;
        return cadena;
    }
    
    public String getFriendPhoto(){
        String cadena = "";
        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#GET_PHOTO#" + this.myCristoMessenger.getFocusFriend();
        return cadena;
    }
     
    public String userData(String theInput){
        String cadena = ""; 
        String[] datos = theInput.split("#");
        cadena = datos[5] + " " + datos[6] + " " + datos[7];
        
        ArrayList<User> friends = this.myCristoMessenger.getFriends();
        for(int i = 0; i < friends.size(); i++){
            if(datos[4].equals(friends.get(i).getLogin())){
                if(friends.get(i).getEstadoUsuario() == false){
                    cadena += " " + "NOT_CONNECTED";
                } else {
                    cadena += " " + "CONNECTED";
                }
            }
        }
        
        return cadena;
    }
    
    public String getFriendData(){
        String cadena = "";
        cadena += cadenaPrincipal + "#" + dateTime + "#CLIENT#ALLDATA_USER#" + this.myCristoMessenger.getFocusFriend();
        return cadena;
    } 
    
    public String sendMessage(String text){
        String cadena = "";
        cadena += cadenaPrincipal + "#" + this.sdf.format(timestamp) + "#" + "CLIENT#CHAT#" + login + "#" + this.myCristoMessenger.getFocusFriend() + "#" + text;
        return cadena;
    }
    
    public String friendStatus(String theInput){
        String cadena = "";
        
        String[] cadenas = theInput.split("#");
        int contadorAtt = 0;
        
        for(String att : cadenas){
            if(contadorAtt == 5){
                cadena = att;
            }
            
            contadorAtt++;
        }
        
        return cadena;
    }
    
    public String getFriendStatus(){
        String cadena  = ""; 
        cadena = cadenaPrincipal + "#" + sdf.format(timestamp) + "#CLIENT#STATUS#" + login + "#";
        return cadena;
    }
    
    public void leerNumeroMensajes(String fromServer){         
        String[] msgs = fromServer.split("#");
        int contadorStt = 0;
        for(String stt : msgs){
            
            if(contadorStt == 6){
                this.totalNumeroMensajes = Integer.parseInt(stt);
            }
            
            if(contadorStt == 7){
                this.numeroMensajes = Integer.parseInt(stt);
            }
            contadorStt++;
        }  
    }
    
    public int getNumeroDeMensajes(){  
        return this.numeroMensajes;
    }
    
    public int getTotalNumeroDeMensajes(){  
        return this.totalNumeroMensajes;
    }
    
    public String getMessages(){
        this.msjs.clear();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis() - 24 * restar * 60 * 60 * 1000L);
        String theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT" + "#MSGS#" + login + "#" + myCristoMessenger.getFocusFriend() + "#" + sdf.format(timestamp);
        restar++;
        return theOutput;
    }
    
    public void leerAmigos(String fromServer){       
        String[] datos = fromServer.split("#");
        
        int contador = 0;
        boolean nombre = true;
        
        String loginF = "";
        
        for(String s : datos){
            if(contador > 6){
                
                if(nombre){
                    
                    loginF = s;
                    nombre = false;
                    
                } else {
                    
                    User aux = new User();
                    aux.setLogin(loginF);
                    if(s.equals("CONNECTED")){
                       aux.setEstadoUsuario(1);
                    } else {
                       aux.setEstadoUsuario(0);
                    }
                    
                    friendList.add(aux);
                    
                    nombre = true;
                }
                
            }
            contador++;
        }
            
        myCristoMessenger.setFriendsOf(friendList);
            
    }
    
    public void leerMsgs(String fromServer) throws IOException{
        
        if(fromServer.startsWith(cadenaPrincipal)){
            
            int contadorStt = 1;
            String logOr = "";
            String logDest = "";
            String dateHour = "";
            String text = "";
            String msgsFiltrado = "";
            String[] msgs = null;

            msgsFiltrado = fromServer.substring(fromServer.indexOf("MSGS") + 5, fromServer.length());
            msgs = msgsFiltrado.split("#");

            for(String att : msgs){
                if(contadorStt == 1){
                    logOr = att;
                }

                if(contadorStt == 2){
                    logDest = att;
                }

                if(contadorStt == 3){
                    dateHour = att;
                }

                if(contadorStt == 4){
                    text = att;
                }
                

                contadorStt++;
            }

            Message m = new Message();
            m.setId_user_orig(logOr);
            m.setId_user_dest(logDest);
            m.setDate(dateHour);
            m.setText(text);
            m.setRead(1);
            m.setSent(1);
            
            this.msjs.add(m);
            this.myCristoMessenger.setMessages(msjs);
        }
    }
    
    public String addNewMsg(String fromServer) throws IOException{
        String cadena = "";
        String[] datos = fromServer.split("#");
        
        if(this.myCristoMessenger.getFocusFriend().equals(datos[4])){
           Message m = new Message();
        
            m.setId_user_orig(datos[4]);
            m.setId_user_dest(datos[5]);
            m.setDate(datos[7]);
            m.setText(datos[6]);
            m.setRead(1);
            m.setSent(1);

            this.msjs.add(m);
            this.myCristoMessenger.setMessages(msjs); 
        }
        
        cadena = "PROTOCOLCRISTOMESSENGER1.0#" + sdf.format(timestamp) + "#CLIENT#CHAT#RECEIVED_MESSAGE#" + datos[4] + "#" + sdf.format(timestamp);
        
        return cadena;
        
    }  
    
}
