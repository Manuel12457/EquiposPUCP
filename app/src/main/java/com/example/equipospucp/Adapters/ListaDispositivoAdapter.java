package com.example.equipospucp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DetallesDispositivo;
import com.example.equipospucp.R;

import java.util.ArrayList;

public class ListaDispositivoAdapter extends RecyclerView.Adapter<ListaDispositivoAdapter.ListaDispositivosViewHolder> {

    private ArrayList<DispositivoDetalleDto> listaDispositivos;
    private Context context;

    public ListaDispositivoAdapter(ArrayList<DispositivoDetalleDto> listaDispositivos, Context context) {
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
    public ListaDispositivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_dispositivo, parent, false);
        return new ListaDispositivoAdapter.ListaDispositivosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaDispositivosViewHolder holder, int position) {

        DispositivoDetalleDto dispositivo = getListaDispositivos().get(position);
        //Cambiarlo por imagen en firebase db. Colocar imagen por defecto si no posee
        if (dispositivo.getDispositivoDto().getTipo().equals("Laptops")) {
            holder.imageView.setImageResource(R.drawable.ic_laptop);
        } else if (dispositivo.getDispositivoDto().getTipo().equals("Monitores")) {
            holder.imageView.setImageResource(R.drawable.ic_monitor);
        } else if (dispositivo.getDispositivoDto().getTipo().equals("Celulares")) {
            holder.imageView.setImageResource(R.drawable.ic_celular);
        } else if (dispositivo.getDispositivoDto().getTipo().equals("Tablets")) {
            holder.imageView.setImageResource(R.drawable.ic_tablet);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_otros);
        }
        holder.textView.setText(dispositivo.getDispositivoDto().getTipo() + " " + dispositivo.getDispositivoDto().getMarca() + "\n" + "Stock: " + dispositivo.getDispositivoDto().getStock());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetallesDispositivo.class);
                intent.putExtra("id", dispositivo.getId());
                intent.putExtra("tipo", dispositivo.getDispositivoDto().getTipo());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() { return getListaDispositivos().size(); }

    public class ListaDispositivosViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public ListaDispositivosViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.textView_Dispositivo);
            this.imageView = itemView.findViewById(R.id.idImagenDispositivo);
        }
    }

}
