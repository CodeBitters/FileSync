import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private final int portNumber;

    public SocketServer(int portNumber) {
        this.portNumber = portNumber;
    }

    public void establishServer() throws IOException {
        ServerSocket socketServer = new ServerSocket(portNumber);
        Socket socket = socketServer.accept();

        DataInputStream dataIn = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String temp = "";
        while (!temp.equals("EOR")) {
            temp = dataIn.readUTF();
            System.out.println("Server out: " + temp);
        }

    }
}
