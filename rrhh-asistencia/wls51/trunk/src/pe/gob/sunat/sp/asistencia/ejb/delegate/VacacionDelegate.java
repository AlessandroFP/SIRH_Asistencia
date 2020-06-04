package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;//ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
import org.apache.commons.logging.LogFactory;//ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII



import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.VacacionFacadeRemote;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>Title: AsistenciaDelegate</p>
 * <p>Description: Clase encargada de administrar las invocaciones para las funcionalidades
 * del modulo de vacaciones</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */
public class VacacionDelegate {

  private VacacionFacadeRemote vacacion;
  private final Log log = LogFactory.getLog(getClass()); //ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII

  public VacacionDelegate() throws VacacionException {
    try {
      VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator.
          getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
                                   VacacionFacadeHome.class);
      vacacion = facadeHome.create();
    }
    catch (Exception e) {
      BeanMensaje beanM = new BeanMensaje();
      beanM.setError(true);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new VacacionException(beanM);
    }
  }

  /**
   *
   * @param anno
   * @param criterio
   * @param valor
   * @param usuario
   * @return
   * @throws VacacionException
   */
/*  public String generarVacaciones(String dbpool, String anno, String criterio,
                                  String valor, String codPers,
                                  String usuario, HashMap seguridad) throws
      VacacionException {*/
  public String generarVacaciones(Map params) throws VacacionException {  

    String res = Constantes.OK;
    try {

/*      HashMap params = new HashMap();
      params.put("anno", anno);
      params.put("criterio", criterio);
      params.put("valor", valor);
      params.put("usuario", usuario);
      params.put("dbpool", dbpool);
      params.put("codPers", codPers);
      params.put("seguridad", seguridad);
      params.put("observacion", "Proceso de generacion de saldos vacacionales para el " + anno); */

      //JRR - 06/05/2011
      params.put("codPers", params.get("codigo").toString());//Para registraLog en QueueDAO
      params.put("observacion", "Proceso de generacion de saldos vacacionales para el " + params.get("anno").toString());
      
      //ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII
      if ("3".equals(params.get("regimen").toString())){//Si es Regimen=3 (Modalidad Formativa)
    	  String anno = (String) params.get("anno");
          String fecIniFormativas=anno+"0101";
          String fecFinFormativas=anno+"1231";
          //String fecFinFormativas = Utiles.obtenerFechaActual();
          params.put("fecIniFormativas", fecIniFormativas);
          params.put("fecFinFormativas", fecFinFormativas);
      }      
      //FIN ICAPUNAY 21/06/2011 FORMATIVAS-PARTEII

      if(log.isDebugEnabled()) log.debug("**** VacacionDelegate-generarVacaciones-params: "+params);//BORRAR
      if(log.isDebugEnabled()) log.debug("**** VacacionDelegate-generarVacaciones-params.get(usuario): "+params.get("usuario").toString());//BORRAR
      
      //vacacion.generarVacaciones(params, usuario);
      vacacion.generarVacaciones((HashMap)params, params.get("usuario").toString());

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al generar las vacaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return res;
  }

  /**
   *
   * @param codPers
   * @param anno
   * @param usuario
   * @return
   * @throws VacacionException
   */
  public String venderVacaciones(String dbpool, String codPers, String anno, String dias,
                                 String usuario, String annoRef,
                                 String areaRef, String numeroRef,String obs) throws
      VacacionException {

    String res = "";
    try {

      res = vacacion.venderVacaciones(dbpool, codPers, anno, dias, usuario,
                                      annoRef, areaRef, numeroRef,obs);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al vender vacaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return res;
  }

  /**
   *
   * @param codPers
   * @param params
   * @param detalle
   * @param usuario
   * @return
   * @throws VacacionException
   */
  public String firmarLibro(String dbpool, String codPers, String[] params, ArrayList detalle,
                            String usuario) throws VacacionException {

    String res = "";
    try {

      res = vacacion.firmarLibro(dbpool, codPers, params, detalle, usuario);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al firmar libro.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return res;
  }
  
  /**
  *
  * @param param
  * @return
  * @throws VacacionException
  */
 public int totalDiasVendidosPorAnno(HashMap param) throws VacacionException {

   int res = 0;
   try {

     res = vacacion.totalDiasVendidosPorAnno(param);

   }
   catch (IncompleteConversationalState e) {
     throw new VacacionException(e.getBeanMensaje());
   }
   catch (RemoteException e) {
     BeanMensaje beanM = new BeanMensaje();
     if (e.detail.getClass().getName().equals(
         "pe.gob.sunat.sol.IncompleteConversationalState")) {
       beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
     }
     else {
       beanM.setError(true);
       beanM.setMensajeerror(
           "Ha ocurrido un error al firmar libro.");
       beanM.setMensajesol("Por favor intente nuevamente.");
     }
     throw new VacacionException(beanM);
   }

   return res;
 }
 
 /**
 *
 * @param param
 * @return
 * @throws VacacionException
 */
public int totalFraccionadoPorAnio(HashMap param) throws VacacionException {

  int res = 0;
  try {

    res = vacacion.totalFraccionadoPorAnio(param);

  }
  catch (IncompleteConversationalState e) {
    throw new VacacionException(e.getBeanMensaje());
  }
  catch (RemoteException e) {
    BeanMensaje beanM = new BeanMensaje();
    if (e.detail.getClass().getName().equals(
        "pe.gob.sunat.sol.IncompleteConversationalState")) {
      beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
    }
    else {
      beanM.setError(true);
      beanM.setMensajeerror(
          "Ha ocurrido un error al firmar libro.");
      beanM.setMensajesol("Por favor intente nuevamente.");
    }
    throw new VacacionException(beanM);
  }

  return res;
}
 
 /**
 *
 * @param param
 * @return
 * @throws VacacionException
 */
public int totalVentasEnSol(HashMap param) throws VacacionException {

  int res = 0;
  try {

    res = vacacion.totalVentasEnSol(param);

  }
  catch (IncompleteConversationalState e) {
    throw new VacacionException(e.getBeanMensaje());
  }
  catch (RemoteException e) {
    BeanMensaje beanM = new BeanMensaje();
    if (e.detail.getClass().getName().equals(
        "pe.gob.sunat.sol.IncompleteConversationalState")) {
      beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
    }
    else {
      beanM.setError(true);
      beanM.setMensajeerror(
          "Ha ocurrido un error al firmar libro.");
      beanM.setMensajesol("Por favor intente nuevamente.");
    }
    throw new VacacionException(beanM);
  }

  return res;
}

/**
*
* @param param
* @return
* @throws VacacionException
*/
public int totalReprogEnSol(HashMap param) throws VacacionException {

 int res = 0;
 try {

   res = vacacion.totalReprogEnSol(param);

 }
 catch (IncompleteConversationalState e) {
   throw new VacacionException(e.getBeanMensaje());
 }
 catch (RemoteException e) {
   BeanMensaje beanM = new BeanMensaje();
   if (e.detail.getClass().getName().equals(
       "pe.gob.sunat.sol.IncompleteConversationalState")) {
     beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
   }
   else {
     beanM.setError(true);
     beanM.setMensajeerror(
         "Ha ocurrido un error al firmar libro.");
     beanM.setMensajesol("Por favor intente nuevamente.");
   }
   throw new VacacionException(beanM);
 }

 return res;
}

/**
*
* @param param
* @return
* @throws VacacionException
*/
public int numDiasDisponibleAdelanto(HashMap param) throws VacacionException {

 int res = 0;
 try {
   res = vacacion.numDiasDisponibleAdelanto(param);
 }
 catch (IncompleteConversationalState e) {
   throw new VacacionException(e.getBeanMensaje());
 }
 catch (RemoteException e) {
   BeanMensaje beanM = new BeanMensaje();
   if (e.detail.getClass().getName().equals(
       "pe.gob.sunat.sol.IncompleteConversationalState")) {
     beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
   }
   else {
     beanM.setError(true);
     beanM.setMensajeerror(
         "Ha ocurrido un error al firmar libro.");
     beanM.setMensajesol("Por favor intente nuevamente.");
   }
   throw new VacacionException(beanM);
 }

 return res;
}

/**
*
* @param mapa
* @return
* @throws VacacionException
*/
public ArrayList buscarSaldoGeneralPorAnno(String dbpool,String codPers,boolean saldoFavor, String anhoIni, String anhoFin) throws VacacionException {

 ArrayList listado = null;
 try {

   listado = vacacion.buscarSaldoGeneralPorAnno(dbpool, codPers, saldoFavor, anhoIni, anhoFin);

 }
 catch (IncompleteConversationalState e) {
   throw new VacacionException(e.getBeanMensaje());
 }
 catch (RemoteException e) {
   BeanMensaje beanM = new BeanMensaje();
   if (e.detail.getClass().getName().equals(
       "pe.gob.sunat.sol.IncompleteConversationalState")) {
     beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
   }
   else {
     beanM.setError(true);
     beanM.setMensajeerror(
         "Ha ocurrido un error al buscar saldos vacacionales.");
     beanM.setMensajesol("Por favor intente nuevamente.");
   }
   throw new VacacionException(beanM);
 }

 return listado;
}

  /**
   *
   * @param codPers
   * @param licencia
   * @param fechaIni
   * @param dias
   * @param fechaFin
   * @param observacion
   * @param usuario
   * @param hmPermit
   * @throws VacacionException
   */
  public void registrarVacacion(String dbpool, String codPers,
                                String licencia,
                                java.sql.Timestamp fechaIni,
                                int dias,String anio,
                                java.sql.Timestamp fechaFin,
                                String observacion,
                                String usuario,
                                String annoRef,
                                String areaRef,
                                String numeroRef,
                                HashMap hmPermit,
                                HashMap seguridad,
								boolean adelanto) throws
      VacacionException {

    try {

      vacacion.registrarNvaVacacion(dbpool, codPers, licencia, fechaIni,
                                    new Integer(dias), anio,fechaFin,
                                    observacion, Constantes.ACTIVO,
                                    usuario, annoRef, areaRef, numeroRef,
                                    hmPermit, seguridad, adelanto);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al registrar vacacion.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return;
  }

  /**
   *
   * @param numero
   * @param codPers
   * @param periodo
   * @param licencia
   * @param fechaIni
   * @param nvaFechaIni
   * @param dias
   * @param nvaFechaFin
   * @param observacion
   * @param usuario
   * @param hmPermit
   * @throws VacacionException
   */
  public void modificarVacacion(String dbpool,
                                String numero,
                                String codPers,
                                String periodo,
                                String licencia,
                                java.sql.Timestamp fechaIni,
                                java.sql.Timestamp nvaFechaIni,
                                int dias,String anio,
                                java.sql.Timestamp nvaFechaFin,
                                String observacion,
                                String usuario,
                                HashMap hmPermit, HashMap seguridad)

      throws VacacionException {

    try {

      vacacion.modificarVacacion(dbpool, numero, codPers, periodo,
                                 licencia, fechaIni, nvaFechaIni,
                                 new Integer(dias), anio, nvaFechaFin,
                                 observacion, usuario, hmPermit, seguridad);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al modificar vacacion.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return;
  }

  /**
   *
   * @param numero
   * @param codPers
   * @param periodo
   * @param fechaIni
   * @param anhoRef
   * @param areaRef
   * @param numRef
   * @param usuario
   * @throws VacacionException
   */
  public void suspenderVacacion(String dbpool,
                                String numero,
                                String codPers,
                                String periodo, 
                                String licencia,
                                java.sql.Timestamp fechaIni,
                                String anhoRef,
                                String areaRef,
                                String numRef,
                                String usuario) throws VacacionException {

    try {

      vacacion.suspenderVacacion(dbpool, numero, codPers, periodo, licencia, fechaIni,
                                 anhoRef, areaRef, numRef, usuario);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al suspender vacaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return;
  }

  /**
   *
   * @param codPers
   * @param licencia
   * @param anho
   * @param fechaIni
   * @param dias
   * @param fechaFin
   * @param observacion
   * @param usuario
   * @throws VacacionException
   */
  public void registrarVacacionProgramada(String dbpool,
                                          String codPers,
                                          String licencia,
                                          String anho,
                                          java.sql.Timestamp fechaIni,
                                          int dias,
                                          java.sql.Timestamp fechaFin,
                                          String observacion,
                                          String usuario,
                                          String annoRef,
                                          String areaRef,
                                          String numeroRef) throws
      VacacionException {

    try {

      vacacion.registrarVacacion(dbpool, codPers, licencia, fechaIni, anho,
                                 new Integer(dias), fechaFin,
                                 observacion, Constantes.ACTIVO, usuario,
                                 annoRef, areaRef, numeroRef);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al registrar vacacion programada.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return;
  }

  /**
   *
   * @param numero
   * @param codPers
   * @param periodo
   * @param licencia
   * @param anho
   * @param fechaIni
   * @param nvaFechaIni
   * @param dias
   * @param nvaFechaFin
   * @param observacion
   * @param usuario
   * @throws VacacionException
   */
  public void modificarVacacionProgramada(String dbpool,
                                          String numero,
                                          String codPers,
                                          String periodo,
                                          java.sql.Timestamp fechaIni,
                                          java.sql.Timestamp nvaFechaIni,
                                          java.lang.Integer dias,
                                          java.sql.Timestamp nvaFechaFin,
                                          String observacion,
                                          String usuario)

      throws VacacionException {

    try {

      vacacion.modificarVacacionProgramada(dbpool, numero, codPers, periodo,
                                           fechaIni,
                                           nvaFechaIni, dias, nvaFechaFin,
                                           observacion,
                                           usuario);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al modificar vacacion programada.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return;
  }

  /**
   *
   * @param numero
   * @param codPers
   * @param periodo
   * @param fechaIni
   * @param usuario
   * @throws VacacionException
   */
  public void eliminarVacacion(String dbpool,String numero,
                                         String codPers,
                                         String periodo,
                                         String licencia, 
                                         java.sql.Timestamp fechaIni,
                                         String usuario)

      throws VacacionException {

    try {

      vacacion.eliminarVacacion(dbpool, numero, codPers, periodo, licencia,fechaIni, usuario);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al eliminar vacacion programada.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return;
  }

  /**
   *
   * @param mapa
   * @return
   * @throws VacacionException
   */
  public ArrayList buscarSaldos(HashMap mapa) throws VacacionException {

    ArrayList listado = null;
    try {

      listado = vacacion.buscarSaldos(mapa);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar saldos vacacionales.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }

  /**
   *
   * @param mapa
   * @return
   * @throws VacacionException
   */
  public List buscarVacacionesXLicencia(HashMap mapa) throws
      VacacionException {

    List listado = null;
    try {

      listado = vacacion.buscarVacacionesXLicencia(mapa);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar vacaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }

  /**
   *
   * @param mapa
   * @return
   * @throws VacacionException
   */
  public List buscarVacacionesEfecPend(HashMap mapa) throws
      VacacionException {

    List listado = null;
    try {
      listado = vacacion.buscarVacacionesEfecPend(mapa);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar vacaciones efectivas pendientes.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }
    return listado;
  }

  /**
   *
   * @param mapa
   * @return
   * @throws VacacionException
   */
  public List buscarVacacionesEfecGoz(HashMap mapa) throws
      VacacionException {

    List listado = null;
    try {
      listado = vacacion.buscarVacacionesEfecGoz(mapa);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar efectivas gozadas.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }

  /**
   *
   * @param codPers
   * @return
   * @throws VacacionException
   */
  public HashMap buscarVacacionesGen(String dbpool, String codPers) throws
      VacacionException {

    HashMap registro = null;
    try {
      registro = vacacion.buscarVacacionesGen(dbpool, codPers);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar reincorporados.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return registro;
  }

  /**
   *
   * @param mapa
   * @throws VacacionException
   */
  public void registrarVacacionesGen(HashMap mapa) throws VacacionException {

    try {
      vacacion.registrarVacacionesGen(mapa);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al registrar reincorporado.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

  }

  /**
   *
   * @param codPers
   * @param anno
   * @param estado
   * @return
   * @throws VacacionException
   */
  public List buscarVacacionesPorFirmar(String dbpool, String codPers,
                                             String anno, String estado) throws
      VacacionException {

    List listado = null;
    try {
      listado = vacacion.buscarVacacionesPorFirmar(dbpool, codPers, anno,
          estado);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar vacaciones por firmar.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }

  /**
   *
   * @param codPers
   * @param anno
   * @param estado
   * @param tipo
   * @return
   * @throws VacacionException
   */
  public List buscarVacacionesPorAnnoEstadoLicencia(String dbpool,
      String codPers, String anno, String estado, String tipo) throws
      VacacionException {

    List listado = null;
    try {
      listado = vacacion.buscarVacacionesPorAnnoEstadoLicencia(dbpool,
          codPers, anno, estado, tipo);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar vacaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }

  /**
   *
   * @param codPers
   * @return
   * @throws VacacionException
   */
  public ArrayList buscarSaldoVenta(String dbpool, String codPers) throws
      VacacionException {

    ArrayList listado = null;
    try {
      listado = vacacion.buscarSaldoVenta(dbpool, codPers);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar saldos de venta.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }

  /**
   *
   * @param codPers
   * @param saldoFavor
   * @param anno
   * @return
   * @throws VacacionException
   */
  public ArrayList buscarSaldoAnno(String dbpool, String codPers,
                                   boolean saldoFavor, String anno,
                                   HashMap seguridad) throws
      VacacionException {

    ArrayList listado = null;
    try {
      listado = vacacion.buscarSaldoAnno(dbpool, codPers, saldoFavor,
                                         anno, seguridad);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar saldos por año.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }

  /**
   *
   * @param codPers
   * @param estado
   * @param licencia
   * @param anno
   * @return
   * @throws VacacionException
   */
  public List buscarDetalleVacaciones(String dbpool, String codPers,
                                           String estado, String licencia,
                                           String anno, HashMap seguridad) throws
      VacacionException {

    List listado = null;
    try {
      listado = vacacion.buscarDetalleVacaciones(dbpool, codPers, estado,
                                                 licencia, anno, seguridad);

    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar detalle de vacaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;
  }
  /**
  *
  * @param mapa
  * @return
  * @throws VacacionException
  */
 public List listaVentas(HashMap mapa) throws
     VacacionException {

   List listado = null;
   try {
     //Buscamos las vacaciones pendientes del trabajador
     listado = vacacion.listaVentas(mapa);
   }
   catch (IncompleteConversationalState e) {
     throw new VacacionException(e.getBeanMensaje());
   }
   catch (RemoteException e) {
     BeanMensaje beanM = new BeanMensaje();
     if (e.detail.getClass().getName().equals(
         "pe.gob.sunat.sol.IncompleteConversationalState")) {
       beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
     }
     else {
       beanM.setError(true);
       beanM.setMensajeerror(
           "Ha ocurrido un error al buscar vacaciones pendientes.");
       beanM.setMensajesol("Por favor intente nuevamente.");
     }
     throw new VacacionException(beanM);
   }

   return listado;

 }
  /**
   *
   * @param mapa
   * @return
   * @throws VacacionException
   */
  public List buscarPendientes(HashMap mapa) throws
      VacacionException {

    List listado = null;
    try {
      //Buscamos las vacaciones pendientes del trabajador
      listado = vacacion.buscarPendientes(mapa);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar vacaciones pendientes.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return listado;

  }

  /**
   *
   * @param dbpool
   * @param numero
   * @param codPers
   * @param periodo
   * @param fechaIni
   * @param annoRef
   * @param areaRef
   * @param numeroRef
   * @param usuario
   * @return
   * @throws VacacionException
   */
  public ArrayList convertirEfectiva(String dbpool,
                                     String numero,
                                     String codPers,
                                     String periodo,
                                     java.sql.Timestamp fechaIni,
                                     String annoRef,
                                     String areaRef,
                                     String numeroRef,
                                     String usuario, HashMap seguridad) throws
      VacacionException {

    ArrayList mensajes = null;
    try {
      mensajes = (ArrayList)vacacion.convertirEfectiva(dbpool, numero, codPers,
                                            periodo, fechaIni, annoRef,
                                            areaRef, numeroRef, usuario,
                                            seguridad);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al convertir vacacion programada a efectiva.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return mensajes;

  }

  /**
   *
   * @param mapa
   * @return
   * @throws VacacionException
   */
  public List buscarVacacionesTipoAnho(HashMap mapa) throws
      VacacionException {

    List lista = null;
    try {
      lista = vacacion.buscarVacacionesTipoAnho(mapa);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al buscar vacaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }
    return lista;

  }

  /**
   *
   * @param dbpool
   * @param params
   * @param lista
   * @param usuario
   * @throws VacacionException
   */
  public void calificarVacProg(String dbpool, String[] params,
                               ArrayList lista, String tipoaut, String usuario) throws
      VacacionException {
    try {
      vacacion.calificarVacProg(dbpool, params, lista, tipoaut, usuario);
    }
    catch (IncompleteConversationalState e) {
      throw new VacacionException(e.getBeanMensaje());
    }
    catch (RemoteException e) {
      BeanMensaje beanM = new BeanMensaje();
      if (e.detail.getClass().getName().equals(
          "pe.gob.sunat.sol.IncompleteConversationalState")) {
        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
      }
      else {
        beanM.setError(true);
        beanM.setMensajeerror(
            "Ha ocurrido un error al calificar vacacion programada.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new VacacionException(beanM);
    }

    return;
  }

/*  JRR - MODIFICADO PARA PROGRAMACION - 17/05/2010  */  
  /**
  *
  * @param mapa
  * @return
  * @throws VacacionException
  */ 
 public Map generarConsultaVacaciones(HashMap paramSaldos, HashMap paramVta, HashMap paramVac, HashMap paramProg) throws
     VacacionException {

   Map lista = new HashMap();
   try {
     lista = vacacion.generarConsultaVacaciones( paramSaldos,  paramVta,  paramVac, paramProg);
   }
   catch (IncompleteConversationalState e) {
     throw new VacacionException(e.getBeanMensaje());
   }
   catch (RemoteException e) {
     BeanMensaje beanM = new BeanMensaje();
     if (e.detail.getClass().getName().equals(
         "pe.gob.sunat.sol.IncompleteConversationalState")) {
       beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
     }
     else {
       beanM.setError(true);
       beanM.setMensajeerror(
           "Ha ocurrido un error al buscar vacaciones.");
       beanM.setMensajesol("Por favor intente nuevamente.");
     }
     throw new VacacionException(beanM);
   }
   return lista;

 }
/*       */
 
 	/****** JRR - 05/12/2008 ********/
	/**
	 * Devuelve una Mapa que contiene las vacaciones programadas de un trabajador
	 * EBV 11/03/2009
	 * @param mapa
	 * @return InputStream
	 * @throws QueueException
	 */
	public Map buscarProgramacionTrabajador(Map mapa) throws VacacionException {

		Map programacion = new HashMap();
		try {
			VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
							VacacionFacadeHome.class);
			VacacionFacadeRemote facadeRemote = facadeHome.create();

			programacion = facadeRemote.buscarProgramacionTrabajador(mapa);

		} catch (Exception e) {
			throw new VacacionException(e.getMessage());
		}
		return programacion;
	}
 

	/****** FRD - 20/04/2009 ********/
	 /**
	  * Devuelve una lista que contiene las vacaciones vencidas y no gozadas de un trabajador
	  * 
	  * @param datos 
	  * @return resul List
	  * @throws VacacionException
	  */
	 public List validarVacacionesVencidas(Map datos) throws VacacionException {
	  List resul = null;
	  try {
	   VacacionFacadeHome facadeHome = (VacacionFacadeHome) ServiceLocator
	     .getInstance().getRemoteHome(VacacionFacadeHome.JNDI_NAME,
	       VacacionFacadeHome.class);
	   VacacionFacadeRemote facadeRemote = facadeHome.create();
	 
	   resul = facadeRemote.validarVacacionesVencidas(datos);
	 
	  } catch (Exception e) {
	   throw new VacacionException(e.getMessage());
	  }
	  return resul;
	 }
	 
	 
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	  /**
	   * Metodo encargado de buscar un tipo de vacacion por Solicitud
	   * @param tipo Tipo de Licencia
	   * @param criterio Criterio de busqueda
	   * @param valor Valor de criterio
	   * @return Lista de licencias
	   * @throws VacacionException
	   */
	  public Map buscarVacacionPorSolRef(String dbpool, String anno_ref, String numero_ref,
	                                   String cod_pers, String mov, String estado) throws VacacionException {

	    Map vac = null;
	    try {
	      vac = vacacion.buscarVacacionPorSolRef(dbpool, anno_ref, numero_ref, cod_pers, mov, estado);
	    }
	    catch (IncompleteConversationalState e) {
	      throw new VacacionException(e.getBeanMensaje());
	    }
	    catch (RemoteException e) {
	      BeanMensaje beanM = new BeanMensaje();
	      if (e.detail.getClass().getName().equals(
	          "pe.gob.sunat.sol.IncompleteConversationalState")) {
	        beanM = ( (IncompleteConversationalState) e.detail).getBeanMensaje();
	      }
	      else {
	        beanM.setError(true);
	        beanM.setMensajeerror(
	            "Ha ocurrido un error al buscar vacacion en buscarVacacionPorSolRef().");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new VacacionException(beanM);
	    }

	    return vac;
	  }
	  //FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
  
}