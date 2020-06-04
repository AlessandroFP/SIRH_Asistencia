package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       : T1444DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1444devol 
 * Proyecto    : ASISTENCIA 
 * Autor       : JROJAS4
 * Fecha       : 04-DIC-2008 
 * 
 * */

public class T1444DAO extends DAOAbstract {
	
	private DataSource dataSource = null;	
	
	private final StringBuffer acumulaResumenConceptoDescuento = new StringBuffer("SELECT a.period_reg, a.periodo, a.cod_pers, sum(a.total) as suma ")			
	.append("FROM   t1444devol a, t1279tipo_mov m ")
	.append("WHERE  a.period_reg = ? ")
	.append("		and m.califica = 'S' ")
//	.append("		and a.est_id = '1' ")
	.append("		and a.mov = ? ")
	.append("		and a.mov = m.mov ")
	.append("GROUP BY a.period_reg, a.periodo, a.cod_pers");

	private final StringBuffer acumulaResumenLicenciaSinGoce = new StringBuffer("SELECT a.period_reg, a.periodo, a.cod_pers, sum(a.total) as suma ")			
	.append("FROM   t1444devol a, t1279tipo_mov m ")
	.append("WHERE  a.period_reg = ? ")
	.append("		and m.califica = 'S' ")
//	.append("		and a.est_id = '1' ")
	.append("		and m.tipo_id = '03' ")
	.append("		and a.mov != '008' ")
	.append("		and a.mov = m.mov ")
	.append("GROUP BY a.period_reg, a.periodo, a.cod_pers");	
	
	private final StringBuffer acumulaResumenTipoConceptoDescuento = new StringBuffer("SELECT a.period_reg, a.periodo, a.cod_pers, sum(a.total) as suma ")			
	.append("FROM   t1444devol a, t1279tipo_mov m ")
	.append("WHERE  a.period_reg = ? ")
	.append("		and m.califica = 'S' ")
//	.append("		and a.est_id = '1' ")
	.append("		and m.tipo_id = ? ")
	.append("		and a.mov = m.mov ")
	.append("GROUP BY a.period_reg, a.periodo, a.cod_pers");	
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T1444DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	
	/**
	 * Metodo encargado de acumular el resumen mensual por concepto descto
	 * @param params (String periodo, String mov)
	 * @throws DAOException
	 */
	public List findAcumulaResumenConceptoDescuento(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, acumulaResumenConceptoDescuento.toString(), 
				new Object[]{params.get("periodo"), params.get("mov")});
		
		return resultado;
	}
	
	/**
	 * Metodo encargado de acumular el resumen mensual, concepto descto 1113
	 * @param params (String periodo)
	 * @throws DAOException
	 */
	public List findAcumulaResumenLicenciaSinGoce(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, acumulaResumenLicenciaSinGoce.toString(), 
				new Object[]{params.get("periodo")});
		
		return resultado;
	}
	
	/**
	 * Metodo encargado de acumular el resumen mensual por Tipo de concepto descto
	 * @param params (String periodo, String mov)
	 * @throws DAOException
	 */
	public List findAcumulaResumenTipoConceptoDescuento(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, acumulaResumenTipoConceptoDescuento.toString(), 
				new Object[]{params.get("periodo"), params.get("mov")});
		
		return resultado;
	}
	
}
