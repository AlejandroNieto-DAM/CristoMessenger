package Controladores;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */

import Classes.RefrescarListaAmigos;
import Vista.CristoMessenger;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
 
public class KnockKnockClient extends Thread{
    
    int portNumber;
    String hostName;
    String login;
    String pass;
    JFrame loginFrame;
    ClientProtocol protocol;
    CristoMessenger a;
    PrintWriter out;
    Socket kkSocket;
    BufferedReader in;
    
    LocalDateTime dateTime = LocalDateTime.now();
    
    RefrescarListaAmigos friendRefresh;
    
    Integer numeroMsgs = 0;
    int totalNumeroMensajes = 0;
    
    public KnockKnockClient(int port, String host, String login, String pass, JFrame frame) throws IOException{
        this.portNumber = port;
        this.hostName = host;
        this.login = login;
        this.pass = pass;
        this.loginFrame = frame;
        a = new CristoMessenger(this);
        protocol = new ClientProtocol(login, pass, a);
        kkSocket = new Socket(hostName, portNumber);
        out = new PrintWriter(kkSocket.getOutputStream(), true);
        in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        
        friendRefresh = new RefrescarListaAmigos(this);

    }
    
    
    @Override
    public void run(){
 
        String fromServer = "";
        String fromUser = "";

        fromUser = protocol.processInput(null);

        out.println(fromUser);
        try {
            fromServer = in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(fromServer.contains("LOGIN_CORRECT")){
                protocol.processInput(fromServer);
                this.loginFrame.setVisible(false);
                this.a.setActualUser(login);
                this.a.setVisible(true);     
            
                this.friendRefresh.start();
        } else {
            try {
                kkSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    public void processMsg(String theInput){
        //TODO 
        protocol.processInput(theInput);
    }
    
    public void refreshFriends() throws IOException{
        String[] friends = this.a.getFriends();
        int contadorF = 0;
        
        for(String friend : friends){
            contadorF++;
        }
        
        String[] refreshFriends = new String[contadorF];
        int contador2 = 0;
        for(String f : friends){
            String loginF  = f.substring(0, f.indexOf(" "));                            //TODO FALTA UN GET LOGIN
            String cadena = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#STATUS#" + login + "#" + loginF;
            out.println(cadena);
            String estado = this.protocol.friendStatus(in.readLine());
            refreshFriends[contador2] = loginF + " " + estado;
            contador2++;
        }
        
        this.a.setFriendsOf(refreshFriends);
    }
    
    
    public void sendMessage(String text) throws IOException{
        String output = protocol.sendMessage(text);
        out.println(output);
        String fromServer = in.readLine();
        //System.out.println("");
    }
      
    public void getFriendStatus() throws IOException{
        String output = protocol.getFriendStatus();
        out.println(output);
        String fromServer = in.readLine();
        System.out.println("PERO MIRA QUE ESTADOOOOO " + fromServer);
        protocol.processInput(fromServer);
    }
    
    public void getFriendData() throws IOException{
        String output = protocol.getFriendData();
        out.println(output);
        String fromServer = in.readLine();
        protocol.processInput(fromServer);
    }
    
    public void getMessagesFrom() throws IOException{
        
        this.numeroMsgs = 0;
        this.totalNumeroMensajes = 0;

        String output =  protocol.getMessages();
        out.println(output);
        String fromServer = in.readLine();
        
        System.out.println("ILLO QUE PASA MIRA QUE CADENA RECIBO " + fromServer);
        output = protocol.processInput(fromServer);
        this.numeroMsgs = protocol.getNumeroDeMensajes();
        this.totalNumeroMensajes = protocol.getTotalNumeroDeMensajes();
        System.out.println("numero de mgsg " + this.numeroMsgs);
        
        if(totalNumeroMensajes != 0){ 
            
            do{
                output =  protocol.getMessages();
                out.println(output);
                fromServer = in.readLine();
                System.out.println("From server --> " + fromServer);
                output = protocol.processInput(fromServer);
                this.numeroMsgs = protocol.getNumeroDeMensajes();
            }while(numeroMsgs == 0);
            
            
            out.println(output);
            System.out.println("output dentro del if + " + output);

            for(int i = 0; i < this.numeroMsgs; i++){
                 fromServer = in.readLine();
                 System.out.println("FROMSERVER DENTRO WHILE " + fromServer);
                 protocol.leerMsgs(fromServer);
            }

            String theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + dateTime + "#CLIENT#ALL_RECEIVED";
            out.println(theOutput);
            System.out.println("yeyo en mi ihpen");
                    
        }
        
        this.numeroMsgs = 0;
        this.totalNumeroMensajes = 0;
             
    }
     
}
