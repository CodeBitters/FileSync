import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

//Master server side

public class ServerMain extends Thread {
    private static String dataBuffer = "{operation_code:'none'}";
    private static String dataBufferMature = "{operation_code:'none'}";

    public static void main(String[] args) {

//        run thread in main method
        ServerMain serverMain = new ServerMain();
        serverMain.start();

        DBConnection dbConnection = new DBConnection();
        dbConnection.connect();
        try {
            boolean isDatabaseExist = dbConnection.setupDatabase();
            Worker worker = new Worker();
            if (!isDatabaseExist) {
//                create database
                worker.initiateDatabase(new Environment().getSourcePath());
            } else {
//                check changes
                JSONObject fileChanges = worker.changesIdentificationEngine();
                System.out.println(fileChanges);
                SocketClient.dataBuffer = fileChanges.toString();
//                start file server to send data
                worker.sendFileFromMaster(fileChanges);
            }
        } catch (SQLException | IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        SocketClient socketClient = null;
        try {
            socketClient = new SocketClient(new Environment().getBackupServerAddress(), new Environment().getCommunicationPortBackupToMaster());

            while (true) {
                dataBuffer = SocketClient.dataBuffer;
                if (!dataBuffer.equals(dataBufferMature)) {
                    dataBufferMature = dataBuffer;
                    socketClient.setupClient();
                }
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
