package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Nodo;

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
    
}

class Metodo{
    boolean procedure;
    Permisos permissions;
    String id;
    String code;
    Nodo ast;
    
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
        this.ast = Archivos.parsearUSQL(this.code);
    }
    
    
    public Metodo(boolean proc, String name, String code){
        this.procedure = proc;
        this.id = name;
        this.code = code;
        
        this.permissions = new Permisos();
        this.ast = new Nodo();
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