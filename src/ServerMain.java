import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try {
            SocketServer socketServer = new SocketServer(155);
            socketServer.establishServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
