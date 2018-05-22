package com.framgia.mysoundcloud.screen.playlist;

import com.framgia.mysoundcloud.data.model.Playlist;
import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.data.repository.TrackRepository;
import com.framgia.mysoundcloud.data.source.TrackDataSource;
import com.framgia.mysoundcloud.data.source.local.SharePreferences;

import java.util.List;


public class PlaylistPresenter implements PlaylistContract.Presenter, TrackDataSource.OnFetchDataListener<Playlist> {

    private PlaylistContract.View mView;

    @Override
    public void setView(PlaylistContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void loadPlaylist() {
        User user = SharePreferences.getInstance().getUser() ;
        if(user != null){
            String id = user.getId();
            onFetchDataSuccess(TrackRepository.getInstance().getDetailPlaylistbyIdUser(id));

        }
    }

    @Override
    public void deletePlayList(Playlist playlist, String id) {
        TrackRepository.getInstance().deletePlaylist(playlist, id, new TrackDataSource.OnHandleDatabaseListener() {
            @Override
            public void onHandleSuccess(String message) {
                mView.showMessage(message);
                loadPlaylist();
            }

            @Override
            public void onHandleFailure(String message) {
                mView.showMessage(message);
            }
        });
    }

    @Override
    public void editPlayList(Playlist playlist, String id) {
        TrackRepository.getInstance().editNamePlayList(playlist, id, new TrackDataSource.OnHandleDatabaseListener() {
            @Override
            public void onHandleSuccess(String message) {
                loadPlaylist();
            }

            @Override
            public void onHandleFailure(String message) {
                mView.showMessage(message);
            }
        });
    }

    @Override
    public void onFetchDataSuccess(List<Playlist> data) {
        mView.showPlaylist(data);
    }

    @Override
    public void onFetchDataFailure(String message) {
    }
}
