import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

public class Worker {

    public void initiateDatabase(String file_path) throws JSONException, SQLException {
        System.out.println("File Witcher is starting...");
        System.out.println("Search on " + file_path);
        FileWitch fileWitch = new FileWitch(file_path);
        Stack<JSONObject> result = fileWitch.lookOnFile();

        DBConnection dbConnection = new DBConnection();
        dbConnection.connect();
        System.out.println("Analysis completed.\nDatabase building started.");
        for (JSONObject jsonObject : result) {
            String path = jsonObject.get("file_path").toString();
            String[] splitArray = path.split(Pattern.quote("\\"));
            dbConnection.insertDataEntry(splitArray[splitArray.length - 1], path, "", jsonObject.get("file_hash").toString());
        }
        dbConnection.closeConnection();
        System.out.println("Database building completed.");
    }

    public JSONObject changesIdentificationEngine() throws SQLException, JSONException, IOException {
//        get database entries
        DBConnection dbConnection = new DBConnection();
        dbConnection.connect();
        Stack<JSONObject> pastVersionList = dbConnection.listAllEntries(1);

//        scan file and create a list
        FileWitch fileWitch = new FileWitch(new Environment().getSourcePath());
        List<JSONObject> newVersionList = fileWitch.lookOnFile();
        List<JSONObject> newlyCreatedFiles = new ArrayList<>(newVersionList);
        List<JSONObject> updatedFileList = new ArrayList<>();

        double maxFileSize = 0.0;
        int arrayPositionOuter = 0;
        for (JSONObject formFile : newVersionList) {
//            get maximum file size
            Path path = Paths.get((String) formFile.get("file_path"));
            int fileSize = (int) Files.size(path);
            if (fileSize > maxFileSize)
                maxFileSize = fileSize;

            int arrayPositionInner = 0;
            for (JSONObject fromDB : pastVersionList) {
                if (formFile.get("file_path").equals(fromDB.get("file_path"))) {
//                    file changed
                    if (!formFile.get("file_hash").equals(fromDB.get("file_hash"))) {
//                        add update file to the list
                        updatedFileList.add(formFile);
                    }
//                    remove element for array for reduce search complexity
                    pastVersionList.remove(arrayPositionInner);

//                    create newly created element array
                    newlyCreatedFiles.remove(arrayPositionOuter--);
                    break;
                }
                arrayPositionInner++;
            }
            arrayPositionOuter++;
        }

        System.out.println("Basic file analysis completed.");

//        check deleted and new file list to identify file moves
        List<JSONObject> movedFileList = new ArrayList<>();
        List<JSONObject> pastVersionListForLoop = new ArrayList<>(pastVersionList);
        int outerIndex = 0;
        for (JSONObject deletedFile : pastVersionListForLoop) {
            int innerIndex = 0;
            for (JSONObject newFile : newlyCreatedFiles) {
                if (deletedFile.get("file_hash").equals(newFile.get("file_hash"))) {
                    String[] deletedFilePathSplit = deletedFile.get("file_path").toString().split(Pattern.quote("\\"));
                    String[] newFilePathSplit = newFile.get("file_path").toString().split(Pattern.quote("\\"));
                    if (deletedFilePathSplit[deletedFilePathSplit.length - 1].equals(newFilePathSplit[newFilePathSplit.length - 1])) {
//                        this is file update
//                        create new json object to put into movedFileList
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("old_path", deletedFile.get("file_path").toString());
                        jsonObject.put("new_path", newFile.get("file_path").toString());
                        jsonObject.put("file_hash", newFile.get("file_hash").toString());
                        movedFileList.add(jsonObject);

//                        remove moved element from new and deleted lists
                        pastVersionList.remove(outerIndex--);
                        newlyCreatedFiles.remove(innerIndex);
                        break;
                    }
                }
                innerIndex++;
            }
            outerIndex++;
        }
        System.out.println("Further file analysis completed.");
        /*
        newlyCreatedFiles contains new file
        updatedFileList contain updated file
        pastVersionList contains deleted files
        movedFileList contains moved files
         */

//        maximum file size adjustment
        maxFileSize = Math.ceil(maxFileSize / 1024.0) * 1024;

//        create return object
        JSONObject returnObject = new JSONObject();
        returnObject.put("maximum_file_size", maxFileSize);
        returnObject.put("newly_created_files", newlyCreatedFiles);
        returnObject.put("updated_files", updatedFileList);
        returnObject.put("deleted_files", pastVersionList);
        returnObject.put("moved_files", movedFileList);

//        TODO add file changes to te database

        dbConnection.closeConnection();
        return returnObject;
    }

    public boolean getFileFromMaster(JSONObject changeList) throws IOException, JSONException {
//        start file server
        JSONArray newFiles = (JSONArray) changeList.get("newly_created_files");
        JSONArray toUpdateFiles = (JSONArray) changeList.get("updated_files");
        Environment.maximumFileSize = changeList.getInt("maximum_file_size");
        FileOperation fileOperation = new FileOperation();

        for (int i = 0; i < newFiles.length(); i++) {
            FileSenderClient fileSenderClient = new FileSenderClient();
            JSONObject file = (JSONObject) newFiles.get(i);
            String filePath = fileOperation.filePathUpdateToBackupPath(file.get("file_path").toString());
//            call function to get the file
            fileSenderClient.receiveFile(filePath);
        }


        for (int i = 0; i < toUpdateFiles.length(); i++) {
            FileSenderClient fileSenderClient = new FileSenderClient();
            JSONObject file = (JSONObject) toUpdateFiles.get(i);
            String filePath = fileOperation.filePathUpdateToBackupPath(file.get("file_path").toString());
//            delete existing file to write new file
            new FileOperation().deleteFile(filePath);
//            call function to get the file
            fileSenderClient.receiveFile(filePath);
        }

        System.out.println("File download form master to backup is completed.");
        return true;
    }

    public void sendFileFromMaster(JSONObject changeList) throws IOException, JSONException {
//        start file server
        JSONArray newFiles = (JSONArray) changeList.get("newly_created_files");
        JSONArray toUpdateFiles = (JSONArray) changeList.get("updated_files");

        for (int i = 0; i < newFiles.length(); i++) {
            FileSenderServer fileSenderClient = new FileSenderServer();
            JSONObject file = (JSONObject) newFiles.get(i);
            fileSenderClient.sendFile(file.get("file_path").toString());
        }

        for (int i = 0; i < toUpdateFiles.length(); i++) {
            FileSenderServer fileSenderClient = new FileSenderServer();
            JSONObject file = (JSONObject) toUpdateFiles.get(i);
            fileSenderClient.sendFile(file.get("file_path").toString());
        }
        System.out.println("File upload to backup form master is completed.");
    }

    public boolean moveAndDeleteOperation(JSONObject changeList) throws IOException, JSONException {
        JSONArray filesToDelete = (JSONArray) changeList.get("deleted_files");
        JSONArray filesToMove = (JSONArray) changeList.get("moved_files");

        List<JSONObject> deletedFiles = new ArrayList<>();
        List<JSONObject> movedFiles = new ArrayList<>();

        FileOperation fileOperation = new FileOperation();

//        preform delete operation
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String currentTimestamp = String.valueOf(timestamp).replace(":", ".");
        Set<String> fileSet = new HashSet<>();

        for (int i = 0; i < filesToDelete.length(); i++) {
            JSONObject file = (JSONObject) filesToDelete.get(i);
//            add to fileSet
            fileSet.add(FileOperation.getPathWithoutFile(fileOperation.filePathUpdateToBackupPath(file.get("file_path").toString())));
//            generate paths
            String sourcePath = fileOperation.filePathUpdateToBackupPath(file.get("file_path").toString());
            String destinationPath = fileOperation.filePathUpdateToTrashPath(sourcePath, currentTimestamp);
//            create directory structure
            new File(destinationPath).getParentFile().mkdirs();
//            perform move to trash
            boolean isDeleted = fileOperation.moveFile(sourcePath, destinationPath);
            if (isDeleted)
                deletedFiles.add(file);
        }

//        preform move operation
        for (int i = 0; i < filesToMove.length(); i++) {
            JSONObject file = (JSONObject) filesToMove.get(i);
//            add to fileSet
            fileSet.add(FileOperation.getPathWithoutFile(fileOperation.filePathUpdateToBackupPath(file.get("old_path").toString())));
//            preform move operation
            boolean isMoved = fileOperation.moveFile(fileOperation.filePathUpdateToBackupPath(file.get("old_path").toString()), fileOperation.filePathUpdateToBackupPath(file.get("new_path").toString()));
            if (isMoved)
                movedFiles.add(file);
        }

//        remove empty directories
        for (String filePath : fileSet) {
            new FileOperation().removeEmptyDirectories(filePath);
        }
        System.out.println("Empty directory analysis and removal completed.");

        return (deletedFiles.size() == filesToDelete.length() && movedFiles.size() == filesToMove.length());

    }
}
