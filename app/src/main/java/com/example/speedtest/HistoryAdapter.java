package com.example.speedtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<HistoryData> mDataset;
    OnClickListener listener;

    public void setOnItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardHistory;
        TextView txtIP, txtPing, txtJitter, txtLoss, txtDownload, txtUpload;

        public ViewHolder(View v) {
            super(v);
            cardHistory = v.findViewById(R.id.card_history);
            txtIP = v.findViewById(R.id.txt_ip);
            txtPing = v.findViewById(R.id.txt_ping);
            txtJitter = v.findViewById(R.id.txt_jitter);
            txtLoss = v.findViewById(R.id.txt_loss);
            txtDownload = v.findViewById(R.id.txt_download);
            txtUpload = v.findViewById(R.id.txt_upload);
        }
    }

    public HistoryAdapter(Context context, ArrayList<HistoryData> myDataset) {
        this.context = context;
        this.mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_history, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        HistoryData history = mDataset.get(position);
        holder.txtIP.setText(history.getIp());
        holder.txtPing.setText("Ping: " + history.getPing() + "ms");
        holder.txtJitter.setText("Jitter: " + history.getJitter() + "ms");
        holder.txtLoss.setText("Loss: " + history.getLoss());
        holder.txtDownload.setText("Download: " + history.getDownload());
        holder.txtUpload.setText("Upload: " + history.getUpload());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}