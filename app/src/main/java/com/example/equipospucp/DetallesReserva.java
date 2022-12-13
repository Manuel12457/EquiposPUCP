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
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.Reserva;
import com.example.equipospucp.DTOs.ReservaDto;
import com.example.equipospucp.DTOs.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DetallesReserva extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("reservas");
    FloatingActionButton rechazarReserva;
    FloatingActionButton aceptarReserva;
    Usuario usuario;
    String idReserva;
    RecyclerView recyclerView;
    List<ReservaDto> listaReserva = new ArrayList<>();

    boolean reservaNoPendiente;
    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_reserva);
        getSupportActionBar().setTitle("Detalles de la reserva");
        //Inicializamos los widgets
        rechazarReserva = findViewById(R.id.rechazarReserva);
        aceptarReserva = findViewById(R.id.aceptarReserva);
        //Obtenemos la key de la reserva el rol del usuario
        Intent intent = getIntent();
        idReserva = intent.getStringExtra("id");
        getDetalles();
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

                                if (reservaNoPendiente) {
                                    rechazarReserva.setVisibility(View.GONE);
                                    aceptarReserva.setVisibility(View.GONE);
                                } else {
                                    rechazarReserva.setVisibility(View.VISIBLE);
                                    rechazarReserva.setOnClickListener(view -> {
                                        //Cancelamos la reserva
                                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DetallesReserva.this);
                                        builder.setMessage("¿Seguro que desea cancelar esta reserva?");
                                        builder.setPositiveButton("Aceptar",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Logica de aceptar
                                                        //FECHA CONSULTA
                                                        Calendar calLimite = Calendar.getInstance();
                                                        try {
                                                            calLimite.setTime(sf.parse(listaReserva.get(0).getReserva().getFechayhora()));
                                                            calLimite.add(Calendar.DATE, 7);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        //FECHA CONSULTA

                                                        //FECHA ACTUAL
                                                        Date currentTime = Calendar.getInstance().getTime();
                                                        Calendar calCurrentTime = Calendar.getInstance();
                                                        try {
                                                            calCurrentTime.setTime(sf.parse(sf.format(currentTime)));
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        //FECHA FILTRO
                                                        Log.d("prueba", "TIEMPO LIMITE: " + calLimite.getTime());
                                                        Log.d("prueba", "TIEMPO CURRENT: " + calCurrentTime.getTime());
                                                        if (calLimite.getTime().after(calCurrentTime.getTime())) {
                                                            FirebaseDatabase.getInstance().getReference().child("reservas").child(idReserva)
                                                                    .child("estado").setValue("CANCELADA")
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {
                                                                            FirebaseDatabase.getInstance().getReference().child("dispositivos").child(listaReserva.get(0).getReserva().getIddispositivo())
                                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                        @Override
                                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                            if (snapshot.exists()) {
                                                                                                Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                                                                                                FirebaseDatabase.getInstance().getReference().child("dispositivos").child(snapshot.getKey())
                                                                                                        .child("stock").setValue(dispositivo.getStock() + 1)
                                                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onSuccess(Void unused) {
                                                                                                                rechazarReserva.setVisibility(View.GONE);
                                                                                                                Snackbar.make(findViewById(R.id.activity_detalles_reserva), "Reserva cancelada exitosamente", Snackbar.LENGTH_LONG).show();
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }

                                                                                        @Override
                                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                                        }
                                                                                    });
                                                                            //SUMAR 1 AL DISPOSITIVO
                                                                        }
                                                                    });
                                                        } else {
                                                            Snackbar.make(findViewById(R.id.activity_detalles_reserva), "Ha pasado la fecha límite de cancelación", Snackbar.LENGTH_LONG).show();
                                                        }
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
                                }
                            } else {

                                if (reservaNoPendiente) {
                                    rechazarReserva.setVisibility(View.GONE);
                                    aceptarReserva.setVisibility(View.GONE);
                                } else {
                                    rechazarReserva.setVisibility(View.VISIBLE);
                                    aceptarReserva.setVisibility(View.VISIBLE);
                                    rechazarReserva.setOnClickListener(view -> {
                                        //Denegamos la reserva
                                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(DetallesReserva.this);
                                        builder.setMessage("¿Seguro que desea rechazar la solicitud?");
                                        builder.setPositiveButton("Aceptar",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        //Logica de aceptar
                                                        FirebaseDatabase.getInstance().getReference().child("reservas").child(idReserva)
                                                                .child("estado").setValue("RECHAZADA")
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        FirebaseDatabase.getInstance().getReference().child("dispositivos").child(listaReserva.get(0).getReserva().getIddispositivo())
                                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        if (snapshot.exists()) {
                                                                                            Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                                                                                            FirebaseDatabase.getInstance().getReference().child("dispositivos").child(snapshot.getKey())
                                                                                                    .child("stock").setValue(dispositivo.getStock() + 1)
                                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                            rechazarReserva.setVisibility(View.GONE);
                                                                                                            aceptarReserva.setVisibility(View.GONE);
                                                                                                            Snackbar.make(findViewById(R.id.activity_detalles_reserva), "Reserva rechazada exitosamente", Snackbar.LENGTH_LONG).show();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });
                                                                        //SUMAR 1 AL DISPOSITIVO
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
                                    });
                                    aceptarReserva.setOnClickListener(view -> {
                                        new MaterialAlertDialogBuilder(view.getContext())
                                                .setTitle("Aceptar Reserva")
                                                .setMessage("¿Estas seguro de querer aceptar esta solicitud de reserva?")
                                                .setNegativeButton("Cancelar", ((dialogInterface, i) -> {
                                                    dialogInterface.cancel();
                                                })).setPositiveButton("Aceptar", ((dialogInterface, i) -> {
                                                    Log.d("filtro", "POSIBLE ACEPTAR LA RESERVA");
                                                    startActivity(new Intent(DetallesReserva.this, ElegirUbicacionReserva.class)
                                                            .putExtra("idReserva", idReserva));
                                                    dialogInterface.dismiss();
                                                    finish();
                                                })).show();
                                    });
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });
    }

    public void getDetalles() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaReserva.clear();
                for (DataSnapshot children : snapshot.getChildren()) {
                    if (children.getKey().equals(idReserva)) {
                        Reserva reserva = children.getValue(Reserva.class);
                        ReservaDto reservaDto = new ReservaDto();
                        reservaDto.setReserva(reserva);
                        reservaDto.setId(children.getKey());
                        listaReserva.add(reservaDto);
                        if (!reserva.getEstado().equals("PENDIENTE")) {
                            reservaNoPendiente = true;
                        }
                        break;
                    }
                }
                recyclerView = findViewById(R.id.recyclerView_reservadetalle);
                ReservaDetalleAdapter adapter = new ReservaDetalleAdapter(DetallesReserva.this, listaReserva);
                recyclerView.setLayoutManager(new LinearLayoutManager(DetallesReserva.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}