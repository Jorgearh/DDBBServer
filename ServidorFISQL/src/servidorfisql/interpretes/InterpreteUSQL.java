package servidorfisql.interpretes;

import servidorfisql.Constantes;
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
        
        for(Nodo sent : lsent.hijos){
            
            response += interpretarSentenciaUsql(codigo, sent, cadUsql);
        }
        
        return response;
    }
    
    public static String reportRequest(int codigo, Nodo sent, String cadUsql){
        String result;
        
        if(sent.token.equals("SELECT")){
            result = SemanticoUSQL.analizar(sent, codigo, cadUsql);
            if(result != null){
                result = EjecucionUSQL.ejecutarReporte(sent);
                result = "[\n" +
                        "	\"paquete\": \"reporte\",\n" +
                        "	\"validar\": " + codigo + ",\n" +
                        "	\"datos\": [\n" +
                        "<tabla>\n" +
                        result +
                        "</tabla>\n" +
                        "	]\n" +
                        "]\n";
            }
            
        }else{
            result = Error.lenguaje(
                    codigo, 
                    "USQL", 
                    cadUsql, 
                    "Semantico", 
                    sent.row, 
                    sent.col, 
                    "No se puede reportar una sentencia que no sea SELECCIONAR.");
        }
        
        return result;
    }
    
    
    public static String interpretarSentenciaUsql(int codigo, Nodo sent, String cadUsql){
        String result;
        
        result = SemanticoUSQL.analizar(sent, codigo, cadUsql);
            
        
        //SI NO HAY ERRORES SEMANTICOS, SE EJECUTA
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
                
            }else if(sent.token.equals("SELECT")){
                
                result = "[\n" +
                        "	\"paquete\": \"usql\",\n" +
                        "	\"validar\": " + codigo + ",\n" +
                        "	\"datos\": [\n" +
                        result +
                        "	]\n" +
                        "]\n";
                
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
