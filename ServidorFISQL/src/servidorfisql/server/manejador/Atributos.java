package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.interpretes.Analizadores.Nodo;

/**
 *
 * @author jorge
 */
public class Atributos {
    private final HashMap<String, Atributo> atributos;
    
    public Atributos(){
        this.atributos = new HashMap<>();
    }
    
    
    public void construirAtributos(Nodo latr){
        Atributo atr;
        
        for(Nodo a : latr.hijos){
            String t = a.getHijo(0).getHijo(0).valor;
            String id = a.getHijo(1).getHijo(0).valor;
            
            atr = new Atributo(t, id);
            this.atributos.put(id, atr);
        }
    }
    
    public String getXml(){
        String xml = "";
        
        xml += "        <atributes>\n";
        
        for(Atributo a : this.atributos.values()){
            xml += a.getXml();
        }
        
        xml += "        </atributes>\n";
        
        return xml;
    }

    
    
    boolean exists(String idAtr) {
        return this.atributos.containsKey(idAtr);
    }

    void crearAtributo(String idAtr, String tipoAtr) {
        Atributo atr = new Atributo(tipoAtr, idAtr);
        
        this.atributos.put(idAtr, atr);
    }
    
}


class Atributo{
    String tipo;
    String id;
    
    public Atributo(String tipo, String id){
        this.tipo = tipo;
        this.id = id;
    }
    
    public String getXml(){
        String xml = "";
        
        xml = "            <atribute>\n"
            + "                <type>" + this.tipo + "</type>\n"
            + "                <name" + this.id + "</name>\n"    
            + "            </atribute>\n";
        
        return xml;
    }
}