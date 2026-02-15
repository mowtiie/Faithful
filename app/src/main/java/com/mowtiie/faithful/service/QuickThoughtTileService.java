package com.mowtiie.faithful.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.service.quicksettings.TileService;

import com.mowtiie.faithful.ui.activities.MainActivity;

public class QuickThoughtTileService extends TileService {

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("QUICK_THOUGHT", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(pendingIntent);
        } else {
            startActivityAndCollapse(intent);
        }
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }
}