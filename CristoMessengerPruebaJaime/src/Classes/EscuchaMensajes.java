/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Controladores.KnockKnockClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alejandronieto
 */
public class EscuchaMensajes extends Thread {
    
    KnockKnockClient myKKClient;
    BufferedReader in;
    
    public EscuchaMensajes(KnockKnockClient myKKClient, BufferedReader in){
        this.myKKClient = myKKClient;
        this.in = in;
        
    }
    
    @Override
    public void run(){
        try {
            while(in.readLine() != null){
                String fromServer = in.readLine();
                if(fromServer.startsWith("PROTOCOLCRISTOMESSENGER1.0") && fromServer.contains("CHAT")){
                    this.myKKClient.processMsg(fromServer);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(EscuchaMensajes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
