package servidorfisql.server.manejador;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParserXML;

/**
 *
 * @author jorge
 */
public class BBDD {
    private HashMap<String, BD> bbdd;
    
    public BBDD(){
        this.bbdd = new HashMap<>();
    }
    
    public void agregarBD(String id, String path){
        this.bbdd.put(id, new BD(id, path));
    }
    
    /***
     * Recorre los objetos bd
     * Lee y parsea los xml asociados a cada bd
     * Llama al metodo que carga a memoria cada bd
     */
    public void cargarBBDD(){
        Nodo ast;
        
        for(BD bd : this.bbdd.values()){
            String pathBD = bd.path;                                //Obtener la ruta de la bd
            ast = Archivos.levantarXML(pathBD);
            
            
        }
    }
    
    private void construirBD(){
        
    }
}


class BD{
    ArrayList<String> permissons;
    String id;
    String path;
    
    String proceduresPath;
    String objectsPath;
    
    Tablas tablas;
    Metodos metodos;
    Objetos objetos;
    
    public BD(String id, String path){
        this.id = id;
        this.path = path;
        
        this.permissons = new ArrayList<>();
        this.proceduresPath = "";
        this.objectsPath = "";
        
        this.tablas = new Tablas();
        this.metodos = new Metodos();
        this.objetos = new Objetos();
    }
    
}