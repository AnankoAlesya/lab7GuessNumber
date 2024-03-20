package com.example.lab4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lab4.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    int comp_num = 0;

    int attemptsLeft = 0;

    int gameMode = 0;

    MenuItem share;
    MenuItem save;

    Date currentDate;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    static final String STATE_NUMBER = "CompNum";
    static final String STATE_ATTEMPTS = "AttemptsLeft";
    static final String STATE_GAME_MODE = "GameMode";
    static final String STATE_MESSAGE_TXT = "HintShowTxt";

    private static final String LOG_TAG = MainActivity.class.getSimpleName() + "1";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "Destroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "Stop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "Resume");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(STATE_NUMBER, comp_num);
        outState.putInt(STATE_ATTEMPTS, attemptsLeft);
        outState.putInt(STATE_GAME_MODE, gameMode);
        outState.putString(STATE_MESSAGE_TXT, binding.hintShowTxt.getText().toString());
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "Save");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        comp_num = savedInstanceState.getInt(STATE_NUMBER);
        attemptsLeft = savedInstanceState.getInt(STATE_ATTEMPTS);
        gameMode = savedInstanceState.getInt(STATE_GAME_MODE);

        binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
        binding.hintShowTxt.setText(savedInstanceState.getString(STATE_MESSAGE_TXT));

        if (attemptsLeft == 0) binding.guessBtn.setClickable(false);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "Restart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "Start");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingMenuItem) openWindowChooseGameMode();

        if (item.getItemId() == R.id.aboutMenuItem)
        {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        if (item.getItemId() == R.id.share_info)
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            String resultMessage = getResources().getString(R.string.result_message);
            String statusMessage = attemptsLeft == 0 ? getResources().getString(R.string.result_message_defeat)
                    : getResources().getString(R.string.result_message_succesfully);

            String guessNumMessage = getResources().getString(R.string.guess_num_message);
            String attemptsLeftMessage = getResources().getString(R.string.attempts_left_message);

            String message = guessNumMessage + " " + comp_num + " " + attemptsLeftMessage + " " + attemptsLeft + " " +
                    resultMessage + " " + statusMessage;

            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");

            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sendIntent);
            }
        }

        if (item.getItemId() == R.id.save_info)
        {
            Intent saveIntent = new Intent();
            saveIntent.setPackage("com.google.android.keep");

            String resultMessage = getResources().getString(R.string.result_message);
            String statusMessage = attemptsLeft == 0 ? getResources().getString(R.string.result_message_defeat)
                    : getResources().getString(R.string.result_message_succesfully);

            String guessNumMessage = getResources().getString(R.string.guess_num_message);
            String attemptsLeftMessage = getResources().getString(R.string.attempts_left_message);

            String message = sdf.format(currentDate) + " " + guessNumMessage + " " + comp_num + " " + attemptsLeftMessage + " " + attemptsLeft + " " +
                    resultMessage + " " + statusMessage;

            saveIntent.putExtra(Intent.EXTRA_TEXT, message);
            saveIntent.setType("text/plain");

            if (saveIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(saveIntent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        share = menu.findItem(R.id.share_info);
        save = menu.findItem(R.id.save_info);
        return true; //super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.copy_text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", binding.numUserTxt.getText().toString());
            clipboard.setPrimaryClip(clip);
            String message = getResources().getString(R.string.copy_message);
            showMessage(getApplicationContext(),message);
        }

        if (item.getItemId()==R.id.inset_text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                ClipData.Item item_data = clipboard.getPrimaryClip().getItemAt(0);
                String pastedText = item_data.getText().toString();
                binding.numUserTxt.setText(pastedText);
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.numUserTxt.setInputType(InputType.TYPE_NULL);
        EditText editText = findViewById(R.id.num_user_txt);
        registerForContextMenu(editText);

        attemptsLeft = 5;
        binding.hintShowTxt.setText(R.string.hint_show_str_2);
        comp_num = GuessNum.rndCompNum(10, 99);
        Log.i (LOG_TAG, "Угадай число.onCreate()");
    }

    public void restart(View view) {
        binding.guessBtn.setText(R.string.guess_str);
        binding.guessBtn.setClickable(true);
        binding.numUserTxt.setText("");
        share.setEnabled(false);
        save.setEnabled(false);

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

    private void openWindowChooseGameMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setTitle(R.string.settings);

        builder.setSingleChoiceItems(R.array.diaps_array, gameMode, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                binding.guessBtn.setText(R.string.guess_str);
                binding.guessBtn.setClickable(true);
                binding.numUserTxt.setText("");
                share.setEnabled(false);
                save.setEnabled(false);

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
                share.setEnabled(true);
                save.setEnabled(true);
                currentDate = new Date();
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
            share.setEnabled(true);
            save.setEnabled(true);
            currentDate = new Date();
            String message = getResources().getString(R.string.defeat);
            showMessage(context, message + " " + comp_num);
        }
    }
}