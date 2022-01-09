package com.flexicore.rules;

import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.JsFunction;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.flexicore.rules.model.ScenarioTrigger;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.flexicore.rules.request.DataSourceCreate;
import com.flexicore.rules.request.JsFunctionCreate;
import com.flexicore.rules.request.ScenarioActionCreate;
import com.flexicore.rules.request.ScenarioCreate;
import com.flexicore.rules.request.ScenarioTriggerCreate;
import com.flexicore.rules.request.ScenarioTriggerTypeCreate;
import com.flexicore.rules.service.DataSourceService;
import com.flexicore.rules.service.JsFunctionService;
import com.flexicore.rules.service.ScenarioActionService;
import com.flexicore.rules.service.ScenarioService;
import com.flexicore.rules.service.ScenarioTriggerService;
import com.flexicore.rules.service.ScenarioTriggerTypeService;
import com.flexicore.security.SecurityContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Autowired private DataSourceService dataSourceService;

  @Autowired private ScenarioActionService scenarioActionService;

  @Autowired private ScenarioTriggerService scenarioTriggerService;

  @Autowired private JsFunctionService jsFunctionService;

  @Autowired private ScenarioTriggerTypeService scenarioTriggerTypeService;

  @Autowired private ScenarioService scenarioService;

  @Autowired
  @Qualifier("adminSecurityContext")
  private SecurityContextBase securityContext;

  @Bean
  public DataSource dataSource() {
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
  public JsFunction jsFunction() {
    JsFunctionCreate jsFunctionCreate = new JsFunctionCreate();
    return jsFunctionService.createJsFunction(jsFunctionCreate, securityContext);
  }

  @Bean
  public ScenarioTriggerType scenarioTriggerType() {
    ScenarioTriggerTypeCreate scenarioTriggerTypeCreate = new ScenarioTriggerTypeCreate();
    return scenarioTriggerTypeService.createScenarioTriggerType(
        scenarioTriggerTypeCreate, securityContext);
  }

  @Bean
  public Scenario scenario() {
    ScenarioCreate scenarioCreate = new ScenarioCreate();
    return scenarioService.createScenario(scenarioCreate, securityContext);
  }
}
