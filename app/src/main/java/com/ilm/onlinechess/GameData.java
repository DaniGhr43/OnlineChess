package com.ilm.onlinechess;

import static androidx.fragment.app.FragmentManager.TAG;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ilm.onlinechess.ui.login.LoginFragment;

import java.util.Random;
import java.util.concurrent.Executor;

public class GameData  {


    public static MutableLiveData<GameModel> _gameModel = new MutableLiveData<>();

    //To acces the gameModel of this class us this variable
    public static LiveData<GameModel> gameModel = _gameModel;

    public static int currentPlayer;

    public static int tempGameID= 0 ;
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    public static final int CREATE = 1;
    public static final int JOIN = 2;
    public static final int STARTED = 3;
    public static final int OFFLINE = -1;
    public static boolean isLoged=false;
    public static int turn;
    public static User user = null;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static void saveGameModel(GameModel model){
        _gameModel.postValue(model);
        gameModel=_gameModel;

        if(model.getGAME_STATUS() != OFFLINE){
            db.collection("games")
                    .document(String.valueOf(model.gameId))
                    .set(model);
        }



    }
    public static void saveGameModelOffline(GameModel model){
        _gameModel.postValue(model);
        gameModel=_gameModel;



    }



    public static void fetchGameModel(){



        db.collection("games")
                .document(String.valueOf(gameModel.getValue().gameId))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                    @Nullable FirebaseFirestoreException e) {
                                    GameModel model = snapshot.toObject(GameModel.class);
                                    if (e != null) {
                                        Log.w("fetchGameModel", "Listen failed.", e);
                                        return;
                                    }

                                    if (snapshot != null && snapshot.exists()) {
                                        Log.d("fetchGameModel", "Current data: " + snapshot.getData());
                                        _gameModel.postValue(model);
                                    } else {
                                        Log.d("fetchGameModel", "Current data: null");
                                    }

                                }
                            });
    }


    public static void updateUser(User user){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        GameData.user=user;

        db.collection("users")
                .document(String.valueOf(user.getEmail()))
                .set(user);
    }
}

//bloqeuar si intenas crear sesion estand sin conexion
//evitar que los dos puedan mover las dos piezas
