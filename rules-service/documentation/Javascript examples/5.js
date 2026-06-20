// ============================================================================
// File: /home/flexicore/upload/118eca2b-b78e-42fd-9e76-c68d6161fd32.js
// Purpose: Send shelter alert email based on trigger type.
// ============================================================================

function evaluateScript(x) {
    x.logger.info(x.toJson(x));

    var action = x.actions[0];
    var holder = action.executeInvokerRequest.executionParametersHolder;
    var triggerName = x.scenarioTriggers[0].name;
    var poiName = x.scenarioEvent.remote.mappedPOI.name;

    holder.emails = java.util.Set.of(
        "dudi@control-iot.co.il",
        "Ncnir@jerusalem.muni.il",
        "Mkherum@jerusalem.muni.il"
    );

    holder.replyTo = "info@control-iot.co.il";
    holder.from = "info@control-iot.co.il";
    holder.replyToAlias = "Control-IoT Notifactions";
    holder.fromAlias = "Control-IoT Notifactions";
    holder.templateId = "d-901c1f9084164cb5b88516d999103893";

    if (triggerName === "ShelterTrigger - Door Lock Open") {
        holder.additionalProperties = java.util.Map.of(
            "title",
            "התראה התקבלה",
            "email_body",
            "התראת פתיחת מנעול דלת התקבלה מהתקן - " + poiName
        );
    }

    if (triggerName === "ShelterTrigger - Flood Detect") {
        holder.additionalProperties = java.util.Map.of(
            "title",
            "התראה התקבלה",
            "email_body",
            "התראת הצפה התקבלה מהתקן - " + poiName
        );
    }

    if (triggerName === "ShelterTrigger - Magnet Open") {
        holder.additionalProperties = java.util.Map.of(
            "title",
            "התראה התקבלה",
            "email_body",
            "התראת פתיחת דלת פיזית התקבלה מהתקן - " + poiName
        );
    }

    if (triggerName === "ShelterTrigger - Motion Detect") {
        holder.additionalProperties = java.util.Map.of(
            "title",
            "התראה התקבלה",
            "email_body",
            "התראת תנועה התקבלה מהתקן - " + poiName
        );
    }

    return [action.id];
}
