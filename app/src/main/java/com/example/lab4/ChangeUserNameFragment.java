package com.example.lab4;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.lab4.databinding.FragmentChangeUserNameBinding;

public class ChangeUserNameFragment extends Fragment {
    private static final String ARG_PLAYER_NAME = "PlayerName";
    private String playerName;
    private FragmentChangeUserNameBinding binding;

    public ChangeUserNameFragment() {
        // Required empty public constructor
    }
    
    public static ChangeUserNameFragment newInstance(String playerName) {
        ChangeUserNameFragment fragment = new ChangeUserNameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYER_NAME, playerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerName = getArguments().getString(ARG_PLAYER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangeUserNameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.playerNameEdit.setText(playerName);
        binding.toolbar4.setNavigationIcon(R.drawable.navigation_icon);
        binding.toolbar4.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        binding.toolbar4.setTitle(R.string.player_name);

        binding.playerNameEdit.requestFocus();
        showSoftKeyboard(binding.playerNameEdit);

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = binding.playerNameEdit.getText().toString().trim();
                if (playerName.length() == 0)
                {
                    showMessage(getResources().getString(R.string.empty_string_msg));
                    return;
                }

                Bundle result = new Bundle();
                result.putString("PlayerName", playerName);
                getParentFragmentManager().setFragmentResult("GetPlayerName", result);
                getParentFragmentManager().popBackStack();
            }
        });
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = getActivity().getSystemService(InputMethodManager.class);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}