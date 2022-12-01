package com.example.equipospucp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.EditarDispositivo;
import com.example.equipospucp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DispositivoDetalleAdapter extends RecyclerView.Adapter<DispositivoDetalleAdapter.DispositivoDetalleViewHolder> {

    private ArrayList<DispositivoDetalleDto> listaDispositivos;
    private Context context;

    public DispositivoDetalleAdapter(ArrayList<DispositivoDetalleDto> listaDispositivos, Context context) {
        this.setListaDispositivos(listaDispositivos);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<DispositivoDetalleDto> getListaDispositivos() {
        return listaDispositivos;
    }

    public void setListaDispositivos(ArrayList<DispositivoDetalleDto> listaDispositivos) {
        this.listaDispositivos = listaDispositivos;
    }

    @NonNull
    @Override
    public DispositivoDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_dispositivodetalle, parent, false);
        return new DispositivoDetalleAdapter.DispositivoDetalleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DispositivoDetalleViewHolder holder, int position) {

        DispositivoDetalleDto dispositivo = getListaDispositivos().get(position);
        //Cambiarlo por imagen en firebase db. Colocar imagen por defecto si no posee

        holder.textViewdetalles.setText("Tipo: "+ dispositivo.getDispositivoDto().getTipo() +"\nMarca: " + dispositivo.getDispositivoDto().getMarca() + "\nStock: " + dispositivo.getDispositivoDto().getStock() + "\nEquipos reservados: ");

        String caracteristicasenview = "";
        String caracteristicas = dispositivo.getDispositivoDto().getCaracteristicas();
        List<String> listadoCaracteristicas = Arrays.asList(caracteristicas.split("\\. "));
        for (String s : listadoCaracteristicas) {
            caracteristicasenview = caracteristicasenview + s + "\n";
        }
        holder.textViewcaracteristicas.setText(caracteristicasenview);

        String incluyeenview = "";
        String inlcuye = dispositivo.getDispositivoDto().getIncluye();
        List<String> listadoIncluye = Arrays.asList(inlcuye.split("\\. "));
        for (String s : listadoIncluye) {
            incluyeenview = incluyeenview + s + "\n";
        }
        holder.textViewincluye.setText(incluyeenview);

    }

    @Override
    public int getItemCount() { return getListaDispositivos().size(); }

    public class DispositivoDetalleViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewdetalles;
        public TextView textViewcaracteristicas;
        public TextView textViewincluye;
        public ImageView imageView;
        public DispositivoDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewdetalles = itemView.findViewById(R.id.textView_marcastockreservas);
            this.textViewcaracteristicas = itemView.findViewById(R.id.textView_caracteristicas);
            this.textViewincluye = itemView.findViewById(R.id.textView_incluye);
            this.imageView = itemView.findViewById(R.id.idImagenDispositivo);
        }
    }

}
