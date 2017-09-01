package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class Objetos {
    private final HashMap<String, Objeto> objetos;
    
    public Objetos(){
        this.objetos = new HashMap<>();
    }
    
    
    public void cargarObjetos(String path){
        Nodo objectFiles = Archivos.levantarXML(path);
        Objeto obj;
        int cont = 1;
        
        Consola.writeln("Cargando objetos...");
        
        for(Nodo object : objectFiles.hijos){
            Consola.writeln("    objeto" + cont++ + "/" + objectFiles.hijos.size());
            
            obj = new Objeto(object);
            this.objetos.put(obj.id, obj);
        }
    }
    
    public void guardarObjectsFile(String path){
        String xml = "";
        
        xml += "<ObjectsFile>";
        
        for(Objeto obj : this.objetos.values()){
            xml += obj.getXml();
        }
        
        xml += "</ObjectsFile>";
        
        Archivos.escribirArchivo(path, xml);
    }

    
    
    boolean existeObjeto(String idObj) {
        return this.objetos.containsKey(idObj);
    }

    boolean existeAtributo(String idObj, String idAtr) {
        return this.objetos.get(idObj).atributes.exists(idAtr);
    }

    void crearObjeto(String idObject, Nodo latr) {
        Objeto obj = new Objeto(idObject, latr);
        this.objetos.put(idObject, obj);
    }
}


class Objeto{
    Permisos permissions;
    String id;
    Atributos atributes;
    
    
    public Objeto(Nodo objeto){
        Nodo nper, natr;
        
        this.permissions = new Permisos();
        this.atributes = new Atributos();
        
        nper = objeto.getHijo(0);
        this.id = objeto.getHijo(1).getHijo(0).valor;
        natr = objeto.getHijo(2);
        
        this.permissions.crearPermisos(nper);
        
        this.atributes.construirAtributos(natr);
    }
    
    
    public Objeto(String id, Nodo latr){
        this.id = id;
        this.permissions = new Permisos(Server.user);
        this.atributes = new Atributos();
        
        for(Nodo atr : latr.hijos){
            String idAtr = atr.getHijo(0).valor;
            String tipoAtr = atr.getHijo(1).valor;
            
            this.atributes.crearAtributo(idAtr, tipoAtr);
        }
    }
    
    
    public String getXml(){
        String xml = "";
        
        xml += "    <object>\n"
             + this.permissions.getXml()
             + "        <name>" + this.id + "</name>\n"
             + this.atributes.getXml()
             + "    </object>\n";
        
        return xml;
    }
    
    
}