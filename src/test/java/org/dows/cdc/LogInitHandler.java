//package com.shdy.admin;
//
//import com.ververica.cdc.connectors.mysql.source.MySqlSource;
//import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.json.DecimalFormat;
//import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.json.JsonConverterConfig;
//import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.flink.api.common.eventtime.WatermarkStrategy;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * @description: </br>
// * @author: lait.zhang@gmail.com
// * @date: 4/24/2024 9:59 AM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
//@Slf4j
//public class LogInitHandler {
//
//    public static void init()  {
//
//        Map<String, Object> configs = new HashMap<>();
//        //声明decimal转换类型
//        configs.put(JsonConverterConfig.DECIMAL_FORMAT_CONFIG, DecimalFormat.NUMERIC.name());
//
//        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
//                .hostname("192.168.111.103")
//                .port(13306)
//                .username("root")
//                .password("shdy123!")
//                .databaseList("wes_all")
//                .tableList("wes_all.log_column")
//                .deserializer(new JsonDebeziumDeserializationSchema(false, configs))
//                //.startupOptions(StartupOptions.initial())
//                .serverTimeZone("Asia/Shanghai")
//                .includeSchemaChanges(true)
//                .build();
//        log.info("init data source");
//        // web console
//            /*Configuration configuration = new Configuration();
//            configuration.set(RestOptions.PORT, 8081);*/
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.enableCheckpointing(5000);
//        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "wes-source")
//                .addSink(new LogSinkHandler());
//        try {
//            env.execute();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        log.info("env start");
//    }
//
//
///*
//    public static void initSqlServer(DataSourceProperties dataSourceProperties, List<String> tables) throws Exception {
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        // 设置全局并行度
//        env.setParallelism(1);
//        // 设置时间语义为ProcessingTime
//        env.getConfig().setAutoWatermarkInterval(0);
//        // 每隔60s启动一个检查点
//        env.enableCheckpointing(60000, CheckpointingMode.EXACTLY_ONCE);
//        // checkpoint最小间隔
//        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
//        // checkpoint超时时间
//        env.getCheckpointConfig().setCheckpointTimeout(60000);
//        // 同一时间只允许一个checkpoint
//        // env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
//        // Flink处理程序被cancel后，会保留Checkpoint数据
//        // env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN\_ON\_CANCELLATION);
//
//        String[] array = new String[tables.size()];
//        tables.toArray(array);
//        SourceFunction<String> sqlServerSource = SqlServerSource.<String>builder()
//                .hostname("localhost")
//                .port(1433)
//                .username("SA")
//                .password("")
//                .database("test")
//                .tableList(array)
//                .startupOptions(StartupOptions.initial())
////                .debeziumProperties(getDebeziumProperties())
////                .deserializer(new CustomerDeserializationSchemaSqlserver())
//                .build();
//
//        DataStreamSource<String> dataStreamSource = env.addSource(sqlServerSource, "transaction_log_source");
//        dataStreamSource.print().setParallelism(1);
//        env.execute("sqlserver-cdc-test");
//        log.info("env start");
//    }
//*/
//
//}
//
