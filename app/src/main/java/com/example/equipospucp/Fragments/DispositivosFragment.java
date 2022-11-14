package com.example.equipospucp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class DispositivosFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ListaTipoDispositivoAdapter adapter;
    ValueEventListener valueEventListener;

    ArrayList<TipoDispositivoDto> listaTipoDispositivos;
    ArrayList<String> listaMarcasLaptop = new ArrayList<>();
    ArrayList<String> listaMarcasMonitor = new ArrayList<>();
    ArrayList<String> listaMarcasCelular = new ArrayList<>();
    ArrayList<String> listaMarcasTablet = new ArrayList<>();
    ArrayList<String> listaMarcasOtro = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivos_fragment,container,false);

        databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");

        recyclerView = view.findViewById(R.id.recyclerView_tipodispositivos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaTipoDispositivos = new ArrayList<>();
        adapter = new ListaTipoDispositivoAdapter(listaTipoDispositivos, getContext());
        recyclerView.setAdapter(adapter);

        valueEventListener = databaseReference.addValueEventListener(new listener());
        return view;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");
//        databaseReference.removeEventListener(valueEventListener);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        valueEventListener = databaseReference.addValueEventListener(new listener());
//    }
    //Ver bien esto para desregistrar el listener, que se esta llamando dos veces

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaTipoDispositivos.clear();

                listaTipoDispositivos.add(new TipoDispositivoDto("Laptops",0,0));
                listaTipoDispositivos.add(new TipoDispositivoDto("Monitores",0,0));
                listaTipoDispositivos.add(new TipoDispositivoDto("Celulares",0,0));
                listaTipoDispositivos.add(new TipoDispositivoDto("Tablets",0,0));
                listaTipoDispositivos.add(new TipoDispositivoDto("Otros",0,0));

                for (DataSnapshot ds : snapshot.getChildren()) {
                    DispositivoDto dispositivo = ds.getValue(DispositivoDto.class);
                    System.out.println(dispositivo.getTipo().equals("Laptop"));
                    if (dispositivo.getTipo().equals("Laptop")) {
                        TipoDispositivoDto tipodispositivo = listaTipoDispositivos.get(0);
                        tipodispositivo.setCantidad(tipodispositivo.getCantidad() + dispositivo.getStock());

                        //System.out.println(listaMarcasLaptop.isEmpty());
                        if (listaMarcasLaptop.isEmpty()) {
                            tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                            listaMarcasLaptop.add(dispositivo.getMarca());
                        } else {
                            if (!listaMarcasLaptop.contains(dispositivo.getMarca())) {
                                tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                listaMarcasLaptop.add(dispositivo.getMarca());
                            }
                        }
                        System.out.println(tipodispositivo.getCantidadMarcas());

                    } else if (dispositivo.getTipo().equals("Monitor")) {
                        TipoDispositivoDto tipodispositivo = listaTipoDispositivos.get(1);
                        tipodispositivo.setCantidad(tipodispositivo.getCantidad() + dispositivo.getStock());

                        if (listaMarcasMonitor.isEmpty()) {
                            tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                            listaMarcasMonitor.add(dispositivo.getMarca());
                        } else {
                            if (!listaMarcasMonitor.contains(dispositivo.getMarca())) {
                                tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                listaMarcasMonitor.add(dispositivo.getMarca());
                            }
                        }
                    } else if (dispositivo.getTipo().equals("Celular")) {
                        TipoDispositivoDto tipodispositivo = listaTipoDispositivos.get(2);
                        tipodispositivo.setCantidad(tipodispositivo.getCantidad() + dispositivo.getStock());

                        if (listaMarcasCelular.isEmpty()) {
                            tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                            listaMarcasCelular.add(dispositivo.getMarca());
                        } else {
                            if (!listaMarcasCelular.contains(dispositivo.getMarca())) {
                                tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                listaMarcasCelular.add(dispositivo.getMarca());
                            }
                        }
                    } else if (dispositivo.getTipo().equals("Tablet")) {
                        TipoDispositivoDto tipodispositivo = listaTipoDispositivos.get(3);
                        tipodispositivo.setCantidad(tipodispositivo.getCantidad() + dispositivo.getStock());

                        if (listaMarcasTablet.isEmpty()) {
                            tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                            listaMarcasTablet.add(dispositivo.getMarca());
                        } else {
                            if (!listaMarcasTablet.contains(dispositivo.getMarca())) {
                                tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                listaMarcasTablet.add(dispositivo.getMarca());
                            }
                        }
                    } else {
                        TipoDispositivoDto tipodispositivo = listaTipoDispositivos.get(4);
                        tipodispositivo.setCantidad(tipodispositivo.getCantidad() + dispositivo.getStock());

                        if (listaMarcasOtro.isEmpty()) {
                            tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                            listaMarcasOtro.add(dispositivo.getMarca());
                        } else {
                            if (!listaMarcasOtro.contains(dispositivo.getMarca())) {
                                tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                listaMarcasOtro.add(dispositivo.getMarca());
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            } else {
                listaTipoDispositivos.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }
}
