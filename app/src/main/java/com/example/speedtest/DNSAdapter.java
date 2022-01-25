package com.example.speedtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DNSAdapter extends RecyclerView.Adapter<DNSAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DNSData> mDataset;
    OnClickListener listener;

    public void setOnItemClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardDNS;
        TextView txtIP;

        public ViewHolder(View v) {
            super(v);
            cardDNS = v.findViewById(R.id.card_dns);
            txtIP = v.findViewById(R.id.txt_ip);
        }
    }

    public DNSAdapter(Context context, ArrayList<DNSData> myDataset) {
        this.context = context;
        this.mDataset = myDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_dns, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        DNSData dns = mDataset.get(position);
        holder.txtIP.setText(dns.getIp());

        holder.cardDNS.setOnClickListener(v -> {
            listener.onItemClick(position, dns.getId());
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}