package de.erichambuch.biproclient.ui.transfer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

import de.erichambuch.biproclient.AppInfo;
import de.erichambuch.biproclient.BuildConfig;
import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.base.CommandCallback;
import de.erichambuch.biproclient.bipro.transfer.GetShipmentCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;
import de.erichambuch.biproclient.utils.XmlUtils;

import static androidx.navigation.Navigation.findNavController;

/**
 * A fragment representing a list of Items.
 */
public class TransferListShipmentItemsFragment extends MyBaseFragment implements View.OnClickListener {

    MainViewModel mainViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TransferListShipmentItemsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transfer_list_shipment_items_list, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        ((TextView)view.findViewById(R.id.transferListResponseMessage)).setText(mainViewModel.getResponseMessage());

        // Set the adapter
        View listView = view.findViewById(R.id.listShipmentsList);
        if (listView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyTransferShipmentItemRecyclerViewAdapter(this, mainViewModel, mainViewModel.getShipmentList()));
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.listShipmentGetButton)
            mainViewModel.getAuthenticationManager().startAuthenticate(v.getContext(), () -> callServiceViewData(v));
        else if (v.getId() == R.id.listShipmentGetDocButton)
            mainViewModel.getAuthenticationManager().startAuthenticate(v.getContext(), () -> callServiceViewDoc(v));
    }

    public void callServiceViewData(final View v) {
        GetShipmentCommand command = new GetShipmentCommand(mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        String id = (String)v.getTag();
        if ( id != null ) {
            command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), Collections.singletonMap("${id}", id), new CommandCallback() {
                @Override
                public void onSuccess(Object data) {
                    try {
                        mainViewModel.setXml((String) data);
                        mainViewModel.setTree(command.createTreeView((String) data));
                        mainViewModel.setResponseMessage(command.getMessage());
                        findNavController(v).navigate(R.id.action_transferListShipmentItemsFragment_to_vertragFullViewFragment);
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
    }

    public void callServiceViewDoc(final View v) {
        GetShipmentCommand command = new GetShipmentCommand(mainViewModel.getConfiguration(), mainViewModel.getRequestLogger());
        String id = (String)v.getTag();
        if ( id != null ) {
            command.execute(mainViewModel.getAuthenticationManager().getAuthentication(), Collections.singletonMap("${id}", id), new CommandCallback() {
                @Override
                public void onSuccess(Object data) {
                    String xmlResponse = (String) data;
                    try {
                        for(GetShipmentCommand.Attachment attachment : command.getAttachments()) {
                            String dateiname = XmlUtils.getValueFromElement(xmlResponse, "Dateiname" ); // TODO geht nur beim 1. Dokument
                            String dateityp = XmlUtils.getValueFromElement(xmlResponse, "Dateityp");
                            if (dateiname == null)
                                dateiname = "Attachment"+XmlUtils.getValueFromElement(xmlResponse, "ID");
                            if (dateityp == null)
                                dateityp = "pdf";
                            File temp = new File(getContext().getFilesDir(), dateiname+ "."+dateityp );
                            try(FileOutputStream out = new FileOutputStream(temp)) {
                                out.write(attachment.data);
                            }
                            // und mit Android Mitteln (unter Berücksichtigung Security etc.) öffnen
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(FileProvider.getUriForFile(requireActivity(), BuildConfig.APPLICATION_ID + ".provider", temp));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            getContext().startActivity(intent);
                        }

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
    }
}