package com.http_s.rest;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.Manifest.permission.READ_CONTACTS;

public class Utilities {
    private final Context context;
    private final SqliteCore sqliteCore;
    private final Languages languages;
    private static final int REQUEST_READ_CONTACTS = 444;
    static final String LATITUDE = "LATITUDE";
    static final String LONGITUDE = "LONGITUDE";
    static final String LOCATION_SETTING = "ADD_LOCATION";
    static final String ENCODE_GET_QUERY_STRING = "ENCODE_GET_QUERY_STRING";
    static final String ENCODE_GET_VALUES_SETTING = "ENCODE_GET_VALUES";
    static final String ENCODE_POST_VALUES_SETTING = "ENCODE_POST_VALUES";

    static final String HTTP_URL = "HTTP_URL";
    static final String HTTP_CONNECTION_OPENED = "HTTP_CONNECTION_OPENED";
    static final String HTTP_OUTGOING_HEADER_SENT = "HTTP_OUTGOING_HEADER_SENT";
    static final String HTTP_OUTGOING_HEADER_COUNT_SENT = "HTTP_OUTGOING_HEADER_COUNT_SENT";
    static final String HTTP_OUTGOING_CONTENT_LENGTH = "HTTP_OUTGOING_CONTENT_LENGTH";
    static final String HTTP_OUTGOING_DATA_OPEN_STREAM = "HTTP_OUTGOING_DATA_OPEN_STREAM";
    static final String HTTP_OUTGOING_DATA_STREAM_SENT = "HTTP_OUTGOING_DATA_STREAM_SENT";
    static final String HTTP_OUTGOING_DATA_CLOSE_STREAM = "HTTP_OUTGOING_DATA_CLOSE_STREAM";
    static final String HTTP_OUTGOING_VARIABLES_SENT = "HTTP_OUTGOING_VARIABLES_SENT";
    static final String HTTP_INCOMING_BEGIN = "HTTP_INCOMING_BEGIN";
    static final String HTTP_INCOMING_CODE = "HTTP_INCOMING_CODE";
    static final String HTTP_INCOMING_DESCRIPTION = "HTTP_INCOMING_DESCRIPTION";
    static final String HTTP_INCOMING_BEGIN_READING_CONTENTS = "HTTP_INCOMING_BEGIN_READING_CONTENTS";
    static final String HTTP_INCOMING_END_READING_CONTENTS = "HTTP_INCOMING_END_READING_CONTENTS";
    static final String HTTP_INCOMING_CONTENTS_LENGTH = "HTTP_INCOMING_CONTENTS_LENGTH";
    static final String HTTP_INCOMING_HEADER_COUNT = "HTTP_INCOMING_HEADER_COUNT";


    Utilities(Context inputContext) {
        context = inputContext;
        sqliteCore = new SqliteCore(context);
        languages = new Languages();
    }

    public boolean requestGpsPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
    }

    public void playSound() {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.chime);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String isNetworkAvailable(Context context) {
        String output = "No Connection Available";
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifiInfo.isConnected()) {
                output = "Wi-Fi";
            }

            if (mobileInfo.isConnected()) {
                if (!output.equals("Wi-Fi")) {
                    output = "Mobile";
                } else {
                    output = "Wi-Fi and Mobile";
                }
            }
        } catch (Exception e) {
            output = "No Connection Available";
            e.printStackTrace();
        }
        return output;
    }

    private JSONObject getDeviceInfo() {
        JSONObject output = new JSONObject();
        try {
            output.put("manufacturer", Build.MANUFACTURER);
            output.put("model", Build.MODEL);
            output.put("release", Build.VERSION.RELEASE);
            output.put("sdk", Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
            output.put("base_os", Build.VERSION.BASE_OS);
            output.put("codename", Build.VERSION.CODENAME);
            output.put("sdk_int", Build.VERSION.SDK_INT);
            output.put("security_patch", Build.VERSION.SECURITY_PATCH);
            output.put("device", Build.DEVICE);
            output.put("id", Build.ID);
            output.put("user", Build.USER);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return output;
    }

    public JSONObject appendDeviceInfo(JSONObject inputJsonObject) {
        try {
            inputJsonObject.put("manufacturer", Base64.encodeToString(Build.MANUFACTURER.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("model", Base64.encodeToString(Build.MODEL.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("release", Base64.encodeToString(Build.VERSION.RELEASE.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("sdk", Base64.encodeToString(Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName().getBytes(), Base64.DEFAULT));
            //inputJsonObject.put("base_os", Base64.encodeToString(Build.VERSION.BASE_OS.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("codename", Base64.encodeToString(Build.VERSION.CODENAME.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("sdk_int", Base64.encodeToString(Integer.toString(Build.VERSION.SDK_INT).getBytes(), Base64.DEFAULT));
            //inputJsonObject.put("security_patch", Base64.encodeToString(Build.VERSION.SECURITY_PATCH.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("device", Base64.encodeToString(Build.DEVICE.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("id", Base64.encodeToString(Build.ID.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("user", Base64.encodeToString(Build.USER.getBytes(), Base64.DEFAULT));
            inputJsonObject.put("unique_id", Base64.encodeToString(UUID.randomUUID().toString().getBytes(), Base64.DEFAULT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inputJsonObject;
    }

    public String getWebServiceUrl() {
        return "https://haroldopaulino.com/web_services/papi_quotes/gateway.php";
    }

    public boolean requestToReadContacts(Activity inputActivity) {
        if (context.checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (inputActivity.shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            inputActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            inputActivity.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    public void sendSmsMessage(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        if (message.length() > 160) {//THIS IS THE LIMIT FOR ANY SINGLE TEXT MESSAGE SIZE, OTHERWISE IT HAS TO BE A MULTIPART MESSAGE
            ArrayList<String> parts = sms.divideMessage(message);
            int numParts = parts.size();
            for (String str : parts)
                Log.e("str", str);

            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();
            Intent sentIntent = new Intent();
            Intent deliveryIntent = new Intent();

            for (int i = 0; i < numParts; i++) {
                sentIntents.add(PendingIntent.getBroadcast(context, 0, sentIntent, 0));
                deliveryIntents.add(PendingIntent.getBroadcast(context, 0, deliveryIntent, 0));
            }

            sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
        } else {
            PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            sms.sendTextMessage(phoneNumber, null, message, pi, null);
        }
    }

    public String getLanguageText(String inputElement) {
        return languages.getText(sqliteCore.getSetting("language"), inputElement);
    }

    public String getFileContentFromAssets(String name) {
        try {
            InputStream inputStream = context.getAssets().open(name);
            int streamSize = inputStream.available();
            byte[] buffer = new byte[streamSize];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String removeLastChar(String inputValue) {
        return (inputValue == null || inputValue.length() == 0)
                ? ""
                : (inputValue.substring(0, inputValue.length() - 1));
    }

    public String getHttpResponseTypeDescription(int code) {
        String output = "";
        switch (code) {
            case 202:
                output = "HTTP ACCEPTED";
                break;
            case 502:
                output = "HTTP BAD_GATEWAY";
                break;
            case 405:
                output = "HTTP BAD_METHOD";
                break;
            case 400:
                output = "HTTP BAD_REQUEST";
                break;
            case 408:
                output = "HTTP CLIENT_TIMEOUT";
                break;
            case 409:
                output = "HTTP CONFLICT";
                break;
            case 201:
                output = "HTTP CREATED";
                break;
            case 413:
                output = "HTTP ENTITY_TOO_LARGE";
                break;
            case 403:
                output = "HTTP FORBIDDEN";
                break;
            case 504:
                output = "HTTP GATEWAY_TIMEOUT";
                break;
            case 410:
                output = "HTTP GONE";
                break;
            case 500:
                output = "HTTP INTERNAL_ERROR";
                break;
            case 411:
                output = "HTTP LENGTH_REQUIRED";
                break;
            case 301:
                output = "HTTP MOVED_PERM";
                break;
            case 302:
                output = "HTTP MOVED_TEMP";
                break;
            case 300:
                output = "HTTP MULT_CHOICE";
                break;
            case 406:
                output = "HTTP NOT_ACCEPTABLE";
                break;
            case 203:
                output = "HTTP NOT_AUTHORITATIVE";
                break;
            case 404:
                output = "HTTP NOT_FOUND";
                break;
            case 501:
                output = "HTTP NOT_IMPLEMENTED";
                break;
            case 304:
                output = "HTTP NOT_MODIFIED";
                break;
            case 204:
                output = "HTTP NO_CONTENT";
                break;
            case 200:
                output = "HTTP OK";
                break;
            case 206:
                output = "HTTP PARTIAL";
                break;
            case 402:
                output = "HTTP PAYMENT_REQUIRED";
                break;
            case 412:
                output = "HTTP PRECON_FAILED";
                break;
            case 407:
                output = "HTTP PROXY_AUTH";
                break;
            case 414:
                output = "HTTP REQ_TOO_LONG";
                break;
            case 205:
                output = "HTTP RESET";
                break;
            case 303:
                output = "HTTP SEE_OTHER";
                break;
            //begin deprecated responses
            case 401:
                output = "HTTP UNAUTHORIZED";
                break;
            case 503:
                output = "HTTP UNAVAILABLE";
                break;
            case 415:
                output = "HTTP UNSUPPORTED_TYPE";
                break;
            case 305:
                output = "HTTP USE_PROXY";
                break;
            case 505:
                output = "HTTP VERSION";
                break;
            //end deprecated responses
        }

        return output;
    }

    public Location getGpsLocation() {
        Location location = null;
        try {
            LocationManager locationManager;
            boolean locationRetrieved = false;
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //Getting the location through the NETWORK is much faster and consumes only a fraction of the power and system resources necessary for the task
            //We are favoring this over GPS. Of course, if it doesn't give us the location, then the GPS code below will be executed
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isNetworkEnabled) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return TODO;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        1000 * 10,
                        10, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {

                            }

                            @Override
                            public void onStatusChanged(String s, int i, Bundle bundle) {

                            }

                            @Override
                            public void onProviderEnabled(String s) {

                            }

                            @Override
                            public void onProviderDisabled(String s) {

                            }
                        });

                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    locationRetrieved = true;
                }
            }

            if (!locationRetrieved) {
                //If it could not get the current location through NETWORK, then it tries to get through GPS
                //GPS consumes a large amount of battery and it's quite slow, so we are trying to avoid slow speeds and costly location queries

                // getting GPS status
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        //return TODO;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1000 * 10,
                            10, (LocationListener) context);

                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
            Activity a = (Activity)context;
            locationManager.removeUpdates((LocationListener) a);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private JSONObject getLocationAddress (double Latitude, double Longitude) {
        JSONObject output = new JSONObject();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
            for (int i = 0; i < addresses.get(0).getMaxAddressLineIndex(); i++) {
                try {
                    output.put("address_line_" + i, addresses.get(0).getAddressLine(i));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            output.put("string_representation", addresses.get(0).toString());
            output.put("phone", addresses.get(0).getPhone());
            output.put("postal_code", addresses.get(0).getPostalCode());
            output.put("premises", addresses.get(0).getPremises());
            output.put("sub_locality", addresses.get(0).getSubLocality());
            output.put("sub_thoroughfare", addresses.get(0).getSubThoroughfare());
            output.put("thoroughfare", addresses.get(0).getThoroughfare());
            output.put("url", addresses.get(0).getUrl());
            output.put("county", addresses.get(0).getSubAdminArea());
            output.put("country_code", addresses.get(0).getCountryCode());
            output.put("country_name", addresses.get(0).getCountryName());
            output.put("feature_name", addresses.get(0).getFeatureName());
            output.put("city", addresses.get(0).getLocality());
            output.put("state", addresses.get(0).getAdminArea());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public String getCurrentTimeStamp() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS a");
            return dateFormat.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
