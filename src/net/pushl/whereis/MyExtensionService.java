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

import android.os.Handler;
import com.sonyericsson.extras.liveware.extension.util.ExtensionService;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.DeviceInfoHelper;
import com.sonyericsson.extras.liveware.extension.util.registration.DisplayInfo;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationAdapter;
import com.sonyericsson.extras.liveware.extension.util.registration.RegistrationInformation;

/**
 * The Sample Extension Service handles registration and keeps track of all
 * controls on all accessories.
 */
public class MyExtensionService extends ExtensionService {

    //public static final String EXTENSION_KEY = "com.sonymobile.smartconnect.extension.samplecontrol.key";
    public static final String EXTENSION_KEY = "net.pushl.whereis.key";

    public static final String LOG_TAG = "Whereis";

    public MyExtensionService() {
        super(EXTENSION_KEY);
    }
    public static void log(String s){
        //Log.d(LOG_TAG,s);
    }

    /**
     * {@inheritDoc}
     *
     * @see android.app.Service#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //Log.d(MyExtensionService.LOG_TAG, "SampleControlService: onCreate");
    }

    @Override
    protected RegistrationInformation getRegistrationInformation() {
        return new MyRegistrationInformation(this);
    }

    /*
     * (non-Javadoc)
     * @see com.sonyericsson.extras.liveware.aef.util.ExtensionService#
     * keepRunningWhenConnected()
     */
    @Override
    protected boolean keepRunningWhenConnected() {
        return false;
    }

    @Override
    public ControlExtension createControlExtension(String hostAppPackageName) {
        // First we check if the API level and screen size required for
        // SampleControlSmartWatch2 is supported
        boolean advancedFeaturesSupported = DeviceInfoHelper.isSmartWatch2ApiAndScreenDetected(
                this, hostAppPackageName);
        if (advancedFeaturesSupported) {
            return new ControlSmartWatch2(hostAppPackageName, this, new Handler());
        } else {
            // If not we return an API level 1 control based on screen size
            final int controlSWWidth = ControlSmartWatch.getSupportedControlWidth(this);
            final int controlSWHeight = ControlSmartWatch.getSupportedControlHeight(this);
            final int controlSWHPWidth = ControlSmartWirelessHeadsetPro
                    .getSupportedControlWidth(this);
            final int controlSWHPHeight = ControlSmartWirelessHeadsetPro
                    .getSupportedControlHeight(this);

            for (DeviceInfo device : RegistrationAdapter.getHostApplication(this,
                    hostAppPackageName)
                    .getDevices()) {
                for (DisplayInfo display : device.getDisplays()) {
                    if (display.sizeEquals(controlSWWidth, controlSWHeight)) {
                        return new ControlSmartWatch(hostAppPackageName, this, new Handler());
                    } else if (display.sizeEquals(controlSWHPWidth, controlSWHPHeight)) {
                        return new ControlSmartWirelessHeadsetPro(hostAppPackageName, this,
                                new Handler());
                    }
                }
            }
            throw new IllegalArgumentException("No control for: " + hostAppPackageName);
        }
    }
}
