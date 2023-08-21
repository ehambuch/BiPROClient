package de.erichambuch.biproclient.ui.partner;

import static androidx.navigation.Navigation.findNavController;

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

/**
 * Fragment zum Auswählen einer Änderung des Partners.
 */
public class SelectPartnerChangeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_change_partner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        ProviderConfiguration configuration = mainViewModel.getConfiguration();
        setNavigation(view, R.id.buttonChangePartnerBankverbindung, R.id.action_partnerSelectChangeFragment_to_partnerGetChangeBankverbindungFragment, configuration.getPartnerServiceSetChangeTemplate());
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
