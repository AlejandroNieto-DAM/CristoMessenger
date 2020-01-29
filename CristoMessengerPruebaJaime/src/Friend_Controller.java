
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
public class Friend_Controller {
    
     Friend_Model a;
     
     Friend_Controller(){
         a = new Friend_Model();
     }
     
    public void getFriendsOf(ArrayList<Friend> amigos, String id_user){
        a.getFriendsOf(amigos, id_user);
    }
    
}
