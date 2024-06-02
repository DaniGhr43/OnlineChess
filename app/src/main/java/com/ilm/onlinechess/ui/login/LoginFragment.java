package com.ilm.onlinechess.ui.login;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.onlinechess.GameData;
import com.ilm.onlinechess.GameModel;
import com.ilm.onlinechess.MainActivity;
import com.ilm.onlinechess.R;
import com.ilm.onlinechess.databinding.FragmentLoginBinding;
import com.ilm.onlinechess.ui.game.GameFragment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;


public class LoginFragment extends Fragment{

    private FragmentLoginBinding binding;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        binding.btnOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameModel model = new GameModel();
                Random r = new Random();
                model.setGAME_STATUS(GameData.OFFLINE  );
                model.setGameId(1234);
                GameData.saveGameModel(model);

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_login_nav);
                navController.navigate(R.id.nav_game); // Reemplaza slideshowFragment con el ID correcto de tu fragmento en nav_graph.xml

            }
        });
        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameModel model = new GameModel();
                model.setGAME_STATUS(GameData.CREATE);
                Random r = new Random();
                GameData.currentPlayer = GameData.WHITE;
                model.setGameId(r.nextInt(9999)+1);
                GameData.saveGameModel(model);
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_login_nav);
                navController.navigate(R.id.nav_game); // Reemplaza slideshowFragment con el ID correcto de tu fragmento en nav_graph.xml
            }
        });

        binding.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(!binding.gameID.getText().equals("") && Integer.parseInt(binding.gameID.getText().toString())!=-2){
                        GameModel model = new GameModel();

                        GameData.currentPlayer = GameData.BLACK;


                        model.setGAME_STATUS(GameData.JOIN);
                        model.setGameId(Integer.parseInt(binding.gameID.getText().toString()));

                        GameData.saveGameModelOffline(model);

                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_login_nav);
                        navController.navigate(R.id.nav_game); // Reemplaza slideshowFragment con el ID correcto de tu fragmento en nav_graph.xml
                    }else if (binding.gameID.getText().equals("")){
                        binding.gameID.setError("Introduce a game id");
                    }
                }catch(NumberFormatException e){
                    binding.gameID.setError("Introduce a valid game id");

                }


            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }





}