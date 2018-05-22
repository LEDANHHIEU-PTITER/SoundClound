package com.framgia.mysoundcloud.data.source.local;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.framgia.mysoundcloud.R;
import com.framgia.mysoundcloud.data.model.Playlist;
import com.framgia.mysoundcloud.data.model.Track;
import com.framgia.mysoundcloud.data.source.TrackDataSource;
import com.framgia.mysoundcloud.data.source.local.config.PlaylistTrackDbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class TrackLocalDataSource implements TrackDataSource.LocalDataSource {

    private static final String QUERY_DIRECTORY_NAME = "%MySoundCloud%";
    private static final String VOLUME_NAME_EXTERNAL = "external";
    private static TrackLocalDataSource sTrackLocalDataSource;

    private Context mContext;
    private PlaylistTrackDbHelper mPlaylistTrackDbHelper;

    public static void init(Context context) {
        if (sTrackLocalDataSource == null) {
            sTrackLocalDataSource = new TrackLocalDataSource(context);
        }
    }

    public static TrackLocalDataSource getInstance() {
        return sTrackLocalDataSource;
    }

    private TrackLocalDataSource(Context context) {
        mContext = context;
        mPlaylistTrackDbHelper = PlaylistTrackDbHelper.getInstance(context);
    }

    @Override
    public void getTracksLocal(OnFetchDataListener<Track> listener) {
        ArrayList<Track> tracks = new ArrayList<>();

        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null,
                MediaStore.Audio.Media.DATA + " LIKE ?",
                new String[]{QUERY_DIRECTORY_NAME}, null);

        if (cursor == null) {
            listener.onFetchDataFailure(mContext.getString(R.string.msg_load_downloaded_failed));
            return;
        }

        if (!cursor.moveToFirst()) {
            listener.onFetchDataSuccess(tracks);
            return;
        }

        // Read data
        int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int filePathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

        do {
            Track track = new Track();
            track.setTitle(cursor.getString(titleColumn));
            track.setUri(cursor.getString(filePathColumn));
            track.setFullDuration(cursor.getInt(durationColumn));
            track.setId(cursor.getInt(idColumn));
            tracks.add(track);
        } while (cursor.moveToNext());

        listener.onFetchDataSuccess(tracks);
    }

    @Override
    public void searchTracksLocal(String trackName, OnFetchDataListener<Track> listener) {
    }

    @Override
    // TODO: 4/25/2018 delete track 
    public boolean deleteTrack(Track track) {
        File file = new File(track.getUri());
        String where = MediaStore.MediaColumns.DATA + "=?";
        String[] selectionArgs = new String[]{file.getAbsolutePath()};
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri filesUri = MediaStore.Files.getContentUri(VOLUME_NAME_EXTERNAL);
        contentResolver.delete(filesUri, where, selectionArgs);
        return file.delete();
    }

    @Override
    public void addTracksToPlaylist(int playlistId,
                                    OnHandleDatabaseListener listener, Track... tracks) {
        if (mPlaylistTrackDbHelper == null || tracks == null || tracks.length == 0) {
            if (listener == null) return;
            listener.onHandleFailure("fail");
            return;
        }

        for (int i = 0; i < tracks.length; i++) {
            mPlaylistTrackDbHelper.insertTrack(tracks[i], listener);
            mPlaylistTrackDbHelper.insertToTablePlaylistHasTrack(tracks[i].getId(), playlistId, listener);
        }
    }
    // Delete PlayList
    @Override
    public void deletePlaylist(Playlist playlist ,String userId, OnHandleDatabaseListener listener) {
           mPlaylistTrackDbHelper.deletePlayList(playlist,userId,listener);
    }

    public void addPlaylistByIdUser(final String idUser,
                                    final OnHandleDatabaseListener listener, String playlist) {
        if (mPlaylistTrackDbHelper == null || playlist == null || playlist == null) {
            listener.onHandleFailure("fail");
            return;
        }
        mPlaylistTrackDbHelper.insertPlaylist(playlist, new OnHandleDatabaseListener() {
            @Override
            public void onHandleSuccess(String playlistId) {
                final int idPlayList = Integer.parseInt(playlistId);
                mPlaylistTrackDbHelper.insertToTableUserHasPlayList(idPlayList, idUser, new OnHandleDatabaseListener() {
                    @Override
                    public void onHandleSuccess(String message) {
                        listener.onHandleSuccess(String.valueOf(idPlayList));
                    }

                    @Override
                    public void onHandleFailure(String message) {
                        listener.onHandleFailure(message);
                    }
                });
            }

            @Override
            public void onHandleFailure(String message) {
                listener.onHandleFailure(message);
            }
        });
    }

    @Override
    public void addTracksToNewPlaylist(String idUser, String newPlaylistName,
                                       final OnHandleDatabaseListener listener, final Track... tracks) {
        if (mPlaylistTrackDbHelper == null || tracks == null
                || tracks.length == 0 || newPlaylistName == null) {
            if (listener == null) return;
            listener.onHandleFailure("fail");
            return;
        }
        addPlaylistByIdUser(idUser, new OnHandleDatabaseListener() {
            @Override
            public void onHandleSuccess(String newPlaylistId) {
                addTracksToPlaylist(Integer.parseInt(newPlaylistId), new OnHandleDatabaseListener() {
                    @Override
                    public void onHandleSuccess(String message) {
                        listener.onHandleSuccess(message);
                    }

                    @Override
                    public void onHandleFailure(String message) {
                        listener.onHandleFailure(message);
                    }
                }, tracks);
            }

            @Override
            public void onHandleFailure(String message) {
                listener.onHandleFailure(message);
            }
        }, newPlaylistName);
    }


    @Override
    public List<Playlist> getPlaylist() {
        if (mPlaylistTrackDbHelper == null) return null;
        return mPlaylistTrackDbHelper.getAllPlaylist();
    }

    @Override
    public void insertPlayList(String idUsder, Playlist playlist, final OnHandleDatabaseListener listener) {
        if (playlist == null) return;
        addPlaylistByIdUser(idUsder, listener, playlist.getName());

    }

    @Override
    public List<Playlist> getDetailPlaylistbyIdUser(String id) {
        List<Playlist> playlistsResult = new ArrayList<>();
        List<Integer> idPlaylists = mPlaylistTrackDbHelper.getPlaylistByIdUser(id);
        if (mPlaylistTrackDbHelper == null) return null;
        List<Playlist> playlists = getPlaylist();
        if (playlists == null) return null;
        for (Playlist playlist : playlists) {
            List<Track> tracks = mPlaylistTrackDbHelper.getTracksWithPlaylistId(playlist.getId());
            playlist.setTracks(tracks);
        }
        for (int i = 0; i < playlists.size(); i++) {
            for (int j = 0; j < idPlaylists.size(); j++) {
                if (playlists.get(i).getId() == idPlaylists.get(j)) {
                    playlistsResult.add(playlists.get(i));
                }
            }
        }
        return playlistsResult;
    }


    @Override
    public void addTracksToFavorite(String idUser, Track track, OnHandleDatabaseListener listener) {
        if (mPlaylistTrackDbHelper == null || track == null) return;
        mPlaylistTrackDbHelper.addTrackFavorite(track, idUser, listener);
    }

    @Override
    public void getTrackFavorite(String idUser, OnFetchDataListener<Track> listener) {
        if (mPlaylistTrackDbHelper == null) return;
        mPlaylistTrackDbHelper.getTrackFavorite(idUser, listener);
    }

    @Override
    public void deleteTrackFavorite(Track track, String idUser, OnHandleDatabaseListener listener) {
        if(mPlaylistTrackDbHelper == null) return;
        mPlaylistTrackDbHelper.deleteTrackFavorite(track, idUser, listener);
    }

    @Override
    public void editNamePlayList(Playlist playlist, String userId, OnHandleDatabaseListener listener) {
        mPlaylistTrackDbHelper.editNamePlayList(playlist, userId, listener);
    }
}
