import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

//Backup server side

class ThreadRunSocketServer extends Thread {

    public void run() {
        try {
            SocketServer socketServer = new SocketServer(new Environment().getCommunicationPortBackupToMaster());
            socketServer.establishServer();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        Thread thread = new Thread(this, "Socket Server Thread");
        thread.start();
    }
}

public class ClientMain extends Thread {
    private static String serverReading = "{operation_code:'none'}";
    private static String serverReadingMature = "{operation_code:'none'}";

    public static void main(String[] args) {

//        run thread in main method
        ClientMain clientMain = new ClientMain();
        clientMain.start();

//        run thread for socket server
        ThreadRunSocketServer threadRunSocketServer = new ThreadRunSocketServer();
        threadRunSocketServer.start();

    }

    public void run() {
        while (true) {
            serverReading = SocketServer.dataClientReceivedMessage;
//            work only reading data change
            if (!serverReading.equals(serverReadingMature)) {
                serverReadingMature = serverReading;
                try {
                    JSONObject jsonObject = new JSONObject(serverReadingMature);
                    switch (jsonObject.get("operation_code").toString()) {
                        case "change list":
//                            analyze change list and check for operations
                            new Worker().getFileFromMaster(jsonObject);
//                            TODO perform delete and move operation
                            break;
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
