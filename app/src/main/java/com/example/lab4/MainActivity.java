package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab4.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    int comp_num = 0; //генерирумое число

    int attemptsLeft = 0; //количество попыток

    int gameMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.numUserTxt.setInputType(InputType.TYPE_NULL);

        attemptsLeft = 5;
        binding.hintShowTxt.setText(R.string.hint_show_str_2);
        comp_num = GuessNum.rndCompNum(10, 99);
    }

    public void restart(View view) {
        binding.guessBtn.setText(R.string.guess_str);
        binding.guessBtn.setClickable(true);
        binding.numUserTxt.setText("");

        if (gameMode == 0)
        {
            attemptsLeft = 5;
            binding.hintShowTxt.setText(R.string.hint_show_str_2);
            comp_num = GuessNum.rndCompNum(10, 99);
        }
        else if (gameMode == 1)
        {
            attemptsLeft = 7;
            binding.hintShowTxt.setText(R.string.hint_show_str_3);
            comp_num = GuessNum.rndCompNum(100, 999);
        }
        else
        {
            attemptsLeft = 10;
            binding.hintShowTxt.setText(R.string.hint_show_str_4);
            comp_num = GuessNum.rndCompNum(1000, 9999);
        }

        binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
    }

    public void showMessage(Context context, String message)
    {
        Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);
        t.show();
    }

    public void chooseGameMode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.settings);

        builder.setSingleChoiceItems(R.array.diaps_array, gameMode, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                binding.guessBtn.setText(R.string.guess_str);
                binding.guessBtn.setClickable(true);
                binding.numUserTxt.setText("");

                if (which == 0)
                {
                    attemptsLeft = 5;
                    binding.hintShowTxt.setText(R.string.hint_show_str_2);
                    comp_num = GuessNum.rndCompNum(10, 99);
                    gameMode = 0;
                }
                else if (which == 1)
                {
                    attemptsLeft = 7;
                    binding.hintShowTxt.setText(R.string.hint_show_str_3);
                    comp_num = GuessNum.rndCompNum(100, 999);
                    gameMode = 1;
                }
                else
                {
                    attemptsLeft = 10;
                    binding.hintShowTxt.setText(R.string.hint_show_str_4);
                    comp_num = GuessNum.rndCompNum(1000, 9999);
                    gameMode = 2;
                }

                binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
                binding.numUserTxt.setText("");
                dialog.dismiss();
            }
        });

        builder.create();
        builder.show();
    }

    public void clear(View view) {
        binding.numUserTxt.setText("");
    }

    public void onClickNumber(View view) {
        int start = binding.numUserTxt.getSelectionStart();
        int end = binding.numUserTxt.getSelectionEnd();
        int length = binding.numUserTxt.getText().length();

        if (end - start == length && length != 0) {
            binding.numUserTxt.setText("");
        }

        String digit = "";

        if (view.getId() == R.id.button_0) digit = "0";
        else if (view.getId() == R.id.button_1) digit = "1";
        else if (view.getId() == R.id.button_2) digit = "2";
        else if (view.getId() == R.id.button_3) digit = "3";
        else if (view.getId() == R.id.button_4) digit = "4";
        else if (view.getId() == R.id.button_5) digit = "5";
        else if (view.getId() == R.id.button_6) digit = "6";
        else if (view.getId() == R.id.button_7) digit = "7";
        else if (view.getId() == R.id.button_8) digit = "8";
        else if (view.getId() == R.id.button_9) digit = "9";
        else digit = "0";

        binding.numUserTxt.setText(binding.numUserTxt.getText().toString() + digit);
        binding.numUserTxt.setSelection(binding.numUserTxt.getText().length());
    }

    public void guess(View view) {
        Context context = getApplicationContext();
        String textNumber = binding.numUserTxt.getText().toString();
        if (textNumber.equals("")) return;

        int number = Integer.parseInt(binding.numUserTxt.getText().toString());

        if (attemptsLeft > 0)
        {
            if (number == comp_num)
            {
                binding.guessBtn.setText(R.string.guessed_str);
                binding.guessBtn.setClickable(false);
                String message = getResources().getString(R.string.wishYou);
                showMessage(context, message);
                return;
            }
            else if (number < comp_num) binding.hintShowTxt.setText(R.string.hint_more);
            else binding.hintShowTxt.setText(R.string.hint_less);

            attemptsLeft--;
            binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
            binding.numUserTxt.requestFocus();
            binding.numUserTxt.selectAll();
        }

        if (attemptsLeft == 0)
        {
            binding.hintShowTxt.setText(R.string.defeat);
            binding.guessBtn.setClickable(false);
            String message = getResources().getString(R.string.defeat);
            showMessage(context, message + " " + comp_num);
        }
    }
}