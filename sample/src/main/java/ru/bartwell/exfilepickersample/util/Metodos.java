package ru.bartwell.exfilepickersample.util;

import android.annotation.SuppressLint;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;

public abstract class Metodos {

    public static LinkedList<String> leer(String file)
            throws IOException, FileNotFoundException, ClassNotFoundException {

        LinkedList<String> arrayList2 = new LinkedList<String>();

        try {

            File archivo = new File(file);

            if (archivo.exists()) {

                ObjectInputStream leyendoFichero = new ObjectInputStream(new FileInputStream(file));

                arrayList2.add(leyendoFichero.readObject().toString());

                leyendoFichero.close();

            }

        }

        catch (Exception e) {
        }

        return arrayList2;

    }

    private static String readAll(Reader rd) throws IOException {

        StringBuilder sb = new StringBuilder();

        int cp;

        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.toString();
    }

    public static String eliminarEspacios(String cadena) {

        cadena = cadena.trim();

        cadena = cadena.replace("  ", " ");

        cadena = cadena.trim();

        return cadena;
    }

    @SuppressLint("NewApi")
    public static void moverArchivo(String origen, String destino) {

        try {

            Files.move(FileSystems.getDefault().getPath(origen), FileSystems.getDefault().getPath(destino),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        catch (IOException e) {
            //
        }

    }
    public static void eliminarFichero(String archivo) {

        File fichero = new File(archivo);

        if (fichero.exists() && !fichero.isDirectory()) {
            fichero.delete();
        }

    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {

        InputStream is = new URL(url).openStream();

        BufferedReader rd = null;

        String jsonText ="";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            jsonText = readAll(rd);

            is.close();

        }

        return new JSONObject(jsonText);

    }

    public static void convertir(String carpeta) {

        conversion("jpeg", "jpg", carpeta);

        conversion("JPEG", "jpg", carpeta);

        conversion("JPG", "jpg", carpeta);

        conversion("PNG", "png", carpeta);

        conversion("webp", "png", carpeta);

        conversion("GIF", "gif", carpeta);

    }
    public static void conversion(String extension, String salida, String carpeta) {

        LinkedList<String> listaImagenes = directorio(carpeta, extension, true, false);

        int resto = 3;

        if (extension.length() == 4) {
            resto = 5;
        }

        for (int i = 0; i < listaImagenes.size(); i++) {

            File f1 = new File(carpeta + "/" + listaImagenes.get(i));

            File f2 = new File(carpeta + "/"
                    + listaImagenes.get(i).substring(0, listaImagenes.get(i).length() - resto) + "." + salida);

            f1.renameTo(f2);

        }

        listaImagenes.clear();
    }
    public static byte[] createChecksum(String filename) throws NoSuchAlgorithmException, IOException {

        InputStream fis = null;

        MessageDigest complete = MessageDigest.getInstance("SHA-256");

        try {

            fis = new FileInputStream(filename);

            byte[] buffer = new byte[1024];

            int numRead;

            do {

                numRead = fis.read(buffer);

                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }

            }

            while (numRead != -1);

            fis.close();

        }

        catch (IOException e) {

            if (fis != null) {
                fis.close();
            }

        }

        return complete.digest();
    }

    public static String extraerExtension(String nombreArchivo) {

        String extension = "";

        if (nombreArchivo.length() >= 3) {

            extension = nombreArchivo.substring(nombreArchivo.length() - 3, nombreArchivo.length());

            extension = extension.toLowerCase();

            if (extension.equals("peg")) {
                extension = "jpeg";
            }

            if (extension.equals("fif")) {
                extension = "jfif";
            }

            if (extension.equals("ebp")) {
                extension = "webp";
            }

            if (extension.equals("ebm")) {
                extension = "webm";
            }

            if (extension.equals("3u8")) {
                extension = "m3u8";
            }

            if (extension.equals(".ts")) {
                extension = "ts";
            }

        }

        return extension;
    }

    public static void renombrar(String ruta1, String ruta2) {

        File f1 = new File(ruta1);

        File f2 = new File(ruta2);

        f1.renameTo(f2);

    }

    public static String eliminarPuntos(String cadena) {

        String cadena2 = cadena;

        try {
            cadena2 = cadena.substring(0, cadena.length() - 4);

            cadena = cadena2.replace(".", "_") + "." + extraerExtension(cadena);
        } catch (Exception e) {

        }

        return cadena;
    }

    public static LinkedList<String> directorio(String ruta, String extension, boolean filtro, boolean carpeta) {

        LinkedList<String> lista = new LinkedList<String>();

        try {

            File f = new File(ruta);

            if (f.exists()) {

                File[] ficheros = f.listFiles();

                String fichero = "";

                String extensionArchivo;

                File folder;

                for (int x = 0; x < ficheros.length; x++) {

                    fichero = ficheros[x].getName();

                    folder = new File(ruta + fichero);

                    extensionArchivo = extraerExtension(fichero);

                    int megas = (int) ((ficheros[x].length() / 1048.576) / 1048.576);

                    if ( !(extensionArchivo.equals("gif") && folder.isFile() && megas > 2)) {

                        if (filtro) {

                            if (folder.isFile()) {

                                if (fichero.length() > 5 && fichero.substring(0, fichero.length() - 5).contains(".")) {

                                    renombrar(ruta + fichero, ruta + eliminarPuntos(fichero));

                                }

                                if (extension.equals("webp") && extensionArchivo.equals("webp")
                                        || extension.equals("jpeg") && extensionArchivo.equals("jpeg")
                                        || extension.equals(".") || extension.equals(extensionArchivo)) {

                                    if (carpeta) {
                                        lista.add(ruta + fichero);
                                    }

                                    else {
                                        lista.add(fichero);
                                    }

                                }

                            }

                        }

                        else {

                            if (folder.isDirectory()) {

                                if (carpeta) {
                                    lista.add(ruta + fichero);
                                }

                                else {

                                    fichero = fichero.trim();

                                    if (!fichero.isEmpty()) {
                                        lista.add(fichero);
                                    }

                                }

                            }

                        }

                    }

                }

            }
        }

        catch (Exception e) {

        }

        Collections.sort(lista);

        return lista;

    }

    public static String getSHA256Checksum(String filename) {

        String result = "";

        try {

            byte[] b;

            b = createChecksum(filename);

            StringBuilder bld = new StringBuilder();

            for (int i = 0; i < b.length; i++) {
                bld.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
            }

            result = bld.toString();

        } catch (Exception e) {
            //
        }

        return result;
    }

}
