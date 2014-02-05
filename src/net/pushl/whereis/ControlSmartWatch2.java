package net.pushl.whereis;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;


class ControlSmartWatch2 extends ControlExtension {
    private Context context;

    private Vibrator vib;
    private static final long VIB_TIME = 1000;
    private static final long SCREEN_ON_TIME = 60000;

    ControlSmartWatch2(final String hostAppPackageName,final Context context,Handler handler){
        super(context,hostAppPackageName);
        if(handler == null) throw new IllegalArgumentException("handler == null");
        this.context = context;
        vib = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        //MyExtensionService.log(hostAppPackageName);
    }

    private boolean isLongTouch = false;
    @Override
    public void onTouch(final ControlTouchEvent event){
        if(event.getAction() == Control.Intents.TOUCH_ACTION_PRESS){
            isLongTouch = false;
        }else if(event.getAction() == Control.Intents.TOUCH_ACTION_LONGPRESS){
            isLongTouch = true;
            onLongTouch();
        }else if(event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE){
            if(isLongTouch){
            }else{
                onShortTouch();
            }
        }
    }

    private void playNotification(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this.context,uri);

        AudioManager audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if(volume == 0){
            volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        }
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM,
                volume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        ringtone.setStreamType(AudioManager.STREAM_ALARM);
        ringtone.play();
    }

    private void onLongTouch(){
        // MyExtensionService.log("onLongTouch()");
        makeScreenOn();
        vib.vibrate(VIB_TIME);
        playNotification();
    }

    private void onShortTouch(){
        makeScreenOn();
        vib.vibrate(VIB_TIME);
    }

    //private WakeLock wakeLock;
    private void makeScreenOn(){
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
        wakeLock.acquire(SCREEN_ON_TIME);

    }

    @Override
    public void onDestroy() {
        MyExtensionService.log("onDestory()");
        // this.handler = null;
    }

    @Override
    public void onResume() {
        MyExtensionService.log("onResume()");
        showLayout(R.layout.whereisphone_2,null);
        SharedInfo.getInstance().isResumed = true;
    }

    @Override
    public void onPause(){
        vib.cancel();
        SharedInfo.getInstance().isResumed = false;

    }

    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }

    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
    }
}
