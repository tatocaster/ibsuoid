package me.tatocaster.ibsuoid.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Html;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import me.tatocaster.ibsuoid.R;
import me.tatocaster.ibsuoid.ui.MainActivity;

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

}
