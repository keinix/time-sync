package io.keinix.timesync.utils;

import android.content.Context;
import android.content.Intent;

public abstract class ShareUtil {

    public static void shareText(Context context, String text){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
}
