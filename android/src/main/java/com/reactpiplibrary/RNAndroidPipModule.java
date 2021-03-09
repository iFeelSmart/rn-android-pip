
package com.reactpiplibrary;

import android.app.PictureInPictureParams;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Rational;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNAndroidPipModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private final ReactApplicationContext reactContext;
    private static final int ASPECT_WIDTH = 3;
    private static final int ASPECT_HEIGHT = 4;
    private boolean isPipSupported = false;
    private boolean isCustomAspectRatioSupported = false;
    private boolean isPipListenerEnabled = false;
    private Rational aspectRatio;
    private static RNAndroidPipModule instance;
    private DeviceEventManagerModule.RCTDeviceEventEmitter mJSModule = null;
    private boolean isInPictureInPictureMode = false;

    public RNAndroidPipModule(ReactApplicationContext reactContext) {
        super(reactContext);
        instance = this;
        this.reactContext = reactContext;
        reactContext.addLifecycleEventListener(this);
        PackageManager pm = reactContext.getPackageManager();
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && pm.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE))) {
            isPipSupported = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                isCustomAspectRatioSupported = true;
                aspectRatio = new Rational(ASPECT_WIDTH, ASPECT_HEIGHT);
            }
        }
    }

    @Override
    public String getName() {
        return "RNAndroidPip";
    }


    /// Pip event handling
    public static void onHostPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig){
        if (instance != null){
            instance.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        }
    }
    void onPictureInPictureModeChanged(boolean _isInPictureInPictureMode, Configuration newConfig) {
        if (mJSModule == null) {
            mJSModule = reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        }
        if (isInPictureInPictureMode != _isInPictureInPictureMode) {
            isInPictureInPictureMode = _isInPictureInPictureMode;
            mJSModule.emit("onPipModeChanged", isInPictureInPictureMode);
        }
    }

    /// Pip methods
    @ReactMethod
    public void enterPictureInPictureMode() {
        if (isPipSupported) {
            if (isCustomAspectRatioSupported) {
                PictureInPictureParams params = new PictureInPictureParams.Builder()
                        .setAspectRatio(this.aspectRatio).build();
                getCurrentActivity().enterPictureInPictureMode(params);
            } else
                getCurrentActivity().enterPictureInPictureMode();
        }
    }

    @ReactMethod
    public void configureAspectRatio(Integer width, Integer height) {
        aspectRatio = new Rational(width, height);
    }

    @ReactMethod
    public void enableAutoPipSwitch() {
        isPipListenerEnabled = true;
    }

    @ReactMethod
    public void disableAutoPipSwitch() {
        isPipListenerEnabled = false;
    }

    /// Lifecycle
    @Override
    public void onHostResume() {
    }

    @Override
    public void onHostPause() {
        if (isPipSupported && isPipListenerEnabled) {
            enterPictureInPictureMode();
        }
    }

    @Override
    public void onHostDestroy() {
    }
}
