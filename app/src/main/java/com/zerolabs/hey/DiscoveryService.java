package com.zerolabs.hey;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.android.volley.VolleyError;
import com.zerolabs.hey.DiscoveryHelper.WLANP2PDiscovery;
import com.zerolabs.hey.comm.ServerComm;
import com.zerolabs.hey.model.User;

import java.util.List;

public class DiscoveryService extends Service {
/*    public DiscoveryService() {
//        wlanp2PDiscovery = new WLANP2PDiscovery(this);
        wlanp2PDiscovery.initialize();
        wlanp2PDiscovery.automateDiscovery(macListener, 10000);
        serverComm = new ServerComm(service);
    }
*/
    WLANP2PDiscovery wlanp2PDiscovery;
    ServerComm serverComm;
    Service service = this;

    WLANP2PDiscovery.MACListener macListener = new WLANP2PDiscovery.MACListener() {
        @Override
        public void MACReturn(List<String> MACList) {

            if(MACList == null || MACList.isEmpty()) return;

            serverComm.getUsersFromMacAddresses(MACList, new ServerComm.OnGetUsersListener() {
                @Override
                public void onResponse(boolean successful, List<User> retrievedUsers) {
                    if(retrievedUsers != null && successful && retrievedUsers.size()>0) {
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(service)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentTitle("There are friendly people near you")
                                        .setContentText(retrievedUsers.get(0).getUsername());

                        //Vibrator vibrator = (Vibrator)getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                        //vibrator.vibrate(500);

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service);
// Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(LoginActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                        Intent resultIntent = new Intent(service, LoginActivity.class);

                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);

                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(0, mBuilder.build());
                    }

                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    };

    boolean isActive;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isActive) {
            wlanp2PDiscovery = new WLANP2PDiscovery(this);
            wlanp2PDiscovery.initialize();
            wlanp2PDiscovery.automateDiscovery(macListener, 10000);
            serverComm = new ServerComm(service);
            isActive = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onDestroy() {
        wlanp2PDiscovery.terminate();
    }
}
