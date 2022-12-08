package com.lesd.exploratchoo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lesd.exploratchoo.databinding.SettingsBinding;

public class SettingsActivity extends AppCompatActivity
{
    private SettingsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = SettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences preferences = this.getSharedPreferences("com.lesd.exploratchoo", MODE_PRIVATE);

        String api_key = preferences.getString("api_key", null);

        EditText editText = binding.apiKey;

        editText.setText(api_key);

        Button save = binding.save;

        save.setOnClickListener(v ->
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("api_key", editText.getText().toString());
            editor.apply();
            finish();
        });
    }
}
