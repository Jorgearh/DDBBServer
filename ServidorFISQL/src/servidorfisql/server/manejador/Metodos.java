package servidorfisql.server.manejador;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Grafica;
import servidorfisql.interpretes.Nodo;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class Metodos {
    private final HashMap<String, Metodo> metodos;
    
    public Metodos(){
        this.metodos = new HashMap<>();
    }
    
    public void cargarMetodos(String path){
        Nodo nodoMethodFile = Archivos.levantarXML(path);
        Metodo metodo;
        int cont = 1;
        
        Consola.writeln("        Cargando Metodos...");
        
        for(Nodo method : nodoMethodFile.hijos){
            Consola.writeln("            metodo" + cont++ + "/" + nodoMethodFile.hijos.size());
            metodo = new Metodo(method);
            this.metodos.put(metodo.idMethod, metodo);
        }
    }
    
    public void guardarMethodsFile(String path){
        String xml = "";
        
        xml += "<MethodFile>\n";
        
        for(Metodo m : this.metodos.values()){
            xml += m.getXml();
        }
        
        xml += "</MethodFile>\n";
        
        Archivos.escribirArchivo(path, xml);
    }

    
    boolean exists(String idMet) {
        return this.metodos.containsKey(idMet);
    }

    void crearMetodo(String idProc, Nodo proc) {
        Metodo met = new Metodo(idProc, proc);
        this.metodos.put(idProc, met);
    }
    
    
    
    

    void denegarPermisosEnTodos(String username) {
        for(String idMet : this.metodos.keySet()){
            denegarPermisosEn(idMet, username);
        }
    }
    
    void denegarPermisosEn(String idMet, String user){
        this.metodos.get(idMet).permissions.denegar(user);
    }
    
    /***
     * Si existe un metodo con el nombre idE, se deniegan permisos en este, para el usuario user
     * @param idE
     * @param user
     * @return true si se otorgan los permisos, false si no existe el metodo.
     */
    boolean denegarPermisosSiExiste(String idE, String user){
        if(this.metodos.containsKey(idE)){
            this.metodos.get(idE).permissions.denegar(user);
            return true;
        }
        return false;
    }
    
    
    void otorgarPermisosEnTodos(String username) {
        for(String idMet : this.metodos.keySet()){
            otorgarPermisosEn(idMet, username);
        }
    }
    
    void otorgarPermisosEn(String idMet, String user){
        this.metodos.get(idMet).permissions.otorgar(user);
    }
    
    
    /***
     * Si existe un metodo con el nombre idE, se otorgan permisos en este, para el usuario user
     * @param idE
     * @param user
     * @return true si se otorgan los permisos, false si no existe el metodo.
     */
    boolean otorgarPermisosSiExiste(String idE, String user){
        if(this.metodos.containsKey(idE)){
            this.metodos.get(idE).permissions.otorgar(user);
            return true;
        }
        return false;
    }
    
    
    

    boolean tienePermisos(String idMet, String user) {
        return this.metodos.get(idMet).permissions.existe(user);
    }

    void eliminarMetodo(String idMet) {
        this.metodos.remove(idMet);
    }

    String getTipoMetodo(String idMet) {
        return this.metodos.get(idMet).tipoMetodo;
    }

    int getCantParams(String idMet) {
        return this.metodos.get(idMet).parametros.hijos.size();
    }

    String getTipoParam(String idMet, int index) {
        return this.metodos.get(idMet).parametros.getHijo(index).getHijo(0).valor;
    }

    HashMap<String, Metodo> getMetodosConPermisos(String user) {
        HashMap<String, Metodo> methods = new HashMap<>();
        
        for(Metodo m : this.metodos.values()){
            if(m.permissions.existe(user))
                methods.put(m.idMethod, m);
        }
        
        return methods;
    }

    
    
}

//class Metodo{
//    String tipoMetodo;
//    Permisos permissions;
//    String idMethod;
//    Nodo parametros;
//    Nodo lsent;
//    
//    /***
//     * Constructor de un objeto Metodo
//     * Construido a pratir de un nodo xml del archivo de metodos de una bd
//     * @param method 
//     */
//    public Metodo(Nodo method){
//        Nodo nodoPermisos, nodoParams, nodoSrc;
//        
//        nodoPermisos = method.getHijo(0);
//        nodoParams = method.getHijo(3);
//        nodoSrc = method.getHijo(4);
//        
//        this.permissions = new Permisos();
//        this.permissions.crearPermisos(nodoPermisos);
//        
//        this.idMethod = method.getHijo(1).getHijo(0).valor;
//
//        this.tipoMetodo = method.getHijo(2).getHijo(0).valor;
//        
//        this.parametros = construirAstParametros(nodoParams);
//        
//        this.lsent = construirAstSentencias(nodoSrc.getHijo(0));
//     
//    }
//    
//    
//    /***
//     * Construir un Metodo a partir de un nodo USQL
//     * @param idMet
//     * @param met 
//     */
//    public Metodo(String idMet, Nodo met){
//        
//        this.permissions = new Permisos(Server.user);
//        this.idMethod = idMet;
//        this.parametros = met.getHijo(1);
//        this.lsent = met.getHijo(2);
//        this.tipoMetodo = met.hijos.size() == 4 ? met.getHijo(3).valor : "VOID";
//    }
//    
//    
//    private Nodo construirAstParametros(Nodo nodoLpar){
//        Nodo lpar = new Nodo("LPAR");
//        
//        for(Nodo param : nodoLpar.hijos){
//            Nodo par = new Nodo("PAR");
//            par.agregarHijos(new Nodo("TIPO", param.token), new Nodo("VAR", param.getHijo(0).valor));
//            lpar.agregarHijo(par);
//        }
//        try {
//            new Grafica().graficar(lpar, "/home/jorge/Escritorio/ASTs/usql/metodos/parametros_" + this.idMethod);
//        } catch (IOException ex) {
//            Logger.getLogger(Metodo.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return lpar;
//    }
//    
//    private Nodo construirAstSentencias(Nodo astXml){
//        
//        Nodo nodo;
//        Nodo token, valor, hijos;
//        
//        token = astXml.getHijo(0);
//        valor = astXml.getHijo(1);
//        hijos = astXml.getHijo(2);
//        
//        nodo = new Nodo(token.getHijo(0).valor);
//        
//        if(valor.hijos.size() > 0) nodo.setValor(valor.getHijo(0).valor);
//        
//        for(Nodo hijo : hijos.hijos){
//            nodo.agregarHijo(construirAstSentencias(hijo));
//        }
//        
//        try {
//            new Grafica().graficar(nodo, "/home/jorge/Escritorio/ASTs/usql/metodos/sentencias_" + this.idMethod);
//        } catch (IOException ex) {
//            Logger.getLogger(Metodo.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return nodo;
//    }
//    
//    
//    
//    
//    public String getXml(){
//        String xml = "";
//        
//        xml = "    <method>\n"
//            + this.permissions.getXml()
//            + "        <name>" + this.idMethod + "</name>\n"
//            + "        <type>" + this.tipoMetodo + "</type>\n"
//            + paramsToXml()
//            + "        <src>\n" 
//            + lsentToXml(this.lsent)
//            + "        </src>\n"
//            + "    </method>\n";
//        
//        return xml;
//    }
//    
//    private String paramsToXml(){
//        String xml;
//        
//        xml = "        <params>\n";
//        
//        for(Nodo par : this.parametros.hijos){
//            String tipo = par.getHijo(0).valor;
//            String id = par.getHijo(1).valor;
//            
//            xml += "            <" + tipo + ">" + id + "</" + tipo + ">\n";
//        }
//        
//        xml += "        </params>\n" ;
//        
//        return xml;
//    }
//    
//    private String lsentToXml(Nodo nodo){
//        String xml;
//        
//        xml = "            <nodo>\n";
//        
//        xml += "                        <token>" + nodo.token + "</token>\n";
//        xml += "                        <valor>" + nodo.valor + "</valor>\n";
//        xml += "                        <hijos>\n";
//        for(Nodo hijo : nodo.hijos)
//            xml += lsentToXml(hijo);
//        xml += "                        </hijos>\n";
//        xml += "            </nodo>\n";
//        
//        return xml;
//    }
//}