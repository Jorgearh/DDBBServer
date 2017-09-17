package servidorfisql.server.manejador;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidorfisql.interpretes.EjecucionSSL;
import servidorfisql.interpretes.Grafica;
import servidorfisql.interpretes.Nodo;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class Metodo {
    public String tipoMetodo;
    Permisos permissions;
    public String idMethod;
    public Nodo parametros;
    public Nodo lsent;
    
    /***
     * Constructor de un objeto Metodo
     * Construido a pratir de un nodo xml del archivo de metodos de una bd
     * @param method 
     */
    public Metodo(Nodo method){
        Nodo nodoPermisos, nodoParams, nodoSrc;
        
        nodoPermisos = method.getHijo(0);
        nodoParams = method.getHijo(3);
        nodoSrc = method.getHijo(4);
        
        this.permissions = new Permisos();
        this.permissions.crearPermisos(nodoPermisos);
        
        this.idMethod = method.getHijo(1).getHijo(0).valor;

        this.tipoMetodo = method.getHijo(2).getHijo(0).valor;
        
        this.parametros = construirAstParametros(nodoParams);
        
        this.lsent = construirAstSentencias(nodoSrc.getHijo(0));
     
    }
    
    
    /***
     * Construir un Metodo a partir de un nodo USQL
     * @param idMet
     * @param met 
     */
    public Metodo(String idMet, Nodo met){
        
        this.permissions = new Permisos(Server.user);
        this.idMethod = idMet;
        this.parametros = met.getHijo(1);
        this.lsent = met.getHijo(2);
        this.tipoMetodo = met.hijos.size() == 4 ? EjecucionSSL.ejecutar.getTipo(met.getHijo(3).valor) : "VOID";
    }
    
    
    private Nodo construirAstParametros(Nodo nodoLpar){
        Nodo lpar = new Nodo("LPAR");
        
        for(Nodo param : nodoLpar.hijos){
            Nodo par = new Nodo("PAR");
            par.agregarHijos(new Nodo("TIPO", param.token), new Nodo("VAR", param.getHijo(0).valor));
            lpar.agregarHijo(par);
        }
        try {
            new Grafica().graficar(lpar, "/home/jorge/Escritorio/ASTs/usql/metodos/parametros_" + this.idMethod);
        } catch (IOException ex) {
            Logger.getLogger(Metodo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lpar;
    }
    
    private Nodo construirAstSentencias(Nodo astXml){
        
        Nodo nodo;
        Nodo token, valor, hijos;
        
        token = astXml.getHijo(0);
        valor = astXml.getHijo(1);
        hijos = astXml.getHijo(2);
        
        nodo = new Nodo(token.getHijo(0).valor);
        
        if(valor.hijos.size() > 0) nodo.setValor(valor.getHijo(0).valor);
        
        for(Nodo hijo : hijos.hijos){
            nodo.agregarHijo(construirAstSentencias(hijo));
        }
        
        try {
            new Grafica().graficar(nodo, "/home/jorge/Escritorio/ASTs/usql/metodos/sentencias_" + this.idMethod);
        } catch (IOException ex) {
            Logger.getLogger(Metodo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return nodo;
    }
    
    
    
    
    public String getXml(){
        String xml = "";
        
        xml = "    <method>\n"
            + this.permissions.getXml()
            + "        <name>" + this.idMethod + "</name>\n"
            + "        <type>" + this.tipoMetodo + "</type>\n"
            + paramsToXml()
            + "        <src>\n" 
            + lsentToXml(this.lsent)
            + "        </src>\n"
            + "    </method>\n";
        
        return xml;
    }
    
    private String paramsToXml(){
        String xml;
        
        xml = "        <params>\n";
        
        for(Nodo par : this.parametros.hijos){
            String tipo = par.getHijo(0).valor;
            String id = par.getHijo(1).valor;
            
            xml += "            <" + tipo + ">" + id + "</" + tipo + ">\n";
        }
        
        xml += "        </params>\n" ;
        
        return xml;
    }
    
    private String lsentToXml(Nodo nodo){
        String xml;
        
        xml = "            <nodo>\n";
        
        xml += "                <token>\"" + nodo.token + "\"</token>\n";
        xml += "                <valor>\"" + nodo.valor + "\"</valor>\n";
        xml += "                <hijos>\n";
        for(Nodo hijo : nodo.hijos)
            xml += lsentToXml(hijo);
        xml += "                </hijos>\n";
        xml += "            </nodo>\n";
        
        return xml;
    }
}
