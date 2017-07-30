/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package qqa;

import interfaz.InterfazPrincipal;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import javax.swing.JOptionPane;
import recolectordepreguntas.Pregunta;

/**
 *
 * @author Jose
 */
public class Main {

    public static Nucleo miCanvas= new Nucleo();

    public static void main(String[] args) {
        agregarPreguntas();
        agregarRespuestas();
        miCanvas.generarModo2();
        InterfazPrincipal.main(args);
    }

    public static void agregarPreguntas(){
        ObjectInputStream entrada = null;
        try {
            //entrada = new ObjectInputStream(new FileInputStream("nucleo.dll"));
            ObtenerPreguntas on = new ObtenerPreguntas();
            entrada = new ObjectInputStream(on.getNucleo());
            Nucleo.misPreguntas.addAll((Vector<Pregunta>) entrada.readObject());
            entrada.close();
//            for(Pregunta p : Nucleo.misPreguntas){
//                System.out.println(p);
//            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Se produjo un error al intentar cargar el nucleo, notificale a Jose\n"+ex);
            System.exit(0);
        }
    }

    public static void agregarRespuestas(){
        ObjectInputStream entrada = null;
        try {
            entrada = new ObjectInputStream(new FileInputStream("reg.dll"));
            Nucleo.misRespuestas.addAll((Vector<Respuesta>) entrada.readObject());
            entrada.close();
        } catch (Exception ex) {
            guardarRespuestas();
        }
    }

    public static void guardarRespuestas(){
        ObjectOutputStream salida = null;
        try {
            salida = new ObjectOutputStream(new FileOutputStream("reg.dll"));
            salida.writeObject(Nucleo.misRespuestas);
            salida.flush();
            salida.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Se produjo un error al intentar guardar los datos, notificale a Jose");
        }
    }
}
