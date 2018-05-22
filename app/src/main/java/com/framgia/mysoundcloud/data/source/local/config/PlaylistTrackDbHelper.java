package com.framgia.mysoundcloud.data.source.local.config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.framgia.mysoundcloud.data.model.Playlist;
import com.framgia.mysoundcloud.data.model.PublisherMetadata;
import com.framgia.mysoundcloud.data.model.Track;
import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.data.source.TrackDataSource;
import com.framgia.mysoundcloud.data.source.UserDataSource;
import com.framgia.mysoundcloud.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import static com.framgia.mysoundcloud.data.source.local.config.PlaylistTrackDbHelper.PlaylistEntry.COLUMN_NAME_PLAYLIST_ID;
import static com.framgia.mysoundcloud.data.source.local.config.PlaylistTrackDbHelper.PlaylistEntry.COLUMN_NAME_TRACK_ID;
import static com.framgia.mysoundcloud.data.source.local.config.PlaylistTrackDbHelper.PlaylistEntry.TABLE_NAME_FAVORITE;
import static com.framgia.mysoundcloud.data.source.local.config.PlaylistTrackDbHelper.PlaylistEntry.TABLE_NAME_PLAYLIST;
import static com.framgia.mysoundcloud.data.source.local.config.PlaylistTrackDbHelper.PlaylistEntry.TABLE_NAME_TRACK;
import static com.framgia.mysoundcloud.data.source.local.config.PlaylistTrackDbHelper.PlaylistEntry.TABLE_NAME_USER_HAS_PLAYLIST;

public class PlaylistTrackDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_PLAYLIST_ENTRIES = "CREATE TABLE "
            + TABLE_NAME_PLAYLIST
            + " ( "
            + PlaylistEntry.COLUMN_NAME_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PlaylistEntry.COLUMN_NAME_PLAYLIST + " TEXT NOT NULL"
            + " );";
    private static final String SQL_CREATE_USER_ENTRIES = "CREATE TABLE "
            + User.UserEntry.TABLE_NAME_USER
            + " ( "
            + User.UserEntry.COLUMN_NAME_USER_ID + " TEXT PRIMARY KEY , "
            + User.UserEntry.COLUMN_NAME_USER + " TEXT ,"
            + User.UserEntry.COLUMN_EMAIL + " TEXT   ,"
            + User.UserEntry.COLUMN_IMAGE + " TEXT ,"
            + User.UserEntry.COLUMN_TOKEN + " TEXT "
            + " );";
    private static final String SQL_CREATE_TRACK_ENTRIES = "CREATE TABLE "
            + TABLE_NAME_TRACK + " ( "
            + Track.TrackEntity.ARTWORK_URL + " TEXT, "
            + Track.TrackEntity.DESCRIPTION + " TEXT, "
            + Track.TrackEntity.DOWNLOADABLE + " INTEGER, "
            + Track.TrackEntity.DOWNLOAD_URL + " TEXT, "
            + Track.TrackEntity.DURATION + " INTEGER, "
            + PlaylistEntry.COLUMN_NAME_TRACK_ID + " INTEGER NOT NULL, "
            + Track.TrackEntity.PLAYBACK_COUNT + " INTEGER, "
            + Track.TrackEntity.TITLE + " TEXT, "
            + Track.TrackEntity.URI + " TEXT, "
            + Track.TrackEntity.USERNAME + " TEXT, "
            + Track.TrackEntity.LIKES_COUNT + " INTEGER, "
            + " PRIMARY KEY( "
            + PlaylistEntry.COLUMN_NAME_TRACK_ID
            + " )"
            + ");";

    private static final String SQL_CREATE_PLAYLIST_HAS_TRACK = "CREATE TABLE "
            + PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK
            + " ( "
            + PlaylistEntry.COLUMN_NAME_PLAYLIST_ID + " INTEGER NOT NULL,"
            + PlaylistEntry.COLUMN_NAME_TRACK_ID + " INTEGER NOT NULL, "
            + "PRIMARY KEY( "
            + PlaylistEntry.COLUMN_NAME_TRACK_ID + " , "
            + PlaylistEntry.COLUMN_NAME_PLAYLIST_ID
            + " ) "
            + ");";
    private static final String SQL_CREATE_FAVORITE = "CREATE TABLE "
            + TABLE_NAME_FAVORITE
            + " ( "
            + User.UserEntry.COLUMN_NAME_USER_ID + " TEXT NOT NULL,"
            + PlaylistEntry.COLUMN_NAME_TRACK_ID + " INTEGER NOT NULL, "
            + "PRIMARY KEY( "
            + PlaylistEntry.COLUMN_NAME_TRACK_ID + " , "
            + User.UserEntry.COLUMN_NAME_USER_ID
            + " ) "
            + ");";
    private static final String SQL_CREATE_HAS_PLAYLIST = "CREATE TABLE "
            + TABLE_NAME_USER_HAS_PLAYLIST
            + " ( "
            + User.UserEntry.COLUMN_NAME_USER_ID + " TEXT NOT NULL,"
            + COLUMN_NAME_PLAYLIST_ID + " INTEGER NOT NULL, "
            + "PRIMARY KEY( "
            + COLUMN_NAME_PLAYLIST_ID + " , "
            + User.UserEntry.COLUMN_NAME_USER_ID
            + " ) "
            + ");";
    private static PlaylistTrackDbHelper sPlaylistTrackDbHelper;

    public static PlaylistTrackDbHelper getInstance(@NonNull Context context) {
        if (sPlaylistTrackDbHelper == null) {
            sPlaylistTrackDbHelper = new PlaylistTrackDbHelper(context);
        }
        return sPlaylistTrackDbHelper;
    }

    private PlaylistTrackDbHelper(Context context) {
        super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLAYLIST_ENTRIES);
        db.execSQL(SQL_CREATE_TRACK_ENTRIES);
        db.execSQL(SQL_CREATE_PLAYLIST_HAS_TRACK);
        db.execSQL(SQL_CREATE_USER_ENTRIES);
        db.execSQL(SQL_CREATE_FAVORITE);
        db.execSQL(SQL_CREATE_HAS_PLAYLIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertTrack(Track track, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Track.TrackEntity.ARTWORK_URL, track.getArtworkUrl());
        values.put(Track.TrackEntity.DESCRIPTION, track.getDescription());
        if (track.isDownloadable()) {
            values.put(Track.TrackEntity.DOWNLOADABLE, 1);
        } else {
            values.put(Track.TrackEntity.DOWNLOADABLE, 0);
        }
        values.put(Track.TrackEntity.DOWNLOAD_URL, track.getDowloadUrl());
        values.put(Track.TrackEntity.DURATION, track.getFullDuration());
        values.put(Track.TrackEntity.ID, track.getId());
        values.put(Track.TrackEntity.PLAYBACK_COUNT, track.getDownloadCount());
        values.put(Track.TrackEntity.TITLE, track.getTitle());
        values.put(Track.TrackEntity.URI, track.getUri());
        if (track.getPublisherMetadata() != null) {
            values.put(Track.TrackEntity.USERNAME, track.getPublisherMetadata().getArtist());
        }
        values.put(Track.TrackEntity.LIKES_COUNT, track.getLikesCount());
        try {
            long a = database.insert(TABLE_NAME_TRACK, null, values);
        } catch (SQLException e) {
            listener.onHandleFailure(e.getMessage());
        }
    }

    public void insertPlaylist(String namePlaylist, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlaylistEntry.COLUMN_NAME_PLAYLIST, namePlaylist);
        long index = database.insert(TABLE_NAME_PLAYLIST, null, values);
        if (index > 0)
            listener.onHandleSuccess(String.valueOf(index));
        else {
            listener.onHandleFailure("failure");
        }
    }

    public void insertPlaylist(String namePlaylist, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(PlaylistEntry.COLUMN_NAME_PLAYLIST, namePlaylist);
        long index = database.insert(TABLE_NAME_PLAYLIST, null, values);
    }

    public void insertToTablePlaylistHasTrack(int trackId, int playlistId, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlaylistEntry.COLUMN_NAME_PLAYLIST_ID, playlistId);
        values.put(PlaylistEntry.COLUMN_NAME_TRACK_ID, trackId);
        try {
            long a = database.insert(PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK, null, values);
            if (a > 0)
                listener.onHandleSuccess("Add to playlist");
            else {
                listener.onHandleFailure("This track is exist !!");
            }
        } catch (SQLException e) {
            listener.onHandleFailure(e.getMessage());
        }
    }

    public List<Playlist> getAllPlaylist() {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.query(TABLE_NAME_PLAYLIST, null,
                null, null, null, null, null);

        List<Playlist> playlists = new ArrayList<>();
        while (cursor.moveToNext()) {
            Playlist playlist = parseCursorToPlayList(cursor);
            playlists.add(playlist);
        }

        cursor.close();
        database.close();

        return playlists;
    }

    private Playlist parseCursorToPlayList(Cursor cursor) {
        Playlist playlist = new Playlist();
        playlist.setName(cursor.getString(
                cursor.getColumnIndexOrThrow(PlaylistEntry.COLUMN_NAME_PLAYLIST)));
        playlist.setId(cursor.getInt(
                cursor.getColumnIndexOrThrow(PlaylistEntry.COLUMN_NAME_PLAYLIST_ID)));
        return playlist;
    }

    public void insertToTableUserHasPlayList(int idPlayList, String idUser, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_PLAYLIST_ID, idPlayList);
        values.put(User.UserEntry.COLUMN_NAME_USER_ID, idUser);
        try {
            long a = database.insert(TABLE_NAME_USER_HAS_PLAYLIST, null, values);
            if (a > 0)
                listener.onHandleSuccess("Add to playlist");
            else {
                listener.onHandleFailure("This track is exist !!");
            }
        } catch (SQLException e) {
            listener.onHandleFailure(e.getMessage());
        }
    }

    public List<Track> getTracksWithPlaylistId(int playlistId) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "
                        + PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK
                        + " , "
                        + TABLE_NAME_TRACK
                        + " WHERE "
                        + PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK + "."
                        + PlaylistEntry.COLUMN_NAME_PLAYLIST_ID
                        + " = "
                        + playlistId
                        + " AND "
                        + TABLE_NAME_TRACK + "." + Track.TrackEntity.ID
                        + " = "
                        + PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK + "."
                        + PlaylistEntry.COLUMN_NAME_TRACK_ID
                , null);
        String s = "SELECT * FROM "
                + PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK
                + " , "
                + TABLE_NAME_TRACK
                + " WHERE "
                + PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK + "."
                + PlaylistEntry.COLUMN_NAME_PLAYLIST_ID
                + " = "
                + playlistId
                + " AND "
                + TABLE_NAME_TRACK + "." + Track.TrackEntity.ID
                + " = "
                + PlaylistEntry.TABLE_NAME_PLAYLIST_HAS_TRACK + "."
                + PlaylistEntry.COLUMN_NAME_TRACK_ID;

        if (cursor == null) return null;

        List<Track> tracks = new ArrayList<>();
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                Track track = parseTrackFromCusor(cursor);
                if (track != null) {
                    tracks.add(track);
                }
            } while (cursor.moveToNext());
        }
        return tracks;
    }

    private Track parseTrackFromCusor(Cursor cursor) {
        Track track = new Track();

        int downloadable = cursor.getInt(
                cursor.getColumnIndexOrThrow(Track.TrackEntity.DOWNLOADABLE));

        if (downloadable == 1) {
            track.setDownloadable(true);
        } else {
            track.setDownloadable(false);
        }

        track.setId(cursor.getInt(cursor.getColumnIndexOrThrow(Track.TrackEntity.ID)));
        track.setFullDuration(cursor.getInt(cursor.getColumnIndexOrThrow(Track.TrackEntity.DURATION)));
        track.setUri(cursor.getString(cursor.getColumnIndexOrThrow(Track.TrackEntity.URI)));
        track.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Track.TrackEntity.TITLE)));
        track.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(Track.TrackEntity.DESCRIPTION)));
        track.setArtworkUrl(cursor.getString(cursor.getColumnIndexOrThrow(Track.TrackEntity.ARTWORK_URL)));
        track.setLikesCount(cursor.getInt(cursor.getColumnIndexOrThrow(Track.TrackEntity.LIKES_COUNT)));
        track.setDownloadCount(cursor.getInt(cursor.getColumnIndexOrThrow(Track.TrackEntity.PLAYBACK_COUNT)));
        track.setDowloadUrl(cursor.getString(cursor.getColumnIndexOrThrow(Track.TrackEntity.DOWNLOAD_URL)));
        PublisherMetadata publisherMetadata = new PublisherMetadata(cursor.getString(cursor.getColumnIndexOrThrow(Track.TrackEntity.USERNAME)));
        track.setPublisherMetadata(publisherMetadata);

        return track;
    }

    public void addUser(User user, UserDataSource.ResultCallBack<User> callBack) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(User.UserEntry.COLUMN_NAME_USER_ID, user.getId());
        values.put(User.UserEntry.COLUMN_NAME_USER, user.getName());
        values.put(User.UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(User.UserEntry.COLUMN_IMAGE, user.getImage());
        values.put(User.UserEntry.COLUMN_TOKEN, user.getToken());
        try {
            long a = database.insert(User.UserEntry.TABLE_NAME_USER, null, values);
            callBack.onSuccess(user);
        } catch (SQLException e) {
            callBack.onFailure(e.getMessage());
        }
    }

    public void getUserbyId(String id, UserDataSource.ResultCallBack<User> callBack) {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.query(User.UserEntry.TABLE_NAME_USER,
                null,
                User.UserEntry.COLUMN_NAME_USER_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user = parseCursorToUser(cursor);
            callBack.onSuccess(user);
        } else {
            callBack.onFailure("null");
        }

    }

    private User parseCursorToUser(Cursor cursor) {
        User user = new User();
        int indexName = cursor.getColumnIndex(User.UserEntry.COLUMN_NAME_USER);
        int indexId = cursor.getColumnIndex(User.UserEntry.COLUMN_NAME_USER_ID);
        int indexImage = cursor.getColumnIndex(User.UserEntry.COLUMN_IMAGE);
        int indexEmail = cursor.getColumnIndex(User.UserEntry.COLUMN_EMAIL);
        int indexToken = cursor.getColumnIndex(User.UserEntry.COLUMN_TOKEN);
        user.setId(cursor.getString(indexId));
        user.setName(cursor.getString(indexName));
        user.setImage(cursor.getString(indexImage));
        user.setToken(cursor.getString(indexToken));
        user.setEmail(cursor.getString(indexEmail));
        return user;
    }

    public void addTrackFavorite(Track track, String idUser, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TRACK_ID, track.getId());
        values.put(User.UserEntry.COLUMN_NAME_USER_ID, idUser);
        try {
            insertTrack(track, listener);
            long a = database.insert(TABLE_NAME_FAVORITE, null, values);
            if (a > 0)
                listener.onHandleSuccess("Add to favorite");
            else {
                listener.onHandleFailure("This track is exist !!");
            }
        } catch (SQLException e) {
            listener.onHandleFailure(e.getMessage());
        }
    }

    //By Hieu
    public void editNamePlayList(Playlist playlist, String userId, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(PlaylistEntry.COLUMN_NAME_PLAYLIST, playlist.getName());
            long a = database.update(TABLE_NAME_PLAYLIST, values,
                    PlaylistEntry.COLUMN_NAME_PLAYLIST_ID + " = ? ",
                    new String[]{String.valueOf(playlist.getId())});
            if (a > 0)
                listener.onHandleSuccess("Update " + playlist.getName() + "success !");
            else {
                listener.onHandleFailure("Error !!");
            }
        } catch (SQLException e) {
            listener.onHandleFailure(e.getMessage());
        }
    }

    //By Hieu
    public void deletePlayList(Playlist playlist, String userId, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        try {
            long a = database.delete(TABLE_NAME_USER_HAS_PLAYLIST,
                    User.UserEntry.COLUMN_NAME_USER_ID + " = ? AND " + COLUMN_NAME_PLAYLIST_ID + " = ?",
                    new String[]{userId, String.valueOf(playlist.getId())});
            if (a > 0)
                listener.onHandleSuccess("Delete " + playlist.getName() + "success !");
            else {
                listener.onHandleFailure("Error !!");
            }
        } catch (SQLException e) {
            listener.onHandleFailure(e.getMessage());
        }
    }

    // By Hieu
    public void deleteTrackFavorite(Track track, String idUser, TrackDataSource.OnHandleDatabaseListener listener) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TRACK_ID, track.getId());
        values.put(User.UserEntry.COLUMN_NAME_USER_ID, idUser);
        try {
            long a = database.delete(TABLE_NAME_FAVORITE,
                    COLUMN_NAME_TRACK_ID + " = ? AND +" + User.UserEntry.COLUMN_NAME_USER_ID + " = ?",
                    new String[]{String.valueOf(track.getId()), String.valueOf(idUser)});
            if (a > 0)
                listener.onHandleSuccess("Delete " + track.getTitle() + "success !");
            else {
                listener.onHandleFailure("Error !!");
            }
        } catch (SQLException e) {
            listener.onHandleFailure(e.getMessage());
        }
    }

    public void getTrackFavorite(String idUser, TrackDataSource.OnFetchDataListener<Track> listener) {
        List<Track> tracksResult = new ArrayList<>();
        List<Track> tracks = getAllTrack();
        List<Integer> idTracks = getFavoritebyIdUser(idUser);
        for (int i = 0; i < tracks.size(); i++) {
            for (int j = 0; j < idTracks.size(); j++) {
                if (tracks.get(i).getId() == idTracks.get(j)) {
                    tracksResult.add(tracks.get(i));
                }
            }
        }
//        Cursor cursor = database.rawQuery("SELECT * FROM "
//                + TABLE_NAME_FAVORITE
//                + " , "
//                + TABLE_NAME_TRACK
//                + " WHERE "
//                + TABLE_NAME_FAVORITE + "."
//                + User.UserEntry.COLUMN_NAME_USER_ID
//                + " = "
//                + idUser
//                + " AND "
//                + TABLE_NAME_TRACK + "." + Track.TrackEntity.ID
//                + " = "
//                + TABLE_NAME_FAVORITE + "."
//                + PlaylistEntry.COLUMN_NAME_TRACK_ID, null);
//
//        if (cursor == null) {
//            listener.onFetchDataFailure("null");
//        }
//
//        List<Track> tracks = new ArrayList<>();
//        if (cursor.moveToFirst() && cursor.getCount() > 0) {
//            do {
//                Track track = parseTrackFromCusor(cursor);
//                if (track != null) {
//                    tracks.add(track);
//                }
//            } while (cursor.moveToNext());
//        }
        listener.onFetchDataSuccess(tracksResult);
    }

    public List<Track> getAllTrack() {
        List<Track> tracks = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME_TRACK, null, null, null, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                Track track = parseTrackFromCusor(cursor);
                if (track != null) {
                    tracks.add(track);
                }
            } while (cursor.moveToNext());
        }
        return tracks;
    }

    public List<Integer> getFavoritebyIdUser(String idUser) {
        List<Integer> tracks = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME_FAVORITE, null, User.UserEntry.COLUMN_NAME_USER_ID + "=?", new String[]{idUser}, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                Integer idTrack = cursor.getInt(cursor.getColumnIndex(Track.TrackEntity.ID));
                tracks.add(idTrack);
            } while (cursor.moveToNext());
        }
        return tracks;
    }

    public List<Integer> getPlaylistByIdUser(String id) {
        SQLiteDatabase database = getReadableDatabase();
        List<Integer> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME_USER_HAS_PLAYLIST, null, User.UserEntry.COLUMN_NAME_USER_ID + "=?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                int idPlaylist = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_PLAYLIST_ID));
                list.add(idPlaylist);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * Inner class that defines the table playlist contents
     */
    public static final class PlaylistEntry {
        public static final String TABLE_NAME_PLAYLIST = "Playlists";
        public static final String TABLE_NAME_TRACK = "Tracks";
        public static final String TABLE_NAME_PLAYLIST_HAS_TRACK = "PlaylistTrack";
        public static final String COLUMN_NAME_PLAYLIST = "playlist_name";
        public static final String COLUMN_NAME_PLAYLIST_ID = "playlist_id";
        public static final String COLUMN_NAME_TRACK_ID = "track_id";
        public static final String TABLE_NAME_USER_HAS_PLAYLIST = "UserPlayList";
        public static final String TABLE_NAME_FAVORITE = "Favorite";
    }
}
