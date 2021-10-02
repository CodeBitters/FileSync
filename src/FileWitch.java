import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileWitch {
    private final String location;

    public FileWitch(String location) {
        this.location = location;
    }

    public String createFileHash(String file) throws IOException, NoSuchAlgorithmException {
        FileInputStream fis = new FileInputStream(file);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1)
            messageDigest.update(byteArray, 0, bytesCount);

        fis.close();

        byte[] bytes = messageDigest.digest();

        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public Stack<JSONObject> lookOnFile() {
        Path start = Paths.get(location);
        Stack<JSONObject> returnData = new Stack<JSONObject>();
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            List<String> pathList = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());

            int totalFiles = pathList.size();
//            System.out.printf("%d total files are there in the directory.\n", totalFiles);
            System.out.println("Analyzing...");
            for (String path : pathList) {
                String hash = createFileHash(path);
                JSONObject dataObject = new JSONObject();
                dataObject.put("file_path", path);
                dataObject.put("file_hash", hash);
                returnData.push(dataObject);
            }
        } catch (IOException | NoSuchAlgorithmException | JSONException e) {
            System.out.println(e);
        }
        return returnData;
    }

    public void scanFile(SocketClient socketClient) {
        Path start = Paths.get(location);
        int fileCount = 0;
        try (Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE)) {
            List<String> pathList = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(String::valueOf)
                    .sorted()
                    .collect(Collectors.toList());

            int totalFiles = pathList.size();
            System.out.printf("%d total files are there in the directory.\n", totalFiles);
            System.out.println("Searching...");
            float currentFileID = 1;
            for (String path : pathList) {
                String hash = createFileHash(path);
                socketClient.setDataBuffer(path + " << >> " + hash);
                System.out.printf("\b\b\b\b\b\b\b%.2f %%", (((currentFileID++) / totalFiles) * 100));
            }
            socketClient.setDataBuffer("EOR");
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println(e);
        }
    }
}
