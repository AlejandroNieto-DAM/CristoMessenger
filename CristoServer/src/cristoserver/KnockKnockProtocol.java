/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import Classes.Friend;
import Classes.User;
import Controllers.Friend_Controller;
import Controllers.User_Controller;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
 
public class KnockKnockProtocol{
    private static final int WAITING = 0;
    private static final int SENTKNOCKKNOCK = 1;
    private static final int SENTCLUE = 2;
    private static final int ANOTHER = 3;
 
    private static final int NUMJOKES = 5;
 
    private int state = WAITING;
    private int currentJoke = 0;
    
    private String cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
    
    ArrayList<User> usuarios = new ArrayList();
    ArrayList<Friend> friends = new ArrayList();
 
 
    public String processInput(String theInput) {
        String theOutput = null;
        
        
 
        if(theInput.startsWith("PROTOCOLCRISTOMESSENGER1.0")){
            
            if(theInput.contains("LOGIN")){
                String login = "";
                String pass = "";
                 int contador = 0;
                 for(int i = 0; i < theInput.length(); i++){
                     if(theInput.charAt(i) == '#'){
                         contador++; 
                     }

                     if(contador == 3){
                             login += theInput.charAt(i);
                     }

                     if(contador == 4){
                         pass += theInput.charAt(i);
                     }
                 }

                 login = login.substring(1, login.length());
                 pass = pass.substring(1, pass.length());

                 User_Controller a = new User_Controller();
                 a.getUsuarios(usuarios);  
                 
                 boolean encontrado = false;
                 int position = 0;

                 for(int i = 0; i < usuarios.size() && encontrado == false; i++){
                     
                     CristoServer.debug(usuarios.get(i).getLogin() + usuarios.get(i).getPasswd());
                     
                     if(usuarios.get(i).getLogin().equals(login) && usuarios.get(i).getPasswd().equals(pass)){
                         System.out.println("Este usuario existe.");
                         CristoServer.debug("Este usuario existe");
                         
                         position  = i;
                         encontrado = true;
                       // PROTOCOLCRISTOMESSENGER1.0#SERVER#LOGIN_CORRECT#1#Alex#FRIENDS#3#Patri#2#CONNECTED#Pedro#3#NOT_CONNECTED#Pablo#4#CONNECTED
                     }
                     
                 }
                 
                 if(encontrado == true){
                    theOutput = getFriends(login);
                 } else {
                     theOutput = "";
                 }
                     
                 
                
                  
            }
            
        }
        
        
        
        return theOutput;
    }
        
    public String getFriends(String login){
        String cadena = "";
        
        Friend_Controller myController = new Friend_Controller();
        myController.getFriendsOf(this.friends, login);
        
        cadena = cadenaPrincipal + "#SERVER#LOGIN_CORRECT#";
                
        for(int i = 0; i < usuarios.size(); i++){
            if(usuarios.get(i).getLogin().equals(login)){
                cadena += login + "#" + usuarios.get(i).getNombreUsuario() + "#FRIENDS";
            }
        }
        
        
        int amigos = 0;
        String substringFriends = "";
        for(int i = 0; i < friends.size(); i++){ 
            if(friends.get(i).getLogin_orig().equals(login)){
                amigos++;
                for(int j = 0; j < usuarios.size(); j++){
                    if(friends.get(i).getLogin_des().equals(usuarios.get(j).getLogin())){
                        substringFriends += "#" + usuarios.get(j).getNombreUsuario() + "#" + usuarios.get(j).getLogin() + "#";
                        if(usuarios.get(j).equals(true)){
                            substringFriends += "CONNECTED";
                        } else {
                            substringFriends += "NOT_CONNECTED";
                        }
                    }
                }  
            }     
        }
        
        cadena += "#" + amigos + substringFriends;
        
        System.out.println("CADENA --> " + cadena);
        
        return cadena;
    }
}
