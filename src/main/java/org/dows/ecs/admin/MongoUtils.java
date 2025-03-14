package org.dows.ecs.admin;

import cn.hutool.core.collection.CollectionUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class MongoUtils{
    /*@Autowired
    private static MongoConfig mongoConfig;

    @Autowired
    private static SpringInitConfig springInitConfig;*/

    private static MongoClient mongoClient;

    private final static String PASSWORD = "pwd";
    private final static String ROLES = "roles";
    private final static String CREATE_USER = "createUser";

    private static final String DROP_USER = "dropUser";
    private final static String PERMISSION = "readWrite";
    private final static String USERS = "users";
    private final static String USER_INFO = "usersInfo";
 
    //定义一个getMongoClient()方法，用于获取MOngoDB数据库的连接对象
    public static MongoClient initMongoClient(String username, String password, String host, int port){
        /*String username = springInitConfig.getMongodb().getUsername();
        String password = springInitConfig.getMongodb().getPassword();
        String host = mongoConfig.getHost();
        int port = mongoConfig.getPort();*/

        String addr = String.format("mongodb://%s:%s@%s:%d/admin", username, password, host, port);
        mongoClient = MongoClients.create(addr);
        return mongoClient;
    }
    //定义一个getMongoConn()方法，用于实现连接指定的MongoDB数据库
    public static MongoDatabase getMongoConn(String database){
//        MongoClient mongoClient=getMongoClient();
        // 切换到tableName数据库
        MongoDatabase db = mongoClient.getDatabase(database);
        return db;
    }

    public static MongoIterable<String> getDBs(){
//        MongoClient mongoClient=getMongoClient();
        return mongoClient.listDatabaseNames();
    }
    // 查询用户
    public static FindIterable<Document> getUsers(String databaseName){
//        MongoClient mongoClient=getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        return db.getCollection("users").find();
    }

    // 查询某个用户信息
    public static List<Document> getUserInfo(String databaseName,String userName){
//        MongoClient mongoClient=getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        org.bson.Document usersInfo = db.runCommand(new Document(USER_INFO, userName));
        return  (List<Document>) usersInfo.get(USERS);
    }

    // 添加用户
    public static void addUser(String databaseName,String userName,String password,String roles){
//        MongoClient mongoClient=getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(databaseName);

        Document usersInfo = db.runCommand(new Document(USER_INFO, userName));
        List users = (List) usersInfo.get(USERS);
        if (CollectionUtil.isEmpty(users)) {
            // 创建用户
            db.runCommand(new BasicDBObject(CREATE_USER, userName)
                    .append(PASSWORD, password)
                    .append(ROLES, Arrays.asList(roles)));
        }
    }

    // 删除用户

    public static void dropUser(String databaseName,String userName){
//        MongoClient mongoClient=getMongoClient();
        MongoDatabase db = mongoClient.getDatabase(databaseName);
        db.runCommand(new BasicDBObject(DROP_USER, userName));
    }

    // 关闭连接
    public static void close(){
//        MongoClient mongoClient=getMongoClient();
        // 关闭MongoClient连接
        mongoClient.close();
    }
}