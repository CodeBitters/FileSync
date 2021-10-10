import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
//                    check permissions to write on the destination
                    String destinationPath = new Environment().getDestinationPath();
                    File file = new File(destinationPath);
                    if (!file.canWrite()) {
                        System.out.printf("Permission is denied to write on %s.\n", destinationPath);
                        System.exit(70);
                    }

                    JSONObject jsonObject = new JSONObject(serverReadingMature);
                    Worker worker = new Worker();
                    switch (jsonObject.get("operation_code").toString()) {
                        case "change list":
//                            analyze change list and check for operations
                            boolean isAllFileReceived = worker.getFileFromMaster(jsonObject);
//                            perform delete and move operation
                            boolean isMoveAndDeleteOperationCompleted = worker.moveAndDeleteOperation(jsonObject);
                            break;
                        case "reinstate database":
//                            save files came from master
                            worker.getFileFromMaster(jsonObject);
                            break;
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
