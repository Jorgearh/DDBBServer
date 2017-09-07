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
            this.columnas.put(col.idColumna, col);
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
        return this.columnas.get(idCol).tipoColumna;
    }

    void eliminarColumna(String idCol) {
        this.columnas.remove(idCol);
    }

    String obtenerPk() {
        String pk = null;
        for(Columna c : this.columnas.values()){
            if(c.pk)
                return c.idColumna;
        }
        return pk;
    }

}


class Columna{
    String tipoColumna;
    String idColumna;
    
    /***
     * COMPLEMENTOS
     */
    boolean pk;
    boolean fk;
    String tablaRef, columnaRef;
    boolean nulo;
    boolean unique;
    boolean autoinc;
    
    public Columna(String tipo, String id, Nodo lcomp){
        this.tipoColumna = tipo;
        this.idColumna = id;
        
        this.pk = this.fk = this.unique = this.autoinc = false;
        this.nulo = true;
        this.tablaRef = this.columnaRef =  "";
        
        setComplements(lcomp);
    }

    public Columna(Nodo column){
        Nodo lcomp;
        
        this.tipoColumna = column.getHijo(0).getHijo(0).valor;
        this.idColumna = column.getHijo(1).getHijo(0).valor;
        this.pk = this.fk = this.unique = this.autoinc = false;
        this.nulo = true;
        this.tablaRef = this.columnaRef =  "";
        
        lcomp = column.getHijo(2);
        
        
        setComplements(lcomp);
        
    }
    
    private void setComplements(Nodo lcomp){
        for(Nodo comp : lcomp.hijos){
            
            switch(comp.token){
                case "PK":
                    this.pk = true;
                    this.nulo = false;
                    break;
                case "FK":
                    this.fk = true;
                    this.tablaRef = comp.getHijo(0).valor;
                    this.columnaRef = comp.getHijo(1).valor;
                break;
                case "NULO":
                    this.nulo = true;
                break;
                case "NO_NULO":
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
             + "                    <type>" + this.tipoColumna + "</type>\n"
             + "                    <name>" + this.idColumna + "</name>\n";
        
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
                 + "                                <tabla>" + this.tablaRef + "</tabla>\n"
                 + "                                <columna>" + this.columnaRef + "</columna>\n"   
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