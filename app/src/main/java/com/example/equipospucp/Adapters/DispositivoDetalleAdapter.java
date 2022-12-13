package com.example.equipospucp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Image;
import com.example.equipospucp.EditarDispositivo;
import com.example.equipospucp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DispositivoDetalleAdapter extends RecyclerView.Adapter<DispositivoDetalleAdapter.DispositivoDetalleViewHolder> {

    private ArrayList<DispositivoDetalleDto> listaDispositivos;
    private Context context;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("imagenes");

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

        holder.textViewdetalles.setText("Tipo: "+ dispositivo.getDispositivoDto().getTipo() +"\nMarca: " + dispositivo.getDispositivoDto().getMarca() + "\nStock: " + dispositivo.getDispositivoDto().getStock());

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

        //Slider - Descomentar para la visualización
        //listaUri: lista con los uris de las imagenes
        ArrayList<String> listaImageRef = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StorageReference imageRef;
                for (DataSnapshot children : snapshot.getChildren()){
                    Image image =children.getValue(Image.class);
                    if (image.getDispositivo().equals(dispositivo.getId())){
                        Log.d("ruta", "img/"+image.getImagen());
                        listaImageRef.add("img/"+image.getImagen());
                    }
                }

                holder.viewPagerImageSllider.setAdapter(new SliderAdapter(listaImageRef, holder.viewPagerImageSllider));

                holder.viewPagerImageSllider.setClipToPadding(false);
                holder.viewPagerImageSllider.setClipChildren(false);
                holder.viewPagerImageSllider.setOffscreenPageLimit(3);
                holder.viewPagerImageSllider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
                CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        float r = 1 - Math.abs(position);
                        page.setScaleY(0.85f + r * 0.15f);
                    }
                });
                holder.viewPagerImageSllider.setPageTransformer(compositePageTransformer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Slider

    }

    @Override
    public int getItemCount() { return getListaDispositivos().size(); }

    public class DispositivoDetalleViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewdetalles;
        public TextView textViewcaracteristicas;
        public TextView textViewincluye;
        private ViewPager2 viewPagerImageSllider;
        public DispositivoDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewdetalles = itemView.findViewById(R.id.textView_marcastockreservas);
            this.textViewcaracteristicas = itemView.findViewById(R.id.textView_caracteristicas);
            this.textViewincluye = itemView.findViewById(R.id.textView_incluye);
            this.viewPagerImageSllider = itemView.findViewById(R.id.viewPagerImageSlider);
        }
    }

}
