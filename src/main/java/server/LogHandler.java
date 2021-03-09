package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogHandler {
    FileWriter serverlog;

    // very simple file writer system
    {
        try {
            File file = new File("Server_system_logger.txt/");
            if (file.createNewFile()) {
                System.out.print("Server system logger Created!\t");
            }
            serverlog = new FileWriter("Server_system_logger.txt/", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    BufferedWriter serverLog = new BufferedWriter(serverlog);



}
