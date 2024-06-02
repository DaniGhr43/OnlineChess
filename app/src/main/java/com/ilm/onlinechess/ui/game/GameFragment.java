package com.ilm.onlinechess.ui.game;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.onlinechess.Chessboard;
import com.ilm.onlinechess.GameData;
import com.ilm.onlinechess.GameModel;
import com.ilm.onlinechess.R;
import com.ilm.onlinechess.databinding.FragmentGameBinding;
import com.ilm.onlinechess.databinding.FragmentLoginBinding;
import com.ilm.onlinechess.ui.login.LoginFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GameFragment extends Fragment {
    FragmentGameBinding binding;

    Chessboard board;
    public GameFragment() {}

    private static GameModel gameModel ;

    private String hostPlayer;
    int cont = 0 ;


    Dialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        gameModel=GameData.gameModel.getValue();

        updateUI();
        // Inflate the layout for this fragment

        GameData.gameModel.observe(getViewLifecycleOwner(), new Observer<GameModel>() {
            @Override
            public void onChanged(GameModel newGameModel) {
                Log.d("changed","CHANGE");
                gameModel = newGameModel;
                updateUI();

            }
        });



        if(GameData.gameModel != null && gameModel.getGAME_STATUS()!=GameData.OFFLINE){
            GameData.fetchGameModel();
        }

        board = new Chessboard(binding.grid, getContext(), getViewLifecycleOwner());

        if(gameModel.getGAME_STATUS()==GameData.CREATE)
            createOnlineGame();
        if(gameModel.getGAME_STATUS()==GameData.JOIN)
            joinOnlineGame();

        gameModel=GameData.gameModel.getValue();


        // Obtener el ancho del ConstraintLayout
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        // Calcular el margen del GridLayout (25% del ancho del ConstraintLayout)
        int margin = (int) (height * 0.25);
        // Obtener los parámetros de diseño del GridLayout

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( (width),  (height) );

        param.height= (int) (height*0.9);

        // Aplicar los nuevos parámetros de diseño al GridLayout
        binding.linearLayout.setLayoutParams(param);
        binding.grid.setRowCount(8);
        binding.grid.setColumnCount(8);// binding.grid.setLayoutParams(param);



        //Start game
        board.createCells();

        binding.bntStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //updateFirebase();
                startGame();

            }
        });



        return root;
    }


    public void joinOnlineGame(){

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("games")
                    .document(String.valueOf(gameModel.gameId))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            // Convertir el documento a un objeto GameModel
                            GameModel model = documentSnapshot.toObject(GameModel.class);

                            if (model != null) {


                                model.setGuestPlayer("GUEST");
                                if(GameData.isLoged) {
                                    model.setGuestRank(String.valueOf(GameData.user.getRank()));
                                    model.setGuestPlayer(GameData.user.getUsername());

                                }

                                updateGameData(model);
                                gameModel = model;

                               // startGame();
                            } else {
                                // Guardar el modelo de juego (parece que debería ser en caso de que sea no nulo, revisa esto)
                                Log.d("ERROR NO","No se pudo unir");
                            }
                        }
                    });



    }
    public void createOnlineGame(){
        GameModel model = new GameModel();

        model.setGameId(gameModel.gameId);
        model.setHostPlayer("HOST");
        model.setGAME_STATUS(gameModel.getGAME_STATUS());

        if(GameData.isLoged){
            model.setHostPlayer(GameData.user.getUsername());
            model.setHostRank(String.valueOf(GameData.user.getRank()));

        }
        updateGameData(model);

        gameModel = model;
        Log.d("fetchGameModel", String.valueOf(gameModel.gameId));

    }


    public void updateUI(){

       // gameModel
        if(gameModel!=null){


            if(gameModel.getGAME_STATUS()==GameData.STARTED || gameModel.getGAME_STATUS()==GameData.OFFLINE)
                binding.cl2.removeView(binding.bntStart);

            if(GameData.user != null){
                binding.guestRank.setText(String.valueOf(gameModel.getGuestRank()));
                binding.hostRank.setText(String.valueOf(gameModel.getHostRank()));

            }

            binding.gameID.setText(String.valueOf(gameModel.gameId));
            binding.gameGuest.setText(gameModel.getGuestPlayer());
            binding.gameHost.setText(gameModel.getHostPlayer());


        }



    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startGame(){
        updateUI();

        gameModel.setGameId(GameData.gameModel.getValue().gameId);

        if(gameModel.getGAME_STATUS() != GameData.OFFLINE)
            gameModel.setGAME_STATUS(GameData.STARTED);

        updateGameData(gameModel);

    }


    public void updateGameData(GameModel model){


        GameData.saveGameModel(model);
    }





}