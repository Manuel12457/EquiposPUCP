package com.example.equipospucp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.Adapters.ListaDispositivoAdapter;
import com.example.equipospucp.DTOs.DispositivoDto;
import com.example.equipospucp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DispositivosPorTipoFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ListaDispositivoAdapter adapter;
    ValueEventListener valueEventListener;
    Query query;
    ArrayList<DispositivoDto> listaDispositivos;
    String tipo;
    ArrayList<String> marcas;
    String marca;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivosportipo_fragment,container,false);
        Bundle bundle = this.getArguments();
        tipo = bundle.getString("tipo");
        marcas = bundle.getStringArrayList("marcas");

        recyclerView = view.findViewById(R.id.recyclerView_dispositivos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaDispositivos = new ArrayList<>();
        adapter = new ListaDispositivoAdapter(listaDispositivos, getContext());
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");
        valueEventListener = databaseReference.addValueEventListener(new DispositivosPorTipoFragment.listener());

        AutoCompleteTextView spinner = view.findViewById(R.id.idMarca);
        ArrayAdapter<String> adapterarray = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, marcas.toArray(new String[0]));
        spinner.setAdapter(adapterarray);
        spinner.setText(adapterarray.getItem(0),false);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextInputLayout spinnera = view.findViewById(R.id.spinner_marca);
                marca = spinner.getText().toString();

                if (marca.equals("Todas las marcas")) {
                    databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");
                    valueEventListener = databaseReference.addValueEventListener(new DispositivosPorTipoFragment.listener());
                } else {
                    query = FirebaseDatabase.getInstance().getReference("dispositivos").orderByChild("marca").equalTo(marca);
                    query.addValueEventListener(new DispositivosPorTipoFragment.listener());
                }
            }
        });

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
