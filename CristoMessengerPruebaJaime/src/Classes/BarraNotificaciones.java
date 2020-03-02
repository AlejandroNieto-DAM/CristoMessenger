/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Controladores.KnockKnockClient;

import java.util.ArrayList;

/**
 *
 * @author alejandronieto
 */
public class BarraNotificaciones extends Thread {
    KnockKnockClient myKKClient;
    long inicio = System.currentTimeMillis();
    long fin = System.currentTimeMillis();
    
    long inicio2 = System.currentTimeMillis();
    long fin2 = System.currentTimeMillis();
    
    int contador = 0;
    
    ArrayList<String> notificaciones;
 
    public BarraNotificaciones(KnockKnockClient myKKClient){
        this.myKKClient = myKKClient;
        notificaciones = null;
    }
    
    @Override
    public void run(){
        while(true){

            if((fin - inicio) / 1000 > 10){
                
                notificaciones = this.myKKClient.getNotificaciones();
                
                inicio = System.currentTimeMillis();
                
            }
            
            fin = System.currentTimeMillis();
            
            if(notificaciones != null && notificaciones.size() != 0){
            
                
                if((fin2 - inicio2) / 1000 > 3){
                
                    this.myKKClient.sendNotificationChar(notificaciones.get(contador));
                    contador++;
                    if(contador >= notificaciones.size()){
                        contador = 0;
                    }
                    inicio2 = System.currentTimeMillis();
                
                }
            }
            
            fin2 = System.currentTimeMillis(); 
        }
    }
}
