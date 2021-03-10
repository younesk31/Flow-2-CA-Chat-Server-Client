package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class ChatServer {

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();


    public static void main(String[] args) throws IOException {
        // Create the LogHandler
        LogHandler lh = new LogHandler();
        // Hardcoded Users
        ArrayList<String> users = new ArrayList<>();
        users.add("1");
        users.add("2");
        users.add("3");
        users.add("4");
        users.add("user1");
        users.add("user2");
        users.add("user3");
        users.add("user4");
        users.add("younes");
        users.add("søren");
        users.add("august");

        ServerSocket ss = new ServerSocket(6666);
        Socket s;
        // infinite loop for client request for as long the socket is open
        System.out.println(lh.df + " " + lh.R + "SERVER#" + lh.RE + " Listing on port: "+ss.getLocalPort());
        do {

            // Accept the incoming request
            s = ss.accept();
            System.out.println(lh.df + " " + lh.R + "SERVER#" + lh.RE + " New client on @" + s.getInetAddress().toString().split("/")[1] + ":" + s.getPort());
            lh.serverLog.write(" New client on @" + s.getInetAddress().toString().split("/")[1] + ":" + s.getPort());
            lh.serverLog.newLine();
            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            String rv = dis.readUTF();
            try {
                if (rv.contains("CONNECT")) {
                    // make sure that the input name is no longer than 20 characters long or else  >:(
                    //String twentyLong = format("%.20s", rv.split("#")[1]);
                    String name = rv.split("#")[1];
                    boolean loggin = false;
                    for (ClientHandler mc : ar) {
                        if (mc.name.equals(name) && mc.isloggedin) {
                            dos.writeUTF(lh.t + " " + lh.R + "SERVER#" + lh.RE + " User: "+name+" already logged in!");
                            dos.writeUTF("CLOSE#2");
                            loggin = true;
                            s.close();
                        }
                    }
                    
                    for (String string : users) {
                        if ((string.equals(name)) && !loggin) {
                            System.out.println(lh.df + " " + lh.R + "SERVER#" + lh.RE + " Authorized user: " + name + " Connected!");
                            lh.serverLog.write(" Authorized user: " + name + " Connected!");
                            lh.serverLog.newLine();
                            // Create a new handler object for handling this request.
                            ClientHandler match = new ClientHandler(s, name, dis, dos);
                            // Create a new Thread with this object.
                            Thread t = new Thread(match);
                            // add this client to active clients list
                            ar.add(match);
                            // start the thread.
                            t.start();
                            // check who is connected on login and output it
                            dos.writeUTF(lh.t + " " + lh.R + "SERVER#" + lh.RE + " Welcome to the Chit-Chat-Server");
                            match.justConnected();
                            loggin = true;
                        }
                    }
                    if (!loggin) {
                        dos.writeUTF("CLOSE#2");
                        System.out.println(lh.df + " " + lh.R + "SERVER#" + lh.RE + " Did not find user: " + name + " - Closing Connection - " + s.getInetAddress().toString().split("/")[1] + ":" + s.getPort());
                        lh.serverLog.write(" Did not find user: " + name + " - Closing Connection - " + s.getInetAddress().toString().split("/")[1] + ":" + s.getPort());
                        lh.serverLog.newLine();
                    }
                }
                lh.serverLog.flush();
            } catch (IOException e) {
                System.out.println(lh.df + " " + lh.R + "SERVER#" + lh.RE + " Server connection error");
                break;
            }
        } while (!ss.isClosed());
        lh.serverLog.flush();
    }

}

