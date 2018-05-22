package com.framgia.mysoundcloud.data.source.remote;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.framgia.mysoundcloud.R;
import com.framgia.mysoundcloud.data.model.Track;
import com.framgia.mysoundcloud.utils.Constant;
import com.framgia.mysoundcloud.utils.StringUtil;



public class DownloadTrackManager {

    private static final String FILE_EXTENSION_MP3 = ".mp3";
    private static final String LOCATE_DOWNLOAD_DIRECTORY = "file:///mnt/sdcard/MySoundCloud/";

    private Context mContext;
    private DownloadListener mListener;

    private DownloadTrackManager() {

    }

    public DownloadTrackManager(Context context, DownloadListener downloadListener) {
        mContext = context;
        mListener = downloadListener;
    }

    public void downloadTrack(Track track) {
        if (track == null || mContext == null) {
            notifyCantDownload();
            return;
        }

//        if (!track.isDownloadable()) {
//            notifyCopyRightIssue();
//            return;
//        }

        DownloadManager manager = (DownloadManager) mContext
                .getSystemService(Context.DOWNLOAD_SERVICE);
//        String url = StringUtil.getUrlStreamTrack(mTracks.get(mCurrentTrackPosition).getUri());
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(StringUtil.getUrlStreamTrack(track.getUri())));
        request.setTitle(track.getTitle());
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                track.getTitle() + FILE_EXTENSION_MP3);
        manager.enqueue(request);

        notifyDownloading();
    }

    private void notifyCantDownload() {
        if (mListener == null) return;
        mListener.onDownloadError(mContext.getString(R.string.msg_download_failed));
    }

    private void notifyDownloading() {
        if (mListener == null) return;
        mListener.onDownloading();
    }

    private void notifyCopyRightIssue() {
        if (mListener == null) return;
        mListener.onDownloadError(mContext.getString(R.string.msg_cant_be_download));
    }

    public interface DownloadListener {
        void onDownloadError(String msg);

        void onDownloading();
    }

}
