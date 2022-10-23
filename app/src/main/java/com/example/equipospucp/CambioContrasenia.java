package com.example.equipospucp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class CambioContrasenia extends AppCompatActivity {

    boolean correoValido = true;
    int vecesCorreo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasenia);
        getSupportActionBar().setTitle("Cambio de contraseña");

        TextInputLayout correo = findViewById(R.id.inputCorreo_cambioPsw);
        correo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (correo.isErrorEnabled()) {
                    if ((correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals(""))) {
                        correo.setErrorEnabled(false);
                        correoValido = true;
                    } else {
                        correo.setError("Ingrese su correo");
                        correoValido = false;
                    }
                }

                if (!correo.isErrorEnabled() && vecesCorreo != 0) {
                    if ((correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals(""))) {
                        correo.setErrorEnabled(false);
                        correoValido = true;
                    } else {
                        correo.setError("Ingrese su correo");
                        correoValido = false;
                    }
                }
            }
        });
    }

    public String generarCodigo(int longitud) {
        String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder s = new StringBuilder(longitud);
        for (int i = 0; i < longitud; i++) {
            int ch = (int)(AlphaNumericStr.length()*Math.random());
            s.append(AlphaNumericStr.charAt(ch));
        }
        return s.toString();
    }

    public void validarCorreo(View view) {
        TextInputLayout correo = findViewById(R.id.inputCorreo_cambioPsw);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
        boolean correoValido = true;
        if (correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            if (!correo.getEditText().getText().toString().matches(emailPattern)) {
                //Texto ingresado NO cumple con el patron de un correo electronico
                correo.setError("Ingrese un correo válido");
                vecesCorreo++;
                correoValido = false;
            } else {
                //Validar si usuario existe en el sistema
                vecesCorreo++;
                correo.setErrorEnabled(false);
            }
        } else {
            //Texto NO ha sido ingresado en el edittext
            correo.setError("Ingrese un correo");
            vecesCorreo++;
            correoValido = false;
        }

        if (correoValido) {
            //Crear codigo y mandar correo
            String codigo = generarCodigo(6);
            SendEmailViewModel sendEmailViewModel = new ViewModelProvider(this).get(SendEmailViewModel.class);
            sendEmailViewModel.enviarCorreo(correo.getEditText().getText().toString(), codigo);

            Intent intent = new Intent(this, CodigoCambioContrasenia.class);
            intent.putExtra("codigo", codigo);
            startActivity(intent);
        }
    }
}