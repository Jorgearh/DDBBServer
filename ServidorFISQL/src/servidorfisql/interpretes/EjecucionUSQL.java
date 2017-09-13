package servidorfisql.interpretes;

import servidorfisql.gui.Consola;
import servidorfisql.server.Server;
import servidorfisql.server.manejador.Archivos;

/**
 *
 * @author jorge
 */
public class EjecucionUSQL {
    
    
    public static String ejecutar(Nodo usqlSent){
        
        String response = null;
        
        switch(usqlSent.token){
                /*   SENTENCIAS DDL   */
                case "CREATE_DB":
                    
                    String idDB = usqlSent.getHijo(0).valor;
                
                    
                    /*crear el directorio de la base de datos*/
                    String dirBD = Archivos.bbddDir + idDB;
                    String dirTables = dirBD + "/tables/";
                    
                    Archivos.crearDirectorio(dirTables);
                    
                    /*crear la base de datos en memoria*/
                    Archivos.bbdd.crearBD(idDB, dirBD, dirTables);
                    Server.actualDB = idDB;
                    
                    Consola.writeln ("Base de datos [" + idDB + "] creada exitosamente\n");
                    break;
                    
                case "CREATE_TABLE":
                    
                    String idTable = usqlSent.getHijo(0).valor;
                    
                    Archivos.bbdd.crearTabla(Server.actualDB, usqlSent);
                    Consola.writeln("Tabla [" + idTable + "] creada exitosamente.\n");
                    break;
                    
                case "CREATE_OBJECT":
                    
                    String idObj = usqlSent.getHijo(0).valor;
                    
                    Archivos.bbdd.crearObjeto(Server.actualDB, usqlSent); 
                    Consola.writeln("Objeto [" + idObj + "] creado exitosamente.\n");
                    break;

                case "CREATE_USER":
                    
                    String user = usqlSent.getHijo(0).valor;
                    String pass = usqlSent.getHijo(1).valor;
                    
                    Archivos.usuarios.agregarUsuario(user, pass);
                    Consola.writeln("Usuario [" + user + "] creado exitosamente.");
                    break;
                    
                case "PROC":
                case "FUNC":
                    
                    String idMet = usqlSent.getHijo(0).valor;
                    String claseMet = usqlSent.token.equals("PROC") ? "PROCEDIMIENTO" : "FUNCION";
                    
                    Archivos.bbdd.crearProc(Server.actualDB, usqlSent);
                    Consola.writeln(claseMet + " [" + idMet + "] creado exitosamente.\n");
                    break;
                    
                case "USE":
                    
                    idDB = usqlSent.getHijo(0).valor;
                    
                    Server.actualDB = idDB;
                    Consola.writeln("Usando Base de Datos [" + idDB + "]\n");
                    break;
                    
                case "ALTER_TABLE_ADD":
                    
                    idTable = usqlSent.getHijo(0).valor;
                    Nodo lcol = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarTablaAgregar(Server.actualDB, idTable, lcol);
                    Consola.write("Se han agregado a la tabla [" + idTable + "] las columnas { ");
                    lcol.hijos.stream().forEach((col) -> {
                        Consola.append(col.getHijo(1).valor + " ");
                    });
                    Consola.append("}\n");
                    break;

                case "ALTER_TABLE_QUIT":
                    
                    idTable = usqlSent.getHijo(0).valor;
                    Nodo lid = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarTablaEliminar(Server.actualDB, idTable, lid);
                    Consola.write("Se han eliminado de la tabla [" + idTable + "] las columnas { ");
                    lid.hijos.stream().forEach((Nodo id) -> {
                        Consola.append(id.valor + " ");
                    });
                    Consola.append("}\n");
                    break;
                    
                case "ALTER_OBJECT_ADD":
                    
                    String idObject = usqlSent.getHijo(0).valor;
                    Nodo latr = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarObjetoAgregar(Server.actualDB, idObject, latr);
                            
                    Consola.write("Se han agregado al objeto [" + idObject + "] los atributos { ");
                    latr.hijos.stream().forEach((atr) -> {
                        Consola.append(atr.getHijo(0).valor + " " + atr.getHijo(1).valor + "  ");
                    });
                    Consola.append("}\n");
                    break;

                case "ALTER_OBJECT_QUIT":
                    
                    idObject = usqlSent.getHijo(0).valor;
                    lid = usqlSent.getHijo(1);
                    
                    Archivos.bbdd.modificarObjetoEliminar(Server.actualDB, idObject, lid);
                    Consola.write("Se han eliminado del objeto [" + idObject + "] los atributos { ");
                    lid.hijos.stream().forEach((id) -> {
                        Consola.append(id.valor + " ");
                    });
                    Consola.append("}\n");
                    break;
                    
                case "ALTER_USER":
                    
                    String idUser = usqlSent.getHijo(0).valor;
                    String passUser = usqlSent.getHijo(1).valor;
                    
                    Archivos.usuarios.modificarUsuario(idUser, passUser);
                    Consola.writeln("Se ha modificado el password del usuario [" + idUser + "] exitosamente.\n");
                    break;
                    
                    
                    
                case "DELETE":
                    
                    String usqlObject = usqlSent.getHijo(0).valor;
                    idObject =  usqlSent.getHijo(1).valor;
                    
                    switch(usqlObject){
                        case "DDBB":
                            
                            Archivos.bbdd.eliminarBD(idObject);
                            Consola.writeln("Se ha eliminado la Base de Datos [" + idObject + "]\n");
                            break;
                            
                        case "USER":
                            
                            //Eliminar todos los permisos para el usuario
                            Archivos.bbdd.denegarPermisosParaUsuario(idObject);
                            //Eliminar el usuario
                            Archivos.usuarios.eliminarUsuario(idObject);
                            Consola.writeln("Se ha eliminado el usuario [" + idObject + "]\n");
                            break;
                            
                        case "TABLE":
                            
                             //Eliminar tabla
                            Archivos.bbdd.eliminarTabla(Server.actualDB, idObject);
                            Consola.writeln("Se ha eliminado la tabla [" + idObject + " ]"
                                    + "de la base de datos [" + Server.actualDB + "]\n");
                            break;
                            
                        case "OBJECT":
                            
                             //Eliminar objeto
                            Archivos.bbdd.eliminarObjeto(Server.actualDB, idObject);
                            Consola.writeln("Se ha eliminado el objeto [" + idObject + " ]"
                                    + "de la base de datos [" + Server.actualDB + "]");
                            break;
                            
                        case "PROC":
                        case "FUNC":
                            
                            String nameAux = usqlSent.token.equals("PROC") ? "el procedimiento" : "la funcion";
                            
                             //Eliminar metodo
                            Archivos.bbdd.eliminarMetodo(Server.actualDB, idObject);
                            Consola.writeln("Se ha eliminado " + nameAux + "  [" + idObject + " ]"
                                    + "de la base de datos [" + Server.actualDB + "]\n");
                            break;
                    }
                    
                    break;
                    
                /*   SENTENCIAS DML*/
                case "INSERT":
                    response = insert(usqlSent);
                    break;
                case "SELECT":
                    select(usqlSent);
                    break;
                case "UPDATE":
                    break;
                case "DELETE_FROM_TABLE":
                    break;
                    
                    
                /*   SENTENCIAS DCL   */
                case "GRANT":
                    
                    idUser = usqlSent.getHijo(0).valor;
                    idDB = usqlSent.getHijo(1).valor;
                    String dbObj = usqlSent.getHijo(2).valor;
                    
                    if(dbObj.equals("*")){
                        Archivos.bbdd.otorgarPermisosEnTodo(idDB, idUser);
                        Consola.writeln("Se ha otorgado permisos al usuario [" + idUser + "] "
                                + "en todos los objetos de la Base de Datos [" + idDB + "]");
                    }else{
                        Archivos.bbdd.otorgarPermisosSiExiste(idDB, dbObj, idUser);
                        Consola.writeln("Se han otorgado los permisos al usuario [" + idUser + "] "
                                + "en el elemento [" + dbObj + "] de la Base de Datos [" + idDB + "]");
                    }
                    
                    
                    break;
                    
                case "DENY":
                    
                    idUser = usqlSent.getHijo(0).valor;
                    idDB = usqlSent.getHijo(1).valor;
                    dbObj = usqlSent.getHijo(2).valor;
                    
                    if(dbObj.equals("*")){
                        Archivos.bbdd.denegarPermisosEnTodo(idDB, idUser);
                        Consola.writeln("Se ha denegado permisos al usuario [" + idUser + "] "
                                + "en todos los objetos de la Base de Datos [" + idDB + "]");
                    }else{
                        Archivos.bbdd.denegarPermisosSiExiste(idDB, dbObj, idUser);
                        Consola.writeln("Se han denegado los permisos al usuario [" + idUser + "] "
                                + "en el elemento [" + dbObj + "] de la Base de Datos [" + idDB + "]");
                    }
                    
                    break;
                    
                default:
            }
        
        return response;
    }
    
    
    
    private static String insert(Nodo nodo){
        Nodo id = nodo.getHijo(0);
        Nodo lcol = nodo.getHijo(1);
        Nodo lexp = nodo.getHijo(2);
        
        String response = null;
        
        //Insertar normal
        if(lcol.hijos.isEmpty()){
            response = Archivos.bbdd.insertarEnTabla(Server.actualDB, id.valor, lexp);
        }else{
            response = Archivos.bbdd.insertarEnTabla(Server.actualDB, id.valor, lcol, lexp);
        }

        return response;
            
    }
    
    
    private static void select(Nodo nodo){
        
    }
    
}
