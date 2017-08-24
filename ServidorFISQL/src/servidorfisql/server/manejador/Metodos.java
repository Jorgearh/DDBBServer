package servidorfisql.server.manejador;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Metodos {
    private HashMap<String, Metodo> metodos;
    
    public Metodos(){
        this.metodos = new HashMap<>();
    }
}

class Metodo{
    boolean procedure;
    ArrayList<String> permissions;
    String name;
    Atributos parametros;
    String code;
    
    public Metodo(boolean proc, String name, String code){
        this.procedure = proc;
        this.name = name;
        this.code = code;
        
        this.permissions = new ArrayList<>();
        this.parametros = new Atributos();
    }
}