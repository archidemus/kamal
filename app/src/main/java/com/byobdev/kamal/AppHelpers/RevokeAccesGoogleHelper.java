package com.byobdev.kamal.AppHelpers;

import android.widget.Toast;

import com.byobdev.kamal.LoginActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by Gabriel on 8/7/2017.
 */

public class RevokeAccesGoogleHelper {
    public void revokeAccess(GoogleApiClient mGoogleApiClient) {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }
}
