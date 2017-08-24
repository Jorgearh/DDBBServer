package servidorfisql.server.manejador;

import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Columnas {
    private final HashMap<String, Columna> columnas;
    
    public Columnas(){
        this.columnas = new HashMap<>();
    }
    
    public void agregarColumna(String tipo, String id, boolean pk, boolean fk, String tabla, String columna, boolean nulo, boolean unique, boolean autoinc){
        if(!this.columnas.containsKey(id)){
            this.columnas.put(id, new Columna(tipo, id, pk, fk, tabla, columna, nulo, unique, autoinc));
        }else{
            /*Responder */
        }
    }
    
}


class Columna{
    String tipo;
    String id;
    
    /***
     * COMPLEMENTOS
     */
    boolean pk;
    boolean fk;
    String tabla, columna;
    boolean nulo;
    boolean unique;
    boolean autoinc;

    public Columna(String tipo, String id, boolean pk, boolean fk, String tabla, String columna, boolean nulo, boolean unique, boolean autoinc) {
        this.tipo = tipo;
        this.id = id;
        
        this.pk = pk;
        this.fk = fk;
        this.tabla = tabla;
        this.columna = columna;
        this.nulo = nulo;
        this.unique = unique;
        this.autoinc = autoinc;
    }
    
    
}