import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import java.util.Stack;

public class DBConnection {

    private Connection connection;

    public DBConnection() {
        this.connection = null;
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:data/file_db.sqlite");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public boolean setupDatabase() throws SQLException {
        String query = "create table if not exists file_info(" +
                "    file_id               integer" +
                "    primary key           autoincrement," +
                "    file_name             varchar(200)," +
                "    source_full_path      varchar(500)," +
                "    destination_full_path varchar(500)," +
                "    updated_at            timestamp," +
                "    hash                  varchar(200)," +
                "    is_active             integer default 1," +
                "    is_altered            integer default 0," +
                "    is_new                int     default 1" +
                ");";
        Statement statement = connection.createStatement();
        statement.execute(query);
        int entryCount = statement.executeQuery("select count(file_id) as count from file_info;").getInt("count");
        return entryCount > 0;
    }

    public Stack<JSONObject> executeSelectQuery(String query) throws SQLException, JSONException {
        Statement statement = connection.createStatement();
        ResultSet queryResult = statement.executeQuery(query);
        Stack<JSONObject> returnData = new Stack<JSONObject>();
        while (queryResult.next()) {
            JSONObject dataObject = new JSONObject();
            dataObject.put("is_select", true);
            dataObject.put("file_id", queryResult.getInt("file_id"));
            dataObject.put("file_name", queryResult.getString("file_name"));
            dataObject.put("source_full_path", queryResult.getString("source_full_path"));
            dataObject.put("destination_full_path", queryResult.getString("destination_full_path"));
            dataObject.put("updated_at", queryResult.getDate("updated_at").toString());
            dataObject.put("hash", queryResult.getString("hash"));
            dataObject.put("is_active", queryResult.getBoolean("is_active"));
            dataObject.put("is_altered", queryResult.getBoolean("is_altered"));
            dataObject.put("is_new", queryResult.getBoolean("is_new"));
            returnData.push(dataObject);
        }
        return returnData;
    }

    public Stack<JSONObject> executeSelectQueryMinimal(String query) throws SQLException, JSONException {
        Statement statement = connection.createStatement();
        ResultSet queryResult = statement.executeQuery(query);
        Stack<JSONObject> returnData = new Stack<JSONObject>();
        while (queryResult.next()) {
            JSONObject dataObject = new JSONObject();
            dataObject.put("file_path", queryResult.getString("source_full_path"));
            dataObject.put("file_hash", queryResult.getString("hash"));
            returnData.push(dataObject);
        }
        return returnData;
    }

    public Stack<JSONObject> executeOtherQuery(String query) throws JSONException {
        Statement statement;
        JSONObject dataObject = new JSONObject();
        dataObject.put("is_select", false);
        try {
            statement = connection.createStatement();
            statement.execute(query);
            dataObject.put("query_status", "1");
        } catch (SQLException | JSONException e) {
            e.printStackTrace();
            dataObject.put("query_status", "0");
        }

        Stack<JSONObject> returnData = new Stack<JSONObject>();
        returnData.push(dataObject);
        return returnData;
    }

    public Object insertDataEntry(String name, String source, String destination, String fileHash) throws JSONException {
        String sqlQuery = "insert into file_info (file_name, source_full_path, destination_full_path, updated_at, hash, is_active) values (" +
                "'" + name + "','" + source + "','" + destination + "',datetime(strftime('%s','now'), 'unixepoch', 'localtime'),'" + fileHash + "',1 );";
        Stack<JSONObject> result = this.executeOtherQuery(sqlQuery);
        return result.get(0).get("query_status").equals("1");
    }

    public Stack<JSONObject> listAllEntries() throws SQLException, JSONException {
        String sqlQuery = "select * from file_info order by source_full_path ASC";
        return this.executeSelectQueryMinimal(sqlQuery);
    }

    public Stack<JSONObject> listAllEntries(int isActive) throws SQLException, JSONException {
        String sqlQuery = "select * from file_info where is_active=" + isActive + " order by source_full_path ASC";
        return this.executeSelectQueryMinimal(sqlQuery);
    }
}
