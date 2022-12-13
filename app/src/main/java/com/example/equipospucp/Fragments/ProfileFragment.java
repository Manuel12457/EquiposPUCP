package com.example.equipospucp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    Usuario usuario;
    ImageButton editarFoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment,container,false);

        firebaseAuth = firebaseAuth.getInstance();
        String id = firebaseAuth.getCurrentUser().getUid();
        System.out.println(id);

        FirebaseDatabase.getInstance().getReference("usuarios").child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            System.out.println("ONDATACHANGE");
                            usuario = snapshot.getValue(Usuario.class);
                            //Cambiar tambien profile picture
                            TextView codigo = view.findViewById(R.id.textview_codigo);
                            TextView rol = view.findViewById(R.id.textview_rol);
                            TextView correo = view.findViewById(R.id.textview_correo);

                            codigo.setText(usuario.getCodigo());
                            rol.setText(usuario.getRol());

                            correo.setText("Correo con el cual se ha registrado\n" + usuario.getCorreo());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });

        return view;
    }
}