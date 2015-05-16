package me.tatocaster.ibsuoid.ui.dialog;

import android.content.Context;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import me.tatocaster.ibsuoid.R;

/**
 * Created by tatocaster on 2015-05-17.
 */
public class DialogGenerator {


    public DialogGenerator() {

    }

    public static void showAboutDialog(Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.drawer_item_about)
                .positiveText("Dismiss")
                .content(Html.fromHtml(context.getString(R.string.about_body)))
                .contentLineSpacing(1.6f)
                .theme(Theme.DARK)
                .show();
    }

    public static MaterialDialog.Builder noNetwork(Context context) {
        return new MaterialDialog.Builder(context)
                .title(R.string.alert_no_network_title)
                .theme(Theme.DARK)
                .positiveText("Connect");
//                .show();
    }

}
