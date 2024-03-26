package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class PlayerNameActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_name);

        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.player_name);
        }

        editText = (EditText) findViewById(R.id.player_name_edit);

        Bundle arguments = getIntent().getExtras();

        if (arguments != null)
        {
            editText.setText(arguments.getString("playerName"));
            editText.selectAll();
        }

        editText.requestFocus();
        showSoftKeyboard(editText);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = getSystemService(InputMethodManager.class);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void setPlayerName(View view)
    {
        String playerName = editText.getText().toString().trim();

        if (playerName.length() == 0)
        {
            showMessage(getResources().getString(R.string.empty_string_msg));
            return;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("playerName", playerName);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void showMessage(String message)
    {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}