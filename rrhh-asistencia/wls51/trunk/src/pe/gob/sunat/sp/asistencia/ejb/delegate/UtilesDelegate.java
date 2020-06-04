package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeRemote;
import pe.gob.sunat.sp.bean.BeanT12;


/**
 * <p>Title: AsistenciaDelegate</p>
 * <p>Description: Clase encargada de administrar las invocaciones a DAOs ya existentes</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */
public class UtilesDelegate {

  private AsistenciaFacadeRemote asistencia;

  public UtilesDelegate() throws UtilesException {
    try {
      AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator.
          getInstance().getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,
                                   AsistenciaFacadeHome.class);
      asistencia = facadeHome.create();
    }
    catch (Exception e) {
      BeanMensaje beanM = new BeanMensaje();
      beanM.setError(true);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new UtilesException(beanM);
    }
  }


  /**
   * Metodo encargado de buscar los datos de un trabajador
   * @param codPers Registro del trabajador
   * @return Datos del trabajador
   * @throws UtilesException
   */
  public HashMap buscarTrabajador(String dbpool, String codPers, HashMap seguridad) throws
      UtilesException {

    HashMap t = null;

    try {
      t = asistencia.buscarTrabajador(dbpool, codPers, seguridad);
    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar trabajador.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new UtilesException(beanM);
    }

    return t;
  }

  /**
   * Metodo de encargado de cargar los datos de un trabajador y de su inmediato
   * superior
   * @param codPers Registro del trabajador
   * @return Datos del trabajador
   * @throws UtilesException
   */
  public HashMap buscarTrabajadorJefe(String dbpool, String codPers, 
  		HashMap seguridad) throws
      UtilesException {

    HashMap t = null;

    try {
      t = asistencia.buscarTrabajadorJefe(dbpool, codPers, seguridad);
    }
    catch (IncompleteConversationalState e) {
       throw new UtilesException(e.getBeanMensaje());
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
             "Ha ocurrido un error al buscar trabajador jefe.");
         beanM.setMensajesol("Por favor intente nuevamente.");
       }
       throw new UtilesException(beanM);
     }

    return t;
  }
  
  /**
   * Metodo de encargado de cargar los datos de un trabajador y de su inmediato
   * superior
   * @param codPers Registro del trabajador
   * @return Datos del trabajador
   * @throws UtilesException
   */
  public HashMap buscarSuperiorSolicitud(HashMap datos) throws
      UtilesException {

    HashMap t = null;

    try {
      t = asistencia.buscarSuperiorSolicitud(datos);
    }
    catch (IncompleteConversationalState e) {
       throw new UtilesException(e.getBeanMensaje());
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
             "Ha ocurrido un error al buscar superior solicitud.");
         beanM.setMensajesol("Por favor intente nuevamente.");
       }
       throw new UtilesException(beanM);
     }

    return t;
  }  

  /**
   *
   * @param uorg
   * @param codPers
   * @return
   * @throws UtilesException
   */
  public HashMap buscarJefe(String dbpool, String uorg, String codPers) throws
      UtilesException {

    HashMap t = null;

    try {
      t = asistencia.buscarJefe(dbpool, uorg.trim(), codPers.trim());
    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar jefe.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new UtilesException(beanM);
    }

    return t;
  }

  /**
   *
   * @param codigo
   * @return
   * @throws UtilesException
   */
  public BeanT12 buscarUObyCodigo(String dbpool, String codigo) throws
      UtilesException {

    BeanT12 uo = null;

    try {
      uo = asistencia.buscarUObyCodigo(dbpool, codigo);
    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar UO.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new UtilesException(beanM);
    }

    return uo;

  }

  /**
   *
   * @param criterio
   * @param valor
   * @return
   * @throws UtilesException
   */
  public ArrayList buscarUOrgan(String dbpool, String criterio, String valor, HashMap seguridad) throws
      UtilesException {

    ArrayList uo = null;

    try {

      uo = asistencia.buscarUOrgan(dbpool, criterio, valor, seguridad);
    }
    catch (IncompleteConversationalState e) {
       throw new UtilesException(e.getBeanMensaje());
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
             "Ha ocurrido un error al buscar UO.");
         beanM.setMensajesol("Por favor intente nuevamente.");
       }
       throw new UtilesException(beanM);
     }

    return uo;

  }
  
//JRR - 21/06/2010 - FUSION PROGRAMACION
  /**
   * Metodo encargado obtener lista de intendencias
   * @param dbpool String, criterio String , valor String , seguridad HashMap 
   * @return ArrayList Datos de las intendencias
   * @throws UtilesException
   */
  public List buscarIntendencias(Map params) throws
  UtilesException {

	  List intendencias = null;

	  try {

		  intendencias = (ArrayList)asistencia.buscarIntendencias(params);
	  }
	  catch (IncompleteConversationalState e) {
		  throw new UtilesException(e.getBeanMensaje());
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
					  "Ha ocurrido un error al buscar UO.");
			  beanM.setMensajesol("Por favor intente nuevamente.");
		  }
		  throw new UtilesException(beanM);
	  }

	  return intendencias;

  }
//
  
  /**
   *
   * @param periodo
   * @param criterio
   * @param valor
   * @return
   * @throws UtilesException
   */
  public ArrayList buscarUOPeriodo(String dbpool, String periodo,
                                   String criterio,
                                   String valor) throws UtilesException {

    ArrayList uo = null;

    try {

      uo = asistencia.buscarUOPeriodo(dbpool, periodo, criterio, valor);

    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
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
             "Ha ocurrido un error al buscar UO.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new UtilesException(beanM);
    }

    return uo;

  }
  
  /**
  *
  * @param periodo
  * @param criterio
  * @param valor
  * @return
  * @throws UtilesException
  */
 public ArrayList buscarUOSinPeriodo(String dbpool, String periodo) throws UtilesException {

   ArrayList uo = null;

   try {

     uo = asistencia.buscarUOSinPeriodo(dbpool, periodo);

   }
   catch (IncompleteConversationalState e) {
     throw new UtilesException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar UO.");
       beanM.setMensajesol("Por favor intente nuevamente.");
     }
     throw new UtilesException(beanM);
   }

   return uo;

 }

  /**
   *
   * @param criterio
   * @param valor
   * @return
   * @throws UtilesException
   */
  public ArrayList buscarPersonal(String dbpool, String criterio, String valor, HashMap seguridad) throws
      UtilesException {

    ArrayList personal = null;

    try {
      personal = asistencia.buscarPersonal(dbpool, criterio, valor, seguridad);
    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
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
             "Ha ocurrido un error al buscar personal.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new UtilesException(beanM);
    }

    return personal;
  }

  /**
   *
   * @param criterio
   * @param valor
   * @param codTab
   * @return
   * @throws UtilesException
   */
  public ArrayList buscarT99Codigo(String dbpool, String criterio, String valor,
                                   String codTab) throws UtilesException {

    ArrayList codigos = null;

    try {
      codigos = asistencia.buscarT99Codigo(dbpool, criterio, valor, codTab);
    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
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
             "Ha ocurrido un error al buscar codigos.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new UtilesException(beanM);
    }

    return codigos;
  }

  /**
   *
   * @param codUsuario
   * @param codPers
   * @param aceptarEncargado
   * @return
   * @throws UtilesException
   */
  public boolean esJefeDe(String dbpool, String codUsuario, String codPers,
                          boolean aceptarEncargado) throws UtilesException {

    boolean res = false;

    try {

      String codJefe = codUsuario;

      HashMap bPers = buscarTrabajadorJefe(dbpool, codPers, null);
      String codUO = (String) bPers.get("t02cod_uorg");

      res = codUsuario.equalsIgnoreCase( (String) bPers.get("t12cod_jefe"));
    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
    }

    return res;
  }

  /**
   *
   * @param fechaReferencia
   * @param numDias
   * @return
   * @throws UtilesException
   */
  public String obtenerFechaSgtesDiasHabiles(String dbpool,
                                             String fechaReferencia,
                                             int numDias) throws
      UtilesException {

    String fechaSgte = "";
    try {
      fechaSgte = asistencia.obtenerFechaSgtesDiasHabiles(dbpool,fechaReferencia,numDias);

    }
    catch (IncompleteConversationalState e) {
      throw new UtilesException(e.getBeanMensaje());
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
             "Ha ocurrido un error al obtener fecha dias habiles.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new UtilesException(beanM);
    }

    return fechaSgte;
  }
  
  /**
  *
  * @param criterio
  * @param valor
  * @param codTab
  * @return
  * @throws UtilesException
  */  
 public ArrayList buscarT5864Parametro(String criterio, String valor, String codTab) throws UtilesException {

   ArrayList codigos = null;

   try {
     codigos = asistencia.buscarT5864Parametro(criterio, valor, codTab);
   }
   catch (IncompleteConversationalState e) {
     throw new UtilesException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar parametros.");
       beanM.setMensajesol("Por favor intente nuevamente.");
     }
     throw new UtilesException(beanM);
   }

   return codigos;
 }


public ArrayList buscarT5864Parametro(HashMap filtro) {
	 ArrayList codigos = null;

	   try {
	     codigos = asistencia.buscarT5864ParametroPorFiltro(filtro);
	   }
	   catch (IncompleteConversationalState e) {
	     throw new UtilesException(e.getBeanMensaje());
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
	            "Ha ocurrido un error al buscar parametros.");
	       beanM.setMensajesol("Por favor intente nuevamente.");
	     }
	     throw new UtilesException(beanM);
	   }

	   return codigos;
}
}