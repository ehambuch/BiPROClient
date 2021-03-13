package de.erichambuch.biproclient.ui.transfer;

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

import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.transfer.ListShipmentsCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferServiceFragment extends MyBaseFragment {

    private MainViewModel mainViewModel;

    public TransferServiceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_transfer_service, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonStartTransferSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
            }
        });
    }

    public void callService(final View v) {
        Map<String,String> parameters = new HashMap<>();
        String geVo = ((TextView)v.findViewById(R.id.editTextTransferGevo)).getText().toString();
        parameters.put(ListShipmentsCommand.PARAM_GEVO, ( geVo.trim().length() == 0 ) ? null : geVo.trim());
        parameters.put(ListShipmentsCommand.PARAM_ACKNOWLEDGED, Boolean.valueOf(v.findViewById(R.id.checkBoxAcknowledgedDocs).isSelected()).toString());
        final ListShipmentsCommand command = new ListShipmentsCommand(mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        final View progressView = v.findViewById(R.id.progressTransferService);
        startProgressBar(progressView);
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), parameters, new CommandCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    finishProgressBar(progressView);
                    mainViewModel.setShipmentList(command.parseData((String)data));
                    mainViewModel.setResponseMessage(command.getMessage());
                    findNavController(v).navigate(R.id.action_transferServiceFragment_to_transferListShipmentItemsFragment);
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