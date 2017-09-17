/*
 *Clase encargada de la Ejecucion de Codigo de alto nivel
 */
package servidorfisql.interpretes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import servidorfisql.gui.Consola;
import servidorfisql.server.manejador.Atributo;
import servidorfisql.server.manejador.Metodo;
import servidorfisql.server.manejador.Objeto;

/**
 * @author jr
 */
@SuppressWarnings("UseOfObsoleteCollectionType")
public class Execute {

    public Hashtable<String, Simbolo> tablaLocal;//guardamos solo variables
    public Hashtable<String, Simbolo> tablitaObjetos;//guardamos solo objetos
    public Hashtable<String, Simbolo> tablaGlobal;//guardamos metodo y atributos
    public Simbolo nameLienzo;
    public String path;
    String nameMetod;
    String tiposMet;
    Operacion operar;
    boolean exit;
    boolean retorno;
    boolean continuar;
 
    Simbolo retorna;
  
    JTextArea txtArea;
    
    HashMap<String, Simbolo> hashtemp;

    public Execute() {
        tablaLocal = new Hashtable<>();
        tablaGlobal = new Hashtable<>();
        
        tablitaObjetos = new Hashtable<>();
        hashtemp = new HashMap<>();
        
        operar = new Operacion();
        
        nameMetod = "";
        retorna = null;
        exit = false;
        nameLienzo = new Simbolo();
       
        retorno = false;
        path = "";
     
    }


    @SuppressWarnings("UnusedAssignment")
    public Simbolo addVariables_Funciones(HashMap lista) {

        Simbolo result = null;
        if (lista == null) {
            return null;
        }
        
        HashMap<String,Metodo> metfun;
        //vamos a recorrer la hash para agregar llas funciones y metodos
       metfun=lista;
        
        
        Iterator it = metfun.entrySet().iterator();
        while (it.hasNext()) {
        Map.Entry e = (Map.Entry)it.next();
        Metodo tmp=(Metodo)e.getValue();
        
        String name=tmp.idMethod;
        String tippo=tmp.tipoMetodo;
                
               Nodo parametros=tmp.parametros;
               Nodo sentencias=tmp.lsent;
               
        if(tmp.tipoMetodo.equalsIgnoreCase("void")) {
            
                Simbolo newFuntion = new Simbolo();
                newFuntion.metodo = new Proc();
                newFuntion.tipe = "void";
                newFuntion.rol = "metodo";
              
                newFuntion.name = tmp.idMethod;//nombre de la funcion
            
                nameMetod = tmp.idMethod;
                String key;
          
                //if (root.hijos.size() == 3) {
                    Nodo para = parametros;
                    Nodo body = sentencias;
                    if (para.token.equals("LARG") && body.token.equals("LSENT")) {

                        tiposMet = "";
                        tipoParametros(para, 1, null);//tipos de parametros                      
                        nameMetod += tiposMet;
                        tipoParametros(para, 2, newFuntion.metodo.parametro);//agregando los parametros a la funcion
                        newFuntion.metodo.sentencias = body;
                    }
                //}
                newFuntion.name = key = nameMetod;
                tablaGlobal.put(key, newFuntion);//insertamos a la tabla Funciones

              
            }else {
                Simbolo newFuntion = new Simbolo();
                newFuntion.metodo = new Proc();
                newFuntion.tipe = getTipo(tmp.tipoMetodo);
                newFuntion.rol = "funcion";
                
                newFuntion.name = tmp.idMethod;//nombre de la funcion
                
                nameMetod = tmp.idMethod;
                String key;
             
                //if (root.hijos.size() == 4) {
                    Nodo para = parametros;
                    Nodo body = sentencias;
                    if (para.token.equals("LARG") && body.token.equals("LSENT")) {

                        tiposMet = "";
                        tipoParametros(para, 1, null);//tipos de parametros                      
                        nameMetod += tiposMet;
                        tipoParametros(para, 2, newFuntion.metodo.parametro);//agregando los parametros a la funcion
                        newFuntion.metodo.sentencias = body;
                    }
               // }
                newFuntion.name = key = nameMetod;
                tablaGlobal.put(key, newFuntion);//insertamos a la tabla Funciones
                
              
            
            }
            
          

        
               
               
        }
        
        

        return result;
    }
    
    public Simbolo addObjetos(HashMap objetoss){
        
    Simbolo result = null;
        if (objetoss == null) {
            return null;
        }
        HashMap<String, Objeto> objetos;
        
        objetos=objetoss;
        Iterator it = objetos.entrySet().iterator();
        while (it.hasNext()) {
        Map.Entry e = (Map.Entry)it.next();
        Objeto tmp=(Objeto)e.getValue();
        Simbolo nuevoobj=new Simbolo();
        
        String nameobj=tmp.idObjeto;
        nuevoobj.tipe = tmp.idObjeto;
        nuevoobj.rol = "objeto";
        nuevoobj.isobjeto=true;
        nuevoobj.atributos=new Hashtable();
        nuevoobj.name = tmp.idObjeto;//nombre del objeto
    
        
        //recorremos atributos
        Iterator atribut =tmp.atributes.atributos.entrySet().iterator();
        while (atribut.hasNext()) {
            Map.Entry f = (Map.Entry)atribut.next();
            Simbolo newat=new Simbolo();
            Atributo newatr=(Atributo)f.getValue();
            
            newat.name = newatr.idAtributo;
            newat.tipe = getTipo(newatr.tipoAtributo);
            newat.ambito = nameobj;
            newat.rol = "atributo";
            newat.value = setValDefault(newat.tipe);
            
            nuevoobj.atributos.put(newatr.idAtributo, newat);
        }
        
        tablitaObjetos.put(nameobj, nuevoobj);
        }
        return result;
    }
    
    public Simbolo addObjetounico(Objeto objetoss){
        
    Simbolo result = null;
        if (objetoss == null) {
            return null;
        }
        
        Objeto tmp=objetoss;
        Simbolo nuevoobj=new Simbolo();
        
        String nameobj=tmp.idObjeto;
        nuevoobj.tipe = tmp.idObjeto;
        nuevoobj.rol = "objeto";
        nuevoobj.isobjeto=true;
        nuevoobj.atributos=new Hashtable();
        nuevoobj.name = tmp.idObjeto;//nombre del objeto
    
        
        //recorremos atributos
        Iterator atribut =tmp.atributes.atributos.entrySet().iterator();
        while (atribut.hasNext()) {
            Map.Entry f = (Map.Entry)atribut.next();
            Simbolo newat=new Simbolo();
            Atributo newatr=(Atributo)f.getValue();
            
            newat.name = newatr.idAtributo;
            newat.tipe = getTipo(newatr.tipoAtributo);
            newat.ambito = nameobj;
            newat.rol = "atributo";
            newat.value = setValDefault(newat.tipe);
            
            nuevoobj.atributos.put(newatr.idAtributo, newat);
        }
        
        if(!tablitaObjetos.containsKey(nameobj)){
            tablitaObjetos.put(nameobj, nuevoobj);
        }
        
        return result;
    }
    public String getTipo(String tipe) {
        if (tipe.equalsIgnoreCase("integer")) {
            return "integer";
        } else if (tipe.equalsIgnoreCase("double")) {
            return "double";
        } else if (tipe.equalsIgnoreCase("bool")) {
            return "bool";
        } else if (tipe.equalsIgnoreCase("date")) {
            return "date";
        } else if (tipe.equalsIgnoreCase("datetime")) {
            return "datetime";
        } else if (tipe.equalsIgnoreCase("text")) {
            return "text";
        }else if (!tipe.equalsIgnoreCase("")) {
            return tipe;
        }
        return "";
    }
    
    protected boolean esTipoobjeto(String tipe) {
        if (tipe.equalsIgnoreCase("integer")) {
            return false;
        } else if (tipe.equalsIgnoreCase("double")) {
            return false;
        } else if (tipe.equalsIgnoreCase("bool")) {
            return false;
        } else if (tipe.equalsIgnoreCase("date")) {
            return false;
        } else if (tipe.equalsIgnoreCase("datetime")) {
            return false;
        } else if (tipe.equalsIgnoreCase("text")) {
            return false;
        }else if (!tipe.equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    
    
    private void tipoParametros(Nodo root, int opcion, ArrayList<Simbolo> parame) {
        switch (opcion) {
            case 1: {
                
            for(Nodo hijo : root.hijos) {
                //solo obteniendo tipos para el nombre del metodo
                tiposMet += ">" + getTipo(hijo.hijos.get(0).valor);             
            }
                break;
            }
            case 2: {
                //se guardan los parametros en la lista de parametros del metodo
                    
            for(Nodo hijo : root.hijos) {
                Simbolo var = new Simbolo();
                var.name = hijo.hijos.get(1).valor;
                var.tipe = getTipo(hijo.hijos.get(0).valor);
                var.ambito = nameMetod;
                var.rol = "Parametro";
                var.acceso = "privado";
                parame.add((Simbolo) var);
            }
                
                break;
            }
        }

    }

    private Simbolo declararVar(Nodo root, String ambito, int caso) {//0 global // 1 local
        Simbolo result = null;
        switch (root.token) {
            case "DEC": {
                String tippo= root.hijos.get(1).valor;
                Simbolo vallor;
                if(root.hijos.size()==3){
                    vallor=Expre(root.hijos.get(2));
                    result=vallor;
                }
                boolean esobjeto=esTipoobjeto(tippo);
                
                if(esobjeto){
                    if(tablitaObjetos.containsKey(tippo)){
                    
                        Simbolo tmpobjeto=tablitaObjetos.get(tippo);
                        for(Nodo variable:root.hijos.get(0).hijos){
                            if(variable.token.equalsIgnoreCase("var")){
                                tmpobjeto.name=variable.valor;
                                tmpobjeto.ambito = ambito;
                                
                                String key = tmpobjeto.name;
                                 if (caso == 1) {
                                    key = ambito + ">" + tmpobjeto.name;
                                    tablaLocal.put(key, tmpobjeto);
                                }
                                
                            }
                        }
                        
                        
                    }else{
                        msjError(root, " objeto no creado, no se puede instancaiar");
                                System.out.println("objeto no creado, No es posible crear en declaracion");
                    
                    }
                }else{
                    for(Nodo variable:root.hijos.get(0).hijos){
                    if(variable.token.equalsIgnoreCase("var")){
                        Simbolo var = new Simbolo();
                        var.name=variable.valor;
                        var.tipe=getTipo(tippo);
                        //boolean objeto=esTipoobjeto(tippo);
                        var.ambito = ambito;
                        var.rol = "variable";
                        
                        if (result != null) {
                            if (operar.casteoAsignacion(var, result)) {
                                var.value = result.value;
                            } else {
                                msjError(root, " no es posible castear");
                                System.out.println("No es posible asignar en declaracion");
                            }
                        } else {
                            var.value = setValDefault(tippo);
                        }
                    
                        String key = variable.valor;
                        if (caso == 1) {
                            key = ambito + ">" + var.name;
                            tablaLocal.put(key, var);
                        }
                    }
                }
                
            }
                
//                System.out.println("Declaracion " + key);

                break;
            }
            
        }
        return result;
    }

    private String setValDefault(String tip) {
        switch (tip) {
            case "integer":
            case "double":
                return "0";

            case "date":
            case "datetime":
            case "text":
                return null;

            case "bool":
                return "0";

            default:
                return "Error";
        }
    }

    @SuppressWarnings("UnusedAssignment")
    public Simbolo Expre(Nodo expr) {

        Simbolo result=null;
        if (expr == null) {
            return null;
        }
        switch (expr.token) {
            case "+":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.sumar(val1, val2);
                return result;
            
            }
            case "-":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.restar(val1, val2);
                return result;
            
            }
            case "*":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.multiplicar(val1, val2);
                return result;
            
            }
            case "/":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.multiplicar(val1, val2);
                return result;
            
            }
            case "_":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                
                result=operar.unario(val1);
                return result;
            
            }
            case "^":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.multiplicar(val1, val2);
                return result;
            
            }
            case "<":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.menor(val1, val2);
                return result;
            
            }
             case ">":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.mayor(val1, val2);
                return result;
            
            }
             case ">=":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.mayorIgual(val1, val2);
                return result;
            
            }
            case "<=":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.menorIgual(val1, val2);
                return result;
            
            }
            
            case "==":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.igualar(val1, val2);
                return result;
            
            }
            
            case "!=":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar.distinto(val1, val2);
                return result;
            
            }
            
            case "||":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar._or(val1, val2);
                return result;
            
            }
            case "&&":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
                Simbolo val2=Expre((Nodo)expr.hijos.get(1));
                result=operar._and(val1, val2);
                return result;
            
            }
            case "!":{
                Simbolo val1=Expre((Nodo)expr.hijos.get(0));
             
                result=operar._not(val1);
                return result;
            
            }
            
            default:{
                return getValue(expr);
            
            }
            
            
        }
            
        // return null;
    }

    @SuppressWarnings("UnusedAssignment")
    private Simbolo getValue(Nodo expr) {//H
        Simbolo valor = new Simbolo();
        operar.valorEval = expr;
        switch (expr.token) {
            case "ENT": {//entero
                valor.tipe = "integer";
                valor.value = expr.valor;
                break;
            }
            case "DOB": {//doble
                valor.tipe = "double";
                valor.value = expr.valor;

                break;
            }
            case "BOOL": {//caracter
                valor.tipe = "bool";
                valor.value="0";
                if(expr.valor.equalsIgnoreCase("true")){
                    valor.value="1";
                
                }else{
                    valor.value="0";
                }
                break;
            }
            case "CAD": {//cadena
                valor.tipe = "text";
                String cad = expr.valor.replace("\"", "");
                valor.value = cad;
                break;
            }
            case "FECHA": {//date
                valor.tipe = "date";
                String caracter = expr.valor.replace("'", "");
                valor.value = caracter;
                break;
            }
            case "FECHAHORA": {//datetime
                valor.tipe = "datetime";
                String caracter = expr.valor.replace("'", "");
                valor.value = caracter;
                break;
            }
            case "VAR": {//variable
                valor = operar.getVariable(expr.valor, Execute.this, valor);
                break;
            }
            case ".": {//AtributodeObjeto
                valor=operar.getVariable(expr.hijos.get(0).valor, Execute.this, valor);
                if(valor.isobjeto && valor.atributos.containsKey(expr.hijos.get(1).valor)){
                    Simbolo atributo=valor.atributos.get(expr.hijos.get(1).valor);
                    valor=atributo;
                }
                //valor = Expre((SimpleNode) expr.jjtGetChild(0));
                break;
            }
            case "->": {//obtiene de la hashtemporal
                String nombrecol=expr.hijos.get(0).valor+"."+expr.hijos.get(1).valor;
                
                valor= hashtemp.get(nombrecol);//obtenemos el valor de la columna. para select
                
                break;
            }
            case "CALL": {//llamada metodo
                valor = callMetodo(expr, valor, 1);
                break;
            }
            case "CONTAR": {//cuenta
                valor.tipe = "integer";
                String caracter = expr.valor.replace("'", "");
                valor.value = caracter;
                break;
            }
            
            case "GET_DATE": {//hala fecha
                
                String date=new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                valor.tipe = "date";
                valor.value = date;
                break;
            }
            case "GET_DATE_TIME": {//hala fecha y hora
                String datetime=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
                valor.tipe = "datetime";
                              valor.value = datetime;
                break;
            }
            
            case "NULO": {//valor nulo
                valor.tipe="text";
                valor.value="NULO";
                break;
            }
            
            default:
                System.err.println("TOKEN = "+  expr.token);
                System.err.println("TOKEN = "+  expr.valor);
                break;

        }
        return valor;
    }

    protected Simbolo callMetodo(Nodo root, Simbolo valor, int opcion) {
        String actualAmb = nameMetod;
        tiposMet = "";
        if (root.hijos.size() == 2) {
            tipoParametro2((Nodo)root.hijos.get(1), null, 0, 1);
        }
        nameMetod = root.hijos.get(0).valor + tiposMet;//nombre del metodo a llamar con tipo parametros

        String key = nameMetod;
        if (tablaGlobal.containsKey(key)) {

            Simbolo myMeto = tablaGlobal.get(key);//obteniendo la funcion
            Proc funcion = myMeto.metodo;

            Execute ejecut = new Execute();
            ejecut.tablaGlobal = this.tablaGlobal;
            ejecut.nameMetod = nameMetod;
           

            if (funcion.parametro.isEmpty() && root.hijos.size() == 1) {//si no tiene parametros lo ejecuta
                ejecut.executeCode(funcion.sentencias);
            } else {
                //colocando parametros
                nameMetod = actualAmb;
                tipoParametro2((Nodo)root.hijos.get(1), funcion.parametro, 0, 2);//obteniendo valores                
                setParametrosInTabla(funcion.parametro, ejecut.tablaLocal);//pasandolos a la tabla de simbolos
                ejecut.executeCode(funcion.sentencias);
            }
            if (opcion == 1) {//para retornar el valor
                if (ejecut.retorna == null && !myMeto.tipe.equalsIgnoreCase("void")) {
                    msjError(root, " este metodo no tiene un return");
                    System.out.println("Error en el retorn del metodo");
                } else if (myMeto.tipe.equalsIgnoreCase("void")) {
                    if (ejecut.retorna != null) {
                        msjError(root, " este metodo es de tipo void y tiene asignado un return");
                    } else {
                        msjError(root, " este metodo no retorna ningun tipo de dato");
                    }
                } else if (myMeto.tipe.equals(ejecut.retorna.tipe) && !myMeto.tipe.equals("")) {
                    valor.tipe = myMeto.tipe;
                    valor.value = ejecut.retorna.value;
                    valor.isobjeto = ejecut.retorna.isobjeto;
                    valor.acceso = ejecut.retorna.acceso;
                    if (valor.isobjeto) {
                        valor.atributos = ejecut.retorna.atributos;
                    }
                    funcion.retornar = ejecut.retorna;
                } else {
                    msjError(root, " el dato retornado no son del mismo tipo del metodo");
                }
            }

            //tablaLocal.clear();
            //tablaLocal.putAll(aux);
            //obteniendo las variables del metodo llamado
            copiarData(tablaLocal, ejecut.tablaLocal);
            retorno = false;
            exit = false;
            continuar = false;
            retorna = null;
        } else {
            msjError(root, " metodo no esta declarado");
            System.out.println("No Existe el metodo " + nameMetod);
        }
        nameMetod = actualAmb;
        return valor;
    }

    private void copiarData(Hashtable<String, Simbolo> copia, Hashtable<String, Simbolo> original) {
        Enumeration e = original.keys();
        String clave;
        Simbolo valor;
        while (e.hasMoreElements()) {
            clave = (String) e.nextElement();
            valor = original.get(clave);
            copia.put(clave, (Simbolo) valor.clone());
        }

    }

    private void setParametrosInTabla(ArrayList<Simbolo> paramet, Hashtable<String, Simbolo> copiaTab) {
        for (Simbolo sim : paramet) {
            copiaTab.put(sim.ambito + ">" + sim.name, (Simbolo) sim.clone());
        }
    }

    private void tipoParametro2(Nodo root, ArrayList<Simbolo> paramet, int i, int caso) {
        
        switch (caso) {
            case 1: {
                for(Nodo hijo : root.hijos) {
                //solo obteniendo tipos para el nombre del metodo
                Simbolo valor = Expre((Nodo) hijo);
                if (!valor.tipe.equals("")) {
                    tiposMet += ">" + valor.tipe;
                }             
            }
                
                break;
            }
            case 2: {
                
                for(Nodo hijo:root.hijos){
                    Simbolo valor = Expre((Nodo) hijo);
                    Simbolo par = paramet.get(i);
                    if (par.tipe.equals(valor.tipe) && !valor.tipe.equals("")) {
                    par.value = valor.value;
                } else {
                    msjError(root, " no son del mismo tipo parametro del metodo");
                    System.out.println("Error semantico en parametros " + par.name);
                }
                    
                    i++;
                
                }
                
                
                break;
            }
        }
        
    }

   

    public void initExecute(String idMetodo) {

      
        //recorremos tabla Global
        

        if (tablaGlobal.containsKey(idMetodo)) {
            Simbolo main_ = tablaGlobal.get(idMetodo);
            nameMetod = idMetodo;
            exit = false;
            retorno = false;
            executeCode(main_.metodo.sentencias);
        } 
       
    }

    private void asignacion(Simbolo variable, Nodo root, Simbolo valor) {
        
        if (operar.casteoAsignacion(variable, valor)) {
            variable.value = valor.value;
        } else {
            msjError(root, " no es posible realizar la asignacion,\n porque no se puede castear");
            System.out.println("Error en asignacion no se puede asignar");
        }
    }

    private boolean accion(Nodo root) {
        operar.valorEval = root;
        Simbolo variable = operar.getVariable(root.hijos.get(0).valor, Execute.this, new Simbolo());//es para verificar si la variable existe
        if (variable.name.equals("")) {
            return false;
        }
        
       
        switch (root.token) {
            
            case "++": {//++
               
                    operar.incrementar(variable);
                
                break;
            }
            case "--": {//--
               
                    operar.decremetar(variable);
                
                break;
            }
        }
        return true;
    }

     String tipocase="";
     String valcase="";
    protected Simbolo executeCode(Nodo root) {
        Simbolo RESULT = null;
        if (root == null) {
            return null;
        }
        switch (root.token) {
            case "LSENT": {
                
                if (!root.hijos.isEmpty() && !exit && !retorno) {

                    for (Nodo sentencia: root.hijos){
                        executeCode(sentencia);
                    }
                }
                break;
            }
            case "=": {
                operar.valorEval = root;
                
                if(root.hijos.get(0).token.equalsIgnoreCase(".")){
                    Nodo ooo=root.hijos.get(0);
                    Simbolo value = Expre(root.hijos.get(1));
                    
                    Nodo obb=ooo.hijos.get(0);
                    Nodo atr=ooo.hijos.get(1);
                    
                    Simbolo objeto = operar.getVariable(obb.valor, Execute.this, new Simbolo());
                    
                        if(objeto.isobjeto && objeto.atributos.containsKey(atr.valor)){
                            
                            asignacion(objeto.atributos.get(atr.valor), root, value);
                        
                        }else{
                            msjError(root, " no existe atributo");
                                System.out.println("Error en asignacion atributo no existe");
                        
                        }
                   
                }
                else{
                Simbolo variable = operar.getVariable(root.hijos.get(0).valor, Execute.this, new Simbolo());
                
                 if (!variable.name.equals("") && !variable.isobjeto) {
                                Simbolo value = Expre(root.hijos.get(1));
                                asignacion(variable, root, value);
                 }else {
                                msjError(root, " la variable no existe");
                                System.out.println("Error en asignacion la variable no existe");
                 }
                }
                
                 break;
            }

               
            case "DEC": {
               
                declararVar(root, nameMetod, 1);//variables o arreglos
                break;
            }
            case "IF": {
             
                Simbolo cond = Expre((Nodo)root.hijos.get(0));
                if (cond.value.equals("1") || cond.value.equals("0")) {

                    if (cond.value.equals("1")) {//if
                        executeCode((Nodo) root.hijos.get(1));
                    }

                    if (root.hijos.size() == 3 && cond.value.equals("0")) {//else
                        executeCode((Nodo) root.hijos.get(2));
                    }
                } else {
                    msjError(root, " la sentencia del if, no es un dato boolean");
                }
                break;
            }
            case "PRINT": {
                
                Simbolo expr = Expre((Nodo) root.hijos.get(0));
                System.out.println("Print( " + expr.value + " );");
                Consola.writeln(expr.value);
                break;
            }
            case "WHILE": {
                exit = false;
                //continuar = false;

                Simbolo cond = Expre((Nodo) root.hijos.get(0));

                if (cond.value.equals("1") || cond.value.equals("0")) {

                    while (cond.value.equals("1")) {
                      

                        executeCode((Nodo) root.hijos.get(1));
                        if (exit || retorno) {
                            break;
                        }
                        cond = Expre((Nodo) root.hijos.get(0));
                        //continuar = false;
                    }
                } else {
                    msjError(root, " la sentencia del Mientras, no es un dato boolean");
                }
                exit = false;
                break;
            }
            
           
            case "SWITCH": {
                exit = false;
                //continuar = false;
             valcase="false";

               

                Simbolo expr = Expre((Nodo) root.hijos.get(0));
                Nodo lcaso = (Nodo) root.hijos.get(1);
                
                for(Nodo caso: lcaso.hijos){
                
                    caso.tipocase = expr.tipe;//le mandamos el tipo a los case
                caso.valcase= expr.value;//mandamos el valor a los case
                executeCode(caso);//nos indica si cumplio con algun case o no
                }
                
                if (!exit && root.hijos.size() == 3 && !retorno) {//si no cumplio con algun case error
                    executeCode((Nodo) root.hijos.get(2));
                }
                exit = false;
                //continuar = false;
                break;
            }
            case "CASE": {
              
                Simbolo expr = Expre((Nodo) root.hijos.get(0));
                //System.out.println("otro caso " + expr.value);
                if (expr.value.equals(root.valcase) || valcase.equals("true")) {//si cumple con un case o que se ejecute simultaneo el otro porque no vino un salir
                    //System.out.println("son iguales");
                    executeCode((Nodo) root.hijos.get(1));//si no vino salir en este case se ejecuta el resto
                    if (!exit  && !retorno) {//si no hay break o retorno
                        
                        valcase = "true";//le mandamos true para que se ejecute el siguiente case
                        
                    }else{
                        valcase="false";
                    }
                } 
                break;
            }
            case "BREAK": {
                
                exit = true;
                break;
            }
            case "FOR": {
                //continuar = false;
                exit = false;

                executeCode((Nodo) root.hijos.get(0));//asignando variable inicial
                Simbolo cond = Expre((Nodo) root.hijos.get(1));//condicion del for
                if (cond.value.equals("1") || cond.value.equals("0")) {
                    while (cond.value.equals("1")) {
                        
                        /*se ejecuta si y solo si es verdadera la condicion, y 
                         se sale cuando la condicion sea false
                         /*ejecutamos sentencias*/
                        executeCode((Nodo) root.hijos.get(3));

                        if (!accion((Nodo) root.hijos.get(2)) || exit || retorno)//incremento
                        {
                            break;
                        }
                        //continuar = false;
                        cond = Expre((Nodo) root.hijos.get(1));//condicion final del for  

                    }
                } else {
                    msjError(root, " la sentencia del Hacer, no es un dato boolean");
                }
                exit = false;
                break;
            }
            
            case "RETURN": {
                
                retorna = Expre((Nodo) root.hijos.get(0));
                retorno = true;
                break;
            }
            case "CALL": {//llamada metodo
                callMetodo(root, new Simbolo(), 0);
                break;
            }
            
            case "CREATE_TABLE":
            case "CREATE_OBJECT":
            case "INSERT":
            case "UPDATE":
            case "DELETE_FROM_TABLE":
            case "SELECT":{
                String result = InterpreteUSQL.interpretarSentenciaUsql(0, root, "");
                Consola.writeln(result);
                break;
            }
            
            default:
                break;
        }
        return RESULT;
    }

    protected void msjError(Nodo root, String texto) {
        //Token val = (Token) root.token;
//        Principal.errores.add(
//                new ErrorS("Semantico",
//                        String.valueOf(root.row),
//                        String.valueOf(root.col),
//                        "<b>" + root.token + "</b>" + texto));
    }

    

}
