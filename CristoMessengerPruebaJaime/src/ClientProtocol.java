
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

    
    private int state = LOGGING;
    
    private String login;
    private String passwd;
    
    CristoMessenger myCristoMessenger;
    ArrayList<Message> msjs = new ArrayList();


    
    ClientProtocol(String login, String pass){
        cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
        this.login = login;
        this.passwd = pass;
        myCristoMessenger = new CristoMessenger();
        msjs = new ArrayList();
    }
    
    public String processInput(String theInput){
        String theOutput = null;
        
        
        if(state == LOGGING){
            theOutput = "PROTOCOLCRISTOMESSENGER1.0#CLIENT#LOGIN#" + login + "#" + passwd;
            state = LOGGED;
            
        } else if (state == LOGGED){
            
            System.out.println("entro");

            if(theInput.startsWith(cadenaPrincipal)){
                if(theInput.contains("LOGIN_CORRECT")){
                    leerAmigos(theInput);
                    theOutput = "PROTOCOLCRISTOMESSENGER1.0#MESSAGES";
                }
                
                if(theInput.contains("BAD_LOGIN")){
                    theOutput = null;
                }
                
                if(theInput.contains("MESSAGES")){
                    this.leerMsjs(theInput);
                    
                    myCristoMessenger.setActualUser(login);
                    myCristoMessenger.setVisible(true);
                    
                    state = ALREADY;
                    
                }
                
                
            }
        } else if(state == ALREADY){
            
        }
        
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
            
            
            myCristoMessenger.setFriendsOf(names);
            
    }
    
    public void leerMsjs(String fromServer){
        
        
        int contadorE = 0;
        String logOr = "";
        String logDe = "";
        String text = "";
        
       
        for(int i = 34; i < fromServer.length(); i++){
            
            if(fromServer.charAt(i) == '#'){
                
                if(contadorE <= 3){
                    contadorE++;
                    
                }
                
                if(contadorE == 3){
                    contadorE = 0;
                }
                
                if(contadorE == 1 && text != ""){
                    msjs.add(new Message());
                    msjs.get(msjs.size() - 1).setId_user_orig(logOr.substring(1, logOr.length()));
                    msjs.get(msjs.size() - 1).setId_user_dest(logDe.substring(1, logDe.length())); 
                    msjs.get(msjs.size() - 1).setText(text.substring(1, text.length()));
                    
                    text = "";          
                    logDe = "";
                    logOr = "";
                    
                }
                
            }
            
            if(contadorE == 1){
                logOr += fromServer.charAt(i);
                System.out.println(logOr);
            }
            
            if(contadorE == 2){
                logDe += fromServer.charAt(i);
                 System.out.println(logDe);

            }
            
            if(contadorE == 0){
                text += fromServer.charAt(i);
                System.out.println(text);

            }
            
            if(i == fromServer.length() - 1){
                msjs.add(new Message());
                msjs.get(msjs.size() - 1).setId_user_orig(logOr.substring(1, logOr.length()));
                msjs.get(msjs.size() - 1).setId_user_dest(logDe.substring(1, logDe.length())); 
                msjs.get(msjs.size() - 1).setText(text.substring(1, text.length()));
                    
            }
            
            
        }
        
        
        System.out.println("Los  mostremos enteros");
        
        for(int i = 0; i < msjs.size(); i++){
            System.out.println(msjs.get(i).getId_user_orig() + " " + msjs.get(i).getId_user_dest() + msjs.get(i).getText());
        }
        
        this.myCristoMessenger.setMessages(msjs);
    }

}
