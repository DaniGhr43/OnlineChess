package com.ilm.onlinechess.Game;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ilm.onlinechess.User;

public class GameData  {


    public static MutableLiveData<GameModel> _gameModel = new MutableLiveData<>();

    //To acces the gameModel of this class us this variable
    public static LiveData<GameModel> gameModel = _gameModel;
    public static int currentPlayer;
    public static MutableLiveData<User> _user = new MutableLiveData<>();
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int CREATE = 1;
    public static final int JOIN = 2;
    public static final int STARTED = 3;
    public static final int FINISHED = 4;
    public static boolean isLoged=false;
    public static int turn = 0;
    public static boolean isOffline;
    public static Bitmap guestAvatar;
    public static Bitmap hostAvatar ;

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void saveGameModel(final GameModel model, boolean saveOnBD) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("saveGameModel", "saveGameModel");

                _gameModel.postValue(model);

                if (!isOffline && saveOnBD) {
                    db.collection("games")
                            .document(String.valueOf(model.gameId))
                            .set(model);
                }
            }
        }).start();
    }



    //To update in realtime the gameModel with the data of firestore colecction
    public static void fetchGameModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("games")
                        .document(String.valueOf(gameModel.getValue().gameId))
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {

                                if (e != null) {
                                    Log.w("fetchGameModel", "Listen failed.", e);
                                    return;
                                }

                                if (snapshot != null && snapshot.exists()) {
                                    Log.d("fetchGameModel", "Current data: " + snapshot.getData());
                                    GameModel model = snapshot.toObject(GameModel.class);
                                    _gameModel.postValue(model);
                                } else {
                                    Log.d("fetchGameModel", "Current data: null");
                                }
                            }
                        });
            }
        }).start();
    }


    public static void updateUser(User user){

        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                _user.postValue(user);

                db.collection("users")
                                .

                        document(String.valueOf(user.getEmail()))
                                .

                        set(user);
            }
        }).start();
    }



}

//bloqeuar si intenas crear sesion estand sin conexion

