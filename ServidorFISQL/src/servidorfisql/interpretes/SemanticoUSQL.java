package servidorfisql.interpretes;

import java.util.ArrayList;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class SemanticoUSQL {
    
    private static int codigo;
    private static String cadUsql;
    
    /***
     * Analisis Semantico de las sentencias USQL
     * @param usqlSent
     * @param cod
     * @param cad
     * @return null si no existen erroresSSL, una cadena de error si existen erroresSSL.
     */
    public static String analizar(Nodo usqlSent, int cod, String cad){
        String response = null;
        
        codigo = cod;
        cadUsql = cad;
        
        switch(usqlSent.token){
                /*   SENTENCIAS DDL   */
                case "CREATE_DB":
                case "CREATE_TABLE":
                case "CREATE_OBJECT":
                case "CREATE_USER":
                case "PROC":
                case "FUNC":
                    response = create(usqlSent);
                    break;
                    
                case "USE":
                    response = usar(usqlSent);
                    break;
                    
                case "ALTER_TABLE_ADD":
                case "ALTER_TABLE_QUIT":
                case "ALTER_OBJECT_ADD":
                case "ALTER_OBJECT_QUIT":
                case "ALTER_USER":
                    response = alter(usqlSent);
                    break;
                    
                case "DELETE":
                    response = delete(usqlSent);
                    break;
                    
                    
                /*   SENTENCIAS DML*/
                case "INSERT":
                    response = insert(usqlSent);
                    break;
                case "SELECT":
                    response = select(usqlSent);
                    break;
                case "UPDATE":
                    response = update(usqlSent);
                    break;
                case "DELETE_FROM_TABLE":
                    response = deleteFromTable(usqlSent);
                    break;
                    
                    
                /*   SENTENCIAS DCL   */
                case "GRANT":
                    response = grant(usqlSent);
                    break;
                case "DENY":
                    response = deny(usqlSent);
                    break;
                    
                case "CALL":
                    response = SemanticoSSL.analizarSentenciaSSL(usqlSent, cod, cad);
                    break;
                    
                case "PRINT":
                    break;
                    
                case "BACKUP_USQLDUMP":
                case "BACKUP_COMPLETO":
                    response = backup(usqlSent);
                    break;
                    
                default:
                    response = Error.logico(
                            codigo, 
                            "logico", 
                            "Sentencia " + usqlSent.token + "invalida");
            }
        
        return response;
    }
    
    /*    SENTENCIAS DDL    */
    
    private static String create(Nodo create){
        String response = null;
        Nodo nodoID;
        
        switch(create.token){
            case "CREATE_DB":
                String idDB;
                
                nodoID = create.getHijo(0);
                idDB = nodoID.valor;
                
                if(Archivos.bbdd.existeBD(idDB)){
                    response = Error.lenguaje(
                            codigo, 
                            "USQL", 
                            cadUsql, 
                            "Semantico", 
                            nodoID.row, 
                            nodoID.col, 
                            "Ya existe la base de datos [" + idDB + "]");
                }
                break;
                
            case "CREATE_TABLE":
                
                //Hay una BD en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    
                    
                    nodoID = create.getHijo(0);
                    
                    String idTable = nodoID.valor;
                    
                    //No existe la tabla en la BD
                    if(!Archivos.bbdd.existeTabla(Server.actualDB, idTable)){
                        
                        //No se repite ningun campo
                        String analisis = analisisDeclaracionColumnas(create);
                        
                        if(analisis != null)
                            return analisis;
                        
                    }else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                nodoID.row, 
                                nodoID.col, 
                                "La tabla [" + idTable + "] ya existe en la base de datos [" + Server.actualDB + "]");
                    }
                    
                }else{
                    response = Error.logico(
                            codigo, 
                            "CREATE TABLE", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                
                break;
                
            case "CREATE_USER":
                if(Server.user.equals("admin")){
                    Nodo user = create.getHijo(0);
                    Nodo pass = create.getHijo(1);

                    if(Archivos.usuarios.existsUser(user.valor)){
                        response = 
                                Error.lenguaje(
                                        codigo,
                                        "USQL",
                                        cadUsql,
                                        "Semantico",
                                        user.row,
                                        user.col, 
                                        "Ya existe el usuario [" + user.valor + "]. "
                                                + "Vuelva a intentarlo con otro nombre de usuario");
                    }
                }else{
                    response = Error.logico(codigo, 
                            "CREATE USER", 
                            "Usuario [" + Server.user + "] sin permisos para crear usuarios.");
                }
                
                break;
                
            case "CREATE_OBJECT":
                
                //Hay una base de datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    
                    Nodo idObj = create.getHijo(0);

                    //No existe un objeto con el mismo nombre en la base de datos
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, idObj.valor)){

                        String analisis = analisisDeclaracionAtributos(create);
                        if(analisis != null){
                            return analisis;
                        }

                    }else{
                        response = 
                            Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    idObj.row, 
                                    idObj.col, 
                                    "Ya existe el objeto [" + idObj.valor + "]"
                                    + "en la base de datos [" + Server.actualDB + "]");
                    }
                }else{
                        response = Error.logico(
                            codigo, 
                            "CREATE OBJECT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
                
            case "PROC":
            case "FUNC":
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    
                    Nodo idProc = create.getHijo(0);

                    //No existe un metodo con el mismo nombre en la base de datos
                    if(!Archivos.bbdd.existeMetodo(Server.actualDB, idProc.valor)){

                        //Analisis semantico SSL(parametros, variables locales, comprobacion de tipos)
                        SemanticoSSL.analizarMetodo(create, codigo, cadUsql);
                        
                        if(!SemanticoSSL.erroresSSL.isEmpty()){
                            return SemanticoSSL.cadenaErrores();
                        }
                        
                    }else{
                        response = 
                            Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    idProc.row, 
                                    idProc.col, 
                                    "Ya existe " + (create.token.equals("PROC") ? "el procedimiento" : "la funcion") 
                                    + " [" + idProc.valor + "]"
                                    + "en la base de datos [" + Server.actualDB + "]");
                    }
                }else{
                        response = Error.logico(
                            codigo, 
                            "CREATE" + (create.token.equals("PROC") ? " PROCEDURE" : " FUNCTION"), 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
            
        }
        
        return response;
    }
    
    private static String usar(Nodo use){
        String response = null;
        
        String idDB = use.getHijo(0).valor;
        
        if(Archivos.bbdd.existeBD(idDB)){
            if(!Archivos.bbdd.tienePermisos(idDB, Server.user)){
                response = Error.logico(codigo,
                        "PERMISOS USE",
                        "El usuario [" + Server.user + "] "
                                + "no tiene permisos"
                                + "en la base de datos [" + Server.actualDB + "]");
            }
        }else{
            response = Error.lenguaje(
                    codigo, 
                    "USQL", 
                    cadUsql, 
                    "Semantico", 
                    use.getHijo(0).row, 
                    use.getHijo(0).col,
                    "No existe la base de datos [" + idDB + "]");
        }
        
        return response;
    }
    
    private static String alter(Nodo alter){
        String response = null;
        Nodo idTable;
        Nodo idObject;
        Nodo lid;
        
        switch(alter.valor){
            case "ALTER_USER":
                Nodo idUser = alter.getHijo(0);
                Nodo passUser = alter.getHijo(1);

                //Existe el usuario a modificar
                if(Archivos.usuarios.existsUser(idUser.valor)){
                    //Un usuario modifica sus credenciales
                    if(!Server.user.equals("admin") && !Server.user.equals(idUser.valor)){                                  //Un usuario no admin 
                        response = Error.logico(
                                codigo,
                                cadUsql, 
                                "Solo el usuario admin puede modificar credenciales ajenas.");
                    }
                }else{
                    response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idUser.row, 
                                            idUser.col,
                                            "No existe el usuario [" + idUser.valor + "]");
                }
                
                break;
            case "ALTER_TABLE_QUIT":
                idTable = alter.getHijo(0);
                lid = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe la Tabla
                    if(!Archivos.bbdd.existeTabla(Server.actualDB, idTable.valor)){
                        //El usuario actual tiene permisos en la tabla
                        if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idTable.valor, Server.user)){
                            
                            for(Nodo col : lid.hijos){
                                if(!Archivos.bbdd.existeColumna(Server.actualDB, idTable.valor, col.valor)){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                cadUsql, 
                                                "Semantico", 
                                                col.row, 
                                                col.col,
                                                "No existe [" + col.valor + "]");
                                    return response;
                                }
                            }
                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER TABLE QUIT", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en la tabla [" + idTable.valor + "]");
                        }
                        
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idTable.row, 
                                            idTable.col,
                                            "No existe la tabla [" + idTable.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER TABLE QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                
                break;
            case "ALTER_OBJECT_QUIT":
                idObject = alter.getHijo(0);
                lid = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe el Objeto
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, idObject.valor)){
                        //El usuario actual tiene permisos en el objeto
                        if(Archivos.bbdd.tienePermisosObjeto(Server.actualDB, idObject.valor, Server.user)){
                            for(Nodo atr : lid.hijos){
                                if(!Archivos.bbdd.existeAtributo(Server.actualDB, idObject.valor, atr.valor)){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                cadUsql, 
                                                "Semantico", 
                                                atr.row, 
                                                atr.col,
                                                "No existe el atributo [" + atr.valor + "]");
                                    return response;
                                }
                            }

                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER OBJECT QUIT", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en el objeto [" + idObject.valor + "]");
                        }
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idObject.row, 
                                            idObject.col,
                                            "No existe el objeto [" + idObject.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER OBJECT QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
            case "ALTER_TABLE_ADD":
                idTable = alter.getHijo(0);
                Nodo lcol = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe la Tabla
                    if(!Archivos.bbdd.existeTabla(Server.actualDB, idTable.valor)){
                        //El usuario actual tiene permisos en la tabla
                        if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idTable.valor, Server.user)){
                            
                            //Analisis semantico de las nuevas columnas
                            String analisisColumnas = analisisDeclaracionColumnas(alter);
                            if(analisisColumnas != null)
                                return analisisColumnas;
                            
                            //Verificar que las nuevas columnas no existan en la tabla
                            for(Nodo col : lcol.hijos){
                                Nodo idCol = col.getHijo(1);
                                
                                if(Archivos.bbdd.existeColumna(Server.actualDB, idTable.valor, idCol.valor)){
                                    response = Error.lenguaje(codigo, 
                                                                "USQL", 
                                                                cadUsql, 
                                                                "Semantico",
                                                                idCol.row, 
                                                                idCol.col, 
                                                                "Multiple declaracion de la columna [" + idCol.valor + "]."); 
                                    return response;
                                }
                            }
                            
                            //Verificar existencia de llaves primarias multiples
                            for(Nodo col : lcol.hijos){
                                Nodo idCol = col.getHijo(1);
                                Nodo lcomp = col.getHijo(2);
                                
                                for(Nodo comp : lcomp.hijos){
                                    if(comp.token.equals("PK")){
                                        String pk = Archivos.bbdd.obtenerPk(Server.actualDB, idTable.valor);
                                        
                                        if(pk != null){
                                            response = 
                                                Error.lenguaje(
                                                        codigo, 
                                                        "USQL", 
                                                        cadUsql, 
                                                        "Semantico", 
                                                        idCol.row, 
                                                        idCol.col, 
                                                        "Multiple definicion de llave primaria en la tabla [" + idTable.valor + "]");
                                            return response; 
                                        }
                                    }
                                }
                            }
                            
                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER TABLE ADD", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en la tabla [" + idTable.valor + "]");
                        }
                        
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idTable.row, 
                                            idTable.col,
                                            "No existe la tabla [" + idTable.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER TABLE QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
            case "ALTER_OBJECT_ADD":
                idObject = alter.getHijo(0);
                Nodo latr = alter.getHijo(1);
                
                //Hay una Base de Datos en uso
                if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                    //Existe el Objeto
                    if(!Archivos.bbdd.existeObjeto(Server.actualDB, idObject.valor)){
                        //El usuario actual tiene permisos en el objeto
                        if(Archivos.bbdd.tienePermisosObjeto(Server.actualDB, idObject.valor, Server.user)){
                            
                            //Declaracion multiple de atributos agregados
                            String analisis = analisisDeclaracionAtributos(alter);
                            if(analisis != null)
                                return analisis;
                            
                            for(Nodo atr : latr.hijos){
                                Nodo tipoAtr = atr.getHijo(0);
                                Nodo idAtr = atr.getHijo(1);
                                
                                //Ya existe el atributo
                                if(Archivos.bbdd.existeAtributo(Server.actualDB, idObject.valor, idAtr.valor)){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                cadUsql, 
                                                "Semantico", 
                                                atr.row, 
                                                atr.col,
                                                "Multiple declaracion el atributo [" + idAtr.valor + "]");
                                    return response;
                                }
                                
                                //El atributo es de tipo objeto
                                if(tipoAtr.token.equals("ID")){
                                    response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                cadUsql, 
                                                "Semantico", 
                                                atr.row, 
                                                atr.col,
                                                "El atributo [" + idAtr.valor + "] no puede ser de tipo objeto");
                                    return response;
                                }
                            }

                        }else{
                            response = Error.logico(codigo, 
                                        "ALTER OBJECT QUIT", 
                                        "El usuario [" + Server.user + "] "
                                        + "no tiene permisos para en el objeto [" + idObject.valor + "]");
                        }
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idObject.row, 
                                            idObject.col,
                                            "No existe el objeto [" + idObject.valor + "]"
                                            + "en la Base de Datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "ALTER OBJECT QUIT", 
                            "No se ha seleccionado una base de datos para usar.");
                }
                break;
        }
        
        
        return response;
    }
    
    private static String delete(Nodo delete){
        String response = null;
        Nodo usqlObject = delete.getHijo(0);
        Nodo idObject = delete.getHijo(1);
        
        
        if(usqlObject.valor.equals("DDBB")){
            if(idObject.valor.equals(Server.actualDB)){
                response = Error.logico(
                        codigo, 
                        "DELETE DB", 
                        "No puede eleiminarse la base de datos [" + idObject.valor + "] "
                                + "porque se encuentra en uso");
            }else{
                //Existe la Base de Datos a eliminar
                if(Archivos.bbdd.existeBD(idObject.valor)){
                    //No tiene permisos en la base de datos
                    if(!Archivos.bbdd.tienePermisos(idObject.valor, Server.user)){
                        response = Error.logico(
                                codigo, 
                                "PERMISOS DELETE DB", 
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos"
                                        + "en la base de datos [" + Server.actualDB + "]");
                    }
                }else{
                    response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idObject.row, 
                                            idObject.col,
                                            "No existe la Base de Datos [" + idObject.valor + "]");
                }
            }
        }else if(usqlObject.valor.equals("USER")){
            
            if(Server.user.equals("admin")){
                if(!idObject.valor.equals("admin")){
                    //Existe el usuario a eliminar
                    if(!Archivos.usuarios.existsUser(idObject.valor)){
                        response = Error.lenguaje(
                                codigo,
                                "USQL",
                                cadUsql,
                                "Semantico",
                                idObject.row,
                                idObject.col, 
                                "No existe el usuario [" + idObject.valor + "]");
                    }
                }else{
                    response = Error.logico(
                            codigo, 
                            "DELTE USER ADMIN", 
                            "No se puede eliminar el usuario admin");
                }
            }else{
                response = Error.logico(
                        codigo, 
                        "DELETE USER NOT ADMIN", 
                        "Solo el usuario admin puede eliminar usuarios.");
            }
            
        }else{
            //Hay una base de datos en uso
            if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
                //El usuario actual tiene permisos en la Base de Datos actual
                if(Archivos.bbdd.tienePermisos(Server.actualDB, Server.user)){
                    switch(usqlObject.valor){
                        case "TABLE":
                            //Existe la tabla a eliminar
                            if(Archivos.bbdd.existeTabla(Server.actualDB, idObject.valor)){
                                
                                //El usuario actual tiene permisos en la tabla
                                if(!Archivos.bbdd.tienePermisosTabla(Server.actualDB, idObject.valor, Server.user)){
                                    response = Error.logico(codigo,
                                            "PERMISOS DELETE TABLE",
                                            "El usuario [" + Server.user + "] "
                                                    + "no tiene permisos"
                                                    + "en la tabla [" + idObject.valor + "]");
                                }
                            }else{
                                response = Error.lenguaje(
                                                    codigo, 
                                                    "USQL", 
                                                    cadUsql, 
                                                    "Semantico", 
                                                    idObject.row,
                                                    idObject.col,
                                                    "No existe la tabla [" + idObject.valor + "]"
                                                    + "en la Base de Datos [" + Server.actualDB + "]");
                            }
                            break;
                        case "OBJECT":
                            //Existe el objeto a eliminar
                            if(Archivos.bbdd.existeObjeto(Server.actualDB, idObject.valor)){
                                
                                //El usuario actual tiene permisos en el objeto
                                if(!Archivos.bbdd.tienePermisosObjeto(Server.actualDB, idObject.valor, Server.user)){
                                    response = Error.logico(codigo,
                                            "PERMISOS DELETE OBJECT",
                                            "El usuario [" + Server.user + "] "
                                                    + "no tiene permisos"
                                                    + "en el objeto [" + idObject.valor + "]");
                                }
                            }else{
                                response = Error.lenguaje(
                                                    codigo, 
                                                    "USQL", 
                                                    cadUsql, 
                                                    "Semantico", 
                                                    idObject.row,
                                                    idObject.col,
                                                    "No existe el objeto [" + idObject.valor + "]"
                                                    + "en la Base de Datos [" + Server.actualDB + "]");
                            }
                            break;
                        case "PROC":
                        case "FUNC":
                             //Existe el metodo a eliminar
                            String nameAux = usqlObject.token.equals("PROC") ? "el procedimiento" : "la funcion";
                            
                            if(Archivos.bbdd.existeMetodo(Server.actualDB, idObject.valor)){
                                
                                //El usuario actual tiene permisos en el metodo
                                if(!Archivos.bbdd.tienePermisosMetodo(Server.actualDB, idObject.valor, Server.user)){
                                    response = Error.logico(codigo,
                                            "PERMISOS DELETE OBJECT",
                                            "El usuario [" + Server.user + "] "
                                                    + "no tiene permisos"
                                                    + "en " + nameAux + " [" + idObject.valor + "]");
                                }
                            }else{
                                response = Error.lenguaje(
                                                    codigo, 
                                                    "USQL", 
                                                    cadUsql, 
                                                    "Semantico", 
                                                    idObject.row,
                                                    idObject.col,
                                                    "No existe " + nameAux + " [" + idObject.valor + "]"
                                                    + "en la Base de Datos [" + Server.actualDB + "]");
                            }
                            break;
                    }
                }else{
                    response = Error.logico(codigo, 
                                "PERMISOS DELETE", 
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos"
                                        + "en la base de datos [" + Server.actualDB + "]");
                }
            }else{
                response = Error.logico(
                            codigo, 
                            "CREATE TABLE", 
                            "No se ha seleccionado una base de datos para usar.");
            }
        }
  
        return response;
    }
    
    
    
    /*    SENTENCIAS DML    */
    
    private static String insert(Nodo insert){
        String response = null;
        
        Nodo idTable = insert.getHijo(0);
        Nodo lidCols = insert.getHijo(1);
        Nodo lexp = insert.getHijo(2);
        
        //Hay una base de datos en uso
        if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
            
            //Existe la tabla en la que se desea insertar
            if(Archivos.bbdd.existeTabla(Server.actualDB, idTable.valor)){
                
                if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idTable.valor, Server.user)){
                    //Insertar normal
                    if(lidCols.hijos.isEmpty()){
                        int cantExps = lexp.hijos.size();
                        int cantCols = Archivos.bbdd.cantColsInsertables(Server.actualDB, idTable.valor);

                        //Coincide la cantidad de columnas insertables con la cantidad de expresiones
                        if (cantExps > cantCols){
                            response = Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    idTable.row, 
                                    idTable.col, 
                                    "Error insertando valores en columnas autoincrementables "
                                        + "de tabla [" + idTable.valor + "]");
                        }else{
                            response = Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    idTable.row, 
                                    idTable.col, 
                                    "Error insertando valores tabla [" + idTable.valor + "]. "
                                            + "Se esperan [" + cantCols + "] valores.");
                        }

                    }else{//Insertar especial
                        for(Nodo idCol : lidCols.hijos){
                            if(!Archivos.bbdd.existeColumna(Server.actualDB, idTable.valor, idCol.valor)){
                                response = Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    idTable.row, 
                                    idTable.col, 
                                    "Error insertando valores tabla [" + idTable.valor + "]. "
                                            + "No existe la columna [" + idCol.valor + "]");
                            }
                        }
                    }
                }else{
                    response = Error.logico(codigo,
                                "SELECT",
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos en la tabla [" + idTable.valor + "]");
                }
                
            }else{
                response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idTable.row, 
                                            idTable.col,
                                            "No existe la Tabla [" + idTable.valor + "]");
            }
            
        }else{
            response = Error.logico(
                            codigo, 
                            insert.token, 
                            "No se ha seleccionado una base de datos para usar.");
        }
        
        return response;
    }

    private static String select(Nodo select){
        String response = null;
        
        if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
            
            Nodo lcol = select.getHijo(0);
            Nodo from = select.getHijo(1);
            Nodo cond = select.hijos.size() == 3 ? select.getHijo(2) : null;
            
            //Evaluar las tablas
            for(Nodo table : from.hijos){
                //Existe la tabla
                if(Archivos.bbdd.existeTabla(Server.actualDB, table.valor)){
                    
                    //El usuario actual tiene permisos en la tablas
                    if(!Archivos.bbdd.tienePermisosTabla(Server.actualDB, table.valor, Server.user)){
                        response = Error.logico(codigo,
                                "SELECT",
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos en la tabla [" + table.valor + "]");
                        return response;
                    }
                }else{
                    response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            table.row, 
                                            table.col,
                                            "No existe la Tabla [" + table.valor + "]");
                    return response;
                }
            }
            
            //Evaluar las columnas
            if(!lcol.token.equals("*")){
                for(Nodo col : lcol.hijos){
                    Nodo table = col.getHijo(0);
                    Nodo column = col.getHijo(1);
                    
                    //Existe la tabla
                    if(Archivos.bbdd.existeTabla(Server.actualDB, table.valor)){
                        
                        //Existe la columna
                        if(!Archivos.bbdd.existeColumna(Server.actualDB, table.valor, column.valor)){
                            response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            table.row, 
                                            table.col,
                                            "No existe la Columna [" + column.valor + "]");
                            return response;
                        }
                    }else{
                        response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            table.row, 
                                            table.col,
                                            "No existe la Tabla [" + table.valor + "]");
                        return response;
                    }
                }
            }
        }else{
            response = Error.logico(
                            codigo, 
                            select.token, 
                            "No se ha seleccionado una base de datos para usar.");
        }
        
        return response;
    }
    
    private static String update(Nodo update){
        String response = null;
        
        if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
            Nodo idTabla = update.getHijo(0);
            Nodo lid = update.getHijo(1);
            Nodo lexp = update.getHijo(2);

            //Existe la tabla
            if(Archivos.bbdd.existeTabla(Server.actualDB, idTabla.valor)){
                
                if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idTabla.valor, Server.user)){
                    if(lid.hijos.size() == lexp.hijos.size()){
                    
                        for(int x = 0; x < lid.hijos.size(); x++){
                            Nodo idCol = lid.getHijo(x);
                            Nodo exp = lexp.getHijo(x);

                            //Existe la columna
                            if(!Archivos.bbdd.existeColumna(Server.actualDB, idTabla.valor, idCol.valor)){

                                String tipoCol = Archivos.bbdd.getTipoColumna(Server.actualDB, idTabla.valor, idCol.valor);
                                String tipoExp = SemanticoSSL.evaluarExpresion(exp);

                                if(!tipoCol.equals(tipoExp)){
                                    response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            exp.row, 
                                            exp.col, 
                                            "Tipos incompatibles en columna y expresion a asignar.");
                                    break;
                                }

                            }else{
                                response = Error.lenguaje(
                                                codigo, 
                                                "USQL", 
                                                cadUsql, 
                                                "Semantico", 
                                                idCol.row, 
                                                idCol.col,
                                                "No existe la Columna [" + idCol.valor + "]");
                                break;
                            }
                        }
                    }else{
                        response = Error.lenguaje(
                                codigo, 
                                "USQL", 
                                cadUsql, 
                                "Semantico", 
                                idTabla.row, 
                                idTabla.col, 
                                "No coinciden los valores ingresados con las columnas por asignar.");
                    }
                }else{
                    response = Error.logico(codigo,
                                "SELECT",
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos en la tabla [" + idTabla.valor + "]");
                }
                
            }else{
                response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idTabla.row, 
                                            idTabla.col,
                                            "No existe la Tabla [" + idTabla.valor + "]");
            }
            
        }else{
            response = Error.logico(
                            codigo, 
                            update.token, 
                            "No se ha seleccionado una base de datos para usar.");
        }
        
        return response;
    }
    
    private static String deleteFromTable(Nodo delete){
        String response = null;
        
        if(Server.actualDB != null && !Server.actualDB.equals("") && !Server.actualDB.isEmpty()){
            
            Nodo idTable = delete.getHijo(0);
            
            if(Archivos.bbdd.tienePermisosTabla(Server.actualDB, idTable.valor, Server.user)){
                //Existe la tabla
                if(!Archivos.bbdd.existeTabla(Server.actualDB, idTable.valor)){
                    response = Error.lenguaje(
                            codigo,
                            "USQL",
                            cadUsql,
                            "Semantico",
                            idTable.row,
                            idTable.col,
                            "No existe la Tabla [" + idTable.valor + "]");
                }
            }else{
                response = Error.logico(codigo,
                                "SELECT",
                                "El usuario [" + Server.user + "] "
                                        + "no tiene permisos en la tabla [" + idTable.valor + "]");
            }
            
        }else{
            response = Error.logico(
                            codigo, 
                            delete.token, 
                            "No se ha seleccionado una base de datos para usar.");
        }
        
        return response;
    }



    /*    SENTENCIAS DCL    */

    private static String grant(Nodo grant){
        String response = null;
        
        //Solo el usuario admin puede otorgar permisos
        if(Server.user.equals("admin")){
            
            Nodo idUser = grant.getHijo(0);
            Nodo idDB = grant.getHijo(1);
            Nodo dbObj = grant.getHijo(2);
            
            //Existe el usuario
            if(Archivos.usuarios.existsUser(idUser.valor)){
                
                //Existe la Base de Datos
                if(Archivos.bbdd.existeBD(idDB.valor)){
                    if(!dbObj.token.equals("*")){
                        
                        //No se ha encontrado el objeto
                        if(!Archivos.bbdd.existeObjetoUsql(idDB.valor, dbObj.valor, idUser.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    dbObj.row,
                                    dbObj.col,
                                    "No existe el elemento [" + dbObj.valor + "] "
                                            + "en la Base de Datos [" + idDB.valor + "]");
                        }
                    }
                }else{
                 response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idDB.row, 
                                            idDB.col,
                                            "No existe la Base de Datos [" + idDB.valor + "]");   
                }
            }else{
                response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idUser.row, 
                                            idUser.col,
                                            "No existe el usuario[" + idUser.valor + "]");
            }
            
            
        }else{
            response = Error.logico(codigo, 
                            "OTORGAR PERMISOS", 
                            "Solo el usuario [admin] puede otorgar permisos");
        }
        
        return response;
    } 
    
    private static String deny(Nodo deny){
        String response = null;
        
        //Solo el usuario admin puede otorgar permisos
        if(Server.user.equals("admin")){
            
            Nodo idUser = deny.getHijo(0);
            Nodo idDB = deny.getHijo(1);
            Nodo dbObj = deny.getHijo(2);
            
            //Existe el usuario
            if(Archivos.usuarios.existsUser(idUser.valor)){
                
                //Existe la Base de Datos
                if(Archivos.bbdd.existeBD(idDB.valor)){
                    if(!dbObj.token.equals("*")){
                        
                        //No se ha encontrado el objeto
                        if(!Archivos.bbdd.existeObjetoUsql(idDB.valor, dbObj.valor, idUser.valor)){
                            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    dbObj.row,
                                    dbObj.col,
                                    "No existe el elemento [" + dbObj.valor + "] "
                                            + "en la Base de Datos [" + idDB.valor + "]");
                        }
                    }else{
                        
                    }
                }else{
                 response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idDB.row, 
                                            idDB.col,
                                            "No existe la Base de Datos [" + idDB.valor + "]");   
                }
            }else{
                response = Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idUser.row, 
                                            idUser.col,
                                            "No existe el usuario[" + idUser.valor + "]");
            }
            
            
        }else{
            response = Error.logico(codigo, 
                            "DENEGAR PERMISOS", 
                            "Solo el usuario [admin] puede denegar permisos");
        }
        
        return response;
    }
        
    
    
    /*    BACKUP    */
    private static String backup(Nodo backup){
        String response = null;
        Nodo db = backup.getHijo(0);
        
        if(!Archivos.bbdd.existeBD(db.valor)){
            response = Error.lenguaje(
                                    codigo,
                                    "USQL",
                                    cadUsql,
                                    "Semantico",
                                    db.row,
                                    db.col,
                                    "No existe la Base de Datos [" + db.valor + "]");
        }else{
            if(!Archivos.bbdd.tienePermisos(db.valor, Server.user)){
                response = Error.logico(codigo,
                        "PERMISOS BACKUP",
                        "El usuario [" + Server.user + "] "
                                + "no tiene permisos"
                                + "en la base de datos [" + Server.actualDB + "]");
            }
        }
        
        return response;
    }
    
    private static String analisisDeclaracionColumnas(Nodo nodo){
        String response = null;
        String idTable = nodo.getHijo(0).valor;
        Nodo lcampo = nodo.getHijo(1);

        ArrayList<String> items = new ArrayList<>();
        int index = 1;
        
        for(Nodo hijo : lcampo.hijos){
            Nodo nieto = hijo.getHijo(index);
            
            if(items.contains(nieto.valor)){
                
                response = Error.lenguaje(codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico",
                                    nieto.row, 
                                    nieto.col, 
                                    "Multiple declaracion de la columna [" + nieto + "]."); 
                
                return response;
            }
            items.add(nieto.valor);
        }
        
        
        boolean pk = false;
        String idPk = "";
        
        //Para cada campo evaluar los complementos
        for(Nodo columna : lcampo.hijos){
            Nodo tipoCol = columna.getHijo(0);
            Nodo idColDef = columna.getHijo(1);
            Nodo lcomp = columna.getHijo(2);

            boolean nulo = false;

            for(Nodo comp : lcomp.hijos){
                switch(comp.token){
                    case "PK":
                        if(!pk){
                            if(!nulo){
                                pk = true;
                                idPk = idColDef.valor;
                            }else{
                                response = 
                                    Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idColDef.row, 
                                            idColDef.col, 
                                            "La llave primaria no puede aceptar valores nulos");
                                return response;
                            }

                        }else{
                            response = 
                                    Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idColDef.row, 
                                            idColDef.col, 
                                            "Multiple definicion de llave primaria en la tabla [" + idTable + "]");
                            return response;
                        }
                        break;

                    case "FK":
                        Nodo idTableRef = comp.getHijo(0);
                        Nodo idColRef = comp.getHijo(1);

                        //Existe tabla referenciada por llave foranea
                        if(Archivos.bbdd.existeTabla(Server.actualDB, idTableRef.valor)){

                            //Exite la columna referenciada por la llave foranea
                            if(Archivos.bbdd.existeColumna(Server.actualDB, idTableRef.valor, idColRef.valor)){

                                //Tipos diferentes columna definida y columna referenciada
                                if(!tipoCol.valor.equals(Archivos.bbdd.getTipoColumna(Server.actualDB, idTableRef.valor, idColRef.valor))){
                                    response = 
                                    Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idColDef.row, 
                                            idColDef.col, 
                                            "Tipos incompatibles entre"
                                            + "columna definida [" + idColDef.valor + "] y "
                                            + "columna referenciada [" + idColRef.valor + "]");
                                    return response;
                                }

                            }else{
                                response = 
                                    Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idColRef.row, 
                                            idColRef.col, 
                                            "No existe la columna "
                                            + "[" + idColRef.valor + "]"
                                            + " referenciada por "
                                            + "la llave foranea.");
                                return response;
                            }

                        }else{
                            response = 
                                    Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idTableRef.row, 
                                            idTableRef.col, 
                                            "No existe la tabla "
                                            + "[" + idTableRef.valor + "]"
                                            + " referenciada por "
                                            + "la llave foranea.");
                            return response;
                        }
                        break;

                    case "NULO":

                        if(idPk.equals(idColDef.valor)){
                            response = 
                                    Error.lenguaje(
                                    codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico", 
                                    idColDef.row, 
                                    idColDef.col, 
                                    "La llave primaria [" + idColDef.valor + "] "
                                    + "no puede tener valores nulos.");
                            return response;
                        }else
                            nulo = true;

                        break;

                    case "AUTOINC":

                        if(!tipoCol.valor.equals("INTEGER")){
                            response = 
                                    Error.lenguaje(
                                            codigo, 
                                            "USQL", 
                                            cadUsql, 
                                            "Semantico", 
                                            idColDef.row, 
                                            idColDef.col, 
                                            "Un campo autoincremental "
                                            + "debe ser de tipo INTEGER");
                            return response;
                        }

                        break;
                }
            }

        }//fin evaluacion de complementos
        
        return response;
    }
    
    private static String analisisDeclaracionAtributos(Nodo nodo){
        String response = null;
        Nodo latr = nodo.getHijo(1);

        ArrayList<String> items = new ArrayList<>();
        int index = 1;
        
        for(Nodo atr : latr.hijos){
            Nodo idAtr = atr.getHijo(index);
            
            if(items.contains(idAtr.valor)){
                
                response = Error.lenguaje(codigo, 
                                    "USQL", 
                                    cadUsql, 
                                    "Semantico",
                                    idAtr.row, 
                                    idAtr.col, 
                                    "Multiple declaracion del atributo [" + idAtr + "]."); 
                
                return response;
            }
            items.add(idAtr.valor);
        }
        
        return response;
    }
    
}
