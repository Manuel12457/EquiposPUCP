package com.example.equipospucp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    public void iniciarSesion(View view){
        Intent intent = new Intent(this, InicioSesion.class);
        startActivity(intent);
    }

    public void registroUsuario(View view){
        Intent intent = new Intent(this,RegistrarUsuario.class);
        startActivity(intent);
    }
}