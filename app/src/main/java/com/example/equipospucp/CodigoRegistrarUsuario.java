package com.example.equipospucp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class CodigoRegistrarUsuario extends AppCompatActivity {

    boolean codigoValido = true;
    int vecesCodigo = 0;

    String codigoGenerado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_registrar_usuario);
        getSupportActionBar().setTitle("Registro de usuario");
        codigoGenerado = getIntent().getStringExtra("codigo");
        Log.d("codigoGenerado", codigoGenerado);

        TextInputLayout codigo = findViewById(R.id.inputCodigoGenerado_registro);
        codigo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (codigo.isErrorEnabled()) {
                    if ((codigo.getEditText().getText().toString() != null && !codigo.getEditText().getText().toString().equals(""))) {
                        codigo.setErrorEnabled(false);
                        codigoValido = true;
                    } else {
                        codigo.setError("Ingrese el código");
                        codigoValido = false;
                    }
                }

                if (!codigo.isErrorEnabled() && vecesCodigo != 0) {
                    if ((codigo.getEditText().getText().toString() != null && !codigo.getEditText().getText().toString().equals(""))) {
                        codigo.setErrorEnabled(false);
                        codigoValido = true;
                    } else {
                        codigo.setError("Ingrese el código");
                        codigoValido = false;
                    }
                }
            }
        });
    }

    public void validarCodigoRegistro(View view) {
        TextInputLayout codigo = findViewById(R.id.inputCodigoGenerado_registro);

        boolean codigoValido = true;
        if (codigo.getEditText().getText().toString() != null && !codigo.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            if (codigo.getEditText().getText().toString().equals(codigoGenerado)) {
                //Codigo ingresado es el correcto
                vecesCodigo++;
                codigo.setErrorEnabled(false);
            } else {
                //Codigo ingresado NO es el correcto
                codigo.setError("El código no es válido");
                vecesCodigo++;
                codigoValido = false;
            }
        } else {
            codigo.setError("Ingrese el código");
            vecesCodigo++;
            codigoValido = false;
        }

        if (codigoValido) {
            //Avisar en inicarsesion que se ha registrado al usuario exitosamente
            Intent intent = new Intent(this, InicioSesion.class);
            intent.putExtra("exito", "Usted se ha registrado exitosamente");
            startActivity(intent);
        }
    }
}