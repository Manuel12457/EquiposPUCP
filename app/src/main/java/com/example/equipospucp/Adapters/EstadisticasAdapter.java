package com.example.equipospucp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.DTOs.UsuarioDto;
import com.example.equipospucp.R;

import java.util.ArrayList;

public class EstadisticasAdapter extends RecyclerView.Adapter<EstadisticasAdapter.MarcasViewHolder> {

    private ArrayList<String> listaMarcas;
    private Context context;

    public EstadisticasAdapter(ArrayList<String> listaMarcas, Context context) {
        this.setListaMarcas(listaMarcas);
        this.setContext(context);
    }

    public ArrayList<String> getListaMarcas() {
        return listaMarcas;
    }

    public void setListaMarcas(ArrayList<String> listaMarcas) {
        this.listaMarcas = listaMarcas;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MarcasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_marcas, parent, false);
        return new EstadisticasAdapter.MarcasViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MarcasViewHolder holder, int position) {
        String string = getListaMarcas().get(position);
        holder.textView_MarcaEstadisticas.setText(string);
    }

    @Override
    public int getItemCount() {
        return getListaMarcas().size();
    }

    public class MarcasViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_MarcaEstadisticas;
        public MarcasViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_MarcaEstadisticas = itemView.findViewById(R.id.textView_MarcaEstadisticas);
        }
    }
}
