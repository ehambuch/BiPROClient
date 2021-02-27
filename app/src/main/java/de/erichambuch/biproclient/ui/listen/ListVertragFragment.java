package de.erichambuch.biproclient.ui.listen;

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
import de.erichambuch.biproclient.bipro.listen.ListVertragCommand;
import de.erichambuch.biproclient.bipro.vertrag.VertragGetDataCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

import static androidx.navigation.Navigation.findNavController;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListVertragFragment extends MyBaseFragment {

    MainViewModel mainViewModel;

    public ListVertragFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View theView = inflater.inflate(R.layout.fragment_list_vertrag, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        return theView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.buttonListVertragSuche).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.getAuthenticationManager().startAuthenticate(getContext(), () -> callService(view));
            }
        });
        RadioGroup spinner = (RadioGroup)view.findViewById(R.id.selectVuNrListVertragRadioGroup);
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
        parameters.put(ListVertragCommand.PARAM_VSNR, getText(v, R.id.editListVertragVsnr));
        parameters.put(ListVertragCommand.PARAM_PARTNERID, getText(v, R.id.editListVertragPartnerId));
        parameters.put(ListVertragCommand.PARAM_VORGANGID, getText(v, R.id.editListVertragVorgangsId));
        RadioGroup spinner = (RadioGroup)v.findViewById(R.id.selectVuNrListVertragRadioGroup);
        int id = spinner.getCheckedRadioButtonId();
        if ( id >= 0 ) {
            parameters.put(VertragGetDataCommand.PARAM_VUNR, (String)v.findViewById(id).getTag());
        }
        final ListVertragCommand command = new ListVertragCommand(mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), parameters, new CommandCallback() {
            @Override
            public void onSuccess(Object data) {
                try {
                    mainViewModel.setListenResultData(command.parseData((String)data));
                    mainViewModel.setResponseMessage(command.getMessage());
                    findNavController(v).navigate(R.id.action_listVertragFragment_to_listenResultItemsFragment);
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