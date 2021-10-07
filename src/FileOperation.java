import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileOperation {

    public static String getPathWithoutFile(String path) {
        String[] splitList = path.split(Pattern.quote("\\"));
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; i < splitList.length - 1; i++) {
            returnString.append(splitList[i]).append("\\");
        }
        return returnString.toString();
    }

    public String filePathUpdateToBackupPath(String sourcePath) throws JSONException {
        return sourcePath.replace(new Environment().getSourcePath(), new Environment().getDestinationPath());
    }

    public String filePathUpdateToTrashPath(String currentPath, String timestamp) throws JSONException {
//        add specific folder named under timestamp
        String trashPath = new Environment().getTrashPath() + "\\" + timestamp + "\\";
        return currentPath.replace(new Environment().getDestinationPath(), trashPath);
    }

    public boolean moveFile(String source, String destination) throws IOException {
        if (Files.exists(Paths.get(source))) {
            Path path = Files.move(Paths.get(source), Paths.get(destination));
            return path != null;
        } else
            return false;
    }

    public boolean deleteFile(String filePath) throws IOException {
        if (Files.exists(Paths.get(filePath))) {
            Files.delete(Paths.get(filePath));
            return !Files.exists(Paths.get(filePath));
        } else
            return false;
    }

    public void removeEmptyDirectories(String pathToScan) throws IOException {
        Path start = Paths.get(pathToScan);
        Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE);
        List<String> pathList = stream
                .filter(Files::isDirectory)
                .map(String::valueOf)
                .sorted()
                .collect(Collectors.toList());
        for (String directoryPath : pathList) {
            File directory = new File(directoryPath);
//            preform delete operation if directory is empty
            if (directory.listFiles().length == 0)
                Files.delete(Paths.get(directoryPath));
        }

    }
}
