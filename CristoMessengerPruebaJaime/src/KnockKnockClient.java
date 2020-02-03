/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.JFrame;
 
public class KnockKnockClient extends Thread{
    
    int portNumber;
    String hostName;
    CristoMessenger myCristoMessenger;
    String login;
    String pass;
    JFrame loginFrame;
    ArrayList<Message> msjs = new ArrayList();
    ClientProtocol protocol;
    
    KnockKnockClient(int port, String host, String login, String pass, JFrame frame){
        this.portNumber = port;
        this.hostName = host;
        this.login = login;
        this.pass = pass;
        this.loginFrame = frame;
        myCristoMessenger = new CristoMessenger();
        protocol = new ClientProtocol(login, pass);

    }
    
    
    @Override
    public void run(){
                 
        try (
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        ) {
            
            String fromServer;
            String fromUser;
                        
            fromUser = protocol.processInput(null);
            out.println(fromUser);

            while ((fromServer = in.readLine()) != null) {
                
                System.out.println("fromUser " + fromServer);

                fromUser = protocol.processInput(fromServer);
                
                out.println(fromUser);
                
                if(fromServer.contains("MESSAGES")){
                    this.loginFrame.setVisible(false);
                }
                
                if (fromUser == null){
                    
                }
                    //break;
                
                
            }
            
            
           /* if(fromServer.contains("PROTOCOLCRISTOMESSENGER1.0")){
                if(!fromServer.contains("ERROR")){
                    leerAmigos(fromServer);
                    
                    fromUser = "PROTOCOLCRISTOMESSENGER1.0#MESSAGES";
                    out.println(fromUser);


                    fromServer = in.readLine();

                    System.out.println(fromServer);

                    this.leerMsjs(fromServer);

                    myCristoMessenger.setActualUser(login);
                    myCristoMessenger.setVisible(true);
                    this.loginFrame.setVisible(false);
                    
                    
                    
                } else {
                    System.out.println("BAD_LOGIN");
                    kkSocket.close();
                }
            }*/
            
            
           
            
            
            
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
    
    public void leerMsjs(String fromServer){
        
        
        int contadorE = 0;
        String logOr = "";
        String logDe = "";
        String text = "";
        
       
        for(int i = 0; i < fromServer.length(); i++){
            
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
}
