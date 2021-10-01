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
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getSourcePath() {
        JSONObject path = (JSONObject) jsonObject.get("PATH");
        return (String) path.get("SOURCE_PATH");
    }

    public String getDestinationPath() {
        JSONObject path = (JSONObject) jsonObject.get("PATH");
        return (String) path.get("DESTINATION_PATH");
    }

    public int getDataMTU() {
        JSONObject path = (JSONObject) jsonObject.get("PARAMETERS");
        return Integer.parseInt((String) path.get("DATA_MTU"));
    }

    public int getFileTransferPort() {
        JSONObject path = (JSONObject) jsonObject.get("COMMUNICATION_POSTS");
        return Integer.parseInt((String) path.get("FILE_TRANSFER_POST"));
    }

    public String getMasterServerAddress() {
        JSONObject path = (JSONObject) jsonObject.get("SERVER_ADDRESSES");
        return (String) path.get("MASTER_SERVER_ADDRESS");
    }

    public String getBackupServerAddress() {
        JSONObject path = (JSONObject) jsonObject.get("SERVER_ADDRESSES");
        return (String) path.get("BACKUP_SERVER_ADDRESS");
    }
}
