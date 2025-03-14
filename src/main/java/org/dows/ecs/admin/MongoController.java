package org.dows.ecs.admin;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoIterable;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Component
@Slf4j
public class MongoController {



//    @PostMapping("/initMongoClient")
//    public void initMongoClient(String username, String password, String host, int port){
//        MongoUtils.initMongoClient(username, password, host, port);
//    }

    @GetMapping("/getDBs")
    public MongoIterable<String> getDBs(String username, String password, String host, int port){
        MongoUtils.initMongoClient(username, password, host, port);
        MongoIterable<String> dBs = MongoUtils.getDBs();
        MongoUtils.close();
        return dBs;
    }

    @GetMapping("/getUsers")
    public FindIterable<Document> getUsers(String username, String password, String host,
                                           int port,String databaseName){
        MongoUtils.initMongoClient(username, password, host, port);
        FindIterable<Document> users = MongoUtils.getUsers(databaseName);
        MongoUtils.close();
        return users;
    }

    @GetMapping("/getUserInfo")
    public List<Document> getUserInfo(String username, String password, String host,
                                      int port,String databaseName,String userName){
        MongoUtils.initMongoClient(username, password, host, port);
        List<Document> userInfo = MongoUtils.getUserInfo(databaseName, userName);
        MongoUtils.close();
        return userInfo;
    }

    // 添加用户
    @PostMapping("/addUser")
    public void addUser(String username, String password, String host,
                        int port,String databaseName,String dbUserName,String dbPassword,String roles){
        MongoUtils.initMongoClient(username, password, host, port);
        MongoUtils.addUser(databaseName,dbUserName,dbPassword,roles);
        MongoUtils.close();
    }

    // 删除用户
    @PostMapping("/dropUser")
    public void dropUser(String username, String password, String host,
                         int port,String databaseName,String userName){
        MongoUtils.initMongoClient(username, password, host, port);
        MongoUtils.dropUser(databaseName,userName);
        MongoUtils.close();
    }

}
