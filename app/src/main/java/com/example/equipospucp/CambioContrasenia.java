package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.equipospucp.DTOs.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CambioContrasenia extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceCorreos;
    ValueEventListener valueEventListener;
    ArrayList<String> listaCorreosRegistrados = new ArrayList<>();

    Button btn;

    boolean correoValido = true;
    int vecesCorreo = 0;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_contrasenia);
        getSupportActionBar().setTitle("Cambio de contraseña");

        btn = findViewById(R.id.btn_enviarCorreo);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceCorreos = firebaseDatabase.getReference("usuarios");
        valueEventListener = databaseReferenceCorreos.addValueEventListener(new CambioContrasenia.listener());

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

        firebaseAuth = firebaseAuth.getInstance();
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
                if (listaCorreosRegistrados.contains(correo.getEditText().getText().toString())) {
                    vecesCorreo++;
                    correo.setErrorEnabled(false);
                } else {
                    vecesCorreo++;
                    correo.setError("El correo ingresado no ha sido registrado");
                    correoValido = false;
                }
            }
        } else {
            //Texto NO ha sido ingresado en el edittext
            correo.setError("Ingrese un correo");
            vecesCorreo++;
            correoValido = false;
        }

        if (correoValido) {
            //Crear codigo y mandar correo
//            String codigo = generarCodigo(6);
//            SendEmailViewModel sendEmailViewModel = new ViewModelProvider(this).get(SendEmailViewModel.class);
//            sendEmailViewModel.enviarCorreo(correo.getEditText().getText().toString(), codigo);

//            Intent intent = new Intent(this, CodigoCambioContrasenia.class);
//            intent.putExtra("codigo", codigo);
//            startActivity(intent);

            firebaseAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        firebaseAuth.sendPasswordResetEmail(correo.getEditText().getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Esconder edittext y cambiar texto
                                Log.d("forgetpsw", "Correo enviado para cambio de contrasenia");
                                Intent intent = new Intent(CambioContrasenia.this, InicioSesion.class);
                                intent.putExtra("exito", "Se le ha enviado un correo para proceder con la solicitud de cambio de contraseña");
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("forgetpsw", "Ourrio un error - " + e.getMessage());
                            }
                        });
                    } else {
                        Snackbar.make(findViewById(R.id.activity_cambiar_contrasenia), "Su cuenta no ha sido verificada. Verifíquela para poder ingresar", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                listaCorreosRegistrados.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Usuario usuario = ds.getValue(Usuario.class);
                    listaCorreosRegistrados.add(usuario.getCorreo());
                }
            } else {
                listaCorreosRegistrados.clear();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }

    @Override
    protected void onDestroy() {
        databaseReferenceCorreos.removeEventListener(valueEventListener);
        super.onDestroy();
    }
}