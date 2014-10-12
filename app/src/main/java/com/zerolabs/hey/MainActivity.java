package com.zerolabs.hey;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;

import com.zerolabs.hey.comm.gcm.GCMIntentService;
import com.zerolabs.hey.comm.gcm.Hey;


public class MainActivity extends Activity {

    public static String KEY_HEY = "hey";

    private BroadcastReceiver mBroadcastReceiver;
    private MainListFragment mFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = new MainListFragment();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle heyData = intent.getBundleExtra(GCMIntentService.HEY_MESSAGE);
                Log.v(getClass().toString(), "received broadcast");
                Hey hey = new Hey(heyData);
                if(mFragment.hasHeyed(hey.getSender())){
                    Intent meetIntent = new Intent(getApplicationContext(), MeetActivity.class);
                    meetIntent.putExtra(MeetActivity.KEY_PARTNER, heyData);
                    startActivity(meetIntent);
                } else {
                    mFragment.registerIncomingHey(hey.getSender());
                }

            }
        };

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mFragment)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mBroadcastReceiver), new IntentFilter(GCMIntentService.HEY_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onStop();
    }

}
