package pe.gob.sunat.utils.jms;

import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * <p>Title: QueueMessageSender</p>
 * <p>Description: Envia mensajes a una cola (queue) JMS</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: SUNAT</p>
 * @author Boris L�pez Araoz
 * @version 1.0
 *
 * Para enviar un mensaje:
 * <code>
 * QueueMessageSender sender = new QueueMessageSender("QueueName");
 * sender.send("Hola");
 * sender.close();
 * </code>
 */

public class QueueMessageSender {
	private static Context context = null;

	private boolean transacted = false;

	private int acknowledgementMode = Session.AUTO_ACKNOWLEDGE;

	private QueueConnectionFactory queueConnectionFactory = null;

	private QueueConnection queueConnection = null;

	private QueueSession queueSession = null;

	private QueueSender queueSender = null;

	private Queue queue = null;

	private String queueConnectionFactoryName = null;

	private String queueName = null;

	private static String defaultQueueConnectionFactoryName = null;

	public QueueMessageSender(String queueName) {
		setQueueName(queueName);
	}

	public boolean isTransacted() {
		return transacted;
	}

	public void setTransacted(boolean transacted) {
		this.transacted = transacted;
	}

	public int getAcknowledgementMode() {
		return acknowledgementMode;
	}

	public void setAcknowledgementMode(int acknowledgementMode) {
		this.acknowledgementMode = acknowledgementMode;
	}

	public String getQueueConnectionFactoryName() {
		if (queueConnectionFactoryName == null) {
			if (defaultQueueConnectionFactoryName == null) {
				Properties properties = new Properties();
				Class myclass = getClass();
				try {
					properties.load(myclass.getResourceAsStream(
						"jms.properties"));
				}
				catch (NullPointerException ex) {
				}
				catch (IOException ex) {
				}
				defaultQueueConnectionFactoryName =
					properties.getProperty("defaultConnectionFactoryName",
					"ConnectionFactory");
			}
			queueConnectionFactoryName = defaultQueueConnectionFactoryName;
		}
		return queueConnectionFactoryName;
	}

	public void setQueueConnectionFactoryName(String queueConnectionFactoryName) {
		this.queueConnectionFactoryName = queueConnectionFactoryName;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public Context getContext() throws MessageException {
		try {
			if (context == null) {
				context = new InitialContext();
			}
		}
		catch (NamingException ex) {
			throw new MessageException("Error al obtener naming.Context", ex);
		}
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private QueueConnectionFactory getQueueConnectionFactory()
	throws MessageException {
		if (queueConnectionFactory == null) {
			Object obj = null;
			try {
				obj = getContext().lookup(getQueueConnectionFactoryName());
				queueConnectionFactory = (QueueConnectionFactory) obj;
			}
			catch (NamingException ex) {
				throw new MessageException("Error al hacer lookup " +
				"al QueueFactory:'" + queueConnectionFactoryName + "'", ex);
			}
			catch (ClassCastException ex) {
				throw new MessageException("El nombre JNDI:' " +
				queueConnectionFactoryName + "' no est� asociado a un " +
				"objeto QueueConnectionFactory", ex);
			}
		}
		return queueConnectionFactory;
	}

	private QueueConnection getQueueConnection() throws MessageException {
		if (queueConnection == null) {
			try {
				queueConnection =
					getQueueConnectionFactory().createQueueConnection();
				queueConnection.start();
			}
			catch (JMSException ex) {
				throw new MessageException("Error al crear " +
				"QueueConnection:'" + queueName + "'", ex);
			}
		}
		return queueConnection;
	}

	private QueueSession getQueueSession() throws MessageException {
		if (queueSession == null) {
			try {
				queueSession = getQueueConnection().createQueueSession(
					transacted, acknowledgementMode);
			}
			catch (JMSException ex) {
				throw new MessageException("Error al crear QueueSession:'" +
				queueName + "'");
			}
		}
		return queueSession;
	}

	private Queue getQueue() {
		if (queue == null) {
			try {
				Object obj = getContext().lookup(queueName);
				queue = (Queue) obj;
			}
			catch (Exception ex) {
				throw new MessageException("Error al hacer lookup a Queue:'" +
				queueName + "'", ex);
			}
		}
		return queue;
	}

	private QueueSender getQueueSender() throws MessageException {
		if (queueSender == null) {
			try {
				queueSender = getQueueSession().createSender(getQueue());
			}
			catch (JMSException ex) {
				throw new MessageException("Error al crear QueueSender:'" +
				queueName + "'", ex);
			}
		}
		return queueSender;
	}

	public void send(Serializable message) throws MessageException {
		if (message == null) {
			throw new MessageException("Error mensaje nulo. Queue:'" +
			queueName + "'");
		}
		try {
			if (message instanceof String) {
				TextMessage textMessage = getQueueSession().createTextMessage();
				textMessage.clearBody();
				textMessage.setText((String)message);
				getQueueSender().send(textMessage);
			}
			else {
				ObjectMessage objectMessage =
					getQueueSession().createObjectMessage();
				objectMessage.clearBody();
				objectMessage.setObject(message);
				getQueueSender().send(objectMessage);
			}
		}
		catch (JMSException ex) {
			throw new MessageException("Error al enviar mensaje:'" +
			message + "'" + "al Queue:'" + queueName + "'");
		}

		if (isTransacted()) {
			try {
				getQueueSession().commit();
			}
			catch (Exception ex) {
				throw new MessageException("Error al hacer commit en Queue:'" +
				queueName + "'");
			}
		}
	}

	public void close() throws MessageException {
		if (queueSender != null) {
			try {
				queueSender.close();
			}
			catch (JMSException ex) {
				throw new MessageException("Error al cerrar QueueSender: '" +
				queueName + "'");
			}
		}
		if (queueSession != null) {
			try {
				queueSession.close();
			}
			catch (JMSException ex1) {
				throw new MessageException("Error al cerrar QueueSession: '" +
				queueName + "'");
			}
		}
		if (queueConnection != null) {
			try {
				queueConnection.close();
			}
			catch (JMSException ex2) {
				throw new MessageException("Error al cerrar QueueConnection: '" +
				queueName + "'");
			}
		}
	}
}
