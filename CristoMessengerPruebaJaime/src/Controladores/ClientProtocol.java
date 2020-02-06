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


    
    ClientProtocol(String login, String pass, CristoMessenger a){
        cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
        this.login = login;
        this.passwd = pass;
        myCristoMessenger = a;
        msjs = new ArrayList();
        dateTime = LocalDateTime.now();
    }
    
    public String processInput(String theInput){
        String theOutput = null;
        
        if(theInput != null){
            System.out.println(theInput);
        }
        
        
        if(state == LOGGING){
            theOutput = "PROTOCOLCRISTOMESSENGER1.0#CLIENT#LOGIN#" + login + "#" + passwd;
            state = LOGGED;
            
        } else if (state == LOGGED){
            
            System.out.println("entro");

            if(theInput.startsWith(cadenaPrincipal)){
                if(theInput.contains("LOGIN_CORRECT")){
                    leerAmigos(theInput);
                    theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#MSGS#" + login + "#" + firstFriend; 
                    
                }
                
                if(theInput.contains("BAD_LOGIN")){
                    theOutput = null;
                }
                
                if(theInput.contains("MSGS")){
                    this.leerMsgs(theInput);
                }
   
            }
        }
        
        return theOutput;
    }
    
    public String getMensajeMensajes(){
        String theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#MSGS#" + login + "#" + myCristoMessenger.getFocusFriend(); 
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
        
        this.msjs.clear();
        
        int contadorStt = 1;
        String logOr = "";
        String logDest = "";
        String dateHour = "";
        String text = "";
        String msgsFiltrado = "";
        String[] msgs = null;
        
        
        try{
            
            msgsFiltrado = fromServer.substring(fromServer.indexOf("LIST") + 5, fromServer.indexOf("END") - 1);
            msgs = msgsFiltrado.split("#");
            
            for(String stt : msgs){

                if(contadorStt == 1){
                    logOr = stt;
                }


                if(contadorStt == 2){
                    dateHour = stt;
                }


                if(contadorStt == 3){
                    text = stt;
                    Message e = new Message();
                    e.setId_user_orig(logOr);
                    e.setId_user_dest(logDest);
                    e.setText(text);
                    msjs.add(e);

                    logOr = "";
                    logDest = "";
                    dateHour = "";
                    text = "";

                    contadorStt = 0;
                }

                contadorStt++;
            }

        } catch (StringIndexOutOfBoundsException e){
            
        }
        
        this.myCristoMessenger.setMessages(msjs);
        
        
        
        
        
        
        
    }

}
