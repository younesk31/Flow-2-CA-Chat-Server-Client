package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import static java.lang.String.format;

public class ChatServer {

    // Vector to store active clients
    static Vector<ClientHandler> ar = new Vector<>();

    public static void main(String[] args) throws IOException {
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
                if (rv.contains("CONNECT")){
                    // make sure that the input name is no longer than 20 characters long or else  >:(
                    String twentyLong = format("%.20s", rv.split("#")[1]);
                    System.out.println("Authorized user: " + twentyLong + " Connected!");
                    // Create a new handler object for handling this request.
                    ClientHandler match = new ClientHandler(s, twentyLong, dis, dos);
                    // Create a new Thread with this object.
                    Thread t = new Thread(match);
                    // add this client to active clients list
                    ar.add(match);
                    // start the thread.
                    t.start();
                    // check who is connected on login and output it
                    match.justConnected();
                } else {
                    s.close();
                    System.out.println("Closing connection: Did not meet specifications ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!ss.isClosed());
    }

}

class ClientHandler implements Runnable {
    final DataInputStream dis;
    final DataOutputStream dos;
    private final String name;
    Socket s;
    boolean isloggedin;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    public void justConnected() throws IOException {
        StringBuilder connected = new StringBuilder();
        connected.append("Welcome to the Chit-Chat-Server \nClients currently connected:\nONLINE#");
        for (ClientHandler ch : ChatServer.ar) {
            if (ch.isloggedin) {
                connected.append(ch.name).append(",");
            }
        }
        connected.deleteCharAt(connected.length() - 1);
        dos.writeUTF(connected.toString());
    }

    public void onlineToString() throws IOException {
        StringBuilder connected = new StringBuilder();
        connected.append("ONLINE#");
        for (ClientHandler ch : ChatServer.ar) {
            if (ch.isloggedin) {
                connected.append(ch.name).append(",");
            }
        }
        connected.deleteCharAt(connected.length() - 1);
        dos.writeUTF(connected.toString());
    }

    @Override
    public void run() {
        String received;
        while (s.isConnected()) {
            try {
                // receive the string
                received = dis.readUTF();

                StringTokenizer st = new StringTokenizer(received,"#");
                String cmd       = st.nextToken();
                String recipient = null;
                String msgToSend = null;

                while (st.hasMoreTokens()) {
                    recipient = st.nextToken();
                    while (st.hasMoreTokens()) {
                        msgToSend = st.nextToken();
                    }
                }

                    for (ClientHandler mc : ChatServer.ar) {
                        // Listen to the Close cmd and inform the rest that this user has left the server
                    if (cmd.equals("CLOSE") && !mc.name.equals(this.name)) {
                        mc.dos.writeUTF("Server: '"+ this.name +"' Left the server!");
                        break;
                        // Send a msg to all
                    } else if (cmd.contains("SEND") && recipient.equals("*") && mc.isloggedin && !mc.name.equals(this.name)) {
                        mc.dos.writeUTF(this.name + ": " + msgToSend);
                        break;
                        // Send a dm to a specific person
                    } else if (cmd.contains("SEND") && mc.name.equals(recipient) && mc.isloggedin && !mc.name.equals(this.name)) {
                        mc.dos.writeUTF(this.name + ": " + msgToSend);
                        break;
                    }
                }
                if (cmd.equals("CLOSE")) {
                    this.dos.writeUTF("CLOSE#");
                    this.isloggedin = false;
                    ChatServer.ar.remove(this.s);
                    this.s.close();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // closing resources
            this.dis.close();
            this.dos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}