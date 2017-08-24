package servidorfisql.server.manejador;

import java.util.HashMap;

/**
 *
 * @author jorge
 */
public class Atributos {
    private HashMap<String, Atributo> atributos;
    
    public Atributos(){
        this.atributos = new HashMap<>();
    }
    
    
}


class Atributo{
    String tipo;
    String id;
    
    public Atributo(String tipo, String id){
        this.tipo = tipo;
        this.id = id;
    }
}