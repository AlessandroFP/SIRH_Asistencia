package pe.gob.sunat.rrhh.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

/**
 * 
 * Clase : T28DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T28PERTE de personal.
 */
public class T28DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select p.t28cod_pers, p.t28tip_tel, p.t28num_ddn, p.t28num_tel, ")
																 .append("p.t28num_ane, p.t28f_graba, p.t28cod_user, ")
																 .append("(select t.t99descrip from t99codigos t ")
																 .append("where t.t99cod_tab = '012' and t.t99tip_desc = 'D' and ")
																 .append("p.t28tip_tel = t.t99codigo)as t28tip_tel_desc ")
																 .append("from t28perte p ")
																 .append("where t28cod_pers = ?");
	
	private final StringBuffer insertByRtps = new StringBuffer("insert into t28perte (t28cod_pers, t28tip_tel, t28num_ddn, t28num_tel, t28f_graba, t28cod_user) values (?,?,?,?,?,?)");
	private final StringBuffer updateByRtps = new StringBuffer("update t28perte set t28num_ddn = ? , t28num_tel = ? , t28f_graba = ? , t28cod_user = ? WHERE t28cod_pers = ? and t28tip_tel = ? ");
	private final StringBuffer findByRegistro = new StringBuffer("select t28tip_tel, t28num_ddn, t28num_tel  from  t28perte where t28cod_pers = ? and t28tip_tel = ? ");
	
	/**
	 * @param datasource Object
	 */
	public T28DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T28PERTE
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers});
	}
	
	/**
	 * Metodo que se encarga de buscar registro por codigo de personal
	 *  
	 * @param params	Map. cod_personal, tip_tel
	 * @return          void
	 * @throws DAOException
	 * */
	public Map findByRegistro(Map params) throws DAOException {				
		
		Map res = executeQueryUniqueResult(dataSource, findByRegistro.toString(), 
						new Object[]{params.get("cod_personal") , params.get("tip_tel")} );
		
		return res;		
	}
	
	/**
	 * Metodo que se encarga de actualizar algunos datos del personal 
	 *  
	 * @param hm	Map. (fecha_reg, num_ddn, num_telef, nomb_usuario, cod_personal, tip_tel
	 * @return      void
	 * @throws DAOException
	 * */
	public void updateByRtps(Map hm) throws DAOException {
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
		executeUpdate(dataSource, updateByRtps.toString(), new Object[]{hm.get("num_ddn"), 
				 hm.get("num_telef"), fecha_reg.getTimestamp(), hm.get("nomb_usuario"), hm.get("cod_personal"), hm.get("tip_tel")} );
				
	}
	
	/**
	 * Metodo que se encargado de insertar datos 
	 *  
	 * @param hm		Map. (fecha_reg, num_ddn, num_telef, nomb_usuario, cod_personal, tip_tel
	 * @return          void
	 * @throws DAOException
	 * */
	public void insertByRtps(Map hm) throws DAOException {
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
		executeUpdate(dataSource, insertByRtps.toString(), new Object[]{hm.get("cod_personal"), hm.get("tip_tel"), hm.get("num_ddn"), 
				 hm.get("num_telef"), fecha_reg.getTimestamp(), hm.get("nomb_usuario")} );
				
	}
}
