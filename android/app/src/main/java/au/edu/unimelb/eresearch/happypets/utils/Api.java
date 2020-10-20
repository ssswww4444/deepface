package au.edu.unimelb.eresearch.happypets.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;

import au.edu.unimelb.eresearch.happypets.BuildConfig;
import au.edu.unimelb.eresearch.happypets.Constants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Api {
    public static Api shared = new Api();

    // API routes
    private static final String API_PATH = "/api";
    private static final String ROUTE_APP_INFO = "/get-app-info";
    private static final String ROUTE_BREED_LIST = "/get-breed-info";
    private static final String ROUTE_FEEDBACK = "/feedback";
    private static final String ROUTE_PREDICTION = "/predict";

    private OkHttpClient client = new OkHttpClient();

    /**
     * Create URL to API endpoint
     * @param path API path
     * @return URL to API endpoint
     */
    private String createUrl(String path) {
        return BuildConfig.API_URL + API_PATH + path;
    }

    /**
     * HTTP GET function
     * @param path path to API endpoint
     * @param callback callback to handle API response
     */
    private void get(String path, ApiCallback callback) {
        Request request = new Request.Builder().url(createUrl(path)).build();
        client.newCall(request).enqueue(createHttpCallback(callback));
    }

    /**
     * HTTP POST function
     * @param path path to API endpoint
     * @param body body for POST
     * @param callback callback to handle API response
     */
    private void post(String path, JSONObject body, ApiCallback callback) {
        RequestBody reqBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), body.toString());
        Request request = new Request.Builder().url(createUrl(path)).post(reqBody).build();
        client.newCall(request).enqueue(createHttpCallback(callback));
    }

    /**
     * Creates OKHttpCallback that utilizes the passed ApiCallback
     * @param callback callback that handles response of the HTTP method
     * @return OKHttpCallback that utilizes the passed ApiCallback
     */
    private Callback createHttpCallback(ApiCallback callback) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e instanceof ConnectException) {
                    callback.fail(e, Constants.ALERT_MSG_ERROR_NETWORK);
                } else {
                    callback.fail(e, Constants.ALERT_MSG_ERROR_DEFAULT);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject result = new JSONObject(response.body().string());
                        callback.pass(result);
                    } catch (JSONException e) {
                        callback.fail(e, e.getLocalizedMessage());
                    }
                } else {
                    int statusCode = response.code();
                    callback.fail(new Exception(response.message()), response.message());
                }
            }
        };
    }

    public void getAppInfo(ApiCallback callback) {
        get(ROUTE_APP_INFO, callback);
    }

    public void getBreedList(ApiCallback callback) {
        get(ROUTE_BREED_LIST, callback);
    }

    public void getPrediction(JSONObject body, ApiCallback callback) {
        post(ROUTE_PREDICTION, body, callback);
    }

    public void postFeedback(JSONObject body, ApiCallback callback) {
        post(ROUTE_FEEDBACK, body, callback);
    }

    public interface ApiCallback {
        void pass(JSONObject result);
        void fail(Exception e, String description);
    }
}
