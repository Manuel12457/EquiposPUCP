package com.example.equipospucp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.ReservaDto;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.DetallesDispositivo;
import com.example.equipospucp.DetallesReserva;
import com.example.equipospucp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaReservasUsuarioAdapter extends RecyclerView.Adapter<ListaReservasUsuarioAdapter.ReservaUsuarioViewHolder>{

    private ArrayList<ReservaDto> listaReservas;
    private Context context;

    public ListaReservasUsuarioAdapter(ArrayList<ReservaDto> listaReservas, Context context) {
        this.setListaReservas(listaReservas);
        this.setContext(context);
    }

    public ArrayList<ReservaDto> getListaReservas() {
        return listaReservas;
    }

    public void setListaReservas(ArrayList<ReservaDto> listaReservas) {
        this.listaReservas = listaReservas;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ListaReservasUsuarioAdapter.ReservaUsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservasusuario, parent, false);
        return new ListaReservasUsuarioAdapter.ReservaUsuarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaReservasUsuarioAdapter.ReservaUsuarioViewHolder holder, int position) {

        ReservaDto reservaDto = getListaReservas().get(position);

        holder.textView_fechareserva_reservasusuario.setText(reservaDto.getReserva().getFechayhora());
        FirebaseDatabase.getInstance().getReference("dispositivos").child(reservaDto.getReserva().getIddispositivo())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                            holder.textView_inforeserva_reservausuario.setText("Reserva: " + dispositivo.getTipo() + " " + dispositivo.getMarca() +"\nTiempo de reserva: " + reservaDto.getReserva().getTiempoReserva() + " días");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.textView_estadoReservausuario.setText(reservaDto.getReserva().getEstado());
        if (reservaDto.getReserva().getEstado().equals("APROBADO")) {
            holder.textView_estadoReservausuario.setTextColor(Color.parseColor("#4CAF50"));
        } else if (reservaDto.getReserva().getEstado().equals("PENDIENTE")) {
            holder.textView_estadoReservausuario.setTextColor(Color.parseColor("#7E877E"));
        } else {
            holder.textView_estadoReservausuario.setTextColor(Color.parseColor("#E45252"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetallesReserva.class);
                intent.putExtra("id", reservaDto.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListaReservas().size();
    }

    public class ReservaUsuarioViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_fechareserva_reservasusuario;
        public TextView textView_inforeserva_reservausuario;
        public TextView textView_estadoReservausuario;
        public ImageView idImagenDispositivoReservas;
        public ReservaUsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_fechareserva_reservasusuario = itemView.findViewById(R.id.textView_fechareserva_reservasusuario);
            this.textView_inforeserva_reservausuario = itemView.findViewById(R.id.textView_inforeserva_reservausuario);
            this.textView_estadoReservausuario = itemView.findViewById(R.id.textView_estadoReservausuario);
            this.idImagenDispositivoReservas = itemView.findViewById(R.id.idImagenDispositivoReservas);
        }
    }
}
