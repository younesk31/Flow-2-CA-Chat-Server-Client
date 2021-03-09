package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

import static server.ChatServer.ar;

class ClientHandler implements Runnable {
    final DataInputStream dis;
    final DataOutputStream dos;
    private final String name;
    Socket s;
    boolean isloggedin;
    LogHandler lh = new LogHandler();

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin = true;
    }

    // check the array for online users as soon as a new client connects and output it
    public void justConnected() throws IOException {
        StringBuilder connected = new StringBuilder();
        connected.append("ONLINE#");
        for (ClientHandler ch : ar) {
            if (ch.isloggedin) {
                connected.append(ch.name).append(",");
            }
        }
        connected.deleteCharAt(connected.length() - 1);

        for (ClientHandler ch2 : ar) {
            ch2.dos.writeUTF(connected.toString());
        }
    }

    // check the array for online users as soon as a client disconnects and output it to the online users
    public void closeOnline() throws IOException {
        StringBuilder connected = new StringBuilder();
        connected.append("Server: '").append(this.name).append("' Left the server!\n");
        connected.append("ONLINE#");
        for (ClientHandler ch : ar) {
            if (ch.isloggedin) {
                connected.append(ch.name).append(",");
            }
        }
        connected.deleteCharAt(connected.length() - 1);
        for (ClientHandler ch2 : ar) {
            if (ch2.isloggedin) {
                ch2.dos.writeUTF(connected.toString());
            }
        }
    }

    public void closethatshit() throws IOException {
        this.isloggedin = false;
        closeOnline();
        this.s.close();
        this.dis.close();
        this.dos.close();
        ar.remove(this);
    }

    @Override
    public void run() {
        String received;
        while (s.isConnected() && !s.isClosed()) {
            try {
                // receive client input string
                received = dis.readUTF();
                // take what we receive and put it trough a tokenizer that splits at delimiter '#'
                StringTokenizer st = new StringTokenizer(received, "#");
                String cmd = st.nextToken();
                String recipient = null;
                String msgToSend = null;
                String[] recipients = null;
                // tokenizer and send to more functionality
                while (st.hasMoreTokens()) {
                    recipient = st.nextToken();
                    if (recipient.contains(",")) {
                        recipients = recipient.split(",");
                    }
                    while (st.hasMoreTokens()) {
                        msgToSend = st.nextToken();
                    }
                }
                // most of the functionality to send messages to eachother on the server
                // Listen to the Close cmd and inform the rest that this user has left the server
                if (cmd.equals("CLOSE")) {
                    try {
                        this.dos.writeUTF("CLOSE#0");
                        closethatshit();
                    } catch (Exception ignored) {
                    }
                    // Send a msg to all
                } else if (cmd.contains("SEND") && recipient.contains("*") && this.isloggedin) {
                    if (ar.size() > 1) {
                        for (ClientHandler mc : ar) {
                            if (!mc.name.equals(this.name)) {
                                lh.serverLog.write("MESSAGE#" + this.name + " --> to all#" + msgToSend);
                                mc.dos.writeUTF("MESSAGE#*#" + msgToSend);
                            }
                        }
                    } else {
                        this.dos.writeUTF("SERVER#" + "INGEN ONLINE");
                    }
                    // Send a dm to a specific person
                } else if (cmd.contains("SEND") && !recipient.contains("*") && !recipient.equals(null) && this.isloggedin) {
                    if (!recipient.contains(",")) {
                        for (ClientHandler mc : ar) {
                            if (mc.name.equals(recipient)) {
                                mc.dos.writeUTF("MESSAGE#" + this.name + "#" + msgToSend);
                                break;
                            }
                        }
                    } else if (recipient.contains(",")) {
                        boolean messagesend;
                        for (String s : recipients) {
                            messagesend = false;
                            for (ClientHandler mc : ar) {
                                if (s.equals(mc.name) && mc.isloggedin) {
                                    mc.dos.writeUTF("MESSAGE#" + this.name + "#" + msgToSend);
                                    messagesend = true;
                                    break;
                                }
                            }
                            if (!messagesend) {
                                dos.writeUTF(s + " not found");
                            }
                        }
                    } else {
                        dos.writeUTF("CLOSE#1");
                        closethatshit();
                    }
                } else {
                    // illegal input
                    dos.writeUTF("CLOSE#1");
                    closethatshit();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
