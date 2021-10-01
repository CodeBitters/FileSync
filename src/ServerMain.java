import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
//        try {
//            SocketServer socketServer = new SocketServer(155);
//            socketServer.establishServer();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        TODO initially create file list in database
//        TODO identify changes
//        DBConnection dbConnection = new DBConnection();
//        dbConnection.connect();
//        try {
//            Object r = dbConnection.insertDataEntry("hunny","hello/3","hello/2","23232");
//            System.out.println(r);
//            Stack<JSONObject> data = dbConnection.executeSelectQuery("select * from file_info");
//            System.out.println(data);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        ClientWorker clientWorker = new ClientWorker();
//        clientWorker.initiateDatabase(new Environment().getSourcePath());
//        try {
//            JSONObject result = clientWorker.changesIdentificationEngine();
//            System.out.println(result);
////            TODO develop file transfer method using sockets
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        FileSenderServer fileSenderServer = new FileSenderServer();
        try {
            fileSenderServer.sendFile("D:\\Works\\cubclust min.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
