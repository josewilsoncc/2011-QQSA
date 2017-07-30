/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package qqa;

import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Jose
 */
public class ObtenerPreguntas {
    private final InputStream nucleo;

    public ObtenerPreguntas() {
        nucleo = getClass().getResourceAsStream("/rec/nucleo.dll");
    }

    public InputStream getNucleo() {
        return nucleo;
    }
}
