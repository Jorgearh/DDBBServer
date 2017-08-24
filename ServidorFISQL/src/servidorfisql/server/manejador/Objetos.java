package servidorfisql.server.manejador;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Objetos {
    private HashMap<String, Objeto> objetos;
    
    public Objetos(){
        this.objetos = new HashMap<>();
    }
    
}


class Objeto{
    ArrayList<String> permissions;
    String id;
    Atributos atributos;
    
    public Objeto(String id){
        this.id = id;
        this.permissions = new ArrayList<>();
        this.atributos = new Atributos();
    }
}