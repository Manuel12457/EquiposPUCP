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

import com.example.equipospucp.Adapters.ListaReservasAdapter;
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.Reserva;
import com.example.equipospucp.DTOs.ReservaDto;
import com.example.equipospucp.R;
import com.google.android.material.textfield.TextInputLayout;
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

public class ReservasFragment extends Fragment {

    DatabaseReference databaseReference;
    ListaReservasAdapter adapter;
    ValueEventListener valueEventListener;

    ArrayList<ReservaDto> listaReservas;
    String seleccionFiltro;

    RecyclerView recyclerView;
    TextInputLayout spinnerReservas;
    TextView noreservas;

    SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    Date currentTime = Calendar.getInstance().getTime();

    Calendar menosundia = Calendar.getInstance();
    Calendar menosdosdias = Calendar.getInstance();
    Calendar menosunasemana = Calendar.getInstance();
    Calendar menosdossemanas = Calendar.getInstance();
    Date fechahorareserva;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reservas_fragment,container,false);

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
        adapter = new ListaReservasAdapter(listaReservas, getContext());
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("reservas");
        valueEventListener = databaseReference.addValueEventListener(new ReservasFragment.listener());

        AutoCompleteTextView spinner = view.findViewById(R.id.idReserva);
        String[] arrayReservas = getResources().getStringArray(R.array.reservas);
        spinner.setText(arrayReservas[0],false);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                seleccionFiltro = spinner.getText().toString();
            }
        });

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaReservas.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Reserva reserva = ds.getValue(Reserva.class);
                    ReservaDto reservaDto = new ReservaDto();
                    reservaDto.setReserva(reserva);
                    reservaDto.setId(ds.getKey());

                    //Verificar fecha y hora de emision de la solicitud segun seleccion del filtro
                    try {
                        fechahorareserva = sf.parse(reserva.getFechayhora());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (seleccionFiltro.equals("Todas las reservas")) {
                        listaReservas.add(reservaDto);
                    } else if (seleccionFiltro.equals("Hoy") && fechahorareserva.after(menosundia.getTime())) {
                        listaReservas.add(reservaDto);
                    } else if (seleccionFiltro.equals("Ayer") && fechahorareserva.after(menosdosdias.getTime())) {
                        listaReservas.add(reservaDto);
                    } else if (seleccionFiltro.equals("Hace 1 semana") && fechahorareserva.after(menosunasemana.getTime())) {
                        listaReservas.add(reservaDto);
                    } else if (fechahorareserva.after(menosdossemanas.getTime())) { //"Hace 2 semanas"
                        listaReservas.add(reservaDto);
                    }
                }

                if (listaReservas.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    spinnerReservas.setVisibility(View.INVISIBLE);
                    noreservas.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    spinnerReservas.setVisibility(View.VISIBLE);
                    noreservas.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            } else {
                listaReservas.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }

}
