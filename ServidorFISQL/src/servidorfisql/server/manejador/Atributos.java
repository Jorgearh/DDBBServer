package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.interpretes.Nodo;

/**
 *
 * @author jorge
 */
public class Atributos {
    public final HashMap<String, Atributo> atributos;
    
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

    void eliminarAtributo(String idAtr) {
        this.atributos.remove(idAtr);
    }

    String getTipoAtr(String idAtr) {
        return this.atributos.get(idAtr).tipoAtributo;
    }
    
}


//class Atributo{
//    String tipoAtributo;
//    String idAtributo;
//    
//    public Atributo(String tipo, String id){
//        this.tipoAtributo = tipo;
//        this.idAtributo = id;
//    }
//    
//    public String getXml(){
//        String xml;
//        
//        xml = "            <atribute>\n"
//            + "                <type>" + this.tipoAtributo + "</type>\n"
//            + "                <name>" + this.idAtributo + "</name>\n"    
//            + "            </atribute>\n";
//        
//        return xml;
//    }
//}