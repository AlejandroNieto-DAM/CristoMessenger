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
import javax.swing.JFrame;
 
public class KnockKnockClient extends Thread{
    
    int portNumber;
    String hostName;
    String login;
    String pass;
    JFrame loginFrame;
    ClientProtocol protocol;
   CristoMessenger a;
    
    KnockKnockClient(int port, String host, String login, String pass, JFrame frame){
        this.portNumber = port;
        this.hostName = host;
        this.login = login;
        this.pass = pass;
        this.loginFrame = frame;
        a = new CristoMessenger();
        protocol = new ClientProtocol(login, pass, a);

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
            String prueba;
            fromUser = protocol.processInput(null);
            out.println(fromUser);

            while ((fromServer = in.readLine()) != null) {
                
                fromUser = protocol.processInput(fromServer);
                
                out.println(fromUser);
                
                /*try{
                    prueba = a.getFocusFriend(); 
                    
                    
                    if(prueba != null){
                       protocol.getMsgs();
                       System.out.println("focus name " + prueba);
                    }
                } catch(NullPointerException e){
                    
                }*/
               
                
                
                if(fromServer.contains("LOGIN_CORRECT")){
                    this.loginFrame.setVisible(false);
                    
                }
                
                if (fromUser == null){
                    break;
                }
                    
                
                
            }  
            
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
     
}
