package com.ilm.onlinechess.Game;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ilm.onlinechess.LoginNav;
import com.ilm.onlinechess.R;
import com.ilm.onlinechess.databinding.ActivityGameBinding;

public class Game extends AppCompatActivity  {

    ActivityGameBinding binding;
    private boolean firstTime = true;
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
        gameModel= GameData.gameModel.getValue();

        if(!GameData.isOffline){
            GameData.fetchGameModel();
        }else{

            binding.trophy1.setVisibility(com.google.android.material.R.id.invisible);
            binding.trophy2.setVisibility(com.google.android.material.R.id.invisible);
            binding.guestRank.setVisibility(com.google.android.material.R.id.invisible);
        }

        updateUI();

        //If the game is CREATE, not JOIN, then start a timer

        GameData.gameModel.observe(this, new Observer<GameModel>() {
            @Override
            public void onChanged(GameModel newGameModel) {

                gameModel = newGameModel;
                updateUI();

            }
        });



        OnBackPressedDispatcher dispatcher = getOnBackPressedDispatcher();
        dispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Aquí puedes agregar el código que deseas ejecutar cuando se pulsa el botón de atrás
                new AlertDialog.Builder(Game.this)
                        .setMessage("¿Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                GameData.gameModel=  new MutableLiveData<>();
                                GameData.turn = 0;

                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        //Una vez que ya tengo la URI


        //Change the user avatar depending on the user

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



        board = new Chessboard(binding.grid, this, this);

        //gameModel=GameData.gameModel.getValue();

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


                if(!binding.gameGuest.getText().toString().equals("Waiting for player..."))
                    startGame();


            }
        });

    }

    public void updateUI(){


        if(gameModel!=null){

            //Avatar bitmaps
            if(GameData.isLoged  ){
                //SETS GameData.avatar with the bitmaps of the users
                changePlayerBitmap(gameModel.getHostUri(),gameModel.getGuestUri());

                //HOST
                if(GameData.hostAvatar!=null && !GameData.isOffline)
                    binding.avatarHost.setImageBitmap(GameData.hostAvatar);
                else
                    binding.avatarHost.setImageResource(R.drawable.avatar);

                //GUEST
                if(GameData.guestAvatar!=null && !GameData.isOffline)
                    binding.avatarGuest.setImageBitmap(GameData.guestAvatar);
                else
                    binding.avatarGuest.setImageResource(R.drawable.avatar);

            }else {
                binding.guestRank.setText("0");
                binding.hostRank.setText(("0"));
            }

            if(gameModel.getGAME_STATUS()==GameData.STARTED )
                binding.cl2.removeView(binding.bntStart);

            if(GameData.isLoged){
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



    public void startGame(){
        updateUI();

        gameModel.setGameId(GameData.gameModel.getValue().gameId);
        gameModel.setGAME_STATUS(GameData.STARTED);

        GameData.saveGameModel(gameModel, true);

    }





   public void changePlayerBitmap(String uriHost, String uriGuest){
       //if (GameData.isLoged ) {
           Uri photoUrl = Uri.parse((uriHost));
          // Uri photoUrl = Uri.parse(GameData._user.getValue().getUrlImage());
           if (photoUrl != null) {
               // Usar Glide para cargar la imagen
               Glide.with(this)
                       .asBitmap()
                       .load(photoUrl)
                       .into(new CustomTarget<Bitmap>() {
                           @Override
                           public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                   GameData.hostAvatar=(resource);

                           }

                           @Override
                           public void onLoadCleared(@Nullable Drawable placeholder) {
                               // Manejo del placeholder o acciones de limpieza si es necesario
                           }

                           @Override
                           public void onLoadFailed(@Nullable Drawable errorDrawable) {
                               super.onLoadFailed(errorDrawable);
                               // Manejo de errores
                           }
                       });
           }
       photoUrl = Uri.parse((uriGuest));
       // Uri photoUrl = Uri.parse(GameData._user.getValue().getUrlImage());
       if (photoUrl != null) {
           // Usar Glide para cargar la imagen
           Glide.with(this)
                   .asBitmap()
                   .load(photoUrl)
                   .into(new CustomTarget<Bitmap>() {
                       @Override
                       public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                           GameData.guestAvatar=(resource);
                       }

                       @Override
                       public void onLoadCleared(@Nullable Drawable placeholder) {
                           // Manejo del placeholder o acciones de limpieza si es necesario
                       }

                       @Override
                       public void onLoadFailed(@Nullable Drawable errorDrawable) {
                           super.onLoadFailed(errorDrawable);
                           // Manejo de errores
                       }
                   });
       }
      // }
   }



   //COMPROBAR ISLOGED
}