package com.ilm.onlinechess;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.onlinechess.Game.GameData;
import com.ilm.onlinechess.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    Button btn;

    //CAMBIAR TODO
    ActivityMainBinding binding;
    private static final int RC_SIGN_IN = 007;
    private GoogleSignInClient mGoogleSignInClient;
    public static FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("180266998589-eg9fuv018j85cme4jbj6dtc1vrrr3i58.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(getApplicationContext(), LoginNav.class);
                FirebaseApp.initializeApp(getApplicationContext());


                startActivity(intent);
            }
        });

        binding.signInButton.setOnClickListener(this::onClick);
        binding.signOutButton.setOnClickListener(this::onClick);


    }
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        } else if (i == R.id.signOutButton) {
            signOut();
        }
    }
    private void signIn() {
        Intent signInIntent = null;
        try {
            signInIntent = mGoogleSignInClient.getSignInIntent();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signOut() {
        // Firebase sign out

        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener( this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);

                            loadUser();

                            Intent intent= new Intent(getApplicationContext(), LoginNav.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            GameData.isLoged = false;
                            Toast.makeText(getApplicationContext(),"Sing in failed ",Toast.LENGTH_SHORT).show();

                            updateUI(mAuth.getCurrentUser());
                        }
                    }
                });
    }
    public void updateUI(FirebaseUser user){
        if (user != null) {

            binding.signInButton.setVisibility(View.GONE);
            binding.signOutButton.setVisibility(View.VISIBLE);

        }else {
            Toast.makeText(this,"Logged Out ",Toast.LENGTH_LONG).show();

            binding.signInButton.setVisibility(View.VISIBLE);
            binding.signOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                Toast.makeText(this,"Bienvenido "+account.getDisplayName()
                        +"["+account.getEmail()+"]",Toast.LENGTH_LONG).show();
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this,"No se pudo iniciar sesion ",Toast.LENGTH_LONG).show();

                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }




    //Save the value of the loged user
    public void loadUser(){
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String email = firebaseUser.getEmail();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Convertir el documento a un objeto GameModel
                        User user = documentSnapshot.toObject(User.class);

                        //Update the user image becuase it could have changed

                        GameData.isLoged = true;

                        if (user != null) {
                            Log.d("USER LOADED","USER LOADED");
                            GameData.updateUser(user);
                            user.setUrlImage(firebaseUser.getPhotoUrl().toString());

                        } else {
                            // CREANDO USUARIO POR PRIMERA VEZ
                            // CREANDO USUARIO POR PRIMERA VEZ
                            // User guestUser = new User();
                            //guestUser.setUsername("Guest");
                            //GameData.user=guestUser;
                            //Log.d("ERRORSAVING USER: ","User not found, creatin user");

                            User logedUser = new User();
                            logedUser.setEmail(firebaseUser.getEmail());
                            logedUser.setUsername(firebaseUser.getDisplayName());
                            logedUser.setUrlImage(firebaseUser.getPhotoUrl().toString());
                            Log.d("USER LOADED","photo url :" + logedUser.getUrlImage());

                            GameData.updateUser(logedUser);
                        }
                    }
                });
    }



}

