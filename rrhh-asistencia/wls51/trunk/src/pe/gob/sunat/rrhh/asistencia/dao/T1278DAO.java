package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

/** 
 * 
 * Clase       : T1278DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1278periodo_area 
 * Proyecto    : ASISTENCIA
 * Fecha       : 17-MAR-2010
 * @author JROJAS4 
 * @version 1.0
 * 
 * */

public class T1278DAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	
	private final StringBuffer insertPeriodoArea = new StringBuffer("insert into t1278periodo_area (periodo, u_organ, ")
	.append("est_id, fgraba, cuser) ")
	.append("values (?,?,?,?,?) " ); 
	
	
	/**
	 *Constructor de la clase
	 *@param datasource Object
	 *
	 * */
	public T1278DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	
	/**
	 * Metodo que inserta un periodo area
	 * @param params Map (String codPers,String periodo,String codUO,String fechaMarcacion,String horaMarcacion, String fechaFinMov,String horaFinMov,String calificacion,String tMov,String relojIni,String relojFin,String usuario)
	 * @throws DAOException
	 */
	public boolean insertPeriodoArea(Map params) throws DAOException {
		int result;
		
		result = executeUpdate(dataSource, insertPeriodoArea.toString(), new Object[]{params.get("periodo"), 
			params.get("u_organ"), "1", new FechaBean().getTimestamp(), params.get("cuser")});
		
		return (result>0);
	}
	
	
}
