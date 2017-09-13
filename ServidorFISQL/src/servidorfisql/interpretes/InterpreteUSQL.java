package servidorfisql.interpretes;

import java.util.ArrayList;
import servidorfisql.Constantes;
import servidorfisql.gui.Consola;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class InterpreteUSQL implements Constantes{
    
    int codigo;
    String cadUsql;
    
    
    public InterpreteUSQL(){
    }

    
    public static String interpretarSentenciasUsql(int codigo, Nodo lsent, String cadUsql){
        String response = "";
        String result;
        
        for(Nodo sent : lsent.hijos){
            /*
            result = SemanticoUSQL.analizar(sent, codigo, cadUsql);
            
            if(result == null){
                
                //EJECUTAR
                result = EjecucionUSQL.ejecutar(sent);
                
                if(result == null)
                    response += "[\n" +
                            "	\"paquete\": \"exito\",\n" +
                            "	\"validar\": " + codigo + ",\n" +
                            "	\"sentencia\": \"" + sent.token + "\"\n" +
                            "]\n";
                else{
                    response += Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Ejecucion", 
                            sent.row, 
                            sent.col, 
                            result);
                }
            }else
                response += result;
        */
            
            response += interpretarSentenciaUsql(codigo, sent, cadUsql);
        }
        
        return response;
    }
    
    
    public static String interpretarSentenciaUsql(int codigo, Nodo sent, String cadUsql){
        String result;
        
        result = SemanticoUSQL.analizar(sent, codigo, cadUsql);
            
        if(result == null){

            //EJECUTAR
            result = EjecucionUSQL.ejecutar(sent);

            //Sin errores
            if(result == null){
                result = "[\n" +
                        "	\"paquete\": \"exito\",\n" +
                        "	\"validar\": " + codigo + ",\n" +
                        "	\"sentencia\": \"" + sent.token + "\"\n" +
                        "]\n";
                backupUsqlDump(sent.token, cadUsql);
                
            }else{
                result = Error.lenguaje(
                        codigo, 
                        "USQL", 
                        cadUsql, 
                        "Ejecucion", 
                        sent.row, 
                        sent.col, 
                        result);
            }
        }
        
        return result;
    }
    
    private static void backupUsqlDump(String idSent, String cadUsql){
        switch(idSent){
            case"SELECT":
            case "GRANT":
            case "DENY":
            case "CALL":
            case "BACKUP_USQLDUMP":
            case "BACKUP_COMPLETO":
                break;
            default:
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    Archivos.bbdd.registrarBackup(Server.actualDB, cadUsql);
                }
                break;
        }
    }
}
