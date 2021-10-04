import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileOperation {

    public String filePathUpdateToBackupPath(String sourcePath) throws JSONException {
        return sourcePath.replace(new Environment().getSourcePath(), new Environment().getDestinationPath());
    }

    public String filePathUpdateToTrashPath(String currentPath) throws JSONException {
        return currentPath.replace(new Environment().getDestinationPath(), new Environment().getTrashPath());
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
}
