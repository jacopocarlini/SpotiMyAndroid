package com.spotimyandroid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotimyandroid.utils.ApplicationSupport;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class LoginActivity  extends AppCompatActivity{
    public static final String CLIENT_ID = "e5f7c755748b463cab7dcf25b83b017d";
    public static final String REDIRECT_URI = "testschema://callback";
    private ApplicationSupport app;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (app==null){
            app = (ApplicationSupport) this.getApplication();
            app.prepare();
        }

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("login");

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                System.out.println(response.getAccessToken());
                app.setToken(response.getAccessToken());
                System.out.println("main");
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
            else System.out.println(response.getError());
        }
        else{
            System.out.println("login fallito");
        }
    }
}
