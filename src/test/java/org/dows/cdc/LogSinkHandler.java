//package com.shdy.admin;
//
//import cn.hutool.extra.spring.SpringUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
//import org.dows.log.event.BinLogEvent;
//
//import java.time.LocalDateTime;
//
//@Slf4j
//public class LogSinkHandler extends RichSinkFunction<String> {
//    @Override
//    public void invoke(String value, Context context) throws Exception {
//        log.info("监听到活动数据" + LocalDateTime.now() + value);
//        SpringUtil.publishEvent(new BinLogEvent(value));
//    }
//}
