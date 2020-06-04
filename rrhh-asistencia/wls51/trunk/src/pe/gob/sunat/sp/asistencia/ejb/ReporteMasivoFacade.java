package pe.gob.sunat.sp.asistencia.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import pe.gob.sunat.batch.dao.QueueDAO;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.sol.BeanMensaje;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.sol.IncompleteConversationalState;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.sp.asistencia.bean.BeanReporte;
import pe.gob.sunat.sp.asistencia.bean.BeanVacacion;
import pe.gob.sunat.sp.asistencia.dao.T1481DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.rrhh.asistencia.dao.T4636DAO;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.rrhh.dao.T12DAO;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeHome;
import pe.gob.sunat.rrhh.programacion.ejb.ProgramacionFacadeRemote;


/**
 * 
 * @ejb.bean name="ReporteMasivoFacadeEJB" description="ReporteMasivoFacade"
 *           jndi-name="ejb/rrhh/facade/sp/asistencia/ReporteMasivoFacadeEJB"
 *           type="Stateless" view-type="remote"
 * 
 * @ejb.home remote-class="pe.gob.sunat.sp.asistencia.ejb.ReporteMasivoFacadeHome"
 *           extends="javax.ejb.EJBHome"
 * 
 * @ejb.interface remote-class="pe.gob.sunat.sp.asistencia.ejb.ReporteMasivoFacadeRemote"
 *                extends="javax.ejb.EJBObject"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @jboss.container-configuration name="Standard Stateless SessionBean"
 * @jboss.version="3.2" 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @weblogic.cache max-beans-in-cache="100"
 * @weblogic.pool max-beans-in-free-pool="100" initial-beans-in-free-pool="0"
 * 
 * @version 1.0
 */
public class ReporteMasivoFacade extends QueueDAO implements SessionBean{

	private SessionContext sessionContext;
	
	//JRR - 07/05/2010
	ServiceLocator sl = ServiceLocator.getInstance();

	public void ejbCreate() {		
	}

	public void ejbRemove() {
	}

	public void ejbActivate() {
	}

	public void ejbPassivate() {
	}

	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void marcaciones(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "MARCACIONES", params, usuario);
			/*
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String horaMarca = (String) params.get("horaMarca");
			HashMap seguridad = (HashMap) params.get("seguridad");*/

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.marcaciones(params);
			//ArrayList reporte = reporte = facadeRemote.marcaciones(params);
			//dbpool, fechaIni, fechaFin, criterio, valor, horaMarca, seguridad
			String unidad = "";

			if (reporte != null) {
				this.escribe("", "MARCACIONES", params, usuario);
				linea = Utiles.formateaCadena("Trabajador", 30, true)
						+ Utiles.formateaCadena("Fecha", 30, true)
						+ Utiles.formateaCadena("Marcaciones", 30, true)
						+ Utiles.formateaCadena("Reloj", 30, true);
				this.escribe(linea, "MARCACIONES", params, usuario);
				this.escribe("", "MARCACIONES", params, usuario);

				//BeanReporte quiebre = null;
				Map quiebre = null;
				List detalles = null;
				//ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {
					//quiebre = (BeanReporte) reporte.get(i);
					quiebre = (HashMap) reporte.get(i);
					detalles = (ArrayList)quiebre.get("detalle");
					//detalles = quiebre.getDetalle();

					//Quiebre por unidad...
					//if (!unidad.trim().equals(quiebre.getUnidad().trim())) {
						//unidad = quiebre.getUnidad().trim();
					if (!unidad.trim().equals(quiebre.get("unidad"))) {
						unidad = (String) quiebre.get("unidad");
						if (i != 0) {
							this.escribe("", "MARCACIONES", params, usuario);
						}
						linea = Utiles.formateaCadena("", 2, false) + unidad;
						this.escribe(linea, "MARCACIONES", params, usuario);
						this.escribe("", "MARCACIONES", params, usuario);
					}

					linea = Utiles.formateaCadena("", 5, false) + quiebre.get("nombre");
					this.escribe(linea, "MARCACIONES", params, usuario);
					if (detalles != null && detalles.size() > 0) {
						String fechaAux = "";
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);
							if (!detalle.getFecha().equals(fechaAux)) {
								j--;
								linea = Utiles.formateaCadena("", 30, false)
										+ Utiles.formateaCadena(detalle.getNombre(), 30, false); //detalle.getFecha() + " " + dia
								this.escribe(linea, "MARCACIONES", params, usuario);
							} else {
								linea = Utiles.formateaCadena("", 60, false)
										+ Utiles.formateaCadena(detalle.getHora().trim(), 30, true)
										+ Utiles.formateaCadena(detalle.getDescripcion().trim(), 30, true);
								this.escribe(linea, "MARCACIONES", params, usuario);
							}
							fechaAux = detalle.getFecha();
						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El trabajador no posee marcaciones";
						this.escribe(linea, "MARCACIONES", params, usuario);
					}
					this.escribe("", "MARCACIONES", params, usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "MARCACIONES", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void turnosTrabajo(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {
			super.creaReporte(id, "TURNOS DE TRABAJO", params, usuario);
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.turnosTrabajo(params);
			
			String unidad = "";

			if (reporte != null) {
				this.escribe("", "TURNOS DE TRABAJO", params, usuario);
				linea = Utiles.formateaCadena("Trabajador", 30, true)
						+ Utiles.formateaCadena("Fecha Inicio", 30, true)
						+ Utiles.formateaCadena("Fecha Fin", 30, true)
						+ Utiles.formateaCadena("Horario", 30, true)
						+ Utiles.formateaCadena("Turno", 30, true);
				this.escribe(linea, "TURNOS DE TRABAJO", params, usuario);
				this.escribe("", "TURNOS DE TRABAJO", params, usuario);				
				//Map quiebre = null;
				//List detalles = null;
				BeanReporte quiebre = null;
				ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);
					//quiebre = (HashMap) reporte.get(i);
					//detalles = (ArrayList)quiebre.get("detalle");
					detalles = quiebre.getDetalle();

					//Quiebre por unidad...
					if (!unidad.trim().equals(quiebre.getUnidad().trim())) {
						unidad = quiebre.getUnidad().trim();
						//if (!unidad.trim().equals(quiebre.get("unidad"))) {
						//unidad = (String) quiebre.get("unidad");
						if (i != 0) {
							this.escribe("", "TURNOS DE TRABAJO", params, usuario);
						}
						linea = Utiles.formateaCadena("", 2, false) + unidad;
						this.escribe(linea, "TURNOS DE TRABAJO", params, usuario);
						this.escribe("", "TURNOS DE TRABAJO", params, usuario);
					}

					linea = Utiles.formateaCadena("", 5, false) + quiebre.getNombre();
					this.escribe(linea, "TURNOS DE TRABAJO", params, usuario);
					if (detalles != null && detalles.size() > 0) {						
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);								
								linea = Utiles.formateaCadena("", 30, false)										
										+ Utiles.formateaCadena(detalle.getFechaInicio().toString().trim(), 30, true)
										+ Utiles.formateaCadena(detalle.getFechaFin().toString().trim(), 30, true)
										+ Utiles.formateaCadena(detalle.getHora().trim(), 30, true)
										+ Utiles.formateaCadena(detalle.getDescripcion().trim(), 30, true);
								this.escribe(linea, "TURNOS DE TRABAJO", params, usuario);							
						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El trabajador no posee turnos de trabajo";
						this.escribe(linea, "TURNOS DE TRABAJO", params, usuario);
					}
					this.escribe("", "TURNOS DE TRABAJO", params, usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "TURNOS DE TRABAJO", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void marcacionesImpares(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "MARCACIONES IMPARES", params, usuario);

			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.marcacionesImpares(dbpool, fechaIni, fechaFin, criterio, valor, seguridad);

			String unidad = "";
			
			//JRR
/*			String trabajador = "";
			String fechaAux = ""; */

			if (reporte != null) {
				this.escribe("", "MARCACIONES IMPARES", params, usuario);
				this.escribe("", "MARCACIONES IMPARES", params, usuario);
				
				linea = Utiles.formateaCadena("UUOO", 8, true)+"|"
				+ Utiles.formateaCadena("Trabajador", 70, true)+"|"
				+ Utiles.formateaCadena("Fecha", 26, true)+"|"
				+ Utiles.formateaCadena("Marcaciones", 20, true)+"|"
				+ Utiles.formateaCadena("Reloj", 30, true);
				
/*				linea = Utiles.formateaCadena("Trabajador", 30, true)
						+ Utiles.formateaCadena("Fecha", 30, true)
						+ Utiles.formateaCadena("Marcaciones", 30, true)
						+ Utiles.formateaCadena("Reloj", 30, true);   */
				this.escribe(linea, "MARCACIONES IMPARES", params, usuario);
				this.escribe("", "MARCACIONES IMPARES", params, usuario);
				
				BeanReporte quiebre = null;
				ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {

					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getDetalle();

					//JRR 03/08/2009 - YA NO VA: Quiebre por unidad...
					if (!unidad.trim().equals(quiebre.getUnidad().trim())) //{
						unidad = quiebre.getUnidad().trim();
/*						if (i != 0) {
							this.escribe("", "CALIFICACIONES", params, usuario);
						}
						linea = Utiles.formateaCadena("", 2, false) + unidad;
						this.escribe(linea, "CALIFICACIONES", params, usuario);
						this.escribe("", "CALIFICACIONES", params, usuario);
					}

					linea = Utiles.formateaCadena("", 5, false)
							+ quiebre.getNombre().trim();
					this.escribe(linea, "MARCACIONES IMPARES", params, usuario);

					*/
					
					if (detalles != null && detalles.size() > 0) {
//						trabajador = "";
//						fechaAux = "";
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);
							
//							if (!quiebre.getNombre().equals(trabajador)) {
								linea = Utiles.formateaCadena(unidad.substring(0, 6), 8, true) +"|"+								
										Utiles.formateaCadena(quiebre.getNombre().substring(0, quiebre.getNombre().indexOf("(")-1), 70, false) +"|"+
							 			Utiles.formateaCadena(detalle.getNombre(), 26, false) +"|"+
							 			Utiles.formateaCadena(detalle.getHora().trim(), 20, true)+"|"+
							 			Utiles.formateaCadena(detalle.getDescripcion().trim(), 30, true);
/*							} else {
								linea = Utiles.formateaCadena("", 8, false) + "|"+ 
										Utiles.formateaCadena("", 70, false) + "|"+
										(!detalle.getFecha().equals(fechaAux)? 
												Utiles.formateaCadena(detalle.getNombre(), 26, false):
												Utiles.formateaCadena("", 26, false))
							 			+"|"+
							 			Utiles.formateaCadena(detalle.getHora().trim(), 20, true) +"|"+
							 			Utiles.formateaCadena(detalle.getDescripcion().trim(), 30, true);
							}
							
							fechaAux = detalle.getFecha();
*/							
							this.escribe(linea, "MARCACIONES IMPARES",
									params, usuario);
							
//							trabajador = quiebre.getNombre();
						}	
						
							
							/*if (!detalle.getFecha().equals(fechaAux)) {
								j--;
								linea = Utiles.formateaCadena("", 30, false)
										+ Utiles.formateaCadena(detalle
												.getNombre(), 30, false);
								this.escribe(linea, "MARCACIONES IMPARES",
										params, usuario);
							} else {
								linea = Utiles.formateaCadena("", 60, false)
										+ Utiles.formateaCadena(detalle
												.getHora().trim(), 30, true)
										+ Utiles.formateaCadena(detalle
												.getDescripcion().trim(), 30,
												true);
								this.escribe(linea, "MARCACIONES IMPARES",
										params, usuario);
							}
							fechaAux = detalle.getFecha();
						}*/
				
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El trabajador no posee marcaciones impares";
						this.escribe(linea, "MARCACIONES IMPARES", params,
								usuario);
					}
					//this.escribe("", "MARCACIONES IMPARES", params, usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "MARCACIONES IMPARES",
					params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	//PRAC-ASANCHEZ 18/06/2009
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void personalSinTurno(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "PERSONAL SIN TURNO", params, usuario);
			/*
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");*/
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.personalSinTurno(params, seguridad);
			
			String unidad = "";

			if (reporte != null) {
				this.escribe("", "PERSONAL SIN TURNO", params, usuario);
				linea = Utiles.formateaCadena("Unidad Orgánica", 50, true)+"|"
						+ Utiles.formateaCadena("Registro", 10, true)+"|"
						+ Utiles.formateaCadena("Trabajador", 40, true)+"|"
						+ Utiles.formateaCadena("Categoría", 30, true)+"|"
						+ Utiles.formateaCadena("Mes", 10, true)+"|"
						+ Utiles.formateaCadena("Nro. Días", 10, true)+"|"
						+ Utiles.formateaCadena("Días Sin Turno", 50, true);
				this.escribe(linea, "PERSONAL SIN TURNO", params, usuario);
				this.escribe("", "PERSONAL SIN TURNO", params, usuario);

				for (int i = 0; i < reporte.size(); i++) {

					HashMap datos=(HashMap)reporte.get(i);
					BeanReporte quiebre = (BeanReporte)datos.get("traba");			
						
					ArrayList periodo=(ArrayList)datos.get("periodo");
					for(int j=0;j<periodo.size();j++){	
						linea = Utiles.formateaCadena(""+quiebre.getUnidad(), 50, true)+"|"
						+ Utiles.formateaCadena(""+quiebre.getCodigo(), 10, true)+"|"
						+ Utiles.formateaCadena(""+quiebre.getNombre().split("-")[2], 40, true)+"|"
						+ Utiles.formateaCadena(""+(quiebre.getNombre().split("-")[3]).split("/")[0], 30, true)+"|"
						+ Utiles.formateaCadena(""+periodo.get(j).toString().split("/")[0], 10, true)+"|"
						+ Utiles.formateaCadena(""+periodo.get(j).toString().split("/")[1], 10, true)+"|"
						+ Utiles.formateaCadena(""+(periodo.get(j).toString().split("/")[2]).replaceAll("-", ","), 50,true);
						this.escribe(linea, "PERSONAL SIN TURNO",params, usuario);
					}
					
					this.escribe("", "PERSONAL SIN TURNO", params, usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "PERSONAL SIN TURNO",
					params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}	
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void calificado(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "REGISTRO DE ASISTENCIA", params, usuario);	//jquispecoi 05/2014
			/*
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String mov = (String) params.get("mov");*/
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.calificado(params, seguridad);

			String unidad = "";

			if (reporte != null) {

				this.escribe("", "CALIFICACIONES", params, usuario);
				this.escribe("", "CALIFICACIONES", params, usuario);
				linea = Utiles.formateaCadena("UUOO", 8, true)+"|"
						+ Utiles.formateaCadena("Trabajador", 70, true)+"|"
						+ Utiles.formateaCadena("Fecha", 26, true)+"|"
						+ Utiles.formateaCadena("Movimientos", 20, true)+"|"
						+ Utiles.formateaCadena("Descripcion", 30, true);
				this.escribe(linea, "CALIFICACIONES", params, usuario);
				this.escribe("", "CALIFICACIONES", params, usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {

					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getDetalle();

					//JRR 06/05/2009 - YA NO VA: Quiebre por unidad...
					if (!unidad.trim().equals(quiebre.getUnidad().trim()))
						unidad = quiebre.getUnidad().trim();

					if (detalles != null && detalles.size() > 0) {
//						String trabajador = "";
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);
//							if (!quiebre.getNombre().equals(trabajador)) {
//								j--;
								/*linea = unidad +"|"+								
							 			quiebre.getNombre().trim() +"|"+*/
								linea = Utiles.formateaCadena(unidad.substring(0, 6), 8, true) +"|"+								
										Utiles.formateaCadena(quiebre.getNombre().substring(0, quiebre.getNombre().indexOf("(")-1), 70, false) +"|"+
							 			Utiles.formateaCadena(detalle.getNombre(), 26, false) +"|"+
							 			Utiles.formateaCadena(detalle.getHora().trim(), 20, true)+"|"+
							 			Utiles.formateaCadena(detalle.getDescripcion().trim(), 30, true);
/*							} else {
								linea = Utiles.formateaCadena("", 8, false) + "|"+ 
										Utiles.formateaCadena("", 70, false) + "|"+
							 			Utiles.formateaCadena(detalle.getNombre(), 26, false) +"|"+
							 			Utiles.formateaCadena(detalle.getHora().trim(), 20, true) +"|"+
							 			Utiles.formateaCadena(detalle.getDescripcion().trim(), 30, true);
							}*/
								
							this.escribe(linea, "CALIFICACIONES", params, usuario);
//							trabajador = quiebre.getNombre();
						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El trabajador no posee calificaciones";
						this.escribe(linea, "CALIFICACIONES", params, usuario);
					}
//					this.escribe("", "CALIFICACIONES", params, usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "CALIFICACIONES", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	
	/** 
	 * Metodo encargado de obtener el reporte de Resumen Mensual
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */	
	public void resumenMensual(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "RESUMEN MENSUAL DE ASISTENCIA", params,
					usuario);

/*			String periodo = (String) params.get("periodo");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			HashMap seguridad = (HashMap) params.get("seguridad");*/

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			//ASANCHEZZ 2010426
			/*
			ArrayList reporte = reporte = facadeRemote.resumenMensual(dbpool,
					periodo, criterio, valor, seguridad);
			*/
			List reporte = facadeRemote.resumenMensual(params);
			//FIN

			String unidad = "";

			if (reporte != null) {
/*				this.escribe("", "RESUMEN MENSUAL DE ASISTENCIA", params,
						usuario);
				this.escribe("", "RESUMEN MENSUAL DE ASISTENCIA", params,
						usuario);
*/				
				linea = 
						Utiles.formateaCadena("Trabajador", 67, true)+"|"
						+ Utiles.formateaCadena("Inasistencia", 14, true)+"|"
						+ Utiles.formateaCadena("Tardanza", 14, true)+"|"
						+ Utiles.formateaCadena("Permiso", 14, true)+"|"
						+ Utiles.formateaCadena("Licencia", 14, true)+"|"
						+ Utiles.formateaCadena("Refrigerio", 14, true)+"|"
						+ Utiles.formateaCadena("Salida", 14, true)+"|"
						+ Utiles.formateaCadena("Suspension", 14, true)+"|"
						+ Utiles.formateaCadena("Total", 14, true)+"|"
						+ Utiles.formateaCadena("UUOO", 8, true);
				this.escribe(linea, "RESUMEN MENSUAL DE ASISTENCIA", params,
						usuario);
				this.escribe("", "RESUMEN MENSUAL DE ASISTENCIA", params,
						usuario);

				BeanReporte quiebre = null;
				HashMap detalles = null;
				for (int i = 0; i < reporte.size(); i++) {

/*					this.escribe("", "RESUMEN MENSUAL DE ASISTENCIA", params,
							usuario);*/
					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getMap();

					//Quiebre por unidad...
					if (!unidad.trim().equals(quiebre.getUnidad().trim())) //{
						unidad = quiebre.getUnidad().trim();
/*						if (i == 0) {
							this.escribe("", "CALIFICACIONES", params, usuario);
						}
						linea = Utiles.formateaCadena("", 2, false) + unidad;
						this.escribe(linea, "CALIFICACIONES", params, usuario);
						this.escribe("", "CALIFICACIONES", params, usuario);
					}*/

					linea = Utiles.formateaCadena("", 5, false)
							+ quiebre.getNombre().trim();
					//this.escribe(linea, "RESUMEN MENSUAL DE ASISTENCIA", params, usuario);
/*					this.escribe("", "RESUMEN MENSUAL DE ASISTENCIA", params,
							usuario);*/ 
					if (!detalles.isEmpty()) {

						linea = 
								Utiles.formateaCadena(quiebre.getNombre().trim(), 67, false)+"|"
//							    + Utiles.formateaCadena("", 22, false)
								+ Utiles.formateaCadena((String) detalles
										.get("Inasistencia"), 14, true) +"|"
								+ Utiles.formateaCadena((String) detalles
										.get("Tardanza"), 14, true)+"|"
								+ Utiles.formateaCadena((String) detalles
										.get("Permiso"), 14, true)+"|"
								+ Utiles.formateaCadena((String) detalles
										.get("Licencia"), 14, true)+"|"
								+ Utiles.formateaCadena((String) detalles
										.get("Refrigerio"), 14, true)+"|"
								+ Utiles.formateaCadena((String) detalles
										.get("Salida"), 14, true)+"|"
								+ Utiles.formateaCadena((String) detalles
										.get("Suspension"), 14, true)+"|"
								+ Utiles.formateaCadena((String) detalles
										.get("Total"), 14, true)+"|"
								+ Utiles.formateaCadena(unidad, 8, true);
						
						this.escribe(linea, "RESUMEN MENSUAL DE ASISTENCIA",
								params, usuario);
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El trabajador no posee detalle de asistencias";
						this.escribe(linea, "RESUMEN MENSUAL DE ASISTENCIA",
								params, usuario);
					}
/*					this.escribe("", "RESUMEN MENSUAL DE ASISTENCIA", params,
							usuario);*/ 
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(),
					"RESUMEN MENSUAL DE ASISTENCIA", params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	
	/** 
	 * Metodo encargado de obtener el reporte de Resumen Diario
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */	
	public void resumenDiario(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "RESUMEN DIARIO DE ASISTENCIA", params,
					usuario);

			String periodo = (String) params.get("periodo");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
/*			ArrayList reporte = reporte = facadeRemote.resumenDiario(dbpool,
					periodo, criterio, valor, seguridad);*/
			
			List reporte = facadeRemote.resumenDiario(params);

			String unidad = "";

			if (reporte != null) {

				this.escribe("", "RESUMEN DIARIO DE ASISTENCIA", params,
						usuario);
				this.escribe("", "RESUMEN DIARIO DE ASISTENCIA", params,
						usuario);
				this.escribe("", "RESUMEN DIARIO DE ASISTENCIA", params,
						usuario);

				linea = Utiles.formateaCadena("Trabajador", 10, true);

//				String fIni = Utiles.toYYYYMMDD(fechaIni);
//				String fFin = Utiles.toYYYYMMDD(fechaFin);
				String fIni = fechaIni;
				String fFin = fechaFin;

				String fAct = fIni;
				String fReal = fechaIni;
	        	fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;

				while (fAct.compareTo(fFin) <= 0) {
					linea += Utiles.formateaCadena(fReal.substring(0, 2), 4,
							true);
					if (fReal.indexOf("/")>3){ 
				  		fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
					}
					fReal = Utiles.dameFechaSiguiente(fReal, 1);
					fAct = Utiles.toYYYYMMDD(fReal);
				}

				linea += Utiles.formateaCadena("Frec.", 10, true);
				linea += Utiles.formateaCadena("Total", 10, true);

				this.escribe(linea, "RESUMEN DIARIO DE ASISTENCIA", params,
						usuario);
				this.escribe("", "RESUMEN DIARIO DE ASISTENCIA", params,
						usuario);

				BeanReporte quiebre = null;
				HashMap detalles = null;
				for (int i = 0; i < reporte.size(); i++) {

					this.escribe("", "RESUMEN DIARIO DE ASISTENCIA", params,
							usuario);
					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getMap();

					//Quiebre por unidad...
					if (!unidad.trim().equals(quiebre.getUnidad().trim())) {
						unidad = quiebre.getUnidad().trim();
						if (i != 0) {
							this.escribe("", "CALIFICACIONES", params, usuario);
						}
						linea = Utiles.formateaCadena("", 2, false) + unidad;
						this.escribe(linea, "CALIFICACIONES", params, usuario);
						this.escribe("", "CALIFICACIONES", params, usuario);
					}

					linea = Utiles.formateaCadena("", 5, false)
							+ quiebre.getNombre().trim();
					this.escribe(linea, "RESUMEN DIARIO DE ASISTENCIA", params,
							usuario);
					this.escribe("", "RESUMEN DIARIO DE ASISTENCIA", params,
							usuario);
					if (!detalles.isEmpty()) {

						linea = Utiles.formateaCadena("", 10, false);

//						fIni = Utiles.toYYYYMMDD(fechaIni);
//						fFin = Utiles.toYYYYMMDD(fechaFin);
						fIni = fechaIni;
						fFin = fechaFin;

						fAct = fIni;
						fReal = fechaIni;
						fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
						
						while (fAct.compareTo(fFin) <= 0) {
							String texto = detalles.get(fReal) != null ? (String) detalles
									.get(fReal)
									: "0";
							linea += Utiles.formateaCadena(texto, 4, true);
							if (fReal.indexOf("/")>3){ 
						  		fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
							}
							fReal = Utiles.dameFechaSiguiente(fReal, 1);
							fAct = Utiles.toYYYYMMDD(fReal);
						}

						linea += Utiles.formateaCadena(detalles
								.get("frecuencia") != null ? (String) detalles
								.get("frecuencia") : "0", 10, true);
						linea += Utiles
								.formateaCadena(
										detalles.get("total") != null ? (String) detalles
												.get("total")
												: "0", 10, true);

						this.escribe(linea, "RESUMEN DIARIO DE ASISTENCIA",
								params, usuario);
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "No se encontraron registros";
						this.escribe(linea, "RESUMEN DIARIO DE ASISTENCIA",
								params, usuario);
					}
					this.escribe("", "RESUMEN DIARIO DE ASISTENCIA", params,
							usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(),
					"RESUMEN DIARIO DE ASISTENCIA", params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void inasistencias(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "INASISTENCIAS", params, usuario);
			String regimen = (String) params.get("regimen");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String numDias = (String) params.get("numDias");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			HashMap seguridad = (HashMap) params.get("seguridad");
			//String codPers = (String) params.get("codPers");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			ArrayList reporte = facadeRemote.inasistencias(dbpool, regimen, criterio, valor, fechaIni, fechaFin, numDias, seguridad);
			
			if (reporte != null) {
//jrr				this.escribe("", "INASISTENCIAS", params, usuario);
				linea = Utiles.formateaCadena("UUOO", 8, true)+"|"
						+ Utiles.formateaCadena("No Reg", 10, true)+"|"
						+ Utiles.formateaCadena("Trabajador", 60, true)+"|"
						+ Utiles.formateaCadena("Total Dï¿½as", 12, true)+"|"
						+ Utiles.formateaCadena("Fechas de Inasistencia", 30, true);

				this.escribe(linea, "INASISTENCIAS", params, usuario);
				this.escribe("", "INASISTENCIAS", params, usuario);

				HashMap quiebre = null;
				HashMap repor = null;
				ArrayList detalles = null;
//			    String registro ="----";
				
				for (int i = 0; i < reporte.size(); i++) {

					quiebre = (HashMap) reporte.get(i);
					detalles = (ArrayList)quiebre.get("listaDetalle");
/*jrr					linea = quiebre.get("cod_uorgan").toString().trim() + " - "
							+ quiebre.get("desc_uorgan").toString().trim();
					this.escribe(linea, "INASISTENCIAS", params, usuario);
					this.escribe("", "INASISTENCIAS", params, usuario);  */

					if (detalles != null && detalles.size() > 0) {						
						for (int j = 0; j < detalles.size(); j++) {
							repor = (HashMap) detalles.get(j);							
/*							if (!registro.equals(repor.get("t02cod_pers")+"")){
								registro = repor.get("t02cod_pers")+"";
*/							
								linea = Utiles.formateaCadena(repor.get("t02cod_uorg").toString().trim(), 8, true)+"|" 
									+ Utiles.formateaCadena(repor.get("t02cod_pers").toString().trim(), 10, true)+"|"
									+ Utiles.formateaCadena(repor.get("t02ap_pate").toString().trim()+" "+
											repor.get("t02ap_mate").toString().trim()+
											", "+repor.get("t02nombres").toString().trim(), 60, false)+"|"
/*									+ Utiles.formateaCadena(repor.get("categoria").toString(), 40, false)
									+ Utiles.formateaCadena(repor.get("totales").toString(), 10, true); */
									+ Utiles.formateaCadena(repor.get("totales").toString(), 12, true)+"|"
									+ Utiles.formateaCadena(repor.get("fecha_desc").toString(), 30, true);
/*							} else {
								linea = Utiles.formateaCadena("", 8, true)+"|" 
								+ Utiles.formateaCadena("", 10, true)+"|"
								+ Utiles.formateaCadena("", 60, false)+"|"
								+ Utiles.formateaCadena("", 12, true)+"|"
								+ Utiles.formateaCadena(repor.get("fecha_desc").toString(), 30, true);
							}
*/							
							this.escribe(linea, "INASISTENCIAS", params,
									usuario);
						}
//jrr						this.escribe("", "INASISTENCIAS", params, usuario);
					} else {
						
					/*	linea = Utiles.formateaCadena(quiebre.get("cod_uorgan").toString().trim(), 8, true) 
							+ Utiles.formateaCadena("", 10, true)
							+ "No se encontraron registros.";
						this.escribe(linea, "INASISTENCIAS", params, usuario); */
					}
					
//jrr					this.escribe(linea, "INASISTENCIAS", params, usuario);
//jrr					this.escribe("", "INASISTENCIAS", params, usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "INASISTENCIAS", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacacionesPendientes(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "SALDOS VACACIONES", params, usuario);
			
			String regimen = (String) params.get("regimen");//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String dias = (String) params.get("dias");
			String anhoIni = (String) params.get("anhoIni");
			String anhoFin = (String) params.get("anhoFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

			ArrayList listaAnhos = facadeRemote
					.buscarAnnosVacacionesPendientes(dbpool, anhoIni, anhoFin,
							criterio, valor, dias, seguridad);

			ArrayList reporte = facadeRemote.vacacionesPendientes(dbpool,
					regimen, anhoIni, anhoFin, criterio, valor, dias, seguridad); //JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS - regimen

			if (reporte != null) {

				HashMap hmAnhos = new HashMap();
				if (listaAnhos != null && listaAnhos.size() > 0) {
					for (int i = 0; i < listaAnhos.size(); i++) {
						hmAnhos.put(listaAnhos.get(i).toString().trim(), ""
								+ (i + 6));
					}
				}

				HashMap hmTotales = new HashMap();
				ArrayList listaTotales = new ArrayList();

				this.escribe("", "SALDOS VACACIONES", params, usuario);

				linea = Utiles.formateaCadena("", 5, false)
						+ Utiles.formateaCadena("Registro", 8, true)
						+ Utiles.formateaCadena("Trabajador", 45, true)
						+ Utiles.formateaCadena("Unidad", 40, true)
						+ Utiles.formateaCadena("F.Ing./Rein.", 10, true);

				if (listaAnhos != null && listaAnhos.size() > 0) {
					for (int i = 0; i < listaAnhos.size(); i++) {
						linea += Utiles.formateaCadena("" + listaAnhos.get(i),
								6, true);
					}
				}

				linea += Utiles.formateaCadena("Acumulado", 6, true);

				this.escribe(linea, "SALDOS VACACIONES", params, usuario);
				this.escribe("", "SALDOS VACACIONES", params, usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);

					linea = Utiles.formateaCadena("", 5, false)
							+ Utiles.formateaCadena(quiebre.getCodigo().trim(),
									8, true)
							+ Utiles.formateaCadena(quiebre.getNombre().trim(),
									45, false)
							+ Utiles.formateaCadena(quiebre.getUnidad().trim(),
									40, false);

					linea += Utiles.formateaCadena(
							quiebre.getFecha() != null ? quiebre.getFecha()
									.trim() : "", 10, true);

					detalles = quiebre.getDetalle();

					int colAnterior = 5;
					int totPersona = 0;

					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanVacacion detalle = (BeanVacacion) detalles
									.get(j);

							totPersona += detalle.getSaldo();

							if ((colAnterior + 1) < Integer
									.parseInt((String) hmAnhos.get(detalle
											.getAnno().trim()))) {
								for (int k = colAnterior + 1; k < Integer
										.parseInt((String) hmAnhos.get(detalle
												.getAnno().trim())); k++) {
									linea += Utiles
											.formateaCadena("0", 6, true);
								}
							}

							colAnterior = Integer.parseInt((String) hmAnhos
									.get(detalle.getAnno().trim()));

							linea += Utiles.formateaCadena(""
									+ detalle.getSaldo(), 6, true);
						}

						if (colAnterior < listaAnhos.size() + 5) {
							for (int l = colAnterior + 1; l <= listaAnhos
									.size() + 5; l++) {
								linea += Utiles.formateaCadena("0", 6, true);
							}
						}
					} else {
						for (int k = colAnterior + 1; k < listaAnhos.size() + 5; k++) {
							linea += Utiles.formateaCadena("0", 6, true);
						}
					}

					linea += Utiles.formateaCadena("" + totPersona, 6, true);

					this.escribe(linea, "SALDOS VACACIONES", params, usuario);
					this.escribe("", "SALDOS VACACIONES", params, usuario);

					int cant = Integer
							.parseInt(hmTotales.get("" + totPersona) != null ? hmTotales
									.get("" + totPersona).toString()
									: "0");

					if (hmTotales.get("" + totPersona) == null) {
						listaTotales.add("" + totPersona);
					}

					hmTotales.put("" + totPersona, "" + (cant + 1));

				}

				if (Integer.parseInt((criterio == null || criterio.trim()
						.equals("")) ? "0" : criterio) > 0
						&& listaTotales.size() > 0) {
					this.escribe("", "SALDOS VACACIONES", params, usuario);

					this.escribe(Utiles.formateaCadena("", 5, false)
							+ Utiles.formateaCadena("RESUMEN DE RESULTADOS",
									this.maxColumnas, true),
							"SALDOS VACACIONES", params, usuario);
					linea = Utiles.formateaCadena("", 40, false)
							+ Utiles.formateaCadena("Cantitad",
									(this.maxColumnas - 70) / 2, true);
					linea += Utiles.formateaCadena("Nro. Personas",
							(this.maxColumnas - 70) / 2, true);
					this.escribe(linea, "SALDOS VACACIONES", params, usuario);

					for (int i = 0; i < listaTotales.size(); i++) {
						linea = Utiles.formateaCadena("", 40, false)
								+ Utiles.formateaCadena(listaTotales.get(i)
										.toString(),
										(this.maxColumnas - 70) / 2, true);
						linea += Utiles.formateaCadena(hmTotales.get(
								listaTotales.get(i).toString()).toString(),
								(this.maxColumnas - 70) / 2, true);
						this.escribe(linea, "SALDOS VACACIONES", params,
								usuario);
					}
				}

			} else {
				this.escribe("No se encontraron registros.",
						"SALDOS VACACIONES", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "SALDOS VACACIONES",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	//DTARAZONA - 
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacacionesProgramadas(HashMap params,String usuario)
			throws RemoteException {
		log.debug("ProgramacionMasivoFacade(vacacionesProgramadas - params): " + params);
		String id = (String) params.get("messageID");
		log.debug("Mesaggggge"+id);
		String dbpool = (String) params.get("dbpool");
		log.debug("Urllllll:"+dbpool);
		//log.debug("Dataaa:"+db.toString());
		String linea = "";
		try {

			super.creaReporte(id, "VACACIONES PROGRAMADAS", params, usuario);		
			log.debug("log1");
     		ProgramacionFacadeHome facadeHome = (ProgramacionFacadeHome) sl.getRemoteHome(ProgramacionFacadeHome.JNDI_NAME,ProgramacionFacadeHome.class);
			ProgramacionFacadeRemote facadeRemote = facadeHome.create();
			log.debug("log2");	
            /*ArrayList reporte = new ArrayList(facadeRemote.generarRepVacProgramadas(db).size());
            reporte.addAll(facadeRemote.generarRepVacProgramadas(db)); */
			log.debug("lllego");
			ArrayList reporte = (ArrayList)facadeRemote.generarRepVacProgramadas((Map)params); 
            log.debug("reporteeeee: " + reporte); //9 campos                             
            
			if (reporte != null && reporte.size() > 0) {				

				linea =Utiles.formateaCadena("N° Reg.", 10, true)+"|"							
				+ Utiles.formateaCadena("Apellidos y Nombres", 40, true)+"|"
				+ Utiles.formateaCadena("UUOO", 31, true)+"|"
				+ Utiles.formateaCadena("Periodo", 10, true)+"|"
				+ Utiles.formateaCadena("Desde", 15, true)+"|"
				+ Utiles.formateaCadena("Hasta", 15, true)+"|"
				+ Utiles.formateaCadena("N° Días", 10, true)+"|"
				+ Utiles.formateaCadena("Estado", 15, true);
						
				log.debug("linea: " + linea); //9 campos

				this.escribe(linea, "VACACIONES PROGRAMADAS", params, usuario);
				this.escribe("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "AUTORIZACIONES DE CLIMA LABORAL", params, usuario);
				this.escribe("", "VACACIONES PROGRAMADAS", params, usuario);

				Map quiebre = null;
												
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (HashMap) reporte.get(i);
					log.debug("quiebre: " + quiebre);
							String apnom=quiebre.get("t02ap_pate").toString().trim()+" "+quiebre.get("t02ap_mate").toString().trim()+", "+quiebre.get("t02nombres").toString().trim();
							linea = Utiles.formateaCadena(""+quiebre.get("t02cod_pers"), 10, false)+"|";
							linea += Utiles.formateaCadena(""+apnom, 40, false)+"|";	
							linea += Utiles.formateaCadena(""+quiebre.get("u_organ").toString().trim()+" - "+quiebre.get("t12des_corta").toString().trim(), 31, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("periodo"), 10, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("desde"), 16, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("hasta"), 16, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("dias"), 10, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("estado"), 15, false)+"|";	
							log.debug("linea data: " + linea); //9 campos

							this.escribe(linea, "VACACIONES PROGRAMADAS", params, usuario);
							this.escribe("", "VACACIONES PROGRAMADAS", params, usuario);					      
				}

			} else {
				this.escribe("No se encontraron registros","VACACIONES PROGRAMADAS", params, usuario);
			}

		} catch (Exception e) {
			log.error("Ha ocurrido un error en vacacionesProgramadas" + e.getMessage(), e);
			this.escribe("Error : " + e.getMessage(), "SALDOS VACACIONES",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	//FIN DTARAZONA 31/01/2018	
	
	/**
	 * Metdo encargado de obtener la lista de reportes
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param dbpool
	 * @param codPers
	 * @return
	 * @throws RemoteException
	 */
	public ArrayList cargarLogReportes(String dbpool, String codPers)
			throws RemoteException {

		ArrayList lista = null;
		try {
			T1481DAO dao = new T1481DAO();
			lista = dao.findLogsByCodPers(dbpool, codPers, "1");
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
		return lista;
	}
	

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacacionesEfectivasPendientes(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "VACACIONES PENDIENTES", params, usuario);

			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String dias = (String) params.get("dias");
			String anhoIni = (String) params.get("anhoIni");
			String anhoFin = (String) params.get("anhoFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList listaAnhos = facadeRemote.buscarAnnosVacacionesPendientes(
                    dbpool, anhoIni, anhoFin, criterio, valor, dias,
                    seguridad);

            ArrayList reporte = facadeRemote.vacacionesPendientesYProg(
                    dbpool, anhoIni, anhoFin, criterio, valor, dias,
                    seguridad);

            
			if (reporte != null) {

				HashMap hmAnhos = new HashMap();
				if (listaAnhos != null && listaAnhos.size() > 0) {
					for (int i = 0; i < listaAnhos.size(); i++) {
						hmAnhos.put(listaAnhos.get(i).toString().trim(), ""
								+ (i + 6));
					}
				}

				HashMap hmTotales = new HashMap();
				ArrayList listaTotales = new ArrayList();

				this.escribe("", "VACACIONES PENDIENTES", params, usuario);

				linea = Utiles.formateaCadena("", 5, false)
						+ Utiles.formateaCadena("Saldos", 90, true)
						+ Utiles.formateaCadena("Pendientes de Goce", 100, true);

				this.escribe(linea, "VACACIONES PENDIENTES", params, usuario);
				this.escribe("", "VACACIONES PENDIENTES", params, usuario);

				linea = Utiles.formateaCadena("", 5, false)
				//+ Utiles.formateaCadena("Registro", 8, true)
				+ Utiles.formateaCadena("Trabajador", 45, true);
				//+ Utiles.formateaCadena("Unidad", 40, true)
				//+ Utiles.formateaCadena("F.Ing.", 10, true)
				//+ Utiles.formateaCadena("F.Ing.", 10, true);

		if (listaAnhos != null && listaAnhos.size() > 0) {
			for (int i = 0; i < listaAnhos.size(); i++) {
				linea += Utiles.formateaCadena("" + listaAnhos.get(i),
						6, true);
			}
			linea += Utiles.formateaCadena("Acum.", 6, true);
		}

		linea += Utiles.formateaCadena("Del", 12, true)
		       + Utiles.formateaCadena("Al", 12, true)
		       + Utiles.formateaCadena("Dias", 6, true);

				
				this.escribe(linea, "VACACIONES PENDIENTES", params, usuario);
				this.escribe("", "VACACIONES PENDIENTES", params, usuario);
				BeanReporte quiebre = null;
				ArrayList detalles = null;
				ArrayList detaArr = null;
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);

					linea = Utiles.formateaCadena("", 5, false)
							+ Utiles.formateaCadena(quiebre.getNombre().trim(),
									45, false);

					this.escribe(linea, "VACACIONES PENDIENTES", params, usuario);
					this.escribe("", "VACACIONES PENDIENTES", params, usuario);
					
					detalles = quiebre.getDetalle();
					//ArrayList detalles = quiebre.getDetalle();
					BeanReporte bRepo = (BeanReporte)detalles.get(0);
					detaArr = bRepo.getDetalle();

					int colAnterior = 5;
					int totPersona = 0;
					linea = Utiles.formateaCadena("", listaAnhos.size()* 11, true);
					if (detaArr != null && detaArr.size() > 0) {
						for (int j = 0; j < detaArr.size(); j++) {
							BeanVacacion detalle = (BeanVacacion) detaArr.get(j);

							totPersona += detalle.getSaldo();

							if ((colAnterior + 1) < Integer
									.parseInt((String) hmAnhos.get(detalle
											.getAnno().trim()))) {
								for (int k = colAnterior + 1; k < Integer
										.parseInt((String) hmAnhos.get(detalle
												.getAnno().trim())); k++) {
									linea += Utiles
											.formateaCadena("0", 6, true);
								}
							}
							colAnterior = Integer.parseInt((String) hmAnhos
									.get(detalle.getAnno().trim()));

							linea += Utiles.formateaCadena(""
									+ detalle.getSaldo(), 6, true);
						}

						if (colAnterior < listaAnhos.size() + 5) {
							for (int l = colAnterior + 1; l <= listaAnhos
									.size() + 5; l++) {
								linea += Utiles.formateaCadena("0", 6, true);
							}
						}
					} else {
						for (int k = colAnterior + 1; k < listaAnhos.size() + 5; k++) {
							linea += Utiles.formateaCadena("0", 6, true);
						}
					}
					linea += Utiles.formateaCadena("" + totPersona, 6, true);
					
					bRepo = (BeanReporte)detalles.get(1);
					detaArr = (ArrayList)bRepo.getDetalle();
					if (detaArr != null && detaArr.size() > 0) {
						for (int j = 0; j < detaArr.size(); j++) {
							BeanReporte detalle = (BeanReporte) detaArr.get(j);

							totPersona = detalle.getCantidad();

							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalle.getFechaInicio()), 12, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalle.getFechaFin()), 12, true);
						
							linea += Utiles.formateaCadena(""
									+ detalle.getCantidad(), 6, true);
						}
						this.escribe(linea, "VACACIONES PENDIENTES", params, usuario);
						this.escribe("", "VACACIONES PENDIENTES", params, usuario);
						linea = Utiles.formateaCadena("", listaAnhos.size()* 23, true);
						linea += Utiles.formateaCadena("" + "TOTAL", 6, true)
						+ Utiles.formateaCadena("" + totPersona, 6, true);
					}
					this.escribe(linea, "VACACIONES PENDIENTES", params, usuario);
					this.escribe("", "VACACIONES PENDIENTES", params, usuario);


					int cant = Integer
							.parseInt(hmTotales.get("" + totPersona) != null ? hmTotales
									.get("" + totPersona).toString()
									: "0");

					if (hmTotales.get("" + totPersona) == null) {
						listaTotales.add("" + totPersona);
					}

					hmTotales.put("" + totPersona, "" + (cant + 1));

				}

			} else {
				this.escribe("No se encontraron registros.",
						"VACACIONES PENDIENTES", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "VACACIONES PENDIENTES",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	//dtarazona
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void asignacionResponsablesMasivo(HashMap params, String usuario)
			throws RemoteException {

		log.debug("asignacionResponsablesMasivo(): " + params);
		String id = (String) params.get("messageID");
		String proceso = (String) params.get("operacion");
		log.debug("OP: " + proceso); //9 campos
		String pool = (String) params.get("dbpool");
		String cod_pers = (String) params.get("cod_pers");
		String linea = "";
		try {

			super.creaReporte(id, "ASIGNACIÓN DE RESPONSABLE", params, cod_pers);		

			MantenimientoFacadeHome facadeHome = (MantenimientoFacadeHome) sl.getRemoteHome(MantenimientoFacadeHome.JNDI_NAME,MantenimientoFacadeHome.class);
			MantenimientoFacadeRemote facadeRemote = facadeHome.create();
			
			T12DAO dao=new T12DAO(pool);
			
            ArrayList reporte = (ArrayList)dao.findByEstado("1"); 
            log.debug("reporte: " + reporte);             
            
			if (reporte != null && reporte.size() > 0) {				

				linea =Utiles.formateaCadena("Id", 10, true)+"|"
				+ Utiles.formateaCadena("UUOO", 70, true)+"|"			
				+ Utiles.formateaCadena("Proceso", 40, true)+"|"
				+ Utiles.formateaCadena("Responsable", 15, true)+"|"
				+ Utiles.formateaCadena("Estado", 30, true);
							

				this.escribe(linea, "ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());
				this.escribe("------------------------------------------------------------------------------------------", "ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());
				this.escribe("", "ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());

				Map quiebre = null;
				String procDesc="";
				if(proceso.equals("1"))
					procDesc="Proceso de Asistencia";
				if(proceso.equals("2"))
					procDesc="Proceso de Calificación";
				if(proceso.equals("3"))
					procDesc="Proceso de Cierre";
				if(proceso.equals("4"))
					procDesc="Generación de Saldos Vacacionales";
				
				if(proceso.equals("5")){
					log.debug("Operación: Todos"); //9 campos
					for(int j=1;j<=4;j++){
						
						if(j==1)
							procDesc="Proceso de Asistencia";
						if(j==2)
							procDesc="Proceso de Calificación";
						if(j==3)
							procDesc="Proceso de Cierre";
						if(j==4)
							procDesc="Generación de Saldos Vacacionales";
						
						for (int i = 0; i < reporte.size(); i++) {
							quiebre = (HashMap) reporte.get(i);
							
							
									HashMap datosResp=new HashMap();
									datosResp.put("operacion",""+j);									
									datosResp.put("u_organ",quiebre.get("t12cod_uorga").toString().trim());
									datosResp.put("cod_pers",cod_pers.trim().toUpperCase());
									datosResp.put("cuser",params.get("cuser").toString().trim());
									
									boolean res=facadeRemote.registrarResponsableMasivo(pool, datosResp);
									String estado="";
									
									if(res) estado="Asignado";
									else estado="El responsable ya ha sido asignado a dicho proceso.";
									
									linea = Utiles.formateaCadena(""+i, 10, false)+"|";
									linea += Utiles.formateaCadena(""+quiebre.get("t12cod_uorga").toString().trim()+" - "+quiebre.get("t12des_uorga").toString().trim(), 70, false)+"|";							
									linea += Utiles.formateaCadena(""+procDesc, 40, false)+"|";
									linea += Utiles.formateaCadena(""+cod_pers, 15, false)+"|";
									linea += Utiles.formateaCadena(""+estado, 30, false);

									this.escribe(linea, "ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());
									this.escribe("", "ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());					      
						}
					}
				}else{
					log.debug("Operación:"+procDesc); //9 campos
					for (int i = 0; i < reporte.size(); i++) {
						quiebre = (HashMap) reporte.get(i);
						
							HashMap datosResp=new HashMap();
							datosResp.put("operacion",""+proceso);									
							datosResp.put("u_organ",quiebre.get("t12cod_uorga").toString().trim());
							datosResp.put("cod_pers",cod_pers.trim().toUpperCase());
							datosResp.put("cuser",params.get("cuser").toString().trim());
							
							boolean res=facadeRemote.registrarResponsableMasivo(pool, datosResp);
							String estado="";
							
							if(res) estado="ASIGNADO";
							else estado="El responsable ya ha sido asignado a dicho proceso.";
							
								linea = Utiles.formateaCadena(""+i, 10, false)+"|";
								linea += Utiles.formateaCadena(""+quiebre.get("t12cod_uorga").toString().trim()+" - "+quiebre.get("t12des_uorga").toString().trim(), 70, false)+"|";							
								linea += Utiles.formateaCadena(""+procDesc, 40, false)+"|";
								linea += Utiles.formateaCadena(""+cod_pers, 15, false)+"|";
								linea += Utiles.formateaCadena(""+estado, 30, false);
														
								this.escribe(linea, "ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());
								this.escribe("", "ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());					      
					}
				}
			} else {
				this.escribe("No se encontraron registros","ASIGNACIÓN DE RESPONSABLE", params, params.get("cuser").toString().trim());
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "ASIGNACION DE RESPONSABLE ",
					params, params.get("cuser").toString().trim());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(pool, params, params.get("cuser").toString().trim());
			} catch (Exception e) {
			}
		}
	}
	//fin dtarazona
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacaciones(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "VACACIONES", params, usuario);

			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String dias = (String) params.get("dias");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.vacacionesGoceEfectivo(dbpool,
                    fechaIni, fechaFin, criterio, valor, dias, seguridad);


            
			if (reporte != null) {

				this.escribe("", "VACACIONES", params, usuario);

				linea = Utiles.formateaCadena("", 5, false)
				//+ Utiles.formateaCadena("Registro", 8, true)
				+ Utiles.formateaCadena("Trabajador", 45, true)
				+ Utiles.formateaCadena("Desde", 12, true)
				+ Utiles.formateaCadena("Hasta", 12, true)
				+ Utiles.formateaCadena("Días", 10, true)
				+ Utiles.formateaCadena("Observación", 25, true)
				+ Utiles.formateaCadena("Tipo", 10, true);


				this.escribe(linea, "VACACIONES", params, usuario);
				this.escribe("", "VACACIONES", params, usuario);
				BeanReporte quiebre = null;
				ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);
					if(log.isDebugEnabled() && detalles!=null) log.debug("QuiebreImp:"+quiebre.getNombre().trim());
					linea = Utiles.formateaCadena("", 5, false)
							+ Utiles.formateaCadena(quiebre.getNombre().trim(),
									45, false);
					
					this.escribe(linea, "VACACIONES", params, usuario);
					this.escribe("", "VACACIONES", params, usuario);
					
					detalles = quiebre.getDetalle();
					//ArrayList detalles = quiebre.getDetalle();

					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);
							linea = Utiles.formateaCadena("", 50, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalle.getFechaInicio()), 12, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalle.getFechaFin()), 12, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getCantidad(), 12, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getDescripcion(), 25, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getNombre(), 12, true);
							this.escribe(linea, "VACACIONES", params, usuario);
							this.escribe("", "VACACIONES", params, usuario);
						
						}
					}else{
						this.escribe("El trabajador no posee vacaciones registradas", "VACACIONES", params, usuario);
						this.escribe("El trabajador no posee vacaciones programadas", "VACACIONES", params, usuario);
						this.escribe("", "VACACIONES", params, usuario);
					}
				}

			} else {
				this.escribe("No se encontraron registros.",
						"VACACIONES", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "VACACIONES",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacacionesEfectuadas(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "VACACIONES EFECTUADAS", params, usuario);
			
			String regimen = (String) params.get("regimen");//JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String dias = (String) params.get("dias");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.vacacionesGozadas(dbpool,
            		regimen, fechaIni, fechaFin, criterio, valor, dias, seguridad); //JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS - regimen
            
			if (reporte != null) {

				this.escribe("", "VACACIONES EFECTUADAS", params, usuario);

				linea = Utiles.formateaCadena("", 5, false)
				//+ Utiles.formateaCadena("Registro", 8, true)
				+ Utiles.formateaCadena("Trabajador", 45, true)
				+ Utiles.formateaCadena("Periodo", 10, true)
				+ Utiles.formateaCadena("Desde", 12, true)
				+ Utiles.formateaCadena("Hasta", 12, true)
				+ Utiles.formateaCadena("Dias", 12, true)
				+ Utiles.formateaCadena("Observacion", 20, true);

				this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
				this.escribe("", "VACACIONES EFECTUADAS", params, usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
				//int totPersona = 0;	
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);

					linea = Utiles.formateaCadena("", 5, false)
							+ Utiles.formateaCadena(quiebre.getNombre().trim(),
									45, false);

					this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
					this.escribe("", "VACACIONES EFECTUADAS", params, usuario);
					
					detalles = quiebre.getDetalle();
					
					int totPersona = 0;	
					int totAnho = 0;
					String anhoAux = "";
					linea = Utiles.formateaCadena("", 11, true);
					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);

							totPersona += detalle.getCantidad();
							if (j==0) {
								anhoAux = detalle.getAnno();
							}

							if (!detalle.getAnno().trim().equals(anhoAux)){
								linea = Utiles.formateaCadena("", 117, true);
								linea += Utiles.formateaCadena(""
										+ "Total Periodo:", 16, true);
								linea += Utiles.formateaCadena(""
										+ totAnho, 12, true);
								this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
								this.escribe("", "VACACIONES EFECTUADAS", params, usuario);
								
								totAnho = detalle.getCantidad();
							} else {
								totAnho += detalle.getCantidad();
							}
							linea = Utiles.formateaCadena("", 54, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getAnno(), 10, true);
							if (detalle.getCodigo().equals(Constantes.VACACION_VENTA)) {
								linea += Utiles.formateaCadena(""
											+ "Acuerdo de Reduccion", 24, true);
							}
							 else{ 
								linea += Utiles.formateaCadena(""
											+ Utiles.timeToFecha(detalle.getFechaInicio()), 12, true);
								linea += Utiles.formateaCadena(""
											+ Utiles.timeToFecha(detalle.getFechaFin()), 12, true);
							 } 

							linea += Utiles.formateaCadena(""
									+ detalle.getCantidad(), 10, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getDescripcion(), 20, true);

							this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
							this.escribe("", "VACACIONES EFECTUADAS", params, usuario);

							anhoAux = detalle.getAnno();
						}
						linea = Utiles.formateaCadena("", 117, true);
						linea += Utiles.formateaCadena(""
								+ "Total Periodo:", 16, true);
						linea += Utiles.formateaCadena(""
								+ totAnho, 12, true);
						this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
						this.escribe("", "VACACIONES EFECTUADAS", params, usuario);
						linea = Utiles.formateaCadena("", 129, true);
						linea += Utiles.formateaCadena("" + "TOTAL:", 6, true)
						+ Utiles.formateaCadena("" + totPersona, 6, true);
					} else {
						this.escribe("El trabajador no posee vacaciones efectuadas.", "VACACIONES EFECTUADAS", params, usuario);
					}	
				
				//linea = Utiles.formateaCadena("", 129, true);
				//linea += Utiles.formateaCadena("" + "TOTAL:", 6, true)
				//+ Utiles.formateaCadena("" + totPersona, 6, true);

				
				 HashMap detalleVac = quiebre.getMap();
			      if (detalleVac!=null && detalleVac.size()>0){	
					ArrayList listaSaldos = (ArrayList)detalleVac.get("vacacion"); 

					linea = Utiles.formateaCadena("", 5, false)
					//+ Utiles.formateaCadena("Registro", 8, true)
					+ Utiles.formateaCadena("Saldo Vacacional Pendiente", 45, true)
					+ Utiles.formateaCadena(" ", 66, true);
					
					this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
					this.escribe("", "VACACIONES EFECTUADAS", params, usuario);

					if (listaSaldos!=null && listaSaldos.size()>0){	

						linea = Utiles.formateaCadena("", 5, false)
						//+ Utiles.formateaCadena("Registro", 8, true)
						+ Utiles.formateaCadena("AÃƒÂ±o", 10, true)
						+ Utiles.formateaCadena("Saldo", 10, true)
						+ Utiles.formateaCadena(" ", 90, true);
						
						this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
						this.escribe("", "VACACIONES EFECTUADAS", params, usuario);

				      	 
				      for (int j = 0; j < listaSaldos.size(); j++){
				    	  BeanVacacion cabVacacion = (BeanVacacion)listaSaldos.get(j);
				    	  linea = Utiles.formateaCadena("", 5, false)
							//+ Utiles.formateaCadena("Registro", 8, true)
							+ Utiles.formateaCadena(cabVacacion.getAnno().trim(), 10, true)
							+ Utiles.formateaCadena(""+cabVacacion.getSaldo(), 10, true)
							+ Utiles.formateaCadena(" ", 90, true);
				    	  this.escribe(linea, "VACACIONES EFECTUADAS", params, usuario);
						this.escribe("", "VACACIONES EFECTUADAS", params, usuario);
					  }
				     }else{
				    	 this.escribe("No tiene Saldos Pendientes ",
									"VACACIONES EFECTUADAS", params, usuario);
						}
					}
			      
				}

			} else {
				this.escribe("No se encontraron registros.",
						"VACACIONES EFECTUADAS", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "VACACIONES EFECTUADAS",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacacionesCompensadas(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "VACACIONES COMPENSADAS", params, usuario);

			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.vacacionesCompensadas(dbpool,
                    fechaIni, fechaFin, criterio, valor, seguridad);


            
			if (reporte != null) {


				this.escribe("", "VACACIONES COMPENSADAS", params, usuario);

				linea = Utiles.formateaCadena("", 5, false)
				//+ Utiles.formateaCadena("Registro", 8, true)
				+ Utiles.formateaCadena("Trabajador", 45, true)
				+ Utiles.formateaCadena("Periodo", 10, true)
				+ Utiles.formateaCadena("Dias", 10, true)
				+ Utiles.formateaCadena("Observacion", 10, true);

				this.escribe(linea, "VACACIONES COMPENSADAS", params, usuario);
				this.escribe("", "VACACIONES COMPENSADAS", params, usuario);
				BeanReporte quiebre = null;
				ArrayList detalles = null;
				int totUnidad = 0;
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);

					linea = Utiles.formateaCadena("", 5, false)
							+ Utiles.formateaCadena(quiebre.getNombre().trim(),
									45, false);

					this.escribe(linea, "VACACIONES COMPENSADAS", params, usuario);
					this.escribe("", "VACACIONES COMPENSADAS", params, usuario);
					
					detalles = quiebre.getDetalle();


					linea = Utiles.formateaCadena("", 55, true);
					
					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);
							totUnidad += detalle.getCantidad();

							linea += Utiles.formateaCadena(""
									+ detalle.getAnno(), 10, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getCantidad(), 10, true);
						
							linea += Utiles.formateaCadena(""
									+ detalle.getDescripcion(), 10, true);
						}
						this.escribe(linea, "VACACIONES COMPENSADAS", params, usuario);
						this.escribe("", "VACACIONES COMPENSADAS", params, usuario);
					}


				}
				linea = Utiles.formateaCadena("", 23, true);
				linea += Utiles.formateaCadena("" + "TOTAL DE LA UNIDAD :", 6, true)
				+ Utiles.formateaCadena("" + totUnidad, 6, true);

				this.escribe(linea, "VACACIONES COMPENSADAS", params, usuario);
				this.escribe("", "VACACIONES COMPENSADAS", params, usuario);


			} else {
				this.escribe("No se encontraron registros.",
						"VACACIONES COMPENSADAS", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "VACACIONES COMPENSADAS",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void licenciaMedica(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "LICENCIA MEDICA : SUBSIDIO ", params, usuario);

			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String dias = (String) params.get("dias");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String tipoLic = (String) params.get("tipoLic");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.LicenciaMedica(dbpool,
                    fechaIni, fechaFin, criterio, valor, tipoLic, dias, seguridad);
            
			if (reporte != null) {

				this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);

				linea = Utiles.formateaCadena("", 5, false)
				//+ Utiles.formateaCadena("Registro", 8, true)
				+ Utiles.formateaCadena("Trabajador", 45, true)
				+ Utiles.formateaCadena("Licencia", 20, true)
				+ Utiles.formateaCadena("Desde", 12, true)
				+ Utiles.formateaCadena("Hasta", 12, true)
				+ Utiles.formateaCadena("Dias", 12, true)
				+ Utiles.formateaCadena("Observacion", 20, true);

				this.escribe(linea, "LICENCIA MEDICA : SUBSIDIO", params, usuario);
				this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
				int totPersona = 0;	
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);

					linea = Utiles.formateaCadena("", 5, false)
							+ Utiles.formateaCadena(quiebre.getNombre().trim(),
									45, false);

					this.escribe(linea, "LICENCIA MEDICA : SUBSIDIO", params, usuario);
					this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);
					
					detalles = quiebre.getDetalle();
					
					
					int totLicencia = 0;
					String licAux = "";
					linea = Utiles.formateaCadena("", 11, true);
					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);

							totPersona += detalle.getCantidad();
							if (j==0) {
								licAux = detalle.getNombre();
							}

							if (!detalle.getNombre().trim().equals(licAux.trim())){
								linea = Utiles.formateaCadena("", 117, true);
								linea += Utiles.formateaCadena(""
										+ "Total Licencia:", 16, true);
								linea += Utiles.formateaCadena(""
										+ totLicencia, 12, true);
								this.escribe(linea, "LICENCIA MEDICA : SUBSIDIO", params, usuario);
								this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);

								licAux = detalle.getNombre();
								totLicencia = detalle.getCantidad();
							} else {
								totLicencia += detalle.getCantidad();
							}
							linea = Utiles.formateaCadena("", 50, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getNombre(), 20, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalle.getFechaInicio()), 12, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalle.getFechaFin()), 12, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getCantidad(), 10, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getDescripcion(), 20, true);

							this.escribe(linea, "LICENCIA MEDICA : SUBSIDIO", params, usuario);
							this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);

						}
						linea = Utiles.formateaCadena("", 89, true);
						linea += Utiles.formateaCadena(""
								+ "Total Licencia:", 16, true);
						linea += Utiles.formateaCadena(""
								+ totLicencia, 12, true);
						this.escribe(linea, "LICENCIA MEDICA : SUBSIDIO", params, usuario);
						this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);
					}
					String lineaTitulo1 = "";
					for (int k = 0; k < 120; k++) {
						lineaTitulo1 += "-";
					}
					this.escribe(lineaTitulo1, "LICENCIA MEDICA : SUBSIDIO", params, usuario);
					this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);
				}
				linea = Utiles.formateaCadena("", 73, true);
				linea += Utiles.formateaCadena("" + "TOTAL:", 6, true)
				+ Utiles.formateaCadena("" + totPersona, 6, true);

				this.escribe(linea, "LICENCIA MEDICA : SUBSIDIO", params, usuario);
				this.escribe("", "LICENCIA MEDICA : SUBSIDIO", params, usuario);


			} else {
				this.escribe("No se encontraron registros.",
						"LICENCIA MEDICA : SUBSIDIO", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "LICENCIA MEDICA : SUBSIDIO",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void horaExtra(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "REPORTE DE LABOR EXCEPCIONAL", params,
					usuario);

			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String indDia = (String) params.get("indDia");
			String indMin = (String) params.get("indMin");
			
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = reporte = facadeRemote.horaExtra(dbpool,
					fechaIni, fechaFin, criterio, valor, seguridad, indDia, indMin);

			String unidad = "";

			if (reporte != null) {
				this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
						usuario);
				this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
						usuario);

				linea = Utiles.formateaCadena("Trabajador", 22, true)
						+ Utiles.formateaCadena("Fecha", 14, true)
						+ Utiles.formateaCadena("Hora Inicio", 14, true)
						+ Utiles.formateaCadena("Hora Fin", 14, true)
						+ Utiles.formateaCadena("Minutos Acumulados", 14, true)
						+ Utiles.formateaCadena("Saldo de Minutos", 14, true);
				this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL", params,
						usuario);
				this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
						usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {

					this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
							usuario);
					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getDetalle();
					HashMap mapa1 = (HashMap)quiebre.getMap();
					ArrayList detalle1 = (ArrayList) mapa1.get("Licencias");					
					ArrayList detalleP = (ArrayList) mapa1.get("Papeletas");


					linea = Utiles.formateaCadena("", 5, false)
							+ quiebre.getNombre().trim();
					this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL",
							params, usuario);
					this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
							usuario);
					if (detalles != null && detalles.size() > 0) {
						int totPersona = 0;	
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);
							HashMap mapa = (HashMap)detalle.getMap();
							linea = Utiles.formateaCadena("", 54, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getFecha(), 20, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getHora(), 12, true);
							linea += Utiles.formateaCadena(""
									+ detalle.getHora1() , 12, true);
							linea += Utiles.formateaCadena(""
									+ mapa.get("MinAcum"), 10, true);
							linea += Utiles.formateaCadena(""
									+ mapa.get("MinSal"), 10, true);
							String p = (String)mapa.get("MinSal");
							totPersona+=Integer.parseInt(p);
							this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL", params, usuario);
							this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params, usuario);

						}
						linea = Utiles.formateaCadena("", 80, true);
						linea +=Utiles.formateaCadena("SALDO TOTAL DE MINUTOS : ", 25, true);
						linea +=Utiles.formateaCadena(String.valueOf(totPersona), 6, true);
						this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL",
								params, usuario);
						this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
								usuario);
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El trabajador no posee minutos de Labor Excepcional";
						this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL",
								params, usuario);
					}
					this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
							usuario);

					
					if (detalle1 != null && detalle1.size() > 0) {
						//Cargamos el detalle de Licencias
						linea = Utiles.formateaCadena("COMPENSACION POR DIA", 22, true)
						+ Utiles.formateaCadena("Fecha Inicio", 14, true)
						+ Utiles.formateaCadena("Fecha Fin", 14, true)
						+ Utiles.formateaCadena("Dias", 14, true);
						this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL", params,	usuario);
						this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params, usuario);

						for (int j = 0; j < detalle1.size(); j++) {
							BeanReporte detalleM = (BeanReporte) detalle1.get(j);
							
							linea = Utiles.formateaCadena("", 54, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.dameDiaSemana(Utiles.timeToFecha(detalleM.getFechaInicio())), 20, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalleM.getFechaInicio()), 12, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.dameDiaSemana(Utiles.timeToFecha(detalleM.getFechaFin())) , 12, true);
							linea += Utiles.formateaCadena(""
									+ Utiles.timeToFecha(detalleM.getFechaFin()), 10, true);
							linea += Utiles.formateaCadena(""
									+ detalleM.getCantidad(), 10, true);

							this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL", params, usuario);
							this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params, usuario);

						}
					} 
					this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
							usuario);
					
					
					if (detalleP != null && detalleP.size() > 0) {
						//Cargamos el detalle de Papeletas
						linea = Utiles.formateaCadena("COMPENSACION POR MINUTOS - HORAS", 22, true)
						+ Utiles.formateaCadena("Fecha", 14, true)
						+ Utiles.formateaCadena("Marcaciones", 14, true)
						+ Utiles.formateaCadena("Estado", 14, true)
						+ Utiles.formateaCadena("Jefe", 14, true);
						this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL", params,	usuario);
						this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params, usuario);

						for (int j = 0; j < detalleP.size(); j++) {
							HashMap detP = (HashMap)detalleP.get(j);
							
							linea = Utiles.formateaCadena("", 54, true);
							linea += Utiles.formateaCadena(""
									+ (String)detP.get("fecha"), 20, true);
							linea += Utiles.formateaCadena(""
									+ (String)detP.get("hora"), 12, true);
							linea += Utiles.formateaCadena(""
									+ (String)detP.get("estado") , 12, true);
							linea += Utiles.formateaCadena(""
									+ (String)detP.get("jefe_autor"), 10, true);
							

							this.escribe(linea, "REPORTE DE LABOR EXCEPCIONAL", params, usuario);
							this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params, usuario);

						}
					}
					this.escribe("", "REPORTE DE LABOR EXCEPCIONAL", params,
							usuario);
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(),
					"REPORTE DE LABOR EXCEPCIONAL", params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	
	//PRAC-ASANCHEZ 03/08/2009
	/**
	 * Metdo encargado 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void detalleDiario(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {
			super.creaReporte(id, "DETALLE DIARIO", params, usuario);

			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String mov = (String) params.get("mov");
			HashMap seguridad = (HashMap) params.get("seguridad");
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.detalleDiario(dbpool,
					fechaIni, fechaFin, criterio, valor, seguridad, mov);
			String unidad = "";

			if (reporte != null) {
				this.escribe("", "DETALLE DIARIO", params, usuario);
				this.escribe("", "DETALLE DIARIO", params, usuario);
				linea = Utiles.formateaCadena("UUOO", 10, true)+"|"
						+ Utiles.formateaCadena("Trabajador", 60, true)+"|"
						+ Utiles.formateaCadena("Fecha", 25, true)+"|"
						+ Utiles.formateaCadena("Descripcion", 35, true)+"|"
						+ Utiles.formateaCadena("Movimientos", 20, true);
				this.escribe(linea, "DETALLE DIARIO", params, usuario);
				this.escribe("", "DETALLE DIARIO", params, usuario);
				BeanReporte quiebre = null;
				ArrayList detalles = null;
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getDetalle();

					if (!unidad.trim().equals(quiebre.getUnidad().trim()))
						unidad = quiebre.getUnidad().trim();

					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);
								linea = Utiles.formateaCadena(unidad.substring(0, 6), 10, true) +"|"+								
										Utiles.formateaCadena(quiebre.getNombre().substring(0, quiebre.getNombre().indexOf("(")-1), 60, false) +"|"+
							 			Utiles.formateaCadena(detalle.getNombre(), 25, false) +"|"+
							 			Utiles.formateaCadena(detalle.getDescripcion().trim(), 35, true) +"|"+
										Utiles.formateaCadena(detalle.getHora().trim(), 20, true);
							this.escribe(linea, "DETALLE DIARIO", params, usuario);
						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "No hay un detalle Diario para el trabajador";
						this.escribe(linea, "DETALLE DIARIO", params, usuario);
					}
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "DETALLE DIARIO", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	//
	
	/**PRAC-ASANCHEZ 28/08/2009
	 * Metdo encargado 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void laborExcepcional(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {
			super.creaReporte(id, "LABOR EXCEPCIONAL", params, usuario);
			
			Map seguridad = (Map) params.get("seguridad");
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			List reporte = facadeRemote.laborExcepcional(params, seguridad);
			String unidad = "";

			if (reporte != null) {
				this.escribe("", "LABOR EXCEPCIONAL", params, usuario);
				this.escribe("", "LABOR EXCEPCIONAL", params, usuario);
				linea = Utiles.formateaCadena("UUOO", 10, true)+"|"
						+ Utiles.formateaCadena("Trabajador", 60, true)+"|"
						+ Utiles.formateaCadena("Fecha", 25, true)+"|"
						+ Utiles.formateaCadena("Turno", 25, true)+"|"
						+ Utiles.formateaCadena("Desde", 25, true)+"|"
						+ Utiles.formateaCadena("Hasta", 25, true)+"|"
						+ Utiles.formateaCadena("Saldo(minutos)", 25, true)+"|"
						+ Utiles.formateaCadena("Saldo(dias)", 20, true);
				this.escribe(linea, "LABOR EXCEPCIONAL", params, usuario);
				this.escribe("", "LABOR EXCEPCIONAL", params, usuario);

				Map mapaDetalle = new HashMap();
				List listasMarcaciones = new ArrayList();
				BeanReporte quiebre = new BeanReporte();
				ArrayList detalle = new ArrayList();
				Map mapa = new HashMap();
				Map mapita = new HashMap();
				List listaMarcaciones = new ArrayList();
				Map intervalo = new HashMap();
				String fecha;
				int conta;
				String minutosDiarios;
				String diasDiarios;
				
				
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte)reporte.get(i);			
					detalle = quiebre.getDetalle();
					mapa = quiebre.getMap();
					
					listasMarcaciones = (ArrayList)mapa.get("listasMarcaciones");
					
					
					if (!unidad.trim().equals(quiebre.getUnidad().trim()))
						unidad = quiebre.getUnidad().trim();

					if (detalle != null && detalle.size() > 0) {
						
						fecha = "";	
						conta = 0;
						
						for (int j = 0; j < detalle.size(); j++) {

							mapaDetalle = (Map)detalle.get(j);
							
							
							if (!fecha.equals(mapaDetalle.get("fecha"))){
								fecha = (String)mapaDetalle.get("fecha"); 
								mapita = (HashMap)listasMarcaciones.get(conta);
								listaMarcaciones = (ArrayList)mapita.get("listaMarcaciones");
								minutosDiarios = (String)mapita.get("minutosDiarios");
								diasDiarios = (String)mapita.get("diasDiarios");
								conta++;
							
								linea = Utiles.formateaCadena(unidad.substring(0, 6), 10, true) +"|"+								
										Utiles.formateaCadena(quiebre.getNombre().substring(0, quiebre.getNombre().indexOf("(")-1), 60, false) +"|"+
										Utiles.formateaCadena((String)mapaDetalle.get("fecha"), 25, true) +"|"+
							 			Utiles.formateaCadena((String)mapaDetalle.get("des_turno"), 25, true) +"|"+
										Utiles.formateaCadena("", 25, true) +"|"+
					 					Utiles.formateaCadena("", 25, true) +"|"+
					 					Utiles.formateaCadena(minutosDiarios, 25, true) +"|"+
					 					Utiles.formateaCadena(diasDiarios, 20, true);					
								this.escribe(linea, "LABOR EXCEPCIONAL", params, usuario);
								for(int k = 0; k < listaMarcaciones.size(); k++){
									intervalo = (HashMap)listaMarcaciones.get(k);
									
									linea = Utiles.formateaCadena(unidad.substring(0, 6), 10, true) +"|"+								
											Utiles.formateaCadena(quiebre.getNombre().substring(0, quiebre.getNombre().indexOf("(")-1), 60, false) +"|"+
											Utiles.formateaCadena("", 25, true) +"|"+
								 			Utiles.formateaCadena("", 25, true) +"|"+
											Utiles.formateaCadena((String)intervalo.get("horaIni"), 25, true) +"|"+
						 					Utiles.formateaCadena((String)intervalo.get("horaFin"), 25, true) +"|"+
						 					Utiles.formateaCadena("", 25, true) +"|"+
						 					Utiles.formateaCadena("", 20, true);
									this.escribe(linea, "LABOR EXCEPCIONAL", params, usuario);
								}
							
							}
							
						}

						linea = Utiles.formateaCadena("", 10, true) +"|"+								
						Utiles.formateaCadena("", 60, false) +"|"+
						Utiles.formateaCadena("", 25, true) +"|"+
			 			Utiles.formateaCadena("", 25, true) +"|"+
						Utiles.formateaCadena("", 25, true) +"|"+
	 					Utiles.formateaCadena("TOTAL", 25, true) +"|"+
	 					Utiles.formateaCadena(mapa.get("totalMinutos").toString(), 25, true) +"|"+
	 					Utiles.formateaCadena(mapa.get("totalDias").toString(), 20, true);
						this.escribe(linea, "LABOR EXCEPCIONAL", params, usuario);
						linea = "";
						this.escribe(linea, "LABOR EXCEPCIONAL", params, usuario);
					
						
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "No hay un detalle DE LABOR EXCEPCIONAL para el trabajador";
						this.escribe(linea, "LABOR EXCEPCIONAL", params, usuario);
					}
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "LABOR EXCEPCIONAL", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/* ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */
	/**
	 * Metodo encargado de la generacion del reporte de Trabajadores notificados para Goce Vacacional
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"	
	 * @param params HashMap
	 * @param usuario String
	 * @throws RemoteException
	 */
	public void notificacionesXVacacionesATrabajadores(HashMap params, String usuario)
	throws RemoteException {

		if (log.isDebugEnabled()) log.debug("Ingreso a notificacionesXVacacionesATrabajadores-ReporteMasivoFacade");
		String id = (String) params.get("messageID");		
		String dbpool = (String) params.get("dbpool");	
		String linea = "";
		try {

			String fechaIni = null;
			String fechaFin = null;

			params.put("fechaIni", fechaIni);
			params.put("fechaFin", fechaFin);		
			if (log.isDebugEnabled()) log.debug("params: "+params);
			super.creaReporte(id, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

			String regimen = (String) params.get("regimen");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String valore = (String) params.get("valor");
			String solicitud = (String) params.get("solicitud");
			String fechaNotific = (String) params.get("fechaNotific");
			String fechaNotificFin = (String) params.get("fechaNotificFin");
			String fechaIniGoce = (String) params.get("fechaIniGoce");			
			String fechaFinGoce = (String) params.get("fechaFinGoce");
			String reg = (String) params.get("reg");
			HashMap seguridad = (HashMap) params.get("seguridad");

			T12DAO t12DAOrh = new T12DAO(dbpool); //trabaja con la t12uorga
			//String valore = "";
			if(criterio.equals("5")){ //intendencia

				List lIntendencia = t12DAOrh.findUObyCodUo(valor);
				Map mIntendencia = (HashMap)lIntendencia.get(0);
				valor = mIntendencia.get("t12des_corta").toString();
			}
			if(criterio.equals("4")){ //institucional        				
				valor = "";
			}


			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

			List reporte = facadeRemote.notificacionesXVacacionesATrabajadores(dbpool, regimen, fechaNotific, fechaNotificFin, fechaIniGoce, fechaFinGoce, criterio, valore,solicitud,seguridad);
			if (log.isDebugEnabled()) log.debug("reporte: "+reporte);

			String unidad = "";

			if (reporte != null) {

				this.escribe("", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true);
							
				this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

				linea = Utiles.formateaCadena(((!reg.equals("") && reg!=null)?"  Reg./Modalidad: "+reg:""), 30, true)				
				+ Utiles.formateaCadena(((!fechaNotific.equals("") && fechaNotific!=null)?"  Fec.Notif.Inicio: "+fechaNotific:((!fechaIniGoce.equals("") && fechaIniGoce!=null)?"  Fec.Inicio Goce: "+fechaIniGoce:"")), 30, true)				
				+ Utiles.formateaCadena(((!fechaNotificFin.equals("") && fechaNotificFin!=null)?"  Fec.Notif.Fin: "+fechaNotificFin:((!fechaFinGoce.equals("") && fechaFinGoce!=null)?"  Fec.Fin Goce: "+fechaFinGoce:"")), 30, true)
				+ Utiles.formateaCadena((criterio.equals("0")?" Registro:":(criterio.equals("1")?" UUOO:":(criterio.equals("5")?" Intendencia:":" Criterio:Institucional")))+ " "+valor, 30, true);

				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

				linea = Utiles.formateaCadena("Sólo registradas: "+ (solicitud.equals("0")?"No":(solicitud.equals("1")?"Si":"Todos")), 30, true);
				if (log.isDebugEnabled()) log.debug("solicitud: "+linea);
				this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

				this.escribe("------------------------------------------------------------------------------------------------------------------------", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

				this.escribe("", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
				linea = Utiles.formateaCadena("UUOO", 10, true)+"|"
				+ Utiles.formateaCadena("Descripción", 30, true)+"|"
				+ Utiles.formateaCadena("Registro", 10, true)+"|"
				+ Utiles.formateaCadena("Trabajador", 30, true)+"|"
				+ Utiles.formateaCadena("Fec.Notif1", 15, true)	+"|"
				+ Utiles.formateaCadena("Fec.Notif2", 15, true)	+"|"
				+ Utiles.formateaCadena("Fec.Notif3", 15, true)	+"|"
				+ Utiles.formateaCadena("Fec.Notif4", 15, true)	+"|"
				+ Utiles.formateaCadena("Fec.Notif5", 15, true)	+"|"
				+ Utiles.formateaCadena("Estado Prog.", 20, true)	+"|"
				+ Utiles.formateaCadena("Fec.Ini.Goce", 15, true)	+"|"
				+ Utiles.formateaCadena("Fec.Registro Sol.", 15, true);
				if (log.isDebugEnabled()) log.debug("linea: "+linea);
				this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
				this.escribe("", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
				HashMap detalle =null;
				for (int i = 0; i < reporte.size(); i++) {

					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getDetalle();

					//Quiebre por unidad organizacional...
					/*if (!unidad.trim().equals(quiebre.getUnidad().trim())) {
						unidad = quiebre.getUnidad().trim();
						if (i != 0) {
							this.escribe("", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
						}
						linea = Utiles.formateaCadena("", 2, false) + unidad;
						this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
						this.escribe("", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
					}*/

					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							detalle = (HashMap) detalles.get(j);
							linea = Utiles.formateaCadena((String)detalle.get("uuoo"), 10, false)+"|"
							+ Utiles.formateaCadena(((String)detalle.get("desuo")).trim(), 30, false)+"|"
							+ Utiles.formateaCadena((String)detalle.get("codpers"), 10, false)+"|"
							+ Utiles.formateaCadena(((String)detalle.get("nombre")).trim(), 30, false)+"|"
							+ Utiles.formateaCadena(detalle.get("fecnotif")!=null?((String)detalle.get("fecnotif")).trim():"", 15,false)+"|"
							+ Utiles.formateaCadena(detalle.get("fecnotif1")!=null?((String)detalle.get("fecnotif1")).trim():"", 15,false)+"|"
							+ Utiles.formateaCadena(detalle.get("fecnotif2")!=null?((String)detalle.get("fecnotif2")).trim():"", 15,false)+"|"
							+ Utiles.formateaCadena(detalle.get("fecnotif3")!=null?((String)detalle.get("fecnotif3")).trim():"", 15,false)+"|"
							+ Utiles.formateaCadena(detalle.get("fecnotif4")!=null?((String)detalle.get("fecnotif4")).trim():"", 15,false)+"|"
							+ Utiles.formateaCadena(((String)detalle.get("estado")).trim(), 20,false)+"|"
							+ Utiles.formateaCadena(((String)detalle.get("fecinigoce")).trim(), 15,false)+"|"
							+ Utiles.formateaCadena(((String)detalle.get("fecharegistro")).trim(), 15,false);
							if (log.isDebugEnabled()) log.debug("linea: "+linea);
							this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL",params, usuario);	
						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
						+ "No existen Notificaciones";
						if (log.isDebugEnabled()) log.debug("linea: "+linea);
						this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params,
								usuario);
					}
					this.escribe("", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
				}

				this.escribe("------------------------------------------------------------------------------------------------------------------------", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

				this.escribe("", "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);

				linea="Total Trabajadores=  "+String.valueOf(reporte.size());
				if (log.isDebugEnabled()) log.debug("linea: "+linea);
				this.escribe(linea, "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL", params, usuario);
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL",params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}	
	/* FIN ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */
			
	/**JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void notificaDirectivos(HashMap params, String usuario)
			throws RemoteException {
		log.debug("notificaDirectivos - params:" + params);
		log.debug("notificaDirectivos - usuario:" + usuario);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";		
		String criterio = (String) params.get("criterio");		
		String valor = (String) params.get("valor");
		String reg = (String) params.get("reg");		
		String periodo = (String) params.get("periodo");
		
		T12DAO t12DAOrh = new T12DAO(dbpool); //trabaja con la tabla t12uorga
		//String valore = "";
        if(criterio.equals("5")){ //intendencia        				
        				List lIntendencia = t12DAOrh.findUObyCodUo(valor);
                        Map mIntendencia = (HashMap)lIntendencia.get(0);
                        valor = mIntendencia.get("t12des_corta").toString();
        }
        if(criterio.equals("4")){ //institucional        				
			valor = "";
        }	
        int contMismaUUOO=1; //contador de directivos de la misma UUOO
        Map mNotif_i=new HashMap();
		String uuoo_i=null;
		Map mNotif_j=new HashMap();
		String uuoo_j=null;
		try {
			super.creaReporteTrab(id, "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario); //JVILLACORTA - 18/04/2011 modificacion de fecha en cabecera			

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.notificacionesDirectivos(params);
			log.debug("reporte:" + reporte);

			if (reporte != null) {				
				linea = Utiles.formateaCadena("Criterios de Búsqueda: ", 25, true)								
				+ Utiles.formateaCadena(((!reg.equals("") && reg!=null)?"  Reg./Modalidad: "+reg:""), 30, true)
				+ Utiles.formateaCadena(((!periodo.equals("") && periodo!=null)?"  Periodo: "+periodo:""), 30, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?" Registro:":(criterio.equals("1")?" UUOO:":(criterio.equals("5")?" Intendencia:":" Criterio:Institucional")))+ " "+valor, 30, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);				
				
				this.escribe("", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
				linea = Utiles.formateaCadena("UUOO", 10, true)
						+ Utiles.formateaCadena("Reg.", 6, true)
						+ Utiles.formateaCadena("Nombres", 30, true)
						+ Utiles.formateaCadena("Cargo", 20, true)
						+ Utiles.formateaCadena("Sem.1", 5, true)
						+ Utiles.formateaCadena("Sem.2", 5, true)
						+ Utiles.formateaCadena("Sem.3", 5, true)
						+ Utiles.formateaCadena("Sem.4", 5, true)
						+ Utiles.formateaCadena("Sem.5", 5, true);						
				this.escribe(linea, "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
				this.escribe("", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);

				Map quiebre = null;
				List detalles = null;
				int sumXUO = 0;
				int sumTotal = 0;
				int sumSem = 0;
				for (int i = 0; i < reporte.size(); i++) {//size=15, 14						
					mNotif_i=null;
					mNotif_j=null;
					sumSem = 0;
					quiebre = (HashMap) reporte.get(i);
					uuoo_i=(String)quiebre.get("uuoo");
					detalles = (ArrayList)quiebre.get("detalle");
					
					linea = Utiles.formateaCadena("", 2, false)
							+ Utiles.formateaCadena(((String)quiebre.get("uuoo")).trim(), 10, false)
							+ Utiles.formateaCadena(((String)quiebre.get("codigo")).trim(), 6, false)
							+ Utiles.formateaCadena(((String)quiebre.get("apenom")).trim(), 30, false)
							+ Utiles.formateaCadena("", 5, false) 
							+ Utiles.formateaCadena(((String)quiebre.get("cargo")).trim(), 20, false);
												
					this.escribe(linea, "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);							
					
					String cantSem1 = "0";
					String cantSem2 = "0";
					String cantSem3 = "0";
					String cantSem4 = "0";
					String cantSem5 = "0";
										
					if (detalles != null && detalles.size() > 0) {
						Map detalle = null;
						String semana = "";						
						String cantidad = "";						
						for (int j = 0; j < detalles.size(); j++) {
							detalle = (HashMap)detalles.get(j);
							semana = ((String) detalle.get("semana")!=null && ((String)detalle.get("semana")).toString().length()>0)?((String) detalle.get("semana")).trim():"";							
							log.debug("semana("+j+"):" + semana + " del directivo: "+((String)quiebre.get("codigo")).trim());
							cantidad = ((String) detalle.get("cantidad_desc")!=null && ((String)detalle.get("cantidad_desc")).toString().length()>0)?((String) detalle.get("cantidad_desc")).trim().substring(0,1):"";
							log.debug("cantidad("+j+"):" + cantidad + " del directivo: "+((String)quiebre.get("codigo")).trim());
							if (semana.equals("Sem1")){								
								cantSem1=cantidad;
							}
							if (semana.equals("Sem2")){								
								cantSem2=cantidad;
							}
							if (semana.equals("Sem3")){								
								cantSem3=cantidad;
							}
							if (semana.equals("Sem4")){								
								cantSem4=cantidad;
							}
							if (semana.equals("Sem5")){								
								cantSem5=cantidad;
							}						
						}
						sumSem = Integer.parseInt(cantSem1)+Integer.parseInt(cantSem2)+ Integer.parseInt(cantSem3)+ Integer.parseInt(cantSem4)+ Integer.parseInt(cantSem5);
						log.debug("sumSe:" + sumSem);
						
						linea = Utiles.formateaCadena("", 66, true) + (!cantSem1.equals("")?cantSem1:"0")	
						+ Utiles.formateaCadena("", 6, true) + (!cantSem2.equals("")?cantSem2:"0")
						+ Utiles.formateaCadena("", 6, true) + (!cantSem3.equals("")?cantSem3:"0")
						+ Utiles.formateaCadena("", 6, true) + (!cantSem4.equals("")?cantSem4:"0")
						+ Utiles.formateaCadena("", 6, true) + (!cantSem5.equals("")?cantSem5:"0");
						log.debug("linea:" + linea + " del directivo: "+((String)quiebre.get("codigo")).trim());
						this.escribe(linea, "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);						
											
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El directivo no posee notificaciones";
						this.escribe(linea, "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
					}
					this.escribe("", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
					
					if (i<reporte.size()-1){//13						
						quiebre = (HashMap) reporte.get(i+1);//1
						uuoo_j=(String)quiebre.get("uuoo");						
						if (uuoo_j.equals(uuoo_i)){
							log.debug("igual");
							contMismaUUOO=contMismaUUOO+1;
							log.debug("sumSem:" + sumSem);
							log.debug("sumXUO1 :" + sumXUO);
							sumXUO = sumXUO + sumSem;
							log.debug("sumXUO2 :" + sumXUO);
						}else{									
							log.debug("sumSem:" + sumSem);
							log.debug("sumXUO1 :" + sumXUO);
							sumXUO = sumXUO + sumSem;
							log.debug("sumXUO2 :" + sumXUO);
							//codigo de impresion en el archivo (subtotal y contMismaUUOO)
							this.escribe("==========================================================================================", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
							this.escribe("Subtotal"+" "+uuoo_i+" =  " + String.valueOf(sumXUO), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
							//this.escribe("Subtotal"+" "+uuoo_i+" =  "+String.valueOf(contMismaUUOO) + "  Subtotal Notificaciones = "+String.valueOf(sumXUO), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
							this.escribe("", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
							contMismaUUOO=1;
							log.debug("sumTotal1:" + sumTotal);
							sumTotal = sumTotal + sumXUO;
							log.debug("sumTotal2:" + sumTotal);
							sumXUO = 0;
							log.debug("sumXUO:" + sumXUO);
						}							
					}else{
						log.debug("ultima subUO");
						log.debug("sumSem:" + sumSem);
						log.debug("sumXUO1 :" + sumXUO);
						sumXUO = sumXUO + sumSem;
						log.debug("sumXUO2 :" + sumXUO);
						log.debug("sumTotal1:" + sumTotal);
						sumTotal = sumTotal + sumXUO;
						log.debug("sumTotal2:" + sumTotal);
						//codigo de impresion en el archivo (ultimo subtotal y contMismaUUOO)
						this.escribe("==========================================================================================", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
						this.escribe("Subtotal"+" "+uuoo_i+" =  " + String.valueOf(sumXUO), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
						//this.escribe("Subtotal"+" "+uuoo_i+" =  "+String.valueOf(contMismaUUOO)+ "  Subtotal Notificaciones = "+String.valueOf(sumXUO), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
						this.escribe("", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
						//codigo de impresion en el archivo (total de filas)
						this.escribe("==========================================================================================", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
						this.escribe("Total General"+" =  " + String.valueOf(sumTotal), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
						//this.escribe("Total General"+" =  "+String.valueOf(reporte.size()+ "  Total Notificaciones = "+String.valueOf(sumTotal)), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);													
					}
				}
			}
			/*if (reporte != null) {
				this.escribe("Total: "+String.valueOf(reporte.size()), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
			}*/
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/**JVILLACORTA - 18/04/2011 - ALERTA DE SOLICITUDES
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void notificaTrabajadores(HashMap params, String usuario)
			throws RemoteException {
		log.debug("notificaTrabajadores - params:" + params);
		log.debug("notificaTrabajadores - usuario:" + usuario);
		
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String criterio = (String) params.get("criterio");		
		String valor = (String) params.get("valor");
		String reg = (String) params.get("reg");		
		String periodo = (String) params.get("periodo");

		T12DAO t12DAOrh = new T12DAO(dbpool); //trabaja con la tabla t12uorga
		//String valore = "";
        if(criterio.equals("5")){ //intendencia        				
        				List lIntendencia = t12DAOrh.findUObyCodUo(valor);
                        Map mIntendencia = (HashMap)lIntendencia.get(0);
                        valor = mIntendencia.get("t12des_corta").toString();
        }
        if(criterio.equals("4")){ //institucional        				
			valor = "";
        }
 
        int contMismaUUOO=1; //contador de directivos de la misma UUOO
        Map mNotif_i=new HashMap();
		String uuoo_i=null;
		Map mNotif_j=new HashMap();
		String uuoo_j=null;
		
		try {
			super.creaReporteTrab(id, "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);			

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			ArrayList reporte = facadeRemote.notificacionesTrabajadores(params);
			log.debug("reporte:" + reporte);

			if (reporte != null) {				
				linea = Utiles.formateaCadena("Criterios de Búsqueda: ", 25, true)				
				+ Utiles.formateaCadena(((!reg.equals("") && reg!=null)?"  Reg./Modalidad: "+reg:""), 30, true)
				+ Utiles.formateaCadena(((!periodo.equals("") && periodo!=null)?"  Periodo: "+periodo:""), 30, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?" Registro:":(criterio.equals("1")?" UUOO:":(criterio.equals("5")?" Intendencia:":" Criterio:Institucional")))+ " "+valor, 30, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS", params, usuario);
								
				this.escribe("", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
				linea = Utiles.formateaCadena("UUOO", 10, true)
						+ Utiles.formateaCadena("Reg.", 6, true)
						+ Utiles.formateaCadena("Nombres", 30, true)
						+ Utiles.formateaCadena("Cargo", 20, true)
						+ Utiles.formateaCadena("Sem.1", 5, true)
						+ Utiles.formateaCadena("Sem.2", 5, true)
						+ Utiles.formateaCadena("Sem.3", 5, true)
						+ Utiles.formateaCadena("Sem.4", 5, true)
						+ Utiles.formateaCadena("Sem.5", 5, true);						
				this.escribe(linea, "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
				this.escribe("", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);

				Map quiebre = null;
				List detalles = null;
				int sumXUO = 0;
				int sumTotal = 0;
				int sumSem = 0;
				for (int i = 0; i < reporte.size(); i++) {
					mNotif_i=null;
					mNotif_j=null;
					sumSem = 0;
					quiebre = (HashMap) reporte.get(i);
					uuoo_i=(String)quiebre.get("uuoo");
					detalles = (ArrayList)quiebre.get("detalle");					
					
					linea = Utiles.formateaCadena("", 2, false)
							+ Utiles.formateaCadena(((String)quiebre.get("uuoo")).trim(), 10, false)
							+ Utiles.formateaCadena(((String)quiebre.get("codigo")).trim(), 6, false)
							+ Utiles.formateaCadena(((String)quiebre.get("apenom")).trim(), 30, false)
							+ Utiles.formateaCadena("", 5, false) //ICAPUNAY 15/08/2011 dejar espacio para iniciar la desc del cargo
							+ Utiles.formateaCadena(((String)quiebre.get("cargo")).trim(), 18, false);							
			
					this.escribe(linea, "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
					
					//ICAPUNAY 12/08/2011 No se repita fila para mostrar las notificaciones de la semana										
					String cantSem1 = "0";
					String cantSem2 = "0";
					String cantSem3 = "0";
					String cantSem4 = "0";
					String cantSem5 = "0";
					//FIN ICAPUNAY 12/08/2011 No se repita fila para mostrar las notificaciones de la semana
					if (detalles != null && detalles.size() > 0) {
						Map detalle = null;
						String semana = "";						
						String cantidad = ""; //ICAPUNAY 12/08/2011 No se repita fila para mostrar las notificaciones de la semana						
						for (int j = 0; j < detalles.size(); j++) {
							detalle = (HashMap)detalles.get(j);
							semana = ((String) detalle.get("semana")!=null && ((String)detalle.get("semana")).toString().length()>0)?((String) detalle.get("semana")).trim():"";							
							log.debug("semana("+j+"):" + semana + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							cantidad = ((String) detalle.get("cantidad_desc")!=null && ((String)detalle.get("cantidad_desc")).toString().length()>0)?((String) detalle.get("cantidad_desc")).trim().substring(0,1):"";
							log.debug("cantidad("+j+"):" + cantidad + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							if (semana.equals("Sem1")){								
								cantSem1=cantidad;
							}							
							if (semana.equals("Sem2")){								
								cantSem2=cantidad;
							}
							if (semana.equals("Sem3")){								
								cantSem3=cantidad;
							}
							if (semana.equals("Sem4")){								
								cantSem4=cantidad;
							}
							if (semana.equals("Sem5")){								
								cantSem5=cantidad;
							}
							log.debug("cantSem1:" + cantSem1 + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							log.debug("cantSem2:" + cantSem2 + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							log.debug("cantSem3:" + cantSem3 + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							log.debug("cantSem4:" + cantSem4 + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							log.debug("cantSem5:" + cantSem5 + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							
						}
						sumSem = Integer.parseInt(cantSem1)+Integer.parseInt(cantSem2)+ Integer.parseInt(cantSem3)+ Integer.parseInt(cantSem4)+ Integer.parseInt(cantSem5);
						log.debug("sumSe:" + sumSem);
												
							//ICAPUNAY 12/08/2011 No se repita fila para mostrar las notificaciones de la semana
							linea = Utiles.formateaCadena("", 66, true) + (!cantSem1.equals("")?cantSem1:"0")
							+ Utiles.formateaCadena("", 6, true) + (!cantSem2.equals("")?cantSem2:"0")
							+ Utiles.formateaCadena("", 6, true) + (!cantSem3.equals("")?cantSem3:"0")
							+ Utiles.formateaCadena("", 6, true) + (!cantSem4.equals("")?cantSem4:"0")
							+ Utiles.formateaCadena("", 6, true) + (!cantSem5.equals("")?cantSem5:"0"); 
							log.debug("linea:" + linea + " del trabajador: "+((String)quiebre.get("codigo")).trim());
							this.escribe(linea, "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);	
						
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "El trabajador no posee notificaciones";
						this.escribe(linea, "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
					}
					this.escribe("", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
					
					if (i<reporte.size()-1){//13						
						quiebre = (HashMap) reporte.get(i+1);//1
						uuoo_j=(String)quiebre.get("uuoo");						
						if (uuoo_j.equals(uuoo_i)){	
							log.debug("igual");
							contMismaUUOO=contMismaUUOO+1;
							log.debug("sumSem:" + sumSem);
							log.debug("sumXUO1 :" + sumXUO);
							sumXUO = sumXUO + sumSem;
							log.debug("sumXUO2 :" + sumXUO);
						}else{							
							log.debug("sumSem:" + sumSem);
							log.debug("sumXUO1 :" + sumXUO);
							sumXUO = sumXUO + sumSem;
							log.debug("sumXUO2 :" + sumXUO);
							//codigo de impresion en el archivo (subtotal y contMismaUUOO)
							this.escribe("==========================================================================================", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
							this.escribe("Subtotal"+" "+uuoo_i+" =  " + String.valueOf(sumXUO), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
							//this.escribe("Subtotal"+" "+uuoo_i+" =  "+String.valueOf(contMismaUUOO) + "  Subtotal Notificaciones = "+String.valueOf(sumXUO), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
							this.escribe("", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
							contMismaUUOO=1;
							log.debug("sumTotal1:" + sumTotal);
							sumTotal = sumTotal + sumXUO;
							log.debug("sumTotal2:" + sumTotal);
							sumXUO = 0;
							log.debug("sumXUO:" + sumXUO);
						}							
					}else{
						log.debug("ultima subUO");
						log.debug("sumSem:" + sumSem);
						log.debug("sumXUO1 :" + sumXUO);
						sumXUO = sumXUO + sumSem;
						log.debug("sumXUO2 :" + sumXUO);
						log.debug("sumTotal1:" + sumTotal);
						sumTotal = sumTotal + sumXUO;
						log.debug("sumTotal2:" + sumTotal);
						//codigo de impresion en el archivo (ultimo subtotal y contMismaUUOO)
						this.escribe("==========================================================================================", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
						this.escribe("Subtotal"+" "+uuoo_i+" =  " + String.valueOf(sumXUO), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
						//this.escribe("Subtotal"+" "+uuoo_i+" =  "+String.valueOf(contMismaUUOO)+ "  Subtotal Notificaciones = "+String.valueOf(sumXUO), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
						this.escribe("", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
						//codigo de impresion en el archivo (total de filas)
						this.escribe("==========================================================================================", "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
						this.escribe("Total General"+" =  " + String.valueOf(sumTotal), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
						//this.escribe("Total General"+" =  "+String.valueOf(reporte.size()+ "  Total Notificaciones = "+String.valueOf(sumTotal)), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);													
					}
				}
			}
			/*if (reporte != null) {
				this.escribe("Total: "+String.valueOf(reporte.size()), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params, usuario);
			}*/
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}	
	
	/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	/** 
	 * Metodo encargado de obtener el reporte de gestion masivo diario por movimiento	
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */	
	public void masivoDiarioMovimiento(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String nombreCategoria = "";		
		ArrayList movimientos = null;
		String descripMov = "";
		String codMov = "";
				
		String periodo = (String) params.get("periodo");
		String regimen = (String) params.get("regimen");
		String indicador = (String) params.get("indicador");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String nombre_inte = (String) params.get("nombre_inte");		
		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
				
		try {

			super.creaReporte(id, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			List detalleReporte = facadeRemote.resumenDiarioMovimiento(params); //Metodo de ReporteFacade			

			if (detalleReporte != null && detalleReporte.size()>0) {
				log.debug("detalleReporte: " + detalleReporte);
				
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad: "+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena(" Periodo: "+periodo, 30, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena(" Indicador: "+(indicador.equals("h")?"Horas":"Dias"), 30, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);

				this.escribe("", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);			

				linea = Utiles.formateaCadena("Categoria/Movimiento", 22, false);//antes true
				String fIni = fechaIni;
				String fFin = fechaFin;
				String fAct = fIni;
				String fReal = fechaIni;
	        	fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;//dd/mm/yyyy
				while (fAct.compareTo(fFin) <= 0) {
					linea = linea + Utiles.formateaCadena(fReal.substring(0,2),12,true); //escribe los dias del periodo
					if (fReal.indexOf("/")>3){ 
				  		fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
					}
					fReal = Utiles.dameFechaSiguiente(fReal, 1);
					fAct = Utiles.toYYYYMMDD(fReal);
				}				
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);
				this.escribe("", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);

				BeanReporte quiebre = null;
				HashMap detalleCategoria = null;
				HashMap movimiento = null;				
				for (int i = 0; i < detalleReporte.size(); i++) {//c/categoria

					this.escribe("", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);
					quiebre = (BeanReporte) detalleReporte.get(i);
					detalleCategoria = quiebre.getMap(); //Todos los valores del mapa					
					nombreCategoria = quiebre.getDescripcion().trim(); //Nombre de la categoria
					movimientos = quiebre.getDetalle(); //Movimientos de la Categoria
					//Quiebre por categoria..					
					linea = Utiles.formateaCadena("", 2, false) + nombreCategoria.toUpperCase(); //icapunay 25/11/2011 - conversion a mayuscula
					this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);
						
					
					for (int j=0; j<movimientos.size(); j++){//c/movimiento
						movimiento = (HashMap) movimientos.get(j);			
						codMov = movimiento.get("cod_mov").toString().trim();	
						descripMov = (String)movimiento.get("descrip");
						linea = Utiles.formateaCadena("", 5, false) + codMov+" "+descripMov+" ";
						this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);					
						
						if (!detalleCategoria.isEmpty()) {
							linea = Utiles.formateaCadena("", 22, false);
							fIni = fechaIni;
							fFin = fechaFin;
							fAct = fIni;
							fReal = fechaIni;
							fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;//dd/mm/yyyy
							String texto="";
							while (fAct.compareTo(fFin) <= 0) {	
								if(indicador.equals("h")){
									if(detalleCategoria.get("horas&"+fReal+"&"+codMov) != null){
										texto=detalleCategoria.get("horas&"+fReal+"&"+codMov).toString();																	
									}else{
										texto="0.0";
									}	
								}else {							
									if(detalleCategoria.get("dias&"+fReal+"&"+codMov) != null){
										texto=detalleCategoria.get("dias&"+fReal+"&"+codMov).toString();																	
									}else{
										texto="0.0";
									}	
								}															
								linea = linea + Utiles.formateaCadena(texto, 12, true);
								if (fReal.indexOf("/")>3){ 
							  		fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
								}
								fReal = Utiles.dameFechaSiguiente(fReal, 1);
								fAct = Utiles.toYYYYMMDD(fReal);
							}
							texto="";
							if(indicador.equals("h")){
								if(detalleCategoria.get("horasPeriodo&"+codMov) != null){
									texto=detalleCategoria.get("horasPeriodo&"+codMov).toString();																
								}else{
									texto="0.0";
								}	
							}else {						
								if(detalleCategoria.get("diasPeriodo&"+codMov) != null){
									texto=detalleCategoria.get("diasPeriodo&"+codMov).toString();																
								}else{
									texto="0.0";
								}	
							}													
							linea = linea + Utiles.formateaCadena(texto, 20, true);
							this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO",params, usuario);	
							
						} else {
							String textoo="";
							linea = Utiles.formateaCadena("", 22, false);
							fIni = fechaIni;
							fFin = fechaFin;
							fAct = fIni;
							fReal = fechaIni;
							fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;//dd/mm/yyyy							
							while (fAct.compareTo(fFin) <= 0) {	
								textoo="0.0";																		
								linea = linea + Utiles.formateaCadena(textoo, 12, true);	
								if (fReal.indexOf("/")>3){ 
							  		fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
								}
								fReal = Utiles.dameFechaSiguiente(fReal, 1);
								fAct = Utiles.toYYYYMMDD(fReal);
							}
							textoo="0.0";																
							linea = linea + Utiles.formateaCadena(textoo,20, true);							
							this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO",params, usuario);
						}						
					}// fin for c/movimiento	
					linea = Utiles.formateaCadena("", 5, false) + "Total";
					this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);					
					
					//Total por dia del periodo por Categoria
					linea = Utiles.formateaCadena("", 22, false);
					fIni = fechaIni;
					fFin = fechaFin;
					fAct = fIni;
					fReal = fechaIni;
					fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
					String texto1="";
					while (fAct.compareTo(fFin) <= 0) {
						if(indicador.equals("h")){
							if(detalleCategoria.get("horas&"+fReal) != null){
								texto1=detalleCategoria.get("horas&"+fReal).toString();											
							}else{
								texto1="0.0";
							}		
						}else {					
							if(detalleCategoria.get("dias&"+fReal) != null){
								texto1=detalleCategoria.get("dias&"+fReal).toString();											
							}else{
								texto1="0.0";
							}		
						}										
						linea = linea + Utiles.formateaCadena(texto1,12, true);
						if (fReal.indexOf("/")>3){ 
					  		fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
						}
						fReal = Utiles.dameFechaSiguiente(fReal, 1);
						fAct = Utiles.toYYYYMMDD(fReal);
					}
					texto1="";
					if(indicador.equals("h")){
						if(detalleCategoria.get("horasPeriodo") != null){
							texto1=detalleCategoria.get("horasPeriodo").toString();																
						}else{
							texto1="0.0";
						}
					}else {					
						if(detalleCategoria.get("diasPeriodo") != null){
							texto1=detalleCategoria.get("diasPeriodo").toString();																
						}else{
							texto1="0.0";
						}
					}					
					linea = linea + Utiles.formateaCadena(texto1, 20, true);
					this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO",params, usuario);	
					this.escribe("", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);
					this.escribe("", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);					
				}//fin for c/categoria
			}else{				
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad: "+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena(" Periodo: "+periodo, 30, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena(" Indicador: "+(indicador.equals("h")?"Horas":"Dias"), 30, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);

				this.escribe("", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);			

				linea = Utiles.formateaCadena("Categoria/Movimiento", 22, false);//antes true
				String fIni = fechaIni;
				String fFin = fechaFin;
				String fAct = fIni;
				String fReal = fechaIni;
	        	fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
				while (fAct.compareTo(fFin) <= 0) {
					linea = linea + Utiles.formateaCadena(fReal.substring(0, 2),12,true); //escribe los dias del periodo
					if (fReal.indexOf("/")>3){ 
				  		fReal = fReal.substring(8,10) + "/" + fReal.substring(5,7) + "/" + fReal.substring(0,4) ;
					}
					fReal = Utiles.dameFechaSiguiente(fReal, 1);
					fAct = Utiles.toYYYYMMDD(fReal);
				}				
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);
				this.escribe("", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);
				
				linea = Utiles.formateaCadena("", 2, false) + "No se encontraron registros que cumplan los criterios de búsqueda ingresados";
				this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);
			}
		} catch (Exception e) {
			
			linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
			+ Utiles.formateaCadena("Regimen/Modalidad: "+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
			+ Utiles.formateaCadena(" Periodo: "+periodo, 30, true)				
			+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
			+ Utiles.formateaCadena(" Indicador: "+(indicador.equals("h")?"Horas":"Dias"), 30, true);
			if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
			this.escribe(linea, "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);		
			this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);
			
			this.escribe("Error : " + e.getMessage(),"EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/** 
	 * Metodo encargado de obtener el reporte de gestion masivo mensual por movimiento	
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */	
	public void masivoMensualMovimiento(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String nombreCategoria = "";		
		ArrayList movimientos = null;
		String nombreMovimiento = "";
		String codMovimiento = "";		
		String anio = (String) params.get("anio");
		String regimen = (String) params.get("regimen");
		String indicador = (String) params.get("indicador");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String nombre_inte = (String) params.get("nombre_inte");		
		ArrayList listaPeriodos = (ArrayList) params.get("listaPeriodos");
				
		try {

			super.creaReporte(id, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			List detalleReporte = facadeRemote.resumenMensualMovimiento(params); //Metodo de ReporteFacade			

			if (detalleReporte != null && detalleReporte.size()>0) {
				log.debug("detalleReporte: " + detalleReporte);
				
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad: "+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena(" Año: "+anio, 30, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena(" Indicador: "+(indicador.equals("h")?"Horas":"Dias"), 30, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);

				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);			

				linea = Utiles.formateaCadena("Categoria/Movimiento", 22, false);//antes true
				for (int p=0; p<listaPeriodos.size(); p++) {	
	        		BeanPeriodo bPer = (BeanPeriodo) listaPeriodos.get(p);
	        		String codPer = bPer.getPeriodo().trim();
					linea = linea + Utiles.formateaCadena(codPer,12,true); //escribe los periodos	        		
				}				
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);

				BeanReporte quiebre = null;
				HashMap detalleCategoria = null;
				HashMap movimiento = null;				
				for (int i = 0; i < detalleReporte.size(); i++) {//c/categoria

					this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);
					quiebre = (BeanReporte) detalleReporte.get(i);
					detalleCategoria = quiebre.getMap();//Todos los valores del mapa					
					nombreCategoria = quiebre.getDescripcion().trim();
					
					//Quiebre por categoria..					
					linea = Utiles.formateaCadena("", 2, false) + nombreCategoria.toUpperCase(); //icapunay 25/11/2011 - conversion a mayuscula
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);
						
					movimientos = quiebre.getDetalle(); //Movimientos de la Categoria					
					for (int j=0; j<movimientos.size(); j++){//c/movimiento
						movimiento = (HashMap) movimientos.get(j);			
						codMovimiento = movimiento.get("cod_mov").toString().trim();	
						nombreMovimiento = (String)movimiento.get("descrip");
						linea = Utiles.formateaCadena("", 5, false) + codMovimiento+" "+nombreMovimiento+" ";
						this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);					
						
						if (!detalleCategoria.isEmpty()) {
							linea = Utiles.formateaCadena("", 22, false);							
							String texto="";
							for (int q=0; q<listaPeriodos.size(); q++) {	
				        		BeanPeriodo bPer2 = (BeanPeriodo) listaPeriodos.get(q);
				        		String codPer2 = bPer2.getPeriodo().trim();	
				        		if(indicador.equals("h")){
				        			if(detalleCategoria.get("horas&"+codMovimiento+"&"+codPer2) != null){
										texto=detalleCategoria.get("horas&"+codMovimiento+"&"+codPer2).toString();																	
									}else{
										texto="0.0";
									}	
				        		}else {
				        			if(detalleCategoria.get("dias&"+codMovimiento+"&"+codPer2) != null){
										texto=detalleCategoria.get("dias&"+codMovimiento+"&"+codPer2).toString();																	
									}else{
										texto="0.0";
									}	
				        		}																
								linea = linea + Utiles.formateaCadena(texto, 12, true);								
							}//fin for
							texto="";
							if(indicador.equals("h")){
								if(detalleCategoria.get("horasAnual&"+codMovimiento) != null){
									texto=detalleCategoria.get("horasAnual&"+codMovimiento).toString();																
								}else{
									texto="0.0";
								}	
							}else {							
								if(detalleCategoria.get("diasAnual&"+codMovimiento) != null){
									texto=detalleCategoria.get("diasAnual&"+codMovimiento).toString();																
								}else{
									texto="0.0";
								}	
							}														
							linea = linea + Utiles.formateaCadena(texto, 20, true);
							this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO",params, usuario);							
						} else {
							linea = Utiles.formateaCadena("", 22, false);					
							String textoo="";
							for (int ro=0; ro<listaPeriodos.size(); ro++) {	
				        		textoo="0.0";															
								linea = linea + Utiles.formateaCadena(textoo, 12, true);						
							}
							textoo="0.0";						
							linea = linea + Utiles.formateaCadena(textoo, 20, true);
							this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO",params, usuario);
						}						
					}// fin for c/movimiento	
					linea = Utiles.formateaCadena("", 5, false) + "Total";
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);					
					
					//Total por periodo de la Categoria
					linea = Utiles.formateaCadena("", 22, false);					
					String texto1="";
					for (int r=0; r<listaPeriodos.size(); r++) {	
		        		BeanPeriodo bPer3 = (BeanPeriodo) listaPeriodos.get(r);
		        		String codPer3 = bPer3.getPeriodo().trim();
		        		if(indicador.equals("h")){
		        			if(detalleCategoria.get("horas&"+codPer3) != null){
								texto1=detalleCategoria.get("horas&"+codPer3).toString();											
							}else{
								texto1="0.0";
							}	
		        		}else {		        		
		        			if(detalleCategoria.get("dias&"+codPer3) != null){
								texto1=detalleCategoria.get("dias&"+codPer3).toString();											
							}else{
								texto1="0.0";
							}	
		        		}												
						linea = linea + Utiles.formateaCadena(texto1, 12, true);						
					}
					texto1="";
					if(indicador.equals("h")){
						if(detalleCategoria.get("horasAnual") != null){
							texto1=detalleCategoria.get("horasAnual").toString();																
						}else{
							texto1="0.0";
						}
					}else {				
						if(detalleCategoria.get("diasAnual") != null){
							texto1=detalleCategoria.get("diasAnual").toString();																
						}else{
							texto1="0.0";
						}
					}						
					linea = linea + Utiles.formateaCadena(texto1, 20, true);
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO",params, usuario);	
					this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);
					this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);					
				}//fin for c/categoria
			}else{
				
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad: "+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena(" Año: "+anio, 30, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena(" Indicador: "+(indicador.equals("h")?"Horas":"Dias"), 30, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);

				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);			

				linea = Utiles.formateaCadena("Categoria/Movimiento", 22, false);//antes true
				if(listaPeriodos!=null && listaPeriodos.size()>0){//si existen periodos
					for (int p=0; p<listaPeriodos.size(); p++) {	
		        		BeanPeriodo bPer = (BeanPeriodo) listaPeriodos.get(p);
		        		String codPer = bPer.getPeriodo().trim();
						linea = linea + Utiles.formateaCadena(codPer,12,true); //escribe los periodos					
					}	
				}				
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);
				
				linea = Utiles.formateaCadena("", 2, false) + "No se encontraron registros que cumplan los criterios de búsqueda ingresados";
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);
			}
		} catch (Exception e) {
			
			linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
			+ Utiles.formateaCadena("Regimen/Modalidad: "+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
			+ Utiles.formateaCadena(" Año: "+anio, 30, true)				
			+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
			+ Utiles.formateaCadena(" Indicador: "+(indicador.equals("h")?"Horas":"Dias"), 30, true);
			if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
			this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);		
			this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);
			this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params,usuario);
			
			this.escribe("Error : " + e.getMessage(),"EVOLUCION MENSUAL DE ASISTENCIA POR MOVIMIENTO", params, usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/** 
	 * Metodo encargado de obtener el reporte de gestion masivo mensual por unidad organica	
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */	
	public void masivoMensualUUOO(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String nombreIntendencia = "";		
		ArrayList subUnidades = null;		
		String codSubuo = "";
		String desSubuo = "";
		String texto="";
		String texto1="";			
		String anio = (String) params.get("anio");
		String regimen = (String) params.get("regimen");
		String indicador = ((String) params.get("indicador")).trim();
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");	
		String nombre_inte = (String) params.get("nombre_inte");		
		ArrayList listaPeriodos = (ArrayList) params.get("listaPeriodos");
		String cod_cate = (String) params.get("cod_cate");
		String nombre_cate = (String) params.get("nombre_cate");		
		
		try {
			super.creaReporte(id, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();			
			List detalleReporte = facadeRemote.resumenMensualUUOO(params); //Metodo: resumenMensualUUOO de ReporteFacade
			if (detalleReporte != null && detalleReporte.size()>0) {
				log.debug("detalleReporte: " + detalleReporte);//intendencias				
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad:"+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena("Año:"+anio+" ", 15, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena("Indicador:"+(indicador.equals("dc")?"Días por colaborador":indicador.equals("nc")?"Número de colaboradores":indicador.equals("d")?"Días":"Horas")+" ", 15, true)
				+ Utiles.formateaCadena("Categoria:"+cod_cate+"-"+nombre_cate, 15, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);

				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);	
				linea = Utiles.formateaCadena("Intendencia/U.Orgánica", 22, false);
				for (int p=0; p<listaPeriodos.size(); p++) {	
	        		BeanPeriodo bPer = (BeanPeriodo) listaPeriodos.get(p);
	        		String codPer = bPer.getPeriodo().toString().trim();
					linea = linea + Utiles.formateaCadena(codPer,12,true); //escribe los periodos					
				}
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);							
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);

				BeanReporte quiebre = null;
				HashMap detalleInten = null;
				HashMap subuuoo = null;				
				for (int i = 0; i < detalleReporte.size(); i++) {//c/intendencia(unidad)

					this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
					quiebre = (BeanReporte) detalleReporte.get(i); //Intendencia(unidad)	
					detalleInten = quiebre.getMap();//Obteniendo todo el Mapa de la Intendencia(unidad)
					subUnidades = quiebre.getDetalle(); //subUnidades de la Intendencia(unidad)	
					nombreIntendencia = quiebre.getDescripcion();
					
					//Quiebre por intendencia..					
					linea = Utiles.formateaCadena("", 2, false) + nombreIntendencia;
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);						
					for (int u=0; u<subUnidades.size(); u++){//c/subUnidad
						subuuoo = (HashMap) subUnidades.get(u);			
						codSubuo = subuuoo.get("t12cod_uorga").toString().trim();	
						desSubuo = (String)subuuoo.get("t12des_corta");						
						linea = Utiles.formateaCadena("", 5, false) + codSubuo+" "+desSubuo+" ";
						this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
						if (detalleInten!=null && detalleInten.size()>0) {
							linea = Utiles.formateaCadena("", 22, false);							
							texto="";
							for (int p1=0; p1<listaPeriodos.size(); p1++) {	
								BeanPeriodo bPer1 = (BeanPeriodo) listaPeriodos.get(p1);
								String codPer1 = bPer1.getPeriodo().toString().trim();								
								if(indicador.equals("h")){
									texto=detalleInten.get("horas&"+codPer1+"&"+codSubuo)!=null?(String)detalleInten.get("horas&"+codPer1+"&"+codSubuo):"0.0";
								}else if(indicador.equals("d")){
									texto=detalleInten.get("dias&"+codPer1+"&"+codSubuo)!=null?(String)detalleInten.get("dias&"+codPer1+"&"+codSubuo):"0.0";
								}else if(indicador.equals("nc")){
									texto=detalleInten.get("nc&"+codPer1+"&"+codSubuo)!=null?(String)detalleInten.get("nc&"+codPer1+"&"+codSubuo):"0";
								}else if(indicador.equals("dc")){
									texto=detalleInten.get("dc&"+codPer1+"&"+codSubuo)!=null?(String)detalleInten.get("dc&"+codPer1+"&"+codSubuo):"0.00";
								}																								
								linea = linea + Utiles.formateaCadena(texto, 12, true);								
							}//fin for listaPeriodos p1
							if(indicador.equals("h")){
								texto1=detalleInten.get("horasAnual&"+codSubuo)!=null?(String)detalleInten.get("horasAnual&"+codSubuo):"0.0";
							}else if(indicador.equals("d")){
								texto1=detalleInten.get("diasAnual&"+codSubuo)!=null?(String)detalleInten.get("diasAnual&"+codSubuo):"0.0";
							}else if(indicador.equals("nc")){
								texto1=detalleInten.get("ncAnual&"+codSubuo)!=null?(String)detalleInten.get("ncAnual&"+codSubuo):"0";
							}else if(indicador.equals("dc")){
								texto1=detalleInten.get("dcAnual&"+codSubuo)!=null?(String)detalleInten.get("dcAnual&"+codSubuo):"0.00";
							}	
							linea = linea + Utiles.formateaCadena(texto1, 20, true);
							this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA",params, usuario);	
						} else {
							linea = Utiles.formateaCadena("", 22, false);							
							for (int p2=0; p2<listaPeriodos.size(); p2++) {	
								texto =(indicador.equals("h") || indicador.equals("d"))?"0.0":indicador.equals("nc")?"0":"0.00";													
								linea = linea + Utiles.formateaCadena(texto, 12, true);								
							}//fin for listaPeriodos p2
							texto1 =(indicador.equals("h") || indicador.equals("d"))?"0.0":indicador.equals("nc")?"0":"0.00";																
							linea = linea + Utiles.formateaCadena(texto1, 20, true);
							this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA",params, usuario);															
						}//fin detalleInten.size()>0						
					}// fin for c/subUnidad	
					linea = Utiles.formateaCadena("", 5, false) + "Total";
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
					
					//Total de la intendencia por periodo
					linea = Utiles.formateaCadena("", 22, false);					
					texto="";
					for (int p3=0; p3<listaPeriodos.size(); p3++) {	
						BeanPeriodo bPer3 = (BeanPeriodo) listaPeriodos.get(p3);
						String codPer3 = bPer3.getPeriodo().toString().trim();
						if(indicador.equals("h")){
							texto=detalleInten.get("horas&"+codPer3)!=null?(String)detalleInten.get("horas&"+codPer3):"0.0";
						}else if(indicador.equals("d")){
							texto=detalleInten.get("dias&"+codPer3)!=null?(String)detalleInten.get("dias&"+codPer3):"0.0";
						}else if(indicador.equals("nc")){
							texto=detalleInten.get("nc&"+codPer3)!=null?(String)detalleInten.get("nc&"+codPer3):"0";
						}else if(indicador.equals("dc")){
							texto=detalleInten.get("dc&"+codPer3)!=null?(String)detalleInten.get("dc&"+codPer3):"0.00";
						}						
						linea = linea + Utiles.formateaCadena(texto, 12, true);						
					} //fin for
					//fin Total de la intendencia por periodo
					
					//Total anual de la intendencia
					if(indicador.equals("h")){
						texto1=detalleInten.get("horasAnual")!=null?(String)detalleInten.get("horasAnual"):"0.0";
					}else if(indicador.equals("d")){
						texto1=detalleInten.get("diasAnual")!=null?(String)detalleInten.get("diasAnual"):"0.0";
					}else if(indicador.equals("nc")){
						texto1=detalleInten.get("ncAnual")!=null?(String)detalleInten.get("ncAnual"):"0";
					}else if(indicador.equals("dc")){
						texto1=detalleInten.get("dcAnual")!=null?(String)detalleInten.get("dcAnual"):"0.00";
					}	
					linea = linea + Utiles.formateaCadena(texto1, 20, true);
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA",params, usuario);			
					//fin Total anual de la intendencia		
					
					this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
					this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
					
				}//fin for c/intendencia
			}else{
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad:"+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena("Año:"+anio+" ", 15, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena("Indicador:"+(indicador.equals("dc")?"Días por colaborador":indicador.equals("nc")?"Número de colaboradores":indicador.equals("d")?"Días":"Horas")+" ", 15, true)
				+ Utiles.formateaCadena("Categoria:"+cod_cate+"-"+nombre_cate, 15, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);	
				linea = Utiles.formateaCadena("Intendencia/U.Orgánica", 22, false);//antes true				
				if(listaPeriodos!=null && listaPeriodos.size()>0){//si existen periodos
					for (int p=0; p<listaPeriodos.size(); p++) {	
		        		BeanPeriodo bPer = (BeanPeriodo) listaPeriodos.get(p);
		        		String codPer = bPer.getPeriodo().trim();
						linea = linea + Utiles.formateaCadena(codPer,12,true); //escribe los periodos					
					}	
				}	
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);				
				linea = Utiles.formateaCadena("", 2, false) + "No se encontraron registros que cumplan los criterios de búsqueda ingresados";
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);
			}
			
		} catch (Exception e) {
			
			linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
			+ Utiles.formateaCadena("Regimen/Modalidad:"+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
			+ Utiles.formateaCadena("Año:"+anio+" ", 15, true)				
			+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
			+ Utiles.formateaCadena("Indicador:"+(indicador.equals("dc")?"Días por colaborador":indicador.equals("nc")?"Número de colaboradores":indicador.equals("d")?"Días":"Horas")+" ", 15, true)
			+ Utiles.formateaCadena("Categoria:"+cod_cate+"-"+nombre_cate, 15, true);
			if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
			this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);		
			this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION DIARIA DE ASISTENCIA POR MOVIMIENTO", params, usuario);
			this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params,usuario);			
			this.escribe("Error : " + e.getMessage(),"EVOLUCION MENSUAL DE ASISTENCIA POR UNIDAD ORGANICA", params, usuario);
			log.debug("Error : " + e.getMessage());
			
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}

	
	/** 
	 * Metodo encargado de obtener el reporte de gestion masivo mensual por colaborador	
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */	
	public void masivoMensualColaborador(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String texto="";
		String nombreColaborador = "";	
		//int contMismaUUOO=1; //contador de trabajadores de la misma UUOO
		String uuooColab=null;
		//String uuooColabo_J=null;		
		
		String anio = (String) params.get("anio");
		String regimen = (String) params.get("regimen");
		String indicador = (String) params.get("indicador");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String nombre_inte = (String) params.get("nombre_inte");		
		String cod_cate = ((String) params.get("cod_cate")).trim();
		String nombre_cate = (String) params.get("nombre_cate");
		ArrayList listaPeriodos = (ArrayList) params.get("listaPeriodos");	
		
		try {
			super.creaReporte(id, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			List detalleReporte = facadeRemote.resumenMensualColaborador(params); //Metodo: resumenMensualColaborador de ReporteFacade / Lista de Colaboradores ordenados por UO ascendente y REGISTRO ascendente	
			if (detalleReporte != null && detalleReporte.size()>0) {
				log.debug("detalleReporte: " + detalleReporte);
				
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad:"+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena("Año:"+anio+" ", 15, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena("Indicador:"+(indicador.equals("h")?"Horas":"Dias")+" ", 15, true)
				+ Utiles.formateaCadena("Categoria:"+cod_cate+"-"+nombre_cate, 15, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				linea = Utiles.formateaCadena("U.Organica/Colaborador", 25, false);				
				for (int p=0; p<listaPeriodos.size(); p++) {	
	        		BeanPeriodo bPer = (BeanPeriodo) listaPeriodos.get(p);
	        		String codPer = bPer.getPeriodo().trim();
					linea = linea + Utiles.formateaCadena(codPer,12,true); //escribe los periodos					
				}				
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);

				BeanReporte quiebre = null;
				//BeanReporte quiebre_J = null;
				HashMap detalleColaborador = null;								
				for (int i = 0; i <= detalleReporte.size()-1; i++) {//c/colaborador
					this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
					quiebre = (BeanReporte) detalleReporte.get(i);
					detalleColaborador = quiebre.getMap();//Todos los valores del mapa					
					nombreColaborador = quiebre.getNombre();
					uuooColab = quiebre.getUnidad().trim(); //2A5500					
					//Impresion de la unidad organica del primer registro
					/*
					if(i==0){
						linea = quiebre.getDescripcion();
						this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
						this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
					}*/										
					//Quiebre por colaborador..					
					//linea = Utiles.formateaCadena("", 2, false) + nombreColaborador;
					linea = Utiles.formateaCadena("", 2, false) + uuooColab + "  " + nombreColaborador;
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
					
					//valores c/periodo y valor anual
					linea = Utiles.formateaCadena("", 25, false);					
					for (int q=0; q<listaPeriodos.size(); q++) {//c/periodo del colaborador	
						BeanPeriodo bPer2 = (BeanPeriodo) listaPeriodos.get(q);
						String codPer2 = bPer2.getPeriodo().trim();		
						if(indicador.equals("h")){
							if(detalleColaborador.get("horas&"+codPer2) != null){
								texto=detalleColaborador.get("horas&"+codPer2).toString();																	
							}else{
								texto="0.0";
							}
						}else {
							if(detalleColaborador.get("dias&"+codPer2) != null){
								texto=detalleColaborador.get("dias&"+codPer2).toString();																	
							}else{
								texto="0.0";
							}
						}	
						linea = linea + Utiles.formateaCadena(texto, 12, true);								
					}//fin for c/periodo del colaborador
					if(indicador.equals("h")){
						if(detalleColaborador.get("horasAnual") != null){
							texto=detalleColaborador.get("horasAnual").toString();																
						}else{
							texto="0.0";
						}	
					}else {
						if(detalleColaborador.get("diasAnual") != null){
							texto=detalleColaborador.get("diasAnual").toString();																
						}else{
							texto="0.0";
						}	
					}	
					linea = linea + Utiles.formateaCadena(texto, 20, true);
					this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR",params, usuario);					
					//fin valores c/periodo y valor anual					
					/*
					if(i<detalleReporte.size()-1){
						quiebre_J = (BeanReporte) detalleReporte.get(i+1);
						uuooColabo_J = quiebre_J.getUnidad().trim();						
						if(uuooColabo_J.equals(uuooColab)){
							contMismaUUOO=contMismaUUOO+1;
						}else{
							//codigo de impresion en el archivo (total de colaboradores por UO)
							this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
							this.escribe("Total"+" "+uuooColab+" =  "+String.valueOf(contMismaUUOO), "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
							this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
							this.escribe(" ", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
							linea = quiebre_J.getDescripcion() ;
							this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
							contMismaUUOO=1;
						}						
					}else{
						//codigo de impresion en el archivo (ultimo total de colaboradores por UO)
						this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
						this.escribe("Total"+" "+uuooColab+" =  "+String.valueOf(contMismaUUOO), "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
						this.escribe(" ", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
						//codigo de impresion en el archivo (total general de colaboradores)
						this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
						this.escribe("Total General"+" =  "+String.valueOf(detalleReporte.size()+" colaboradores"), "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
					}*/
					//codigo de impresion en el archivo (total general de colaboradores)					
				}//fin for c/colaborador
				this.escribe(" ", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				this.escribe("Total General"+" =  "+String.valueOf(detalleReporte.size()+" colaboradores"), "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				
			}else{				
				linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
				+ Utiles.formateaCadena("Regimen/Modalidad:"+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
				+ Utiles.formateaCadena("Año:"+anio+" ", 15, true)				
				+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
				+ Utiles.formateaCadena("Indicador:"+(indicador.equals("h")?"Horas":"Dias")+" ", 15, true)
				+ Utiles.formateaCadena("Categoria:"+cod_cate+"-"+nombre_cate, 15, true);
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);			
				linea = Utiles.formateaCadena("U.Organica/Colaborador", 25, false);				
				if(listaPeriodos!=null && listaPeriodos.size()>0){//si existen periodos
					for (int p=0; p<listaPeriodos.size(); p++) {	
		        		BeanPeriodo bPer = (BeanPeriodo) listaPeriodos.get(p);
		        		String codPer = bPer.getPeriodo().trim();
						linea = linea + Utiles.formateaCadena(codPer,12,true); //escribe los periodos					
					}	
				}				
				linea = linea + Utiles.formateaCadena("Total", 20, true);
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
				linea = Utiles.formateaCadena("", 2, false) + "No se encontraron registros que cumplan los criterios de búsqueda ingresados";
				this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
			}
		} catch (Exception e) {			
			linea = Utiles.formateaCadena("Criterios de Busqueda:", 25, true)
			+ Utiles.formateaCadena("Regimen/Modalidad:"+(regimen.equals("0")?"D.L. 276 - 728":(regimen.equals("1")?"D.L. 1057":"Modalidad formativa")), 30, true)
			+ Utiles.formateaCadena("Año:"+anio+" ", 15, true)				
			+ Utiles.formateaCadena((criterio.equals("0")?"Registro: "+valor:(criterio.equals("1")?"UUOO: "+valor:(criterio.equals("4")?"Intendencia: "+nombre_inte+" "+valor:"Criterio:Institucional"))), 30, true)
			+ Utiles.formateaCadena("Indicador:"+(indicador.equals("h")?"Horas":"Dias")+" ", 15, true)
			+ Utiles.formateaCadena("Categoria:"+cod_cate+"-"+nombre_cate, 15, true);
			if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
			this.escribe(linea, "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);		
			this.escribe("------------------------------------------------------------------------------------------------------------------------", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
			this.escribe("", "EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params,usuario);
			this.escribe("Error : " + e.getMessage(),"EVOLUCION MENSUAL DE ASISTENCIA POR COLABORADOR", params, usuario);
			log.debug("Error : " + e.getMessage());
			
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}	
	/*FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	
	
	/**NVILLAR 24/04/2012
	 */
	/*public void laborExcepcional1(HashMap params, String usuario)
			throws RemoteException {
		log.debug("Ingreso a REPORTE MASIVO FACADE:" );
		log.debug("laborExcepcional - params:" + params);
		log.debug("laborExcepcional - usuario:" + usuario);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String codigo = (String) params.get("codigo");
		String nombre = (String) params.get("nombCompleto");
		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
		String labor = (String) params.get("labor");
		String titulo = "";
		
		try {
			if(labor.equals("Labor Excepcional")){
				titulo = "SOLICITUDES DE LABOR EXCEPCIONAL";
			}
			if(labor.equals("Compensacion")){
				titulo = "SOLICITUDES DE COMPENSACION";
			}
			
			super.creaReporte(id, titulo, params, usuario); 
			
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			List reporte = facadeRemote.resumenSolLabExcepcional(params);
			log.debug("reporte:" + reporte);

			if (reporte != null) {
				linea = Utiles.formateaCadena(" Criterios: ", 12, true)				
				+ Utiles.formateaCadena(((!codigo.equals("") && codigo!=null)?" Registro: "+codigo+" "+nombre :""), 40, true)				
				+ Utiles.formateaCadena(((!fechaIni.equals("") && fechaIni!=null)?" Inicio: "+fechaIni:""), 20, true)
				+ Utiles.formateaCadena(((!fechaFin.equals("") && fechaFin!=null)?" Fin: "+fechaFin:""), 20, true)
				+ Utiles.formateaCadena(((!labor.equals("") && labor!=null)?" Tipo: "+labor:""), 30, true);
				
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				
				this.escribe(linea, titulo, params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------------------", titulo, params, usuario);
				
				if (log.isDebugEnabled()) log.debug("POSTA 1: ");			
				linea = Utiles.formateaCadena("No Solicitud", 15, true)
						+ Utiles.formateaCadena("Asunto", 26, true)
						+ Utiles.formateaCadena("Fecha Sol.", 12, true)
						+ Utiles.formateaCadena("Estado", 15, true);
				if(labor.equals("Labor Excepcional")){
				linea = linea 
						+ Utiles.formateaCadena("Fec.Permiso", 15, true)
						+ Utiles.formateaCadena("Inicio", 10, true)
						+ Utiles.formateaCadena("Fin", 10, true)
						+ Utiles.formateaCadena("Sustento", 20, true);
				}
				if(labor.equals("Compensacion")){
				linea = linea 
						+ Utiles.formateaCadena("Fec.Compensacion", 15, true)
						+ Utiles.formateaCadena("Observacion", 20, true);
				}
				
				this.escribe(linea, titulo, params, usuario);
				if (log.isDebugEnabled()) log.debug("POSTA 2: ");

				Map quiebre = null;
				ArrayList detalles = null;

				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (HashMap)reporte.get(i);			
					detalles = (ArrayList)quiebre.get("listaDetalle");

					if (log.isDebugEnabled()) log.debug("quiebre: "+quiebre);
					if (log.isDebugEnabled()) log.debug("detalles: "+detalles);

					if (quiebre!= null && quiebre.size() > 0) {
						
						Map detalle = null;
						
						for (int j = 0; j < detalles.size(); j++) {
							detalle = (HashMap)detalles.get(j);
							
							if(j==0){
								linea = Utiles.formateaCadena("", 2, true) +
								Utiles.formateaCadena(quiebre.get("anio").toString()+ " - " +quiebre.get("numero").toString().trim(), 15, false) +
								Utiles.formateaCadena(quiebre.get("asunto").toString().trim(), 26, false) +
					 			Utiles.formateaCadena(quiebre.get("fecha").toString().trim(), 12, false) +												
								Utiles.formateaCadena(quiebre.get("det_estado").toString().trim(), 15, false);
							}else {
								linea = Utiles.formateaCadena("", 70, true);
							}
							if (detalle!= null && detalle.size() > 0) {
								if(labor.equals("Labor Excepcional")){
									if (log.isDebugEnabled()) log.debug("LABOR EXCEPCIONAL: ");
									linea = linea +							
											Utiles.formateaCadena(detalle.get("fec_permiso_desc")!=null?detalle.get("fec_permiso_desc").toString().trim():"", 15, false) +
											Utiles.formateaCadena(detalle.get("hora_inicio")!=null?detalle.get("hora_inicio").toString().trim():"", 10, false) +
								 			Utiles.formateaCadena(detalle.get("hora_fin")!=null?detalle.get("hora_fin").toString().trim():"", 10, false) +
											Utiles.formateaCadena(detalle.get("sustento")!=null?detalle.get("sustento").toString().trim():"", 20, false);
								}
								if(labor.equals("Compensacion")){
									if (log.isDebugEnabled()) log.debug("COMPENSACION: ");
									linea = linea+ 
									        Utiles.formateaCadena(detalle.get("fec_permiso_desc")!=null?detalle.get("fec_permiso_desc").toString().trim():"", 15, false)+
											Utiles.formateaCadena(detalle.get("sustento")!=null?detalle.get("sustento").toString().trim():"", 20, false);
								}
								
							}
							this.escribe(linea, titulo, params, usuario);

						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "No hay un detalle DE LABOR EXCEPCIONAL para el trabajador";
						this.escribe(linea, titulo, params, usuario);
					}
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), titulo, params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}*/
	
	/**NVILLAR 03/05/2012
	 * Metdo encargado 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void laborExcepcional2(HashMap params, String usuario)
			throws RemoteException {
		log.debug("Ingreso a REPORTE MASIVO FACADE:" );
		log.debug("laborExcepcional - params:" + params);
		log.debug("laborExcepcional - usuario:" + usuario);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String regimen = (String) params.get("regimen");
		String codigo = (String) params.get("codigo");
		String nombre = (String) params.get("nombCompleto");
		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
		String indicador = (String) params.get("indicador");
		String criterio = (String) params.get("criterio");
		
		String valor =  ( params.get("valor").toString()!= null )?(String) params.get("valor"):"";
		
		if (criterio.trim().equals("5")) { // Intendencia
			String nombre_inte =  ( params.get("nombre_inte").toString()!= null )?(String) params.get("nombre_inte"):"";
			log.debug("nombre_inte:" + nombre_inte);
			valor = nombre_inte;
        }
		
		log.debug("criterio:" + criterio);
		log.debug("valor:" + valor);
		
		String tipoSol= "";
		String titulo = "";

		try {
			if (indicador.equals("1")){
				tipoSol="Labor Excepcional y Compensacion";
				titulo = "SOLICITUDES DE LABOR EXCEPCIONAL Y COMPENSACIONES";
			}
			if (indicador.equals("2")){
				tipoSol="Labor Excepcional";
				titulo = "SOLICITUDES DE LABOR EXCEPCIONAL";
			}
			if (indicador.equals("3")){
				tipoSol="Compensacion";
				titulo = "SOLICITUDES DE COMPENSACION";
			}
			
			super.creaReporte(id, titulo, params, usuario); 
			
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			List reporte = facadeRemote.resumenSolLabExcepcional2(params);
			log.debug("reporte:" + reporte);
            
			if(regimen.trim().equals("0")){
            	regimen = "D.L.276-728";
			}
			if(regimen.trim().equals("1")){
				regimen = "D.L.1057";
			}
			if(regimen.trim().equals("2")){
				regimen = "Modalidades formativas";
			}
            
            if(criterio.trim().equals("0")){
            	criterio = "Registro";
            	valor =codigo+""+nombre;
			}
			if(criterio.trim().equals("1")){
				criterio = "Und.Organizacional";
			}
			if(criterio.trim().equals("5")){
				criterio = "Intendencia";
			}
			if(criterio.trim().equals("4")){
				criterio = "Criterio";
				valor="Institucional";
			}
			
			if (reporte != null) {
				linea = Utiles.formateaCadena(" Criterios: ", 12, true)	
				+ Utiles.formateaCadena(((!regimen.equals("") && regimen!=null)?" Regime/Modalidad: "+regimen:""), 40, true)
				
				+ Utiles.formateaCadena(((!valor.equals("") && valor!=null)? " "+criterio+": "+valor:""), 25, true)
				
				+ Utiles.formateaCadena(((!fechaIni.equals("") && fechaIni!=null)?" Inicio: "+fechaIni:""), 20, true)
				+ Utiles.formateaCadena(((!fechaFin.equals("") && fechaFin!=null)?" Fin: "+fechaFin:""), 20, true)
				+ Utiles.formateaCadena(((!indicador.equals("") && indicador!=null)?" Tipo: "+tipoSol:""), 30, true);
				
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, titulo, params, usuario);		
				this.escribe("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------", titulo, params, usuario);	
				linea = Utiles.formateaCadena("Und.Org.", 10, true)
						+ Utiles.formateaCadena("Registro", 8, true)
						+ Utiles.formateaCadena("No Solicitud", 15, true)
						+ Utiles.formateaCadena("Asunto", 26, true)
						+ Utiles.formateaCadena("Fecha Sol.", 12, true)
						+ Utiles.formateaCadena("Estado", 15, true);
				if(indicador.equals("1")||indicador.equals("2")){
					linea = linea 
							+ Utiles.formateaCadena("Fec.Permiso", 15, true)
							+ Utiles.formateaCadena("Inicio", 10, true)
							+ Utiles.formateaCadena("Fin", 10, true)
							+ Utiles.formateaCadena("Sustento", 20, true);
				}
				if(indicador.equals("3")){
					linea = linea 
							+ Utiles.formateaCadena("Fec.Compensacion", 15, true)
							+ Utiles.formateaCadena("Observacion", 20, true);
				}

				this.escribe(linea, titulo, params, usuario);
				if (log.isDebugEnabled()) log.debug("POSTA 2: ");
				this.escribe("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------", titulo, params, usuario);
				Map quiebre = null;
				ArrayList detalles = null;

				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (HashMap)reporte.get(i);			
					detalles = (ArrayList)quiebre.get("listaDetalle");

					if (log.isDebugEnabled()) log.debug("POSTA 3: ");
					if (log.isDebugEnabled()) log.debug("quiebre: "+quiebre);
					if (log.isDebugEnabled()) log.debug("detalles: "+detalles);
					
					

					
					if (detalles != null && detalles.size() > 0) {
						
						Map detalle = null;
						
						for (int j = 0; j < detalles.size(); j++) {
							    
							if(j==0){
								linea = Utiles.formateaCadena("", 2, false) +
								Utiles.formateaCadena(quiebre.get("unidad").toString().trim(), 10, false) +
								Utiles.formateaCadena(quiebre.get("registro").toString().trim(), 8, false) +
								Utiles.formateaCadena(quiebre.get("anio").toString()+ " - " +quiebre.get("numero").toString().trim(), 15, false) +
								Utiles.formateaCadena(quiebre.get("asunto").toString().trim(), 26, false) +
					 			Utiles.formateaCadena(quiebre.get("fecha").toString().trim(), 12, false) +												
								Utiles.formateaCadena(quiebre.get("det_estado").toString().trim(), 15, false);
								
							}else {
								linea = Utiles.formateaCadena("", 88, false);
							}
							
							detalle = (HashMap)detalles.get(j);
							    if(indicador.equals("1")||indicador.equals("2")){
									if (log.isDebugEnabled()) log.debug("LABOR EXCEPCIONAL: ");
									linea = linea +							
											Utiles.formateaCadena(detalle.get("fec_permiso_desc")!=null?detalle.get("fec_permiso_desc").toString().trim():"", 15, false) +
											Utiles.formateaCadena(detalle.get("hora_inicio")!=null?detalle.get("hora_inicio").toString().trim():"", 10, false) +
								 			Utiles.formateaCadena(detalle.get("hora_fin")!=null?detalle.get("hora_fin").toString().trim():"", 10, false) +
											Utiles.formateaCadena(detalle.get("sustento")!=null?detalle.get("sustento").toString().trim():"", 20, false);
								}
								if(indicador.equals("3")){
									if (log.isDebugEnabled()) log.debug("COMPENSACION: ");
									linea = linea +
									        Utiles.formateaCadena(detalle.get("fec_permiso_desc")!=null?detalle.get("fec_permiso_desc").toString().trim():"", 15, false)+
											Utiles.formateaCadena(detalle.get("sustento")!=null?detalle.get("sustento").toString().trim():"", 20, false);
								}
								
								this.escribe(linea, titulo, params, usuario);

						}
					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ "No hay un detalle DE LABOR EXCEPCIONAL para el trabajador";
						this.escribe(linea, titulo, params, usuario);
					}
				}
				this.escribe("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------", titulo, params, usuario);
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), titulo, params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/**NVILLAR 04/05/2012
	 * Metdo encargado 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void consultaPermanencias(HashMap params, String usuario)
			throws RemoteException {
		log.debug("Ingreso a REPORTE MASIVO FACADE:" );
		log.debug("consultaPermanencias - params:" + params);
		log.debug("consultaPermanencias - usuario:" + usuario);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String regimen = (String) params.get("regimen");
		//String registro = (String) params.get("codPers");

		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		

		
		try {
			super.creaReporte(id, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario); 
			
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			//List reporte = facadeRemote.laborExcepcional(params, seguridad); resumenSolLabExcepcional
			List reporte = facadeRemote.resumenPermanenciaExcepcional(params);
			log.debug("reporte:" + reporte);

			if(regimen.trim().equals("0")){
            	regimen = "D.L.276-728";
			}
			if(regimen.trim().equals("1")){
				regimen = "D.L.1057";
			}
			if(regimen.trim().equals("2")){
				regimen = "Modalidades formativas";
			}
            
            if(criterio.trim().equals("0")){
            	criterio = "Registro";
			}
			if(criterio.trim().equals("1")){
				criterio = "Und.Organizacional";
			}
			
			if (reporte != null) {
				linea = Utiles.formateaCadena("Criterios: ", 12, true)				
				+ Utiles.formateaCadena(((!regimen.equals("") && regimen!=null)?" Regime/Modalidad: "+regimen:""), 40, true)				
				+ Utiles.formateaCadena(((!fechaIni.equals("") && fechaIni!=null)?" Inicio: "+fechaIni:""), 20, true)
				+ Utiles.formateaCadena(((!fechaFin.equals("") && fechaFin!=null)?" Fin: "+fechaFin:""), 20, true)
				+ Utiles.formateaCadena(((!valor.equals("") && valor!=null)? " "+criterio+": "+valor:""), 15, true);
				
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				
				if (log.isDebugEnabled()) log.debug("POSTA 1: ");			
				linea = Utiles.formateaCadena("Fecha", 12, true)
						+ Utiles.formateaCadena("Registro", 10, true)
						+ Utiles.formateaCadena("Nombre", 65, true)
						+ Utiles.formateaCadena("Inicio", 10, true)
						+ Utiles.formateaCadena("Fin", 10, true)
						+ Utiles.formateaCadena("Min. Saldo", 15, true)//ICAPUNAY 19/06 AGREGANDO SALDO
						+ Utiles.formateaCadena("No.Solicitud", 12, true)
						+ Utiles.formateaCadena("Estado", 10, true)
						+ Utiles.formateaCadena("Inicio", 10, true)
						+ Utiles.formateaCadena("Fin", 10, true)
				        + Utiles.formateaCadena("Minutos", 8, true);
				this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				if (log.isDebugEnabled()) log.debug("POSTA 2: ");
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				Map quiebre = null;
				ArrayList detalles = null;

				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (HashMap)reporte.get(i);			
					detalles = (ArrayList)quiebre.get("listaDetalle");
			        if (log.isDebugEnabled()) log.debug("POSTA 4: ");
					
					if (quiebre != null && quiebre.size() > 0) {
						
						Map detalle = null;
						
						if (detalles != null && detalles.size() > 0){
							for (int j = 0; j < detalles.size(); j++) {
							    detalle = (HashMap)detalles.get(j);
							    
							    if(j==0){
							    	linea = Utiles.formateaCadena("", 2, true) +
									Utiles.formateaCadena(quiebre.get("fecha_desc").toString().trim(), 12, false) +
									Utiles.formateaCadena(quiebre.get("registro").toString().trim(), 8, true) +
						 			Utiles.formateaCadena(quiebre.get("nombre").toString().trim(), 65, false) +
						 			Utiles.formateaCadena(quiebre.get("inicio").toString().trim(), 10, false) +
						 			//Utiles.formateaCadena(quiebre.get("fin").toString().trim(), 10, true) ; //ICAPUNAY 19/06 AGREGANDO SALDO
							    	Utiles.formateaCadena(quiebre.get("fin").toString().trim(), 10, true) + //ICAPUNAY 19/06 AGREGANDO SALDO
							    	Utiles.formateaCadena(quiebre.get("saldo").toString().trim(), 15, true); //ICAPUNAY 19/06 AGREGANDO SALDO
									//Utiles.formateaCadena(quiebre.get("hor_autorizada").toString().trim(), 10, false);
								}else {
									linea = Utiles.formateaCadena("", 112, true);
								}
							    
							    if (detalle!= null && detalle.size() > 0) {
							    
							    linea = linea +							
										Utiles.formateaCadena(detalle.get("solicitud").toString().trim(), 12, false) +
										Utiles.formateaCadena(detalle.get("estado2").toString(), 10, false) +
							 			Utiles.formateaCadena(detalle.get("hinicio").toString(), 10, false) +
							 			Utiles.formateaCadena(detalle.get("hfin").toString(), 10, false) +
										Utiles.formateaCadena(detalle.get("minutos").toString(), 8, false);
							    }
							    this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
							}
						} else {
							linea = Utiles.formateaCadena("", 2, true) +
							Utiles.formateaCadena(quiebre.get("fecha_desc").toString().trim(), 12, false) +
							Utiles.formateaCadena(quiebre.get("registro").toString().trim(), 8, true) +
				 			Utiles.formateaCadena(quiebre.get("nombre").toString().trim(), 65, false) +
				 			Utiles.formateaCadena(quiebre.get("inicio").toString().trim(), 10, false) +
				 			//Utiles.formateaCadena(quiebre.get("fin").toString().trim(), 10, true) ; //ICAPUNAY 19/06 AGREGANDO SALDO
					    	Utiles.formateaCadena(quiebre.get("fin").toString().trim(), 10, true) + //ICAPUNAY 19/06 AGREGANDO SALDO
					    	Utiles.formateaCadena(quiebre.get("saldo").toString().trim(), 15, true); //ICAPUNAY 19/06 AGREGANDO SALDO
							this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
						}

					} else {
						linea = Utiles.formateaCadena("", 30, false)
								+ " ";
						this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
					}
					
				}
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "PERMANENCIA DE LABOR EXCEPCIONAL", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	
	/**NVILLAR 04/05/2012
	 * Metdo encargado 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void consultaPermanencias2(HashMap params, String usuario)
			throws RemoteException {
		log.debug("Ingreso a REPORTE MASIVO FACADE:" );
		log.debug("consultaPermanencias2 - params:" + params);
		log.debug("consultaPermanencias2 - usuario:" + usuario);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String regimen = (String) params.get("regimen");
		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
		String criterio = (String) params.get("criterio");
		String valor =  ((String) params.get("valor")!= null )?(String) params.get("valor"):"";

		String solicitud1 = (String) params.get("solicitud1");
		String solicitud2 = (String) params.get("solicitud2");
		String solicitud3 = (String) params.get("solicitud3");
		String cadenaEstado="";
		
		if (criterio.trim().equals("5")) { // Intendencia
			String nombre_inte =  ( params.get("nombre_inte").toString()!= null )?(String) params.get("nombre_inte"):"";
			log.debug("nombre_inte:" + nombre_inte);
			valor = nombre_inte;
        }
		
		log.debug("criterio:" + criterio);
		log.debug("valor:" + valor);
		
		
		
		try {
			super.creaReporte(id, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario); 
			
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			//List reporte = facadeRemote.laborExcepcional(params, seguridad); resumenSolLabExcepcional
			List reporte = facadeRemote.resumenPermanenciaExcepcional2(params);
			ArrayList reporte2 = new ArrayList();
			reporte2.addAll(reporte.subList(0, reporte.size()-2));
			
			Map suma_minutos = (HashMap) reporte.get(reporte.size()-2);
			String suma = suma_minutos.get("suma_minutos").toString();

			
			Map cant_registro = (HashMap) reporte.get(reporte.size()-1);
			String cantidad = cant_registro.get("cant_registro").toString();

			log.debug("reporte:" + reporte);

			if(regimen.trim().equals("0")){
            	regimen = "D.L.276-728";
			}
			if(regimen.trim().equals("1")){
				regimen = "D.L.1057";
			}
			if(regimen.trim().equals("2")){
				regimen = "Modalidades formativas";
			}
            
            if(criterio.trim().equals("0")){
            	criterio = "Registro";
			}
			if(criterio.trim().equals("1")){
				criterio = "Und.Organizacional";
			}
			if(criterio.trim().equals("5")){
				criterio = "Intendencia";
			}
			if(criterio.trim().equals("4")){
				criterio = "Criterio";
				valor="Institucional";
			}
			
			if (reporte2 != null) {
				
				cadenaEstado=((!solicitud1.equals("0") && solicitud1!=null)? " Autorizada":"");
				cadenaEstado=cadenaEstado+((!solicitud2.equals("0") && solicitud2!=null)? (cadenaEstado.equals("")?"No Autorizada":", No Autorizada"):"");
				cadenaEstado=cadenaEstado+((!solicitud3.equals("0") && solicitud3!=null)? (cadenaEstado.equals("")?"Compensada":", Compensada"):"");
				
				linea = Utiles.formateaCadena("Criterios: ", 12, true)				
				+ Utiles.formateaCadena(((!regimen.equals("") && regimen!=null)?" Regime/Modalidad: "+regimen:""), 32, true)				
				+ Utiles.formateaCadena(((!fechaIni.equals("") && fechaIni!=null)?" Inicio: "+fechaIni:""), 18, true)
				+ Utiles.formateaCadena(((!fechaFin.equals("") && fechaFin!=null)?" Fin: "+fechaFin:""), 18, true)
				+ Utiles.formateaCadena(((!valor.equals("") && valor!=null)? " "+criterio+": "+valor:""), 25, true)
				+ Utiles.formateaCadena( " Estado: " +cadenaEstado, 25, true);
				
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);		
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				
				if (log.isDebugEnabled()) log.debug("POSTA 1: ");			
				linea = Utiles.formateaCadena("Fecha", 12, true)
				        + Utiles.formateaCadena("Und.Organiz.", 42, true)
						+ Utiles.formateaCadena("Registro", 8, true)
						+ Utiles.formateaCadena("Nombre", 54, true)
						+ Utiles.formateaCadena("Hor.Inicio", 10, true)
						+ Utiles.formateaCadena("Hor.Fin", 10, true)
						+ Utiles.formateaCadena("Minutos", 8, true)
						+ Utiles.formateaCadena("Estado", 16, true);
				this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				if (log.isDebugEnabled()) log.debug("POSTA 2: ");
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);

				Map quiebre = null;
				ArrayList detalles = null;

				for (int i = 0; i < reporte2.size(); i++) {
					quiebre = (HashMap)reporte2.get(i);			

					if (log.isDebugEnabled()) log.debug("POSTA 3: ");
					if (log.isDebugEnabled()) log.debug("quiebre: "+quiebre);
					if (log.isDebugEnabled()) log.debug("detalles: "+detalles);
					
					linea = Utiles.formateaCadena("", 1, true) +
					Utiles.formateaCadena(quiebre.get("fecha_desc").toString().trim(), 12, false) +
					Utiles.formateaCadena(quiebre.get("des_unidad").toString().trim(), 42, false) +
					Utiles.formateaCadena(quiebre.get("registro").toString().trim(), 8, true) +
		 			Utiles.formateaCadena(quiebre.get("nombre").toString().trim(), 54, false) +
		 			Utiles.formateaCadena(quiebre.get("inicio").toString().trim(), 10, false) +
		 			Utiles.formateaCadena(quiebre.get("fin").toString().trim(), 10, false) +
		 			Utiles.formateaCadena(quiebre.get("minutos_sal").toString().trim(), 8, false) +
					Utiles.formateaCadena(quiebre.get("estado").toString().trim(), 16, false);
					if (log.isDebugEnabled()) log.debug("linea: "+linea);
					
			        this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
			        if (log.isDebugEnabled()) log.debug("POSTA 4: ");
					
				}
				
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
				
				
				linea = Utiles.formateaCadena("", 116, true) +
				Utiles.formateaCadena("Total Colaboradores: ", 20, false) +
				Utiles.formateaCadena(cantidad.trim(), 10, false);
				if (log.isDebugEnabled()) log.debug("linea: "+linea);
				
		        this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
		        
		        
		        linea = Utiles.formateaCadena("", 116, true) +
				Utiles.formateaCadena("Total Minutos: ", 20, false) +
				Utiles.formateaCadena(suma.trim(), 0, false);
				if (log.isDebugEnabled()) log.debug("linea: "+linea);
				
		        this.escribe(linea, "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
		        this.escribe("", "PERMANENCIA DE LABOR EXCEPCIONAL", params, usuario);
		        
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "PERMANENCIA DE LABOR EXCEPCIONAL", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte2
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/**NVILLAR 25/04/2012
	 * Metdo encargado 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 * @param params
	 * @param usuario
	 * @return
	 * @throws RemoteException
	 */
	public void ConsultaHorasAutNoAutComp(HashMap params, String usuario)
			throws RemoteException {
		log.debug("Ingreso a REPORTE MASIVO FACADE HORAS AUTORIZADAS:" );
		log.debug("ConsultaHorasAutNoAutComp - params:" + params);
		log.debug("ConsultaHorasAutNoAutComp - usuario:" + usuario);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		String regimen = (String) params.get("regimen");
		String medida = (String) params.get("medida");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String registro = (String) params.get("codPers");
		//String nombre = (String) params.get("nombCompleto");
		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
		//uuoo
		
		if (criterio.trim().equals("5")) { // Intendencia
			String nombre_inte =  ( params.get("nombre_inte").toString()!= null )?(String) params.get("nombre_inte"):"";
			log.debug("nombre_inte:" + nombre_inte);
			valor = nombre_inte;
        }
		
		log.debug("criterio:" + criterio);
		log.debug("valor:" + valor);
		
		try {
			super.creaReporte(id, "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario); 
			
			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();
			
			//List reporte = facadeRemote.laborExcepcional(params, seguridad); resumenSolLabExcepcional
			List reporte = facadeRemote.consultaHorasAutNoAutComp(params);
			log.debug("reporte:" + reporte);
            
			if(regimen.trim().equals("0")){
	        	regimen = "D.L.276-728";
			}
			if(regimen.trim().equals("1")){
				regimen = "D.L.1057";
			}
			if(regimen.trim().equals("2")){
				regimen = "Modalidades formativas";
			}
	        
			if(medida.trim().equals("01")){
	        	medida = "Minutos";
			}
			if(medida.trim().equals("02")){
				medida = "Horas";
			}
			if(medida.trim().equals("03")){
				medida = "Dias";
			}
			
	        if(criterio.trim().equals("0")){
	        	criterio = "Registro";
			}
			if(criterio.trim().equals("1")){
				criterio = "Und.Organizacional";
			}
			if(criterio.trim().equals("5")){
				criterio = "Intendencia";
			}
			if(criterio.trim().equals("4")){
				criterio = "Criterio";
				valor="Institucional";
			}
			
			if (reporte != null) {
				linea = Utiles.formateaCadena("Criterios: ", 12, true)				
				+ Utiles.formateaCadena(((!regimen.equals("") && regimen!=null)?" Regime/Modalidad: "+regimen:""), 35, true)
				+ Utiles.formateaCadena(((!medida.equals("") && medida!=null)?" Und.: "+medida:""), 12, true)
				+ Utiles.formateaCadena(((!fechaIni.equals("") && fechaIni!=null)?" Inicio: "+fechaIni:""), 20, true)
				+ Utiles.formateaCadena(((!fechaFin.equals("") && fechaFin!=null)?" Fin: "+fechaFin:""), 20, true)
				+ Utiles.formateaCadena(((!criterio.equals("") && criterio!=null)?criterio+": "+valor:""), 25, true);
				
				if (log.isDebugEnabled()) log.debug("linea criterios busqueda: "+linea);
				this.escribe(linea, "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);		
				this.escribe("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);
				this.escribe("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);
				
				if (log.isDebugEnabled()) log.debug("POSTA 1: ");	
				linea = Utiles.formateaCadena("Und.Organizacional", 42, true)
						+ Utiles.formateaCadena("Reg.", 6, true)
						+ Utiles.formateaCadena("Nombre", 66, true)
						+ Utiles.formateaCadena("Autorizadas", 15, true)
						+ Utiles.formateaCadena("No Autorizadas", 16, true)
						+ Utiles.formateaCadena("Rechazadas", 12, true)
						+ Utiles.formateaCadena("Compensadas", 12, true);
				this.escribe(linea, "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);
				if (log.isDebugEnabled()) log.debug("POSTA 2: ");
				this.escribe("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);

				Map quiebre = null;

				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (HashMap)reporte.get(i);			

					if (log.isDebugEnabled()) log.debug("POSTA 3: ");
					if (log.isDebugEnabled()) log.debug("quiebre: "+quiebre);

					if(i == (reporte.size()-1) ){
						this.escribe("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);
						this.escribe("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);
						linea = Utiles.formateaCadena("", 48, true) +
						Utiles.formateaCadena("Total: ", 67, true) +
						Utiles.formateaCadena(quiebre.get("suma1").toString().trim(), 15, false) +
			 			Utiles.formateaCadena(quiebre.get("suma2").toString().trim(), 16, false) +
			 			Utiles.formateaCadena(quiebre.get("suma3").toString().trim(), 11, false) +
						Utiles.formateaCadena(quiebre.get("suma4").toString().trim(), 12, false);
						if (log.isDebugEnabled()) log.debug("linea: "+linea);
						
				        this.escribe(linea, "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);
						
					}else{
						
						String col = (String)quiebre.get("colaborador") ;
				    	String reg = col.substring(0,col.indexOf("-")-1);
				    	String nombre = col.substring(col.indexOf("-")+1,col.length()-1);
						
						linea = Utiles.formateaCadena("", 1, true) +
						Utiles.formateaCadena(quiebre.get("unidad").toString().trim(), 42, false) +
						Utiles.formateaCadena(reg.trim(), 6, false) +
						Utiles.formateaCadena(nombre.trim(), 68, false) +
			 			Utiles.formateaCadena(quiebre.get("xautorizada").toString().trim(), 15, false) +
			 			Utiles.formateaCadena(quiebre.get("xnoautorizada").toString().trim(), 16, false) +
						Utiles.formateaCadena(quiebre.get("xrechazada").toString().trim(), 11, false) +
						Utiles.formateaCadena(quiebre.get("xcompensada").toString().trim(), 10, false);
						if (log.isDebugEnabled()) log.debug("linea: "+linea);
						
				        this.escribe(linea, "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params, usuario);
					}
			        if (log.isDebugEnabled()) log.debug("POSTA 4: ");
				}
			}
		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS", params,
					usuario);
			log.debug("Error : " + e.getMessage());
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacacionesGozadasMatrimonio(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);
			
			String regimen = (String) params.get("regimen");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.vacacionesGozadasMatrimonio(dbpool,
            		regimen, fechaIni, fechaFin, criterio, valor, seguridad); 
            
			if (reporte != null) {

				this.escribe("", "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);

				linea = Utiles.formateaCadena("", 1, false)+Utiles.formateaCadena("Registro", 10, true)+"|"
				+ Utiles.formateaCadena("Trabajador", 60, true)+"|"
				+ Utiles.formateaCadena("RÃ©gimen", 40, true)+"|"
				
				+ Utiles.formateaCadena("Unidad", 40, true)+"|"
				+ Utiles.formateaCadena("Periodo", 10, true)+"|"
				+ Utiles.formateaCadena("Desde", 12, true)+"|"
				+ Utiles.formateaCadena("Hasta", 12, true)+"|"
				+ Utiles.formateaCadena("DÃ­as", 10, true)+"|"
				+ Utiles.formateaCadena("ObservaciÃ³n", 100, false);

				this.escribe(linea, "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);
				this.escribe("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);
				this.escribe("", "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
				String nombre ="";
				String nombreModalidad = "";
					
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);
					nombre = quiebre.getNombre().trim();
					nombreModalidad = nombre.substring(0,nombre.lastIndexOf("/"));
					String[] partsNombreModalidad = nombreModalidad.split("-");
					
					linea = Utiles.formateaCadena("", 1, false)+ 
							Utiles.formateaCadena(partsNombreModalidad[0], 10, false)+"|";
					linea += Utiles.formateaCadena(partsNombreModalidad[1], 60, false)+"|";
					linea += Utiles.formateaCadena(partsNombreModalidad[2], 40, false)+"|";
					
					linea += Utiles.formateaCadena(""
							+ nombre.substring(nombre.lastIndexOf("/")+Integer.parseInt("1"),nombre.length()), 40, true)+"|";
										
					detalles = quiebre.getDetalle();

					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);

							linea += Utiles.formateaCadena(""
									+ detalle.getAnno(), 10, true)+"|";
 
							linea += Utiles.formateaCadena(""
											+ Utiles.timeToFecha(detalle.getFechaInicio()), 12, true)+"|";
							linea += Utiles.formateaCadena(""
											+ Utiles.timeToFecha(detalle.getFechaFin()), 12, true)+"|";

							linea += Utiles.formateaCadena(""
									+ detalle.getCantidad(), 10, true)+"|";
							linea += Utiles.formateaCadena(""
									+ detalle.getDescripcion(), 100, false);

							this.escribe(linea, "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);
							this.escribe("", "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);
				
						}

					} else {
						this.escribe("El trabajador no posee vacaciones efectuadas por licencia de matrimonio CAS.", "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);
					}				
			      
				}

			} else {
				this.escribe("No se encontraron registros.","VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "VACACIONES EFECTUADAS POR LICENCIA MATRIMONIO CAS",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}	
	//FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void autorizacionesClimaLaboral(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "ACTIVIDAD DE CLIMA LABORAL", params, usuario);
			
			String regimen = (String) params.get("regimen");
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String fechaIni = (String) params.get("fechaIni");
			String fechaFin = (String) params.get("fechaFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,
							ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.autorizacionesClimaLaboral(dbpool,
            		regimen, fechaIni, fechaFin, criterio, valor, seguridad); 
            log.debug("reporte: " + reporte); //9 campos
            
			if (reporte != null) {				

				linea =Utiles.formateaCadena("UUOO", 10, true)+"|"
				+ Utiles.formateaCadena("Descripción", 40, true)+"|"
				+ Utiles.formateaCadena("Régimen", 15, true)+"|"				
				+ Utiles.formateaCadena("Nro Reg.", 10, true)+"|"
				+ Utiles.formateaCadena("Trabajador", 40, true)+"|"
				+ Utiles.formateaCadena("Autorizador", 50, true)+"|"
				+ Utiles.formateaCadena("Fecha", 15, true)+"|"
				+ Utiles.formateaCadena("Min. Clima", 10, true)+"|"
				+ Utiles.formateaCadena("Min. Exceso", 10, false);
				log.debug("linea: " + linea); //9 campos

				this.escribe(linea, "ACTIVIDAD DE CLIMA LABORAL", params, usuario);
				this.escribe("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "AUTORIZACIONES DE CLIMA LABORAL", params, usuario);
				this.escribe("", "ACTIVIDAD DE CLIMA LABORAL", params, usuario);

				BeanReporte quiebre = null;
				ArrayList detalles = null;
								
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (BeanReporte) reporte.get(i);
					detalles = quiebre.getDetalle();				
					
					/*linea = Utiles.formateaCadena("", 3, false);
					log.debug("linea2: " + linea); //9 campos
					this.escribe(linea, "AUTORIZACIONES DE CLIMA LABORAL", params, usuario);
					this.escribe("", "AUTORIZACIONES DE CLIMA LABORAL", params, usuario);*/
				
					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							BeanReporte detalle = (BeanReporte) detalles.get(j);

							linea = Utiles.formateaCadena(""+detalle.getCodigoUnidad(), 10, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getUnidadCorta(), 40, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getCategoria(), 15, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getCodigo(), 10, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getNombre(), 40, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getCodigoJefe()+" - "+detalle.getDescripcion(), 50, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getFecha(), 15, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getCantidad(), 10, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.getCantidad2(), 10, false);
							log.debug("linea3: " + linea); //9 campos

							this.escribe(linea, "ACTIVIDAD DE CLIMA LABORAL", params, usuario);
							this.escribe("", "ACTIVIDAD DE CLIMA LABORAL", params, usuario);
				
						}

					} else {
						this.escribe("El trabajador no autorizaciones de clima laboral.", "ACTIVIDAD DE CLIMA LABORAL", params, usuario);
					}	
			      
				}

			} else {
				this.escribe("No se encontraron registros.","ACTIVIDAD DE CLIMA LABORAL", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "ACTIVIDAD DE CLIMA LABORAL",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}	
	//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void notificaDirectivosNoVacaciones(HashMap params, String usuario)
			throws RemoteException {

		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES", params, usuario);
			
			String criterio = (String) params.get("criterio");
			String valor = (String) params.get("valor");
			String fechaNotific = (String) params.get("fechaNotific");
			String fechaNotificFin = (String) params.get("fechaNotificFin");
			HashMap seguridad = (HashMap) params.get("seguridad");

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.notificaDirectivosNoVacaciones(dbpool, fechaNotific, fechaNotificFin, criterio, valor,seguridad); 
            log.debug("reporte: " + reporte); //9 campos                
            
			if (reporte != null) {				

				linea =Utiles.formateaCadena("UUOO", 10, true)+"|"
				+ Utiles.formateaCadena("Descripción", 40, true)+"|"							
				+ Utiles.formateaCadena("Reg. Trabajador", 10, true)+"|"
				+ Utiles.formateaCadena("Apellidos y Nombres Trabajador", 40, true)+"|"
				+ Utiles.formateaCadena("Fecha Ingreso Trab.", 15, true)+"|"
				+ Utiles.formateaCadena("Reg. Directivo", 10, true)+"|"
				+ Utiles.formateaCadena("Apellidos y Nombres Directivo", 40, true)+"|"
				+ Utiles.formateaCadena("Fecha Notificación", 15, false);				
				log.debug("linea: " + linea); //9 campos

				this.escribe(linea, "NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES", params, usuario);
				this.escribe("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "AUTORIZACIONES DE CLIMA LABORAL", params, usuario);
				this.escribe("", "NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES", params, usuario);

				Map quiebre = null;
				ArrayList detalles = null;
				HashMap detalle = null;
								
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (HashMap) reporte.get(i);
					detalles = (ArrayList)quiebre.get("detalle");					
				
					if (detalles != null && detalles.size() > 0) {
						for (int j = 0; j < detalles.size(); j++) {
							detalle = (HashMap) detalles.get(j);

							linea = Utiles.formateaCadena(""+detalle.get("coduo"), 10, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.get("desuo"), 40, false)+"|";							
							linea += Utiles.formateaCadena(""+detalle.get("registro"), 10, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.get("apenom"), 40, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.get("fechaingreso_desc"), 15, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.get("regdirec"), 10, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.get("apenomdirec"), 40, false)+"|";
							linea += Utiles.formateaCadena(""+detalle.get("fecnotif_desc"), 15, false);						
							log.debug("linea data: " + linea); //9 campos

							this.escribe(linea, "NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES", params, usuario);
							this.escribe("", "NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES", params, usuario);
				
						}
					} else {
						this.escribe("No se encontraron notificaciones para la búsqueda", "NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES", params, usuario);
					}			      
				}

			} else {
				this.escribe("No se encontraron registros","NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES DE TRABAJADORES",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void vacacionesTruncas(HashMap params, String usuario)
			throws RemoteException {

		log.debug("ReporteMasivoFacade(vacacionesTruncas - params): " + params);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String linea = "";
		try {

			super.creaReporte(id, "VACACIONES TRUNCAS", params, usuario);		

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.vacacionesTruncas(dbpool,params); 
            log.debug("reporte: " + reporte); //9 campos                
            
			if (reporte != null && reporte.size() > 0) {				

				linea =Utiles.formateaCadena("N° Reg.", 10, true)+"|"
				+ Utiles.formateaCadena("UUOO", 10, true)+"|"			
				+ Utiles.formateaCadena("Apellidos y Nombres", 40, true)+"|"
				+ Utiles.formateaCadena("Fecha Ingreso", 15, true)+"|"
				+ Utiles.formateaCadena("Fecha Cese", 15, true)+"|"
				+ Utiles.formateaCadena("Régimen", 20, true)+"|"
				+ Utiles.formateaCadena("Fecha Ingreso Modificada", 15, true)+"|"
				+ Utiles.formateaCadena("Fecha Corte", 15, true)+"|"
				+ Utiles.formateaCadena("Días Vacaciones Truncas", 10, false);				
				log.debug("linea: " + linea); //9 campos

				this.escribe(linea, "VACACIONES TRUNCAS", params, usuario);
				this.escribe("-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------", "AUTORIZACIONES DE CLIMA LABORAL", params, usuario);
				this.escribe("", "VACACIONES TRUNCAS", params, usuario);

				Map quiebre = null;
												
				for (int i = 0; i < reporte.size(); i++) {
					quiebre = (HashMap) reporte.get(i);
					
							linea = Utiles.formateaCadena(""+quiebre.get("registro"), 10, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("coduo"), 10, false)+"|";							
							linea += Utiles.formateaCadena(""+quiebre.get("apenom"), 40, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("fechaingreso_desc"), 15, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("fechacese_desc"), 15, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("regimen"), 20, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("fechaingresomod_desc"), 15, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("fechacorte_desc"), 15, false)+"|";
							linea += Utiles.formateaCadena(""+quiebre.get("diasvacaciones"), 10, false);						
							log.debug("linea data: " + linea); //9 campos

							this.escribe(linea, "VACACIONES TRUNCAS", params, usuario);
							this.escribe("", "VACACIONES TRUNCAS", params, usuario);					      
				}

			} else {
				this.escribe("No se encontraron registros","VACACIONES TRUNCAS", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "VACACIONES TRUNCAS",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	//FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones reportePapeletas
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void reportePorTipoLicencia(HashMap params, String usuario)
			throws RemoteException {

		log.debug("Reporte por tipo de licencia: " + params);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String tipo = (String) params.get("tipo");
		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String tipoQuiebre = (String) params.get("tipoQuiebre");
		HashMap seguridad = (HashMap) params.get("seguridad");
		String linea = "";
		try {

			super.creaReporte(id, "TIPO DE LICENCIAS", params, usuario);		

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.tipoLicencia(dbpool, tipo, fechaIni, fechaFin, criterio, valor, tipoQuiebre, seguridad); 
            log.debug("reporte: " + reporte); //9 campos                
            
			if (reporte != null && reporte.size() > 0) {				

				linea =Utiles.formateaCadena("UUOO", 10, true)+"|"
				+ Utiles.formateaCadena("N° Reg.", 10, true)+"|"			
				+ Utiles.formateaCadena("Apellidos y Nombres", 40, true)+"|"
				+ Utiles.formateaCadena("Tipo licencia", 15, true)+"|"
				+ Utiles.formateaCadena("Año", 10, true)+"|"
				+ Utiles.formateaCadena("F. Inicio", 15, true)+"|"
				+ Utiles.formateaCadena("F. Fin", 15, true)+"|"
				+ Utiles.formateaCadena("Días", 15, true)+"|"
				+ Utiles.formateaCadena("Observación", 15, true);				
				log.debug("linea: " + linea); //9 campos

				this.escribe(linea, "VACACIONES TRUNCAS", params, usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "REPORTE POR TIPO DE LICENCIA", params, usuario);
				this.escribe("", "VACACIONES TRUNCAS", params, usuario);

				Map quiebre = null;
												
				for (int i = 0; i < reporte.size(); i++) {
					BeanReporte quiebreR = (BeanReporte)reporte.get(i);	
					ArrayList detalles = quiebreR.getDetalle();
					int totPersona = 0;
					String[] trabajador=null;
					if(tipoQuiebre.equals("0"))
					trabajador=quiebreR.getNombre().toString().trim().split("-");
					
					//quiebre = (HashMap) reporte.get(i);
					if (detalles!=null && detalles.size()>0){				
						for (int j=0; j<detalles.size(); j++){
							BeanReporte detalle = (BeanReporte)detalles.get(j);
							totPersona += detalle.getCantidad();
							if(tipoQuiebre.equals("1")){
								trabajador=detalle.getNombre().toString().trim().split("-");
								linea = Utiles.formateaCadena(""+detalle.getUnidad(), 10, false)+"|"
										+ Utiles.formateaCadena(""+trabajador[0], 10, true)+"|"			
										+ Utiles.formateaCadena(""+trabajador[1], 40, true)+"|"
										+ Utiles.formateaCadena(""+quiebreR.getNombre(), 15, true)+"|";
							}else{
								linea = Utiles.formateaCadena(""+quiebreR.getUnidad(), 10, false)+"|"
										+ Utiles.formateaCadena(""+quiebreR.getCodigo(), 10, true)+"|"			
										+ Utiles.formateaCadena(""+trabajador[1], 40, true)+"|"
										+ Utiles.formateaCadena(""+detalle.getNombre(), 15, true)+"|";
							}
								
							
								linea+= Utiles.formateaCadena(""+detalle.getAnno(), 10, true)+"|"
									+ Utiles.formateaCadena(""+Utiles.timeToFecha(detalle.getFechaInicio()), 15, true)+"|"
									+ Utiles.formateaCadena(""+Utiles.timeToFecha(detalle.getFechaFin()), 15, true)+"|"
									+ Utiles.formateaCadena(""+detalle.getCantidad(), 15, true)+"|"
									+ Utiles.formateaCadena(""+detalle.getDescripcion(), 15, true);
							log.debug("linea data: " + linea); //9 campos

							this.escribe(linea, "REPORTE POR TIPO DE LICENCIA", params, usuario);
							this.escribe("", "REPORTE POR TIPO DE LICENCIA", params, usuario);					      
						}
					}
				}

			} else {
				this.escribe("No se encontraron registros","REPORTE POR TIPO DE LICENCIA", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "REPORTE POR TIPO DE LICENCIA",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void reportePapeletas(HashMap params, String usuario)
			throws RemoteException {

		log.debug("Entrando a creacion del reporte de impresión reportePapeletas: " + params);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		HashMap seguridad = (HashMap) params.get("seguridad");
		String linea = "";
		try {

			super.creaReporte(id, "DE PAPELETAS", params, usuario);		

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.papeletas(params, seguridad); 
            log.debug("reporte: " + reporte); //9 campos                
            
			if (reporte != null && reporte.size() > 0) {				

				linea =Utiles.formateaCadena("Registro", 5, true)+"|"
				+ Utiles.formateaCadena("Trabajador", 30, true)+"|"
				+ Utiles.formateaCadena("Fecha", 10, true)+"|"			
				+ Utiles.formateaCadena("Marcaciones", 10, true)+"|"
				+ Utiles.formateaCadena("Papeleta", 15, true)+"|"
				+ Utiles.formateaCadena("Estado", 10, true)+"|"
				+ Utiles.formateaCadena("Fecha Estado", 10, true)+"|"
				+ Utiles.formateaCadena("Jefe", 10, true);				
				log.debug("linea: " + linea); //9 campos

				this.escribe(linea, "DE PAPELETAS", params, usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "DE PAPELETAS", params, usuario);
				this.escribe("", "DE PAPELETAS", params, usuario);
				
				for (int i = 0; i < reporte.size(); i++) {
					BeanReporte quiebre = (BeanReporte)reporte.get(i);			
					ArrayList detalles = quiebre.getDetalle();
					String[] trabajador=quiebre.getNombre().toString().trim().split("-");
					if (detalles!=null && detalles.size()>0){					
						for (int j=0; j<detalles.size(); j++){
							HashMap detalle = (HashMap)detalles.get(j);							
							linea= Utiles.formateaCadena(""+trabajador[0], 5, true)+"|"
								+ Utiles.formateaCadena(""+trabajador[1], 30, true)+"|"
								+ Utiles.formateaCadena(""+detalle.get("fecha"), 10, true)+"|"
								+ Utiles.formateaCadena(""+detalle.get("hora"), 10, true)+"|"
								+ Utiles.formateaCadena(""+detalle.get("papeleta"), 15, true)+"|"
								+ Utiles.formateaCadena(""+detalle.get("estado"), 10, true)+"|"
								+ Utiles.formateaCadena(""+detalle.get("fmod"), 10, true)+"|"
								+ Utiles.formateaCadena(""+detalle.get("jefe_autor"), 10, true);
								log.debug("linea data: " + linea); //9 campos
		
								this.escribe(linea, "REPORTE DE PAPELETAS", params, usuario);
								this.escribe("", "REPORTE DE PAPELETAS", params, usuario);	
						}
					}
				}

			} else {
				this.escribe("No se encontraron registros","REPORTE DE PAPELETAS", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "REPORTE DE PAPELETAS",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void reporteAcumuladoLicencias(HashMap params, String usuario)
			throws RemoteException {

		log.debug("Entrando a creacion del reporte de impresión reporteAcumuladoPapeletas: " + params);
		String id = (String) params.get("messageID");
		String dbpool = (String) params.get("dbpool");
		String tipo = (String) params.get("tipo");
		String fechaIni = (String) params.get("fechaIni");
		String fechaFin = (String) params.get("fechaFin");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String tipoQuiebre = (String) params.get("tipoQuiebre");
		HashMap seguridad = (HashMap) params.get("seguridad");
		String linea = "";
		try {

			super.creaReporte(id, "ACUMULADO DE LICENCIAS", params, usuario);		

			ReporteFacadeHome facadeHome = (ReporteFacadeHome) sl.getRemoteHome(ReporteFacadeHome.JNDI_NAME,ReporteFacadeHome.class);
			ReporteFacadeRemote facadeRemote = facadeHome.create();

            ArrayList reporte = facadeRemote.acumuladoLicencia(dbpool, tipo, fechaIni, fechaFin, criterio, valor, tipoQuiebre, seguridad); 
            log.debug("reporte: " + reporte); //9 campos                
            
			if (reporte != null && reporte.size() > 0) {	
				linea = Utiles.formateaCadena("UU.OO", 75, true)+"|"
				+ Utiles.formateaCadena("Registro", 10, true)+"|"
				+ Utiles.formateaCadena("Trabajador", 55, true)+"|"
				+ Utiles.formateaCadena("Licencia", 30, true)+"|"
				+ Utiles.formateaCadena("Cantidad", 10, true)+"|"			
				+ Utiles.formateaCadena("Días", 10, true);				
				log.debug("linea: " + linea); //9 campos

				this.escribe(linea, "ACUMULADO DE LICENCIAS", params, usuario);
				this.escribe("------------------------------------------------------------------------------------------------------------------------", "DE PAPELETAS", params, usuario);
				this.escribe("", "ACUMULADO DE LICENCIAS", params, usuario);
				
				for (int i = 0; i < reporte.size(); i++) {
					BeanReporte quiebreR = (BeanReporte)reporte.get(i);	
					ArrayList detalles = quiebreR.getDetalle();
					int totPersona = 0;
					String[] trabajador=null;
					if(tipoQuiebre.equals("0"))
					trabajador=quiebreR.getNombre().toString().trim().split("-");
					
					//quiebre = (HashMap) reporte.get(i);
					if (detalles!=null && detalles.size()>0){				
						for (int j=0; j<detalles.size(); j++){
							BeanReporte detalle = (BeanReporte)detalles.get(j);
							totPersona += detalle.getCantidad();
							if(tipoQuiebre.equals("1")){
								trabajador=detalle.getNombre().toString().trim().split("-");
								linea = Utiles.formateaCadena(""+detalle.getUnidad(), 75, false)+"|"
										+ Utiles.formateaCadena(""+trabajador[0], 10, true)+"|"			
										+ Utiles.formateaCadena(""+trabajador[1], 55, true)+"|"
										+ Utiles.formateaCadena(""+quiebreR.getNombre(), 40, true)+"|";
							}else{
								linea = Utiles.formateaCadena(""+quiebreR.getUnidad(), 75, false)+"|"
										+ Utiles.formateaCadena(""+quiebreR.getCodigo(), 10, true)+"|"			
										+ Utiles.formateaCadena(""+trabajador[1], 55, true)+"|"
										+ Utiles.formateaCadena(""+detalle.getNombre(), 40, true)+"|";
							}
							
								linea+= Utiles.formateaCadena(""+detalle.getCantidad(), 10, true)+"|"
									+ Utiles.formateaCadena(""+detalle.getDescripcion(), 15, true);
							log.debug("linea data: " + linea); //9 campos

							this.escribe(linea, "ACUMULADO DE LICENCIAS", params, usuario);
						}
					}
				}

			} else {
				this.escribe("No se encontraron registros","ACUMULADO DE LICENCIAS", params, usuario);
			}

		} catch (Exception e) {
			this.escribe("Error : " + e.getMessage(), "ACUMULADO DE LICENCIAS",
					params, usuario);
		} finally {
			try {
				//registramos el log del reporte
				super.registraReporte(dbpool, params, usuario);
			} catch (Exception e) {
			}
		}
	}
	
}