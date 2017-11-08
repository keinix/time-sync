package io.keinix.timesync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


public class AuthenticatorService extends Service {
    private AccountAuthenticator mAuthenticatorActivity;

    @Override
    public void onCreate() {
        mAuthenticatorActivity = new AccountAuthenticator(this);
        Log.d("FINDME", "service started");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticatorActivity.getIBinder();
    }

}
