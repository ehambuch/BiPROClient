package de.erichambuch.biproclient.ui.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.vertrag.VertragGetDataCommand;
import de.erichambuch.biproclient.main.StaticData;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewBinder;

public class TreeNodeBinder extends TreeViewBinder<TreeNodeBinder.ViewHolder> {
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

    public static void expand(final TreeNode node) {
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
