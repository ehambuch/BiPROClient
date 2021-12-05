package de.erichambuch.biproclient.ui.extranet;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.extranet.ExtranetGetLinksCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

/**
 */
public class ExtranetServiceSearchFragment extends MyBaseFragment {

    private MainViewModel mainViewModel;

    public ExtranetServiceSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public synchronized View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View theView = inflater.inflate(R.layout.fragment_extranet_service_search, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return theView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonExtranetSucheKunde).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
            }
        });

        // vorbelegen
        ((TextView)view.findViewById(R.id.editTextVsnr)).setText(mainViewModel.getVsnr());
        ((TextView)view.findViewById(R.id.editTextPartnerId)).setText(mainViewModel.getPartnerId());
        view.findViewById(R.id.progressExtranetSearch).setVisibility(View.GONE);
    }

    public void callService(final View v) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put(ExtranetGetLinksCommand.PARAM_VSNR, getText(v, R.id.editTextVsnr));
        parameters.put(ExtranetGetLinksCommand.PARAM_PARTNERNR, getText(v, R.id.editTextPartnerId));
        parameters.put(ExtranetGetLinksCommand.PARAM_NAME, getText(v, R.id.editTextPersonName));
        parameters.put(ExtranetGetLinksCommand.PARAM_VORNAME, getText(v, R.id.editTextPersonVorName));
        parameters.put(ExtranetGetLinksCommand.PARAM_ORT, getText(v, R.id.editTextPersonOrt));
        parameters.put(ExtranetGetLinksCommand.PARAM_PLZ, getText(v, R.id.editTextPersonPLZ));
        parameters.put(ExtranetGetLinksCommand.PARAM_STRASSE, getText(v, R.id.editTextPersonStrasse));
        final ExtranetGetLinksCommand command = new ExtranetGetLinksCommand(mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        final View progressView = v.findViewById(R.id.progressExtranetSearch);
        startProgressBar(progressView);
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), parameters, new CommandCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    finishProgressBar(progressView);
                    mainViewModel.setURLs(command.parseData((String)data));
                    mainViewModel.setResponseMessage(command.getMessage());
                    requireActivity().runOnUiThread(() -> {
                        findNavController(v).navigate(R.id.action_extranetServiceSearchFragment_to_extranetItemFragment);
                    });
                } catch (Exception e) {
                    Log.e(AppInfo.APP_NAME, "Fehler beim Parsen", e);
                    showError("Interner Fehler", e.getMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                finishProgressBar(progressView);
                Log.e(AppInfo.APP_NAME, "Fehler beim Aufruf des Service", e);
                showError(e);
            }
        });

    }
}