package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.DTOs.UsuarioDto;
import com.example.equipospucp.Fragments.DispositivosFragment;
import com.example.equipospucp.Fragments.UsuariosFragment;
import com.example.equipospucp.Fragments.UsuariosTIFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetalleUsuario extends AppCompatActivity {

    Usuario usuario;
    UsuarioDto usuarioDto;
    String id;
    TextView textview_codigo;
    TextView textview_rol;
    TextView textview_correo;
    FloatingActionButton eliminarUsuarioTI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_usuario);

        id = getIntent().getStringExtra("id");
        getSupportActionBar().setTitle("Usuario");

        textview_codigo = findViewById(R.id.textview_codigo);
        textview_rol = findViewById(R.id.textview_rol);
        textview_correo = findViewById(R.id.textview_correo);
        eliminarUsuarioTI = findViewById(R.id.floatingActionButton);

        FirebaseDatabase.getInstance().getReference("usuarios").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            usuario = snapshot.getValue(Usuario.class);

                            //CAMBIAR FOTO

                            textview_codigo.setText(usuario.getCodigo());
                            textview_rol.setText(usuario.getRol());
                            textview_correo.setText("Correo con el cual se ha registrado\n" + usuario.getCorreo());

                            if (usuario.getRol().equals("Usuario TI")) {
                                eliminarUsuarioTI.setVisibility(View.VISIBLE);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });

        eliminarUsuarioTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DetalleUsuario.this);
                builder.setMessage("¿Seguro que desea eliminar a este usuario TI?");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseDatabase.getInstance().getReference().child("usuarios").child(id)
                                        .child("estado").setValue(false)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent intent = new Intent(DetalleUsuario.this,Drawer.class);
                                                intent.putExtra("exito", "El usuario TI se ha eliminado exitosamente");
                                                intent.putExtra("accion", "Lista usuarios TI");
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("error", "Ocurrio un error - " + e.getMessage());
                                            }
                                        });
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
        });
    }
}