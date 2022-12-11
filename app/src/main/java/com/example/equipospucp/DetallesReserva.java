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
import android.widget.Button;
import android.widget.Toast;

import com.example.equipospucp.Adapters.ReservaDetalleAdapter;
import com.example.equipospucp.DTOs.Reserva;
import com.example.equipospucp.DTOs.ReservaDto;
import com.example.equipospucp.DTOs.Usuario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DetallesReserva extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("reservas");
    FloatingActionButton rechazarReserva;
    FloatingActionButton aceptarReserva;
    Usuario usuario;
    String idReserva;
    RecyclerView recyclerView;
    List<ReservaDto> listaReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_reserva);
        //Inicializamos los widgets
        rechazarReserva = findViewById(R.id.rechazarReserva);
        aceptarReserva = findViewById(R.id.aceptarReserva);
        //Obtenemos la key de la reserva el rol del usuario
        Intent intent = getIntent();
        idReserva = intent.getStringExtra("id");
        //Escondemos a partir del rol
        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            usuario = snapshot.getValue(Usuario.class);
                            //Funcionalidad de los botones
                            if (!usuario.getRol().equals("Usuario TI")) {
                                aceptarReserva.setVisibility(View.GONE);
                                rechazarReserva.setOnClickListener(view -> {
                                    //Cancelamos la reserva
                                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DetallesReserva.this);
                                    builder.setMessage("¿Seguro que desea cancelar esta reserva");
                                    builder.setPositiveButton("Aceptar",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //Logica de aceptar
                                                    LocalDateTime now = LocalDateTime.now();
                                                    databaseReference.child(idReserva).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            Reserva reserva = snapshot.getValue(Reserva.class);
                                                            LocalDateTime reservaTime = LocalDateTime.parse(reserva.getFechayhora());
                                                            if(reservaTime.compareTo(now) > 0){
                                                                databaseReference.child(idReserva).child("estado").setValue("CANCELADO");
                                                                Toast.makeText(DetallesReserva.this,"Reserva cancelada",Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DetallesReserva.this);
                                                                builder.setMessage("Su reserva ha expirado. No puede ser cancelada");
                                                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        getDetalles();
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {}
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
                                });
                            }else{
                                rechazarReserva.setOnClickListener(view -> {
                                    //Denegamos la reserva
                                });
                                aceptarReserva.setOnClickListener(view -> {
                                    //Aceptamos la reserva
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });
        //Obtenemos los datos de firebasedatabase
        getDetalles();


    }

    public void getDetalles(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaReserva.clear();
                for (DataSnapshot children : snapshot.getChildren()){
                    if (children.getKey().equals(idReserva)){
                        Reserva reserva = children.getValue(Reserva.class);
                        ReservaDto reservaDto = new ReservaDto();
                        reservaDto.setReserva(reserva);
                        reservaDto.setId(children.getKey());
                        listaReserva.add(reservaDto);
                        break;
                    }
                }
                recyclerView = findViewById(R.id.recyclerView_reservadetalle);
                ReservaDetalleAdapter adapter = new ReservaDetalleAdapter(DetallesReserva.this,listaReserva);
                recyclerView.setLayoutManager(new LinearLayoutManager(DetallesReserva.this));
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}