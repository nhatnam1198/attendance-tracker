package com.example.facerecogapp.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facerecogapp.R;
import com.example.facerecogapp.Service.ServiceGenerator;
import com.google.gson.JsonObject;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;

import java.io.Serializable;

public class LoginActivity extends AppCompatActivity {
    private final static String[] SCOPES = {"api://e2da588d-2e4b-4020-80f6-b97c0fe62adf/.default"};
    final static String AUTHORITY = "https://login.microsoftonline.com/b3b9cfc4-0719-45ae-9178-946ebe5ac23f";
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private static final String TAG = MainActivity.class.getSimpleName();
    SharedPreferences sharedPref;
    private ImageView signInButton;
    private String authorizedJwtToken;
    private boolean isRetrived = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        initializeUI();
        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(),
                R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        mSingleAccountApp = application;
                        loadAccount();
                    }
                    @Override
                    public void onError(MsalException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                });
    }
    private void loadAccount() {
        if (mSingleAccountApp == null) {
            return;
        }
        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                if(activeAccount != null){
                    retrieveToken();
                }

            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
//                    performOperationOnSignOut();
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }
    private void retrieveToken() {
        if (mSingleAccountApp == null) {
            return;
        }
        mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
    }
    private Dialog getProgressBarDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setView(R.layout.custom_progress_dialog);
        builder.setCancelable(false);
        Dialog dialog = builder.create();
        return dialog;
    }

    private void initializeUI(){
        signInButton = findViewById(R.id.sign_in_btn);
        //Sign in user
        signInButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (mSingleAccountApp == null) {
                    return;
                }
                mSingleAccountApp.signIn(LoginActivity.this, null, SCOPES, getAuthInteractiveCallback());
                if (mSingleAccountApp == null){
                    return;
                }
                mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
            }
        });
    }
    private AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                Log.d(TAG, "Successfully authenticated");
                Log.d(TAG, "Successfully authenticated");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d(TAG, "Authentication failed: " + exception.toString());
//                displayError(exception);
            }
            @Override
            public void onCancel() {
                /* User canceled the authentication */
                Log.d(TAG, "User cancelled login.");
            }
        };
    }
    private SilentAuthenticationCallback getAuthSilentCallback() {
        Dialog dialog = getProgressBarDialog();
        dialog.show();
        return new SilentAuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d(TAG, "Successfully authenticated");
                //Const.authorizationHeader = authenticationResult.getAuthorizationHeader();
//                authorizationHeader = authenticationResult.getAuthorizationHeader();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.token), authenticationResult.getAuthorizationHeader());
                editor.putString(getString(R.string.email), authenticationResult.getAccount().getUsername());
                editor.apply();
                isRetrived = true;
                dialog.dismiss();
                ServiceGenerator.setAuthorizedTokenHeader(authenticationResult.getAuthorizationHeader());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
            @Override
            public void onError(MsalException exception) {
                Log.d(TAG, "Authentication failed: " + exception.toString());
//                displayError(exception);
                dialog.dismiss();
            }
        };
    }
//    private void callGraphAPI(IAuthenticationResult authenticationResult) {
//
//        final String accessToken = authenticationResult.getAccessToken();
//
//        IGraphServiceClient graphClient =
//                GraphServiceClient
//                        .builder()
//                        .authenticationProvider(new IAuthenticationProvider() {
//                            @Override
//                            public void authenticateRequest(IHttpRequest request) {
//                                Log.d(TAG, "Authenticating request," + request.getRequestUrl());
//                                request.addHeader("Authorization", "Bearer " + accessToken);
//                            }
//                        })
//                        .buildClient();
//        graphClient
//                .me()
//                .drive()
//                .buildRequest()
//                .get(new ICallback<Drive>() {
//                    @Override
//                    public void success(final Drive drive) {
//                        Log.d(TAG, "Found Drive " + drive.id);
//                        displayGraphResult(drive.getRawObject());
//                    }
//
//                    @Override
//                    public void failure(ClientException ex) {
//                        displayError(ex);
//                    }
//                });
//    }

}
