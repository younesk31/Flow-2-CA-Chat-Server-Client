package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class ChatServer {

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();


    public static void main(String[] args) throws IOException {
        ArrayList<String> users = new ArrayList<>();
        users.add("1");
        users.add("2");
        users.add("3");
        users.add("4");


        ServerSocket ss = new ServerSocket(8088);
        Socket s;
        // infinite loop for client request for as long the socket is open
        do {
            // Accept the incoming request


            s = ss.accept();
            System.out.println("New client received @ " + s.getInetAddress().toString().split("/")[1] + ":" + s.getPort());
            // obtain input and output streams
            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            String rv = dis.readUTF();
            try {
                try {
                    File file = new File("log.txt/");
                    if (file.createNewFile()) {
                        System.out.print("Log created!\t");
                    } else {
                        System.out.print("Log loaded!\t");
                    }
                } catch (IOException e) {
                    System.out.println("Log creation error! - " + e + "\n");
                }

                if (rv.contains("CONNECT")) {
                    // make sure that the input name is no longer than 20 characters long or else  >:(
                    //String twentyLong = format("%.20s", rv.split("#")[1]);
                    String name = rv.split("#")[1];

                    boolean loggin = false;


                    for (String string : users) {
                        if (string.equals(name)) {
                            System.out.println("Authorized user: " + name + " Connected!");
                            // Create a new handler object for handling this request.
                            ClientHandler match = new ClientHandler(s, name, dis, dos);
                            // Create a new Thread with this object.
                            Thread t = new Thread(match);
                            // add this client to active clients list
                            ar.add(match);
                            // start the thread.
                            t.start();
                            // check who is connected on login and output it
                            dos.writeUTF("Welcome to the Chit-Chat-Server");
                            match.justConnected();
                            loggin = true;
                        }
                    }
                    if (!loggin) {
                        dos.writeUTF("CLOSE#2");
                        //System.out.println("User not found");
                        //s.close();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!ss.isClosed());
    }

}

