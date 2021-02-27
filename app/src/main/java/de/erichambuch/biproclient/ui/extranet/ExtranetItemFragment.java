package de.erichambuch.biproclient.ui.extranet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.ui.MyBaseFragment;

/**
 * A fragment representing a list of Items.
 */
public class ExtranetItemFragment extends MyBaseFragment {

    private MainViewModel mainViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExtranetItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        ((TextView)view.findViewById(R.id.extranetResponseMessage)).setText(mainViewModel.getResponseMessage());

        // Set the adapter
        View listView = view.findViewById(R.id.extranetResponseList);
        if (listView instanceof RecyclerView) {
            Context context = listView.getContext();
            RecyclerView recyclerView = (RecyclerView) listView;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyExtranetItemRecyclerViewAdapter(mainViewModel.getURLs()));
        }
        return view;
    }
}