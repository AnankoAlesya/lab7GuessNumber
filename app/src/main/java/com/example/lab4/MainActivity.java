package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

        binding.numUserTxt.requestFocus();
        showSoftKeyboard(binding.numUserTxt);

        attemptsLeft = 5;
        binding.hintShowTxt.setText(R.string.hint_show_str_2);
        comp_num = GuessNum.rndCompNum(10, 99);

        Context context = getApplicationContext();

        View.OnClickListener clckLstnr = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //int attemptsLeft = Integer.parseInt(binding.attemptsLeftTxt.getText().toString());
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
                }

                if (attemptsLeft == 0)
                {
                    binding.hintShowTxt.setText(R.string.defeat);
                    binding.guessBtn.setClickable(false);
                    String message = getResources().getString(R.string.defeat);
                    showMessage(context, message + " " + comp_num);
                }
            }
        };

        binding.guessBtn.setOnClickListener(clckLstnr);
    }

    public void restart(View view) {
        //comp_num = GuessNum.rndCompNum();
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

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = getSystemService(InputMethodManager.class);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void showMessage(Context context, String message)
    {
        Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);
        t.show();
    }

    public void chooseGameMode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.settings);

        /*builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                flag = true;

            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });*/

        builder.setSingleChoiceItems(R.array.diaps_array, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
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
}