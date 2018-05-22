package com.framgia.mysoundcloud.data.source;

import com.framgia.mysoundcloud.data.model.Playlist;
import com.framgia.mysoundcloud.data.model.Track;

import java.util.List;



public interface TrackDataSource {

    /**
     * LocalData For Tracks
     */
    interface LocalDataSource extends TrackDataSource {
        // Lấy Track từ Local
        void getTracksLocal(OnFetchDataListener<Track> listener);
        // Tìm kiếm Track từ Local
        void searchTracksLocal(String trackName, OnFetchDataListener<Track> listener);
        // Xóa Track
        boolean deleteTrack(Track track);
        // Thêm Track vào PlayList
        void addTracksToPlaylist(int playlistId,
                                 OnHandleDatabaseListener listener, Track... tracks);
        // Delete PlayList
        void deletePlaylist(Playlist playlist,String userId,
                                 OnHandleDatabaseListener listener);
        // Thêm Track vào PlayList mới
        void addTracksToNewPlaylist( String idUser ,String newPlaylistName,
                                    OnHandleDatabaseListener listener, Track... tracks);
        // Lấy danh sách PlayList
        List<Playlist> getPlaylist();
        // Thêm mới PlayList
        void insertPlayList(String idUser ,Playlist playlist, OnHandleDatabaseListener listener);
        // Lấy danh sách chi tiết PlayList từ User
        List<Playlist> getDetailPlaylistbyIdUser(String idUser);
        // Thêm mới Track vào mục Favorite
        void addTracksToFavorite(String idUser, Track track, OnHandleDatabaseListener listener);
        // Lấy Track from Favorite
        void getTrackFavorite(String idUser, OnFetchDataListener<Track> listener);

        void deleteTrackFavorite(Track track, String idUser, TrackDataSource.OnHandleDatabaseListener listener);
         void editNamePlayList(Playlist playlist, String userId, TrackDataSource.OnHandleDatabaseListener listener) ;
    }

    /**
     * RemoteData For Tracks
     */
    interface RemoteDataSource extends TrackDataSource {
        void getTracksRemote(String genre, int limit, int offSet,
                             OnFetchDataListener<Track> listener);

        void searchTracksRemote(String trackName, int offSet, OnFetchDataListener<Track> listener);

    }

    interface OnFetchDataListener<T> {
        void onFetchDataSuccess(List<T> data);

        void onFetchDataFailure(String message);
    }
// day là interface lắng nghe kêt quả thực hiện truy vấn
    interface OnHandleDatabaseListener {
        void onHandleSuccess(String message);
        void onHandleFailure(String message);
    }
}
