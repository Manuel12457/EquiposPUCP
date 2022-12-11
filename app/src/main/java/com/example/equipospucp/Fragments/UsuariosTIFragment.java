package com.example.equipospucp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.equipospucp.Adapters.ListaUsuariosAdapter;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.DTOs.UsuarioDto;
import com.example.equipospucp.DetallesDispositivo;
import com.example.equipospucp.NuevoUsuarioTI;
import com.example.equipospucp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UsuariosTIFragment extends Fragment {

    DatabaseReference databaseReference;
    ListaUsuariosAdapter adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;

    ArrayList<UsuarioDto> listaUsuarios;
    String busqueda;
    String filtro;
    TextView noresultados;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_usuarios_t_i, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");

        recyclerView = view.findViewById(R.id.recyclerViewUsuariosTI);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaUsuarios = new ArrayList<>();
        adapter = new ListaUsuariosAdapter(listaUsuarios, getContext());
        recyclerView.setAdapter(adapter);

        valueEventListener = databaseReference.addValueEventListener(new UsuariosTIFragment.listener());

        noresultados = view.findViewById(R.id.noUsuarios);
        TextInputLayout search = view.findViewById(R.id.inputBuscarPorCodigoUsuarioTI);
        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                busqueda = search.getEditText().getText().toString();
                valueEventListener = databaseReference.addValueEventListener(new UsuariosTIFragment.listener());
            }
        });

        FloatingActionButton fabNuevoUsuarioTI = view.findViewById(R.id.fabNuevoUsuarioTI);
        fabNuevoUsuarioTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NuevoUsuarioTI.class);
                getContext().startActivity(intent);
            }
        });

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaUsuarios.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Usuario usuario = ds.getValue(Usuario.class);

                    UsuarioDto usuarioDto = new UsuarioDto();
                    usuarioDto.setUsuario(usuario);
                    usuarioDto.setId(ds.getKey());

                    if (!usuario.getRol().equals("Alumno") && !usuario.getRol().equals("Docente") && !usuario.getRol().equals("Administrativo") && !usuario.getRol().equals("Admin") && usuario.isEstado()) {
                        if ((busqueda != null && !busqueda.equals(""))) {
                            if (usuario.getCodigo().toLowerCase().contains(busqueda.toLowerCase())) {
                                listaUsuarios.add(usuarioDto);
                            }
                        } else {
                            listaUsuarios.add(usuarioDto);
                        }
                    }

                }

                if (listaUsuarios.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    noresultados.setVisibility(View.VISIBLE);
                    if (busqueda == null || busqueda.equals("")) {
                        noresultados.setText("No se han encontrado usuarios");
                    } else {
                        noresultados.setText("No se han encontrado usuarios de codigo \"" + busqueda + "\"");
                    }
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noresultados.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            } else {
                listaUsuarios.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }
}