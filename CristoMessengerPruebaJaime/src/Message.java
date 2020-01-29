/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alejandronieto
 */
public class Message {
    
    String id_user_orig;
    String id_user_dest;
    String date;
    String hour;
    Boolean read;
    Boolean sent;
    String text;

    
    Message(){
        id_user_orig = "";
        id_user_dest = "";
        date = "";
        hour = "";
        read = false;
        sent = false;
        text = "";
    }

    public void setId_user_orig(String id_user_orig) {
        this.id_user_orig = id_user_orig;
    }

    public void setId_user_dest(String id_user_dest) {
        this.id_user_dest = id_user_dest;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setRead(int read) {
        if(read == 1){
          this.read = true;
        }
        else{
            this.read = false;
        }
    }

    public void setSent(int sent) {
        if(sent == 1){
          this.sent = true;
        }
        else{
            this.sent = false;
        }
        
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId_user_orig() {
        return id_user_orig;
    }

    public String getId_user_dest() {
        return id_user_dest;
    }

    public String getDate() {
        return date;
    }

    public String getHour() {
        return hour;
    }

    public Boolean getRead() {
        return read;
    }

    public Boolean getSent() {
        return sent;
    }

    public String getText() {
        return text;
    }
    
    
}
