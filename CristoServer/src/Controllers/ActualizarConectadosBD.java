/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import cristoserver.KKServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alejandronieto
 */
public class ActualizarConectadosBD extends Thread{
    
    ArrayList<String> conectados;
    KKServer mykkServer;
    User_Controller user_controller;

    long inicio = System.currentTimeMillis();
    long fin = System.currentTimeMillis();
    
    public ActualizarConectadosBD(KKServer mykkServer){
        conectados  = new ArrayList();
        this.mykkServer = mykkServer;
        user_controller = new User_Controller();   
    }
    
    public void run(){
        
        while(true){

            if((fin - inicio) / 1000 > 30){
                
                if(this.mykkServer.getHebrasSize() > 0){
                
                    for(int i = 0; i < this.mykkServer.getHebrasSize(); i++){
                        conectados.add(this.mykkServer.getConexionAt(i).getLogin());
                    }


                    user_controller.setDisconnectedOtherUsers(conectados);
                }
                
            }
            
            fin = System.currentTimeMillis();
        }
        
    }
    
}
