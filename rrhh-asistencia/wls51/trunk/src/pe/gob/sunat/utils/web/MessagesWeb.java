package pe.gob.sunat.utils.web;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import pe.gob.sunat.sol.BeanMensaje;

/**
 * Permite gestionar los mensajes que se muestran en la web mediante el JSP "messages.jsp"
 * 
 * @author jmaravi
 * @since 28/04/2014
 */
public class MessagesWeb {	
	
	/**
	 * Tipo de mensaje ERROR. Permite alertar al usuario final sobre errores fatales que no permitieron el exito de la operación.
	 */
	public static final String TYPE_ERROR = "error";
	/**
	 * Tipo de mensaje WARNING (Peligro). Permite alertar al usuario sobre algun problema no FATAL, es decir culminación de un proceso con advertencias.
	 */
	public static final String TYPE_WARNING = "warning";
	/**
	 * Tipo de mensaje INFO (Informativo). Permite mostrar mensajes de orientación simple a los usuarios, no usarlo para indicar exito de procesamientos ni errores o alertas.
	 */
	public static final String TYPE_INFO = "info";
	/**
	 * Tipo de mensaje SUCCESS.(Exito en algún proceso o acción que involucre procesamiento de datos)
	 */
	public static final String TYPE_SUCCESS = "success";
	
	HttpSession session;
	
	private static final Logger log = Logger.getLogger(MessagesWeb.class);
	
	/**
	 * Constructor del gestor de mensajes WEB.
	 * @param session Obligatorio para el correcto funcionamiento de los mensajes.
	 * @author jmaravi 
	 */
	public MessagesWeb(HttpSession session){		
		this.session = session;		
	}
	
	/**
	 * 
	 * Permite adicionar mensajes que se mostrarán en el gestor de mensajes: messages.jsp
	 * 
	 * @param sTipoMessage Indica el tipo de mensaje que puede ser: success, info, warning, error. Puede usar las constantes TYPE_ERROR, TYPE_WARNING, TYPE_INFO y TYPE_SUCCESS en su lugar.
	 * @param sMensaje Es el contenido del mensaje que se quiere mostrar.
	 * @throws ServletException
	 * @author jmaravi
	 * @since 14/04/2014
	 */
	public boolean setMensajeWeb(String sTipoMessage, String sMensaje) throws ServletException {
		boolean bResult = false; 
		try {			
			ArrayList listaMensajes = (ArrayList)session.getAttribute("listaMensajes");
			if(listaMensajes == null)
				listaMensajes = new ArrayList();
			HashMap hmMensaje = new HashMap();
			hmMensaje.put("tipo", sTipoMessage);
			hmMensaje.put("mensaje", sMensaje);
			listaMensajes.add(hmMensaje);
			session.setAttribute("listaMensajes", listaMensajes);
			bResult = true;
			if(log.isDebugEnabled()) log.debug("MessagesWeb :::"+sTipoMessage+":::"+sMensaje); 
		}catch(Exception e){
			log.error("ERROR: No se pudo adicionar el mensaje web::: "+sTipoMessage+":::"+sMensaje);
			if(session != null){
				BeanMensaje bean = new BeanMensaje();
				bean.setError(true);
				bean.setMensajeerror("Ha ocurrido un error al gestionar el mensaje:::  "+sTipoMessage+":::"+sMensaje+":::"+ e.getMessage());
				bean.setMensajesol("Por favor intente nuevamente ejecutar la opcion.");
				session.setAttribute("beanErr", bean);
			}
			throw new ServletException("ERROR: No se pudo adicionar el mensaje web::: "+sTipoMessage+":::"+sMensaje);
		}
		return bResult;
	}

	
	//Getters and Setters
	
	/**
	 * @return the session
	 */
	public HttpSession getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(HttpSession session) {
		this.session = session;
	}
}