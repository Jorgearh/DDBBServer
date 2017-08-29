package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.interpretes.Analizadores.Nodo;

/**
 *
 * @author jorge
 */
public class Columnas {
    private final HashMap<String, Columna> columnas;
    
    public Columnas(){
        this.columnas = new HashMap<>();
    }
    
    public void cargarColumnas(Nodo columnas){
        for(Nodo c : columnas.hijos){
            Columna col = new Columna(c);
            this.columnas.put(col.id, col);
        }
    }
    
    public void agregarColumna(String tipo, String id, boolean pk, boolean fk, String tabla, String columna, boolean nulo, boolean unique, boolean autoinc){
        if(!this.columnas.containsKey(id)){
            this.columnas.put(id, new Columna(tipo, id, pk, fk, tabla, columna, nulo, unique, autoinc));
        }else{
            /*Responder */
        }
    }
    
    
    
    
    public String getXml(){
        String xml = "";
        
        xml += "            <rows>\n";
        
        for(Columna c : this.columnas.values())
            xml += c.getXml();
        
        xml += "            </rows>\n";
        
        return xml;
    }
}


class Columna{
    String tipo;
    String id;
    
    /***
     * COMPLEMENTOS
     */
    boolean pk;
    boolean fk;
    String tabla, columna;
    boolean nulo;
    boolean unique;
    boolean autoinc;

    public Columna(Nodo column){
        Nodo lcomp;
        
        this.tipo = column.getHijo(0).getHijo(1).valor;
        this.id = column.getHijo(1).getHijo(0).valor;
        lcomp = column.getHijo(2);
        
        
        
        for(Nodo complemento : lcomp.hijos){
            Nodo comp = complemento.getHijo(0);
            
            switch(comp.token){
                case "PK":
                    this.pk = true;
                    break;
                case "FK":
                    this.fk = true;
                    this.tabla = comp.getHijo(0).getHijo(0).valor;
                    this.columna = comp.getHijo(1).getHijo(0).valor;
                break;
                case "NULL":
                    this.nulo = true;
                break;
                case "NOT NULL":
                    this.nulo = false;
                break;
                case "UNIQUE":
                    this.unique = true;
                break;
                case "AUTOINC":
                    this.autoinc = true;
                break;
            }
        }
    }
     
    public Columna(String tipo, String id, boolean pk, boolean fk, String tabla, String columna, boolean nulo, boolean unique, boolean autoinc) {
        this.tipo = tipo;
        this.id = id;
        
        this.pk = pk;
        this.fk = fk;
        this.tabla = tabla;
        this.columna = columna;
        this.nulo = nulo;
        this.unique = unique;
        this.autoinc = autoinc;
    }
    
    
    public String getXml(){
        String xml = "";
        
        xml += "                <row>\n"
             + "                    <type>" + this.tipo + "</type>\n"
             + "                    <name>" + this.id + "</name\n";
        
        xml += getXmlComplementos();
        
        xml += "                </row>\n";
        
        return xml;
    }
    
    private String getXmlComplementos(){
        String xml = "                    <complementos>\n";
        
        if(this.pk) xml += "                        <complemento>PK</complemento>";
        
        if(this.fk) {
            xml += "                        <complemento>\n"
                 + "                            <FK>\n"
                 + "                                <tabla>" + this.tabla + "</tabla>"
                 + "                                <columna>" + this.columna + "</columna>"   
                 + "                            </FK>\n"   
                 + "                        </complemento>\n";
        }
        
        if(this.nulo) xml += "                        <complemento>NULL</complemento>";
        else xml += "                        <complemento>NOT NULL</complemento>";
        
        if(this.unique) xml += "                        <complemento>UNIQUE</complemento>";
        if(this.autoinc) xml += "                        <complemento>AUTOINC</complemento>";
        
                xml += "                    </complementos>\n";
        
        return xml;
    }
    
}