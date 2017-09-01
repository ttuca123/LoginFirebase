package com.mobile.zenus.loginfirebase;

import android.app.ActionBar;
import android.bluetooth.BluetoothA2dp;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mUsuario;

    private EditText mSenha;

    private Button mLogin;

    TextView mTxtLogado;

    GoogleApiClient mGoogleApiClient;

    int RC_SIGN_IN=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsuario = (EditText) findViewById(R.id.edtNome);

        mSenha = (EditText) findViewById(R.id.edtSenha);

        mLogin = (Button) findViewById(R.id.btnLogin);

        mTxtLogado = (TextView) findViewById(R.id.txtLogado);



        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    mTxtLogado.setText(user.getEmail());

                    // User is signed in

                } else {
                    // User is signed out
                    mTxtLogado.setText("Deslogado");
                }
                // ...
            }
        };



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            mTxtLogado.setText(acct.getDisplayName());

        } else {
            mTxtLogado.setText("Usuário não autenticado");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void logof(View view){

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
            mTxtLogado.setText("Home");
        }
    }


    public void criarUsuario(View view){

        if((!mUsuario.getText().toString().isEmpty()&& mUsuario.getText().toString().length()>2)
               && (mSenha.getText().toString().length()>6) ){

            final String email = mUsuario.getText().toString().trim();
            String password = mSenha.getText().toString().trim();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {

                                Toast.makeText(MainActivity.this, "Usuário já existe / email inválido",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Usuario criado com sucesso",
                                        Toast.LENGTH_SHORT).show();
//                                actionBar.setTitle(email);


                            }

                            // ...
                        }
                    });
        }

    }


    public void loginUsuario(View view){

        final String email = mUsuario.getText().toString().trim();
        final String password = mSenha.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Falha de Autenticação",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Usuario autenticado com sucesso",
                                    Toast.LENGTH_SHORT).show();
                            mTxtLogado.setText(email);

                        }

                        // ...
                    }
                });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
