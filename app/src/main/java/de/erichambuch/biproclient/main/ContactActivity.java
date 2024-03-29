package de.erichambuch.biproclient.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.erichambuch.biproclient.BuildConfig;
import de.erichambuch.biproclient.R;

/**
 * Activity kann per HTML-Link aufgerufen werden, um Kontaktdaten anzuzugen.
 */
public class ContactActivity extends Activity {

    @Override
    public void onStart() {
        super.onStart();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ContactActivity.this);
        builder.setTitle(R.string.impressum_title);
        builder.setMessage(Html.fromHtml(BuildConfig.IMPRESSUM, Html.FROM_HTML_MODE_COMPACT));
        builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ContactActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        TextView view = (TextView)dialog.findViewById(android.R.id.message);
        if (view != null ) view.setMovementMethod(LinkMovementMethod.getInstance()); // make links clickable
    }
}
