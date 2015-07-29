package com.garanal.elight;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mopub.mobileads.MoPubView;


public class MainActivity extends ActionBarActivity {

    //Create camera object to access flahslight
    private Camera camera;
    // Boolean to know if there's a flash ligth
    private boolean flashLight;
    // Set boolean flag when service is running
    private boolean isServiceRunning = false;

    private ImageView imageView;
    private TextView textView;

    // Variable para MoPub
    private MoPubView moPubView;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connects to the widgets in the screen
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);

        // Finds out if the phone has a flash light
        Context context = this;
        PackageManager pm = context.getPackageManager();
        if(!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            Toast.makeText(this, R.string.msgNoFlash, Toast.LENGTH_SHORT).show();
            flashLight = false;
            return;
        } else {
            flashLight = true;
        }

        // MoPub
        moPubView = (MoPubView) findViewById(R.id.adview);
        moPubView.setAdUnitId("39ee4509aafb42d397f57681a42f249d");
        moPubView.loadAd();
        //moPubView.setBannerAdListener((MoPubView.BannerAdListener)this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Updates the screen according to the service status
        if (isMyServiceRunning(ServiceActivity.class)) {
            imageView.setImageResource(R.drawable.background_on);
            textView.setText(R.string.Tap_Turn_Off);
//            button.setText(R.string.btApagar);
        } else {
            imageView.setImageResource(R.drawable.background_off);
            textView.setText(R.string.Tap_Turn_On);
//            button.setText(R.string.btEncender);
        }

    }

    public void torchControl(View view) {
        // Returns if there's no flash light
        if(!flashLight) {
            Toast toast= Toast.makeText(this, R.string.msgNoFlash, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // Stops the services if it's running or starts it if it's not
        if (isMyServiceRunning(ServiceActivity.class)){
            // Updates the screen
            imageView.setImageResource(R.drawable.background_off);
            textView.setText(R.string.Tap_Turn_On);

            // Stops the service
            stopService(new Intent(getBaseContext(), ServiceActivity.class));

            // Waits until the service is stopped
            do{} while (isMyServiceRunning(ServiceActivity.class));

            // Updates the widget icon
            Intent offIntent = new Intent(this, WidgetProvider.class);
            offIntent.setAction("Service_Off");
            sendBroadcast(offIntent);
        } else {
            // Updates the screen
            imageView.setImageResource(R.drawable.background_on);
            textView.setText(R.string.Tap_Turn_Off);

            // Starts the service
            startService(new Intent(getBaseContext(), ServiceActivity.class));

            // Sends a broadcast to update the widget icon
            Intent onIntent = new Intent(this, WidgetProvider.class);
            onIntent.setAction("Service_On");
            sendBroadcast(onIntent);
        }
    }

    // To know if the service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    protected void onDestroy() {
        moPubView.destroy();
        super.onDestroy();
    }
}
