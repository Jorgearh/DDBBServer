package servidorfisql.server.manejador;

import servidorfisql.interpretes.Nodo;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class Objeto {
    Permisos permissions;
    String idObjeto;
    Atributos atributes;
    
    
    /***
     * A partir de un nodo XML
     * @param objeto 
     */
    public Objeto(Nodo objeto){
        Nodo nper, natr;
        
        this.permissions = new Permisos();
        this.atributes = new Atributos();
        
        nper = objeto.getHijo(0);
        this.idObjeto = objeto.getHijo(1).getHijo(0).valor;
        natr = objeto.getHijo(2);
        
        this.permissions.crearPermisos(nper);
        
        this.atributes.construirAtributos(natr);
    }
    
    
    /***
     * A partir de un nodo USQL
     * @param id
     * @param latr 
     */
    public Objeto(String id, Nodo latr){
        this.idObjeto = id;
        this.permissions = new Permisos(Server.user);
        this.atributes = new Atributos();
        
        for(Nodo atr : latr.hijos){
            String idAtr = atr.getHijo(1).valor;
            String tipoAtr = atr.getHijo(0).valor;
            
            this.atributes.crearAtributo(idAtr, tipoAtr);
        }
    }
    
    
    public String getXml(){
        String xml = "";
        
        xml += "    <object>\n"
             + this.permissions.getXml()
             + "        <name>" + this.idObjeto + "</name>\n"
             + this.atributes.getXml()
             + "    </object>\n";
        
        return xml;
    }
    
 
}
