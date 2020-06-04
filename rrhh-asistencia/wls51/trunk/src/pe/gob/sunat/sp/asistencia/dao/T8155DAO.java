//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
/**
 * Title : T8153DAO 
 * Description : Clase encargada de administrar las consultas a la tabla t8155solreintegro
 * @author agonzalesf
 *
 */
public class T8155DAO  extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T8155DAO.class);
	 

	private DataSource datasource;
	 
	private static final String SELECT_SOLICITUD = "SELECT num_seqrein,ann_solicitud,num_solicitud,cod_uuoo,cod_pers,cod_mov,fec_solicitud,num_archivo,cod_planorig,cod_splanorig,ann_solplan,mes_solplan"
			+ " FROM t8155solreintegro "
			+ " WHERE  cod_pers = ? "
			+ " AND    ann_solicitud = ? "
			+ " AND    num_solicitud = ? "; 
	
	private static final String SELECT_SOLICITUD_BY_NUMBER_SEQREIN = "SELECT num_seqrein,ann_solicitud,num_solicitud,cod_uuoo,cod_pers,cod_mov,fec_solicitud,num_archivo,cod_planorig,cod_splanorig,ann_solplan,mes_solplan,"
			+ " nvl(ann_plandev,'') ann_plandev , nvl(mes_plandev,'')mes_plandev "
			+ " FROM t8155solreintegro "
			+ " WHERE   num_seqrein = ?  ";
	  
	private static final String INSERT_SOLICITUD = "INSERT INTO t8155solreintegro ("
			+ "num_seqrein,"
			+ "ann_solicitud,"
			+ "num_solicitud,"
			+ "cod_uuoo,"
			+ "cod_pers,"
			+ "cod_mov, "
			+ "fec_solicitud, "
			+ "num_archivo,"
			+ "cod_planorig,"
			+ "cod_splanorig,"
			+ "ann_solplan,"
			+ "mes_solplan,"
			+ "cod_usucrea,"
			+ "fec_crea,"
			+ "ind_del) "
	+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String UPDATE_DEV_SOLICITUD = "UPDATE t8155solreintegro SET cod_plandev = ?, cod_splandev = ?, ann_plandev = ?, mes_plandev = ?, cod_usumodif = ?, fec_modif = ? WHERE num_seqrein = ?";
 
	public T8155DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

	/**
	 * Funcion para obtener datos de solicitud de reintegro por codPers, año y numero
	 * @param codPers Codigo de trabajador
	 * @param anno:  Año de solicitud
	 * @param numero :  Numero de solicitud
	 * @return Mapa con datos de la solicitud
	 * @throws SQLException
	 */
	public Map findSolicitudReintegro(String codPers, String anno, String numero) throws SQLException {
		Map map = new HashMap();
		log.debug("Ingreso findSolicitudReintegro");
		Object obj[] = { codPers, anno, numero };
		map = executeQueryUniqueResult(datasource, SELECT_SOLICITUD.toString(), obj);

		return map;
	}

	/**
	 * Funcion para obtener datos de solicitud de reintegro numseqrein
	 * @param numseqrein :  Numero de solicitud
	 * @return Mapa con datos de la solicitud
	 * @throws SQLException
	 */
	public Map findSolicitudReintegroByNumSeqRein(String numseqrein) throws SQLException {
		Map map = new HashMap();
		log.debug("Ingreso findSolicitudReintegroByNumSeqRein");
		Object obj[] = { numseqrein };
		map = executeQueryUniqueResult(datasource, SELECT_SOLICITUD_BY_NUMBER_SEQREIN.toString(), obj);

		return map;
	}

	/**
	 * Funcion para la insercion de datos en t8155solreintegro
	 * @param datos : datos para la insercion de data de solicitud
	 * @return true si es correcto
	 */
	public boolean insertSolReintegro(Map datos) {
		executeUpdate(datasource, INSERT_SOLICITUD,
				new Object[] { datos.get("num_seqrein"), datos.get("ann_solicitud"), datos.get("num_solicitud"), datos.get("cod_uuoo"), datos.get("cod_pers"),
						datos.get("cod_mov"), datos.get("fec_solicitud"), datos.get("num_archivo"), datos.get("cod_planorig"), datos.get("cod_splanorig"),
						datos.get("ann_solplan"), datos.get("mes_solplan"), datos.get("cod_usucrea"), datos.get("fec_creacion"), datos.get("ind_del") });
		return true;
	}

	/**
	 * Actualiza los datos de devolución de la solicitud 
	 * @param params : datos para la actualizacion 
	 */
	public void updateDevSolicitud(Map params) {
		executeUpdate(
				datasource,
				UPDATE_DEV_SOLICITUD,
				new Object[] { params.get("cod_plandev"), params.get("cod_splandev"), params.get("ann_plandev"), params.get("mes_plandev"),
						params.get("cod_usumodif"), params.get("fec_modif"), params.get("num_seqrein") });
	}

}
