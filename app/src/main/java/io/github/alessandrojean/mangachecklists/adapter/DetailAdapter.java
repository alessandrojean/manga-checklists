package io.github.alessandrojean.mangachecklists.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.domain.Detail;

/**
 * Created by Desktop on 17/12/2017.
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private Context context;
    private List<Detail> details;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDetailName, tvDetail;

        ViewHolder(View itemView) {
            super(itemView);

            tvDetailName = itemView.findViewById(R.id.detail_name);
            tvDetail = itemView.findViewById(R.id.detail_value);
        }

        private void setData(Detail detail) {
            tvDetailName.setText(detail.getName());
            tvDetail.setText(detail.getDetail());
        }
    }

    public DetailAdapter(Context context) {
        this.context = context;
        this.details = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.item_detail, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(details.get(position));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public void setDetails(List<Detail> details) {
        this.details.clear();
        this.details.addAll(details);

        notifyDataSetChanged();
    }
}
