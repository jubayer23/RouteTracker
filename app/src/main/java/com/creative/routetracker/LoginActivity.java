package com.creative.routetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.routetracker.Utility.CommonMethods;
import com.creative.routetracker.Utility.DeviceInfoUtils;
import com.creative.routetracker.Utility.RunnTimePermissions;
import com.creative.routetracker.alertbanner.AlertDialogForAnything;
import com.creative.routetracker.appdata.GlobalAppAccess;
import com.creative.routetracker.appdata.MydApplication;
import com.creative.routetracker.model.User;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Gson gson;
    // UI references.
    private Button btn_submit;
    private EditText ed_email, ed_password;
    private TextView tv_register_now, tv_reset_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * If User is already logged-in then redirect user to the home page
         * */
        if (MydApplication.getInstance().getPrefManger().getUserProfile() != null) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
            return;
        }
        /**
         * If User is not logged-in proceed with the login form
         * */
        setContentView(R.layout.activity_login);

        init();

        initializeCacheValue();

        RunnTimePermissions.requestForAllRuntimePermissions(this);
    }

    private void init() {
        gson = new Gson();
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_password = (EditText) findViewById(R.id.ed_password);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
        tv_register_now = (TextView) findViewById(R.id.tv_register_now);
        tv_register_now.setOnClickListener(this);
        tv_reset_password = (TextView) findViewById(R.id.tv_reset_password);
        tv_reset_password.setOnClickListener(this);
    }

    private void initializeCacheValue() {
        ed_email.setText(MydApplication.getInstance().getPrefManger().getEmailCache());
    }

    private void saveCache(String email) {
        MydApplication.getInstance().getPrefManger().setEmailCache(email);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (!RunnTimePermissions.requestForAllRuntimePermissions(this)) {
           return;
        }

        if (!DeviceInfoUtils.isConnectingToInternet(this)) {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "ERROR", "Please connect to a working internet connection!", false);
            return;
        }

        if (!DeviceInfoUtils.isGooglePlayServicesAvailable(this)) {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Warning", "This app need google play service to work properly. Please install it!!", false);
            return;
        }

        if (id == R.id.btn_submit) {
            if (isValidCredentialsProvided()) {

                CommonMethods.hideKeyboardForcely(this, ed_email);
                CommonMethods.hideKeyboardForcely(this, ed_password);

                saveCache(ed_email.getText().toString());

                sendRequestForLogin(GlobalAppAccess.URL_LOGIN, ed_email.getText().toString(), ed_password.getText().toString());
            }


            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }


        if (id == R.id.tv_register_now) {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        }

        if (id == R.id.tv_reset_password) {

            //EmailConnector emailConnector = new EmailConnector(LoginActivity.this);
            //emailConnector.showEmailDialog();

        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private boolean isValidCredentialsProvided() {

        // Store values at the time of the login attempt.
        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();

        // Reset errors.
        ed_email.setError(null);
        ed_password.setError(null);
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(email)) {
            ed_email.setError("Required");
            ed_email.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_email.setError("Invalid");
            ed_email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ed_password.setError("Required");
            ed_password.requestFocus();
            return false;
        }

        return true;
    }

    public void sendRequestForLogin(String url, final String email, final String password) {

        // TODO Auto-generated method stub
        showProgressDialog("Loading..", true, false);

        final StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("DEBUG",response);

                        dismissProgressDialog();


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            int result = jsonObject.getInt("result");

                            if (result == 1) {
                                int id = jsonObject.getInt("userId");
                                String name = jsonObject.getString("name");

                                User user = new User(id, name, email);

                                MydApplication.getInstance().getPrefManger().setUserProfile(user);
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();

                            } else {
                                AlertDialogForAnything.showAlertDialogWhenComplte(LoginActivity.this, "Error", "Wrong login information!", false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dismissProgressDialog();

                AlertDialogForAnything.showAlertDialogWhenComplte(LoginActivity.this, "Error", "Network problem. please try again!", false);

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // TODO Auto-generated method stub
        MydApplication.getInstance().addToRequestQueue(req);
    }
}
