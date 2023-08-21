package de.erichambuch.biproclient.ui.vertrag;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.ui.base.TreeNodeBinder;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFullViewFragment extends Fragment {

    protected MainViewModel mainViewModel;

    public DataFullViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_vertrag_full_view, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        final TreeViewAdapter adapter = new TreeViewAdapter(Collections.singletonList(mainViewModel.getTree()),
                Collections.singletonList(new TreeNodeBinder(mainViewModel.getStaticData())));
        final RecyclerView tView = view.findViewById(R.id.dataFullViewFragment);
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
        view.findViewById(R.id.floating_action_button_vertrag_expand_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mainViewModel.getTree().isExpand() ) {
                    adapter.collapseAll();
                } else {
                    TreeNodeBinder.expand(mainViewModel.getTree());
                    adapter.refresh(Collections.singletonList(mainViewModel.getTree()));
                }
            }
        });
        // enable Edit Button or not
        view.findViewById(R.id.floating_action_button_change).setVisibility(mainViewModel.isChange() ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.floating_action_button_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNavController(v).navigate(R.id.action_dataFullViewChange_to_partnerChangeDataFragment);
            }
        });
        return view;
    }


}