package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.io.ByteArrayOutputStream; 
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeRemote;
import pe.gob.sunat.sp.asistencia.ejb.ReporteMasivoFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.ReporteMasivoFacadeRemote;

/**
 * <p>
 * Title: AsistenciaDelegate
 * </p>
 * <p>
 * Description: Clase encargada de administrar las invocaciones para las
 * funcionalidades del modulo de reportes
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
public class ReporteDelegate {

	private ReporteFacadeRemote reporte;
	
	Logger log = Logger.getLogger(this.getClass());

	public ReporteDelegate() throws ReporteException {
		try {
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			reporte = facadeHome.create();
		} catch (Exception e) {
			e.printStackTrace();
			BeanMensaje beanM = new BeanMensaje();
			beanM.setError(true);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new ReporteException(beanM);
		}
	}

	/**
	 * 
	 * @param tipo
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param quiebre
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList tipoLicencia(String dbpool, String tipo, String fechaIni,
			String fechaFin, String criterio, String valor, String quiebre,
			HashMap seguridad) throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.tipoLicencia(dbpool, tipo, fechaIni, fechaFin,
					criterio, valor, quiebre, seguridad);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de tipos de licencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * Metodo que se encarga de listar las marcaciones
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	//JVV-ini --se agrego regimen
	public ArrayList marcaciones(Map params) throws ReporteException {

		ArrayList lista = null;
		try {
			//JVV-ini --se agrego regimen
			lista = reporte.marcaciones(params);
			//JVV-fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList marcacionesImpares(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {
			lista = reporte.marcacionesImpares(dbpool, fechaIni, fechaFin,
					criterio, valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de marcaciones impares.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**PRAC-ASANCHEZ 26/08/2009
	 * 
	 * @param params
	 * @param seguridad
	 * @return @throws
	 *         ReporteException
	 */
	public List laborExcepcional(HashMap params, Map seguridad)
			throws ReporteException {

		List lista = null;
		try {
			lista = reporte.laborExcepcional(params, seguridad);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de Labor Excepcional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	
	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	//JVV-ini --se agrego regimen
	public ArrayList calificado(Map params, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {
			//JVV-ini --se agrego regimen
			lista = reporte.calificado(params, seguridad);
			//JVV-fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte calificado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	//JVV-ini --se agrego regimen
	public ArrayList turnosTrabajo(HashMap params)
			throws ReporteException {

		ArrayList lista = null;
		try {
			//JVV-ini --se agrego regimen
			lista = reporte.turnosTrabajo(params);
			//JVV-fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de turnos de trabajo.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param tipo
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param quiebre
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList acumuladoLicencia(String dbpool, String tipo,
			String fechaIni, String fechaFin, String criterio, String valor,
			String quiebre, HashMap seguridad) throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.acumuladoLicencia(dbpool, tipo, fechaIni, fechaFin,
					criterio, valor, quiebre, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte acumulado de licencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList horaExtra(String dbpool, String fechaIni, String fechaFin,
			String criterio, String valor, HashMap seguridad, String indDia, String indMin)
			throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.horaExtra(dbpool, fechaIni, fechaFin, criterio,
					valor, seguridad, indDia, indMin);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de autorizaciones de labor excepcional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param quiebre
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList compensaciones(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, String quiebre,
			HashMap seguridad) throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.compensaciones(dbpool, fechaIni, fechaFin,
					criterio, valor, quiebre, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de compensaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	//ASANCHEZZ 20100426
	/*
	public ArrayList resumenMensual(String dbpool, String periodo,
			String criterio, String valor, HashMap seguridad)
			throws ReporteException {
	*/
	public List resumenMensual(Map params)
			throws ReporteException {
	//FIN
		List lista = null;
		try {
			//ASANCHEZZ 20100426
			/*
			lista = reporte.resumenMensual(dbpool, periodo, criterio, valor,
					seguridad);
			*/
			lista = reporte.resumenMensual(params);
			//FIN
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual de descuentos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param periodo
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenDiario(Map params) throws ReporteException {
/*	public ArrayList resumenDiario(String dbpool, String periodo,
			String criterio, String valor, HashMap seguridad)
			throws ReporteException {*/

		List lista = null;
		try {

		/*	lista = reporte.resumenDiario(dbpool, periodo, criterio, valor,
					seguridad);*/
			lista = reporte.resumenDiario(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen diario de descuentos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param mayorA
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList vacacionesGozadas(String dbpool, String regimen, String fechaIni, //JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS - String regimen
			String fechaFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.vacacionesGozadas(dbpool, regimen, fechaIni, fechaFin, //JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS - regimen
					criterio, valor, mayorA, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones gozadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param anhoIni
	 * @param anhoFin
	 * @param criterio
	 * @param valor
	 * @param mayorA
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList vacacionesPendientes(String dbpool, String regimen, String anhoIni,//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS 
			String anhoFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.vacacionesPendientes(dbpool, regimen, anhoIni, anhoFin,//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
					criterio, valor, mayorA, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param anhoIni
	 * @param anhoFin
	 * @param criterio
	 * @param valor
	 * @param mayorA
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList vacacionesPendientesyProg(String dbpool, String anhoIni,
			String anhoFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.vacacionesPendientesYProg(dbpool, anhoIni, anhoFin,
					criterio, valor, mayorA, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones pendientes y programadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList vacacionesCompensadas(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.vacacionesCompensadas(dbpool, fechaIni, fechaFin,
					criterio, valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones compensadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList resumenVacacionesUOrg(String dbpool, String fechaIni,
			String fechaFin, HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.resumenVacacionesUOrg(dbpool, fechaIni, fechaFin,
					seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen de vacaciones por UO.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param mayorA
	 * @return @throws
	 *         ReporteException
	 *
	public ArrayList vacacionesGoceEfectivo(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, String mayorA,
			HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.vacacionesGoceEfectivo(dbpool, fechaIni, fechaFin,
					criterio, valor, mayorA, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
*/	
	
/* JRR - PROGRAMACION - 20/05/2010 */	
	/**
	 * Metodo que se encarga de listar las vacaciones de Goce Efectivo.
	 * @param Map parametros
	 * @return List 
	 * @throws ReporteException
	 */
	public List vacacionesGoceEfectivo(Map parametros) throws ReporteException {
		List lista = null;
		try {
			lista = reporte.vacacionesGoceEfectivo(parametros);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
/*    */	

	/**
	 * 
	 * @param criterio
	 * @param valor
	 * @param periodo
	 * @param opcion
	 * @param filtro
	 * @return @throws
	 *         ReporteException
	 */
	//JVV-ini --se agrego regimen
	public ArrayList inasistencias(String dbpool, String regimen, String criterio,
			String valor, String fechaIni, String fechaFin, String numDias,
			HashMap seguridad) throws ReporteException {

		ArrayList lista = null;
		try {
			//log.debug("============================");
			//log.debug("dentro del delegate...metodo inasistencias");
			//log.debug("============================");
        	//log.debug("sl momento de llamar al metodo inasistencias del facade");
        	//log.debug("inasistencias("+dbpool+","+ criterio+","+valor+","+fechaIni+","+fechaFin+","+numDias+","+seguridad);
        	//log.debug("============================");
			//JVV-ini --se agrego regimen
			lista = reporte.inasistencias(dbpool, regimen, criterio, valor, fechaIni,
					fechaFin, numDias, seguridad);
			//JVV-fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de inasistencias.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param periodo
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList devolucionesDescuentos(String dbpool, String periodo,
			String criterio, String valor, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.devolucionesDescuentos(dbpool, periodo, criterio,
					valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de devoluciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	//dtarazona 3er entregable
	/**
	 * 
	 * @param anhoIni
	 * @param anhoFin
	 * @param criterio
	 * @param valor
	 * @param dias
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList estadisticoAnualVacaciones(String dbpool,
			String anhoIni, String anhoFin, String criterio, String valor,
			HashMap seguridad) throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.estadisticoAnualVacaciones(dbpool, anhoIni,
					anhoFin, criterio, valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar años de vacaciones pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/** 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param dias
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList personalNoGeneroSaldoVacacional(String dbpool,
			String fechaIni, String fechaFin, String criterio, String valor,
			HashMap seguridad) throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.personalNoGeneroSaldoVacacional(dbpool, fechaIni,
					fechaFin, criterio, valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar años de vacaciones pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	//dtarazona 
	//dtarazona - Ajuste 28/05/2018 - findDetalleSaldoVacacional
		/**
		 * 
		 * @param anhoIni
		 * @param anhoFin
		 * @param criterio
		 * @param valor
		 * @param dias
		 * @return @throws
		 *         ReporteException
		 */
		public ArrayList findDetalleSaldoVacacional(String dbpool,
				String numero) throws ReporteException {

			ArrayList lista = null;
			try {

				lista = reporte.findDetalleSaldoVacacional(dbpool, numero);

			} catch (IncompleteConversationalState e) {
				throw new ReporteException(e.getBeanMensaje());
			} catch (RemoteException e) {
				BeanMensaje beanM = new BeanMensaje();
				if (e.detail.getClass().getName().equals(
						"pe.gob.sunat.sol.IncompleteConversationalState")) {
					beanM = ((IncompleteConversationalState) e.detail)
							.getBeanMensaje();
				} else {
					beanM.setError(true);
					beanM
							.setMensajeerror("Ha ocurrido un error al buscar el detalle de saldo vacacional.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new ReporteException(beanM);
			}

			return lista;
		}
	//fin dtarazona
	
	/**
	 * 
	 * @param anhoIni
	 * @param anhoFin
	 * @param criterio
	 * @param valor
	 * @param dias
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList buscarAnnosVacacionesPendientes(String dbpool,
			String anhoIni, String anhoFin, String criterio, String valor,
			String dias, HashMap seguridad) throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.buscarAnnosVacacionesPendientes(dbpool, anhoIni,
					anhoFin, criterio, valor, dias, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al buscar años de vacaciones pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/**
	 * Genera el reporte masivo de marcaciones
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoMarcaciones(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoMarcaciones(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**
	 * Genera el reporte masivo de turnos
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoTurnos(String dbpool, HashMap params, String usuario, HashMap seguridad) {

		try {
			reporte.masivoTurnos(dbpool, params, usuario, seguridad);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de turnos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}

	/**
	 * Genera el reporte masivo de inasistencias
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoInasistencias(String dbpool, HashMap params,
			String usuario) {

		try {
			//log.debug("===========================");			
			//log.debug("dentro de delegate metodo...masivoInasistencias("+dbpool+", "+params+", "+usuario);
			//log.debug("antes de llamar al facade");			
			reporte.masivoInasistencias(dbpool, params, usuario);
			//log.debug("despues de llamar al facade");
			//log.debug("===========================");
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de inasistencias.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}

	/**
	 * Genera el reporte masivo de marcaciones impares
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoImpares(String dbpool, HashMap params, String usuario) {

		try {

			reporte.masivoImpares(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de marcaciones impares.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}

	/**
	 * Genera el reporte masivo de marcaciones impares
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoCalificado(String dbpool, HashMap params, String usuario) {

		try {

			reporte.masivoCalificado(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo calificado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}

	//PRAC-ASANCHEZ 18/06/2009
	/**
	 * Genera el reporte masivo de personal sin Turno
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoPersonalSinTurno(String dbpool, HashMap params, String usuario) {

		try {

			reporte.masivoPersonalSinTurno(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de personal sin turno.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	//
	
	/**
	 * Genera el reporte masivo de resumen mensual
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoResumenMensual(String dbpool, HashMap params,
			String usuario) {

		try {

			reporte.masivoResumenMensual(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de resumen mensual.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}

	/**
	 * Genera el reporte masivo de resumen diario
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoResumenDiario(String dbpool, HashMap params,
			String usuario) {

		try {

			reporte.masivoResumenDiario(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de resumen diario.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}

	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoVacacionesPendientes(HashMap params, String usuario) {
		try {
			reporte.masivoVacacionesPendientes(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	//DTARAZONA	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoVacacionesProgramadas(Map params,String usuario) {
		try {
			reporte.masivoVacacionesProgramadas(params,usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones programadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	//FIN DTARAZONA

	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList sinTurno(String dbpool, String fechaIni,
			String fechaFin, String codUO, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.sinTurno(dbpool, fechaIni, fechaFin, codUO, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de personal sin turno.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	//PRAC-ASANCHEZ 18/06/2009
	/**
	 * 
	 * Genera el reporte del personal sin Turno
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	//JVV-ini --se agrego regimen
	public ArrayList personalSinTurno(Map params, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {			
			//JVV-ini --se agrego regimen
			lista = reporte.personalSinTurno(params, seguridad);
			//JVV-fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de personal sin turno.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	//
	
	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList marcacionesPersonal(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.marcacionesPersonal(dbpool, fechaIni, fechaFin, criterio,
					valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte calificado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList papeletas(HashMap datos, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.papeletas(datos, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de papeletas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}

	/**
	 * Metodo encargado de cargar los logs de los procesos masivos
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return @throws
	 *         QueueException
	 */
	public ArrayList cargarLogReportes(String dbpool, String codPers)
			throws ReporteException {

		ArrayList lista = null;
		try {

			ReporteMasivoFacadeHome facadeHome = (ReporteMasivoFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(ReporteMasivoFacadeHome.JNDI_NAME,
							ReporteMasivoFacadeHome.class);
			ReporteMasivoFacadeRemote facadeRemote = facadeHome.create();

			lista = facadeRemote.cargarLogReportes(dbpool, codPers);

		} catch (Exception e) {
			throw new ReporteException(e.getMessage());
		}

		return lista;
	}	

	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoVacacionesEfectivasPendientes(HashMap params, String usuario) {
		try {
			reporte.masivoVacacionesEfectivasPendientes(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones Efectivas pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoReportePorTipoLicencia(HashMap params) {
		try {
			reporte.masivoReportePorTipoLicencia(params);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones Efectivas pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	 /** 
	 * @param params
	 * @param usuario
	 */
	public void masivoPapeletas(HashMap params) {
		try {
			reporte.masivoPapeletas(params);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones Efectivas pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**masivoAcumuladoLicencia
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoAcumuladoLicencia(HashMap params) {
		try {
			reporte.masivoAcumuladoLicencia(params);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte acumulado de licencias - masivo.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoVacaciones(HashMap params, String usuario) {
		try {
			reporte.masivoVacaciones(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoVacacionesEfectuadas(HashMap params, String usuario) {
		try {
			reporte.masivoVacacionesEfectuadas(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones Efectuadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoVacacionesCompensadas(HashMap params, String usuario) {
		try {
			reporte.masivoVacacionesCompensadas(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones compensadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param tipoLic
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList licenciaMedica(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, String tipoLic,
			String dias, HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.LicenciaMedica(dbpool, fechaIni, fechaFin,
					criterio, valor, tipoLic, dias, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones gozadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoLicenciaMedica(HashMap params, String usuario) {
		try {
			reporte.masivoLicenciaMedica(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones Efectuadas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	/**
	 * Genera el reporte masivo de labor Excepcional
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoHoraExtra(String dbpool, HashMap params,
			String usuario) {

		try {

			reporte.masivoHoraExtra(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de Labor Excepcional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	//PRAC-ASANCHEZ 31/07/2009
	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList detalleDiario(String dbpool, String fechaIni,
			String fechaFin, String criterio, String valor, HashMap seguridad, String mov)
			throws ReporteException {

		ArrayList lista = null;
		try {

			lista = reporte.detalleDiario(dbpool, fechaIni, fechaFin, criterio,
					valor, seguridad, mov);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte calificado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	//

	//PRAC-ASANCHEZ 31/07/2009
	
	/**
	 * Genera el reporte masivo de detalle Diario
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoDetalleDiario(String dbpool, HashMap params, String usuario) {

		try {

			reporte.masivoDetalleDiario(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo calificado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	//
	
	
	/**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         ReporteException
	 */
	public List compensacionesBolsa(Map datos, Map seguridad)
			throws ReporteException {

		List lista = null;
		try {

			lista = reporte.compensacionesBolsa(datos, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de papeletas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/**PRAC-ASANCHEZ 28/08/2009
	 * Genera el reporte masivo de detalle Diario
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoLaborExcepcional(String dbpool, HashMap params, String usuario) {

		try {

			reporte.masivoLaborExcepcional(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo calificado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
/* ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */
	
	/**	
	 * Genera el reporte de Trabajadores notificados para Goce Vacacional 
	 * @param dbpool String
	 * @param fechaNotific String
	 * @param fechaIniGoce String
	 * @param criterio String
	 * @param valor String
	 * @param solicitud String
	 * @return lista List
	 * @throws ReporteException
	 */
	public List notificacionesXVacacionesATrabajadores(String dbpool, String regimen, String fechaNotific,
			String fechaNotificFin, String fechaIniGoce, String fechaFinGoce, String criterio, String valor,String solicitud, HashMap seguridad)
			throws ReporteException {

		List lista = null;
		try {
			
			if (log.isDebugEnabled()) log.debug("Ingreso a notificacionesXVacacionesATrabajadores-ReporteDelegate");

			lista = reporte.notificacionesXVacacionesATrabajadores(dbpool, regimen, fechaNotific, fechaNotificFin, fechaIniGoce, fechaFinGoce, criterio, valor,solicitud, seguridad);
			
			if (log.isDebugEnabled()) log.debug("lista ReporteDelegate: "+lista);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el reporte de Trabajadores notificados para Goce Vacacional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	
	/**
	 * Genera el reporte masivo de Trabajadores notificados para Goce Vacacional	
	 * @param dbpool String
	 * @param params Map
	 * @param usuario String
	 */
	public void masivoNotificacionesXVacacionesATrabajadores(String dbpool, Map params, String usuario) {

		try {
			
			if (log.isDebugEnabled()) log.debug("Ingresara a masivoNotificacionesXVacacionesATrabajadores-Delegate");			

			reporte.masivoNotificacionesXVacacionesATrabajadores(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar reporte masivo de Trabajadores notificados para Goce Vacacional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}	
	/* FIN ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */

	//JVILLACORTA - 08/04/2011 - ALERTA DE SOLICITUDES
	/**
	 * Metodo que se encarga de listar las notificaciones a directivos por solicitudes pendientes
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList notificacionesDirectivos(Map params) throws ReporteException {

		ArrayList lista = null;
		try {			
			lista = reporte.notificacionesDirectivos(params);			
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar reporte de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	} //FIN - JVILLACORTA - 08/04/2011 - ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	/**
	 * Metodo que se encarga de listar el detalle de las notificaciones a directivos por solicitudes pendientes
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList buscaDetalleNotificaDirec(Map params) throws ReporteException {

		ArrayList lista = null;
		try {			
			lista = reporte.buscaDetalleNotificaDirec(params);			
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el detalle de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	} //FIN - JVILLACORTA - 19/04/2011 - ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	/**
	 * Metodo que se encarga de listar las notificaciones a trabajadores por movimientos de asistencia 
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList notificacionesTrabajadores(Map params) throws ReporteException {

		ArrayList lista = null;
		try {			
			lista = reporte.notificacionesTrabajadores(params);			
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar reporte de notificaciones a trabajadores.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	}//FIN - JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 08/04/2011 - ALERTA DE SOLICITUDES
	/**
	 * Genera el reporte masivo de notificaciones a directivos
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoNotificacionesDirectivos(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoNotificacionesDirectivos(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	} //FIN - JVILLACORTA - 08/04/2011 - ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	/**
	 * Genera el reporte masivo de notificaciones de movimientos de asistencia a trabajadores 
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoNotificacionesTrabajadores(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoNotificacionesTrabajadores(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de notificaciones a trabajadores.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
	}//FIN - JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	
	//JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
	/**
	 * Metodo que se encarga de listar el detalle de las notificaciones a directivos por solicitudes pendientes
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList buscaDetalleNotificaTrab(Map params) throws ReporteException {

		ArrayList lista = null;
		try {			
			lista = reporte.buscaDetalleNotificaTrab(params);			
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el detalle de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	} //FIN - JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
	
	/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	/**
     * 
     * @return @throws
     *         ReporteException
     */
    public ArrayList buscarPeriodosCerradosPorRegimen(String dbpool,String tipoRegimen,String fechaInicio) throws ReporteException {

        ArrayList periodos = null;
        try {
        	periodos = reporte.buscarPeriodosCerradosPorRegimen(dbpool,tipoRegimen,fechaInicio);	
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los periodos cerrados por regimen.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return periodos;
    }  
    
    /**
     * 
     * @return @throws
     *         ReporteException
     */
    public ArrayList buscarPeriodosCerradosPorRegimenPorAnio(String dbpool,String tipoRegimen, String anio) throws ReporteException {

        ArrayList periodosXanio = null;
        try {
        	periodosXanio = reporte.buscarPeriodosCerradosPorRegimenPorAnio(dbpool,tipoRegimen,anio);	
        } catch (IncompleteConversationalState e) {
            throw new MantenimientoException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al cargar los periodos cerrados por regimen y anio.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new MantenimientoException(beanM);
        }

        return periodosXanio;
    } 
    
    /**
	 * Metodo encargado de listar el reporte de gestion mensual por unidad organica
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenMensualUUOO(Map params) throws ReporteException {

		List listaMensual = null;
		
		try {

			listaMensual = reporte.resumenMensualUUOO(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaMensual;
	}
	
	/**
	 * Genera el reporte de gestion masivo mensual por unidad organica	
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoResumenMensualUUOO(String dbpool, HashMap params,
			String usuario) {

		try {

			reporte.masivoResumenMensualUUOO(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
	}	
	
    
    /**
	 * Metodo encargado de listar el reporte de gestion mensual por colaborador
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenMensualColaborador(Map params) throws ReporteException {

		List listaMensual = null;
		
		try {

			listaMensual = reporte.resumenMensualColaborador(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por colaborador.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaMensual;
	}
	
	/**
	 * Genera el reporte de gestion masivo mensual por colaborador	
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoResumenMensualColaborador(String dbpool, HashMap params,
			String usuario) {

		try {

			reporte.masivoResumenMensualColaborador(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de resumen mensual por colaborador.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}	
    
    /**
	 * Metodo encargado de listar el reporte de gestion mensual por movimiento
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenMensualMovimiento(Map params) throws ReporteException {

		List listaMensual = null;
		
		try {

			listaMensual = reporte.resumenMensualMovimiento(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaMensual;
	}
	
	/**
	 * Genera el reporte de gestion masivo mensual por movimiento	
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoResumenMensualMovimiento(String dbpool, HashMap params,
			String usuario) {

		try {

			reporte.masivoResumenMensualMovimiento(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de resumen mensual por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}	
    
    /**
	 * Metodo encargado de listar el reporte de gestion diario por movimiento
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenDiarioMovimiento(Map params) throws ReporteException {


		List lista = null;
		
		try {

			lista = reporte.resumenDiarioMovimiento(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen diario por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/**
	 * Genera el reporte de gestion masivo diario por movimiento	
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoResumenDiarioMovimiento(String dbpool, HashMap params,
			String usuario) {

		try {

			reporte.masivoResumenDiarioMovimiento(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de resumen diario por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	
	//Metodos nuevos agregados el 14/03/2012 icapunay	
	 /**
	 * Metodo encargado de listar la cabecera del reporte de gestion mensual por unidad organica (opcion Buscar)
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenMensualUUOO_cabecera(Map params) throws ReporteException {

		List listaMensual = null;
		
		try {

			listaMensual = reporte.resumenMensualUUOO_cabecera(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaMensual;
	}	
	
	/**
	 * Metodo encargado de devolver el mapa detalle del reporte de gestion mensual por unidad organica (opcion Buscar)
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public Map resumenMensualUUOO_detalle(Map params) throws ReporteException {

		Map mapaDetalle = null;
		
		try {

			mapaDetalle = reporte.resumenMensualUUOO_detalle(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return mapaDetalle;
	}	
	
	/**
	 * Metodo encargado de listar la cabecera del reporte de gestion mensual por movimientos (opcion Buscar)
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenMensualMovimiento_cabecera(Map params) throws ReporteException {

		List listaMensual = null;
		
		try {

			listaMensual = reporte.resumenMensualMovimiento_cabecera(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaMensual;
	}
	
	/**
	 * Metodo encargado de listar el detalle del reporte de gestion mensual por movimientos (opcion Buscar)
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public Map resumenMensualMovimiento_detalle(Map params) throws ReporteException {

		Map mapaDetalle = null;
		
		try {

			mapaDetalle = reporte.resumenMensualMovimiento_detalle(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return mapaDetalle;
	}
	
	/**
	 * Metodo encargado de listar la cabecera del reporte de gestion diario por movimientos (opcion Buscar)
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public List resumenDiarioMovimiento_cabecera(Map params) throws ReporteException {

		List listaMensual = null;
		
		try {

			listaMensual = reporte.resumenDiarioMovimiento_cabecera(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen diario por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaMensual;
	}
	
	/**
	 * Metodo encargado de listar el detalle del reporte de gestion diario por movimientos (opcion Buscar)
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public Map resumenDiarioMovimiento_detalle(Map params) throws ReporteException {

		Map mapaDetalle = null;
		
		try {

			mapaDetalle = reporte.resumenDiarioMovimiento_detalle(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen diario por movimiento.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return mapaDetalle;
	}
	//fin Metodos nuevos	
	
    /* FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	
	//NVILLAR - 14/03/2012 - LABOR EXCEPCIONAL
	/**
	 * Metodo que se encarga de listar el detalle de las notificaciones a directivos por solicitudes pendientes
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList consultarSolicitudesLE(Map params) throws ReporteException {

		if (log.isDebugEnabled()) log.debug("Ingresa a consultarSolicitudesLE-Delegate");
		ArrayList lista = null;
		try {			
			lista = reporte.consultarSolicitudesLE(params);
			if (log.isDebugEnabled()) log.debug("lista ReporteDelegate: "+ lista);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el detalle de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	} //FIN - NVILLAR - 14/03/2012 - LABOR EXCEPCIONAL
	


	/** NVILLAR - 02/05/2012 - LABOR EXCEPCIONA
	 * Metodo que se encarga de listar las marcaciones
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList consultarSolicitudesLE2(Map params) throws ReporteException {

		ArrayList lista = null;
		try {
			//NVS - inicio
			lista = reporte.consultarSolicitudesLE2(params);
			//NVS - fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de Permanencias Labor Excepcional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}//FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
   
   
    //NVILLAR - 19/03/2012 - LABOR EXCEPCIONAL
	/**
	 * Metodo que se encarga de listar el detalle de las notificaciones a directivos por solicitudes pendientes
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList consultarDetalleSolicitudesLE(Map params) throws ReporteException {

		if (log.isDebugEnabled()) log.debug("Ingresa a consultarSolicitudesLE-Delegate");
		ArrayList lista = null;
		try {			
			lista = reporte.buscaDetalleConsultaSolicitudesLE(params);
			if (log.isDebugEnabled()) log.debug("lista ReporteDelegate: "+ lista);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el detalle de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	} //FIN - NVILLAR - 19/03/2012 - LABOR EXCEPCIONAL
	
	
	//NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	/**
	 * Metodo que se encarga de listar el detalle de las notificaciones a directivos por solicitudes pendientes
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList consultarDetalleSolicitudesLE2(Map params) throws ReporteException {

		if (log.isDebugEnabled()) log.debug("Ingresa a consultarSolicitudesLE-Delegate");
		ArrayList lista = null;
		try {			
			lista = reporte.buscaDetalleConsultaSolicitudesLE2(params);
			if (log.isDebugEnabled()) log.debug("lista ReporteDelegate: "+ lista);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el detalle de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	} //FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	
	
	/**INICIO - NVILLAR 28/04/2012 - LABOR EXCEPCIONAL
	 * Genera el reporte masivo de detalle Diario
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public ByteArrayOutputStream masivoLaborExcepcional1(String dbpool, HashMap params, String usuario) {
	    if(log.isDebugEnabled()){
	        log.debug(this.toString().concat(" INICIO - ")
	            .concat("masivoLaborExcepcional1 DELEGATE"));
	      }
	      ByteArrayOutputStream liquidacionPDF = null;
	      try {      
	        liquidacionPDF = reporte.masivoLaborExcepcional1(dbpool, params,usuario);
	      } catch (RemoteException e) {
	        MensajeBean beanM = new MensajeBean();
	        if ("pe.gob.sunat.framework.core.ejb.FacadeException"
	          .equals(e.detail
	            .getClass().getName())) {
	          beanM=((pe.gob.sunat.framework.core.ejb.FacadeException)
	            e.detail).getMensaje();
	        } else {
	          StringBuffer msg = new StringBuffer().append(
	          " ERROR - masivoLaborExcepcional1(String dbpool, HashMap params, String usuario) : ")
	          .append(e.toString());
	          log.error(this.toString().concat(msg.toString()));
	          beanM.setError(true);
	          beanM.setMensajeerror(msg.toString());
	          beanM.setMensajesol("Por favor intente nuevamente."
	            .concat(" Si el problema persiste comun�quese ")
	            .concat("con el WebMaster."));
	        }
	        throw new DelegateException(this, beanM);
	      } finally {      
	      }
	      if(log.isDebugEnabled()){
	        log.debug(this.toString() .concat( " FIN - ")
	            .concat(" masivoLaborExcepcional1(String dbpool, HashMap params, String usuario)"));
	      }
	      return liquidacionPDF;
	    } //FIN -NVILLAR 28/04/2012 - LABOR EXCEPCIONAL
	
	
	/**INICIO - NVILLAR 03/05/2012 - LABOR EXCEPCIONAL
	 * Genera el reporte masivo de detalle Diario
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoLaborExcepcional2(String dbpool, HashMap params, String usuario) {

		try {
			if (log.isDebugEnabled()) log.debug("Ingreso a MASIVOLABOREXCEPCIONAL = " + usuario);
			reporte.masivoLaborExcepcional2(dbpool, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo calificado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}//FIN -NVILLAR 28/04/2012 - LABOR EXCEPCIONAL
	
	
	/** NVILLAR - 22/03/2012 - LABOR EXCEPCIONA
	 * Metodo que se encarga de listar las marcaciones
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList permanencias(Map params) throws ReporteException {

		ArrayList lista = null;
		try {
			//NVS - inicio
			lista = reporte.permanencias(params);
			//NVS - fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de Permanencias Labor Excepcional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}//FIN - NVILLAR - 22/03/2012 - LABOR EXCEPCIONAL
	
	/** NVILLAR - 03/05/2012 - LABOR EXCEPCIONA
	 * Metodo que se encarga de listar las marcaciones
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList permanencias2(Map params) throws ReporteException {

		ArrayList lista = null;
		try {
			//NVS - inicio
			lista = reporte.permanencias2(params);
			//NVS - fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de Permanencias Labor Excepcional.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}//FIN - NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	
	
	/** NVILLAR - 22/03/2012 - LABOR EXCEPCIONAL 
	 * Genera el reporte masivo de marcaciones
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoConsultaPermanencia(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoConsultaPermanencia(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) { 
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}//FIN - NVILLAR - 22/03/2012 - LABOR EXCEPCIONAL
	
	/** NVILLAR - 04/05/2012 - LABOR EXCEPCIONAL *** FALTA IMPLEMENTAR LA IMPRESION ****
	 * Genera el reporte masivo de marcaciones
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoConsultaPermanencia2(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoConsultaPermanencia2(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}//FIN - NVILLAR - 04/05/2012 - LABOR EXCEPCIONAL
	
	
	//NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL
	/**
	 * Metodo que se encarga de listar el detalle de las notificaciones a directivos por solicitudes pendientes
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList buscaDetallePermanenciasLE(Map params) throws ReporteException {

		if (log.isDebugEnabled()) log.debug("Ingresa a consultarDetallePermanenciaLE-Delegate");
		ArrayList lista = null;
		try {			
			lista = reporte.buscaDetallePermanenciasLE(params);
			if (log.isDebugEnabled()) log.debug("lista ReporteDelegate: "+ lista);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el detalle de notificaciones a directivos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	} //FIN - NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL
	
	/** NVILLAR - 16/04/2012 - LABOR EXCEPCIONA
	 * Metodo que se encarga de listar las marcaciones
	 * @param Map params	 
	 * @return ArrayList
	 * @throws ReporteException
	 */
	public ArrayList consultaHorasAutNoAutComp(Map params) throws ReporteException {

		ArrayList lista = null;
		try {
			//NVS - inicio
			lista = reporte.consultaHorasAutNoAutComp(params);
			//NVS - fin
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}//FIN - NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
	
	/** NVILLAR - 24/03/2012 - LABOR EXCEPCIONAL *** FALTA IMPLEMENTAR LA IMPRESION ****
	 * Genera el reporte masivo de marcaciones
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoConsultaHorasAutNoAutComp(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoConsultaHorasAutNoAutComp(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de marcaciones.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}//FIN - NVILLAR - 24/03/2012 - LABOR EXCEPCIONAL 

	/** INICIO - NVILLAR - 19/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar el reporte de gestion mensual por unidad organica
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList resumenSolLabExcepcional(Map params) throws ReporteException {

		ArrayList listaSolicitudes = null;
		
		try {

			listaSolicitudes = reporte.resumenSolLabExcepcional(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaSolicitudes;
	}// FIN - NVILLAR - 19/04/2012 - LABOR EXCEPCIONAL
	
	
	/** INICIO - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar el reporte de gestion mensual por unidad organica
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList resumenSolLabExcepcional2(Map params) throws ReporteException {

		ArrayList listaSolicitudes = null;
		
		try {

			listaSolicitudes = reporte.resumenSolLabExcepcional2(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaSolicitudes;
	}// FIN - NVILLAR - 02/05/2012 - LABOR EXCEPCIONAL
	
	
	 /** INICIO - NVILLAR - 19/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar el reporte de gestion mensual por unidad organica
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList resumenPermanenciaExcepcional(Map params) throws ReporteException {

		ArrayList listaSolicitudes = null;
		
		try {

			listaSolicitudes = reporte.resumenPermanenciaExcepcional(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaSolicitudes;
	}// FIN - NVILLAR - 20/04/2012 - LABOR EXCEPCIONAL
	
	/** INICIO - NVILLAR - 04/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar el reporte de gestion mensual por unidad organica
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList resumenPermanenciaExcepcional2(Map params) throws ReporteException {

		ArrayList listaSolicitudes = null;
		
		try {

			listaSolicitudes = reporte.resumenPermanenciaExcepcional2(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaSolicitudes;
	}// FIN - NVILLAR - 04/05/2012 - LABOR EXCEPCIONAL
	
	
	/** INICIO - NVILLAR - 25/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar el reporte de gestion mensual por unidad organica
	 * @param params Map	
	 * @return @throws
	 *         ReporteException
	 */
	/*public ArrayList resumenHorasAutNoAutComp(Map params) throws ReporteException {

		ArrayList listaSolicitudes = null;
		
		try {

			listaSolicitudes = reporte.resumenHorasAutNoAutComp(params);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de resumen mensual por unidad organica.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return listaSolicitudes;
	}// FIN - NVILLAR - 25/04/2012 - LABOR EXCEPCIONAL*/
	
	/** NVILLAR - LABOR EXCEPCIONAL
     * Metodo encargado de buscar el nombre completo del registro indicado
     * @param codigo
     * @return @throws
     *         MantenimientoException
     */
    public String buscarNombCompleto(String dbpool, String codigo)
            throws ReporteException {

        String nombCompleto = "";
        try {
        	nombCompleto = reporte.buscarNombCompleto(dbpool, codigo);
        } catch (IncompleteConversationalState e) {
            throw new ReporteException(e.getBeanMensaje());
        } catch (RemoteException e) {
            BeanMensaje beanM = new BeanMensaje();
            if (e.detail.getClass().getName().equals(
                    "pe.gob.sunat.sol.IncompleteConversationalState")) {
                beanM = ((IncompleteConversationalState) e.detail)
                        .getBeanMensaje();
            } else {
                beanM.setError(true);
                beanM
                        .setMensajeerror("Ha ocurrido un error al buscar el nombre.");
                beanM.setMensajesol("Por favor intente nuevamente.");
            }
            throw new ReporteException(beanM);
        }
        return nombCompleto;
    }//FIN - NVILLAR - LABOR EXCEPCIONAL
    
    
    
    //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
    /**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param mayorA
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList vacacionesGozadasMatrimonio(String dbpool, String regimen, String fechaIni, 
			String fechaFin, String criterio, String valor, HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.vacacionesGozadasMatrimonio(dbpool, regimen, fechaIni, fechaFin, 
					criterio, valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de vacaciones efectuadas por licencia de matrimonio CAS.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoVacacionesGozadasMatrimonio(HashMap params, String usuario) {
		try {
			reporte.masivoVacacionesGozadasMatrimonio(params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones gozadas por licencia matrimonio CAS.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
    /**
	 * 
	 * @param fechaIni
	 * @param fechaFin
	 * @param criterio
	 * @param valor
	 * @param mayorA
	 * @return @throws
	 *         ReporteException
	 */
	public ArrayList autorizacionesClimaLaboral(String dbpool, String regimen, String fechaIni, 
			String fechaFin, String criterio, String valor, HashMap seguridad) throws ReporteException {
		ArrayList lista = null;
		try {

			lista = reporte.autorizacionesClimaLaboral(dbpool, regimen, fechaIni, fechaFin, 
					criterio, valor, seguridad);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte de autorizaciones de clima laboral.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	/**
	 * 
	 * @param params
	 * @param usuario
	 */
	public void masivoAutorizacionesClimaLaboral(HashMap params, String usuario) {
		try {
			reporte.masivoAutorizacionesClimaLaboral(params, usuario); 
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de autorizaciones de clima laboral.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	}
	//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	/**	
	 * Genera el reporte de directivos notificados que no programaron vacaciones de trabajadores 
	 * @param dbpool String
	 * @param fechaNotific String
	 * @param fechaNotificFin String	 
	 * @param criterio String
	 * @param valor String	
	 * @return lista List
	 * @throws ReporteException
	 */
	public ArrayList notificaDirectivosNoVacaciones(String dbpool, String fechaNotific,
			String fechaNotificFin,String criterio, String valor, HashMap seguridad)
			throws ReporteException {

		ArrayList lista = null;
		try {
			
			if (log.isDebugEnabled()) log.debug("Ingreso a notificaDirectivosNoVacaciones-ReporteDelegate");

			lista = reporte.notificaDirectivosNoVacaciones(dbpool, fechaNotific, fechaNotificFin, criterio, valor, seguridad);
			
			if (log.isDebugEnabled()) log.debug("lista ReporteDelegate: "+lista);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el reporte de directivos notificados que no programaron vacaciones de trabajadores.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

		return lista;
	}
	
	
	/**
	 * Genera el reporte masivo de directivos notificados que no programaron vacaciones de trabajadores
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoNotificaDirectivosNoVacaciones(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoNotificaDirectivosNoVacaciones(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de directivos notificados que no programaron vacaciones de trabajadores.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	} 
	
	/**
	 * Genera el reporte masivo de vacaciones truncas
	 * 
	 * @param dbpool
	 * @param params
	 * @param usuario
	 */
	public void masivoVacacionesTruncas(String dbpool, HashMap params, String usuario) {

		try {
			reporte.masivoVacacionesTruncas(dbpool, params, usuario);
		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM
						.setMensajeerror("Ha ocurrido un error al generar reporte masivo de vacaciones truncas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}

	} 
	/**	
	 * Genera el reporte de vacaciones truncas 
	 * @param dbpool String
	 * @param regimen String
	 * @param fechaCorte String	
	 * @param estado String 
	 * @param criterio String
	 * @param valor String	
	 * @return lista List
	 * @throws ReporteException
	 */
	public ArrayList vacacionesTruncas(String dbpool, HashMap params)
			throws ReporteException {

		ArrayList lista = null;
		try {			
			if (log.isDebugEnabled()) log.debug("Ingreso a vacacionesTruncas-ReporteDelegate");

			lista = reporte.vacacionesTruncas(dbpool,params);
			
			if (log.isDebugEnabled()) log.debug("lista ReporteDelegate: "+lista);

		} catch (IncompleteConversationalState e) {
			throw new ReporteException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals(
					"pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail)
						.getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al generar el reporte de vacaciones truncas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new ReporteException(beanM);
		}
		return lista;
	}
	
	//FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	
	
	
}