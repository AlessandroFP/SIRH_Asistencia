package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
//FIN ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gesti�n de turnos y calificacion de procesos
import java.util.Map;

import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.ServletTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.ejb.TurnoTrabajoFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.TurnoTrabajoFacadeRemote;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>Title: AsistenciaDelegate</p>
 * <p>Description: Clase encargada de administrar las invocaciones para las funcionalidades
 * del modulo de turnos de trabajo</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */
public class TurnoTrabajoDelegate {

  private TurnoTrabajoFacadeRemote turnoTrabajo;
  private static final Logger log = Logger.getLogger(ServletTurnoTrabajo.class);

  public TurnoTrabajoDelegate() throws TurnoTrabajoException {
    try {
      TurnoTrabajoFacadeHome facadeHome = (TurnoTrabajoFacadeHome) ServiceLocator.
          getInstance().getRemoteHome(TurnoTrabajoFacadeHome.JNDI_NAME,
                                   TurnoTrabajoFacadeHome.class);
      turnoTrabajo = facadeHome.create();
    }
    catch (Exception e) {
      BeanMensaje beanM = new BeanMensaje();
      beanM.setError(true);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new TurnoTrabajoException(beanM);
    }
  }

  /**
   *
   * @param criterio
   * @param valor
   * @return
   * @throws TurnoTrabajoException
   */
  public ArrayList buscarTurnosTrabajo(String dbpool, String criterio,
                                       String valor) throws
      TurnoTrabajoException {

    ArrayList turnos = null;
    try {
      turnos = turnoTrabajo.buscarTurnosTrabajo(dbpool, criterio, valor);
    }
    catch (IncompleteConversationalState e) {
      throw new TurnoTrabajoException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar turnos de trabajo.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new TurnoTrabajoException(beanM);
    }

    return turnos;
  }

  /**
   *
   * @param params
   * @param turnos
   * @return
   * @throws TurnoTrabajoException
   */
  public ArrayList eliminarTurnosTrabajo(String[] params, ArrayList turnos) throws
      TurnoTrabajoException {

    try {

      turnos = turnoTrabajo.eliminarTurnosTrabajo(params, turnos);

    }
    catch (IncompleteConversationalState e) {
      throw new TurnoTrabajoException(e.getBeanMensaje());
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
            "Ha ocurrido un error al eliminar turnos de trabajo.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new TurnoTrabajoException(beanM);
    }

    return turnos;
  }

  /**
   *
   * @param criterio
   * @param valor
   * @param turno
   * @param fechaIni
   * @param fechaFin
   * @param usuario
   * @param onomastico
   * @param cruce
   * @return
   * @throws TurnoTrabajoException
   */
  public String registrarTurnoTrabajo(String dbpool, String criterio,
                                      String valor,
                                      String turno, String fechaIni,
                                      String fechaFin,String obs, String usuario,
                                      String onomastico, boolean cruce,
                                      String codPers, HashMap seguridad) throws
      TurnoTrabajoException {

    String res = Constantes.OK;
    try {

      HashMap params = new HashMap();
      params.put("dbpool", dbpool);
      params.put("criterio", criterio);
      params.put("valor", valor.toUpperCase());
      params.put("turno", turno);
      params.put("fechaIni", fechaIni);
      params.put("fechaFin", fechaFin);
      params.put("obsSustento", obs);
      params.put("onomastico", onomastico);
      params.put("cruce", "" + cruce);
      params.put("codPers", codPers);
      params.put("seguridad", seguridad);
      params.put("observacion",
                 "Registro de turnos administrativos del " + fechaIni + " al " +
                 fechaFin);

      turnoTrabajo.registrarTurnoTrabajo(params, usuario);

    }
    catch (IncompleteConversationalState e) {
      throw new TurnoTrabajoException(e.getBeanMensaje());
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
            "Ha ocurrido un error al registrar turno de trabajo.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new TurnoTrabajoException(beanM);
    }

    return res;
  }

  /* *
   *
   * @param codPers
   * @param turno
   * @param fechaIni
   * @param fechaFin
   * @param estado
   * @param usuario
   * @param cruce
   * @return
   * @throws TurnoTrabajoException
   *
  public String modificarTurnoTrabajo(String dbpool, String codPers,
  									  String codUO, String turno,
                                      java.sql.Timestamp fechaIni,
                                      String fechaFin,
                                      String estado, String usuario,
                                      boolean cruce) throws
      TurnoTrabajoException { */
 //ASANCHEZZ 20100617
  /**
  * @param mapa
  */
  public String modificarTurnoTrabajo(Map mapa) throws TurnoTrabajoException {

    String res = "";
    try {
      //ASANCHEZZ 20100617
      //res = turnoTrabajo.modificarTurnoTrabajo(dbpool, codPers, codUO, turno, fechaIni, fechaFin, estado, usuario, cruce);
      res = turnoTrabajo.modificarTurnoTrabajo(mapa);
    }
    catch (IncompleteConversationalState e) {
      throw new TurnoTrabajoException(e.getBeanMensaje());
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
            "Ha ocurrido un error al modificar turno de trabajo.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new TurnoTrabajoException(beanM);
    }

    return res;
  }

  /**
   * Metodo encargado de realizar la planificacion operativa
   * @param trabajadores Lista de trabajadores a procesar
   * @param detalle Detalle de la planificacion
   * @param fechaIni Fecha Inicio
   * @param fechaFin Fecha Fin
   * @param bSabado Indica si se programaran los sabados
   * @param bDomingo Indica si se programaran los domingos
   * @param bFeriado Indica si se programaran los feriados
   * @param usuario Usuario
   * @return Resultado del proceso
   * @throws TurnoTrabajoException
   */
  public void procesarOperativo(String dbpool,
                                ArrayList trabajadores,
                                ArrayList detalle,
                                String fechaIni,
                                String fechaFin,
                                boolean bSabado,
                                boolean bDomingo,
                                boolean bFeriado,
                                String codPers,
                                String usuario) throws TurnoTrabajoException {

    try {

      turnoTrabajo.procesarOperativo(dbpool, trabajadores,
                                     detalle,
                                     fechaIni,
                                     fechaFin,
                                     bSabado,
                                     bDomingo,
                                     bFeriado,
                                     codPers,
                                     usuario);
    }
    catch (IncompleteConversationalState e) {
      throw new TurnoTrabajoException(e.getBeanMensaje());
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
            "Ha ocurrido un error al registrar turnos operativos.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new TurnoTrabajoException(beanM);
    }

  }

  /**
   *
   * @param codPers
   * @param fecha
   * @return
   * @throws TurnoTrabajoException
   */
  public BeanTurnoTrabajo buscarTrabTurno(String dbpool, String codPers,
                                          String fecha) throws
      TurnoTrabajoException {

    BeanTurnoTrabajo turno = null;
    try {

      turno = turnoTrabajo.buscarTrabTurno(dbpool, codPers, fecha);

    }
    catch (IncompleteConversationalState e) {
      throw new TurnoTrabajoException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar turno de trabajador.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new TurnoTrabajoException(beanM);
    }

    return turno;
  }

  /**
   *
   * @param fechaIni
   * @param fechaFin
   * @param codTurno
   * @param criterio
   * @param valor
   * @return
   * @throws TurnoTrabajoException
   */
  public ArrayList buscarTurnosTrabajo(String dbpool,
                                       String fechaIni, String fechaFin,
                                       String codTurno, String criterio,
                                       String valor, HashMap seguridad) throws
      TurnoTrabajoException {

    ArrayList listado = null;
    try {

      listado = turnoTrabajo.buscarTurnosTrabajo(dbpool, fechaIni, fechaFin,
                                                 codTurno, criterio,
                                                 valor, seguridad);

    }
    catch (IncompleteConversationalState e) {
      throw new TurnoTrabajoException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar turnos de trabajo.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new TurnoTrabajoException(beanM);
    }

    return listado;
  }
  
//ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestion de turnos y calificacion de procesos
  
  /* ICAPUNAY 27/04/2011- REQUERIDO X RUBEN LAZARTE 
	/**
	 * Metodo encargado de listar las Unidades Organizaciones (UUOO) Activas
	 * @param criterio String
	 * @param valor String
	 * @param orden String
	 * @return List
	 * @throws TurnoTrabajoException
	 */
/* ICAPUNAY 27/04/2011- REQUERIDO X RUBEN LAZARTE 
	public List buscarUUOOActivos(String criterio,
			String valor, String orden) throws TurnoTrabajoException {

		List uuoo = null;
		try {
			
			uuoo = turnoTrabajo.buscarUUOOActivos(criterio, valor,orden);
			
		} catch (IncompleteConversationalState e) {
			
			throw new TurnoTrabajoException(e.getBeanMensaje());
			
		} catch (RemoteException e) {
			
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al buscar Unidadades Organizacionales Activas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new TurnoTrabajoException(beanM);
		}

		return uuoo;
	}
	*/
	
	/**
	 * Metodo encargado de la Planificacion de Turnos Masivos
	 * @param dbean	DynaBean
	 * @return String
	 * @throws TurnoTrabajoException
	 */
	public String planificarTurnosMasivos(DynaBean dbean) throws TurnoTrabajoException {

		String res = Constantes.OK;
		if (log.isDebugEnabled()) log.debug("Ingreso a planificarTurnosMasivos-delegate");

		
		try {
			
						
			if (log.isDebugEnabled()) log.debug("Va a ingresar a planificarTurnosMasivos-facade");
			
			if (log.isDebugEnabled()) log.debug("dbean: "+dbean);
			
			turnoTrabajo.planificarTurnosMasivos(dbean);
			
			if (log.isDebugEnabled()) log.debug("Sali� de planificarTurnosMasivos-facade");
			

		} catch (IncompleteConversationalState e) {
			
			throw new TurnoTrabajoException(e.getBeanMensaje());
			
		} catch (RemoteException e) {
			
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al realizar la carga del archivo.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new TurnoTrabajoException(beanM);
		}

		return res;
	}
	//FIN ICAPUNAY 15/12/2010 AOM URGENTE: 44U3T10 Gestion de turnos y calificacion de procesos

}