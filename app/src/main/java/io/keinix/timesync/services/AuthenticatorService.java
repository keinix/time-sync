package io.keinix.timesync.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.keinix.timesync.utils.AccountAuthenticator;


public class AuthenticatorService extends Service {
    private AccountAuthenticator mAccountAuthenticator;

    @Override
    public void onCreate() {
        mAccountAuthenticator = new AccountAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAccountAuthenticator.getIBinder();
    }
}
