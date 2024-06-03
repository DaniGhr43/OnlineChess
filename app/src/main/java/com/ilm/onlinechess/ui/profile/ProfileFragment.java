package com.ilm.onlinechess.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ilm.onlinechess.GameData;
import com.ilm.onlinechess.GameModel;
import com.ilm.onlinechess.User;
import com.ilm.onlinechess.databinding.FragmentLoginBinding;
import com.ilm.onlinechess.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;
    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


       user = GameData._user.getValue();
        if(user==null)
            binding.username.setText(("Sign in to record here your personal stats"));
        else{


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


}