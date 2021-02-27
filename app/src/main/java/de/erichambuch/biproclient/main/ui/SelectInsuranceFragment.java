package de.erichambuch.biproclient.main.ui;


import android.content.Intent;
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
import de.erichambuch.biproclient.main.MainApplication;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.SettingsActivity;
import de.erichambuch.biproclient.main.SettingsDataStore;
import de.erichambuch.biproclient.main.provider.ProviderConfiguration;
import de.erichambuch.biproclient.main.provider.SettingsConfiguration;

import static androidx.navigation.Navigation.findNavController;

/**
 * Fragment zum Auswählen eines Diensteanbieters (Versicherung).
 */
public class SelectInsuranceFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_insurance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize(view, R.id.buttonProvider1, R.id.buttonConfiguration1, 0);
        initialize(view, R.id.buttonProvider2, R.id.buttonConfiguration2, 1);
        initialize(view, R.id.buttonProvider3, R.id.buttonConfiguration3, 2);
        initialize(view, R.id.buttonProvider4, R.id.buttonConfiguration4, 3);
    }

    private ProviderConfiguration createConfiguration(int index) {
        return new SettingsConfiguration(new SettingsDataStore(this.getContext(), index), requireContext());
    }

    private void initialize(View view, final int id_button, final int id_config, final int index) {
        SettingsDataStore settingsDataStore = new SettingsDataStore(getContext(), index);
        String providerName = settingsDataStore.getString("prefs_provider_name", null);
        Button button = view.findViewById(id_button);
        if ( providerName != null ) {
            button.setEnabled(true);
            button.setText(providerName);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
                    mainViewModel.initConfiguration(createConfiguration(index));
                    mainViewModel.setStaticData((MainApplication)requireActivity().getApplication());
                    findNavController(v).navigate(R.id.actionSelectInsurance);
                }
            });
        } else {
            button.setEnabled(false);
        }
        view.findViewById(id_config).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_CONFIGURATION_INDEX, index);
                startActivity(intent);
            }
        });
    }
}
