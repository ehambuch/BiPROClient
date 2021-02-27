package de.erichambuch.biproclient.ui.listen;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

import static androidx.navigation.Navigation.findNavController;

/**
 * A fragment representing a list of Items.
 */
public class ListenResultItemsFragment extends MyBaseFragment implements View.OnClickListener {

    MainViewModel mainViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListenResultItemsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listen_result_items_list, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Set the adapter
        View listView = view.findViewById(R.id.listenResultsList);
        if (listView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyListenResultItemRecyclerViewAdapter(this,
                    mainViewModel, mainViewModel.getListenResultData()));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.listenResultJumpPartner) {
                mainViewModel.setPartnerId((String)v.getTag()); // PartyId setzen
                mainViewModel.setVsnr(null);
                findNavController(v).navigate(R.id.action_listenResultItemsFragment_to_partnerServiceFragment);
        } else if (v.getId() == R.id.listenResultJumpVertrag) {
                mainViewModel.setVsnr((String)v.getTag()); // VSNR setzen // TODO: evtl. noch Risikoträger übertragen
                mainViewModel.setPartnerId(null);
                findNavController(v).navigate(R.id.action_listenResultItemsFragment_to_vertragServiceFragment);
        }
    }
}