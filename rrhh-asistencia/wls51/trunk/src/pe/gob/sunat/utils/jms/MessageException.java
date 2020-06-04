package pe.gob.sunat.utils.jms;

/**
 * <p>Title: Excepcion general para mensajes JMS</p>
 * <p>Description: Es RunTimeException</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SUNAT</p>
 * @author Boris López Araoz
 * @version 1.0
 */
public class MessageException extends RuntimeException {
	public MessageException(String msg) {
		super(msg);
	}

	public MessageException(String msg, Exception ex) {
		this("JMS:" + msg + ":" +
			ex.getClass().getName() +  ":" + ex.getMessage());
	}

	public MessageException() {
	}
}
