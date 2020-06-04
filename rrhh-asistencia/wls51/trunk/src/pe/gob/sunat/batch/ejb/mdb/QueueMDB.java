package pe.gob.sunat.batch.ejb.mdb;

import java.util.HashMap;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import pe.gob.sunat.batch.dao.QueueDAO;

/**
 * @ejb.bean name="QueueMDB" description="Maneja la cola de procesos batch"
 *           local-jndi-name="ejb/mdb/batch/QueueMDB"
 *           destination-type="javax.jms.Queue"
 *           subscription-durability="NonDurable"
 * 
 * @ejb.transaction-type type="Container"
 * 
 * @jboss.container-configuration name="Standard Message Driven Bean"
 * @jboss.destination-jndi-name name="queue/BatchQueue"
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"
 * @weblogic.pool initial-beans-in-free-pool="5" max-beans-in-free-pool="20"
 * 
 * @weblogic.message-driven connection-factory-jndi-name="RRHHConnectionFactory"
 *                          destination-jndi-name="queue/BatchQueue"
 * 
 * 
 * @jboss.version="3.2"
 * @author cgarratt
 *         <p>
 *         Copyright: Copyright (c) 2004
 *         </p>
 * @version 1.0
 */
public class QueueMDB implements MessageDrivenBean, MessageListener {

	private static final Logger log = Logger.getLogger(QueueMDB.class);

	private MessageDrivenContext context;

	public void ejbCreate() {
	}

	public void ejbRemove() {
	}

	public void setMessageDrivenContext(MessageDrivenContext context) {
		this.context = context;
	}

	/**
	 * Metodo encargado de consumir los mensajes colocados en al cola de
	 * procesomiento masivo
	 * 
	 * @param message
	 */
	public void onMessage(Message message) {

		try {
			log.info("Nuevo mensaje : "+ new java.sql.Timestamp(System.currentTimeMillis()));

			ObjectMessage obj = (ObjectMessage) message;
			HashMap mapa = (HashMap) obj.getObject();

			// instanciamos el DAO que administra los procesos batch
			QueueDAO dao = new QueueDAO();

			String type = (String) mapa.get("type");
			if (type.equals("proceso"))
				dao.ejecutaProceso(mapa);
			if (type.equals("reporte"))
				dao.generaReporte(mapa);
			if (type.equals("correo"))
				dao.sendMail(mapa);

		} catch (Exception e) {
			log.error("Error : " + e.toString());
		}
	}

}