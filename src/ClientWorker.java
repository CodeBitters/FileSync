import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

public class ClientWorker {

    public void initiateDatabase(String file_path) {
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
        System.out.println("Database building completed.");
    }

    public JSONObject changesIdentificationEngine() throws SQLException {
//        get database entries
        DBConnection dbConnection = new DBConnection();
        dbConnection.connect();
        Stack<JSONObject> pastVersionList = dbConnection.listAllEntries(1);

//        scan file and create a list
        FileWitch fileWitch = new FileWitch(new Environment().getSourcePath());
        List<JSONObject> newVersionList = fileWitch.lookOnFile();
        List<JSONObject> newlyCreatedFiles = new ArrayList<>(newVersionList);
        List<JSONObject> updatedFileList = new ArrayList<>();

        int arrayPositionOuter = 0;
        for (JSONObject formFile : newVersionList) {
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

//        create return object
        JSONObject returnObject = new JSONObject();
        returnObject.put("newly_created_files", newlyCreatedFiles);
        returnObject.put("updated_files", updatedFileList);
        returnObject.put("deleted_files", pastVersionList);
        returnObject.put("moved_files", movedFileList);

        return returnObject;
    }
}
