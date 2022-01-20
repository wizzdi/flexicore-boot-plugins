package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.dynamic.properties.converter.DynamicColumnDefinition;
import com.wizzdi.dynamic.properties.converter.JsonConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.HashMap;
import java.util.Map;

@Entity
public class ScenarioSavableEvent extends SecuredBasic {

    @ManyToOne(targetEntity = ScenarioTrigger.class)
    private ScenarioTrigger scenarioTrigger;
    @Convert(converter = JsonConverter.class)
    @JsonIgnore
    private Map<String,Object> other=new HashMap<>();

    @JsonAnySetter
    public void set(String key, Object val) {
        other.put(key, val);
    }

    @DynamicColumnDefinition
    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    @JsonIgnore
    public Map<String, Object> getOther() {
        return other;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return other;
    }


    public <T extends ScenarioSavableEvent> T setOther(Map<String, Object> other) {
        this.other= other;
        return (T) this;
    }


    @ManyToOne(targetEntity = ScenarioTrigger.class)
    public ScenarioTrigger getScenarioTrigger() {
        return scenarioTrigger;
    }

    public <T extends ScenarioSavableEvent> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
        this.scenarioTrigger = scenarioTrigger;
        return (T) this;
    }
}
