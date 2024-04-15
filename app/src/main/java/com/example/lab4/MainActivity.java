package com.example.lab4;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
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
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lab4.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    int comp_num = 0;

    int attemptsLeft = 0;

    int gameMode = 0;

    boolean isWin = false;

    MenuItem share;

    MenuItem save;

    Date currentDate;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    static final String STATE_NUMBER = "CompNum";
    static final String STATE_ATTEMPTS = "AttemptsLeft";
    static final String STATE_GAME_MODE = "GameMode";
    static final String STATE_MESSAGE_TXT = "HintShowTxt";
    static final String STATE_PLAYER_NAME = "PlayerName";

    private static final String LOG_TAG = MainActivity.class.getSimpleName() + "1";

    private static final int NOTIFY_ID = 101;
    private static final String CHANNEL_ID = "GuessChannel";
    private static final String CHANNEL_NAME = "Guess notification channel";
    private static final int PERMISSION_REQUEST_CODE = 1001;


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
        outState.putString(STATE_PLAYER_NAME, binding.playerNameShow.getText().toString());
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
        binding.playerNameShow.setText(savedInstanceState.getString(STATE_PLAYER_NAME));

        if (attemptsLeft == 0 || isWin) {
            binding.guessBtn.setClickable(false);
        }

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

        if (item.getItemId() == R.id.aboutMenuItem) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        if (item.getItemId() == R.id.share_info) {
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

        if (item.getItemId() == R.id.save_info) {
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
            else
            {
                showMessage(getResources().getString(R.string.error_message_google_keep));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        share = menu.findItem(R.id.share_info);
        save = menu.findItem(R.id.save_info);

        if (attemptsLeft == 0 || isWin) {
            share.setEnabled(true);
            save.setEnabled(true);
        }

        Log.i(LOG_TAG, "Menu");
        return true; //super.onCreateOptionsMenu(menu);
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
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", binding.numUserTxt.getText().toString());
            clipboard.setPrimaryClip(clip);
            String message = getResources().getString(R.string.copy_message);
            showMessage(message);
        }

        if (item.getItemId() == R.id.insert_text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
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
        /*
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.numUserTxt.setInputType(InputType.TYPE_NULL);
        EditText editText = findViewById(R.id.num_user_txt);
        registerForContextMenu(editText);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            String playerName = data.getStringExtra("playerName");
                            binding.playerNameShow.setText(playerName);
                        }
                    }
                }
        );

        binding.playerNameShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((gameMode == 0 && attemptsLeft < 5) || (gameMode == 1 && attemptsLeft < 7) || (gameMode == 2 && attemptsLeft < 10))
                {
                    showMessage(getResources().getString(R.string.error_change_name));
                    return;
                }

                Intent intent = new Intent(MainActivity.this, PlayerNameActivity.class);
                intent.putExtra("playerName", binding.playerNameShow.getText().toString());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    activityResultLauncher.launch(intent);
                }
            }
        });

        attemptsLeft = 5;
        binding.hintShowTxt.setText(R.string.hint_show_str_2);
        comp_num = GuessNum.rndCompNum(10, 99);
        createNotificationChannel();

        Log.i(LOG_TAG, "Угадай число.onCreate()");

         */
    }

    public void restart(View view) {
        startNewGame(gameMode);
    }

    private void startNewGame(int gameMode) {
        binding.guessBtn.setText(R.string.guess_str);
        binding.playerNameShow.setText(R.string.player_name_show);
        binding.numUserTxt.setText("");

        binding.guessBtn.setClickable(true);
        share.setEnabled(false);
        save.setEnabled(false);

        if (gameMode == 0) {
            attemptsLeft = 5;
            binding.hintShowTxt.setText(R.string.hint_show_str_2);
            comp_num = GuessNum.rndCompNum(10, 99);
        } else if (gameMode == 1) {
            attemptsLeft = 7;
            binding.hintShowTxt.setText(R.string.hint_show_str_3);
            comp_num = GuessNum.rndCompNum(100, 999);
        } else {
            attemptsLeft = 10;
            binding.hintShowTxt.setText(R.string.hint_show_str_4);
            comp_num = GuessNum.rndCompNum(1000, 9999);
        }

        binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
        isWin = false;
    }

    public void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openWindowChooseGameMode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
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

    public void clear(View view) {
        binding.numUserTxt.setText("");
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

    public void guess(View view) {
        String textNumber = binding.numUserTxt.getText().toString();
        if (textNumber.equals("")) return;

        int number = Integer.parseInt(binding.numUserTxt.getText().toString());

        if (attemptsLeft > 0) {
            if (number == comp_num) {
                binding.guessBtn.setText(R.string.guessed_str);
                binding.guessBtn.setClickable(false);

                String message = getResources().getString(R.string.wishYou);
                SendNotification(message);
                showMessage(message);
                share.setEnabled(true);
                save.setEnabled(true);

                currentDate = new Date();
                isWin = true;
                return;
            } else if (number < comp_num) binding.hintShowTxt.setText(R.string.hint_more);
            else binding.hintShowTxt.setText(R.string.hint_less);

            attemptsLeft--;
            binding.attemptsLeftTxt.setText(Integer.toString(attemptsLeft));
            binding.numUserTxt.requestFocus();
            binding.numUserTxt.selectAll();
        }

        if (attemptsLeft == 0) {
            binding.hintShowTxt.setText(R.string.defeat);
            binding.guessBtn.setClickable(false);
            share.setEnabled(true);
            save.setEnabled(true);

            currentDate = new Date();
            String message = getResources().getString(R.string.defeat);
            SendNotification(message);
            showMessage(message + " " + comp_num);
        }
    }

    public void SendNotification(String textContent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getResources().getString(R.string.result_message))
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(MainActivity.this);

        notificationManager.cancelAll();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }

        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String message = !isWin ? getResources().getString(R.string.defeat) :
                        getResources().getString(R.string.wishYou);

                SendNotification(message);
            } else {
                showMessage(getResources().getString(R.string.error_permission_notification));
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
