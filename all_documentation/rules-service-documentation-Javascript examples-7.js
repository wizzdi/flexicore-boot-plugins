// ============================================================================
// File: /home/flexicore/upload/37704323-efc1-4805-aa14-0a772ea7c906.js
// Purpose: Create version-update alert and update device state values.
// ============================================================================

function evaluateScript(x) {
    var createAlertAction = null;
var changeStateAction = null;

for (var i = 0; i < x.actions.length; i++) {
if (x.actions[i].executeInvokerRequest.invokerMethodName === "createAlert") {
    createAlertAction = x.actions[i];
}

if (x.actions[i].executeInvokerRequest.invokerMethodName === "changeState") {
changeStateAction = x.actions[i];
}
}

var alertHolder =
createAlertAction.executeInvokerRequest.executionParametersHolder;

alertHolder.alertLevel = com.wizzdi.alerts.AlertLevel.WARNING;
alertHolder.alertCategory = "התראות התקנים";
alertHolder.alertContent =
"התראת עדכון גרסה התקבלה מהתקן - " +
x.scenarioEvent.remote.mappedPOI.name +
" עבור גרסה - " +
x.scenarioEvent.remote.version;

alertHolder.relatedType = "com.wizzdi.basic.iot.model.Device";
alertHolder.relatedId = x.scenarioEvent.remote.id;

var stateHolder =
changeStateAction.executeInvokerRequest.executionParametersHolder;

stateHolder.deviceFilter.remoteIds =
java.util.Set.of(x.scenarioEvent.remote.remoteId);

stateHolder.setValue("DimOnOff", true);
stateHolder.setValue("DimLevel", 100);
stateHolder.setValue("SystemFailureLevel", 85);
stateHolder.setValue("ConnTimeOut", 1000);
stateHolder.setValue("GnssTimeout", 500);
stateHolder.setValue("KeepAlivePeriod", 300);

return [changeStateAction.id, createAlertAction.id];
}
