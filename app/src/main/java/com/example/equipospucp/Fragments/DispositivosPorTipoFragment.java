package com.example.equipospucp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.Adapters.ListaDispositivoAdapter;
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Dispositivo;
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

    DatabaseReference databaseReference;
    Query query;
    ListaDispositivoAdapter adapter;
    ValueEventListener valueEventListener;

    ArrayList<DispositivoDetalleDto> listaDispositivos;
    String tipo;
    ArrayList<String> marcas;
    String marca;

    RecyclerView recyclerView;
    TextInputLayout spinnera;
    TextView noregistro;

    Boolean pestaniaOtros;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivosportipo_fragment,container,false);
        Bundle bundle = this.getArguments();
        tipo = bundle.getString("tipo");
        marcas = bundle.getStringArrayList("marcas");

        pestaniaOtros = !(tipo.equals("Laptop") || tipo.equals("Monitor") || tipo.equals("Celular") || tipo.equals("Tablet") || tipo.equals("Laptop"));

        noregistro = view.findViewById(R.id.textView7);
        spinnera = view.findViewById(R.id.spinner_marca);
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
                    Dispositivo dispositivo = ds.getValue(Dispositivo.class);
                    DispositivoDetalleDto dispositivoDetalle = new DispositivoDetalleDto();
                    dispositivoDetalle.setDispositivoDto(dispositivo);
                    dispositivoDetalle.setId(ds.getKey());

                    if (pestaniaOtros) {
                        if (!dispositivo.getTipo().equals("Laptop") && !dispositivo.getTipo().equals("Monitor") && !dispositivo.getTipo().equals("Celular") && !dispositivo.getTipo().equals("Tablet") && dispositivo.getVisible()) {
                            listaDispositivos.add(dispositivoDetalle);
                        }
                    } else {
                        if (dispositivo.getTipo().equals(tipo) && dispositivo.getVisible()) {
                            listaDispositivos.add(dispositivoDetalle);
                        }
                    }
                }

                if (listaDispositivos.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    spinnera.setVisibility(View.INVISIBLE);
                    noregistro.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    spinnera.setVisibility(View.VISIBLE);
                    noregistro.setVisibility(View.INVISIBLE);
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
