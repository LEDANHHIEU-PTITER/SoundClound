package com.framgia.mysoundcloud.screen.playlistdetail;

import com.framgia.mysoundcloud.data.model.Track;
import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.data.repository.TrackRepository;
import com.framgia.mysoundcloud.data.source.TrackDataSource;
import com.framgia.mysoundcloud.data.source.local.SharePreferences;
import com.framgia.mysoundcloud.utils.Constant;

import java.util.ArrayList;
import java.util.List;



public class PlaylistDetailPresenter implements PlayListDetailViewContract.Presenter,
        TrackDataSource.OnFetchDataListener<Track> {

    private PlayListDetailViewContract.View mView;

    @Override
    public void setView(PlayListDetailViewContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void loadTrack(String flag) {
        if(flag.equals(Constant.FAVORITE)){
            // TODO: 4/28/2018 idUser
            String idUser  = SharePreferences.getInstance().getUser().getId();
            TrackRepository.getInstance().getTrackFavorite(idUser ,this );
        }
        if(flag.equals(Constant.DOWLOAD)){
            TrackRepository.getInstance().getTracksLocal(this);
        }

    }

    @Override
    public void deleteTrackFavorie(Track track) {
        User user = SharePreferences.getInstance().getUser();
        if(user == null)return;
        String idUser = user.getId();
        TrackRepository.getInstance().deleteTrackFavorite(track, idUser, new TrackDataSource.OnHandleDatabaseListener() {
            @Override
            public void onHandleSuccess(String message) {
                mView.showMessage(message);
                loadTrack(Constant.FAVORITE);
            }

            @Override
            public void onHandleFailure(String message) {
                mView.showMessage(message);
            }
        });
    }
    @Override
    public void deleteTrackLocal(Track track) {
       if(TrackRepository.getInstance().deleteTrack(track)){
           loadTrack(Constant.DOWLOAD);
       }

    }

    @Override
    public void onFetchDataSuccess(List<Track> data) {
        mView.hideLoadingIndicator();

        if (data == null || data.isEmpty()) {
            mView.showNoTrack();
        } else {
            mView.showTracks((ArrayList<Track>) data);
        }
    }

    @Override
    public void onFetchDataFailure(String message) {
        mView.hideLoadingIndicator();
        mView.showLoadingTracksError(message);
    }
}
