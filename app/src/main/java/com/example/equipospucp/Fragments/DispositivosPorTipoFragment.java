package com.example.equipospucp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.Adapters.ListaDispositivoAdapter;
import com.example.equipospucp.Adapters.ListaTipoDispositivoAdapter;
import com.example.equipospucp.DTOs.DispositivoDto;
import com.example.equipospucp.DTOs.TipoDispositivoDto;
import com.example.equipospucp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DispositivosPorTipoFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ListaDispositivoAdapter adapter;
    ValueEventListener valueEventListener;
    ArrayList<DispositivoDto> listaDispositivos;
    String tipo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivosportipo_fragment,container,false);
        Bundle bundle = this.getArguments();
        tipo = bundle.getString("tipo");
        System.out.println("He llegado: " + tipo);

        databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");

        recyclerView = view.findViewById(R.id.recyclerView_dispositivos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaDispositivos = new ArrayList<>();
        adapter = new ListaDispositivoAdapter(listaDispositivos, getContext());
        recyclerView.setAdapter(adapter);

        valueEventListener = databaseReference.addValueEventListener(new DispositivosPorTipoFragment.listener());

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaDispositivos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    DispositivoDto dispositivo = ds.getValue(DispositivoDto.class);
                    if (dispositivo.getTipo().equals(tipo)) {
                        listaDispositivos.add(dispositivo);
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
