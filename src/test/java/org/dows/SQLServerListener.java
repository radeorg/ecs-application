//package org.dows;
//
//import com.ververica.cdc.connectors.base.options.StartupOptions;
//import com.ververica.cdc.connectors.sqlserver.source.SqlServerSourceBuilder;
//import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.flink.api.common.eventtime.WatermarkStrategy;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.io.Serializable;
//import java.util.Properties;
//
///**
// * SQL server CDC变更监听器
// **/
////@EnableAsync
////@Configurable
//@Component
//@Slf4j
//public class SQLServerListener implements ApplicationRunner, Serializable {
//
//    /**
//     * CDC数据源配置
//     */
//    @Value("${CDC.DataSource.host}")
//    private String host;
//    @Value("${CDC.DataSource.port}")
//    private String port;
//    @Value("${CDC.DataSource.database}")
//    private String database;
//    @Value("${CDC.DataSource.tableList}")
//    private String tableList;
//    @Value("${CDC.DataSource.username}")
//    private String username;
//    @Value("${CDC.DataSource.password}")
//    private String password;
//
//
//    public void run(ApplicationArguments args) throws Exception {
//
////        CompletableFuture.runAsync(() -> {
//
//            Properties properties = new Properties();
//            properties.put("encrypt", true);
//            properties.put("trustServerCertificate", true);
//            SqlServerSourceBuilder.SqlServerIncrementalSource<String> sqlServerSource =
//                    new SqlServerSourceBuilder<String>()
//                            .hostname("192.168.111.103")
//                            .port(11433)
////                        .databaseList("acre;encrypt=false;trustServerCertificate=false")
//                            .databaseList("acre")
//                            .tableList("dbo.work_order")
//                            .username("sa")
//                            .password("shdy123!")
//                            .debeziumProperties(properties)
//                            .deserializer(new JsonDebeziumDeserializationSchema())
//                            .startupOptions(StartupOptions.initial())
//                            .build();
//
//            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//            // enable checkpoint
//            env.enableCheckpointing(3000);
//
//            // set the source parallelism to 2
//            env.fromSource(sqlServerSource, WatermarkStrategy.noWatermarks(), "SqlServerIncrementalSource")
////                .setParallelism(2)
//                    .addSink(new DataChangeSink())
//                    //.print()
//                    .setParallelism(1);
//
//            try {
//                env.execute();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
////        });
//
//
////        DebeziumSourceFunction<String> dataChangeInfoMySqlSource = bean.buildDataChangeSource(bean1);
////        DataStream<String> streamSource = env
////                .addSource(dataChangeInfoMySqlSource, "SQLServer-source")
////                .setParallelism(1);
////        streamSource.addSink(new DataChangeSink());
//
//            /*SourceFunction<DataChangeInfo> sourceFunction = SqlServerSource.<DataChangeInfo>builder()
//                    .hostname("192.168.111.103")
//                    .port(11433)
//                    .database("acre")
//                    .tableList("dbo.work_order")
//                    .username("sa")
//                    .startupOptions(StartupOptions.latest())
//                    .password("shdy123!")
//                    .deserializer(new SqlServerDeserialization())
//                    .build();
//
//            env.addSource(sourceFunction)
//                    .filter(Objects::nonNull)
//                    .addSink(dataChangeSink)
//                    .setParallelism(1);*/
//
//
//    }
//
//
//    /**
//     * 构造CDC数据源
//     */
////    public DebeziumSourceFunction<String> buildDataChangeSource(DataSourceProperties dataSourceProperties) {
////        ClassLoader classLoader = dataSourceProperties.getClassLoader();
////        String url = dataSourceProperties.getUrl();
////        System.out.println("========================="+classLoader);
////        String[] tables = tableList.replace(" ", "").split(",");
////        MySqlSource
////        return SqlServerSource.<String>builder()
////                .hostname(host)
////                .port(Integer.parseInt(port))
////                .database(database) // monitor sqlserver database
////                .tableList(tables) // monitor products table
////                .username(username)
////                .password(password)
////                /*
////                 *initial初始化快照,即全量导入后增量导入(检测更新数据写入)
////                 * latest:只进行增量导入(不读取历史变化)
////                 */
////                .startupOptions(StartupOptions.latest())
////                .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
////                .build();
////    }
//}