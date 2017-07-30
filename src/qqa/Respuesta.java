/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qqa;

import java.io.Serializable;
import java.util.Vector;

/**
 *
 * @author Jose
 */
public class Respuesta implements Serializable{
    private Vector<Integer> preguntasRonda = new Vector<Integer>();
    private Vector<Integer> respuestasRonda = new Vector<Integer>();
    private double tiempo;

    public Respuesta(Vector<Integer> preguntasRonda, Vector<Integer> respuestasRonda, double tiempo) {
        this.preguntasRonda=preguntasRonda;
        this.respuestasRonda=respuestasRonda;
        this.tiempo=tiempo;
    }

    public Vector<Integer> getPreguntasRonda() {
        return preguntasRonda;
    }

    public void setPreguntasRonda(Vector<Integer> preguntasRonda) {
        this.preguntasRonda = preguntasRonda;
    }

    public Vector<Integer> getRespuestasRonda() {
        return respuestasRonda;
    }

    public void setRespuestasRonda(Vector<Integer> respuestasRonda) {
        this.respuestasRonda = respuestasRonda;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }
}
