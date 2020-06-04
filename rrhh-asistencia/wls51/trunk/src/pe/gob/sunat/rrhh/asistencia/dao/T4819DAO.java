package pe.gob.sunat.rrhh.asistencia.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;


/** 
 * 
 * Clase       : T4819DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1489compensacion 
 * Proyecto    : ASISTENCIA 
 * Autor       : EBENAVID
 * Fecha       : 03-ABR-2012 
 * 
 * */

public class T4819DAO extends DAOAbstract {

	
	private DataSource dataSource = null;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");

	
	
	/* EBENAVID - LABOR AUTORIZADA CON SALDO - 03/04/2012 */	
	private final StringBuffer FIND_HORAS_LABOR_AUTORIZADAS_CON_SALDO = new StringBuffer(" select cod_pers, fec_perm, hor_ini_perm, hor_fin_perm, hor_ini_comp, hor_fin_comp, cnt_min_comp_acu, cnt_min_comp_sal from t4819compensacion ")
	  .append(" where cod_pers= ? ")
	  .append(" and ind_comp = '1' ")
	  .append(" and cnt_min_comp_sal > 0 ")
	  //.append(" order by fec_perm"); //ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
	  .append(" and fec_perm>=? ") //ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
	  .append(" order by fec_perm"); //ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
		
	/* EBENAVID - SALDO DE LABOR AUTORIZADA - 03/04/2012 */	
	private final StringBuffer FIND_SALDO_LABOR_AUTORIZADAS = new StringBuffer(" select SUM(cnt_min_comp_sal) cantidad from t4819compensacion ")
	  .append(" where cod_pers= ? ")
	  .append(" and ind_comp = '1' ")
	  .append(" and cnt_min_comp_sal > 0 ")
	  .append(" and fec_perm >= ?  ");
	
	/* EBENAVID - LABOR AUTORIZADA CON SALDO - 03/04/2012 */	
	private final StringBuffer UPDATE_LABOR = new StringBuffer(" UPDATE t4819compensacion set cnt_min_comp_sal = ?, ind_comp= ?, cod_user_mod = ?, fec_modifica = ? ")
	  .append(" where cod_pers= ? ")
	  .append(" and fec_perm = ? ")
	  .append(" and hor_ini_perm = ? ")
	  .append(" and hor_ini_comp = ? ")
	  .append(" and ind_comp = '1' ");
	
	private final StringBuffer UPDATE_REGISTRO_INDICADOR = new StringBuffer(" UPDATE t4819compensacion set ind_comp= ?, cod_user_mod = ?, fec_modifica = ? "+ //jquispecoi 03/2014
	  " where cod_pers= ? "+
	  " and fec_perm = ? "+
	  " and hor_ini_aut = ? "+
	  " and (ind_comp = '1' or ind_comp = '3') ");
	
	//INICIO ICAPUNAY
	private final StringBuffer FIND_FECHA_PERMANENCIA_EXTRA = new StringBuffer("select distinct fec_perm,cod_user_crea from t4819compensacion "	
	).append("where cod_pers=? and fec_perm=? ");	

	private final StringBuffer DELETE_INTERVALOS_COMPENSACIONES= new StringBuffer("delete from t4819compensacion "	
	).append("where cod_pers=? and fec_perm=? "
	).append("and cnt_min_comp_acu=cnt_min_comp_sal ");		
	
	//ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	private final StringBuffer UPDATE_SALDOS = new StringBuffer(" UPDATE t4819compensacion set cnt_min_comp_sal = ?, cod_user_mod = ?, fec_modifica = ? ")
	  .append(" where cod_pers= ? ")
	  .append(" and fec_perm = ? ")
	  .append(" and cnt_min_comp_acu<>cnt_min_comp_sal ");
		
	private final StringBuffer DELETE_INTERVALO_COMPENSACION= new StringBuffer("delete from t4819compensacion "	
	).append("where cod_pers=? and fec_perm=? "
	).append("and hor_ini_comp=? and hor_fin_comp=? ");
	//FIN ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100

	private final StringBuffer INSERTAR_INTERVALO_COMPENSACION = new StringBuffer("insert into t4819compensacion "
	).append("(cod_pers,fec_perm,hor_ini_perm,hor_fin_perm,cod_uorga,hor_ini_aut,cod_jefe_aut,"
	).append("hor_ini_comp,hor_fin_comp,cnt_min_comp_acu,ind_comp,cnt_min_comp_sal,obs_sustento_aut,cod_user_crea,fec_creacion) "
	).append("values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");

	private final StringBuffer FIND_INTERVALOS_CON_ACUMULADO_DIFERENTE_SALDO = new StringBuffer("select c.* from t4819compensacion c "
	).append("where c.cod_pers=? and c.fec_perm=? "	
	).append("and c.cnt_min_comp_acu<>c.cnt_min_comp_sal ");	
	//FIN ICAPUNAY
	
	//ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	private final StringBuffer FIND_RANGOS_ACUMULADO_DIFERENTE_SALDO = new StringBuffer("select first 1 c.* from t4819compensacion c "
	).append("where c.cod_pers=? and c.fec_perm=? "	
	).append("and ((? < hor_ini_comp or ? < hor_fin_comp) "	
	).append("and (? > hor_ini_comp or ? > hor_fin_comp)) "	
	).append("and c.cnt_min_comp_acu<>c.cnt_min_comp_sal ");
	
	//private final StringBuffer FIND_RANGOS_ACUMULADO_IGUAL_SALDO = new StringBuffer("select first 1 c.* from t4819compensacion c "
	private final StringBuffer FIND_RANGOS_ACUMULADO_IGUAL_SALDO = new StringBuffer("select c.* from t4819compensacion c "
	).append("where c.cod_pers=? and c.fec_perm=? "	
	).append("and ((? < hor_ini_comp or ? < hor_fin_comp) "	
	).append("and (? > hor_ini_comp or ? > hor_fin_comp)) "	
	).append("and c.cnt_min_comp_acu=c.cnt_min_comp_sal ");
	//ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	
	//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	private final StringBuffer FIND_PERMANENCIA_BY_PK = new StringBuffer("select * from t4819compensacion "	
	).append("where cod_pers=? and fec_perm=? "
	).append("and hor_ini_perm=? and hor_ini_comp=? ");
	//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL

	private final StringBuffer FIND_PERMANENCIA_BY_FEC_HOR = new StringBuffer("select * from t4819compensacion "+	
	"where cod_pers=? and fec_perm=? "+
	"and hor_ini_aut=? "+
	"and (ind_comp='1' or ind_comp='3') ");
	
	//NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
	private final StringBuffer FIND_HORAS_AUT_NOAUT =		
		new StringBuffer(" select cod_uorga,t12des_corta,cod_pers, cod_uorga||' - '||t12des_corta AS unidad, cod_pers||' - '||t02nombres||t02ap_pate||t02ap_mate AS colaborador, ")		
		.append(" SUM(CASE WHEN ind_comp='1' AND cnt_min_comp_acu - cnt_min_comp_sal = 0 THEN cnt_min_comp_acu WHEN ind_comp='1' AND cnt_min_comp_acu - cnt_min_comp_sal > 0 THEN cnt_min_comp_sal ELSE 0 END) AS autorizada, ") 
		.append(" SUM(CASE WHEN ind_comp='4' THEN cnt_min_comp_acu ELSE 0 END) AS noautorizada, ")
		.append(" SUM(CASE WHEN ind_comp='0' THEN cnt_min_comp_acu ELSE 0 END) AS rechazada, ")
		.append(" SUM(CASE WHEN ind_comp in ('3','1') AND cnt_min_comp_acu - cnt_min_comp_sal > 0  THEN (cnt_min_comp_acu - cnt_min_comp_sal) ELSE 0 END) AS compensada ")
		.append(" FROM t4819compensacion, t02perdp, t12uorga ")
		.append(" WHERE cod_pers = t02cod_pers ")
		.append(" AND cod_uorga= t12cod_uorga ")
		//.append(" AND cod_pers = '1548' ")
		//.append(" AND t02cod_rel = ? ")
		//.append(" AND fec_perm > '2012-01-01' ")
		.append(" AND fec_perm between ? AND ? ");
	//FIN - NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
	
	//NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL---DEC
	private final StringBuffer FIND_PERMANENCIAS2 =		
		new StringBuffer(" SELECT fec_perm AS fecha, cod_uorga AS unidad, ") 
		.append(" cnt_min_comp_acu AS minutos_acu, cnt_min_comp_sal AS minutos_sal, ")	
		.append(" cod_uorga||' - '||t12des_corta AS des_unidad, ")
		.append(" cod_pers AS registro,TRIM(t02nombres)||','||TRIM(t02ap_pate)||' '||TRIM(t02ap_mate) AS nombre, hor_ini_comp[1,5] AS inicio,  ") 
		.append(" hor_fin_comp[1,5] AS fin, hor_ini_aut AS autorizada, ind_comp AS indicador,  ")
		.append(" CASE WHEN ind_comp='4' THEN 'No autorizada' WHEN ind_comp='1' THEN 'Autorizada' WHEN ind_comp='3' THEN 'Compensada' ELSE 'Rechazada' END AS estado ") 
		.append(" FROM t4819Compensacion, t02perdp, t12uorga ") 
		.append(" WHERE t02cod_pers =  cod_pers ")
		.append(" AND  cod_uorga = t12cod_uorga ");
	//.append(" AND ind_comp='1' ");
	//FIN - NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	
	
	//NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	private final StringBuffer FIND_PERMANENCIAS3 =		
		new StringBuffer(" SELECT COUNT(DISTINCT cod_pers) AS cant_registro ")		
	.append(" FROM t4819Compensacion, t02perdp, t12uorga ")         
	.append(" WHERE t02cod_pers =  cod_pers ")        
	.append(" AND  cod_uorga = t12cod_uorga "); 
	//.append(" AND ind_comp='1' ");
	//FIN - NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	
	//NVILLAR - 10/04/2012 - LABOR EXCEPCIONAL
	private final StringBuffer FIND_PERMANENCIAS =
		//new StringBuffer(" SELECT fec_perm AS fecha, cod_pers AS registro,t02nombres||t02ap_pate||t02ap_mate AS nombre, hor_ini_comp[1,5] AS inicio, hor_fin_comp[1,5] AS fin, hor_ini_aut AS autorizada, hor_ini_aut[1,5] AS hor_autorizada") //ICAPUNAY 19/06 AGREGANDO SALDO
		new StringBuffer(" SELECT fec_perm AS fecha, cod_pers AS registro,t02nombres||t02ap_pate||t02ap_mate AS nombre, hor_ini_comp[1,5] AS inicio, hor_fin_comp[1,5] AS fin, hor_ini_aut AS autorizada, hor_ini_aut[1,5] AS hor_autorizada, cnt_min_comp_sal AS saldo ") //ICAPUNAY 19/06 AGREGANDO SALDO	
	.append(" FROM t4819Compensacion, t02perdp ") 
	.append(" WHERE t02cod_pers =  cod_pers ")
	.append(" AND ind_comp='1' ");
    //FIN - NVILLAR - 10/04/2012 - LABOR EXCEPCIONAL
	
	//NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL
	private final StringBuffer FIND_DETALLE_PERMANENCIA =		
		//new StringBuffer(" SELECT x.ann_sol||' - '|| x.num_sol AS solicitud ,z.accion_id AS estado, x.hor_ini_permiso[1,5] AS hinicio, x.hor_fin_permiso[1,5] AS hfin, y.cnt_min_comp_sal AS minutos ") //ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA		
	new StringBuffer(" SELECT distinct x.ann_sol||' - '|| x.num_sol AS solicitud ,z.accion_id AS estado, x.hor_ini_permiso[1,5] AS hinicio, x.hor_fin_permiso[1,5] AS hfin ") //ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA	
	.append(" FROM t4821solicituddet x, t4819compensacion y, t1455sol_seg z ") 
	.append(" WHERE x.cod_pers_sol = y.cod_pers ")
	.append(" AND x.fec_permiso = y.fec_perm ")
	.append(" AND x.hor_ini_permiso = y.hor_ini_aut ")
	.append(" AND y.cod_pers = ? ")
	.append(" AND y.ind_comp='1' ")
	.append(" AND x.ann_sol = z.anno ")
	.append(" AND x.num_sol = z.numero ")
	.append(" AND x.cod_pers_sol = z.cod_pers ")
	.append(" AND y.cod_pers = z.cod_pers ")
	.append(" AND z.accion_id not in ('3', '1')  ") 
	.append(" AND z.num_seguim = (SELECT max(z1.num_seguim) ")
	.append(" FROM t4821solicituddet x1, t4819compensacion y1, t1455sol_seg z1 ")
	.append(" WHERE x1.cod_pers_sol = y1.cod_pers ")
	.append(" AND x1.fec_permiso = y1.fec_perm ")
	.append(" AND x1.hor_ini_permiso = y1.hor_ini_aut ")
	.append(" AND y1.cod_pers = ? ")
	.append(" AND x1.ann_sol = z1.anno ")
	.append(" AND x1.num_sol = z1.numero ")
	.append(" AND x1.cod_pers_sol = z1.cod_pers ")
	.append(" AND y1.cod_pers = z1.cod_pers ")
    .append(" AND y1.fec_perm = ? ")
    .append(" AND y1.hor_ini_aut = ? ")        
    .append(" AND y1.ind_comp='1' ")        
    .append(" AND z1.accion_id not in ('3', '1') ) ");  

	private final String FIND_ACUMULADO_BY_RANGO = 		//jquispecoi 05/2014
			"Select sum(cnt_min_comp_acu) cantidad " +
			"from t4819Compensacion " +
			"where cod_pers = ? " +
			"and ind_comp in ('1','2','3') " +
			"and fec_perm between ? and ? " ;
			
	/**
	 *@param datasource Object
	 *
	 * */
	public T4819DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	  * Metodo que obtiene los datos a partir del registro 
	  * @param datos (String cod_pers)
	  * @return
	  * @throws DAOException
	  */
	public List findHorasLaborAutorizadasConSaldo(Map datos) throws DAOException {
		if(log.isDebugEnabled()) log.debug("method: findHorasLaborAutorizadasConSaldo");
		
		List soli;
		try{
			soli = executeQuery(dataSource, FIND_HORAS_LABOR_AUTORIZADAS_CON_SALDO.toString(), new Object[]{ 
			datos.get("cod_pers"), datos.get("fecha_ingreso")} ); //ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4819DAO. [findHorasLaborAutorizadasConSaldo]");
		}	
			
		return soli;
	}
	

		
	/**
	 * Metodo que obtiene el saldo de labor autorizada de un trabajador 
	 * @return List
	 * @throws Exception 
	 */	
	public Integer findSaldoLaborAutorizadas(Map datos) throws Exception {	
		
		Integer cantidad = new Integer("0");
		
			if (log.isDebugEnabled()) log.debug("T4819DAO findSaldoLaborAutorizadas");
			Map rs = new HashMap();
			String cadena = "0";
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			rs = executeQueryUniqueResult(dataSource, FIND_SALDO_LABOR_AUTORIZADAS.toString(), new Object[]{
				datos.get("cod_pers"), datos.get("fecha_ingreso")});	//ICR 08/05/2015-PAS20155E230000154-labor antigua  (autorizacion y compensacion) y labor nueva (sol. compensacion) mayor o igual fecha ingreso a sunat
			if (rs!=null)
			{
				 if (rs.size()>0)
				 {
					 cadena = rs.get("cantidad")!=null?rs.get("cantidad").toString().trim():"0";
				 }
				cantidad = new Integer(cadena);
			 
			}

			return cantidad;
		
	}
	
	/**
	  * Metodo que obtiene los datos a partir del registro 
	  * @param datos (String cod_pers)
	  * @return
	  * @throws DAOException
	  */
	public boolean updateLabor(Map datos) throws DAOException {
		
		FechaBean fecha1 = new FechaBean();
		boolean result = false;
		if (log.isDebugEnabled()) log.debug("T4819DAO - updateLabor - datos: " + datos);		
		try{
			int resultado = executeUpdate(dataSource, UPDATE_LABOR.toString(), new Object[]{ new Integer(datos.get("tempnum").toString()),
						datos.get("indicador").toString(),
						datos.get("usuario"),fecha1.getTimestamp(),
						datos.get("cod_pers"), datos.get("fec_perm"), datos.get("hor_ini_perm"), datos.get("hor_ini_comp")} );
			result = (resultado >0);
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4819DAO. [UPDATE_LABOR]");
		}	
		return result;
	}
	
	public boolean updateRegistroLaborRechazar(Map datos)	//jquispecoi 03/2014
			throws SQLException {
		FechaBean fecha1 = new FechaBean();
		int modificado = 0;
		try{			
			modificado = executeUpdate(dataSource, UPDATE_REGISTRO_INDICADOR.toString(),
				new Object[] {
				datos.get("ind_comp").toString(),
				datos.get("cod_user_mod"),
				new java.sql.Timestamp(System.currentTimeMillis()),
				datos.get("cod_pers"),
				new FechaBean((String)datos.get("fec_aut")).getSQLDate(),
				datos.get("hor_ini_aut")
				});
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [UPDATE_REGISTRO_INDICADOR]");
		}	
			
		return (modificado > 0);
	}

	
	//INICIO ICAPUNAY
	/**
	  * Metodo que busca si existe una fecha de permanencia extra para un colaborador	
	  * @param codPers String
	  * @param fechaPermanencia String	
	  * @return mfechaPermanencia Map
	  * @throws DAOException
	  */
	public Map findFechaPermanenciaExtra(String codPers, String fechaPermanencia) throws DAOException {
		
		
		Map mfechaPermanencia = null;
		if (log.isDebugEnabled()) log.debug("T4819DAO - findFechaPermanenciaExtra - codPers: " + codPers + " - fechaPermanencia: " + fechaPermanencia);		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mfechaPermanencia = executeQueryUniqueResult(dataSource, FIND_FECHA_PERMANENCIA_EXTRA.toString(), new Object[]{codPers,
				new FechaBean(fechaPermanencia).getSQLDate()});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_FECHA_PERMANENCIA_EXTRA]");
		}	
		return mfechaPermanencia;
	}
	
	/**
	 * Metodo encargado de eliminar(fisico) una categoria a partir de la llave cod_cate
	 * @param codPers String
	 * @param fechaPermanencia String
	 * @return eliminado boolean
	 * @throws DAOException
	 */
	public boolean deleteAllIntervalosCompensacionesByCodPersByFecPerm(String codPers,String fechaPermanencia) throws DAOException {
		
		int eliminados=0;
		if (log.isDebugEnabled()) log.debug("T4819DAO - deleteAllIntervalosCompensacionesByCodPersByFecPerm - codPers: " + codPers);
		if (log.isDebugEnabled()) log.debug("T4819DAO - deleteAllIntervalosCompensacionesByCodPersByFecPerm - fechaPermanencia: " + fechaPermanencia);
		try{
					
			eliminados = executeUpdate(dataSource,DELETE_INTERVALOS_COMPENSACIONES.toString(), new Object[]{codPers,new FechaBean(fechaPermanencia).getSQLDate()});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [DELETE_INTERVALOS_COMPENSACIONES]");
		}		
		return (eliminados>0);
	}
	
	
	//ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	/**
	  * Metodo que actualiza saldos de labor a los rangos de labor donde lo acumulado sea diferente al saldo 
	  * @param datos Map
	  * @return result boolean
	  * @throws DAOException
	  */
	public boolean updateSaldos(Map datos) throws DAOException {
		
		FechaBean fecha1 = new FechaBean();
		boolean result = false;
		if (log.isDebugEnabled()) log.debug("T4819DAO - updateSaldos - datos: " + datos);		
		try{
			int resultado = executeUpdate(dataSource, UPDATE_SALDOS.toString(), new Object[]{ new Integer(datos.get("saldo").toString()),
						datos.get("usuario_mod"),fecha1.getTimestamp(),
						datos.get("cod_pers"), new FechaBean(datos.get("fec_perm").toString()).getSQLDate()} );
			result = (resultado >0);
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T4819DAO. [UPDATE_SALDOS]");
		}	
		return result;
	}
	
	
	/**
	 * Metodo encargado de eliminar(fisico) un rango de compensacion de una fecha
	 * @param codPers String
	 * @param fechaPermanencia String
	 * @param horIniComp String
	 * @param horFinComp String
	 * @return eliminado boolean
	 * @throws DAOException
	 */
	public boolean deleteIntervaloCompensacion(String codPers,String fechaPermanencia,String horIniComp, String horFinComp) throws DAOException {
		
		int eliminado=0;
		if (log.isDebugEnabled()) log.debug("T4819DAO - deleteIntervaloCompensacion - codPers: " + codPers);
		if (log.isDebugEnabled()) log.debug("T4819DAO - deleteIntervaloCompensacion - fechaPermanencia: " + fechaPermanencia);
		if (log.isDebugEnabled()) log.debug("T4819DAO - deleteIntervaloCompensacion - horIniComp: " + horIniComp);
		if (log.isDebugEnabled()) log.debug("T4819DAO - deleteIntervaloCompensacion - horFinComp: " + horFinComp);
		try{
					
			eliminado = executeUpdate(dataSource,DELETE_INTERVALO_COMPENSACION.toString(), new Object[]{codPers,new FechaBean(fechaPermanencia).getSQLDate(),horIniComp,horFinComp});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [deleteIntervaloCompensacion-DELETE_INTERVALO_COMPENSACION]");
		}		
		return (eliminado>0);
	}
	//FIN ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
		
	/**
	 * Metodo que se encarga de registrar un intervalo de compensacion autorizado, no autorizado o rechazado
	 * @param params Map (cod_pers,fec_perm,hor_ini_perm,hor_fin_perm,cod_uorga,hor_ini_aut,cod_jefe_aut,hor_ini_comp,hor_fin_comp,cnt_min_comp_acu,ind_comp,cnt_min_comp_sal,cod_user_crea,fec_creacion)  
	 * @return insertado boolean	 
	 * @throws DAOException
	 */	
	public boolean insertIntervaloCompensacion(Map params) throws DAOException {
	
		int insertado=0;
		if (log.isDebugEnabled()) log.debug("T4819DAO - insertRangoCompensacion - params " + params);		
		
		try{
			insertado = executeUpdate(dataSource,INSERTAR_INTERVALO_COMPENSACION.toString(), new Object[]{params.get("cod_pers"), 
				params.get("fec_perm"),params.get("hor_ini_perm"),params.get("hor_fin_perm"),params.get("cod_uorga"),params.get("hor_ini_aut"),
				params.get("cod_jefe_aut"),params.get("hor_ini_comp"),params.get("hor_fin_comp"),params.get("cnt_min_comp_acu"),params.get("ind_comp"),
				params.get("cnt_min_comp_sal"),params.get("obs_sustento_aut"),params.get("cod_user_crea"),params.get("fec_creacion")});
		} catch (Exception e) {
			insertado=0;
			log.error("*** SQL Error ****", e);			
			throw new DAOException(this, "Error en consulta. [INSERTAR_INTERVALO_COMPENSACION]-mapa: "+params);
		
		}		
		return (insertado>0);
	}	
	
	/**
	  * Metodo que devuelve intervalos de compensacion para una fecha de permanencia donde lo acumulado es diferente al saldo actual (ya usado totalmente o usado parcialmente con saldo)
	  * @param codPers String
	  * @param fechaPermanencia String
	  * @return intervalos List
	  * @throws DAOException
	  */
	public List findByCodPersByFecPermAcumDiferenteActual(String codPers,Date fechaPermanencia) throws DAOException {
		
		List intervalos= null;
		if (log.isDebugEnabled()) log.debug("T4819DAO - findByCodPersByFecPermAcumDiferenteActual - codPers: " + codPers);
		if (log.isDebugEnabled()) log.debug("T4819DAO - findByCodPersByFecPermAcumDiferenteActual - fechaPermanencia: " + fechaPermanencia);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			intervalos = executeQuery(dataSource,FIND_INTERVALOS_CON_ACUMULADO_DIFERENTE_SALDO.toString(),new Object[]{codPers,fechaPermanencia});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_INTERVALOS_CON_ACUMULADO_DIFERENTE_SALDO]");
		}		
		return intervalos;
	}	
	//FIN ICAPUNAY
	
	
	/** ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	  * Metodo que devuelve intervalos de compensacion para una fecha de permanencia y un rango de marcaciones donde lo acumulado es diferente al saldo actual (ya usado totalmente o usado parcialmente con saldo)
	  * @param codPers String
	  * @param fechaPermanencia String
	  * @param horIniPerm String
	  * @param horFinPerm String
	  * @return intervalo List
	  * @throws DAOException
	  */
	public List findAcumDiferenteActual_ByRangos(String codPers,Date fechaPermanencia, String horIniPerm, String horFinPerm) throws DAOException {
		
		List intervalo= null;
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumDiferenteActual_ByRangos - codPers: " + codPers);
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumDiferenteActual_ByRangos - fechaPermanencia: " + fechaPermanencia);
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumDiferenteActual_ByRangos - horIniPerm: " + horIniPerm);
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumDiferenteActual_ByRangos - horFinPerm: " + horFinPerm);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			intervalo = executeQuery(dataSource,FIND_RANGOS_ACUMULADO_DIFERENTE_SALDO.toString(),new Object[]{codPers,fechaPermanencia,horIniPerm,horIniPerm,horFinPerm,horFinPerm});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findAcumDiferenteActual_ByRangos(FIND_RANGOS_ACUMULADO_DIFERENTE_SALDO)]");
		}		
		return intervalo;
	}	
	
	/** ICAPUNAY-PAS20144EB20000144-Optimizacion Labor Excepcional-Memo 31-2013-4F3100
	  * Metodo que devuelve intervalos de compensacion para una fecha de permanencia y un rango de marcaciones donde lo acumulado es igual al saldo actual (ya usado totalmente o usado parcialmente con saldo)
	  * @param codPers String
	  * @param fechaPermanencia String
	  * @param horIniPerm String
	  * @param horFinPerm String
	  * @return intervalo List
	  * @throws DAOException
	  */
	public List findAcumIgualActual_ByRangos(String codPers,Date fechaPermanencia, String horIniPerm, String horFinPerm) throws DAOException {
		
		List intervalo= null;
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumIgualActual_ByRangos - codPers: " + codPers);
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumIgualActual_ByRangos - fechaPermanencia: " + fechaPermanencia);
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumIgualActual_ByRangos - horIniPerm: " + horIniPerm);
		if (log.isDebugEnabled()) log.debug("T4819DAO - findAcumIgualActual_ByRangos - horFinPerm: " + horFinPerm);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			intervalo = executeQuery(dataSource,FIND_RANGOS_ACUMULADO_IGUAL_SALDO.toString(),new Object[]{codPers,fechaPermanencia,horIniPerm,horIniPerm,horFinPerm,horFinPerm});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findAcumIgualActual_ByRangos(FIND_RANGOS_ACUMULADO_IGUAL_SALDO)]");
		}		
		return intervalo;
	}	
	//FIN ICAPUNAY
	
	/** NVILLAR - 23/03/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar las marcaciones realizadas por un trabajador
	 * dentro de un rango de fechas y filtradas por hora de marcacion Join de
	 * las tablas T1275Marcacion y T1280Tipo_Reloj.
	 * @throws SQLException
	 */
	public ArrayList findPermanencias(Map params) 
	throws SQLException {

		StringBuffer strSQL = new StringBuffer(FIND_PERMANENCIAS.toString());
		PreparedStatement pre = null;
		Connection con = null;
		//ResultSet rs = null;
		ArrayList lista = null;

		try {
			String dbpool = (params.get("dbpool") != null ) ? params.get("dbpool").toString():"";
			String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():""; //Ejm. 2012
			String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():""; //Ejm. 320
			String fechaFin = (params.get("fechaFin") != null ) ? params.get("fechaFin").toString():""; //licencia. 1=Iniciada, 2=Aprobada, 3=Rechazada
			String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
			String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";
			String codPers = (params.get("codPers") != null ) ? params.get("codPers").toString():"";

			if (log.isDebugEnabled()) log.debug("regimen: " + params.get("regimen"));
			if (log.isDebugEnabled()) log.debug("fechaIni: " + params.get("fechaIni"));
			if (log.isDebugEnabled()) log.debug("fechaFin: " + params.get("fechaFin"));
			if (log.isDebugEnabled()) log.debug("criterio: " + params.get("criterio"));
			if (log.isDebugEnabled()) log.debug("valor: " + params.get("valor"));
			if (log.isDebugEnabled()) log.debug("codPers: " + params.get("codPers"));
			if (log.isDebugEnabled()) log.debug("strSQL: " + strSQL);

			Map seguridad = (HashMap)params.get("seguridad");
			
			//tipo de Regimen/Modalidad
			if (regimen.equals("0")){ //Regimen 276 - 728
                strSQL.append("and t02cod_rel not in('09','10') ");                
			}else if (regimen.equals("1")){ //Regimen 1057
                strSQL.append("and t02cod_rel = '09' ");
			}else if (regimen.equals("2")){ //Modalidad Formativa
                strSQL.append("and t02cod_rel = '10' ");
			}
			
			//rango de fechas de la consulta
			//sSQL.append("AND date(s.fec_permiso) between '").append( fechIni).append( "' and '").append( fechFin).append("' ");
			if (!fechaIni.equals("")) {
				strSQL.append(" AND fec_perm >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("') ");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and fec_perm <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("') ");
			}
			
			//criterios de consulta
			if(criterio.trim().equals("0")){ // la opcion escogida es REGISTRO
				strSQL.append(" AND cod_pers = '").append(valor.trim()).append("' ");
			}
			if(criterio.trim().equals("1")){ // la opcion escogida es UND. ORGANIZACIONAL
				strSQL.append(" AND cod_uorga = '").append(valor.trim()).append("' ");
			}
			if(criterio.trim().equals("5")){ // la opcion escogida es INTENDENCIA
				//criterio = "Intendencia";
			}
			if(criterio.trim().equals("4")){ // la opcion escogida es INSTITUCIONAL
				//criterio = "Institucional";
			}
			
     		//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {
				Map roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPerso = (String) seguridad.get("codPers");
				log.debug("findDetalleSolicitudLE_Seguridad / roles: " + roles);
				log.debug("findDetalleSolicitudLE_Seguridad / uoSeg: " + uoSeg);
				log.debug("findDetalleSolicitudLE_Seguridad / codSeg: " + codPerso);

				if (roles.get(Constantes.ROL_JEFE) != null) { //Visualiza solo a los de la UO a la cual pertenece el usuario logueado

					//NVS para que rol_jefe pueda visualizar los colaboradores de su area
					if (log.isDebugEnabled()) log.debug("uoSeg: "+uoSeg);

					if (roles.get(Constantes.ROL_JEFE)!=null){
						if (log.isDebugEnabled()) log.debug("uoSeg inicial de ROL_JEFE: "+uoSeg);
						if (log.isDebugEnabled()) log.debug("uoSeg.length(): "+uoSeg.length());

						for(int p = uoSeg.length()-1; p == 0; p--){
							String letra = uoSeg.substring(p,p+1).trim();
							if(!letra.equals("0")){
								uoSeg = uoSeg.substring(0,p+1).concat("%");
								p = 0;
							}
						}						
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_JEFE: "+uoSeg);
					}
					log.debug(" AND cod_uorga LIKE '"+uoSeg+"'");
					//NVS para que rol_jefe pueda visualizar los colaboradores de su area

					strSQL.append(" AND cod_uorga LIKE '").append(uoSeg).append( "' ");

				} else { //No visualiza ninguno
					strSQL.append(" AND 1=2 ");					
					log.debug(" AND 1=2 ");
				}
			}
			
			strSQL.append(" ORDER BY 1,2,4 ");
			
			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
			lista = (ArrayList) executeQuery(dataSource, strSQL.toString());
			setIsolationLevel(-1);

		} catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
			throw new SQLException(e.toString());
		} 
		return lista;
	}//FIN - NVILLAR - 23/03/2012 - LABOR EXCEPCIONAL
	
	
	/** NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar las marcaciones realizadas por un trabajador
	 * dentro de un rango de fechas y filtradas por hora de marcacion Join de
	 * las tablas T1275Marcacion y T1280Tipo_Reloj.
	 * @throws SQLException
	 */
	public Map findPermanencias2(Map params) 
	throws SQLException {

		StringBuffer strSQL = new StringBuffer(FIND_PERMANENCIAS2.toString());
		StringBuffer strSQL_Cont = new StringBuffer(FIND_PERMANENCIAS3.toString());
		PreparedStatement pre = null;
		Connection con = null;
		//ResultSet rs = null;
		Map mapaListas = new HashMap();

		try {
			
			ArrayList lista = null;
			ArrayList lista2 = null;
			String dbpool = (params.get("dbpool") != null ) ? params.get("dbpool").toString():"";
			String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():""; //Ejm. 2012
			String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():""; //Ejm. 320
			String fechaFin = (params.get("fechaFin") != null ) ? params.get("fechaFin").toString():""; //licencia. 1=Iniciada, 2=Aprobada, 3=Rechazada
			String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
			String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";
			String codPers = (params.get("codPers") != null ) ? params.get("codPers").toString():"";
			String solicitud1 = (params.get("solicitud1") != null ) ? params.get("solicitud1").toString():"";
			String solicitud2 = (params.get("solicitud2") != null ) ? params.get("solicitud2").toString():"";
			String solicitud3 = (params.get("solicitud3") != null ) ? params.get("solicitud3").toString():"";

			if (log.isDebugEnabled()) log.debug("regimen: " + params.get("regimen"));
			if (log.isDebugEnabled()) log.debug("fechaIni: " + params.get("fechaIni"));
			if (log.isDebugEnabled()) log.debug("fechaFin: " + params.get("fechaFin"));
			if (log.isDebugEnabled()) log.debug("criterio: " + params.get("criterio"));
			if (log.isDebugEnabled()) log.debug("valor: " + params.get("valor"));
			if (log.isDebugEnabled()) log.debug("codPers: " + params.get("codPers"));
			if (log.isDebugEnabled()) log.debug("strSQL: " + strSQL);
			if (log.isDebugEnabled()) log.debug("strSQL_Cont: " + strSQL_Cont);
			if (log.isDebugEnabled()) log.debug("solicitud1: " + solicitud1);
			if (log.isDebugEnabled()) log.debug("solicitud2: " + solicitud2);
			if (log.isDebugEnabled()) log.debug("solicitud3: " + solicitud3);

			Date fechIni = new Date();
			Date fechFin = new Date();
			fechIni = new FechaBean(fechaIni).getSQLDate();
			fechFin = new FechaBean(fechaFin).getSQLDate();
			
			Map seguridad = (HashMap)params.get("seguridad");
			
			
			//tipo de Regimen/Modalidad
			if (regimen.equals("0")){ //Regimen 276 - 728
                strSQL.append("and t02cod_rel not in('09','10') ");
                strSQL_Cont.append("and t02cod_rel not in('09','10') ");
			}else if (regimen.equals("1")){ //Regimen 1057
                strSQL.append("and t02cod_rel = '09' ");
                strSQL_Cont.append("and t02cod_rel = '09' ");
			}else if (regimen.equals("2")){ //Modalidad Formativa
                strSQL.append("and t02cod_rel = '10' ");
                strSQL_Cont.append("and t02cod_rel = '10' ");
			}

			
			//tipo de solicitud
			if (solicitud3.equals("0"))
			{
				if (solicitud1.equals("1") && solicitud2.equals("0")){
					strSQL.append(" and ind_comp = '1' ");
					strSQL_Cont.append(" and ind_comp = '1' ");
				}

				if (solicitud1.equals("0") && solicitud2.equals("1")){
					strSQL.append(" and ind_comp = '4' ");
					strSQL_Cont.append(" and ind_comp = '4' ");
				}

				if (solicitud1.equals("1") && solicitud2.equals("1")){
					strSQL.append(" and ind_comp in ('1', '4') ");
					strSQL_Cont.append(" and ind_comp in ('1', '4') ");
				}
				
				if (solicitud1.equals("0") && solicitud2.equals("0")){
					//strSQL.append(" and ind_comp in ('3', '1', '0') ");
					//strSQL_Cont.append(" and ind_comp in ('3', '1', '0') ");
					}

			} else {

				if (solicitud1.equals("1") && solicitud2.equals("0")){
					strSQL.append(" and ind_comp in ('3', '1') ");
					strSQL_Cont.append(" and ind_comp in ('3', '1') ");
				}
				

				if (solicitud1.equals("0") && solicitud2.equals("1")){
				
					strSQL.append(" and ( (cnt_min_comp_acu > cnt_min_comp_sal and ind_comp in ('3', '1')) or (cnt_min_comp_acu=cnt_min_comp_sal and ind_comp='4') ) ");        
					strSQL_Cont.append(" and ( (cnt_min_comp_acu > cnt_min_comp_sal and ind_comp in ('3', '1')) or (cnt_min_comp_acu=cnt_min_comp_sal and ind_comp='4') ) "); 

				}

				if (solicitud1.equals("1") && solicitud2.equals("1")){
					strSQL.append(" and ind_comp in ('3', '1', '4') ");
					strSQL_Cont.append(" and ind_comp in ('3', '1', '4') ");
				}
				
				if (solicitud1.equals("0") && solicitud2.equals("0")){
					strSQL.append(" and ind_comp in ('3','1') ");        
					strSQL_Cont.append(" and ind_comp in ('3','1') ");        
					strSQL.append(" and cnt_min_comp_acu > cnt_min_comp_sal ");        
					strSQL_Cont.append(" and cnt_min_comp_acu > cnt_min_comp_sal ");  

				}

			}
			
			//rango de fechas de la consulta
			strSQL.append(" AND date(fec_perm) between '").append( fechIni).append( "' and '").append( fechFin).append("' ");
			strSQL_Cont.append(" AND date(fec_perm) between '").append( fechIni).append( "' and '").append( fechFin).append("' ");
			
			
			//criterios de consulta
			if(criterio.trim().equals("0")){ // la opcion escogida es REGISTRO
				strSQL.append(" AND cod_pers = '").append(valor.trim()).append("' ");
				strSQL_Cont.append(" AND cod_pers = '").append(valor.trim()).append("' ");
			}
			if(criterio.trim().equals("1")){ // la opcion escogida es UND. ORGANIZACIONAL
				strSQL.append(" AND cod_uorga = '").append(valor.trim()).append("' ");
				strSQL_Cont.append(" AND cod_uorga = '").append(valor.trim()).append("' ");
			}
			if(criterio.trim().equals("5")){ // la opcion escogida es INTENDENCIA
				//criterio = "Intendencia";
				String intendencia = valor.toString().trim().substring(0,2); // 2A de 2A0000
				strSQL.append(" and cod_uorga LIKE '").append( intendencia.trim().toUpperCase() ).append( "%'");
				
				strSQL_Cont.append(" and cod_uorga LIKE '").append( intendencia.trim().toUpperCase() ).append( "%'");
			}
			if(criterio.trim().equals("4")){ // la opcion escogida es INSTITUCIONAL
				//criterio = "Institucional";
			}
     		
			//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {
				Map roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPerso = (String) seguridad.get("codPers");
				String uoAO = (String) seguridad.get("uoAO");
				log.debug("findDetalleSolicitudLE_Seguridad / roles: " + roles);
				log.debug("findDetalleSolicitudLE_Seguridad / uoSeg: " + uoSeg);
				log.debug("findDetalleSolicitudLE_Seguridad / codSeg: " + codPerso);

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {//Visualiza a todos
					strSQL.append(" AND 1=1 ");	
					strSQL_Cont.append(" AND 1=1 ");
					log.debug(" AND 1=1 ");

				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null //Visualiza solo a los de la UO a la cual pertenece el usuario logueado
						|| roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {

					// para que rol_jefe pueda visualizar los colaboradores de su area
					if (log.isDebugEnabled()) log.debug("uoSeg: "+uoSeg);
					if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
						uoSeg = uoSeg.substring(0,2).concat("%");
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_ANALISTA_OPERATIVO: "+uoSeg);
					}
					if (roles.get(Constantes.ROL_JEFE)!=null){
						if (log.isDebugEnabled()) log.debug("uoSeg inicial de ROL_JEFE: "+uoSeg);
						if (log.isDebugEnabled()) log.debug("uoSeg.length(): "+uoSeg.length());

						for(int p = uoSeg.length()-1; p == 0; p--){
							String letra = uoSeg.substring(p,p+1).trim();
							if(!letra.equals("0")){
								uoSeg = uoSeg.substring(0,p+1).concat("%");
								p = 0;
							}
						}						
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_JEFE: "+uoSeg);
					}
					log.debug(" AND cod_uorga LIKE '"+uoSeg+"'");
					// para que rol_jefe pueda visualizar los colaboradores de su area

					
					if(roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){
						strSQL.append(" AND ((cod_uorga LIKE '").append(uoSeg).append( "') ");						
						
						strSQL.append(" or (cod_uorga in ")
						.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
						.append(uoAO).append( "'))) ");
						
						
						strSQL_Cont.append(" AND cod_uorga LIKE '").append(uoSeg).append( "' "); 
						strSQL_Cont.append(" or (cod_uorga in ")
						.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
						.append(uoAO).append( "'))) ");
						
					}else{
						strSQL.append(" AND cod_uorga LIKE '").append(uoSeg).append( "' ");
						strSQL_Cont.append(" AND cod_uorga LIKE '").append(uoSeg).append( "' "); 
						}
					

				} else { //No visualiza ninguno
					strSQL.append(" AND 1=2 ");	
					strSQL_Cont.append(" AND 1=2 "); 
					log.debug(" AND 1=2 ");
				}
			}
			
			strSQL.append(" ORDER BY 1,7,8 ");	


			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED); 
			lista = (ArrayList) executeQuery(dataSource, strSQL.toString());
			lista2 = (ArrayList) executeQuery(dataSource, strSQL_Cont.toString());
			setIsolationLevel(-1);
			
			mapaListas.put("lista", lista);
			mapaListas.put("lista2", lista2);

		} catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
			throw new SQLException(e.toString());
		} 
		return mapaListas;
	}//FIN - NVILLAR - 03/05/2012 - LABOR EXCEPCIONAL
	
	/**NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de buscar el detalle de notificaciones a directivos 
	 * de acuerdo a criterio de busqueda
	 * @param Map datos
	 * @return List 
	 * @throws DAOException
	 */	
	public List buscaDetallePermanenciaLE(Map datos) throws DAOException {

		StringBuffer sSQL = new StringBuffer(FIND_DETALLE_PERMANENCIA.toString()); 
		List listaXUUOO = null;
		int numColab = 0;
		List lista = null;
		try {		
			if (log.isDebugEnabled()) log.debug("registro: " + datos.get("registro"));
			if (log.isDebugEnabled()) log.debug("fecha: " + datos.get("fecha"));
			if (log.isDebugEnabled()) log.debug("autorizada: " + datos.get("autorizada"));

			String registro = (datos.get("registro") != null ) ? datos.get("registro").toString():""; //Ejm. 2012
			String fecha = (datos.get("fecha") != null ) ? datos.get("fecha").toString():""; //Ejm. 320
			String autorizada = (datos.get("autorizada") != null ) ? datos.get("autorizada").toString():""; //licencia. 1=Iniciada, 2=Aprobada, 3=Rechazada

			autorizada = autorizada.replace('/',':');
			
			Map seguridad = (HashMap)datos.get("seguridad");

			//if (log.isDebugEnabled()) log.debug("fecha_desc: " + fecha_desc);
			Date fechDes = new Date();
			fechDes = new FechaBean(fecha,"yyyy-MM-dd").getSQLDate();
			if (log.isDebugEnabled()) log.debug("fechDes: " + fechDes);
			
			 sSQL.append(" AND y.fec_perm = '").append(fechDes).append("' ");			
			 sSQL.append(" AND y.hor_ini_aut = '").append(autorizada.trim()).append("' ");  

			 sSQL.append(" ORDER BY hinicio asc "); //ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA//ICAPUNAY 19/06 MODIFICANDO A SALDO DE SOLICITUD NO DE COMPENSA


			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			
			lista = (ArrayList) executeQuery(dataSource, sSQL.toString(), new Object[]{datos.get("registro"), datos.get("registro"), fechDes, autorizada.trim()}); 
			setIsolationLevel(-1);


		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. T4819DAO - [buscaDetallePermanenciaLE]");
		} finally {	
		}
		return lista;			
	} //FIN - NVILLAR - 17/04/2012 - LABOR EXCEPCIONAL

	/** NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
	 * Metodo encargado de listar las marcaciones realizadas por un trabajador
	 * dentro de un rango de fechas y filtradas por hora de marcacion Join de
	 * las tablas T1275Marcacion y T1280Tipo_Reloj.
	 * @throws SQLException
	 */
	public ArrayList findHorasAutNoAutComp(Map params) 
	throws SQLException {

		StringBuffer strSQL = new StringBuffer(FIND_HORAS_AUT_NOAUT.toString());
		PreparedStatement pre = null;
		Connection con = null;
		//ResultSet rs = null;
		ArrayList lista = null;

		try {

			String dbpool = (params.get("dbpool") != null ) ? params.get("dbpool").toString():"";
			String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
			String medida = (params.get("medida") != null ) ? params.get("medida").toString():"";
			String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():""; //Ejm. 320
			String fechaFin = (params.get("fechaFin") != null ) ? params.get("fechaFin").toString():""; //licencia. 1=Iniciada, 2=Aprobada, 3=Rechazada
			String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
			String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";
			String codPers = (params.get("codPers") != null ) ? params.get("codPers").toString():"";

			if (log.isDebugEnabled()) log.debug("regimen: " + params.get("regimen"));
			if (log.isDebugEnabled()) log.debug("medida: " + params.get("medida"));
			if (log.isDebugEnabled()) log.debug("fechaIni: " + params.get("fechaIni"));
			if (log.isDebugEnabled()) log.debug("fechaFin: " + params.get("fechaFin"));
			if (log.isDebugEnabled()) log.debug("criterio: " + params.get("criterio"));
			if (log.isDebugEnabled()) log.debug("valor: " + params.get("valor"));
			if (log.isDebugEnabled()) log.debug("codPers: " + params.get("codPers"));
			if (log.isDebugEnabled()) log.debug("strSQL: " + strSQL);
			
			Date fechIni = new Date();
			Date fechFin = new Date();
			fechIni = new FechaBean(fechaIni).getSQLDate();
			fechFin = new FechaBean(fechaFin).getSQLDate();

			Map seguridad = (HashMap)params.get("seguridad");
			
            //tipo de Regimen/Modalidad
            if (regimen.equals("0")){ //Regimen 276 - 728
                            strSQL.append("and t02cod_rel not in('09','10') ");
                            
            }else if (regimen.equals("1")){ //Regimen 1057
                            strSQL.append("and t02cod_rel = '09' ");

            }else if (regimen.equals("2")){ //Modalidad Formativa
                            strSQL.append("and t02cod_rel = '10' ");
            }
            
            // Opcion Criterio-Valor
			if(criterio.trim().equals("0")){ // la opcion escogida es REGISTRO
				strSQL.append(" AND cod_pers = '").append(valor.trim()).append("' ");
			}
			if(criterio.trim().equals("1")){ // la opcion escogida es UND. ORGANIZACIONAL
				strSQL.append(" AND cod_uorga = '").append(valor.trim()).append("' ");
			}
			if(criterio.trim().equals("5")){ // la opcion escogida es INTENDENCIA
				//criterio = "Intendencia";
				String intendencia = valor.toString().trim().substring(0,2); // 2A de 2A0000
				
				strSQL.append(" and cod_uorga LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%'");	
			}
			if(criterio.trim().equals("4")){ // la opcion escogida es INSTITUCIONAL
				//criterio = "Institucional";
			}

			
			//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {
				Map roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPerso = (String) seguridad.get("codPers");
				String uoAO = (String) seguridad.get("uoAO");
				log.debug("findDetalleSolicitudLE_Seguridad / roles: " + roles);
				log.debug("findDetalleSolicitudLE_Seguridad / uoSeg: " + uoSeg);
				log.debug("findDetalleSolicitudLE_Seguridad / codSeg: " + codPerso);

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {//Visualiza a todos
					strSQL.append(" AND 1=1 ");					
					log.debug(" AND 1=1 ");

				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null //Visualiza solo a los de la UO a la cual pertenece el usuario logueado
						|| roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {

					//ICAPUNAY 28/11/2011 para que rol_jefe pueda visualizar los colaboradores de su area
					if (log.isDebugEnabled()) log.debug("uoSeg: "+uoSeg);
					if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
						uoSeg = uoSeg.substring(0,2).concat("%");
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_ANALISTA_OPERATIVO: "+uoSeg);
					}
					if (roles.get(Constantes.ROL_JEFE)!=null){
						if (log.isDebugEnabled()) log.debug("uoSeg inicial de ROL_JEFE: "+uoSeg);
						if (log.isDebugEnabled()) log.debug("uoSeg.length(): "+uoSeg.length());

						for(int p = uoSeg.length()-1; p == 0; p--){
							String letra = uoSeg.substring(p,p+1).trim();
							if(!letra.equals("0")){
								uoSeg = uoSeg.substring(0,p+1).concat("%");
								p = 0;
							}
						}						
						if (log.isDebugEnabled()) log.debug("uoSeg final de ROL_JEFE: "+uoSeg);
					}
					log.debug(" AND cod_uorga LIKE '"+uoSeg+"'");
					//ICAPUNAY 28/11/2011 para que rol_jefe pueda visualizar los colaboradores de su area

					
					if(roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){
						strSQL.append(" AND ((cod_uorga LIKE '").append(uoSeg).append( "') ");						
						
						strSQL.append(" or (cod_uorga in ")
						.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
						.append(uoAO).append( "'))) ");
						
					}else{
						strSQL.append(" AND cod_uorga LIKE '").append(uoSeg).append( "' ");
						}
					
					

				} else { //No visualiza ninguno
					strSQL.append(" AND 1=2 ");					
					log.debug(" AND 1=2 ");
				}
			}
			strSQL.append(" GROUP BY 1,2,3,4,5 ");        
			strSQL.append(" ORDER BY 1,2,3 ");  

			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED); 
			lista = (ArrayList) executeQuery(dataSource, strSQL.toString(), new Object[]{fechIni, fechFin });
			setIsolationLevel(-1);

		} catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
			throw new SQLException(e.toString());
		} /*finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (pre != null)
					pre.close();
			} catch (Exception e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}*/
		return lista;
	}//FIN - NVILLAR - 16/04/2012 - LABOR EXCEPCIONAL
	
	
	//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	/**
	  * Metodo que busca si existe una permanencia extra para un colaborador segun pk (cod_pers, fec_perm,hor_ini_perm,hor_ini_comp)	
	  * @param params Map (cod_pers,fec_perm,hor_ini_perm,hor_fin_perm,cod_uorga,hor_ini_aut,cod_jefe_aut,hor_ini_comp,hor_fin_comp,cnt_min_comp_acu,ind_comp,cnt_min_comp_sal,cod_user_crea,fec_creacion)	  		
	  * @return mPermanencia Map
	  * @throws DAOException
	  */
	public Map findPermanenciaExtraByPk(Map params) throws DAOException {
		
		Map mPermanencia = null;
		if (log.isDebugEnabled()) log.debug("T4819DAO - findPermanenciaExtraByPk - params: " + params);	
		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mPermanencia = executeQueryUniqueResult(dataSource, FIND_PERMANENCIA_BY_PK.toString(), new Object[]{params.get("cod_pers"),
				params.get("fec_perm"),params.get("hor_ini_perm"),params.get("hor_ini_comp")});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_PERMANENCIA_BY_PK]");
		}	
		return mPermanencia;
	}
	//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	
	/**
	  * Metodo que busca si existe una permanencia extra para un colaborador segun (cod_pers, fec_perm,hor_ini_perm): jquispecoi 03/2014	
	  * @param params Map   		
	  * @return mPermanencia Map
	  * @throws DAOException
	  */
	public boolean findPermanenciaExtraByFecHor(String cod_pers, String fec_perm, String hor_ini_perm) throws DAOException {
		
		Map mPermanencia = null;
		if (log.isDebugEnabled()) log.debug("T4819DAO - findPermanenciaExtraByPk - params: " + fec_perm + "--" + hor_ini_perm);	

		try{
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			mPermanencia = executeQueryUniqueResult(dataSource, FIND_PERMANENCIA_BY_FEC_HOR.toString(), new Object[]{cod_pers,
				new FechaBean(fec_perm).getSQLDate(),
				hor_ini_perm});
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_PERMANENCIA_BY_FEC_HOR]");
		}	
		return mPermanencia!=null;
	}	
	
	/**
	 * Método encargado de verificar si el trabajador tiene saldo para la fecha indicada.  //jquispecoi 05/2014
	 * 
	 * @param String codPers
	 *  Número de registro del trabajador.
	 * @param String fechaini
	 * 	Fecha de Inicio del rango a buscar
	 * @param String fechafin
	 *  Fecha de Fin del rango a buscar
	 * @return cantidad
	 *  Devuelve la cantidad de minutos de saldo para el rango de fechas ingresada.
	 * @throws SQLException
	 */
	public Integer findAcumuladobyRango( String codPers,
			String fechaini, String fechafin) throws SQLException {

		Map rs = new HashMap();
		Integer cantidad = new Integer("0");
		String cadena = "0";
		
		FechaBean fb = new FechaBean(fechaini, "dd/MM/yyyy");
		FechaBean fb1 = new FechaBean(fechafin, "dd/MM/yyyy");
		rs = executeQueryUniqueResult(dataSource,FIND_ACUMULADO_BY_RANGO, new Object[]{codPers,fb.getSQLDate(),fb1.getSQLDate()});
		
		if (rs!=null){
			 if (rs.size()>0){
				 cadena = rs.get("cantidad")!=null?rs.get("cantidad").toString().trim():"0";
			 }
			cantidad = new Integer(cadena);
		}
		return cantidad;
	}
		
}