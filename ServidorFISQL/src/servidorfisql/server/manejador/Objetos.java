package servidorfisql.server.manejador;

import java.util.HashMap;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.server.Server;

/**
 *
 * @author jorge
 */
public class Objetos {
    private final HashMap<String, Objeto> objetos;
    
    public Objetos(){
        this.objetos = new HashMap<>();
    }
    
    
    public void cargarObjetos(String path){
        Nodo objectFiles = Archivos.levantarXML(path);
        Objeto obj;
        int cont = 1;
        
        Consola.writeln("Cargando objetos...");
        
        for(Nodo object : objectFiles.hijos){
            Consola.writeln("    objeto" + cont++ + "/" + objectFiles.hijos.size());
            
            obj = new Objeto(object);
            this.objetos.put(obj.idObjeto, obj);
        }
    }
    
    public void guardarObjectsFile(String path){
        String xml = "";
        
        xml += "<ObjectsFile>\n";
        
        for(Objeto obj : this.objetos.values()){
            xml += obj.getXml();
        }
        
        xml += "</ObjectsFile>\n";
        
        Archivos.escribirArchivo(path, xml);
    }

    
    
    boolean existeObjeto(String idObj) {
        return this.objetos.containsKey(idObj);
    }

    boolean existeAtributo(String idObj, String idAtr) {
        return this.objetos.get(idObj).atributes.exists(idAtr);
    }

    void crearObjeto(String idObject, Nodo latr) {
        Objeto obj = new Objeto(idObject, latr);
        this.objetos.put(idObject, obj);
    }

    
    
    
    void denegarPermisosEnTodos(String username) {
        for(String idObj : this.objetos.keySet()){
            denegarPermisosEn(idObj, username);
        }
    }
    
    void denegarPermisosEn(String idObj, String user){
        this.objetos.get(idObj).permissions.denegar(user);
    }
    
    /***
     * Si existe un objeto con el nombre idE, se deniegan permisos en este, para el usuario user
     * @param idE
     * @param user
     * @return true si se otorgan los permisos, false si no existe el objeto.
     */
    boolean denegarPermisosSiExiste(String idE, String user){
        if(this.objetos.containsKey(idE)){
            this.objetos.get(idE).permissions.denegar(user);
            return true;
        }
        return false;
    }
    
    
    
    
    
    void otorgarPermisosEnTodos(String username) {
        for(String idObj : this.objetos.keySet()){
            otorgarPermisosEn(idObj, username);
        }
    }
    
    void otorgarPermisosEn(String idObj, String user){
        this.objetos.get(idObj).permissions.otorgar(user);
    }

    
    /***
     * Si existe un objeto con el nombre idE, se otorgan permisos en este, para el usuario user
     * @param idE
     * @param user
     * @return true si se otorgan los permisos, false si no existe el objeto.
     */
    boolean otorgarPermisosSiExiste(String idE, String user){
        if(this.objetos.containsKey(idE)){
            this.objetos.get(idE).permissions.otorgar(user);
            return true;
        }
        return false;
    }
    
    
    
    boolean tienePermisos(String idObject, String user) {
        return this.objetos.get(idObject).permissions.existe(user);
    }

    void eliminarObjeto(String idObject) {
        this.objetos.remove(idObject);
    }

    void modificarObjetoEliminar(String idObject, Nodo lid) {
        for(Nodo atr : lid.hijos){
            this.objetos.get(idObject).atributes.eliminarAtributo(atr.valor);
        }
    }

    void modificarObjetoAgregar(String idObject, Nodo latr) {
        latr.hijos.stream().forEach((atr) -> {
            String tipo = atr.getHijo(0).valor;
            String id = atr.getHijo(1).valor;
            this.objetos.get(idObject).atributes.crearAtributo(id, tipo);
        });
    }
}


class Objeto{
    Permisos permissions;
    String idObjeto;
    Atributos atributes;
    
    
    /***
     * A partir de un nodo XML
     * @param objeto 
     */
    public Objeto(Nodo objeto){
        Nodo nper, natr;
        
        this.permissions = new Permisos();
        this.atributes = new Atributos();
        
        nper = objeto.getHijo(0);
        this.idObjeto = objeto.getHijo(1).getHijo(0).valor;
        natr = objeto.getHijo(2);
        
        this.permissions.crearPermisos(nper);
        
        this.atributes.construirAtributos(natr);
    }
    
    
    /***
     * A partir de un nodo USQL
     * @param id
     * @param latr 
     */
    public Objeto(String id, Nodo latr){
        this.idObjeto = id;
        this.permissions = new Permisos(Server.user);
        this.atributes = new Atributos();
        
        for(Nodo atr : latr.hijos){
            String idAtr = atr.getHijo(1).valor;
            String tipoAtr = atr.getHijo(0).valor;
            
            this.atributes.crearAtributo(idAtr, tipoAtr);
        }
    }
    
    
    public String getXml(){
        String xml = "";
        
        xml += "    <object>\n"
             + this.permissions.getXml()
             + "        <name>" + this.idObjeto + "</name>\n"
             + this.atributes.getXml()
             + "    </object>\n";
        
        return xml;
    }
    
    
}