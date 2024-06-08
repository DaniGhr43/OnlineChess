package com.ilm.onlinechess.game;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.DocumentReference;
import com.ilm.onlinechess.R;
import com.ilm.onlinechess.User;
import com.ilm.onlinechess.databinding.ActivityGameBinding;

import java.io.IOException;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity  {

    private ActivityGameBinding binding;
    private boolean firstTime = true;
    Chessboard board;
    private static GameModel gameModel ;
    private Button btnExit, btnReturn;
    private boolean clocksStarted = false;
    private Dialog dialog;
    private boolean statsUpdated = false;
    public  Bitmap guestAvatar;
    public  Bitmap hostAvatar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gameModel= GameData.gameModel.getValue();

        board = new Chessboard(binding.grid, this, this);


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
                new AlertDialog.Builder(GameActivity.this)
                        .setMessage("¿Are you sure you want to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                //if the game is online update stats
                                if(!GameData.isOffline){
                                    try {
                                        board.out.close();
                                        board.socket.close();
                                        board.in.close();
                                        //If a player exits when the game is started update his stats

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                GameData.currentPlayer = 0;
                                gameModel=  new GameModel();
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
            public void onClick(View v) {finishAffinity();}
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!GameData.isOffline){
                    try {
                        board.out.close();
                        board.socket.close();
                        board.in.close();
                        //If a player exits when the game is started update his stats


                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                GameData.currentPlayer = 0;
                gameModel=  new GameModel();
                GameData.turn = 0;
                GameData.saveGameModel(gameModel,false);
                finish();
            }
        });




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
                if(hostAvatar!=null)
                    binding.avatarHost.setImageBitmap(hostAvatar);


                //GUEST
                if(guestAvatar!=null )
                    binding.avatarGuest.setImageBitmap(guestAvatar);


            }else {

                binding.guestRank.setText("0");
                binding.hostRank.setText(("0"));
            }

            if(gameModel.getGAME_STATUS()==GameData.STARTED)
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

                if(!statsUpdated){
                    //Delete the doc game in the bd in the client that is loosing, because is the last one to use the doc

                    DocumentReference docRef = GameData.db.collection("games").document(String.valueOf(gameModel.gameId));
                    docRef.delete();
                    updateStats();
                    statsUpdated=true;
                }

                dialog.show();

            }
        }
    }

    public void updateStats(){

        if(GameData.currentPlayer == gameModel.getWinner() && GameData.isLoged){
            User user =  GameData._user.getValue();
            user.setRank(user.getRank()+20);
            user.setLevel(user.getLevel()+0.25);

            GameData.updateUser(user);
        }else if(GameData.isLoged){
            User user =  GameData._user.getValue();
            if(user.getRank()>0)
                user.setRank(user.getRank()-20);
            user.setLevel(user.getLevel()+0.10);

            GameData.updateUser(user);
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
                               hostAvatar=(resource);

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
                           guestAvatar=(resource);
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