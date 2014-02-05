package net.pushl.whereis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.sonyericsson.extras.liveware.aef.control.Control;

/**
 * The extension receiver receives the extension intents and starts the
 * extension service when it arrives.
 */
public class ExtensionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        //Log.d(.LOG_TAG, "onReceive: " + intent.getAction());
        //MyExtensionService.log("onReceive: " + intent.getAction());
        


        String s = intent.getStringExtra(Control.Intents.EXTRA_AHA_PACKAGE_NAME);
        if(s != null || s != SharedInfo.getInstance().hostPackageName){
            SharedInfo.getInstance().hostPackageName = s;
        }
        intent.setClass(context, MyExtensionService.class);
        context.startService(intent);
    }
}
