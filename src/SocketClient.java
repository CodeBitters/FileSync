import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient {
    public static String dataBuffer = "{operation_code:'none'}";
    private final String serverIP;
    private final int portNumber;

    public SocketClient(String serverIP, int portNumber) {
        this.serverIP = serverIP;
        this.portNumber = portNumber;
    }

    public void setupClient() throws IOException {
        Socket socket = new Socket(serverIP, portNumber);

        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());

        String temp = "";
        String oldTemp = "";
        while (!temp.equals("EOR")) {
            temp = dataBuffer;
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
        SocketClient.dataBuffer = dataBuffer;
    }
}
