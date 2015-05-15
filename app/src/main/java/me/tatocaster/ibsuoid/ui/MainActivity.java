package me.tatocaster.ibsuoid.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
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
    private Toast mToast;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // display all preferences
       /* Map<String, ?> keys = prefs.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(TAG, entry.getKey() + ": " + entry.getValue().toString());
        }*/

        mUser = new User();
        mUser.setEmail("kutaliatato@gmail.com");
        // get display name from prefereces
        mUser.setName(prefs.getString("display_name", ""));
        materialDrawer = initDrawerWithListeners(mUser);
        if (prefs.getString("password", "").isEmpty()) {
            showLoginDialog();
        } else {
            fetchTranscript();
        }
        scheduleAlarm();
    }


    private Drawer.Result initDrawerWithListeners(User user) {

        ProfileDrawerItem profileDraweritem = new ProfileDrawerItem().withName(user.getName()).withEmail(user.getEmail()).withIcon(getResources().getDrawable(R.drawable.profile));

        // creating account header
        AccountHeader.Result headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.cover)
                .addProfiles(
                        profileDraweritem
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .withCompactStyle(true)
                .build();

        final Drawer.Result result = new Drawer()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(Constants.DRAWER_HOME_ID),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(Constants.DRAWER_SETTINGS_ID),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_about).withIcon(FontAwesome.Icon.faw_android).withIdentifier(Constants.DRAWER_ABOUT_ID),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon.faw_user).withIdentifier(Constants.DRAWER_LOGOUT_ID)
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
                            case Constants.DRAWER_LOGOUT_ID:
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.remove("password").remove("display_name").apply();
                                cancelAlarm();
                                break;
                            case Constants.DRAWER_ABOUT_ID:
                                showAboutDialog();
                                break;
                        }

                    }
                })
                .build();

        return result;
    }

    public void fetchTranscript() {
        VolleyClient.getInstance(this).getTranscript(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                    }
                }, null, "11200125", prefs.getString("password", "") // this must change from preferences
        );
    }

    private void showLoginDialog() {

        new MaterialDialog.Builder(this)
                .title("Login Credentials")
                .content("Please Enter Your Password")
                .positiveText("OK")
                .negativeText("Cancel")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputMaxLength(16)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (dialog.getInputEditText().getText() != null) {
                            String password = dialog.getInputEditText().getText().toString();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("password", password).apply();
                            fetchTranscript();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
//                        dialog.dismiss();
                        MainActivity.this.finish();
                    }
                })

                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        MainActivity.this.finish();
                    }
                })
                .input("Password", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        String inputString = input.toString();
                        if (inputString.length() == 0 || inputString.isEmpty()) {
                            showToast("Please Fill The Input");
                        }
                    }
                })
                .show();
    }

    private void showAboutDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.drawer_item_about)
                .positiveText("Dismiss")
                .content(Html.fromHtml(getString(R.string.about_body)))
                .contentLineSpacing(1.6f)
                .theme(Theme.DARK)
                .show();
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
            long interval = 30 * 1000L;
            long firstTime = SystemClock.elapsedRealtime();
            // set alarm
            am.setInexactRepeating(type, firstTime, interval, pi);
        }

    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), TranscriptBroadcastReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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


    // helper function
    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }


}
