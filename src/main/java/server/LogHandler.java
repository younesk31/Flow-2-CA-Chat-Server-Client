package server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LogHandler {
    FileWriter serverlog;
    FileWriter clientlog;

    // very simple file writer system
    {
        try {
            serverlog = new FileWriter("Server_Log.txt/", true);
            clientlog = new FileWriter("Client_Log.txt/", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    BufferedWriter serverLog = new BufferedWriter(serverlog);
    BufferedWriter clientLog = new BufferedWriter(clientlog);



}
