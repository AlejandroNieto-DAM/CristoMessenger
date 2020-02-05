package Controllers;


import Classes.Message;
import Models.Message_Model;
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
public class Message_Controller {
    
    Message_Model myMessageModel;
    
    public Message_Controller(){
        myMessageModel = new Message_Model();
    }
    
    public void getMessages(ArrayList<Message> messages, String login){
        myMessageModel.getMessages(messages, login);
    }
    
    public void getMessages1(ArrayList<Message> messages, String login, String login_dest){
        myMessageModel.getMessages1(messages, login, login_dest);
    }
}
