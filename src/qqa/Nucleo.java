/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qqa;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import recolectordepreguntas.Pregunta;

/**
 *
 * @author Jose
 */
public class Nucleo extends Canvas implements KeyListener {

    //Definen las dimenciones de la pantalla:
    public final static String VERSION = "1.11";

    //Definen las dimenciones de la pantalla:
    public final static int ANCHO = 640, ALTO = 480;

    //Definen los estados del juego
    public final static int INTRO = 0;
    public final static int MENU = 1;
    public final static int CONTEO = 2;
    public final static int JUEGO = 3;
    public final static int RESULTADOS = 4;
    public final static int ESTADISTICAS = 5;
    public final static int CREDITOS = 6;
    public final static int INSTRUCCIONES = 7;
    public final static int EXTRA = 8;
    public final static int TECLADO = 9;
    public final static int MODO = 10;
    public final static int SALON_DE_LA_FAMA = 11;
    public final static int SECRETO = 12;

    //Retiene 1 si la ultima respuesta fue correcta
    public static boolean ultimaPregunta;
    
    //Si es 1 bloquea en el estado de JUEGO
    public static boolean bloqueo = false;

    //si es 1 procedera a desbloquear en el estado de JUEGO
    public static boolean continuar = false;

    public static boolean pulsadoP = false;

    //Retiene 1 si el ultimo modo jugado fue el modo 2
    public static boolean ultimoModo2=false;

    //Indica si el sonido del intro del juego fue reproducido
    public static boolean sonidoIntro=false;

    /**
     * FLECHA DEL MENU
     */
    //Define el movimiento de la Flecha del estado MENU
    public static boolean movimientoDerechaFlecha = true;
    //Retiene la coordenada del la Flecha del estado MENU
    public static int coordenadaFlecha = 0;

    /**
     * FONDO
     */

    //Define el movimiento de la fondo
    public static boolean movimientoDerechaFondo = true;
    //Define la coordenada del fondo
    public static int coordenadaFondo = -240;

    /**
     * INDICADORES DE SI LA ULTIMA RESPUESTA FUE CORRECTA O NO
     */
    //retiene el numero de la opcion correcta de la ultima respuesta
    public static int ultimaRespuestaCorrecta;
    //lleva el tiempo en el que se muestra si estuvo BIEN o MAL la ultima respuesta
    public static double tiempoReporte = 0;

    //Es la coordenada de la imagen del estado CREDITOS
    public static int coordenadaCreditos = 0;

    //Indica el numero de archivos de audio ( BIEN - MAL ) que se usaran en el estado JUEGO
    public static int topeArchivosDeSonido;

    //Es la cantidad de preguntas por defecto de las rondas
    public final static int CANTIDAD_DE_PREGUNTAS = 15;

    //lleva la cuenta interna del tiempo de la ronda actual en el estado JUEGO
    public static float tiempo = (float) -1.7;

    /*lleva un "tiempo interno"  que define el comportamiento de varios
     * elementos como lo son la flecha del estado MENU, y los letreros "Presiona Enter"
     */
    public static float tiempoInterno = 0;

    //Define el estado actual del juego, es el nucleo de todo
    public static int estado = INTRO;

    //Indica el elemento del estado MENU o MODO que esta seleccionado
    public static int elemento = 0;

    public static int numeroDeSuprimir = 0;

    //indica cuantas preguntas han sido realizadas hasta el momento
    public static int indexPreguntas = 0;

    //MODO 2:
    //indica cuantas rondas se han jugado hasta el momento en el MODO 2
    public static int indexModo2 = 0;
    //indica el numero total de rondas en el MODO 2
    public static int topeRondasModo2 = 0;

    //INFORMACION
    //contiene todas las preguntas usadas en el juego
    public static Vector<Pregunta> misPreguntas = new Vector<Pregunta>();
    //contiene todas las respuestas recolectadas por el juego
    public static Vector<Respuesta> misRespuestas = new Vector<Respuesta>();
    //INFORMACION TEMPORAL
    //son las preguntas de la ronda actual
    private static Vector<Integer> preguntasRonda = new Vector<Integer>();
    //retiene las preguntas generadas para el modo dos, separadas por rondas
    private static Vector<Vector<Integer>> preguntasRondas = new Vector<Vector<Integer>>();
    //retiene las respuestas dadas en una ronda
    private static Vector<Integer> respuestasRonda = new Vector<Integer>();
    //Retiene el nombre para el salon de la fama
    private static String nombreUsuario="";

    //GRAFICOS
    //son los graficos de la imagen interna
    private Graphics g;
    //es la imagen interna
    private ImageIcon imagenInterna;
    //indica si la imagen interna ya fue creada
    private boolean imagenInternaCreada;

    //SONIDO
    //contiene los sonidos que se reproducen cuando el usuario contesta correctamente
    private Vector<Clip> sonidosBien = new Vector<Clip>();
    //contiene los sonidos que se reproducen cuando el usuario contesta incorrectamente
    private Vector<Clip> sonidosMal = new Vector<Clip>();

    //Regula el formato de los numeros
    NumberFormat nf = NumberFormat.getInstance();

    public Nucleo() {
        new HiloPrincipal().start();
        try {
            topeArchivosDeSonido = 5;
            for (int i = 0; i < topeArchivosDeSonido; i++) {
                Clip temp = AudioSystem.getClip(), temp2 = AudioSystem.getClip();
                sonidosBien.add(temp);
                sonidosMal.add(temp2);
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void paint(Graphics gr) {
        update(gr);
    }

    @Override
    public void update(Graphics gr) {
        if (!imagenInternaCreada) {
            imagenInternaCreada = true;
            imagenInterna = new ImageIcon(createImage(ANCHO, ALTO));
            g = imagenInterna.getImage().getGraphics();
        }
        pintar(g);
        gr.drawImage(imagenInterna.getImage(), 0, 0, getWidth(), getHeight(), 0, 0, 640, 480, this);
    }

    private void pintar(Graphics g) {
        if (preguntasRonda.size() == 0) {
            preguntasRonda = obtenerPreguntasRonda();
        }
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, ANCHO, ALTO);
        if (tiempoInterno < 10) {
            tiempoInterno += 0.1;
        } else {
            tiempoInterno = 0;
        }
        switch (estado) {
            case INTRO:
                intro(g);
                break;
            case MENU:
                menu(g);
                break;
            case CONTEO:
                conteo(g);
                break;
            case JUEGO:
                juego(g);
                break;
            case RESULTADOS:
                resultados(g);
                break;
            case ESTADISTICAS:
                estadisticas(g);
                break;
            case CREDITOS:
                creditos(g);
                break;
            case INSTRUCCIONES:
                instrucciones(g);
                break;
            case EXTRA:
                extra(g);
                break;
            case TECLADO:
                teclado(g);
                break;
            case MODO:
                modo(g);
                break;
            case SALON_DE_LA_FAMA:
                salonDeLaFama(g);
                break;
            case SECRETO:
                secreto(g);
                break;
        }
    }

    private void intro(Graphics g) {
        if(!sonidoIntro){
            sonidoIntro=true;
            try {
                Clip temp = AudioSystem.getClip();
                temp.open(AudioSystem.getAudioInputStream(getClass().getResource("/rec/son/intro.wav")));
                temp.start();
            } catch (Exception ex) {}
        }
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/icono.png")).getImage(), 40, 110,302,246, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/logo_uniquindio.png")).getImage(), 350, 90,300,300, this);
        tiempo += 0.1;
        if (tiempo >= 0.1) {
            estado = TECLADO;
        }
    }

    private void teclado(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/teclado.png")).getImage(), 0, 0, this);
        g.setColor(Color.WHITE);
        g.setFont(new Font("", 0, 18));
        if (tiempoInterno - ((int) tiempoInterno) <= 0.5) {
            g.drawString("¡Presiona Enter!", 260, 460);
        }
    }

    private void menu(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_menu.png")).getImage(), 0, 0, this);
        int Ejugar = 1, Eestadistica = 1, Einstrucciones = 1, Eextras = 1, Ecreditos = 1, desplazamiento = -75, xInicial = 175, yInicial = 115;
        switch (elemento) {
            case 0:
                Ejugar = desplazamiento;
                break;
            case 1:
                Eestadistica = desplazamiento;
                break;
            case 2:
                Einstrucciones = desplazamiento;
                break;
            case 3:
                Eextras = desplazamiento;
                break;
            case 4:
                Ecreditos = desplazamiento;
                break;
        }
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/cabezote.png")).getImage(), 0, -20, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/jugar.png")).getImage(), xInicial + Ejugar, yInicial, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/estadistica.png")).getImage(), xInicial + Eestadistica, yInicial + 70, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/instrucciones.png")).getImage(), xInicial + Einstrucciones, yInicial + 140, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/extras.png")).getImage(), xInicial + Eextras, yInicial + 210, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/creditos.png")).getImage(), xInicial + Ecreditos, yInicial + 280, this);
        if (movimientoDerechaFlecha && coordenadaFlecha < 20) {
            coordenadaFlecha += 4;
        }
        if (movimientoDerechaFlecha && coordenadaFlecha >= 20) {
            movimientoDerechaFlecha = false;
        }
        if (!movimientoDerechaFlecha && coordenadaFlecha > 0) {
            coordenadaFlecha -= 4;
        }
        if (!movimientoDerechaFlecha && coordenadaFlecha <= 0) {
            movimientoDerechaFlecha = true;
        }
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/flecha.png")).getImage(), xInicial + 300 + coordenadaFlecha, yInicial + 70 * elemento, this);
        g.setFont(new Font("", 0, 12));
        g.setColor(Color.GREEN);
        g.drawString("Contiene " + misPreguntas.size() + " preguntas :)", 10, 470);
        g.drawString("NoteAgger!", 570, 470);
        g.setFont(new Font("", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("JuegoAcreditacion.netai.net  -  Versión: "+VERSION, 150, 80);
    }

    private void conteo(Graphics g) {
        if (tiempo > 0) {
            tiempo -= 0.1;
        }
        int t = 3;
        if (tiempo <= 2) {
            t = 2;
        }
        if (tiempo <= 1) {
            t = 1;
        }
        if (tiempo <= 0) {
            t = 0;
            tiempo = 0;
            estado = JUEGO;
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("", 0, 90));
        g.drawString("" + t, 300, 250);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/publicidad.png")).getImage(), 0, 389, this);
    }

    private void juego(Graphics g) {
        nf.setMaximumFractionDigits(1);
        g.setFont(new Font("", 0, 14));
        g.setColor(Color.BLACK);
        Vector<String> pregunta = partirString("El PAR pregunta: " + misPreguntas.get(preguntasRonda.get(indexPreguntas)).getTexto());
        tiempo += 0.1;
        //g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_jugando.png")).getImage(), 0, 0, this);
        if(movimientoDerechaFondo){
            if(coordenadaFondo<0)
                coordenadaFondo++;
            else
                movimientoDerechaFondo=false;
        }
        else{
            if(coordenadaFondo>-240)
                coordenadaFondo--;
            else
                movimientoDerechaFondo=true;
        }
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_jugando.png")).getImage(),coordenadaFondo,coordenadaFondo,960,720, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/cabezote.png")).getImage(), 0, 110, this);

        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/man.png")).getImage(), 0, 25, this);

        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_opcion.png")).getImage(), 0, 360, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_opcion.png")).getImage(), 0, 390, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_opcion.png")).getImage(), 0, 420, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_opcion.png")).getImage(), 0, 450, this);

        switch (pregunta.size()) {
            case 1:
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta_corta.png")).getImage(), 0, 320, this);
                g.drawString(pregunta.get(0), 28, 340);
                break;
            case 2:
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta_up.png")).getImage(), 0, 290, this);
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta_down.png")).getImage(), 0, 320, this);
                g.drawString(pregunta.get(0), 28, 310);
                g.drawString(pregunta.get(1), 28, 340);
                break;
            case 3:
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta_up.png")).getImage(), 0, 260, this);
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta.png")).getImage(), 0, 290, this);
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta_down.png")).getImage(), 0, 320, this);
                g.drawString(pregunta.get(0), 28, 280);
                g.drawString(pregunta.get(1), 28, 310);
                g.drawString(pregunta.get(2), 28, 340);
                break;
            case 4:
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta_up.png")).getImage(), 0, 230, this);
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta.png")).getImage(), 0, 260, this);
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta.png")).getImage(), 0, 290, this);
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_pregunta_down.png")).getImage(), 0, 320, this);
                g.drawString(pregunta.get(0), 28, 250);
                g.drawString(pregunta.get(1), 28, 280);
                g.drawString(pregunta.get(2), 28, 310);
                g.drawString(pregunta.get(3), 28, 340);
                break;
        }
        g.drawString("A", 8, 380);
        g.drawString("B", 8, 410);
        g.drawString("C", 8, 440);
        g.drawString("D", 8, 470);
        g.drawString(misPreguntas.get(preguntasRonda.get(indexPreguntas)).getA(), 28, 380);
        g.drawString(misPreguntas.get(preguntasRonda.get(indexPreguntas)).getB(), 28, 410);
        g.drawString(misPreguntas.get(preguntasRonda.get(indexPreguntas)).getC(), 28, 440);
        g.drawString(misPreguntas.get(preguntasRonda.get(indexPreguntas)).getD(), 28, 470);
        g.setColor(Color.WHITE);
        g.drawString("Pregunta #"+preguntasRonda.get(indexPreguntas), 540, 15);
        g.setColor(Color.cyan);
        g.fillRect(11, 13, 198, 3);
        g.fillRect(10, 16, 200, 6);
        g.fillRect(11, 22, 198, 3);
        g.setColor(new Color(50, 50, 255));
        g.fillRect(11, 13, (198 * indexPreguntas) / CANTIDAD_DE_PREGUNTAS, 3);
        g.setColor(new Color(50, 50, 190));
        g.fillRect(10, 16, (200 * indexPreguntas) / CANTIDAD_DE_PREGUNTAS, 6);
        g.setColor(new Color(50, 50, 120));
        g.fillRect(11, 22, (198 * indexPreguntas) / CANTIDAD_DE_PREGUNTAS, 3);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/barra.png")).getImage(), 9, 9, this);
        g.setFont(new Font("", 0, 12));
        g.setColor(Color.WHITE);
        String temp="--- Ronda #"+indexModo2+" de "+topeRondasModo2;
        if(indexModo2==0)
            temp="--- Ronda #"+topeRondasModo2+" de "+topeRondasModo2;
        if(!ultimoModo2)
            temp="";
        g.drawString(indexPreguntas + 1 + " / " + CANTIDAD_DE_PREGUNTAS+" "+temp, 215, 25);
        resultadoRespuesta(g);
    }

    private void resultados(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_jugando.png")).getImage(), 0, 0, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/icono.png")).getImage(), 190, 20, this);
        g.setFont(new Font("", 0, 35));
        g.setColor(Color.WHITE);
        g.drawString("Resultados: ", 20, 210);
        g.setColor(Color.GREEN);
        g.fillArc(370, 40, 80, 80, 90, 360);
        int cantidadCorrectas = cantidadCorrectas(misRespuestas.get(misRespuestas.size() - 1));
        int cantidadIncorrectas = cantidadErroneas(misRespuestas.get(misRespuestas.size() - 1));
        nf.setMaximumFractionDigits(2);
        g.drawString("Respuestas Correctas: " + cantidadCorrectas+" ("+nf.format(((cantidadCorrectas/(double)CANTIDAD_DE_PREGUNTAS)*(double)100))+"%)", 20, 260);
        g.setColor(Color.RED);
        g.drawString("Respuestas Incorrectas: " + cantidadIncorrectas+" ("+nf.format(((cantidadIncorrectas/(double)CANTIDAD_DE_PREGUNTAS)*(double)100))+"%)", 20, 310);
        g.fillArc(371, 41, 78, 78, 90, (int)(360*(cantidadIncorrectas / (double) (CANTIDAD_DE_PREGUNTAS))));
        g.setColor(Color.WHITE);
        if(cantidadCorrectas<5)
            g.drawString("Motivacion para ser acreditado!", 20, 360);
        else
            if(cantidadCorrectas<8)
                g.drawString("se puede ¡la preparacion es clave!", 20, 360);
            else
                if(cantidadCorrectas<10)
                    g.drawString("Falta poco :)!", 20, 360);
                else
                    if(cantidadCorrectas<12)
                        g.drawString("Vamos bien :)!", 20, 360);
                    else
                        if(cantidadCorrectas<15)
                            g.drawString("Muy bien esa es la actitud :)!", 20, 360);
                        else
                            g.drawString("Simplemente perfecto :)!", 20, 360);
        if (tiempoInterno - ((int) tiempoInterno) <= 0.5) {
            g.drawString("¡Presiona Enter! ", 200, 450);
        }
    }

    private void estadisticas(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/fondo_jugando.png")).getImage(), 0, 0, this);
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/icono.png")).getImage(), 245, 20, this);
        g.setFont(new Font("", 0, 25));
        g.setColor(Color.WHITE);
        g.drawString("Estadisticas: ", 20, 180);
        g.drawString("Total rondas jugadas: " + misRespuestas.size(), 20, 220);
        nf.setMaximumFractionDigits(2);
        g.setColor(Color.GREEN);
        g.fillArc(530, 232, 80, 80, 90, 360);
        g.drawString("Total respuestas Correctas: " + totalCantidadCorrectas() + " (" + nf.format((totalCantidadCorrectas() / (double) (misRespuestas.size() * CANTIDAD_DE_PREGUNTAS)) * 100) + "%)", 20, 260);
        g.setColor(Color.RED);
        g.fillArc(531, 233, 78, 78, 90, (int)(360*(totalCantidadErroneas() / (double) (misRespuestas.size() * CANTIDAD_DE_PREGUNTAS))));
        g.drawString("Total respuestas Incorrectas: " + totalCantidadErroneas() + " (" + nf.format((totalCantidadErroneas() / (double) (misRespuestas.size() * CANTIDAD_DE_PREGUNTAS)) * 100) + "%)", 20, 300);
        g.setColor(Color.WHITE);
        g.drawString("Total tiempo jugado: " + nf.format(totalTiempo()) + " segundos", 20, 340);
        g.drawString("Tiempo promedio por ronda: " + nf.format((double) (totalTiempo() / (double) misRespuestas.size())) + " segundos", 20, 380);
        if (tiempoInterno - ((int) tiempoInterno) <= 0.5) {
            g.drawString("¡Presiona Enter! ", 235, 440);
        }
    }

    private void creditos(Graphics g) {
        if(!sonidoIntro){
            sonidoIntro=true;
            try {
                Clip temp = AudioSystem.getClip();
                temp.open(AudioSystem.getAudioInputStream(getClass().getResource("/rec/son/intro.wav")));
                temp.start();
            } catch (Exception ex) {}
        }
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/creditos2.png")).getImage(), 0, 480 - coordenadaCreditos, this);
        if (coordenadaCreditos < 2100) {
            coordenadaCreditos += 8;
        } else {
            coordenadaCreditos += 10;
        }
        if (coordenadaCreditos >= 2800) {
            estado = MENU;
        }
    }

    private void instrucciones(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/instrucciones2.png")).getImage(), 0, 0, this);
        g.setColor(Color.WHITE);
        g.setFont(new Font("", 0, 18));
        if (tiempoInterno - ((int) tiempoInterno) <= 0.5) {
            g.drawString("¡Presiona Enter! ", 255, 450);
        }
    }

    private void extra(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/extra.png")).getImage(), 0, 0, this);
        g.setColor(Color.WHITE);
        g.setFont(new Font("", 0, 18));
        if (tiempoInterno - ((int) tiempoInterno) <= 0.5) {
            g.drawString("¡Presiona Enter!", 260, 460);
        }
    }

    private void modo(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/modo.png")).getImage(), 0, 0, this);
        if (tiempoInterno - ((int) tiempoInterno) <= 0.2 || (tiempoInterno - ((int) tiempoInterno) >= 0.5 && tiempoInterno - ((int) tiempoInterno) <= 0.7)) {
            if (elemento == 0) {
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/index_modo.png")).getImage(), 85, 115, this);
            }
            if (elemento == 1) {
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/index_modo.png")).getImage(), 85, 263, this);
            }
        }
        g.drawImage(new ImageIcon(getClass().getResource("/rec/img/nota_salon.png")).getImage(), 0, 438, this);
    }

    private void secreto(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 640, 480);
//        g.setColor(Color.RED);
//        g.setFont(new Font("", 0, 18));
//        g.drawString("SECRETO DE JOSE", 245, 35);
//        g.setFont(new Font("", 0, 15));
//        Vector<String> cadenas = partirString("Hay actos o acciones que surgen aparentemente sin ninguna explicación,"
//                + "pero la realidad es que si la tienen, pero pocos la conocen, de ahí nacen los secretos. Jose W.");
//        g.drawString(cadenas.get(0), 20, 80);
//        g.drawString(cadenas.get(1), 20, 100);
//        g.drawString("Algunas cosas aparentemente ilógicas son más lógicas de lo que parecen", 20, 140);
//        g.drawString("Aunque esta persona NO lo sepa, este juego es dedicado a ella: ", 20, 180);
//        Vector<String> temp = new Vector<String>();
//        temp.add("Si se nos da!");
//        temp.add("La oportunidad!");
//        temp.add("Que nuestros cuerpos se puedan encontrar :) !!!");
//        temp.add("Yo me asegure!");
//        temp.add("De traerte a un lugar!");
//        temp.add("Donde tú y yo la pasemos bien :) !!!");
//        int x = (int) (Math.random() * 6);
//        if (x == 6) {
//            x = 5;
//        }
//        g.drawString(temp.get(x), 20, 420);
//        g.setFont(new Font("", 0, 100));
//        g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
//        g.drawString("KT", 255, 320);
        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("", 0, 15));
        Vector<String>fraces=new Vector<String>();
        fraces.add("Te Quiero");
        fraces.add("Hermoza");
        fraces.add("20/Abril/2011");
        fraces.add("Kiss");
        fraces.add("myLove");
        fraces.add("Bb");
        fraces.add("Nuestro Secreto :P");
        fraces.add("myLove");
        fraces.add("Te Quiero");
        for(int i=0;i<fraces.size();i++){
            g.drawString(fraces.get(i), (int)(Math.random()*500), (i+1)*50);
        }
        g.setColor(Color.YELLOW);
        int x=0, y=0, escala=25+(int)tiempoInterno;
        x=(640-8*escala)/2;
        y=(480-6*escala)/2;
        int[] x1 = {x+4*escala,x+2*escala,x+6*escala};
        int[] y1 = {y,y+3*escala,y+3*escala};
        int[] x2 = {x+2*escala,x,x+4*escala};
        int[] y2 = {y+3*escala,y+6*escala,y+6*escala};
        int[] x3 = {x+6*escala,x+4*escala,x+8*escala};
        int[] y3 = {y+3*escala,y+6*escala,y+6*escala};
        g.fillPolygon(x1, y1, 3);
        g.fillPolygon(x2, y2, 3);
        g.fillPolygon(x3, y3, 3);
        g.setFont(new Font("", 0, 40));
        g.setColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        g.drawString("Paulina Orozco & Jose Capera", 55, 250);
    }

    private void salonDeLaFama(Graphics g) {
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 0, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 200, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 400, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 600, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 0, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 200, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 400, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 600, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 100, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 200, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 300, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 400, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 0, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 200, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 400, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo2.png")).getImage(), 600, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo3.png")).getImage(), 0, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo3.png")).getImage(), 200, 200, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo3.png")).getImage(), 400, 400, this);
        g.drawImage(new ImageIcon(getClass().getResource("/lib/logo1.png")).getImage(), 150, 150, this);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("", 0, 20));
        g.drawString("Bienvenido al Salón de la Fama, puede tomar una captura de pantalla", 10, 30);
        g.drawString("y enviarla al email josewilsoncc@gmail.com para aparecer en el", 10, 60);
        g.drawString("Salón de la Fama de la pagina web si aun no está en el :)", 10, 90);
        g.drawString("Para continuar presione Escape :P", 10, 120);
        g.setColor(Color.WHITE);
        for(int i=0;i<3;i++)
            for(int j=0;j<12;j++)
                g.drawString(nombreUsuario,10+300*i, 150+30*j);
    }

    public double totalTiempo() {
        double contador = 0;
        for (Respuesta r : misRespuestas) {
            contador += r.getTiempo();
        }
        return contador;
    }

    public int totalCantidadCorrectas() {
        int contador = 0;
        for (Respuesta r : misRespuestas) {
            contador += cantidadCorrectas(r);
        }
        return contador;
    }

    public int totalCantidadErroneas() {
        int contador = 0;
        for (Respuesta r : misRespuestas) {
            contador += cantidadErroneas(r);
        }
        return contador;
    }

    public int cantidadCorrectas(Respuesta r) {
        int contador = 0;
        for (int i = 0; i < r.getPreguntasRonda().size(); i++) {
            if (misPreguntas.get(r.getPreguntasRonda().get(i)).getCorrecta() == r.getRespuestasRonda().get(i)) {
                contador++;
            }
        }
        return contador;
    }

    public int cantidadErroneas(Respuesta r) {
        return r.getPreguntasRonda().size() - cantidadCorrectas(r);
    }

    public static void generarModo2() {
        System.out.println("Enter");
        double rondasCalculo = misPreguntas.size() / (double) CANTIDAD_DE_PREGUNTAS;
        int rondas = (int) rondasCalculo;
        if (rondasCalculo - rondas != 0) {
            rondas++;
        }
        topeRondasModo2 = rondas;
        System.out.println("rondas: " + rondas);
        for (int j = 0; j < rondas; j++) {
            preguntasRondas.add(new Vector<Integer>());
        }
        int i = 0;
        while (i < rondas) {
            int cantidad = 0;
            while (cantidad < CANTIDAD_DE_PREGUNTAS) {
                int x = (int) (Math.random() * misPreguntas.size());
                if (!yaAgregadaModo(preguntasRondas, x) || (totalPreguntasModo2() >= misPreguntas.size() && !yaAgregada(preguntasRondas.get(i), x))) {
                    preguntasRondas.get(i).add(x);
                    cantidad++;
                }
            }
            i++;
        }
    }

    public static int totalPreguntasModo2() {
        int contador = 0;
        for (Vector<Integer> v : preguntasRondas) {
            contador += v.size();
        }
        return contador;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        switch (estado) {
            case SECRETO:
                estado = MENU;
                break;
            case TECLADO:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    estado = MENU;
                }
                break;
            case MENU:
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (elemento > 0) {
                        elemento--;
                    } else {
                        elemento = 4;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (elemento < 4) {
                        elemento++;
                    } else {
                        elemento = 0;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && elemento == 0) {
                    elemento = 0;
                    estado = MODO;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && elemento == 1) {
                    elemento = 0;
                    estado = ESTADISTICAS;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && elemento == 2) {
                    elemento = 0;
                    estado = INSTRUCCIONES;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && elemento == 3) {
                    elemento = 0;
                    estado = EXTRA;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && elemento == 4) {
                    elemento = 0;
                    coordenadaCreditos = 0;
                    sonidoIntro=false;
                    estado = CREDITOS;
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                break;
            case JUEGO:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    estado = MENU;
                    if (ultimoModo2)
                        indexModo2--;
                }
                if (e.getKeyCode() == KeyEvent.VK_A && !bloqueo) {
                    tiempoReporte = 3;
                    ultimaPregunta = analizarRespuesta(0, indexPreguntas);
                    ultimaRespuestaCorrecta = misPreguntas.get(preguntasRonda.get(indexPreguntas)).getCorrecta();
                    bloqueo = true;
                    reproducirSonido();
                    respuestasRonda.add(0);
                }
                if (e.getKeyCode() == KeyEvent.VK_B && !bloqueo) {
                    tiempoReporte = 3;
                    ultimaPregunta = analizarRespuesta(1, indexPreguntas);
                    ultimaRespuestaCorrecta = misPreguntas.get(preguntasRonda.get(indexPreguntas)).getCorrecta();
                    bloqueo = true;
                    reproducirSonido();
                    respuestasRonda.add(1);
                }
                if (e.getKeyCode() == KeyEvent.VK_C && !bloqueo) {
                    tiempoReporte = 3;
                    ultimaPregunta = analizarRespuesta(2, indexPreguntas);
                    ultimaRespuestaCorrecta = misPreguntas.get(preguntasRonda.get(indexPreguntas)).getCorrecta();
                    bloqueo = true;
                    reproducirSonido();
                    respuestasRonda.add(2);
                }
                if (e.getKeyCode() == KeyEvent.VK_D && !bloqueo) {
                    tiempoReporte = 3;
                    ultimaPregunta = analizarRespuesta(3, indexPreguntas);
                    ultimaRespuestaCorrecta = misPreguntas.get(preguntasRonda.get(indexPreguntas)).getCorrecta();
                    bloqueo = true;
                    reproducirSonido();
                    respuestasRonda.add(3);
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && bloqueo) {
                    continuar = true;
                    tiempoReporte = 0;
                }
                if (respuestasRonda.size() >= preguntasRonda.size() && !bloqueo) {
                    estado = RESULTADOS;
                    misRespuestas.add(new Respuesta(preguntasRonda, respuestasRonda, tiempo));
                    Main.guardarRespuestas();
                }
                break;
            case RESULTADOS:
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    numeroDeSuprimir++;
                }
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (numeroDeSuprimir == 8) {
                        pulsadoP = true;
                    } else {
                        numeroDeSuprimir = 0;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_J) {
                    if (pulsadoP && cantidadCorrectas(misRespuestas.get(misRespuestas.size() - 1)) >= 10) {
                        estado = SECRETO;
                    }
                }
                if (e.getKeyCode() != KeyEvent.VK_DELETE && e.getKeyCode() != KeyEvent.VK_P && e.getKeyCode() != KeyEvent.VK_J) {
                    pulsadoP = false;
                    numeroDeSuprimir = 0;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(cantidadCorrectas(misRespuestas.get(misRespuestas.size() - 1))==CANTIDAD_DE_PREGUNTAS){
                        boolean sinNombre=true;
                        while(sinNombre){
                            nombreUsuario=JOptionPane.showInputDialog("Ingrese su nombre COMPLETO por favor :)");
                            if(nombreUsuario.trim().length()>10)
                                sinNombre=false;
                            else
                                JOptionPane.showMessageDialog(null, "El nombre ingresado debe ser mayor de 10 caracteres :)");
                        }
                        estado=SALON_DE_LA_FAMA;
                    }
                    else
                        estado = MENU;
                }
                break;
            case ESTADISTICAS:
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    misRespuestas = new Vector<Respuesta>();
                    Main.guardarRespuestas();
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    estado = MENU;
                }
                break;
            case INSTRUCCIONES:
            case EXTRA:
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    estado = MENU;
                }
                break;
            case CREDITOS:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    estado = MENU;
                }
                break;
            case CONTEO:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    estado = MENU;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    estado = JUEGO;
                }
                break;
            case MODO:
                respuestasRonda=new Vector<Integer>();
                bloqueo=false;
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    estado = MENU;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && elemento == 0) {
                    ultimoModo2=false;
                    indexPreguntas = 0;
                    preguntasRonda = obtenerPreguntasRonda();
                    respuestasRonda = new Vector<Integer>();
                    tiempo = (float) 3.1;
                    tiempoReporte = 0;
                    elemento = 0;
                    estado = CONTEO;
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER && elemento == 1) {
                    ultimoModo2=true;
                    indexPreguntas = 0;
                    preguntasRonda = preguntasRondas.get(indexModo2);
                    if(indexModo2<topeRondasModo2-1)
                        indexModo2++;
                    else
                        indexModo2=0;
                    respuestasRonda = new Vector<Integer>();
                    tiempo = (float) 3.1;
                    tiempoReporte = 0;
                    elemento = 0;
                    estado = CONTEO;
                }
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (elemento > 0) {
                        elemento--;
                    } else {
                        elemento = 1;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (elemento < 1) {
                        elemento++;
                    } else {
                        elemento = 0;
                    }
                }
                break;
            case SALON_DE_LA_FAMA:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    estado = MENU;
                }
                break;
        }
    }

    private boolean analizarRespuesta(int opcioneEscojida, int respuetaAnalizada) {
        if (misPreguntas.get(preguntasRonda.get(respuetaAnalizada)).getCorrecta() == opcioneEscojida) {
            return true;
        }
        return false;
    }

    public Vector<Integer> obtenerPreguntasRonda() {
        Vector<Integer> preguntasRonda = new Vector<Integer>();
        while (preguntasRonda.size() < CANTIDAD_DE_PREGUNTAS) {
            int x = (int) (Math.random() * misPreguntas.size());
            if (!yaAgregada(preguntasRonda, x)) {
                preguntasRonda.add(x);
            }
        }
        return preguntasRonda;
    }

    private static boolean yaAgregada(Vector<Integer> preguntasRonda, int x) {
        for (int i : preguntasRonda) {
            if (x == i) {
                return true;
            }
        }
        return false;
    }

    private static boolean yaAgregadaModo(Vector<Vector<Integer>> preguntasRondas, int x) {
        for (Vector<Integer> v : preguntasRondas) {
            for (int i : v) {
                if (x == i) {
                    return true;
                }
            }
        }
        return false;
    }

    public void resultadoRespuesta(Graphics g) {
        if (tiempoReporte > 0) {
            tiempoReporte -= 0.1;
            if (ultimaPregunta)
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/bien.png")).getImage(), 520, 10, this);
            else
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/mal.png")).getImage(), 520, 10, this);
        } else {
            if (bloqueo && continuar) {
                bloqueo = false;
                continuar = false;
                indexPreguntas++;
                if (respuestasRonda.size() >= preguntasRonda.size()) {
                    estado = RESULTADOS;
                    misRespuestas.add(new Respuesta(preguntasRonda, respuestasRonda, tiempo));
                    Main.guardarRespuestas();
                }
            }
        }
        if (bloqueo) {
            if(ultimaPregunta)
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/seleccionada.png")).getImage(), 0, 360 + 30 * ultimaRespuestaCorrecta, this);
            else{
                //if (tiempoInterno - ((int) tiempoInterno) <= 0.5)
                if (((int) tiempoInterno)%2 == 0)
                    g.drawImage(new ImageIcon(getClass().getResource("/rec/img/erronea.png")).getImage(), 0, 360+30*respuestasRonda.get(respuestasRonda.size()-1), this);
                g.drawImage(new ImageIcon(getClass().getResource("/rec/img/correcta.png")).getImage(), 0, 360 + 30 * ultimaRespuestaCorrecta, this);
                //g.fillRect(4, 366+30*respuestasRonda.get(respuestasRonda.size()-1), 17, 18);
            }
            if (tiempo - ((int) tiempo) <= 0.5) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("", 0, 20));
                g.drawString("¡Presiona Enter!", 245, 245);
            }
        }
    }

    public static Vector<String> partirString(String cadena) {
        Vector<String> partido = new Vector<String>();
        StringTokenizer miToken = new StringTokenizer(cadena," ");
        partido.add("");
        while(miToken.hasMoreTokens()){
            String tokenTemp=miToken.nextToken();
            if(partido.get(partido.size()-1).length()+tokenTemp.length()+1<=85){
                partido.set(partido.size()-1,partido.get(partido.size()-1)+" "+tokenTemp);
            }
            else{
                partido.add(tokenTemp);
            }
        }
        return partido;
    }

    private void reproducirSonido() {
        if (sonidosBien != null && sonidosBien.size() == topeArchivosDeSonido && sonidosMal != null && sonidosMal.size() == topeArchivosDeSonido) {
            try {
                int x = (int) (Math.random() * topeArchivosDeSonido);
                if (x == topeArchivosDeSonido) {
                    x = topeArchivosDeSonido - 1;
                }
                for (int i = 0; i < topeArchivosDeSonido; i++) {
                    sonidosBien.get(i).stop();
                    sonidosBien.get(i).close();
                    sonidosMal.get(i).stop();
                    sonidosMal.get(i).close();
                }
                if (ultimaPregunta) {
                    sonidosBien.get(x).open(AudioSystem.getAudioInputStream(getClass().getResource("/rec/son/bien" + x + ".wav")));
                    sonidosBien.get(x).start();
                    System.out.println("bien" + x + ".wav");
                } else {
                    sonidosMal.get(x).open(AudioSystem.getAudioInputStream(getClass().getResource("/rec/son/mal" + x + ".wav")));
                    sonidosMal.get(x).start();
                    System.out.println("mal" + x + ".wav");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public class HiloPrincipal extends Thread {

        public void run() {
            while (true) {
                try {
                    sleep(100);
                } catch (InterruptedException ex) {
                }
                synchronized (this) {
                    repaint();
                    for (int i = 0; i < topeArchivosDeSonido; i++) {
                        if (sonidosBien != null && i < sonidosBien.size() && !sonidosBien.get(i).isRunning()) {
                            sonidosBien.get(i).close();
                        }
                        if (sonidosMal != null && i < sonidosMal.size() && !sonidosMal.get(i).isRunning()) {
                            sonidosMal.get(i).close();
                        }
                    }
                }
            }
        }
    }
}
