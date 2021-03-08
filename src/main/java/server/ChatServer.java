package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
            // Create the log file
            FileWriter writer = new FileWriter("Log-fil.txt/", true);
            BufferedWriter addW = new BufferedWriter(writer);
            // Accept the incoming request
            s = ss.accept();
            addW.write( " New client on @" + s.getInetAddress().toString().split("/")[1] + ":" + s.getPort());
            addW.newLine();
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
                    for (String string : users) {
                        if (string.equals(name)) {
                            addW.write(" Authorized user: " + name + " Connected!");
                            addW.newLine();
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
                        addW.write(" Did not find user: " + name + " - Closed - " + s.getInetAddress().toString().split("/")[1] + ":" + s.getPort() + " Connection");
                        addW.newLine();
                    }
                }
                addW.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!ss.isClosed());
    }

}

