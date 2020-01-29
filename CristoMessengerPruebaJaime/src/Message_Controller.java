
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
    
    Message_Controller(){
        myMessageModel = new Message_Model();
    }
    
    public void getMessages(ArrayList<Message> messages){
        myMessageModel.getMessages(messages);
    }
}
