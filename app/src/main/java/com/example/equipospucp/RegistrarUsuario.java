package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.equipospucp.DTOs.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegistrarUsuario extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceCorreos;
    ValueEventListener valueEventListener;
    ArrayList<String> listaCorreosRegistrados = new ArrayList<>();

    Button btn;

    boolean codigoValido = true;
    boolean correoValido = true;
    boolean rolValido = true;
    boolean passwordValido = true;
    boolean verifyPasswordValido = true;

    int vecesCodigo = 0;
    int vecesCorreo = 0;
    int vecesPassword = 0;

    String rol = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        getSupportActionBar().setTitle("Registro de usuario");

        btn = findViewById(R.id.btn_registrarseForm);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceCorreos = firebaseDatabase.getReference("usuarios");
        valueEventListener = databaseReferenceCorreos.addValueEventListener(new listener());

        TextInputLayout codigo = findViewById(R.id.inputCodigo_registro);
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
                        if (!longitudCodigo) {
                            codigo.setError("La longitud del código debe ser de 8 caracteres");
                            codigoValido = false;
                        } else if (!codigoEsEntero) {
                            codigo.setError("El código debe contener únicamente números");
                            codigoValido = false;
                        } else {
                            codigo.setErrorEnabled(false);
                            codigoValido = true;
                        }
                    } else {
                        codigo.setError("Ingrese su código");
                        codigoValido = false;
                    }
                }

                if (!codigo.isErrorEnabled() && vecesCodigo != 0) {
                    if ((codigo.getEditText().getText().toString() != null && !codigo.getEditText().getText().toString().equals(""))) {
                        if (!longitudCodigo) {
                            codigo.setError("La longitud del código debe ser de 8 caracteres");
                            codigoValido = false;
                        } else if (!codigoEsEntero) {
                            codigo.setError("El código debe contener únicamente números");
                            codigoValido = false;
                        } else {
                            codigo.setErrorEnabled(false);
                            codigoValido = true;
                        }
                    } else {
                        codigo.setError("Ingrese su código");
                        codigoValido = false;
                    }
                }
            }
        });

        TextInputLayout correo = findViewById(R.id.inputCorreo_registro);
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

        TextInputLayout password = findViewById(R.id.inputPassword_registro);
        password.setErrorIconDrawable(null);
        password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                boolean longitudContrasenia = password.getEditText().getText().toString().length() >= 6;
                if (password.isErrorEnabled()) {
                    if ((password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals(""))) {
                        if (!longitudContrasenia) {
                            password.setError("La longitud del código debe ser de mínimo 6 caracteres");
                            passwordValido = false;
                        } else {
                            password.setErrorEnabled(false);
                            passwordValido = true;
                        }
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

        TextInputLayout verifyPassword = findViewById(R.id.inputVerifyPassword_registro);
        verifyPassword.setErrorIconDrawable(null);
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

        AutoCompleteTextView spinner = findViewById(R.id.idRol);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextInputLayout spinnera = findViewById(R.id.spinner_rol);
                spinnera.setErrorEnabled(false);

                rol = spinner.getText().toString();
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

    public void validarRegistro(View view) {

        TextInputLayout codigo = findViewById(R.id.inputCodigo_registro);
        TextInputLayout correo = findViewById(R.id.inputCorreo_registro);
        TextInputLayout password = findViewById(R.id.inputPassword_registro);
        TextInputLayout verifyPassword = findViewById(R.id.inputVerifyPassword_registro);

        boolean codigoEsEntero = true;
        try {
            int codigoInt = Integer.parseInt(codigo.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            codigoEsEntero = false;
            e.printStackTrace();
        }
        if (codigo.getEditText().getText().toString() == null || codigo.getEditText().getText().toString().equals("")) {
            //Texto no ha sido ingresado en el edittext
            vecesCodigo++;
            codigo.setError("Ingrese su código");
            codigoValido = false;
        } else if (codigo.getEditText().getText().toString().length() != 8) {
            vecesCodigo++;
            codigo.setError("La longitud del código debe ser de 8 caracteres");
            codigoValido = false;
        } else if (!codigoEsEntero) {
            vecesCodigo++;
            codigo.setError("El código debe contener únicamente números");
            codigoValido = false;
        }

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z.]+";
        boolean correoValido = true;
        if (correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            if (!correo.getEditText().getText().toString().matches(emailPattern)) {
                //Texto ingresado NO cumple con el patron de un correo electronico
                vecesCorreo++;
                correo.setError("Ingrese un correo válido");
                correoValido = false;
            } else {
                //Validar si usuario existe en el sistema
                if (!listaCorreosRegistrados.contains(correo.getEditText().getText().toString())) {
                    vecesCorreo++;
                    correo.setErrorEnabled(false);
                } else {
                    vecesCorreo++;
                    correo.setError("El correo ingresado ya ha sido registrado");
                    correoValido = false;
                }
            }
        } else {
            //Texto NO ha sido ingresado en el edittext
            vecesCorreo++;
            correo.setError("Ingrese un correo");
            correoValido = false;
        }

        boolean passwordValido = true;
        if (password.getEditText().getText().toString() != null && !password.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            vecesPassword++;
            password.setErrorEnabled(false);
        } else {
            vecesPassword++;
            password.setError("Ingrese una contraseña");
            passwordValido = false;
        }

        if (verifyPassword.getEditText().getText().toString() != null && !verifyPassword.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            verifyPassword.setErrorEnabled(false);
        } else {
            verifyPassword.setError("Debe verificar su contraseña");
            verifyPasswordValido = false;
        }

        rolValido = (rol != null && !rol.equals("")) ? true : false;
        if (!rolValido) {
            TextInputLayout spinner = findViewById(R.id.spinner_rol);
            spinner.setError("Seleccione un rol");
        }

        if (codigoValido && correoValido && passwordValido && verifyPasswordValido && rolValido) {
            Log.d("task", "Registro valido");

            firebaseAuth.createUserWithEmailAndPassword(correo.getEditText().getText().toString(), password.getEditText().getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("task", "EXITO EN REGISTRO");

                        //Guardar usuario en db
                        DatabaseReference databaseReference = firebaseDatabase.getReference().child("usuarios").child(firebaseAuth.getCurrentUser().getUid());
                        Usuario usuario = new Usuario();
                        usuario.setCodigo(codigo.getEditText().getText().toString());
                        usuario.setRol(rol);
                        usuario.setCorreo(correo.getEditText().getText().toString());
                        usuario.setFoto("");
                        usuario.setEstado(true);
                        databaseReference.setValue(usuario)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("registro", "USUARIO GUARDADO");
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("registro", "USUARIO NO GUARDADO - " + e.getMessage());
                                    }
                                });

                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("task", "EXITO EN ENVIO DE CORREO DE VERIFICACION");
                                Intent intent = new Intent(RegistrarUsuario.this, InicioSesion.class);
                                intent.putExtra("exito", "Se ha enviado un correo para la verificación de su cuenta");
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("task", "ERROR EN ENVIO DE CORREO DE VERIFICACION - " + e.getMessage());
                            }
                        });

                    } else {
                        Log.d("task", "ERROR EN REGISTRO - " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegistrarUsuario.this);
                builder.setMessage("¿Seguro que desea regresar a la pantalla de inicio? Perderá los datos ingresados");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(RegistrarUsuario.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(RegistrarUsuario.this);
        builder.setMessage("¿Seguro que desea regresar a la pantalla de inicio? Perderá los datos ingresados");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(RegistrarUsuario.this,MainActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
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