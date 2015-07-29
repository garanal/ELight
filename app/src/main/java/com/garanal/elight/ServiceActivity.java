package com.garanal.elight;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * Created by Giovany on 14/03/2015.
 */
public class ServiceActivity extends Service {

    //Create camera object to access flashlight
    private Camera camera;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Access to the camera
        try {
            camera = null;
            camera = Camera.open();
        } catch (Exception e) {
            Toast toast= Toast.makeText(this, R.string.msgAccessError, Toast.LENGTH_SHORT);
            toast.show();
            stopSelf();
            return 0;
        }

        // Gets the camera parameters
        final Camera.Parameters parameters = camera.getParameters();

        //Lists the flash modes
        List<String> flashModes = parameters.getSupportedFlashModes();

        // Establishes the parameter according to the mode accepted by the flash
        if(flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            // Sets the torch parameter
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            // Creates a preview of the cam so the led stills on
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) { }
            });
        } else if(flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            camera.setParameters(parameters);
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) { }
            });
        } else if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            camera.setParameters(parameters);
            try {
                camera.setPreviewTexture(new SurfaceTexture(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) { }
            });
        }

        // Builds the notification
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.elight_on);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(bm)
                .setColor(0xff000000)
                .setContentTitle(getString(R.string.Notification_Tittle))
                .setContentText(getString(R.string.Notification_Text))
                .setSubText(getString(R.string.Notification_Subtext))
                .setTicker(getString(R.string.Notification_Ticker))
                .setOngoing(true); // So the notification can be removed only by the app

        // Creates an intent to go to the main activity when the notification is tapped
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        mBuilder.setContentIntent(resultPendingIntent);

        // Shows the notification
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        // Releases the camera so the light goes off
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        // Removes the notification
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(0);
    }
}
