/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.ArrayList;

/**
 *
 * @author osmel
 */
public class Tamices {

    private ArrayList<String> numberTamices;
    private ArrayList<Double> apertureSizeMm;
    private final ReadCSV rCSV = new ReadCSV();

    public Tamices() {
        this.numberTamices = new ArrayList<>();
        this.apertureSizeMm = new ArrayList<>();
        setNumberTamicesAndapertureSizeMm();
    }

    private void setNumberTamicesAndapertureSizeMm() {
        ArrayList<String> data = rCSV.ReadCsv();

        //ciclo que permite asignar las opciones de los idiomas a los jcombobox y almacena los codigos de los idiomas al arraylist codigo
        for (int i = 3; i < data.size(); i += 2) {
            this.numberTamices.add(data.get(i - 1));
            this.apertureSizeMm.add(Double.parseDouble(data.get(i).replace("mm", "")));
        }
    }

    public ArrayList<String> getNumberTamices() {
        return numberTamices;
    }

    public ArrayList<Double> getApertureSizeMm() {
        return apertureSizeMm;
    }
}
