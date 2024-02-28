package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

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
    int comp_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        comp_num = GuessNum.rndCompNum();

        binding.numUserTxt.requestFocus();
        showSoftKeyboard(binding.numUserTxt);

        Context context = getApplicationContext();

        View.OnClickListener clckLstnr = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int attemptsLeft = Integer.parseInt(binding.attemptsLeftTxt.getText().toString());
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
        comp_num = GuessNum.rndCompNum();
        binding.guessBtn.setText(R.string.guess_str);
        binding.attemptsLeftTxt.setText(R.string.attempts_left_str);
        binding.hintShowTxt.setText(R.string.hint_show_str);
        binding.guessBtn.setClickable(true);
        binding.numUserTxt.setText("");
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
}