package au.edu.unimelb.eresearch.happypets.ui.fragments;


import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import au.edu.unimelb.eresearch.happypets.BuildConfig;
import au.edu.unimelb.eresearch.happypets.Constants;
import au.edu.unimelb.eresearch.happypets.R;
import au.edu.unimelb.eresearch.happypets.utils.Alert;
import au.edu.unimelb.eresearch.happypets.utils.Api;

/**
 * About fragment - shows some information of the app
 */
public class AboutFragment extends BaseFragment {
    private static final String TAG = "AboutFragment";

    private TextView tvContact;
    private TextView tvDescription;
    private TextView tvDevelopedBy;
    private TextView tvVersion;

    public AboutFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        initViews(view);
        retrieveAppInfo();

        return view;
    }

    private void initViews(View view) {
        tvContact = view.findViewById(R.id.tv_contact);
        tvDescription = view.findViewById(R.id.tv_description);
        tvDevelopedBy = view.findViewById(R.id.tv_developed_by);
        tvVersion = view.findViewById(R.id.tv_version);

        tvVersion.setText("(Version: " + BuildConfig.VERSION_NAME + ")");
    }

    private void retrieveAppInfo() {
        Api.shared.getAppInfo(new Api.ApiCallback() {
            @Override
            public void pass(JSONObject result) {
                Log.d(TAG, "Retrieved app info.");
                mActivity.runOnUiThread(() -> {
                    try {
                        tvContact.setText("Contact us: " + result.getString("contact"));
                        tvDescription.setText(result.getString("description"));
                        tvDevelopedBy.setText(result.getString("developedBy"));
                    } catch (JSONException e) {
                        Log.d(TAG, "Failed to get app info from API response: " + e.getMessage());
                    }
                });
            }

            @Override
            public void fail(Exception e, String description) {
                Log.d(TAG, "Failed to retrieve app info: " + e.getMessage());
                Alert.alert(getFActivity(), Constants.ALERT_TITLE_ERROR_OOPS,
                        description, Constants.ALERT_BTN_OK);
            }
        });
    }

}
