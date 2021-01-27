package ru.bartwell.exfilepickersample;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
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

    LinkedList<String> carpetas=new LinkedList<String>();

    private Object Constant;

    LinkedList<String>config=new LinkedList<String>();

    EditText host,extensionesPermitidas;

    CheckBox hidden,newFolder,sort;
    String datoHost = "0",datoHidden= "0",datoNewFolder= "1",datoSort="1",datoExtensionesAllow= "0";

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

            config = Metodos.leer("peridroid.dat");
        }

        catch(Exception e){

        }

    }
    public void guardarFichero(View v){

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

            arrayList1.add(new Objeto(datoHost + "«" + datoHidden + "»" + datoNewFolder + "¬" + datoSort + "═"
                    + datoExtensionesAllow));

            ObjectOutputStream escribiendoFichero = new ObjectOutputStream(
                    new FileOutputStream("peridroid.dat"));

            escribiendoFichero.writeObject(arrayList1);

            escribiendoFichero.close();

        }

        catch(Exception e){

        }

    }

    private String saberNombreCarpeta() throws IOException, JSONException {

        JSONArray imagenesBD;

        JSONObject json;

        int respuesta;

        json = Metodos.readJsonFromUrl("https://apiperiquito.herokuapp.com/recibo-json.php?imagenes=a");

        imagenesBD = json.getJSONArray("imagenes_bd");

        String carpetaSha=imagenesBD.get(0).toString();

        carpetaSha=carpetaSha.substring(0, carpetas.indexOf("."))+"_";

        return carpetaSha;

    }
    private void explorar(boolean carpeta) {

        ExFilePicker exFilePicker = new ExFilePicker();

        exFilePicker.setQuitButtonEnabled(true);

        exFilePicker.setUseFirstItemAsUpEnabled(true);

        exFilePicker.setChoiceType(ExFilePicker.ChoiceType.DIRECTORIES);

        if(Integer.parseInt(datoNewFolder)==1){
            exFilePicker.setNewFolderButtonDisabled(true);
        }

        if(Integer.parseInt(datoSort)==1){
            exFilePicker.setSortButtonDisabled(true);
        }

        if(Integer.parseInt(datoHidden)==1){
            exFilePicker.setHideHiddenFilesEnabled(true);
        }


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

        String nombreArchivo;

        String ruta;

        String extension;

        String carpetaSha = saberNombreCarpeta();

        JSONObject json;

        JSONArray imagenesBD;

        int respuesta;

        for (int i = 0; i < listaSha.size(); i++) {

            json = Metodos.readJsonFromUrl("http://server/api.php?sha256='" + listaSha.get(i)+"'");

            imagenesBD = json.getJSONArray("imagenes_bd");

            respuesta=Integer.parseInt(imagenesBD.get(0).toString());

            if(respuesta==200){

                CheckBox borrarImagenes = findViewById(R.id.borrarImagenes);

                if(borrarImagenes.isSelected()){

                    Metodos.eliminarFichero(imagenes.get(i));

                }

                else{

                    Metodos.moverArchivo(imagenes.get(i), directorioActual +"/PeriDroid/imagenes_subidas");

                }

            }

            else{

                if(imagenes.size()>10){

                    int indice=0;

                    String directorioSha;

                    for(int vueltas=0;vueltas<imagenes.size()/10;vueltas++){

                        directorioSha=carpetaSha;

                        directorioSha+=vueltas;

                        for(int x=0;x<10;x++){

                            if(indice<imagenes.size()){

                                Metodos.moverArchivo(imagenes.get(i),directorioActual +"/PeriDroid/imagenes_para_subir/"+directorioSha);

                            }

                        }

                    }

                }

                else{

                    carpetaSha = saberNombreCarpeta();

                    for(int x=0;x<imagenes.size();x++){
                        Metodos.moverArchivo(imagenes.get(i),directorioActual +"/PeriDroid/imagenes_para_subir/"+carpetaSha+1);
                    }

                }

            }

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
                for (int i = 0; i < result.getCount(); i++) {

                    System.out.println("archivo: "+result.getNames().get(i));

                }

            }
        }
    }
}
