package pe.gob.sunat.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>Title: Ordenamiento</p>
 * <p>Description: Clase encargada de implementar los métodos de la clase
 * abtracta PropertyComparator a fin de poder ordenar una lista de HashMap</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: SUNAT</p>
 * @author rmontes
 * @version 1.0
 */

public class Ordenamiento {

  /**
   * Método encargado de obtener el resultado de la comparación de dos objetos
   * de una misma clase para ordenar los HashMaps contenidos en el ArrayList
   * recibido de acuerdo al resultado obtenido.
   *
   * @param lista           ArrayList. Listado de HashMaps a ordenar.
   * @param sortKey         String. Campo del HashMap por el cual ordenar
   *                        el listado.
   * @param sortAsc			boolean. Indica si el tipo de ordenamiento es 
   *                        ascendente o no.
   * @return                ArrayList. Lista ordenada.
   */
  public static ArrayList sortByKey(ArrayList lista, String sortKey, boolean sortAsc) {

    PropertyComparator comparator = new PropertyComparator() {
      public String getProperty(Object obj) {
        return (obj == null) ? null : (String) ( (HashMap) obj).get(key);
      }

      public void setKey(String newKey) {
        key = newKey;
      }
      
      public void setAsc(boolean newAsc) {
        asc = newAsc;
      }
    };

    sort(lista, comparator, sortKey, sortAsc);
    return lista;
  }

  /**
   * Método encargado de ordenar una lista de HashMaps.
   *
   * @param lista           ArrayList. Listado de HashMaps a ordenar.
   * @param comparator      PropertyComparator. Que contiene los datos de la
   *                        comparación realizada
   * @param sortKey         String. Campo del HashMap por el cual ordenar
   *                        el listado.
   * @param sortAsc			boolean. Indica si el tipo de ordenamiento es 
   *                        ascendente o no.
   */
  private static void sort(ArrayList lista, PropertyComparator comparator, 
  						  String sortKey, boolean sortAsc) {

    if (lista == null) {
      return;
    }

    comparator.setKey(sortKey);
    comparator.setAsc(sortAsc);
    Object[] array = lista.toArray();
    Arrays.sort(array, comparator);
    lista.clear();
    for (int i = 0; i < array.length; ++i) {
      lista.add(array[i]);
    }
  }

}
