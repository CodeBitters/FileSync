import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient {
    private String serverIP;
    private int portNumber;
    private String dataBuffer;

    public SocketClient(String serverIP, int portNumber) {
        this.serverIP = serverIP;
        this.portNumber = portNumber;
        this.dataBuffer = "Server starting...";
    }

    public void setupClient() throws IOException {
        Socket socket = new Socket(serverIP, portNumber);

        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

        String temp = "";
        String oldTemp = "";
        while (!temp.equals("EOR")) {
            temp = this.dataBuffer;
            if (oldTemp.equals(temp)) {
                System.out.print("");
            } else {
                dataOut.writeUTF(temp);
                dataOut.flush();
                oldTemp = temp;
            }
        }
    }

    public void setDataBuffer(String dataBuffer) {
        this.dataBuffer = dataBuffer;
    }
}
