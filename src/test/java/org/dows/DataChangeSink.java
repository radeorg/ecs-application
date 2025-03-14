package org.dows;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 自定义sink 交由spring管理
 * 处理变更数据
 **/
@Slf4j
public class DataChangeSink extends RichSinkFunction<String> {

    private static final long serialVersionUID = -74375380912179188L;


    /**
     * 在open()方法中动态注入Spring容器的类
     * 在启动SpringBoot项目是加载了Spring容器，其他地方可以使用@Autowired获取Spring容器中的类；
     * 但是Flink启动的项目中，默认启动了多线程执行相关代码，导致在其他线程无法获取Spring容器，
     * 只有在Spring所在的线程才能使用@Autowired，故在Flink自定义的Sink的open()方法中初始化Spring容器
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public void invoke(String dataChangeInfo, Context context) throws IOException {
        log.info("收到变更原始数据：{}", dataChangeInfo);
//        Path path = Path.of("d:/demo.txt");
        FileWriter fileWriter = new FileWriter("d:/demo.txt", true);
        fileWriter.write(dataChangeInfo);
        fileWriter.close();
//        Files.write(path,dataChangeInfo.getBytes(StandardCharsets.UTF_8));
        // TODO 开始处理你的数据吧
    }
}