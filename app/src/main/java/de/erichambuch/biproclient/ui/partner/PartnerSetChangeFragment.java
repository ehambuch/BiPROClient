package de.erichambuch.biproclient.ui.partner;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.partner.PartnerGetDataCommand;
import de.erichambuch.biproclient.bipro.partner.PartnerSetChangeCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;
import de.erichambuch.biproclient.ui.base.TreeNodeBinder;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PartnerSetChangeFragment extends MyBaseFragment {

    private MainViewModel mainViewModel;

    public PartnerSetChangeFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_partner_setchange_view, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        final TreeViewAdapter adapter = new TreeViewAdapter(Collections.singletonList(mainViewModel.getTree()),
                Collections.singletonList(new TreeNodeBinder(mainViewModel.getStaticData())));
        final RecyclerView tView = view.findViewById(R.id.dataPartnerChangeFragment);
        tView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        tView.setAdapter(adapter);
        adapter.ifCollapseChildWhileCollapseParent(true);
        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {
                if (!node.isLeaf()) {
                    onToggle(!node.isExpand(), holder);
                }
                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                TreeNodeBinder.ViewHolder dirViewHolder = (TreeNodeBinder.ViewHolder) holder;
                final ImageView ivArrow = dirViewHolder.getIvArrow();
                int rotateDegree = isExpand ? 90 : 0;
                ivArrow.animate().rotation(rotateDegree).start();
            }
        });
        TreeNodeBinder.expand(mainViewModel.getTree());

        view.findViewById(R.id.buttonSetChangePartner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO send command
            }
        });
        return view;
    }

    public void callService(final View v) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put(PartnerGetDataCommand.PARAM_PARTNERNR, getText(v, R.id.viewChangePartnerBankverbindungPartnerNr));
        parameters.put("gueltigAb", LocalDate.now().toString());
        parameters.put("artId", "140014000");
        parameters.put("iban", getText(v, R.id.editChangePartnerIBAN));
        parameters.put("bic", getText(v, R.id.editChangePartnerBIC));
        parameters.put("kontoInhaber", getText(v, R.id.editChangePartnerKontoinhaber));
        parameters.put("referenz", getText(v, R.id.editChangePartnerReferenz));
        final PartnerSetChangeCommand command = new PartnerSetChangeCommand
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
                        findNavController(v).navigate(R.id.action_partnerServiceFragment_to_dataFullViewPartnerFragment); // TODO: 2.Schritt
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