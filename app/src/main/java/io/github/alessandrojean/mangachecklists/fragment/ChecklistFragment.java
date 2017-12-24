package io.github.alessandrojean.mangachecklists.fragment;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.alessandrojean.mangachecklists.MainActivity;
import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.adapter.MangasAdapter;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.parser.checklist.ChecklistParser;
import io.github.alessandrojean.mangachecklists.parser.checklist.JBCChecklistParser;
import io.github.alessandrojean.mangachecklists.parser.checklist.NewPOPChecklistParser;
import io.github.alessandrojean.mangachecklists.parser.checklist.PaniniChecklistParser;
import io.github.alessandrojean.mangachecklists.task.ChecklistRequest;
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable;


public class ChecklistFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        DatePickerDialog.OnDateSetListener{

    public static final String TAG = ChecklistFragment.class.getName();
    public static final String TITLE = "Checklist";
    public static final String KEY = "fragment";

    // Arguments from bundle.
    private static final String ARG_MONTH = "arg_month";
    private static final String ARG_YEAR = "arg_year";
    private static final String ARG_FILTER = "arg_filter";
    private static final String ARG_VIEW_VISIBLE = "arg_view_visible";
    private static final String ARG_REFRESHING = "arg_refreshing";
    private static final String ARG_MANGA_LIST = "arg_manga_list";

    // Views available to show.
    public static final int STATE_CONTENT = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_LOADING = 2;

    // Class variables.
    private int mMonth;
    private int mYear;
    private int mFilter;
    private int mViewVisible;
    private boolean mRefreshing;
    private ArrayList<Manga> mMangaList;

    private Parcelable rvState;

    // Views to show.
    private View viewContent;
    private View viewError;
    private View viewLoading;

    // Views in viewContent
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private TextView textViewEmpty;
    private MenuItem menuItemFilter;

    // Views in viewError
    private Button buttonTryAgain;

    // Views in viewLoading
    private ProgressBar progressBarLoading;

    // Another
    private MangasAdapter mangasAdapter;
    private ChecklistRequest checklistRequest;
    private ChecklistParser checklistParser;

    public ChecklistFragment() {
        // Required empty public constructor
        mViewVisible = STATE_CONTENT;
        mMangaList = new ArrayList<>();
    }

    public static ChecklistFragment newInstance(int month, int year, int filter) {
        ChecklistFragment fragment = new ChecklistFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_FILTER, filter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMonth = getArguments().getInt(ARG_MONTH);
            mYear = getArguments().getInt(ARG_YEAR);
            mFilter = getArguments().getInt(ARG_FILTER);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mMonth = savedInstanceState.getInt(ARG_MONTH);
            mYear = savedInstanceState.getInt(ARG_YEAR);
            mFilter = savedInstanceState.getInt(ARG_FILTER);
            mViewVisible = savedInstanceState.getInt(ARG_VIEW_VISIBLE);
            mRefreshing = savedInstanceState.getBoolean(ARG_REFRESHING);
            mMangaList = savedInstanceState.getParcelableArrayList(ARG_MANGA_LIST);
        }

        showCorrectView(mViewVisible);
        swipeRefreshLayout.setRefreshing(mRefreshing);

        mangasAdapter = new MangasAdapter(getContext(), mMangaList);
        recyclerView.setAdapter(mangasAdapter);

        checklistParser = getCorrectParser(mFilter);

        if (mMangaList.size() == 0 || mRefreshing || mViewVisible == STATE_LOADING)
            retrieveMangas(mMonth, mYear);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_checklist, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewContent = view.findViewById(R.id.layout_content_checklist);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_checklist);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.recycler_view_checklist);

        GridLayoutManager gridLayoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
        else
            gridLayoutManager = new GridLayoutManager(getContext(), 4);

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        floatingActionButton = view.findViewById(R.id.fab_checklist);
        floatingActionButton.setOnClickListener(this);

        textViewEmpty = view.findViewById(R.id.text_empty_checklist);

        viewError = view.findViewById(R.id.layout_error_checklist);

        buttonTryAgain = view.findViewById(R.id.button_try_again_checklist);
        buttonTryAgain.setOnClickListener(this);

        viewLoading = view.findViewById(R.id.layout_loading_checklist);

        progressBarLoading = view.findViewById(R.id.progress_bar_checklist);
        progressBarLoading.setIndeterminateDrawable(new IndeterminateCircularProgressDrawable(getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        rvState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (rvState != null)
            recyclerView.getLayoutManager().onRestoreInstanceState(rvState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cancelRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelRequest();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_MONTH, mMonth);
        outState.putInt(ARG_YEAR, mYear);
        outState.putInt(ARG_FILTER, mFilter);
        outState.putInt(ARG_VIEW_VISIBLE, mViewVisible);
        outState.putBoolean(ARG_REFRESHING, mRefreshing);
        outState.putParcelableArrayList(ARG_MANGA_LIST, mMangaList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_checklists, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menuItemFilter = menu.getItem(0);

        SubMenu menuFilter = menuItemFilter.getSubMenu();

        for (int i = 0; i < menuFilter.size(); i++)
            if (menuFilter.getItem(i).getItemId() == mFilter)
                menuFilter.getItem(i).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_filter && item.getItemId() != mFilter) {

            mFilter = item.getItemId();

            ((MainActivity) getActivity()).setActualChecklistFilterId(mFilter);
            checklistParser = getCorrectParser(mFilter);
            item.setChecked(true);

            if (isDateAfterMinimum(checklistParser)) {
                mMonth = checklistParser.getMinimumMonth();
                mYear = checklistParser.getMinimumYear();
            }

            ((MainActivity) getActivity()).setActualChecklist(mMonth, mYear);

            retrieveMangas(mMonth, mYear);
        }

        return true;
    }

    private void cancelRequest() {
        if (checklistRequest != null && checklistRequest.getStatus() == ChecklistRequest.Status.RUNNING) {
            checklistRequest.cancel(true);
            checklistRequest = null;
        }
    }

    private void rvState() {
        rvState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    private boolean isDateAfterMinimum(ChecklistParser newParser) {
        Calendar newMinimumDate = Calendar.getInstance();
        Calendar actualDate = Calendar.getInstance();

        newMinimumDate.set(newParser.getMinimumYear(), newParser.getMinimumMonth() - 1, 0);
        actualDate.set(mYear, mMonth, 0);

        return newMinimumDate.after(actualDate);
    }


    public void showCorrectView(int viewId) {
        getActivity().runOnUiThread(() -> {
            mViewVisible = viewId;

            viewContent.setVisibility(viewId == STATE_CONTENT ? View.VISIBLE : View.GONE);
            viewError.setVisibility(viewId == STATE_ERROR ? View.VISIBLE : View.GONE);
            viewLoading.setVisibility(viewId == STATE_LOADING ? View.VISIBLE : View.GONE);

            if (menuItemFilter != null)
                menuItemFilter.setVisible(viewId == STATE_CONTENT);
        });
    }

    private void retrieveMangas(int month, int year) {
        checklistRequest = new ChecklistRequest(this, month, year, getCorrectParser(mFilter));
        checklistRequest.setReloading(mRefreshing);
        checklistRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void showChecklist(List<Manga> mangaList) {
        int oldView = mViewVisible;

        showCorrectView(mangaList == null ? STATE_ERROR : STATE_CONTENT);

        if (mangaList != null) {
            if (oldView == STATE_LOADING) {
                this.mMangaList.clear();
                this.mMangaList.addAll(mangaList);

                mangasAdapter.notifyDataSetChanged();
            }
            else {
                int size = this.mMangaList.size();
                this.mMangaList.clear();

                mangasAdapter.notifyItemRangeRemoved(0, size);

                this.mMangaList.addAll(mangaList);

                mangasAdapter.notifyItemRangeInserted(0, this.mMangaList.size());
            }

            swipeRefreshLayout.setRefreshing(false);

            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(0, 0);

            textViewEmpty.setVisibility(mangaList.size() == 0 ? View.VISIBLE : View.GONE);
        }

        mRefreshing = false;


    }

    private ChecklistParser getCorrectParser(int filter) {
        switch (filter) {
            case R.id.action_filter_jbc:
                return new JBCChecklistParser();
            case R.id.action_filter_panini:
                return new PaniniChecklistParser(getContext());
            case R.id.action_filter_newpop:
                return new NewPOPChecklistParser(getContext());
            default:
                return null;
        }
    }

    protected void initDateDialog() {
        MonthYearPickerDialog monthYearPickerDialog = MonthYearPickerDialog.newInstance(checklistParser, mMonth, mYear);
        monthYearPickerDialog.setListener(this);
        monthYearPickerDialog.show(getActivity().getSupportFragmentManager(), MonthYearPickerDialog.KEY);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_try_again_checklist) {
            viewLoading.setVisibility(View.VISIBLE);
            viewError.setVisibility(View.GONE);

            showCorrectView(STATE_LOADING);
        }
        else if (view.getId() == R.id.fab_checklist) {
            initDateDialog();
        }
    }

    @Override
    public void onRefresh() {
        mRefreshing = true;

        retrieveMangas(mMonth, mYear);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (mYear != year || mMonth != month) {
            mMonth = month;
            mYear = year;

            ((MainActivity) getActivity()).setActualChecklist(mMonth, mYear);

            retrieveMangas(month, year);
        }
    }
}
