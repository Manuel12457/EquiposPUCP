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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.Fragments.DispositivosFragment;
import com.example.equipospucp.Fragments.EstadisticasFragment;
import com.example.equipospucp.Fragments.ProfileFragment;
import com.example.equipospucp.Fragments.ReservasFragment;
import com.example.equipospucp.Fragments.ReservasUsuarioFragment;
import com.example.equipospucp.Fragments.UsuariosFragment;
import com.example.equipospucp.Fragments.UsuariosTIFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        getSupportActionBar().setTitle("");

        String mensaje_exito = getIntent().getStringExtra("exito");
        String accion = getIntent().getStringExtra("accion");
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

        View headerView = navigationView.getHeaderView(0);
        TextView navCodigoUsuario =  headerView.findViewById(R.id.codigoUsuario);
        TextView navRolUsuario =  headerView.findViewById(R.id.rolUsuario);
        ImageView navFotoUsuario =  headerView.findViewById(R.id.imagenUsuario);

        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe

                            Usuario usuario = snapshot.getValue(Usuario.class);
                            Menu nav_Menu = navigationView.getMenu();
                            if (usuario.getRol().equals("Admin")) {
                                nav_Menu.findItem(R.id.devices_item).setVisible(false);
                                nav_Menu.findItem(R.id.reservations_item).setVisible(false);
                                nav_Menu.findItem(R.id.reservationsUser_item).setVisible(false);

                                navRolUsuario.setText("");
                                navCodigoUsuario.setText("Administrador");
                                //COLOCAR AQUI LA IMAGEN

                                if (accion != null && !accion.equals("")) {
                                    if (accion.equals("Lista usuarios TI")) {
                                        getSupportActionBar().setTitle("Usuarios TI");
                                        fragmentManager = getSupportFragmentManager();
                                        fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.container_fragment, new UsuariosTIFragment());
                                        fragmentTransaction.commit();
                                    }
                                } else {
                                    getSupportActionBar().setTitle("Perfil");
                                    fragmentManager = getSupportFragmentManager();
                                    fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container_fragment, new ProfileFragment());
                                    fragmentTransaction.commit();
                                }

                            } else if (usuario.getRol().equals("Usuario TI")) {
                                nav_Menu.findItem(R.id.usuariosti_item).setVisible(false);
                                nav_Menu.findItem(R.id.estadisticas_item).setVisible(false);
                                nav_Menu.findItem(R.id.usuarios_item).setVisible(false);
                                nav_Menu.findItem(R.id.reservationsUser_item).setVisible(false);

                                navRolUsuario.setText(usuario.getRol());
                                navCodigoUsuario.setText(usuario.getCodigo());
                                //COLOCAR AQUI LA IMAGEN

                                getSupportActionBar().setTitle("Dispositivos");
                                fragmentManager = getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container_fragment, new DispositivosFragment());
                                fragmentTransaction.commit();
                            } else {
                                nav_Menu.findItem(R.id.usuariosti_item).setVisible(false);
                                nav_Menu.findItem(R.id.estadisticas_item).setVisible(false);
                                nav_Menu.findItem(R.id.usuarios_item).setVisible(false);
                                nav_Menu.findItem(R.id.reservations_item).setVisible(false);

                                navRolUsuario.setText(usuario.getRol());
                                navCodigoUsuario.setText(usuario.getCodigo());
                                //COLOCAR AQUI LA IMAGEN

                                getSupportActionBar().setTitle("Dispositivos");
                                fragmentManager = getSupportFragmentManager();
                                fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.container_fragment, new DispositivosFragment());
                                fragmentTransaction.commit();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });
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
        } else if (item.getItemId() == R.id.reservationsUser_item) {
            getSupportActionBar().setTitle("Reservas");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new ReservasUsuarioFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.usuariosti_item) {
            getSupportActionBar().setTitle("Usuarios TI");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new UsuariosTIFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.estadisticas_item) {
            getSupportActionBar().setTitle("Estadísticas");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new EstadisticasFragment());
            fragmentTransaction.commit();
        } else if (item.getItemId() == R.id.usuarios_item) {
            getSupportActionBar().setTitle("Usuarios");
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_fragment, new UsuariosFragment());
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