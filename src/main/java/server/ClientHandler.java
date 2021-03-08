package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

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
        connected.append("ONLINE#");
        for (ClientHandler ch : ChatServer.ar) {
            if (ch.isloggedin) {
                connected.append(ch.name).append(",");
            }
        }
        connected.deleteCharAt(connected.length() - 1);


        for (ClientHandler ch2 : ChatServer.ar) {
            ch2.dos.writeUTF(connected.toString());
        }

    }


    public void closeOnline() throws IOException {
        StringBuilder connected = new StringBuilder();


        connected.append("Server: '").append(this.name).append("' Left the server!\n");
        connected.append("ONLINE#");
        for (ClientHandler ch : ChatServer.ar) {
            if (ch.isloggedin) {
                connected.append(ch.name).append(",");
            }
        }
        connected.deleteCharAt(connected.length() - 1);
        for (ClientHandler ch2 : ChatServer.ar) {
            if (ch2.isloggedin) {
                ch2.dos.writeUTF(connected.toString());
            }
        }
    }

    @Override
    public void run() {
        String received;
        while (s.isConnected() && !s.isClosed()) {
            try {
                // receive the string
                received = dis.readUTF();

                StringTokenizer st = new StringTokenizer(received, "#");
                String cmd = st.nextToken();
                String recipient = "null";
                String msgToSend = "null";
                String[] recipients = null;

                while (st.hasMoreTokens()) {
                    recipient = st.nextToken();
                    if (recipient.contains(",")) {
                        recipients = recipient.split(",");
                    }
                    while (st.hasMoreTokens()) {
                        msgToSend = st.nextToken();
                    }
                }

                for (ClientHandler mc : ChatServer.ar) {
                    // Listen to the Close cmd and inform the rest that this user has left the server
                    if (cmd.equals("CLOSE") && !mc.name.equals(this.name)) {
                        this.isloggedin = false;
                        closeOnline();
                        break;
                        // Send a msg to all
                    } else if (cmd.contains("SEND") && recipient.equals("*") && mc.isloggedin && !mc.name.equals(this.name)) {
                        mc.dos.writeUTF("MESSAGE#" + this.name + ": " + msgToSend);
                        break;
                        // Send a dm to a specific person
                    } else if (cmd.contains("SEND") && mc.name.equals(recipient) && mc.isloggedin && !mc.name.equals(this.name)) {
                        mc.dos.writeUTF("MESSAGE#" + this.name + ": " + msgToSend);
                        break;
                    } else if (cmd.contains("SEND") && recipients != null && mc.isloggedin && !mc.name.equals(this.name)) {
                        boolean messageSend;
                        for (int i = 0; i < recipients.length; i++) {
                            messageSend = false;
                            for (ClientHandler mc1 : ChatServer.ar) {
                                if (recipients[i].equals(mc1.name) && mc1.isloggedin) {
                                    mc1.dos.writeUTF("MESSAGE#" + this.name + ": " + msgToSend);
                                    messageSend = true;
                                    break;
                                }
                            }
                            if (!messageSend) {
                                dos.writeUTF(recipients[i] + " not found");
                            }
                        }
                        break;
                    } else {
                        dos.writeUTF("CLOSE#1");
                        closeOnline();
                        this.isloggedin = false;
                        ChatServer.ar.remove(this.s);
                        this.s.close();
                        break;
                    }
                }
                if (cmd.equals("CLOSE")) {
                    this.dos.writeUTF("CLOSE#0");
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
