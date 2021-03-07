package de.erichambuch.biproclient.ui.listen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.listen.ListPartnerCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListPartnerFragment extends MyBaseFragment {

    MainViewModel mainViewModel;

    public ListPartnerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_list_partner, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return theView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonListPartnerSucheKunde).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
            }
        });
    }

    public void callService(final View v) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put(ListPartnerCommand.PARAM_VSNR, getText(v, R.id.editListPartnerVsnr));
        parameters.put(ListPartnerCommand.PARAM_NAME, getText(v, R.id.editListPartnerPersonName));
        parameters.put(ListPartnerCommand.PARAM_VORNAME, getText(v, R.id.editListPartnerPersonVorName));
        parameters.put(ListPartnerCommand.PARAM_ORT, getText(v, R.id.editListPartnerOrt));
        parameters.put(ListPartnerCommand.PARAM_PLZ, getText(v, R.id.editListPartnerPersonPLZ));
        parameters.put(ListPartnerCommand.PARAM_STRASSE, getText(v, R.id.editListPartnerPersonStrasse));
        final ListPartnerCommand command = new ListPartnerCommand(mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        final View progressView = v.findViewById(R.id.progressListPartnerService);
        progressView.setVisibility(View.VISIBLE);
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), parameters, new CommandCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    finishProgressBar(progressView);
                    mainViewModel.setListenResultData(command.parseData((String)data));
                    mainViewModel.setResponseMessage(command.getMessage());
                    findNavController(v).navigate(R.id.action_listPartnerFragment_to_listenResultItemsFragment);
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