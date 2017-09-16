package servidorfisql.server.manejador;

import java.util.ArrayList;
import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Nodo;
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
        Consola.writeln("Cargando Bases de Datos... ");
        
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
            bd.guardarBackupUsqlDump();
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
    
    public String getTipoMetodo(String idDB, String idMet){
        return this.bbdd.get(idDB).metodos.getTipoMetodo(idMet);
    }
    
    public String getTipoAtributo(String idDB, String idObj, String idAtr){
        return this.bbdd.get(idDB).objetos.getTipoAtributo(idObj, idAtr);
    }
    
    public String getTipoParam(String idDB, String idMet, int index){
        return this.bbdd.get(idDB).metodos.getTipoParam(idMet, index);
    }
    
    
    public boolean tienePermisos(String idDB, String user){
        return this.bbdd.get(idDB).permissonsDB.existe(user);
    }
    
    public boolean tienePermisosTabla(String idDB, String idTable, String user) {
        return this.bbdd.get(idDB).tablas.tienePermisos(idTable, user);
    }
    
    public boolean tienePermisosObjeto(String idDB, String idObject, String user) {
        return this.bbdd.get(idDB).objetos.tienePermisos(idObject, user);
    }
    
    public boolean tienePermisosMetodo(String idDB, String idMet, String user) {
        return this.bbdd.get(idDB).metodos.tienePermisos(idMet, user);
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
        
        this.bbdd.get(idDB).metodos.crearMetodo(idProc, create);
    }

    
    
    public void eliminarBD(String idDB) {
        String dirDB = Archivos.bbddDir + idDB + "/";
        
        Archivos.eliminarDirectorio(dirDB);
        this.bbdd.remove(idDB);
        
    }
    
    public void eliminarTabla(String idDB, String idTable) {
        this.bbdd.get(idDB).tablas.eliminarTabla(idTable);
    }
    
    public void eliminarObjeto(String idDB, String idObject) {
        this.bbdd.get(idDB).objetos.eliminarObjeto(idObject);
    }

    public void eliminarMetodo(String idDB, String idMet) {
        this.bbdd.get(idDB).metodos.eliminarMetodo(idMet);
    }
    
    
    
    
    /***
     * Otorga permisos al usuario user en todos los objetos de la Base de Datos idDB
     * @param idDB
     * @param user 
     */
    public void otorgarPermisosEnTodo(String idDB, String user){
        
        BD bd = this.bbdd.get(idDB);
        
        bd.tablas.otorgarPermisosEnTodas(user);
        bd.metodos.otorgarPermisosEnTodos(user);
        bd.objetos.otorgarPermisosEnTodos(user);
    }
    
    public boolean existeObjetoUsql(String idDB, String idE, String user){
        BD bd = this.bbdd.get(idDB);
        
        if(bd.tablas.existe(idE)) return true;
        if(bd.metodos.exists(idE)) return true;
        if(bd.objetos.existeObjeto(idE)) return true;
        
        return false;
    }
    
    
    public boolean otorgarPermisosSiExiste(String idDB, String idE, String user){
        BD bd = this.bbdd.get(idDB);
        
        if(bd.tablas.otorgarPermisosSiExiste(idE, user)) return true;
        if(bd.metodos.otorgarPermisosSiExiste(idE, user)) return true;
        if(bd.objetos.otorgarPermisosSiExiste(idE, user)) return true;
        
        return false;
    }
    
    /***
     * Deniega permisos al usuario user en todos los objetos de la base de Datos idDB.
     * @param idDB
     * @param user 
     */
    public void denegarPermisosEnTodo(String idDB, String user){
        
        BD bd = this.bbdd.get(idDB);
        
        bd.tablas.denegarPermisosEnTodas(user);
        bd.metodos.denegarPermisosEnTodos(user);
        bd.objetos.denegarPermisosEnTodos(user);
    }
    
    
    /***
     * Si existe el elemento idE en la Base de Datos idDB, 
     * se le otorgan los permisos en este, al usuario user.
     * @param idDB
     * @param idE
     * @param user
     * @return 
     */
    public boolean denegarPermisosSiExiste(String idDB, String idE, String user){
        BD bd = this.bbdd.get(idDB);
        
        if(bd.tablas.denegarPermisosSiExiste(idE, user)) return true;
        if(bd.metodos.denegarPermisosSiExiste(idE, user)) return true;
        if(bd.objetos.denegarPermisosSiExiste(idE, user)) return true;
        
        return false;
    }
    
    
    
    /***
     * Deniega los permisos para todos los objetos, en todas las Bases de Datos, para un usuario.
     * @param username 
     */
    public void denegarPermisosParaUsuario(String username) {
        for(BD bd : this.bbdd.values()){
            bd.permissonsDB.denegar(username);
            bd.tablas.denegarPermisosEnTodas(username);
            bd.metodos.denegarPermisosEnTodos(username);
            bd.objetos.denegarPermisosEnTodos(username);
        }
    }
    
    

    
    
    
    public void modificarTablaEliminar(String idDB, String idTable, Nodo lid) {
        this.bbdd.get(idDB).tablas.modificarTablaEliminar(idTable, lid);
    }

    public void modificarObjetoEliminar(String idDB, String idObject, Nodo lid) {
        this.bbdd.get(idDB).objetos.modificarObjetoEliminar(idObject, lid);
    }
    
    
    public void modificarTablaAgregar(String idDB, String idTable, Nodo lcol) {
        this.bbdd.get(idDB).tablas.modificarTablaAgregar(idTable, lcol);
    }

    public void modificarObjetoAgregar(String idDB, String idObject, Nodo latr) {
        this.bbdd.get(idDB).objetos.modificarObjetoAgregar(idObject, latr);
    }
    
    public String obtenerPk(String idDB, String idTable) {
        return this.bbdd.get(idDB).tablas.obtenerPkIndex(idTable);
    }

    
    
    
    public int cantColsInsertables(String idDB, String idTable) {
        return this.bbdd.get(idDB).tablas.cantColsInsertables(idTable);
    }

    public int getCantParametros(String idDB, String idMet) {
        return this.bbdd.get(idDB).metodos.getCantParams(idMet);
    }

    
    
    boolean tablaContieneValor(String idDB, String idTabla, String idCol, String val) {
        return this.bbdd.get(idDB).tablas.tablaContieneValor(idTabla, idCol, val);
    }

    public String insertarEnTabla(String idDB, String idTable, Nodo lexp) {
        return this.bbdd.get(idDB).tablas.insertar(idTable, lexp);
    }

    public String insertarEnTabla(String idDB, String idTable, Nodo lcol, Nodo lexp) {
        return this.bbdd.get(idDB).tablas.insertar(idTable, lcol, lexp);
    }

    
    
    
    public void registrarBackup(String idDB, String sentUsql){
        this.bbdd.get(idDB).registrarInstruccion(sentUsql);
    }

    public HashMap<String, Objeto> getObjetos(String idDB, String user) {
        return this.bbdd.get(idDB).objetos.getObjetosConPermisos(user);
    }

    public HashMap<String, Metodo> getMetodos(String idDB, String user) {
        return this.bbdd.get(idDB).metodos.getMetodosConPermisos(user);
    }

    public Nodo getAstRows(String idDB, String idTable) {
        return this.bbdd.get(idDB).tablas.getAstRows(idTable);
    }

    public void setAstRows(String idDB, String idTable, Nodo rows) {
        this.bbdd.get(idDB).tablas.setAstRows(idTable, rows);
    }

    public String getBackUpUsqlDump(String idDB) {
        return this.bbdd.get(idDB).getBackUpUsqlDump();
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
    
    ArrayList<String> usqldump;
    
    
    /**
     * Constructor para crear una BD a partir de un ast usql
     * @param idDB
     * @param dirBD
     * @param pathTables 
     */
    public BD(String idDB, String dirBD, String pathTables){
        this.permissonsDB = new Permisos(Server.user);
        
        this.idDB = idDB;
        
        this.pathXmlDB = dirBD + "/" + idDB + ".xml";
        
        this.proceduresPath = dirBD + "/methods.xml";
        
        this.objectsPath = dirBD + "/objects.xml";
        
        this.tablas = new Tablas();
        this.metodos = new Metodos();
        this.objetos = new Objetos();
        
        this.usqldump = new ArrayList<>();
        //this.usqldump.add("CREAR BASE_DATOS " + this.idDB + ";");
        
    }
    
    
    
    
    /***
     * Constructor para levantar una BD a partir de archivos xml
     * @param idDB
     * @param path 
     */
    public BD(String idDB, String path){
        Nodo nodoDBFile, permisos, tables;
        Nodo backup;
        
        this.permissonsDB = new Permisos();
        this.tablas = new Tablas();
        this.metodos = new Metodos();
        this.objetos = new Objetos();
        
        this.usqldump = new ArrayList<>();
        
        this.idDB = idDB;
        this.pathXmlDB = path;
        
        nodoDBFile = Archivos.levantarXML(this.pathXmlDB);
        
        permisos = nodoDBFile.getHijo(0);
        this.proceduresPath = nodoDBFile.getHijo(1).getHijo(0).getHijo(0).valor;
        this.objectsPath = nodoDBFile.getHijo(2).getHijo(0).getHijo(0).valor;
        tables = nodoDBFile.getHijo(3);
        
        this.permissonsDB.crearPermisos(permisos);
        
        this.tablas.cargarTablas(tables);
        this.metodos.cargarMetodos(this.proceduresPath);
        this.objetos.cargarObjetos(this.objectsPath);
        
        cargarBackupUsqlDump();
        
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
    
    public void guardarBackupUsqlDump(){
        String xml = "";
        
        xml += "<usqldump>\n";
        
        for(String instr : this.usqldump){
            xml += "<instr>\"" + instr + "\"</instr>\n"; 
        }
        
        xml += "</usqldump>\n";
        
        Archivos.escribirArchivo(Archivos.bbddDir + this.idDB + "/backup.xml", xml);
    }
    
    private void cargarBackupUsqlDump(){
        Nodo backup = Archivos.levantarXML(Archivos.bbddDir + this.idDB + "/backup.xml");
        
        for(Nodo instr : backup.hijos){
            String instruccion = instr.getHijo(0).valor;
            this.usqldump.add(instruccion);
        }
    }

    String getBackUpUsqlDump() {
        String instrucciones = "";
        
        for(String instr : this.usqldump){
            instrucciones += instr + "\n";
        }
        
        return instrucciones;
    }

    void registrarInstruccion(String sentUsql) {
        if(!this.usqldump.contains(sentUsql))
            usqldump.add(sentUsql);
    }
    
}