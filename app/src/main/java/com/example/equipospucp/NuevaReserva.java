package com.example.equipospucp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NuevaReserva extends AppCompatActivity {

    private TextView tvNombreAlumno, tvNombreDispositivo, tvNumDias;
    private TextInputLayout inputMotivo, inputCurso, inputProgramas, inputDetalles;
    private ImageView imgDNI;
    private Bitmap bitmap;
    private Boolean conFoto = false;

    private final int PERMISOS_CODE = 100;
    private final int GALERIA_CODE = 101;
    private final int CAMARA_CODE = 102;

    CascadeClassifier classifier;
    Mat mat;
    MatOfRect rects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_reserva);

        // Se piden los permisos para la cámara
        if (checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA},PERMISOS_CODE);
        }

        // Se mapean los diferentos elementos de la vista
        tvNombreAlumno = findViewById(R.id.tvAlumno_Reserva);
        tvNombreDispositivo = findViewById(R.id.tvDispositivo_Reserva);
        tvNumDias = findViewById(R.id.tvDias_Reserva);

        inputMotivo = findViewById(R.id.inputMotivo_Reserva);
        inputCurso = findViewById(R.id.inputCurso_Reserva);
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
            imgDNI.setImageBitmap(bitmap);
            imgDNI.setScaleType(ImageView.ScaleType.CENTER_CROP);
            conFoto = true;
        }
    }
}