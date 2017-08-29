package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.interpretes.Analizadores.Nodo;

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
        for(Nodo tabla : tablas.hijos){
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
}


class Tabla{
    Permisos permissions;
    String name;
    String rowsPath;
    Columnas columns;
    Nodo records;
    
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
        xml += "                <path>" + this.rowsPath + "</path\n";
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