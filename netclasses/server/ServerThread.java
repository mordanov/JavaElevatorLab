package netclasses.server;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// поток для каждого клиента сервера
public class ServerThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean active;
    private List<String> outitems = new ArrayList<>();

    public ServerThread(Socket s) throws IOException {
        socket = s;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        active = true;
        start();
    }

    public void addOutItem(String item) {
        outitems.add(item);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void run() {
        try {
            while (active) {
                while(outitems.size()>0) {
                    String line = outitems.get(0);
                    out.println(line);
                    outitems.remove(0);
                }
                sleep(500);
            }
        } catch (Exception ex) {
            outstr(ex.getMessage());
        } finally {
            try {
                out.println("Сервер недоступен!");
                socket.close();
            } catch (IOException ex) {
                outstr(ex.getMessage());
            }
        }
    }

    private void outstr(String text) {
        Date ld = new Date();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        System.out.println(df.format(ld) + ' ' + text);
    }

}

