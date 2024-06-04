package com.ilm.onlinechess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.Application;
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

public class Game extends AppCompatActivity  {

    ActivityGameBinding binding;

    Chessboard board;
    private static GameModel gameModel ;

    private Button btnExit, btnReturn;
    private boolean clocksStarted = false;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gameModel=GameData.gameModel.getValue();

        updateUI();


        //If the game is CREATE, not JOIN, then start a timer


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

        if(!GameData.isOffline){
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

            //if(gameModel.getGAME_STATUS()==GameData.STARTED && !clocksStarted){
            //    startTimer(30);
             //   clocksStarted=true;
            //}

            if(gameModel.getGAME_STATUS()==GameData.STARTED )
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
                else if(gameModel.getWinner() == GameData.BLACK)
                    winner.setText("Black");

                GameData.turn=GameData.WHITE;

                dialog.show();

            }

        }



    }


    @Override
    protected void onStop() {
        super.onStop();
        if(GameData.currentPlayer == GameData.WHITE){
            gameModel.setWinner(GameData.BLACK);

        }

        if(GameData.currentPlayer == GameData.BLACK)
            gameModel.setWinner(GameData.WHITE);
        updateGameData(gameModel);
    }

    public void startGame(){
        updateUI();

        gameModel.setGameId(GameData.gameModel.getValue().gameId);
        gameModel.setGAME_STATUS(GameData.STARTED);

        updateGameData(gameModel);

    }


    public void updateGameData(GameModel model){


        GameData.saveGameModel(model);
    }






    private long minutes ;
    private long remainingSeconds ;;
   /* private void startTimer(int limitSeconds) {

        CountDownTimer timer = new CountDownTimer(limitSeconds * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                if(GameData.turn == GameData.WHITE){
                    GameData.hostTime--;
                    minutes=GameData.hostTime/60;
                    remainingSeconds =GameData.hostTime - TimeUnit.MINUTES.toSeconds(minutes);
                    binding.timeHost.setText(minutes + " : " +remainingSeconds);
                }else{
                    GameData.guestTime--;
                    binding.timeGuest.setText(String.valueOf(GameData.guestTime));
                }

                if( GameData.hostTime <=0){
                    gameModel.setGAME_STATUS(GameData.FINISHED);
                    gameModel.setWinner(GameData.BLACK);
                    GameData.saveGameModel(gameModel);
                }
                if( GameData.guestTime <=0){
                    gameModel.setGAME_STATUS(GameData.FINISHED);
                    gameModel.setWinner(GameData.WHITE);
                    GameData.saveGameModel(gameModel);
                }

            }
            @Override
            public void onFinish() {


            }
        };
        timer.start();

    }*/

}