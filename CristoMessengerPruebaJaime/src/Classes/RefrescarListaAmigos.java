/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Controladores.KnockKnockClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alejandronieto
 */
public class RefrescarListaAmigos extends Thread {
    
    KnockKnockClient myKKClient;
    long inicio = System.currentTimeMillis();
    long fin = System.currentTimeMillis();
    
 
    public RefrescarListaAmigos(KnockKnockClient myKKClient){
        this.myKKClient = myKKClient;    
    }
    
    @Override
    public void run(){
        while(true){

            if((fin - inicio) / 1000 > 5){
                
                try {
                    this.myKKClient.refreshFriends();
                } catch (IOException ex) {
                    Logger.getLogger(RefrescarListaAmigos.class.getName()).log(Level.SEVERE, null, ex);
                }
                inicio = System.currentTimeMillis();
            }
            
            fin = System.currentTimeMillis();
        }
    }
}
