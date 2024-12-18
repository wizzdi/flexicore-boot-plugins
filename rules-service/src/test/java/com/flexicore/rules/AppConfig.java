package com.flexicore.rules;

import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.DataSourceCreate;
import com.flexicore.rules.request.JSFunctionCreate;
import com.flexicore.rules.request.ScenarioActionCreate;
import com.flexicore.rules.request.ScenarioCreate;
import com.flexicore.rules.request.ScenarioTriggerCreate;
import com.flexicore.rules.request.ScenarioTriggerTypeCreate;
import com.flexicore.rules.service.DataSourceService;
import com.flexicore.rules.service.JSFunctionService;
import com.flexicore.rules.service.ScenarioActionService;
import com.flexicore.rules.service.ScenarioService;
import com.flexicore.rules.service.ScenarioTriggerService;
import com.flexicore.rules.service.ScenarioTriggerTypeService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Autowired private DataSourceService dataSourceService;

  @Autowired private ScenarioActionService scenarioActionService;

  @Autowired private ScenarioTriggerService scenarioTriggerService;

  @Autowired private ScenarioTriggerTypeService scenarioTriggerTypeService;

  @Autowired private ScenarioService scenarioService;

  @Autowired private JSFunctionService jSFunctionService;

  @Autowired
  @Qualifier("adminSecurityContext")
  private SecurityContext securityContext;

  @Bean
  @Qualifier("scenarioDataSource")
  public DataSource scenarioDataSource() {
    DataSourceCreate dataSourceCreate = new DataSourceCreate();
    return dataSourceService.createDataSource(dataSourceCreate, securityContext);
  }

  @Bean
  public ScenarioAction scenarioAction() {
    ScenarioActionCreate scenarioActionCreate = new ScenarioActionCreate();
    return scenarioActionService.createScenarioAction(scenarioActionCreate, securityContext);
  }

  @Bean
  public ScenarioTrigger scenarioTrigger() {
    ScenarioTriggerCreate scenarioTriggerCreate = new ScenarioTriggerCreate();
    return scenarioTriggerService.createScenarioTrigger(scenarioTriggerCreate, securityContext);
  }

  @Bean
  public ScenarioTriggerType scenarioTriggerType() {
    ScenarioTriggerTypeCreate scenarioTriggerTypeCreate = new ScenarioTriggerTypeCreate()
            .setEventCanonicalName("x.y.custom");
    return scenarioTriggerTypeService.createScenarioTriggerType(
        scenarioTriggerTypeCreate, securityContext);
  }

  @Bean
  public Scenario scenario() {
    ScenarioCreate scenarioCreate = new ScenarioCreate();
    return scenarioService.createScenario(scenarioCreate, securityContext);
  }

  @Bean
  public JSFunction jSFunction() {
    JSFunctionCreate jSFunctionCreate = new JSFunctionCreate();
    return jSFunctionService.createJSFunction(jSFunctionCreate, securityContext);
  }
}
