// ============================================================================
// File: /home/flexicore/upload/7c9e9e8c-d31b-4761-8e3d-3f15c7f63a85.js
// Purpose: Send cabinet alert email based on trigger type.
// ============================================================================

function evaluateScript(x) {
    x.logger.info(x.toJson(x));

    var action = x.actions[0];
    var holder = action.executeInvokerRequest.executionParametersHolder;
    var triggerName = x.scenarioTriggers[0].name;
    var poiName = x.scenarioEvent.remote.mappedPOI.name;

    holder.emails = java.util.Set.of(
        "dudi@control-iot.co.il",
        "UriA@rishonlezion.muni.il"
    );

    holder.replyTo = "info@control-iot.co.il";
    holder.from = "info@control-iot.co.il";
    holder.replyToAlias = "Control-IoT Notifactions";
    holder.fromAlias = "Control-IoT Notifactions";
    holder.templateId = "d-901c1f9084164cb5b88516d999103893";

    if (triggerName === "Cabinet - Door Open") {
        holder.additionalProperties = java.util.Map.of(
            "title",
            "התראה התקבלה",
            "email_body",
            "התראת פתיחת דלת התקבלה מהתקן - " + poiName
        );
    }

    if (
        triggerName === "Cabinet - Turned Off" ||
        triggerName === "Cabinet - Turned Off After Midnight"
    ) {
        holder.additionalProperties = java.util.Map.of(
            "title",
            "התראה התקבלה",
            "email_body",
            "התראת כיבוי בשעה חריגה התקבלה מהתקן - " + poiName
        );
    }

    return [action.id];
}
