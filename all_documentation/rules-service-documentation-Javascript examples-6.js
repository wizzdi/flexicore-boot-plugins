// ============================================================================
// File: /home/flexicore/upload/d081ce13-b6c8-4c2d-b39d-e4477d61b2af.js
// Purpose: Create door-open warning alert.
// ============================================================================

function evaluateScript(x) {
    x.logger.info(x.toJson(x));

    var createAlertAction = null;

    for (var i = 0; i < x.actions.length; i++) {
        if (x.actions[i].executeInvokerRequest.invokerMethodName === "createAlert") {
            createAlertAction = x.actions[i];
            break;
        }
    }

    var holder = createAlertAction.executeInvokerRequest.executionParametersHolder;

    holder.alertLevel = com.wizzdi.alerts.AlertLevel.WARNING;
    holder.alertCategory = "התראות התקנים";
    holder.alertContent =
        "התראת פתיחת דלת התקבלה מהתקן - " +
        x.scenarioEvent.remote.mappedPOI.name;

    holder.relatedType = "com.wizzdi.basic.iot.model.Device";
    holder.relatedId = x.scenarioEvent.remote.id;

    return [createAlertAction.id];
}
