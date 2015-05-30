package me.tatocaster.ibsuoid.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import cn.pedant.SweetAlert.SweetAlertDialog;
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

    public static SweetAlertDialog sweetAlertLoading(Context context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        return pDialog;
    }

    public static SweetAlertDialog sweetAlertError(Context context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitleText("Not Available!")
                .setContentText("No Marks Available At This Moment :(")
                .setConfirmText("OK")
                .showCancelButton(false)
                .setCancelClickListener(null)
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
        return pDialog;
    }

}
