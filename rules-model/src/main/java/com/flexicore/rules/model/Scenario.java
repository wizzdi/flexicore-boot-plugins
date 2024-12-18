package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.file.model.FileResource;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Scenario extends Baseclass {

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource evaluatingJSCode;

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource logFileResource;

    @Lob
    private String scenarioHint;
    @JsonIgnore
    @OneToMany(targetEntity = ScenarioToTrigger.class, mappedBy = "scenario")
    private List<ScenarioToTrigger> scenarioToTriggers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(targetEntity = ScenarioToAction.class, mappedBy = "scenario")
    private List<ScenarioToAction> scenarioToActions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(targetEntity = ScenarioToDataSource.class, mappedBy = "scenario")
    private List<ScenarioToDataSource> scenarioToDataSource= new ArrayList<>();

    /**
     * @return evaluatingJSCode
     */
    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getEvaluatingJSCode() {
        return this.evaluatingJSCode;
    }

    /**
     * @param evaluatingJSCode evaluatingJSCode to set
     * @return Scenario
     */
    public <T extends Scenario> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
        this.evaluatingJSCode = evaluatingJSCode;
        return (T) this;
    }

    /**
     * @return logFileResource
     */
    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getLogFileResource() {
        return this.logFileResource;
    }

    /**
     * @param logFileResource logFileResource to set
     * @return Scenario
     */
    public <T extends Scenario> T setLogFileResource(FileResource logFileResource) {
        this.logFileResource = logFileResource;
        return (T) this;
    }

    /**
     * @return scenarioHint
     */
    @Lob
    public String getScenarioHint() {
        return this.scenarioHint;
    }

    /**
     * @param scenarioHint scenarioHint to set
     * @return Scenario
     */
    public <T extends Scenario> T setScenarioHint(String scenarioHint) {
        this.scenarioHint = scenarioHint;
        return (T) this;
    }
    @JsonIgnore
    @OneToMany(targetEntity = ScenarioToTrigger.class, mappedBy = "scenario")
    public List<ScenarioToTrigger> getScenarioToTriggers() {
        return scenarioToTriggers;
    }

    public <T extends Scenario> T setScenarioToTriggers(
            List<ScenarioToTrigger> scenarioToTriggers) {
        this.scenarioToTriggers = scenarioToTriggers;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = ScenarioToAction.class, mappedBy = "scenario")
    public List<ScenarioToAction> getScenarioToActions() {
        return scenarioToActions;
    }

    public <T extends Scenario> T setScenarioToActions(
            List<ScenarioToAction> scenarioToActions) {
        this.scenarioToActions = scenarioToActions;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = ScenarioToDataSource.class, mappedBy = "scenario")
    public List<ScenarioToDataSource> getScenarioToDataSource() {
        return scenarioToDataSource;
    }

    public <T extends Scenario> T setScenarioToDataSource(List<ScenarioToDataSource> scenarioToDataSource) {
        this.scenarioToDataSource = scenarioToDataSource;
        return (T) this;
    }
}
