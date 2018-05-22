package com.framgia.mysoundcloud.screen.playlist;

import com.framgia.mysoundcloud.data.model.Playlist;
import com.framgia.mysoundcloud.screen.BasePresenter;

import java.util.List;



public interface PlaylistContract {
    /**
     * View.
     */
    interface View {
        void showPlaylist(List<Playlist> playlists);

        void showMessage(String message);
    }

    /**
     * Presenter.
     */
    interface Presenter extends BasePresenter<PlaylistContract.View> {
        void loadPlaylist();

        void deletePlayList(Playlist playlist, String id);

        void editPlayList(Playlist playlist, String id);
    }
}
