package de.erichambuch.biproclient.main.ui;

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

//            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//            builder.setTitle("Fehler beim Abruf");
//            builder.setMessage(e.getMessage());
//            builder.setIcon(android.R.drawable.ic_dialog_alert);
//            builder.setPositiveButton(R.string.cancel, (dialog, which) -> {});
//            builder.create().show();


    protected void showError(String title, String message) {
        requireActivity().runOnUiThread(() -> {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .setAction("Abbruch", v -> {
                    }).show();
        });
/*
        requireActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(R.string.cancel, (dialog, which) -> {});
            builder.create().show();
        });
    }*/
    }
}
