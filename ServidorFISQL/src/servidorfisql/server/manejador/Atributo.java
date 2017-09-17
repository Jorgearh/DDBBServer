package servidorfisql.server.manejador;

/**
 *
 * @author jorge
 */
public class Atributo {
    public String tipoAtributo;
    public String idAtributo;
    
    public Atributo(String tipo, String id){
        this.tipoAtributo = tipo;
        this.idAtributo = id;
    }
    
    public String getXml(){
        String xml;
        
        xml = "            <atribute>\n"
            + "                <type>" + this.tipoAtributo + "</type>\n"
            + "                <name>" + this.idAtributo + "</name>\n"    
            + "            </atribute>\n";
        
        return xml;
    }
}
