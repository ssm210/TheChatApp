
import java.io.*;
import java.security.*;
import javax.crypto.*;


public class Message implements Serializable {
    String sender;
    String receiver;
    byte[] cipher;
    String vMessage="VERIFY";
    Message(String sender,String receiver){
        this.sender=sender;
        this.receiver=receiver;
    }
}


