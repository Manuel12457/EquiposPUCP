package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesion extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button btninisesion;
    Button btncambiopassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);
        getSupportActionBar().setTitle("Iniciar sesión");

        firebaseAuth = firebaseAuth.getInstance();
        btninisesion = findViewById(R.id.btn_ingreso);
        btncambiopassword = findViewById(R.id.btn_cambioContrasenia);

        String mensaje_exito = getIntent().getStringExtra("exito");
        if (mensaje_exito != null && !mensaje_exito.equals("")) {
            Snackbar.make(findViewById(R.id.activity_iniciar_sesion), mensaje_exito, Snackbar.LENGTH_LONG).show();
        }

    }

    public void cambiarContrasenia(View view){
        Intent intent = new Intent(this, CambioContrasenia.class);
        startActivity(intent);
    }

    public void validarInicioSesion(View view) {

        TextInputLayout correo = findViewById(R.id.inputCorreo_iniSesion);
        TextInputLayout password = findViewById(R.id.inputPassword_iniSesion);

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
        boolean correoValido = true;
        if (correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            if (!correo.getEditText().getText().toString().matches(emailPattern)) {
                //Texto ingresado NO cumple con el patron de un correo electronico
                correo.setError("Ingrese un correo válido");
                correoValido = false;
            } else {
                //Validar si usuario existe en el sistema
                correo.setErrorEnabled(false);
            }
        } else {
            //Texto NO ha sido ingresado en el edittext
            correo.setError("Ingrese un correo");
            correoValido = false;
        }

        boolean passwordValido = true;
        if (password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            //Validar si password esta ligado con el correo ingresado en el sistema
            password.setErrorEnabled(false);
        } else {
            password.setError("Ingrese una contraseña");
            passwordValido = false;
        }

        if (correoValido && passwordValido) {
            Log.d("msg", "Correo ingresado: " + correo.getEditText().getText().toString() + " | Password ingresado: " + password.getEditText().getText().toString());

            firebaseAuth.signInWithEmailAndPassword(correo.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("task", "EXITO EN REGISTRO");

                        firebaseAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    Intent intent = new Intent(InicioSesion.this, Drawer.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar.make(findViewById(R.id.activity_iniciar_sesion), "Su cuenta no ha sido verificada. Verifíquela para poder ingresar", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

                    } else {
                        Log.d("task", "ERROR EN REGISTRO - " + task.getException().getMessage());
                        //Ver bien mensaje de error
                        Snackbar.make(findViewById(R.id.activity_iniciar_sesion), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        }
    }
}