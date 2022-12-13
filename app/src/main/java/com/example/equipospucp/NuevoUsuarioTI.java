package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.config.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NuevoUsuarioTI extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceCorreos;
    ValueEventListener valueEventListener;
    ArrayList<String> listaCorreosRegistrados = new ArrayList<>();
    Helper helper;

    FloatingActionButton fab;
    CircularProgressIndicator circularProgressIndicator;

    String correoAdmin;
    String passwordAdmin;

    boolean codigoValido = true;
    boolean correoValido = true;
    int vecesCodigo = 0;
    int vecesCorreo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_usuario_ti);
        getSupportActionBar().setTitle("Nuevo usuario TI");

        correoAdmin = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        passwordAdmin = "123456";

        fab = findViewById(R.id.fav_validarRegistroUsuarioTI);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceCorreos = firebaseDatabase.getReference("usuarios");
        valueEventListener = databaseReferenceCorreos.addValueEventListener(new NuevoUsuarioTI.listener());

        TextInputLayout codigo = findViewById(R.id.inputCodigo_registroUsuarioTI);
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
                        codigo.setError("Ingrese un código");
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
                        codigo.setError("Ingrese un código");
                        codigoValido = false;
                    }
                }
            }
        });

        TextInputLayout correo = findViewById(R.id.inputCorreo_registroUsuarioTI);
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
                        correo.setError("Ingrese un correo");
                        correoValido = false;
                    }
                }

                if (!correo.isErrorEnabled() && vecesCorreo != 0) {
                    if ((correo.getEditText().getText().toString() != null && !correo.getEditText().getText().toString().equals(""))) {
                        correo.setErrorEnabled(false);
                        correoValido = true;
                    } else {
                        correo.setError("Ingrese un correo");
                        correoValido = false;
                    }
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarRegistro(view);
            }
        });

    }

    public void validarRegistro(View view) {

        TextInputLayout codigo = findViewById(R.id.inputCodigo_registroUsuarioTI);
        TextInputLayout correo = findViewById(R.id.inputCorreo_registroUsuarioTI);

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
            codigo.setError("Ingrese un código");
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

        if (codigoValido && correoValido) {
            Log.d("task", "Registro valido");

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo.getEditText().getText().toString(), "123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("task", "EXITO EN REGISTRO");

                        //Guardar usuario en db
                        DatabaseReference databaseReference = firebaseDatabase.getReference().child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Usuario usuario = new Usuario();
                        usuario.setCodigo(codigo.getEditText().getText().toString());
                        usuario.setRol("Usuario TI");
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

                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                FirebaseAuth.getInstance().signOut();
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(correoAdmin, passwordAdmin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("task", "EXITO EN REGISTRO");
                                            Log.d("task", "EXITO EN ENVIO DE CORREO DE VERIFICACION");
                                            RequestQueue queue = Volley.newRequestQueue(NuevoUsuarioTI.this);
                                            String url = "http://ec2-52-207-211-253.compute-1.amazonaws.com/api/enviarCorreo";
                                            JSONObject jsonObject = new JSONObject();
                                            try {
                                                System.out.println(correo.getEditText().getText().toString());
                                                jsonObject.put("to",correo.getEditText().getText().toString());
                                                jsonObject.put("subject","Bienvenido a la plataforma de equipos PUCP");
                                                //jsonObject.put("body","prueba");
                                                jsonObject.put("body","Usted ha sido registrado con las siguientes credenciales\n"+"correo: "+correo.getEditText().getText().toString()+"\n"+"contraseña: 123456\n"+"Por favor ingrese a su carpeta span y valide su cuenta!");
                                            }catch (JSONException e){
                                                System.out.println(e);
                                            }
                                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    Toast.makeText(NuevoUsuarioTI.this,"Peticion exitosa",Toast.LENGTH_SHORT).show();
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    Toast.makeText(NuevoUsuarioTI.this,"Peticion fallida",Toast.LENGTH_SHORT).show();
                                                    System.out.println(error.getMessage());
                                                }
                                            });
                                            queue.add(jsonObjectRequest);
                                            Intent intent = new Intent(NuevoUsuarioTI.this, Drawer.class);
                                            intent.putExtra("exito", "Se ha enviado un correo para la verificación de la cuenta del nuevo usuario TI");
                                            intent.putExtra("accion", "Lista usuarios TI");
                                            startActivity(intent);
                                        } else {
                                            Log.d("task", "ERROR EN REGISTRO - " + task.getException().getMessage());
                                            //Ver bien mensaje de error
                                            Snackbar.make(findViewById(R.id.activity_iniciar_sesion), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                });
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
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NuevoUsuarioTI.this);
        builder.setMessage("¿Volver a la pantalla anterior? No se creará el usuario TI");

        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NuevoUsuarioTI.this,Drawer.class);
                        intent.putExtra("accion", "Lista usuarios TI");
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

    @Override
    protected void onDestroy() {
        databaseReferenceCorreos.removeEventListener(valueEventListener);
        super.onDestroy();
    }

}