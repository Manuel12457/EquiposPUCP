package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.equipospucp.Adapters.DispositivoDetalleAdapter;
import com.example.equipospucp.Adapters.ListaTipoDispositivoAdapter;
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.DispositivoDto;
import com.example.equipospucp.Fragments.DispositivosFragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_dispositivo);

        id = getIntent().getStringExtra("id");
        getSupportActionBar().setTitle(getIntent().getStringExtra("tipo"));
        TextView t = findViewById(R.id.textView_tipodetalledispositivo);
        t.setText(getIntent().getStringExtra("tipo"));

        databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");

        recyclerView = findViewById(R.id.recyclerView_dispositivosdetalle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaDispositivos = new ArrayList<>();
        adapter = new DispositivoDetalleAdapter(listaDispositivos, this);
        recyclerView.setAdapter(adapter);

        valueEventListener = databaseReference.addValueEventListener(new listener());
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaDispositivos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.getKey().equals(id)) {
                        DispositivoDto dispositivo = ds.getValue(DispositivoDto.class);
                        DispositivoDetalleDto dispositivoDetalle = new DispositivoDetalleDto();
                        dispositivoDetalle.setDispositivoDto(dispositivo);
                        dispositivoDetalle.setId(ds.getKey());
                        listaDispositivos.add(dispositivoDetalle);
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