//AGONZALESF -PAS20181U230200023 - LICENCIA POR ENFERMEDAD  
package pe.gob.sunat.sp.asistencia.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

public class T9395DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T9395DAO.class);
	/*
	 num_papeleta
	cod_personal
	cod_periodo
	fec_papeleta
	hor_papeleta
	cod_mov_ini
	cod_mov_pap
	cod_estado
	ind_del
	cod_usuregis
	fec_regis
	cod_usumodif
	fec_modif

	 * */

	private DataSource datasource;
	 
	private static final String SELECT_PAPELETA = " SELECT num_papeleta,cod_personal,cod_periodo,fec_papeleta,hor_papeleta,cod_mov_ini,cod_mov_pap,cod_estado,ind_del "
			+ " FROM t9395papeleta"
			+ " WHERE  cod_personal = ? "
			+ " AND    cod_periodo = ? "
			+ " AND    fec_papeleta = ? "
			+ " AND    hor_papeleta = ? "; 
	
	 
	  
	private static final String INSERT_PAPELETA = "INSERT INTO t9395papeleta ("	
			+ "num_papeleta,"
			+ "cod_personal,"
			+ "cod_periodo,"
			+ "fec_papeleta,"
			+ "hor_papeleta,"
			+ "cod_mov_ini,"
			+ "cod_mov_pap,"
			+ "cod_estado,"
			+ "ind_del,"
			+ "cod_usuregis,"
			+ "fec_regis)"
			//+ "cod_usumodif"
			//+ "fec_modif"
	+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String UPDATE_MOV_FIN="UPDATE t9395papeleta SET cod_mov_pap=?, cod_usumodif=?, fec_modif=? "
			+ " WHERE  cod_personal = ? "
			+ " AND    cod_periodo = ? "
			+ " AND    fec_papeleta = ? "
			+ " AND    hor_papeleta = ? ";
	
	private static final String UPDATE_MOV_INI_FIN="UPDATE t9395papeleta SET cod_mov_ini=?, cod_mov_pap=?, cod_usumodif=?, fec_modif=? "
			+ " WHERE  cod_personal = ? "
			+ " AND    cod_periodo = ? "
			+ " AND    fec_papeleta = ? "
			+ " AND    hor_papeleta = ? ";
	
	public T9395DAO(Object datasource) {
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
	public Map findByPK(String codPers,String periodo, String fing, String hing) throws SQLException {
		Map map = new HashMap();
		log.debug("Ingreso findByPK: ");
		Object obj[] = { codPers, periodo, fing,hing };
		map = executeQueryUniqueResult(datasource, SELECT_PAPELETA.toString(), obj);

		return map;
	}

	/**
	 * Funcion para la insercion de datos en txxxxsolmedica
	 * @param datos : datos para la insercion de data de solicitud
	 * @return true si es correcto
	 */ 
	
	public boolean insertSeguimPapeleta(Map datos) {
		log.debug("Ingreso insertSeguimPapeleta:"+datos);
		log.debug("SQL iNSERT:"+INSERT_PAPELETA);
		executeUpdate(datasource, INSERT_PAPELETA,
				new Object[] { datos.get("secuencia"),datos.get("cod_pers"), datos.get("periodo"), datos.get("fing"), datos.get("hing"), datos.get("mov"),
						datos.get("cod_mov_pap"), datos.get("estado_id"), datos.get("ind_del"), datos.get("cod_usuregis"), datos.get("fec_regis")});
		return true;
	}
 
	/**
	 * Funcion para la modificar el movimiento final del seguimPapeleta
	 * @param datos : datos para la insercion de data de solicitud
	 * @return true si es correcto
	 */ 
	
	public boolean updateMovFin(Map datos) {
		log.debug("Ingreso updateMovFin:"+datos);
		log.debug("SQL UPDATE-MOV-FIN:"+UPDATE_MOV_FIN);
		executeUpdate(datasource, UPDATE_MOV_FIN,
				new Object[] { datos.get("cod_mov_pap"), datos.get("cod_usumodif"), datos.get("fec_modif"),datos.get("cod_pers"), datos.get("periodo"), datos.get("fing"), datos.get("hing")});
		return true;
	}
	/**
	 * Funcion para la modificar el movimiento inicial y final del seguimPapeleta
	 * @param datos : datos para la insercion de data de solicitud
	 * @return true si es correcto
	 */ 
	
	public boolean updateMovIniFin(Map datos) {
		log.debug("Ingreso updateMovFin:"+datos);
		log.debug("SQL UPDATE-MOV-FIN:"+UPDATE_MOV_INI_FIN);
		executeUpdate(datasource, UPDATE_MOV_INI_FIN,
				new Object[] {datos.get("mov"),
				datos.get("cod_mov_pap"), datos.get("cod_usumodif"), datos.get("fec_modif"), datos.get("cod_pers"), datos.get("periodo"), datos.get("fing"), datos.get("hing")});
		return true;
	}
	 
}