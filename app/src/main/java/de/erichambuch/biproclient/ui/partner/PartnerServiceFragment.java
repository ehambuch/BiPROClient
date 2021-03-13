package de.erichambuch.biproclient.ui.partner;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.partner.PartnerGetDataCommand;
import de.erichambuch.biproclient.bipro.vertrag.VertragGetDataCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class PartnerServiceFragment extends MyBaseFragment {

    private MainViewModel mainViewModel;

    public PartnerServiceFragment() {
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
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return inflater.inflate(R.layout.fragment_partner_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonStartPartnerSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
            }
        });

        // Feld vorbelegen mit VSNR
        ((TextView)view.findViewById(R.id.editTextPartnerId)).setText(mainViewModel.getPartnerId());

        RadioGroup spinner = (RadioGroup)view.findViewById(R.id.selectVuNrPartnerRadioGroup);
        for(String gdv : mainViewModel.getConfiguration().getGDVNummern()) {
            MaterialRadioButton rationButton = new MaterialRadioButton(requireContext());
            rationButton.setText(gdv);
            rationButton.setTag(gdv);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            spinner.addView(rationButton, -1, layoutParams);
        }
    }

    public void callService(final View v) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put(PartnerGetDataCommand.PARAM_PARTNERNR, getText(v, R.id.editTextPartnerId));
        RadioGroup spinner = (RadioGroup)v.findViewById(R.id.selectVuNrPartnerRadioGroup);
        int id = spinner.getCheckedRadioButtonId();
        if ( id >= 0 ) {
            parameters.put(VertragGetDataCommand.PARAM_VUNR, (String)v.findViewById(id).getTag());
        }
        final PartnerGetDataCommand command = new PartnerGetDataCommand
                (mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        final View progressView = v.findViewById(R.id.progressPartnerService);
        startProgressBar(progressView);
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), parameters, new CommandCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    finishProgressBar(progressView);
                    mainViewModel.setXml((String)data);
                    mainViewModel.setTree(command.createTreeView((String)data));
                    mainViewModel.setResponseMessage(command.getMessage());
                    findNavController(v).navigate(R.id.action_partnerServiceFragment_to_dataFullViewFragment);
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