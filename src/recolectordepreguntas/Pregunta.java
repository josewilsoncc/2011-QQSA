package recolectordepreguntas;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.Serializable;

/**
 *
 * @author Ing. Sistemas
 */
public class Pregunta implements Serializable{
    public final static int A=0;
    public final static int B=1;
    public final static int C=2;
    public final static int D=3;

    public Pregunta(String texto, String a, String b, String c, String d, int correcta) {
        this.texto = texto;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.correcta = correcta;
    }

    private String texto, a, b, c, d;
    private int correcta;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public int getCorrecta() {
        return correcta;
    }

    public void setCorrecta(int correcta) {
        this.correcta = correcta;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    @Override
    public String toString(){
        return texto+" || "+a+" || "+b+" || "+c+" || "+d+" || "+correcta;
    }
}