package pe.gob.sunat.utils;

import java.util.Comparator;

/**
 * <p>Title: PropertyComparator</p>
 * <p>Description: Clase abstracta que define los métodos de comparación entre
 * dos objetos.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SUNAT</p>
 * @author rmontes
 * @version 1.0
 */
public abstract class PropertyComparator implements Comparator {
  protected String key = ""; //Nombre de la propiedad de los objetos a buscar
  protected boolean asc = true; //Tipo de orden requerido

  /**
   * Método encargado de devolver el resultado de la comparación de dos objetos
   * de una misma clase: devuelve 0 si son iguales, -1 si el objeto aObj es menor
   * al objeto bObj y, de lo contrario, 1.
   *
   * @param aObj            Object. Primer objeto a evaluar.
   * @param bObj            Object. Segundo objeto a evaluar.
   * @return                int. Devuelve -1 si aObj es menor que bObj,
   *                        0 si son iguales y 1 si aObj es mayor que bObj
   */
  public int compare(Object aObj, Object bObj) {

  	String a = getProperty(aObj);
    String b = getProperty(bObj);

    if ( (a == null) && (b == null)) {
      return 0;
    }
    
    if (asc){
	    if (a == null) {
	      return -1;
	    }
	
	    if (b == null) {
	      return 1;
	    }
    } else {
    	if (b == null) {
  	      return -1;
  	    }
  	
  	    if (a == null) {
  	      return 1;
  	    }
    }
    
    if (asc) {
    	return a.compareTo(b);
    } else {
    	return b.compareTo(a);
    }
  }

  /**
   * Obtiene la propiedad indicada por el atributo "key" del objeto enviado
   * como parámetro.
   *
   * @param obj             Object. Objeto del cual se obtendrá la propiedad
   *                        indicada por el atributo "key".
   * @return
   */
  public abstract String getProperty(Object obj);

  /**
   * Asigna el nombre de la propiedad a evaluar al atributo "key".
   *
   * @param newKey          String. Valor con el cual se seteará la propiedad
   *                        key
   */
  public abstract void setKey(String newKey);
  
  public abstract void setAsc(boolean newAsc);

}