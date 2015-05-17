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
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.ThemeSingleton;
import com.afollestad.materialdialogs.internal.MDTintHelper;
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
import me.tatocaster.ibsuoid.Utilities;
import me.tatocaster.ibsuoid.model.User;
import me.tatocaster.ibsuoid.network.VolleyClient;
import me.tatocaster.ibsuoid.service.TranscriptBroadcastReceiver;
import me.tatocaster.ibsuoid.service.TranscriptFetchService;
import me.tatocaster.ibsuoid.ui.dialog.DialogGenerator;

/**
 * tatocaster <kutaliatato@gmail.com>
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private Drawer.Result materialDrawer;
    private User mUser;
    public static Activity thisActivity;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set ativity context
        thisActivity = MainActivity.this;

        if (!Utilities.checkNetworkAvailability(thisActivity)) {
            DialogGenerator.noNetwork(thisActivity).callback(new MaterialDialog.ButtonCallback() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).dismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    thisActivity.finish();
                }
            }).show();
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // display all preferences
       /* Map<String, ?> keys = prefs.getAll();
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(TAG, entry.getKey() + ": " + entry.getValue().toString());
        }*/

        if (prefs.getString("password", "").isEmpty() || prefs.getString("username", "").isEmpty()) {
            showLoginDialog();
        } else {
            fetchTranscript();
        }

        mUser = new User();
        // get display name from prefereces
        mUser.setName(prefs.getString("username", ""));
        materialDrawer = initDrawerWithListeners(mUser);

    }


    private Drawer.Result initDrawerWithListeners(User user) {

        ProfileDrawerItem profileDraweritem = new ProfileDrawerItem().withEmail(user.getName()).withIcon(getResources().getDrawable(R.drawable.profile));

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
                                editor.remove("password").remove("display_name").remove("username").apply();
                                cancelAlarm();
                                break;
                            case Constants.DRAWER_ABOUT_ID:
                                DialogGenerator.showAboutDialog(thisActivity);
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
                        // https://gist.github.com/tatocaster/a5d0e7066d29aa3410cc
                        Log.d(TAG, response.toString());
                    }
                }, null, prefs.getString("username", ""), prefs.getString("password", "") // this must change from preferences
        );
    }

    private View positiveAction;
    private EditText passwordInput;
    private EditText usernameInput;

    private void showLoginDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Login Credentials")
                .customView(R.layout.login_dialog_view, true)
                .positiveText("OK")
                .negativeText(android.R.string.cancel)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
//                        thisActivity.finish();
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (passwordInput != null && usernameInput != null) {
                            String password = passwordInput.getText().toString();
                            String username = usernameInput.getText().toString();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("password", password).putString("username", username).apply();
                            fetchTranscript();
                            scheduleAlarm();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        thisActivity.finish();
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.password);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        usernameInput = (EditText) dialog.getCustomView().findViewById(R.id.username);
        usernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        // Toggling the show password CheckBox will mask or unmask the password input EditText
        CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.showPassword);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
                passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
            }
        });
        int widgetColor = ThemeSingleton.get().widgetColor;
        MDTintHelper.setTint(checkbox,
                widgetColor == 0 ? getResources().getColor(R.color.material_teal_500) : widgetColor);
        MDTintHelper.setTint(passwordInput,
                widgetColor == 0 ? getResources().getColor(R.color.material_teal_500) : widgetColor);
        MDTintHelper.setTint(usernameInput,
                widgetColor == 0 ? getResources().getColor(R.color.material_teal_500) : widgetColor);

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
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
            long interval = 60 * 1000L;
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
        stopService(new Intent(MainActivity.this, TranscriptFetchService.class));

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
