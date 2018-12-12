package netclasses.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// сервер, принимающий клиентов
public class NetServer implements Runnable {
    private int port;
    private ServerSocket ssocket;
    private ServerThread serverThread;

    public NetServer(int port) throws IOException {
        this.port = port;
        this.ssocket = new ServerSocket(port);
    }

    public void sendMessage(String message) {
        if(serverThread!=null)
            serverThread.addOutItem(message);
    }

    public void stop() {
        if(!(serverThread==null))
            serverThread.setActive(false);
        try {
            if (!ssocket.isClosed()) {
                ssocket.close();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        Socket socket = null;
        try {
            while (true) {
                socket = ssocket.accept();
                try {
                    serverThread = new ServerThread(socket);
                } catch (IOException e) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            try {
                if((socket!=null) && (!socket.isClosed()))
                    socket.close();
            }
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } finally {
            try {
                if(socket!=null) {
                    socket.close();
                    System.out.println("Net server завершил работу");
                }
            }
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
