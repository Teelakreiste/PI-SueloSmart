/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package suelosmart;

import Entity.Tamices;
import UI.UILogin;
import UI.UILogin1;
import UI.UItamizado;
/**
 *
 * @author osmel
 */
public class SueloSmart {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new UItamizado().setVisible(true);
//        Tamices objTamices = new Tamices();
//        for (int i = 0; i < objTamices.getApertureSizeMm().size(); i++) {
//            System.out.print("Tamiz n° " + objTamices.getNumberTamices().get(i));
//            System.out.println("\t|\tApertura (mm)° " + objTamices.getApertureSizeMm().get(i) + " mm");
//        }
    }
    
}
