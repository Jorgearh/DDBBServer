package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class Tablas {
    private final HashMap<String, Tabla> tablas;
    
    public Tablas(){
        this.tablas = new HashMap<>();
    }
    
    
    public void cargarTablas(Nodo tablas){
        Consola.writeln("Cargando tablas...");
        int cont = 1;
       
        for(Nodo tabla : tablas.hijos){
            Consola.writeln("    tabla" + cont++ + "/" + tablas.hijos.size());
            
            Tabla t = new Tabla(tabla);
            this.tablas.put(t.idTable, t);
        }
    }
    
    public String getXml(){
        String xml = "";
        
        xml += "    <tables>\n";
        
        for(Tabla t : this.tablas.values())
            xml += t.getXmlTable();
        
        xml += "    </tables>\n";
        
        return xml;
    }
    
    public void guardarTableFiles(){
        for(Tabla t : this.tablas.values()){
            t.guardarRowsFile();
        }
    }
    
    
    
    
    public boolean existe(String idTable){
        return this.tablas.containsKey(idTable);
    }
    
    public boolean existeColumna(String idTable, String idCol){
        return this.tablas.get(idTable).columns.existe(idCol);
    }
    
    
    public boolean tienePermisos(String idTable, String user){
        return this.tablas.get(idTable).permissions.existe(user);
    }
    
    
    public String getTipoColumna(String idTable, String idCol){
        return this.tablas.get(idTable).columns.getTipoColumna(idCol);
    }
    
    
    
    public void crearTabla(String idTable, Nodo lcampo, String rowsPath){
        Tabla t = new Tabla(idTable, lcampo, rowsPath);
        this.tablas.put(idTable, t);
    }

    
    
    
    
    void denegarPermisosEnTodas(String username) {
        for(String idTable : this.tablas.keySet()){
            denegarPermisosEn(idTable, username);
        }
    }
    
    /***
     * Si existe una tabla con el nombre idE, se deniegan permisos en esta, para el usuario user
     * @param idE
     * @param user
     * @return true si se otorgan los permisos, false si no existe la tabla.
     */
    boolean denegarPermisosSiExiste(String idE, String user){
        if(this.tablas.containsKey(idE)){
            this.tablas.get(idE).permissions.denegar(user);
            return true;
        }
        return false;
    }
     
    void denegarPermisosEn(String idTable, String user){
        this.tablas.get(idTable).permissions.denegar(user);
    }
    
    
    
    void otorgarPermisosEnTodas(String username) {
        for(String idTable : this.tablas.keySet()){
            otorgarPermisosEn(idTable, username);
        }
    }
    
    void otorgarPermisosEn(String idTable, String user){
        this.tablas.get(idTable).permissions.otorgar(user);
    }
    
    /***
     * Si existe una tabla con el nombre idE, se otorgan permisos en esta, para el usuario user
     * @param idE
     * @param user
     * @return true si se otorgan los permisos, false si no existe la tabla.
     */
    boolean otorgarPermisosSiExiste(String idE, String user){
        if(this.tablas.containsKey(idE)){
            this.tablas.get(idE).permissions.otorgar(user);
            return true;
        }
        return false;
    }
    
    
    
    
    
    

    void eliminarTabla(String idTable) {
        Archivos.eliminarDirectorio(this.tablas.get(idTable).rowsPath);
        this.tablas.remove(idTable);
    }

    void modificarTablaEliminar(String idTable, Nodo lid) {
        for(Nodo id : lid.hijos){
            this.tablas.get(idTable).columns.eliminarColumna(id.valor);
        }
    }
    
    void modificarTablaAgregar(String idTable, Nodo lcol) {
        for(Nodo col : lcol.hijos){
            String tipo = col.getHijo(0).valor;
            String id = col.getHijo(1).valor;
            Nodo lcomp = col.getHijo(2);
            
            this.tablas.get(idTable).columns.crearColumna(tipo, id, lcomp);
        }
    }

    String obtenerPkIndex(String idTable) {
        return this.tablas.get(idTable).obtenerPk();
    }

    
}








class Tabla{
    Permisos permissions;
    String idTable;
    String rowsPath;
    Columnas columns;
    Nodo records;
    
    /***
     * A partir de un nodo USQL
     * @param idTable
     * @param lcampo
     * @param rowsPath 
     */
    public Tabla(String idTable, Nodo lcampo, String rowsPath){
        this.permissions = new Permisos(Server.user);
        
        this.idTable = idTable;
        
        this.rowsPath = rowsPath;
        
        this.columns = new Columnas();
        for(Nodo col : lcampo.hijos){
            String tipo = col.getHijo(0).valor;
            String idCol = col.getHijo(1).valor;
            Nodo lcomp = col.getHijo(2);
            
            this.columns.crearColumna(tipo, idCol, lcomp);
        }
        
        this.records = new Nodo("RowsFile");
    }
    
    /***
     * A partir de un nodo Xml
     * @param tabla 
     */
    public Tabla(Nodo tabla){
        Nodo permisos, columnas;
        
        this.permissions = new Permisos();
        this.columns = new Columnas();
        
        permisos = tabla.getHijo(0);
        this.idTable = tabla.getHijo(1).getHijo(0).valor;
        this.rowsPath = tabla.getHijo(2).getHijo(0).valor;
        columnas = tabla.getHijo(3);
        
        this.permissions.crearPermisos(permisos);
        
        this.columns.cargarColumnas(columnas);
        
        this.records = Archivos.levantarXML(this.rowsPath);
    }
    
    public Tabla(String name, String rowsPath){
        this.idTable = name;
        this.rowsPath = rowsPath;
        
        this.permissions = new Permisos();
        this.columns = new Columnas();
    }
    
    public String getXmlTable(){
        String xml = "";
        
        xml += "            <table>\n";
        xml += this.permissions.getXml();
        xml += "                <name>" + this.idTable + "</name>\n";
        xml += "                <path>" + this.rowsPath + "</path>\n";
        xml += this.columns.getXml();
        xml += "            </table>\n";
        
        return xml;
    }
    
    
    public void guardarRowsFile(){
        String xml = "";
        
        xml += "<RowsFile>\n";
        
        for(Nodo row : this.records.hijos){
            xml += "    <row>\n";
            
            for(Nodo field : row.hijos){
                xml += "        <" + field.token + ">\n";
                
                xml += field.hijos.size() > 0 ? field.getHijo(0).valor : "";
                
                xml += "        </" + field.token + ">\n";
            }
            
            xml += "    </row>\n";
        }
        
        xml += "</RowsFile>\n";
        
        Archivos.escribirArchivo(this.rowsPath, xml);
    }

    String obtenerPk() {
        return this.columns.obtenerPk();
    }
    
    
}