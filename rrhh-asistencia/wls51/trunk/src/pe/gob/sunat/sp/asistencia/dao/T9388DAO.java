//AGONZALESF -PAS20181U230200023 - LICENCIA POR ENFERMEDAD  
package pe.gob.sunat.sp.asistencia.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

public class T9388DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T8155DAO.class);
	 

	private DataSource datasource;
	 
	private static final String SELECT_SOLICITUD = " SELECT num_solicitud,ann_solicitud,num_sol,cod_uuoo,cod_pers,cod_mov,fec_solicitud,cod_cmp,num_archivo,cod_cie10,ind_del,cod_user_crea,fec_creacion,cod_user_mod,fec_modifica "
			+ " FROM t9388solmedica "
			+ " WHERE  cod_pers = ? "
			+ " AND    ann_solicitud = ? "
			+ " AND    num_sol = ? "; 
	
	 
	  
	private static final String INSERT_SOLICITUD = "INSERT INTO t9388solmedica ("
			+ "num_solicitud,"
			+ "ann_solicitud,"
			+ "num_sol,"
			+ "cod_uuoo,"
			+ "cod_pers,"
			+ "cod_mov,"
			+ "fec_solicitud,"
			+ "cod_cmp,"
			+ "num_archivo,"
			+ "cod_cie10,"
			+ "ind_del,"
			+ "cod_user_crea,"
			+ "fec_creacion)"
	+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
  
	public T9388DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

	/**
	 * Funcion para obtener datos de solicitud medica por codPers, año y numero
	 * @param codPers Codigo de trabajador
	 * @param anno:  Año de solicitud
	 * @param numero :  Numero de solicitud
	 * @return Mapa con datos de la solicitud
	 * @throws SQLException
	 */
	public Map findSolicitudLicMedica(String codPers, String anno, String numero) throws SQLException {
		Map map = new HashMap();
		log.debug("Ingreso findSolicitudMedica");
		Object obj[] = { codPers, anno, numero };
		map = executeQueryUniqueResult(datasource, SELECT_SOLICITUD.toString(), obj);

		return map;
	}

 

	/**
	 * Funcion para la insercion de datos en txxxxsolmedica
	 * @param datos : datos para la insercion de data de solicitud
	 * @return true si es correcto
	 */ 
	 
	public boolean insertSolMedica(Map datos) {
		executeUpdate(datasource, INSERT_SOLICITUD,
				new Object[] { datos.get("num_solicitud"), datos.get("ann_solicitud"), datos.get("num_sol"), datos.get("cod_uuoo"), datos.get("cod_pers"),
						datos.get("cod_mov"), datos.get("fec_solicitud"), datos.get("cod_cmp"), datos.get("num_archivo"), datos.get("cod_cie10"), datos.get("ind_del"),   datos.get("cod_user_crea"), datos.get("fec_creacion") });
		return true;
	}
 

	 
}