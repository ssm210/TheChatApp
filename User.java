
import java.io.Serializable;
import java.security.PublicKey;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ssmis
 */
public class User implements Serializable{
    String userName;
    String IP;
    PublicKey publicKey;
    User(String username, String IPAddress){
        userName=username;
        IP=IPAddress;
    }
}
