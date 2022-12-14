package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.equipospucp.Adapters.DispositivoDetalleAdapter;
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.Fragments.ReservasFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetallesDispositivo extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DispositivoDetalleAdapter adapter;
    ValueEventListener valueEventListener;

    ArrayList<DispositivoDetalleDto> listaDispositivos;
    String id;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_dispositivo);

        FloatingActionButton editar = findViewById(R.id.editarDispositivo);
        FloatingActionButton eliminar = findViewById(R.id.eliminarDispositivo);
        FloatingActionButton reservas = findViewById(R.id.reservar);
        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            usuario = snapshot.getValue(Usuario.class);
                            if (usuario.getRol().equals("Usuario TI")) {
                                editar.setVisibility(View.VISIBLE);
                                eliminar.setVisibility(View.VISIBLE);
                                reservas.setVisibility(View.GONE);
                            } else {
                                editar.setVisibility(View.GONE);
                                eliminar.setVisibility(View.GONE);
                                reservas.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });

        id = getIntent().getStringExtra("id");
        getSupportActionBar().setTitle(getIntent().getStringExtra("tipo"));

        databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");

        recyclerView = findViewById(R.id.recyclerView_dispositivosdetalle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaDispositivos = new ArrayList<>();
        adapter = new DispositivoDetalleAdapter(listaDispositivos, this);
        recyclerView.setAdapter(adapter);

        valueEventListener = databaseReference.addValueEventListener(new listener());

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("tamanio", String.valueOf(listaDispositivos.size()));
                Intent intent = new Intent(DetallesDispositivo.this, EditarDispositivo.class);
                intent.putExtra("accion", "editar");
                intent.putExtra("dispositivo", listaDispositivos.get(0));
                startActivity(intent);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DetallesDispositivo.this);
                builder.setMessage("??Seguro que quiere eliminar el dispositivo?");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Dispositivo dispositivo = new Dispositivo();

                                dispositivo.setTipo(listaDispositivos.get(0).getDispositivoDto().getTipo());
                                dispositivo.setFoto("");
                                dispositivo.setMarca(listaDispositivos.get(0).getDispositivoDto().getMarca());
                                dispositivo.setCaracteristicas(listaDispositivos.get(0).getDispositivoDto().getCaracteristicas());
                                dispositivo.setIncluye(listaDispositivos.get(0).getDispositivoDto().getIncluye());
                                dispositivo.setStock(listaDispositivos.get(0).getDispositivoDto().getStock());
                                dispositivo.setVisible(false);

                                FirebaseDatabase.getInstance().getReference().child("dispositivos").child(listaDispositivos.get(0).getId())
                                        .child("visible").setValue(false)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("registro", "DISPOSITIVO GUARDADO");
                                                Intent intent = new Intent(DetallesDispositivo.this,Drawer.class);
                                                intent.putExtra("exito", "El dispositivo se ha eliminado exitosamente");
                                                startActivity(intent);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("registro", "DISPOSITIVO NO GUARDADO - " + e.getMessage());
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

        reservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetallesDispositivo.this, NuevaReserva.class);
                intent.putExtra("dispositivo", listaDispositivos.get(0));
                startActivity(intent);
            }
        });

    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaDispositivos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(id)) {
                        Dispositivo dispositivo = ds.getValue(Dispositivo.class);
                        DispositivoDetalleDto dispositivoDetalle = new DispositivoDetalleDto();
                        dispositivoDetalle.setDispositivoDto(dispositivo);
                        dispositivoDetalle.setId(ds.getKey());
                        if (dispositivo.getVisible()) {
                            listaDispositivos.add(dispositivoDetalle);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            } else {
                listaDispositivos.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }
}