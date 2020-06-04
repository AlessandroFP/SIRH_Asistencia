package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;


/** 
 * 
 * Clase       : T1276DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1276periodo 
 * Proyecto    : ASISTENCIA
 * Fecha       : 02-JUN-2008
 * @author PRAC-JCALLO 
 * @version 1.0
 * 
 * */

public class T1276DAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	
	//JRR - 12/03/2010 ADAPTACIONES PARA CAS
	private final StringBuffer findPeriodoByFecha = new StringBuffer("select periodo, finicio, ffin, fcierre, ")
	.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, ")
	.append( "est_id from t1276periodo where est_id = ? and finicio <= ? and ? <= ffin" ); 
	
	private final StringBuffer updatePeriodo = new StringBuffer("UPDATE t1276periodo ")
	.append("Set finicio = ?, ffin = ?, fcierre = ?, fec_ini_cas = ?, fec_fin_cas = ?, fec_cierre_cas = ?, ")
	//JRR - 01/06/2011
	.append("fec_ini_mf = ?, fec_fin_mf = ?, fec_cierre_mf = ?, ")
	.append("est_id = ?, fgraba = ?, cuser = ? where periodo = ? ");
	
	private final StringBuffer findByPrimaryKey = new StringBuffer("select periodo, finicio, ffin, fcierre, ")
	.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, fec_ini_mf, fec_fin_mf, fec_cierre_mf, est_id ")
	.append("from t1276periodo where periodo = ? " ); 

	//JRR - 01/06/2011	
	private final StringBuffer insertPeriodo = new StringBuffer("insert into t1276periodo (periodo, finicio, ffin, fcierre, ")
	.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, fec_ini_mf, fec_fin_mf, fec_cierre_mf, est_id, fgraba, cuser) ")
	.append("values (?,?,?,?,?,?,?,?,?,?,?,?,?) " );
	
	private final StringBuffer eliminaPeriodo = new StringBuffer("UPDATE t1276periodo ")
	.append("Set est_id = ? where periodo = ? ");

	//JRR - 28/04/2011
	private final StringBuffer FIND_PERIODO_BY_FECHA_CAS = new StringBuffer("select periodo, finicio, ffin, fcierre, ")
	.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, fec_ini_mf, fec_fin_mf, fec_cierre_mf, ")
	.append( "est_id from t1276periodo where est_id = ? and fec_ini_cas <= ? and ? <= fec_fin_cas" ); 
	
	private final StringBuffer FIND_PERIODO_BY_FECHA_MOD_FORMATIVA = new StringBuffer("select periodo, finicio, ffin, fcierre, ")
	.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, fec_ini_mf, fec_fin_mf, fec_cierre_mf, ")
	.append( "est_id from t1276periodo where est_id = ? and fec_ini_mf <= ? and ? <= fec_fin_mf" ); 
	//
	
	/**
	 *Constructor de la clase
	 *@param datasource Object
	 *
	 * */
	public T1276DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	  * Metodo que obtiene el periodo a la que pertenece el parametro fecha 
	  * @param String fecha (formato yyyy/MM/dd)
	  * @return Map periodo
	  * @throws DAOException
	  */
	public Map findPeriodoByFecha(String fecha) throws DAOException {
		Map periodo = null;
		
		periodo = executeQueryUniqueResult(dataSource, findPeriodoByFecha.toString(), new Object[]{"1",fecha,fecha});
		
		return periodo;
	}
	
	
	/* JRR - 05/03/2010 */
	/**
	 * Metodo que actualiza un periodo determinado
	 * @param params Map(String tMov,String fechaFinMov,String horaFinMov,String usuario,String codPers,String periodo,String fechaMarcacion,String horaMarcacion,String calificacion)
	 * @throws DAOException
	 */
	public boolean updatePeriodo(Map params) throws DAOException {
		
		int result = 0;
		FechaBean fb_fechaInicio = new FechaBean(params.get("finicio").toString());
		FechaBean fb_fechaFin = new FechaBean(params.get("ffin").toString());
		//FechaBean fb_fechaCierre = new FechaBean(params.get("fcierre").toString());
		FechaBean fb_fechaInicioCAS = new FechaBean(params.get("fec_ini_cas").toString());
		FechaBean fb_fechaFinCAS = new FechaBean(params.get("fec_fin_cas").toString());
		//FechaBean fb_fechaCierreCAS = new FechaBean(params.get("fec_cierre_cas").toString());
		
		//JRR - 01/06/2011
		FechaBean fb_fechaInicioMF = new FechaBean(params.get("fec_ini_mf").toString());
		FechaBean fb_fechaFinMF = new FechaBean(params.get("fec_fin_mf").toString());
		
		
		String fechaInicio = fb_fechaInicio.getFormatDate("yyyy/MM/dd");
		String fechaFin = fb_fechaFin.getFormatDate("yyyy/MM/dd");
		//String fechaCierre = fb_fechaCierre.getFormatDate("yyyy/MM/dd");
		String fechaInicioCAS = fb_fechaInicioCAS.getFormatDate("yyyy/MM/dd");
		String fechaFinCAS = fb_fechaFinCAS.getFormatDate("yyyy/MM/dd");
		//String fechaCierreCAS = fb_fechaCierreCAS.getFormatDate("yyyy/MM/dd");

		//JRR - 01/06/2011
		String fechaInicioMF = fb_fechaInicioMF.getFormatDate("yyyy/MM/dd");
		String fechaFinMF = fb_fechaFinMF.getFormatDate("yyyy/MM/dd");

		
		result = executeUpdate(dataSource, updatePeriodo.toString(), new Object[]{fechaInicio, 
			fechaFin, params.get("fcierre"), fechaInicioCAS, fechaFinCAS, params.get("fec_cierre_cas"),
			fechaInicioMF, fechaFinMF, params.get("fec_cierre_mf"),
			"1", new FechaBean().getTimestamp(), params.get("cuser"),
			params.get("periodo")});

		return (result>0);
	}
	
	
	/**
	  * Metodo que obtiene los datos de un periodo 
	  * @param String periodo
	  * @return Map mperiodo
	  * @throws DAOException
	  */
	public Map findByPrimaryKey(String periodo) throws DAOException {
		Map mperiodo = null;
		
		mperiodo = executeQueryUniqueResult(dataSource, findByPrimaryKey.toString(), new Object[]{periodo});
		
		return mperiodo;
	}
	
	/**
	 * Metodo que inserta un periodo
	 * @param params Map (String codPers,String periodo,String codUO,String fechaMarcacion,String horaMarcacion, String fechaFinMov,String horaFinMov,String calificacion,String tMov,String relojIni,String relojFin,String usuario)
	 * @throws DAOException
	 */
	public boolean insertPeriodo(Map params) throws DAOException {
		int result;
		
		FechaBean fb_fechaInicio = new FechaBean(params.get("finicio").toString());
		FechaBean fb_fechaFin = new FechaBean(params.get("ffin").toString());
		FechaBean fb_fechaInicioCAS = new FechaBean(params.get("fec_ini_cas").toString());
		FechaBean fb_fechaFinCAS = new FechaBean(params.get("fec_fin_cas").toString());
		//JRR - 01/06/2011
		FechaBean fb_fechaInicioMF = new FechaBean(params.get("fec_ini_mf").toString());
		FechaBean fb_fechaFinMF = new FechaBean(params.get("fec_fin_mf").toString());

		String fechaInicio = fb_fechaInicio.getFormatDate("yyyy/MM/dd");
		String fechaFin = fb_fechaFin.getFormatDate("yyyy/MM/dd");
		String fechaInicioCAS = fb_fechaInicioCAS.getFormatDate("yyyy/MM/dd");
		String fechaFinCAS = fb_fechaFinCAS.getFormatDate("yyyy/MM/dd");

		//JRR - 01/06/2011
		String fechaInicioMF = fb_fechaInicioMF.getFormatDate("yyyy/MM/dd");
		String fechaFinMF = fb_fechaFinMF.getFormatDate("yyyy/MM/dd");
		
		if (log.isDebugEnabled()) log.debug("T1276DAO - insertPeriodo: " + params);
		
		result = executeUpdate(dataSource, insertPeriodo.toString(), new Object[]{params.get("periodo"), 
			fechaInicio, fechaFin, params.get("fcierre"), fechaInicioCAS, fechaFinCAS, params.get("fec_cierre_cas"),
			fechaInicioMF, fechaFinMF, params.get("fec_cierre_mf"),
			"1", new FechaBean().getTimestamp(), params.get("cuser")});
		
		return (result>0);
	}
	
	/**
	 * Metodo que desactiva un periodo determinado
	 * @param params Map(String periodo)
	 * @throws DAOException
	 */
	public boolean eliminaPeriodo(String periodo) throws DAOException {
		
		int result = 0;
		if (log.isDebugEnabled()) log.debug("T1276DAO - eliminaPeriodo: " + periodo);
		result = executeUpdate(dataSource, eliminaPeriodo.toString(), new Object[]{"0", periodo});

		return (result>0);
	}
	
	//JRR - 28/04/2011
	/**
	  * Metodo que obtiene el periodo a la que pertenece el parametro fecha 
	  * @param String fecha (formato yyyy/MM/dd)
	  * @return Map periodo
	  * @throws DAOException
	  */
	public Map findPeriodoByFechaCAS(String fecha) throws DAOException {
		Map periodo = null;
		
		periodo = executeQueryUniqueResult(dataSource, FIND_PERIODO_BY_FECHA_CAS.toString(), new Object[]{"1",fecha,fecha});
		
		return periodo;
	}
	
	/**
	  * Metodo que obtiene el periodo a la que pertenece el parametro fecha 
	  * @param String fecha (formato yyyy/MM/dd)
	  * @return Map periodo
	  * @throws DAOException
	  */
	public Map findPeriodoByFechaModFormativa(String fecha) throws DAOException {
		Map periodo = null;
		
		periodo = executeQueryUniqueResult(dataSource, FIND_PERIODO_BY_FECHA_MOD_FORMATIVA.toString(), new Object[]{"1",fecha,fecha});
		
		return periodo;
	}
	//

	
}
