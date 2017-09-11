package servidorfisql.interpretes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author jorge
 */
public class Grafica {
    File f;
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter pw;
    int indice;

    public Grafica(){
        this.f = null;
        this.fw = null;
        this.bw = null;
        this.pw = null;
        this.indice = -1;
    }

    public void graficar(Nodo raiz, String nombre) throws IOException{
        ProcessBuilder pb;
        String archivo_origen;
        String archivo_destino;

        archivo_origen = nombre + ".txt";
        archivo_destino = nombre + ".png";

        f = new File(archivo_origen);
        fw = new FileWriter(f);
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw); 


        //**
        pw.println("graph G {");
        pw.println("node [shape= square];");

        indice = 0;
        crear_nodos(raiz);

        indice = 0;
        enlazar_nodos(raiz, 0);

        pw.println("}");
        //**


        pw.close();
        bw.close();


        try{
            pb = new ProcessBuilder("dot", "-Tpng", "-o", archivo_destino, archivo_origen);
            pb.redirectErrorStream(true);
            pb.start();
            
        }catch(Exception e){ 
            System.err.println(e.getMessage()); 
        }
    }

    private void crear_nodos(Nodo raiz) {
        if(raiz != null)
        {
            String token = raiz.token;
            String valor = raiz.valor.equals("") ? "" : "\n" + raiz.valor;
            
            pw.println("node"+indice+"[label = " +"\""+ token + " " + valor + "\","+ "];");
            indice++;
            for(Nodo nodo: raiz.hijos)
                crear_nodos(nodo);
        }
    }

    private void enlazar_nodos(Nodo raiz, int actual) {
        if(raiz != null)
        {
            for( Nodo nodo: raiz.hijos)
            {
                indice++;
                pw.println("\"node" + actual + "\"--" + "\"node" + indice+ "\"");
                enlazar_nodos(nodo,indice);
            }
        }
    }
}
