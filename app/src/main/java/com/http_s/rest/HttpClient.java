package com.http_s.rest;

import android.app.Activity;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class HttpClient {
    private final Utilities utilities;
    private final SqliteCore sqliteCore;
    private JSONObject responseData;
    private String httpResponse = "";
    private final String webServiceURL;
    private final String requestMethod;
    private String resultHTTPClient = "RUNNING";
    private final JSONArray headerData;
    private final JSONArray variableData;
    private HttpURLConnection httpURLConnection;
    private Future future;
    private ExecutorService executorService;

    public class HttpTask implements Callable {
        public HttpTask() {
        }

        @Override
        public String call() {
            try {
                runHttpProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultHTTPClient;
        }

        private void runHttpProcess() {
            URL object;
            try {
                String
                        queryString = getVariablesQuery(),
                        finalUrl;

                if (requestMethod.equalsIgnoreCase("GET")) {
                    finalUrl = webServiceURL + "?" + queryString;
                } else {
                    finalUrl = webServiceURL;
                }

                object = new URL(finalUrl);
                sqliteCore.createSetting(Utilities.HTTP_URL, finalUrl);

                sqliteCore.createSetting(Utilities.HTTP_CONNECTION_OPENED, utilities.getCurrentTimeStamp());
                httpURLConnection = (HttpURLConnection) object.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setUseCaches(false);

                int headerCount = headerData.length();
                StringBuilder headerSummary = new StringBuilder();
                if (headerCount > 0) {
                    for (int i = 0; i < headerCount; i++) {
                        JSONObject record = headerData.getJSONObject(i);
                        httpURLConnection.setRequestProperty(record.getString("name").trim(), record.getString("value").trim());
                        headerSummary.append(
                                "Name: " + record.getString("name").trim() + "\n" +
                                "Value: " + record.getString("value").trim() + "\n");
                    }
                    sqliteCore.createSetting(Utilities.HTTP_OUTGOING_HEADER_SENT, headerSummary.toString());
                    sqliteCore.createSetting(Utilities.HTTP_OUTGOING_HEADER_COUNT_SENT, Integer.toString(headerCount));
                }

                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.setRequestProperty("Content-Language", "en-US");
                httpURLConnection.setRequestMethod(requestMethod);

                int queryStringSizeInBytes = queryString.getBytes().length;
                String contentLength = Integer.toString(queryStringSizeInBytes) + (queryStringSizeInBytes > 1 ? " bytes" : "byte");
                sqliteCore.createSetting(Utilities.HTTP_OUTGOING_CONTENT_LENGTH, contentLength);

                if (requestMethod.equalsIgnoreCase("POST")) {
                    httpURLConnection.setRequestProperty("Content-Length", contentLength);
                    sqliteCore.createSetting(Utilities.HTTP_OUTGOING_DATA_OPEN_STREAM, utilities.getCurrentTimeStamp());
                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(queryString);
                    sqliteCore.createSetting(Utilities.HTTP_OUTGOING_DATA_STREAM_SENT, utilities.getCurrentTimeStamp());
                    wr.flush();
                    wr.close();
                    sqliteCore.createSetting(Utilities.HTTP_OUTGOING_DATA_CLOSE_STREAM, utilities.getCurrentTimeStamp());
                }

                resultHTTPClient = "SUCCESS";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                resultHTTPClient = "EXCEPTION";
                httpResponse = "Error connecting to server (MalformedURLException)";
            } catch (ProtocolException e) {
                e.printStackTrace();
                resultHTTPClient = "EXCEPTION";
                httpResponse = "Error connecting to server (ProtocolException)";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                resultHTTPClient = "EXCEPTION";
                httpResponse = "Error connecting to server (UnsupportedEncodingException)";
            } catch (IOException e) {
                e.printStackTrace();
                resultHTTPClient = "EXCEPTION";
                httpResponse = "Error connecting to server (IOException)";
            } catch (JSONException e) {
                e.printStackTrace();
                resultHTTPClient = "EXCEPTION";
                httpResponse = "Error parsing JSON content";
            }

            responseData = getResponseData();
            parseHTTPResponse();
        }
    }

    public HttpClient(
            Activity inputActivity,
            String requestMethodInput,
            String urlInput,
            JSONArray headerInput,
            JSONArray variableInput) {
        requestMethod = requestMethodInput;
        webServiceURL = urlInput;
        headerData = headerInput;
        variableData = variableInput;
        utilities = new Utilities(inputActivity);
        sqliteCore = new SqliteCore(inputActivity);
    }

    public void callback(JSONObject responseData) { }

    public void runHttpClient() {
        executorService = Executors.newFixedThreadPool(1);
        HttpTask task = new HttpTask();
        future = executorService.submit(task);
    }

    public void parseHTTPResponse() {
        if (resultHTTPClient.equalsIgnoreCase("SUCCESS")) {
            try {
                callback(responseData);
            } catch(Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private String getVariablesQuery() {
        StringBuilder variablesBuilder = new StringBuilder();
        String streamContents = "";
        try {
            if (sqliteCore.getSetting(Utilities.LOCATION_SETTING).equalsIgnoreCase("YES")) {
                try {
                    variableData
                            .put(new JSONObject()
                                    .put("name", Utilities.LATITUDE)
                                    .put("value", sqliteCore.getSetting(Utilities.LATITUDE)))
                            .put(new JSONObject()
                                    .put("name", Utilities.LONGITUDE)
                                    .put("value", sqliteCore.getSetting(Utilities.LONGITUDE)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int variableCount = variableData.length();
            if (variableCount > 0) {
                String
                        stringName,
                        stringValue;
                for (int i = 0; i < variableCount; i++) {
                    JSONObject record = variableData.getJSONObject(i);
                    stringName = record.getString("name").trim();
                    stringValue = record.getString("value").trim();
                    if (requestMethod.equalsIgnoreCase("GET")) {
                        if (sqliteCore.getSetting(Utilities.ENCODE_GET_VALUES_SETTING).equalsIgnoreCase("YES")) {
                            stringValue = new String(Base64.encode(stringValue.getBytes(), Base64.DEFAULT));
                        }
                    } else {
                        if (sqliteCore.getSetting(Utilities.ENCODE_POST_VALUES_SETTING).equalsIgnoreCase("YES")) {
                            stringValue = new String(Base64.encode(stringValue.getBytes(), Base64.DEFAULT));
                        }
                    }

                    variablesBuilder.append(stringName).append("=").append(stringValue).append("&");
                }
                streamContents = utilities.removeLastChar(variablesBuilder.toString());

                if (requestMethod.equalsIgnoreCase("GET")) {
                    if (sqliteCore.getSetting(Utilities.ENCODE_GET_QUERY_STRING).equalsIgnoreCase("YES")) {
                        streamContents = new String(Base64.encode(streamContents.getBytes(), Base64.DEFAULT));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sqliteCore.createSetting(Utilities.HTTP_OUTGOING_VARIABLES_SENT, streamContents);
        return streamContents;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private JSONObject getResponseData() {
        JSONObject
                headers = new JSONObject(),
                summary = new JSONObject(),
                output = new JSONObject();
        StringBuilder messageBuilder = new StringBuilder();
        sqliteCore.createSetting(Utilities.HTTP_INCOMING_BEGIN, utilities.getCurrentTimeStamp());

        try {
            sqliteCore.createSetting(Utilities.HTTP_INCOMING_CODE, Integer.toString(httpURLConnection.getResponseCode()));
            sqliteCore.createSetting(Utilities.HTTP_INCOMING_DESCRIPTION, utilities.getHttpResponseTypeDescription(httpURLConnection.getResponseCode()));

            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                resultHTTPClient = "SUCCESS";
                sqliteCore.createSetting(Utilities.HTTP_INCOMING_BEGIN_READING_CONTENTS, utilities.getCurrentTimeStamp());
                BufferedReader bufferedReader = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
                }
                String line;
                while (true) {
                    assert bufferedReader != null;
                    if ((line = bufferedReader.readLine()) == null) break;
                    messageBuilder.append(line);
                }
                bufferedReader.close();
                httpResponse = messageBuilder.toString();
                sqliteCore.createSetting(Utilities.HTTP_INCOMING_END_READING_CONTENTS, utilities.getCurrentTimeStamp());

                int httpResponseSizeInBytes = httpResponse.getBytes().length;
                String contentLength = Integer.toString(httpResponseSizeInBytes) + (httpResponseSizeInBytes > 1 ? " bytes" : "byte");
                sqliteCore.createSetting(Utilities.HTTP_INCOMING_CONTENTS_LENGTH, contentLength);
            } else {
                resultHTTPClient = "EXCEPTION";
                httpResponse = "Error connecting to server (Negative Response)";
            }
            output
                    .put("response_code", httpURLConnection.getResponseCode())
                    .put("response_code_description", utilities.getHttpResponseTypeDescription(httpURLConnection.getResponseCode()))
                    .put("response_code_message", httpURLConnection.getResponseMessage())
                    .put("response_message", httpResponse);

            Map<String, List<String>> map = httpURLConnection.getHeaderFields();
            int headerCount = 0;
            StringBuilder headerValues;
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                headerCount++;
                String headerKey = entry.getKey();
                headerKey = headerKey == null ? "." : headerKey;

                List<String> headerValuesList = entry.getValue();
                Iterator<String> iterator = headerValuesList.iterator();
                headerValues = new StringBuilder();
                if (iterator.hasNext()) {
                    headerValues.append(iterator.next());

                    while (iterator.hasNext()) {
                        headerValues
                            .append(", ")
                            .append(iterator.next());
                    }
                }

                headers.put(headerKey, headerValues.toString());
            }
            sqliteCore.createSetting(Utilities.HTTP_INCOMING_HEADER_COUNT, Integer.toString(headerCount));
            output.put("headers", headers);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
    
    public void cancel() {
        future.cancel(true);
        executorService.shutdown();
    }
}