package de.erichambuch.biproclient.ui.vertrag;

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
import de.erichambuch.biproclient.bipro.vertrag.VertragGetDataCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VertragServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VertragServiceFragment extends MyBaseFragment {

    private MainViewModel mainViewModel;

    public VertragServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return inflater.inflate(R.layout.fragment_vertrag_service, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonStartVertragSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
            }
        });

        // Feld vorbelegen mit VSNR
        ((TextView)view.findViewById(R.id.editTextVsnrVertrag)).setText(mainViewModel.getVsnr());

        RadioGroup spinner = (RadioGroup)view.findViewById(R.id.selectVuNrVertragRadioGroup);
        for(String gdv : mainViewModel.getConfiguration().getGDVNummern()) {
            MaterialRadioButton rationButton = new MaterialRadioButton(getContext());
            rationButton.setText(gdv);
            rationButton.setTag(gdv);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            spinner.addView(rationButton, -1, layoutParams);
        }
    }

    public void callService(final View v) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put(VertragGetDataCommand.PARAM_VSNR, getText(v, R.id.editTextVsnrVertrag));
        RadioGroup spinner = (RadioGroup)v.findViewById(R.id.selectVuNrVertragRadioGroup);
        int id = spinner.getCheckedRadioButtonId();
        if ( id >= 0 ) {
            parameters.put(VertragGetDataCommand.PARAM_VUNR, (String)v.findViewById(id).getTag());
        }
        final VertragGetDataCommand command = new VertragGetDataCommand(mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), parameters, new CommandCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    mainViewModel.setXml((String)data);
                    mainViewModel.setTree(command.createTreeView((String)data));
                    mainViewModel.setResponseMessage(command.getMessage());
                    findNavController(v).navigate(R.id.action_vertragServiceFragment_to_vertragFullViewFragment);
                } catch (Exception e) {
                    Log.e(AppInfo.APP_NAME, "Fehler beim Parsen", e);
                    showError("Interner Fehler", e.getMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(AppInfo.APP_NAME, "Fehler beim Aufruf des Service", e);
                showError(e);
            }
        });

    }

    private String getText(final View mainView, int resId) {
        String text = ((TextView)mainView.findViewById(resId)).getText().toString();
        return text.trim();
    }
}