/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pop3client;

import java.io.IOException;
import java.util.List;
import  javax.net;
/**
 *
 * @author 374172
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
    POP3Client client = new POP3Client();
    client.setDebug(true);
    client.Altaconnect("pop.gmail.com");
    client.login("emanueltosto@gmail.com", "basquete ball");
    System.out.println("Number of new emails: " + client.getNumberOfNewMessages());
    List<POP3Client.Message> messages = client.getMessages();
    for (int index = 0; index < messages.size(); index++) {
    System.out.println("--- Message num. " + index + " ---");
    System.out.println(messages.get(index).getBody());
    }
    client.logout();
    client.disconnect();

}
    
}
