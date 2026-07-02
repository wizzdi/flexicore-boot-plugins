// ============================================================================
// File: /home/flexicore/upload/38d09349-f0b4-4f5d-a6a9-1f328d99c4e9.js
// Purpose: Create shelter warning alert based on trigger type.
// ============================================================================

function evaluateScript(x) {
    var action = x.actions[0];
    var holder = action.executeInvokerRequest.executionParametersHolder;
    var triggerName = x.scenarioTriggers[0].name;
    var poiName = x.scenarioEvent.remote.mappedPOI.name;

    holder.alertLevel = com.wizzdi.alerts.AlertLevel.WARNING;
    holder.alertCategory = "התראות התקנים";

    if (triggerName === "ShelterTrigger - Door Lock Open") {
        holder.alertContent =
            "התראת פתיחת מנעול דלת התקבלה מהתקן - " + poiName;
    }

    if (triggerName === "ShelterTrigger - Flood Detect") {
        holder.alertContent =
            "התראת הצפה התקבלה מהתקן - " + poiName;
    }

    if (triggerName === "ShelterTrigger - Magnet Open") {
        holder.alertContent =
            "התראת פתיחת דלת פיזית התקבלה מהתקן - " + poiName;
    }

    if (triggerName === "ShelterTrigger - Motion Detect") {
        holder.alertContent =
            "התראת תנועה התקבלה מהתקן - " + poiName;
    }

    holder.relatedType = "com.wizzdi.basic.iot.model.Device";
    holder.relatedId = x.scenarioEvent.remote.id;

    return [action.id];
}
