package com.framgia.mysoundcloud.data.repository;

import com.framgia.mysoundcloud.data.model.Playlist;
import com.framgia.mysoundcloud.data.model.Track;
import com.framgia.mysoundcloud.data.source.TrackDataSource;
import com.framgia.mysoundcloud.data.source.local.TrackLocalDataSource;
import com.framgia.mysoundcloud.data.source.remote.TrackRemoteDataSource;

import java.util.List;


public class TrackRepository implements TrackDataSource.RemoteDataSource,
        TrackDataSource.LocalDataSource {

    private static TrackRepository sTrackRepository;

    private TrackDataSource.LocalDataSource mTrackLocalDataSource;
    private TrackDataSource.RemoteDataSource mTrackRemoteDataSource;

    private TrackRepository(LocalDataSource trackLocalDataSource,
                            RemoteDataSource trackRemoteDataSource) {
        mTrackLocalDataSource = trackLocalDataSource;
        mTrackRemoteDataSource = trackRemoteDataSource;
    }

    public static TrackRepository getInstance() {
        if (sTrackRepository == null) {
            sTrackRepository = new TrackRepository(
                    TrackLocalDataSource.getInstance(),
                    TrackRemoteDataSource.getInstance());
        }
        return sTrackRepository;

    }

    @Override
    public void getTracksLocal(OnFetchDataListener<Track> listener) {
        if (mTrackLocalDataSource != null) {
            mTrackLocalDataSource.getTracksLocal(listener);
        }
    }

    @Override
    public void searchTracksLocal(String trackName, OnFetchDataListener<Track> listener) {
        if (mTrackLocalDataSource != null) {
            mTrackLocalDataSource.searchTracksLocal(trackName, listener);
        }
    }

    @Override
    public boolean deleteTrack(Track track) {
        return mTrackLocalDataSource != null && mTrackLocalDataSource.deleteTrack(track);
    }

    @Override
    public void addTracksToPlaylist(int playlistId,
                                    OnHandleDatabaseListener listener, Track... tracks) {
        if (mTrackLocalDataSource == null) return;
        mTrackLocalDataSource.addTracksToPlaylist(playlistId, listener, tracks);
    }

    @Override
    public void deletePlaylist(Playlist playlist, String userId, OnHandleDatabaseListener listener) {
        if (mTrackLocalDataSource == null) return;
        mTrackLocalDataSource.deletePlaylist(playlist, userId, listener);
    }

    @Override
    public void addTracksToNewPlaylist(String idUser, String newPlaylistName,
                                       OnHandleDatabaseListener listener, Track... tracks) {
        if (mTrackLocalDataSource == null) return;
        mTrackLocalDataSource.addTracksToNewPlaylist(idUser, newPlaylistName, listener, tracks);
    }


    @Override
    public List<Playlist> getPlaylist() {
        if (mTrackLocalDataSource == null) return null;
        return mTrackLocalDataSource.getPlaylist();
    }

    @Override
    public void insertPlayList(String idUser, Playlist playlist, OnHandleDatabaseListener listener) {
        if (mTrackLocalDataSource == null) return;
        mTrackLocalDataSource.insertPlayList(idUser, playlist, listener);
    }

    @Override
    public void getTrackFavorite(String idUser, OnFetchDataListener<Track> listener) {
        if (mTrackLocalDataSource == null) return;
        mTrackLocalDataSource.getTrackFavorite(idUser, listener);
    }

    @Override
    public void deleteTrackFavorite(Track track, String idUser, OnHandleDatabaseListener listener) {
        if(mTrackLocalDataSource == null)return;
        mTrackLocalDataSource.deleteTrackFavorite(track, idUser, listener);
    }

    @Override
    public void editNamePlayList(Playlist playlist, String userId, OnHandleDatabaseListener listener) {
        if(mTrackLocalDataSource == null)return;
        mTrackLocalDataSource.editNamePlayList(playlist, userId, listener);
    }

    @Override
    public List<Playlist> getDetailPlaylistbyIdUser(String id) {
        if (mTrackLocalDataSource == null) return null;
        return mTrackLocalDataSource.getDetailPlaylistbyIdUser(id);
    }

    @Override
    public void getTracksRemote(String genre, int limit, int offSet,
                                OnFetchDataListener<Track> listener) {
        if (mTrackRemoteDataSource == null) return;
        mTrackRemoteDataSource.getTracksRemote(genre, limit, offSet, listener);
    }

    @Override
    public void searchTracksRemote(String trackName, int offSet,
                                   OnFetchDataListener<Track> listener) {
        if (mTrackRemoteDataSource != null) {
            mTrackRemoteDataSource.searchTracksRemote(trackName, offSet, listener);
        }
    }

    @Override
    public void addTracksToFavorite(String idUser, Track track, OnHandleDatabaseListener listener) {
        if (mTrackLocalDataSource == null) return;
        mTrackLocalDataSource.addTracksToFavorite(idUser, track, listener);
    }
}
