// ============================================================================
// File: /home/flexicore/upload/213d63ac-c792-4788-8956-4035ca365729.js
// Purpose: Create a warning alert for a device/version test event.
// ============================================================================

function evaluateScript(x) {
    x.getLogger().info("this is a test");

    var createAlertAction;

    createAlertAction.executeInvokerRequest.executionParametersHolder.alertLevel =
        com.wizzdi.alerts.AlertLevel.WARNING;

    createAlertAction.executeInvokerRequest.executionParametersHolder.alertCategory =
        "התראות התקנים";

    createAlertAction.executeInvokerRequest.executionParametersHolder.alertContent =
        "בדיקה ההתקבלה מהתקן - " +
        x.scenarioEvent.remote.mappedPOI.name +
        " עבור גרסה - " +
        x.scenarioEvent.remote.version;

    createAlertAction.executeInvokerRequest.executionParametersHolder.relatedType =
        "com.wizzdi.basic.iot.model.Device";

    createAlertAction.executeInvokerRequest.executionParametersHolder.relatedId =
        x.scenarioEvent.remote.id;

    return [createAlertAction.id];
}
