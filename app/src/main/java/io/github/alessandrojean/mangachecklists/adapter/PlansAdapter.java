package io.github.alessandrojean.mangachecklists.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.alessandrojean.mangachecklists.MangaDetailsActivity;
import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.domain.Plan;

/**
 * Created by Desktop on 16/12/2017.
 */

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder> {
    private Context context;
    private List<Plan> plans;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle, tvGift, tvSentDate;

        Plan plan;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.title);
            tvGift = itemView.findViewById(R.id.gift);
            tvSentDate = itemView.findViewById(R.id.sent_date);
        }

        private void setData(Plan plan) {
            this.plan = plan;

            tvTitle.setText(
                    (plan.getManga().getVolume() != -1)
                            ? Html.fromHtml("<b>"+plan.getManga().getName()+"</b> #"+plan.getManga().getVolume())
                            : Html.fromHtml("<b>" + plan.getManga().getName() +"</b>")
            );

            tvSentDate.setText(plan.getFormattedSentDate());
            tvGift.setText(plan.getGift());
            tvGift.setVisibility(plan.getGift() == null ? View.GONE : View.VISIBLE);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            showInformations();
        }

        private void showInformations() {
            if (plan.getManga().getUrl() != null) {
                Intent intent = new Intent(context, MangaDetailsActivity.class);
                intent.putExtra(Manga.MANGAS_KEY, plan.getManga());
                context.startActivity(intent);
            }
        }
    }

    public PlansAdapter(Context context, List<Plan> plans) {
        this.context = context;
        this.plans = plans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.item_plan, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(plans.get(position));
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }
}
