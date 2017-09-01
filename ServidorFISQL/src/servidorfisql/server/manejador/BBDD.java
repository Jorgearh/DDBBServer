package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.server.Server;

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
        Consola.writeln("Cargando... ");
        
        int cont = 1;
        Nodo astMaster = Archivos.levantarXML(pathMaster);
        
        for(Nodo db : astMaster.hijos){
            Consola.writeln("    base de datos" + cont++ + "/" + astMaster.hijos.size());
            
            String id = db.getHijo(0).getHijo(0).valor;
            String path = db.getHijo(1).getHijo(0).valor;
            
            BD bd = new BD(id, path);
            this.bbdd.put(bd.idDB, bd);
        }
    }
    
    
    /***
     * Escribe el archivo maestro.
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
     * Escribe los archivos xml asociados a cada BD.
     */
    public void guardarBBDD(){
        for(BD bd : this.bbdd.values()){
            bd.guardarDataBaseFile();
            bd.metodos.guardarMethodsFile(bd.proceduresPath);
            bd.objetos.guardarObjectsFile(bd.objectsPath);
            bd.tablas.guardarTableFiles();
        }
    }
    
    
    
    public boolean existeBD(String id){
        return this.bbdd.containsKey(id);
    }
    
    public boolean existeTabla(String idDB, String idTable){
        return this.bbdd.get(idDB).tablas.existe(idTable);
    }
    
    public boolean existeColumna(String idDB, String idTable, String idCol){
        return this.bbdd.get(idDB).tablas.existeColumna(idTable, idCol);
    }
    
    public boolean existeObjeto(String idDB, String idObj){
        return this.bbdd.get(idDB).objetos.existeObjeto(idObj);
    }
    
    public boolean existeAtributo(String idDB, String idObj, String idAtr) {
        return this.bbdd.get(idDB).objetos.existeAtributo(idObj, idAtr);
    }
    
    public boolean existeMetodo(String idDB, String idMet) {
        return this.bbdd.get(idDB).metodos.exists(idMet);
    }

    
    
    
    
    
    
    public String getTipoColumna(String idDB, String idTable, String idCol){
        return this.bbdd.get(idDB).tablas.getTipoColumna(idTable, idCol);
    }
    
    
    public boolean tienePermisos(String idDB, String user){
        return this.bbdd.get(idDB).permissonsDB.existe(user);
    }
    
    
    public void crearBD(String idDB, String dirBD, String pathTables){
        this.bbdd.put(idDB, new BD(idDB, dirBD, pathTables));
    }

    public void crearTabla(String actualDB, Nodo create) {
        String idTable = create.getHijo(0).valor;
        Nodo lcampo = create.getHijo(1);
        String rowsPath = Archivos.bbddDir + actualDB + "/tables/" + idTable + ".xml";
        
        this.bbdd.get(actualDB).tablas.crearTabla(idTable, lcampo, rowsPath);
    }
    
    public void crearObjeto(String idDB, Nodo create){
        String idObject = create.getHijo(0).valor;
        Nodo latr = create.getHijo(1);
        
        this.bbdd.get(idDB).objetos.crearObjeto(idObject, latr);
    }

    public void crearProc(String idDB, Nodo create) {
        String idProc = create.getHijo(0).valor;
        
        this.bbdd.get(idDB).metodos.crearProc(idProc, create);
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
    
    
    public BD(String idDB, String dirBD, String pathTables){
        this.permissonsDB = new Permisos(Server.user);
        
        this.idDB = idDB;
        
        this.pathXmlDB = dirBD + "/" + idDB + ".xml";
        
        this.proceduresPath = dirBD + "/methods.xml";
        
        this.objectsPath = dirBD + "/objects.xml";
        
        this.tablas = new Tablas();
        this.metodos = new Metodos();
        this.objetos = new Objetos();
        
        
    }
    
    
    
    
    /***
     * Constructor para levantar una BD a partir de archivos xml
     * @param id
     * @param path 
     */
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
        
        xml += "    <db>\n";
        xml += "        <name>" + this.idDB + "</name>\n";
        xml += "        <path>" + this.pathXmlDB + "</path>\n";
        xml += "    </db>\n";
        
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