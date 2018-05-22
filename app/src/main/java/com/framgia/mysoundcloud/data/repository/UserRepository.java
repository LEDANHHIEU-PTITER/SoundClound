package com.framgia.mysoundcloud.data.repository;

import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.data.source.UserDataSource;
import com.framgia.mysoundcloud.data.source.local.SharePreferences;
import com.framgia.mysoundcloud.data.source.local.UserLocalDataSource;
import com.framgia.mysoundcloud.data.source.remote.UserRemoveDatasource;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

/**
 * Created by buidanhnam on 4/28/2018.
 */

public class UserRepository implements UserDataSource.UserRemoveDataSource,
        UserDataSource.UserLocalDataSource {
    private static UserRepository repository;

    private UserDataSource.UserRemoveDataSource userRemoveDataSource;
    private UserDataSource.UserLocalDataSource userLocalDataSource;

    private UserRepository(UserDataSource.UserLocalDataSource userLocalDataSource,
                           UserDataSource.UserRemoveDataSource userRemoveDataSource
    ) {
        this.userRemoveDataSource = userRemoveDataSource;
        this.userLocalDataSource = userLocalDataSource;
    }

    public static UserRepository getInstance() {
        if (repository == null) {
            repository = new UserRepository(
                    UserLocalDataSource.getInstance(),
                    UserRemoveDatasource.getInstance());
        }
        return repository;

    }

    @Override
    public void getInforUser(String idUser, UserDataSource.ResultCallBack<User> callBack) {
        userLocalDataSource.getInforUser(idUser, callBack);
    }

    @Override
    public void addUser(User user, UserDataSource.ResultCallBack<User> callBack) {
        userLocalDataSource.addUser(user, callBack);
    }

    @Override
    public void login(GoogleSignInAccount user, final UserDataSource.ResultCallBack<User> callBack) {
        userRemoveDataSource.login(user, new UserDataSource.ResultCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                addUser(user, new UserDataSource.ResultCallBack<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user == null) return;
                        SharePreferences.getInstance().putUser(user);
                        SharePreferences.getInstance().putTypeLogin(User.TYPE_LOGIN.LOGIN_GG);
                        callBack.onSuccess(user);
                    }

                    @Override
                    public void onFailure(String mes) {
                        callBack.onFailure(mes);
                    }
                });
            }

            @Override
            public void onFailure(String mes) {
                callBack.onFailure(mes);
            }
        });
    }

    @Override
    public void login(User user, final UserDataSource.ResultCallBack<User> callBack) {
        userRemoveDataSource.login(user, new UserDataSource.ResultCallBack<User>() {
            @Override
            public void onSuccess(User user) {
                addUser(user, new UserDataSource.ResultCallBack<User>() {
                    @Override
                    public void onSuccess(User user) {
                        if (user == null) return;
                        SharePreferences.getInstance().putUser(user);
                        SharePreferences.getInstance().putTypeLogin(User.TYPE_LOGIN.LOGIN_FACEBOOK);

                        callBack.onSuccess(user);
                    }

                    @Override
                    public void onFailure(String mes) {
                        callBack.onFailure(mes);
                    }
                });
            }

            @Override
            public void onFailure(String mes) {
                callBack.onFailure(mes);
            }
        });
    }
}
