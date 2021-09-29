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

    public void changesIdentificationEngin() throws SQLException {
//        get database entries
        DBConnection dbConnection = new DBConnection();
        dbConnection.connect();
        Stack<JSONObject> pastVersionList = dbConnection.listAllEntries(1);

//        scan file and create a list
        FileWitch fileWitch = new FileWitch(new Environment().getSourcePath());
        List<JSONObject> newVersionList = fileWitch.lookOnFile();
        List<JSONObject> newlyCreatedFiles = new ArrayList<>(newVersionList);
        List<JSONObject> updatedFileList = new ArrayList<>();
        long comparisons = 0;
        int arrayPositionOuter = 0;
        for (JSONObject formFile : newVersionList) {
            int arrayPositionInner = 0;
            for (JSONObject fromDB : pastVersionList) {
                comparisons++;
                if (formFile.get("file_path").equals(fromDB.get("file_path"))) {
//                    file changed
                    if (!formFile.get("file_hash").equals(fromDB.get("file_hash"))) {
//                        updated file are hear
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
        System.out.println(comparisons);

        /*

        newlyCreatedFiles contains file
        updatedFileList contain updated file
        pastVersionList contains deleted files

         */
        System.out.println(newlyCreatedFiles);
        System.out.println(updatedFileList);
        System.out.println(pastVersionList);

//        TODO analyze deleted and newly created lists and and file updates(file path changes) by comparing hashes, then finalize three lists
//        TODO Try to implement progress bar for time consuming activities
    }
}
