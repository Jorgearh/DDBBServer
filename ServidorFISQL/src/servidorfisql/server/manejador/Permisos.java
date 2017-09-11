package servidorfisql.server.manejador;

import java.util.ArrayList;
import servidorfisql.interpretes.Nodo;

/**
 *
 * @author jorge
 */
public class Permisos {
    private final ArrayList<String> permisos;
    
    
    
    public Permisos(String user){
        this.permisos = new ArrayList<>();
        
        this.permisos.add("admin");
        this.otorgar(user);
    }
    
    public void otorgar(String user){
        if(!this.permisos.contains(user))
            this.permisos.add(user);
    }
    
    public void denegar(String user){
        if(this.permisos.contains(user))
            this.permisos.remove(user);
    }
    
    public boolean existe(String user){
        return this.permisos.contains(user);
    }
    
    
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
        
        xml += "        <permissions>\n";
        
        for(String p : this.permisos){
            xml += "            <name>" + p + "</name>\n";
        }
        
        xml += "        </permissions>\n";
        
        return xml;
    }
}
