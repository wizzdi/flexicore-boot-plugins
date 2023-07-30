package com.flexicore.ui.component.request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asaf on 12/07/2017.
 */
public class UIComponentsRegistrationContainer {

  private List<UIComponentRegistrationContainer> componentsToRegister= new ArrayList<>();

    public List<UIComponentRegistrationContainer> getComponentsToRegister() {
        return componentsToRegister;
    }

    public void setComponentsToRegister(List<UIComponentRegistrationContainer> componentsToRegister) {
        this.componentsToRegister = componentsToRegister;
    }
}
