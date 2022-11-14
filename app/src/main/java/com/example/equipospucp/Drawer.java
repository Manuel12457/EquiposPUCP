package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.equipospucp.Fragments.DispositivosFragment;
import com.example.equipospucp.Fragments.ProfileFragment;
import com.example.equipospucp.Fragments.ReservasFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class Drawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    Toolbar toolbar;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_dispositivos);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dispositivos");

        String mensaje_exito = getIntent().getStringExtra("exito");
        if (mensaje_exito != null && !mensaje_exito.equals("")) {
            Snackbar.make(findViewById(R.id.id_drawer), mensaje_exito, Snackbar.LENGTH_LONG).show();
        }

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_fragment, new DispositivosFragment());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (item.getItemId() == R.id.profile_item) {
            getSupportActionBar().setTitle("Perfil");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new ProfileFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.devices_item) {
            getSupportActionBar().setTitle("Dispositivos");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new DispositivosFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.reservations_item) {
            getSupportActionBar().setTitle("Reservas");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new ReservasFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.logout_item) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Drawer.this);
            builder.setMessage("¿Seguro que desea cerrar sesión?");
            builder.setPositiveButton("Aceptar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(Drawer.this,MainActivity.class);
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
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}