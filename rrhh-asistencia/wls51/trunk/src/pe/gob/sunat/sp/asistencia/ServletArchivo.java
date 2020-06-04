//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.pattern.DynaBean;
import pe.gob.sunat.framework.core.servlet.ServletAbstract;
import pe.gob.sunat.framework.util.io.Archivo;
import pe.gob.sunat.sp.asistencia.ejb.delegate.ArchivoDelegate;
import pe.gob.sunat.tecnologia.menu.bean.UsuarioBean;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * @web.servlet name="ServletArchivo"
 * @web.servlet-mapping url-pattern = "/asisS15Alias"
 * @web.servlet-init-param name = "dir_temp" value = "/data0/sip/dat/tmp"  
 * @web.servlet-init-param name = "pool_sp" value = "jdbc/dcsp"
 * @web.servlet-init-param name = "pool_sp_g" value = "jdbc/dgsp"
 * Clase: ArchivoServlet 
 * Proyecto :  
 * Descripcion : 
 * Autor: 
 */
public class ServletArchivo extends ServletAbstract {

	private static String dir_temp = "";
	private static String pool_sp;
	private static String pool_sp_g;
	private static String MAX_SIZE_FILE_STRING = "512kb";
	private static int MAX_SIZE_FILE_NUMBER = 512000; //maximo posible 512000 en servidor
	private static int MAX_FILE_NUMBER =100; 
	private static String EXTENSIONES ="docx,pdf,msg,zip";
	public ServletArchivo() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dir_temp = config.getInitParameter("dir_temp");
		pool_sp = config.getInitParameter("pool_sp");
		pool_sp_g = config.getInitParameter("pool_sp_g");
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	public void procesa(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {

		setAttribute(session, "closeWindows", new Integer(0));
		String accion = "";
		try {
			DynaBean dynaBean = null;
			try {
				dynaBean = new DynaBean(request);
				log.debug("*** dynaBean  ***" + dynaBean);
			} catch (Exception e) {
				MensajeBean mBean = new MensajeBean();
				mBean.setError(true);
				mBean.setMensajeerror("El archivo debe ser mayor  0 kb y menor igual " + MAX_SIZE_FILE_STRING);
				setAttribute(request, "beanErr", mBean);
				forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
				return;
			}

			if (dynaBean.isMultipart()) {
				log.debug("*** isMultipart  ***");
				if (!dynaBean.isMultipartOK()) {
					MensajeBean mBean = new MensajeBean();
					mBean.setError(true);
					mBean.setMensajeerror("El archivo debe ser mayor  0 kb y menor igual " + MAX_SIZE_FILE_STRING);
					setAttribute(request, "beanErr", mBean);
					forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
					return;
				}
				request.removeAttribute("beanErr");
			} else {

			}
			accion = (String) dynaBean.get("accion");

			log.debug("*** Accion  ***" + accion);
			if (accion == null) {
				accion = "cargarArchivoVarios";
			}

			if (session == null) {
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/PagSession.jsp");
				dispatcher.forward(request, response);
				return;
			}

			//modificar
			if ("cargarArchivo".equals(accion)) {
				cargarArchivo(request, response, session, dynaBean);
			} else if ("adjuntarArchivo".equals(accion)) {
				adjuntarArchivo(request, response, session, dynaBean);
			} else if ("eliminarArchivo".equals(accion)) {
				eliminarArchivo(request, response, session, dynaBean);
			} else if ("previstaArchivo".equals(accion)) {
				previstaArchivo(request, response, session, dynaBean);
			} else if ("cancelarCarga".equals(accion)) {
				cancelarCarga(request, response, session, dynaBean);
			} else if ("aceptarCarga".equals(accion)) {
				aceptarCarga(request, response, session, dynaBean);

			} else if ("verArchivos".equals(accion)) {
				verArchivos(request, response, session, dynaBean);
			} else if ("descargarArchivo".equals(accion)) {
				descargarArchivo(request, response, session, dynaBean);
			}

		} catch (Exception e) {
			log.error("*** Error ***", e);
			MensajeBean mBean = new MensajeBean();
			mBean.setError(true);
			mBean.setMensajeerror("Por favor intente nuevamente ejecutar la opcion. " + e.getMessage());
			setAttribute(request, "beanErr", mBean);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;
		}
	}

	/**
	 * Cargar archivos para adjuntar nuevos
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void cargarArchivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {

		try {
			List lista = new ArrayList();
			Integer numArchivo = new Integer(dbean.getInt("num_archivo")); //el num_archivo es unico por la secuencia
			Integer numSeqDoc = new Integer(dbean.getInt("num_seqdoc"));
			if (log.isDebugEnabled())
				log.debug("cargarArchivoVarios -- Numero de archivo a trabajar => " + numArchivo + " - " + numSeqDoc);

			ArchivoDelegate ad = new ArchivoDelegate();
			Map mapa = new HashMap();

			//en caso el usuario cancele (x) y abra nuevamente se revierte
			//Anular archivos marcados para agregar  EST_ARC_TEMP_PARA_AGREGAR->EST_ARC_TEMP_ANULADO
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("ind_del", Constantes.EST_ARC_TEMP_PARA_AGREGAR);
			int res = ad.cambiarEstado(pool_sp_g, mapa, Constantes.EST_ARC_TEMP_ANULADO);

			//recupera los archivos marcados para borrrar  EST_ARC_TEMP_PARA_BORRAR->EST_ARC_TEMP_OK
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("ind_del", Constantes.EST_ARC_TEMP_PARA_BORRAR);
			res = ad.cambiarEstado(pool_sp_g, mapa, Constantes.EST_ARC_TEMP_OK);

			lista = obtenerListaArchivos(numArchivo, numSeqDoc);

			setAttribute(session, "titulo", "Adjuntar Archivos");
			setAttribute(session, "listaAdjuntos", lista);
			setAttribute(session, "numArchivo", numArchivo);
			setAttribute(session, "numSeqDoc", numSeqDoc);
			setAttribute(session, "maxSize", MAX_SIZE_FILE_STRING);
			
			try {
				String extensiones=  dbean.getString("extensiones");			
				if(extensiones!=null && !extensiones.equals("")){
					setAttribute(session, "extensiones", extensiones);				
				}else{
					setAttribute(session, "extensiones", EXTENSIONES);
						
				}	
			} catch (Exception e) {
				 	setAttribute(session, "extensiones", EXTENSIONES);
			}
			
			try {
				int max=  dbean.getInt("maxFileNumber");	
			    if(max>0){
			       setAttribute(session, "maxFileNumber", new Integer( max));  
			    }else{
			       setAttribute(session, "maxFileNumber", new Integer( MAX_FILE_NUMBER));
			    }
			} catch (Exception e) {
				setAttribute(session, "maxFileNumber", new Integer( MAX_FILE_NUMBER));		
			}

			

			
			setAttribute(session, "indModoEsc", Constantes.ARCHIVO_MODO_ESCRITURA);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
		} catch (Exception e) {
			log.error("*** Error ***", e);
			forwardError(request, response, "Por favor intente nuevamente ejecutar la opcion.");
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param dbean
	 * @throws ServletException
	 * @throws IOException
	 */
	private void adjuntarArchivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {
		try {

			List lista = null;
			Integer numArchivo = new Integer(dbean.getInt("num_archivo"));
			Integer numSeqDoc = new Integer(dbean.getInt("num_seqdoc"));

			if (log.isDebugEnabled())
				log.debug("adjuntarArchivo --Numero de archivo a trabajar => " + numArchivo + " - " + numSeqDoc);
			UsuarioBean bUsuario = (UsuarioBean) session.getAttribute("usuarioBean");
			String menu_usr = bUsuario.getTicket();
			String ruta_arch = dir_temp + menu_usr;
			String nomCorto = dbean.getString("archivo-filter-filename");
			String descripcion = dbean.getString("descripcion");

			//Validar tamanio
			String size_arch = dbean.getString("archivo-size");
			int tamanio = Integer.parseInt(size_arch);
			InputStream adjunto = null;
			if (tamanio > 0) {
				if (tamanio <= MAX_SIZE_FILE_NUMBER) {
					adjunto = new FileInputStream((File) dbean.get("archivo"));
				} else {
					MensajeBean beanM = new MensajeBean();
					beanM.setError(true);
					beanM.setMensajeerror("El archivo debe ser mayor  0 kb y menor igual " + MAX_SIZE_FILE_STRING);
					setAttribute(request, "beanErr", beanM);
					forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
					return;
				}

			} else {
				MensajeBean beanM = new MensajeBean();
				beanM.setError(true);
				beanM.setMensajeerror("Debe cargar archivo");
				setAttribute(request, "beanErr", beanM);
				forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
				return;
			}
			//Validar si la descripcion
			if (Utiles.esNuloesVacio(descripcion)) {
				MensajeBean beanM = new MensajeBean();
				beanM.setError(true);
				beanM.setMensajeerror("Debe escribir descripción");
				setAttribute(request, "beanErr", beanM);
				forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
				return;
			}

			//Validar si ya se cargo el archivo
			lista = obtenerListaArchivos(numArchivo, numSeqDoc);
			boolean existe = false;
			if (lista == null) {
				lista = new ArrayList();
			} else {
				HashMap aux = null;
				for (int i = 0; i < lista.size() && !existe; i++) {
					aux = (HashMap) lista.get(i);
					String nombre = (String) aux.get("nom_archivo");
					if (nombre != null && nombre.equals(nomCorto))
						existe = true;
				}
			}
			if (existe) {
				MensajeBean beanM = new MensajeBean();
				beanM.setError(true);
				beanM.setMensajeerror("Intente adjuntando otro archivo o elimine el existente y adjunte el nuevo");
				setAttribute(request, "beanErr", beanM);
				forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
				return;
			}

			 
			
			String extension = obtenerExtension(nomCorto).toLowerCase();
			//Verificar tipo de dato
			
			try {
				String extensiones = new String(dbean.getString("extensiones"));
				if(extensiones==null&& extension.equals("")){
					extensiones=EXTENSIONES;
				}
				StringTokenizer tokenizer = new StringTokenizer(extensiones,",");
				boolean extensionValida=false; 
				while (tokenizer.hasMoreElements()) {				
					String object = tokenizer.nextToken();
					if (extension.equals(object)) {
						extensionValida=true;
						break;
					}
				}
			 
				
				if(!extensionValida){
					MensajeBean beanM = new MensajeBean();
					beanM.setError(true);
					beanM.setMensajeerror("Seleccione un archivo de tipo  " +extensiones);
					setAttribute(request, "beanErr", beanM);
					forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
					return;
				}
			}
			
			catch (Exception e) {
				 
			} 
			
			//Verificar maximo 
			int max=MAX_SIZE_FILE_NUMBER;
			try {
 				  max =  dbean.getInt("maxFileNumber");	
 				  if(lista.size()>=max){
				MensajeBean beanM = new MensajeBean();
				beanM.setError(true);
 					beanM.setMensajeerror("Maximo de archivos permitido(" +max+")");
				setAttribute(request, "beanErr", beanM);
				forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
				return;
			}
			} catch (Exception e) {
				 
			} 

			//Cargar el archivo a ruta temporal
			ruta_arch = ruta_arch + nomCorto;
			FileOutputStream file = new FileOutputStream(ruta_arch);
			if (adjunto.available() > 0) {
				file.write(inputToBytes(adjunto));
			}
			file.close();

			HashMap mapa = new HashMap();
			mapa.put("des_archivo", descripcion);
			mapa.put("nom_archivo", nomCorto);
			mapa.put("num_archivo", numArchivo);
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("cod_tiparc", extension);
			mapa.put("rut_adjunto", ruta_arch);
			mapa.put("cod_usucrea", bUsuario.getLogin());
			if (log.isDebugEnabled())
				log.debug("Mapa antes de registro");
			if (log.isDebugEnabled())
				log.debug(mapa);

			ArchivoDelegate ad = new ArchivoDelegate();
			int id = ad.registrarArchivo(pool_sp_g, mapa);

			lista = obtenerListaArchivos(numArchivo, numSeqDoc);
			setAttribute(session, "listaAdjuntos", lista);
			setAttribute(session, "indModoEsc", Constantes.ARCHIVO_MODO_ESCRITURA);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());

		} catch (Exception e) {
			log.error("*** Error ***", e);
			MensajeBean mBean = new MensajeBean();
			mBean.setError(true);
			mBean.setMensajeerror("Error al intentar adjuntar el archivo.");
			setAttribute(request, "beanErr", mBean);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;
		}
	}

	/**
	 *  
	 * @param request
	 * @param response
	 * @param session
	 * @param dbean
	 * @throws ServletException
	 * @throws IOException
	 */
	private void eliminarArchivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {

		try {
			List lista = null;
			Integer numArchivo = new Integer(dbean.getInt("num_archivo"));
			Integer numSeqDoc = new Integer(dbean.getInt("num_seqdoc"));
			String numArcdet = null;
			log.debug(dbean.get("chk_opcion").getClass());

			if ((String.class).equals(dbean.get("chk_opcion").getClass())) {
				numArcdet = (String) dbean.get("chk_opcion");
			} else {
				numArcdet = ((String[]) dbean.get("chk_opcion"))[0];//obtener el primero
			}
			HashMap mapa = new HashMap();
			mapa.put("num_archivo", numArchivo);
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("num_arcdet", numArcdet);
			ArchivoDelegate ad = new ArchivoDelegate();
			int ok = ad.cambiarEstado(pool_sp_g, mapa, Constantes.EST_ARC_TEMP_PARA_BORRAR);

			lista = obtenerListaArchivos(numArchivo, numSeqDoc);
			setAttribute(session, "listaAdjuntos", lista);
			setAttribute(session, "indModoEsc", Constantes.ARCHIVO_MODO_ESCRITURA);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;

		} catch (Exception e) {
			log.error("*** Error ***", e);
			MensajeBean mBean = new MensajeBean();
			mBean.setError(true);
			mBean.setMensajeerror("Error al intentar eliminar el  archivo .");
			setAttribute(request, "beanErr", mBean);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;
		}
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param dbean
	 * @throws ServletException
	 * @throws IOException
	 */
	private void previstaArchivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {

		try {

			Integer numArchivo = new Integer(dbean.getInt("num_archivo"));
			Integer numArcdet = new Integer(dbean.getInt("num_arcdet"));

			Map mapa = new HashMap();
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_arcdet", numArcdet);

			ArchivoDelegate ad = new ArchivoDelegate();
			Map adjunto = null;
			adjunto = ad.descargarArchivo(pool_sp, mapa);
			if (adjunto != null) {
				try {
					new Archivo().descargar((InputStream) adjunto.get("arc_adjunto"), (String) adjunto.get("nom_archivo"), response);
				} catch (Exception e) {
					log.debug("***Error***", e);
					MensajeBean beanM = new MensajeBean();
					beanM.setError(true);
					beanM.setMensajeerror("Error al descargar el archivo adjunto.");
					beanM.setMensajesol("Por favor intente nuevamente.");
					forwardError(request, response, beanM);
					return;
				}
			}

		} catch (Exception e) {
			log.error("*** Error ***", e);
			MensajeBean mBean = new MensajeBean();
			mBean.setError(true);
			mBean.setMensajeerror("Error al intentar descargar el archivo. Por favor intente nuevamente ejecutar la opcion.");
			setAttribute(request, "beanErr", mBean);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;
		}
	}

	private void cancelarCarga(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {

		try {
			List lista = null;
			Integer numArchivo = new Integer(dbean.getInt("num_archivo"));
			Integer numSeqDoc = new Integer(dbean.getInt("num_seqdoc"));
			ArchivoDelegate ad = new ArchivoDelegate();
			Map mapa = new HashMap();

			//Anular archivos marcados para agregar  EST_ARC_TEMP_PARA_AGREGAR->EST_ARC_TEMP_ANULADO
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("ind_del", Constantes.EST_ARC_TEMP_PARA_AGREGAR);
			int res = ad.cambiarEstado(pool_sp_g, mapa, Constantes.EST_ARC_TEMP_ANULADO);

			//recupera los archivos marcados para borrrar  EST_ARC_TEMP_PARA_BORRAR->EST_ARC_TEMP_OK
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("ind_del", Constantes.EST_ARC_TEMP_PARA_BORRAR);
			res = ad.cambiarEstado(pool_sp_g, mapa, Constantes.EST_ARC_TEMP_OK);

			setAttribute(session, "closeWindows", new Integer(1));
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;
		} catch (Exception e) {
			log.error("*** Error ***", e);
			MensajeBean mBean = new MensajeBean();
			mBean.setError(true);
			mBean.setMensajeerror("Error al cerrar");
			setAttribute(request, "beanErr", mBean);

		}
	}

	private void aceptarCarga(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {

		try {
			List lista = null;
			Integer numArchivo = new Integer(dbean.getInt("num_archivo"));
			Integer numSeqDoc = new Integer(dbean.getInt("num_seqdoc"));
			ArchivoDelegate ad = new ArchivoDelegate();
			Map mapa = new HashMap();

			//EST_ARC_TEMP_PARA_AGREGAR->EST_ARC_TEMP_OK
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("ind_del", Constantes.EST_ARC_TEMP_PARA_AGREGAR);
			int res = ad.cambiarEstado(pool_sp_g, mapa, Constantes.EST_ARC_TEMP_OK);

			//EST_ARC_TEMP_PARA_BORRAR->EST_ARC_TEMP_ANULADO
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_seqdoc", numSeqDoc);
			mapa.put("ind_del", Constantes.EST_ARC_TEMP_PARA_BORRAR);
			res = ad.cambiarEstado(pool_sp_g, mapa, Constantes.EST_ARC_TEMP_ANULADO);

			setAttribute(session, "closeWindows", new Integer(1));
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;
		} catch (Exception e) {
			log.error("*** Error ***", e);
			MensajeBean mBean = new MensajeBean();
			mBean.setError(true);
			mBean.setMensajeerror("Error al cerrar");
			setAttribute(request, "beanErr", mBean);

		}
	}

	/**
	 * Cargar listado archivos para el modo lectura	  
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void verArchivos(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {

		try {
			List lista = new ArrayList();
			Integer numArchivo = new Integer(dbean.getInt("num_archivo"));
			Integer numSeqDoc = new Integer(dbean.getInt("num_seqdoc"));

			if (log.isDebugEnabled())
				log.debug("verArchivos -- Numero de archivo a trabajar => " + numArchivo + " - " + numSeqDoc);

			lista = obtenerListaArchivosFinales(numArchivo, numSeqDoc);
			setAttribute(session, "titulo", "Adjuntar Archivos");
			setAttribute(session, "listaAdjuntos", lista);
			setAttribute(session, "numArchivo", numArchivo);
			setAttribute(session, "numSeqDoc", numSeqDoc);
			setAttribute(session, "maxSize", MAX_SIZE_FILE_STRING);
			setAttribute(session, "maxFileNumber", new Integer(MAX_FILE_NUMBER));
			setAttribute(session, "indModoEsc", Constantes.ARCHIVO_MODO_LECTURA);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
		} catch (Exception e) {
			log.error("*** Error ***", e);
			forwardError(request, response, "Por favor intente nuevamente ejecutar la opcion.");
		}
	}

	/**
	 * Descarga de archivo permanente 
	 * @param request
	 * @param response
	 * @param session
	 * @throws ServletException
	 * @throws IOException
	 */
	private void descargarArchivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, DynaBean dbean) throws ServletException,
			IOException {

		try {

			Integer numArchivo = new Integer(dbean.getInt("num_archivo"));
			Integer numArcdet = new Integer(dbean.getInt("num_arcdet"));
			Map mapa = new HashMap();
			mapa.put("num_archivo", numArchivo); //obligatorio
			mapa.put("num_arcdet", numArcdet);

			ArchivoDelegate ad = new ArchivoDelegate();
			Map adjunto = null;
			adjunto = ad.descargarArchivoFinal(pool_sp, mapa);
			if (adjunto != null) {
				try {
					new Archivo().descargar((InputStream) adjunto.get("arc_adjunto"), (String) adjunto.get("nom_archivo"), response);
				} catch (Exception e) {
					log.debug("***Error***", e);
					MensajeBean beanM = new MensajeBean();
					beanM.setError(true);
					beanM.setMensajeerror("Error al descargar el archivo adjunto.");
					beanM.setMensajesol("Por favor intente nuevamente.");
					forwardError(request, response, beanM);
					return;
				}
			}

		} catch (Exception e) {
			log.error("*** Error ***", e);
			MensajeBean mBean = new MensajeBean();
			mBean.setError(true);
			mBean.setMensajeerror("Error al intentar descargar el archivo. Por favor intente nuevamente ejecutar la opcion.");
			setAttribute(request, "beanErr", mBean);
			forward(request, response, "/DatosAdjuntos.jsp?idSession=" + System.currentTimeMillis());
			return;
		}
	}

	/**
	 * Funcion auxiliar
	 * @param numArchivo
	 * @param numSeqDoc
	 * @return
	 */
	private List obtenerListaArchivos(Integer numArchivo, Integer numSeqDoc) {
		List lista;
		ArchivoDelegate ad = new ArchivoDelegate();
		Map mapa = new HashMap();
		mapa.put("num_archivo", numArchivo); //obligatorio			
		mapa.put("num_seqdoc", numSeqDoc);
		List indDels =  Arrays.asList( new String [] { Constantes.EST_ARC_TEMP_OK, Constantes.EST_ARC_TEMP_PARA_AGREGAR});
		mapa.put("ind_dels", indDels); 
		lista = ad.buscarArchivos(pool_sp, mapa);
		return lista;
	}

	/**
	 * Funcion auxiliar
	 * @param numArchivo
	 * @param numSeqDoc
	 * @return
	 */
	private List obtenerListaArchivosFinales(Integer numArchivo, Integer numSeqDoc) {
		List lista;
		ArchivoDelegate ad = new ArchivoDelegate();
		Map mapa = new HashMap();
		mapa.put("num_archivo", numArchivo); //obligatorio			
		mapa.put("num_seqdoc", numSeqDoc);
		lista = ad.buscarArchivosFinales(pool_sp, mapa);
		return lista;
	}

	public static byte[] inputToBytes(InputStream in) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
		byte[] buffer = new byte[in.available()];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
		return out.toByteArray();
	}

	private String obtenerExtension(String fileName) throws IOException {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}
}