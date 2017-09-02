package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Nodo;
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
        Nodo methodFile = Archivos.levantarXML(path);
        Metodo m;
        int cont = 1;
        
        Consola.writeln("Cargando metodos...");
        
        for(Nodo method : methodFile.hijos){
            Consola.writeln("    metodo" + cont++ + "/" + methodFile.hijos.size());
            m = new Metodo(method);
            this.metodos.put(m.id, m);
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

    void denegarPermisos(String username) {
        for(Metodo m : this.metodos.values()){
            m.permissions.denegar(username);
        }
    }

    boolean tienePermisos(String idMet, String user) {
        return this.metodos.get(idMet).permissions.existe(user);
    }

    void eliminarMetodo(String idMet) {
        this.metodos.remove(idMet);
    }
    
}

class Metodo{
    boolean procedure;
    Permisos permissions;
    String id;
    String code;
    Nodo parametros;
    Nodo lsent;
    
    /***
     * Constructor de un objeto Metodo
     * Construido a pratir de un nodo xml del archivo de metodos de una bd
     * @param method 
     */
    public Metodo(Nodo method){
        Nodo permisos;
        
        this.permissions = new Permisos();
        
        permisos = method.getHijo(0);
        this.id = method.getHijo(1).getHijo(0).valor;
        this.code = method.getHijo(2).getHijo(0).valor;
        
        this.permissions.crearPermisos(permisos);
        
        /*LLAMARA PARSER USQL PARA GENERAR AST DEL CODIGO DEL METODO*/
        this.lsent = Archivos.parsearUSQL(this.code);
    }
    
    
    public Metodo(String idMet, Nodo met){
        this.procedure = met.hijos.size() == 3;
        this.permissions = new Permisos(Server.user);
        this.id = idMet;
        this.parametros = met.getHijo(1);
        this.lsent = met.getHijo(2);
    }
    
    
    public String getXml(){
        String xml = "";
        
        xml = "    <method>\n"
            + this.permissions.getXml()
            + "        <name>" + this.id + "</name>\n"    
            + "        <src>\n\n" 
            + this.code 
            + "        \n</src>\n"
            + "    </method>\n";
        
        return xml;
    }
}