import java.io.IOException;

public class ClientMain extends Thread {
    private static final SocketClient socketClient = new SocketClient("localhost", 155);

    public static void main(String[] args) {
//        ClientMain thread = new ClientMain();
//        thread.start();
//        System.out.println("FileSync Client....");
//        try {
//            socketClient.setupClient();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        FileSenderClient fileSenderClient = new FileSenderClient();
        try {
            fileSenderClient.receiveFile("hii.pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void run() {
//        String location = "D:\\Desktop Files\\";
//        System.out.println("File Witcher is starting...");
//        System.out.println("Search on " + location);
//        FileWitch fileWitch = new FileWitch(location);
//        fileWitch.scanFile(socketClient);
//    }

}
