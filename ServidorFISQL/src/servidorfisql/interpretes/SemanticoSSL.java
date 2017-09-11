package servidorfisql.interpretes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class SemanticoSSL {
    
    
    private static final HashMap<String, Variable> TS = new HashMap<>();
    private static int codigo;
    private static String cadUsql;
    private static String idMetodo;
    
    public static final ArrayList<String> erroresSSL = new ArrayList<>();
    
    private static Stack<Boolean> display;
    
    public static void analizarMetodo(Nodo metodo, int cod, String cad){
        codigo = cod;
        cadUsql = cad;
        erroresSSL.clear();
        display = new Stack<>();
        
        idMetodo = metodo.getHijo(0).valor;
        Nodo lpar = metodo.getHijo(1);
        Nodo lsent = metodo.getHijo(2);
        
        llenarTS(lpar, lsent);
        
        if(!erroresSSL.isEmpty())
            return;
        
        for(Nodo sent : lsent.hijos){
            analisisSemantico(sent);
        }
    }
    
    private static void llenarTS(Nodo lpar, Nodo lsent){
        String response;
        
        //Parametros
        for(Nodo atr : lpar.hijos){
            
        }
        
        for(Nodo sent : lsent.hijos){
            evaluarSentencias(sent);
        }
        
    }
    
    private static void evaluarSentencias(Nodo sent){
        String response;
        Nodo lsent;
        String tipoResult;

        switch (sent.token) {
            case "PAR":
                {
                    String tipo = sent.getHijo(0).valor;
                    Nodo id = sent.getHijo(1);
                    Variable var = new Variable(tipo, id.valor);
                    if(TS.containsKey(id.valor)){
                        response = Error.lenguaje(
                                codigo,
                                "USQL",
                                cadUsql,
                                "Semantico",
                                id.row,
                                id.col,
                                "Multiple declaracion del atributo [ " + id.valor + " ]");
                        erroresSSL.add(response);
                    }else{
                        TS.put(id.valor, var);
                    }       break;
                }
            case "DEC":
                {
                    Nodo lvar = sent.getHijo(0);
                    Nodo tipo = sent.getHijo(1);
                    for(Nodo id : lvar.hijos){
                        Variable var = new Variable(tipo.valor, id.valor);
                        if(TS.containsKey(id.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    id.row,
                                    id.col,
                                    "Multiple declaracion de la variable [ " + id.valor + " ]");
                            erroresSSL.add(response);
                        }else
                            TS.put(id.valor, var);
                    }   break;
                }
            case "IF":
                Nodo lsentv = sent.getHijo(1);
                Nodo lsentf = sent.hijos.size() == 3 ? sent.getHijo(2) : null;
                for(Nodo s : lsentv.hijos)
                    evaluarSentencias(s);
                if(lsentf != null)
                    for(Nodo s : lsentf.hijos)
                        evaluarSentencias(s);
                break;
            case "SWITCH":
                
                Nodo lcase = sent.getHijo(1);
                for(Nodo caso : lcase.hijos){
                    
                    display.push(true);
                    if(caso.token.equals("CASE")){
                        lsent = caso.getHijo(1);
                        for(Nodo s : lsent.hijos)
                            evaluarSentencias(s);
                    }else{
                        lsent = caso.getHijo(0);
                        for(Nodo s : lsent.hijos)
                            evaluarSentencias(s);
                    }
                    display.pop();
                }   
                break;
                
            case "FOR":
                display.push(true);
                Nodo dec = sent.getHijo(0);
                lsent = sent.getHijo(3);
                evaluarSentencias(dec);
                for(Nodo s : lsent.hijos)
                    evaluarSentencias(s);
                display.pop();
                break;
                
            case "WHILE":
                display.push(true);
                lsent = sent.getHijo(1);
                for(Nodo s : lsent.hijos)
                    evaluarSentencias(s);
                display.pop();
                break;
                
            case "BREAK":
                if(display.empty()){
                    response = Error.lenguaje(
                            codigo, 
                            "USSQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Sentencia DETENER no permitida en el contexto actual.");
                    erroresSSL.add(response);
                    }
                break;
                
            case "RETURN":
                String tipoMet = Archivos.bbdd.getTipoMetodo(Server.actualDB, idMetodo);
                if(!tipoMet.equals("VOID")){
                    
                    tipoResult = evaluarExpresion(sent.getHijo(0));
                    if(tipoMet.equals(tipoResult)){
                        
                    }else{
                        response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Tipos de funcion y de retorno incompatibles");
                        erroresSSL.add(response);
                    }                
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Sentencia return invalida en un Procedimiento.");
                    erroresSSL.add(response);
                }
                
                break;
            case "PRINT":
                break;
            case "CALL":
                evaluarExpresion(sent);
                break;
                
                
            case "CREATE_DB":
            case "CREATE_TABLE":
            case "CREATE_OBJECT":
            case "CREATE_USER":
            case "PROC":
            case "FUNC":
            case "INSERT":
            case "SELECT":
            case "UPDATE":
            case "DELETE_FROM_TABLE":
                
                response = SemanticoUSQL.analizar(sent, codigo, cadUsql);
                if(response != null)
                    erroresSSL.add(response);
                break;
            case "USE":
            case "ALTER_TABLE_ADD":
            case "ALTER_TABLE_QUIT":
            case "ALTER_OBJECT_ADD":
            case "ALTER_OBJECT_QUIT":
            case "ALTER_USER":
            case "DELETE":
                
                response = Error.lenguaje(
                        codigo, 
                        "USQL", 
                        cadUsql, 
                        "Semantico", 
                        sent.row, 
                        sent.col, 
                        "Sentencia [" + sent.token + "] invalida "
                                + "en el ambito [" + idMetodo + "]");
                erroresSSL.add(response);
                break;
                
                
            default:
                break;
        }
    }
    
    private static void analisisSemantico(Nodo sent){
        
    }
    
    
    private static String evaluarExpresion(Nodo exp){
        String tipoResult;
        String izq, der;
        
        switch(exp.token){
            case "||":
            case "&&":
                izq = evaluarExpresion(exp.getHijo(0));
                der = evaluarExpresion(exp.getHijo(1));
                tipoResult = compararTiposExp(izq, der, "COND", exp);
        }
        
        return null;
    }
    
    private static String compararTiposExp(String izq, String der, String clase, Nodo op){
        String tipo = "";
        String response;
        
        switch(clase){
            case "COND":
                if(op.token.equals("!")){
                    if(izq.equals("BOOL"))
                        tipo = "BOOL";
                    else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion !. "
                                    + "Se esperaba [BOOL], se ha encontrado [" + izq + "]");
                        erroresSSL.add(response);
                    }
                }else{
                    if(izq.equals("BOOL") && der.equals("BOOL"))
                        tipo = "BOOL";
                    else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token + ". "
                                + "Se esperaba [BOOL], se ha encontrado [" + izq + "] y [" + der + "]");
                        erroresSSL.add(response);
                    }
                }
                break;
                
            case "REL":
                if(izq.equals(der)){
                    if(izq.equals("BOOL")){
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion relacional " + op.token + ". "
                                + "Los tipos de datos no pueden ser BOOL");
                        erroresSSL.add(response);
                    }else{
                        tipo = "BOOL";
                    }
                }else{
                    response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion relacional " + op.token + ". "
                                + "Los tipos deben ser iguales.");
                        erroresSSL.add(response);
                }
                break;
                
            case "AR":
                switch(op.token){
                    case "+":
                        
                        break;
                        
                    case "-":
                        break;
                        
                    case "*":
                        break;
                        
                    case "/":
                        break;
                        
                    case "^":
                        break;
                        
                    case "_":
                        break;
                }
                break;
        }
        
        
        
        return null;
    }
    
    
    
    public static String cadenaErrores(){
        String cad = "";
        
        for(String error : erroresSSL){
            cad += error;
        }
        
        return cad;
    }
}


class Variable{
    String tipo;
    String id;
    
    public Variable(String tipo, String id){
        this.tipo = tipo;
        this.id = id;
    }
}
