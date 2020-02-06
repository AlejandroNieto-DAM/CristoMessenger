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
                fromUser = protocol.processInput(fromServer);
                out.println(fromUser);
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

        try {
            fromServer = in.readLine();
        } catch (IOException ex) {
            Logger.getLogger(KnockKnockClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        fromUser = protocol.processInput(fromServer);
            
            
            
           
            
        
    }
    
    public void getMessagesFrom() throws IOException{
       String output =  protocol.getMensajeMensajes();
       out.println(output);
       String fromServer = in.readLine();
       protocol.processInput(fromServer);
       
    }
     
}
