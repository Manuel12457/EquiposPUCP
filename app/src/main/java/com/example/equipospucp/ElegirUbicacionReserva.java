package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.example.equipospucp.DTOs.Dispositivo;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ElegirUbicacionReserva extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapView;
    TextInputLayout inputDireccion;
    GoogleMap googleMap;
    String idReserva;

    String latitud;
    String longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_ubicacion_reserva);

        idReserva = getIntent().getStringExtra("idReserva");
        mapView = findViewById(R.id.mvElegirUbiReserva);

        if (permisosValidos()) {
            mapView.getMapAsync(this);
            mapView.onCreate(savedInstanceState);
        }

        Button btn = findViewById(R.id.btn_ElegirUbicacion);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((latitud != null && !latitud.equals("")) && (longitud != null && !longitud.equals(""))) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ElegirUbicacionReserva.this);
                    builder.setMessage("¿Desea confirmar la ubicación de recojo del dispositivo?");
                    builder.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    FirebaseDatabase.getInstance().getReference().child("reservas").child(idReserva)
                                            .child("latitud").setValue(latitud)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseDatabase.getInstance().getReference().child("reservas").child(idReserva)
                                                            .child("longitud").setValue(longitud)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    FirebaseDatabase.getInstance().getReference().child("reservas").child(idReserva)
                                                                            .child("estado").setValue("APROBADO")
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Intent intent = new Intent(ElegirUbicacionReserva.this,Drawer.class);
                                                                                    intent.putExtra("exito", "Reserva aprobada exitosamente");
                                                                                    startActivity(intent);
                                                                                }
                                                                            });
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                    builder.setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.show();
                } else {
                    Snackbar.make(findViewById(R.id.activity_elegir_ubicacion_reserva), "Debe elegir una ubicación antes de aprobar la reserva", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    // TODO: validar permisos
    public boolean permisosValidos() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ElegirUbicacionReserva.this);
                builder.setMessage("¿Volver a la pantalla anterior? No se aprobará la reserva");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ElegirUbicacionReserva.this,Drawer.class);
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ElegirUbicacionReserva.this);
        builder.setMessage("¿Volver a la pantalla anterior? No se aprobará la reserva");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ElegirUbicacionReserva.this,Drawer.class);
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
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng latLng = new LatLng(-12.06952386565627, -77.08019039640382);
//        MarkerOptions marker = new MarkerOptions();
//        marker.title("Lugar de recojo");
//        marker.position(latLng);
//        googleMap.addMarker(marker);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15);
        googleMap.animateCamera(update);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                latitud = String.valueOf(latLng.latitude);
                longitud = String.valueOf(latLng.longitude);
                markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                googleMap.clear();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 15
                ));
                markerOptions.title("Lugar de recojo");
                googleMap.addMarker(markerOptions);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}