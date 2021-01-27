package ru.bartwell.exfilepickersample.util;

import java.io.Serializable;

public class Objeto implements Serializable {

    private String _dato;

    public Objeto(String dato) {
        this._dato = dato;
    }

    public String toString() {
        return this._dato;
    }

}