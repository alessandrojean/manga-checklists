package io.github.alessandrojean.mangachecklists.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 14/12/2017.
 */

public class MangasAdapter extends RecyclerView.Adapter<MangasAdapter.ViewHolder> {
    private Context context;
    private List<Manga> mangas;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageView ivThumbnail, ivOverflow;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.title);
            tvDate = itemView.findViewById(R.id.date);
            ivThumbnail = itemView.findViewById(R.id.thumbnail);
            ivOverflow = itemView.findViewById(R.id.overflow);
        }

        private void setData(Manga manga) {
            tvTitle.setText(
                    (manga.getVolume() != -1)
                    ? Html.fromHtml("<b>"+manga.getName()+"</b> #"+manga.getVolume())
                    : Html.fromHtml("<b>" + manga.getName() +"</b>")
            );
            tvDate.setText(manga.getFormattedDate());

            Picasso.with(context)
                    .load(manga.getThumbnailUrl())
                    .into(ivThumbnail);

            ivOverflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(ivOverflow);
                }
            });
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(context, view);
            MenuInflater menuInflater = popupMenu.getMenuInflater();
            menuInflater.inflate(R.menu.menu_manga, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_want:
                            Toast.makeText(context, "Quero clicado", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.action_information:
                            Toast.makeText(context, "Informações clicado", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        }
    }

    public MangasAdapter(Context context, List<Manga> mangas) {
        this.context = context;
        this.mangas = mangas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(context)
                .inflate(R.layout.item_manga, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mangas.get(position));
    }

    @Override
    public int getItemCount() {
        return mangas.size();
    }
}
