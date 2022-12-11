package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ElegirUbicacionReserva extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapView;
    TextInputLayout inputDireccion;
    GoogleMap googleMap;
    String idReserva;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_ubicacion_reserva);

        idReserva = getIntent().getStringExtra("idReserva");

        inputDireccion = findViewById(R.id.inputDireccionReserva);
        mapView = findViewById(R.id.mvElegirUbiReserva);

        if (permisosValidos()){
            mapView.getMapAsync(this);
            mapView.onCreate(savedInstanceState);

            inputDireccion.getEditText().setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    Geocoder geocoder = new Geocoder(ElegirUbicacionReserva.this, Locale.getDefault());
                    String direccion = inputDireccion.getEditText().getText().toString().trim();

                    try {
                        List<Address> listaDirecciones =geocoder.getFromLocationName(direccion,1);
                        if (listaDirecciones.size()>0){
                            LatLng latLng = new LatLng(listaDirecciones.get(0).getLatitude(),listaDirecciones.get(0).getLongitude());
                            MarkerOptions marker = new MarkerOptions();
                            marker.title("Lugar de recojo");
                            marker.position(latLng);
                            googleMap.addMarker(marker);
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,5);
                            googleMap.animateCamera(update);

                            return true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });
        }
    }

    // TODO: validar permisos
    public boolean permisosValidos () {
        return true;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng latLng = new LatLng(0,0);
        MarkerOptions marker = new MarkerOptions();
        marker.title("Lugar de recojo");
        marker.position(latLng);
        googleMap.addMarker(marker);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng,5);
        googleMap.animateCamera(update);

    }

    @Override
    protected void onStart() {super.onStart();mapView.onStart();}
    @Override
    protected void onResume() {super.onResume();mapView.onResume();}
    @Override
    protected void onPause() {super.onPause();mapView.onPause();}
    @Override
    protected void onStop() {super.onStop();mapView.onStop();}
    @Override
    protected void onDestroy() {super.onDestroy();mapView.onDestroy();}
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory() {super.onLowMemory(); mapView.onLowMemory();}
}