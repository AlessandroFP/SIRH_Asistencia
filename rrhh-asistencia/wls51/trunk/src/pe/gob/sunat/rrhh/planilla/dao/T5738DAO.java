//PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.rrhh.planilla.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.utils.Constantes;
/**
 * <p>Title: T5738DAO </p>
 * <p>Description: Clase para realizar la consulta de la tabla en Oracle t5738devlicencia</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: SUNAT</p>
 * @author AGONZALESF
 * @version 1.0 
 */
public class T5738DAO  extends DAOAbstract {
	  private DataSource datasource; // Variable creada para instanciar el DataSource
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer CALL_REG_REIN_LIC = new StringBuffer("{ call siga01.per_tregistro_plame.sp_reg_reinlic(?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ? ) }");

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro    
	private static final StringBuffer FIND_DEVOLUCION_POR_PERIODO =  new StringBuffer("")
			.append(" select trim(cod_mot_licencia)cod_mot_licencia  , sum(num_dias_devol) devueltos FROM t5738devlicencia WHERE")
			.append(" (cod_licencia, ann_lic_calc,mes_lic_calc)  IN (") //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" SELECT licencias.cod_licencia ,licencias_empleados.ann_lic_calc,licencias_empleados.mes_lic_calc ") //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" FROM licencias")
			.append(" JOIN licencias_empleados  ON licencias.cod_licencia = licencias_empleados.cod_licencia")
			.append(" JOIN hist_metas_empleado hm  ON hm.tipo_plan_tpl = licencias.tipo_plan_tpl")
			.append(" AND hm.subt_plan_tpl = '1'")
			.append(" AND hm.ano_peri_hme = licencias_empleados.ann_lic_calc")
			.append(" AND hm.nume_peri_hme = licencias_empleados.mes_lic_calc")
			.append(" AND hm.codi_empl_per = licencias.codi_empl_per ")
			.append(" WHERE licencias.tipo_plan_tpl = ?")
			.append(" AND licencias.codi_empl_per = ?")
			.append(" AND licencias_empleados.ann_lic_calc = ?")
			.append(" AND licencias_empleados.mes_lic_calc = ?")
			.append(" AND licencias.ind_del = '1'")
			.append(" AND licencias.cod_tiplic in (? ,?, ?)")
			.append(" AND licencias_empleados.ind_del = '1')")
			.append(" AND ind_del <> 'S'")//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" AND ind_cierre < '3' ") //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" group by cod_mot_licencia");

		
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro    
	private static final StringBuffer FIND_DEVOLUCION_POR_MOVIMIENTO = new StringBuffer("")
			.append(" SELECT cod_licencia , SUM(num_dias_devol) devueltos FROM t5738devlicencia WHERE")
			.append(" (cod_licencia , ann_lic_calc,mes_lic_calc) IN (") //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" SELECT licencias.cod_licencia ,licencias_empleados.ann_lic_calc,licencias_empleados.mes_lic_calc ")//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" FROM licencias")
			.append(" JOIN licencias_empleados  ON licencias.cod_licencia = licencias_empleados.cod_licencia ")
			.append(" JOIN hist_metas_empleado hm  ON hm.tipo_plan_tpl = licencias.tipo_plan_tpl")
			.append(" AND hm.subt_plan_tpl = '1'")
			.append(" AND hm.ano_peri_hme = licencias_empleados.ann_lic_calc")
			.append(" AND hm.nume_peri_hme = licencias_empleados.mes_lic_calc")
			.append(" AND hm.codi_empl_per = licencias.codi_empl_per ")
			.append(" WHERE licencias.tipo_plan_tpl = ?")
			.append(" AND licencias.codi_empl_per = ?")
			.append(" AND licencias_empleados.ann_lic_calc = ?")
			.append(" AND licencias_empleados.mes_lic_calc = ?")
			.append(" AND licencias.ind_del = '1'")
			.append(" AND licencias_empleados.ind_del = '1'")
			.append(" AND TRIM(licencias.codi_moti_tmo) = ?)")
			.append(" AND ind_del <> 'S'")//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" AND ind_cierre < '3' ")//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
			.append(" GROUP BY cod_licencia");
																																			
		
	
	//dias ya devueltos en una licencia empleado
	private static final StringBuffer FIND_DEVOL_TOTAL_LIC_EMP =  new StringBuffer("")
	.append(" SELECT  nvl(sum(num_dias_devol),0) devolucion ")
	.append(" FROM t5738devlicencia ")	 
	.append(" WHERE cod_licencia=?")
	.append(" AND ann_lic_calc=?")
	.append(" AND mes_lic_calc=?")
	.append(" AND ind_del <> 'S'")//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	.append(" AND ind_cierre < '3' ");//AGONZALESF -PAS20171U230200028 - solicitud de reintegro

	
	
        //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	//PK COD_TIP_PLANILLA, COD_SUB_PLANILLA, ANN_DEVOLUCION, MES_DEVOLUCION, COD_EMPL_PER, COD_LICENCIA
	//TAMBIEN Se necesita : ann_lic_calc mes_lic_calc
	//Advertencia de uso
	private static final StringBuffer FIND_DEVOL_POR_LIC_EMP_DETERMINADO_PERIODO =  new StringBuffer("")
	.append(" SELECT nvl(num_dias_devol,0) devolucion ")
	.append(" FROM t5738devlicencia ")
	.append(" WHERE cod_tip_planilla=?")
	.append(" AND cod_sub_planilla=?")
	.append(" AND ann_devolucion=?")
	.append(" AND mes_devolucion=?")
	.append(" AND cod_empl_per=?")
	.append(" AND cod_licencia=?")	
	.append(" AND ann_lic_calc=?")
	.append(" AND mes_lic_calc=?")
	.append(" AND ind_del <> 'S'")
	.append(" AND ind_cierre < '3' ");
	
        //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	//PK COD_TIP_PLANILLA, COD_SUB_PLANILLA, ANN_DEVOLUCION, MES_DEVOLUCION, COD_EMPL_PER, COD_LICENCIA
	//este es registro que se desea actualizar
	private static final StringBuffer FIND_DEVOLUCION_BY_PK =  new StringBuffer("")
	.append(" SELECT cod_tip_planilla,cod_sub_planilla,ann_devolucion,mes_devolucion,cod_empl_per,cod_licencia ,ann_lic_calc,mes_lic_calc,ind_del,ind_cierre,nvl(num_dias_devol,0) devolucion  ")
	.append(" FROM t5738devlicencia ") 
	.append(" WHERE cod_tip_planilla=?")
	.append(" AND cod_sub_planilla=?")
	.append(" AND ann_devolucion=?")
	.append(" AND mes_devolucion=?")
	.append(" AND cod_empl_per=?")
	.append(" AND cod_licencia=?");
	 
				
	private static final StringBuffer FIND_MTO_UNITARIO = new StringBuffer(" SELECT round( nvl(siga01.per_tregistro_plame.fn_obt_mtoreinalic( ?,?,?,?,?,?,?,?),0),2) as monto FROM DUAL");
	
    //AGONZALESF -PAS20191U230200011 - solicitud de reintegro , solo una devolucion por licencia puede estar activa
	private static final StringBuffer FIND_CONTEO_DEVOLUCIONES_ACTIVAS = new StringBuffer("select nvl(count(*),0) as conteo  from t5738devlicencia where ind_cierre =0 and ind_del = 'N' and cod_licencia = ?");
	
	public T5738DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
	
	/**
	 * Metodo que llama al procedimiento sp_reg_reinlic (procedimiento de ingreso/modificacion de devolucion licencias/subsidios)
	 * @param   Lista de parametros para insercion de devolucion  
	 * @throws SQLException 
	 */
	public void insertDevLicencia( Map params) throws SQLException { 
		CallableStatement cs = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap solicitud = new HashMap();

		try { 
 
			con = getConnection(datasource);
			cs = con.prepareCall( CALL_REG_REIN_LIC.toString() );
			cs.setString(1, params.get("p_tipo").toString()); 
			cs.setString(2, params.get("p_subtipo").toString()); 
			cs.setString(3, params.get("p_anio").toString()); 
			cs.setString(4, params.get("p_mes").toString()); 
			cs.setString(5, params.get("p_empl").toString()); 
			cs.setString(6, params.get("p_cod_licencia").toString()); 
			cs.setString(7, params.get("p_cod_tiplic").toString()); 
			cs.setString(8, params.get("p_cod_origmov").toString()); 
			cs.setString(9, params.get("p_con_dscto").toString()); 
			cs.setInt(10, ((Integer) params.get("p_cnt_diadev")).intValue()); 
			cs.setString(11, params.get("p_des_observa").toString()); 
			cs.setString(12, params.get("p_ann_lic_calc").toString());			
			cs.setString(13, params.get("p_mes_lic_calc").toString());  
			cs.setString(14, params.get("p_ind_origen").toString()); 
			cs.setBigDecimal(15, (BigDecimal)params.get("p_mto_unitario") );
			cs.setString(16, params.get("p_cod_tipdev").toString());
			cs.registerOutParameter(17, java.sql.Types.NUMERIC);
			cs.registerOutParameter(18, java.sql.Types.VARCHAR);	 
		
			cs.executeUpdate();
			
			String error = cs.getString(17);
			String errorMsg = cs.getString(18);
			log.debug(error + "----" + errorMsg ); 
			if(error!=null&& error!=""){
				throw new SQLException(errorMsg);
			}
			
 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** ", e);
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {

			}
			try {
				cs.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		} 
	} 


	/** 
	 * Metodo para obtener los dias devueltos de las licencias de un trabajador por un periodo  agrupadas  por movimiento 
	 * @param params  Filtro para busqueda
	 * @return Lista de dias devueltos por movimiento
	 */
	public List findDevolucionLicencia(Map params) {
	if (log.isDebugEnabled())log.debug(" INICIO - findDevolucionLicencia()-> " +params);
		
		List listaDetalleAsistencia = null; 
		Object[] filtro = null   ;
		
		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
			if (log.isDebugEnabled())log.debug("Seleccionar devoluciones asistencia planilla - haberes");
			filtro = new Object[] {
					params.get("tipo_plan_tpl"), 
					params.get("codi_empl_per"), 					
					params.get("anio"), 
					params.get("mes") ,
					params.get("cod_tiplic1"),
					params.get("cod_tiplic2"),
					params.get("cod_tiplic3") };
		}
		
		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
			if (log.isDebugEnabled())log.debug("Seleccionar devoluciones asistencia planilla - haberes");
			filtro = new Object[] { 
					params.get("tipo_plan_tpl"), 
					params.get("codi_empl_per"), 					
					params.get("anio"), 
					params.get("mes") ,
					params.get("cod_tiplic1"),
					params.get("cod_tiplic2"),
					params.get("cod_tiplic3") };
		}
		listaDetalleAsistencia = executeQuery(datasource, FIND_DEVOLUCION_POR_PERIODO.toString(), filtro);
		if (log.isDebugEnabled())	log.debug(" FIN - findDevolucionLicencia() ");
		return listaDetalleAsistencia;
	}
	/**
	 * Metodo para obtener los dias devueltos de las licencias de un trabajador por movimiento agrupado por licencia
	 * @param params Filtro para busqueda
	 * @return Lista de dias devueltos por licencia
	 */
	public List findDevolucionLicenciaDetalle(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDevolucionLicenciaDetalle() ->" + params);
		List detalleDevoluciones = null;
		Object[] filtro = null;

		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
			if (log.isDebugEnabled()) log.debug("Seleccionar devoluciones licencia  planilla - haberes");
			filtro = new Object[] { 
					params.get("tipo_plan_tpl"), 
					params.get("codi_empl_per"), 					
					params.get("anio"), 
					params.get("mes") ,
					params.get("movimiento") };
		}

		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
			if (log.isDebugEnabled()) log.debug("Seleccionar devoluciones licencia  planilla - cas");
			filtro = new Object[] { 
					params.get("tipo_plan_tpl"), 
					params.get("codi_empl_per"), 					
					params.get("anio"), 
					params.get("mes") ,
					params.get("movimiento") };
		}

		detalleDevoluciones = executeQuery(datasource, FIND_DEVOLUCION_POR_MOVIMIENTO.toString(), filtro);
		if (log.isDebugEnabled()) log.debug("FIN - findDevolucionLicenciaDetalle()");
		return detalleDevoluciones;
	}
	
	/**
	 * Metodo para hallar los dias devueltos de una licencia
	 * @param params
	 * @return dias devueltos
	 */
	public Map findDevueltoLicencia(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDevueltoLicencia()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_licencia"), 
				params.get("ann_lic_calc"), 					
				params.get("mes_lic_calc")}; 		
		devolucion = executeQueryUniqueResult(datasource, FIND_DEVOL_TOTAL_LIC_EMP.toString(), filtro);		
	 
		if (log.isDebugEnabled()) log.debug("FIN - findDevueltoLicencia()");
		return devolucion;
	}
	
	/**
	 * Metodo para hallar los dias devueltos de una licencia en un periodo determinado
	 * @param params
	 * @return Lista de devoluciones ya ingresadas
	 */
	public Map findDevueltoLicenciaEmpleado(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDevueltoPeriodoLicencia()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_tip_planilla"), 
				params.get("cod_sub_planilla"), 	 
				params.get("ann_devolucion") ,
				params.get("mes_devolucion"),
				params.get("cod_empl_per"),
				params.get("cod_licencia"), 
				params.get("ann_lic_calc"), 					
				params.get("mes_lic_calc")  }; 
 		devolucion = executeQueryUniqueResult(datasource, FIND_DEVOL_POR_LIC_EMP_DETERMINADO_PERIODO.toString(), filtro);		
	 
		if (log.isDebugEnabled()) log.debug("FIN - findDevueltoPeriodoLicencia()");
		return devolucion;
	}

	/**
	 * Metodo para hallar si existe una devolucion para la fila que se quiere ingresar
	 * @param params
	 * @return data de devolucion
	 */
	public Map findDatosDevolucion(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDatosDevolucion()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { params.get("cod_tip_planilla"), 
				params.get("cod_sub_planilla"), 	 
				params.get("ann_devolucion") ,
				params.get("mes_devolucion"),
				params.get("cod_empl_per"),
				params.get("cod_licencia")  };
		devolucion = executeQueryUniqueResult(datasource, FIND_DEVOLUCION_BY_PK.toString(), filtro);		
	 
		if (log.isDebugEnabled()) log.debug("FIN - findDatosDevolucion()");
		return devolucion;
	}

	/**
	 * Funcion para el calculo de monto
	 * @param params parametros para filtro
	 * @return monto por dia
	 */
	public Map obtenerMontoUnitario(Map params) {
		  if (log.isDebugEnabled()) log.debug(" INICIO - obtenerMontoUnitario()-> " + params); 
			Map devolucion = null;
			Object[] filtro = null;
			filtro = new Object[] { 
					params.get("pc_tipo"), 
					params.get("pc_subtipo"), 					
					params.get("pc_ann_lic_calc"),
					params.get("pc_mes_lic_calc") ,
					params.get("pc_empl")  ,
					params.get("pc_con_dscto") ,
					params.get("pc_cod_origmov"),
					params.get("pc_cod_tiplic")}; 
			devolucion = executeQueryUniqueResult(datasource, FIND_MTO_UNITARIO.toString(), filtro);	
		 
			if (log.isDebugEnabled()) log.debug("FIN - obtenerMontoUnitario()");
			return devolucion;
	}
	
	/**
	 * Metodo para hallar el numero de devoluciones activas por una licencia
	 * @param params
	 * @return conteo de devoluciones en estado 0 y no eliminadas
	 */
	public Map findConteoDevolucionesAprobadas(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findConteoDevolucionesAprobadas()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_licencia")};				 		
		devolucion = executeQueryUniqueResult(datasource, FIND_CONTEO_DEVOLUCIONES_ACTIVAS.toString(), filtro);
		if (log.isDebugEnabled()) log.debug("FIN - findConteoDevolucionesAprobadas()");
		return devolucion;
	}
	
}
