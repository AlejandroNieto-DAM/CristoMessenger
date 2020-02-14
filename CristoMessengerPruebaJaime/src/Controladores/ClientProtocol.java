package Controladores;


import Classes.Message;
import Vista.CristoMessenger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

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
    int totalNumeroMensajes;
    
    int restar = 1;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     
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
        
        /*if(theInput != null){
            System.out.println(theInput);
        }*/
        
        System.out.println("FROM SERVER --> " + theInput);
        
        if(state == LOGGING){
            theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#LOGIN#" + login + "#" + passwd;
            state = LOGGED;
            
        } else if (state == LOGGED){
            
            //System.out.println("entro");

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

                if(theInput.contains("STATUS")){
                    String status = this.friendStatus(theInput);
                    this.myCristoMessenger.setFriendStatus(status);
                }
                
                if(theInput.contains("ALLDATA_USER")){
                    String nombre = this.userData(theInput);
                    this.myCristoMessenger.setFriendData(nombre);
                }
                
                if(theInput.contains("CHAT")){
                    //String nombre = this.userData(theInput);
                    //this.myCristoMessenger.setFriendData(nombre);
                }
   
            }
        }
        
        
        return theOutput;
    }
    
    
    public String userData(String theInput){
        String cadena = "";
        
        String[] datos = theInput.split("#");
        cadena = datos[5] + " " + datos[6] + " " + datos[7];
        //System.out.println("pero mira que datos mas frescos " + cadena);
        return cadena;
    }
    
    public String getFriendData(){
        String cadena = "";
        
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#CLIENT#ALLDATA_USER#<LOGIN>

        cadena += cadenaPrincipal + "#" + dateTime + "#CLIENT#ALLDATA_USER#" + this.myCristoMessenger.getFocusFriend();
        
        return cadena;
    }
    
    
    public String sendMessage(String text){
        String cadena = "";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#CLIENT#CHAT#<LOGIN_ORIG#<LOGIN_DEST>#<MESSAGE>
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
        //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#CLIENT#STATUS#<LOGIN_CLIENT#<LOGIN_AMIGO>
        cadena = cadenaPrincipal + "#" + dateTime + "#CLIENT#STATUS#" + login + "#" + this.myCristoMessenger.getFocusFriend();
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
        
       //System.out.println("Numero de mensajes " + this.numeroMensajes);
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



            this.myCristoMessenger.setMessages(msjs);
            //PROTOCOLCRISTOMESSENGER1.0#FECHA/HORA#SERVER#MSG#<MESSAGE_X>
            //    <MESSAGE_X> = <EMISOR>#<RECEPTOR>#<FECHA>#<TEXTO>
        }
    }

}
