package de.erichambuch.biproclient.ui.vertrag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Map;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.vertrag.VertragGetDataCommand;
import de.erichambuch.biproclient.main.MainViewModel;
import de.erichambuch.biproclient.main.StaticData;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataFullViewFragment extends Fragment {

    protected MainViewModel mainViewModel;

    public static class TreeNodeBinder extends TreeViewBinder<TreeNodeBinder.ViewHolder> {
        private final StaticData staticData;
        public TreeNodeBinder(StaticData staticData) {
            this.staticData = staticData;
        }

        @Override
        public ViewHolder provideViewHolder(View itemView) {
            return new ViewHolder(itemView);
        }

        @Override
        public void bindView(ViewHolder holder, int position, TreeNode node) {
            holder.ivArrow.setRotation(node.isExpand() ? 90 : 0);
            if (node.isLeaf())
                holder.ivArrow.setVisibility(View.INVISIBLE);
            else
                holder.ivArrow.setVisibility(View.VISIBLE);
            VertragGetDataCommand.TreeNodeElement fileNode = (VertragGetDataCommand.TreeNodeElement) node.getContent();
            holder.tvName.setText(fileNode.elementName);
            holder.tvValue.setText(fileNode.value);
            Map<String, String> dtMap = staticData.getDatentypMap("ST_" + fileNode.elementName); // aus Namen des Elements wird der Datentyp abgeleitet
            if (fileNode.value != null && fileNode.value.length() > 0 && dtMap != null)
                holder.tvDesc.setText(dtMap.get(fileNode.value));
            else
                holder.tvDesc.setText(null);
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_treenode;
        }

        public static class ViewHolder extends TreeViewBinder.ViewHolder {
            final TextView tvName;
            final TextView tvValue;
            final ImageView ivArrow;
            final TextView tvDesc;

            public ViewHolder(View rootView) {
                super(rootView);
                this.tvName = (TextView) rootView.findViewById(R.id.tv_name);
                this.tvValue = (TextView) rootView.findViewById(R.id.tv_value);
                this.ivArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
                this.tvDesc = (TextView) rootView.findViewById(R.id.tv_desc);
            }
            public ImageView getIvArrow() {
                return ivArrow;
            }
        }
    }

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
                    expand(mainViewModel.getTree());
                    adapter.refresh(Collections.singletonList(mainViewModel.getTree()));
                }
            }
        });
        return view;
    }

    private void expand(final TreeNode node) {
        if ( node != null ) {
            if ( !node.isExpand() && !node.isLeaf() )
                node.toggle();
            if(node.getChildList() != null) {
                for (Object t : node.getChildList())
                    expand((TreeNode) t);
            }
        }
    }
}