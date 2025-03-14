//package com.shdy.admin;//package org.dows.rbac.config;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.lang.tree.TreeNode;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.app.api.AppApi;
//import org.dows.app.api.TenantApp;
//import org.dows.app.api.admin.request.SaveAppMenuRequest;
//import org.dows.rbac.api.InitResources;
//import org.dows.rbac.api.RbacApi;
//import org.dows.rbac.api.RbacContext;
//import org.dows.rbac.api.RbacMenu;
//import org.dows.rbac.api.admin.request.SaveRbacRoleRequest;
//import org.dows.rbac.api.annotation.Menu;
//import org.dows.rbac.config.*;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import java.util.*;
//
///**
// * @description: </br>
// * @author: lait.zhang@gmail.com
// * @date: 3/20/2024 10:06 AM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class RbacInitializer implements CommandLineRunner, ApplicationListener<ContextRefreshedEvent> {
//
//    private final RbacScanner rbacScanner;
//
//    private final RbacConfig rbacConfig;
//    private final InitSetting initSetting;
//
//    private final RbacApi rbacApi;
//
//    private final AppApi appApi;
//
//    private final List<InitResources> resources;
//    private final Map<String, List<TreeNode<String>>> appMenus = new HashMap<>();
//    private final Map<String, List<TreeNode<String>>> roleMenus = new HashMap<>();
//
//    @Override
//    public void run(String... args) throws Exception {
//        Boolean initScan = initSetting.isInitScan();
//        log.info("菜单角色接口信息初始化扫描是否开启 {}",initScan);
//        if(initScan){
//            log.info("菜单角色接口信息初始化开始...");
//            // 初始化应用角色信息
//            initRoles();
//            // 初始化应用菜单
//            initMenus();
//            // 初始化应用uri
//            initUris();
//            RbacContext.flag = true;
//            log.info("菜单角色接口信息初始化完成...");
//        }
//    }
//
//    private void initRoles() {
//        List<RoleItem> roleItems = rbacConfig.getRoleItems();
//        List<SaveRbacRoleRequest> list = new ArrayList<>();
//        for (RoleItem roleItem : roleItems) {
//            // 找出每个appId 需要更新的 menu,uri,role
//            List<String> updateItems = rbacConfig.getUpdateItemsByAppIdAndItem(roleItem.getCode(), "role");
//            if (updateItems.contains(roleItem.getCode())) {
//                SaveRbacRoleRequest saveRbacRoleRequest = new SaveRbacRoleRequest();
//                saveRbacRoleRequest.setParentRoleCode(roleItem.getParentCode());
//                saveRbacRoleRequest.setRoleCode(roleItem.getName());
//                saveRbacRoleRequest.setRoleName(roleItem.getName());
//                saveRbacRoleRequest.setAppId(roleItem.getAppId());
//                list.add(saveRbacRoleRequest);
//            }
//        }
//        if(CollectionUtil.isNotEmpty(list)){
//            rbacApi.initAppRole(list);
//        }
//    }
//
//    private void initMenus() {
//        List<MenuItem> menuItems = rbacConfig.getMenuItems();
//        for (MenuItem menuItem : menuItems) {
//            // 找出每个appId 需要更新的 menu,uri,role
//            List<String> updateItems = rbacConfig.getUpdateItemsByAppIdAndItem(menuItem.getCode(), "menu");
//            if (updateItems.contains(menuItem.getCode())) {
//
//                List<TreeNode<String>> treeNodes = appMenus.computeIfAbsent(menuItem.getAppId(), k -> new ArrayList<>());
//                List<TreeNode<String>> roleTreeNodes = roleMenus.computeIfAbsent(menuItem.getRoles() + "-" + menuItem.getAppId(), k -> new ArrayList<>());
//                // 构建根菜单
//                TreeNode<String> rootMenu = new TreeNode<>(menuItem.getCode(), null, menuItem.getName(), 0);
//                Map<String, Object> extra = new HashMap<>();
//                extra.put("path", "/");
//                rootMenu.setExtra(extra);
//                treeNodes.add(rootMenu);
//                roleTreeNodes.add(rootMenu);
//                List<String> scanPackages = menuItem.getScanPackages();
//                for (String scanPackage : scanPackages) {
//                    // 统一构建
//                    buildTree(rbacScanner.scanMenu(scanPackage), menuItem.getCode(), menuItem.getAppId(), menuItem.getRoles() + "-" + menuItem.getAppId());
//                }
//            }
//        }
//        // 保存到数据库
//        List<RbacMenu> allMenus = new ArrayList<>();
//        Set<String> appIds = appMenus.keySet();
//        for (String appId : appIds) {
//            List<RbacMenu> rbacMenus = rbacApi.initAppMenu(appMenus.get(appId), appId);
//            if(CollectionUtil.isNotEmpty(rbacMenus)){
//                allMenus.addAll(rbacMenus);
//            }
//        }
//        List<TenantApp> tenantApps = appApi.getAppId();
//        if (CollectionUtil.isNotEmpty(tenantApps)){
//            for (TenantApp tenantApp : tenantApps) {
//                String appId = tenantApp.getAppId();
//                List<SaveAppMenuRequest> saveAppMenuRequests = new ArrayList<>();
//                for (RbacMenu allMenu : allMenus) {
//                    SaveAppMenuRequest saveAppMenuRequest = new SaveAppMenuRequest();
//                    saveAppMenuRequest.setAppId(appId);
//                    saveAppMenuRequest.setRbacMenuId(allMenu.getRbacMenuId());
//                    saveAppMenuRequest.setPid(allMenu.getPid());
//                    saveAppMenuRequest.setMenuName(allMenu.getName());
//                    saveAppMenuRequest.setMenuCode(allMenu.getCode());
//                    saveAppMenuRequest.setMenuPath(allMenu.getNamePath());
//                    saveAppMenuRequest.setIcon(allMenu.getIcon());
//                    saveAppMenuRequest.setOpenType(allMenu.getOpenType());
//                    saveAppMenuRequest.setTargetUrl(allMenu.getPath());
//                    saveAppMenuRequest.setSorted(allMenu.getSorted());
//                    saveAppMenuRequest.setFrame(allMenu.getIsframe());
//                    saveAppMenuRequest.setIdPath(allMenu.getIdPath());
//                    saveAppMenuRequest.setNamePath(allMenu.getNamePath());
//                    saveAppMenuRequest.setLevel(allMenu.getLevel());
//                    saveAppMenuRequests.add(saveAppMenuRequest);
//                }
//                appApi.saveAppMenus(saveAppMenuRequests);
//            }
//        }else{
//            log.error("tenantApps is null");
//        }
//        roleMenus.forEach((roleCode, menus) -> {
//            String[] split = roleCode.split("-");
//            if (split.length >= 2) {
//                rbacApi.initRoleMenu(menus, split[0], split[1]);
//            }
//        });
//
//    }
//
//    private void initUris() {
//        List<UriItem> uriPackages = rbacConfig.getUriPackages();
//
//        Map<String, List<InitResources>> map = new HashMap<>();
//        if (CollectionUtils.isEmpty(uriPackages)) {
//            map.put("manager", resources);
//        } else {
//            for (UriItem uriItem : uriPackages) {
//                // 找出每个appId 需要更新的 menu,uri,role
//                List<String> updateItems = rbacConfig.getUpdateItemsByAppIdAndItem(uriItem.getCode(), "uri");
//                for (InitResources resource : resources) {
//                    for (String updateItem : updateItems) {
//                        if (resource.getPackageName().startsWith(updateItem)) {
//                            String[] roleCode = uriItem.getRoles().split(",");
//                            for (String code : roleCode) {
//                                List<InitResources> scanResources = map.getOrDefault(code + "-" + uriItem.getAppId(), new ArrayList<>());
//                                scanResources.add(resource);
//                                map.put(code + "-" + uriItem.getAppId(), scanResources);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        // 将资源数据批量添加到数据库
//        map.forEach((k, v) -> {
//            if (!CollectionUtils.isEmpty(v)) {
//                rbacApi.saveResource(v);
//                String[] split = k.split("-");
//                if (split.length >= 2) {
//                    rbacApi.initRoleUri(v, split[0], split[1]);
//                }
//            }
//        });
//
//    }
//
//
//    private void buildTree(Set<Class<?>> menuClasses, Object parent,/*Class<?> parent,*/ String appId, String role) {
//        List<TreeNode<String>> treeNodes = appMenus.get(appId);
//        List<TreeNode<String>> roleTreeNodes = roleMenus.get(role);
//        for (Class<?> menuClass : menuClasses) {
//            Menu menu = menuClass.getAnnotation(Menu.class);
//            TreeNode<String> m = new TreeNode<>(menu.code(), null, menu.name(), 0);
//            Map<String, Object> extra = new HashMap<String, Object>();
//            extra.put("path", menu.path());
//            m.setExtra(extra);
//            if (!menu.parent().equals(Object.class)) {
//                parent = menu.parent();
//            }
//            //appMenus.put(appId,m);
//            if (parent instanceof Class) {
//                // parent不是String类型
//                // 如果是Object.class 则没有上级节点,直接增加到集合,否则取当前节点指向的上级节点
//                if (!menu.parent().getName().equals(Object.class.getName())) {
//                    String code = ((Class<?>) parent).getAnnotation(Menu.class).code();
//                    m.setParentId(code);
//                }
//
//            } else {
//                // parent是String类型
//                m.setParentId((String) parent);
//            }
//
//            treeNodes.add(m);
//            roleTreeNodes.add(m);
//        }
//        roleMenus.put(role, roleTreeNodes);
//    }
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        RbacContext.flag = false;
//    }
//}