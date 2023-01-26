package com.mordechay.shabbatassociation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class SecretCodeReceiverToMangerActivity extends BroadcastReceiver {
    public static final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";
    public static final String TAG = "secretCodeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == null){
            Log.e(TAG, "Null action");
        }else if (intent.getAction().equals(SECRET_CODE_ACTION)) {
            Log.i(TAG, "Receive secret code intent");
            Intent i = new Intent(context, mangerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
