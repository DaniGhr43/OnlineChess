package com.ilm.onlinechess;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.GameManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.ilm.onlinechess.databinding.ActivityGameBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
        gameModel=GameData.gameModel.getValue();

        if(!GameData.isOffline){
            GameData.fetchGameModel();
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


/*
        if(GameData.isLoged){
            Log.d("currentPlayerr", String.valueOf(GameData.currentPlayer));
            if(GameData.currentPlayer == GameData.WHITE){
                //Update GameData.bitmap
                Log.d("Host uri", GameData._user.getValue().getUrlImage());

                gameModel.setHostUri(GameData._user.getValue().getUrlImage());
                //Set that bitmap to the imageView
                GameData.saveGameModel(gameModel);
            }else {
                Log.d("Guest uri", GameData._user.getValue().getUrlImage());
                //Update GameData.bitmap
                //Set that bitmap to the imageView
                gameModel.setGuestUri(GameData._user.getValue().getUrlImage());
                GameData.saveGameModel(gameModel);
            }
        }

*/

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

                //updateFirebase();
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

        GameData.saveGameModel(gameModel);

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


    @Override
    public void onBackPressed() {
        // Aquí puedes agregar el código que deseas ejecutar cuando se pulsa el botón de atrás
        // Por ejemplo, mostrar un diálogo de confirmación
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("¿Seguro que quieres salir?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Game.super.onBackPressed();

                        GameData.gameModel=  new MutableLiveData<>();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
   //COMPROBAR ISLOGED
}