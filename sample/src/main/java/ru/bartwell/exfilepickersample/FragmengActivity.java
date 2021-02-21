package ru.bartwell.exfilepickersample;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ru.bartwell.exfilepickersample.util.Metodos;
import ru.bartwell.exfilepickersample.util.Objeto;

public class FragmengActivity extends AppCompatActivity {
    EditText host,extensionesPermitidas;

    CheckBox hidden,newFolder,sort;
    String datoHost = "0",datoHidden= "0",datoNewFolder= "1",datoSort="1",datoExtensionesAllow= "0";
    public static final String EXTRA_MESSAGE = "";

    RelativeLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragmeng);

        mRootLayout = (RelativeLayout) findViewById(R.id.rootlayout);

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction transaction = manager.beginTransaction();

        FileFragment fileFragment = FileFragment.newInstance();

        transaction.replace(R.id.rootlayout, fileFragment);

        transaction.commit();

    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }

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
}