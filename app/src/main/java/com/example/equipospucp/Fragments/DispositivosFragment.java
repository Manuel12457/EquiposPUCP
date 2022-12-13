package com.example.equipospucp.Fragments;

import android.content.Intent;
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

import com.example.equipospucp.Adapters.ListaTipoDispositivoAdapter;
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.TipoDispositivoDto;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.EditarDispositivo;
import com.example.equipospucp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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

    Usuario usuario;
    boolean esUsuarioTI = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dispositivos_fragment,container,false);

        FloatingActionButton button = view.findViewById(R.id.fab_nuevodispositivo);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String mensaje_exito = bundle.getString("exito");
            if (mensaje_exito != null && !mensaje_exito.equals("")) {
                Snackbar.make(view.findViewById(R.id.id_dispositivosFragment), mensaje_exito, Snackbar.LENGTH_LONG).show();
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), EditarDispositivo.class);
                intent.putExtra("accion", "nuevo");
                startActivity(intent);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("dispositivos");

        recyclerView = view.findViewById(R.id.recyclerView_tipodispositivos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaTipoDispositivos = new ArrayList<>();
        adapter = new ListaTipoDispositivoAdapter(listaTipoDispositivos, getContext());
        recyclerView.setAdapter(adapter);

        valueEventListener = databaseReference.addValueEventListener(new listener());

        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            usuario = snapshot.getValue(Usuario.class);
                            if (usuario.getRol().equals("Usuario TI")) {
                                button.setVisibility(View.VISIBLE);
                                esUsuarioTI = true;
                                valueEventListener = databaseReference.addValueEventListener(new listener());
                            } else {
                                valueEventListener = databaseReference.addValueEventListener(new listener());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaTipoDispositivos.clear();
                listaMarcasLaptop.clear();
                listaMarcasMonitor.clear();
                listaMarcasCelular.clear();
                listaMarcasTablet.clear();
                listaMarcasOtro.clear();

                listaTipoDispositivos.add(new TipoDispositivoDto("Laptops",0,0, listaMarcasLaptop));
                listaTipoDispositivos.add(new TipoDispositivoDto("Monitores",0,0, listaMarcasMonitor));
                listaTipoDispositivos.add(new TipoDispositivoDto("Celulares",0,0, listaMarcasCelular));
                listaTipoDispositivos.add(new TipoDispositivoDto("Tablets",0,0, listaMarcasTablet));
                listaTipoDispositivos.add(new TipoDispositivoDto("Otros",0,0, listaMarcasOtro));

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Dispositivo dispositivo = ds.getValue(Dispositivo.class);
                    if (dispositivo.getVisible()) {

                        if (!esUsuarioTI) {
                            if (dispositivo.getStock() != 0) {
                                if (dispositivo.getTipo().equals("Laptop")) {
                                    TipoDispositivoDto tipodispositivo = listaTipoDispositivos.get(0);
                                    tipodispositivo.setCantidad(tipodispositivo.getCantidad() + dispositivo.getStock());

                                    if (listaMarcasLaptop.isEmpty()) {
                                        tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                        listaMarcasLaptop.add(dispositivo.getMarca());
                                    } else {
                                        if (!listaMarcasLaptop.contains(dispositivo.getMarca())) {
                                            tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                            listaMarcasLaptop.add(dispositivo.getMarca());
                                        }
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                    }
                                    tipodispositivo.setListaMarcas(listaMarcasLaptop);

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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                    }
                                    tipodispositivo.setListaMarcas(listaMarcasMonitor);
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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                    }
                                    tipodispositivo.setListaMarcas(listaMarcasCelular);
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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                    }
                                    tipodispositivo.setListaMarcas(listaMarcasTablet);
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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                    }
                                    tipodispositivo.setListaMarcas(listaMarcasOtro);
                                }
                            }
                        } else {
                            if (dispositivo.getTipo().equals("Laptop")) {
                                TipoDispositivoDto tipodispositivo = listaTipoDispositivos.get(0);
                                tipodispositivo.setCantidad(tipodispositivo.getCantidad() + dispositivo.getStock());

                                if (listaMarcasLaptop.isEmpty()) {
                                    tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                    listaMarcasLaptop.add(dispositivo.getMarca());
                                } else {
                                    if (!listaMarcasLaptop.contains(dispositivo.getMarca())) {
                                        tipodispositivo.setCantidadMarcas(tipodispositivo.getCantidadMarcas() + 1);
                                        listaMarcasLaptop.add(dispositivo.getMarca());
                                    }
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                }
                                tipodispositivo.setListaMarcas(listaMarcasLaptop);

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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                }
                                tipodispositivo.setListaMarcas(listaMarcasMonitor);
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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                }
                                tipodispositivo.setListaMarcas(listaMarcasCelular);
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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                }
                                tipodispositivo.setListaMarcas(listaMarcasTablet);
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
//                            else {
//                                tipodispositivo.setCantidadMarcas(tipodispositivo.getListaMarcas().size());
//                            }
                                }
                                tipodispositivo.setListaMarcas(listaMarcasOtro);
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
