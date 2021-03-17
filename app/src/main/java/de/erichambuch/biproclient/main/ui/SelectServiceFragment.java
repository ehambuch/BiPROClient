package de.erichambuch.biproclient.main.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;

import static androidx.navigation.Navigation.findNavController;

/**
 * Fragment zum Auswählen eines BiPRO-Service.
 */
public class SelectServiceFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_service, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        ProviderConfiguration configuration = mainViewModel.getConfiguration();
        setNavigation(view, R.id.buttonServiceExtranet, R.id.action_selectServiceFragment_to_extranetServiceFragment, configuration.getBipro440ServiceURL());
        setNavigation(view, R.id.buttonServiceVuPortal, R.id.action_selectServiceFragment_to_vuPortalFragment, configuration.getVuPortalURL());
        setNavigation(view, R.id.buttonServiceTransfer, R.id.action_selectServiceFragment_to_transferServiceFragment, configuration.getTransferServiceURL());
        setNavigation(view, R.id.buttonServiceListPartner, R.id.action_selectServiceFragment_to_listPartnerFragment, configuration.getListServiceURL());
        setNavigation(view, R.id.buttonServiceListVertrag, R.id.action_selectServiceFragment_to_listVertragFragment, configuration.getListServiceURL());
        setNavigation(view, R.id.buttonServiceVertrag, R.id.action_selectServiceFragment_to_vertragServiceFragment, configuration.getVertragServiceURL());
        setNavigation(view, R.id.buttonServicePartner, R.id.action_selectServiceFragment_to_partnerServiceFragment, configuration.getPartnerServiceURL());
        setNavigation(view, R.id.buttonServiceSchaden, R.id.action_selectServiceFragment_to_schadenServiceFragment, configuration.getSchadenServiceURL());
    }

    private void setNavigation(final View view, final int buttonId, final int navigationTarget, String serviceUrl) {
        Button button = view.findViewById(buttonId);
        if (serviceUrl != null && serviceUrl.length() > 1 ) {
            button.setEnabled(true);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findNavController(v).navigate(navigationTarget);
                }
            });
        } else
            button.setEnabled(false);
    }
}
