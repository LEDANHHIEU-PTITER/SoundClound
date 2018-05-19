package com.framgia.mysoundcloud.screen.playlist;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.framgia.mysoundcloud.R;
import com.framgia.mysoundcloud.data.model.Playlist;
import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.data.repository.TrackRepository;
import com.framgia.mysoundcloud.data.source.TrackDataSource;
import com.framgia.mysoundcloud.data.source.local.SharePreferences;
import com.framgia.mysoundcloud.screen.main.MainViewConstract;
import com.framgia.mysoundcloud.utils.Constant;
import com.framgia.mysoundcloud.widget.DialogManager;
import com.framgia.mysoundcloud.widget.DialogManagerInterface;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment implements PlaylistContract.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, PlaylistAdapter.CallBack {

    private static PlaylistFragment sPlaylistFragment;

    private List<Playlist> mPlaylists;
    private PlaylistContract.Presenter mPresenter;
    private MainViewConstract.TrackListListener mListener;
    private PlaylistAdapter mPlaylistAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton btnAddPlaylist;
    private AlertDialog alertDialog;
    private DialogManager mDialogManager;

    public static PlaylistFragment newInstance(MainViewConstract.TrackListListener listener) {
        if (sPlaylistFragment == null) {
            sPlaylistFragment = new PlaylistFragment();
            Bundle args = new Bundle();
            args.putParcelable(Constant.ARGUMENT_TRACK_LIST_LISTENER, listener);
            sPlaylistFragment.setArguments(args);
        }
        return sPlaylistFragment;
    }

    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        mPresenter = new PlaylistPresenter();
        mPresenter.setView(this);

        mListener = getArguments().getParcelable(Constant.ARGUMENT_TRACK_LIST_LISTENER);

        setupUI(view);
        mPresenter.loadPlaylist();
        mDialogManager = new DialogManager(getContext());
        return view;
    }

    @Override
    public void showPlaylist(List<Playlist> playlists) {
        mPlaylists = playlists;
        mPlaylistAdapter.replaceData(playlists);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

    }

    private void setupUI(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        mPlaylistAdapter = new PlaylistAdapter(getContext(), mListener , this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mPlaylistAdapter);
        btnAddPlaylist = view.findViewById(R.id.add_playlist);
        btnAddPlaylist.setOnClickListener(this);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        mPresenter.loadPlaylist();
    }

    @Override
    public void onClick(View view) {
        showDialogAddAlbum();
    }

    private void showDialogAddAlbum() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Playlist");
        builder.setMessage("Name Playlist");
        final EditText input = new EditText(builder.getContext());
        builder.setView(input);
        builder.setPositiveButton("Add ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(
                            getContext(), R.string.msg_err_name_null, Toast.LENGTH_SHORT).show();
                } else {
                    Playlist playlist = new Playlist();
                    playlist.setName(name);
                    User user = SharePreferences.getInstance().getUser();
                    if (user == null) return;
                    TrackRepository.getInstance().insertPlayList(user.getId(), playlist, new TrackDataSource.OnHandleDatabaseListener() {
                        @Override
                        public void onHandleSuccess(String message) {
                            updateUI();
                        }

                        @Override
                        public void onHandleFailure(String message) {

                        }
                    });
                }
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Cancel", null);
        alertDialog = builder.show();

    }

    private void updateUI() {
        mPresenter.loadPlaylist();
    }

    @Override
    public void onItemMoreClick(Playlist playlist , View view) {
        if(playlist == null) return;
        showPopupMenu(playlist , view);
    }

    private void showPopupMenu(final Playlist playlist, View view) {
        final PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.options_menu_item_playlist);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit_name_playlist:

                        return true;
                    case R.id.action_delete_playlist:
                        showDialogConfirmDeletePlaylist(playlist);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void showDialogConfirmDeletePlaylist(final Playlist playlist) {
        mDialogManager.dialogButton(getString(R.string.msg_delete_playlist),
                "Delete", "Yes",
                "No",
                new DialogManagerInterface.DialogListener() {
                    @Override
                    public void onDialogPositiveClick() {
                        User user = SharePreferences.getInstance().getUser();
                        if(user != null) {
                            mPresenter.deletePlayList(playlist, user.getId());
                        }
                    }

                    @Override
                    public void onDialogNegativeClick() {

                    }
                });
    }


}
