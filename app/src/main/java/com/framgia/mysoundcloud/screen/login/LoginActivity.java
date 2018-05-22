package com.framgia.mysoundcloud.screen.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.framgia.mysoundcloud.R;
import com.framgia.mysoundcloud.data.model.User;
import com.framgia.mysoundcloud.screen.main.MainActivity;
import com.framgia.mysoundcloud.utils.Constant;
import com.framgia.mysoundcloud.utils.Navigator;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginContact.LoginView {
    private static final String STATE = LoginActivity.class.getCanonicalName();
    private static final int RC_SIGN_IN = 1001;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private LoginPresenter presenter;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        presenter = new LoginPresenter(this);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(this.getResources().getString(R.string.))?
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final GraphRequest request = GraphRequest.newGraphPathRequest(
                        loginResult.getAccessToken(),
                        loginResult.getAccessToken().getUserId(),
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                try {
                                    String name = response.getJSONObject().getString("name");
                                    String id = response.getJSONObject().getString("id");
                                    User user = new User();
                                    user.setName(name);
                                    user.setId(id);
                                    presenter.handleSignInResult(user);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this,"Login attempt canceled.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this,"Login attempt failed.", Toast.LENGTH_LONG).show();
            }

        });

    }

    @Override
    public void onClick(View view) {
        presenter.login();
    }

    @Override
    public void onSuccess(User user) {
        if (user != null) {
            openMainActivity(user);
        }

    }

    private void openMainActivity(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.USER , user);
        new Navigator(this).startActivity(MainActivity.class, false);
        finish();
    }

    @Override
    public void showDialogLogin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "Login Failure ! ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            presenter.handleSignInResult(task);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
