package ru.bartwell.exfilepickersample;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;
import ru.bartwell.exfilepickersample.util.Metodos;
import ru.bartwell.exfilepickersample.util.Objeto;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_PERMISSIONS = 1;

    private static final int EX_FILE_PICKER_RESULT = 0;

    private static final String EXTRA_MESSAGE = "";

    String directorioActual;

    TextView numeroImagenes;

    LinkedList<String> listaSha=new LinkedList<String>();

    LinkedList<String> imagenes=new LinkedList<String>();

    LinkedList<String> rutaimagenes=new LinkedList<String>();

    LinkedList<String> carpetas=new LinkedList<String>();

    private Object Constant;

    LinkedList<String>config=new LinkedList<String>();

    EditText host,extensionesPermitidas;

    CheckBox hidden,newFolder,sort;
    String datoHost = "0",datoHidden= "0",datoNewFolder= "1",datoSort="1",datoExtensionesAllow= "0";

    public void guardarConfiguracion(View v){

        try {

            ArrayList<Objeto> arrayList1 = new ArrayList<Objeto>();

            host=findViewById(R.id.host);

            datoHost=host.getText().toString();

            hidden=findViewById(R.id.ocultarArchivos);

            datoHidden="0";

            if(hidden.isSelected()){
                datoHidden="1";
            }

            newFolder=findViewById(R.id.crearCarpeta);

            datoNewFolder="0";

            if(newFolder.isSelected()){
                datoNewFolder="1";
            }

            sort=findViewById(R.id.activarSort);

            datoSort="0";

            if(sort.isSelected()){
                datoSort="1";
            }

            extensionesPermitidas=findViewById(R.id.extensiones);

            datoExtensionesAllow=extensionesPermitidas.getText().toString();

            datoHost= Metodos.eliminarEspacios(datoHost);

            arrayList1.add(new Objeto(datoHost + " " + datoHidden + " " + datoNewFolder + " " + datoSort + " "
                    + datoExtensionesAllow));

            ObjectOutputStream escribiendoFichero = new ObjectOutputStream(
                    new FileOutputStream("/storage/emulated/0/peridroid.dat"));

            escribiendoFichero.writeObject(arrayList1);

            escribiendoFichero.close();

        }

        catch(Exception e){
e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_PERMISSIONS);
        }

        try {

            directorioActual="/storage/emulated/0";

            String ruta = directorioActual +"/PeriDroid";

            File directorio = new File(ruta);

            directorio.mkdir();

            ruta =directorioActual +"/PeriDroid"+"/imagenes_subidas";

            directorio = new File(ruta);

            directorio.mkdir();

            ruta =directorioActual +"/PeriDroid"+"/imagenes_para_subir";

            directorio = new File(ruta);

            directorio.mkdir();

        }

        catch (Exception e) {

        }

        setContentView(R.layout.activity_main);

        try {

            config = Metodos.leer("/storage/emulated/0/peridroid.dat");
        }

        catch(Exception e){
e.printStackTrace();
        }

    }


    private String saberNombreCarpeta() throws IOException, JSONException, InterruptedException {

        return consultaJson("https://apiperiquito.herokuapp.com/recibo-json.php?imagenes=a","imagenes_bd");

    }

    private String consultaJson(final String api, final String campo) throws InterruptedException {
        final String[] carpetaSha = {""};

        try  {

            Thread thread = new Thread(new Runnable() {

                @Override

                public void run() {

                    JSONArray imagenesBD;

                    JSONObject json;

                    try {

                        json = Metodos.readJsonFromUrl(api);

                        imagenesBD = json.getJSONArray(campo);

                        carpetaSha[0] = imagenesBD.get(0).toString();


                        carpetaSha[0] = carpetaSha[0].substring(0, carpetaSha[0].indexOf(".")) + "_";

                        carpetas.clear();

                        carpetas.add(carpetaSha[0]);

                    }  catch (Exception e) {

                    }

                }

            });

            thread.start();

        }

        catch (Exception e) {

        }

        return carpetas.getLast();

    }

    private void explorar(boolean carpeta) {

        ExFilePicker exFilePicker = new ExFilePicker();

        exFilePicker.setQuitButtonEnabled(true);

        exFilePicker.setUseFirstItemAsUpEnabled(true);

        exFilePicker.setChoiceType(ExFilePicker.ChoiceType.DIRECTORIES);

       /* if(Integer.parseInt(datoNewFolder)==1){
            exFilePicker.setNewFolderButtonDisabled(true);
        }

        if(Integer.parseInt(datoSort)==1){
            exFilePicker.setSortButtonDisabled(true);
        }

        if(Integer.parseInt(datoHidden)==1){
            exFilePicker.setHideHiddenFilesEnabled(true);
        }
*/

// explode                + datoExtensionesAllow)

       /*if (((AppCompatCheckBox) findViewById(R.id.filter_listed)).isChecked()) {
            exFilePicker.setShowOnlyExtensions("jpg", "jpeg");
        }
*/
        if(carpeta) {
            exFilePicker.setChoiceType(ExFilePicker.ChoiceType.DIRECTORIES);
        }

        else{
            exFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
        }

        exFilePicker.start(this, EX_FILE_PICKER_RESULT);

    }

    public void comprobarSha(View v) throws JSONException, IOException {

        try {

            config = Metodos.leer("/storage/emulated/0/peridroid.dat");

            if(config.isEmpty()){
                Toast.makeText(getApplicationContext(),"Por favor, configura el servidor",Toast.LENGTH_SHORT).show();
            }
            else {

                final String[] carpetaSha = {saberNombreCarpeta()};

                if (!carpetaSha[0].isEmpty()) {

                    String nombreArchivo;

                    String ruta;

                    String extension;

                    Metodos.crearCarpeta(directorioActual + "/PeriDroid/imagenes_para_subir/" + carpetaSha[0]);
                    
                    int resultado;

                    final int[] status = new int[1];

                    for (int i = 0; i < config.size(); i++) {
                        System.out.println("dato: " + config.get(i));
                    }

                    for (int i = 0; i < listaSha.size(); i++) {


                        int finalContador = i;

                        Thread thread = new Thread(new Runnable() {

                            @Override

                            public void run() {

                                try {

                                    URL url = new URL(config.get(0)+"/api/api.php?sha256='" +Metodos.getSHA256Checksum(listaSha.get(finalContador)) + "'");

                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                                    connection.setDoOutput(true);

                                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                                    connection.setRequestMethod("GET");

                                    String line;

                                    status[0] = connection.getResponseCode();
                                    System.out.println("RESPUESTA: "+status[0]);
                                    System.out.println("archivo: "+imagenes.get(finalContador));
                                    if (status[0] == 200) {

                                        CheckBox borrarImagenes = findViewById(R.id.borrarImagenes);

                                        if (borrarImagenes.isSelected()) {

                                           // Metodos.eliminarFichero(imagenes.get(finalContador));

                                        } else {

                                           // Metodos.moverArchivo(imagenes.get(finalContador), directorioActual + "/PeriDroid/imagenes_subidas");

                                        }

                                    }

                                    else {

                                        if (imagenes.size() > 10) {

                                            int indice = 0;

                                            String directorioSha;

                                            for (int vueltas = 0; vueltas < imagenes.size() / 10; vueltas++) {

                                                directorioSha = carpetaSha[0];

                                                directorioSha += vueltas;

                                                for (int x = 0; x < 10; x++) {

                                                    if (indice < imagenes.size()) {

                                                       // Metodos.moverArchivo(imagenes.get(x), directorioActual + "/PeriDroid/imagenes_para_subir/" + directorioSha+"/"+imagenes.get(x));

                                                    }

                                                }

                                            }

                                        } else {

                                            for (int x = 0; x < imagenes.size(); x++) {

                                                System.out.println("MUEVO: "+rutaimagenes.get(x)+ " A "+ directorioActual + "/PeriDroid/imagenes_para_subir/" + carpetaSha[0]+"/"+imagenes.get(x));

                                                Metodos.moverArchivo(new File(rutaimagenes.get(x)),new File(directorioActual + "/PeriDroid/imagenes_para_subir/" + carpetaSha[0]+"/"+imagenes.get(x)));
                                            }

                                        }

                                    }

                                }

                                catch (Exception e1) {
                                    e1.printStackTrace();
                                }

                            }

                        });

                        thread.start();


                    }

                }

            }
        }

        catch (Exception e){
            e.printStackTrace();
        }

    }

    public void abrirCarpeta( View v) {

        explorar(true);

    }

    public void config(View v){

        setContentView(R.layout.activity_fragmeng);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(FragmengActivity.EXTRA_MESSAGE);

    }

    public void atras(View v){

        try {

            setContentView(R.layout.activity_main);

            Intent intent = getIntent();

            String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        }

        catch (Exception e){

        }

    }

    public void abrirArchivo( View v) {

        explorar(false);

    }

    public void limpiar(View v){
        rutaimagenes.clear();
        imagenes.clear();

        carpetas.clear();

        listaSha.clear();

        if (numeroImagenes!=null && !numeroImagenes.getText().toString().isEmpty()) {

            numeroImagenes.setText("");

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == EX_FILE_PICKER_RESULT) {

            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);

            if (result != null && result.getCount() > 0) {

                StringBuilder stringBuilder = new StringBuilder();

                String archivo;

                for (int i = 0; i < result.getCount(); i++) {

                    archivo=result.getPath()+result.getNames().get(i);

                    if(!listaSha.contains(archivo)) {

                        imagenes.add(result.getNames().get(i));

                        rutaimagenes.add(archivo);

                        listaSha.add(Metodos.getSHA256Checksum(archivo));

                    }

                }

            }

        }

    }

}
