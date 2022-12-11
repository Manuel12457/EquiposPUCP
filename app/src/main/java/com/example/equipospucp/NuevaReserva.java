package com.example.equipospucp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.equipospucp.DTOs.DispositivoDetalleDto;
import com.example.equipospucp.DTOs.Reserva;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
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

        // Se mapean los elementos de firebase
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference("reservas");
        imgRef = FirebaseStorage.getInstance().getReference("/img");

        dispositivoDetalleDto = (DispositivoDetalleDto) getIntent().getSerializableExtra("dispositivo");

        // Se piden los permisos para la cámara
        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},PERMISOS_CODE);
        }

        // Se mapean los diferentos elementos de la vista
        tvNombreAlumno = findViewById(R.id.tvAlumno_Reserva);
        tvNombreDispositivo = findViewById(R.id.tvDispositivo_Reserva);
        tvNumDias = findViewById(R.id.tvDias_Reserva);

        inputMotivo = findViewById(R.id.inputMotivo_Reserva);
        inputCurso = findViewById(R.id.inputDireccionReserva);
        inputProgramas = findViewById(R.id.inputProgramas_Reserva);
        inputDetalles = findViewById(R.id.inputDetallesAdicionales_Reserva);

        imgDNI = findViewById(R.id.imageViewDNI);

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

    public void cambiarDias(View view){
        Integer nuevoDia = Integer.parseInt(tvNumDias.getText().toString()) + (Integer) view.getTag();

        if (nuevoDia>=1 || nuevoDia<=30){
            tvNumDias.setText(nuevoDia.toString());
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
        reserva.setFechayhora(LocalDateTime.now().toString());
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
                    Intent intent = new Intent(NuevaReserva.this, Drawer.class);
                    intent.putExtra("exito", "La reserva se ha realizado con éxito");
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(NuevaReserva.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
            inputProgramas.setError("Debe especificar una lista de programas");
            valido = false;
        }
        if (detalles.equals("")){
            inputMotivo.setError("Deben especificarse detalles adcionales del ispositivvo");
            valido = false;
        }
        if (!conFoto){
            Toast.makeText(NuevaReserva.this, "Debe ingresar una foto para continuar con la reservas", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        return valido;
    }
}