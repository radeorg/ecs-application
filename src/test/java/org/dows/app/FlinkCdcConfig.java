//package com.shdy.admin;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.framework.cdc.CdcConfiguration;
//import org.dows.framework.cdc.CdcInitializer;
//import org.dows.framework.datasource.DatasourceProperties;
//import org.dows.log.mysql.entity.LogTableEntity;
//import org.dows.log.mysql.repository.LogTableRepository;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * @description: SQL server CDC变更监听器
// * @author: lait.zhang@gmail.com
// * @date: 7/2/2024 5:20 PM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
//@RequiredArgsConstructor
//@Configuration
//@Slf4j
//public class FlinkCdcConfig implements ApplicationRunner, Serializable {
//
//    private final LogTableRepository logTableRepository;
//    private final Environment environment;
//    private final CdcConfiguration cdcConfiguration;
//
//
//    @Bean
//    CdcInitializer cdcInitializer() {
//        return new CdcInitializer();
//    }
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        log.info("开启Flink CDC获取变更数据......");
//        String appName = environment.getProperty("spring.application.name");
//        List<LogTableEntity> wes = logTableRepository.lambdaQuery()
//                .eq(LogTableEntity::getAppId, "wes")
//                .list();
//        List<String> tables = wes.stream().map(LogTableEntity::getTableName).toList();
//
//        DatasourceProperties datasourceProperties =
//                cdcConfiguration.getDatasourceContext().getDatasourceProperties("acre-mssql");
//        datasourceProperties.addTables(tables);
//
//        DatasourceProperties mdatasourceProperties =
//                cdcConfiguration.getDatasourceContext().getDatasourceProperties("acre-mysql");
//
//        mdatasourceProperties.addTables(tables);
//        try {
//            CdcInitializer.init(cdcConfiguration);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
////        DebeziumSourceFunction<String> dataChangeInfoMySqlSource = buildDataChangeSource();
////        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
////        // 设置全局并行度
////        env.setParallelism(1);
////        // 设置时间语义为ProcessingTime
////        env.getConfig().setAutoWatermarkInterval(0);
////        // 每隔60s启动一个检查点
////        env.enableCheckpointing(60000, CheckpointingMode.EXACTLY_ONCE);
////        // checkpoint最小间隔
////        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
////        // checkpoint超时时间
////        env.getCheckpointConfig().setCheckpointTimeout(60000);
////        // 同一时间只允许一个checkpoint
////        // env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
////        // Flink处理程序被cancel后，会保留Checkpoint数据
////        //   env.getCheckpointConfig().setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
////
////        env.addSource(dataChangeInfoMySqlSource, "acre-source")
////                .addSink(workOrderSinkHandler);
////        // web console
////        /*org.apache.flink.configuration.Configuration configuration = new org.apache.flink.configuration.Configuration();
////        configuration.set(RestOptions.PORT, 8081);*/
////        env.execute("acre-sqlserver-cdc");
//    }
//
//    /**
//     * 构造CDC数据源
//     */
//    /*private DebeziumSourceFunction<String> buildDataChangeSource() {
//        CdcSettings cdcSettings = cdcConfiguration.get("acre");
//        String tableList = cdcSettings.getTableList();
//        String[] tables = tableList.replace(" ", "").split(",");
//        Properties properties = new Properties();
//        properties.setProperty("characterEncoding", "UTF-8");
//        properties.setProperty("characterSetResult", "UTF-8");
//        // 自定义格式，可选
//        properties.put("sqlserverDebeziumConverter.format.datetime", "yyyy-MM-dd HH:mm:ss");
//        properties.put("sqlserverDebeziumConverter.format.date", "yyyy-MM-dd");
//        properties.put("sqlserverDebeziumConverter.format.time", "HH:mm:ss");
//
//        return SqlServerSource.<String>builder()
//                .hostname(cdcSettings.getHost())
//                .port(Integer.parseInt(cdcSettings.getPort()))
//                .database(cdcSettings.getDatabase())
//
//                .tableList(tables)
//                .username(cdcSettings.getUsername())
//                .password(cdcSettings.getPassword())
//                .debeziumProperties(properties)
//                *//*
//     *initial初始化快照,即全量导入后增量导入(检测更新数据写入)
//     * latest:只进行增量导入(不读取历史变化)
//     *//*
//                .startupOptions(StartupOptions.latest())
//                .deserializer(new JsonDebeziumDeserializationSchema())
//                .build();
//    }*/
//}
//
