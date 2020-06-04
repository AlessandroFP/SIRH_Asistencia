package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanDevolucion;
import pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.AsistenciaFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.ProcesoFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.ProcesoFacadeRemote;
import pe.gob.sunat.utils.Constantes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Title: AsistenciaDelegate
 * </p>
 * <p>
 * Description: Clase encargada de administrar las invocaciones para las
 * funcionalidades del modulo de asistencia
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author cgarratt
 * @version 1.0
 */
public class AsistenciaDelegate {

	private final Log log = LogFactory.getLog(getClass());
	private AsistenciaFacadeRemote asistencia;

	public AsistenciaDelegate() throws AsistenciaException {
		try {
			AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(
							AsistenciaFacadeHome.JNDI_NAME,
							AsistenciaFacadeHome.class);
			asistencia = facadeHome.create();
		} catch (Exception e) {
			BeanMensaje beanM = new BeanMensaje();
			beanM.setError(true);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new AsistenciaException(beanM);
		}
	}

	/**
	 * Metodo encargado de buscar marcaciones impares
	 * 
	 * @param fechaIni
	 *            String Fecha Inicio
	 * @param fechaFin
	 *            String Fecha Fin
	 * @param criterio
	 *            String Criterio
	 * @param valor
	 *            String Valor
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public ArrayList buscarMarcacionesImpares(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws AsistenciaException {

		ArrayList marcaciones = null;
		try {

			marcaciones = asistencia.buscarMarcacionesImpares(dbpool, fechaIni,
					fechaFin, criterio, valor, seguridad);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las marcaciones impares.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return marcaciones;
	}
	
	//dtarazona
	/**
	* Metodo encargado de obtener el parametro fecha de implentacion del reporte de personal que no generó saldo
	 * 
	 * @param codPers
	 *            Registro del trabajador
	 * @return Saldo
	 * @throws SolicitudException findParamByCodTabCodigo
	 */
	public String findParamByCodTabCodigo(HashMap param)
			throws SolicitudException {

		String estado;
		try {
			estado = asistencia.findParamByCodTabCodigo(param);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error en findParamByCodTabCodigo.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return estado;
	}
	//dtarazona
	
	/**
	 * Metodo encargado de buscar marcaciones impares
	 * 
	 * @param fechaIni
	 *            String Fecha Inicio
	 * @param fechaFin
	 *            String Fecha Fin
	 * @param criterio
	 *            String Criterio
	 * @param valor
	 *            String Valor
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public ArrayList buscarMarcaciones(String dbpool, String fecha,
			String codPers, HashMap seguridad) throws AsistenciaException {

		ArrayList marcaciones = null;
		try {

			marcaciones = asistencia.buscarMarcaciones(dbpool, fecha, codPers,
					seguridad);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return marcaciones;
	}

	/**
	 * Metodo encargado de buscar marcaciones con pase provisional
	 * 
	 * @param fechaIni
	 *            String Fecha Inicio
	 * @param fechaFin
	 *            String Fecha Fin
	 * @param criterio
	 *            String Criterio
	 * @param valor
	 *            String Valor
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public ArrayList buscarMarcacionesPase(String dbpool, String fechaIni, String fechaFin,
			String codPers, HashMap seguridad) throws AsistenciaException {

		ArrayList marcaciones = null;
		try {

			marcaciones = asistencia.buscarMarcacionesPase(dbpool, fechaIni, fechaFin, 
					codPers, seguridad);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return marcaciones;
	}

	/**
	 * Metodo encargado de listar las marcaciones impares
	 * 
	 * @param codPers
	 *            String Registro del Trabajador
	 * @param fecha
	 *            String fecha
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public ArrayList listarMarcacionesImpares(String dbpool, String codPers,
			String fecha) throws AsistenciaException {

		ArrayList marcaciones = null;
		try {
			marcaciones = asistencia.listarMarcacionesImpares(dbpool, codPers,
					fecha);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al listar las marcaciones impares.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return marcaciones;
	}

	/**
	 * Metodo encargado de ejecutar el proceso masivo de proceso de marcaciones
	 * 
	 * @param codReloj
	 *            String Codigo del reloj
	 * @param fechaIni
	 *            String Fecha Inicio
	 * @param fechaFin
	 *            String Fecha Fin
	 * @param path
	 *            String Ruta del archivo origen
	 * @param usuario
	 *            String Usuario
	 * @return boolean
	 * @throws AsistenciaException
	 */
	public boolean procesarMarcaciones(HashMap params) throws AsistenciaException {

		boolean res = true;
		try {

			params.put("observacion",
					"Proceso de carga de marcaciones de reloj del " + (String)params.get("fechaIni")
							+ " al " + (String)params.get("fechaFin"));

			asistencia.procesarMarcaciones(params, (String)params.get("usuario"));

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar las marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return res;
	}

	/**
	 * Metodo encargado del registro de una marcacion impar
	 * 
	 * @param codPers
	 *            String Registro del trabajador
	 * @param fecha
	 *            String Fecha
	 * @param hora
	 *            String Hora
	 * @param usuario
	 *            String Usuario
	 * @throws AsistenciaException
	 */
	public void registrarMarcacionImpar(String dbpool, String codPers,
			String fecha, String hora, String usuario)
			throws AsistenciaException {

		try {

			asistencia.registrarMarcacionImpar(dbpool, codPers, fecha, hora,
					usuario);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al registrar la marcacion impar.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

	}

	/**
	 * Metodo que ejecuta el proceso masivo de registro de asistencias
	 * 
	 * @param fechaIni
	 *            String Fecha Inicio
	 * @param fechaFin
	 *            String Fecha Fin
	 * @param usuario
	 *            String usuario
	 * @return boolean
	 * @throws AsistenciaException
	 */
	public boolean generarRegistroAsistencia(HashMap mapa)
			throws AsistenciaException {

		boolean res = true;
		try {
			
			mapa.put("observacion", "Registro de asistencia del " + (String)mapa.get("fechaIni")
					+ " al " + (String)mapa.get("fechaFin"));

			asistencia.generarRegistros(mapa, (String)mapa.get("usuario"));

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar el registro de asistencias.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return res;
	}

	/**
	 * Metodo que ejecuta el proceso de asistencia
	 * 
	 * @param periodo
	 *            String Periodo a procesar
	 * @param criterio
	 *            String Criterio
	 * @param valor
	 *            String valor
	 * @param usuario
	 *            String usuario
	 * @return String
	 * @throws AsistenciaException
	 */
	/*public String procesarAsistencia(String dbpool, String periodo,
			String criterio, String valor, String codPers, String usuario,
			HashMap seguridad, String indPap)*/ 
	public String procesarAsistencia(Map mProceso) throws AsistenciaException {

		String res = Constantes.OK;
		try {

			res = asistencia.procesarAsistencia(mProceso);
			/*res = asistencia.procesarAsistencia(dbpool, periodo, criterio,
					valor, codPers, usuario, seguridad, indPap);*/

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar la asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return res;
	}

	/**
	 * Metodo que ejecuta el proceso masivo de calificacion
	 * 
	 * @param fecha
	 *            String Fecha a calificar
	 * @param usuario
	 *            Strig Usuario
	 * @return String
	 * @throws AsistenciaException
	 */
	public String procesarCalificacion(String dbpool, String fecha,
			String codPers, String usuario, HashMap seguridad)
			throws AsistenciaException {

		String res = Constantes.OK;
		try {

			HashMap params = new HashMap();
			params.put("dbpool", dbpool);
			params.put("fecha", fecha);
			params.put("codPers", codPers);
			params.put("seguridad", seguridad);
			params.put("observacion", "Proceso de calificaciones del " + fecha);

			asistencia.procesarCalificacion(params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar la calificacion.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return res;
	}

	/**
	 * Metodo que ejecuta el proceso masivo de cierre de asistencia
	 * 
	 * @param periodo
	 *            String Periodo a cerrar
	 * @param criterio
	 *            String criterio
	 * @param valor
	 *            String valor
	 * @return String
	 * @throws AsistenciaException
	 */
	public String cerrarAsistencia(String dbpool, String periodo,
			String criterio, String valor, String codPers, String usuario,
			HashMap seguridad) throws AsistenciaException {

		String res = Constantes.OK;
		try {

			HashMap params = new HashMap();
			params.put("dbpool", dbpool);
			params.put("periodo", periodo);
			params.put("criterio", criterio);
			params.put("valor", valor);
			params.put("codPers", codPers);
			params.put("observacion", "Cierre de asistencia del periodo "
					+ periodo);
			params.put("seguridad", seguridad);
			params.put("indMail", seguridad.get("indMail").toString().trim());
			params.put("ind_estado", seguridad.get("ind_estado").toString().trim());

			asistencia.cerrarAsistencia(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al cerrar la asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return res;
	}

	/**
	 * Metodo encargado de actualizar las papeletas
	 * 
	 * @param params
	 *            String[] Indice de las papeletas a modificar
	 * @param lista
	 *            ArrayList Listado de papeletas
	 * @param usuario
	 *            String usuario
	 * @throws AsistenciaException
	 */
	public ArrayList actualizarPapeletas(String dbpool, String[] params,
			ArrayList lista, String usuario, String fecha, String supervisor, String[] observs)
			throws AsistenciaException {
		ArrayList mensajes = null;
		try {

			mensajes = asistencia.actualizarPapeletas(dbpool, params, lista,
					usuario, fecha, supervisor, observs);

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			log.error("RemoteException en actualizarPapeletas(AsistenciaDelegate): " + e.getMessage(), e);//ICR 18052015
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al actualizarr las papeletas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return mensajes;
	}

	/**
	 * Metodo encargado de calificar las papeletas
	 * 
	 * @param params
	 *            String[] Indice de las papeletas a modificar
	 * @param lista
	 *            ArrayList Listado de papeletas
	 * @param usuario
	 *            String usuario
	 * @throws AsistenciaException
	 */
	public ArrayList calificarPapeletas(String dbpool, String[] params,
			ArrayList lista, String codJefe, String usuario)
			throws AsistenciaException {
		ArrayList mensajes = null;
		try {

			mensajes = asistencia.calificarPapeletas(dbpool, params, lista,
					codJefe, usuario);

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al calificar las papeletas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return mensajes;
	}

	/**
	 * Metodo encargado de calificar las asitencias
	 * 
	 * @param params
	 *            String[] Indice de las asistencias a modificar
	 * @param lista
	 *            ArrayList Listado de asistencias
	 * @param usuario
	 *            String usuario
	 * @throws AsistenciaException
	 */
	public ArrayList calificarAsistencias(String dbpool, String[] params,
			ArrayList lista, String usuario, String fecha)
			throws AsistenciaException {

		ArrayList mensajes = null;
		try {
			mensajes = asistencia.calificarAsistencias(dbpool, params, lista,
					usuario, fecha);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al calificar la asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return mensajes;
	}

	//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
	/**
	 * Metodo encargado de calificar las asitencias
	 * 
	 * @param params
	 *            String[] Indice de las asistencias a modificar
	 * @param lista
	 *            ArrayList Listado de asistencias
	 * @param usuario
	 *            String usuario
	 * @throws AsistenciaException
	 */
	public ArrayList calificarAsistenciasCasPlanilla(String dbpool, String[] params,
			ArrayList lista, String usuario, String fecha, String regimenModalidad)
			throws AsistenciaException {

		ArrayList mensajes = null;
		try {
			mensajes = asistencia.calificarAsistenciasCasPlanilla(dbpool, params, lista,
					usuario, fecha, regimenModalidad);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al calificar la asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return mensajes;
	}
	//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII

	
	/**
	 * Metodo encargado de generar la data para el reloj
	 * 
	 * @param fecha
	 *            String Fecha a generar
	 * @return boolean
	 * @throws AsistenciaException
	 */
	public boolean generarDataReloj(String dbpool, String fecha,
			String codPers, String usuario, HashMap seguridad)
			throws AsistenciaException {

		boolean res = true;
		try {

			HashMap params = new HashMap();
			params.put("dbpool", dbpool);
			params.put("fecha", fecha);
			params.put("codPers", codPers);
			params.put("observacion", "GeneraciÃ³n de data de reloj del "
					+ fecha);
			params.put("seguridad", seguridad);

			asistencia.generarDataReloj(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar la data de reloj.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return res;
	}

	/**
	 * Metodo encargado de grabar la devolucion
	 * 
	 * @param codPers
	 *            String Registro del trabajador
	 * @param periodo
	 *            String Periodo
	 * @param mov
	 *            String Movimiento
	 * @param total
	 *            String Monto a devolver
	 * @param observacion
	 *            String Observacion
	 * @param usuario
	 *            String usuario
	 * @throws AsistenciaException
	 */
	public void grabarDevolucion(Map devol)
			throws AsistenciaException {
		try {
			asistencia.grabarDevolucion(devol);

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al grabar la devolucion.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

	}

	/**
	 * Metodo que devuelve las papeletas registradas por los trabajadores a
	 * cargo de un determinado jefe
	 * 
	 * @param codPers
	 *            String Registro del jefe
	 * @param fecha
	 *            String Fecha
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public List buscarPapeletasSubordinados(String dbpool, String codPers,
			String fechaIni, String fechaFin, HashMap seguridad)
			throws AsistenciaException {

		List papeletas = null;

		try {
			papeletas = asistencia.buscarPapeletasSubordinados(dbpool, codPers,
					fechaIni, fechaFin, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las papeletas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return papeletas;
	}
	
	/**
	 * Metodo que devuelve las papeletas generadas por los trabajadores a
	 * cargo de un determinado jefe
	 * 
	 * @param codPers
	 *            String Registro del jefe
	 * @param fecha
	 *            String Fecha
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public List buscarPapeletasGeneradas(String dbpool, String codPers, HashMap seguridad)
			throws AsistenciaException {

		List papeletas = null;

		try {
			papeletas = asistencia.buscarPapeletasGeneradas(dbpool, codPers, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las papeletas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return papeletas;
	}	

	/**
	 * Metodo encargado de buscar las asistencias
	 * 
	 * @param codPers
	 *            String Registro del trabajador
	 * @param fecha
	 *            String Fecha a consultar
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public List buscarAsistencias(String dbpool, String codPers,
			String fecha, HashMap seguridad) throws AsistenciaException {

		List lista = null;

		try {
			lista = asistencia.buscarAsistencias(dbpool, codPers, fecha,
					seguridad);

		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las asistencias.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return lista;
	}

	/**
	 * Metodo encargado de buscar las asistencias de un determinado periodo
	 * 
	 * @param periodo
	 *            String Periodo
	 * @param criterio
	 *            String Criterio
	 * @param valor
	 *            String Valor
	 * @return ArrayList
	 * @throws AsistenciaException
	 */
	public ArrayList buscarAsistenciasPeriodo(String dbpool, String periodo,
			String criterio, String valor) throws AsistenciaException {

		ArrayList lista = null;
		try {
			lista = asistencia.buscarAsistenciasPeriodo(dbpool, periodo,
					criterio, valor);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las asistencias por periodo.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return lista;
	}

	/**
	 * Metodo que devuelve los datos de una devoluciï¿½n
	 * 
	 * @param codPers
	 *            String Registro del trabajador
	 * @param periodo
	 *            String Periodo
	 * @param mov
	 *            String Movimiento
	 * @return BeanT1444Devol
	 * @throws AsistenciaException
	 */
	public BeanDevolucion buscarDevoluciones(String dbpool, String codPers,
			String periodo, String mov) throws AsistenciaException {

		BeanDevolucion bean = null;

		try {
			bean = asistencia.buscarDevoluciones(dbpool, codPers, periodo, mov);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar las devoluciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return bean;
	}

	/**
	 * Metodo encargado de procesar los pases provisionales
	 * 
	 * @param datos
	 * @throws AsistenciaException
	 */
	public void procesarPases(HashMap datos) throws AsistenciaException {

		try {
			asistencia.procesarPases(datos);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar las marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}
	}
	
	/**
	 * Metodo encargado de registrar las marcaciones manuales
	 * 
	 * @param datos
	 * @throws AsistenciaException
	 */
	public void registrarManual(HashMap datos) throws AsistenciaException {

		try {
			asistencia.registrarManual(datos);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al registrar las marcaciones manuales.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}
	}	

	/**
	 * Metodo encargado de verificar si un trabajador es responsable de un area
	 * 
	 * @throws AsistenciaException
	 */
	public boolean esJefeEncargadoDelegado(HashMap datos)
			throws AsistenciaException {

		boolean es = false;
		try {
			es = asistencia.esJefeEncargadoDelegado(datos);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar las marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}
		return es;
	}
	
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	/**
	 * Metodo encargado de verificar si un trabajador es responsable como jefe/encargado/delegado de una o varias unidades
	 * 
	 * @throws AsistenciaException
	 */
	public boolean esJefeEncargadoDelegadoSolicitudes(HashMap datos)
			throws AsistenciaException {

		boolean es = false;
		try {
			es = asistencia.esJefeEncargadoDelegadoSolicitudes(datos);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar esJefeEncargadoDelegadoSolicitudes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}
		return es;
	}
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp

	/** WERR-PAS20155E230300132
	 * Metodo encargado de verificar si un trabajador es delegado de area
	 * 
	 * @throws AsistenciaException
	 */
	public boolean esDelegadoEncargadoPapeleta(HashMap datos)
			throws AsistenciaException {

		boolean es = false;
		try {
			es = asistencia.esDelegadoEncargadoPapeleta(datos);
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al procesar las marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}
		return es;
	}
	//end WERR-PAS20155E230300132
	/**
	 * Metodo encargado de cargar los logs de los procesos masivos
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return @throws
	 *         QueueException
	 */
	public ArrayList cargarLogProcesos(String dbpool, String codPers)
			throws AsistenciaException {

		ArrayList lista = null;
		try {

			ProcesoFacadeHome facadeHome = (ProcesoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
							ProcesoFacadeHome.class);
			ProcesoFacadeRemote facadeRemote = facadeHome.create();

			lista = facadeRemote.cargarLogProcesos(dbpool, codPers);

		} catch (Exception e) {
			throw new AsistenciaException(e.getMessage());
		}

		return lista;
	}

	/**
	 * Metodo encargado de descargar el log de un proceso masivo
	 * 
	 * @param dbpool
	 * @param id
	 * @return InputStream
	 * @throws QueueException
	 */
	public InputStream descargarLogProceso(String dbpool, String id)
			throws AsistenciaException {

		InputStream archivo;
		try {

			ProcesoFacadeHome facadeHome = (ProcesoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(ProcesoFacadeHome.JNDI_NAME,
							ProcesoFacadeHome.class);
			ProcesoFacadeRemote facadeRemote = facadeHome.create();

			archivo = facadeRemote.descargarLogProceso(dbpool, id);

		} catch (Exception e) {
			throw new AsistenciaException(e.getMessage());
		}

		return archivo;
	}
	
	/**
	 * Metodo encargado de verificar si el usuario es supervisor de la UO
	 * 
	 * @param dbpool
	 * @param id
	 * @return InputStream
	 * @throws QueueException
	 */
	public boolean esSupervisor(String dbpool, String codPers, String mov, String codUO)
			throws AsistenciaException {

		boolean esSupervisor = false;
		try {

			AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,
							AsistenciaFacadeHome.class);
			AsistenciaFacadeRemote facadeRemote = facadeHome.create();

			esSupervisor = facadeRemote.esSupervisor(dbpool, codPers, mov, codUO);

		} catch (Exception e) {
			throw new AsistenciaException(e.getMessage());
		}

		return esSupervisor;
	}
	
	public HashMap buscarTrabajadorJefe(String dbpool, String codPers, HashMap seguridad)
		throws AsistenciaException {

			HashMap buscaTrabajadorJefe = new HashMap();
			try {

				AsistenciaFacadeHome facadeHome = (AsistenciaFacadeHome) ServiceLocator
						.getInstance().getRemoteHome(AsistenciaFacadeHome.JNDI_NAME,
								AsistenciaFacadeHome.class);
				AsistenciaFacadeRemote facadeRemote = facadeHome.create();

				buscaTrabajadorJefe = facadeRemote.buscarTrabajadorJefe(dbpool, codPers, seguridad);

			} catch (Exception e) {
				throw new AsistenciaException(e.getMessage());
			}

			return buscaTrabajadorJefe;
		}
	
	//JVV - 30/09/2010
	/**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList cargarRelojes(String dbpool, String estado)
            throws AsistenciaException {

        ArrayList relojes = null;
        try {
            relojes = asistencia.cargarRelojes(dbpool, estado);
        } catch (IncompleteConversationalState e) {
            throw new AsistenciaException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los relojes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new AsistenciaException(beanM);
        }

        return relojes;
    }
    
  //ICAPUNAY - FORMATIVAS 09/06/2011	
    /**
	 * Metodo encargado de calificar las asistencias para modalidades formativas
	 * 
	 * @param params
	 *            String[] Indice de las asistencias a modificar
	 * @param lista
	 *            ArrayList Listado de asistencias
	 * @param usuario
	 *            String usuario
	 * @throws AsistenciaException
	 */
	public ArrayList calificarAsistenciasFormativas(String dbpool, String[] params,
			//ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII ArrayList lista, String usuario, String fecha)
			ArrayList lista, String usuario, String fecha, String regimenModalidad) //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
			throws AsistenciaException {

		ArrayList mensajes = null;
		try {
			mensajes = asistencia.calificarAsistenciasFormativas(dbpool, params, lista,
					//ICAPUNAY 01/07/2011 usuario, fecha);
					usuario, fecha, regimenModalidad); //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII
		} catch (IncompleteConversationalState e) {
			throw new AsistenciaException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al calificar la asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new AsistenciaException(beanM);
		}

		return mensajes;
	}
	//FIN ICAPUNAY - FORMATIVAS

	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarUnidades(String dbpool, String registro)
            throws AsistenciaException {

        ArrayList unidades = null;
        try {
            unidades = asistencia.buscarUnidades(dbpool, registro);
        } catch (IncompleteConversationalState e) {
            throw new AsistenciaException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar las unidades.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new AsistenciaException(beanM);
        }

        return unidades;
    }
    
    /**
     * lista unidades de determinado nivel que no dependan de otro nivel donde sean jefe/delegado el usuario
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarUnidadesNoDependientes(String dbpool, String registro, String nivel1, String nivel2)
            throws AsistenciaException {

        ArrayList unidades = null;
        try {
            unidades = asistencia.buscarUnidadesNoDependientes(dbpool, registro,nivel1,nivel2);
        } catch (IncompleteConversationalState e) {
            throw new AsistenciaException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM.setMensajeerror("Ha ocurrido un error al cargar las unidades no dependientes.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new AsistenciaException(beanM);
        }

        return unidades;
    }
    
    /**
     * 
     * @param estado
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarSubUnidades(String dbpool, String unidadSinCeros, String unidad, String nivel)
            throws AsistenciaException {

        ArrayList unidades = null;
        try {
            unidades = asistencia.buscarSubUnidades(dbpool, unidadSinCeros,unidad,nivel);
        } catch (IncompleteConversationalState e) {
            throw new AsistenciaException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar las subunidades.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new AsistenciaException(beanM);
        }

        return unidades;
    }
    
    /**
     * 
     * @param datos Map
     * @return @throws
     *         MantenimientoException
     */
    public List findColaboradoresSinClimaLicenciaVacacionesByUOByFecha(String dbpool, Map datos)
            throws AsistenciaException {

    	List colaboradores = null;
        try {
        	colaboradores = asistencia.findColaboradoresSinClimaLicenciaVacacionesByUOByFecha(dbpool,datos);
        } catch (IncompleteConversationalState e) {
            throw new AsistenciaException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los colaboradores por unidad y fecha.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new AsistenciaException(beanM);
        }

        return colaboradores;
    }
    
    /**
     * 
     * @param datos Map
     * @return @throws
     *         MantenimientoException
     */
    public void registrarPermisoRefrigerio(String dbpool, Map datos)
            throws AsistenciaException {

    	try {
        	asistencia.registrarPermisoRefrigerio(dbpool,datos);
        } catch (IncompleteConversationalState e) {
            throw new AsistenciaException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al registrar permiso de colaboradores.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new AsistenciaException(beanM);
        }

        return;
    }
    //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
    
    //ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
    /**
     * 
     * @param nivel
     * @param registro
     * @return @throws
     *         MantenimientoException
     */
    public ArrayList buscarUnidadesByNivelByReporte(String dbpool, String nivel, String registro)
            throws AsistenciaException {

        ArrayList unidades = null;
        try {
            unidades = asistencia.buscarUnidadesByNivelByReporte(dbpool, nivel, registro);
        } catch (IncompleteConversationalState e) {
            throw new AsistenciaException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar las unidades.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new AsistenciaException(beanM);
        }

        return unidades;
    }
    //

}