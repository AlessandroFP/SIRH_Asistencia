package pe.gob.sunat.batch.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.pattern.ServiceLocator;
import pe.gob.sunat.personalizer.mail.bean.BeanCorreo;
import pe.gob.sunat.personalizer.mail.dao.DAOEnviaCorreo;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.dao.T1481DAO;
import pe.gob.sunat.sp.dao.CorreoDAO;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 *  
 * Clase       : QueueDAO 
 * Proyecto    : Asistencia 
 * Descripcion : Clase encargada de la ejecucion de procesos masivos
 * Autor       : CGARRATT
 * Fecha       : 10-mar-2005 12:01:23
 * 
 */
public class QueueDAO extends DAOAccesoBD {

	public PrintStream output = new PrintStream(System.out);
	protected final Log log = LogFactory.getLog(getClass());
	public int numLinea = 0;
	public int maxColumnas = 120;
	public int maxLineas = 40;
	public int numPagina = 1;
	

	public QueueDAO() {
	}

	/**
	 * 
	 * Metodo para encolar procesos. Se invoca desde la aplicacion cliente
	 * @param jndiEJB JNDI del EJB que ejecutara el proceso
	 * @param method Metodo del EJB
	 * @param params Parametros del proceso
	 * @param usuario Usuario que ejecuta el proceso
	 */
	public void encolaProceso(String jndiEJB, String method, HashMap params,String usuario) {

		try {

			//generamos el id del mensaje
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("DHHmmssSSS");
			params.put("messageID", sdf.format(now));
			this.encolaProcesoBatch(jndiEJB, method, params, usuario);

		} catch (Exception e) {
			log.debug("Error : " + e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo para encolar reportes. Se invoca desde la aplicacion cliente
	 * @param jndiEJB JNDI del EJB que ejecutara el reporte
	 * @param method Metodo del EJB
	 * @param params Parametros del reporte
	 * @param usuario Usuario que ejecuta el reporte
	 */
	public void encolaReporte(String jndiEJB, String method, HashMap params,
			String usuario) {

		try {

			//generamos el id del mensaje
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("DHHmmssSSS");
			params.put("messageID", sdf.format(now));
			
			this.encolaReporteBatch(jndiEJB, method, params, usuario);

		} catch (Exception e) {
			log.debug("Error : " + e.getMessage());
		}
	}

	/**
	 * Metodo encargado de colocar un proceso en la cola de procesamiento
	 * @param jndiEJB
	 * @param method
	 * @param params
	 * @param usuario
	 * @throws RemoteException
	 */
	private void encolaProcesoBatch(String jndiEJB, String method, HashMap params,
			String usuario) throws RemoteException {

		QueueConnectionFactory factory;
		QueueConnection connection = null;
		QueueSession session = null;
		QueueSender sender = null;
		Queue queue;
		ObjectMessage message;
		HashMap mapa = null;
		try {

			//obtenemos la cola
			factory = ServiceLocator.getInstance().getQueueConnectionFactory("RRHHConnectionFactory");
			queue = ServiceLocator.getInstance().getQueue("queue/BatchQueue");

			//creamos la conexion
			connection = factory.createQueueConnection();

			//creamos la session
			session = connection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			sender = session.createSender(queue);

			//creamos el mapa con los parametros del mensaje
			mapa = new HashMap();

			//tipo de proceso batch a ejecutar
			mapa.put("type", "proceso");
			//nombre del componente que ejecutara el proceso
			mapa.put("jndiEJB", jndiEJB);
			//nombre del metodo a ejecutarse
			mapa.put("method", method);
			//mapa con los parametros necesarios para el proceso
			mapa.put("params", params);
			//nombre del usuario que ejecuta el proceso
			mapa.put("user", usuario);
			//nombre del usuario que ejecuta el proceso
			mapa.put("messageID", (String) params.get("messageID"));

			//creamos el mensaje
			message = session.createObjectMessage(mapa);
			message.setJMSMessageID((String) params.get("messageID"));

			sender.send(message, //mensaje a enviar
					DeliveryMode.NON_PERSISTENT, //no persistente
					0, //no prioridad
					0); //no expiracion
			
			log.info("Enviando mensaje "+(String) params.get("messageID"));

			//invocamos el metodo
			String id = (String) mapa.get("messageID");
			String user = (String) mapa.get("user");
			String obs = (String) params.get("observacion");
			String codPers = (String) params.get("codPers");
			String dbpool = (String) params.get("dbpool");
			java.sql.Timestamp fInicio = new java.sql.Timestamp(System
					.currentTimeMillis());

			HashMap mensaje = new HashMap();
			mensaje.put("messageID", id);
			mensaje.put("codPers", codPers);
			mensaje.put("observacion", obs);			
			mensaje.put("fInicio", fInicio);
			mensaje.put("tipoProceso", "0");
			
			T1481DAO t1481 = new T1481DAO();
			t1481.registraMensajeLogBD(dbpool, mensaje, user);
			
		} catch (JMSException je) {
			log.debug("Error de JMS : " + je.getMessage());
		} finally {
			try {
				sender.close();
			} catch (Exception e) {
			}
			try {
				session.close();
			} catch (Exception e) {
			}
			try {
				connection.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Metodo encargado de colocar un reporte en la cola de procesamiento
	 * @param jndiEJB
	 * @param method
	 * @param params
	 * @param usuario
	 * @throws RemoteException
	 */
	private void encolaReporteBatch(String jndiEJB, String method, HashMap params,
			String usuario) throws RemoteException {

		QueueConnectionFactory factory;
		QueueConnection connection = null;
		QueueSession session = null;
		QueueSender sender = null;
		Queue queue;
		ObjectMessage message;
		HashMap mapa = null;
		try {

			//obtenemos la cola
			factory = ServiceLocator.getInstance().getQueueConnectionFactory("RRHHConnectionFactory");
			queue = ServiceLocator.getInstance().getQueue("queue/BatchQueue");

			//creamos la conexion
			connection = factory.createQueueConnection();

			//creamos la session
			session = connection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			sender = session.createSender(queue);

			//creamos el mapa con los parametros del mensaje
			mapa = new HashMap();

			//tipo de proceso batch a ejecutar
			mapa.put("type", "reporte");
			//nombre del componente que ejecutara el reporte
			mapa.put("jndiEJB", jndiEJB);
			//nombre del metodo a ejecutarse
			mapa.put("method", method);
			//mapa con los parametros necesarios para el reporte
			mapa.put("params", params);
			//nombre del usuario que ejecuta el reporte
			mapa.put("user", usuario);
			//nombre del usuario que ejecuta el reporte
			mapa.put("messageID", (String) params.get("messageID"));

			//creamos el mensaje
			message = session.createObjectMessage(mapa);
			message.setJMSMessageID((String) params.get("messageID"));

			sender.send(message, //mensaje a enviar
					DeliveryMode.NON_PERSISTENT, //no persistente
					0, //no prioridad
					0); //no expiracion
			//invocamos el metodo
			String id = (String) mapa.get("messageID");
			String user = (String) mapa.get("user");
			String obs = (String) params.get("observacion");
			String codPers = (String) params.get("codPers");
			String dbpool = (String) params.get("dbpool");
			java.sql.Timestamp fInicio = new java.sql.Timestamp(System
					.currentTimeMillis());

			HashMap mensaje = new HashMap();
			mensaje.put("messageID", id);
			mensaje.put("codPers", codPers);
			mensaje.put("observacion", obs);
			mensaje.put("messageID", id);
			mensaje.put("fInicio", fInicio);
			mensaje.put("tipoProceso", "1");

			T1481DAO t1481 = new T1481DAO();
			t1481.registraMensajeLogBD(dbpool, mensaje, user);
			
		} catch (JMSException je) {
			log.debug("Error de JMS : " + je.getMessage());
		} finally {
			try {
				sender.close();
			} catch (Exception e) {
			}
			try {
				session.close();
			} catch (Exception e) {
			}
			try {
				connection.close();
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * Metodo que envia un mail y lo coloca en un cola de mensajes
	 * @param mapa
	 * @throws RemoteException
	 */
	public void enviaCorreo(HashMap mapa) throws IncompleteConversationalState {

		QueueConnectionFactory factory;
		QueueConnection connection = null;
		QueueSession session = null;
		QueueSender sender = null;
		Queue queue;
		ObjectMessage message;
		try {
			log.debug("Enviando Mail:"+mapa);
			//obtenemos la cola
			factory = ServiceLocator.getInstance().getQueueConnectionFactory("RRHHConnectionFactory");
			queue = ServiceLocator.getInstance().getQueue("queue/BatchQueue");

			//creamos la conexion
			connection = factory.createQueueConnection();

			//creamos la session
			session = connection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			sender = session.createSender(queue);

			//tipo de proceso batch a ejecutar
			mapa.put("type", "correo");

			//creamos el mensaje
			message = session.createObjectMessage(mapa);

			sender.send(message); 

		} catch (JMSException je) {
			log.debug("Error de JMS : " + je.getMessage());
		} finally {
			try {
				sender.close();
			} catch (Exception e) {
			}
			try {
				session.close();
			} catch (Exception e) {
			}
			try {
				connection.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Metodo que se encarga de ejecutar un proceso batch. Utiliza reflexion.
	 * @param mapa
	 * @throws RemoteException
	 */	
	public void ejecutaProceso(HashMap mapa) throws RemoteException {

		try {

			//obtenemos el componente con JNDI
			Object objref = ServiceLocator.getInstance().lookup((String) mapa.get("jndiEJB"));

			//obtenemos la interfaz home
			Object home = PortableRemoteObject.narrow(objref, objref.getClass());
			Method create = home.getClass().getMethod("create", null);
			//obtenemos la interfaz remote
			Object remote = create.invoke(home, null);

			//los parametros son un HashMap y el usuario
			Class params[] = { Class.forName("java.util.HashMap"),
					Class.forName("java.lang.String") };
			Method metodo = remote.getClass().getMethod(
					(String) mapa.get("method"), params);

			//seteamos los argumentos
			HashMap parametros = (HashMap) mapa.get("params");
			Object args[] = { parametros, (String) mapa.get("user") };

			//invocamos el metodo
			String id = (String) mapa.get("messageID");
			String user = (String) mapa.get("user");
			String obs = (String) parametros.get("observacion");
			String codPers = (String) parametros.get("codPers");
			String dbpool = (String) parametros.get("dbpool");
			java.sql.Timestamp fInicio = new java.sql.Timestamp(System
					.currentTimeMillis());

			HashMap mensaje = new HashMap();
			mensaje.put("messageID", id);
			mensaje.put("codPers", codPers);
			mensaje.put("observacion", obs);			
			mensaje.put("fInicio", fInicio);
			mensaje.put("tipoProceso", "0");

			log.info("(" + id + "): " + "Inicio : " + fInicio);
			
			T1481DAO t1481 = new T1481DAO();
			t1481.registraBatchLogBD(dbpool, mensaje, user);
			
			//this.registraMensajeLogBD(dbpool, mensaje, user);
			metodo.invoke(remote, args);
			log.info("(" + (String) mapa.get("messageID") + "): "
					+ "Fin : "
					+ new java.sql.Timestamp(System.currentTimeMillis()));

		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Error ejecutaProceso : " + e.toString());
		}
	}

	/**
	 * Metodo que se encarga de generar un reporte masivo. Utiliza reflexion
	 * @param mapa
	 * @throws RemoteException
	 */
	public void generaReporte(HashMap mapa) throws RemoteException {

		try {

			//obtenemos el componente con JNDI
			Object objref = ServiceLocator.getInstance().lookup((String) mapa.get("jndiEJB"));

			//obtenemos la interfaz home
			Object home = PortableRemoteObject
					.narrow(objref, objref.getClass());
			Method create = home.getClass().getMethod("create", null);
			//obtenemos la interfaz remote
			Object remote = create.invoke(home, null);

			//los parametros son un HashMap y el usuario
			Class params[] = { Class.forName("java.util.HashMap"),
					Class.forName("java.lang.String") };
			Method metodo = remote.getClass().getMethod(
					(String) mapa.get("method"), params);

			//seteamos los argumentos
			HashMap parametros = (HashMap) mapa.get("params");
			Object args[] = { parametros, (String) mapa.get("user") };

			//invocamos el metodo
			String id = (String) mapa.get("messageID");
			String user = (String) mapa.get("user");
			String obs = (String) parametros.get("observacion");
			String codPers = (String) parametros.get("codPers");
			String dbpool = (String) parametros.get("dbpool");
			java.sql.Timestamp fInicio = new java.sql.Timestamp(System
					.currentTimeMillis());

			HashMap mensaje = new HashMap();
			mensaje.put("messageID", id);
			mensaje.put("codPers", codPers);
			mensaje.put("observacion", obs);
			mensaje.put("messageID", id);
			mensaje.put("fInicio", fInicio);
			mensaje.put("tipoProceso", "1");

			log.info("(" + id + "): " + "Inicio : " + fInicio);
			
			T1481DAO t1481 = new T1481DAO();
			t1481.registraBatchLogBD(dbpool, mensaje, user);
			
			//this.registraMensajeLogBD(dbpool, mensaje, user);
			metodo.invoke(remote, args);
			log.info("(" + (String) mapa.get("messageID") + "): "
					+ "Fin : "
					+ new java.sql.Timestamp(System.currentTimeMillis()));

		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Error ejecutaProceso : " + e.toString());
		}
	}

	/**
	 * Metodo que colocar un mensaje en la cola de correos.
	 * @param mapa
	 * @throws RemoteException
	 */
	public void sendMail(HashMap mapa) throws RemoteException {

		try {

			String message = (String) mapa.get("message");
			String subject = (String) mapa.get("subject");
			String from = (String) mapa.get("from");
			String to = (String) mapa.get("to");
			

			DAOEnviaCorreo sender = new DAOEnviaCorreo();
			BeanCorreo beanCorreo = new BeanCorreo();

			//seteamos los parametros del mensaje
			beanCorreo.setAsunto(subject);
			beanCorreo.setMensaje(message);
			beanCorreo.setBuzon_de(from);
			beanCorreo.setBuzon_para(to);
			

			//enviamos el correo
			if (from!=null && to!=null && !from.equals("") && !to.equals("")) {
				sender.enviarCorreo(beanCorreo, "queue/CorreoQueue");
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Error : " + e.getMessage());
		}
	}

	/**
	 * Metodo que crea el archivo log
	 * @param messageId
	 * @param proceso
	 * @param usuario
	 * @throws RemoteException
	 */
	public void creaLog(String messageId, String proceso, String usuario)
			throws IncompleteConversationalState {

		try {

			String path = Constantes.RUTA_LOG_PROCESOS + messageId + ".txt";
			output = new PrintStream(new FileOutputStream(path));

			String linea = "";
			String lineaTitulo = "";
			for (int i = 0; i < this.maxColumnas; i++) {
				lineaTitulo += "_";
			}

			output.println(Utiles.formateaCadena(lineaTitulo, this.maxColumnas,true));
			output.println(Utiles.formateaCadena("LOG DEL "
					+ proceso.toUpperCase(), this.maxColumnas, true));
			output.println("");
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "Usuario : " + usuario;
			output.println(linea);
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "  Fecha : " + Utiles.obtenerFechaActual();
			output.println(linea);
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "   Hora : " + Utiles.obtenerHoraActual();
			output.println(linea);
			output.println(Utiles.formateaCadena(lineaTitulo, this.maxColumnas,
					true));
			output.println("");
		} catch (Exception e) {
			log.debug("Error al crear el archivo log : " + e.getMessage());
		}
	}

	/**
	 * Metodo que crea la cabecera del reporte
	 * @param reporte
	 * @param params
	 * @param usuario
	 * @throws RemoteException
	 */
	public void cabeceraReporte(String reporte, HashMap params, String usuario)
			throws IncompleteConversationalState {

		try {

			String linea = "";
			String lineaTitulo = "";
			for (int i = 0; i < this.maxColumnas; i++) {
				lineaTitulo += "_";
			}

			String fechaIni = params.get("fechaIni") != null ? (String) params
					.get("fechaIni") : "";
			String fechaFin = params.get("fechaFin") != null ? (String) params
					.get("fechaFin") : "";

			output.println("");
			output.println(lineaTitulo);
			output.println(Utiles.formateaCadena("REPORTE DE "+ reporte.toUpperCase(), this.maxColumnas, true));
			
			if (!fechaIni.equals("") || !fechaFin.equals("")) {
				linea = Utiles.formateaCadena("Del " + fechaIni + " al "
						+ fechaFin, this.maxColumnas, true);
				output.println(linea);
			} else {
				output.println("");
			}
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "Usuario : " + usuario;
			output.println(linea);
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "  Fecha : " + Utiles.obtenerFechaActual();
			output.println(linea);
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "   Hora : " + Utiles.obtenerHoraActual();
			output.println(linea);
/*			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
//					+ " P�gina : " + this.numPagina;
					+ " Página : " + this.numPagina;
			output.println(linea);*/
			output.println(lineaTitulo);
			output.println("");

			this.numLinea = 10;

		} catch (Exception e) {
		}
	}
	
	/** JVILLACORTA - 23/08/2011 - ALERTA DE SOLICITUDES
	 * Metodo que crea la cabecera del reporte
	 * @param reporte
	 * @param params
	 * @param usuario
	 * @throws RemoteException
	 */
	public void cabeceraReporteTrab(String reporte, HashMap params, String usuario)
			throws IncompleteConversationalState {

		try {

			String linea = "";
			String lineaTitulo = "";
			for (int i = 0; i < this.maxColumnas; i++) {
				lineaTitulo += "_";
			}

			/*String fechaIni = params.get("fechaIni") != null ? (String) params
					.get("fechaIni") : "";
			String fechaFin = params.get("fechaFin") != null ? (String) params
					.get("fechaFin") : "";*/

			output.println("");
			output.println(lineaTitulo);
			output.println(Utiles.formateaCadena("REPORTE DE "+ reporte.toUpperCase(), this.maxColumnas, true));
			
			/*if (!fechaIni.equals("") || !fechaFin.equals("")) {
				linea = Utiles.formateaCadena("Del " + fechaIni + " al "
						+ fechaFin, this.maxColumnas, true);
				output.println(linea);
			} else {
				output.println("");
			}*/
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "Usuario : " + usuario;
			output.println(linea);
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "  Fecha : " + Utiles.obtenerFechaActual();
			output.println(linea);
			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
					+ "   Hora : " + Utiles.obtenerHoraActual();
			output.println(linea);
/*			linea = Utiles.formateaCadena("", this.maxColumnas - 25, false)
//					+ " P�gina : " + this.numPagina;
					+ " Página : " + this.numPagina;
			output.println(linea);*/
			output.println(lineaTitulo);
			output.println("");

			this.numLinea = 10;

		} catch (Exception e) {
		}
	}

	/**
	 * Metodo que crea el archivo log del reporte
	 * @param messageId
	 * @param reporte
	 * @param params
	 * @param usuario
	 * @throws RemoteException
	 */
	public void creaReporte(String messageId, String reporte, HashMap params,
			String usuario) throws IncompleteConversationalState {

		try {

			this.numLinea = 0;
			this.numPagina = 1;
			String path = Constantes.RUTA_LOG_REPORTES + messageId + ".txt";
			output = new PrintStream(new FileOutputStream(path));
			this.cabeceraReporte(reporte, params, usuario);

		} catch (Exception e) {
			log.debug("Error al crear el archivo log : " + e.getMessage());
		}
	}
	
	/** JVILLACORTA - 23/08/2011 - ALERTA DE SOLICITUDES
	 * Metodo que crea el archivo log del reporte de notificaciones para trabajadores
	 * @param messageId
	 * @param reporte
	 * @param params
	 * @param usuario
	 * @throws RemoteException
	 */
	public void creaReporteTrab(String messageId, String reporte, HashMap params,
			String usuario) throws IncompleteConversationalState {

		try {

			this.numLinea = 0;
			this.numPagina = 1;
			String path = Constantes.RUTA_LOG_REPORTES + messageId + ".txt";
			output = new PrintStream(new FileOutputStream(path));
			this.cabeceraReporteTrab(reporte, params, usuario);

		} catch (Exception e) {
			log.debug("Error al crear el archivo log : " + e.getMessage());
		}
	}
	
	/**
	 * Metodo que escribe una linea en el log
	 * @param texto
	 * @param reporte
	 * @param params
	 * @param usuario
	 */
	public void escribe(String texto, String reporte, HashMap params,
			String usuario) {

		try {

			output.println(texto);
			this.numLinea++;

			if (this.numLinea >= this.maxLineas) {
				this.numPagina++;
//				this.cabeceraReporte(reporte, params, usuario);
			}

		} catch (Exception e) {

		}
	}

	
	/**
	 * Metodo que registra finaliza el log de un proceso 
	 * @param dbpool
	 * @param mapa
	 * @param usuario
	 * @throws IncompleteConversationalState
	 */
	public void registraLog(String dbpool, HashMap mapa, String usuario)
			throws IncompleteConversationalState {

		T02DAO personalDAO = new T02DAO();
		CorreoDAO correoDAO = new CorreoDAO();
		String messageId = (String) mapa.get("messageID");
		try {
			
			String codPers = (String) mapa.get("codPers");
			java.sql.Timestamp fFin = new java.sql.Timestamp(System
					.currentTimeMillis());

			output.println("");
			output.println("Fin del Proceso : " + fFin);
			//generamos el archivo
			this.output.close();
			//obtenemos el path del archivo
			String path = messageId + ".txt";
			String pathZip = messageId + ".zip";
			//comprimiendo archivo
			boolean zip = comprimeLog(Constantes.RUTA_LOG_PROCESOS, path,
					pathZip);
			if (zip) {

				//leemos de la fuente de datos
				InputStream in = new FileInputStream(
						Constantes.RUTA_LOG_PROCESOS + pathZip);

				HashMap params = new HashMap();
				params.put("messageID", messageId);
				params.put("fFin", fFin);
				params.put("codPers", codPers);
				params.put("archivo", in);

				//registramos el log en BD
				T1481DAO t1481 = new T1481DAO();
				boolean res = t1481.registraLogBD(dbpool, params, usuario);
				//boolean res = this.registraLogBD(dbpool, params, usuario);
				if (res) {

					String observacion = (String) mapa.get("observacion");
					
					//modificado para que no envie correos por tema de generar, procesar asistencia y acumular horas extras
					//if (!observacion.startsWith("Registro de asistencias") && !observacion.startsWith("Proceso de asistencia del periodo")//ICAPUNAY 30/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
					if (!observacion.startsWith("Registro de asistencias") && !observacion.startsWith("Proceso de asistencia del periodo") && !observacion.startsWith("Proceso acumulacion horas")//ICAPUNAY 30/05/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
							&& !observacion.startsWith("Proceso de generacion de saldos vacacionales")){
					
						String mensaje = "El <strong>" + observacion+ " ("+messageId+")</strong> ha terminado.";
						
						/*ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
						String servidorIP = bundle.getString("servidorIP");
						String strURL = "http://"+servidorIP+"/asistencia/asisS13Alias";
						String paramsURL = "accion=cargarLogProcesos&codPers="+codPers;
						String programa = bundle.getString("programa4");
						Cifrador cifrador = new Cifrador();
						String url = cifrador.encriptaURL(codPers, programa, strURL, paramsURL);*/
						
						String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, codPers);
						String texto = Utiles.textoCorreoProceso(dbpool, nombre, mensaje,"");
	
						//enviamos el mail al trabajador
						HashMap datos = new HashMap();
						datos.put("subject", "Administrador de Procesos");
						datos.put("message", texto);
						datos.put("from", correoDAO.findCorreoByCodPers(dbpool,
								codPers));
						datos.put("to", correoDAO.findCorreoByCodPers(dbpool,
								codPers));
						this.enviaCorreo(datos);
						
					}

				}
			}

			//eliminando el archivo temporal
			File f = new File(Constantes.RUTA_LOG_PROCESOS + path);
			if (f.exists()) {
				f.delete();
			}
			//eliminando el archivo zip
			File fZ = new File(Constantes.RUTA_LOG_PROCESOS + pathZip);
			if (fZ.exists()) {
				fZ.delete();
			}

		} catch (Exception e) {
			log.error("Error al registrar el log del proceso con messageId " + messageId,e);
		}
	}

	/**
	 * Metodo que finaliza el log de un reporte
	 * @param dbpool
	 * @param mapa
	 * @param usuario
	 * @throws IncompleteConversationalState
	 */
	public void registraReporte(String dbpool, HashMap mapa, String usuario)
			throws IncompleteConversationalState {

		T02DAO personalDAO = new T02DAO();
		CorreoDAO correoDAO = new CorreoDAO();
		String messageId = (String) mapa.get("messageID");
		try {

			String codPers = (String) mapa.get("codPers");
			java.sql.Timestamp fFin = new java.sql.Timestamp(System.currentTimeMillis());

			//generamos el archivo
			this.output.close();
			//obtenemos el path del archivo
			String path = messageId + ".txt";
			String pathZip = messageId + ".zip";
			//comprimiendo archivo
			boolean zip = comprimeLog(Constantes.RUTA_LOG_REPORTES, path, pathZip);
			if (zip) {

				//leemos de la fuente de datos
				InputStream in = new FileInputStream(Constantes.RUTA_LOG_REPORTES + pathZip);

				HashMap params = new HashMap();
				params.put("messageID", messageId);
				params.put("fFin", fFin);
				params.put("codPers", codPers);
				params.put("archivo", in);

				//registramos el log en BD
				T1481DAO t1481 = new T1481DAO();
				boolean res = t1481.registraLogBD(dbpool, params, usuario);
				
				//boolean res = this.registraLogBD(dbpool, params, usuario);
				if (res) {

					String observacion = (String) mapa.get("observacion");
					String mensaje = "El <strong>" + observacion+" ("+messageId+") </strong> ha terminado.";

					/*ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.sunat.sp.asistencia.sirh");
					String servidorIP = bundle.getString("servidorIP");
					String strURL = "http://"+servidorIP+"/asistencia/asisS13Alias";
					String paramsURL = "accion=cargarLogReportes&codPers="+ codPers;
					String programa = bundle.getString("programa5");
					Cifrador cifrador = new Cifrador();
					String url = cifrador.encriptaURL(codPers, programa, strURL, paramsURL);*/

					String nombre = personalDAO.findNombreCompletoByCodPers(dbpool, codPers);
					String texto = Utiles.textoCorreoProceso(dbpool, nombre,mensaje,"");

					//enviamos el mail al trabajador
					HashMap datos = new HashMap();
					datos.put("subject", "Administrador de Reportes");
					datos.put("message", texto);
					datos.put("from", correoDAO.findCorreoByCodPers(dbpool,
							codPers));
					datos.put("to", correoDAO.findCorreoByCodPers(dbpool,
							codPers));
					this.enviaCorreo(datos);
				}
			}

			//eliminando el archivo temporal
			File f = new File(Constantes.RUTA_LOG_REPORTES + path);
			if (f.exists()) {
				f.delete();
			}

			//eliminando el archivo zip
			File fZ = new File(Constantes.RUTA_LOG_REPORTES + pathZip);
			if (fZ.exists()) {
				fZ.delete();
			}

		} catch (Exception e) {
			log.error("Error al registrar el log del reporte con messageId " + messageId,e);
		}
	}

	/**
	 * Metodo encargado de comprimir el archivo log para ser almacenado en BD
	 * @param dir
	 * @param origen
	 * @param destino
	 * @return
	 */
	public boolean comprimeLog(String dir, String origen, String destino) {
		boolean res = true;
		try {

			String path = dir + origen;
			String pathZip = dir + destino;

			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					new File(pathZip)));
			ZipEntry zentry = new ZipEntry(origen);
			out.putNextEntry(zentry);

			FileInputStream in = new FileInputStream(new File(path));
			byte[] buffer = new byte[0x10000]; //64kb
			int n;
			while ((n = in.read(buffer)) > 0) {
				out.write(buffer, 0, n);
			}

			in.close();
			out.closeEntry();
			out.close();

		} catch (Exception e) {
			output.println("Error al comprimir archivo : " + e.getMessage());
			res = false;
		}
		return res;
	}

	
}