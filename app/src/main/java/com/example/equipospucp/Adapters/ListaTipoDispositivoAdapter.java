package com.example.equipospucp.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equipospucp.DTOs.TipoDispositivoDto;
import com.example.equipospucp.Fragments.DispositivosPorTipoFragment;
import com.example.equipospucp.R;

import java.util.ArrayList;

public class ListaTipoDispositivoAdapter extends RecyclerView.Adapter<ListaTipoDispositivoAdapter.DispositivosViewHolder> {

    private ArrayList<TipoDispositivoDto> listaTipoDispositivos;
    private Context context;

    public ListaTipoDispositivoAdapter(ArrayList<TipoDispositivoDto> listaTipoDispositivos, Context context) {
        this.setListaDispositivos(listaTipoDispositivos);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<TipoDispositivoDto> getListaDispositivos() {
        return listaTipoDispositivos;
    }

    public void setListaDispositivos(ArrayList<TipoDispositivoDto> listaDispositivos) {
        this.listaTipoDispositivos = listaDispositivos;
    }

    @NonNull
    @Override
    public DispositivosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_tipo_dispositivo, parent, false);
        return new ListaTipoDispositivoAdapter.DispositivosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DispositivosViewHolder holder, int position) {

        TipoDispositivoDto tipodispositivo = getListaDispositivos().get(position);

        if (tipodispositivo.getTipo().equals("Laptops")) {
            holder.imageView.setImageResource(R.drawable.ic_laptop);
        } else if (tipodispositivo.getTipo().equals("Monitores")) {
            holder.imageView.setImageResource(R.drawable.ic_monitor);
        } else if (tipodispositivo.getTipo().equals("Celulares")) {
            holder.imageView.setImageResource(R.drawable.ic_celular);
        } else if (tipodispositivo.getTipo().equals("Tablets")) {
            holder.imageView.setImageResource(R.drawable.ic_tablet);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_otros);
        }
        holder.textView.setText(tipodispositivo.getTipo() + "\n" + tipodispositivo.getCantidadMarcas() + " marcas" + "\n"
                + tipodispositivo.getCantidad() + " dispositivos");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();

                Bundle bundle = new Bundle();
                if (tipodispositivo.getTipo().equals("Laptops")) {
                    bundle.putString("tipo", "Laptop");
                } else if (tipodispositivo.getTipo().equals("Monitores")) {
                    bundle.putString("tipo", "Monitor");
                } else if (tipodispositivo.getTipo().equals("Celulares")) {
                    bundle.putString("tipo", "Celular");
                } else if (tipodispositivo.getTipo().equals("Tablets")) {
                    bundle.putString("tipo", "Tablet");
                } else {
                    bundle.putString("tipo", "Otro");
                }

                ArrayList<String> a = new ArrayList<>();
                a.add("Todas las marcas");
                for (String s : tipodispositivo.getListaMarcas()) {
                    a.add(s);
                }
                bundle.putStringArrayList("marcas", a);
                DispositivosPorTipoFragment dispositivosPorTipoFragment = new DispositivosPorTipoFragment();
                dispositivosPorTipoFragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.container_fragment, dispositivosPorTipoFragment).addToBackStack(null).commit();
            }
        });

    }

    @Override
    public int getItemCount() { return getListaDispositivos().size(); }

    public class DispositivosViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;
        public DispositivosViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.textView_TipoDispositivo);
            this.imageView = itemView.findViewById(R.id.idImagenTipoDispositivo);
        }
    }

}
