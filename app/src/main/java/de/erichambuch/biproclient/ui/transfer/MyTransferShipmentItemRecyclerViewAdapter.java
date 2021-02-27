package de.erichambuch.biproclient.ui.transfer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.erichambuch.biproclient.R;
import de.erichambuch.biproclient.bipro.transfer.TransferEntry;
import de.erichambuch.biproclient.main.MainViewModel;

public class MyTransferShipmentItemRecyclerViewAdapter extends RecyclerView.Adapter<MyTransferShipmentItemRecyclerViewAdapter.ViewHolder> {

    private final List<TransferEntry> mValues;
    private final MainViewModel mainViewModel;
    private final View.OnClickListener listener;

    public MyTransferShipmentItemRecyclerViewAdapter(View.OnClickListener listener, MainViewModel mainViewModel, List<TransferEntry> items) {
        mValues = new ArrayList<>(items);
        this.mainViewModel = mainViewModel;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_transfer_list_shipment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        TransferEntry entry = mValues.get(position);
        holder.mItem = entry;
        holder.mIdView.setText(entry.getShipmentID());
        holder.mGeVoView.setText(entry.getGevo());
        holder.mGeVoDescView.setText(entry.getGevo() != null ? (mainViewModel.getStaticData().getGevoMap().getOrDefault(entry.getGevo(), null)) : null);
        holder.mArtView.setText(entry.getArt());
        holder.mButtonView.findViewById(R.id.listShipmentGetButton).setOnClickListener(listener);
        holder.mButtonView.setTag(entry.getShipmentID());
        holder.mButtonDocView.findViewById(R.id.listShipmentGetDocButton).setOnClickListener(listener);
        holder.mButtonDocView.setTag(entry.getShipmentID());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


        public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mGeVoView;
        public final TextView mGeVoDescView;
            public final TextView mArtView;
        public final Button mButtonView;
            public final Button mButtonDocView;
        public TransferEntry mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.listShipmentId);
            mGeVoView = (TextView) view.findViewById(R.id.listShipmentGevo);
            mGeVoDescView = (TextView) view.findViewById(R.id.listShipmentGevoDesc);
            mArtView = (TextView) view.findViewById(R.id.listShipmentArt);
            mButtonView = view.findViewById(R.id.listShipmentGetButton);
            mButtonDocView = view.findViewById(R.id.listShipmentGetDocButton);
        }

        @NotNull
        @Override
        public String toString() {
            return super.toString() + " '" + mIdView.getText() + "'";
        }
    }
}