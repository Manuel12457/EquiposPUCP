package com.example.equipospucp.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.equipospucp.Adapters.EstadisticasAdapter;
import com.example.equipospucp.Adapters.ListaUsuariosAdapter;
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.Reserva;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.DTOs.UsuarioDto;
import com.example.equipospucp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class EstadisticasFragment extends Fragment {

    DatabaseReference databaseReference;
    EstadisticasAdapter adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;

    TextView cantidadTotalEquiposPrestados;
    TextView equipoMasPrestado;
    ArrayList<String> listaCantidadPrestamosPorMarca = new ArrayList<>();
    LinkedHashMap<String, Integer> marcaYCantidad = new LinkedHashMap<>();
    LinkedHashMap<String, Integer> marcaDispositivoYCantidad = new LinkedHashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("reservas");

        recyclerView = view.findViewById(R.id.recyclerViewCantidadPrestamos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaCantidadPrestamosPorMarca = new ArrayList<>();
        adapter = new EstadisticasAdapter(listaCantidadPrestamosPorMarca, getContext());
        recyclerView.setAdapter(adapter);

        valueEventListener = databaseReference.addValueEventListener(new EstadisticasFragment.listener());

        cantidadTotalEquiposPrestados = view.findViewById(R.id.idCantidadEquiposPrestadosEstadisticas);
        equipoMasPrestado = view.findViewById(R.id.idEquipoMasPrestadoReserva);

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaCantidadPrestamosPorMarca.clear();
                marcaYCantidad.clear();
                marcaDispositivoYCantidad.clear();

                int cantidadEquiposPrestados = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Reserva reserva = ds.getValue(Reserva.class);
                    if (reserva.getEstado().equals("PENDIENTE") || reserva.getEstado().equals("APROBADO")) {
                        cantidadEquiposPrestados++;
                        FirebaseDatabase.getInstance().getReference("dispositivos").child(reserva.getIddispositivo())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                                            String marca = dispositivo.getMarca();
                                            String tipo = dispositivo.getTipo();

                                            if (marcaYCantidad.isEmpty()) {
                                                marcaYCantidad.put(marca,1);
                                                marcaDispositivoYCantidad.put(tipo+" "+marca,1);
                                            } else {
                                                for (Map.Entry<String, Integer> entry : marcaYCantidad.entrySet()) {
                                                    if (entry.getKey().equals(marca)) {
                                                        marcaYCantidad.put(entry.getKey(), entry.getValue()+1);
                                                    } else {
                                                        marcaYCantidad.put(marca, entry.getValue()+1);
                                                    }
                                                }
                                                for (Map.Entry<String, Integer> entry : marcaDispositivoYCantidad.entrySet()) {
                                                    if (entry.getKey().equals(tipo+" "+marca)) {
                                                        marcaDispositivoYCantidad.put(entry.getKey(), entry.getValue()+1);
                                                    } else {
                                                        marcaDispositivoYCantidad.put(tipo+" "+marca, entry.getValue()+1);
                                                    }
                                                }
                                            }

                                            listaCantidadPrestamosPorMarca.clear();
                                            for (Map.Entry<String, Integer> entry : marcaYCantidad.entrySet()) {
                                                listaCantidadPrestamosPorMarca.add(entry.getKey()+"\n"+entry.getValue());
                                            }

                                            String equipoMasPrestadoStr = "";
                                            Integer cantidadEquipoMasPrestado = 0;
                                            for (Map.Entry<String, Integer> entry : marcaDispositivoYCantidad.entrySet()) {
                                                if (entry.getValue() > cantidadEquipoMasPrestado) {
                                                    equipoMasPrestadoStr = entry.getKey();
                                                    cantidadEquipoMasPrestado = entry.getValue();
                                                }
                                            }

                                            equipoMasPrestado.setText(equipoMasPrestadoStr);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                }
                cantidadTotalEquiposPrestados.setText(String.valueOf(cantidadEquiposPrestados));
                adapter.notifyDataSetChanged();
            } else {
                listaCantidadPrestamosPorMarca.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }
}