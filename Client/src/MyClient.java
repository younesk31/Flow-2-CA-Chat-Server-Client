import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MyClient {
    private final Scanner scanner = new Scanner(System.in);
    private Socket socket = null;
    private DataInputStream input = null;
    private DataOutputStream out = null;
    private boolean loggedIn = false;

    // constructor to put ip address and port
    public MyClient(String address, int port) throws IOException {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");
            input = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException u) {
            System.out.println(u);
        }

        // forsøge at logge ind her....

        System.out.print("Username: ");
        String user = scanner.nextLine(); // tag imod brugernavn

        System.out.print("Password: ");
        String pass = scanner.nextLine(); // tag imod password

        // send til server
        out.writeUTF(user + ":" + pass);
        String loginResponse = input.readUTF();
        // velkomst besked
        if (loginResponse.contains("w:")) {
            loggedIn = true;
            System.out.println("Logged in as : " + user);
        }
        // forkert login
        if (loginResponse.contains("e:")) {
            System.out.println(loginResponse.split(":")[1]);
        }

       /*
       Tråd eksempel
       Thread sendMessage = new Thread(new Runnable()
       {
           @Override
           public void run() {
               while (true) {

               }
           }
       });
       */

        String line = "";
        while (!line.equals("Over") || loggedIn) {
            try {
                String cmd = scanner.nextLine();
                out.writeUTF(cmd);

                line = input.readUTF();
                System.out.println(line);
            } catch (IOException i) {
                System.out.println(i);
            }
        }
        try {
            input.close();
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public static void main(String[] args) throws IOException {
        MyClient client = new MyClient("207.154.219.212", 5000);
    }
}

