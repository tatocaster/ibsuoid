package me.tatocaster.ibsuoid.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import me.tatocaster.ibsuoid.Constants;
import me.tatocaster.ibsuoid.R;
import me.tatocaster.ibsuoid.model.User;

/**
 * tatocaster <kutaliatato@gmail.com>
 */
public class MainActivity extends Activity {

    private Drawer.Result materialDrawer;
    private User mUser;
    public static Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                        switch (drawerItem.getIdentifier()){
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


}
