package com.framgia.mysoundcloud.screen.musicgenres;

import com.framgia.mysoundcloud.screen.BasePresenter;
import com.framgia.mysoundcloud.screen.BaseView;


interface MusicGenresContract {
    /**
     * View.
     */
    interface View extends BaseView {
        void updateTextNumberTrack();
    }

    /**
     * Presenter.
     */
    interface Presenter extends BasePresenter<MusicGenresContract.View> {
        void loadTrack(String genre, int limit, int offSet);
    }
}
