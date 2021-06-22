package com.example.dentalclinic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.exchange.Service;

import java.util.ArrayList;

class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.ViewHolder> {
    private static final String TAG = "myLogs";

    private final LayoutInflater inflater;
    private final ArrayList<Service> prices;

    PriceAdapter(Context context, ArrayList<Service> prices) {
        this.prices = prices;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PriceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "PriceAdapter onCreateViewHolder");
        View view = inflater.inflate(R.layout.item_price, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PriceAdapter.ViewHolder holder, int position) {
        Service service = prices.get(position);
        Log.d(TAG, "PriceAdapter onBindViewHolder");

        String name = service.getName();
        String pr = String.valueOf(service.getPrice());

        holder.tvName.setText(name);
        holder.tvPrice.setText(pr);
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvPrice;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
