package org.dows.ecs.admin;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.dows.aac.api.AacApi;
import org.dows.aac.api.AacUser;
import org.dows.app.api.AppContext;
import org.dows.rbac.api.RbacContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@RequiredArgsConstructor
@Primary
@Component(value = "fillHandler")
@Slf4j
public class FillHandler implements MetaObjectHandler {
    private final AacApi aacApi;
    private static final Map<String, Class<?>> fieldTypMap = new HashMap<>();


    @PostConstruct
    public void init() {
        fieldTypMap.put("deleted", Boolean.class);
        fieldTypMap.put("dt", Date.class);
        fieldTypMap.put("updateDt", Date.class);
        fieldTypMap.put("name", String.class);
        fieldTypMap.put("label", String.class);
        fieldTypMap.put("title", String.class);
        fieldTypMap.put("appId", String.class);
        fieldTypMap.put("pycode", String.class);
        fieldTypMap.put("accountId", Long.class);
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        String snowflakeIdName = Introspector.decapitalize((metaObject.getOriginalObject().getClass().getSimpleName() + "Id"))
                .replace("Entity", "");
        /*
         *  @notice 如果是主键，对主键填充处理(主键规则：域+Id)
         *  如：
         *  UserInstanceEntity.class -> user_instance_id
         *  AppUrisEntity.class -> app_uris_id
         */
        if (metaObject.hasSetter(snowflakeIdName)) {
            Object o = metaObject.getValue(snowflakeIdName);
            // 主键值是否为空,如果为空则进行填充
            if (o == null) {
                fillStrategy(metaObject, snowflakeIdName, IdWorker.getId());
//                fillStrategy(metaObject, snowflakeIdName, IdCenter.nextId());
            }
        }
        /*
         * 非主键填充
         */
        fillField(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        fillField(metaObject);
    }

    private void fillField(MetaObject metaObject) {
        fieldTypMap.forEach((k, v) -> fillField(metaObject, k, v));
    }

    private void fillField(MetaObject metaObject, String k, Class<?> v) {
        if (metaObject.hasSetter(k)) {
            Object o = metaObject.getValue(k);
            if (o == null && k.equalsIgnoreCase("deleted")) {
                fillStrategy(metaObject, k, false);
            } else if (o == null && k.equalsIgnoreCase("dt")) {
                fillStrategy(metaObject, k, new Date());
            } else if (k.equalsIgnoreCase("appId")) {
                Object appId = metaObject.getValue("appId");
                if (StrUtil.isBlankIfStr(appId)) {
                    appId = AppContext.getAppId();
                }
                this.setFieldValByName(k, appId, metaObject);
            } else if (k.equalsIgnoreCase("accountId")) {
                if (!RbacContext.flag) {
                    return;
                }
                AacUser aacUser = aacApi.getCurrentAccUser();
                this.setFieldValByName(k, aacUser.getAccountId(), metaObject);
            } else if (k.equalsIgnoreCase("pycode")) {
//                String pyf = ClassUtil.getDeclaredField(metaObject.getOriginalObject().getClass(), "pycode")
//                        .getAnnotation(Pinyin.class).value();
//                Object value = metaObject.getValue(pyf);
//                if (value != null) {
//                    try {
////                    String value = AnnotationUtil
////                            .getAnnotationValue(metaObject.getOriginalObject().getClass(), Pinyin.class, k);
//                        setFieldValByName(k, PinyinHelper.getShortPinyin(value.toString()), metaObject);
//                    } catch (PinyinException e) {
//                        log.error(e.getMessage());
//                    }
//                }
            }

        }
    }


}
