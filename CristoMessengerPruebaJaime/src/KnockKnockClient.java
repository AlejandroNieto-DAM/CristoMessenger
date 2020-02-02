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
 
public class KnockKnockClient {
    
    int portNumber;
    String hostName;
    CristoMessenger myCristoMessenger;
    String login;
    
    KnockKnockClient(int port, String host){
        this.portNumber = port;
        this.hostName = host;
    }
    
    
    public void connect(String login, String pass, JFrame willy) throws IOException {
         
        this.login = login;
        
        try (
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        ) {
            
            String fromServer;
            String fromUser;

            fromUser = "PROTOCOLCRISTOMESSENGER1.0#CLIENT#LOGIN#" + login + "#" + pass;
            out.println(fromUser);
            
            fromServer = in.readLine();
            
            System.out.println(fromServer);
            
            
            if(fromServer.contains("PROTOCOLCRISTOMESSENGER1.0")){
                if(!fromServer.contains("ERROR")){
                    leerAmigos(fromServer);
                } else {
                    System.out.println("BAD LOGIN");
                    kkSocket.close();
                }
            }
            

            
            
           /* System.out.println("Hemos pasao la ventana");
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
                
   
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

                
                
                if(amigos.charAt(i) == '#' || i == amigos.length() - 1){
                    
                    if(contadorA <= 3){
                       contadorA++;
                    } 

                    if(contadorA == 3){
                      names[contadorFriends] = nomAmigo.substring(1, nomAmigo.length());
                      nomAmigo = "";  
                      contadorFriends++;
                      contadorA = 0;
                      primero = true;
                    } 
                    
                    if(contadorA == 1 && primero == true){
                        names[contadorStado] = names[contadorStado] + " " + conectado;
                        System.out.println("names[] --> " + conectado);
                        contadorStado++;
                        conectado = "";
                    }
                    
                    
   
                } 
                
                if(contadorA == 0){
                    conectado += amigos.charAt(i);
                    System.out.println(conectado);
                }
                

                if(contadorA == 2){
                    nomAmigo += amigos.charAt(i);
                }
                
            }
            
            
            myCristoMessenger = new CristoMessenger();
            myCristoMessenger.setFriendsOf(names);
            myCristoMessenger.setActualUser(login);
            myCristoMessenger.setVisible(true);
    }
}
