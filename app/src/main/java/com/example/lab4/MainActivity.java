package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
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
        Context context = getApplicationContext();
        View.OnClickListener clckLstnr = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                {
                    int attemptsLeft = Integer.parseInt(binding.attemptsLeftTxt.getText().toString());
                    int number = Integer.parseInt(binding.numUserTxt.getText().toString());

                    if (attemptsLeft > 0)
                    {
                        if (number == comp_num)
                        {
                            binding.guessBtn.setText(R.string.guessed_str);
                            binding.guessBtn.setClickable(false);
                            Toast t = Toast.makeText(context, R.string.wishYou, Toast.LENGTH_LONG);
                            t.show();
                        }
                        else if (number < comp_num)
                        {
                            binding.hintShowTxt.setText(R.string.hint_more);
                        }
                        else if (number > comp_num)
                        {
                            binding.hintShowTxt.setText(R.string.hint_less);
                        }

                        attemptsLeft--;
                        binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
                    }

                    if (attemptsLeft == 0)
                    {
                        binding.hintShowTxt.setText(R.string.defeat);
                        Toast t = Toast.makeText(context, R.string.defeat, Toast.LENGTH_LONG);
                        t.show();
                    }
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
}