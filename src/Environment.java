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
            this.jsonObject = (JSONObject) parser.parse(new FileReader("D://Works//FileSync//environment.json"));
        } catch (IOException | ParseException e) {
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
}
