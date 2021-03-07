package de.erichambuch.biproclient.main.ui;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class MyBaseFragment extends Fragment {

    protected void showError(Exception e) {
        requireActivity().runOnUiThread(() -> {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), e.getLocalizedMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Abbruch", v -> {
                    }).show();
        });
    }

    protected void showError(String title, String message) {
        requireActivity().runOnUiThread(() -> {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .setAction("Abbruch", v -> {
                    }).show();
        });
    }

    protected void finishProgressBar(final View view) {
        requireActivity().runOnUiThread(() -> {
            view.setVisibility(View.GONE);
        });
    }

    /**
     * Liefert den Text eines TextView Feldes. Dabei wird speziell ein leeres Feld als null geliefert.
     * @param mainView MainView
     * @param resId ID
     * @return der Text oder <code>null</code> wenn leer
     */
    protected String getText(final View mainView, int resId) {
        CharSequence text = ((TextView)mainView.findViewById(resId)).getText();
        if (text == null || text.length() == 0)
            return null;
        else
            return text.toString().trim();
    }
}
