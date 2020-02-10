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

import Vista.CristoMessenger;
import java.io.*;
import java.net.*;
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
    
    Integer numeroMsgs = 0;
    
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
        } else {
            try {
                kkSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    
    public void getFriendStatus() throws IOException{
        String output = protocol.getFriendStatus();
        out.println(output);
        String fromServer = in.readLine();
        protocol.processInput(fromServer);
    }
    
    public void getMessagesFrom() throws IOException{
       
        String output =  protocol.getMessages();
        out.println(output);
        String fromServer = in.readLine();
        output = protocol.processInput(fromServer);
        this.numeroMsgs = protocol.getNumeroDeMensajes();
        System.out.println("numero de mgsg " + this.numeroMsgs);
        if(numeroMsgs != 0){
            int i = 0;
            out.println(output);
            System.out.println("output dentro del if + " + output);
            
            for(int j = 0; j< this.numeroMsgs; j++){
                 fromServer = in.readLine();
                 System.out.println("FROMSERVER DENTRO WHILE " + fromServer);
                 protocol.processInput(fromServer);
                 //out.println(output);
            }
            
        }
        
        
        String theOutput = "PROTOCOLCRISTOMESSENGER1.0#" + "#CLIENT#ALL_RECEIVED!";
        out.println(theOutput);
       
       //fromServer = in.readLine();
       
    }
     
}
