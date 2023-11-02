/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author osmel
 */
public class Fonts {

    private Font font = null;
    public final String ROBOTO_BLACK = "../Assets/fonts/Roboto-Black.ttf";
    public final String ROBOTO_BLACK_ITALIC = "../Assets/fonts/Roboto-BlackItalic.ttf";
    public final String ROBOTO_BOLD = "../Assets/fonts/Roboto-Bold.ttf";
    public final String ROBOTO_BOLD_ITALIC = "../Assets/fonts/Roboto-BoldItalic.ttf";
    public final String ROBOTO_ITALIC = "../Assets/fonts/Roboto-Italic.ttf";
    public final String ROBOTO_LIGHT = "../Assets/fonts/Roboto-Light.ttf";
    public final String ROBOTO_LIGHT_ITALIC = "../Assets/fonts/Roboto-LightItalic.ttf";
    public final String ROBOTO_MEDIUM = "../Assets/fonts/Roboto-Medium.ttf";
    public final String ROBOTO_MEDIUM_ITALIC = "../Assets/fonts/Roboto-MediumItalic.ttf";
    public final String ROBOTO_REGULAR = "../Assets/fonts/Roboto-Regular.ttf";
    public final String ROBOTO_THIN = "../Assets/fonts/Roboto-Thin.ttf";
    public final String ROBOTO_THIN_ITALIC = "../Assets/fonts/Roboto-ThinItalic.ttf";

    public Font Font(String fontName, int style, float size) {
        try {
            //Se carga la fuente
            InputStream is = getClass().getResourceAsStream(fontName);
            font = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException ex) {
            //Si existe un error se carga fuente por defecto ARIAL
            System.err.println(fontName + " No se cargo la fuente");
            font = new Font("Arial", Font.PLAIN, 14);
        }
        Font tfont = font.deriveFont(style, size);
        return tfont;
    }
}
