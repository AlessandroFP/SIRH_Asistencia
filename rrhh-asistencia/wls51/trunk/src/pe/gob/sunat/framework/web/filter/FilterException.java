package pe.gob.sunat.framework.web.filter;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.bean.MensajeBean;

/**
 * 
 * @author JVALDEZ
 * @since web-1.2
 */
public class FilterException extends RuntimeException {

	private static final long serialVersionUID = 3018311540246001004L;

	private MensajeBean mensaje;

	public FilterException() {
		super();
	}

	public FilterException(Object origen, String mensaje) {
		super(mensaje);
		Log log = LogFactory.getLog(origen.getClass());
		log.error(origen.getClass().getName()+"|"+mensaje);
		log.debug("Error",this);
	}

	public FilterException(Object origen, Exception ex) {
		super(ex);
		Log log = LogFactory.getLog(origen.getClass());
		if (ex instanceof SQLException) {
			SQLException sqlex = (SQLException) ex;
			log.error(origen.getClass().getName()+"|"+"errorCode=" + sqlex.getErrorCode() + ",message="
					+ sqlex.getMessage() + ",localizedMessage="
					+ sqlex.getLocalizedMessage());
		} else {
			log.error(origen.getClass().getName()+"|"+ex.toString());
		}
		log.debug("Error",ex);
	}

	public FilterException(Object origen, MensajeBean mensaje) {
		super(mensaje.getMensajeerror());
		this.mensaje = mensaje;
		Log log = LogFactory.getLog(origen.getClass());
		log.error(origen.getClass().getName()+"|"+mensaje.getMensajeerror());
		log.debug("Error",this);
	}

	public MensajeBean getMensaje() {
		return this.mensaje;
	}	
}
