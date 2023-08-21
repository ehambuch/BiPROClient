package de.erichambuch.biproclient.ui.partner;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.partner.PartnerGetChangeCommand;
import de.erichambuch.biproclient.bipro.partner.PartnerGetDataCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PartnerGetChangeFragment extends MyBaseFragment {

    private MainViewModel mainViewModel;

    public PartnerGetChangeFragment() {
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
        return inflater.inflate(R.layout.fragment_change_partner_bankverbindung, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonGetChangePartnerBankverbindung).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
            }
        });

        // Feld vorbelegen mit VSNR
        ((TextView)view.findViewById(R.id.viewChangePartnerBankverbindungPartnerNr)).setText(mainViewModel.getPartnerId());
        // andere Felder aus Daten
        Map<String,String> bankverbindungMap = PartnerGetChangeCommand.getBankverbindung(mainViewModel.getXml());
        if(bankverbindungMap == null) {
            view.findViewById(R.id.buttonGetChangePartnerBankverbindung).setEnabled(false);
            showError("Fehler", "Keine Bankverbindung vorhanden");
        }
        else {
            ((TextView)view.findViewById(R.id.editChangePartnerIBAN)).setText(bankverbindungMap.get(PartnerGetChangeCommand.PARAM_IBAN));
            ((TextView)view.findViewById(R.id.editChangePartnerBIC)).setText(bankverbindungMap.get(PartnerGetChangeCommand.PARAM_BIC));
            ((TextView)view.findViewById(R.id.editChangePartnerKontoinhaber)).setText(bankverbindungMap.get(PartnerGetChangeCommand.PARAM_KONTOINHABER));
            ((TextView)view.findViewById(R.id.editChangePartnerReferenz)).setText(bankverbindungMap.get(PartnerGetChangeCommand.PARAM_REFERENZ));
        }
    }

    public void callService(final View v) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put(PartnerGetDataCommand.PARAM_PARTNERNR, getText(v, R.id.viewChangePartnerBankverbindungPartnerNr));
        parameters.put(PartnerGetChangeCommand.PARAM_GUELTIGAB, LocalDate.now().toString());
        parameters.put(PartnerGetChangeCommand.PARAM_ARTID, "140014000");
        parameters.put(PartnerGetChangeCommand.PARAM_IBAN, getText(v, R.id.editChangePartnerIBAN));
        parameters.put(PartnerGetChangeCommand.PARAM_BIC, getText(v, R.id.editChangePartnerBIC));
        parameters.put(PartnerGetChangeCommand.PARAM_KONTOINHABER, getText(v, R.id.editChangePartnerKontoinhaber));
        parameters.put(PartnerGetChangeCommand.PARAM_REFERENZ, getText(v, R.id.editChangePartnerReferenz));
        final PartnerGetChangeCommand command = new PartnerGetChangeCommand
                (mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        final View progressView = v.findViewById(R.id.progressChangePartnerBankService);
        startProgressBar(progressView);
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), parameters, new CommandCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    finishProgressBar(progressView);
                    mainViewModel.setXml((String)data);
                    mainViewModel.setTree(command.createTreeView((String)data));
                    mainViewModel.setResponseMessage(command.getMessage());
                    mainViewModel.setChange(true); // activate Change Button
                    requireActivity().runOnUiThread(() -> {
                        findNavController(v).navigate(R.id.action_partnerGetChange_to_dataViewSetChangePartnerFragment); // TODO: 2.Schritt
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