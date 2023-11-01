/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suelosmart;

import Entity.ReadCSV;
import UI.UILogin;
import UI.UILogin1;
import UI.UItamizado;
import java.util.ArrayList;

/**
 *
 * @author osmel
 */
public class SueloSmart {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        new UItamizado().setVisible(true);
        getIdiomas();
    }
    
    private static ArrayList<String> codigos = new ArrayList<>();
    private static ReadCSV read = new ReadCSV();
    //función que permite asignar las opciones de idiomas a los combo box
    private static void getIdiomas() {
        //arraylist que obtiene los datos que retonara la función readCsv();
        ArrayList<String> data = read.ReadCsv();
        
        System.out.println(data);
        //ciclo que permite asignar las opciones de los idiomas a los jcombobox y almacena los codigos de los idiomas al arraylist codigo
        for (int i = 3; i < data.size(); i+=2) {
            System.out.println("N° Tamiz: " + data.get(i-1));
            System.out.println("Abertura: " + data.get(i));
        }
    }
    
}
