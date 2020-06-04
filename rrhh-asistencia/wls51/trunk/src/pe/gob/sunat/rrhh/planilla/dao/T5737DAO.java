//PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.rrhh.planilla.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.n3.nanoxml.p;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.utils.Constantes;
 
/**
 * <p>Title: T5737DAO </p>
 * <p>Description: Clase para realizar la consulta de la tabla en Oracle t5737devasistencia</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: SUNAT</p>
 * @author AGONZALESF
 * @version 1.0 
 */
public class T5737DAO  extends DAOAbstract {
	  private DataSource datasource; // Variable creada para instanciar el DataSource
	 
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer CALL_REG_REIN_ASI = new StringBuffer().append("{ call siga01.per_tregistro_plame.sp_reg_reinasi(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?) }");

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	//Valido para cas  y para haberes
	private static final StringBuffer FIND_DEVOLUCION_POR_PERIODO =  new StringBuffer(
			"SELECT cod_mov_origen movimiento,   NVL( SUM(cnt_min_devol ),0) devueltos")
			.append(" FROM t5737devasistencia  dev ")
			.append(" WHERE   cod_empl_per = ?")
			.append(" AND TO_CHAR( fec_asis_origen, 'yyyy') =?")
			.append(" AND TO_CHAR( fec_asis_origen, 'mm') = ?") 
			.append(" AND ind_del <> 'S'")
			.append(" AND ind_cierre < '3' ")
			.append(" GROUP BY cod_mov_origen");
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro    
	//Valido para cas  y para haberes
	private static final StringBuffer FIND_DEVOLUCION_POR_MOVIMIENTO =  new StringBuffer("SELECT   fec_asis_origen , NVL( SUM(cnt_min_devol ),0) devueltos")
			.append(" FROM t5737devasistencia  dev ")
			.append(" WHERE cod_empl_per = ?")
			.append(" AND TO_CHAR( fec_asis_origen, 'yyyy') =?")
			.append(" AND TO_CHAR( fec_asis_origen, 'mm') = ?")
			.append(" AND trim(cod_mov_origen) like trim(?)")
			.append(" AND ind_del <> 'S'")
			.append(" AND ind_cierre < '3' ")			
			.append(" GROUP BY  fec_asis_origen "); 
		
		
	//Minutos ya devueltos 
	private static final StringBuffer FIND_DEVOL_TOTAL  =  new StringBuffer("")
	.append(" SELECT  nvl(sum(cnt_min_devol),0) devolucion ")
	.append(" FROM t5737devasistencia ")	 
	.append(" WHERE cod_empl_per=?")
	.append(" AND  fec_asis_origen = ?")
	.append(" AND cod_mov_origen=?") 
	.append(" AND ind_del <> 'S'") //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	.append(" AND ind_cierre < '3' ")	;//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	
	//Minutos por devolver en un periodo , planilla especifica  
        //Advetencia de registro ya usado  
	private static final StringBuffer FIND_DEVOL_PERIODO_DEVOLUCION =  new StringBuffer("")
	.append(" SELECT  nvl(cnt_min_devol,0) devolucion")
	.append(" FROM t5737devasistencia ")	 
	.append(" WHERE cod_empl_per=?")
	.append(" AND cod_tip_planilla=?")
	.append(" AND cod_sub_planilla=?")
	.append(" AND ann_devolucion=?")//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	.append(" AND mes_devolucion=?")//AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	.append(" AND  fec_asis_origen  = ?")
	.append(" AND cod_mov_origen=?")
	.append(" AND ind_del <> 'S'")
	.append(" AND ind_cierre < '3' ")	;
	
         //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	//PK   COD_EMPL_PER, COD_TIP_PLANILLA, COD_SUB_PLANILLA, ANN_DEVOLUCION, MES_DEVOLUCION, FEC_ASIS_ORIGEN, COD_MOV_ORIGEN
	//Registro que se desea actualizar 
	private static final StringBuffer FIND_DEVOLUCION_BY_PK  =  new StringBuffer("")
	.append(" SELECT cod_empl_per,cod_tip_planilla,cod_sub_planilla,ann_devolucion,mes_devolucion,fec_asis_origen, cod_mov_origen,ind_cierre,ind_del, nvl(cnt_min_devol,0) devolucion")
	.append(" FROM t5737devasistencia ")	 
	.append(" WHERE cod_empl_per=?")
	.append(" AND cod_tip_planilla=?")
	.append(" AND cod_sub_planilla=?")
	.append(" AND ann_devolucion=?")
	.append(" AND mes_devolucion=?") 
	.append(" AND  fec_asis_origen  = ?")
	.append(" AND cod_mov_origen=?");
	
	
	
	
	private static final StringBuffer FIND_MTO_UNITARIO = new StringBuffer(" SELECT nvl(siga01.per_tregistro_plame.fn_obt_mtoreinasi( ?,?,?,?,?,?,?),0) as monto FROM DUAL");
		
	public T5737DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo para listar las devoluciones de  asistencias corresponden a un periodo agrupadas  por movimiento 
	 * @param params : Filtro para busqueda
	 * @return Minutos ya devueltos de acuerdo a filtro, agrupadas por movimiento
	 * @throws DAOException
	 */
	public List findDevolucionAsistencia(Map params) throws DAOException {
		if (log.isDebugEnabled())log.debug(" INICIO - findDevolucionAsistencia()-> " +params);
		
		List listaDetalleAsistencia = null; 
		Object[] filtro = null   ;
		
		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
			if (log.isDebugEnabled())log.debug("Seleccionar devoluciones asistencia planilla - haberes");
			filtro = new Object[] { params.get("codi_empl_per"),params.get("anioConsulta"), params.get("mesConsulta") };
		}
		
		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
			if (log.isDebugEnabled())log.debug("Seleccionar devoluciones asistencia planilla - CAS");
			filtro = new Object[] { params.get("codi_empl_per"),  params.get("anioConsulta"), params.get("mesConsulta") };
		}
		listaDetalleAsistencia = executeQuery(datasource, FIND_DEVOLUCION_POR_PERIODO.toString(), filtro);
		if (log.isDebugEnabled())	log.debug(" FIN - findDevolucionAsistencia() ->"+ listaDetalleAsistencia);
		return listaDetalleAsistencia;
	}
	
	/**
	 * Metodo para listar las devoluciones de  asistencias corresponden a un periodo agrupadas  por dia 
	 * @param params : Filtro para busqueda
	 * @return Lista de fechas  con minutos ya devueltos de acuerdo a filtro 
	 * @throws DAOException
	 */
	public List findDevolucionAsistenciaDetalle(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDevolucionAsistenciaDetalle() ->" + params);
		List detalleDevoluciones = null;
		Object[] filtro = null;

		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
			if (log.isDebugEnabled()) log.debug("Seleccionar devoluciones asistencia planilla - haberes");  
			filtro = new Object[] { params.get("codi_empl_per"), params.get("anioConsulta"), params.get("mesConsulta"), params.get("movimiento") };
		}

		if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
			if (log.isDebugEnabled()) log.debug("Seleccionar devoluciones asistencia planilla - haberes");
			filtro = new Object[] { params.get("codi_empl_per"), params.get("anioConsulta"), params.get("mesConsulta"), params.get("movimiento") };
		}

		detalleDevoluciones = executeQuery(datasource, FIND_DEVOLUCION_POR_MOVIMIENTO.toString(), filtro);
		if (log.isDebugEnabled())
			log.debug("FIN - findDevolucionAsistenciaDetalle() ->" + detalleDevoluciones);
		 
		return detalleDevoluciones;
	}

	/**
	 * Metodo que llama al procedimiento sp_reg_reinasi (procedimiento de ingreso/modificacion de devolucion asistencia)
	 * @param params Lista de parametros para insercion de devolucion 
	 * @throws SQLException 
	 */
	public void insertDevAsistencia(Map params) throws SQLException {
		 
		CallableStatement cs = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap solicitud = new HashMap();

		try {
 
			con = getConnection(datasource);
			cs = con.prepareCall( CALL_REG_REIN_ASI.toString() );
			cs.setString(1, params.get("p_tipo").toString()); 
			cs.setString(2, params.get("p_subtipo").toString()); 
			cs.setString(3, params.get("p_anio").toString()); 
			cs.setString(4, params.get("p_mes").toString()); 
			cs.setString(5, params.get("p_empl").toString()); 
			cs.setString(6, params.get("p_fec_asis").toString()); 
			cs.setString(7, params.get("p_cod_origmov").toString()); 
			cs.setString(8, params.get("p_con_dscto").toString()); 
			cs.setString(9, params.get("p_cod_tipdev").toString()); 
			cs.setInt(10, ((Integer)params.get("p_cnt_mindev")).intValue()); 
			cs.setString(11, params.get("p_des_observa")!=null?params.get("p_des_observa").toString():"");			
			cs.setString(12, params.get("p_anioorig").toString()); 
			cs.setString(13, params.get("p_mesorig").toString());  
			cs.setString(14, params.get("p_ind_origen").toString()); 
			cs.setBigDecimal(15, (BigDecimal)params.get("p_mto_unitario")); 
			cs.registerOutParameter(16, java.sql.Types.NUMERIC);
			cs.registerOutParameter(17, java.sql.Types.VARCHAR);
			cs.executeUpdate();			
			String error = cs.getString(16);
			String errorMsg = cs.getString(17);			
			log.debug(error + "----" + errorMsg );
			
			if(error!=null&&error!=""){
				throw new SQLException(errorMsg);
			} 
 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** ",e);
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
	 * Metodo para hallar los dias devueltos de una licencia
	 * @param params
	 * @return Lista de devoluciones ya ingresadas
	 */
	public Map findDevueltoAsistencia(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDevueltoAsistencia()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_empl_per"), 
				params.get("fec_asis_origen") ,
				params.get("cod_mov_origen") }; 		
		devolucion = executeQueryUniqueResult(datasource, FIND_DEVOL_TOTAL .toString(), filtro);		
	 
		if (log.isDebugEnabled()) log.debug("FIN - findDevueltoAsistencia()");
		return devolucion;
	}
	
	/**
	 * Metodo para hallar los dias devueltos de una licencia en un periodo determinado
	 * @param params
	 * @return Lista de devoluciones ya ingresadas
	 */
	public Map findDevueltoAsistenciaPeriodo(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDevueltoAsistenciaPeriodo()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_empl_per"), 
				params.get("cod_tip_planilla"), 					
				params.get("cod_sub_planilla"),
				params.get("ann_devolucion") ,
				params.get("mes_devolucion"),
				params.get("fec_asis_origen") ,
				params.get("cod_mov_origen")  				
				}; 
		devolucion = executeQueryUniqueResult(datasource, FIND_DEVOL_PERIODO_DEVOLUCION.toString(), filtro);		
	 
		if (log.isDebugEnabled()) log.debug("FIN - findDevueltoAsistenciaPeriodo()");
		return devolucion;
	}

        //AGONZALESF -PAS20171U230200028 - solicitud de reintegro
	/**
	 * Metodo para hallar el estado de una devolucion 
	 * @param params
	 * @return data de devolucion
	 */
	public Map findDatosDevolucion(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDatosDevolucion()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_empl_per"), 
				params.get("cod_tip_planilla"), 					
				params.get("cod_sub_planilla"),
				params.get("ann_devolucion") ,
				params.get("mes_devolucion"),
				params.get("fec_asis_origen") ,
				params.get("cod_mov_origen")  
			}; 
		devolucion = executeQueryUniqueResult(datasource, FIND_DEVOLUCION_BY_PK.toString(), filtro);		
	 
		if (log.isDebugEnabled()) log.debug("FIN - findDatosDevolucion()");
		return devolucion;
	}

	/**
	 * Funcion para el calculo de monto
	 * @param params parametros para filtro
	 * @return monto por minuto
	 */
	public Map obtenerMontoUnitario(Map params) {
		  if (log.isDebugEnabled()) log.debug(" INICIO - obtenerMontoUnitario()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("pc_tipo"), 
				params.get("pc_subtipo"), 					
				params.get("pc_anioorig"),
				params.get("pc_mesorig") ,
				params.get("pc_empl")  ,
				params.get("pc_con_dscto") ,
				params.get("pc_cod_origmov")}; 
		devolucion = executeQueryUniqueResult(datasource, FIND_MTO_UNITARIO.toString(), filtro);	
	 
		if (log.isDebugEnabled()) log.debug("FIN - obtenerMontoUnitario()");
		return devolucion;
	}

	
 
	

}
