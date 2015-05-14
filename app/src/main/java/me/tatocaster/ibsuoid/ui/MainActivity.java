package me.tatocaster.ibsuoid.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.json.JSONObject;

import me.tatocaster.ibsuoid.Constants;
import me.tatocaster.ibsuoid.R;
import me.tatocaster.ibsuoid.model.User;
import me.tatocaster.ibsuoid.network.VolleyClient;
import me.tatocaster.ibsuoid.service.TranscriptBroadcastReceiver;
import me.tatocaster.ibsuoid.service.TranscriptFetchService;

/**
 * tatocaster <kutaliatato@gmail.com>
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private Drawer.Result materialDrawer;
    private User mUser;
    public static Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleAlarm();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // display all preferences

//        Map<String,?> keys = prefs.getAll();
//        for(Map.Entry<String,?> entry : keys.entrySet()){
//            Log.d("map values", entry.getKey() + ": " +
//                    entry.getValue().toString());
//        }

        mUser = new User();
        mUser.setEmail("kutaliatato@gmail.com");
        // get display name from prefereces
        mUser.setName(prefs.getString("display_name", ""));
        materialDrawer = initDrawerWithListeners(mUser);

        VolleyClient.getInstance(this).getTranscriptNewMarks(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, null, "11200125", "rocker" // this must change from preferences
        );

    }


    private Drawer.Result initDrawerWithListeners(User user) {

        ProfileDrawerItem profileDraweritem = new ProfileDrawerItem().withName(user.getName()).withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.color.md_black_1000));

        // creating account header
        AccountHeader.Result headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.color.md_red_100)
                .addProfiles(
                        profileDraweritem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        final Drawer.Result result = new Drawer()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(Constants.DRAWER_HOME_ID),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(Constants.DRAWER_SETTINGS_ID)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {

                        switch (drawerItem.getIdentifier()) {
                            case Constants.DRAWER_HOME_ID:
                                onBackPressed();
                                break;
                            case Constants.DRAWER_SETTINGS_ID:
                                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(intent);
                                break;
                        }

                    }
                })
                .build();

        return result;
    }

    @Override
    public void onBackPressed() {
        //handle the back press, close the drawer first and if the drawer is closed close the activity
        if (materialDrawer != null && materialDrawer.isDrawerOpen()) {
            materialDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    public void scheduleAlarm() {

        if (this.isMyServiceRunning(TranscriptFetchService.class)) {
            Log.d("MAIN ACTIVITY", "EXISTS");
            cancelAlarm();
        } else {

            String alarm = Context.ALARM_SERVICE;
            AlarmManager am = (AlarmManager) getSystemService(alarm);

            Intent intent = new Intent("REFRESH_TRANSCRIPT_MARKS");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

            int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            // this will change with preference settings
//            long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            long interval = 60000L;
            long triggerTime = SystemClock.elapsedRealtime() + interval;
            // set alarm
            am.setInexactRepeating(type, triggerTime, interval, pi);
        }

    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), TranscriptBroadcastReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
