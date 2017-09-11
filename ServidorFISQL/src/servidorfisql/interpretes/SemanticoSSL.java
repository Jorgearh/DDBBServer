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
                    }       
                    break;
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
                    }   
                    break;
                }
                
            case "INST":
                Nodo lvar = sent.getHijo(0);
                Nodo tipo = sent.getHijo(1);
                
                for(Nodo obj : lvar.hijos){
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, tipo.valor)){
                        response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    tipo.row,
                                    tipo.col,
                                    "No se puede instanciar la variable ["+ obj.valor +"]"
                                            + " porque no existe el objeto [" + tipo.valor + "]"
                                            + " en a Base de Datos");
                            erroresSSL.add(response);
                    }else{
                        Variable var = new Variable(tipo.valor, obj.valor);
                        if(TS.containsKey(obj.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    obj.row,
                                    obj.col,
                                    "Multiple declaracion de la variable [ " + obj.valor + " ]");
                            erroresSSL.add(response);
                        }else
                            TS.put(obj.valor, var);
                    }
                }
                
                break;
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
                    
                    if(caso.token.equals("CASE")){
                        lsent = caso.getHijo(1);
                        for(Nodo s : lsent.hijos)
                            evaluarSentencias(s);
                    }else{
                        lsent = caso.getHijo(0);
                        for(Nodo s : lsent.hijos)
                            evaluarSentencias(s);
                    }
                }   
                break;
                
            case "FOR":
                Nodo dec = sent.getHijo(0);
                lsent = sent.getHijo(3);
                
                if(dec.getHijo(1).valor.equals("INTEGER")){
                    evaluarSentencias(dec);
                    for(Nodo s : lsent.hijos)
                        evaluarSentencias(s);
                }else{
                    response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    dec.getHijo(1).row,
                                    dec.getHijo(1).col,
                                    "La variable de control debe ser de tipo INTEGER");
                    erroresSSL.add(response);
                }
                
                break;
                
            case "WHILE":
                lsent = sent.getHijo(1);
                for(Nodo s : lsent.hijos)
                    evaluarSentencias(s);
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
        String tipo, tipoResult;
        Nodo cond, lsent;
        String response;
        
        
        switch(sent.token){
            case "DEC":
                if(sent.hijos.size() == 3){
                    tipo = sent.getHijo(1).valor;
                    tipoResult = evaluarExpresion(sent.getHijo(2));
                    
                    if(!tipo.equals(tipoResult)){
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.row, 
                                            sent.col, 
                                            "Tipos incompatibles");
                        erroresSSL.add(response);
                    }
                }
                break;
            case "=":
                tipoResult = evaluarExpresion(sent.getHijo(1));
                
                //Es una variable normal
                if(sent.getHijo(0).token.equals("VAR")){
                    String idVar = sent.getHijo(0).valor;
                    if(!TS.containsKey(idVar)){
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.getHijo(0).row, 
                                            sent.getHijo(0).col, 
                                            "No existe la variable [" + sent.getHijo(0).valor + "]");
                        erroresSSL.add(response);
                        break;
                    }else{
                        tipo = TS.get(idVar).tipo;
                    }
                }else{//Es una instancia de un objeto
                    
                    String idObjeto = sent.getHijo(0).getHijo(0).valor;
                    
                    //Existe el ebjeto
                    if(TS.containsKey(idObjeto)){
                        String tipoObjeto = TS.get(idObjeto).tipo;
                        String idAtr = sent.getHijo(0).getHijo(1).valor;
                        
                        //Existe el atributo referenciado
                        if(Archivos.bbdd.existeAtributo(Server.actualDB, tipoObjeto, idAtr)){
                            tipo = Archivos.bbdd.getTipoAtributo(Server.actualDB, tipoObjeto, idAtr);
                        }else{
                            response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.getHijo(0).getHijo(1).row, 
                                            sent.getHijo(0).getHijo(1).col, 
                                            "No existe el atributo [" + idAtr + "]");
                            erroresSSL.add(response);
                            break;
                        }
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            sent.getHijo(0).row, 
                                            sent.getHijo(0).col, 
                                            "No existe la variable [" + sent.getHijo(0).valor + "]");
                        erroresSSL.add(response);
                        break;
                    }
                }
                
                if(!tipo.equals(tipoResult)){
                    response = Error.lenguaje(
                                        codigo, 
                                        "USQL", 
                                        cadUsql, 
                                        "Semantico", 
                                        sent.row, 
                                        sent.col, 
                                        "Tipos incompatibles");
                    erroresSSL.add(response);
                }
                
                break;
            case "CALL":
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

            case "IF":
                cond = sent.getHijo(0);
                lsent = sent.getHijo(1);
                
                if(evaluarExpresion(cond).equals("BOOL")){
                    for(Nodo s : lsent.hijos){
                        analisisSemantico(s);
                    }
                    
                    if(sent.hijos.size() == 3){
                        lsent = sent.getHijo(2);
                        for(Nodo s : lsent.hijos){
                            analisisSemantico(s);
                        }
                    }
                    
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Se requiere una expresion condicional en la sentencia IF");
                    erroresSSL.add(response);
                }
                break;
                
            case "SWITCH":
                Nodo exp = sent.getHijo(0);
                Nodo lcase = sent.getHijo(1);
                
                tipo = evaluarExpresion(exp);
                
                for(Nodo caso : lcase.hijos){
                        display.push(true);
                        if(caso.token.equals("CASE")){
                            
                            String tipoCaso = evaluarExpresion(caso.getHijo(0));
                            if(tipoCaso.equals(tipo)){
                                lsent = caso.getHijo(1);
                                for(Nodo s : lsent.hijos){
                                    analisisSemantico(s);
                                }
                            }else{
                                response = Error.lenguaje(
                                        codigo, 
                                        "USSQL", 
                                        cadUsql, 
                                        "Semantico", 
                                        sent.row, 
                                        sent.col, 
                                        "Tipos incompatibles de expresion y caso de evaluacion.");
                                erroresSSL.add(response);
                            }
                        }else{
                            lsent = caso.getHijo(0);
                            for(Nodo s : lsent.hijos){
                                analisisSemantico(s);
                            }
                        }
                        
                        display.pop();
                    }
                break;
            
            case "FOR":
                cond = sent.getHijo(1);
                lsent = sent.getHijo(3);
                
                if(evaluarExpresion(cond).equals("BOOL")){
                    for(Nodo s : lsent.hijos){
                        analisisSemantico(s);
                    }                    
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Se requiere una expresion condicional en la sentencia FOR");
                    erroresSSL.add(response);
                }
                
                break;
            case "WHILE":
                cond = sent.getHijo(0);
                lsent = sent.getHijo(1);
                
                if(evaluarExpresion(cond).equals("BOOL")){
                    for(Nodo s : lsent.hijos){
                        analisisSemantico(s);
                    }                    
                }else{
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            sent.row, 
                            sent.col, 
                            "Se requiere una expresion condicional en la sentencia WHILE");
                    erroresSSL.add(response);
                }
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
                
            case "PRINT":
                break;
        }
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
                break;
                
            case "!":
                izq = evaluarExpresion(exp.getHijo(0));
                tipoResult= compararTiposExp(izq, null, "COND", exp);
                break;
                
            case "==":
            case "!=":
            case "<":
            case "<=":
            case ">":
            case ">=":
                izq = evaluarExpresion(exp.getHijo(0));
                der = evaluarExpresion(exp.getHijo(1));
                tipoResult = compararTiposExp(izq, der, "REL", exp);
                break;
                
            case "+":
            case "-":
            case "*":
            case "/":
            case "^":
                izq = evaluarExpresion(exp.getHijo(0));
                der = evaluarExpresion(exp.getHijo(1));
                tipoResult = compararTiposExp(izq, der, "AR", exp);
                break;
        }
        
        return tipoResult;
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
                        if(izq.equals("TEXT") || der.equals("TEXT"))
                            tipo = "TEXT";
                        else if(izq.equals("BOOL"))
                            tipo = der;
                        else if(izq.equals("INTEGER")){
                            if(der.equals("BOOL") || der.equals("INTEGER"))
                                tipo = "INTEGER";
                            else
                                tipo = der;
                        }else{
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token + ". "
                                + "Los tipos no pueden ser DATE/DATETIME");
                            erroresSSL.add(response);
                        }
                        break;
                        
                    case "-":
                    case "^":
                        if((izq.equals("INTEGER") || der.equals("DOUBLE") && (der.equals("INTEGER") || der.equals("DOUBLE")))){
                            if(izq.equals("DOUBLE") || der.equals("DOUBLE"))
                                tipo = "DOUBLE";
                            else
                                tipo = "INTEGER";
                        }else{
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token + ". "
                                + "Los tipos solo pueden ser INTEGER/DOUBLE");
                            erroresSSL.add(response);
                        }
                        break;
                        
                    case "*":
                        if(!izq.equals("DATE") && !izq.equals("DATETIME") && !izq.equals("TEXT") &&
                                !der.equals("DATE") && der.equals("DATETIME") && !der.equals("TEXT")){
                            
                            if(izq.equals("BOOL"))
                                tipo = der;
                            else{
                                if(izq.equals("DOUBLE") || der.equals("DOUBLE"))
                                    tipo = "DOUBLE";
                                else
                                    tipo = "INTEGER";
                            }
                        }else{
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token + ". "
                                + "Los tipos solo pueden ser INTEGER/DOUBLE/BOOL");
                            erroresSSL.add(response);
                        }
                        break;
                        
                    case "/":
                        if((izq.equals("INTEGER") || der.equals("DOUBLE") && (der.equals("INTEGER") || der.equals("DOUBLE")))){
                            tipo = "DOUBLE";
                        }else{
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipos incompatibles en operacion " + op.token + ". "
                                + "Los tipos solo pueden ser INTEGER/DOUBLE");
                            erroresSSL.add(response);
                        }
                        break;
                        
                    case "_":
                        if(izq.equals("INTEGER") || izq.equals("DOUBLE"))
                            tipo = izq;
                        else{
                            response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                op.row, 
                                op.col, 
                                "Tipo incompatibles en operacion - unario. "
                                + "El tipo solo pueden ser INTEGER/DOUBLE");
                            erroresSSL.add(response);
                        }
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
