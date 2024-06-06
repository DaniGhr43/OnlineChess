package com.ilm.onlinechess.ui.login;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.onlinechess.Game.Game;
import com.ilm.onlinechess.Game.GameData;
import com.ilm.onlinechess.Game.GameModel;

import com.ilm.onlinechess.Network;

import com.ilm.onlinechess.databinding.FragmentLoginBinding;



import java.util.Random;


public class LoginFragment extends Fragment{

    private FragmentLoginBinding binding;
    private GameModel gameModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        gameModel = GameData.gameModel.getValue();
        binding.btnOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { createOfflineGame();}

        });
        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!Network.isConnected(getContext())) {
                    Toast.makeText(getContext(), "Please connect to internet to create or join online session", Toast.LENGTH_LONG).show();
                    return;
                }

                createOnlineGame();
            }
        });

        binding.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Network.isConnected(getContext())) {
                    Toast.makeText(getContext(), "Please connect to internet to create online session", Toast.LENGTH_LONG).show();
                    return;
                }
                try{

                    if(binding.gameID.getText().length()>0 && binding.gameID.getText().length()<=4 )
                        joinOnlineGame();
                    else
                        binding.gameID.setError("Insert a valid game id");

                }catch(NumberFormatException e){
                    binding.gameID.setError("Insert a valid game id");
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

    public void createOfflineGame(){
        GameModel model = new GameModel();
        GameData.isOffline= true;
        model.setGameId(1234);
        GameData.saveGameModel(model, false);
        model.setHostPlayer("LocalUser1");
        model.setGuestPlayer("LocalUser2");
        model.setGuestRank("0");
        model.setHostRank("0");
        Intent i = new Intent(getContext(),Game.class);
        startActivity(i);
    }
    public void createOnlineGame(){
        GameModel model = new GameModel();
        GameData.isOffline= false;
        Random r = new Random();
        model.setGAME_STATUS(GameData.CREATE);
        model.setGameId(r.nextInt(9999)+1);
        GameData.currentPlayer = GameData.WHITE;

        GameData.turn=0;
        if(GameData.isLoged){
            model.setHostPlayer(GameData._user.getValue().getUsername());
            model.setHostRank(String.valueOf(GameData._user.getValue().getRank()));
            model.setHostUri(GameData._user.getValue().getUrlImage());


        }else
            model.setHostPlayer("Guest1");


        model.setGuestPlayer("Waiting for player...");
        model.setGuestRank("0");
        GameData.saveGameModel(model, true);
        gameModel = model;

        Intent i = new Intent(getContext(), Game.class);
        startActivity(i);
        Log.d("fetchGameModel", String.valueOf(gameModel.gameId));

    }
    public void joinOnlineGame(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("games")
                .document(String.valueOf(binding.gameID.getText()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Convertir el documento a un objeto GameModel
                        GameModel model = documentSnapshot.toObject(GameModel.class);

                        if (model != null) {

                            Log.d("join", String.valueOf(model.getHostRank()));

                            if(model.getGAME_STATUS() == GameData.CREATE){
                                model.setGuestPlayer("Guest 2");
                                model.setGAME_STATUS(GameData.JOIN);

                                GameData.currentPlayer = GameData.BLACK;
                                if(GameData.isLoged) {
                                    model.setGuestRank(String.valueOf(GameData._user.getValue().getRank()));
                                    model.setGuestPlayer(GameData._user.getValue().getUsername());
                                    model.setGuestUri(GameData._user.getValue().getUrlImage());

                                }
                                //Update GameData.bitmap
                                //Set that bitmap to the imageView
                                GameData.saveGameModel(model,true);
                                gameModel = model;


                                Intent i = new Intent(getContext(), Game.class);
                                startActivity(i);


                            }else{
                                binding.gameID.setError("The game has already ended or started");

                            }

                        } else {
                            // Guardar el modelo de juego (parece que debería ser en caso de que sea no nulo, revisa esto)
                            binding.gameID.setError("The game does not exists");
                        }
                    }
                });
    }




}