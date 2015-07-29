package com.garanal.elight;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Created by Giovany on 15/03/2015.
 */
public class WidgetProvider extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch a broadcast to this very same class (WidgetProvider)
            Intent intent = new Intent(context, WidgetProvider.class);
            intent.setAction("Service"); // Adds an action to identify the broadcast
            // Creates a pending intent to send the broadcast
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            //Toast.makeText(context, "Se creó el PendingIntent", Toast.LENGTH_SHORT).show();
            // Get the layout for the App Widget and attach an on-click listener to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            //Toast.makeText(context, "Se activó el setOnclickPendingIntent", Toast.LENGTH_SHORT).show();

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            //Toast.makeText(context, "Se actualiza el widget", Toast.LENGTH_SHORT).show();
        }
    }

    @Override // This callback receives the broadcasts sent to this very same class (WidgetProvider)
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        // Prueba
        //Toast.makeText(context, "Se recibió el broadcast: " + intent.getAction().toString(), Toast.LENGTH_SHORT).show();

        // Identifies the broadcast
        if(intent.getAction().equals("Service")) {
            // Creates an intent to start the service
            Intent serviceIntent = new Intent(context, ServiceActivity.class);
            // Verifies the status of the service to start it or to stop it
            if(isMyServiceRunning(ServiceActivity.class, context)) {
                // Stops the service
                context.stopService(serviceIntent);
                // Establish the image for the widget
                views.setImageViewResource(R.id.widget, R.drawable.elight_off);
                // Prueba
                //Toast.makeText(context, "Se detuvo el servicio desde el widget", Toast.LENGTH_SHORT).show();
            } else {
                // Starts the service
                context.startService(serviceIntent);
                // Establish the image for the widget
                views.setImageViewResource(R.id.widget, R.drawable.elight_on);
                // Prueba
                //Toast.makeText(context, "Se inició el servicio desde el widget", Toast.LENGTH_SHORT).show();
            }
        } else if(intent.getAction().equals("Service_On")) {
            // Establish the image for the widget
            views.setImageViewResource(R.id.widget, R.drawable.elight_on);
            //Toast.makeText(context, "Se inició el servicio desde el app", Toast.LENGTH_SHORT).show();
        } else if(intent.getAction().equals("Service_Off")) {
            // Establish the image for the widget
            views.setImageViewResource(R.id.widget, R.drawable.elight_off);
            //Toast.makeText(context, "Se detuvo el servicio desde el app", Toast.LENGTH_SHORT).show();
        }

        // Updates the widget according to established above
        appWidgetManager.updateAppWidget(new ComponentName(context, WidgetProvider.class), views);
    }

    // To know if the service is running
    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
