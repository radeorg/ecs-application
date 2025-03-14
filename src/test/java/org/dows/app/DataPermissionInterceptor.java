//package com.shdy.admin;
//
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.StrUtil;
//import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
//import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.Expression;
//import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.statement.select.PlainSelect;
//import net.sf.jsqlparser.statement.select.Select;
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.session.ResultHandler;
//import org.apache.ibatis.session.RowBounds;
//import org.dows.framework.api.exceptions.BaseException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import java.sql.SQLException;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 数据权限控制
// */
//@Data
//@Component
//@Slf4j
//public class DataPermissionInterceptor implements InnerInterceptor {
//    //    @Autowired
////    private RedissonClient redissonClient;
//    @Value("${data-permission.not-control-tables:''}")
//    public String notControlTables;
//    @Value("${data-permission.base-table:''}")
//    public String baseTable;
//    @Value("${data-permission.not-control-uri:''}")
//    public String notControlUri;
//
//    @Override
//    public boolean willDoQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds,
//                               ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
//        return InnerInterceptor.super.willDoQuery(executor, ms, parameter, rowBounds, resultHandler, boundSql);
//    }
//
//    @Override
//    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
//                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
//        fillAppId();
//        processDp(boundSql);
//    }
//
//    // 填充appId
//    private void fillAppId(){
//
//    }
//
//
//    private void processDp(BoundSql boundSql) {
//        log.debug("数据权限处理……");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (ObjectUtil.isNull(authentication)) {
//            log.debug("数据权限处理, 未登录！");
//            return;
//        }
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        /*LoginUser loginUser = (LoginUser) principal;
//        String username = loginUser.getUsername();
//        // 如果是系统管理员，不需做作权限处理
//        if (SYSTEM_ADMINISTRATOR_ACCOUNT.equals(username)) {
//            log.debug("数据权限处理，当前为管理员，不需要处理数据权限。");
//            return;
//        }
//        String uri = loginUser.getUri();
//        log.debug("数据权限处理，当前uri为：{}", uri);
//        if (notControlUri.contains(uri)) {
//            log.debug("数据权限处理，当前uri，不需要处理数据权限。");
//            return;
//        }
//        String sql = boundSql.getSql();
//        Select select;
//        try {
//            select = (Select) CCJSqlParserUtil.parse(sql);
//        } catch (JSQLParserException e) {
//            throw new RuntimeException(e);
//        }
//        // 系统自动生成的sql，一般都是单表查询，所以这里暂时不考虑复杂的情况
//        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
//        net.sf.jsqlparser.schema.Table table = (net.sf.jsqlparser.schema.Table) plainSelect.getFromItem();
//        String tableName = table.getName();
//        // 排除一些不需要控制的表
//        List<String> notControlTablesList = Arrays.asList(notControlTables.split(","));
//        if (notControlTablesList.contains(tableName.toLowerCase())) {
//            log.debug("数据权限处理，当前表不做权限控制，table is {}", tableName);
//            return;
//        }
//        String userId = loginUser.getUser().getPkId();
//        RBucket<String> bucket = redissonClient.getBucket(OPERATION_COMPANY + userId);
//        String companyId = bucket.get();
//        if (StrUtil.isBlank(companyId)) {
//            throw new BaseException("公司id不存在！");
//        }
//        // 处理SQL语句
//        // 基础表，根据主键进行控制
//        log.debug("数据权限处理，处理之前的sql为: {}", sql);
//        Expression where = plainSelect.getWhere();
//        Expression envCondition;
//        try {
//            if (baseTable.equals(tableName.toLowerCase())) {
//                envCondition = CCJSqlParserUtil.parseCondExpression("PK_ID = " + companyId);
//            } else {
//                envCondition = CCJSqlParserUtil.parseCondExpression("COMPANY_ID = " + companyId);
//            }
//        } catch (JSQLParserException e) {
//            throw new RuntimeException(e);
//        }
//        if (where == null) {
//            plainSelect.setWhere(envCondition);
//        } else {
//            AndExpression andExpression = new AndExpression(where, envCondition);
//            plainSelect.setWhere(andExpression);
//        }
//        sql = plainSelect.toString();
//        log.debug("数据权限处理，处理之后的sql为: {}", sql);
//        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
//        mpBs.sql(sql);*/
//    }
//}