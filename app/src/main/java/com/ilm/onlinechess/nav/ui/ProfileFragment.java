package com.ilm.onlinechess.nav.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ilm.onlinechess.game.GameData;
import com.ilm.onlinechess.UserDTO;
import com.ilm.onlinechess.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;
    private UserDTO user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


       user = GameData._user.getValue();
        if(user==null)
            binding.username.setText(("Sign in to record here your personal stats"));
        else{

            setUserImage(user.getUrlImage());
            binding.username.setText(String.valueOf(user.getUsername()));
            binding.email.setText(String.valueOf(user.getEmail()));
            binding.level.setText(String.valueOf((int)user.getLevel()));
            binding.rank.setText(String.valueOf(user.getRank()));

        }

        return root;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }
    private void setUserImage(String uri) {
        //if (GameData.isLoged ) {
        Uri photoUrl = Uri.parse((uri));
        // Uri photoUrl = Uri.parse(GameData._user.getValue().getUrlImage());
        if (photoUrl != null) {
            // Usar Glide para cargar la imagen
            Glide.with(this)
                    .asBitmap()
                    .load(photoUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                            binding.imageView5.setImageBitmap(resource);
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
    }

}