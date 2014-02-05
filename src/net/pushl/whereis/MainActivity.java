package net.pushl.whereis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.aef.registration.Registration;
import com.sonyericsson.extras.liveware.extension.util.Dbg;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ImageButton button = (ImageButton) findViewById(R.id.search_watch_button);
        button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    startSearchingWatch();
                }
        });
    }
    private void startSearchingWatch(){
        Intent intent = new Intent(Control.Intents.CONTROL_START_REQUEST_INTENT);
        intent.putExtra(Control.Intents.EXTRA_AEA_PACKAGE_NAME,getPackageName());

        sendBroadcast(intent,Registration.HOSTAPP_PERMISSION);

        waitForResumeAndVibrate(1000,1000,1);
        
    }

    private static final long WAIT_FOR_RESUME = 10;
    private static final long WAIT_COUNT = 1000 / WAIT_FOR_RESUME;
    private void waitForResumeAndVibrate(final int onDuration, final int offDuration, final int repeats){
        (new Thread(new Runnable() {
            @Override
            public void run() {
                for(int cnt=0;cnt<WAIT_COUNT && !(SharedInfo.getInstance().isResumed && 
                        SharedInfo.getInstance().hostPackageName == null);cnt++){
                    try {
                        Thread.sleep(WAIT_FOR_RESUME);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if(!(SharedInfo.getInstance().isResumed && 
                        SharedInfo.getInstance().hostPackageName == null)){
                    startVibrator(onDuration, offDuration, repeats);
                }
            }
        })).start();
    }
    /**
     * Start repeating vibrator
     *
     * @param onDuration On duration in milliseconds.
     * @param offDuration Off duration in milliseconds.
     * @param repeats The number of repeats of the on/off pattern. Use
     *            {@link Control.Intents#REPEAT_UNTIL_STOP_INTENT} to repeat
     *            until explicitly stopped.
     */
    private void startVibrator(int onDuration, int offDuration, int repeats) {
        if (Dbg.DEBUG) {
            Dbg.v("startVibrator: onDuration: " + onDuration + ", offDuration: " + offDuration
                    + ", repeats: " + repeats);
        }
        Intent intent = new Intent(Control.Intents.CONTROL_VIBRATE_INTENT);
        intent.putExtra(Control.Intents.EXTRA_ON_DURATION, onDuration);
        intent.putExtra(Control.Intents.EXTRA_OFF_DURATION, offDuration);
        intent.putExtra(Control.Intents.EXTRA_REPEATS, repeats);
        sendToHostApp(intent);
    }

    /**
     * Send intent to host application. Adds host application package name and
     * our package name.
     *
     * @param intent The intent to send.
     */
    private void sendToHostApp(final Intent intent) {
        intent.putExtra(Control.Intents.EXTRA_AEA_PACKAGE_NAME, getPackageName());
        intent.setPackage(SharedInfo.getInstance().hostPackageName);
        sendBroadcast(intent, Registration.HOSTAPP_PERMISSION);
    }

}
