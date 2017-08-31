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
    
    public String getXml(){
        String xml = "";
        
        xml += "            <rows>\n";
        
        for(Columna c : this.columnas.values())
            xml += c.getXml();
        
        xml += "            </rows>\n";
        
        return xml;
    }

      
    void crearColumna(String tipo, String idCol, Nodo lcomp) {
        Columna col = new Columna(tipo, idCol, lcomp);
        this.columnas.put(idCol, col);
    }

    
    
    public boolean existe(String idCol) {
        return this.columnas.containsKey(idCol);
    }
    
    public String getTipoColumna(String idCol){
        return this.columnas.get(idCol).tipo;
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
    
    public Columna(String tipo, String id, Nodo lcomp){
        this.tipo = tipo;
        this.id = id;
        
        this.pk = this.fk = this.nulo = this.unique = this.autoinc = false;
        this.tabla = this.columna =  "";
        
        setComplements(lcomp);
    }

    public Columna(Nodo column){
        Nodo lcomp;
        
        this.tipo = column.getHijo(0).getHijo(0).valor;
        this.id = column.getHijo(1).getHijo(0).valor;
        this.pk = this.fk = this.nulo = this.unique = this.autoinc = false;
        this.tabla = this.columna =  "";
        
        lcomp = column.getHijo(2);
        
        
        setComplements(lcomp);
        
    }
    
    private void setComplements(Nodo lcomp){
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
    
    
    
    public String getXml(){
        String xml = "";
        
        xml += "                <row>\n"
             + "                    <type>" + this.tipo + "</type>\n"
             + "                    <name>" + this.id + "</name>\n";
        
        xml += getXmlComplementos();
        
        xml += "                </row>\n";
        
        return xml;
    }
    
    private String getXmlComplementos(){
        String xml = "                    <complementos>\n";
        
        if(this.pk) xml += "                        <complemento>PK</complemento>\n";
        
        if(this.fk) {
            xml += "                        <complemento>\n"
                 + "                            <FK>\n"
                 + "                                <tabla>" + this.tabla + "</tabla>\n"
                 + "                                <columna>" + this.columna + "</columna>\n"   
                 + "                            </FK>\n"   
                 + "                        </complemento>\n";
        }
        
        if(this.nulo) xml += "                        <complemento>NULL</complemento>\n";
        else xml += "                        <complemento>NOT NULL</complemento>\n";
        
        if(this.unique) xml += "                        <complemento>UNIQUE</complemento>\n";
        if(this.autoinc) xml += "                        <complemento>AUTOINC</complemento>\n";
        
                xml += "                    </complementos>\n";
        
        return xml;
    }
    
}