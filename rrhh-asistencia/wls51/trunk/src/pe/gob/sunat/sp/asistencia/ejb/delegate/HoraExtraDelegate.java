package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.HoraExtraFacadeRemote;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>Title: AsistenciaDelegate</p>
 * <p>Description: Clase encargada de administrar las invocaciones para las funcionalidades
 * del modulo de labor excepcional</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Sunat</p>
 * @author cgarratt
 * @version 1.0
 */
public class HoraExtraDelegate {

  private HoraExtraFacadeRemote horaextra;

  public HoraExtraDelegate() throws HoraExtraException {
    try {
      HoraExtraFacadeHome facadeHome = (HoraExtraFacadeHome) ServiceLocator.
          getInstance().getRemoteHome(HoraExtraFacadeHome.JNDI_NAME,
                                   HoraExtraFacadeHome.class);
      horaextra = facadeHome.create();
    }
    catch (Exception e) {
      BeanMensaje beanM = new BeanMensaje();
      beanM.setError(true);
      beanM.setMensajeerror(e.getMessage());
      beanM.setMensajesol("Por favor intente nuevamente.");
      throw new HoraExtraException(beanM);
    }
  }

  /**
   * Metodo encargado de registrar una autorizacion de hora extra
   * @param criterio Criterio para registrar por persona o unidad
   * @param valor Valor del criterio
   * @param codUO Unidad Organizacional
   * @param fecha Fecha de autorizacion
   * @param horaIni Hora inicio de autorizacion
   * @param horaFin Hora fin de autorizacion
   * @param obs Observacion
   * @param codJefe Codigo del jefe
   * @param usuario Usuario
   * @throws HoraExtraException
   */
  public void registrarAutorizacionHE(String dbpool, ArrayList trabajadores,
                                      String codUO, String fecha,
                                      String horaIni,
                                      String horaFin, String obs,
                                      String codJefe, String usuario,
                                      HashMap seguridad) throws
      HoraExtraException {

    try {

      HashMap params = new HashMap();
      params.put("dbpool", dbpool);
      params.put("codUO", codUO);
      params.put("fecha", fecha);
      params.put("horaIni", horaIni);
      params.put("horaFin", horaFin);
      params.put("obs", obs);
      params.put("codJefe", codJefe);
      params.put("codPers", codJefe);
      params.put("seguridad", seguridad);
      params.put("observacion","AutorizaciÃ³n de labor excepcional para el dia " + fecha);

      horaextra.registraAutorizacionHE(dbpool, params, trabajadores, usuario);

    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al registrar la autorizacion.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

  }

  /**
   * Metodo que carga las autorizaciones de horas extras
   * @param codJefe Codigo del Jefe
   * @return ArrayList Lista de autorizaciones
   * @throws HoraExtraException
   */
  public ArrayList cargarHorasExtras(String dbpool, String codJefe) throws
      HoraExtraException {

    ArrayList horasExtras = null;
    try {

      horasExtras = horaextra.cargarHorasExtras(dbpool, codJefe);

    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al cargar las autorizaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return horasExtras;
  }

  /**
   * Metodo encargado de modificar una autorizacion de hora extra
   * @param he Bean con los datos de la autorizacion a modificar
   * @param fechaAut Fecha de autorizacion
   * @param horaIni Hora inicio
   * @param horaFin Hora fin
   * @param horaSalida Hora de salida
   * @param obs Observacion
   * @param usuario Usuario
   * @return String Resultado de la modificacion
   * @throws HoraExtraException
   */
  public String modificarHoraExtra(BeanHoraExtra he, String fechaAut,
                                   String horaIni,
                                   String horaFin, String horaSalida,
                                   String obs, String usuario) throws
      HoraExtraException {

    String res = "";
    try {

      res = horaextra.modificarHoraExtra(he, fechaAut, horaIni, horaFin,
                                         horaSalida, obs, usuario);

    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al modificar la autorizacion.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return res;
  }

  /**
   * Metodo encargado de eliminar las autorizaciones de horas extras
   * @param params Lista de indice de autorizaciones a eliminar
   * @param lista Lista de autorizaciones
   * @return Lista de autorizaciones modificada
   * @throws HoraExtraException
   */
  public ArrayList eliminarHorasExtras(String[] params, ArrayList lista) throws
      HoraExtraException {

    try {

      lista = horaextra.eliminarHorasExtras(params, lista);

    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al eliminar las autorizaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return lista;
  }

  /**
   * Metodo encargado de buscar autorizaciones
   * @param criterio Criterio de busqueda
   * @param valor Valor del criterio
   * @param codJefe Codigo del jefe
   * @return Lista de autorizaciones
   * @throws HoraExtraException
   */
  public ArrayList buscarHorasExtras(String dbpool, String criterio,
                                     String valor, String codJefe,
                                     HashMap seguridad) throws
      HoraExtraException {

    ArrayList horas = null;
    try {

      horas = horaextra.buscarHorasExtras(dbpool, criterio, valor, codJefe,
                                          seguridad);

    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar las autorizaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return horas;
  }

  /**
   * Metodo encargado de ejecutar el proceso masivo de acumulacion de horas extras
   * @param params criterio, valor, usuario, dbpool 
   * @return String Resultado de acumulacion
   * @throws HoraExtraException
   */
  public String acumularHE(Map params) throws
      HoraExtraException {

    String res = Constantes.OK;
    try {
      params.put("observacion", "Proceso de acumulaciÃ³n de horas de labor excepcional");

      horaextra.acumularHE((HashMap)params, params.get("usuario").toString().trim());
    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al acumular las horas de labor excepcional.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return res;
  }

  /**
   * Metodo encargado de buscar los acumulados de horas extras
   * @param criterio Criterio de busqueda
   * @param valor Valor de criterio
   * @param codUO Unidad Organizacional
   * @return Lista de acumulados
   * @throws HoraExtraException
   */
  public ArrayList buscarAcumulados(String dbpool, String criterio,
                                    String valor, String codUO,
                                    HashMap seguridad) throws
      HoraExtraException {

    ArrayList listaAcumulados = null;
    try {

      listaAcumulados = horaextra.buscarAcumulados(dbpool, criterio, valor,
          codUO, seguridad);

    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al buscar los acumulados.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return listaAcumulados;
  }

  /**
   * Metodo encargado de registrar una compensacion de horas extras
   * @param codPers Registro del trabajador
   * @param codUO Unidad Organizacional
   * @param fecha Fecha de compensacion
   * @param usuario Usuario
   * @return String Resultado de la compensacion
   * @throws HoraExtraException
   */
  public String registraCompensacionHE(String dbpool, String codPers,
                                       String codUO,
                                       String fecha, String usuario) throws
      HoraExtraException {

    String res = Constantes.OK;
    try {

      res = horaextra.registraCompensacionHE(dbpool, codPers, codUO, fecha,
                                             usuario);

    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al registrar la compensacion.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return res;
  }

  /**
   * Metodo encargado del registro de la salida de horas extras
   * @param codPersonal Registro del personal
   * @param fecha Fecha de salida
   * @param horaIni Hora Inicio de autorizacion
   * @param horaSalida Hora Salida de autorizacion
   * @param fechaEfect Fecha real de salida
   * @throws HoraExtraException
   */
  public void registrarSalidaHE(String dbpool, String codPersonal, String fecha,
                                String horaIni,
                                String horaSalida, String fechaEfect) throws
      HoraExtraException {

    try {
      horaextra.registrarSalidaHE(dbpool, codPersonal,fecha,horaIni,horaSalida,fechaEfect);
    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al registrar la salida de labor excepcional.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

  }

  /**
   * Metodo encargado de cargar las autorizaciones de un trabajador
   * @param codPers Registro del trabajador
   * @return Lista de autorizaciones
   * @throws HoraExtraException
   */
  public ArrayList cargarAutorizaciones(String dbpool, String codPers) throws
      HoraExtraException {

    ArrayList autorizaciones = null;
    try {
      autorizaciones = horaextra.cargarAutorizaciones(dbpool, codPers);
    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al cargar las autorizaciones.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return autorizaciones;
  }

  /**
   * Metodo encargado de verificar si  un trabajador posee autorizacion de horas extras
   * para la fecha actual
   * @param codPersonal Registro del trabajador
   * @return Datos de la autorizacion
   * @throws HoraExtraException
   */
  public BeanHoraExtra verificaSalidaHE(String dbpool, String codPersonal) throws
      HoraExtraException {

    BeanHoraExtra salida = null;
    try {
      salida = horaextra.verificaSalidaHE(dbpool, codPersonal);
    }
    catch (IncompleteConversationalState e) {
      throw new HoraExtraException(e.getBeanMensaje());
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
            "Ha ocurrido un error al verificar la existencia de una autorizacion de la labor excepcional.");
        beanM.setMensajesol("Por favor intente nuevamente.");
      }
      throw new HoraExtraException(beanM);
    }

    return salida;
  }
  
  //ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
  /**
	 * Metodo encargado de procesar la acumulación de horas de labor excepcional de colaboradores por regimen/modalidad	 
	 * @param mapa HashMap	 
	 * @return resultado boolean
	 * @throws HoraExtraException
	 */
	public boolean procesarLaborExcepcional(HashMap mapa) throws HoraExtraException {

		boolean resultado = true;
		try {
			
			String criterio = (String)mapa.get("criterio");			
			mapa.put("observacion", "Proceso de acumulaci&oacute;n de horas extras del " + (String)mapa.get("fechaIni")
					+ " al " + (String)mapa.get("fechaFin")+".Criterio: "+criterio);

			//horaextra.procesarLaborExcepcional(mapa, (String)mapa.get("usuario"));//ICAPUNAY 31/01/2013 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES (por derivacion mal solicitudes por proceso con hilos)
			horaextra.procesarLaborExcepcional_interfaz(mapa, (String)mapa.get("usuario"));//ICAPUNAY 31/01/2013 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES (por derivacion mal solicitudes por proceso con hilos)

		} catch (IncompleteConversationalState e) {
			throw new HoraExtraException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar la acumulación de labor excepcional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new HoraExtraException(beanM);
		}
		return resultado;
	}
	//FIN ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	
	
	//ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	/**
	 * Metodo encargado de registrar temporalmente las compensaciones antiguas	
	 * @param params HashMap
	 *            Parametros de la compensacion	
	 * @return Lista de mensajes
	 * @throws HoraExtraException
	 */
	public ArrayList procesarCompensacionTemporal(HashMap params) throws HoraExtraException {

		ArrayList lista = null;
		try {

			lista = horaextra.procesarCompensacionTemporal(params);

		} catch (IncompleteConversationalState e) {
			throw new HoraExtraException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al registrar temporalmente las compensaciones antiguas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new HoraExtraException(beanM);
		}
		return lista;
	}
	//FIN ICAPUNAY 06/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064


}