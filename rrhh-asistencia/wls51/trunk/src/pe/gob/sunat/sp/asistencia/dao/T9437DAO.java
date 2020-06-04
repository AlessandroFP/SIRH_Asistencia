//AGONZALESF -PAS20181U230200023 - LICENCIA POR ENFERMEDAD  
package pe.gob.sunat.sp.asistencia.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

public class T9437DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T9437DAO.class);

	private DataSource datasource;
	 
	private static final String SELECT_SALDOVAC_PK = " SELECT num_saldovac,cod_personal,num_anno,fec_saldovac,num_dias_labor,num_dias_descont,num_dias_exclic,ind_genera "
			+ " FROM t9437saldovac"
			+ " WHERE  cod_personal = ? "
			+ " AND    num_anno = ? "; 
	
	private static final String SELECT_MAX_SERIAL="SELECT MAX(num_saldovac) as maximo from t9437saldovac"; 
	  
	private static final String INSERT_DET_SALDOVAC = "INSERT INTO t9437saldovac ("	
			+ "cod_personal,"
			+ "num_anno,"
			+ "fec_saldovac,"
			+ "num_dias_labor,"
			+ "num_dias_descont,"
			+ "num_dias_exclic,"
			+ "ind_genera,"
			+ "ind_del,"
			+ "fec_creacion,"
			+ "cod_usuregis) "
	+ "VALUES (?,?,?,?,?,?,?,?,?,?)";
	

	public T9437DAO(Object datasource) {
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
	public Map findByPK(String codPers,String periodo) throws SQLException {
		Map map = new HashMap();
		log.debug("Ingreso findByPK: ");
		Object obj[] = { codPers, periodo};
		map = executeQueryUniqueResult(datasource, SELECT_SALDOVAC_PK.toString(), obj);

		return map;
	}

	/**
	 * Funcion para la insercion de datos en t9437saldovac   
	 * @param datos : datos para la insercion de data de solicitud
	 * @return true si es correcto
	 */ 
	 
	public boolean insertDetalleSaldoVacacional(Map datos) {
		log.debug("Ingreso a insertDetalleSaldoVacacional:"+INSERT_DET_SALDOVAC);
		executeUpdate(datasource, INSERT_DET_SALDOVAC,
				new Object[] { datos.get("cod_pers"), datos.get("anno"),datos.get("fec_saldovac"), datos.get("dias"), datos.get("saldo"), datos.get("saldo_temp"),
						datos.get("ind_saldovac"), datos.get("ind_del"), datos.get("fcreacion"),datos.get("cuser_crea") });
		return true;
	}
	
	public int findMaxNumSerial(){
		log.debug("Ingreso a findMaxNumSerial");
		int numMax=-1;Map maxim=new HashMap();
		Object obj[] = {};
		maxim=executeQueryUniqueResult(datasource, SELECT_MAX_SERIAL.toString(), obj);
		numMax = Integer.parseInt(maxim.get("maximo").toString());
		return numMax;
	}
 
	/**
	 * Funcion para la modificar el movimiento inicial y final del seguimPapeleta
	 * @param datos : datos para la insercion de data de solicitud
	 * @return true si es correcto
	 */ 
	
	public boolean updatePorPK(Map datos) {//pendiente
		log.debug("Ingreso updatePorPK:"+datos);
		log.debug("SQL UPDATE-MOV-FIN:"+INSERT_DET_SALDOVAC);
		executeUpdate(datasource, INSERT_DET_SALDOVAC,
				new Object[] {datos.get("mov"),
				datos.get("cod_pers"), datos.get("cod_usumodif"), datos.get("fec_modif"), datos.get("anno"), datos.get("periodo"), datos.get("fing"), datos.get("hing")});
		return true;
	}
	 
}