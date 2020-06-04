package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       : T1274DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1274licen_med 
 * Proyecto    : ASISTENCIA 
 * Autor       : PRAC-JCALLO
 * Fecha       : 11-MAR-2007 
 * 
 * */

public class T1274DAO extends DAOAbstract {
	
	private DataSource dataSource = null;	
		 
	//prac-jcallo
	private final StringBuffer findAllColumnsByKey =  new StringBuffer("SELECT cod_pers, licencia, periodo, anno, numero, ffinicio, ")
	.append(" ffin, nvl(c_certific,'') as c_certific, nvl(cod_cmp,'') as cod_cmp, fecha, nvl(enfermedad,'') as enfermedad, fcreacion, ")
	.append(" cuser_crea, fmod, cuser_mod from t1274licen_med where cod_pers = ? and licencia = ? and periodo = ? and anno = ? ")
	.append(" and numero = ? and ffinicio = ?"); 
	
	private final StringBuffer deleteByPrimaryKey =  new StringBuffer("DELETE  from t1274licen_med  WHERE cod_pers = ? and licencia = ? and periodo = ? and anno = ? and numero = ? and ffinicio = ?");
	private final StringBuffer deleteByReference =  new StringBuffer("DELETE  from t1274licen_med  WHERE cod_pers = ? and licencia = ? and anno = ? and ffinicio = ? ");
	
	//PAS20181U230200023 
	private final StringBuffer insertarLicenciaMed =  new StringBuffer("INSERT INTO t1274licen_med ( cod_pers, licencia, periodo, anno, numero, ffinicio, ")
	.append(" ffin, c_certific, cod_cmp, fecha, enfermedad, num_archivo,cod_cie10, fcreacion, cuser_crea ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?)");
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T1274DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	//PRAC-JCALLO
	/**
	 * Metodo encargado de de buscar un registro dentro de la tabla t1274licen_med por la llave primaria
	 * @param params (String cod_pers, String licencia, String periodo, String anno, Short numero, timeStamp ffinicio)
	 * @throws DAOException
	 */
	public Map findAllColumnsByKey(Map params) throws DAOException {
		
		Map resultado = executeQueryUniqueResult(dataSource, findAllColumnsByKey.toString(), new Object[]{params.get("cod_pers"), params.get("licencia"),
			params.get("periodo"), params.get("anno"), params.get("numero"), params.get("ffinicio")});
		
		return resultado;
	}
	
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro
	 * @param params contiene (Map columns, String cod_pers, String licencia, String periodo, String anno, Short numero, timeStamp ffinicio)
	 * @throws DAOException
	 */
	public boolean updateCustomColumns(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1274licen_med ");
		List listaVal = new ArrayList();
		Map columns = (params.get("columns") != null) ? (HashMap)params.get("columns"): new HashMap();
		
		if(columns != null && !columns.isEmpty()) {
			Iterator it = columns.entrySet().iterator();
			boolean first = true;//para ver si es el primer campo de la sentencia SQL
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				if (first) {
					strSQL.append(" set "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
					first = false;
				} else {
					strSQL.append(", "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
				}				
			}
		}
		
		strSQL.append(" WHERE cod_pers = ? and licencia = ? and periodo = ? and anno = ? and numero = ? and ffinicio = ?");
		if(log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString()); 
		//los datos de la llave primaria
		listaVal.add(params.get("cod_pers"));
		listaVal.add(params.get("licencia"));
		listaVal.add(params.get("periodo"));
		listaVal.add(params.get("anno"));
		listaVal.add(params.get("numero"));
		listaVal.add(params.get("ffinicio"));
		
		int modificado = executeUpdate(dataSource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1274licen_med
	 * @param params (String periodo, Short numero, Timestamp ffinicio, String cod_pers, String licencia)
	 * @throws DAOException
	 */
	public boolean deleteByPrimaryKey(Map params) throws DAOException {
		
		int modificado = executeUpdate(dataSource, deleteByPrimaryKey.toString(), new Object[]{params.get("cod_pers"), 
			params.get("licencia"), params.get("periodo") , params.get("anno"), params.get("numero"), params.get("ffinicio")});
		
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1274licen_med
	 * @param params (Short numero, String cod_pers, String licencia, String anno)
	 * @throws DAOException
	 */
	public boolean deleteByReference(Map params) throws DAOException {

		int modificado = executeUpdate(dataSource, deleteByReference.toString(), new Object[]{params.get("cod_pers"), 
			params.get("licencia"), params.get("anno"), params.get("ffinicio")});
		
		return (modificado>0);
	}	
	
	/**
	 * Metodo encargado de insertar un registro en la tabla t1274licen_med
	 * @param params (String periodo, Short numero, Timestamp ffinicio, String cod_pers, String licencia)
	 * @throws DAOException
	 */
	public boolean insertarLicenciaMed(Map params) throws DAOException {
		//cod_pers, licencia, periodo, anno, numero, ffinicio, ffin, nvl(c_certific,'') as c_certific, nvl(cod_cmp,'') as cod_cmp, fecha, nvl(enfermedad,'') as enfermedad, fcreacion, cuser_crea
		int modificado = executeUpdate(dataSource, insertarLicenciaMed.toString(), new Object[]{params.get("cod_pers"), 
			params.get("licencia"), params.get("periodo") , params.get("anno"), params.get("numero"), params.get("ffinicio"), 
			params.get("ffin"), params.get("c_certific"), params.get("cod_cmp"), params.get("fecha"), params.get("enfermedad"), 
			params.get("num_archivo"),params.get("cod_cie10"),   
			params.get("fcreacion"), params.get("cuser_crea")});
		
		return (modificado>0);
	}

}
