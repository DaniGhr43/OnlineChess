package com.ilm.onlinechess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ilm.onlinechess.databinding.ActivityGameBinding;
import com.ilm.onlinechess.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class Game extends AppCompatActivity {

    ActivityGameBinding binding;

    Chessboard board;
    private static GameModel gameModel ;

    private String hostPlayer;

    private Button btnExit, btnReturn;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gameModel=GameData.gameModel.getValue();

        updateUI();


        //If the game is CREATE, not JOIN, then start a timer
        if(gameModel.getGAME_STATUS()==GameData.CREATE){
            startTimer(600);
            binding.gameHost.setText("Wating for player... Time until game is cancelled:");
        }

        GameData.gameModel.observe(this, new Observer<GameModel>() {
            @Override
            public void onChanged(GameModel newGameModel) {
                Log.d("changed","CHANGE");
                gameModel = newGameModel;
                updateUI();

            }
        });


        dialog = new Dialog (this);

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
                Intent i = new Intent(getApplicationContext(), LoginNav.class);
                startActivity(i);
            }
        });

        if(GameData.gameModel != null && gameModel.getGAME_STATUS()!=GameData.OFFLINE){
            GameData.fetchGameModel();
        }

        board = new Chessboard(binding.grid, this, this);

        //if(gameModel.getGAME_STATUS()==GameData.CREATE)
        //    createOnlineGame();
        //if(gameModel.getGAME_STATUS()==GameData.JOIN)
        //joinOnlineGame();

        gameModel=GameData.gameModel.getValue();


        // Obtener el ancho del ConstraintLayout
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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

    public void startGame(){
        updateUI();

        gameModel.setGameId(GameData.gameModel.getValue().gameId);

        if(gameModel.getGAME_STATUS() != GameData.OFFLINE)
            gameModel.setGAME_STATUS(GameData.STARTED);

        updateGameData(gameModel);

    }

    public void updateStats(){

        if(GameData.currentPlayer == gameModel.getWinner()){
            User user =  GameData._user.getValue();
            user.setRank(user.getRank()+20);
            user.setLevel(user.getLevel()+0.25);

            GameData.updateUser(user);
        }else{
            User user =  GameData._user.getValue();
            if(user.getRank()>0)
                user.setRank(user.getRank()-20);
            user.setLevel(user.getLevel()+0.10);

            GameData.updateUser(user);
        }
    }
    public void updateGameData(GameModel model){


        GameData.saveGameModel(model);
    }



    protected void onDestroy() {
        super.onDestroy();

        gameModel.setGAME_STATUS(GameData.FINISHED);
        updateGameData(gameModel);
        // Código que deseas ejecutar cuando se cierre la aplicación
        updateStats();
    }

    private void startTimer(int seconds) {

        CountDownTimer timer = new CountDownTimer(seconds * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                if(GameData.turn == GameData.WHITE){
                    GameData.hostTime--;
                    binding.timeHost.setText(String.valueOf(GameData.hostTime));
                }else{
                    GameData.guestTime--;
                    binding.timeGuest.setText(String.valueOf(GameData.guestTime));
                }


                binding.hostRank.setText(String.valueOf(millisUntilFinished / 1000));

            }
            @Override
            public void onFinish() {


            }
        };
        timer.start();

    }

}