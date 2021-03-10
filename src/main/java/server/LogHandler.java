package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogHandler {
    public final String R = "\033[0;31m";   //RED
    public final String G = "\u001B[32m";   //GREEN
    public final String B = "\u001B[34m";   //BLUE
    public final String RE = "\033[0m";     //Text Reset
    public final String df = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
    public final String t = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));

    FileWriter serverlog;

    // very simple file writer system
    {
        try {
            File file = new File("Server_system_logger.txt/");
            if (file.createNewFile()) {
                System.out.print(df+" "+R+"SERVER#" +RE+ " Log Created!\t");
            }
            serverlog = new FileWriter("Server_system_logger.txt/", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    BufferedWriter serverLog = new BufferedWriter(serverlog);



}
