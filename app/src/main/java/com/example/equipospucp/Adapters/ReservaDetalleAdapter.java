package com.example.equipospucp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.ReservaDto;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.DTOs.UsuarioDto;
import com.example.equipospucp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ReservaDetalleAdapter extends RecyclerView.Adapter<ReservaDetalleAdapter.ReservaDetalleViewHolder> {

    private Context context;
    private List<ReservaDto> listaReserva;
    Usuario usuario;
    GoogleMap mapa;

    public ReservaDetalleAdapter(Context context, List<ReservaDto> listaReserva) {
        this.context = context;
        this.listaReserva = listaReserva;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<ReservaDto> getListaReserva() {
        return listaReserva;
    }

    public void setListaReserva(List<ReservaDto> listaReserva) {
        this.listaReserva = listaReserva;
    }

    public class ReservaDetalleViewHolder extends RecyclerView.ViewHolder {

        public TextView estadoReserva_detalle;
        public TextView usuarioReserva_detalle;
        public TextView fechaHoraReserva_detalle;
        public TextView dispositivoReserva_detalle;
        public TextView motivoReserva_detalle;
        public TextView cursoReserva_detalle;
        public TextView tiempoReserva_detalle;
        public TextView programasReserva_detalle;
        public ImageView dniUsuario_detalleReserva;
        public TextView detallesAdicionalesReserva_detalle;
        public TextView textView20;
        public MapView mapView2;
        public ReservaDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.estadoReserva_detalle = itemView.findViewById(R.id.estadoReserva_detalle);
            this.usuarioReserva_detalle =  itemView.findViewById(R.id.usuarioReserva_detalle);
            this.fechaHoraReserva_detalle = itemView.findViewById(R.id.fechaHoraReserva_detalle);
            this.dispositivoReserva_detalle = itemView.findViewById(R.id.dispositivoReserva_detalle);
            this.motivoReserva_detalle = itemView.findViewById(R.id.motivoReserva_detalle);
            this.cursoReserva_detalle = itemView.findViewById(R.id.cursoReserva_detalle);
            this.tiempoReserva_detalle = itemView.findViewById(R.id.tiempoReserva_detalle);
            this.programasReserva_detalle = itemView.findViewById(R.id.programasReserva_detalle);
            this.dniUsuario_detalleReserva = itemView.findViewById(R.id.dniUsuario_detalleReserva);
            this.detallesAdicionalesReserva_detalle = itemView.findViewById(R.id.detallesAdicionalesReserva_detalle);
            this.mapView2 = itemView.findViewById(R.id.mapView2);
            this.textView20 = itemView.findViewById(R.id.textView20);
        }
    }

    @NonNull
    @Override
    public ReservaDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservadetalle, parent, false);
        return new ReservaDetalleAdapter.ReservaDetalleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaDetalleViewHolder holder, int position) {
        ReservaDto reservaDto = listaReserva.get(position);

        if (reservaDto.getReserva().getEstado().equals("APROBADO")) {
            holder.textView20.setVisibility(View.VISIBLE);
            holder.mapView2.setVisibility(View.VISIBLE);

            holder.mapView2.onCreate(null);
            holder.mapView2.onResume();
            holder.mapView2.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    LatLng pucp = new LatLng(-12.06952386565627, -77.08019039640382);
                    mapa = googleMap;
                    mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(pucp, 15));
                    mapa.getUiSettings().setZoomGesturesEnabled(false);
                    mapa.getUiSettings().setScrollGesturesEnabled(false);
                    mapa.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);

                    LatLng ubicacionRecojo = new LatLng(Double.parseDouble(reservaDto.getReserva().getLatitud()), Double.parseDouble(reservaDto.getReserva().getLongitud()));
                    mapa.addMarker(new MarkerOptions()
                            .position(ubicacionRecojo)
                            .title("Lugar de recojo"));
                }
            });
        }

        holder.fechaHoraReserva_detalle.setText("Fecha y hora de la reserva\n"+reservaDto.getReserva().getFechayhora());
        if (reservaDto.getReserva().getDetallesAdicionales().equals("")) {
            holder.detallesAdicionalesReserva_detalle.setText("Sin detalles adicionales");
        } else {
            holder.detallesAdicionalesReserva_detalle.setText(reservaDto.getReserva().getDetallesAdicionales());
        }

        holder.motivoReserva_detalle.setText(reservaDto.getReserva().getMotivo());
        if (reservaDto.getReserva().getProgramasInstalados().equals("")) {
            holder.programasReserva_detalle.setText("Sin programas adicionales a instalar");
        } else {
            holder.programasReserva_detalle.setText(reservaDto.getReserva().getProgramasInstalados());
        }

        holder.estadoReserva_detalle.setText(" " + reservaDto.getReserva().getEstado());
        if (reservaDto.getReserva().getEstado().equals("APROBADO")) {
            holder.estadoReserva_detalle.setTextColor(Color.parseColor("#4CAF50"));
        } else if (reservaDto.getReserva().getEstado().equals("PENDIENTE")) {
            holder.estadoReserva_detalle.setTextColor(Color.parseColor("#7E877E"));
        } else {
            holder.estadoReserva_detalle.setTextColor(Color.parseColor("#E45252"));
        }

        holder.cursoReserva_detalle.setText("Curso: "+reservaDto.getReserva().getCurso());
        holder.tiempoReserva_detalle.setText("Tiempo de reserva: "+reservaDto.getReserva().getTiempoReserva().toString()+" día(s)");
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("img/"+reservaDto.getReserva().getDni());
        Glide.with(getContext()).load(imageRef).into(holder.dniUsuario_detalleReserva);

        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            usuario = snapshot.getValue(Usuario.class);
                            if (!usuario.getRol().equals("Usuario TI")) {
                                holder.mapView2.setVisibility(View.GONE);
                                holder.itemView.findViewById(R.id.textView20).setVisibility(View.GONE);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });
        FirebaseDatabase.getInstance().getReference().child("usuarios").child(reservaDto.getReserva().getIdUsuario()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Usuario usuario1 = snapshot.getValue(Usuario.class);
                    holder.usuarioReserva_detalle.setText("Hecha por\n"+usuario1.getRol()+" "+usuario1.getCodigo());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        FirebaseDatabase.getInstance().getReference().child("dispositivos").child(reservaDto.getReserva().getIddispositivo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                    holder.dispositivoReserva_detalle.setText("Dispositivo a reservar\n" + dispositivo.getTipo() + " " + dispositivo.getMarca());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }

    @Override
    public int getItemCount() {
        return listaReserva.size();
    }
}
