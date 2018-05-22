package com.framgia.mysoundcloud.screen.playlistdetail;

import com.framgia.mysoundcloud.data.model.Track;
import com.framgia.mysoundcloud.screen.BasePresenter;
import com.framgia.mysoundcloud.screen.BaseView;


public interface PlayListDetailViewContract {
    /**
     * View.
     */
    interface View extends BaseView {
        void showMessage(String message);
    }

    /**
     * Presenter.
     */
    interface Presenter extends BasePresenter<PlayListDetailViewContract.View> {
        void loadTrack(String flag);

        void deleteTrackFavorie(Track track);

        public void deleteTrackLocal(Track track);
    }

    interface DeleteTrackListener {
        void onDeleteClicked(Track track);
    }
}
