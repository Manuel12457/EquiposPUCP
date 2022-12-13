package com.example.equipospucp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.Adapters.ListaReservasAdapter;
import com.example.equipospucp.Adapters.ListaReservasUsuarioAdapter;
import com.example.equipospucp.DTOs.Reserva;
import com.example.equipospucp.DTOs.ReservaDto;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReservasUsuarioFragment extends Fragment {

    DatabaseReference databaseReference;
    ListaReservasUsuarioAdapter adapterUsuario;
    ValueEventListener valueEventListener;

    ArrayList<ReservaDto> listaReservas = new ArrayList<>();
    String seleccionFiltro = "Todas las reservas";

    RecyclerView recyclerView;
    TextInputLayout spinnerReservas;
    TextView noreservas;

    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Date currentTime = Calendar.getInstance().getTime();

    Calendar menosundia = Calendar.getInstance();
    Calendar menosdosdias = Calendar.getInstance();
    Calendar menosunasemana = Calendar.getInstance();
    Calendar menosdossemanas = Calendar.getInstance();

    boolean todaslasconsultas = true;
    boolean ultimas24h = false;
    boolean ultimas48h = false;
    boolean ultimasemana = false;
    boolean ultimasdossemanas = false;
    String fecha;

    Usuario usuario;
    boolean esUsuarioTI = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reservas_fragment,container,false);

        Log.d("prueba", "EN RESERVA USER FRAGMENT ");

        try {
            menosundia.setTime(sf.parse(sf.format(currentTime)));
            menosundia.add(Calendar.DATE, -1);
            menosdosdias.setTime(sf.parse(sf.format(currentTime)));
            menosdosdias.add(Calendar.DATE, -2);
            menosunasemana.setTime(sf.parse(sf.format(currentTime)));
            menosunasemana.add(Calendar.DATE, -7);
            menosdossemanas.setTime(sf.parse(sf.format(currentTime)));
            menosdossemanas.add(Calendar.DATE, -14);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        noreservas = view.findViewById(R.id.textView_noreservas);
        spinnerReservas = view.findViewById(R.id.spinner_reservas);

        recyclerView = view.findViewById(R.id.recyclerView_reservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaReservas = new ArrayList<>();

        adapterUsuario = new ListaReservasUsuarioAdapter(listaReservas, getContext());
        recyclerView.setAdapter(adapterUsuario);

        databaseReference = FirebaseDatabase.getInstance().getReference("reservas");

        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            usuario = snapshot.getValue(Usuario.class);
                            if (usuario.getRol().equals("Usuario TI")) {
                                esUsuarioTI = true;
                            }
                            valueEventListener = databaseReference.addValueEventListener(new ReservasUsuarioFragment.listener());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });

        AutoCompleteTextView spinner = view.findViewById(R.id.idReserva);
        String[] arrayReservas = getResources().getStringArray(R.array.reservas);
        spinner.setText(arrayReservas[0],false);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fecha = spinner.getText().toString();
                if (fecha.equals("Todas las reservas")) {
                    todaslasconsultas = true;
                    ultimas24h = false;
                    ultimas48h = false;
                    ultimasemana = false;
                    ultimasdossemanas = false;
                    valueEventListener = databaseReference.addValueEventListener(new ReservasUsuarioFragment.listener());
                } else if (fecha.equals("Hoy")) {
                    todaslasconsultas = false;
                    ultimas24h = true;
                    ultimas48h = false;
                    ultimasemana = false;
                    ultimasdossemanas = false;
                    valueEventListener = databaseReference.addValueEventListener(new ReservasUsuarioFragment.listener());
                } else if (fecha.equals("Ayer")) {
                    todaslasconsultas = false;
                    ultimas24h = false;
                    ultimas48h = true;
                    ultimasemana = false;
                    ultimasdossemanas = false;
                    valueEventListener = databaseReference.addValueEventListener(new ReservasUsuarioFragment.listener());
                } else if (fecha.equals("Hace 1 semana")) {
                    todaslasconsultas = false;
                    ultimas24h = false;
                    ultimas48h = false;
                    ultimasemana = true;
                    ultimasdossemanas = false;
                    valueEventListener = databaseReference.addValueEventListener(new ReservasUsuarioFragment.listener());
                } else if (fecha.equals("Hace 2 semanas")) {
                    todaslasconsultas = false;
                    ultimas24h = false;
                    ultimas48h = false;
                    ultimasemana = false;
                    ultimasdossemanas = true;
                    valueEventListener = databaseReference.addValueEventListener(new ReservasUsuarioFragment.listener());
                }
            }
        });

        return view;
    }

    class listener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d("prueba", "SNAPSHOT EXISTS: " + snapshot.exists());
            if (snapshot.exists()) { //Nodo referente existe
                listaReservas.clear();
                //Si esUsuarioTI es true, deberan mostrarse todas las reservas, sino, solo las que posean la id del usuario cliente
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Reserva reserva = ds.getValue(Reserva.class);
                    ReservaDto reservaDto = new ReservaDto();
                    reservaDto.setReserva(reserva);
                    reservaDto.setId(ds.getKey());

                    //FECHA CONSULTA
                    Calendar calConsulta = Calendar.getInstance();
                    try {
                        calConsulta.setTime(sf.parse(reserva.getFechayhora()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //FECHA CONSULTA

                    //FECHA FILTRO
                    Date currentTime = Calendar.getInstance().getTime();
                    Calendar calFiltro = Calendar.getInstance();
                    try {
                        calFiltro.setTime(sf.parse(sf.format(currentTime)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (ultimas24h) {
                        Log.d("filtro", "ULTIMAS 24H");
                        calFiltro.add(Calendar.DATE, -1);
                    } else if (ultimas48h) {
                        Log.d("filtro", "ULTIMAS 48H");
                        calFiltro.add(Calendar.DATE, -2);
                    } else if (ultimasemana) {
                        Log.d("filtro", "ULTIMO MES");
                        calFiltro.add(Calendar.DATE, -7);
                    } else if (ultimasdossemanas) {
                        Log.d("filtro", "ULTIMO MES");
                        calFiltro.add(Calendar.DATE, -14);
                    }
                    //FECHA FILTRO

                    Log.d("prueba", "RESERVA GET ID USUARIO: " + reserva.getIdUsuario());
                    Log.d("prueba", "ID USUARIO LOGUEADO: " + FirebaseAuth.getInstance().getUid());
                    if (reserva.getIdUsuario().equals(FirebaseAuth.getInstance().getUid())) {
                        if (todaslasconsultas) {
                            listaReservas.add(reservaDto);
                        } else {
                            if (calConsulta.getTime().after(calFiltro.getTime())) {
                                Log.d("filtro", "CUMPLE FILTRO");
                                listaReservas.add(reservaDto);
                            }
                        }
                    }

//                    Date fechahorareserva = new Date();
//                    //Verificar fecha y hora de emision de la solicitud segun seleccion del filtro
//                    try {
//                        fechahorareserva = sf.parse(reserva.getFechayhora());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (seleccionFiltro.equals("Todas las reservas")) {
//                        listaReservas.add(reservaDto);
//                    } else if (seleccionFiltro.equals("Hoy") && fechahorareserva.after(menosundia.getTime())) {
//                        listaReservas.add(reservaDto);
//                    } else if (seleccionFiltro.equals("Ayer") && fechahorareserva.after(menosdosdias.getTime())) {
//                        listaReservas.add(reservaDto);
//                    } else if (seleccionFiltro.equals("Hace 1 semana") && fechahorareserva.after(menosunasemana.getTime())) {
//                        listaReservas.add(reservaDto);
//                    } else if (fechahorareserva.after(menosdossemanas.getTime())) { //"Hace 2 semanas"
//                        listaReservas.add(reservaDto);
//                    }
                }

                if (listaReservas.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    noreservas.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noreservas.setVisibility(View.INVISIBLE);
                }
                adapterUsuario.notifyDataSetChanged();
            } else {
                noreservas.setVisibility(View.VISIBLE);
                listaReservas.clear();
                adapterUsuario.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }

}
