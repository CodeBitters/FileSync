import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileSenderServer {

    private final int fileServerPort;

    public FileSenderServer() {
        fileServerPort = new Environment().getFileTransferPort();
    }

    public void sendFile(String filePath) throws IOException {
        System.out.println("Waiting...");
        try {
            ServerSocket serverSocket = new ServerSocket(fileServerPort);
            Socket socket = serverSocket.accept();
            System.out.println("Accepted connection : " + socket);

//            read file
            File sourceFile = new File(filePath);
            byte[] dataBuffer = new byte[(int) sourceFile.length()];
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            fileInputStream.read(dataBuffer, 0, dataBuffer.length);

//            generate output stream to send
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(dataBuffer, 0, dataBuffer.length);
            outputStream.flush();

            System.out.printf("File (%s) transferring completed.", filePath);

//            dataBuffer.length provide file size in bytes

            outputStream.close();
            fileInputStream.close();
            serverSocket.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
