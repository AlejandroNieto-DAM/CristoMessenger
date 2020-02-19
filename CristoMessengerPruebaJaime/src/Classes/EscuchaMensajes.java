/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Controladores.KnockKnockClient;
import Vista.CristoMessenger;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alejandronieto
 */
public class EscuchaMensajes extends Thread{
    
    BufferedReader myKKClient;
    
    public EscuchaMensajes(BufferedReader myKKClient) throws IOException{
        this.myKKClient = myKKClient;  
    }
    
    @Override
    public void run(){
        String inputLine = "";
        try {
            while((inputLine = myKKClient.readLine()) != null){

                System.out.println("En la hebra eh" + inputLine);

                CristoMessenger.jTextAreaDebugWindow.setText(CristoMessenger.jTextAreaDebugWindow.getText() + "\nyeyo");
            }  
        } catch (IOException ex) {
            Logger.getLogger(EscuchaMensajes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
