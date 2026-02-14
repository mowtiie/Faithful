package com.mowtiie.faithful.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.mowtiie.faithful.R;
import com.mowtiie.faithful.ui.activities.MainActivity;

public class QuickThoughtWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetID : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("QUICK_THOUGHT", true);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quick_thought);
            views.setOnClickPendingIntent(R.id.widget_quick_thought_icon, pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_quick_thought_text, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetID, views);
        }
    }
}