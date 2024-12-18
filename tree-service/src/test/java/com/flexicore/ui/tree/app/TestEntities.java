package com.flexicore.ui.tree.app;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.flexicore.ui.tree.model.TreeNode;
import com.flexicore.ui.tree.request.TreeCreate;
import com.flexicore.ui.tree.request.TreeNodeCreate;
import com.flexicore.ui.tree.service.TreeNodeService;
import com.wizzdi.dynamic.properties.converter.JsonConverterImplementationHolder;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.DynamicExecutionCreate;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TestEntities {

    @Autowired
    private JsonConverterImplementationHolder jsonConverterImplementationHolder;

    @Autowired
    private SecurityContext adminSecurityContext;
    @Autowired
    private TreeNodeService treeNodeService;
    @Autowired
    private DynamicExecutionService dynamicExecutionService;

    @Bean
    public DynamicExecution dynamicExecution(){
        return dynamicExecutionService.createDynamicExecution(new DynamicExecutionCreate().setName("test"),adminSecurityContext);
    }
    @Bean
    public TreeNode treeNode(DynamicExecution dynamicExecution){
        return treeNodeService.createTreeNode(new TreeNodeCreate().setDynamicExecution(dynamicExecution).setJsonNode(Map.of("test","test")).setName("test"),adminSecurityContext);
    }




}
