/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 374172
 */
package pop3client;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class POP3Client {

private Socket socket;

private boolean debug = false;

private BufferedReader reader;
private BufferedWriter writer;

private static final int DEFAULT_PORT = 995;

public boolean isDebug() {
return debug;
}

public void setDebug(boolean debug) {
this.debug = debug;
}
public void Baixaconnect(String host, int port) throws IOException {
SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);
InputStream reader = sslsocket.getInputStream();
OutputStream writer = sslsocket.getOutputStream();
if (debug)
System.out.println("Connected to the host");
}
public void Altaconnect(String host) throws IOException {
Baixaconnect(host, DEFAULT_PORT);
readResponseLine();
}

public boolean isConnected() {
return socket != null && socket.isConnected();
}
public void disconnect() throws IOException {
if (!isConnected())
throw new IllegalStateException("Not connected to a host");
socket.close();
reader = null;
writer = null;
if (debug)
System.out.println("Disconnected from the host");
}
protected String readResponseLine() throws IOException{
String response = reader.readLine();
if (debug) {
System.out.println("DEBUG [in] : " + response);
}
if (response!=null && response.startsWith("-ERR")){
throw new RuntimeException("Server has returned an error: " + response.replaceFirst("-ERR ", ""));
}
return response;
}
protected String sendCommand(String command) throws IOException {
if (debug) {
System.out.println("DEBUG [out]: " + command);
}
writer.write(command + "\n");
writer.flush();
return readResponseLine();
}
public void login(String username, String password) throws IOException {
sendCommand("USER " + username);
sendCommand("PASS " + password);
}
public int getNumberOfNewMessages() throws IOException {
String response = sendCommand("STAT");
String[] values = response.split(" ");
return Integer.parseInt(values[1]);
}

public void logout() throws IOException {
sendCommand("QUIT");
}

public class Message {

private final Map<String, List<String>> headers;

private final String body;

protected Message(Map<String, List<String>> headers, String body) {
this.headers = Collections.unmodifiableMap(headers);
this.body = body;
}

public Map<String, List<String>> getHeaders() {
    return headers;
}    

public String getBody() {
return body;
}

}

protected Message getMessage(int i) throws IOException {
String response = sendCommand("RETR " + i);
Map<String, List<String>> headers = new HashMap<String, List<String>>();
String headerName = null;
// process headers
while ((response = readResponseLine()).length() != 0) {
if (response.startsWith("\t")) {
continue; //no process of multiline headers
}
int colonPosition = response.indexOf(":");
headerName = response.substring(0, colonPosition);
String headerValue;
if (headerName.length() > colonPosition) {
headerValue = response.substring(colonPosition + 2);
} else {
headerValue = "";
}
List<String> headerValues = headers.get(headerName);
if (headerValues == null) {
headerValues = new ArrayList<String>();
headers.put(headerName, headerValues);
}
headerValues.add(headerValue);
}
// process body
StringBuilder bodyBuilder = new StringBuilder();
while (!(response = readResponseLine()).equals(".")) {
bodyBuilder.append(response + "\n");
}
return new Message(headers, bodyBuilder.toString());
}

public List<Message> getMessages() throws IOException {
    int numOfMessages = getNumberOfNewMessages();
    List<Message> messageList = new ArrayList<Message>();
    for (int i = 1; i <= numOfMessages; i++) {
    messageList.add(getMessage(i));
    }
    return messageList;
    
}
}
