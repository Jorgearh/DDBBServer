package servidorfisql.server.manejador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import servidorfisql.Constantes;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Error;
import servidorfisql.interpretes.Grafica;
import servidorfisql.interpretes.Nodo;
import servidorfisql.interpretes.Analizadores.USQL.analizador.ParserUSQL;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParserXML;

/**
 *
 * @author jorge
 */
public class Archivos implements Constantes{
    
    /*NOMBRES*/
    private static final String dbms = "FISQL";
    
    /*DIRECTORIOS*/
        //BASE
    private static final String rootDir = "/home/jorge/" + dbms + "/";
        //SISTEMA DE ARCHIVOS
    public static final String bbddDir = rootDir + "bbdd/";
    
    /*ARCHIVOS*/
    private static final String masterFile = rootDir + "maestro.xml";
    private static final String usersFile = rootDir + "usuarios.xml";
    
    
    private static File folder;
    
    public static Usuarios usuarios;
    public static BBDD bbdd;
    
    /***
     * Inicializa el sistema de archivos, de no existeir.
     */
    public static void inicializarSistemaDeArchivos(){
        Consola.writeln("Inicializando sistema de archivos...");
        
        folder = new File(rootDir);
        usuarios = new Usuarios();
        bbdd = new BBDD();
        
        if(!folder.exists()){
            //CREAR DIRECTORIO RAIZ FISQL
            new File(bbddDir).mkdirs();
            
            //CREAR ARCHIVO DE USUARIOS
            escribirArchivo(usersFile, 
                    "<UsersFile>\n" +
                    "	<user>\n" +
                    "		<name>admin</name>\n" +
                    "		<password>admin</password>\n" +
                    "	</user>\n" +
                    "</UsersFile>");
            
            //CREAR ARCHIVO MAESTRO
            escribirArchivo(masterFile, 
                    "<MasterFile>\n" +
                    "</MasterFIle>");
            
            
        }
        
        Consola.writeln("Sistema de archivos inicializado exitosamente...");
    }
    
    
    /***
     * Lectura de archivos xml y carga de estructura de usuarios.
     */
    public static void cargarUsuarios(){
        Consola.writeln("Cargando a memoria los usuarios del sistema...");
        
        Nodo astUsuarios = levantarXML(usersFile);
        
        for (Nodo user : astUsuarios.hijos) {
            String username = user.getHijo(0).getHijo(0).valor;
            String password = user.getHijo(1).getHijo(0).valor;
            
            usuarios.agregarUsuario(username, password);
        }
        
        //{Archivos.usuarios.imprimirUsuarios();}
        Consola.writeln("Usuarios cargados exitosamente...");
    }
    
    public static void guardarUsuarios(){
        String xml = Archivos.usuarios.getXmlUsuarios();
        escribirArchivo(usersFile, xml);
    }
    
    
    /***
     * Lectura de archivos xml que integran las bases de datos y
     * carga a memoria de la informacion de los objetos de la base de datos.
     */
    public static void cargarInformacion(){       
        Consola.writeln("Cargando bases de datos...");
        try{
        
            Archivos.bbdd.cargarMaserFile(masterFile);
            Consola.writeln("Bases de datos cargadas exitosamente...");
        
        }catch(Exception e){
            Consola.writeln("ERROR CARGANDO MASTERFILE...");
            Consola.writeln(e.getLocalizedMessage());
        }
        
    }
    
    public static void guardarInformacion(){
        Consola.writeln("Guardando bases de datos...");
        Archivos.bbdd.guardarMasterFile(masterFile);
        Archivos.bbdd.guardarBBDD();
        Consola.writeln("Bases de datos guardadas exitosamente...");
    }
    
    
    
    
    
    
    public static Nodo levantarXML(String path){
        ParserXML parserXML;
        String xml;
        Nodo astXML = null;
        
        xml = leerArchivo(path);
        
        try{
            parserXML = new ParserXML(new StringReader(xml));
            astXML = parserXML.INI();
            new Grafica().graficar(astXML, "/home/jorge/Escritorio/ASTs/xml/" + new File(path).getName());
            
        } catch (ParseException ex) {
            Consola.writeln("ERROR EN EL PARSEO DE ARCHIVO XML {" + new File(path).getName() + "}");
            Consola.writeln(ex.getLocalizedMessage());
        } catch (IOException ex) {
            Consola.writeln(ex.getLocalizedMessage());
        }
        
        return astXML;
    }
    
    public static Nodo parsearUSQL(int codigo, String instruccion) throws IOException{
        ParserUSQL parserUSQL;
        Nodo astUSQL = null;
        
        try {
            parserUSQL = new ParserUSQL(new StringReader(instruccion));
            astUSQL = parserUSQL.INI();
            
            new Grafica().graficar(astUSQL, "/home/jorge/Escritorio/ASTs/usql/instruccion");
            
        }catch(servidorfisql.interpretes.Analizadores.USQL.analizador.TokenMgrError ex){
            Consola.writeln(ex.getLocalizedMessage());
            String token = "ERROR";
            String valor = Error.lenguaje(
                    codigo, 
                    "USQL", 
                    instruccion, 
                    "Lexico", 
                    0, 
                    0, 
                    ex.getMessage());
            astUSQL = new Nodo(token, valor);
            
        }catch (servidorfisql.interpretes.Analizadores.USQL.analizador.ParseException ex) {
            Consola.writeln(ex.getLocalizedMessage());
            String token = "ERROR";
            String valor = Error.lenguaje(
                    codigo, 
                    "USQL", 
                    instruccion, 
                    "Sintactico", 
                    ex.currentToken.beginLine, 
                    ex.currentToken.beginColumn, 
                    ex.getMessage());
            astUSQL = new Nodo(token, valor);
        }
        
        return astUSQL;
    }
    
    static String leerArchivo(String path){
        File f;
        FileReader fr;
        BufferedReader br;
        String linea;
        String texto;
        
        fr = null;
        texto = "";
        
        try{
            f = new File(path);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            
            while((linea = br.readLine()) != null)
                texto += linea + "\n";
            
        }catch(IOException ioe){
            Consola.writeln("Error leyendo archivo " + path + " \n" + ioe.getMessage());
            return null;
        }finally{
            try{
                if(null != fr)
                    fr.close();
            }catch(IOException ioe2){
                Consola.writeln("Error cerrando fichero.\n" + ioe2.getMessage());
                return null;
            }
        }
        
        return texto;
    }
    
    public static void escribirArchivo(String path, String content){
        FileWriter fw;
        BufferedWriter bw;

        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);

            bw.write(content);

            bw.close();
            fw.close();
            
        } catch (IOException ex) {
            Consola.writeln("Error escribiendo el archivo [" + path + "]");
        }
    }
            
    public static void crearDirectorio(String path){
        File file = new File(path);
        
        if(!file.exists())
            file.mkdirs();
        else
            Consola.writeln("Error, ya existe el directorio [" + path + "]");
    }

    public static void eliminarDirectorio(String path){
        File file = new File(path);
        
        if(file.exists()){
            if(file.isFile()){
                file.delete();
                Consola.writeln("Eliminando " + path);
            }else{
                
                String[] list = file.list();
                
                for(String l : list){
                    Consola.writeln("Eliminando " + path + l);
                    new File(path + l).delete();
                }
                
                Consola.writeln("Eliminando " + path);
                new File(path).delete();
            }
        }else{
            Consola.writeln("No se ha eliminado [" + path + "] porque no existe");
        }
    }

}
