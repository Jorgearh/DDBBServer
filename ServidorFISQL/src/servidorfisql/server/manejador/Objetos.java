package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.interpretes.Analizadores.Nodo;

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
        
        for(Nodo object : objectFiles.hijos){
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
        
        this.permissions.crearPermisos(nper);;
        
        this.atributes.construirAtributos(natr);
    }
    
    
    public Objeto(String id){
        this.id = id;
        this.permissions = new Permisos();
        this.atributes = new Atributos();
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