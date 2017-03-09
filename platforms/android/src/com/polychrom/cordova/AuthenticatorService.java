// Copyright (C) 2013 Polychrom Pty Ltd
//
// This program is licensed under the 3-clause "Modified" BSD license,
// see LICENSE file for full definition.

package com.polychrom.cordova;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service
{
    private Authenticator auth;

    @Override
    public void onCreate()
    {
    	auth = new Authenticator(this);
    }

    @Override
    public void onDestroy()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return auth.getIBinder();
    }
}
