package io.github.alessandrojean.mangachecklists.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.domain.Detail;
import io.github.alessandrojean.mangachecklists.domain.DetailGroup;

/**
 * Created by Desktop on 17/12/2017.
 */

public class DetailGroupAdapter extends RecyclerView.Adapter<DetailGroupAdapter.ViewHolder> {
    private Context context;
    private List<DetailGroup> detailGroups;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        RecyclerView rvDetails;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.detail_group_name);
            rvDetails = initRecyclerView(R.id.recycler_view_details);
        }

        private RecyclerView initRecyclerView(int recyclerViewId) {
            RecyclerView recyclerView = itemView.findViewById(recyclerViewId);

            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            layoutManager.setAutoMeasureEnabled(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setAdapter(new DetailAdapter(context));

            return recyclerView;
        }

        private void setData(DetailGroup detailGroup) {
            tvTitle.setText(detailGroup.getName());
            updateRecyclerView(rvDetails, detailGroup.getDetails());
        }

        private void updateRecyclerView(RecyclerView recyclerView, List<Detail> details) {
            DetailAdapter adapter = (DetailAdapter) recyclerView.getAdapter();
            adapter.setDetails(details);
        }
    }

    public DetailGroupAdapter(Context context, List<DetailGroup> detailGroups) {
        this.context = context;
        this.detailGroups = detailGroups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.item_detail_group, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(detailGroups.get(position));
    }

    @Override
    public int getItemCount() {
        return detailGroups.size();
    }

    public void setDetails(List<DetailGroup> detailGroups) {
        this.detailGroups.clear();
        this.detailGroups.addAll(detailGroups);

        notifyDataSetChanged();
    }
}
