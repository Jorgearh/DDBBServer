package servidorfisql.server.manejador;

import java.util.ArrayList;
import servidorfisql.interpretes.Analizadores.Nodo;

/**
 *
 * @author jorge
 */
public class Permisos {
    private final ArrayList<String> permisos;
    
    public Permisos(){
        this.permisos = new ArrayList<>();
    }
    
    public void crearPermisos(Nodo permissions){
        for(Nodo p : permissions.hijos){
            this.permisos.add(p.getHijo(0).valor);
        }
    }
    
    public String getXml(){
        String xml = "";
        
        xml += "        <permissions>";
        
        for(String p : this.permisos){
            xml += "            <name>" + p + "</name>";
        }
        
        xml += "        </permissions>";
        
        return xml;
    }
}
