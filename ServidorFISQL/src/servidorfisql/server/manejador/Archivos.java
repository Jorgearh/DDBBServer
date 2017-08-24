package servidorfisql.server.manejador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import servidorfisql.gui.Consola;
import servidorfisql.interpretes.InterpreteXML;

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
    private static InterpreteXML interpreteXML;
    
    public static Usuarios usuarios;
    
    
    public static void inicializarSistemaDeArchivos(){
        folder = new File(rootDir);
        interpreteXML = new InterpreteXML();
        
        usuarios = new Usuarios();
        
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
        String xml;
        
        xml = leerArchivo(usersFile);
        interpreteXML.analizar(xml);
        {Archivos.usuarios.imprimirUsuarios();}
    }
    
    public static void guardarUsuarios(){
        String xml;
        
        xml = Archivos.usuarios.getXmlUsuarios();
        escribirArchivo(usersFile, xml);
    }
    
    
    public static void cargarInformacion(String user){
        
    }
    
    public static void guardarInformacion(){
        
    }
    
    
    
    
    
    
    
    
    private static String leerArchivo(String path){
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
    
    private static void escribirArchivo(String path, String content){
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
