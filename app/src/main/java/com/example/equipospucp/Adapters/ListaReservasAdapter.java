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
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Reserva;
import com.example.equipospucp.DTOs.ReservaDto;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.DetallesDispositivo;
import com.example.equipospucp.DetallesReserva;
import com.example.equipospucp.R;
import com.example.equipospucp.databinding.ReservasFragmentBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListaReservasAdapter extends RecyclerView.Adapter<ListaReservasAdapter.ReservaViewHolder> {

    private ArrayList<ReservaDto> listaReservas;
    private Context context;

    public ListaReservasAdapter(ArrayList<ReservaDto> listaReservas, Context context) {
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
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservas, parent, false);
        return new ListaReservasAdapter.ReservaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {

        ReservaDto reservaDto = getListaReservas().get(position);
        //Cambiarlo por imagen en firebase db. Colocar imagen por defecto si no posee
        FirebaseDatabase.getInstance().getReference("usuarios").child(reservaDto.getReserva().getIdUsuario())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Usuario usuario = snapshot.getValue(Usuario.class);
                                    holder.textView_usuario.setText(usuario.getCodigo() + "\n" + usuario.getRol());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        FirebaseDatabase.getInstance().getReference("dispositivos").child(reservaDto.getReserva().getIddispositivo())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                            holder.textView_fechaydispositivo.setText(reservaDto.getReserva().getFechayhora() + "\nReserva: " + dispositivo.getTipo() + " " + dispositivo.getMarca());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.textView_estado.setText(reservaDto.getReserva().getEstado());
        if (reservaDto.getReserva().getEstado().equals("APROBADO")) {
            holder.textView_estado.setTextColor(Color.parseColor("#4CAF50"));
        } else if (reservaDto.getReserva().getEstado().equals("PENDIENTE")) {
            holder.textView_estado.setTextColor(Color.parseColor("#7E877E"));
        } else {
            holder.textView_estado.setTextColor(Color.parseColor("#E45252"));
        }
        //poder seleccionarlo

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

    public class ReservaViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_usuario;
        public TextView textView_fechaydispositivo;
        public TextView textView_estado;
        public ImageView imageView;
        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_usuario = itemView.findViewById(R.id.textView_InfoUsuarioreservas);
            this.textView_fechaydispositivo = itemView.findViewById(R.id.textView_fechareserva_reservas);
            this.textView_estado = itemView.findViewById(R.id.textView_estadoReserva);
            this.imageView = itemView.findViewById(R.id.idImagenUsuarioReservas);
        }
    }
}
