// ============================================================================
// File: /home/flexicore/upload/bd2ff0d7-4993-4265-b0ee-113fd903b3ef.js
// Purpose: Send an external call/event report to Holon API.
// ============================================================================

function evaluateScript(x) {
    x.logger.info(x.toJson(x));
    x.logger.info("Remote => " + x.scenarioEvent.remote.remoteId);

    var url = "https://moked-holon.binaa.co.il/api/external_call.php";
    var bearerToken = "<BEARER_TOKEN>";

    var data = {
        first_name: "בקרה - אוטומטי",
        last_name: "בקרה - אוטומטי",
        mobile_phone: "0526316767",
        event_description:
            "התקבלה התראה מבקר - " +
            x.scenarioEvent.remote.remoteId +
            " בכתובת : " +
            x.scenarioEvent.remote.mappedPOI.address.street.name +
            " " +
            x.scenarioEvent.remote.mappedPOI.address.houseNumber +
            " על תקלה בפנס"
    };

    var request = new java.net.URL(url).openConnection();
    request.setRequestMethod("POST");
    request.setRequestProperty("Content-Type", "application/json");
    request.setRequestProperty("Authorization", "Bearer " + bearerToken);
    request.setDoOutput(true);

    var outputStream = request.getOutputStream();
    var writer = new java.io.OutputStreamWriter(outputStream, "UTF-8");
    writer.write(JSON.stringify(data));
    writer.close();

    var inputStream;

    try {
        inputStream = request.getInputStream();
    } catch (e) {
        inputStream = request.getErrorStream();
    }

    var reader = new java.io.BufferedReader(
        new java.io.InputStreamReader(inputStream)
    );

    var response = "";
    var line;

    while ((line = reader.readLine()) !== null) {
        response += line;
    }

    reader.close();

    var responseData = JSON.parse(response);
    var status = responseData.status;
    var error = responseData.error;
    var responseDataParams = responseData.data;
    var callNumber = responseData.call_number;

    x.logger.info("Data: " + JSON.stringify(responseDataParams));

    if (status) {
        x.logger.info("Success:");
        x.logger.info("Call Number: " + callNumber);
    } else {
        x.logger.error("Error: " + error);
    }

    return null;
}
