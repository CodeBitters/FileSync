import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class FileSenderClient {
    private final int fileServerPort;
    private final String fileServerAddress;
    private final int fileSize;

    public FileSenderClient() throws JSONException {
        fileSize = new Environment().getDataMTU();
        fileServerPort = new Environment().getFileTransferPort();
        fileServerAddress = new Environment().getMasterServerAddress();
    }

    public void receiveFile(String fileDestination) {
        try {
            Socket socket = new Socket(fileServerAddress, fileServerPort);
            System.out.println("Connecting...");

            // receive file
            byte[] dataBuffer = new byte[fileSize];
            InputStream inputStream = socket.getInputStream();

//            create directory structure
            File file = new File(fileDestination);
            file.getParentFile().mkdirs();

            FileOutputStream fileOutputStream = new FileOutputStream(fileDestination);

            int bytesRead;
            int fileSize = 0;

//            recognize file size
            do {
                bytesRead = inputStream.read(dataBuffer, fileSize, (dataBuffer.length - fileSize));
                if (bytesRead >= 0)
                    fileSize += bytesRead;
            } while (bytesRead > -1);

//            write the file
            fileOutputStream.write(dataBuffer, 0, fileSize);
            fileOutputStream.flush();
            System.out.println("File (" + fileDestination + ") downloaded (" + Math.round((fileSize / 1024.0) * 100.0) / 100.0 + " KB)");

            socket.close();
            inputStream.close();
            fileOutputStream.close();
            System.gc();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
