package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

/** 
 * 
 * Clase       : T1272DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1272asistencia_r 
 * Proyecto    : ASISTENCIA 
 * Autor       : PRAC-JCALLO
 * Fecha       : 09-JUN-2008 
 * 
 * */

public class T1272DAO extends DAOAbstract {
	
	private DataSource dataSource = null;	

	//JRR
	private final StringBuffer findDiasLaborablesByFiniFin =  new StringBuffer("SELECT count(distinct a.fecha) as dias ")
	.append(" from    t1454asistencia_d a, t1279tipo_mov m ")
	.append(" where   a.cod_pers in (?, ?) and ")
	.append("         a.fecha >= ? and ")
	.append("         a.fecha <= ? and ")
	.append("         (not (a.mov = '00' or (m.tipo_id = '03' and m.califica = 'S'))) ")  
	.append("         and a.mov = m.mov"); 
	
	private final StringBuffer findDiasInasistLicDescByFiniFin =  new StringBuffer("SELECT count(distinct a.fecha) as dias ")
	.append(" from    t1454asistencia_d a, t1279tipo_mov m ")
	.append(" where   a.cod_pers in (?, ?) and ")
	.append("         a.fecha >= ? and ")
	.append("         a.fecha <= ? and ")
	.append("         (a.mov = '00' or (m.tipo_id = '03' and m.califica = 'S')) ")  
	.append("         and a.mov = m.mov"); 
	
	private final StringBuffer findDiasEnfermedadByFiniFin =  new StringBuffer("SELECT count(distinct a.fecha) as dias ")
	.append(" from    t1454asistencia_d a, t1279tipo_mov m ")
	.append(" where   a.cod_pers in (?, ?) and ")
	.append("         a.fecha >= ? and ")
	.append("         a.fecha <= ? and ")
	.append("         m.tipo_id = '03' and ")
	.append("         m.mov = '21' and ")
	.append("         a.mov = m.mov ");
	
	//dtarazona findListDiasLaborablesByFiniFfin
	private final StringBuffer findListDiasLaborablesByFiniFfin =  new StringBuffer("SELECT distinct a.fecha ")
	.append(" from    t1454asistencia_d a, t1279tipo_mov m ")
	.append(" where   a.cod_pers in (?, ?) and ")
	.append("         a.fecha >= ? and ")
	.append("         a.fecha <= ? and ")
	.append("         (not (a.mov = '00' or (m.tipo_id = '03' and m.califica = 'S'))) and(not(m.tipo_id = '03' and  m.mov = '21')) ")  
	.append("         and a.mov = m.mov"); 
	
	private final StringBuffer findListDiasDescontablesByFiniFfin =  new StringBuffer("SELECT distinct a.fecha ")
	.append(" from    t1454asistencia_d a, t1279tipo_mov m ")
	.append(" where   a.cod_pers in (?, ?) and ")
	.append("         a.fecha >= ? and ")
	.append("         a.fecha <= ? and ")
	.append("         (a.mov = '00' or (m.tipo_id = '03' and m.califica = 'S')) ")  
	.append("         and a.mov = m.mov"); 
	
	private final StringBuffer findListDiasEnfermedadByFiniFfin =  new StringBuffer("SELECT distinct a.fecha ")
	.append(" from    t1454asistencia_d a, t1279tipo_mov m ")
	.append(" where   a.cod_pers in (?, ?) and ")
	.append("         a.fecha >= ? and ")
	.append("         a.fecha <= ? and ")
	.append("         m.tipo_id = '03' and ")
	.append("         m.mov = '21' and ")
	.append("         a.mov = m.mov ");
	//fin dtarazona
	
	//JRR
	private final StringBuffer acumulaResumenConceptoDescuento = new StringBuffer("SELECT a.periodo, a.cod_pers, sum(a.total) as suma ")			
	.append("FROM   t1272asistencia_r a, t1279tipo_mov m ")
	.append("WHERE  a.periodo = ? ")
	.append("		and m.califica = 'S' ")
	.append("		and a.est_id = '1' ")
	.append("		and a.mov = ? ")
	.append("		and a.mov = m.mov ")
	.append("GROUP BY a.periodo, a.cod_pers");

	//JRR
	private final StringBuffer acumulaResumenLicenciaSinGoce = new StringBuffer("SELECT a.periodo, a.cod_pers, sum(a.total) as suma ")			
	.append("FROM   t1272asistencia_r a, t1279tipo_mov m ")
	.append("WHERE  a.periodo = ? ")
	.append("		and m.califica = 'S' ")
	.append("		and a.est_id = '1' ")
	.append("		and m.tipo_id = '03' ")
	.append("		and a.mov != '008' ")
	.append("		and a.mov = m.mov ")
	.append("GROUP BY a.periodo, a.cod_pers");
	
	//EBV 20/11/2008
	private final StringBuffer acumulaResumenTipoConceptoDescuento = new StringBuffer("SELECT a.periodo, a.cod_pers, sum(a.total) as suma ")			
	.append("FROM   t1272asistencia_r a, t1279tipo_mov m ")
	.append("WHERE  a.periodo = ? ")
	.append("		and m.califica = 'S' ")
	.append("		and a.est_id = '1' ")
	.append("		and m.tipo_id = ? ")
	.append("		and a.mov = m.mov ")
	.append("GROUP BY a.periodo, a.cod_pers");
	
	/**
	 *@param datasource Object
	 *
	 * */
	public T1272DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo encargado de buscar dias laborados por numero de registro y rango de fechas en formato yyyy/MM/dd
	 * @param params (String codPers, String finicio, String ffin)
	 * @throws DAOException
	 */
	public int findDiasLaborablesByFiniFin(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug("T1272DAO findDiasLaborablesByFiniFin - params: "+params);
		if (log.isDebugEnabled()) log.debug("fini: " + new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate());
		if (log.isDebugEnabled()) log.debug("ffin: " + new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate());

		Map resultado = executeQueryUniqueResult(dataSource, findDiasLaborablesByFiniFin.toString(),
			new Object[]{params.get("codPers"), params.get("codPersAnte"), new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate(),
			new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate()}) ;
		
		return (resultado!=null?Integer.parseInt(resultado.get("dias").toString()):0);
	}
	
	//dtarazona
	/**
	 * Metodo encargado de buscar dias laborados por numero de registro y rango de fechas en formato yyyy/MM/dd
	 * @param params (String codPers, String finicio, String ffin)
	 * @throws DAOException
	 */
	public int findDiasInasistLicDescByFiniFin(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug("T1272DAO findDiasInasistLicDescByFiniFin - params: "+params);
		if (log.isDebugEnabled()) log.debug("fini: " + new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate());
		if (log.isDebugEnabled()) log.debug("ffin: " + new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate());

		Map resultado = executeQueryUniqueResult(dataSource, findDiasInasistLicDescByFiniFin.toString(),
			new Object[]{params.get("codPers"), params.get("codPersAnte"), new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate(),
			new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate()}) ;
		
		return (resultado!=null?Integer.parseInt(resultado.get("dias").toString()):0);
	}
	/**
	 * Metodo encargado de acumular el resumen mensual por concepto descto
	 * @param params (String periodo, String mov)
	 * @throws DAOException
	 */
	public List findListDiasLaborablesByFiniFfin(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, findListDiasLaborablesByFiniFfin.toString(), 
				new Object[]{params.get("codPers"), params.get("codPersAnte"), new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate(),
			new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate()});
		
		return resultado;
	}
	/**
	 * Metodo encargado de acumular el resumen mensual por concepto descto
	 * @param params (String periodo, String mov)
	 * @throws DAOException
	 */
	public List findListDiasDescontablesByFiniFfin(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, findListDiasDescontablesByFiniFfin.toString(), 
				new Object[]{params.get("codPers"), params.get("codPersAnte"), new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate(),
			new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate()});
		
		return resultado;
	}
	/**
	 * Metodo encargado de acumular el resumen mensual por concepto descto
	 * @param params (String periodo, String mov)
	 * @throws DAOException
	 */
	public List findListDiasEnfermedadByFiniFfin(Map params) throws DAOException {
		
		List resultado = executeQuery(dataSource, findListDiasEnfermedadByFiniFfin.toString(), 
				new Object[]{params.get("codPers"), params.get("codPersAnte"), new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate(),
            new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate()});
		
		return resultado;
	}
	
	//fin dtarazona
	
	/**
	 * Metodo encargado de buscar dias de licencia por enfermedad por cada trabajador por rango de fechas en formato yyyy/MM/dd
	 * @param params (String codPers, String finicio, String ffin)
	 * @throws DAOException
	 */
	public int findDiasEnfermedadByFiniFin(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug("T1272DAO findDiasEnfermedadByFiniFin - params: "+params);
        if (log.isDebugEnabled()) log.debug("fini: " + new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate());
        if (log.isDebugEnabled()) log.debug("ffin: " + new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate());        

        Map resultado = executeQueryUniqueResult(dataSource, findDiasEnfermedadByFiniFin.toString(),
        	new Object[]{params.get("codPers"), params.get("codPersAnte"), new FechaBean((String)params.get("finicio"),"yyyy/MM/dd").getSQLDate(),
            new FechaBean((String)params.get("ffin"),"yyyy/MM/dd").getSQLDate()});
          //new Object[]{params.get("codPers"), params.get("codPersAnte"), params.get("finicio"), params.get("ffin")});       

        return (resultado!=null?Integer.parseInt(resultado.get("dias").toString()):0);
	}
	
	/***********************************/
	
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
	 * Metodo encargado de acumular el resumen mensual, concepto descto 4032
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
