package seassoon.court;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import practice.ConnectionDB;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.*;

public class MySQLPipeline implements Pipeline {

    protected String JDBC_DRIVER = "com.mysql.jbdc.Driver";
    private String DB_URL = "jdbc:mysql://localhost:3306/court?characterEncoding=utf8&useSSL=false";
    private String USER = "root";
    private String PASS = "123456";
    private String tableName;

    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;

    private ConnectionDB connectionDB;

    public MySQLPipeline(String tableName) {
        connectionDB = new ConnectionDB(DB_URL,USER,PASS);
        this.tableName = tableName;
    }

    public MySQLPipeline(String DB_URL, String USER, String PASS, String tableName) {
        this.DB_URL = DB_URL;
        this.USER = USER;
        this.PASS = PASS;
        connectionDB = new ConnectionDB(DB_URL,USER,PASS);
        this.tableName = tableName;
    }

    public void process(ResultItems resultItems, Task task) {
//        startConnection();

            String[] columns = {"url", "content"};
//        String url = resultItems.get("url");
//        String content = resultItems.get("content");
            String[] paras = {resultItems.get("url"), resultItems.get("content")};

            connectionDB.insert(columns, paras, tableName);


//        String sql = "INSERT INTO "+tableName+" () VALUES (?,?)";
    }

//    protected void startConnection() {
//        try {
//            if(connection==null) {
//                //注册JDBC驱动
//                Class.forName("com.mysql.jdbc.Driver");
//                //打开链接
//                System.out.println("连接数据库...");
//                connection = DriverManager.getConnection(DB_URL, USER, PASS);
//            }
//            else {
//                System.out.println("connection already exists!");
//            }
//        } catch (Exception e) {
//            //处理Class.forName错误
//            e.printStackTrace();
//
//        }
//    }
//
//    protected void endConnection() {
//        try {
//            if (statement != null) statement.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            if (connection != null) connection.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected ResultSet executeQuery(String sqlQuery, String[] parameters) {
//        try {
//            preparedStatement = connection.prepareStatement(sqlQuery);
//            for(int i=1;i<=parameters.length;i++) {
//                preparedStatement.setString(i, parameters[i-1]);
//            }
//            ResultSet rs = preparedStatement.executeQuery();
//            return rs;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//
//    protected boolean execute(String sqlQuery, String[] parameters) {
//        try {
//            preparedStatement = connection.prepareStatement(sqlQuery);
//            for(int i=1;i<=parameters.length;i++) {
//                preparedStatement.setString(i, parameters[i-1]);
//            }
//            boolean r = preparedStatement.execute();
//            return r;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

}



