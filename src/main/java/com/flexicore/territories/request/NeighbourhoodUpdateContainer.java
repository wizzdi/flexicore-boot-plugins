package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Neighbourhood;

public class NeighbourhoodUpdateContainer extends NeighbourhoodCreationContainer{

   private String id;
   @JsonIgnore
   private Neighbourhood neighbourhood;

    public String getId() {
        return id;
    }

    public NeighbourhoodUpdateContainer setId(String id) {
        this.id = id;
        return this;
    }

    @JsonIgnore
    public Neighbourhood getNeighbourhood() {
        return neighbourhood;
    }

    public NeighbourhoodUpdateContainer setNeighbourhood(Neighbourhood neighbourhood) {
        this.neighbourhood = neighbourhood;
        return this;
    }
}