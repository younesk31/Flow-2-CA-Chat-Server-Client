package server;

import java.io.BufferedWriter;
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

    BufferedWriter serverLog;

    {
        try {
            serverLog = new BufferedWriter( new FileWriter( "Server_system_logger.txt/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
