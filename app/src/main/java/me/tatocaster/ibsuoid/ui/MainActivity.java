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
import android.widget.ListView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.tatocaster.ibsuoid.Constants;
import me.tatocaster.ibsuoid.R;
import me.tatocaster.ibsuoid.Utilities;
import me.tatocaster.ibsuoid.adapter.TranscriptListAdapter;
import me.tatocaster.ibsuoid.model.Transcript;
import me.tatocaster.ibsuoid.model.User;
import me.tatocaster.ibsuoid.network.VolleyClient;
import me.tatocaster.ibsuoid.service.TranscriptBroadcastReceiver;
import me.tatocaster.ibsuoid.service.TranscriptFetchService;
import me.tatocaster.ibsuoid.ui.dialog.DialogGenerator;

/**
 * tatocaster <kutaliatato@gmail.com>
 */
public class MainActivity extends Activity implements Drawer.OnDrawerItemClickListener {

    private static final String TAG = "MainActivity";
    private Drawer.Result materialDrawer;
    private User mUser;
    public static Activity thisActivity;
    private List<Transcript> transcriptList = new ArrayList<>();
    private ListView listView;

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set ativity context
        thisActivity = MainActivity.this;

        listView = (ListView) findViewById(R.id.transcript_list);

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
        /*Map<String, ?> keys = prefs.getAll();
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
                .withOnDrawerItemClickListener(this)
                .build();

        return result;
    }

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
                materialDrawer.removeItem(position);
                materialDrawer.addItem(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_login).
                                withIcon(FontAwesome.Icon.faw_user).
                                withIdentifier(Constants.DRAWER_LOGIN_ID),
                        Constants.DRAWER_LOGIN_ID);
                break;
            case Constants.DRAWER_LOGIN_ID:
                showLoginDialog();
                break;
            case Constants.DRAWER_ABOUT_ID:
                DialogGenerator.showAboutDialog(thisActivity);
                break;
        }

    }

    public void fetchTranscript() {
        transcriptList.clear();
        VolleyClient.getInstance(this).getTranscript(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // https://gist.github.com/tatocaster/a5d0e7066d29aa3410cc
                        Log.d(TAG, response.toString());
                        JSONArray jArrTranscript = null;

                        try {
                            jArrTranscript = response.getJSONObject("sis_response").getJSONArray("transcript");
                        } catch (JSONException e) {
                        }

                        // Iterate through each item in the transcript, starting with the year name
                        for (int yi = 0; yi < jArrTranscript.length(); yi++) {
                            String strYearName = null;
                            String strSemesterName = null;
                            try {
                                JSONObject jObjYi = jArrTranscript.getJSONObject(yi);
                                strYearName = jObjYi.names().getString(0);
                                JSONArray jArrSi = jObjYi.getJSONArray(strYearName);
                                // Iterate through semesters in the year
                                for (int si = 0; si < jArrSi.length(); si++) {

                                    JSONObject jObjSi = jArrSi.getJSONObject(si);
                                    strSemesterName = jObjSi.names().getString(0);
                                    JSONArray jArrMi = jObjSi.getJSONArray(strSemesterName);

                                    // Iterate through modules in a semester
                                    for (int mi = 0; mi < jArrMi.length(); mi++) {

                                        JSONObject jObjMi = jArrMi.getJSONObject(mi);
                                        String strModuleName = jObjMi.names().getString(0);
                                        // Extract the data from the module object
                                        String strAcademicYear = jObjMi.getJSONObject(strModuleName).getString("AcademicYear");
                                        String strSubjectName = jObjMi.getJSONObject(strModuleName).getString("SubjectName");
                                        String strECTS = String.format("%d", jObjMi.getJSONObject(strModuleName).getInt("ECTS"));
                                        String strHOUR = String.format("%d", jObjMi.getJSONObject(strModuleName).getInt("HOUR"));
                                        String strMid = jObjMi.getJSONObject(strModuleName).getString("mid");
                                        String strFinal = jObjMi.getJSONObject(strModuleName).getString("final");
                                        String strXFinal = jObjMi.getJSONObject(strModuleName).getString("xFinal");
                                        String strMakeup = jObjMi.getJSONObject(strModuleName).getString("makeup");
                                        String strGrade = jObjMi.getJSONObject(strModuleName).getString("grade");

                                        Transcript transcriptItem = new Transcript();
                                        transcriptItem.setStudyYearName(strYearName);
                                        transcriptItem.setSemesterName(strSemesterName);
                                        transcriptItem.setModuleName(strModuleName);
                                        transcriptItem.setAcademicYear(strAcademicYear);
                                        transcriptItem.setSubjectName(strSubjectName);
                                        transcriptItem.setStudentECTS(strECTS);
                                        transcriptItem.setLectureHours(strHOUR);
                                        transcriptItem.setPointMid(strMid);
                                        transcriptItem.setPointFinal(strFinal);
                                        transcriptItem.setPointXFinal(strXFinal);
                                        transcriptItem.setPointMakeUp(strMakeup);
                                        transcriptItem.setStudentGrade(strGrade);

                                        transcriptList.add(transcriptItem);
                                    }
                                }
                                TranscriptListAdapter transcriptListAdapter = new TranscriptListAdapter(transcriptList, MainActivity.this);
                                listView.setAdapter(transcriptListAdapter);
                                transcriptListAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (jArrTranscript.length() == 0) {
                            Utilities.showToast(thisActivity, "Sorry no data is yet available.");
                        }
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
            Log.d(TAG, "EXISTS");
            cancelAlarm();
        } else {
            String alarm = Context.ALARM_SERVICE;
            AlarmManager am = (AlarmManager) getSystemService(alarm);

            Intent intent = new Intent("REFRESH_TRANSCRIPT_MARKS");
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, 0);

            int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            // this will change with preference settings
            String syncTimeFromPref = prefs.getString("sync_frequency","60");
            long interval = Integer.valueOf(syncTimeFromPref) * 1000L;
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
