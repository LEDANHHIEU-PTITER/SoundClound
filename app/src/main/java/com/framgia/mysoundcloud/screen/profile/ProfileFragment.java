package com.framgia.mysoundcloud.screen.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.framgia.mysoundcloud.R;
import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.screen.login.LoginActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements ProfileContract.View, View.OnClickListener {

    private static ProfileFragment fragment;
    private TextView tvname, tvMail;
    private CircleImageView circleImageView;
    private Button btnLogout;

    private ProfileContract.Presenter mPresenter;

    public static ProfileFragment newInstance() {
        if (fragment == null) {
            fragment = new ProfileFragment();
        }
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        mPresenter = new ProfilePresenter();
        mPresenter.setView(this);
        setupUI(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.loadData();
    }

    @Override
    public void initData(User user) {
        if (user == null) return;
        tvname.setText(user.getName());
        tvMail.setText(user.getEmail());
        Glide.with(getActivity()).load(user.getImage())
                .into(circleImageView);
    }

    @Override
    public void logOutSuccess() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void setupUI(View upUI) {
        tvMail = upUI.findViewById(R.id.email);
        tvname = upUI.findViewById(R.id.name);
        circleImageView = upUI.findViewById(R.id.profile);
        btnLogout = upUI.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        mPresenter.doLogout();
    }
//    private void signOut() {
//        mGoogleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        // ...
//                    }
//                });
//    }
}
