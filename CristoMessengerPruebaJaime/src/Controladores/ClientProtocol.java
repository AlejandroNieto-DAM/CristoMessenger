package Controladores;


import Classes.Message;
import Vista.CristoMessenger;
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
    
    private String cadenaPrincipal;
    private final int LOGGING = 0;
    private final int LOGGED = 1;
    private final int ALREADY = 2;

    
    public int state = LOGGING;
    
    private String login;
    private String passwd;
    
    private String firstFriend;
    
    CristoMessenger myCristoMessenger;
    ArrayList<Message> msjs = new ArrayList();
    
    LocalDateTime dateTime;
    
    int numeroMensajes;
    
    


    
    ClientProtocol(String login, String pass, CristoMessenger a){
        cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
        this.login = login;
        this.passwd = pass;
        myCristoMessenger = a;
        msjs = new ArrayList();
        dateTime = LocalDateTime.now();
        numeroMensajes = 0;
    }
    
    public String processInput(String theInput){
        String theOutput = null;
        
        if(theInput != null){
            System.out.println(theInput);
        }
        
        
        if(state == LOGGING){
            theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#LOGIN#" + login + "#" + passwd;
            state = LOGGED;
            
        } else if (state == LOGGED){
            
            System.out.println("entro");

            if(theInput.startsWith(cadenaPrincipal)){
                if(theInput.contains("LOGIN_CORRECT")){
                    leerAmigos(theInput);
                    //theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT" + "#MSGS#" + login + "#" + firstFriend; 
                    
                }
                
                if(theInput.contains("BAD_LOGIN")){
                    theOutput = null;
                }
                
                if(theInput.contains("#MSGS#")){
                    this.msjs.clear();
                    this.leerNumeroMensajes(theInput);
                    theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#MSGS#OK_SEND!";

                }
                
                if(theInput.contains("#MSG#")){
                    this.leerMsgs(theInput);    
                    theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + "#CLIENT#ALL_RECEIVED!";
                    this.myCristoMessenger.setMessages(msjs);
                }
                
                if(theInput.contains("STATUS")){
                    String status = this.friendStatus(theInput);
                    this.myCristoMessenger.setFriendStatus(status);
                }
   
            }
        }
        
        
        return theOutput;
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
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#CLIENT#STATUS#<LOGIN_CLIENT#<LOGIN_AMIGO>
        cadena = cadenaPrincipal + dateTime + "#CLIENT#STATUS#" + login + "#" + this.myCristoMessenger.getFocusFriend();
        return cadena;
    }
    
    
    public void leerNumeroMensajes(String fromServer){
        
        String[] msgs = fromServer.split("#");
        int contadorStt = 0;
        for(String stt : msgs){
            
            if(contadorStt == 6){
                this.numeroMensajes = Integer.parseInt(stt);
            }      
            contadorStt++;
        }
        
        System.out.println("Numero de mensajes " + this.numeroMensajes);
    }
    
    
    public int getNumeroDeMensajes(){  
        return this.numeroMensajes;
    }
    
    public String getMensajeMensajes(){
        this.msjs.clear();
        this.numeroMensajes = 0;
        String theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT" + "#MSGS#" + login + "#" + myCristoMessenger.getFocusFriend(); 
        return theOutput;
    }
    
    public void leerAmigos(String fromServer){
        
        
        int numeroAmigos = 0;
            
            String numAmigos = "";
            String amigos = "";
            
            int contador1  = 0;
            for(int i = 0; i < fromServer.length(); i++){
                if(fromServer.charAt(i) == '#'){
                    contador1++;
                }
                
                if(contador1 == 6){
                    numAmigos += fromServer.charAt(i);
                }
                
                if(contador1 >= 7){
                    amigos += fromServer.charAt(i);
                }
            }
            
            numAmigos = numAmigos.substring(1, numAmigos.length());
                        
            numeroAmigos = Integer.parseInt(numAmigos);
            
            
            int contadorA = 0;
            String nomAmigo = "";
            String conectado = "";
             
            String[] names = new String[numeroAmigos];
            
            
            int contadorFriends = 0;
            int contadorStado = 0;
            boolean primero = false;
            

            for(int i = 0; i < amigos.length(); i++){
                
                if(amigos.charAt(i) == '#'){
                    
                    if(contadorA <= 2){
                       contadorA++;
                    } 

                    if(contadorA == 2){
                      names[contadorFriends] = nomAmigo.substring(1, nomAmigo.length());
                      nomAmigo = "";  
                      contadorFriends++;
                      contadorA = 0;
                      primero = true;
                    } 
                    
                    if(contadorA == 1 && primero == true){
                        names[contadorStado] = names[contadorStado] + " " + conectado.substring(1, conectado.length());
                        contadorStado++;
                        conectado = "";
                    }    
   
                } 
                
                if(contadorA == 0){
                    conectado += amigos.charAt(i);
                    
                }
                
                
                if(contadorA == 1){
                    nomAmigo += amigos.charAt(i);
                }
                
                if( i == amigos.length() - 1){
                    names[contadorStado] = names[contadorStado] + " " + conectado.substring(1, conectado.length());
                }
                
            }
            
            firstFriend = names[0].substring(0, names[0].indexOf(" "));
            
            
            myCristoMessenger.setFriendsOf(names);
            
    }
    
    
    
    public void leerMsgs(String fromServer){
        
        
        
        int contadorStt = 1;
        String logOr = "";
        String logDest = "";
        String dateHour = "";
        String text = "";
        String msgsFiltrado = "";
        String[] msgs = null;
        
        msgsFiltrado = fromServer.substring(fromServer.indexOf("MSG") + 4, fromServer.length());
        msgs = msgsFiltrado.split("#");
        System.out.println("MSGS RECORTAO + " + msgs);
        
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
        //m.setDate(dateHour.substring(0, dateHour.indexOf(".")));
        //m.setHour(dateHour.substring(dateHour.indexOf("."), dateHour.length()));
        m.setText(text);
        this.msjs.add(m);
        
        
        
            
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#MSG#<MESSAGE_X>
        //    <MESSAGE_X> = <EMISOR>#<RECEPTOR>#<FECHA>#<TEXTO>    
    }

}
