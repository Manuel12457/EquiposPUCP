package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.equipospucp.DTOs.DispositivoDto;
import com.example.equipospucp.Fragments.DispositivosFragment;
import com.example.equipospucp.Fragments.DispositivosPorTipoFragment;
import com.example.equipospucp.Fragments.ProfileFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditarDispositivo extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;

    boolean tipoValido = true;
    boolean marcaValido = true;
    boolean stockValido = true;
    boolean caracteristicasValido = true;
    boolean incluyeValido = true;

    int vecestipo = 0;
    int vecesmarca = 0;
    int vecesstock = 0;
    int vecescaracteristicas = 0;
    int vecesincluye = 0;

    String tiposelected = "";
    boolean otroselected = false;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dispositivo);

        String accion = getIntent().getStringExtra("accion");
        if (accion.equals("nuevo")) {
            getSupportActionBar().setTitle("Nuevo dispositivo");
        } else {
            getSupportActionBar().setTitle("Editar dispositivo");
        }

        firebaseAuth = firebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

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
                    if (!(marca.getEditText().getText().toString() != null && !marca.getEditText().getText().toString().equals(""))) {
                        marca.setError("Ingrese una marca");
                        marcaValido = false;
                    }
                }

                if (!marca.isErrorEnabled() && vecesmarca != 0) {
                    if (!(marca.getEditText().getText().toString() != null && !marca.getEditText().getText().toString().equals(""))) {
                        marca.setError("Ingrese una marca");
                        marcaValido = false;
                    }
                }
            }
        });

        TextInputLayout caracteristicas = findViewById(R.id.inputCaracteristicas_nuevodispositivo);
        marca.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (caracteristicas.isErrorEnabled()) {
                    if (!(caracteristicas.getEditText().getText().toString() != null && !caracteristicas.getEditText().getText().toString().equals(""))) {
                        caracteristicas.setError("Ingrese las características");
                        caracteristicasValido = false;
                    }
                }

                if (!caracteristicas.isErrorEnabled() && vecescaracteristicas != 0) {
                    if (!(caracteristicas.getEditText().getText().toString() != null && !caracteristicas.getEditText().getText().toString().equals(""))) {
                        caracteristicas.setError("Ingrese las características");
                        caracteristicasValido = false;
                    }
                }
            }
        });

        TextInputLayout incluye = findViewById(R.id.inputIncluye_nuevodispositivo);
        marca.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (incluye.isErrorEnabled()) {
                    if (!(incluye.getEditText().getText().toString() != null && !incluye.getEditText().getText().toString().equals(""))) {
                        incluye.setError("Ingrese lo que incluye el dispositivo");
                        incluyeValido = false;
                    }
                }

                if (!incluye.isErrorEnabled() && vecesincluye != 0) {
                    if (!(incluye.getEditText().getText().toString() != null && !incluye.getEditText().getText().toString().equals(""))) {
                        incluye.setError("Ingrese lo que incluye el dispositivo");
                        incluyeValido = false;
                    }
                }
            }
        });

        //Agregar condicion de tiposeleccionado == "Otro"
        TextInputLayout tipo = findViewById(R.id.inputtipo_nuevodispositivo);
        marca.getEditText().addTextChangedListener(new TextWatcher() {
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
                        if (!(tipo.getEditText().getText().toString() != null && !tipo.getEditText().getText().toString().equals(""))) {
                            tipo.setError("Ingrese el tipo de dispositivo");
                            tipoValido = false;
                        }
                    }

                    if (!tipo.isErrorEnabled() && vecestipo != 0) {
                        if (!(tipo.getEditText().getText().toString() != null && !tipo.getEditText().getText().toString().equals(""))) {
                            tipo.setError("Ingrese el tipo de dispositivo");
                            tipoValido = false;
                        }
                    }
                } else {
                    tipoValido = true;
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

        ImageButton add = findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView stock = findViewById(R.id.textView_stock);
                int stockInt = Integer.parseInt(stock.getText().toString());
                stock.setText(String.valueOf(stockInt + 1));
            }
        });

        ImageButton minus = findViewById(R.id.minButton);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView stock = findViewById(R.id.textView_stock);
                int stockInt = Integer.parseInt(stock.getText().toString());
                if (stockInt != 0) {
                    stock.setText(String.valueOf(stockInt - 1));
                }
            }
        });
    }

    //MODIFICAR
    public void validarRegistroDispositivo(View view) {
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
                tipoValido = false;
            }
        } else {
            tipoValido = true;
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
            vecescaracteristicas++;
            marca.setErrorEnabled(false);
        } else {
            vecescaracteristicas++;
            caracteristicas.setError("Ingrese las características");
            caracteristicasValido = false;
        }

        if (incluye.getEditText().getText().toString() != null && !incluye.getEditText().getText().toString().equals("")) {
            //Texto ha sido ingresado en el edittext
            vecesincluye++;
            incluye.setErrorEnabled(false);
        } else {
            vecesincluye++;
            incluye.setError("Ingrese lo que incluye el dispositivo");
            incluyeValido = false;
        }

        if (Integer.parseInt(stock.getText().toString()) == 0) {
            stockValido = false;
            Log.d("msg", "VALOR STOCK IGUAL 0");
        } else {
            stockValido = true;
            Log.d("msg", "VALOR STOCK DIFERENTE DE 0");
        }

        tipoValido = (tiposelected != null && !tiposelected.equals("")) ? true : false;
        if (!tipoValido) {
            TextInputLayout spinner = findViewById(R.id.spinner_tipo);
            spinner.setError("Seleccione un tipo de dispositivo");
        }

        if (tipoValido && marcaValido && caracteristicasValido && incluyeValido && stockValido) {
            Log.d("task", "Registro valido");

            //Guardar usuario en db
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("dispositivos");
            DispositivoDto dispositivoDto = new DispositivoDto();

            if (!tiposelected.equals("Otro")) {
                dispositivoDto.setTipo(tiposelected);
            } else {
                dispositivoDto.setTipo(tipo.getEditText().getText().toString());
            }
            dispositivoDto.setFoto("");
            dispositivoDto.setMarca(marca.getEditText().getText().toString());
            dispositivoDto.setCaracteristicas(caracteristicas.getEditText().getText().toString());
            dispositivoDto.setIncluye(incluye.getEditText().getText().toString());
            dispositivoDto.setStock(Integer.parseInt(stock.getText().toString()));
            dispositivoDto.setVisible(true);

            databaseReference.push().setValue(dispositivoDto)
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(EditarDispositivo.this);
                builder.setMessage("¿Seguro que desea regresar a la pantalla de inicio? Perderá los datos ingresados");
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
        builder.setMessage("¿Seguro que desea regresar a la pantalla de inicio? Perderá los datos ingresados");
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
}