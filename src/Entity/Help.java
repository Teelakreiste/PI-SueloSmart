/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author osmel
 */
public class Help {

    public void Help(JButton jButton, JPanel jPanel) {
        try {
            // Cara el fichero de ayuda
            File file = new File("src" + File.separator + "Help" + File.separator + "help_set.hs");
            URL hsURL = file.toURI().toURL();

            // Crea el HelpSet y el HelpBroker
            HelpSet helpSet = new HelpSet(getClass().getClassLoader(), hsURL);
            HelpBroker helpBroker = helpSet.createHelpBroker();

            //Coloca ayuda al item del menu al pulsarlo y a F1 en ventana
            // principal y secundaria
            helpBroker.enableHelpOnButton(jButton, "main", helpSet);
            helpBroker.enableHelpKey(jPanel, "main", helpSet);
        } catch (IllegalArgumentException | MalformedURLException | HelpSetException e) {

        }
    }
}
