import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Environment {
    private JSONObject jsonObject;

    public Environment() {
        JSONParser parser = new JSONParser();
        this.jsonObject = null;
        try {
            this.jsonObject = (JSONObject) parser.parse(new FileReader("D:/Works/FileSync/environment.json"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public String getSourcePath() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("PATH");
        return (String) path.get("SOURCE_PATH");
    }

    public String getDestinationPath() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("PATH");
        return (String) path.get("DESTINATION_PATH");
    }

    public String getTemporaryDestinationPath() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("PATH");
        return (String) path.get("TEMPORARY_DESTINATION_PATH");
    }

    public String getTrashPath() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("PATH");
        return (String) path.get("TRASH_PATH");
    }

    public int getDataMTU() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("PARAMETERS");
        return Integer.parseInt((String) path.get("DATA_MTU"));
    }

    public int getFileTransferPort() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("COMMUNICATION_POSTS");
        return Integer.parseInt((String) path.get("FILE_TRANSFER_POST"));
    }

    public String getMasterServerAddress() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("SERVER_ADDRESSES");
        return (String) path.get("MASTER_SERVER_ADDRESS");
    }

    public String getBackupServerAddress() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("SERVER_ADDRESSES");
        return (String) path.get("BACKUP_SERVER_ADDRESS");
    }

    public int getCommunicationPortMasterToBackup() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("COMMUNICATION_POSTS");
        return Integer.parseInt((String) path.get("MASTER_TO_BACKUP_COMMUNICATION_POST"));
    }

    public int getCommunicationPortBackupToMaster() throws JSONException {
        JSONObject path = (JSONObject) jsonObject.get("COMMUNICATION_POSTS");
        return Integer.parseInt((String) path.get("BACKUP_TO_MASTER_COMMUNICATION_POST"));
    }
}
