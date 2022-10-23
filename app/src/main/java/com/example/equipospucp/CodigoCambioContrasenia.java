package com.example.equipospucp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;

public class CodigoCambioContrasenia extends AppCompatActivity {

    boolean codigoValido = true;
    boolean passwordValido = true;
    boolean verifyPasswordValido = true;

    String codigoGenerado;
    int vecesPassword = 0;
    int vecesCodigo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_cambio_contrasenia);
        getSupportActionBar().setTitle("Cambio de contraseña");

        //Pasar el valor del codigo generado enviado por correo
        codigoGenerado = getIntent().getStringExtra("codigo");
        Log.d("codigoGenerado", codigoGenerado);

        TextInputLayout codigo = findViewById(R.id.inputCodigo_cambioPsw);
        codigo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean codigoEsEntero = true;
                boolean longitudCodigo = codigo.getEditText().getText().toString().length() == 8;
                try {
                    int codigoInt = Integer.parseInt(codigo.getEditText().getText().toString());
                } catch (NumberFormatException e) {
                    codigoEsEntero = false;
                    e.printStackTrace();
                }

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

        TextInputLayout password = findViewById(R.id.inputPassword_cambioPsw);
        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password.isErrorEnabled()) {
                    if ((password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals(""))) {
                        password.setErrorEnabled(false);
                        passwordValido = true;
                    } else {
                        password.setError("Ingrese su contraseña");
                        passwordValido = false;
                    }
                }

                if (!password.isErrorEnabled() && vecesPassword != 0) {
                    if ((password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals(""))) {
                        password.setErrorEnabled(false);
                        passwordValido = true;
                    } else {
                        password.setError("Ingrese su contraseña");
                        passwordValido = false;
                    }
                }
            }
        });

        TextInputLayout verifyPassword = findViewById(R.id.inputVerifyPassword_cambioPsw);
        verifyPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence pass, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable verifyPass) {
                if (verifyPass.toString().equals(password.getEditText().getText().toString())) {
                    //verifyPassword es identica a password
                    verifyPassword.setErrorEnabled(false);
                    verifyPasswordValido = true;
                } else {
                    //verifyPassword NO es identica a password
                    verifyPassword.setError("Las contraseñas no coinciden");
                    verifyPasswordValido = false;
                }
            }
        });
    }

    public void validarCambioContrasenia(View view) {
        TextInputLayout codigo = findViewById(R.id.inputCodigo_cambioPsw);
        TextInputLayout password = findViewById(R.id.inputPassword_cambioPsw);
        TextInputLayout verifyPassword = findViewById(R.id.inputVerifyPassword_cambioPsw);

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

        boolean passwordValido = true;
        if (password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            password.setErrorEnabled(false);
            vecesPassword++;
        } else {
            password.setError("Ingrese una contraseña");
            vecesPassword++;
            passwordValido = false;
        }

        if (verifyPassword.getEditText().getText().toString() != null && !verifyPassword.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            verifyPassword.setErrorEnabled(false);
        } else {
            verifyPassword.setError("Debe verificar su contraseña");
            verifyPasswordValido = false;
        }

        if (passwordValido && verifyPasswordValido && codigoValido) {
            //Avisar en inicarsesion que se ha cambiado la contraseña exitosamente
            Intent intent = new Intent(this, InicioSesion.class);
            intent.putExtra("exito", "Se ha cambiado la contraseña exitosamente");
            startActivity(intent);
        }
    }
}