package pe.gob.sunat.sp.asistencia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.ejb.DelegateException;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sol.BeanMensaje;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.AutenticacionException;
//import pe.gob.sunat.sol.seguridad.acceso.jaas.ServletAutenticacionIntranet;
//import pe.gob.sunat.sol.seguridad.acceso.portal.bean.BeanUsuario;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoReloj;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.AsistenciaException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.MantenimientoException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ReporteException;
import pe.gob.sunat.sp.asistencia.ejb.delegate.SolicitudDelegate;
import pe.gob.sunat.sp.asistencia.ejb.delegate.VacacionDelegate;
import pe.gob.sunat.tecnologia.menu.bean.MenuCliente;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.sp.asistencia.bean.BeanReporte;// ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.rrhh.dao.T12DAO;// ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011

/**
 * @web.servlet name="ServletReporte"
 * @web.servlet-mapping url-pattern = "/asisS10Alias"
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * 
 * Title: Control de Asistencia
 * Description:
 * Copyright: Copyright (c) 2004
 * Company: Sunat
 * @author cgarratt
 * @version 1.0
 * @web.ejb-ref name="ReporteFacadeEJB"
 *              type="session"
 *              home="pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeHome"
 *              remote="pe.gob.sunat.sp.asistencia.ejb.ReporteFacadeRemote"
 *              link="asistencia/ejbsp-reportefacade.jar#ReporteFacadeEJB"
 */
 
public class ServletReporte extends ServletAbstract {

	private static final Logger log = Logger.getLogger(ServletReporte.class);
	private static String pool_sp;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	MensajeBean bean= null;
	public static final String ROLES_PROPERTIES_FILE_NAME = "/pe/gob/sunat/rrhh/roles.properties";
	
	public void init(ServletConfig config) throws ServletException {
		try{    
			super.init(config);
			pool_sp = config.getInitParameter("pool_sp");
			//PRAC-ASANCHEZ - 04/08/2009
			cargarParametros();			
		} catch (Exception e) {
			log.error("**** Error ****", e);
			throw new ServletException(e.getMessage());
		} finally {
		}

	}

    public void procesa(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
      try{
		String accion = request.getParameter("accion");		
		if (session == null) {
			RequestDispatcher dispatcher =
			getServletContext().getRequestDispatcher("/PagSession.jsp");
			dispatcher.forward(request, response);
			return;
		}

		UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
		if (bUsuario!=null)
			NDC.push(bUsuario.getLogin().concat("-").concat(bUsuario.getTicket()));

		log.info("Accion : " + accion);
		
        if (accion.equals("cargarMarcaciones")) {
            cargarMarcaciones(request, response, session);
        }

        if (accion.equals("generarMarcaciones")) {
            generarMarcaciones(request, response, session);
        }

        if (accion.equals("cargarImpares")) {
            cargarImpares(request, response, session);
        }

        if (accion.equals("generarImpares")) {
            generarImpares(request, response, session);
        }

        if (accion.equals("cargarCalificado")) {
            cargarCalificado(request, response, session);
        }

        if (accion.equals("generarCalificado")) {
            generarCalificado(request, response, session);
        }

        if (accion.equals("cargarResumenMensual")) {
            cargarResumenMensual(request, response, session);
        }

        if (accion.equals("generarResumenMensual")) {
            generarResumenMensual(request, response, session);
        }

        if (accion.equals("cargarInasistencias")) {
            cargarInasistencias(request, response, session);
        }

        if (accion.equals("generarInasistencias")) {
            generarInasistencias(request, response, session);
        }

        if (accion.equals("cargarResumenDiario")) {
            cargarResumenDiario(request, response, session);
        }

        if (accion.equals("generarResumenDiario")) {
            generarResumenDiario(request, response, session);
        }

        if (accion.equals("cargarTurnosTrabajo")) {
            cargarTurnosTrabajo(request, response, session);
        }

        if (accion.equals("generarTurnosTrabajo")) {
            generarTurnosTrabajo(request, response, session);
        }

        if (accion.equals("cargarHoraExtra")) {
            cargarLaborExcepcional(request, response, session);
        }

/*        if (accion.equals("generarHoraExtra")) {
            generarHoraExtra(request, response, session);
        }*/

        if (accion.equals("generarLaborExcepcional")) {
        	generarLaborExcepcional(request, response, session);
        }
        
        if (accion.equals("cargarCompensaciones")) {
            cargarCompensaciones(request, response, session);
        }

        if (accion.equals("generarCompensaciones")) {
            generarCompensaciones(request, response, session);
        }

        if (accion.equals("cargarTipoLicencia")) {
            cargarTipoLicencia(request, response, session);
        }

        if (accion.equals("generarTipoLicencia")) {
            generarTipoLicencia(request, response, session);
        }

        if (accion.equals("cargarAcumuladoLicencia")) {
            cargarAcumuladoLicencia(request, response, session);
        }

        if (accion.equals("generarAcumuladoLicencia")) {
            generarAcumuladoLicencia(request, response, session);
        }

        if (accion.equals("cargarVacacionesGozadas")) {
            cargarVacacionesGozadas(request, response, session);
        }

        if (accion.equals("generarVacacionesGozadas")) {
            generarVacacionesGozadas(request, response, session);
        }

        if (accion.equals("cargarVacacionesPendientes")) {
            cargarVacacionesPendientes(request, response, session);
        }

        if (accion.equals("generarVacacionesPendientes")) {
            generarVacacionesPendientes(request, response, session);
        }

        if (accion.equals("cargarVacacionesSaldos")) {
            cargarVacacionesSaldos(request, response, session);
        }

        if (accion.equals("generarVacacionesSaldos")) {
            generarVacacionesSaldos(request, response, session);
        }

        if (accion.equals("cargarVacacionesCompensadas")) {
            cargarVacacionesCompensadas(request, response, session);
        }        
       
        if (accion.equals("generarVacacionesCompensadas")) {
            generarVacacionesCompensadas(request, response, session);
        }

        if (accion.equals("cargarVacacionesUnidad")) {
            cargarVacacionesUnidad(request, response, session);
        }

        if (accion.equals("generarVacacionesUnidad")) {
            generarVacacionesUnidad(request, response, session);
        }

        if (accion.equals("cargarVacaciones")) {
            cargarVacaciones(request, response, session);
        }

        if (accion.equals("generarVacaciones")) {
            generarVacaciones(request, response, session);
        }

        if (accion.equals("cargarDevoluciones")) {
            cargarDevoluciones(request, response, session);
        }

        if (accion.equals("generarDevoluciones")) {
            generarDevoluciones(request, response, session);
        }

        if (accion.equals("generarConsultaVacaciones")) {
            generarConsultaVacaciones(request, response, session);
        }

        if (accion.equals("cargarCalificadoPersonal")) {
            cargarCalificadoPersonal(request, response, session);
        }

        if (accion.equals("generarCalificadoPersonal")) {
            generarCalificadoPersonal(request, response, session);
        }

        if (accion.equals("cargarMarcacionesPersonal")) {
            cargarMarcacionesPersonal(request, response, session);
        }

        if (accion.equals("generarMarcacionesPersonal")) {
            generarMarcacionesPersonal(request, response, session);
        }

        if (accion.equals("cargarSinTurno")) {
            cargarSinTurno(request, response, session);
        }

        if (accion.equals("generarSinTurno")) {
            generarSinTurno(request, response, session);
        }

        //Comentando para Prueba
        if (accion.equals("cargarPapeletas")) {
            cargarPapeletas(request, response, session);
        }

        if (accion.equals("generarPapeletas")) {
            generarPapeletas(request, response, session);
        }
        
        if (accion.equals("cargarLicenciaMedica")) {
            cargarLicenciaMedica(request, response, session);
        }

        if (accion.equals("generarLicenciaMedica")) {
            generarLicenciaMedica(request, response, session);
        }
        
        if (accion.equals("cargarCompensacionBolsa")) {
        	cargarCompensacionBolsa(request, response, session);
        }

        if (accion.equals("generarCompensacionBolsa")) {
        	generarCompensacionBolsa(request, response, session);
        }
        
        // PRAC-ASANCHEZ 30/07/2009
        if (accion.equals("cargarDetalleDiario")) {
            cargarDetalleDiario(request, response, session);
        }
         
        if (accion.equals("generarDetalleDiario")) {
            generarDetalleDiario(request, response, session);
        }        
        //   
        
        /* ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */
        if (accion.equals("cargarNotificacionesVacaciones")) {
            cargarNotificacionesVacaciones(request, response, session);
        }

        if (accion.equals("generarNotificacionesVacaciones")) {
            generarNotificacionesVacaciones(request, response, session);
        }
        /* FIN ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */
        
      //JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
        if (accion.equals("cargarNotificaDirectivos")) {
        	cargarNotificaDirectivos(request, response, session);
        }
        
        if (accion.equals("generarNotificaDirectivos")) {
            generarNotificaDirectivos(request, response, session);
        }        
        
        if (accion.equals("buscaDetalleNotificaDirectivo")) {
        	buscaDetalleNotificaDirectivo(request, response, session);
        }
        
        if (accion.equals("cargarNotificaTrabajador")) {
        	cargarNotificaTrabajador(request, response, session);
        }
        
        if (accion.equals("generarNotificaTrabajador")) {
            generarNotificaTrabajador(request, response, session);
        }
        
        if (accion.equals("buscaDetalleNotificaTrabajador")) {
        	buscaDetalleNotificaTrabajador(request, response, session);
        }
        //FIN - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
        
        /* ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
        if (accion.equals("cargarMensualMovimiento")) {
        	cargarMensualMovimiento(request, response, session);
        }
        if (accion.equals("generarMensualMovimiento")) {
        	generarMensualMovimiento(request, response, session);
        }
        if (accion.equals("cargarMensualUUOO")) {
        	cargarMensualUUOO(request, response, session);
        }
        if (accion.equals("generarMensualUUOO")) {
        	generarMensualUUOO(request, response, session);
        }
        if (accion.equals("buscarDetalleMensualUUOO")) {
        	buscarDetalleMensualUUOO(request, response, session);
		}
        if (accion.equals("cargarMensualColaborador")) {
        	cargarMensualColaborador(request, response, session);
        }
        if (accion.equals("generarMensualColaborador")) {
        	generarMensualColaborador(request, response, session);
        }
        if (accion.equals("cargarDiarioMovimiento")) {
        	cargarDiarioMovimiento(request, response, session);
        }
        if (accion.equals("generarDiarioMovimiento")) {
        	generarDiarioMovimiento(request, response, session);
        }  
        if (accion.equals("cargarPeriodosCerradosPorRegimen")) {
        	cargarPeriodosCerradosPorRegimen(request, response, session);
        } 
        if (accion.equals("buscarDetalleDiarioMovi")) {
        	buscarDetalleDiarioMovi(request, response, session);
		    }
        if (accion.equals("buscarDetalleMensualMovi")) {
        	buscarDetalleMensualMovi(request, response, session);
		    }
        /* FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
        
        // INICIO - NVS - AOM LABOR EXCEPCIONAL
        if (accion.equals("cargarSolicitudLabExcComp")) {
        	cargarSolicitudLabExcComp(request, response, session);
        }
        if (accion.equals("consultarSolicitudLE")) {
        	consultarSolicitudLE(request, response, session);
        }
        if (accion.equals("consultarSolicitudLE2")) {
        	consultarSolicitudLE2(request, response, session);
        }
		if (accion.equals("buscarDetalleSolicitudLE")) {
        	buscarDetalleSolicitudLE(request, response, session);
        }
		if (accion.equals("buscarDetalleSolicitudLE2")) {
        	buscarDetalleSolicitudLE2(request, response, session);
        }
        if (accion.equals("cargarReportePermanencia")) {
        	cargarReportePermanencia(request, response, session);
        }
		if (accion.equals("consultarPermanenciaLE")) {
        	consultarPermanenciaLE(request, response, session);
        }
		if (accion.equals("consultarPermanenciaLE2")) {
        	consultarPermanenciaLE2(request, response, session);
        }
		if (accion.equals("buscarDetallePermanenciaLE")) {
			buscarDetallePermanenciaLE(request, response, session);
        }
		if (accion.equals("cargarHorasAutNoAutComp")) {
			cargarHorasAutNoAutComp(request, response, session);
        }
		if (accion.equals("ConsultarHorasAutNoAutComp")) {
			ConsultarHorasAutNoAutComp(request, response, session);
        }
        // FIN - NVS - LABOR EXCEPCIONAL
		
		//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
		 if (accion.equals("cargarVacacionesGozadasMatrimonio")) {
			 cargarVacacionesGozadasMatrimonio(request, response, session);
	     }
		 
		 if (accion.equals("generarVacacionesGozadasMatrimonio")) {
			 generarVacacionesGozadasMatrimonio(request, response, session);
	     }
		 //FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
		 
		//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
		 if (accion.equals("cargarAutorizacionesClimaLaboral")) {
			 cargarAutorizacionesClimaLaboral(request, response, session);
	     }
		 
		 if (accion.equals("generarAutorizacionesClimaLaboral")) {
			 generarAutorizacionesClimaLaboral(request, response, session);
	     }
		 //FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
		 //ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	     if (accion.equals("cargarNotificaDirectivosNoVacaciones")) {
	    	 cargarNotificaDirectivosNoVacaciones(request, response, session);
	     }	        
	     if (accion.equals("generarNotificaDirectivosNoVacaciones")) {
	    	 generarNotificaDirectivosNoVacaciones(request, response, session);
	     }          
	     if (accion.equals("detalleNotificaDirectivoNoVacaciones")) {
	    	 detalleNotificaDirectivoNoVacaciones(request, response, session);
	     }
	     if (accion.equals("cargarVacacionesTruncas")) {
	    	 cargarVacacionesTruncas(request, response, session);
	     }	        
	     if (accion.equals("generarVacacionesTruncas")) {
	    	 generarVacacionesTruncas(request, response, session);
	     }         
	     //FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	     
	     //dtarazona
	     if (accion.equals("cargarEstadisticoAnualVacaciones")) {
	        cargarEstadisticoAnualVacaciones(request, response, session);
	     }
	     if(accion.equals("generarEstadisticoAnualVacaciones"))
	     {
	    	 estadisticoAnualVacaciones(request, response, session);
	     }	
	     if (accion.equals("cargarRepPersNoGenSaldoVac")) {
	    	 cargarRepPersNoGenSaldoVac(request, response, session);
		 }
		 if(accion.equals("generarRepPersNoGenSaldoVac"))
		 {
			 generarRepPersNoGenSaldoVac(request, response, session);
		 }
		 if(accion.equals("verDetalleSaldoVac"))
	     {
			 verDetalleSaldoVac(request, response, session);
	     }	
	        
	     //dtarazona

      } catch (Exception e) {
	    log.error("*** Error ***", e);
	  } finally{
		NDC.pop();
		NDC.remove();
	  }	
    }
    
    //dtarazona Modificado 24/05/2018
    /**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void verDetalleSaldoVac(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {

		try {
			HashMap datos = new HashMap();
			datos.put("dbpool", pool_sp);
			datos.put("num_saldovac", (String) request.getParameter("numSaldoVac"));
			String trabajador=request.getParameter("codPers").toString()+" - "+request.getParameter("nombres").toString();
			log.debug("num_saldovac:"+request.getParameter("numSaldoVac"));
			ReporteDelegate rd = new ReporteDelegate();
			List lista = rd.findDetalleSaldoVacacional(pool_sp,(String) request.getParameter("numSaldoVac"));

			session.removeAttribute("detalleSaldoVac");
			setAttribute(session, "detalleSaldoVac", lista);
			session.removeAttribute("trabajador");
			setAttribute(session, "trabajador", trabajador);
			
			forward(request, response, "/detalleSaldoVac.jsp?idSession = "
					+ System.currentTimeMillis());

			return;
		} catch (AsistenciaException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			log.error(e, e);
			bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al ordenar los Dias Laborables");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion");
			forwardError(request, response, bean);
		} finally {
		}
	}
	
    // fin dtarazona

    /**
     * Metodo encargado de cargar la pagina del reporte de marcaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarMarcaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp));		//jquispecoi PAS20155E230000073
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			
			//BeanUsuario bUsuario = (BeanUsuario) session.getAttribute("beanusuario");
			//String codPers = bUsuario.getNumreg();
			String codUO = bUsuario.getCodUO();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);			
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			log.debug("uoSeg: "+uoSeg);//ICAPUNAY-MEMO 32-4F3100-2013
			log.debug("roles: "+roles);//ICAPUNAY-MEMO 32-4F3100-2013
			log.debug("roles.get(Constantes.ROL_JEFE): "+roles.get(Constantes.ROL_JEFE));//ICAPUNAY-MEMO 32-4F3100-2013

			AsistenciaDelegate ad = new AsistenciaDelegate();
			//JVV - 30/09/2010
			ArrayList relojes = ad.cargarRelojes(pool_sp, Constantes.ACTIVO);
			session.removeAttribute("listaRelojes");
			session.setAttribute("listaRelojes", relojes);				
			
			//MM es Reporte de Marcaciones
			if ( (ad.esSupervisor(pool_sp, codPers, "MM", codUO)) || (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) 
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  || (roles.get(Constantes.ROL_JEFE)!=null) )  {
				
	            session.removeAttribute("detalleReporte");
	            session.removeAttribute("operativo");
			    session.setAttribute("operativo", roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				if (log.isDebugEnabled()) log.debug("operativo: " + roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				session.removeAttribute("jefe");
			    session.setAttribute("jefe", roles.get(Constantes.ROL_JEFE));
				if (log.isDebugEnabled()) log.debug("jefe: " + roles.get(Constantes.ROL_JEFE));	            
				
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteMarcaciones.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de marcaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarMarcaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	//JVV-ini
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");						
			//getRoles(request, response, session, bUsuario);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);			
            //String codigo = bUsuario.getNumreg();            
            //HashMap roles = super.getRoles(session);
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
            String uuoo = bUsuario.getCodUO();
            ArrayList relojes = (ArrayList) session.getAttribute("listaRelojes");
            //aca segmentamos
			//if ("usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())) uoSeg = uoSeg.substring(0,2).concat("%");			
			if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_OPERATIVO"))!=null) uoSeg = uoSeg.substring(0,2).concat("%");			
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles );//session.getAttribute("perfil_usuario").toString());
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uuoo", uuoo);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
          
            String regimen = request.getParameter("cmbRegimen");
            String indReloj = request.getParameter("cmbReloj");
            
            int intReloj = Integer.parseInt(indReloj);
			String codReloj = "-1";
			if (intReloj != -1) {
				BeanTipoReloj reloj = (BeanTipoReloj) relojes.get(intReloj);
				codReloj = reloj.getReloj();				
			}
			log.debug("codReloj: "+codReloj);
          //JVV-fin
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");            
            String horaMarca = request.getParameter("horaMarca");
            
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");
            log.debug("repetir: "+repetir);
            
            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            log.debug("tipoR: "+tipoR);

            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("regimen", regimen);
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("horaMarca", horaMarca);
                params.put("reloj",codReloj);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);

                rd.masivoMarcaciones(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                	//JVV-ini --se agrego regimen
                	HashMap params = new HashMap();
                	params.put("dbpool", pool_sp);
                	params.put("regimen", regimen);
                	params.put("fechaIni", fechaIni);
                    params.put("fechaFin", fechaFin);
                    params.put("horaMarca", horaMarca);
                    params.put("reloj",codReloj);
                    params.put("criterio", criterio);
                    params.put("valor", valor);                    
                    params.put("seguridad", seguridad);
                    
                    ArrayList detalleReporte = rd.marcaciones(params);
                	//JVV-fin
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }

            if (tipoR.equals("reporte")) { //IGUAL
            	//JVV-ini
            	//request.setAttribute("roles", roles);
            	request.setAttribute("regimen", regimen);
            	request.setAttribute("reloj", codReloj);            	
            	//JVV-fin
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);
                request.setAttribute("horaMarca", horaMarca);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteMarcaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelMarcaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE DE MARCACIONES DEL PERSONAL");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de marcaciones impares
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarImpares(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp));		//jquispecoi PAS20155E230000073

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);			
			String codUO = bUsuario.getCodUO();
			log.debug("codUO "+codUO);
			String uoSeg = bUsuario.getVisibilidad();
			log.debug("uoSeg "+uoSeg);
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) {
				log.debug("uoSeg.substring(0,2).concat('%') " + uoSeg.substring(0,2).concat("%"));
				uoSeg = uoSeg.substring(0,2).concat("%");
			}

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  )  {

	            session.removeAttribute("detalleReporte");

	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteImpares.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de marcaciones impares
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarImpares(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
			
            //String codigo = bUsuario.getNumreg();
            //String usuario = bUsuario.getLogin();
            //HashMap roles = super.getRoles(session);
            String uoSeg = bUsuario.getVisibilidad();
            
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("imprimir")) {

                HashMap params = new HashMap();
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("codPers", codigo);
                params.put("seguridad", seguridad);

                rd.masivoImpares(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                    ArrayList detalleReporte = rd.marcacionesImpares(pool_sp,
                            fechaIni, fechaFin, criterio, valor, seguridad);
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteImpares.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelImpares.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE DE MARCACIONES IMPARES");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte calificado de asistencia
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarCalificado(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp));		//jquispecoi PAS20155E230000073
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
			
			String codUO = bUsuario.getCodUO();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) 
					|| (roles.get(Constantes.ROL_JEFE)!=null))  {
	            MantenimientoDelegate md = new MantenimientoDelegate();
	            
	            ArrayList movimientos = md.buscarMovimientos(pool_sp, "5", "",
	                    "-1");

	            session.removeAttribute("movimientos");
	            session.setAttribute("movimientos", movimientos);
	            session.removeAttribute("detalleReporte");
	            session.removeAttribute("operativo");
			    session.setAttribute("operativo", roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				if (log.isDebugEnabled()) log.debug("operativo: " + roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				session.removeAttribute("jefe");
			    session.setAttribute("jefe", roles.get(Constantes.ROL_JEFE));
				if (log.isDebugEnabled()) log.debug("jefe: " + roles.get(Constantes.ROL_JEFE));	            
				

	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteCalificado.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte calificado de asistencia
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarCalificado(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            //String codigo = bUsuario.getNumreg();
            //String usuario = bUsuario.getLogin();
            //HashMap roles = super.getRoles(session);
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
          //JVV-ini
            String regimen = request.getParameter("cmbRegimen");
          //JVV-fin
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String mov = request.getParameter("cmbTipMov");
            if (mov.equals("-1")){ mov = "";}

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) {
				valor = codInten;
			}
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("imprimir")) {

                HashMap params = new HashMap();
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("regimen", regimen);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("codPers", codigo);
                params.put("seguridad", seguridad);
                params.put("mov", mov);

                rd.masivoCalificado(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                	//JVV-ini --se agrego regimen
                	Map params = new HashMap();
                	params.put("dbpool", pool_sp);
                	params.put("fechaIni", fechaIni);
                    params.put("fechaFin", fechaFin);
                    params.put("regimen", regimen);
                    params.put("criterio", criterio);
                    params.put("valor", valor);
                    params.put("mov", mov);
                	ArrayList detalleReporte = rd.calificado(params, seguridad);
                	//JVV-fin
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }

            if (tipoR.equals("reporte")) {
            	//JVV-ini
            	request.setAttribute("regimen", regimen);
            	//JVV-fin
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);
                request.setAttribute("mov", mov);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/ReporteCalificadoM.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelCalificado.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REGISTRO DE ASISTENCIA"); 		//jquispecoi 05/2014
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de tipos de licencia
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarTipoLicencia(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
			
			String codUO = bUsuario.getCodUO();
			//HashMap roles = super.getRoles(session);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

				MantenimientoDelegate md = new MantenimientoDelegate();
	            ArrayList tipos = md.buscarMovimientos(pool_sp, "0", "",
	                    Constantes.CORREL_LICENCIA);

	            session.removeAttribute("tiposLicencia");
	            session.setAttribute("tiposLicencia", tipos);

	            session.removeAttribute("detalleReporte");

	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteTipoLicencia.jsp");
	            dispatcher.forward(request, response);
	            return;
	            
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (MantenimientoException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de tipos de licencia
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarTipoLicencia(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            ArrayList tiposLicencia = (ArrayList) session
                    .getAttribute("tiposLicencia");
            String tipo = request.getParameter("cmbTipo");
            String tp = tipo;
            String quiebre = request.getParameter("cmbQuiebre");

            if (!tipo.equals("-1")) {
                BeanTipoMovimiento mov = (BeanTipoMovimiento) tiposLicencia
                        .get(Integer.parseInt(tipo));
                tipo = mov.getMov();
            }
            log.debug("TipoFinal:"+tipo);
            String criterio = request.getParameter("cmbCriterio");
            //String valor = request.getParameter("txtValor");
            //DTARAZONA - SE AGREGÓ LA OPCION INTENDENCIA
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor").trim().toUpperCase():""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();
            
            String codInten = request.getParameter("cod_intendencia").trim();
            if (log.isDebugEnabled()) log.debug("codInten: "+codInten);
			if (criterio.trim().equals("4")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);
            
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool",pool_sp);
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("tipo", tipo);
                mapa.put("tipoQuiebre", quiebre);
                mapa.put("usuario", usuario);
                mapa.put("fechaIni", fechaIni);
                mapa.put("fechaFin", fechaFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoReportePorTipoLicencia(mapa);
                log.debug("Paso 0: "+pool_sp);	
                request.setAttribute("nomReporte",
                        "REPORTE POR TIPO DE LICENCIA");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {

            if (!repetir) {
                ArrayList detalleReporte = rd
                        .tipoLicencia(pool_sp, tipo, fechaIni, fechaFin,
                                criterio, valor, quiebre, seguridad);
                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("tipo", tp);
                request.setAttribute("tipoQuiebre", quiebre);
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteTipoLicencia.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("tipoQuiebre", quiebre);
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelTipoLicencia.jsp");
                dispatcher.forward(request, response);
                return;
            }
            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte acumulado de licencia
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarAcumuladoLicencia(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

            MantenimientoDelegate md = new MantenimientoDelegate();
            ArrayList tipos = md.buscarMovimientos(pool_sp, "0", "",
                    Constantes.CORREL_LICENCIA);

            session.removeAttribute("tiposLicencia");
            session.setAttribute("tiposLicencia", tipos);

            session.removeAttribute("detalleReporte");

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/reporteAcumuladoLicencia.jsp");
            dispatcher.forward(request, response);
            return;

        } catch (MantenimientoException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte acumulado de licencias
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarAcumuladoLicencia(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario=bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
        	
            //HashMap roles = super.getRoles(session);
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            ArrayList tiposLicencia = (ArrayList) session
                    .getAttribute("tiposLicencia");
            String tipo = request.getParameter("cmbTipo");
            String tp = tipo;
            String quiebre = request.getParameter("cmbQuiebre");

            if (!tipo.equals("-1")) {
                BeanTipoMovimiento mov = (BeanTipoMovimiento) tiposLicencia
                        .get(Integer.parseInt(tipo));
                tipo = mov.getMov();
            }

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            
            if (tipoR.equals("imprimir")) {
            	HashMap datos=new HashMap();
            	datos.put("dbpool", pool_sp);
            	datos.put("tipo", tipo);
            	datos.put("fechaIni", fechaIni);
            	datos.put("fechaFin", fechaFin);
            	datos.put("criterio", criterio);
            	datos.put("tipoQuiebre", quiebre);
            	datos.put("codPers", codigo);
            	datos.put("usuario", usuario);
            	datos.put("valor", valor);
            	datos.put("seguridad", seguridad);
                rd.masivoAcumuladoLicencia(datos);
                request.setAttribute("nomReporte",
                        "REPORTE ACUMULADO DE LICENCIAS");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {
	            if (!repetir) {
	                ArrayList detalleReporte = rd
	                        .acumuladoLicencia(pool_sp, tipo, fechaIni, fechaFin,
	                                criterio, valor, quiebre, seguridad);
	                session.removeAttribute("detalleReporte");
	                session.setAttribute("detalleReporte", detalleReporte);
	            }
	            if (tipoR.equals("reporte")) {
	                request.setAttribute("tipo", tp);
	                request.setAttribute("tipoQuiebre", quiebre);
	                request.setAttribute("criterio", criterio);
	                request.setAttribute("valor", valor);
	                request.setAttribute("fechaIni", fechaIni);
	                request.setAttribute("fechaFin", fechaFin);
	
	                RequestDispatcher dispatcher = getServletContext()
	                        .getRequestDispatcher("/reporteAcumuladoLicencia.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
	
	            if (tipoR.equals("excel")) {
	                request.setAttribute("tipoQuiebre", quiebre);
	                request.setAttribute("fechaIniExcel", fechaIni);
	                request.setAttribute("fechaFinExcel", fechaFin);
	                RequestDispatcher dispatcher = getServletContext()
	                        .getRequestDispatcher("/excelAcumuladoLicencia.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
	
	            if (tipoR.equals("imprimir")) {
	                RequestDispatcher dispatcher = getServletContext()
	                        .getRequestDispatcher("/reporteAcumuladoLicencia.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de turnos de trabajo
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarTurnosTrabajo(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();

			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) 
					|| (roles.get(Constantes.ROL_JEFE)!=null))  {

	            session.removeAttribute("detalleReporte");
	            session.removeAttribute("operativo");
			    session.setAttribute("operativo", roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				if (log.isDebugEnabled()) log.debug("operativo: " + roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				session.removeAttribute("jefe");
			    session.setAttribute("jefe", roles.get(Constantes.ROL_JEFE));
				if (log.isDebugEnabled()) log.debug("jefe: " + roles.get(Constantes.ROL_JEFE));	            
				

	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteTurnosTrabajo.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de turnos de trabajo
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarTurnosTrabajo(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            //JVV-ini
            String regimen = request.getParameter("cmbRegimen");
            //JVV-fin
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) { //Intendencia
				valor = codInten;
			}            
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
                    
			if (tipoR.equals("imprimir")) {
				HashMap params = new HashMap();
            	params.put("pool_sp", pool_sp);
            	params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("regimen", regimen);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("codPers", codigo);
                //rd.masivoMarcaciones(pool_sp, params, usuario);
				rd.masivoTurnos(pool_sp, params, usuario, seguridad);

			} else {
				if (!repetir) {
	            	//JVV-ini --se agrego regimen 
					HashMap params = new HashMap();
	            	params.put("dbpool", pool_sp);
	            	params.put("fechaIni", fechaIni);
	                params.put("fechaFin", fechaFin);
	                params.put("regimen", regimen);
	                params.put("criterio", criterio);
	                params.put("valor", valor);
	                params.put("seguridad", seguridad);
	                
	            	ArrayList detalleReporte = rd.turnosTrabajo(params);
	            	//JVV-fin
	                session.removeAttribute("detalleReporte");
	                session.setAttribute("detalleReporte", detalleReporte);
	            }
			}

            if (tipoR.equals("reporte")) {            	
            	request.setAttribute("regimen", regimen);            	
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteTurnosTrabajo.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelTurnosTrabajo.jsp");
                dispatcher.forward(request, response);
                return;
            }
            
            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE DE TURNOS DE TRABAJO");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**PRAC-ASANCHEZ 25/08/2009
     * Metodo encargado de cargar la pagina del Reporte de Labor Excepcional
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarLaborExcepcional(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
			session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima("jdbc/dgsp"));		//jquispecoi PAS20155E230000073
        	
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			
			session.setAttribute("esAnalLexc", "NO");
			
			
			//JRR - 22/09/2009
			if (log.isDebugEnabled()) log.debug("pre uoSeg : "+uoSeg);
			if (roles.get(Constantes.ROL_ANALISTA_LEXC)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista LE");
				uoSeg = uoSeg.substring(0,4).concat("%");
				session.setAttribute("esAnalLexc", "SI");
			}
			
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista Operativo");
				uoSeg = bUsuario.getVisibilidad();
				uoSeg = uoSeg.substring(0,2).concat("%");
			}
			if (log.isDebugEnabled()) log.debug("final uoSeg: "+uoSeg);
			//
			//if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			//seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) 
					||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null)
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)
					//JRR - 22/09/2009
					|| (roles.get(Constantes.ROL_ANALISTA_LEXC)!=null)
					//
				)  {
	            session.removeAttribute("detalleReporte");

	            /*RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/ReporteLaborExcepcional.jsp");
	            dispatcher.forward(request, response);*/
	            forward(request,response,"/ReporteLaborExcepcional.jsp?idSession="+System.currentTimeMillis());
	            return;

			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}
        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor VERIFIQUE CON INRH.");
			session.setAttribute("beanErr", bean);
			/*RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);*/
			forward(request,response,"/PagE.jsp?idSession="+System.currentTimeMillis());
        }

    }

    /**PRAC-ASANCHEZ 26/08/2009
     * Metodo encargado de generar el reporte de Labor Excepcional
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarLaborExcepcional(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
            String uoSeg = bUsuario.getVisibilidad();
            
			//JRR - 22/09/2009
			if (log.isDebugEnabled()) log.debug("pre uoSeg : "+uoSeg);
			if (roles.get(Constantes.ROL_ANALISTA_LEXC)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista LE");
				uoSeg = uoSeg.substring(0,4).concat("%");
			}
			
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) {
				if (log.isDebugEnabled()) log.debug("Es analista Operativo");
				uoSeg = bUsuario.getVisibilidad();
				uoSeg = uoSeg.substring(0,2).concat("%");
			}
			if (log.isDebugEnabled()) log.debug("final uoSeg: "+uoSeg);
			//
            //if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            Map seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
        
            HashMap params = new HashMap();
            params.put("dbpool", pool_sp);
            params.put("fechaIni", fechaIni);
            params.put("fechaFin", fechaFin);
            params.put("criterio", criterio);
            params.put("valor", valor);
            params.put("codPers", codigo);
            params.put("seguridad", seguridad); 
            params.put("codUsuario", codigo);
            if (tipoR.equals("imprimir")) {
                rd.masivoLaborExcepcional(pool_sp, params, usuario);
            } else {
                if (!repetir) {
                	List detalleReporte = rd.laborExcepcional(params, seguridad);
                	session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                /*RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/ReporteLaborExcepcional.jsp");

                dispatcher.forward(request, response);*/
                forward(request,response,"/ReporteLaborExcepcional.jsp?idSession="+System.currentTimeMillis());
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                /*RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/ExcelLaborExcepcional.jsp");
                dispatcher.forward(request, response);*/
                forward(request,response,"/ExcelLaborExcepcional.jsp?idSession="+System.currentTimeMillis());
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE DE LABOR EXCEPCIONAL");
                /*RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);*/
                forward(request,response,"/resultadoReporte.jsp?idSession="+System.currentTimeMillis());
                return;

            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            /*RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);*/
            forward(request,response,"/PagE.jsp?idSession="+System.currentTimeMillis());
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            /*RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);*/
            forward(request,response,"/PagE.jsp?idSession="+System.currentTimeMillis());
        }

    }
    
    /**
     * Metodo encargado de generar el reporte de horas extras
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     *
    private void generarHoraExtra(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
			String indDia = request.getParameter("chk_ind_dia")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
			String indMin = request.getParameter("chk_ind_min")!= null ? Constantes.ACTIVO
					: Constantes.INACTIVO;
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);
                params.put("indDia", indDia);
                params.put("indMin", indMin);
                rd.masivoHoraExtra(pool_sp, params, usuario);

            } else {
            	if (!repetir) {
            		ArrayList detalleReporte = rd.horaExtra(pool_sp, fechaIni,
            				fechaFin, criterio, valor, seguridad, indDia, indMin);
            		session.removeAttribute("detalleReporte");
            		session.setAttribute("detalleReporte", detalleReporte);
            	}
            }



            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);
                

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteHoraExtra.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelHoraExtra.jsp");
                dispatcher.forward(request, response);
                return;
            }
            
            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte", "REPORTE DE LABOR EXCEPCIONAL");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
*/
    /**
     * Metodo encargado de cargar la pagina del reporte de compensaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarCompensaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  )  {

	            session.removeAttribute("detalleReporte");
	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteCompensaciones.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de compensaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarCompensaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String quiebre = request.getParameter("cmbQuiebre");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            if (!repetir) {
                ArrayList detalleReporte = rd.compensaciones(pool_sp, fechaIni,
                        fechaFin, criterio, valor, quiebre, seguridad);
                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("reporte")) {
                request.setAttribute("tipoQuiebre", quiebre);
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteCompensaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("tipoQuiebre", quiebre);
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelCompensaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de resumen mensual
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarResumenMensual(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	String fechaMinima = Utiles.obtenerFechaMinima(pool_sp);
        	fechaMinima = fechaMinima.substring(6, 10) + fechaMinima.substring(3, 5);

            MantenimientoDelegate md = new MantenimientoDelegate();
            ArrayList periodos = md.cargarPeriodos(pool_sp);

            //ICR-MEMO
            ArrayList periodosFiltrados = new ArrayList();
            if (periodos!=null && periodos.size()>0){
            	 for(int i=0; i< periodos.size(); i++){
            		 BeanPeriodo periodo = (BeanPeriodo)periodos.get(i);
            		 if (periodo.getPeriodo().compareTo(fechaMinima)>=0)	//jquispecoi PAS20155E230000073
            			 periodosFiltrados.add(periodo);                	
                 }
            } 
            session.removeAttribute("listaPeriodos");
            //session.setAttribute("listaPeriodos", periodos);
            session.setAttribute("listaPeriodos", periodosFiltrados);
            //ICR-MEMO

            session.removeAttribute("detalleReporte");

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/reporteResumenMensual.jsp");
            dispatcher.forward(request, response);
            return;

        } catch (MantenimientoException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de resumen mensual
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarResumenMensual(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String usuario = bUsuario.getLogin();
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            //String valor = request.getParameter("txtValor");
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):"";
            
            //ASANCHEZZ 20100424
			String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) {
				valor = codInten;
			}
			String regimen = request.getParameter("cmbTipoDL");
            //FIN
            
            String periodo = request.getParameter("cmbPeriodo");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            MantenimientoDelegate md = new MantenimientoDelegate();
            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            BeanPeriodo bPer = md.buscarPeriodoCodigo(pool_sp, periodo);

            if (tipoR.equals("imprimir")) {

                HashMap params = new HashMap();
                params.put("periodo", periodo);
                params.put("fechaIni", bPer.getFechaIni());
                params.put("fechaFin", bPer.getFechaFin());
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);
                params.put("regimen", regimen);
                rd.masivoResumenMensual(pool_sp, params, usuario);

            } else {

                if (!repetir) {
                	//ASANCHEZZ 20100424
                	/*
                	ArrayList detalleReporte = rd.resumenMensual(pool_sp,
                            periodo, criterio, valor, seguridad);
                    */
                	Map mapa = new HashMap();
                	mapa.put("dbpool", pool_sp);
                	mapa.put("periodo", periodo);
                	mapa.put("criterio", criterio);
                	mapa.put("valor", valor);
                	mapa.put("seguridad", seguridad);
                	mapa.put("regimen", regimen);
                    List detalleReporte = rd.resumenMensual(mapa);
                    //FIN
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }

            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("per", periodo);
                request.setAttribute("regimen", regimen);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteResumenMensual.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", bPer.getFechaIni());
                request.setAttribute("fechaFinExcel", bPer.getFechaFin());

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelResumenMensual.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE RESUMEN MENSUAL DE ASISTENCIA");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de resumen diario
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarResumenDiario(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	String fechaMinima = Utiles.obtenerFechaMinima(pool_sp);
        	fechaMinima = fechaMinima.substring(6, 10) + fechaMinima.substring(3, 5);
        	
            MantenimientoDelegate md = new MantenimientoDelegate();
            ArrayList periodos = md.cargarPeriodos(pool_sp);
            
            //ICR-MEMO
            ArrayList periodosFiltrados = new ArrayList();
            if (periodos!=null && periodos.size()>0){
            	 for(int i=0; i< periodos.size(); i++){
            		 BeanPeriodo periodo = (BeanPeriodo)periodos.get(i);
            		 if (periodo.getPeriodo().compareTo(fechaMinima)>=0)	//jquispecoi PAS20155E230000073
            			 periodosFiltrados.add(periodo);
                 }
            } 
            session.removeAttribute("listaPeriodos");
            //session.setAttribute("listaPeriodos", periodos);
            session.setAttribute("listaPeriodos", periodosFiltrados);
            //ICR-MEMO

            session.removeAttribute("detalleReporte");

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/reporteResumenDiario.jsp");
            dispatcher.forward(request, response);
            return;

        } catch (MantenimientoException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de resumen diario
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarResumenDiario(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            //String valor = request.getParameter("txtValor");
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):"";
            
            //JROJASR 29/04/2010
			String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) {
				valor = codInten;
			}
			String regimen = request.getParameter("cmbTipoDL");
            //FIN
            
            String periodo = request.getParameter("cmbPeriodo");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            MantenimientoDelegate md = new MantenimientoDelegate();
            BeanPeriodo bPer = md.buscarPeriodoCodigo(pool_sp, periodo);

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            //EBENAVID -FORMATIVAS 16/06/2011        
			if (regimen.trim().equals("1")) {
				bPer.setFechaIni(bPer.getFechaIniCAS());
				bPer.setFechaFin(bPer.getFechaFinCAS());
			} else{
				if (regimen.trim().equals("2")) {
					bPer.setFechaIni(bPer.getFechaIniModForm());
					bPer.setFechaFin(bPer.getFechaFinModForm());
				}
			}
			//FIN EBENAVID -FORMATIVAS 16/06/2011

            if (tipoR.equals("imprimir")) {

                HashMap params = new HashMap();
                params.put("periodo", periodo);
                params.put("fechaIni", bPer.getFechaIni());
                params.put("fechaFin", bPer.getFechaFin());
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);
                params.put("regimen", regimen);
                rd.masivoResumenDiario(pool_sp, params, usuario);

            } else {

                if (!repetir) {
                	//ArrayList detalleReporte = rd.resumenDiario(pool_sp,periodo, criterio, valor, seguridad);
                	//JROJASR - 29/04/2010	
                	Map mapa = new HashMap();
                	mapa.put("dbpool", pool_sp);
                	mapa.put("periodo", periodo);
                	mapa.put("criterio", criterio);
                	mapa.put("valor", valor);
                	mapa.put("seguridad", seguridad);
                	mapa.put("regimen", regimen);
                    List detalleReporte = rd.resumenDiario(mapa);
                    //
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                    
                }
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("fechaIniExcel", bPer.getFechaIni());
                request.setAttribute("fechaFinExcel", bPer.getFechaFin());

                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("per", periodo);
                request.setAttribute("regimen", regimen);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteResumenDiario.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", bPer.getFechaIni());
                request.setAttribute("fechaFinExcel", bPer.getFechaFin());

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelResumenDiario.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE RESUMEN DIARIO DE ASISTENCIA");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de vacaciones gozadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarVacacionesGozadas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

	            session.removeAttribute("detalleReporte");

	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteVacacionesGozadas.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de vacaciones gozadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarVacacionesGozadas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
            String regimen = request.getParameter("cmbRegimen");
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String dias = request.getParameter("txtDias");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            //JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS         
            String codInten = request.getParameter("cod_intendencia");
        	if (criterio.trim().equals("5")) {
        		valor = codInten;
        	} //FIN - JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS     
            
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            dias = dias.trim().equals("") ? "0" : dias;

            ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool", pool_sp);
                mapa.put("regimen", regimen); //JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("dias", dias);
                mapa.put("fechaIni", fechaIni);
                mapa.put("fechaFin", fechaFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoVacacionesEfectuadas(mapa, usuario);

                request.setAttribute("nomReporte",
                        "REPORTE DE VACACIONES EFECTUADAS");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {

            if (!repetir) {
                ArrayList detalleReporte = rd.vacacionesGozadas(pool_sp, regimen, //JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS
                        fechaIni, fechaFin, criterio, valor, dias, seguridad);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }


            if (tipoR.equals("reporte")) {
            	request.setAttribute("regimen", regimen);//JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);
                request.setAttribute("dias", dias);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteVacacionesGozadas.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {

                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);
                request.setAttribute("dias", dias);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelVacacionesGozadas.jsp");
                dispatcher.forward(request, response);
                return;
            }
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de vacaciones pendientes
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarVacacionesPendientes(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  )  {

	            session.removeAttribute("detalleReporte");
	            session.removeAttribute("listaAnhos");

	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteVacacionesPendientes.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de vacaciones pendientes
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarVacacionesSaldos(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String regimen = request.getParameter("cmbRegimen"); //JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String dias = request.getParameter("txtDias");
            String anhoIni = request.getParameter("anhoIni");
            String anhoFin = request.getParameter("anhoFin");

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            
            //JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
            String codInten = request.getParameter("cod_intendencia");
        	if (criterio.trim().equals("5")) {
        		valor = codInten;
        	}//FIN - JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS

            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            dias = dias.trim().equals("") ? "0" : dias;

            ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool", pool_sp);
                mapa.put("regimen", regimen);
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("dias", dias);
                mapa.put("anhoIni", anhoIni);
                mapa.put("anhoFin", anhoFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoVacacionesPendientes(mapa, usuario);

                request.setAttribute("nomReporte",
                        "REPORTE DE SALDOS VACACIONALES");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {
                if (!repetir) {
                		
                    ArrayList detalleReporte = rd.vacacionesPendientes(pool_sp,
                            regimen, anhoIni, anhoFin, criterio, valor, dias, seguridad);//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS

                    ArrayList listaAnhos = rd.buscarAnnosVacacionesPendientes(
                            pool_sp, anhoIni, anhoFin, criterio, valor, dias,
                            seguridad);

                    session.removeAttribute("detalleReporte");
                    session.removeAttribute("listaAnhos");

                    session.setAttribute("detalleReporte", detalleReporte);
                    session.setAttribute("listaAnhos", listaAnhos);
                }

                if (tipoR.equals("reporte")) {
                	request.setAttribute("regimen", regimen); //JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
                	request.setAttribute("criterio", criterio);
                    request.setAttribute("valor", valor);
                    request.setAttribute("aIni", anhoIni);
                    request.setAttribute("aFin", anhoFin);
                    request.setAttribute("dias", dias);

                    RequestDispatcher dispatcher = getServletContext()
                            .getRequestDispatcher(
                                    "/reporteVacacionesSaldos.jsp");

                    dispatcher.forward(request, response);
                    return;
                }

                if (tipoR.equals("excel")) {

                    request.setAttribute("criterio", criterio);
                    request.setAttribute("aIni", anhoIni);
                    request.setAttribute("aFin", anhoFin);
                    request.setAttribute("dias", dias);

                    RequestDispatcher dispatcher = getServletContext()
                            .getRequestDispatcher("/excelVacacionesSaldos.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de vacaciones pendientes
     * y gozadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarVacacionesSaldos(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

            session.removeAttribute("detalleReporte");
            session.removeAttribute("listaAnhos");

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/reporteVacacionesSaldos.jsp");
            dispatcher.forward(request, response);
            return;

        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de vacaciones pendientes y
     * programadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarVacacionesPendientes(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            //String valor = request.getParameter("txtValor");
            String dias = request.getParameter("txtDias");
            String anhoIni = request.getParameter("anhoIni");
            String anhoFin = request.getParameter("anhoFin");
            
            //DTARAZONA - SE AGREGÓ LA OPCION INTENDENCIA
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor").trim().toUpperCase():""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();
            
            String codInten = request.getParameter("cod_intendencia").trim();
            if (log.isDebugEnabled()) log.debug("codInten: "+codInten);
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            dias = dias.trim().equals("") ? "0" : dias;

            ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool",pool_sp);
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("dias", dias);
                mapa.put("anhoIni", anhoIni);
                mapa.put("anhoFin", anhoFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoVacacionesEfectivasPendientes(mapa, usuario);
                log.debug("Paso 0: "+pool_sp);	
                request.setAttribute("nomReporte",
                        "REPORTE DE VACACIONES EFECTIVAS PENDIENTES");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {

            if (!repetir) {
                ArrayList detalleReporte = rd.vacacionesPendientesyProg(
                        pool_sp, anhoIni, anhoFin, criterio, valor, dias,
                        seguridad);
                ArrayList listaAnhos = rd.buscarAnnosVacacionesPendientes(
                        pool_sp, anhoIni, anhoFin, criterio, valor, dias,
                        seguridad);

                session.removeAttribute("detalleReporte");
                session.removeAttribute("listaAnhos");

                session.setAttribute("detalleReporte", detalleReporte);
                session.setAttribute("listaAnhos", listaAnhos);
            }

            
            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("aIni", anhoIni);
                request.setAttribute("aFin", anhoFin);
                request.setAttribute("dias", dias);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher(
                                "/reporteVacacionesPendientes.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("aIni", anhoIni);
                request.setAttribute("aFin", anhoFin);
                request.setAttribute("dias", dias);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelVacacionesPendientes.jsp");
                dispatcher.forward(request, response);
                return;
            }
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    //dtarazona 
    /**
     * Metodo encargado de generar el reporte de vacaciones pendientes y
     * programadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void estadisticoAnualVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
    	if(log.isDebugEnabled()) log.debug("method estadisticoAnualVacaciones");
    	
        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles: " + roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");            
            String anhoIni = request.getParameter("anhoIni");
            String anhoFin = request.getParameter("anhoFin");            

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            log.debug("repetir: " + repetir);
            if (!repetir) {
                ArrayList detalleReporte = rd.estadisticoAnualVacaciones(pool_sp, anhoIni, anhoFin, criterio, valor,seguridad);
                //ArrayList listaAnhos = rd.buscarAnnosVacacionesPendientes(pool_sp, anhoIni, anhoFin, criterio, valor,seguridad);

                session.removeAttribute("detalleReporte");
                session.removeAttribute("listaAnhos");

                session.setAttribute("detalleReporte", detalleReporte);
                if(log.isDebugEnabled()) log.debug("detalleReporte: "+detalleReporte);
                //session.setAttribute("listaAnhos", listaAnhos);
            }

            log.debug("tipoR: " + tipoR);
            if (tipoR.equals("reporte")) {
            	T02DAO personalDAO = new T02DAO();
            	String nombres=personalDAO.findNombreCompletoByCodPers(pool_sp, valor).toString(); 
            	request.setAttribute("codPers", valor);
            	request.setAttribute("nombres", nombres);
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("aIni", anhoIni);
                request.setAttribute("aFin", anhoFin);                

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher(
                                "/estadisticoAnualVacaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }
            
            if (tipoR.equals("excel")) {
            	request.setAttribute("criterio", criterio);
                request.setAttribute("aIni", anhoIni);
                request.setAttribute("aFin", anhoFin); 
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelEstadisticoAnualVacaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }            
        } catch (ReporteException e) {
        	log.error("Ha ocurrido un error en estadisticoAnualVacaciones: " + e.getMessage(), e);
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
        	log.error("Ha ocurrido un error en estadisticoAnualVacaciones: " + e.getMessage(), e);
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    /**
     * Metodo encargado de cargar la pagina del estadistico anual de vacaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarEstadisticoAnualVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_JEFE)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) || (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) )  {

	            session.removeAttribute("detalleReporte");
	            session.removeAttribute("listaAnhos");

	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/estadisticoAnualVacaciones.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    
    /**
     * Metodo encargado de generar el reporte de vacaciones pendientes y
     * programadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarRepPersNoGenSaldoVac(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
    	if(log.isDebugEnabled()) log.debug("method estadisticoAnualVacaciones");
    	
        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles: " + roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");            
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");            

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            log.debug("repetir: " + repetir);
            if (!repetir) {
                ArrayList detalleReporte = rd.personalNoGeneroSaldoVacacional(pool_sp, fechaIni, fechaFin, criterio, valor,seguridad);

                session.removeAttribute("detalleReporte");
                session.removeAttribute("listaAnhos");

                session.setAttribute("detalleReporte", detalleReporte);
                if(log.isDebugEnabled()) log.debug("detalleReporte: "+detalleReporte);
            }

            log.debug("tipoR: " + tipoR);
            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);                

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher(
                                "/personalNoGeneroSaldo.jsp");
                dispatcher.forward(request, response);
                return;
            }
            
            if (tipoR.equals("excel")) {
            	request.setAttribute("criterio", criterio);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin); 
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelPersonalNoGeneroSaldo.jsp");
                dispatcher.forward(request, response);
                return;
            }            
        } catch (ReporteException e) {
        	log.error("Ha ocurrido un error en estadisticoAnualVacaciones: " + e.getMessage(), e);
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
        	log.error("Ha ocurrido un error en estadisticoAnualVacaciones: " + e.getMessage(), e);
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    /**
     * Metodo encargado de cargar la pagina del estadistico anual de vacaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarRepPersNoGenSaldoVac(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			HashMap param1=new HashMap();
            param1.put("t99cod_tab", "510");
            param1.put("t99codigo", "48");
            param1.put("dbpool",pool_sp);
            String fec_impl=ad.findParamByCodTabCodigo(param1);
            
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_JEFE)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  )  {

	            session.removeAttribute("detalleReporte");
	            session.removeAttribute("listaAnhos");
	            
	            session.removeAttribute("fec_impl");
				session.setAttribute("fec_impl", fec_impl);
	            
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/personalNoGeneroSaldo.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    //dtarazona
    
    /**
     * Metodo encargado de cargar la pagina del reporte de vacaciones
     * compensadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarVacacionesCompensadas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  )  {

	            session.removeAttribute("detalleReporte");

	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteVacacionesCompensadas.jsp");
	            dispatcher.forward(request, response);
	            return;
	            
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de vacaciones compensadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarVacacionesCompensadas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            //String valor = request.getParameter("txtValor");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
                    
           //DTARAZONA - SE AGREGÓ LA OPCION INTENDENCIA
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor").trim().toUpperCase():""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();
            
            String codInten = request.getParameter("cod_intendencia").trim();
            if (log.isDebugEnabled()) log.debug("codInten: "+codInten);
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);

            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool", pool_sp);
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("fechaIni", fechaIni);
                mapa.put("fechaFin", fechaFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoVacacionesCompensadas(mapa, usuario);

                request.setAttribute("nomReporte",
                        "REPORTE DE VACACIONES COMPENSADAS");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {
  
            if (!repetir) {
                ArrayList detalleReporte = rd.vacacionesCompensadas(pool_sp,
                        fechaIni, fechaFin, criterio, valor, seguridad);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }


            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher(
                                "/reporteVacacionesCompensadas.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelVacacionesCompensadas.jsp");
                dispatcher.forward(request, response);
                return;
            }
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de vacaciones por uo
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarVacacionesUnidad(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

            session.removeAttribute("detalleReporte");

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/reporteVacacionesUnidad.jsp");
            dispatcher.forward(request, response);
            return;

        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de vacaciones por unidad
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarVacacionesUnidad(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            if (!repetir) {
                ArrayList detalleReporte = rd.resumenVacacionesUOrg(pool_sp,
                        fechaIni, fechaFin, seguridad);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("reporte")) {
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteVacacionesUnidad.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelVacacionesUnidad.jsp");
                dispatcher.forward(request, response);
                return;
            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de goce efectivo
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     *
    private void cargarVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

	            session.removeAttribute("detalleReporte");

	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteVacaciones.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
*/
    
/* JRR - 19/05/2010 - METODO COMSA PROGRAMACION */
	/**
	 * Metodo encargado de cargar la pagina del reporte de goce efectivo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
	throws ServletException, IOException {
		
		try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			getRoles(request, response, session,bUsuario);
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();
			
			///aca segmentamos
			if ("usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())) uoSeg = uoSeg.substring(0,2).concat("%");
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", session.getAttribute("perfil_usuario").toString());
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			
			//AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ("usuario_Jefe".equals(session.getAttribute("perfil_usuario").toString()) ||
    			"usuario_AnalisCentral".equals(session.getAttribute("perfil_usuario").toString()) ||
    			"usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())){
    			//--/
				session.removeAttribute("detalleReporte");
				request.setAttribute("tipoQuiebre", "0");
				this.forward(request, response, "/reporteVacaciones.jsp");
				// /--
			}
			else {
				throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}
			//--/
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
			.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
		
	}

	
	/**
	 * Obtiene el archivo de propiedades
	 * @return Archivo de Propiedades 
	 * @since 03-oct-2007
	 */
	public Propiedades getPropiedades() {
		return (new Propiedades(getClass(), ROLES_PROPERTIES_FILE_NAME));
	}
	
	/**
	 * Metodo que se encarga de obtener los Roles del Usuario
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @param bUsuario UsuarioBean
	 * @throws ServletException
	 * @throws IOException
	 */
	public void getRoles(HttpServletRequest request,
			HttpServletResponse response, HttpSession session, UsuarioBean bUsuario)
	throws ServletException,IOException {
		
		Map roles = (HashMap)MenuCliente.getRoles(bUsuario);
	
		if (roles.get(getPropiedades().leePropiedad("ROL_EMPLEADO"))!=null){
			session.setAttribute("perfil_usuario", "usuario_Empleado");
		}else if (roles.get(getPropiedades().leePropiedad("ROL_JEFE"))!=null){
			session.setAttribute("perfil_usuario", "usuario_Jefe");
		}else if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_CENTRAL"))!=null){
			session.setAttribute("perfil_usuario", "usuario_AnalisCentral");
		}else if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_OPERATIVO"))!=null){
			session.setAttribute("perfil_usuario", "usuario_AnalisOperativo");
		}/*else if (roles.get(getPropiedades().leePropiedad("ROL_ANA_OPERATIVO"))!=null){ //JVILLACORTA-PASE 2010-701 (01/06/2011)
			session.setAttribute("perfil_usuario", "usuario_AnaOperativo");
		}*/else{
			forward(request, response, "/PagInhabilitado.jsp");
			
		}
	}
	
	
/*     */
    
    
    /**
     * Metodo encargado de generar el reporte de goce efectivo
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     *
    private void generarVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String dias = request.getParameter("txtDias");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");

            String tipoR = request.getParameter("tipoReporte") != null ? request.getParameter("tipoReporte") : "reporte";

            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");

            dias = dias.trim().equals("") ? "0" : dias;

            ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool", pool_sp);
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("dias", dias);
                mapa.put("fechaIni", fechaIni);
                mapa.put("fechaFin", fechaFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoVacaciones(mapa, usuario);

                request.setAttribute("nomReporte",
                        "REPORTE DE VACACIONES");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {

            if (!repetir) {
                ArrayList detalleReporte = rd.vacacionesGoceEfectivo(pool_sp,
                        fechaIni, fechaFin, criterio, valor, dias, seguridad);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }


            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);
                request.setAttribute("dias", dias);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteVacaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);
                request.setAttribute("dias", dias);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelVacaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }
            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
*/
	
/* JRR - 19/05/2010 - METODO COMSA PROGRAMACION */
	/**
	 * Metodo encargado de generar el reporte de goce efectivo
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void generarVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
	throws ServletException, IOException {
		
		try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
			
			getRoles(request, response, session, bUsuario);			
			
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
			String uuoo = bUsuario.getCodUO();

			///aca segmentamos
			if ("usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())) uoSeg = uoSeg.substring(0,2).concat("%");
			if ("usuario_AnalisCentral".equals(session.getAttribute("perfil_usuario").toString())) uoSeg = "%"; //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas

			
			HashMap seguridad = new HashMap();
			//seguridad.put("roles", session.getAttribute("perfil_usuario").toString()); ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
			seguridad.put("perfil_usuario", session.getAttribute("perfil_usuario").toString()); //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
			seguridad.put("roles",roles);//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad reporte vacaciones y reporte vacaciones efectuadas
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uuoo", uuoo);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			log.debug("seguridad: "+seguridad);
			String criterio = request.getParameter("cmbCriterio");
			String valor = request.getParameter("txtValor");
			String dias = request.getParameter("txtDias");
			String fechaIni = request.getParameter("fechaIni");
			String fechaFin = request.getParameter("fechaFin");
			
			String tipoR = request.getParameter("tipoReporte") != null ? request.getParameter("tipoReporte") : "reporte";
			
			boolean repetir = request.getParameter("repetir").toString().trim().equals("true");
			if(log.isDebugEnabled()){
				log.debug("tipoR-->" + tipoR);
				log.debug("request.getParameter(repetir).toString()-->" + request.getParameter("repetir").toString());
			}
			dias = dias.trim().equals("") ? "0" : dias;
			
			ReporteDelegate rd = new ReporteDelegate();
			
			if (tipoR.equals("imprimir")) {
				HashMap mapa = new HashMap();
				
				mapa.put("dbpool", pool_sp);
				mapa.put("criterio", criterio);
				mapa.put("valor", valor);
				mapa.put("dias", dias);
				mapa.put("fechaIni", fechaIni);
				mapa.put("fechaFin", fechaFin);
				mapa.put("seguridad", seguridad);
				mapa.put("codPers", codigo);
				
				rd.masivoVacaciones(mapa, usuario);
				
				request.setAttribute("nomReporte",
				"REPORTE DE VACACIONES");
				forward(request, response, "/resultadoReporte.jsp");
				return;
				
			} else {
				
				if (!repetir) {
					if(log.isDebugEnabled()){
						log.debug("repetir FALSE-- desde el Servlet");
						log.debug("dbpool-->" + pool_sp);
						log.debug("criterio-->" + criterio);
						log.debug("valor-->" + valor);
						log.debug("fechaIni-->" + fechaIni);
						log.debug("fechaFin-->" + fechaFin);
						log.debug("seguridad-->" + seguridad);
					}
					HashMap mapa = new HashMap();
					
					mapa.put("dbpool", pool_sp);
					mapa.put("criterio", criterio);
					mapa.put("valor", valor);
					mapa.put("dias", dias);
					mapa.put("fechaIni", fechaIni);
					mapa.put("fechaFin", fechaFin);
					mapa.put("seguridad", seguridad);
					mapa.put("codPers", codigo);
					//Map detalleFinal = null;
					List detalleReporte = rd.vacacionesGoceEfectivo(mapa);
					if(log.isDebugEnabled()) log.debug("comsa servlet reporte---->" + detalleReporte.size());
					if(log.isDebugEnabled()) log.debug("comsa servlet reporte---->" + detalleReporte);
					session.removeAttribute("detalleReporte");
					setAttribute(session,"detalleReporte", detalleReporte);
				}
				
				
				if (tipoR.equals("reporte")) {
					request.setAttribute("criterio", criterio);
					request.setAttribute("valor", valor);
					request.setAttribute("fini", fechaIni);
					request.setAttribute("ffin", fechaFin);
					request.setAttribute("dias", dias);
					if(log.isDebugEnabled()) log.debug("al JSP");
					
					forward(request, response, "/reporteVacaciones.jsp");
					
					return;
				}
				
				if (tipoR.equals("excel")) {
					request.setAttribute("fini", fechaIni);
					request.setAttribute("ffin", fechaFin);
					request.setAttribute("dias", dias);
					if(log.isDebugEnabled()) log.debug("AL Excel");
					forward(request, response, "/excelVacaciones.jsp");
					
					return;
				}
			}
			
		} catch (ReporteException e) {
			setAttribute(session,"beanErr", e.getBeanMensaje());
			
			forward(request, response, "/PagE.jsp");
		} catch (Exception e) {
			MensajeBean bean = new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
					+ e.getMessage());
			bean
			.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			setAttribute(session,"beanErr", bean);
			forward(request, response, "/PagE.jsp");
		}
		
	}
	
/*     */	
	

    /**
     * Metodo encargado de cargar la pagina del reporte de inasistencias
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarInasistencias(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp));		//jquispecoi PAS20155E230000073

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
        	
       	
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			log.debug("codUO "+codUO);
			
			String uoSeg = bUsuario.getVisibilidad();
			
			log.debug("uoSeg "+uoSeg);
			
			
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
				log.debug("entro al if ");
				log.debug("uoSeg.substring(0,2).concat('%') " + uoSeg.substring(0,2).concat("%"));
				uoSeg = uoSeg.substring(0,2).concat("%");
			}
			log.debug("paso el if ");
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  
					(roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) 
					|| (roles.get(Constantes.ROL_JEFE)!=null)) {				
	            
				session.removeAttribute("detalleReporte");
				session.removeAttribute("operativo");
			    session.setAttribute("operativo", roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				if (log.isDebugEnabled()) log.debug("operativo: " + roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				session.removeAttribute("jefe");
			    session.setAttribute("jefe", roles.get(Constantes.ROL_JEFE));
				if (log.isDebugEnabled()) log.debug("jefe: " + roles.get(Constantes.ROL_JEFE));	            
				
	            
	            this.forward(request, response, "/reporteInasistencias.jsp?idSession="+System.currentTimeMillis());
	            return;
			}
			else {
				throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}
			
        } catch (MantenimientoException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            this.forwardError(request, response, "error al cargar Reporte deInasistencias: "+e.getBeanMensaje());            
        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			
			this.forwardError(request, response, bean);
        }

    }

    /**
     * Metodo encargado de generar el reporte de inasistencias
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarInasistencias(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
            if(log.isDebugEnabled()) log.debug("uoSeg: " + uoSeg);
            
            ReporteDelegate rd = new ReporteDelegate();
            
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

          
            String regimen = request.getParameter("cmbRegimen");          
            String tipoReporte = request.getParameter("tipoReporte") != null ? request.getParameter("tipoReporte") : "reporte";
            String criterio = request.getParameter("cmbCriterio") != null ? request.getParameter("cmbCriterio") : "";
            String valor = request.getParameter("txtValor") != null ? request.getParameter("txtValor") : "";            
            String fechaIni = request.getParameter("fechaIni") != null ? request.getParameter("fechaIni") : "";
            String fechaFin = request.getParameter("fechaFin") != null ? request.getParameter("fechaFin") : "";
            String numDias = request.getParameter("numDias") != null ? request.getParameter("numDias") : "";
            
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) { //Intendencia
				valor = codInten;
			}
            
            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");
                        
            if (tipoReporte.equals("imprimir")) {            	
                HashMap params = new HashMap();
                params.put("regimen", regimen);
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("numDias", numDias);                
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);
                
                rd.masivoInasistencias(pool_sp, params, usuario);                   
            } else {
                if (!repetir) {                	
                	//JVV-ini --se agrego regimen
                	ArrayList detalleReporte = rd.inasistencias(pool_sp, regimen, criterio, valor, fechaIni, fechaFin, numDias, seguridad);
                	//JVV-fin
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }

            if (tipoReporte.equals("reporte")) {            	            	
            	//JVV-ini
            	request.setAttribute("regimen", regimen);
            	//JVV-fin
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("numDias", numDias);                
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);
                
                this.forward(request, response, "/reporteInasistencias.jsp?idSession="+System.currentTimeMillis());
                return;
            }

            if (tipoReporte.equals("excel")) {                
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);
                request.setAttribute("criterio", criterio);                
                this.forward(request, response, "/excelInasistencias.jsp?idSession="+System.currentTimeMillis());
                return;
            }

            if (tipoReporte.equals("imprimir")) {
                request.setAttribute("nomReporte", "REPORTE DE INASISTENCIAS");
                
                this.forward(request, response, "/resultadoReporte.jsp?idSession="+System.currentTimeMillis());                
                return;
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            this.forwardError(request, response, "error al cargar Reporte de Inasistencias: "+e.getBeanMensaje());
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. " + e.getMessage());
            bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            this.forwardError(request, response, bean);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de devoluciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarDevoluciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

            MantenimientoDelegate md = new MantenimientoDelegate();
            ArrayList periodos = md.cargarPeriodos(pool_sp);
            ArrayList movimientos = md.buscarMovimientos(pool_sp, "5", "S",
                    "-1");

            session.removeAttribute("listaPeriodos");
            session.setAttribute("listaPeriodos", periodos);

            session.removeAttribute("movimientos");
            session.setAttribute("movimientos", movimientos);

            session.removeAttribute("detalleReporte");

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/reporteDevoluciones.jsp");
            dispatcher.forward(request, response);
            return;

        } catch (MantenimientoException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de devoluciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarDevoluciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String periodo = request.getParameter("cmbPeriodo");
            boolean repetir = request.getParameter("repetir").trim().equals(
                    "true");

            MantenimientoDelegate md = new MantenimientoDelegate();
            BeanPeriodo bPer = md.buscarPeriodoCodigo(pool_sp, periodo);
            
            if (!repetir) {

                ReporteDelegate rd = new ReporteDelegate();
                ArrayList detalleReporte = rd.devolucionesDescuentos(pool_sp,
                        periodo, criterio, valor, seguridad);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("per", periodo);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteDevoluciones.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("periodo", periodo);
                request.setAttribute("fechaIniExcel", bPer.getFechaIni());
                request.setAttribute("fechaFinExcel", bPer.getFechaFin());
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelDevoluciones.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    
/* JRR - PROGRAMACION - 17/05/2010 */
	/**
	 * Metodo encargado de generar el reporte personalizado de vacaciones
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void generarConsultaVacaciones(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
	throws ServletException, IOException {
		
		try {        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
			String codigo = bUsuario.getNroRegistro();			    
			
			Map roles = (HashMap) bUsuario.getMap().get("roles");			
			
			List usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles ((ArrayList)usrRoles);
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
			Map seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codigo);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			
			VacacionDelegate vd = new VacacionDelegate();
			Map paramSaldos = new HashMap();
			Map paramVta = new HashMap();
			Map paramVac = new HashMap();
			Map paramProg = new HashMap();
			
			paramSaldos.put("codPers", codigo);
			paramSaldos.put("saldoFavor", "false");
			paramSaldos.put("anho", "");
			paramSaldos.put("dbpool", pool_sp);            
			
			paramVta.put("codPers", codigo);//paramVta.put("codpers", codigo); - PROGRAMACION - 17/05/2010
			paramVta.put("estado", "");
			paramVta.put("licencia", Constantes.VACACION_VENTA);
			paramVta.put("anho", "");
			paramVta.put("dbpool", pool_sp);
			
			paramVac.put("cod_pers", codigo);
			paramVac.put("fechaFin", Utiles.obtenerFechaActual());
			paramVac.put("dias", constantes.leePropiedad("VALOR_POR_DEFECTO_CERO"));
			paramVac.put("orden", Constantes.INACTIVO);
			paramVac.put("dbpool", pool_sp);
			
			paramProg.put("codPers", codigo);//paramProg.put("codpers", codigo); - PROGRAMACION - 17/05/2010
			paramProg.put("estado", "");
			paramProg.put("licencia", Constantes.VACACION_PROGRAMADA);
			paramProg.put("anho", "");
			paramProg.put("dbpool", pool_sp);  

			/*
			 ArrayList listaSaldos = vd.buscarSaldos(paramSaldos);
			 ArrayList listaVendidas = vd.buscarVacacionesXLicencia(paramVta);
			 ArrayList listaPendientes = vd.buscarVacacionesEfecPend(paramVac);
			 ArrayList listaGozadas = vd.buscarVacacionesEfecGoz(paramVac);
			 */
		
			Map arreglos = vd.generarConsultaVacaciones((HashMap)paramSaldos,(HashMap)paramVta, (HashMap)paramVac, (HashMap)paramProg);
			
			List listaSaldos = (ArrayList)arreglos.get("listaSaldos");
			List listaVendidas = (ArrayList)arreglos.get("listaVendidas");
			List listaPendientes = (ArrayList)arreglos.get("listaPendientes");
			List listaGozadas =(ArrayList)arreglos.get("listaGozadas");
			List listaProgramadas = (ArrayList)arreglos.get("listaProgramadas");
			List listaCompensadas = (ArrayList)arreglos.get("listaCompensadas");
			
			session.removeAttribute("listaSaldos");
			session.removeAttribute("listaVendidas");
			session.removeAttribute("listaPendientes");
			session.removeAttribute("listaGozadas");
			session.removeAttribute("listaProgramadas");
			session.removeAttribute("listaCompensadas");
			
			setAttribute(session, "listaSaldos", listaSaldos);
			setAttribute(session,"listaVendidas", listaVendidas);
			setAttribute(session,"listaPendientes", listaPendientes);
			setAttribute(session,"listaGozadas", listaGozadas);
			setAttribute(session,"listaProgramadas", listaProgramadas);
			setAttribute(session,"listaCompensadas", listaCompensadas);
			
			forward(request, response, "/consultaPersonalVacaciones.jsp");          
			return;
		} catch (DelegateException e) {
			log.error(e,e);
			forwardError(request, response, e.getMensaje());
		} catch (Exception e) {
			log.error(e,e);
			bean= new MensajeBean();
			bean.setError(true);
			bean.setMensajeerror("Ha ocurrido un error al agregar la programacion");
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcin");
			forwardError(request, response, bean);
		} finally {
		}
	}
/***************/    

	
    /**
     * Metodo encargado de generar el reporte personalizado de calificaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarCalificadoPersonal(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            if (!repetir) {
            	Map params = new HashMap();
            	params.put("pool_sp", pool_sp);
            	params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("regimen", "2");
                params.put("criterio", codigo);
                params.put("valor", null);
                params.put("mov", null);
            	ArrayList detalleReporte = rd.calificado(params, null);
            	//pool_sp, fechaIni, fechaFin, "2", codigo, null, null
                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }

            request.setAttribute("fechaIni", fechaIni);
            request.setAttribute("fechaFin", fechaFin);

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/consultaPersonalCalificaciones.jsp");
            dispatcher.forward(request, response);
            return;
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte personalizado de
     * calificaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarCalificadoPersonal(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

            session.removeAttribute("detalleReporte");
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/consultaPersonalCalificaciones.jsp");
            dispatcher.forward(request, response);
            return;
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de personal sin turno
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarSinTurno(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
			//PRAC-ASANCHEZ 18/06/2009 - COMENTANDO LO DE ABAJO PQ YA NO SIRVE
            /*
            UtilesDelegate ud = new UtilesDelegate();
            ArrayList listaUO = ud.buscarUOrgan(pool_sp, "2", "'1','2'", null);

            session.removeAttribute("listaUO");
            session.setAttribute("listaUO", listaUO);
			*/
			//
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
        	String codPers = bUsuario.getNroRegistro();//.getNumreg();
        	
        	HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
        	
			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			
			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esSupervisor(pool_sp, codPers, "MM", codUO)) || (ad.esJefeEncargadoDelegado(seguridad)) || (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) 
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  || (roles.get(Constantes.ROL_JEFE)!=null) )  {
				session.removeAttribute("detalleReporte");
	            session.removeAttribute("detalleReporte");
	            session.removeAttribute("operativo");
			    session.setAttribute("operativo", roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				if (log.isDebugEnabled()) log.debug("operativo: " + roles.get(Constantes.ROL_ANALISTA_OPERATIVO));
				session.removeAttribute("jefe");
			    session.setAttribute("jefe", roles.get(Constantes.ROL_JEFE));
				if (log.isDebugEnabled()) log.debug("jefe: " + roles.get(Constantes.ROL_JEFE));		

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/reporteSinTurno.jsp");
            dispatcher.forward(request, response);
            return;
	        }
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}

        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de personal sin turnos de trabajo
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarSinTurno(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws

    ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			//PRAC-ASANCHEZ 18/06/2009
			String usuario = bUsuario.getLogin();
			//
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            //JVV-ini
            String regimen = request.getParameter("cmbRegimen");
            //JVV-fin
            //PRAC-ASANCHEZ 18/06/2009
            //String cmbUO = request.getParameter("cmbUO");
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");            
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) { //Intendencia
				valor = codInten;
			}
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            //PRAC-ASANCHEZ 18/06/2009
            if (tipoR.equals("imprimir")) {

                HashMap params = new HashMap();
                params.put("regimen", regimen);
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("codPers", codigo);
                params.put("seguridad", seguridad);

                rd.masivoPersonalSinTurno(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                	//JVV-ini --se agrego regimen 
                	Map params = new HashMap();
                	params.put("dbpool", pool_sp);
                	params.put("fechaIni", fechaIni);
                    params.put("fechaFin", fechaFin);
                    params.put("regimen", regimen);
                    params.put("criterio", criterio);
                    params.put("valor", valor);
                    
                	ArrayList detalleReporte = rd.personalSinTurno(params, seguridad);
                	//JVV-fin
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }
            
            /*
            if (tipoR.equals("reporte")) {

                request.setAttribute("codUO", cmbUO);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteSinTurno.jsp");
                dispatcher.forward(request, response);
                return;
            }
			*/
            if (tipoR.equals("reporte")) {
            	//JVV-ini
            	request.setAttribute("regimen", regimen);
            	//JVV-fin
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteSinTurno.jsp");
                dispatcher.forward(request, response);
                return;
            }
            //
            
            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelSinTurno.jsp");
                dispatcher.forward(request, response);
                return;
            }

            //PRAC-ASANCHEZ
            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE DE PERSONAL SIN TURNO");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }
            //
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte personalizado de
     * marcaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarMarcacionesPersonal(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			
        	request.setAttribute("usuario", bUsuario.getLogin());
            session.removeAttribute("detalleReporte");
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/consultaPersonalMarcaciones.jsp");
            dispatcher.forward(request, response);
            return;
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
    }

    /**
     * Metodo encargado de generar el reporte personalizado de marcaciones
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarMarcacionesPersonal(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            if (!repetir) {
                ArrayList detalleReporte = rd.marcacionesPersonal(pool_sp,
                        fechaIni, fechaFin, "0", codigo, null);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }

            request.setAttribute("fechaIni", fechaIni);
            request.setAttribute("fechaFin", fechaFin);
            request.setAttribute("usuario", bUsuario.getLogin());

            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/consultaPersonalMarcacionesM.jsp");
            dispatcher.forward(request, response);
            return;
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de cargar la pagina del reporte de papeletas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarPapeletas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp));		//jquispecoi PAS20155E230000073

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

	            MantenimientoDelegate md = new MantenimientoDelegate();
	            ArrayList tiposPapeleta = md.buscarParametros(pool_sp,
	                    Constantes.CODTAB_TIPO_PAPELETA);            
	            ArrayList estadosPapeleta = md.cargarT99(pool_sp,
	                    Constantes.CODTAB_ESTADO_PAPELETA, Constantes.ACTIVO);

	            session.removeAttribute("tiposPapeleta");
	            session.setAttribute("tiposPapeleta", tiposPapeleta);

	            session.removeAttribute("estadosPapeleta");
	            session.setAttribute("estadosPapeleta", estadosPapeleta);
	            
	            session.removeAttribute("detalleReporte");

	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reportePapeletas.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de papeletas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarPapeletas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws

    ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario=bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor")!=null?request.getParameter("txtValor"):"";
            String estado = request.getParameter("cmbEstado");
            String papeleta = request.getParameter("cmbPapeleta");

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            
            HashMap datos = new HashMap();
            datos.put("dbpool", pool_sp);
            datos.put("fechaIni", fechaIni);
            datos.put("fechaFin", fechaFin);
            datos.put("criterio", criterio);
            datos.put("valor", valor);
            datos.put("estado", estado);
            datos.put("papeleta", papeleta);
            
            if (tipoR.equals("imprimir")) {
            	datos.put("codPers", codigo);
            	datos.put("usuario", usuario);
            	datos.put("seguridad", seguridad);
                rd.masivoPapeletas(datos);
                request.setAttribute("nomReporte",
                        "REPORTE MASIVO DE PAPELETAS");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {
	            if (!repetir) {
	                ArrayList detalleReporte = rd.papeletas(datos, seguridad);
	                session.removeAttribute("detalleReporte");
	                session.setAttribute("detalleReporte", detalleReporte);
	            }
	            if (tipoR.equals("reporte")) {
	
	                request.setAttribute("criterio", criterio);
	                request.setAttribute("valor", valor);
	                request.setAttribute("estado", estado);
	                request.setAttribute("papeleta", papeleta);
	                request.setAttribute("fechaIni", fechaIni);
	                request.setAttribute("fechaFin", fechaFin);
	
	                RequestDispatcher dispatcher = getServletContext()
	                        .getRequestDispatcher("/reportePapeletas.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
	
	            if (tipoR.equals("excel")) {
	                request.setAttribute("fechaIniExcel", fechaIni);
	                request.setAttribute("fechaFinExcel", fechaFin);
	                RequestDispatcher dispatcher = getServletContext()
	                        .getRequestDispatcher("/excelPapeletas.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    /**
     * Metodo encargado de cargar la pagina del reporte de vacaciones gozadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarLicenciaMedica(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

	            session.removeAttribute("detalleReporte");

	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteLicenciaMedica.jsp");
	            dispatcher.forward(request, response);
	            return;
	            
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}


        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }

    /**
     * Metodo encargado de generar el reporte de vacaciones gozadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarLicenciaMedica(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			//log.debug("Roles "+roles);
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String tipoLic = request.getParameter("cmbTipo");
            String dias = request.getParameter("txtDias");
            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            dias = dias.trim().equals("") ? "0" : dias;
            
           ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool", pool_sp);
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);
                mapa.put("fechaIni", fechaIni);
                mapa.put("fechaFin", fechaFin);
                mapa.put("dias", dias);
                mapa.put("tipoLic", tipoLic);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoLicenciaMedica(mapa, usuario);

                request.setAttribute("nomReporte",
                        "REPORTE DE LICENCIA MEDICA : SUBSIDIO");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {

            if (!repetir) {
                ArrayList detalleReporte = rd.licenciaMedica(pool_sp,
                        fechaIni, fechaFin, criterio, valor, tipoLic, dias, seguridad);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }


            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);
                request.setAttribute("tipoLic", tipoLic);
                request.setAttribute("dias", dias);
                

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteLicenciaMedica.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {

                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);
                request.setAttribute("tipoLic", tipoLic);
                request.setAttribute("dias", dias);
                
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelLicenciaMedica.jsp");
                dispatcher.forward(request, response);
                return;
            }
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    
/****************JRR******************/
    
    /**
     * Metodo encargado de cargar la pagina del reporte de Compensacion por Bolsa
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarCompensacionBolsa(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
/*			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			//log.debug("Roles "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);  */
			//log.debug("Roles "+roles);
			
			Map roles = MenuCliente.getRoles(bUsuario);
            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();

			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

	            //MantenimientoDelegate md = new MantenimientoDelegate();
	            
	            session.removeAttribute("detalleReporte");
	            
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/ReporteCompensaBolsa.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    

    /**
     * Metodo encargado de generar el reporte de papeletas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarCompensacionBolsa(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws

    ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			
			Map roles = MenuCliente.getRoles(bUsuario);
			
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            HashMap datos = new HashMap();
            datos.put("dbpool", pool_sp);
            datos.put("fechaIni", fechaIni);
            datos.put("fechaFin", fechaFin);
            datos.put("criterio", criterio);
            datos.put("valor", valor);
            
            if (!repetir) {
                List detalleReporte = rd.compensacionesBolsa(datos, seguridad);
                //log.debug("detalleReporte: " + detalleReporte);
                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("reporte")) {

                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/ReporteCompensaBolsa.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/ExcelCompensaBolsa.jsp");
                dispatcher.forward(request, response);
                return;
            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    /*
	 * Metodo que se encarga de convertir los ROles de HashMap a ArrayList
	 * @param roles
	 */
	public static HashMap obtRoles(ArrayList roles)
	throws ServletException, IOException {
	HashMap res = new HashMap();
	if (roles!=null && roles.size()>0){
	String tmpRol;
	for (int i = 0; i < roles.size(); i++){
		tmpRol = (String) roles.get(i);
		res.put(tmpRol,tmpRol);
	}
	}
	return res;
	}

	
    //PRAC-ASANCHEZ 30/07/2009
    /**
     * Metodo encargado de cargar la pagina del reporte del detalle diario
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarDetalleDiario(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima(pool_sp));		//jquispecoi PAS20155E230000073
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();//.getNumreg();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			
			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
			
			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  )  {
	            MantenimientoDelegate md = new MantenimientoDelegate();
	            
	            ArrayList movimientos = md.buscarMovimientos(pool_sp, "5", "", "-1");	            
	            
	            session.removeAttribute("movimientos");
	            session.setAttribute("movimientos", movimientos);

	            session.removeAttribute("detalleReporte");

	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/ReporteDetalleDiario.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    

    /**
     * Metodo encargado de generar el reporte calificado de asistencia
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarDetalleDiario(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");
            String mov = request.getParameter("cmbTipMov");
            if (mov.equals("-1")){ mov = "";}

            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            
			String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) {
				valor = codInten;
			}
            
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";

            if (tipoR.equals("imprimir")) {

                HashMap params = new HashMap();
                params.put("fechaIni", fechaIni);
                params.put("fechaFin", fechaFin);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("codPers", codigo);
                params.put("seguridad", seguridad);
                params.put("mov", mov);

                rd.masivoDetalleDiario(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                    ArrayList detalleReporte = rd.detalleDiario(pool_sp, fechaIni,
                            fechaFin, criterio, valor, seguridad, mov);

                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);

                }
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fechaIni", fechaIni);
                request.setAttribute("fechaFin", fechaFin);
                request.setAttribute("mov", mov);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/ReporteDetalleDiarioM.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/ExcelDetalleDiario.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE DE DETALLE DIARIO DE ASISTENCIA");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }

        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    /* ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */
    /**
     * Metodo encargado de cargar la pagina del reporte de Trabajadores notificados para Goce Vacacional     
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void cargarNotificacionesVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

			session.removeAttribute("detalleReporte");
                      
            forward(request,response,"/reporteNotificacionesVacaciones.jsp"+"?idSession="+System.currentTimeMillis());
            
            return;

        } catch (Exception e) {
        	
        	forwardError(request,response,e.getMessage());
        }

    }
    
  //JVILLACORTA - 30/11/2011 - MODIFICA ALERTA DE SOLICITUDES
    /**
     * Metodo encargado de generar el reporte de Trabajadores notificados para Goce Vacacional  
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void generarNotificacionesVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        try {

        	if (log.isDebugEnabled()) log.debug("Ingreso a generarNotificacionesVacaciones");
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			if (log.isDebugEnabled()) log.debug("codigo: "+codigo);
			
			String usuario = bUsuario.getLogin();
			if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("roles1: "+roles);
			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			if (log.isDebugEnabled()) log.debug("usrRoles: "+usrRoles);
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("roles2: "+roles);
			            
            String uoSeg = bUsuario.getVisibilidad();          
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
            if (log.isDebugEnabled()) log.debug("uoSeg Final: "+uoSeg);

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            if (log.isDebugEnabled()) log.debug("seguridad: "+seguridad);
            
            String regimen = request.getParameter("cmbRegimen");
            if (log.isDebugEnabled()) log.debug("regimen: " + regimen);
            
            String reg = "";            
            if (regimen.trim().equals("01")) {            	
				reg = "D.L.276-728";
            } else if (regimen.trim().equals("09")) {            	
				reg = "D.L.1057";
            } else {            	
				reg = "Modalidades formativas";
            }

            String criterio = request.getParameter("cmbCriterio");
            if (log.isDebugEnabled()) log.debug("criterio: " + criterio);
            //String valor = request.getParameter("txtValor").toUpperCase();
            //String valor = request.getParameter("txtValor");
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();
            
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);
            //String solicitud = request.getParameter("cmbSolicitud");           
            //if (log.isDebugEnabled()) log.debug("solicitud: " + solicitud);
            
            String solicitud = request.getParameter("chk_ind_mail")!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
            if (log.isDebugEnabled()) log.debug("checkbox: " + solicitud);
            String indicador = "";            
            if (solicitud.trim().equals("1")) {            	
            	indicador = "Si";
            } else if (solicitud.trim().equals("0")) {            	
            	indicador = "No";
            } 
            if (log.isDebugEnabled()) log.debug("indicador: " + indicador);
            
            String fechaNotific = request.getParameter("fechaNotific");
            String fechaNotificFin = request.getParameter("fechaNotificFin");            
            String fechaIniGoce = request.getParameter("fechaIniGoce"); 
            String fechaFinGoce = request.getParameter("fechaFinGoce");            
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: "+tipoR);
           
            if (tipoR.equals("imprimir")) {

                Map params = new HashMap();
                params.put("regimen", regimen);
                params.put("criterio", criterio);               
                params.put("valor", valor);
                params.put("solicitud", solicitud);
                params.put("fechaNotific", fechaNotific);
                params.put("fechaNotificFin", fechaNotificFin);
                params.put("fechaIniGoce", fechaIniGoce);
                params.put("fechaFinGoce", fechaFinGoce);
                params.put("reg", reg);
                params.put("codPers", codigo);
                params.put("seguridad", seguridad);    
                
                rd.masivoNotificacionesXVacacionesATrabajadores(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                	
                    List detalle = rd.notificacionesXVacacionesATrabajadores(pool_sp, regimen, fechaNotific, fechaNotificFin, fechaIniGoce, fechaFinGoce, criterio, valor,solicitud,seguridad);                  
                                       
                    ArrayList detalleReporte= (ArrayList)detalle;
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                    if (log.isDebugEnabled()) log.debug("detalleReporte: "+detalleReporte);
                }
            } 
            
            if (tipoR.equals("reporte")) {
            	request.setAttribute("regimen", regimen);
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("solicitud", solicitud);
                request.setAttribute("fechaNotific", fechaNotific);
                request.setAttribute("fechaNotificFin", fechaNotificFin);
                request.setAttribute("fechaIniGoce", fechaIniGoce);                
                request.setAttribute("fechaFinGoce", fechaFinGoce);
           
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteNotificacionesVacaciones.jsp");
                dispatcher.forward(request, response);
                return;
            }
            
            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaNotific);
                request.setAttribute("fechaFinExcel", fechaNotificFin);                
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);                
                request.setAttribute("regimen", reg);
                request.setAttribute("indicador", indicador);
                /*RequestDispatcher dispatcher = getServletContext()
                		.getRequestDispatcher("/excelNotificaGoceVaca.jsp");
                dispatcher.forward(request, response);*/
                forward(request,response,"/excelNotificaGoceVaca.jsp?idSession="+System.currentTimeMillis());
                return;
            }
           
            if (tipoR.equals("imprimir")) {
            	
                request.setAttribute("nomReporte","REPORTE DE TRABAJADORES NOTIFICADOS PARA GOCE VACACIONAL");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
           
            
        } catch (ReporteException e) {
        	
        	forwardError(request,response,e.getMessage());
        	
        } catch (Exception e) {
        	
        	forwardError(request,response,e.getMessage());
        }

    }    
    //FIN - JVILLACORTA - 30/11/2011 - MODIFICA ALERTA DE SOLICITUDES        
	  
	
    /** JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
     * Metodo encargado de cargar la pagina del reporte de notificaciones a directivos
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarNotificaDirectivos(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
						
			if ( (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) 
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) )  {					            	            
				MantenimientoDelegate md = new MantenimientoDelegate();
	            ArrayList periodos = md.cargarPeriodos(pool_sp);

	            session.removeAttribute("listaPeriodos");
	            session.setAttribute("listaPeriodos", periodos);

	            session.removeAttribute("detalleReporte");
				RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteNotificacionesDirectivos.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }//FIN - JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES

    /** JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
     * Metodo encargado de generar el reporte de notificacion de solicitudes pendientes a directivos
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarNotificaDirectivos(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");						
			getRoles(request, response, session, bUsuario);
			
			String codigo = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
            String uuoo = bUsuario.getCodUO();            
            
			if ("usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())) uoSeg = uoSeg.substring(0,2).concat("%");		
						
            HashMap seguridad = new HashMap();
            seguridad.put("roles", session.getAttribute("perfil_usuario").toString());
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uuoo", uuoo); 
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
                                       
            String regimen = request.getParameter("cmbRegimen");
            if (log.isDebugEnabled()) log.debug("regimen: " + regimen);
            String cmbperiodo = request.getParameter("cmbPeriodo");
            if (log.isDebugEnabled()) log.debug("cmbperiodo: " + cmbperiodo);
            String periodo = cmbperiodo.substring(60,66);
            if (log.isDebugEnabled()) log.debug("periodo: " + periodo);
            
            MantenimientoDelegate md = new MantenimientoDelegate();
            BeanPeriodo bPer = md.buscarPeriodoCodigo(pool_sp, periodo);
            String reg = "";
            
            if (regimen.trim().equals("01")) {
            	bPer.setFechaIni(bPer.getFechaIni());
				bPer.setFechaFin(bPer.getFechaFin());
				reg = "D.L.276-728";
            } else if (regimen.trim().equals("09")) {
            	bPer.setFechaIni(bPer.getFechaIniCAS());
				bPer.setFechaFin(bPer.getFechaFinCAS());
				reg = "D.L.1057";
            } else {
            	bPer.setFechaIni(bPer.getFechaIniModForm());
				bPer.setFechaFin(bPer.getFechaFinModForm());
				reg = "Modalidades formativas";
            }            
            //String fechaIni = request.getParameter("fechaIni");
            //String fechFin = request.getParameter("fechaFin");
            String fIni = bPer.getFechaIni(); //2011/11/01
            String fFin = bPer.getFechaFin();  //2011/11/30
            String fechaIni = fIni.substring(8,10)+"/"+fIni.substring(5,7)+"/"+fIni.substring(0,4);
            String fechFin = fFin.substring(8,10)+"/"+fFin.substring(5,7)+"/"+fFin.substring(0,4);
            if (log.isDebugEnabled()) log.debug("fechaIniPeriodo: " + fechaIni);
            if (log.isDebugEnabled()) log.debug("fechFinPeriodo: " + fechFin);
            
    		FechaBean fb = new FechaBean();			
    		String ffSem1 = fb.getOtraFecha(fechaIni, 7, Calendar.DATE); //<08-03
    		if (log.isDebugEnabled()) log.debug("ffSem1: " + ffSem1);
    		String ffSem2 = fb.getOtraFecha(ffSem1, 7, Calendar.DATE); //<15-03
    		if (log.isDebugEnabled()) log.debug("ffSem2: " + ffSem2);
    		String ffSem3 = fb.getOtraFecha(ffSem2, 7, Calendar.DATE); //<22-03
    		if (log.isDebugEnabled()) log.debug("ffSem3: " + ffSem3);
    		//String fechaFin = fb.getOtraFecha(ffSem3, 8, Calendar.DATE); //<29-03 //ICAPUNAY 10/08/2011 - VALIDACION DE 4 SEMANAS (28 DIAS)
    		String ffSem4 = fb.getOtraFecha(ffSem3, 7, Calendar.DATE); //<29-03 //ICAPUNAY 10/08/2011 - VALIDACION DE 4 SEMANAS (28 DIAS)
    		if (log.isDebugEnabled()) log.debug("ffSem4: " + ffSem4);    		
    		//String fechaFin = fb.getOtraFecha(ffSem4, 1, Calendar.DATE); //VALIDACION DE 5 SEMANAS (30 DIAS)
    		//if (log.isDebugEnabled()) log.debug("fechaFin: " + fechaFin);
    		if (log.isDebugEnabled()) log.debug("fechaFin: " + fechFin);
    		
            String criterio = request.getParameter("cmbCriterio");
            if (log.isDebugEnabled()) log.debug("criterio: " + criterio);
            //String valor = request.getParameter("txtValor"); //JVILLACORTA 18/08/2011 - CONVERSION A MAYUSCULAS
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();
            
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);
			
            session.removeAttribute("fechaIniNotif");            
            setAttribute(session,"fechaIniNotif", fechaIni);
            
            session.removeAttribute("fechaFinPeri");            
            setAttribute(session,"fechaFinPeri", fechFin);

           
            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");
            if (log.isDebugEnabled()) log.debug("repetir: " + repetir);            
            
            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: " + tipoR);
            
            String semana = request.getParameter("semanas");
            if (log.isDebugEnabled()) log.debug("semana: " + request.getParameter("semanas"));                    
            String ffIni = request.getParameter("fechaInicio");
            String ffFin = request.getParameter("fechaFin");
                    
            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("semana", semana);
                params.put("fechaIni", fechaIni);
                params.put("fchSem1", ffSem1);
                params.put("fchSem2", ffSem2);
                params.put("fchSem3", ffSem3);
                params.put("fchSem4", ffSem4);
                //params.put("fechaFin", fechaFin);
                params.put("fechaFin", fechFin);
                params.put("fechFin", fechFin);
                params.put("criterio", criterio);
                params.put("valor", valor);                                
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);
                params.put("reg", reg);
                params.put("periodo", periodo);
                params.put("regimen", regimen);

                rd.masivoNotificacionesDirectivos(pool_sp, params, usuario); 

            } else {
                if (!repetir) {                	
                	HashMap params = new HashMap();
                	params.put("dbpool", pool_sp);
                	params.put("semana", semana);
                	params.put("fechaIni", fechaIni);
                	params.put("fchSem1", ffSem1);
                    params.put("fchSem2", ffSem2);
                    params.put("fchSem3", ffSem3);
                    params.put("fchSem4", ffSem4);
                    //params.put("fechaFin", fechaFin);
                    params.put("fechaFin", fechFin);
                    params.put("criterio", criterio);
                    params.put("valor", valor);                    
                    params.put("seguridad", seguridad);
                    params.put("regimen", regimen);
                    
                    ArrayList detalleReporte = rd.notificacionesDirectivos(params);
                	
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }

            if (tipoR.equals("reporte")) {             	
            	request.setAttribute("regimen", regimen);
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("per", periodo);
                if (log.isDebugEnabled()) log.debug("cmbperiodo en tipoR: " + periodo);
            	request.setAttribute("fechaInicio", ffIni);
                request.setAttribute("fechaFin", ffFin);               

                /*RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteNotificacionesDirectivos.jsp");
                dispatcher.forward(request, response);*/
                log.debug("=====================>");
                forward(request,response,"/reporteNotificacionesDirectivos.jsp?idSession="+System.currentTimeMillis());
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechFin);                
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);                
                request.setAttribute("periodo", periodo);
                request.setAttribute("regimen", reg);
                forward(request,response,"/excelNotificaDirectivo.jsp?idSession="+System.currentTimeMillis());
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "NOTIFICACIONES DE SOLICITUDES PENDIENTES PARA DIRECTIVOS");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp"); 
                dispatcher.forward(request, response);
                return;

            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
    } //FIN - JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
    
    /** JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
     * Metodo para buscar Detalle de Notificaciones de solicitudes pendientes a directivos
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void buscaDetalleNotificaDirectivo(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
    	
    	ArrayList listaDetalleNotifica = null;
    	String fechaIniNotif = "";
    	String fechaFinPer = "";
        try {
        	
        	fechaIniNotif = (String) session.getAttribute("fechaIniNotif");
        	fechaFinPer = (String) session.getAttribute("fechaFinPeri");
        	String codNotif = request.getParameter("codigo_notif");        	
        	String sem = request.getParameter("sem");
        	HashMap params = new HashMap();
        	params.put("fechaIni", fechaIniNotif);
        	params.put("fechaFin", fechaFinPer);
        	params.put("codNotif", codNotif);
        	params.put("sem", sem);
        	if (log.isDebugEnabled()) log.debug("fechaIniNotif:" + fechaIniNotif);
        	if (log.isDebugEnabled()) log.debug("fechaFin:" + fechaFinPer);
        	if (log.isDebugEnabled()) log.debug("codNotif:" + codNotif);
        	if (log.isDebugEnabled()) log.debug("sem:" + sem);
        	
        	ReporteDelegate rd = new ReporteDelegate();        	
        	listaDetalleNotifica = rd.buscaDetalleNotificaDirec(params);       	
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
        session.removeAttribute("listaDetalleNotifica");
        setAttribute(session,"listaDetalleNotifica", listaDetalleNotifica);
        forward(request, response, "/DetalleNotifica.jsp");       
    }//FIN - JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
    
    /** JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
     * Metodo encargado de cargar la pagina del reporte de notificaciones a trabajadores
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarNotificaTrabajador(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
						
			if ( (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) 
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) )  {					            	            
				MantenimientoDelegate md = new MantenimientoDelegate();
	            ArrayList periodos = md.cargarPeriodos(pool_sp);

	            session.removeAttribute("listaPeriodos");
	            session.setAttribute("listaPeriodos", periodos);

	            session.removeAttribute("detalleReporte");
				
				RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteNotificacionesTrabajadores.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }//FIN - JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES

    /** JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
     * Metodo encargado de generar el reporte de notificacion de 
     * movimientos de asistencia para trabajadores
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarNotificaTrabajador(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");						
			getRoles(request, response, session, bUsuario);
			
			String codigo = bUsuario.getNroRegistro();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
            String uuoo = bUsuario.getCodUO();            
            
			if ("usuario_AnalisOperativo".equals(session.getAttribute("perfil_usuario").toString())) uoSeg = uoSeg.substring(0,2).concat("%");		
						
            HashMap seguridad = new HashMap();
            seguridad.put("roles", session.getAttribute("perfil_usuario").toString());
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uuoo", uuoo);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
            String regimen = request.getParameter("cmbRegimen");
            if (log.isDebugEnabled()) log.debug("regimen: " + regimen);
            String cmbperiodo = request.getParameter("cmbPeriodo");
            if (log.isDebugEnabled()) log.debug("cmbperiodo: " + cmbperiodo);
            String periodo = cmbperiodo.substring(60,66);
            if (log.isDebugEnabled()) log.debug("periodo: " + periodo);
            
            MantenimientoDelegate md = new MantenimientoDelegate();
            BeanPeriodo bPer = md.buscarPeriodoCodigo(pool_sp, periodo);
            String reg = "";
            
            if (regimen.trim().equals("01")) {
            	bPer.setFechaIni(bPer.getFechaIni());
				bPer.setFechaFin(bPer.getFechaFin());
				reg = "D.L.276-728";
            } else if (regimen.trim().equals("09")) {
            	bPer.setFechaIni(bPer.getFechaIniCAS());
				bPer.setFechaFin(bPer.getFechaFinCAS());
				reg = "D.L.1057";
            } else {
            	bPer.setFechaIni(bPer.getFechaIniModForm());
				bPer.setFechaFin(bPer.getFechaFinModForm());
				reg = "Modalidades formativas";
            }
                                       
            //String fechaIni = request.getParameter("fechaIni");
            //String fechFin = request.getParameter("fechaFin");
            
            String fIni = bPer.getFechaIni(); //2011/11/01
            String fFin = bPer.getFechaFin();  //2011/11/30
            String fechaIni = fIni.substring(8,10)+"/"+fIni.substring(5,7)+"/"+fIni.substring(0,4);
            String fechFin = fFin.substring(8,10)+"/"+fFin.substring(5,7)+"/"+fFin.substring(0,4);
            if (log.isDebugEnabled()) log.debug("fechaIniPeriodo: " + fechaIni);
            if (log.isDebugEnabled()) log.debug("fechFinPeriodo: " + fechFin);           
            
            FechaBean fb = new FechaBean();			
    		String ffSem1 = fb.getOtraFecha(fechaIni, 7, Calendar.DATE); //<08-03
    		if (log.isDebugEnabled()) log.debug("ffSem1: " + ffSem1);
    		String ffSem2 = fb.getOtraFecha(ffSem1, 7, Calendar.DATE); //<15-03
    		if (log.isDebugEnabled()) log.debug("ffSem2: " + ffSem2);
    		String ffSem3 = fb.getOtraFecha(ffSem2, 7, Calendar.DATE); //<22-03
    		if (log.isDebugEnabled()) log.debug("ffSem3: " + ffSem3);
    		String ffSem4 = fb.getOtraFecha(ffSem3, 7, Calendar.DATE); //<29-03 //ICAPUNAY 10/08/2011 - VALIDACION DE 4 SEMANAS (28 DIAS)
    		if (log.isDebugEnabled()) log.debug("ffSem4: " + ffSem4);
    		//String fechaFin = fb.getOtraFecha(ffSem4, 1, Calendar.DATE); //<29-03 //ICAPUNAY 10/08/2011 - VALIDACION DE 4 SEMANAS (28 DIAS)
    		//if (log.isDebugEnabled()) log.debug("fechaFin: " + fechaFin);
    		if (log.isDebugEnabled()) log.debug("fechaFin: " + fechFin);
            
            String criterio = request.getParameter("cmbCriterio");
            //String valor = request.getParameter("txtValor").toUpperCase(); //JVILLACORTA 18/08/2011 - CONVERSION A MAYUSCULAS
            //String valor = request.getParameter("txtValor");
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();   
            
            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);
			
			session.removeAttribute("fechaIniNotifTrab");            
            setAttribute(session,"fechaIniNotifTrab", fechaIni);
            
			session.removeAttribute("fechaFinPer");            
            setAttribute(session,"fechaFinPer", fechFin);

           
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");
            if (log.isDebugEnabled()) log.debug("repetir: " + repetir);
            
            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: " + tipoR);
            
            String semana = request.getParameter("semanas");
            if (log.isDebugEnabled()) log.debug("semana: " + request.getParameter("semanas"));
            String ffIni = request.getParameter("fechaInicio");
            String ffFin = request.getParameter("fechaFin");
            
            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("semana", semana);
                params.put("fechaIni", fechaIni);
                params.put("fchSem1", ffSem1);
                params.put("fchSem2", ffSem2);
                params.put("fchSem3", ffSem3);
                params.put("fchSem4", ffSem4);
                //params.put("fechaFin", fechaFin);
                params.put("fechaFin", fechFin);
                params.put("fechFin", fechFin);
                params.put("criterio", criterio);
                params.put("valor", valor);                                
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);
                params.put("reg", reg);
                params.put("periodo", periodo);
                params.put("regimen", regimen);

                rd.masivoNotificacionesTrabajadores(pool_sp, params, usuario); 

            } else {
                if (!repetir) {                	
                	HashMap params = new HashMap();
                	params.put("dbpool", pool_sp);
                	params.put("semana", semana);
                	params.put("fechaIni", fechaIni);
                	params.put("fchSem1", ffSem1);
                    params.put("fchSem2", ffSem2);
                    params.put("fchSem3", ffSem3);
                    params.put("fchSem4", ffSem4);
                    //params.put("fechaFin", fechaFin);
                    params.put("fechaFin", fechFin);
                    params.put("criterio", criterio);
                    params.put("valor", valor);                    
                    params.put("seguridad", seguridad);
                    params.put("regimen", regimen);
                    
                    ArrayList detalleReporte = rd.notificacionesTrabajadores(params);
                	
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                }
            }

            if (tipoR.equals("reporte")) {             	
                //request.setAttribute("fechaIni", fechaIni);
                //request.setAttribute("fechaFin", fechFin);
            	request.setAttribute("regimen", regimen);
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                if (log.isDebugEnabled()) log.debug("cmbperiodo en tipoR: " + periodo);
                request.setAttribute("per", periodo);
            	request.setAttribute("fechaInicio", ffIni);
                request.setAttribute("fechaFin", ffFin);
                request.setAttribute("ss", semana);
                if (log.isDebugEnabled()) log.debug("semmana en tipoR: " + semana);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteNotificacionesTrabajadores.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechFin);                
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("periodo", periodo);
                request.setAttribute("regimen", reg);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelNotificaTrabajador.jsp"); 
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "NOTIFICACIONES DE MOVIMIENTOS DE ASISTENCIA PARA TRABAJADORES");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp"); 
                dispatcher.forward(request, response);
                return;

            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
    }//FIN - JVILLACORTA - 18/11/2011 - MODIFICA ALERTA DE SOLICITUDES
    
    /** JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
     * Metodo para buscar Detalle de Notificaciones de movimientos a trabajadores
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void buscaDetalleNotificaTrabajador(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
    	
    	ArrayList listaDetalleNotifica = null;
    	String fechaIniNotifTrab = "";
    	String fechaFinPer = "";
        try {
        	
        	fechaIniNotifTrab = (String) session.getAttribute("fechaIniNotifTrab");
        	fechaFinPer = (String) session.getAttribute("fechaFinPer");
        	String codNotif = request.getParameter("codigo_notif");        	
        	String sem = request.getParameter("sem");
        	HashMap params = new HashMap();
        	params.put("fechaIni", fechaIniNotifTrab);
        	params.put("fechaFin", fechaFinPer);
        	params.put("codNotif", codNotif);
        	params.put("sem", sem);
        	if (log.isDebugEnabled()) log.debug("fechaIni:" + fechaIniNotifTrab);
        	if (log.isDebugEnabled()) log.debug("fechaFin:" + fechaFinPer);
        	if (log.isDebugEnabled()) log.debug("codNotif:" + codNotif);
        	if (log.isDebugEnabled()) log.debug("sem:" + sem);
        	
        	ReporteDelegate rd = new ReporteDelegate();        	
        	listaDetalleNotifica = rd.buscaDetalleNotificaTrab(params);       	
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
        session.removeAttribute("listaDetalleNotifica");
        setAttribute(session,"listaDetalleNotifica", listaDetalleNotifica);
        forward(request, response, "/DetalleNotificaTrab.jsp");       
    }//FIN - JVILLACORTA - 29/11/2011 - ALERTA DE SOLICITUDES
    
    
/* ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
    
    /**
     * Metodo encargado de cargar la pagina del reporte de gestion: mensual por movimiento     
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void cargarMensualMovimiento(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	//Listar desde un anio fijo hasta anio actual
        	FechaBean fechaActual = new FechaBean();
        	Map mapaAnios=new HashMap();
        	mapaAnios.put("anioInicial",constantes.leePropiedad("ANIO_INICIO"));//anio inicial=2009            
        	mapaAnios.put("anioFinal",fechaActual.getAnho());//anio actual
        	session.removeAttribute("anios");
   	     	setAttribute(session,"anios",mapaAnios);
   	     	//fin listar

			session.removeAttribute("detalleReporte");
                      
            forward(request,response,"/reporteMensualMovimiento.jsp"+"?idSession="+System.currentTimeMillis());
            
            return;

        } catch (Exception e) {
        	
        	forwardError(request,response,e.getMessage());
        }

    }
    
    /**
     * Metodo encargado de generar el reporte de gestion: mensual por movimiento  
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void generarMensualMovimiento(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {

    	try {
    		List cabeceraReporte = null;//nuevo agregado 14/03/2012 icapunay
    		List reporte = null;//nuevo agregado 14/03/2012 icapunay
    		String nombreIntendencia="";
    		T12DAO t12DAOrh = new T12DAO(pool_sp); //trabaja con la t12uorga
    		ReporteDelegate rd = new ReporteDelegate();    		
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); 			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);            
            String usuario = bUsuario.getLogin();            
            String uoSeg = bUsuario.getVisibilidad();
                     
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);// si perfil es 'Adm. Asistencia'= analista central (rol); si perfil es 'Jefe UO'= jefe (rol)
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);// 'todas las unidades' si es rol 'analista central'; la 'uo del jefe' si es rol 'jefe'
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
           
            String criterio = request.getParameter("cmbCriterio"); //0=registro; 1=unidad organica; 4=intendencia; 3=institucional         
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();         
			String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) {
				valor = codInten;//2B0000
				List lIntendencia = t12DAOrh.findUObyCodUo(valor);
				Map mIntendencia = (HashMap)lIntendencia.get(0);
				nombreIntendencia = mIntendencia.get("t12des_corta").toString();
			}
			String regimen = request.getParameter("cmbTipoDL"); //0=planilla; 1=cas; 2=formativas             
            String anio = request.getParameter("cmbAnio"); //ejem: 2011
            String indicador = request.getParameter("cmbIndicador"); //h=horas; d=dias;
            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            ArrayList listaPeriodos = rd.buscarPeriodosCerradosPorRegimenPorAnio(pool_sp,regimen,anio.trim()); //obteniendo los datos de los periodos cerrados segun regimen y anio
                                   
            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("anio", anio);
                params.put("listaPeriodos", listaPeriodos);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("nombre_inte", nombreIntendencia);
                params.put("regimen", regimen);
                params.put("indicador", indicador);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);//registro del usuario logueado (NB30)                
                rd.masivoResumenMensualMovimiento(pool_sp, params, usuario);//usuario=ICAPUNAY

            } else {
            	log.debug("repetir1= " + repetir);
            	log.debug("tipoR1= " + tipoR);
            	//nuevo agregado 14/03/2012 icapunay
            	if (tipoR.equals("excel")) {
            		log.debug("tipoR2= " + tipoR);
            		ArrayList cabeceraReporte2 = (ArrayList) session.getAttribute("cabeceraReporte");
            		if(cabeceraReporte2!=null){
            			log.debug("entro a reporte!=null");
            			repetir = false;
            			log.debug("repetir2= " + repetir);
            		}            		
            	} 
            	//nuevo agregado 14/03/2012 icapunay
                if (!repetir) {              
                	Map mapa = new HashMap();
                	mapa.put("dbpool", pool_sp);
                	mapa.put("anio", anio);
                	mapa.put("listaPeriodos", listaPeriodos);
                	mapa.put("criterio", criterio);
                	mapa.put("valor", valor);
                	mapa.put("regimen", regimen);
                	mapa.put("indicador", indicador);
                	mapa.put("seguridad", seguridad);                	
                    
                	if (tipoR.equals("reporte")) {//nuevo agregado 14/03/2012 icapunay
                		log.debug("entro a reporte");
                		cabeceraReporte = rd.resumenMensualMovimiento_cabecera(mapa);//Obteniendo el Mapa cabecera de las categorias (horas&codPer / horasAnual)
                		session.removeAttribute("cabeceraReporte");
                        session.setAttribute("cabeceraReporte", cabeceraReporte);
                        log.debug("cabeceraReporte= " + cabeceraReporte);
                	}
                	if (tipoR.equals("excel")) {//nuevo agregado 14/03/2012 icapunay
                		log.debug("entro a excel");
                		reporte = rd.resumenMensualMovimiento(mapa);
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte);
                        log.debug("reporte= " + reporte);
                	}                	                                      
                    session.removeAttribute("listaPeriodos");
                    session.setAttribute("listaPeriodos", listaPeriodos);
                    //nuevo agregado 14/03/2012 icapunay
                    session.removeAttribute("regimen");
                    session.setAttribute("regimen", regimen); 
                    session.removeAttribute("anio");
                    session.setAttribute("anio", anio);                 
                    session.removeAttribute("criterio");
                    session.setAttribute("criterio", criterio);//0(registro) / 1(uuoo)
                    session.removeAttribute("valor");
                    session.setAttribute("valor", valor);//0091 o 2B2100
                    session.removeAttribute("indicador");
                    session.setAttribute("indicador", indicador);//h,d
                    //fin nuevo agregado 14/03/2012 icapunay
                }
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("anio", anio);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteMensualMovimiento.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {                 
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("nombre_inte", nombreIntendencia);
                request.setAttribute("anio", anio);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelMensualMovimiento.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE EVOLUCION MENSUAL POR MOVIMIENTO");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    /**
     * Metodo encargado de cargar la pagina del reporte de gestion: mensual por UUOO     
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void cargarMensualUUOO(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

    	try {

        	//Listar desde un anio fijo hasta anio actual
        	FechaBean fechaActual = new FechaBean();
        	Map mapaAnios=new HashMap();
        	mapaAnios.put("anioInicial",constantes.leePropiedad("ANIO_INICIO"));//anio inicial=2009            
        	mapaAnios.put("anioFinal",fechaActual.getAnho());//anio actual
        	session.removeAttribute("anios");
   	     	setAttribute(session,"anios",mapaAnios);
   	     	//fin listar
   	     	
   	     	//Listar categorias ordenadas por descripcion
   	     	MantenimientoDelegate md = new MantenimientoDelegate();
   	     	ArrayList categorias = md.buscarCategoriasOrdenadasByDescrip(pool_sp);   	     	
        	session.removeAttribute("categorias");
   	     	setAttribute(session,"categorias",categorias);
   	     	//fin listar

   	     	session.removeAttribute("detalleReporte");
                      
            forward(request,response,"/reporteMensualUUOO.jsp"+"?idSession="+System.currentTimeMillis());
            
            return;

        } catch (Exception e) {
        	
        	forwardError(request,response,e.getMessage());
        }
    }
    
    /**
     * Metodo encargado de generar el reporte de gestion: mensual por UUOO  
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void generarMensualUUOO(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {

    	try {
    		List cabeceraReporte = null;//nuevo agregado 14/03/2012 icapunay
    		List reporte = null;//nuevo agregado 14/03/2012 icapunay
    		String nombreIntendencia=""; 
    		T12DAO t12DAOrh = new T12DAO(pool_sp); //trabaja con la t12uorga	
    		ReporteDelegate rd = new ReporteDelegate(); 
    		MantenimientoDelegate md = new MantenimientoDelegate();    		
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); 			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);            
            String usuario = bUsuario.getLogin();            
            String uoSeg = bUsuario.getVisibilidad();                     
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);// si perfil es 'Adm. Asistencia'= analista central (rol); si perfil es 'Jefe UO'= jefe (rol)
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);// 'todas las unidades' si es rol 'analista central'; la 'uo del jefe' si es rol 'jefe'
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
			String regimen = request.getParameter("cmbTipoDL"); //0=planilla; 1=cas; 2=formativas
			log.debug("cmbTipoDL= " + regimen);
            String anio = request.getParameter("cmbAnio"); //ejem: 2011
            log.debug("cmbAnio= " + anio);
            String codigoCategoria = request.getParameter("cmbCategoria"); //ejem: 006 (Ausencia no justificada)
            log.debug("cmbCategoria= " + codigoCategoria);
            String indicador = request.getParameter("cmbIndicador"); //d=dias; nc=numero de colaboradores; dc=dias por colaborador;
            log.debug("cmbIndicador= " + indicador);
            String criterio = request.getParameter("cmbCriterio"); //0=registro; 1=unidad organica; 4=intendencia; 3=institucional   
            log.debug("cmbCriterio= " + criterio);
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: registro o UO
            log.debug("txtValor= " + valor);
            valor = valor.toUpperCase();            		
            String cod_intendencia = request.getParameter("cod_intendencia");
            log.debug("cod_intendencia= " + cod_intendencia);
            
            //String codInten =  (String) session.getAttribute("codInten"); //2B0000
            String codInten =  request.getParameter("txtCodInten"); //2B0000
            log.debug("codInten= " + codInten); 	
			
			if (criterio.trim().equals("4")) {//Intendencia
				valor = cod_intendencia!=null && !cod_intendencia.equals("")?cod_intendencia:(codInten!=null && !codInten.equals("")?codInten:"");//2B0000
				log.debug("valor= " + valor);
				if(valor!=null && !valor.equals("")){//solo if adicionado el 22/02/2012
					List lIntendencia = t12DAOrh.findUObyCodUo(valor);
					Map mIntendencia = (HashMap)lIntendencia.get(0);
					nombreIntendencia = mIntendencia.get("t12des_corta").toString();
				}else{			
					log.debug("valor3= " + valor);
					throw new ReporteException("Si desea volver a buscar por el criterio: Intendencia, por favor seleccionar otro criterio diferente a intendencia y finalmente volver a seleccionar el criterio intendencia.");
				}
			}
            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");
            log.debug("repetir0= " + repetir);
            if(request.getParameter("tipo")!=null){
            	 log.debug("request.getParameter(tipo0)= " + request.getParameter("tipo"));
            }           
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            log.debug("tipoR= " + tipoR);
            
            ArrayList listaPeriodos = rd.buscarPeriodosCerradosPorRegimenPorAnio(pool_sp,regimen,anio.trim()); //obteniendo los datos de los periodos cerrados segun regimen y anio
            Map mCategoria = md.buscarCategoria(pool_sp, codigoCategoria); //ejem: 006 (Ausencia no justificada)
            String nombreCategoria = mCategoria != null ? mCategoria.get("descrip").toString(): ""; //ejem: 006 (Ausencia no justificada)
                                   
            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("anio", anio);
                params.put("cod_cate", codigoCategoria);
                params.put("nombre_cate", nombreCategoria);
                params.put("listaPeriodos", listaPeriodos);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("nombre_inte", nombreIntendencia);
                params.put("regimen", regimen);
                params.put("indicador", indicador);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);//registro del usuario logueado (NB30)                
                rd.masivoResumenMensualUUOO(pool_sp, params, usuario);//usuario=ICAPUNAY

            } else {
            	log.debug("repetir1= " + repetir);
            	log.debug("tipoR1= " + tipoR);
            	//nuevo agregado 14/03/2012 icapunay
            	if (tipoR.equals("excel")) {
            		log.debug("tipoR2= " + tipoR);
            		ArrayList cabeceraReporte2 = (ArrayList) session.getAttribute("cabeceraReporte");
            		if(cabeceraReporte2!=null){
            			log.debug("entro a reporte!=null");
            			repetir = false;
            			log.debug("repetir2= " + repetir);
            		}            		
            	} 
            	//nuevo agregado 14/03/2012 icapunay
                if (!repetir) {
                	log.debug("repetir3= " + repetir);
                	Map mapa = new HashMap();
                	mapa.put("dbpool", pool_sp);                	
                	mapa.put("cod_cate", codigoCategoria);
                	mapa.put("listaPeriodos", listaPeriodos);
                	mapa.put("criterio", criterio);
                	mapa.put("valor", valor);
                	mapa.put("anio", anio);
                	mapa.put("regimen", regimen);
                	mapa.put("indicador", indicador);
                	mapa.put("seguridad", seguridad); 
                	
                	if (tipoR.equals("reporte")) {//nuevo agregado 14/03/2012 icapunay
                		log.debug("entro a reporte");
                		cabeceraReporte = rd.resumenMensualUUOO_cabecera(mapa);//Obteniendo el Mapa cabecera de las unidades(intendencias) (horas&codPer / horasAnual)
                		session.removeAttribute("cabeceraReporte");
                        session.setAttribute("cabeceraReporte", cabeceraReporte);
                        log.debug("cabeceraReporte= " + cabeceraReporte);
                	}
                	if (tipoR.equals("excel")) {//nuevo agregado 14/03/2012 icapunay
                		log.debug("entro a excel");
                		reporte = rd.resumenMensualUUOO(mapa);
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte);
                        log.debug("reporte= " + reporte);
                	}                                                        
                    session.removeAttribute("listaPeriodos");
                    session.setAttribute("listaPeriodos", listaPeriodos);                     
                    //nuevo agregado 14/03/2012 icapunay
                    session.removeAttribute("regimen");
                    session.setAttribute("regimen", regimen); 
                    session.removeAttribute("anio");
                    session.setAttribute("anio", anio); 
                    session.removeAttribute("categoria");
                    session.setAttribute("categoria", codigoCategoria); 
                    session.removeAttribute("criterio");
                    session.setAttribute("criterio", criterio);//0(registro) / 1(uuoo) / 4(intendencia)
                    session.removeAttribute("valor");
                    session.setAttribute("valor", valor);//0091 o 2B2100 o 2B0000  
                    session.removeAttribute("indicador");
                    session.setAttribute("indicador", indicador);//h,d,nc,dc
                    //if(criterio.trim().equals("4")){//intendencia
                    	//session.removeAttribute("codInten");//19/03/2012 mantener valor de intendencia buscada
                        //session.setAttribute("codInten", valor);//2B0000  
                    //}                    
                    //fin nuevo agregado 14/03/2012 icapunay
                }
            }
            if (tipoR.equals("reporte")) {
            	log.debug("tipoR.equals(reporte): " + tipoR.equals("reporte"));
            	log.debug("criterio: " + criterio);
                log.debug("valor: " + valor);
                request.setAttribute("criterio", criterio);
                if(criterio.trim().equals("0") || criterio.trim().equals("1")){//registro o uuoo
                	request.setAttribute("valor", valor);  
                }else if(criterio.trim().equals("4")){//intendencia                	
                	session.removeAttribute("codIntenBus");//19/03/2012 mantener valor de intendencia buscada
                    session.setAttribute("codIntenBus", valor);//2B0000 
                }
                request.setAttribute("anio", anio);
                request.setAttribute("categoria", codigoCategoria);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);
                //request.setAttribute("intendencia", cod_intendencia);//comente el 20/03/2012 icapunay                
                
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteMensualUUOO.jsp");
                dispatcher.forward(request, response);
                return;
            }
            if (tipoR.equals("excel")) { 
            	log.debug("tipoR.equals(excel): " + tipoR.equals("excel"));
            	log.debug("criterio: " + criterio);
                log.debug("valor: " + valor);
                request.setAttribute("criterio", criterio);
                if(criterio.trim().equals("0") || criterio.trim().equals("1")){//registro o uuoo
                	request.setAttribute("valor", valor);               	
                }else if(criterio.trim().equals("4")){//intendencia
                	session.removeAttribute("codIntenExc");//19/03/2012 mantener valor de intendencia buscada
                    session.setAttribute("codIntenExc", valor);//2B0000  
                }   
                request.setAttribute("nombre_inte", nombreIntendencia);
                request.setAttribute("anio", anio);
                request.setAttribute("categoria", codigoCategoria);
                request.setAttribute("nombreCategoria", nombreCategoria);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelMensualUUOO.jsp");
                dispatcher.forward(request, response);
                return;
            }
            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte","REPORTE EVOLUCION MENSUAL POR UNIDAD ORGANICA");
                
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
        	BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror(e.getMessage());
            session.setAttribute("beanErr",bean);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "+ e.getMessage());
            bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
    }
    
    
    /**
     * Metodo encargado de cargar la pagina del reporte de gestion: mensual por colaborador     
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void cargarMensualColaborador(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	//Listar desde un anio fijo hasta anio actual
        	FechaBean fechaActual = new FechaBean();
        	Map mapaAnios=new HashMap();
        	mapaAnios.put("anioInicial",constantes.leePropiedad("ANIO_INICIO"));//anio inicial=2009            
        	mapaAnios.put("anioFinal",fechaActual.getAnho());//anio actual
        	session.removeAttribute("anios");
   	     	setAttribute(session,"anios",mapaAnios);
   	     	//fin listar
   	     	
   	     	//Listar categorias ordenadas por descripcion
   	     	MantenimientoDelegate md = new MantenimientoDelegate();
   	     	ArrayList categorias = md.buscarCategoriasOrdenadasByDescrip(pool_sp);   	     	
        	session.removeAttribute("categorias");
   	     	setAttribute(session,"categorias",categorias);
   	     	//fin listar

   	     	session.removeAttribute("detalleReporte");
                      
            forward(request,response,"/reporteMensualColaborador.jsp"+"?idSession="+System.currentTimeMillis());
            
            return;

        } catch (Exception e) {
        	
        	forwardError(request,response,e.getMessage());
        }

    }
    
    /**
     * Metodo encargado de generar el reporte de gestion: mensual por colaborador  
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void generarMensualColaborador(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {

    	try {
    		String nombreIntendencia=""; 
    		T12DAO t12DAOrh = new T12DAO(pool_sp); //trabaja con la t12uorga
    		ReporteDelegate rd = new ReporteDelegate(); 
    		MantenimientoDelegate md = new MantenimientoDelegate();	
    		    		
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); 			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);            
            String usuario = bUsuario.getLogin();            
            String uoSeg = bUsuario.getVisibilidad();
                     
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);// si perfil es 'Adm. Asistencia'= analista central (rol); si perfil es 'Jefe UO'= jefe (rol)
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);// 'todas las unidades' si es rol 'analista central'; la 'uo del jefe' si es rol 'jefe'
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
           
            String criterio = request.getParameter("cmbCriterio"); //0=registro; 1=unidad organica; 4=intendencia; 3=institucional         
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: registro o UO
            valor = valor.toUpperCase();             
			String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) {
				valor = codInten;//2B0000
				List lIntendencia = t12DAOrh.findUObyCodUo(valor);
				Map mIntendencia = (HashMap)lIntendencia.get(0);
				nombreIntendencia = mIntendencia.get("t12des_corta").toString();
			}
			String regimen = request.getParameter("cmbTipoDL"); //0=planilla; 1=cas; 2=formativas             
            String anio = request.getParameter("cmbAnio"); //ejem: 2011
            String codigoCategoria = request.getParameter("cmbCategoria"); //ejem: 006 (Ausencia no justificada)
            String indicador = request.getParameter("cmbIndicador"); //h=horas; d=dias;
            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            ArrayList listaPeriodos = rd.buscarPeriodosCerradosPorRegimenPorAnio(pool_sp,regimen,anio.trim()); //obteniendo los datos de los periodos cerrados segun regimen y anio
            Map mCategoria = md.buscarCategoria(pool_sp, codigoCategoria); //ejem: 006 (Ausencia no justificada)
            String nombreCategoria = mCategoria != null ? mCategoria.get("descrip").toString(): ""; //ejem: 006 (Ausencia no justificada)
                                   
            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("anio", anio);
                params.put("cod_cate", codigoCategoria);
                params.put("nombre_cate", nombreCategoria);
                params.put("listaPeriodos", listaPeriodos);
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("nombre_inte", nombreIntendencia);
                params.put("regimen", regimen);
                params.put("indicador", indicador);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);//registro del usuario logueado (NB30)                
                rd.masivoResumenMensualColaborador(pool_sp, params, usuario);//usuario=ICAPUNAY

            } else {
                if (!repetir) {
              
                	Map mapa = new HashMap();
                	mapa.put("dbpool", pool_sp);
                	mapa.put("anio", anio);
                	mapa.put("cod_cate", codigoCategoria);
                	mapa.put("listaPeriodos", listaPeriodos);
                	mapa.put("criterio", criterio);
                	mapa.put("valor", valor);
                	mapa.put("regimen", regimen);
                	mapa.put("indicador", indicador);
                	mapa.put("seguridad", seguridad);                	
                    List detalleReporte = rd.resumenMensualColaborador(mapa); 
                                    
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                                    
                    session.removeAttribute("listaPeriodos");
                    session.setAttribute("listaPeriodos", listaPeriodos);                   
                }
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("anio", anio);
                request.setAttribute("categoria", codigoCategoria);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteMensualColaborador.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {                 
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("nombre_inte", nombreIntendencia);
                request.setAttribute("anio", anio);
                request.setAttribute("categoria", codigoCategoria);
                request.setAttribute("nombreCategoria", nombreCategoria);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelMensualColaborador.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE EVOLUCION MENSUAL POR COLABORADOR");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
    }    
    
    
    /**
     * Metodo encargado de cargar la pagina del reporte de gestion: diario por movimiento     
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void cargarDiarioMovimiento(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
    	
    	ArrayList listaPeriodosPlanilla = null;

        try {

        	if (log.isDebugEnabled()) log.debug("regimen:" + "0"); //regimen=0 planilla 
        	
        	ReporteDelegate rd = new ReporteDelegate();        	
        	listaPeriodosPlanilla = rd.buscarPeriodosCerradosPorRegimen(pool_sp,"0",constantes.leePropiedad("FECHA_INICIO"));      
        	session.removeAttribute("detalleReporte");
        	session.removeAttribute("listaPeriodos");
            setAttribute(session,"listaPeriodos", listaPeriodosPlanilla);
                      
            forward(request,response,"/reporteDiarioMovimiento.jsp"+"?idSession="+System.currentTimeMillis());
            
            return;

        } catch (Exception e) {
        	
        	forwardError(request,response,e.getMessage());
        }

    }
    
    /**
     * Metodo para los periodos cerrados por regimen/modalidad seleccionada
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarPeriodosCerradosPorRegimen(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {
    	
    	ArrayList listaPeriodos = null;
    	
        try {
        	        
        	String indicador = request.getParameter("cmbIndicador");
        	String criterio = request.getParameter("cmbCriterio");
        	String tipoRegimen = request.getParameter("tipoRegimen"); //0 planilla /1 cas /2 formativas
        	String valor="";        	
        	
        	request.setAttribute("regimen", tipoRegimen);
        	request.setAttribute("indicador", indicador);        	
        	request.setAttribute("criterio", criterio);    
        	
        	if (!criterio.trim().equals("4")) {// criterio no es intendencia
        		valor = request.getParameter("txtValor"); 
        		request.setAttribute("valor", valor);
        	} 	        	
        	if (log.isDebugEnabled()) log.debug("tipoRegimen: " + tipoRegimen);
        	
        	ReporteDelegate rd = new ReporteDelegate();        	
        	listaPeriodos = rd.buscarPeriodosCerradosPorRegimen(pool_sp,tipoRegimen,constantes.leePropiedad("FECHA_INICIO"));         	           
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
        session.removeAttribute("listaPeriodos");
        setAttribute(session,"listaPeriodos", listaPeriodos);
        forward(request, response, "/reporteDiarioMovimiento.jsp");       
    }
    
    /**
     * Metodo encargado de generar el reporte de gestion: mensual por movimiento  
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void generarDiarioMovimiento(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {

    	try {
    		List cabeceraReporte = null;//nuevo agregado 14/03/2012 icapunay
    		List reporte = null;//nuevo agregado 14/03/2012 icapunay
    		String nombreIntendencia=""; 
    		T12DAO t12DAOrh = new T12DAO(pool_sp); //trabaja con la t12uorga
    		ReporteDelegate rd = new ReporteDelegate();
            MantenimientoDelegate md = new MantenimientoDelegate();    		
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); 			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);            
            String usuario = bUsuario.getLogin();            
            String uoSeg = bUsuario.getVisibilidad();                     
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);// si perfil es 'Adm. Asistencia'= analista central (rol); si perfil es 'Jefe UO'= jefe (rol)
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);// 'todas las unidades' si es rol 'analista central'; la 'uo del jefe' si es rol 'jefe'
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
           
            String regimen = request.getParameter("cmbTipoDL"); //0=planilla; 1=cas; 2=formativas             
            String periodo = request.getParameter("cmbPeriodo"); //<>-1 /ejem: 201108
            String indicador = request.getParameter("cmbIndicador"); //h=horas; d=dias;
            String criterio = request.getParameter("cmbCriterio"); //0=registro; 1=unidad organica; 4=intendencia; 3=institucional         
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: registro o UO  
            valor = valor.toUpperCase();           
			String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("4")) {
				valor = codInten;//2B0000
				List lIntendencia = t12DAOrh.findUObyCodUo(valor);
				Map mIntendencia = (HashMap)lIntendencia.get(0);
				nombreIntendencia = mIntendencia.get("t12des_corta").toString();
			}			
            boolean repetir = request.getParameter("repetir").toString().trim().equals("true");
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            BeanPeriodo bPer = md.buscarPeriodoCodigo(pool_sp, periodo);//obteniendo los datos del periodo seleccionado
                       
            if (regimen.trim().equals("1")) {//cas
				bPer.setFechaIni(bPer.getFechaIniCAS());
				bPer.setFechaFin(bPer.getFechaFinCAS());
			} else{
				if (regimen.trim().equals("2")) {//formativas
					bPer.setFechaIni(bPer.getFechaIniModForm());
					bPer.setFechaFin(bPer.getFechaFinModForm());
				}
			}

            if (tipoR.equals("imprimir")) {
                HashMap params = new HashMap();
                params.put("periodo", periodo);
                params.put("fechaIni", bPer.getFechaIni());
                params.put("fechaFin", bPer.getFechaFin());
                params.put("criterio", criterio);
                params.put("valor", valor);
                params.put("nombre_inte", nombreIntendencia);
                params.put("seguridad", seguridad);
                params.put("codPers", codigo);//registro del usuario logueado (NB30)
                params.put("regimen", regimen);
                params.put("indicador", indicador);
                rd.masivoResumenDiarioMovimiento(pool_sp, params, usuario);//usuario=ICAPUNAY

            } else {
            	log.debug("repetir1= " + repetir);
            	log.debug("tipoR1= " + tipoR);
            	//nuevo agregado 14/03/2012 icapunay
            	if (tipoR.equals("excel")) {
            		log.debug("tipoR2= " + tipoR);
            		ArrayList cabeceraReporte2 = (ArrayList) session.getAttribute("cabeceraReporte");
            		if(cabeceraReporte2!=null){
            			log.debug("entro a reporte!=null");
            			repetir = false;
            			log.debug("repetir2= " + repetir);
            		}            		
            	} 
            	//nuevo agregado 14/03/2012 icapunay
                if (!repetir) {              
                	Map mapa = new HashMap();
                	mapa.put("dbpool", pool_sp);
                	mapa.put("periodo", periodo);
                	mapa.put("criterio", criterio);
                	mapa.put("valor", valor);
                	mapa.put("seguridad", seguridad);
                	mapa.put("regimen", regimen);
                	mapa.put("indicador", indicador);
                	
                	if (tipoR.equals("reporte")) {//nuevo agregado 14/03/2012 icapunay
                		log.debug("entro a reporte");
                		cabeceraReporte = rd.resumenDiarioMovimiento_cabecera(mapa);//Obteniendo el Mapa cabecera de las categorias (horas&codPer / horasAnual)
                		session.removeAttribute("cabeceraReporte");
                        session.setAttribute("cabeceraReporte", cabeceraReporte);
                        log.debug("cabeceraReporte= " + cabeceraReporte);
                	}
                	if (tipoR.equals("excel")) {//nuevo agregado 14/03/2012 icapunay
                		log.debug("entro a excel");
                		reporte = rd.resumenDiarioMovimiento(mapa);
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte);
                        log.debug("reporte= " + reporte);
                	} 
                	//nuevo agregado 14/03/2012 icapunay
                    session.removeAttribute("regimen");
                    session.setAttribute("regimen", regimen); 
                    session.removeAttribute("periodo");
                    session.setAttribute("periodo", periodo);   
                    session.removeAttribute("criterio");
                    session.setAttribute("criterio", criterio);//0(registro) / 1(uuoo)
                    session.removeAttribute("valor");
                    session.setAttribute("valor", valor);//0091 o 2B2100
                    session.removeAttribute("indicador");
                    session.setAttribute("indicador", indicador);//h,d
                    session.removeAttribute("fechaIni");
                    session.setAttribute("fechaIni", bPer.getFechaIni()); 
                    session.removeAttribute("fechaFin");
                    session.setAttribute("fechaFin", bPer.getFechaFin());
                    //fin nuevo agregado 14/03/2012 icapunay
                }
            }

            if (tipoR.equals("reporte")) {
                request.setAttribute("fechaIni", bPer.getFechaIni());
                request.setAttribute("fechaFin", bPer.getFechaFin());
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("per", periodo);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteDiarioMovimiento.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
            	request.setAttribute("fechaIni", bPer.getFechaIni());
                request.setAttribute("fechaFin", bPer.getFechaFin());               
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("nombre_inte", nombreIntendencia);
                request.setAttribute("periodo", periodo);
                request.setAttribute("regimen", regimen);
                request.setAttribute("indicador", indicador);

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelDiarioMovimiento.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        "REPORTE EVOLUCION DIARIA POR MOVIMIENTO");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    
    /**
	 * Metodo encargado de mostrar el detalle diario de los valores de los movimientos de una categoria seleccionada
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarDetalleDiarioMovi(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
			ReporteDelegate rd = new ReporteDelegate();
			String indice = request.getParameter("indice");//indice (i) de categoria seleccionada
			String regimen = (String) session.getAttribute("regimen"); //0=planilla; 1=cas; 2=formativas
			String periodo = (String) session.getAttribute("periodo"); //ejem: 200801            
            String criterio = (String) session.getAttribute("criterio"); //0=registro; 1=unidad organica;       
            String valor =  (String) session.getAttribute("valor"); //cuando criterios son: registro(0091) o UO(2B2100)
            String indicador =  (String) session.getAttribute("indicador");//h,d
						
			if (!indice.equals("")) { //ha seleccionado una categoria
				String fechaIni = (String) session.getAttribute("fechaIni"); //ejem: 01/03/2008 
	            String fechaFin = (String) session.getAttribute("fechaFin"); //ejem: 31/03/2008  
				ArrayList cabeceraReporte = (ArrayList) session.getAttribute("cabeceraReporte");
				
				BeanReporte categoriaSelec = (BeanReporte) cabeceraReporte.get(Integer.parseInt(indice));
				String codigoCateSelec = categoriaSelec.getCategoria(); //Codigo de Categoria Seleccionada
				String nombreCateSelec = categoriaSelec.getDescripcion(); //Nombre de Categoria Seleccionada				
				ArrayList movimientosCateSelec = categoriaSelec.getDetalle(); //Movimientos de la categoria
				HashMap cabeceraCateSelec = categoriaSelec.getMap(); //Obteniendo todo el Mapa de la Categoria Seleccionada
				
				Map mapa = new HashMap();
	        	mapa.put("dbpool", pool_sp);	        	
	        	mapa.put("periodo", periodo);
	        	mapa.put("criterio", criterio);
	        	mapa.put("valor", valor);	        
	        	mapa.put("regimen", regimen);
	        	mapa.put("indicador", indicador);
	        	mapa.put("categoria", codigoCateSelec);//codigo (009=otros) de categoria seleccionada
				
				Map detalleCateSelec = rd.resumenDiarioMovimiento_detalle(mapa); //Obteniendo el Mapa detalle de la categoria Seleccionada	(horas&codMov&codPer / horasAnual&codMov)

				//request.setAttribute("fechaIniExcel", fechaIni);
                //request.setAttribute("fechaFinExcel", fechaFin);
                
                session.removeAttribute("nombreCategoria");
				session.setAttribute("nombreCategoria", nombreCateSelec);
				session.removeAttribute("cabeceraCategoria");
				session.setAttribute("cabeceraCategoria", cabeceraCateSelec);
				session.removeAttribute("detalleCategoria");
				session.setAttribute("detalleCategoria", detalleCateSelec);
				session.removeAttribute("movimientos");
				session.setAttribute("movimientos", movimientosCateSelec);
				session.removeAttribute("indicador");
				session.setAttribute("indicador", indicador);
				session.removeAttribute("fechaIni");
				session.setAttribute("fechaIni", fechaIni);
				session.removeAttribute("fechaFin");
				session.setAttribute("fechaFin", fechaFin);
								
			} //fin ha seleccionado una categoria			

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/detalleRptDiarioMovimiento.jsp");
			dispatcher.forward(request, response);	
			return;

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	/**
	 * Metodo encargado de mostrar el detalle mensual de los valores de los movimientos de una categoria seleccionada
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarDetalleMensualMovi(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
			ReporteDelegate rd = new ReporteDelegate();
			String indice = request.getParameter("indice");//indice (i) de categoria seleccionada				
			String regimen = (String) session.getAttribute("regimen"); //0=planilla; 1=cas; 2=formativas             
            String anio = (String) session.getAttribute("anio"); //ejem: 2008           
            String criterio = (String) session.getAttribute("criterio"); //0=registro; 1=unidad organica;       
            String valor =  (String) session.getAttribute("valor"); //cuando criterios son: registro(0091) o UO(2B2100)
            String indicador =  (String) session.getAttribute("indicador");//h,d
			
			
			if (!indice.equals("")) { //ha seleccionado una categoria
						
				ArrayList listaPeriodos = (ArrayList) session.getAttribute("listaPeriodos");
				ArrayList cabeceraReporte = (ArrayList) session.getAttribute("cabeceraReporte");	
				
				BeanReporte categoriaSelec = (BeanReporte) cabeceraReporte.get(Integer.parseInt(indice));
				String codigoCateSelec = categoriaSelec.getCategoria(); //Codigo de Categoria Seleccionada
				String nombreCateSelec = categoriaSelec.getDescripcion(); //Nombre de Categoria Seleccionada						
				ArrayList movimientosCateSelec = categoriaSelec.getDetalle(); //Movimientos de la categoria
				HashMap cabeceraCateSelec = categoriaSelec.getMap(); //Obteniendo todo el Mapa de la Categoria Seleccionada		
				
				Map mapa = new HashMap();
	        	mapa.put("dbpool", pool_sp);	        	
	        	mapa.put("listaPeriodos", listaPeriodos);
	        	mapa.put("criterio", criterio);
	        	mapa.put("valor", valor);
	        	mapa.put("anio", anio);
	        	mapa.put("regimen", regimen);
	        	mapa.put("indicador", indicador);
	        	mapa.put("categoria", codigoCateSelec);//codigo (009=otros) de categoria seleccionada
				
				Map detalleCateSelec = rd.resumenMensualMovimiento_detalle(mapa); //Obteniendo el Mapa detalle de la categoria Seleccionada	(horas&codMov&codPer / horasAnual&codMov)
				
				session.removeAttribute("nombreCategoria");
				session.setAttribute("nombreCategoria", nombreCateSelec);
				session.removeAttribute("cabeceraCategoria");
				session.setAttribute("cabeceraCategoria", cabeceraCateSelec);
				session.removeAttribute("detalleCategoria");
				session.setAttribute("detalleCategoria", detalleCateSelec);				
				session.removeAttribute("movimientos");
				session.setAttribute("movimientos", movimientosCateSelec);
				session.removeAttribute("periodos");
				session.setAttribute("periodos", listaPeriodos);
				session.removeAttribute("indicador");
				session.setAttribute("indicador", indicador);
								
			} //fin ha seleccionado una categoria			

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/detalleRptMensualMovimiento.jsp");
			dispatcher.forward(request, response);	
			return;

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}
	
	/**
	 * Metodo encargado de mostrar el detalle mensual de los valores de las Unidades Organicas subordinadas de una Intendencia seleccionada
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarDetalleMensualUUOO(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		try {
			ReporteDelegate rd = new ReporteDelegate();
			String indice = request.getParameter("indice");//indice (i) de unidad(Intendencia) seleccionada			
			String regimen = (String) session.getAttribute("regimen"); //0=planilla; 1=cas; 2=formativas             
            String anio = (String) session.getAttribute("anio"); //ejem: 2008
            String categoria = (String) session.getAttribute("categoria"); //ejem: 009 (Otros)
            String criterio = (String) session.getAttribute("criterio"); //0=registro; 1=unidad organica; 4=intendencia       
            String valor =  (String) session.getAttribute("valor"); //cuando criterios son: registro(0091) o UO(2B2100) o intendencia(2B0000)
            String indicador =  (String) session.getAttribute("indicador");//h,d,nc,dc
           				
			if (!indice.equals("")) { //ha seleccionado una unidad(Intendencia)
				
				ArrayList listaPeriodos = (ArrayList) session.getAttribute("listaPeriodos");
				ArrayList cabeceraReporte = (ArrayList) session.getAttribute("cabeceraReporte");//tiene getCodigo(), getDescripcion(), getDetalle(Subunidades) y getMap(valores de la intendencia)			
			
				BeanReporte intendenciaSelec = (BeanReporte) cabeceraReporte.get(Integer.parseInt(indice));
				String codigoIntenSelec = intendenciaSelec.getCodigo(); //Codigo de la unidad(intendencia) Seleccionada
				String nombreIntenSelec = intendenciaSelec.getDescripcion(); //Nombre de la unidad(intendencia) Seleccionada
				ArrayList subUnidadesIntenSelec = intendenciaSelec.getDetalle(); //subUnidades que conforman la unidad(intendencia) seleccionada
				HashMap cabeceraIntenSelec = intendenciaSelec.getMap(); //Obteniendo el Mapa cabecera de la unidad(intendencia) Seleccionada	(horas&codPer / horasAnual)
				
				Map mapa = new HashMap();
	        	mapa.put("dbpool", pool_sp);                	
	        	mapa.put("cod_cate", categoria);
	        	mapa.put("listaPeriodos", listaPeriodos);
	        	mapa.put("criterio", criterio);
	        	mapa.put("valor", valor);
	        	mapa.put("anio", anio);
	        	mapa.put("regimen", regimen);
	        	mapa.put("indicador", indicador);
	        	mapa.put("intendencia", codigoIntenSelec);
				
				Map detalleIntenSelec = rd.resumenMensualUUOO_detalle(mapa); //Obteniendo el Mapa detalle de la unidad(intendencia) Seleccionada	(horas&codPer&codSubuo / horasAnual&codSubuo)
				
				session.removeAttribute("unidadIntendencia");
				session.setAttribute("unidadIntendencia", nombreIntenSelec);
				session.removeAttribute("cabeceraInten");
				session.setAttribute("cabeceraInten", cabeceraIntenSelec);
				session.removeAttribute("detalleInten");
				session.setAttribute("detalleInten", detalleIntenSelec);
				session.removeAttribute("subUnidades");
				session.setAttribute("subUnidades", subUnidadesIntenSelec);
				session.removeAttribute("periodos");
				session.setAttribute("periodos", listaPeriodos);				
								
			} //fin ha seleccionado una categoria			

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/detalleRptMensualUUOO.jsp");
			dispatcher.forward(request, response);	
			return;

		} catch (MantenimientoException e) {
			session.setAttribute("beanErr", e.getBeanMensaje());
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		} catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
		}
	}
    /* FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
    
    //PRAC-ASANCHEZ 04/08/2009
	/**
	 * Metodo encargado de cargar los objetos a memoria
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarParametros() throws ServletException, IOException {
		try {
			ParamDAO paramDAO = new ParamDAO();
			
			//Intendencias
			String query = "select t12cod_uorga, t12des_uorga from t12uorga where " +
			"t12ind_estad = '1' and ( " +		
			"(t12cod_uorga like '1%0000' and t12cod_uorga != '100000') or " + 
		    "(t12cod_uorga like '2%0000' and t12cod_uorga != '200000') or " + 
		    //ICAPUNAY 09/02/2012 PAS20124E550000022 listado de nuevas intendencias de la uo: 400000
			//"(t12cod_uorga like '3%0000' and t12cod_uorga != '300000') )";
		    "(t12cod_uorga like '3%0000' and t12cod_uorga != '300000') or " +
		    "(t12cod_uorga like '4%0000' and t12cod_uorga != '400000') or " + 
		    //ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014
		    "(t12cod_uorga like '5%0000' and t12cod_uorga != '500000') or " + 
		    "(t12cod_uorga like '6%0000' and t12cod_uorga != '600000') or " +	
		    "(t12cod_uorga like '7%0000' and t12cod_uorga != '700000') or " +	//ICAPUNAY - PAS20175E230300114 - Cambio Rof 2017
		    "(t12cod_uorga like '8%0000' and t12cod_uorga != '800000') )";
			//ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014
			
			paramDAO.cargar(query, pool_sp, "spprmIntendencias");
			
			/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 20/03/2012 */
			//Intendencias activas e inactivas
			String query2 = "select t12cod_uorga, t12des_uorga from t12uorga where " +				
			"((t12cod_uorga like '1%0000' and t12cod_uorga != '100000') or " + 
		    "(t12cod_uorga like '2%0000' and t12cod_uorga != '200000') or " +		  
		    "(t12cod_uorga like '3%0000' and t12cod_uorga != '300000') or " +
		    "(t12cod_uorga like '4%0000' and t12cod_uorga != '400000') or " + 
		    //ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014
		    "(t12cod_uorga like '5%0000' and t12cod_uorga != '500000') or " + 
		    "(t12cod_uorga like '6%0000' and t12cod_uorga != '600000') or " +
		    "(t12cod_uorga like '7%0000' and t12cod_uorga != '600000') or " +	//ICAPUNAY - PAS20175E230300114 - Cambio Rof 2017
		    "(t12cod_uorga like '8%0000' and t12cod_uorga != '800000') ) " +
			//ICAPUNAY-PAS20144EB20000106-Cambio ROF 2014
			"order by t12des_corta asc,t12cod_uorga asc ";
						
			paramDAO.cargar(query2, pool_sp, "spprmIntendencias2");
			/*FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 20/03/2012 */
			
		} catch (Exception e) {
			log.error("error al cargar Cache:"+e);
		}
	}	
	//	
	
    /** NVS - 12/03/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para llamar al jsp que mostrara la opcion de Solicitud Labor Excepcional y Compensaciones
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarSolicitudLabExcComp(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        try {
        	if (log.isDebugEnabled()) log.debug("Ingreso a cargarSolicitudLabExcComp");
       	 UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("roles:"+roles);
			if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  {				
				//Analista

				String chk_1 = "1";
	            String chk_2 = "1";
	            String chk_3 = "1";
	            session.setAttribute("chk_1", chk_1);
	            session.setAttribute("chk_2", chk_2);
	            session.setAttribute("chk_3", chk_3);

	        	RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/consultaSolLabExcepAnalista.jsp");
	            dispatcher.forward(request, response);				
			}
			else {			
				//Colaborador
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/consultaSolLaborExcepcional.jsp");
				dispatcher.forward(request, response);
			}
        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }  
    }//FIN - NVS - 12/03/2012 - AOM LABOR EXCEPCIONAL
    
    
    /** NVS - 12/03/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para buscar data de la consulta de solicitud de labor excepcional y compensacion COLABORADOR
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void consultarSolicitudLE(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {    	
        try {
        	if (log.isDebugEnabled()) log.debug("Ingreso a consultarSolicitudLE");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			if (log.isDebugEnabled()) log.debug("codigo: "+codigo);
			String usuario = bUsuario.getLogin();
			if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("roles: "+roles);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			if (log.isDebugEnabled()) log.debug("usrRoles: "+usrRoles);
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("roles2: "+roles);			
			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();
			int flag =0;	
			HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("codUO", codUO);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            String anio = "";
            String numero = "";
            if (log.isDebugEnabled()) log.debug("seguridad: "+seguridad);
            String criterio = request.getParameter("cmbCriterio");
            if (log.isDebugEnabled()) log.debug("criterio: " + criterio);
            String labor = "";
            if (criterio.trim().equals("0")) {            	
            	labor = "Labor Excepcional";
            } 
            if (criterio.trim().equals("1")) {            	
            	labor = "Compensacion";
            }
            /*if (criterio.trim().equals("2")){
            	labor = "Labor Excepcional y Compensacion";
            }*/
            String estado = request.getParameter("cmbEstado");
            if (log.isDebugEnabled()) log.debug("estado: " + estado);
            String fechaIni = request.getParameter("fechaIni");
            if (log.isDebugEnabled()) log.debug("fechaIni: " + fechaIni);
            String fechaFin = request.getParameter("fechaFin");
            if (log.isDebugEnabled()) log.debug("fechaFin: " + fechaFin);
            if(request.getParameter("txtAnio").length()>0)
            {
            	flag=1;
            	anio = request.getParameter("txtAnio");
            	if (log.isDebugEnabled()) log.debug("anio: " + anio);
            
            	numero = request.getParameter("txtNumero");
            	if (log.isDebugEnabled()) log.debug("numero: " + numero);
            }
            boolean repetir = request.getParameter("repetir").toString().trim()
            .equals("true");
            log.debug("repetir: "+repetir);
            
            ReporteDelegate rd = new ReporteDelegate();
            String nombCompleto = rd.buscarNombCompleto(pool_sp, codigo);
            if (log.isDebugEnabled()) log.debug("tipoR INICIAL: "+ request.getParameter("tipoReporte"));
            String tipoR = request.getParameter("tipoReporte") != null ? request.getParameter("tipoReporte") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: "+tipoR);
            HashMap params = new HashMap();
            params.put("dbpool", pool_sp);
            params.put("fechaIni", fechaIni);
            params.put("fechaFin", fechaFin);
            params.put("criterio", criterio);
            params.put("estado", estado);
            params.put("seguridad", seguridad);
            params.put("codigo", codigo);
            params.put("nombCompleto", nombCompleto);
            params.put("labor", labor);
            params.put("codPers", codigo);
            params.put("flag", ""+flag);
            if(flag==1){
            	params.put("anio", anio);
            	params.put("numero", numero);
            }
            if (tipoR.equals("imprimir")) {
            	if (log.isDebugEnabled()) log.debug("tipoR.equals = " + tipoR);
            	ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            	pdf=rd.masivoLaborExcepcional1(pool_sp, params, usuario);
            	if (pdf!=null) {
                    response.setContentType("application/pdf");            
                    response.setContentLength(pdf.size());
                    response.setHeader("Content-Disposition","inline; filename=cas.pdf");
                    ServletOutputStream out = response.getOutputStream();
                    pdf.writeTo(out);
                    out.flush();  
                  }
            } else {
            	if (log.isDebugEnabled()) log.debug("repetir??? = " + repetir);
            	if (log.isDebugEnabled()) log.debug("tipoR??? = " + tipoR);
            	if (tipoR.equals("excel")) {
            		if (log.isDebugEnabled()) log.debug("tipoEXCEL= " + tipoR);
            		ArrayList validaReporte = (ArrayList) session.getAttribute("detalleReporte");
            		if(validaReporte!=null){
            			log.debug("entro a reporte!=null");
            			repetir = false;
            			log.debug("repetir2= " + repetir);
            		}            		
            	}
            	log.debug("repetir aca: "+repetir);
            	if (!repetir) {
                	
                	if (tipoR.equals("reporte")) {
                		log.debug("entro a reporte");
                		if (log.isDebugEnabled()) log.debug(">> Entro a reporte: ");
                		ArrayList detalleReporte = rd.consultarSolicitudesLE(params);
                		if (log.isDebugEnabled()) log.debug("Continua en el SERVLET - SALIO DEL DELEGATE: "+detalleReporte);
                		session.removeAttribute("detalleReporte");
                		session.setAttribute("detalleReporte", detalleReporte);
                		if (log.isDebugEnabled()) log.debug("detalleReporte: "+detalleReporte);
                	}
                	if (tipoR.equals("excel")) {
                		log.debug("entro a excel");
                		ArrayList reporte = rd.resumenSolLabExcepcional(params);
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte);
                        if (log.isDebugEnabled()) log.debug("reporte= " + reporte);
                	}
                } 
            }
            
            if (tipoR.equals("reporte")) {
                session.setAttribute("anio", anio);
                session.setAttribute("numero", numero);
                
                session.setAttribute("criterio", criterio);
                session.setAttribute("estado", estado);
                session.setAttribute("fini", fechaIni);
                session.setAttribute("ffin", fechaFin);
                
                if (log.isDebugEnabled()) log.debug(">>seguridad: "+ seguridad);
                if (log.isDebugEnabled()) log.debug(">>criterio: " + criterio);
                if (log.isDebugEnabled()) log.debug(">>estado: " + estado);
                if (log.isDebugEnabled()) log.debug(">>fechaIni: " + fechaIni);
                if (log.isDebugEnabled()) log.debug(">>fechaFin: " + fechaFin);
                if (log.isDebugEnabled()) log.debug(">>anio: " + anio);
                if (log.isDebugEnabled()) log.debug(">>numero: " + numero);
                if (log.isDebugEnabled()) log.debug("va ingresar al JSP: ");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/consultaSolLaborExcepcional.jsp") ;
                dispatcher.forward(request, response);
            }
            
            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                request.setAttribute("usuario", usuario);
                request.setAttribute("codigo", codigo);
                request.setAttribute("nombCompleto", nombCompleto);
                request.setAttribute("labor", labor);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelConsultaSolLabExcepcional.jsp");
                dispatcher.forward(request, response);
                return;
            }

            /*if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        " de SOLICITUDES DE LABOR EXCEPCIONAL");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }*/
            
        } catch (Exception e) {
        	log.error("Error:"+e.getMessage());
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }  
    }//FIN - NVS - 12/03/2012 - AOM LABOR EXCEPCIONAL
	
    /** NVS - 02/05/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para buscar la consulta de solicitud de labor excepcional y compensacion ANALISTA CENTRAL
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void consultarSolicitudLE2(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_OPERATIVO"))!=null) uoSeg = uoSeg.substring(0,2).concat("%");			
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles );//session.getAttribute("perfil_usuario").toString());
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg); 
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
            String regimen = request.getParameter("cmbRegimen");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String criterio = request.getParameter("cmbCriterio");
            String valor =   (request.getParameter("txtValor")!= null )? request.getParameter("txtValor"):""; //cuando criterios son: Registro o UUOO
            String nombConsultado="";
            String chk_1 = request.getParameter("chk_1");
            String chk_2 = request.getParameter("chk_2");
            String chk_3 = request.getParameter("chk_3");
            
            String cod_intendencia= null;
            String nombreIntendencia = null;
            
            T12DAO t12DAOrh = new T12DAO(pool_sp); //trabaja con la t12uorga (rrhh.dao)

			if (criterio.trim().equals("5")) { // Intendencia
				
				cod_intendencia = request.getParameter("cod_intendencia");
	            log.debug("cod_intendencia= " + cod_intendencia);
				valor = cod_intendencia!=null && !cod_intendencia.equals("")?cod_intendencia:"";
				log.debug("valor= " + valor);
				if(valor!=null && !valor.equals("")){//solo if adicionado el 22/02/2012
                    List lIntendencia = t12DAOrh.findUObyCodUo(valor);
                    Map mIntendencia = (HashMap)lIntendencia.get(0);
                    nombreIntendencia = mIntendencia.get("t12des_corta").toString();
               }else{                  
                    log.debug("valor3= " + valor);
                    throw new ReporteException("Si desea volver a buscar por el criterio: Intendencia, por favor seleccionar otro criterio diferente a intendencia y finalmente volver a seleccionar el criterio intendencia.");
               }
				
			}
			
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");
            log.debug("repetir: "+repetir);
            
            ReporteDelegate rd = new ReporteDelegate();
            String nombCompleto = rd.buscarNombCompleto(pool_sp, codigo);
            if (criterio.trim().equals("0")) { // Registro
            	nombConsultado = rd.buscarNombCompleto(pool_sp, valor); 
			}

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: "+tipoR);
            if (log.isDebugEnabled()) log.debug("tipoRaaaa1: "+chk_1);
            if (log.isDebugEnabled()) log.debug("tipoRaaaaa2: "+chk_2);
            if (log.isDebugEnabled()) log.debug("tipoRaaaaa3: "+chk_3);
            
            String solicitud1 = chk_1!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
            String solicitud2 = chk_2!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
            String solicitud3 = chk_3!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
            
            if (log.isDebugEnabled()) log.debug("checkbox LE: " + solicitud1);
            if (log.isDebugEnabled()) log.debug("checkbox AUTO: " + solicitud2);
            if (log.isDebugEnabled()) log.debug("checkbox COMPE: " + solicitud3);
            
            session.setAttribute("solicitud1", solicitud1);
            session.setAttribute("solicitud2", solicitud2);
            session.setAttribute("solicitud3", solicitud3);
            
            String id_labor="0"; //Labor Excepcional deseleccionada.
            String indicador="0"; //Identifica el tipo de solicitud Seleccionado.
            
            if( ( solicitud1.equals("1") && solicitud2.equals("1") ) ||  ( solicitud1.equals("0") && solicitud2.equals("1") )  ||  ( solicitud1.equals("1") && solicitud2.equals("0") ) ){
            	id_labor = "1"; // Labor Excepcional Seleccionada.
            }
            
            if( ( id_labor.equals("1") && solicitud3.equals("1") ) ||  ( id_labor.equals("0") && solicitud3.equals("0") ) ){
            	indicador = "1"; // Labor Excepcional y Compensacion Seleccionados.
            }
            
            if(  id_labor.equals("1") && solicitud3.equals("0")  ){
            	indicador = "2"; // Solamente Labor Excepcional Seleccionada.
            }
            
            if(  id_labor.equals("0") && solicitud3.equals("1")  ){
            	indicador = "3"; // Solamente Compensacion Seleccionada.
            }
            
            
            HashMap params = new HashMap();
            params.put("dbpool", pool_sp);
            params.put("regimen", regimen);
            params.put("fechaIni", fechaIni);
            params.put("fechaFin", fechaFin);
            params.put("criterio", criterio);
            params.put("valor", valor);
            if (criterio.trim().equals("5")) { // Intendencia
            	params.put("nombre_inte", nombreIntendencia);
            }
            params.put("seguridad", seguridad);
            params.put("codPers", codigo);
            params.put("codigo", codigo);
            params.put("nombCompleto", nombCompleto);
            params.put("solicitud1", solicitud1);
            params.put("solicitud2", solicitud2);
            params.put("solicitud3", solicitud3);
            params.put("indicador", indicador);
            
            if (tipoR.equals("imprimir")) {
            	rd.masivoLaborExcepcional2(pool_sp, params, usuario);
            } else {
                
            	if (log.isDebugEnabled()) log.debug("repetir??? = " + repetir);
            	if (log.isDebugEnabled()) log.debug("tipoR??? = " + tipoR);
            	if (tipoR.equals("excel")) {
            		if (log.isDebugEnabled()) log.debug("tipoEXCEL= " + tipoR);
            		ArrayList validaReporte = (ArrayList) session.getAttribute("detalleReporte");
            		if(validaReporte!=null){
            			if (log.isDebugEnabled()) log.debug("entro a reporte!=null");
            			repetir = false;
            			if (log.isDebugEnabled()) log.debug("repetir2= " + repetir);
            		}            		
            	}
            	if (log.isDebugEnabled()) log.debug("repetir aca: "+repetir);
            	if (!repetir) {
                	// Aqui ingresa tipoR = reporte y repetir=falso
            		if (tipoR.equals("reporte")) {
            			if (log.isDebugEnabled()) log.debug("params: "+params);
            			ArrayList detalleReporte = rd.consultarSolicitudesLE2(params);
            			if (log.isDebugEnabled()) log.debug("detalleReporte: "+detalleReporte);
                        session.removeAttribute("detalleReporte");
                        session.setAttribute("detalleReporte", detalleReporte);
            		}
            		if (tipoR.equals("excel")) {
            			if (log.isDebugEnabled()) log.debug("entro a excel");
                		ArrayList reporte = rd.resumenSolLabExcepcional2(params); 
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte);
                        if (log.isDebugEnabled()) log.debug("reporte= " + reporte);
                	}
                }
            }
            if (tipoR.equals("reporte")) {
            	if (log.isDebugEnabled()) log.debug("INGRESO IF DATO tipoR.equals : "+tipoR.equals("reporte"));
            	if (log.isDebugEnabled()) log.debug("1: "+regimen);
            	session.setAttribute("regimen", regimen);           	
            	if (log.isDebugEnabled()) log.debug("2: "+criterio);
            	session.setAttribute("criterio", criterio);
            	session.setAttribute("chk_1", chk_1);
            	session.setAttribute("chk_2", chk_2);
            	session.setAttribute("chk_3", chk_3);
            	if (log.isDebugEnabled()) log.debug("chk_1"+ chk_1);
            	if (log.isDebugEnabled()) log.debug("chk_2"+ chk_2);
            	if (log.isDebugEnabled()) log.debug("chk_3"+ chk_3);
            	
            	if (log.isDebugEnabled()) log.debug("3: "+valor);
            	session.setAttribute("valor", valor);
                if (log.isDebugEnabled()) log.debug("4: "+fechaIni);
                session.setAttribute("fechaIni", fechaIni);
                if (log.isDebugEnabled()) log.debug("5: "+fechaFin);
                session.setAttribute("fechaFin", fechaFin);
          
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/consultaSolLabExcepAnalista.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                
                request.setAttribute("usuario", usuario);
                request.setAttribute("codigo", codigo);
                request.setAttribute("nombCompleto", nombCompleto);
                //request.setAttribute("labor", labor);
                
                if(regimen.trim().equals("0")){
                	regimen = "D.L.276-728";
				}
				if(regimen.trim().equals("1")){
					regimen = "D.L.1057";
				}
				if(regimen.trim().equals("2")){
					regimen = "Modalidades formativas";
				}
                request.setAttribute("regimen", regimen);
                
                if(criterio.trim().equals("0")){
                	criterio = "Registro";
                	valor = valor + " - " + nombConsultado.trim();
				}
				if(criterio.trim().equals("1")){
					criterio = "Und.Organizacional";
				}
				if(criterio.trim().equals("5")){
					criterio = "Intendencia";
				}
				if(criterio.trim().equals("4")){
					criterio = "Institucional";
				}
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                
                
                request.setAttribute("indicador", indicador); // Identifica el Tipo de Solicitud seleccionado.
 
                
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelConsultaSolLabExcepcional2.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        " de SOLICITUDES DE LABOR EXCEPCIONAL PARA ANALISTA DE ASISTENCIA");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
        	if (log.isDebugEnabled()) log.debug("INGRESO CATCH 1 : "+e);
        	session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
        	if (log.isDebugEnabled()) log.debug("INGRESO CATCH 2 : "+e);
        	BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }//FIN - NVS - 02/05/2012 - AOM LABOR EXCEPCIONAL
    
    
    /** NVS - 19/03/2012 - AOM LABOR EXCEPCIONAL
	 * Metodo que busca data detalle para para solicitud labor excepcional COLABORADOR
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarDetalleSolicitudLE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		ArrayList listaDetalleConsultaLE = null;
		try {
			ReporteDelegate rd = new ReporteDelegate();            
            String anio = (String) request.getParameter("annual"); //ejem: 2012
            String numero = (String) request.getParameter("nummeral"); //ejem: 320 (Otros)
            String licencia = (String) request.getParameter("liccencia"); //123=Labor Excepcional; 124=Compensacion
            String fecha_desc = (String) request.getParameter("feccha");
            
            if (log.isDebugEnabled()) log.debug("anio: "+anio);
            if (log.isDebugEnabled()) log.debug("numero: "+numero);
            if (log.isDebugEnabled()) log.debug("licencia: "+licencia);
            if (log.isDebugEnabled()) log.debug("fecha_desc: "+fecha_desc);
			if (!anio.equals("")) { //ha seleccionado una unidad(Intendencia)
				ArrayList detalleReporte = (ArrayList) session.getAttribute("detalleReporte");				
            if (log.isDebugEnabled()) log.debug("Ingreso a consultarSolicitudLE");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			if (log.isDebugEnabled()) log.debug("codigo: "+codigo);			
			String usuario = bUsuario.getLogin();
			if (log.isDebugEnabled()) log.debug("usuario: "+usuario);						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("roles1: "+roles);			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			if (log.isDebugEnabled()) log.debug("usrRoles: "+usrRoles);
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("roles2: "+roles);			
			log.debug("roles.get(Constantes.ROL_ANALISTA_OPERATIVO) :" + roles.get(Constantes.ROL_ANALISTA_OPERATIVO)+"**");	
			log.debug("roles.get(Constantes.ROL_ANALISTA_CENTRAL) :" + roles.get(Constantes.ROL_ANALISTA_CENTRAL)+"**");
			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();

			HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("codUO", codUO);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
            Map params = new HashMap();
            //HashMap params = new HashMap();        	
        	params.put("seguridad", seguridad);
        	params.put("anio", anio);
        	params.put("numero", numero);
        	params.put("licencia", licencia);
        	params.put("fecha_desc", fecha_desc);
        	if (log.isDebugEnabled()) log.debug(">>>detalleReporte:" + detalleReporte);
        	if (log.isDebugEnabled()) log.debug("seguridad:" + seguridad);
            
        	       	
        	listaDetalleConsultaLE = rd.consultarDetalleSolicitudesLE(params);  
        	if (log.isDebugEnabled()) log.debug("listaDetalleConsultaLE: "+listaDetalleConsultaLE);
            session.removeAttribute("listaDetalleConsultaLE");
            setAttribute(session,"listaDetalleConsultaLE", listaDetalleConsultaLE);
			} //fin ha seleccionado una categoria
			

			String labor = "";
            if (licencia.trim().equals("124")) {            	
            	labor = "Labor Excepcional";
            } 
            if (licencia.trim().equals("125")) {            	
            	labor = "Compensacion";
            }
			request.setAttribute("labor", labor);
			request.setAttribute("anio", anio);
			request.setAttribute("numero", numero);
			
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/DetalleConsultaLE.jsp");
			dispatcher.forward(request, response);
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }  
	}
    /* FIN NVILLAR - 19/03/2012 - AOM LABOR EXCEPCIONAL */
	
	/** NVS - 02/05/2012 - AOM LABOR EXCEPCIONAL
	 * Metodo que busca data detalle para para solicitud labor excepcional ANALISTA CENTRAL
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarDetalleSolicitudLE2(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		ArrayList listaDetalleConsultaLE = null;
		try {
			ReporteDelegate rd = new ReporteDelegate();
			String anio = (String) request.getParameter("xanio"); //ejem: 2012
            String numero = (String) request.getParameter("xnumero"); //ejem: 320 (Otros)
            String registro = (String) request.getParameter("xregistro"); //Ejm: 1548
            String licencia = (String) request.getParameter("xlicencia"); //123=Labor Excepcional; 124=Compensacion
            String fecha = (String) request.getParameter("xfecha");
            
            if (log.isDebugEnabled()) log.debug("anio: "+anio);
            if (log.isDebugEnabled()) log.debug("numero: "+numero);
            if (log.isDebugEnabled()) log.debug("registro: "+registro);
            if (log.isDebugEnabled()) log.debug("licencia: "+licencia);
            if (log.isDebugEnabled()) log.debug("fecha: "+fecha);
            
			if (!anio.equals("")) { //ha seleccionado un link para mostrar su detalles
				ArrayList detalleReporte = (ArrayList) session.getAttribute("detalleReporte");
				
				
            if (log.isDebugEnabled()) log.debug("Ingreso a consultarSolicitudLE");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			if (log.isDebugEnabled()) log.debug("codigo: "+codigo);
			
			String usuario = bUsuario.getLogin();
			if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("roles1: "+roles);
			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			if (log.isDebugEnabled()) log.debug("usrRoles: "+usrRoles);
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("roles2: "+roles);
			
			log.debug("roles.get(Constantes.ROL_ANALISTA_OPERATIVO) :" + roles.get(Constantes.ROL_ANALISTA_OPERATIVO)+"**");	
			log.debug("roles.get(Constantes.ROL_ANALISTA_CENTRAL) :" + roles.get(Constantes.ROL_ANALISTA_CENTRAL)+"**");

			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();
			//int flag =0;
			
			HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("codUO", codUO);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
            Map params = new HashMap();
            //HashMap params = new HashMap();        	
        	params.put("seguridad", seguridad);
        	params.put("anio", anio);
        	params.put("numero", numero);
        	params.put("registro", registro);
        	params.put("licencia", licencia);
        	params.put("fecha", fecha);
        	if (log.isDebugEnabled()) log.debug(">>>detalleReporte:" + detalleReporte);
        	if (log.isDebugEnabled()) log.debug("seguridad:" + seguridad);
            
        	       	
        	listaDetalleConsultaLE = rd.consultarDetalleSolicitudesLE2(params);  
        	if (log.isDebugEnabled()) log.debug("listaDetalleConsultaLE: "+listaDetalleConsultaLE);
            session.removeAttribute("listaDetalleConsultaLE");
            setAttribute(session,"listaDetalleConsultaLE", listaDetalleConsultaLE);
			} //fin ha seleccionado una categoria	
			
			String labor = "";
            if (licencia.trim().equals("124")) {            	
            	labor = "Labor Excepcional";
            } 
            if (licencia.trim().equals("125")) {            	
            	labor = "Compensacion";
            }
			request.setAttribute("labor", labor);
			request.setAttribute("anio", anio);
			request.setAttribute("numero", numero);
			
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/DetalleConsultaLE.jsp");
			dispatcher.forward(request, response);
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
        
               
	}
    /* FIN NVILLAR - 02/05/2012 - AOM LABOR EXCEPCIONAL */
	
	
    /** NVS - 21/03/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para llamar al jsp que mostrara la opcion de Reporte de Permanencia Excepcional
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarReportePermanencia(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {
    	if (log.isDebugEnabled()) log.debug("Ingreso a consultaPermanenciaExcepcional");
        try {
			session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima("jdbc/dgsp"));		//jquispecoi PAS20155E230000073
        	
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			log.debug("roles:"+roles);
			if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)  {				
				//Analista
				String chk_1 = "1";
	            String chk_2 = "1";
	            String chk_3 = "1";
	            session.setAttribute("chk_1", chk_1);
	            session.setAttribute("chk_2", chk_2);
	            session.setAttribute("chk_3", chk_3);
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/consultaPermanenciaExcepAnalista.jsp");
	            dispatcher.forward(request, response);				
			}
			else if (roles.get(Constantes.ROL_JEFE)!=null) {			
				//Colaborador
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/consultaPermanenciaExcepcional.jsp");
				dispatcher.forward(request, response);
			}
        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }  
    }//FIN - NVS - 21/03/2012 - AOM LABOR EXCEPCIONAL
    
    /** NVS - 22/03/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para buscar data de la consulta de Reporte de Permanencia Excepcional JEFE
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void consultarPermanenciaLE(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_OPERATIVO"))!=null) uoSeg = uoSeg.substring(0,2).concat("%");			
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles );//session.getAttribute("perfil_usuario").toString());
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg); 
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            String regimen = request.getParameter("cmbRegimen");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String criterio = request.getParameter("cmbCriterio");
            //String valor = request.getParameter("txtValor");
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: Registro o UUOO   
            //valor = valor.toUpperCase();

            String codInten = request.getParameter("cod_intendencia");
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");
            log.debug("repetir: "+repetir);
            
            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            log.debug("tipoR: "+tipoR);

            HashMap params = new HashMap();
            params.put("dbpool", pool_sp);
            params.put("regimen", regimen);
            params.put("fechaIni", fechaIni);
            params.put("fechaFin", fechaFin);
            params.put("criterio", criterio);
            params.put("valor", valor);
            params.put("seguridad", seguridad);
            params.put("codPers", codigo);
            if (tipoR.equals("imprimir")) {
            	rd.masivoConsultaPermanencia(pool_sp, params, usuario);
            } else {
            	if (log.isDebugEnabled()) log.debug("repetir??? = " + repetir);
            	if (log.isDebugEnabled()) log.debug("tipoR??? = " + tipoR);
            	if (tipoR.equals("excel")) {
            		if (log.isDebugEnabled()) log.debug("tipoEXCEL= " + tipoR);
            		ArrayList validaReporte = (ArrayList) session.getAttribute("detalleReporte");
            		if(validaReporte!=null){
            			log.debug("entro a reporte!=null");
            			repetir = false;
            			log.debug("repetir2= " + repetir);
            		}            		
            	}
            	log.debug("repetir aca: "+repetir);
            	if (!repetir) {
            		if (tipoR.equals("reporte")) {
            			ArrayList detalleReporte = rd.permanencias(params);
                        log.debug("detalleReporte: "+detalleReporte);
                        session.removeAttribute("detalleReporte");
                        session.setAttribute("detalleReporte", detalleReporte);
            		}
            		if (tipoR.equals("excel")) {
                		log.debug("entro a excel");
                		ArrayList reporte = rd.resumenPermanenciaExcepcional(params);
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte);
                        log.debug("reporte= " + reporte);
                	}
                }
            }

            if (tipoR.equals("reporte")) { //IGUAL
            	log.debug("INGRESO IF DATO tipoR.equals : "+tipoR.equals("reporte"));
            	session.setAttribute("regimen", regimen);
            	session.setAttribute("criterio", criterio);
            	session.setAttribute("valor", valor);
            	session.setAttribute("fechaIni", fechaIni);
            	session.setAttribute("fechaFin", fechaFin);
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/consultaPermanenciaExcepcional.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                
                request.setAttribute("usuario", usuario);
                request.setAttribute("codigo", codigo);
                
                if(regimen.trim().equals("0")){
                	regimen = "D.L.276-728";
				}
				if(regimen.trim().equals("1")){
					regimen = "D.L.1057";
				}
				if(regimen.trim().equals("2")){
					regimen = "Modalidades formativas";
				}
                request.setAttribute("regimen", regimen);
                
                if(criterio.trim().equals("0")){
                	criterio = "Registro";
				}
				if(criterio.trim().equals("1")){
					criterio = "Und.Organizacional";
				}
				
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelConsultaPermanenciaLE.jsp");
                dispatcher.forward(request, response);
                return;
            }
            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        " de PERMANENCIA EXCEPCIONAL");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
        	log.debug("INGRESO CATCH 1 : "+e);
        	session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
        	log.debug("INGRESO CATCH 2 : "+e);
        	BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
    }//FIN - NVS - 22/03/2012 - AOM LABOR EXCEPCIONAL
    
    
    /** NVS - 03/05/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para buscar data de la consulta de Reporte de Permanencia Excepcional ANALISTA CENTRAL
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void consultarPermanenciaLE2(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_OPERATIVO"))!=null) uoSeg = uoSeg.substring(0,2).concat("%");			
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles );//session.getAttribute("perfil_usuario").toString());
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
          
            String regimen = request.getParameter("cmbRegimen");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String criterio = request.getParameter("cmbCriterio"); //0=registro; 1=unidad organica; 4=intendencia; 3=institucional
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: Registro o UUOO
            
            String chk_1 = request.getParameter("chk_1");
            String chk_2 = request.getParameter("chk_2");
            String chk_3 = request.getParameter("chk_3");

            String cod_intendencia= null;
            String nombreIntendencia = null;
            
            T12DAO t12DAOrh = new T12DAO(pool_sp); //trabaja con la t12uorga (rrhh.dao)

			if (criterio.trim().equals("5")) { // Intendencia
				
				cod_intendencia = request.getParameter("cod_intendencia");
	            log.debug("cod_intendencia= " + cod_intendencia);
				valor = cod_intendencia!=null && !cod_intendencia.equals("")?cod_intendencia:"";
				log.debug("valor= " + valor);
				if(valor!=null && !valor.equals("")){//solo if adicionado el 22/02/2012
                    List lIntendencia = t12DAOrh.findUObyCodUo(valor);
                    Map mIntendencia = (HashMap)lIntendencia.get(0);
                    nombreIntendencia = mIntendencia.get("t12des_corta").toString();
               }else{                  
                    log.debug("valor3= " + valor);
                    throw new ReporteException("Si desea volver a buscar por el criterio: Intendencia, por favor seleccionar otro criterio diferente a intendencia y finalmente volver a seleccionar el criterio intendencia.");
               }
				
			}
			
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");
            log.debug("repetir: "+repetir);
            
            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: "+tipoR);
            if (log.isDebugEnabled()) log.debug("tipoRaaaa1: "+chk_1);
            if (log.isDebugEnabled()) log.debug("tipoRaaaaa2: "+chk_2);
            if (log.isDebugEnabled()) log.debug("tipoRaaaaa3: "+chk_3);
            
            String solicitud1 = chk_1!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
            String solicitud2 = chk_2!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
            String solicitud3 = chk_3!= null ? Constantes.ACTIVO : Constantes.INACTIVO;
            
            if (log.isDebugEnabled()) log.debug("checkbox LE: " + solicitud1);
            if (log.isDebugEnabled()) log.debug("checkbox AUTO: " + solicitud2);
            if (log.isDebugEnabled()) log.debug("checkbox COMPE: " + solicitud3);
            
            String indicador1 = ""; 
            String indicador2 = ""; 
            String indicador3 = ""; 
            if (solicitud1.trim().equals("1")) {            	
            	indicador1 = "Autorizada";
            } else if (solicitud1.trim().equals("0")) {            	
            	indicador1 = "No";
            } 
            if (solicitud2.trim().equals("1")) {            	
            	indicador2 = "No Autorizada";
            } else if (solicitud2.trim().equals("0")) {          	
            	indicador2 = "No";
            } 
            if (solicitud3.trim().equals("1")) {            	
            	indicador3 = "Compensada";
            } else if (solicitud3.trim().equals("0")) {            	
            	indicador3 = "No";
            }

            HashMap params = new HashMap();
            params.put("dbpool", pool_sp);
            params.put("regimen", regimen);
            params.put("fechaIni", fechaIni);
            params.put("fechaFin", fechaFin);
            params.put("criterio", criterio);
            params.put("valor", valor);
            if (criterio.trim().equals("5")) { // Intendencia
            	params.put("nombre_inte", nombreIntendencia);
            }
            params.put("seguridad", seguridad);
            params.put("codPers", codigo);
            params.put("solicitud1", solicitud1);
            params.put("solicitud2", solicitud2);
            params.put("solicitud3", solicitud3);
            
            if (tipoR.equals("imprimir")) {
            	
                rd.masivoConsultaPermanencia2(pool_sp, params, usuario);

            } else {
                
            	if (log.isDebugEnabled()) log.debug("repetir??? = " + repetir);
            	if (log.isDebugEnabled()) log.debug("tipoR??? = " + tipoR);
            	if (tipoR.equals("excel")) {
            		if (log.isDebugEnabled()) log.debug("tipoEXCEL= " + tipoR);
            		ArrayList validaReporte = (ArrayList) session.getAttribute("detalleReporte");
            		if(validaReporte!=null){
            			log.debug("entro a reporte!=null");
            			repetir = false;
            			log.debug("repetir2= " + repetir);
            		}            		
            	}
            	log.debug("repetir aca: "+repetir);
            	if (!repetir) {
            		if (tipoR.equals("reporte")) {
            			ArrayList detalleReporte = rd.permanencias2(params);
            			ArrayList detalleReporte2 = new ArrayList();
            			
            			String suma="";
            			String canttt="";
            			
            			if (detalleReporte.size()>0){
            				detalleReporte2.addAll(detalleReporte.subList(0, detalleReporte.size()-2));

                			Map suma_minutos = (HashMap) detalleReporte.get(detalleReporte.size()-2);
                			suma = suma_minutos.get("suma_minutos").toString();
                			if (log.isDebugEnabled()) log.debug("suma = " + suma);
                			
                			Map cant_registro = (HashMap) detalleReporte.get(detalleReporte.size()-1);
                			canttt = cant_registro.get("cant_registro").toString();
                			if (log.isDebugEnabled()) log.debug("canttt = " + canttt);

            			}
            			session.setAttribute("suma_minutos",suma );
            			session.setAttribute("cant_registro",canttt );

                        log.debug("detalleReporte: "+detalleReporte);
                        session.removeAttribute("detalleReporte");
                        session.setAttribute("detalleReporte", detalleReporte2);
            		}
            		if (tipoR.equals("excel")) {
                		log.debug("entro a excel");
                		ArrayList reporte = rd.resumenPermanenciaExcepcional2(params);
                		ArrayList reporte2 = new ArrayList();
                		
                		String suma="";
            			String canttt="";
            			
            			if (reporte.size()>0){
            				reporte2.addAll(reporte.subList(0, reporte.size()-2));
                			
                			Map suma_minutos = (HashMap) reporte.get(reporte.size()-2);
                			suma = suma_minutos.get("suma_minutos").toString();
                			
                			Map cant_registro = (HashMap) reporte.get(reporte.size()-1);
               			    canttt = cant_registro.get("cant_registro").toString();
               			    
            			}
            			session.setAttribute("suma_minutos",suma );
            			session.setAttribute("cant_registro",canttt );
            			
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte2);
                        log.debug("reporte= " + reporte);
                	}
                }
            }
            if (tipoR.equals("reporte")) { //IGUAL
            	log.debug("INGRESO IF DATO tipoR.equals : "+tipoR.equals("reporte"));
            	session.setAttribute("regimen", regimen);
            	session.setAttribute("criterio", criterio);
            	session.setAttribute("valor", valor);
            	session.setAttribute("fechaIni", fechaIni);
                log.debug("5: "+fechaFin);
                session.setAttribute("fechaFin", fechaFin);
                session.setAttribute("chk_1", chk_1);
                session.setAttribute("chk_2", chk_2);
                session.setAttribute("chk_3", chk_3);
            	if (log.isDebugEnabled()) log.debug("chk_1"+ chk_1);
            	if (log.isDebugEnabled()) log.debug("chk_2"+ chk_2);
            	if (log.isDebugEnabled()) log.debug("chk_3"+ chk_3);
                

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/consultaPermanenciaExcepAnalista.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                
                request.setAttribute("usuario", usuario);
                request.setAttribute("codigo", codigo);
                
                if(regimen.trim().equals("0")){
                	regimen = "D.L.276-728";
				}
				if(regimen.trim().equals("1")){
					regimen = "D.L.1057";
				}
				if(regimen.trim().equals("2")){
					regimen = "Modalidades formativas";
				}
                request.setAttribute("regimen", regimen);
                
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
					criterio = "Institucional";
				}
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                
                request.setAttribute("indicador1", indicador1);
                request.setAttribute("indicador2", indicador2);
                request.setAttribute("indicador3", indicador3);
                
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelConsultaPermanenciaLE2.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        " de PERMANENCIA EXCEPCIONAL PARA ANALISTAS DE ASISTENCIA ");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }
        } catch (ReporteException e) {
        	log.debug("INGRESO CATCH 1 : "+e);
        	session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
        	log.debug("INGRESO CATCH 2 : "+e);
        	BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
    }//FIN - NVS - 03/05/2012 - AOM LABOR EXCEPCIONAL
    
    /** NVS - 17/04/2012 - AOM LABOR EXCEPCIONAL
	 * Metodo que busca data detalle para consulta de Reporte de Permanencia Excepcional JEFE
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param session HttpSession
	 * @throws ServletException
	 * @throws IOException
	 */
	private void buscarDetallePermanenciaLE(HttpServletRequest request,
			HttpServletResponse response, HttpSession session)
			throws ServletException, IOException {
		
		ArrayList listaPermanenciaLE = null;
		try {
			ReporteDelegate rd = new ReporteDelegate();          
            String registro = (String) request.getParameter("xregistro"); //ejem: 2012
            String fecha = (String) request.getParameter("xfecha"); //ejem: 320 (Otros)
            String autorizada = (String) request.getParameter("xautorizada"); //123=Labor Excepcional; 124=Compensacion

            if (log.isDebugEnabled()) log.debug("registro: "+registro);
            if (log.isDebugEnabled()) log.debug("fecha: "+fecha);
            if (log.isDebugEnabled()) log.debug("autorizada: "+autorizada);
            
			if (!fecha.equals("")) { //ha seleccionado una unidad(Intendencia)
				ArrayList detalleReporte = (ArrayList) session.getAttribute("detalleReporte");
				
            if (log.isDebugEnabled()) log.debug("Ingreso a consultarSolicitudLE");
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			if (log.isDebugEnabled()) log.debug("codigo: "+codigo);
			
			String usuario = bUsuario.getLogin();
			if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("roles1: "+roles);
			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			if (log.isDebugEnabled()) log.debug("usrRoles: "+usrRoles);
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("roles2: "+roles);
			
			log.debug("roles.get(Constantes.ROL_ANALISTA_OPERATIVO) :" + roles.get(Constantes.ROL_ANALISTA_OPERATIVO)+"**");	
			log.debug("roles.get(Constantes.ROL_ANALISTA_CENTRAL) :" + roles.get(Constantes.ROL_ANALISTA_CENTRAL)+"**");

			String codUO = bUsuario.getCodUO();
			String uoSeg = bUsuario.getVisibilidad();

			HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("codUO", codUO);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
            Map params = new HashMap();    	
        	params.put("seguridad", seguridad);
        	params.put("registro", registro);
        	params.put("fecha", fecha);
        	params.put("autorizada", autorizada);
        	if (log.isDebugEnabled()) log.debug(">>>detalleReporte:" + detalleReporte);
        	if (log.isDebugEnabled()) log.debug("seguridad:" + seguridad);
 	
        	listaPermanenciaLE = rd.buscaDetallePermanenciasLE(params);  
        	if (log.isDebugEnabled()) log.debug("listaPermanenciaLE: "+listaPermanenciaLE);
            session.removeAttribute("listaPermanenciaLE");
            setAttribute(session,"listaPermanenciaLE", listaPermanenciaLE);
			} //fin ha seleccionado una categoria	
			
			request.setAttribute("fecha", fecha);
            request.setAttribute("registro", registro);
			
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/DetallePermanenciaLE.jsp");
			dispatcher.forward(request, response);
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
	}
    /* FIN NVILLAR - 17/04/2012 - AOM LABOR EXCEPCIONAL */

    /** NVS - 25/04/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para llamar al jsp que mostrara la opcion de Reporte de Horas por Estado ANALISTA CENTRAL
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarHorasAutNoAutComp(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {
    	if (log.isDebugEnabled()) log.debug("Ingreso a consultaPermanenciaExcepcional");
        try {
			session.setAttribute("fechaMinima",Utiles.obtenerFechaMinima("jdbc/dgsp"));		//jquispecoi PAS20155E230000073
            
			RequestDispatcher dispatcher = getServletContext()
            .getRequestDispatcher("/consultaHorasLaborExcepcional.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }  
    }//FIN - NVS - 16/04/2012 - AOM LABOR EXCEPCIONAL
    
    /** NVS - 16/04/2012 - AOM LABOR EXCEPCIONAL
     * Metodo para buscar data de la consulta de Reporte de Permanencia Excepcional ANALISTA CENTRAL
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void ConsultarHorasAutNoAutComp(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			String codigo = bUsuario.getNroRegistro();//.getNumreg();
			String usuario = bUsuario.getLogin();
			String uoSeg = bUsuario.getVisibilidad();
            String uuoo = bUsuario.getCodUO();		
			if (roles.get(getPropiedades().leePropiedad("ROL_ANALISTA_OPERATIVO"))!=null) uoSeg = uoSeg.substring(0,2).concat("%");			
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles );//session.getAttribute("perfil_usuario").toString());
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uuoo", uuoo);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            String regimen = request.getParameter("cmbRegimen");
            String medida = request.getParameter("cmbMedida");
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String criterio = request.getParameter("cmbCriterio");
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor"):""; //cuando criterios son: Registro o UUOO
            
            String cod_intendencia= null;
            String nombreIntendencia = null;
            
            T12DAO t12DAOrh = new T12DAO(pool_sp); //trabaja con la t12uorga (rrhh.dao)

			if (criterio.trim().equals("5")) { // Intendencia
				
				cod_intendencia = request.getParameter("cod_intendencia");
	            log.debug("cod_intendencia= " + cod_intendencia);
				valor = cod_intendencia!=null && !cod_intendencia.equals("")?cod_intendencia:"";
				log.debug("valor= " + valor);
				if(valor!=null && !valor.equals("")){//solo if adicionado el 22/02/2012
                    List lIntendencia = t12DAOrh.findUObyCodUo(valor);
                    Map mIntendencia = (HashMap)lIntendencia.get(0);
                    nombreIntendencia = mIntendencia.get("t12des_corta").toString();
               }else{                  
                    log.debug("valor3= " + valor);
                    throw new ReporteException("Si desea volver a buscar por el criterio: Intendencia, por favor seleccionar otro criterio diferente a intendencia y finalmente volver a seleccionar el criterio intendencia.");
               }
				
			}

            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");
            log.debug("repetir: "+repetir);
            
            ReporteDelegate rd = new ReporteDelegate();

            String tipoR = request.getParameter("tipo") != null ? request
                    .getParameter("tipo") : "reporte";
            log.debug("tipoR: "+tipoR);

            HashMap params = new HashMap();
            params.put("dbpool", pool_sp);
            params.put("regimen", regimen);
            params.put("medida", medida);
            params.put("fechaIni", fechaIni);
            params.put("fechaFin", fechaFin);
            params.put("criterio", criterio);
            params.put("valor", valor);
            if (criterio.trim().equals("5")) { // Intendencia
            	params.put("nombre_inte", nombreIntendencia);
            }
            params.put("seguridad", seguridad);
            params.put("codPers", codigo);

            if (tipoR.equals("imprimir")) {
                rd.masivoConsultaHorasAutNoAutComp(pool_sp, params, usuario); 
            } else {
            	
            	if (log.isDebugEnabled()) log.debug("repetir??? = " + repetir);
            	if (log.isDebugEnabled()) log.debug("tipoR??? = " + tipoR);
            	if (tipoR.equals("excel")) {
            		if (log.isDebugEnabled()) log.debug("tipoEXCEL= " + tipoR);
            		ArrayList validaReporte = (ArrayList) session.getAttribute("detalleReporte");
            		if(validaReporte!=null){
            			log.debug("entro a reporte!=null");
            			repetir = false;
            			log.debug("repetir2= " + repetir);
            		}            		
            	}
                if (!repetir) {
                	
                	
                	
                	if (tipoR.equals("reporte")) {
                		ArrayList detalleReporte = rd.consultaHorasAutNoAutComp(params);
                        log.debug("detalleReporte: "+detalleReporte);
                        ArrayList detalleReporte2 = new ArrayList();
                        log.debug("detalleReporte2: "+detalleReporte2);
                        
                        String suma1="0";
                    	String suma2="0";
                    	String suma3="0";
                    	String suma4="0";
                        
                        if(detalleReporte!=null && detalleReporte.size()>0){
                        	detalleReporte2.addAll(detalleReporte.subList(0, detalleReporte.size()-1));
                        	Map sumas = (HashMap) detalleReporte.get(detalleReporte.size()-1);
                        	suma1 = sumas.get("suma1").toString();
                        	suma2 = sumas.get("suma2").toString();
                        	suma3 = sumas.get("suma3").toString();
                        	suma4 = sumas.get("suma4").toString();
                        }
                        
                        if (log.isDebugEnabled()) log.debug("suma1 = " + suma1);
           			    if (log.isDebugEnabled()) log.debug("suma2 = " + suma2);
           			    if (log.isDebugEnabled()) log.debug("suma3 = " + suma3);
           			    if (log.isDebugEnabled()) log.debug("suma4 = " + suma4);
                        
                        session.removeAttribute("detalleReporte");
                        session.setAttribute("detalleReporte", detalleReporte2);
                        
                        session.removeAttribute("suma1");
                        session.setAttribute("suma1", suma1);
                        
                        session.removeAttribute("suma2");
                        session.setAttribute("suma2", suma2);
                        
                        session.removeAttribute("suma3");
                        session.setAttribute("suma3", suma3);
                        
                        session.removeAttribute("suma4");
                        session.setAttribute("suma4", suma4);
            		}
            		if (tipoR.equals("excel")) {
                		log.debug("entro a excel");
                		ArrayList reporte = rd.consultaHorasAutNoAutComp(params);
                		session.removeAttribute("reporte");
                        session.setAttribute("reporte", reporte);
                        log.debug("reporte= " + reporte);
                	}
                }
            }
            if (tipoR.equals("reporte")) { 

            	if (log.isDebugEnabled()) log.debug("Ingreso a reporte : "+tipoR.equals("reporte"));
            	log.debug("1: "+regimen);
            	session.setAttribute("regimen", regimen);
            	session.setAttribute("medida", medida);           	
            	if (log.isDebugEnabled()) log.debug("2: "+criterio);
            	session.setAttribute("criterio", criterio);
            	if (log.isDebugEnabled()) log.debug("3: "+valor);
            	session.setAttribute("valor", valor);
                if (log.isDebugEnabled()) log.debug("4: "+fechaIni);
                session.setAttribute("fechaIni", fechaIni);
                if (log.isDebugEnabled()) log.debug("5: "+fechaFin);
                session.setAttribute("fechaFin", fechaFin);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/consultaHorasLaborExcepcional.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {
            	if (log.isDebugEnabled()) log.debug("******** PARA ENTRAN AL JSP EXCEL ******" + repetir);
                request.setAttribute("fechaIniExcel", fechaIni);
                request.setAttribute("fechaFinExcel", fechaFin);
                
                request.setAttribute("usuario", usuario);
                request.setAttribute("regimen", regimen);
                
                if(medida.trim().equals("01")){
                	medida = "Minutos";
				}
				if(medida.trim().equals("02")){
					medida = "Horas";
				}
				if(medida.trim().equals("03")){
					medida = "Dias";
				}
                request.setAttribute("medida", medida);
                
                if(regimen.trim().equals("0")){
                	regimen = "D.L.276-728";
				}
				if(regimen.trim().equals("1")){
					regimen = "D.L.1057";
				}
				if(regimen.trim().equals("2")){
					regimen = "Modalidades formativas";
				}
                request.setAttribute("regimen", regimen);
                
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
					criterio = "Institucional";
				}
                
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/excelHorasAutNoAutComp.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("imprimir")) {
                request.setAttribute("nomReporte",
                        " de HORAS DE PERMANENCIA AUTORIZADAS, NO AUTORIZADAS Y COMPENSADAS");
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            }
        } catch (ReporteException e) {
        	if (log.isDebugEnabled()) log.debug("INGRESO CATCH 1 : "+e);
        	session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
        	if (log.isDebugEnabled()) log.debug("INGRESO CATCH 2 : "+e);
        	BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. " + e.getMessage());
            bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }//FIN - NVS - 25/04/2012 - AOM LABOR EXCEPCIONAL
    
    
    
    //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
    /**
     * Metodo encargado de cargar la pagina del reporte de vacaciones gozadas o efectuadas por licencias de matrimonio (tipo 23) de colaboradores regimen DL 1057 (CAS)
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarVacacionesGozadasMatrimonio(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegado(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

	            session.removeAttribute("detalleReporte");

	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteVacacionesMatrimonio.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opcion.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    
    
    /**
     * Metodo encargado de generar el reporte de vacaciones gozadas
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarVacacionesGozadasMatrimonio(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
		
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
			String regimen = "09";  //para DL 1057 (cas)
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");  
            log.debug("valor: "+valor);
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
                
            String codInten = request.getParameter("cod_intendencia");
        	if (criterio.trim().equals("5")) {
        		valor = codInten;
        	}   
        	log.debug("valor final: "+valor);
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool", pool_sp);
                mapa.put("regimen", regimen); //para DL 1057 (cas)
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);               
                mapa.put("fechaIni", fechaIni);
                mapa.put("fechaFin", fechaFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoVacacionesGozadasMatrimonio(mapa, usuario);//FALTA

                request.setAttribute("nomReporte",
                        "REPORTE DE VACACIONES EFECTUADAS POR LICENCIAS DE MATRIMONIO CAS");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {

            if (!repetir) {
                ArrayList detalleReporte = rd.vacacionesGozadasMatrimonio(pool_sp, regimen, 
                        fechaIni, fechaFin, criterio, valor, seguridad);

                session.removeAttribute("detalleReporte");
                session.setAttribute("detalleReporte", detalleReporte);
            }


            if (tipoR.equals("reporte")) {
            	request.setAttribute("regimen", regimen);//para DL 1057 (cas)
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);
                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);               

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteVacacionesMatrimonio.jsp");
                dispatcher.forward(request, response);
                return;
            }

            if (tipoR.equals("excel")) {

                request.setAttribute("fini", fechaIni);
                request.setAttribute("ffin", fechaFin);               

                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/excelVacacionesMatrimonio.jsp");
                dispatcher.forward(request, response);
                return;
            }
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    //FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
    
    //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
    /**
     * Metodo encargado de cargar la pagina del reporte de clima laboral de colaboradores regimen DL 1057 (CAS) y DL 276-728
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarAutorizacionesClimaLaboral(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	log.debug("ingreso a cargarAutorizacionesClimaLaboral");
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codPers = bUsuario.getNroRegistro();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles"); //super.getRoles(session);
			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			            
			String codUO = bUsuario.getCodUO();
			
			String uoSeg = bUsuario.getVisibilidad();
			if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
			log.debug("roles: "+roles);
			log.debug("uoSeg: "+uoSeg);

			HashMap seguridad = new HashMap();
			seguridad.put("roles", roles);
			seguridad.put("codPers", codPers);
			seguridad.put("uoSeg", uoSeg);
			seguridad.put("codUO", codUO);
			seguridad.put("codOpcion", Constantes.DELEGA_PAPELETAS);
			seguridad.put("dbpool", pool_sp);
			seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			AsistenciaDelegate ad = new AsistenciaDelegate();
			
			if ( (ad.esJefeEncargadoDelegadoSolicitudes(seguridad)) ||  (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null)   )  {

	            session.removeAttribute("detalleReporte");

	            request.setAttribute("tipoQuiebre", "0");
	            RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteClimaLaboral.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opción.");
			}



        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    
    
    /**
     * Metodo encargado de generar el reporte de clima laboral
     * 
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void generarAutorizacionesClimaLaboral(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {

        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
		
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
			
            
            String usuario = bUsuario.getLogin();
            
            String uoSeg = bUsuario.getVisibilidad();
            if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
            
			String regimen = "No10";  //para DL 1057 (cas) y DL 276-728 (planilla)
            String criterio = request.getParameter("cmbCriterio");
            String valor = request.getParameter("txtValor");  
            log.debug("valor: "+valor);
            String fechaIni = request.getParameter("fechaIni");
            String fechaFin = request.getParameter("fechaFin");
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
                
            String codInten = request.getParameter("cod_intendencia");
        	if (criterio.trim().equals("5")) {
        		valor = codInten;
        	}   
        	log.debug("valor final: "+valor);
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();

            if (tipoR.equals("imprimir")) {
                HashMap mapa = new HashMap();

                mapa.put("dbpool", pool_sp);
                mapa.put("regimen", regimen); 
                mapa.put("criterio", criterio);
                mapa.put("valor", valor);               
                mapa.put("fechaIni", fechaIni);
                mapa.put("fechaFin", fechaFin);
                mapa.put("seguridad", seguridad);
                mapa.put("codPers", codigo);

                rd.masivoAutorizacionesClimaLaboral(mapa, usuario);//FALTA DESARROLLAR

                request.setAttribute("nomReporte",
                        "REPORTE DE AUTORIZACIONES DE CLIMA LABORAL");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;

            } else {

	            if (!repetir) {
	                ArrayList detalleReporte = rd.autorizacionesClimaLaboral(pool_sp, regimen, 
	                        fechaIni, fechaFin, criterio, valor, seguridad);
	
	                session.removeAttribute("detalleReporte");
	                session.setAttribute("detalleReporte", detalleReporte);
	            }
	
	            if (tipoR.equals("reporte")) {
	            	request.setAttribute("regimen", regimen);//para DL 1057 (cas)
	            	request.setAttribute("criterio", criterio);
	                request.setAttribute("valor", valor);
	                request.setAttribute("fini", fechaIni);
	                request.setAttribute("ffin", fechaFin);               
	
	                RequestDispatcher dispatcher = getServletContext()
	                        .getRequestDispatcher("/reporteClimaLaboral.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
	
	            if (tipoR.equals("excel")) {
	
	                request.setAttribute("fini", fechaIni);
	                request.setAttribute("ffin", fechaFin);               
	
	                RequestDispatcher dispatcher = getServletContext()
	                        .getRequestDispatcher("/excelClimaLaboral.jsp");
	                dispatcher.forward(request, response);
	                return;
	            }
            }
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }

    }
    //FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
    
    //ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
     /** Metodo encargado de cargar la pagina del reporte de notificaciones a directivos
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarNotificaDirectivosNoVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
						
			if ( (roles.get(Constantes.ROL_JEFE)!=null) 
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) )  {      
				
	            session.removeAttribute("detalleReporte");
				RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteNotifDirectivoNoProgramaron.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opción.");
			}

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    
    /**
     * Metodo encargado de generar el reporte de Trabajadores notificados para Goce Vacacional  
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void generarNotificaDirectivosNoVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        try {

        	if (log.isDebugEnabled()) log.debug("Ingreso a generarNotificaDirectivosNoVacaciones");
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			if (log.isDebugEnabled()) log.debug("codigo: "+codigo);
			
			String usuario = bUsuario.getLogin();
			if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("roles1: "+roles);
			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			if (log.isDebugEnabled()) log.debug("usrRoles: "+usrRoles);
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("roles2: "+roles);
			            
            String uoSeg = bUsuario.getVisibilidad();          
            //if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null) uoSeg = uoSeg.substring(0,2).concat("%");
            //if (log.isDebugEnabled()) log.debug("uoSeg Final: "+uoSeg);

            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO()); 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());
            if (log.isDebugEnabled()) log.debug("seguridad: "+seguridad);
            
            
            String criterio = request.getParameter("cmbCriterio");
            if (log.isDebugEnabled()) log.debug("criterio: " + criterio);            
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor").trim().toUpperCase():""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();
            
            String codInten = request.getParameter("cod_intendencia").trim();
            if (log.isDebugEnabled()) log.debug("codInten: "+codInten);
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);
           
            
            String fechaNotific = request.getParameter("fechaNotific");
            String fechaNotificFin = request.getParameter("fechaNotificFin");                    
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: "+tipoR);
           
            if (tipoR.equals("imprimir")) {

            	HashMap params = new HashMap();               
                params.put("criterio", criterio);               
                params.put("valor", valor);               
                params.put("fechaNotific", fechaNotific);
                params.put("fechaNotificFin", fechaNotificFin);             
                params.put("codPers", codigo);//necesario para registrar reporte en administrador reportes
                params.put("seguridad", seguridad);    
                
                rd.masivoNotificaDirectivosNoVacaciones(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                	                                    
                	ArrayList detalleReporte = rd.notificaDirectivosNoVacaciones(pool_sp, fechaNotific, fechaNotificFin, criterio, valor,seguridad);
                                     
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                    if (log.isDebugEnabled()) log.debug("detalleReporte: "+detalleReporte);
                }
            } 
            
            if (tipoR.equals("reporte")) {            	
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);               
                request.setAttribute("fechaNotific", fechaNotific);
                request.setAttribute("fechaNotificFin", fechaNotificFin);               
           
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteNotifDirectivoNoProgramaron.jsp");
                dispatcher.forward(request, response);
                return;
            }
            
            if (tipoR.equals("excel")) {
                request.setAttribute("fechaIniExcel", fechaNotific);
                request.setAttribute("fechaFinExcel", fechaNotificFin);                
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);                                        
                forward(request,response,"/excelNotifDirectivoNoProgramaron.jsp?idSession="+System.currentTimeMillis());
                return;
            }
           
            if (tipoR.equals("imprimir")) {
            	
                request.setAttribute("nomReporte","REPORTE DE NOTIFICACIONES A DIRECTIVOS QUE NO PROGRAMARON VACACIONES");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }          
            
        } catch (ReporteException e) {        	
        	forwardError(request,response,e.getMessage());        	
        } catch (Exception e) {        	
        	forwardError(request,response,e.getMessage());
        }
    } 
    
    /**
     * Metodo para buscar Detalle de Notificaciones de solicitudes pendientes a directivos
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void detalleNotificaDirectivoNoVacaciones(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {    	
    
    	ArrayList detalle = null;
    
        try {
        	
        	ArrayList detalleReporte = (ArrayList) session.getAttribute("detalleReporte");
        	String indice = request.getParameter("indice");//indice (i) de fila seleccionada
        	Map quiebre = (HashMap) detalleReporte.get(Integer.parseInt(indice)); //lista de colaboradores con sus datos
        	if (log.isDebugEnabled()) log.debug("quiebre: " + quiebre);
        	detalle = (ArrayList) quiebre.get("detalle");
        	if (log.isDebugEnabled()) log.debug("detalle: " + detalle);
        	        	   	
            
        } catch (ReporteException e) {
            session.setAttribute("beanErr", e.getBeanMensaje());
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            BeanMensaje bean = new BeanMensaje();
            bean.setError(true);
            bean.setMensajeerror("Ha ocurrido un error en la aplicacion. "
                    + e.getMessage());
            bean
                    .setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
            session.setAttribute("beanErr", bean);
            RequestDispatcher dispatcher = getServletContext()
                    .getRequestDispatcher("/PagE.jsp");
            dispatcher.forward(request, response);
        }
        session.removeAttribute("listaDetalleNotifica");
        setAttribute(session,"listaDetalleNotifica", detalle);
        forward(request, response, "/DetalleNotifDirectivoNoPrograma.jsp");       
    }
    
    /** Metodo encargado de cargar la pagina del reporte de vacaciones truncas
     * @param request
     * @param response
     * @param session
     * @throws ServletException
     * @throws IOException
     */
    private void cargarVacacionesTruncas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        try {
        	
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
						
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			ArrayList usrRoles = (ArrayList) roles.get("*");
			roles = obtRoles (usrRoles);
						
			if ( (roles.get(Constantes.ROL_JEFE)!=null) 
					|| (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) )  {      
				
	            session.removeAttribute("detalleReporte");
				RequestDispatcher dispatcher = getServletContext()
	                    .getRequestDispatcher("/reporteVacacionesTruncas.jsp");
	            dispatcher.forward(request, response);
	            return;
			}
			else {
			throw new Exception("Usted no se encuentra habilitado para ejecutar esta opción.");
			}

        } catch (Exception e) {
			BeanMensaje bean = new BeanMensaje();
			bean.setError(true);
			bean.setMensajeerror(e.getMessage());
			bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
			session.setAttribute("beanErr", bean);
			RequestDispatcher dispatcher = getServletContext()
					.getRequestDispatcher("/PagE.jsp");
			dispatcher.forward(request, response);
        }

    }
    
    /**
     * Metodo encargado de generar el reporte de Vacaciones Truncas  
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param session HttpSession
     * @throws ServletException
     * @throws IOException
     */
    private void generarVacacionesTruncas(HttpServletRequest request,
            HttpServletResponse response, HttpSession session) throws ServletException, IOException {

        try {
        	if (log.isDebugEnabled()) log.debug("Ingreso a generarVacacionesTruncas");
        	UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String codigo = bUsuario.getNroRegistro();
			if (log.isDebugEnabled()) log.debug("codigo: "+codigo);
			
			String usuario = bUsuario.getLogin();
			if (log.isDebugEnabled()) log.debug("usuario: "+usuario);
			
			HashMap roles = (HashMap) bUsuario.getMap().get("roles");
			if (log.isDebugEnabled()) log.debug("roles1: "+roles);
			
			ArrayList usrRoles = (ArrayList) roles.get("*");
			if (log.isDebugEnabled()) log.debug("usrRoles: "+usrRoles);
			roles = obtRoles (usrRoles);
			if (log.isDebugEnabled()) log.debug("roles2: "+roles);
			            
            String uoSeg = bUsuario.getVisibilidad();          
         
            HashMap seguridad = new HashMap();
            seguridad.put("roles", roles);
            seguridad.put("codPers", codigo);
            seguridad.put("uoSeg", uoSeg);
            seguridad.put("uoAO", bUsuario.getCodUO()); 
			log.debug("bUsuario.getCodUO(): "+bUsuario.getCodUO());
            if (log.isDebugEnabled()) log.debug("seguridad: "+seguridad);
            
            String regimen = request.getParameter("cmbRegimen");//01=276-728, 09=cas
            String estado = request.getParameter("cmbEstado");//1=activo, 0=inactivo
            String fechaCorte = request.getParameter("fechaCorte");
            String criterio = request.getParameter("cmbCriterio");//0=registro, 1=uuoo, 5=intendencia, 4= institucional
            if (log.isDebugEnabled()) log.debug("criterio: " + criterio);            
            String valor =  (request.getParameter("txtValor")!= null )?request.getParameter("txtValor").trim().toUpperCase():""; //cuando criterios son: registro o UO   
            valor = valor.toUpperCase();
            
            String codInten = request.getParameter("cod_intendencia").trim();
			if (criterio.trim().equals("5")) {
				valor = codInten;
			}
			if (log.isDebugEnabled()) log.debug("valor: " + valor);          
            
            boolean repetir = request.getParameter("repetir").toString().trim()
                    .equals("true");

            ReporteDelegate rd = new ReporteDelegate();
            String tipoR = request.getParameter("tipo") != null ? request.getParameter("tipo") : "reporte";
            if (log.isDebugEnabled()) log.debug("tipoR: "+tipoR);
            
            HashMap params = new HashMap(); 
            params.put("regimen", regimen);
            params.put("criterio", criterio);               
            params.put("valor", valor);  
            params.put("fechaCorte", fechaCorte);
            params.put("estado", estado);                         
            params.put("codPers", codigo);//necesario para registrar reporte en administrador reportes
            params.put("seguridad", seguridad); 
           
            if (tipoR.equals("imprimir")) {           	   
                
                rd.masivoVacacionesTruncas(pool_sp, params, usuario);

            } else {
                if (!repetir) {
                	params.put("dbpool", pool_sp);
                	                                    
                	ArrayList detalleReporte = rd.vacacionesTruncas(pool_sp,params);
                                     
                    session.removeAttribute("detalleReporte");
                    session.setAttribute("detalleReporte", detalleReporte);
                    if (log.isDebugEnabled()) log.debug("detalleReporte: "+detalleReporte);
                }
            }            
            if (tipoR.equals("reporte")) {            	
                request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);               
                request.setAttribute("regimen", regimen);
                request.setAttribute("estado", estado);
                request.setAttribute("fechaCorte", fechaCorte);
           
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/reporteVacacionesTruncas.jsp");
                dispatcher.forward(request, response);
                return;
            }            
            if (tipoR.equals("excel")) {
            	request.setAttribute("criterio", criterio);
                request.setAttribute("valor", valor);               
                request.setAttribute("regimen", regimen);
                request.setAttribute("estado", estado);
                request.setAttribute("fechaCorte", fechaCorte);                                    
                forward(request,response,"/excelVacacionesTruncas.jsp?idSession="+System.currentTimeMillis());
                return;
            }           
            if (tipoR.equals("imprimir")) {            	
                request.setAttribute("nomReporte","REPORTE DE VACACIONES TRUNCAS");
                RequestDispatcher dispatcher = getServletContext()
                        .getRequestDispatcher("/resultadoReporte.jsp");
                dispatcher.forward(request, response);
                return;
            }          
            
        } catch (ReporteException e) {        	
        	forwardError(request,response,e.getMessage());        	
        } catch (Exception e) {        	
        	forwardError(request,response,e.getMessage());
        }
    } 
    //FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones

	
}