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
            this.tablas.put(t.name, t);
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
    
    
    
    
    public boolean existe(String id){
        return this.tablas.containsKey(id);
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

    void denegarPermisos(String username) {
        for(Tabla t : this.tablas.values()){
            t.permissions.denegar(username);
        }
    }

    void eliminarTabla(String idTable) {
        this.tablas.remove(idTable);
    }
}


class Tabla{
    Permisos permissions;
    String name;
    String rowsPath;
    Columnas columns;
    Nodo records;
    
    public Tabla(String idTable, Nodo lcampo, String rowsPath){
        this.permissions = new Permisos(Server.user);
        
        this.name = idTable;
        
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
    
    public Tabla(Nodo tabla){
        Nodo permisos, columnas;
        
        this.permissions = new Permisos();
        this.columns = new Columnas();
        
        permisos = tabla.getHijo(0);
        this.name = tabla.getHijo(1).getHijo(0).valor;
        this.rowsPath = tabla.getHijo(2).getHijo(0).valor;
        columnas = tabla.getHijo(3);
        
        this.permissions.crearPermisos(permisos);
        
        this.columns.cargarColumnas(columnas);
        
        this.records = Archivos.levantarXML(this.rowsPath);
    }
    
    public Tabla(String name, String rowsPath){
        this.name = name;
        this.rowsPath = rowsPath;
        
        this.permissions = new Permisos();
        this.columns = new Columnas();
    }
    
    public String getXmlTable(){
        String xml = "";
        
        xml += "            <table>\n";
        xml += this.permissions.getXml();
        xml += "                <name>" + this.name + "</name>\n";
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
    
    
}