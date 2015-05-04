package me.tatocaster.ibsuoid.ui.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by tatocaster on 2015-05-04.
 */

public class LoginDialog extends DialogFragment {

    Activity mActivity;

    public static final LoginDialog newInstance() {
        LoginDialog statusDialog = new LoginDialog();
        Bundle bundle = new Bundle();
        statusDialog.setArguments(bundle);

        return statusDialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Set an EditText view to get user input
        final EditText input = new EditText(mActivity);
        input.setTextColor(Color.BLACK);
        input.setPadding(75,75,75,75);

        AlertDialog builder = new AlertDialog.Builder(mActivity)
                .setTitle("Login Credentials")
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String message = input.getText().toString();

                        if (message.length() == 0) {
                            showToast("Input is Empty");
                            return;
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
//                .setIcon(android.R.drawable.ic_menu_edit)
                .setView(input)
                .show();
        return builder;

//        Button okButton = builder.getButton(DialogInterface.BUTTON_POSITIVE);
//        // set OK button color here
//        okButton.setTextColor(Color.parseColor("#A6C"));
//
//        Button noButton = builder.getButton(DialogInterface.BUTTON_NEGATIVE);
//        // set NO button color here
//        noButton.setTextColor(Color.parseColor("#A6C"));
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        this.mActivity = activity;
    }

    public void showToast(final String toast)
    {
        if(mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(mActivity.getBaseContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
