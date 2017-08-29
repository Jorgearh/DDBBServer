package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.interpretes.Analizadores.Nodo;

/**
 *
 * @author jorge
 */
public class BBDD {
    private final HashMap<String, BD> bbdd;
    
    public BBDD(){
        this.bbdd = new HashMap<>();
    }
    

    public void cargarMaserFile(String pathMaster){
        Nodo astMaster = Archivos.levantarXML(pathMaster);
        
        for(Nodo db : astMaster.hijos){
            String id = db.getHijo(0).getHijo(0).valor;
            String path = db.getHijo(1).getHijo(0).valor;
            
            BD bd = new BD(id, path);
            this.bbdd.put(bd.idDB, bd);
        }
        
    }
    
    
    /***
     * Recorre los objetos bd
     * @param path
     */
    
    public void guardarMasterFile(String path){
        String xml = "";
        
        xml += "<MasterFile>\n";
        
        for(BD bd : this.bbdd.values())
            xml += bd.getXmlMaster();
        
        xml += "</MasterFile>\n";

        Archivos.escribirArchivo(path, xml);
    }
    
    
    /***
     * Escribe los archivos xml asociados a cada BD
     */
    public void guardarBBDD(){
        for(BD bd : this.bbdd.values()){
            bd.guardarDataBaseFile();
            bd.metodos.guardarMethodsFile(bd.proceduresPath);
            bd.objetos.guardarObjectsFile(bd.objectsPath);
            bd.tablas.guardarTableFiles();
        }
    }
}





class BD{
    Permisos  permissonsDB;
    String idDB;
    String pathXmlDB;
    
    String proceduresPath;
    String objectsPath;
    
    Tablas tablas;
    Metodos metodos;
    Objetos objetos;
    
    public BD(String id, String path){
        Nodo dbFile, permisos, tables;
        
        this.permissonsDB = new Permisos();
        this.tablas = new Tablas();
        this.metodos = new Metodos();
        this.objetos = new Objetos();
        
        this.idDB = id;
        this.pathXmlDB = path;
        
        dbFile = Archivos.levantarXML(this.pathXmlDB);
        
        permisos = dbFile.getHijo(0);
        this.proceduresPath = dbFile.getHijo(1).getHijo(0).getHijo(0).valor;
        this.objectsPath = dbFile.getHijo(2).getHijo(0).getHijo(0).valor;
        tables = dbFile.getHijo(3);
        
        this.permissonsDB.crearPermisos(permisos);
        
        this.tablas.cargarTablas(tables);
        this.metodos.cargarMetodos(this.proceduresPath);
        this.objetos.cargarObjetos(this.objectsPath);
        
    }
    
    public String getXmlMaster(){
        String xml = "";
        
        xml += "        <db>\n";
        xml += "            <name>" + this.idDB + "</name>";
        xml += "            <path>" + this.pathXmlDB + "</path>";
        xml += "        </db>\n";
        
        return xml;
    }
    
    public void guardarDataBaseFile(){
        String xml = "";
        
        xml += "<DataBaseFile>\n";
        
        xml += this.permissonsDB.getXml();
        xml += "    <methods>\n"
             + "        <path>" + this.proceduresPath + "</path>\n"
             + "    </methods>\n";
        
        xml += "    <objects>\n"
             + "        <path>" + this.objectsPath + "</path>\n"
             + "    </objects>\n";
        
        xml += this.tablas.getXml();
        
        xml += "</DataBaseFile>\n";
        
        Archivos.escribirArchivo(this.pathXmlDB, xml);
    }
    
}