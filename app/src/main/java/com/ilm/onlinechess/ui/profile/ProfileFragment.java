package com.ilm.onlinechess.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ilm.onlinechess.GameData;
import com.ilm.onlinechess.databinding.FragmentLoginBinding;
import com.ilm.onlinechess.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.username.setText(String.valueOf(GameData.user.getUsername()));
        binding.email.setText(String.valueOf(GameData.user.getEmail()));
        binding.level.setText(String.valueOf(GameData.user.getLevel()));
        binding.rank.setText(String.valueOf(GameData.user.getRank()));

        return root;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        binding = null;
    }


}