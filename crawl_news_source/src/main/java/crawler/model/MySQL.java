package crawler.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MySQL {
    // MySQL 8.0 以上版本 - JDBC 驅動名及資料庫 URL
    private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String DB_URL = "";
    private String username = "";
    private String password = "";

    public MySQL() {
        Map<String,String> dbConfig = getDBConfig();
        this.DB_URL = "jdbc:mysql://"+dbConfig.get("host")+":"+dbConfig.get("port")+"/"+dbConfig.get("dbName")+"?useUnicode=true&characterEncoding=utf8";
        this.username = dbConfig.get("username");
        this.password = dbConfig.get("password");
        System.out.println(this.DB_URL);
    }

    public List<Map<String, Object>> select(String sql){
        Connection conn = null;
        Statement stmt = null;
        List<Map<String, Object>> sqlResult = new ArrayList<>();
        try{
            // 註冊 JDBC 驅動
            Class.forName(this.JDBC_DRIVER);

            // 開啟連結
            System.out.println("connecting DB...");
            conn = DriverManager.getConnection(this.DB_URL,this.username,this.password);

            // 執行查詢
            System.out.println(" 範例化Statement物件...");
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData rm = rs.getMetaData();
            int cnum = rm.getColumnCount();
            while(rs.next())
            {
                Map<String, Object> sqlMap = new HashMap<>();
                for(int i=1; i<=cnum; i++)
                {
                    sqlMap.put(rm.getColumnName(i), rs.getObject(i));
                    System.out.println(rm.getColumnName(i)+":"+rs.getObject(i)+" ");
                }
                sqlResult.add(sqlMap);
            }
            // 完成後關閉
            rs.close();
            stmt.close();
            conn.close();
        }catch(SQLException se){
            // 處理 JDBC 錯誤
            se.printStackTrace();
            LoggerTool.infoMsg("excutError","Handle JDBC Error: " + se);
        }catch(Exception e){
            // 處理 Class.forName 錯誤
            LoggerTool.infoMsg("excutError","Handle Class.forName Error: " + e);
            e.printStackTrace();
        }finally{
            // 關閉資源
            try{
                if(stmt!=null) stmt.close();
            }catch(SQLException se2){
            }// 什麼都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }
        }
        System.out.println("connecting DB end");
        return sqlResult;
    }

    public int update(String sql){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = -1;

        try {
            // 註冊 JDBC 驅動
            Class.forName(this.JDBC_DRIVER);

            // 開啟連結
            System.out.println("connecting DB...");
            conn = DriverManager.getConnection(this.DB_URL,this.username,this.password);
            // 執行查詢
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();

            //返回的结果集中包含主键,注意：主键还可以是UUID,
            //复合主键等,所以这里不是直接返回一个整型
            rs = ps.getGeneratedKeys();
            if(rs.next()) {
                id = rs.getInt(1);
            }
            // 完成後關閉
            rs.close();
            ps.close();
            conn.close();
        }catch(SQLException se){
            // 處理 JDBC 錯誤
            se.printStackTrace();
            LoggerTool.infoMsg("excutError","Handle Class.forName Error: " + se+ " SQL: "+sql);
        }catch(Exception e){
            // 處理 Class.forName 錯誤
            e.printStackTrace();
            LoggerTool.infoMsg("excutError","Handle Class.forName Error: " + e+ " SQL: "+sql);
        }finally{
            // 關閉資源
            try{
                if(ps!=null) ps.close();
            }catch(SQLException se2){
                LoggerTool.infoMsg("excutError","Handle SQLException Error: " + se2);
            }// 什麼都不做
            try{
                if(conn!=null) conn.close();
            }catch(SQLException se){
                se.printStackTrace();
                LoggerTool.infoMsg("excutError","Handle SQLException Error: " + se);
            }
        }
        System.out.println("connecting DB end");
        return id;
    }

    public Map<String,String> getDBConfig() {
        Properties properties = new Properties();
        String configFile = "Config.properties";
        try {
            properties.load(new FileInputStream(configFile));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            LoggerTool.infoMsg("excutError","Handle FileNotFoundException Error: " + ex);
            return null;
        } catch (IOException ex) {
            LoggerTool.infoMsg("excutError","Handle IOException Error: " + ex);
            ex.printStackTrace();
            return null;
        }

        // 第二個參數為預設值，如果沒取到值的時候回傳預設值
        String host = properties.getProperty("db_host", "jdbc:mysql://localhost/default");
        String username = properties.getProperty("db_username");
        String password = properties.getProperty("db_password", "");
        String dbName = properties.getProperty("db_dbName", "");
        String port = properties.getProperty("db_port", "");
        Map<String,String> sql_con = new HashMap(){{
            put("host",host);
            put("username",username);
            put("password",password);
            put("dbName",dbName);
            put("port",port);
        }};
        return sql_con;
    }
}
