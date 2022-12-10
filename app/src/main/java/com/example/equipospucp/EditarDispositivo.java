package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.equipospucp.Adapters.ImagenesEdicionDispositivoAdapter;
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.Image;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditarDispositivo extends AppCompatActivity implements ImagenesEdicionDispositivoAdapter.CountOfImagesWhenRemoved, ImagenesEdicionDispositivoAdapter.ItemClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;

    private Uri imageURL;
    RecyclerView recyclerView;
    ArrayList<Uri> listaImagenes = new ArrayList<>();
    Button btnGaleria;
    TextView noImagenes;
    ImagenesEdicionDispositivoAdapter adapter;
    private static final int Read_Permission = 101;
    private static final int PICK_IMAGE = 1;
    boolean masde5imagenes = false;

    boolean tipoNuevoValido = true;

    boolean tipoValido = true;
    boolean marcaValido = true;
    boolean stockValido = true;
    boolean caracteristicasValido = true;
    boolean incluyeValido = true;
    boolean cantidadFotosValido = true;

    int vecestipo = 0;
    int vecesmarca = 0;
    int vecesstock = 0;
    int vecescaracteristicas = 0;
    int vecesincluye = 0;

    String tiposelected = "";
    boolean otroselected = false;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    DispositivoDetalleDto dispositivoDetalleDto;
    String accion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dispositivo);

        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //SELECCIONAR IMAGENES
        btnGaleria = findViewById(R.id.btn_elegirImagenes);
        recyclerView = findViewById(R.id.recyclerViewImagenesEdicion);
        noImagenes = findViewById(R.id.textView_noImagenes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImagenesEdicionDispositivoAdapter(listaImagenes, this, this, this);
        recyclerView.setAdapter(adapter);

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(EditarDispositivo.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditarDispositivo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},Read_Permission);
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                //intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"),PICK_IMAGE);
            }
        });

        //SELECCIONAR IMAGENES

        TextInputLayout marca = findViewById(R.id.inputMarca_nuevodispositivo);
        marca.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (marca.isErrorEnabled()) {
                    if ((marca.getEditText().getText().toString() != null && !marca.getEditText().getText().toString().equals(""))) {
                        marca.setErrorEnabled(false);
                        marcaValido = true;
                    } else {
                        marca.setError("Ingrese una marca");
                        marcaValido = false;
                    }
                }

                if (!marca.isErrorEnabled() && vecesmarca != 0) {
                    if ((marca.getEditText().getText().toString() != null && !marca.getEditText().getText().toString().equals(""))) {
                        marca.setErrorEnabled(false);
                        marcaValido = true;
                    } else {
                        marca.setError("Ingrese una marca");
                        marcaValido = false;
                    }
                }
            }
        });

        TextInputLayout caracteristicas = findViewById(R.id.inputCaracteristicas_nuevodispositivo);
        caracteristicas.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (caracteristicas.isErrorEnabled()) {
                    if ((caracteristicas.getEditText().getText().toString() != null && !caracteristicas.getEditText().getText().toString().equals(""))) {
                        caracteristicas.setErrorEnabled(false);
                        caracteristicasValido = true;
                    } else {
                        caracteristicas.setError("Ingrese las características");
                        caracteristicasValido = false;
                    }
                }

                if (!caracteristicas.isErrorEnabled() && vecescaracteristicas != 0) {
                    if ((caracteristicas.getEditText().getText().toString() != null && !caracteristicas.getEditText().getText().toString().equals(""))) {
                        caracteristicas.setErrorEnabled(false);
                        caracteristicasValido = true;
                    } else {
                        caracteristicas.setError("Ingrese las características");
                        caracteristicasValido = false;
                    }
                }
            }
        });

        TextInputLayout incluye = findViewById(R.id.inputIncluye_nuevodispositivo);
        incluye.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (incluye.isErrorEnabled()) {
                    if ((incluye.getEditText().getText().toString() != null && !incluye.getEditText().getText().toString().equals(""))) {
                        incluye.setErrorEnabled(false);
                        incluyeValido = true;
                    } else {
                        incluye.setError("Ingrese lo que incluye el dispositivo");
                        incluyeValido = false;
                    }
                }

                if (!incluye.isErrorEnabled() && vecesincluye != 0) {
                    if ((incluye.getEditText().getText().toString() != null && !incluye.getEditText().getText().toString().equals(""))) {
                        incluye.setErrorEnabled(false);
                        incluyeValido = true;
                    } else {
                        incluye.setError("Ingrese lo que incluye el dispositivo");
                        incluyeValido = false;
                    }
                }
            }
        });

        //Agregar condicion de tiposeleccionado == "Otro"
        TextInputLayout tipo = findViewById(R.id.inputtipo_nuevodispositivo);
        tipo.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (otroselected) {
                    if (tipo.isErrorEnabled()) {
                        if ((tipo.getEditText().getText().toString() != null && !tipo.getEditText().getText().toString().equals(""))) {
                            tipo.setErrorEnabled(false);
                            tipoNuevoValido = true;
                        } else {
                            incluye.setError("Ingrese el tipo de dispositivo");
                            tipoNuevoValido = false;
                        }
                    }

                    if (!tipo.isErrorEnabled() && vecestipo != 0) {
                        if ((tipo.getEditText().getText().toString() != null && !tipo.getEditText().getText().toString().equals(""))) {
                            tipo.setErrorEnabled(false);
                            tipoNuevoValido = true;
                        } else {
                            tipo.setError("Ingrese el tipo de dispositivo");
                            tipoNuevoValido = false;
                        }
                    }
                } else {
                    tipo.setErrorEnabled(false);
                    tipoNuevoValido = true;
                }
            }
        });

        AutoCompleteTextView spinner = findViewById(R.id.idTipo);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextInputLayout spinnera = findViewById(R.id.spinner_tipo);
                spinnera.setErrorEnabled(false);

                tiposelected = spinner.getText().toString();
                if (tiposelected.equals("Otro")) {
                    tipo.setVisibility(View.VISIBLE);
                    otroselected = true;
                } else {
                    tipo.setVisibility(View.GONE);
                    otroselected = false;
                }
            }
        });

        TextView stock = findViewById(R.id.textView_stock);
        ImageButton add = findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockInt = Integer.parseInt(stock.getText().toString());
                stock.setText(String.valueOf(stockInt + 1));
            }
        });

        ImageButton minus = findViewById(R.id.minButton);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockInt = Integer.parseInt(stock.getText().toString());
                if (stockInt != 1) {
                    stock.setText(String.valueOf(stockInt - 1));
                }
            }
        });

        TextInputLayout spinnera = findViewById(R.id.spinner_tipo);
        accion = getIntent().getStringExtra("accion");
        dispositivoDetalleDto = (DispositivoDetalleDto) getIntent().getSerializableExtra("dispositivo");
        String[] some_array = getResources().getStringArray(R.array.tipos);
        if (accion.equals("nuevo")) {
            getSupportActionBar().setTitle("Nuevo dispositivo");
        } else {
            //Variables utilizadas
            stock.setText(String.valueOf(dispositivoDetalleDto.getDispositivoDto().getStock()));
            marca.getEditText().setText(dispositivoDetalleDto.getDispositivoDto().getMarca());
            caracteristicas.getEditText().setText(dispositivoDetalleDto.getDispositivoDto().getCaracteristicas());
            incluye.getEditText().setText(dispositivoDetalleDto.getDispositivoDto().getIncluye());
            //Añadimos las fotos
            firebaseDatabase.getReference().child("imagenes").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot children : snapshot.getChildren()){
                        Image image = children.getValue(Image.class);
                        if(image.getDispositivo().equals(dispositivoDetalleDto.getId())){
                            StorageReference imageRef = storageReference.child("img/"+image.getImagen());
                            Uri uri = imageRef.getDownloadUrl().getResult();
                            listaImagenes.add(uri);
                        }
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(EditarDispositivo.this, LinearLayoutManager.HORIZONTAL, false));
                    adapter = new ImagenesEdicionDispositivoAdapter(listaImagenes, EditarDispositivo.this, EditarDispositivo.this, EditarDispositivo.this);
                    recyclerView.setAdapter(adapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            spinnera.setEnabled(false);
            spinner.setEnabled(false);

            if (dispositivoDetalleDto.getDispositivoDto().getTipo().equals("Laptop")) {
                tiposelected = "Laptop";
                spinner.setText(some_array[0],false);
            } else if (dispositivoDetalleDto.getDispositivoDto().getTipo().equals("Monitor")) {
                tiposelected = "Monitor";
                spinner.setText(some_array[1],false);
            } else if (dispositivoDetalleDto.getDispositivoDto().getTipo().equals("Celular")) {
                tiposelected = "Celular";
                spinner.setText(some_array[2],false);
            } else if (dispositivoDetalleDto.getDispositivoDto().getTipo().equals("Tablet")) {
                tiposelected = "Tablet";
                spinner.setText(some_array[3],false);
            } else {
                spinner.setText(some_array[4],false);
                tiposelected = "Otro";
                tipo.setVisibility(View.VISIBLE);
                tipo.getEditText().setText(dispositivoDetalleDto.getDispositivoDto().getTipo());
                tipo.setEnabled(false);
            }

            getSupportActionBar().setTitle("Editar dispositivo");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            if (data.getClipData() != null) {
                int countOfImages = data.getClipData().getItemCount();
                for (int i = 0; i < countOfImages; i++) {
                    imageURL = data.getClipData().getItemAt(i).getUri();
                    listaImagenes.add(imageURL);
                }
            } else {
                imageURL = data.getData();
                listaImagenes.add(imageURL);
            }
            noImagenes.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        } else {
            noImagenes.setVisibility(View.VISIBLE);
        }
    }

    //MODIFICAR
    public void validarRegistroDispositivo(View view) {

        Log.d("seleccionado", "Seleccionado Otro: " + otroselected);

        TextInputLayout tipo = findViewById(R.id.inputtipo_nuevodispositivo);
        TextInputLayout marca = findViewById(R.id.inputMarca_nuevodispositivo);
        TextInputLayout caracteristicas = findViewById(R.id.inputCaracteristicas_nuevodispositivo);
        TextInputLayout incluye = findViewById(R.id.inputIncluye_nuevodispositivo);

        TextView stock = findViewById(R.id.textView_stock);

        if (otroselected) {
            if (tipo.getEditText().getText().toString() != null && !tipo.getEditText().getText().toString().equals("")) {
                //Texto ha sido ingresado en el edittext
                vecestipo++;
                tipo.setErrorEnabled(false);
            } else {
                vecestipo++;
                tipo.setError("Ingrese el tipo de dispositivo");
                tipoNuevoValido = false;
            }
        } else {
            tipoNuevoValido = true;
        }

        if (marca.getEditText().getText().toString() != null && !marca.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            vecesmarca++;
            marca.setErrorEnabled(false);
        } else {
            vecesmarca++;
            marca.setError("Ingrese una marca");
            marcaValido = false;
        }

        if (caracteristicas.getEditText().getText().toString() != null && !caracteristicas.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            if (caracteristicas.getEditText().getText().toString().length() > 500) {
                vecescaracteristicas++;
                caracteristicas.setError("Las características no pueden exceder de los 500 caracteres");
                caracteristicasValido = false;
            } else {
                vecescaracteristicas++;
                marca.setErrorEnabled(false);
            }
        } else {
            vecescaracteristicas++;
            caracteristicas.setError("Ingrese las características");
            caracteristicasValido = false;
        }

        if (incluye.getEditText().getText().toString() != null && !incluye.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            if (incluye.getEditText().getText().toString().length() > 500) {
                vecesincluye++;
                incluye.setError("Lo que incluye no pueden exceder de los 500 caracteres");
                incluyeValido = false;
            } else {
                vecesincluye++;
                incluye.setErrorEnabled(false);
            }
        } else {
            vecesincluye++;
            incluye.setError("Ingrese lo que incluye el dispositivo");
            incluyeValido = false;
        }

        tipoValido = (tiposelected != null && !tiposelected.equals("")) ? true : false;
        if (!tipoValido) {
            TextInputLayout spinner = findViewById(R.id.spinner_tipo);
            spinner.setError("Seleccione un tipo de dispositivo");
        }

        Log.d("seleccionado", "tipoValido: " + tipoNuevoValido);

        if (listaImagenes.size()>=3) {
            cantidadFotosValido = true;
        } else {
            Snackbar.make(view.findViewById(R.id.activity_editar_dispositivo), "Debe seleccionar un mínimo de 3 imágenes", Snackbar.LENGTH_LONG).show();
            cantidadFotosValido = false;
        }

        if (tipoValido && marcaValido && caracteristicasValido && incluyeValido && stockValido && tipoNuevoValido && cantidadFotosValido) {
            Log.d("task", "Registro valido");

            //Guardar usuario en db
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("dispositivos");
            Dispositivo dispositivo = new Dispositivo();

            if (!tiposelected.equals("Otro")) {
                dispositivo.setTipo(tiposelected);
            } else {
                dispositivo.setTipo(tipo.getEditText().getText().toString());
            }
            dispositivo.setFoto("");
            dispositivo.setMarca(marca.getEditText().getText().toString());
            dispositivo.setCaracteristicas(caracteristicas.getEditText().getText().toString());
            dispositivo.setIncluye(incluye.getEditText().getText().toString());
            dispositivo.setStock(Integer.parseInt(stock.getText().toString()));
            dispositivo.setVisible(true);

            //AQUI NO OLVIDARSE DE LA SUBIDA DE LAS IMAGENES DE LA LISTA DE IMAGENES
            if (accion.equals("nuevo")) {
                String key = databaseReference.push().getKey();
                for (Uri imageUri: listaImagenes) {
                    if (imageUri != null){
                        String[] path= imageUri.toString().split("/");
                        String filename = path[path.length-1];
                        StorageReference imageReference = storageReference.child("img/"+filename);
                        imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                            HashMap<String,String> map = new HashMap<>();
                            map.put("dispositivo",key);
                            map.put("imagen",filename);
                            databaseReference.child("imagenes").push().setValue(map);
                        });
                    }
                }
                databaseReference.child(key).setValue(dispositivo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("registro", "DISPOSITIVO GUARDADO");
                                Intent intent = new Intent(EditarDispositivo.this, Drawer.class);
                                intent.putExtra("exito", "El dispositivo se ha guardado exitosamente");
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("registro", "DISPOSITIVO NO GUARDADO - " + e.getMessage());
                            }
                        });
            } else {
                databaseReference.child(dispositivoDetalleDto.getId()).setValue(dispositivo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Eliminar Todas las fotos en database
                                databaseReference.child("imagenes").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot children : snapshot.getChildren()){
                                            Image image = children.getValue(Image.class);
                                            /**Validamos que el dispositivo vinculado a la imagen sea el mismo que esta siendo editado
                                             * en caso que lo sea se procede eliminar tanto la imagen del firebase storage como en la relacion
                                             * Dispositivo - imagen guardados**/
                                            if (image.getDispositivo().equals(dispositivoDetalleDto.getId())){
                                                //Se procede a eliminar la imagen
                                                storageReference.child("img/"+image.getImagen()).delete();
                                                databaseReference.child("imagenes").child(children.getKey()).removeValue();
                                            }
                                        }
                                        Toast.makeText(EditarDispositivo.this,"Imagenes eliminadas se procedera a subir las nuevsa imagenes",Toast.LENGTH_SHORT);
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {}
                                });
                                //Se guardan todas las fotos que estan en listaImagenes
                                for (Uri imageUri: listaImagenes) {
                                    if (imageUri != null){
                                        String[] path= imageUri.toString().split("/");
                                        String filename = path[path.length-1];
                                        StorageReference imageReference = storageReference.child("img/"+filename);
                                        imageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                                            HashMap<String,String> map = new HashMap<>();
                                            map.put("dispositivo",dispositivoDetalleDto.getId());
                                            map.put("imagen",filename);
                                            databaseReference.child("imagenes").push().setValue(map);
                                        });
                                    }
                                }
                                Log.d("registro", "DISPOSITIVO GUARDADO");
                                Intent intent = new Intent(EditarDispositivo.this, Drawer.class);
                                intent.putExtra("exito", "El dispositivo se ha editado exitosamente");
                                startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("registro", "DISPOSITIVO NO GUARDADO - " + e.getMessage());
                            }
                        });
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditarDispositivo.this);
                if (accion.equals("nuevo")) {
                    builder.setMessage("¿Volver a la pantalla anterior? Perderá los datos ingresados");
                } else {
                    builder.setMessage("¿Volver a la pantalla anterior? Perderá los datos editados");
                }
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(EditarDispositivo.this,Drawer.class);
                                startActivity(intent);
                            }
                        });
                builder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditarDispositivo.this);
        if (accion.equals("nuevo")) {
            builder.setMessage("¿Volver a la pantalla anterior? Perderá los datos ingresados");
        } else {
            builder.setMessage("¿Volver a la pantalla anterior? Perderá los datos editados");
        }
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(EditarDispositivo.this,Drawer.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    @Override
    public void clicked(int getSize) {
//        if (listaImagenes.size() != 0) {
//            if (listaImagenes.size() == 1) {
//                cantidadImagenes.setText(listaImagenes.size() + " imagen seleccionada");
//            } else {
//                cantidadImagenes.setText(listaImagenes.size() + " imágenes seleccionadas");
//            }
//        } else {
//            noImagenes.setVisibility(View.VISIBLE);
//            cantidadImagenes.setText("");
//        }
    }

    @Override
    public void itemClick(int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_zoom);
        ImageView imagenDialog = dialog.findViewById(R.id.imageView2);
        imagenDialog.setImageURI(listaImagenes.get(position));
        dialog.show();
    }
}