package com.flexicore.rules.events;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.nosql.BaseclassNoSQL;

import java.util.*;
import java.util.stream.Collectors;

public class ScenarioSavableEvent extends BaseclassNoSQL {

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String DATE_CREATED = "dateCreated";
    public static final String NAME = "name";
    public static final String SCENARIO_EVENT_ID = "scenarioEventId";
    private static final Set<String> staticFields=new HashSet<>(Arrays.asList(ID,TYPE,DATE_CREATED,NAME,SCENARIO_EVENT_ID));
    private Map<String, Object> more = new HashMap<>();
    private String scenarioEventId;

    public ScenarioSavableEvent() {
    }

    public ScenarioSavableEvent(Map<String, Object> all) {
        setId((String) all.get(ID));
        setType((String) all.get(TYPE));
        setDateCreated((Date) all.get(DATE_CREATED));
        setName((String) all.get(NAME));
        scenarioEventId = (String) all.get(SCENARIO_EVENT_ID);
        this.more = all.entrySet().stream().filter(f->!staticFields.contains(f.getKey())).collect(Collectors.toMap(f->f.getKey(),f->f.getValue()));
    }

    @JsonIgnore
    public Map<String,Object> getAll(){
        Map<String,Object> all=new HashMap<>(get());
        all.put(ID,getId());
        all.put(TYPE,getType());
        all.put(DATE_CREATED,getDateCreated());
        all.put(NAME,getName());
        all.put(SCENARIO_EVENT_ID,this.scenarioEventId);
        return all;

    }

    @JsonAnyGetter
    public Map<String, Object> get() {
        return more;
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        more.put(key, value);
    }

    @JsonIgnore
    public Map<String, Object> getMore() {
        return more;
    }

    public <T extends ScenarioSavableEvent> T setMore(Map<String, Object> more) {
        this.more = more;
        return (T) this;
    }

    public String getScenarioEventId() {
        return scenarioEventId;
    }

    public <T extends ScenarioSavableEvent> T setScenarioEventId(String scenarioEventId) {
        this.scenarioEventId = scenarioEventId;
        return (T) this;
    }
}
