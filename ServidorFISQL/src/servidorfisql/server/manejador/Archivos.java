package servidorfisql.server.manejador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import static servidorfisql.Constantes.FILE_AST_XML;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.Analizadores.Grafica;
import servidorfisql.interpretes.Analizadores.Nodo;
import servidorfisql.interpretes.Analizadores.USQL.analizador.ParserUSQL;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParseException;
import servidorfisql.interpretes.Analizadores.XML.analizador.ParserXML;
import servidorfisql.interpretes.InterpreteUSQL;

/**
 *
 * @author jorge
 */
public class Archivos {
    
    /*NOMBRES*/
    private static final String dbms = "FISQL";
    
    /*DIRECTORIOS*/
        //BASE
    private static final String rootDir = "/home/jorge/" + dbms + "/";
        //SISTEMA DE ARCHIVOS
    private static final String bbddDir = rootDir + "bbdd/";
    
    /*ARCHIVOS*/
    private static final String masterFile = rootDir + "maestro.xml";
    private static final String usersFile = rootDir + "usuarios.xml";
    
    
    private static File folder;
    
    public static Usuarios usuarios;
    public static BBDD bbdd;
    
    
    public static void inicializarSistemaDeArchivos(){
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
            
            
        }else{
            Consola.write("El directorio raiz ya existe...");
        }
    }
    
    
    
    public static void cargarUsuarios(){
        
        Nodo astUsuarios = levantarXML(usersFile);
        
        for (Nodo user : astUsuarios.hijos) {
            String username = user.getHijo(0).getHijo(0).valor;
            String password = user.getHijo(1).getHijo(0).valor;
            
            usuarios.agregarUsuario(username, password);
        }
        
        {Archivos.usuarios.imprimirUsuarios();}
    }
    
    public static void guardarUsuarios(){
        String xml = Archivos.usuarios.getXmlUsuarios();
        escribirArchivo(usersFile, xml);
    }
    
    
    /***
     * 
     */
    public static void cargarInformacion(){       
        
        Archivos.bbdd.cargarMaserFile(masterFile);
        
    }
    
    public static void guardarInformacion(){
        Archivos.bbdd.guardarMasterFile(masterFile);
        Archivos.bbdd.guardarBBDD();
    }
    
    
    
    
    
    
    public static Nodo levantarXML(String path){
        ParserXML parserXML;
        Grafica grafica = new Grafica();
        String xml;
        Nodo astXML = null;
        
        xml = leerArchivo(path);
        
        try{
            parserXML = new ParserXML(new StringReader(xml));
            astXML = parserXML.INI();
            grafica.graficar(astXML, FILE_AST_XML);
            
        } catch (ParseException ex) {
            Consola.write(ex.getLocalizedMessage());
            Consola.write("ERROR EN EL PARSEO DE ARCHIVO XML");
        } catch (IOException ex) {
            Consola.write(ex.getLocalizedMessage());
        }
        
        return astXML;
    }
    
    public static Nodo parsearUSQL(String instruccion){
        ParserUSQL parserUSQL;
        Nodo astUSQL = null;
        
        try {
            parserUSQL = new ParserUSQL(new StringReader(instruccion));
            astUSQL = parserUSQL.INI();
        } catch (servidorfisql.interpretes.Analizadores.USQL.analizador.ParseException ex) {
            Consola.write(ex.getLocalizedMessage());
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
            Consola.write("Error leyendo archivo " + path + " \n" + ioe.getMessage());
            return null;
        }finally{
            try{
                if(null != fr)
                    fr.close();
            }catch(IOException ioe2){
                Consola.write("Error cerrando fichero.\n" + ioe2.getMessage());
                return null;
            }
        }
        
        return texto;
    }
    
    static void escribirArchivo(String path, String content){
        FileWriter fw;
        BufferedWriter bw;

        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);

            bw.write(content);

            bw.close();
            fw.close();
            
        } catch (IOException ex) {
            Consola.write("Error escribiendo el archivo [" + path + "]");
        }
    }
            
}
