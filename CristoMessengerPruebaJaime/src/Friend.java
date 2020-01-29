
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class Friend {
    
    private String login_orig;
    private String login_des;
    private Boolean accept_request;
    
    Friend(){
        
        login_orig = "";
        login_des = "";
        accept_request = false;
        
    }

    public void setLogin_orig(String login_orig) {
        this.login_orig = login_orig;
    }

    public void setLogin_des(String login_des) {
        this.login_des = login_des;
    }

    public void setAccept_request(int a) {
        if(a == 1){
            this.accept_request = true;
        } else if(a == 0){
            this.accept_request = false;
        }
    }

    public String getLogin_orig() {
        return login_orig;
    }

    public String getLogin_des() {
        return login_des;
    }

    public Boolean getAccept_request() {
        return accept_request;
    }
    
   
}
