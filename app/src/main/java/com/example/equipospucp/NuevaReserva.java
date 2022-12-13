package com.example.equipospucp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.equipospucp.DTOs.Dispositivo;
import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Reserva;
import com.example.equipospucp.DTOs.Usuario;
import com.example.equipospucp.Fragments.DispositivosFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class NuevaReserva extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference ref;
    StorageReference imgRef;

    private TextView tvNombreAlumno, tvNombreDispositivo, tvNumDias;
    private TextInputLayout inputMotivo, inputCurso, inputProgramas, inputDetalles;
    private FloatingActionButton fab;
    private ImageView imgDNI;
    private Bitmap bitmap;
    private Boolean conFoto = false;

    private final int PERMISOS_CODE = 100;
    private final int GALERIA_CODE = 101;
    private final int CAMARA_CODE = 102;

    CascadeClassifier classifier;
    Mat mat;
    MatOfRect rects;

    DispositivoDetalleDto dispositivoDetalleDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_reserva);
        getSupportActionBar().setTitle("Nueva reserva");

        // Se mapean los elementos de firebase
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("reservas");
        imgRef = FirebaseStorage.getInstance().getReference("/img");

        dispositivoDetalleDto = (DispositivoDetalleDto) getIntent().getSerializableExtra("dispositivo");

        tvNombreAlumno = findViewById(R.id.tvAlumno_Reserva);
        tvNombreDispositivo = findViewById(R.id.tvDispositivo_Reserva);
        FirebaseDatabase.getInstance().getReference("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            Usuario usuario = snapshot.getValue(Usuario.class);
                            tvNombreAlumno.setText("Hecha por:\n" + usuario.getRol() + " " + usuario.getCodigo());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });
        FirebaseDatabase.getInstance().getReference("dispositivos").child(dispositivoDetalleDto.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        System.out.println("ONDATACHANGE - AFUERA DEL IF");
                        if (snapshot.exists()) { //Nodo referente existe
                            Dispositivo dispositivo = snapshot.getValue(Dispositivo.class);
                            tvNombreDispositivo.setText("Dispositivo a reservar:\n" + dispositivo.getTipo() + " " + dispositivo.getMarca());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("ONCANCELLED");
                        Log.e("msg", "Error onCancelled", error.toException());
                    }
                });

        // Se piden los permisos para la cámara
        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},PERMISOS_CODE);
        }

        // Se mapean los diferentos elementos de la vista
        tvNumDias = findViewById(R.id.tvDias_Reserva);

        inputMotivo = findViewById(R.id.inputMotivo_Reserva);
        inputCurso = findViewById(R.id.inputCurso_Reserva);
        inputProgramas = findViewById(R.id.inputProgramas_Reserva);
        inputDetalles = findViewById(R.id.inputDetallesAdicionales_Reserva);

        imgDNI = findViewById(R.id.imageViewDNI);

        ImageButton add = findViewById(R.id.addButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockInt = Integer.parseInt(tvNumDias.getText().toString());
                if (stockInt != 30) {
                    tvNumDias.setText(String.valueOf(stockInt + 1));
                }
            }
        });

        ImageButton minus = findViewById(R.id.minButton);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockInt = Integer.parseInt(tvNumDias.getText().toString());
                if (stockInt != 1) {
                    tvNumDias.setText(String.valueOf(stockInt - 1));
                }
            }
        });

        // Se obtiene el clasificador para identificar la cara en la imagen
        if (OpenCVLoader.initDebug()){

            File file = new File(getDir("cascade",MODE_PRIVATE),"cascade_frontalface.xml");
            try (InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                 FileOutputStream fos = new FileOutputStream(file)){

                byte[] data = new byte[4096];
                int readBytes;

                while ((readBytes = is.read(data))!=-1){
                    fos.write(data,0,readBytes);
                }

                classifier = new CascadeClassifier(file.getAbsolutePath());
                if (classifier.empty()) classifier = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
            file.delete();
        }

    }

    public void obtenerImagen(View view){
        new MaterialAlertDialogBuilder(NuevaReserva.this)
                .setTitle("Obtener Imagen")
                .setMessage("Elija de donde quiere obtener su imagen")
                .setNegativeButton("Galeria",((dialogInterface, i) -> {

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent,GALERIA_CODE);
                    dialogInterface.dismiss();

                })).setNeutralButton("Cancelar",((dialogInterface, i) -> {
                    dialogInterface.cancel();
                })).setPositiveButton("Cámara", ((dialogInterface, i) -> {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,CAMARA_CODE);
                    dialogInterface.dismiss();

                })).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mat = new Mat();
        rects = new MatOfRect();

        // Se mapean los resultados para ambos casos
        if(requestCode == GALERIA_CODE && data != null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());

                // Se le hace blur a la imagen
                Utils.bitmapToMat(bitmap,mat);
                classifier.detectMultiScale(mat,rects,1.1,2);
                for (Rect rect : rects.toList()){
                    Mat areaBlur = mat.submat(rect);
                    Imgproc.blur(areaBlur,areaBlur,new Size(10,10));
                    areaBlur.release();
                }
                Utils.matToBitmap(mat,bitmap);

                // Se pone en el imgView
                imgDNI.setImageBitmap(bitmap);
                imgDNI.setScaleType(ImageView.ScaleType.CENTER_CROP);
                conFoto = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMARA_CODE && data != null){
            bitmap = (Bitmap) data.getExtras().get("data");

            // Se le hace blur a la imagen
            Utils.bitmapToMat(bitmap,mat);
            classifier.detectMultiScale(mat,rects,1.1,2);
            for (Rect rect : rects.toList()){
                Mat areaBlur = mat.submat(rect);
                Imgproc.blur(areaBlur,areaBlur,new Size(10,10));
                areaBlur.release();
            }
            Utils.matToBitmap(mat,bitmap);

            // Se pone en el imgView
            imgDNI.setImageBitmap(bitmap);
            imgDNI.setScaleType(ImageView.ScaleType.CENTER_CROP);
            conFoto = true;
        }
    }

    public void hacerReserva(View view){
        String motivo = inputMotivo.getEditText().getText().toString().trim();
        String curso = inputCurso.getEditText().getText().toString().trim();
        String tiempoReserva = tvNumDias.getText().toString().trim();
        String programas = inputProgramas.getEditText().getText().toString().trim();
        String detalles = inputDetalles.getEditText().getText().toString().trim();

        if(!camposValidos(motivo,curso,tiempoReserva,programas,detalles)) return;

        Reserva reserva = new Reserva();
        reserva.setIdUsuario(auth.getCurrentUser().getUid());
        reserva.setIddispositivo(dispositivoDetalleDto.getId());

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        reserva.setFechayhora(sf.format(currentTime));

        reserva.setMotivo(motivo);
        reserva.setCurso(curso);
        reserva.setTiempoReserva(Integer.parseInt(tiempoReserva));
        reserva.setProgramasInstalados(programas);
        reserva.setDetallesAdicionales(detalles);
        reserva.setEstado("PENDIENTE");

        Bitmap bitmapFoto = ((BitmapDrawable) imgDNI.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapFoto.compress(Bitmap.CompressFormat.WEBP,50,baos);

        String nombre = UUID.randomUUID().toString()+".webp";
        reserva.setDni(nombre);

        imgRef.child(nombre).putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> {
            ref.push().setValue(reserva).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance().getReference().child("dispositivos").child(dispositivoDetalleDto.getId())
                            .child("stock").setValue(dispositivoDetalleDto.getDispositivoDto().getStock()-1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Intent intent = new Intent(NuevaReserva.this, Drawer.class);
                                    intent.putExtra("exito", "La reserva se ha realizado con éxito");
                                    startActivity(intent);
                                    finish();
                                }
                            });
                } else {
                    Snackbar.make(findViewById(R.id.activity_nueva_reserva), task.getException().getMessage() ,Snackbar.LENGTH_LONG).show();
                    //Toast.makeText(NuevaReserva.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("NuevaReserva", task.getException().getMessage());
                }
            });
        });
    }

    public boolean camposValidos(String motivo, String curso, String tiempoReserva, String programas, String detalles){
        boolean valido = true;

        // Se limpian los errores previos
        inputMotivo.setError(null);
        inputCurso.setError(null);
        inputProgramas.setError(null);
        inputDetalles.setError(null);

        // Si hay un error lo notifica al usuario
        if (motivo.equals("")){
            inputMotivo.setError("El motivo no puede estar vacío");
            valido = false;
        }
        if (curso.equals("")){
            inputCurso.setError("El curso no puede estar vacío");
            valido = false;
        }
        if (programas.equals("")){
            inputProgramas.setError("Ingrese una lista de los programas que deben instalarse");
            valido = false;
        }
        if (detalles.equals("")){
            inputDetalles.setError("Ingrese detalles adicionales de su reserva");
            valido = false;
        }
        if (!conFoto){
            Snackbar.make(findViewById(R.id.activity_nueva_reserva), "Debe ingresar una foto de su DNI para realizar su reserva", Snackbar.LENGTH_LONG).show();
            valido = false;
        }

        return valido;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NuevaReserva.this);
                builder.setMessage("¿Volver a la pantalla anterior? No se realizará su reserva");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(NuevaReserva.this,Drawer.class);
                                startActivity(intent);
                                finish();
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NuevaReserva.this);
        builder.setMessage("¿Volver a la pantalla anterior? No se realizará su reserva");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(NuevaReserva.this,Drawer.class);
                        startActivity(intent);
                        finish();
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