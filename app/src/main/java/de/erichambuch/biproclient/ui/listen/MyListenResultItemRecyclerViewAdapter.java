package de.erichambuch.biproclient.ui.listen;

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
import de.erichambuch.biproclient.bipro.listen.ListResultData;
import de.erichambuch.biproclient.main.MainViewModel;

public class MyListenResultItemRecyclerViewAdapter extends RecyclerView.Adapter<MyListenResultItemRecyclerViewAdapter.ViewHolder> {

    private final List<ListResultData> mValues;
    private final MainViewModel mainViewModel;
    private final View.OnClickListener listener;

    public MyListenResultItemRecyclerViewAdapter(View.OnClickListener listener, MainViewModel mainViewModel, List<ListResultData> items) {
        mValues = new ArrayList<>(items);
        this.mainViewModel = mainViewModel;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_listen_result_item, parent, false);
        view.findViewById(R.id.listenResultJumpPartner).setOnClickListener(listener);
        view.findViewById(R.id.listenResultJumpVertrag).setOnClickListener(listener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ListResultData entry = mValues.get(position);
        holder.mItem = entry;
        holder.mPartnerIdView.setText(entry.partnerId);
        holder.mAnschriftView.setText(entry.anschrift);
        holder.mDatumView.setText(entry.geburtsdatum);
        holder.mNameView.setText(entry.name);
        holder.mVsnrView.setText(entry.vsnr);
        holder.mButtonParterView.setEnabled((entry.partnerId != null && entry.partnerId.length() > 0));
        holder.mButtonParterView.setTag(entry.partnerId);
        holder.mButtonVertragView.setEnabled((entry.vsnr != null && entry.vsnr.length() > 0));
        holder.mButtonVertragView.setTag(entry.vsnr);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


        public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mPartnerIdView;
        public final TextView mNameView;
        public final TextView mAnschriftView;
            public final TextView mDatumView;
            public final TextView mVsnrView;
        public final Button mButtonParterView;
            public final Button mButtonVertragView;
        public ListResultData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPartnerIdView = (TextView) view.findViewById(R.id.listenResultPartnerId);
            mNameView = (TextView) view.findViewById(R.id.listenResultName);
            mAnschriftView = (TextView) view.findViewById(R.id.listenResultAnschrift);
            mDatumView = (TextView) view.findViewById(R.id.listenResultGeburtsdatum);
            mVsnrView = view.findViewById(R.id.listenResultVsnr);
            mButtonParterView = view.findViewById(R.id.listenResultJumpPartner);
            mButtonVertragView = view.findViewById(R.id.listenResultJumpVertrag);
        }
    }
}