package com.example.equipospucp.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.DTOs.UsuarioDto;
import com.example.equipospucp.DetalleUsuario;
import com.example.equipospucp.DetallesDispositivo;
import com.example.equipospucp.Fragments.DispositivosPorTipoFragment;
import com.example.equipospucp.R;

import java.util.ArrayList;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.UsuariosViewHolder> {

    private ArrayList<UsuarioDto> listaUsuarios;
    private Context context;

    public ListaUsuariosAdapter(ArrayList<UsuarioDto> listaUsuarios, Context context) {
        this.setListaUsuarios(listaUsuarios);
        this.setContext(context);
    }

    public ArrayList<UsuarioDto> getListaUsuarios() {
        return listaUsuarios;
    }

    public void setListaUsuarios(ArrayList<UsuarioDto> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_usuario, parent, false);
        return new ListaUsuariosAdapter.UsuariosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosViewHolder holder, int position) {

        UsuarioDto usuario = getListaUsuarios().get(position);
        holder.textView_InfoUsuario.setText(usuario.getUsuario().getCodigo() + "\n" + usuario.getUsuario().getRol() + "\n" + usuario.getUsuario().getCorreo());
        //PONER LA FOTO
        //RECIBIR LISTA DE URIs DE FOTOS

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetalleUsuario.class);
                intent.putExtra("id", usuario.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListaUsuarios().size();
    }

    public class UsuariosViewHolder extends RecyclerView.ViewHolder {
        public TextView textView_InfoUsuario;
        public ImageView idImagenUsuario;
        public UsuariosViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView_InfoUsuario = itemView.findViewById(R.id.textView_InfoUsuario);
            this.idImagenUsuario = itemView.findViewById(R.id.idImagenUsuario);
        }
    }
}
