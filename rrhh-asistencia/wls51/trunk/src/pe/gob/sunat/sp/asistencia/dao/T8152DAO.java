//PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * Title : T8152DAO 
 * Description : Clase para el manejo de la tabla t8152archivo y(cabecera de archivo) 
 * Company: Sunat 
 *  
 * @author agonzalesf
 * @version 1.0
 */

public class T8152DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T8152DAO.class);
	private DataSource datasource;
	private static final String INSERT_SENTENCE = "insert into t8152archivo(num_archivo,fec_registro,cod_estado, cod_usucrea, fec_creacion , ind_del) "
			+ "values (?,sysdate,?,?,sysdate,0)";
	 
	public T8152DAO() {
	}

	public T8152DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

	/**
	 * Método que se encarga de insertar archivo
	 * @param datos: Datos de archivo a insertar
	 * @return
	 * @throws DAOException
	 */
	public int insert(Map datos) throws DAOException {
		log.debug("insert ->"+datos);
		int filas = 0;
		Connection connection = null;
		PreparedStatement statement= null; 
		try {

			 connection = datasource.getConnection();
			 statement = connection.prepareStatement(INSERT_SENTENCE.toString());
			log.debug("Mapa -> "+  datos);
			statement.setInt(1, Integer.parseInt(datos.get("num_archivo").toString()));
			statement.setString(2, (String) datos.get("cod_estado"));
			statement.setString(3, (String) datos.get("cod_usucrea"));
			log.debug("previo insert  ");
			filas = statement.executeUpdate();
			log.debug("affectedRows --> " +filas);			 

		} catch (Exception e) { 
			log.error(e);
			throw new DAOException();
		} finally {
			log.debug("Cierre de conexion"); 
			try {
				statement.close();
			} catch (Exception ex) {
				 
			}
			try {
				connection.close();
			} catch (Exception ex) {
			 
			} 
			 
		} 
		return filas; 

	}


}
