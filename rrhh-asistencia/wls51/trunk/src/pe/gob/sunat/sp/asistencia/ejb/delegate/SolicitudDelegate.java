package pe.gob.sunat.sp.asistencia.ejb.delegate;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sol.BeanMensaje;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sp.asistencia.bean.BeanSolReinConcepto;
import pe.gob.sunat.sp.asistencia.ejb.SolicitudFacadeHome;
import pe.gob.sunat.sp.asistencia.ejb.SolicitudFacadeRemote;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>
 * Title: AsistenciaDelegate
 * </p>
 * <p>
 * Description: Clase encargada de administrar las invocaciones para las
 * funcionalidades del modulo de solicitudes
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
public class SolicitudDelegate {

	private SolicitudFacadeRemote solicitud;

	public SolicitudDelegate() throws SolicitudException {
		try {
			SolicitudFacadeHome facadeHome = (SolicitudFacadeHome) ServiceLocator
					.getInstance().getRemoteHome(SolicitudFacadeHome.JNDI_NAME,
							SolicitudFacadeHome.class);
			solicitud = facadeHome.create();
		} catch (Exception e) {
			BeanMensaje beanM = new BeanMensaje();
			beanM.setError(true);
			beanM.setMensajeerror(e.getMessage());
			beanM.setMensajesol("Por favor intente nuevamente.");
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo encargado de registrar una solicitud
	 * 
	 * @param params
	 *            Parametros de la solicitud
	 * @param tipo
	 *            Tipo de solicitud
	 * @param usuario
	 *            Usuario
	 * @return Lista con mensajes de error
	 * @throws SolicitudException
	 */
	public ArrayList verificarSolicitud(String dbpool, HashMap params,
			String tipo, String usuario) throws SolicitudException {

		ArrayList lista = null;
		try {

			lista = solicitud.verificarSolicitud(dbpool, params, tipo, usuario);

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
						.setMensajeerror("Ha ocurrido un error al registrar la solicitud.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}
	/**
	 * WERR-PAS20155E230300132
	 * @param dbpool
	 * @param params
	 * @param tipo
	 * @param usuario
	 * @return
	 * @throws RemoteException 
	 * @throws SolicitudException
	 */
	public boolean  inicioLicenciaHabil(String dbpool, String fechaAlta, String fechaNac) throws RemoteException{
		boolean diaHabil;		
			diaHabil = solicitud.inicioLicenciaHabil(dbpool, fechaAlta,fechaNac);
		return diaHabil;
	}
	//end WERR-PAS20155E230300132

	/**
	 * Metodo encargado de procesar una solicitud
	 * 
	 * @param solicitud
	 *            Datos de la solicitud
	 * @param params
	 *            Parametros de la solicitud
	 * @param aprobador
	 *            Datos del usuario aprobador
	 * @param usuario
	 *            Usuario
	 * @return Lista de mensajes
	 * @throws SolicitudException
	 */
	public ArrayList procesarSolicitud(String dbpool, HashMap soli,
			HashMap params, HashMap aprobador, String usuario)
			throws SolicitudException {

		ArrayList lista = null;
		try {

			lista = solicitud.procesarSolicitud(dbpool, soli, params,
					aprobador, usuario);

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
						.setMensajeerror("Ha ocurrido un error al procesar la solicitud.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}
	
	/**
	 * Metodo encargado de procesar una solicitud: jquispecoi 03/2014
	 * 
	 * @param solicitud		Datos de la solicitud
	 * @param params		Parametros de la solicitud
	 * @param aprobador		Datos del usuario aprobador
	 * @param usuario		Usuario
	 * @return Lista de mensajes
	 * @throws SolicitudException
	 */
	public ArrayList procesarSolicitudAdministracion(String dbpool, HashMap soli,
			HashMap params, String usuario)
			throws SolicitudException {

		ArrayList lista = null;
		try {
			lista = solicitud.procesarSolicitudAdministracion(dbpool, soli, params, usuario);

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al procesar la solicitud.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
		
		return lista;
	}

	/**
	 * Metodo encargado de cargar las solicitudes concluidas de un trabajador
	 * 
	 * @param codPers
	 *            Registro del trabajador
	 * @param tipo
	 *            Tipo de solicitud
	 * @param criterio
	 *            Criterio de busqueda
	 * @param valor
	 *            Valor de criterio
	 * @return Lista de mensajes
	 * @throws SolicitudException
	 */
	public ArrayList cargarSolicitudesConcluidas(String dbpool, String codPers,
			String tipo, String criterio, String valor, String usuario)
			throws SolicitudException {

		ArrayList lista = null;
		try {

			lista = solicitud.cargarSolicitudesConcluidas(dbpool, codPers,
					tipo, criterio, valor, usuario);
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
						.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}

	/**
	 * Metodo encargado de cargar las solicitudes pendietes de un trabajador
	 * 
	 * @param codPers
	 *            Registro del trabajador
	 * @param tipo
	 *            Tipo de solicitud
	 * @param criterio
	 *            Criterio de busqueda
	 * @param valor
	 *            Valor de criterio
	 * @return Lista de mensajes
	 * @throws SolicitudException
	 */
	public ArrayList cargarSolicitudesIniciadas(String dbpool, String codPers,
			String tipo, String criterio, String valor, String usuario)
			throws SolicitudException {

		ArrayList lista = null;
		try {

			lista = solicitud.cargarSolicitudesIniciadas(dbpool, codPers, tipo,
					criterio, valor, usuario);
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
						.setMensajeerror("Ha ocurrido un error al cargar solicitudes pendientes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}

	/**
	 * 
	 * @param dbpool
	 * @param codPers
	 * @param tipo
	 * @param criterio
	 * @param valor
	 * @param usuario
	 * @return
	 * @throws SolicitudException
	 */
	public ArrayList cargarConsultaGeneral(HashMap datos, HashMap seguridad) throws SolicitudException {

		ArrayList lista = null;
		try {

			lista = solicitud.cargarConsultaGeneral(datos, seguridad);
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
						.setMensajeerror("Ha ocurrido un error al cargar solicitudes.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}

	/**
	 * Metodo encargado de cargar las solicitudes recibidas de un trabajador
	 * 
	 * @param codPers
	 *            Registro del trabajador
	 * @param tipo
	 *            Tipo de solicitud
	 * @param criterio
	 *            Criterio de busqueda
	 * @param valor
	 *            Valor de criterio
	 * @return Lista de mensajes
	 * @throws SolicitudException
	 */
	public ArrayList cargarSolicitudesRecibidas(String dbpool, String codPers,
			String tipo, String criterio, String valor)
			throws SolicitudException {

		ArrayList lista = null;
		try {
			lista = solicitud.cargarSolicitudesRecibidas(dbpool, codPers, tipo,
					criterio, valor);
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
						.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}

	/**
	 * Metodo encargado de cargar los datos de una solicitud
	 * 
	 * @param codPers
	 *            Registro del trabajador
	 * @param anno
	 *            Aï¿½o de solicitud
	 * @param numero
	 *            Numero de solicitud
	 * @return Datos de la solicitud
	 * @throws SolicitudException
	 */
	public HashMap cargarSolicitud(String dbpool, String codPers, String anno,
			String numero) throws SolicitudException {

		HashMap soli = new HashMap();
		try {

			soli = solicitud.cargarSolicitud(dbpool, codPers, anno, numero);
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
						.setMensajeerror("Ha ocurrido un error al cargar solicitud.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return soli;
	}

	/**
	 * Metodo que obtiene los datos del usuario aprobador de una solicitud para
	 * una determinada Unidad Operativa
	 * 
	 * @param solicitud
	 *            Datos de la solicitud
	 * @return Datos del usuario aprobador
	 * @throws SolicitudException
	 */
	public HashMap obtenerAprobador(String dbpool, HashMap soli)
			throws SolicitudException {

		HashMap aprobador = new HashMap();
		try {

			aprobador = solicitud.obtenerAprobador(dbpool, soli);
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
						.setMensajeerror("Ha ocurrido un error al cargar los datos del usuario aprobador.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return aprobador;
	}

	/**
	 * Metodo encargado de cargar la lista de seguimientos de una solicitud
	 * 
	 * @param codPers
	 *            Registro del trabajador
	 * @param anno
	 *            Aï¿½o de solicitud
	 * @param numero
	 *            Numero de solicitud
	 * @return Lista de seguimientos
	 * @throws SolicitudException
	 */
	public ArrayList cargarSeguimientos(String dbpool, String codPers,
			String anno, String numero) throws SolicitudException {

		ArrayList lista = null;
		try {
			lista = solicitud.cargarSeguimientos(dbpool, codPers, anno, numero);
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
						.setMensajeerror("Ha ocurrido un error al cargar los seguimientos.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}
	
	/**
	 * Metodo encargado de cargar la lista de seguimientos de una solicitud
	 * 
	 * @param codPers
	 *            Registro del trabajador
	 * @param anno
	 *            Aï¿½o de solicitud
	 * @param numero
	 *            Numero de solicitud
	 * @return Lista de seguimientos
	 * @throws SolicitudException
	 */
	public ArrayList buscarSupervisores(String dbpool, String codPers, String mov, String uorg)
	 throws SolicitudException {

		ArrayList lista = null;
		try {
			lista = solicitud.buscarSupervisores(dbpool, codPers, mov, uorg);
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
						.setMensajeerror("Ha ocurrido un error al buscar los supervisores.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return lista;
	}
	
	
	/*FUSION PROGRAMACION  */
	
	/**
	   * Metodo encargado de traer las vacaciones fisicas y programadas de la persona
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  public Map traerVacFisyProg(Map dBean) throws  DelegateException {
	  	Map mapaResult=null;
	  	try {      
	  		mapaResult = solicitud.traerVacFisyProg(dBean);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al traer las vacaciones fisicas y programadas.");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return mapaResult;
	  }
	  
	  //dtarazona cargarVacProgramadas cargarVacSolicitadasRep
	  /**
		 * Metodo encargado de cargar las solicitudes recibidas de un trabajador
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @param tipo
		 *            Tipo de solicitud
		 * @param criterio
		 *            Criterio de busqueda
		 * @param valor
		 *            Valor de criterio
		 * @return Lista de mensajes
		 * @throws SolicitudException
		 */
		public ArrayList cargarVacProgramadas(String dbpool, String codPers,
				String anno)
				throws SolicitudException {

			ArrayList lista = null;
			try {
				lista = solicitud.cargarVacProgramadas(dbpool, codPers, anno);
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
							.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return lista;
		}
		/**
		 * Metodo encargado de cargar las solicitudes recibidas de un trabajador
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @param tipo
		 *            Tipo de solicitud
		 * @param criterio
		 *            Criterio de busqueda
		 * @param valor
		 *            Valor de criterio
		 * @return Lista de mensajes
		 * @throws SolicitudException
		 */
		public ArrayList cargarVacSolicitadasRep(String dbpool, String codPers,
				String numero,String anno)
				throws SolicitudException {

			ArrayList lista = null;
			try {
				lista = solicitud.cargarVacSolicitadasRep(dbpool, codPers,numero, anno);
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
							.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return lista;
		}

	  /**
	   * Metodo encargado de agregar la reprogramacion de vacaciones
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  public Map agregarReProgramacion(Map datos) throws  DelegateException {
	  	Map mapaResult=null;
	  	try {      
	  		mapaResult = solicitud.agregarReProgramacion(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al agregar la reprogramacion de vacaciones.");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return mapaResult;
	  }
	  
	  /**
	   * Metodo encargado de eliminar una vacacion fisica
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  /*
	  public Map eliminarVacFis(Map datos) throws  DelegateException {
	  	//List listaVacFisAprob=null;
	  	Map mapaResult=null;
	  	try {      
	  		mapaResult = solicitud.eliminarVacFis(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al eliminar una vacacion fisica");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return mapaResult;
	  } 
	*/
	  
	  /**
	   * Metodo encargado de eliminar una vacacion fisica
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  /*
	  public List eliminarReProgramacion(Map datos) throws  DelegateException {
	  	List listaProgramacion=null;
	  	try {      
	  		listaProgramacion = solicitud.eliminarReProgramacion(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al eliminar una vacacion fisica");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	    return listaProgramacion;
	  } 
	  */
	  /**
	   * Metodo encargado de grabar la Anulacion de Vacaciones Fisicas
	   * @param Map datos
	   * @return Map
	   * @throws DelegateException
	   */  
	  public void grabarAnulVacFis(Map datos) throws  DelegateException {
	    try {      
	    	solicitud.grabarAnulVacFis(datos);
	    } catch (RemoteException e) {      
	      MensajeBean beanM = new MensajeBean();
	      if ("pe.gob.sunat.framework.core.ejb.FacadeException".equals(e.detail.getClass().getName())) {
	        beanM = ((pe.gob.sunat.framework.core.ejb.FacadeException) e.detail).getMensaje();
	      } else {
	        beanM.setError(true);
	        beanM.setMensajeerror("Ha ocurrido un error al obtener la informacion del Usuario.");
	        beanM.setMensajesol("Por favor intente nuevamente.");
	      }
	      throw new DelegateException(this, beanM);
	    } finally {
	    }
	  } 
	  /**
		 * Metodo encargado de cargar las solicitudes recibidas de un trabajador
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @param tipo
		 *            Tipo de solicitud
		 * @param criterio
		 *            Criterio de busqueda
		 * @param valor
		 *            Valor de criterio
		 * @return Lista de mensajes
		 * @throws SolicitudException
		 */
		public List procesarSolicitudes(Map param)
				throws SolicitudException {

			List lista = null;
			try {
				lista = solicitud.procesarSolicitudes(param);
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
							.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return lista;
		}
	
	/*        */
		
		  /**
		 * Metodo encargado de cargar las solicitudes recibidas de un trabajador
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @param tipo
		 *            Tipo de solicitud
		 * @param criterio
		 *            Criterio de busqueda
		 * @param valor
		 *            Valor de criterio
		 * @return Lista de mensajes
		 * @throws SolicitudException
		 */
		public List findHorasLaborAutorizadasConSaldo(HashMap param)
				throws SolicitudException {

			List lista = null;
			try {
				lista = solicitud.findHorasLaborAutorizadasConSaldo(param);
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
							.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return lista;
		}
	
		 /**
		 * Metodo encargado de cargar la lista de fechas y horas de solicitudes de labor expcecional
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @param tipo
		 *            Tipo de solicitud
		 * @param anno
		 *            anho de solicitud
		 * @param numero
		 *            numero de solicitud
		 * @param coduo
		 *            codigo uo                      
		 * @return Lista
		 * @throws SolicitudException
		 */
		public List listaSolicitud(String dbpool, String anno, String numero, String coduo, String codPers, String tipo
				)
				throws SolicitudException {

			List lista = null;
			try {
				lista = solicitud.listaSolicitud(dbpool,anno,numero,coduo,codPers,tipo);
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
							.setMensajeerror("Ha ocurrido un error al cargar la lista de fechas y horas de solicitudes  labor excepcional.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return lista;
		}
		
		 /**
		 * Metodo encargado de cargar la lista de fechas y horas de solicitudes de labor expcecional: jquispecoi 03/2014
		 * 
		 * @param codPers 	Registro del trabajador
		 * @param tipo 		Tipo de solicitud
		 * @param anno 		anho de solicitud
		 * @param numero 	numero de solicitud
		 * @param coduo 	codigo uo                      
		 * @return Lista
		 * @throws SolicitudException
		 */
		public List listaSolicitudAdministracion(String dbpool, String anno, String numero, String coduo, String codPers, String tipo
				)
				throws SolicitudException {

			List lista = null;
			try {
				lista = solicitud.listaSolicitudAdministracion(dbpool,anno,numero,coduo,codPers,tipo);
			} catch (IncompleteConversationalState e) {
				throw new SolicitudException(e.getBeanMensaje());
			} catch (RemoteException e) {
				BeanMensaje beanM = new BeanMensaje();
				if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
					beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
				} else {
					beanM.setError(true);
					beanM.setMensajeerror("Ha ocurrido un error al cargar la lista de fechas y horas de solicitudes  labor excepcional.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return lista;
		}
	
	/*        */
			  /**
		 * Metodo encargado de obtener la LE de un trabajador
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @return Saldo
		 * @throws SolicitudException
		 */
		public Integer findLaborAutorizadaSaldo(HashMap param)
				throws SolicitudException {

			Integer saldo;
			try {
				saldo = solicitud.findLaborAutorizadaSaldo(param);
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
							.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return saldo;
		}
	//DTARAZONA RESTRINGIR SOLICITUD DE LABOR EXCEPCIONAL CON PLANILLA VIGENTE
		/**
		* Metodo encargado de obtener la planilla vigente de un trabajador
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @return Saldo
		 * @throws SolicitudException
		 */
		public Map findPlanillaActiva(HashMap param)
				throws SolicitudException {

			Map planilla;
			try {
				planilla = solicitud.findPlanillaActiva(param);
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
							.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas en findPlanillaActiva.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return planilla;
		}
		
		/**
		* Metodo encargado de obtener el estado del flag de cruce con planilla activa
		 * 
		 * @param codPers
		 *            Registro del trabajador
		 * @return Saldo
		 * @throws SolicitudException findParamByCodTabCodigo
		 */
		public String findEstadoByCodTabCodigo(HashMap param)
				throws SolicitudException {

			String estado;
			try {
				estado = solicitud.findEstadoByCodTabCodigo(param);
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
							.setMensajeerror("Ha ocurrido un error al cargar solicitudes concluidas en findEstadoByCodTabCodigo.");
					beanM.setMensajesol("Por favor intente nuevamente.");
				}
				throw new SolicitudException(beanM);
			}

			return estado;
		}
		
	//FIN DTARAZONA		
		
	//PAS20171U230200001 - solicitud de reintegro   
	/**
	 * Metodo para  obtener datos de la solicitud de reintegro para solicitudes iniciadas y busqueda
	 * @param codPers codigo de personal
	 * @param anno año solicitado
	 * @param numero numero de solicitud
	 * @return Datos de la solicitud de reintegro
	 * Invoca a SolicitudFacade.obtenerSolicitudReintegro
	 * 
	 */
	public Map obtenerDatosSolReintegro(String codPers, String anno, String numero) {
		Map detalle = new HashMap();
		try {
			detalle = solicitud.obtenerSolicitudReintegro(codPers, anno, numero);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar detalle de solicitud de reintegro asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return detalle;
	}

	//PAS20181U230200023 - solicitud de licencia medica   
	/**
	 * Metodo para  obtener datos de la solicitud de licencia medca
	 * @param codPers codigo de personal
	 * @param anno año solicitado
	 * @param numero numero de solicitud
	 * @return Datos adicionales de la solicitud de lciencia medica 
	 * 
	 */
	public Map obtenerDatosSolLicenciaEnfermedad(String codPers, String anno, String numero) {
		Map detalle = new HashMap();
		try {
			detalle = solicitud.obtenerDatosSolLicenciaEnfermedad(codPers, anno, numero);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar detalle de solicitud de reintegro asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return detalle;
	}

	/**
	 * Metodo para obtener lista de conceptos trabajador por  mes y año 
	 * @param dbpool : pool de conexion
	 * @param dbpoolSig :pool de conexion
	 * @param filtro : datos para busqueda
	 * @param tipo : tipo de solicitud
	 * @return Lista de conceptos
	 * Para reintegros de asistencia/licencia invoa  SolicitudFacade.buscarConceptosAsistencia
	 * Para reintegros de subsidio invoca  SolicitudFacade.buscarConceptosSubsidios
	 */
	public List obtenerConceptos(String dbpool, String dbpoolSig, Map filtro, String tipo) {
		List conceptos = new ArrayList();
		try {
			if (tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_ASISTENCIA)) {
				conceptos = solicitud.buscarConceptosAsistencia(dbpoolSig, dbpool, filtro);
			}
			if (tipo.equals(Constantes.MOV_REINTEGRO_POR_DESCUENTO_SUBSIDIO)) {
				conceptos = solicitud.buscarConceptosSubsidios(dbpoolSig, dbpool, filtro);
			}

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar detalle de solicitud de reintegro asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return conceptos;

	}

	/**
	 * Metodo para obtener detalles de concepto de Asistencia
	 * @param dbpool : pool de conexion
	 * @param dbpoolSig :pool de conexion
	 * @param filtro : datos para busqueda
	 * @return Lista de detalles de concepto
	 * Invoca a SolicitudFacade.buscarDetalleConceptoAsistencia
	 */
	public List obtenerDetalleConceptoAsistencia(String dbpool, String dbpoolSig, Map filtro) {
		List detalles = new ArrayList();
		try {
			detalles = solicitud.buscarDetalleConceptoAsistencia(dbpool, dbpoolSig, filtro);

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al carga del detalle de concepto.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return detalles;
	}

	/**
	 * Metodo para obtener detalles de concepto de Licencia , permiso , subsidio
	 * @param dbpool : pool de conexion
	 * @param dbpoolSig :pool de conexion
	 * @param filtro : datos para busqueda
	 * @return Lista de detalles de concepto
	 * Invoca a SolicitudFacade.buscarDetalleConceptoLicencia
	 */
	public List obtenerDetalleConceptoLicencia(String dbpool, String dbpoolSig, Map filtro) {
		List detalles = new ArrayList();
		try {
			detalles = solicitud.buscarDetalleConceptoLicencia(dbpool, dbpoolSig, filtro);

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al carga del detalle de concepto.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return detalles;
	}

	/**
	 * Metodo para obtener listado de meses y años para la solicitudes de reintegro 
	 * @param datos: datos para busqueda
	 * @return Lista de periodos(año y mes )
	 * Invoca a SolicitudFacade.obtenerAnioMesesParaSolReintegro
	 */
	public Map obtenerAnioMesesParaSolReintegro(Map datos) {

		Map aniosMeses = new HashMap();
		try {
			aniosMeses = solicitud.obtenerAnioMesesParaSolReintegro(datos);

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar los años y meses de solicitud de reintegro.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return aniosMeses;

	}

	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/**
	 * Metodo para obtener listado de meses y años para la solicitudes de reintegro 
	 * @param datos: datos para busqueda
	 * @return Lista de periodos(año y mes )
	 * Invoca a SolicitudFacade.obtenerAnioMesesParaSolReintegro
	 */
	public Map obtenerPlanillasParaSolReintegro(Map datos) {

		Map aniosMeses = new HashMap();
		try {
			aniosMeses = solicitud.obtenerPlanillasParaSolReintegro(datos);

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar los años y meses de solicitud de reintegro.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return aniosMeses;

	}

	/**
	 * Metodo para marcar conceptos como exluidos (desde validacion de jefe)
	 * @param dbpool :pool de conexion 
	 * @param mapa  :datos de concepto
	 * @return Lista de errores
	 * Invoca SolicitudFacade.marcarExcluirConcepto
	 */
	public List marcarExcluirConcSolReintegro(String dbpool, HashMap mapa) {
		List mensajes = new ArrayList();
		try {
			mensajes = solicitud.marcarExcluirConcepto(dbpool, mapa);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar detalle de solicitud de reintegro asistencia.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return mensajes;
	}

	/**
	 * Metodo para obtener  solicitud incluido conceptos
	 * @param dbpool
	 * @param anno
	 * @param numero
	 * @param uoOrig
	 * @param codPers
	 * @param tipo
	 * @return Datos de solicitud de reintegro
	 * Invoca a SolicitudFacade.obtenerSolicitudReintegro 
	 */
	public Map obtenerDatosSolReintegroDerivacion(String dbpool, String anno, String numero, String uoOrig, String codPers, String tipo) {
		Map sol = new HashMap();
		try {
			sol = solicitud.obtenerSolicitudReintegro(codPers, anno, numero);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar detalle de solicitud de reintegro .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return sol;
	}

	/**
	 * Metodo para listar datos de solicitud para aprobacion de rrhh 
	 * @param anno : año de solicitud
	 * @param numero : numero de solicityd
	 * @param uoOrig : uo Origen
	 * @param codPers : codigo de persona 
	 * @param tipo: tipo de solicitud
	 * @param codRel : codigo de regimen laboral
	 * @return Mapa de datos de la solicitud
	 * Invoca a SolicitudFacade.obtenerDatosSolReintegroFinal
	 */
	public Map obtenerDatosSolReintegroFinal(String anno, String numero, String uoOrig, String codPers, String tipo, String codRel) {
		Map sol = new HashMap();
		try {
			sol = solicitud.obtenerSolicitudReintegroFinal(codPers, anno, numero, codRel);
			/*sol = solicitud.obtenerSolicitudReintegro( dbpool, codPers, anno, numero) ;
			HashMap filtro = new HashMap();
			//colocar datos para filtrar
			Map planilla = solicitud.obtenerPlanillaParaSolReintegro(filtro, codPers);
			if(planilla!=null){
				sol.put("planillaReintegro",planilla);
				sol.put("tienePlanilla",true);
			}else{
				sol.put("tienePlanilla",false);
			}
			boolean esCesado =  solicitud.esTrabajadorCesado(codPers);
			sol.put("esCesado", esCesado);
			
			Map params = new HashMap();
			params.put("codPers", codPers);
			params.put("fecha", new FechaBean().getTimestamp());
			Map planDev = solicitud.obtenerPlanillaDevolucion(params);
			
			if (planDev!= null){
				sol.put("ann_plandev", planDev.get("ano_peri_hme"));
				sol.put("mes_plandev", planDev.get("nume_peri_hme"));
				sol.put("cod_plandev", planDev.get("tipo_plan_tpl"));
				sol.put("cod_splandev", planDev.get("subt_plan_tpl"));
				sol.put("desc_subt_stp", planDev.get("desc_subt_stp"));
			}*/

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un obtener datos de la solicitud .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

		return sol;
	}

	/**
	 * Metodo para notificar a empleado alerta de no reintegro  
	 * @param mapa :datos para la busqueda
	 * @return Lista de mensaje de error
	 * Invoca a SolicitudFacade.enviarMensajeNotificacion 
	 */
	public List notificarSolReintegro(Map mapa) {
		List mensajes = new ArrayList();
		try {
			mensajes = solicitud.enviarMensajeNotificacion(mapa);
			return mensajes;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido al crear notificacion .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

	}

	/**
	 * Metodo para actualizar los detalles de un concepto de solicitud de reintegro
	 * @param mapa datos a actualizar
	 * @return Lista de mensajes
	 * Invoca a SolicitudFacade.procesarDetalleConcSolReintegro
	 */
	public List procesarDetalleConcSolReintegro(HashMap mapa) {
		List mensajes = new ArrayList();
		try {
			mensajes = solicitud.procesarDetalleConcSolReintegro(mapa);
			return mensajes;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al actualizar detalle de concepto solicitud de reintegro .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo para listar los documentos aprobados 
	 * @param mapa datos para la busqueda
	 * @return Mapa con listas de documentos aprobados 
	 * Invoca a SolicitudFacade.listaDocumentosAprobados
	 */
	public Map listarDocumentosAprobados(HashMap mapa) {
		Map documentos = new HashMap();
		try {
			documentos = solicitud.listaDocumentosAprobados(mapa);
			return documentos;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar lista de documentos aprobados .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo para obtener datos de trabajador
	 * @param dbpool : pool de conexion
	 * @param codPers : codigo de trabajador
	 * @return Mapa con datos de trabajador
	 */
	public Map obtenerDataTrabajador(String dbpool, String codPers) {
		Map documentos = new HashMap();
		try {
			documentos = solicitud.obtenerDataTrabajador(dbpool, codPers);
			return documentos;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar los datos del trabajador .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}

	}

	/**
	 * Metodo para obtener tipos de devolucion
	 * @param dbpool pool de conexion
	 * @param tipoLicencia tipo de licencia
	 * @return Lista de tipos de devolucion
	 * 
	 * Para tipo de licencia null/blanco (asistencia) Invoca SolicitudFacade.obtenerTiposDevolucion
	 * Para tipo de licencia  L (licencia)o P(permiso) Invoca SolicitudFacade.obtenerTiposDevolucionLicencias
	 * Para tipo de licencia S(subsidio) Invoca SolicitudFacade.obtenerTiposDevolucionSubsudios
	 * 
	 */
	public List listaTipoDevoluciones(String dbpool, String tipoLicencia) {
		List documentos = new ArrayList();
		try {
			if (tipoLicencia.equals("") || tipoLicencia == null) {
				documentos = solicitud.obtenerTiposDevolucion(dbpool);
			}
			if (tipoLicencia.equals("L") || tipoLicencia.equals("P")) {
				documentos = solicitud.obtenerTiposDevolucionLicencias(dbpool);
			}
			if (tipoLicencia.equals("S")) {
				documentos = solicitud.obtenerTiposDevolucionSubsudios(dbpool);
			}
			return documentos;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al cargar los datos del trabajador .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo para verificar regimen laboral
	 * @param filtro datos de busqueda
	 * @return true/false
	 * Invoca a solicitudFacade.verificarRegimenLaboral
	 */
	public boolean verificarRegimenLaboral(Map filtro) {
		boolean respuesta = false;
		try {
			respuesta = solicitud.verificarRegimenLaboral(filtro);
			return respuesta;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido verificar regimen laboral .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo para verificar reingreso
	 * @param filtro datos de busqueda
	 * @return true/false
	 * Invoca a solicitudFacade.verificarReIngreso
	 */
	public boolean verificarReIngreso(Map filtro) {
		boolean respuesta = false;
		try {
			respuesta = solicitud.verificarReIngreso(filtro);
			return respuesta;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al verificar reingreso .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo para verificar existe trabajador esta presente en planilla
	 * @param filtro datos de busqueda
	 * @return true/false
	 * Invoca a solicitudFacade.verificarExistePlanilla
	 */
	public boolean verificarExistePlanilla(Map filtro) {
		boolean respuesta = false;
		try {
			respuesta = solicitud.verificarExistePlanilla(filtro);
			return respuesta;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al verificar existencia en planillas .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo para verificar si el regimen de trabajador esta configurado para devolucion
	 * @param filtro datos de busqueda
	 * @return true/false
	 * Invoca a solicitudFacade.verificarRegimenConfigurado
	 */
	public boolean verificarRegimenConfigurado(Map filtro) {
		boolean respuesta = false;
		try {

			respuesta = solicitud.verificarRegimenConfigurado(filtro);
			return respuesta;

		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al verificar reingreso .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}

	/**
	 * Metodo para obtener fechas con movimientos  de un trabajador en una periodo
	 * @param filtro datos para la busqueda
	 * @return  Lista con fechas con movimientos
	 * Invova a solicitudFacade.buscarFechasConMovimientos
	 */
	public List buscarFechaConMovimiento(Map filtro) {
		List respuesta = new ArrayList();
		try {
			respuesta = solicitud.buscarFechasConMovimientos(filtro);
			return respuesta;
	
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error buscar fechas con movimientos .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}
	
	/** 
	 * Carga el detalle de solicitudes de reintegro
	 * @param concepto : datos de concepto
	 * @return lista de detalles
	 * Invoca a  solicitudFacade.cargarDetSolReintegro
	 */
	public List cargarDetSolReintegro(Map concepto) {
		List respuesta = new ArrayList();
		try {
			respuesta = solicitud.cargarDetSolReintegro(concepto);
			return respuesta;
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al obtener el detalle de la solicitud de reintegro.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}
	

	/** 
	 * Actualizar el detalle de solicitudes de reintegro
	 * @param concepto : datos de concepto
	 * @return lista de detalles
	 * Invoca a  solicitudFacade.cargarDetSolReintegro
	 */
	public boolean actualizarDetSolReintegro(Map concepto) {
		boolean respuesta = false;
		try {
			respuesta = solicitud.actualizarDetSolReintegro(concepto); 
			return respuesta;
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al actualizar el detalle de solicitud.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}
	
	
	/** 
	 * Metodo para verificar cese de trabajador
	 * @param codPers : codigo de trabajador
	 * @return true/false
	 * Invoca a  solicitudFacade.esTrabajadorCesado
	 */
	public boolean esTrabajadorCesado(String codPers) {
		try {
			return solicitud.esTrabajadorCesado(codPers);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error al intentar determinar si se trata de un trabajador cesado.");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}
	
	/** 
	 * Metodo para obtener devoluciones
	 * @param solicitud : datos de la solicitud
	 * @param listDetalle TODO
	 * @return lista de devoluciones 
	 */
	public List cargarDatosDevolucion (Map sol ,Map concepto, List listDetalle) {
		try {
			return solicitud.obtenerDevoluciones (sol,concepto,listDetalle);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error obtener datos de devolucion .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}
	
	/** 
	 * Metodo para obtener monto unitario
	 * @param solicitud : datos de la solicitud
	 * @param listDetalle  
	 * @return lista de devoluciones 
	 */
	public BigDecimal cargarMontoUnitario (Map sol ,Map concepto ) {
		try {
			return solicitud.obtenerMontoUnitario (sol,concepto);
		} catch (IncompleteConversationalState e) {
			throw new SolicitudException(e.getBeanMensaje());
		} catch (RemoteException e) {
			BeanMensaje beanM = new BeanMensaje();
			if (e.detail.getClass().getName().equals("pe.gob.sunat.sol.IncompleteConversationalState")) {
				beanM = ((IncompleteConversationalState) e.detail).getBeanMensaje();
			} else {
				beanM.setError(true);
				beanM.setMensajeerror("Ha ocurrido un error obtener monto unitario .");
				beanM.setMensajesol("Por favor intente nuevamente.");
			}
			throw new SolicitudException(beanM);
		}
	}
	//FIN -PAS20171U230200001 - solicitud de reintegro   
}