
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ssmis
 */
public class MyUser extends User implements Runnable, Serializable {
    int port=1111;
    private PrivateKey privateKey;
    BlockChain blockChain;
    DatagramPacket receivedPacket;
    DatagramPacket sentPacket;
    static DatagramSocket mySocket;
    User otherUser;
    boolean isUp=false;
    MyUser(String username,String otherName, String myIP, String otherIP) throws NoSuchAlgorithmException{
        super(username,myIP);
        KeyPairGenerator kpg=KeyPairGenerator.getInstance("RSA");
	kpg.initialize(2048);
	KeyPair keyPair=kpg.generateKeyPair();
	publicKey=keyPair.getPublic();
        privateKey=keyPair.getPrivate();
        otherUser=new User(otherName,otherIP); 
        //System.out.println("My PublicKey:"+this.publicKey+"\nMy PrivateKey:"+this.privateKey);
    }
    void startThread(){
        isUp=true;
        Thread t1=new Thread(this);
        t1.start();
    }
    void sendMyKeys(String theIP){
        try {
            mySocket=new DatagramSocket(port);
            String str=SerializeObject.serializeObject(this.publicKey);
            byte[] toBeSent=str.getBytes();
            InetAddress ia=InetAddress.getByName(theIP);
            sentPacket=new DatagramPacket(toBeSent,toBeSent.length,ia,port);
            mySocket.send(sentPacket);
        } catch (IOException ex) {
            Logger.getLogger(MyUser.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    void listenForOthersKeys(){
        try {
            System.out.println("Listening for others..");
            byte[] receivedData=new byte[1024];
            receivedPacket=new DatagramPacket(receivedData,receivedData.length);
            mySocket.receive(receivedPacket);
            receivedData=receivedPacket.getData();
            String str=new String(receivedData);
            Object o=SerializeObject.deserializeObject(str);
            otherUser.publicKey=(PublicKey)o;
            System.out.println("Got key succsessfully");
            System.out.println(otherUser.publicKey);
        } catch (IOException ex) {
            Logger.getLogger(MyUser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run() {
       while(isUp){
           try {
               receive();
           } catch (Exception ex) {
               Logger.getLogger(MyUser.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }
    void send(String msg) throws IOException, Exception{
        msg="Message,"+msg;
        byte[] toBeSent;
        Message m=new Message(this.userName,otherUser.userName);
        String str;
        m.cipher=encrypt(otherUser.publicKey,msg);
        str=SerializeObject.serializeObject(m);
        toBeSent=str.getBytes();
        InetAddress ia=InetAddress.getByName(otherUser.IP);
        sentPacket=new DatagramPacket(toBeSent,toBeSent.length,ia,port);
        mySocket.send(sentPacket);
    }
    void receive() throws Exception{
        try {
            System.out.println("Connection is on");
            byte[] receivedData=new byte[1024];
            receivedPacket=new DatagramPacket(receivedData,receivedData.length);
            mySocket.receive(receivedPacket);
            receivedData=receivedPacket.getData();
            String str=new String(receivedData);
            Message m=(Message)SerializeObject.deserializeObject(str);
            String message=decrypt(this.privateKey,m.cipher);
            String arr[]=message.split(",",2);
            if(arr[0].equals("Message")){
                System.out.println("Message received from "+otherUser.userName+":"+arr[1]);
            }
            //String allText=chatBox.getText();
              
        } catch (IOException ex) {
            Logger.getLogger(MyUser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyUser.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    public byte[] encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        byte[] data=cipher.doFinal(message.getBytes());
        return data;
    }
    public String decrypt(PrivateKey privateKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        String message=new String(cipher.doFinal(encrypted));
        return message;
    }

    void end(){
        isUp=false;
    }
    
}

