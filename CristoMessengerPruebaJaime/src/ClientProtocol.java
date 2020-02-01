/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class ClientProtocol {
    
    private String cadenaPrincipal;
    private final int LOGGING = 0;
    private final int LOGGED = 0;
    
    private int state = LOGGING;
    
    private String login;
    private String passwd;
    
    ClientProtocol(String login, String passwd){
        cadenaPrincipal = "PROTOCOLOCRISTOMESSENGER1.0";
        login = login;
        passwd = passwd;
    }
    
    public String processInput(String theInput){
        String theOutput = null;
        
        
        if(state == LOGGING){
            theOutput = "PROTOCOLCRISTOMESSENGER1.0#CLIENT#LOGIN#" + login + "#" + passwd;
            state = LOGGED;
            
        } else if (state == LOGGED){
            if(theInput.startsWith(cadenaPrincipal)){
                if(theInput.contains("LOGIN_CORRECT")){

                }
            }
        }
        return theOutput;
    }
}
