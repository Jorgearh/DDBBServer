package servidorfisql.server.manejador;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Tablas {
    private HashMap<String, Tabla> tablas;
    
    public Tablas(){
        this.tablas = new HashMap<>();
    }
}


class Tabla{
    ArrayList<String> permissions;
    String name;
    String rowsPath;
    Columnas columnas;
    
    public Tabla(String name, String rowsPath){
        this.name = name;
        this.rowsPath = rowsPath;
        
        this.permissions = new ArrayList<>();
        this.columnas = new Columnas();
    }
}