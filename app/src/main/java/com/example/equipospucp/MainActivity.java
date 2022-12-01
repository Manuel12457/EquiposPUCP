package com.example.equipospucp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.equipospucp.DTOs.UsuarioDto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Log.d("Calendar", sf.format(currentTime));

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sf.parse(sf.format(currentTime)));
            c.add(Calendar.DATE, 1);
            Log.d("Calendar", "Sumo un dia: " + sf.format(c.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (currentTime.before(c.getTime())) {
            Log.d("Calendar", "ENTRO EN IF");
        } else {
            Log.d("Calendar", "NO ENTRO EN IF");
        }


//        try {
//            Date dateTime = sf.parse("19-08-2022; 01:34:23");
//            System.out.println(dateTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        //Guardar usuario en db
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("reservas");
//        Dispositivo dispositivo = new Dispositivo();
//        UsuarioDto usuarioDto = new UsuarioDto();
//
//        dispositivo.setFoto("");
//        dispositivo.setCaracteristicas("Caracteristicas");
//        dispositivo.setIncluye("Incluye");
//        dispositivo.setMarca("Marca");
//        dispositivo.setStock(100);
//        dispositivo.setVisible(true);
//        dispositivo.setTipo("Tipo");
//
//        usuarioDto.setCorreo("correo");
//        usuarioDto.setFoto("");
//        usuarioDto.setCodigo("Codigo");
//        usuarioDto.setRol("Rol");
//
//        Reserva reserva = new Reserva();
//        reserva.setUsuario(usuarioDto);
//        reserva.setDispositivo(dispositivo);
//        reserva.setCurso("Curso");
//        reserva.setTiempoReserva(30);
//        reserva.setDni("");
//        reserva.setDetallesAdicionales("Detalles adicionales");
//        reserva.setEstado("Estado");
//        reserva.setFechayhora("Fecha y hora");
//        reserva.setLatitud("Latitud");
//        reserva.setLongitud("Longitud");
//        reserva.setMotivo("Motivo");
//        reserva.setProgramasInstalados("Programas instalados");
//
//            databaseReference.push().setValue(reserva)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Log.d("registro", "DISPOSITIVO GUARDADO");
//
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("registro", "DISPOSITIVO NO GUARDADO - " + e.getMessage());
//                        }
//                    });


    }

    public boolean verificarEstadoInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void iniciarSesion(View view){
        if (verificarEstadoInternet()) {
            Intent intent = new Intent(this, InicioSesion.class);
            startActivity(intent);
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setMessage("Verifique su conexión a internet para poder ingresar a la aplicación");
            builder.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
//            builder.setNegativeButton("Cancelar",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
            builder.show();
        }

    }

    public void registroUsuario(View view){
        if (verificarEstadoInternet()) {
            Intent intent = new Intent(this,RegistrarUsuario.class);
            startActivity(intent);
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setMessage("Verifique su conexión a internet para poder ingresar a la aplicación");
            builder.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
//            builder.setNegativeButton("Cancelar",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    });
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setMessage("¿Seguro que desea salir de la aplicación?");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
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
    protected void onStart() {
        super.onStart();

        // Si ya existe una cuenta verificada guardada, se saltea esta vista
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()){

            startActivity(new Intent(getApplicationContext(),Drawer.class));
        }
    }
}