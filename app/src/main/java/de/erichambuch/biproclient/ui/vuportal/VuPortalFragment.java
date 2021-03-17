package de.erichambuch.biproclient.ui.vuportal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.base.OpenVuPortalCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

/**
 */
public class VuPortalFragment extends MyBaseFragment {

    // TODO: hier fehlt der SAML-Flow

    private MainViewModel mainViewModel;

    public VuPortalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_vuportal, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return theView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
    }

    public void callService(final View v) {
        final OpenVuPortalCommand command = new OpenVuPortalCommand(mainViewModel.getConfiguration());
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), v.findViewById(R.id.vuPortalWebView), new CommandCallback() {

            @Override
            public void onSuccess(Object data) {
                // empty
            }

            @Override
            public void onFailure(Exception e) {
                showError(e);
            }
        });
    }
}