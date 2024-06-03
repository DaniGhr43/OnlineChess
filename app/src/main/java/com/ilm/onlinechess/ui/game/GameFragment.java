package com.ilm.onlinechess.ui.game;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ilm.onlinechess.Chessboard;
import com.ilm.onlinechess.GameData;
import com.ilm.onlinechess.GameModel;
import com.ilm.onlinechess.LoginNav;
import com.ilm.onlinechess.MainActivity;
import com.ilm.onlinechess.R;
import com.ilm.onlinechess.databinding.ActivityGameBinding;
import com.ilm.onlinechess.databinding.FragmentLoginBinding;
import com.ilm.onlinechess.ui.login.LoginFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GameFragment extends Fragment {
    ActivityGameBinding binding;

    Chessboard board;
    public GameFragment() {}

    private static GameModel gameModel ;

    private String hostPlayer;
    int cont = 0 ;


    private Button btnExit, btnReturn;
    Dialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityGameBinding.inflate(inflater, container, false);
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


        dialog = new Dialog (getContext());

        dialog.setContentView(R.layout.custom_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);

        btnExit = dialog.findViewById(R.id.btnExit);
        btnReturn = dialog.findViewById(R.id.btnReturn);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LoginNav.class);
                startActivity(i);
            }
        });

        if(GameData.gameModel != null && gameModel.getGAME_STATUS()!=GameData.OFFLINE){
            GameData.fetchGameModel();
        }

        board = new Chessboard(binding.grid, getContext(), getViewLifecycleOwner());

        //if(gameModel.getGAME_STATUS()==GameData.CREATE)
        //    createOnlineGame();
        //if(gameModel.getGAME_STATUS()==GameData.JOIN)
            //joinOnlineGame();

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



    public void updateUI(){

       // gameModel
        if(gameModel!=null){


            if(gameModel.getGAME_STATUS()==GameData.STARTED || gameModel.getGAME_STATUS()==GameData.OFFLINE)
                binding.cl2.removeView(binding.bntStart);

            if(GameData._user.getValue() != null){
                binding.guestRank.setText(String.valueOf(gameModel.getGuestRank()));
                binding.hostRank.setText(String.valueOf(gameModel.getHostRank()));

            }

            binding.gameID.setText(String.valueOf(gameModel.gameId));
            binding.gameGuest.setText(gameModel.getGuestPlayer());
            binding.gameHost.setText(gameModel.getHostPlayer());

            if(gameModel.getGAME_STATUS()==GameData.FINISHED){
                TextView winner = dialog.findViewById(R.id.winner);
                if(gameModel.getWinner() == GameData.WHITE)
                    winner.setText("White");
                else if(gameModel.getWinner() == GameData.WHITE)
                    winner.setText("Black");

                dialog.show();
            }

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



    //ANADIR QUE AL UNIRSE A ONLINE SIN CONEXION DE ERROR
    //BOTON DE REGISTER

}