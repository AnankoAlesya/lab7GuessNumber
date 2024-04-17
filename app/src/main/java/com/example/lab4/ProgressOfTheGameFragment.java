package com.example.lab4;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lab4.databinding.FragmentProgressOfTheGameBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgressOfTheGameFragment extends Fragment {
    private FragmentProgressOfTheGameBinding binding;

    public interface OnFragmentInteractionListener {
        void sendNotification(String textContent, boolean isWin);
        String getPlayerName();
    }

    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    int comp_num = 0;

    int attemptsLeft = 0;

    int gameMode = 0;

    boolean isWin = false;

    Date currentDate;

    MenuItem share;
    MenuItem save;

    String hintShowStr;
    String playerNameStr;

    static final String STATE_NUMBER = "CompNum";
    static final String STATE_ATTEMPTS = "AttemptsLeft";
    static final String STATE_GAME_MODE = "GameMode";
    static final String STATE_MESSAGE_TXT = "HintShowTxt";
    static final String STATE_PLAYER_NAME = "PlayerName";
    static final String STATE_DATE = "Date";

    public ProgressOfTheGameFragment() {

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STATE_NUMBER, comp_num);
        outState.putInt(STATE_ATTEMPTS, attemptsLeft);
        outState.putInt(STATE_GAME_MODE, gameMode);
        outState.putString(STATE_MESSAGE_TXT, hintShowStr);
        outState.putString(STATE_PLAYER_NAME, playerNameStr);
        outState.putSerializable(STATE_DATE, currentDate);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            comp_num = savedInstanceState.getInt(STATE_NUMBER);
            attemptsLeft = savedInstanceState.getInt(STATE_ATTEMPTS);
            gameMode = savedInstanceState.getInt(STATE_GAME_MODE);
            currentDate = (Date) savedInstanceState.getSerializable(STATE_DATE);
            hintShowStr = savedInstanceState.getString(STATE_MESSAGE_TXT);
            playerNameStr = savedInstanceState.getString(STATE_PLAYER_NAME);
        }

        Log.d("543tydbhd", "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProgressOfTheGameBinding.inflate(inflater, container, false);
        setListenersForDigitNumbers();
        binding.numUserTxt.setInputType(InputType.TYPE_NULL);
        registerForContextMenu(binding.numUserTxt);

        if (savedInstanceState != null)
        {
            binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
            binding.hintShowTxt.setText(hintShowStr);
            binding.playerNameShow.setText(playerNameStr);
        }
        else{
            attemptsLeft = 5;
            hintShowStr = getResources().getString(R.string.hint_show_str_2);
            playerNameStr = getResources().getString(R.string.player_name_show);
            binding.playerNameShow.setText(R.string.player_name_show);
            binding.hintShowTxt.setText(R.string.hint_show_str_2);
            comp_num = GuessNum.rndCompNum(10, 99);
        }

        binding.playerNameShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((gameMode == 0 && attemptsLeft < 5) || (gameMode == 1 && attemptsLeft < 7) || (gameMode == 2 && attemptsLeft < 10))
                {
                    showMessage(getResources().getString(R.string.error_change_name));
                    return;
                }

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                ChangeUserNameFragment changeUserNameFragment = ChangeUserNameFragment.newInstance(playerNameStr);
                transaction.replace(R.id.progress_of_the_game, changeUserNameFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Log.d("543tydbhd", "onCreateView");

        if (mListener.getPlayerName() != null)
        {
            setPlayerName(mListener.getPlayerName());
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbar.getMenu().clear();
        binding.toolbar.inflateMenu(R.menu.main_menu);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.settingMenuItem) openWindowChooseGameMode();

            if (item.getItemId() == R.id.share_info) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);

                String message = getMessage();

                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setType("text/plain");

                if (sendIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(sendIntent);
                }
            }

            if (item.getItemId() == R.id.save_info) {
                Intent saveIntent = new Intent();
                saveIntent.setPackage("com.google.android.keep");

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String message = sdf.format(currentDate) + " " + getMessage();

                saveIntent.putExtra(Intent.EXTRA_TEXT, message);
                saveIntent.setType("text/plain");

                if (saveIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(saveIntent);
                } else {
                    showMessage(getResources().getString(R.string.error_message_google_keep));
                }
            }

            if (item.getItemId() == R.id.aboutMenuItem)
            {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setReorderingAllowed(true);

                DeveloperInfoFragment developerInfoFragment = new DeveloperInfoFragment();
                transaction.replace(R.id.progress_of_the_game, developerInfoFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

            return true;
        });

        share = binding.toolbar.getMenu().findItem(R.id.share_info);
        save = binding.toolbar.getMenu().findItem(R.id.save_info);

        if (attemptsLeft == 0 || isWin) {
            binding.guessBtn.setClickable(false);

            share.setEnabled(true);
            save.setEnabled(true);
        }
    }

    @NonNull
    private String getMessage() {
        String resultMessage = getResources().getString(R.string.result_message);
        String statusMessage = attemptsLeft == 0 ? getResources().getString(R.string.result_message_defeat)
                : getResources().getString(R.string.result_message_succesfully);

        String guessNumMessage = getResources().getString(R.string.guess_num_message);
        String attemptsLeftMessage = getResources().getString(R.string.attempts_left_message);

        return guessNumMessage + " " + comp_num + " " + attemptsLeftMessage + " " + attemptsLeft + " " +
                resultMessage + " " + statusMessage;
    }

    private void setListenersForDigitNumbers() {
        binding.button0.setOnClickListener(this::onClickNumber);
        binding.button1.setOnClickListener(this::onClickNumber);
        binding.button2.setOnClickListener(this::onClickNumber);
        binding.button3.setOnClickListener(this::onClickNumber);
        binding.button4.setOnClickListener(this::onClickNumber);
        binding.button5.setOnClickListener(this::onClickNumber);
        binding.button6.setOnClickListener(this::onClickNumber);
        binding.button7.setOnClickListener(this::onClickNumber);
        binding.button8.setOnClickListener(this::onClickNumber);
        binding.button9.setOnClickListener(this::onClickNumber);

        binding.clearBtn.setOnClickListener(this::clear);
        binding.guessBtn.setOnClickListener(this::guess);
        binding.restartBtn.setOnClickListener(this::restart);
    }

    public void onClickNumber(View view) {
        int start = binding.numUserTxt.getSelectionStart();
        int end = binding.numUserTxt.getSelectionEnd();
        int length = binding.numUserTxt.getText().length();
        String digit = "";

        if (end - start == length && length != 0) binding.numUserTxt.setText("");

        if (view.getId() == R.id.button_0) digit = "0";
        if (view.getId() == R.id.button_1) digit = "1";
        if (view.getId() == R.id.button_2) digit = "2";
        if (view.getId() == R.id.button_3) digit = "3";
        if (view.getId() == R.id.button_4) digit = "4";
        if (view.getId() == R.id.button_5) digit = "5";
        if (view.getId() == R.id.button_6) digit = "6";
        if (view.getId() == R.id.button_7) digit = "7";
        if (view.getId() == R.id.button_8) digit = "8";
        if (view.getId() == R.id.button_9) digit = "9";

        binding.numUserTxt.setText(binding.numUserTxt.getText().toString() + digit);
        binding.numUserTxt.setSelection(binding.numUserTxt.getText().length());
    }

    public void clear(View view) {
        binding.numUserTxt.setText("");
    }

    public void restart(View view) {
        startNewGame(gameMode);
    }

    private void startNewGame(int gameMode) {
        binding.guessBtn.setText(R.string.guess_str);
        playerNameStr = getResources().getString(R.string.player_name_show);
        binding.playerNameShow.setText(R.string.player_name_show);
        binding.numUserTxt.setText("");

        binding.guessBtn.setClickable(true);
        share.setEnabled(false);
        save.setEnabled(false);

        if (gameMode == 0) {
            attemptsLeft = 5;
            hintShowStr = getResources().getString(R.string.hint_show_str_2);
            binding.hintShowTxt.setText(R.string.hint_show_str_2);
            comp_num = GuessNum.rndCompNum(10, 99);
        } else if (gameMode == 1) {
            attemptsLeft = 7;
            hintShowStr = getResources().getString(R.string.hint_show_str_3);
            binding.hintShowTxt.setText(R.string.hint_show_str_3);
            comp_num = GuessNum.rndCompNum(100, 999);
        } else {
            attemptsLeft = 10;
            hintShowStr = getResources().getString(R.string.hint_show_str_4);
            binding.hintShowTxt.setText(R.string.hint_show_str_4);
            comp_num = GuessNum.rndCompNum(1000, 9999);
        }

        binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
        isWin = false;
    }

    public void showMessage(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void guess(View view) {
        String textNumber = binding.numUserTxt.getText().toString();
        if (textNumber.equals("")) return;

        int number = Integer.parseInt(binding.numUserTxt.getText().toString());

        if (attemptsLeft > 0) {
            if (number == comp_num) {
                binding.guessBtn.setText(R.string.guessed_str);
                binding.guessBtn.setClickable(false);

                String message = getResources().getString(R.string.wishYou);
                mListener.sendNotification(message, isWin);
                showMessage(message);
                share.setEnabled(true);
                save.setEnabled(true);

                currentDate = new Date();
                isWin = true;
                return;
            } else if (number < comp_num) {
                binding.hintShowTxt.setText(R.string.hint_more);
                hintShowStr = getResources().getString(R.string.hint_more);
            }
            else {
                binding.hintShowTxt.setText(R.string.hint_less);
                hintShowStr = getResources().getString(R.string.hint_more);
            }

            attemptsLeft--;
            binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
            binding.numUserTxt.requestFocus();
            binding.numUserTxt.selectAll();
        }

        if (attemptsLeft == 0) {
            hintShowStr = getResources().getString(R.string.defeat);
            binding.hintShowTxt.setText(R.string.defeat);
            binding.guessBtn.setClickable(false);

            share.setEnabled(true);
            save.setEnabled(true);

            currentDate = new Date();
            String message = getResources().getString(R.string.defeat);
            mListener.sendNotification(message, isWin);
            showMessage(message + " " + comp_num);
        }
    }

    private void openWindowChooseGameMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom);
        builder.setTitle(R.string.settings);

        builder.setSingleChoiceItems(R.array.diaps_array, gameMode, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startNewGame(which);
                gameMode = which;
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

    public boolean checkNumbers(String symbols) {
        for (int i = 0; i < symbols.length(); i++) {
            if (!Character.isDigit(symbols.charAt(i))) return false;
        }

        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.copy_text) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", binding.numUserTxt.getText().toString());
            clipboard.setPrimaryClip(clip);
            String message = getResources().getString(R.string.copy_message);
            showMessage(message);
        }

        if (item.getItemId() == R.id.insert_text) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                ClipData.Item item_data = clipboard.getPrimaryClip().getItemAt(0);
                String pastedText = item_data.getText().toString();

                if (checkNumbers(pastedText)) binding.numUserTxt.setText(pastedText);
                else showMessage(getResources().getString(R.string.incorret_data));
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    public void setPlayerName(String playerName)
    {
        playerNameStr = playerName;
        binding.playerNameShow.setText(playerName);
    }
}