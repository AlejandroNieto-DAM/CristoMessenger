/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cristoserver;

import Classes.Friend;
import Classes.Message;
import Classes.User;
import Controllers.Friend_Controller;
import Controllers.Message_Controller;
import Controllers.User_Controller;
import java.util.ArrayList;
 
public class KnockKnockProtocol{
    
    private String cadenaPrincipal = "PROTOCOLCRISTOMESSENGER1.0";
    
    ArrayList<User> usuarios = new ArrayList();
    ArrayList<Friend> friends = new ArrayList();
    ArrayList<Message> messages = new ArrayList();

 
 
    public String processInput(String theInput) {
        String theOutput = null;
        
        
 
        if(theInput.startsWith(cadenaPrincipal)){
            
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

                 for(int i = 0; i < usuarios.size() && encontrado == false; i++){
                     
                     CristoServer.debug(usuarios.get(i).getLogin() + " " + usuarios.get(i).getPasswd() + " " + usuarios.get(i).getEstadoUsuario());
                     
                     if(usuarios.get(i).getLogin().equals(login) && usuarios.get(i).getPasswd().equals(pass)){
                         encontrado = true;
                     }
                     
                 }
                 
                 if(encontrado == true){
                    System.out.println("Este usuario existe.");
                    CristoServer.debug("Este usuario existe");
                    theOutput = getFriends(login);
                 } else {
                     System.out.println("Este usuario no existe.");
                     CristoServer.debug("Este usuario no existe");
                     theOutput = "PROTOCOLCRISTOMESSENGER1.0#SERVER#ERROR#BAD_LOGIN";
                 }
     
            } 
            
            if(theInput.contains("MESSAGES")){
                String login = "@zizou";
                Message_Controller myC = new Message_Controller();
                myC.getMessages(messages, login);
                theOutput = "PROTOCOLCRISTOMESSENGER1.0MESSAGES";
                
                for(int i = 0; i < messages.size(); i++){
                    
                    System.out.println(messages.get(i).getId_user_orig() + " " + messages.get(i).getId_user_dest() + " " + messages.get(i).getText());
                    theOutput += "#" + messages.get(i).getId_user_orig() + "#" + messages.get(i).getId_user_dest() + "#" + messages.get(i).getText();
                }
                
                
            }
            
        } else {
            CristoServer.debug("MENSAJE INVALIDO");  
        }

        CristoServer.debug(theOutput);
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
                        substringFriends +=  "#" + usuarios.get(j).getLogin() + "#";
                        if(usuarios.get(j).getEstadoUsuario().equals(true)){
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
