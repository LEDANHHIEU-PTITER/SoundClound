package com.framgia.mysoundcloud.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.framgia.mysoundcloud.R;
import com.framgia.mysoundcloud.data.model.Track;
import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.data.repository.TrackRepository;
import com.framgia.mysoundcloud.data.source.TrackDataSource;
import com.framgia.mysoundcloud.data.source.local.SharePreferences;
import com.framgia.mysoundcloud.screen.playlist.PlaylistDialogAdapter;



public class DialogManager implements DialogManagerInterface {

    private Context mContext;
    private AlertDialog mAlertDialog;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void dialogMessage(String msg, String title) {
        if (mContext != null) {
            mAlertDialog = new AlertDialog.Builder(mContext)
                    .setMessage(msg)
                    .setTitle(title)
                    .create();
            mAlertDialog.show();
        }
    }

    @Override
    public void dialogButton(
            String msg, String title, String positiveButton,
            String negativeButton, final DialogListener mListener) {
        if (mContext != null && mListener != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(msg)
                    .setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogPositiveClick();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick();
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public void dialogAddToPlaylist(final Activity activity, final Track... tracks) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_playlist, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(mContext.getString(R.string.action_add_to_playlist));
        final AlertDialog alertDialog = dialogBuilder.create();

        final TrackDataSource.OnHandleDatabaseListener listener =
                new TrackDataSource.OnHandleDatabaseListener() {
                    @Override
                    public void onHandleSuccess(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onHandleFailure(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                };

        final EditText editTextNewPlaylist = dialogView.findViewById(R.id.edit_text_new_playlist);
        ImageView imageAddNewPlaylist = dialogView.findViewById(R.id.image_add_new_playlist);
        imageAddNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = SharePreferences.getInstance().getUser();
                if(user== null)return;
                String id = user.getId();
                TrackRepository.getInstance().addTracksToNewPlaylist(id,
                        editTextNewPlaylist.getText().toString(), listener, tracks);
            }
        });

        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_playlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new PlaylistDialogAdapter(listener, tracks));

        alertDialog.show();
    }

    @Override
    public void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

}
