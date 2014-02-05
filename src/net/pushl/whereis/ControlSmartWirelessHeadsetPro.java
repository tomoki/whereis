/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

/**
 * The sample control for Smart Wireless Headset pro handles the control on the
 * accessory. This class exists in one instance for every supported host
 * application that we have registered to.
 */
class ControlSmartWirelessHeadsetPro extends ControlExtension {

    private Context context;

    private Vibrator vib;
    private static final long VIB_TIME = 1000;
    private static final long SCREEN_ON_TIME = 60000;

    public ControlSmartWirelessHeadsetPro(final String hostAppPackageName, final Context context,
            Handler handler) {
        super(context, hostAppPackageName);
        if (handler == null)
            throw new IllegalArgumentException("handler == null");
        this.context = context;
        vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //MyExtensionService.log(hostAppPackageName);
    }

    private boolean isLongTouch = false;

    @Override
    public void onTouch(final ControlTouchEvent event) {
        if (event.getAction() == Control.Intents.TOUCH_ACTION_PRESS) {
            isLongTouch = false;
        } else if (event.getAction() == Control.Intents.TOUCH_ACTION_LONGPRESS) {
            isLongTouch = true;
            onLongTouch();
        } else if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE) {
            if (isLongTouch) {
            } else {
                onShortTouch();
            }
        }
    }

    private void playNotification() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(this.context, uri);

        AudioManager audioManager = (AudioManager) this.context
                .getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        if (volume == 0) {
            volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        }
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume,
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        ringtone.setStreamType(AudioManager.STREAM_ALARM);
        ringtone.play();
    }

    private void onLongTouch() {
        // MyExtensionService.log("onLongTouch()");
        makeScreenOn();
        vib.vibrate(VIB_TIME);
        playNotification();
    }

    private void onShortTouch() {
        makeScreenOn();
        vib.vibrate(VIB_TIME);
    }

    // private WakeLock wakeLock;
    private void makeScreenOn() {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
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
        showLayout(R.layout.whereisphone_2, null);
        SharedInfo.getInstance().isResumed = true;
    }

    @Override
    public void onPause() {
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
