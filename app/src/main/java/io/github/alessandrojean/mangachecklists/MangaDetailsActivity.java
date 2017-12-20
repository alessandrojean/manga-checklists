package io.github.alessandrojean.mangachecklists;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SubtitleCollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.github.alessandrojean.mangachecklists.adapter.DetailGroupAdapter;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.parser.detail.DetailParser;
import io.github.alessandrojean.mangachecklists.parser.detail.JBCDetailParser;
import io.github.alessandrojean.mangachecklists.parser.detail.NewPOPDetailParser;
import io.github.alessandrojean.mangachecklists.parser.detail.PaniniDetailParser;
import io.github.alessandrojean.mangachecklists.task.DetailsRequest;
import io.github.alessandrojean.mangachecklists.util.LoadingUtils;
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable;

public class MangaDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String SHOWING_IMAGE_URL_KEY = "showing_image_url_key";

   // private CollapsingToolbarLayout collapsingToolbarLayout;
    private SubtitleCollapsingToolbarLayout subtitleCollapsingToolbarLayout;
    private Toolbar toolbar;
    private ImageView header;
    private ImageView cover;
    private TextView synopsis;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private DetailGroupAdapter adapter;

    private DetailsRequest detailsRequest;
    private Manga manga;

    private String showingImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_details);

        manga = new Manga();

        if (savedInstanceState != null) {
            showingImageUrl = savedInstanceState.getString(SHOWING_IMAGE_URL_KEY);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subtitleCollapsingToolbarLayout = findViewById(R.id.toolbar_layout);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Função não programada ainda.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        header = findViewById(R.id.image);

        synopsis = findViewById(R.id.synopsis);

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminateDrawable(new IndeterminateCircularProgressDrawable(this));

        nestedScrollView = findViewById(R.id.nested_scroll_view);
        nestedScrollView.setVisibility(View.GONE);

        Button buttonViewInSite = findViewById(R.id.button_site);
        buttonViewInSite.setOnClickListener(this);

        cover = findViewById(R.id.cover);
        cover.setOnClickListener(this);

        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_details_group);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        adapter = new DetailGroupAdapter(this, manga.getDetailGroups());
        recyclerView.setAdapter(adapter);
    }

    private void retrieveInfo() {
        if (manga.getSynopsis() != null || manga.getDetailGroups().size() > 0) {
            showInformation(manga, false);
            return;
        }

        detailsRequest = new DetailsRequest(this, getDetailParser());
        detailsRequest.execute();
    }

    private DetailParser getDetailParser() {
        switch (manga.getType()) {
            case Manga.TYPE_JBC:
                return new JBCDetailParser(manga);
            case Manga.TYPE_PANINI:
                return new PaniniDetailParser(manga);
            case Manga.TYPE_NEWPOP:
                return new NewPOPDetailParser(manga);
            default:
                return null;
        }
    }

    public void showInformation(Manga manga, boolean animate) {
        if (manga != null) {
            toolbar.setSubtitle(manga.getSubtitle());
            subtitleCollapsingToolbarLayout.setSubtitle(manga.getSubtitle());
            synopsis.setText(manga.getSynopsis());

            if (manga.getSynopsis() == null) {
                findViewById(R.id.synopsis_title).setVisibility(View.GONE);
                findViewById(R.id.divider_synopsis).setVisibility(View.GONE);
                synopsis.setVisibility(View.GONE);
            }

            adapter.setDetails(manga.getDetailGroups());

            if (showingImageUrl == null) {
                Picasso.with(this)
                        .load(manga.getThumbnailUrl())
                        .placeholder(R.drawable.example_cover)
                        .into(cover);

                showingImageUrl = manga.getThumbnailUrl();
            }

            if (manga.getHeaderUrl() != null) {
                showingImageUrl = manga.getHeaderUrl();
            }

            updateImage(showingImageUrl, header);
        }

        if (animate)
            LoadingUtils.showContent(nestedScrollView, progressBar, getResources().getInteger(android.R.integer.config_longAnimTime));
        else {
            nestedScrollView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        manga = getIntent().getParcelableExtra(Manga.MANGAS_KEY);

        if (showingImageUrl == null)
            showingImageUrl = manga.getThumbnailUrl();

        String title = (manga.getVolume() == -1)
                ? manga.getName()
                : manga.getName() + " #" + manga.getVolume();

        toolbar.setTitle(title);
        subtitleCollapsingToolbarLayout.setTitle(title);

        Picasso.with(this)
                .load(manga.getThumbnailUrl())
                .placeholder(R.drawable.example_cover)
                .into(cover);

        updateImage(showingImageUrl, header);

        if (manga.getDetailGroups().size() == 0) {
            showingImageUrl = manga.getThumbnailUrl();

            retrieveInfo();
        }
        else
            showInformation(manga, false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (detailsRequest != null && detailsRequest.getStatus() == DetailsRequest.Status.RUNNING) {
            detailsRequest.cancel(true);
            detailsRequest = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Manga.MANGAS_KEY, manga);
        outState.putString(SHOWING_IMAGE_URL_KEY, showingImageUrl);
        super.onSaveInstanceState(outState);
    }

    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.colorPrimaryDark);
        int primary = getResources().getColor(R.color.colorPrimary);

        subtitleCollapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        subtitleCollapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));

        updateBackground(floatingActionButton, palette);
    }

    private void updateBackground(FloatingActionButton floatingActionButton, Palette palette) {
        int lightVibrantColor = palette.getLightVibrantColor(getResources().getColor(android.R.color.white));
        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorAccent));

        floatingActionButton.setRippleColor(lightVibrantColor);
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(vibrantColor));
    }

    private void updateImage(String imageUrl, final ImageView imageView) {
        Picasso.with(this)
                .load(imageUrl)
                .error(R.drawable.drawer_background_temp)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                applyPalette(palette);
                            }
                        });
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_site:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(manga.getUrl()));
                startActivity(browserIntent);
                break;
            case R.id.cover:
                showCoverAlert();
                break;
        }
    }

    private void showCoverAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.dialog_image, null);

        ImageView image = view.findViewById(R.id.picture);
        Picasso.with(this)
                .load(manga.getThumbnailUrl())
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MangaDetailsActivity.this);
                        builder.setView(view);
                        builder.setCancelable(true);
                        builder.create().show();
                    }

                    @Override
                    public void onError() { }
                });
    }
}
