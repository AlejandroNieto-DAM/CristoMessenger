package Controllers;


import Classes.Message;
import Models.Message_Model;
import java.sql.SQLException;
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
    
    public void getMessages1(ArrayList<Message> messages, String login, String login_dest, String date) throws SQLException{
        myMessageModel.getMessages1(messages, login, login_dest, date);
    }
    
    public int getTotalMessagesOfAConversation(String login, String login_dest){
        int a = myMessageModel.getTotalMessagesOfAConversation(login, login_dest);
        return a;
    }
    
    public void insertMessage(String login, String dest, String text, int userDestState){
        this.myMessageModel.insertMessage(login, dest, text, userDestState);
    }
}
