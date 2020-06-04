package pe.gob.sunat.rrhh.planilla.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>Title: LicenciasDAO </p>
 * <p>Description: Clase para realizar la consulta de Licencias</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class LicenciasDAO extends DAOAbstract {
	  private DataSource datasource; // Variable creada para instanciar el DataSource

	  private static final StringBuffer FIND_LICENCIAS = new StringBuffer("")
	  .append("SELECT 'LS' TIPO_MOVI_PER, ")
	  .append("'Licencias y Subsidios' DESC_TIPO_TPR, ")
	  .append("SUM(NUME_DIAS_PEM) NUME_DIAS_PEM ")
	  .append("FROM LICENCIAS_EMPLEADOS ")
	  .append("WHERE IND_DEL = '1' ")
	  .append("AND ANN_LIC_CALC = ? ") //'2011'
	  .append("AND MES_LIC_CALC = ? ") //'11'
	  .append("AND IND_DEL = '1' ")
	  .append("AND CODI_EMPL_PER = ? ") // '00008867'
	  .append("GROUP BY 'LS', 'Licencias y Subsidios' ")
	  .append("HAVING SUM(NUME_DIAS_PEM) > 0 "); 
	  
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer SELECT_CONCEPTO_LICENCIAS_HABERES = new StringBuffer("")
			.append(" SELECT")
			.append(" TRIM(licencias.cod_tiplic) cod_tiplic,")
			.append(" TRIM(tmotivos.codi_moti_tmo) codi_moti_tmo,")
			.append(" tmotivos.desc_moti_tmo motivo, ")
			.append(" SUM(licencias_empleados.nume_dias_pem) dias_dscto,")
			.append(" TRIM(tmotivos.cod_concepto) cod_concepto,")
			.append(" conceptos.desc_conc_tco ")
			.append(" FROM licencias")
			.append(" JOIN licencias_empleados  ON licencias.cod_licencia = licencias_empleados.cod_licencia")
			.append(" JOIN tmotivos  ON licencias.codi_moti_tmo = tmotivos.codi_moti_tmo")
			.append(" JOIN conceptos  ON tmotivos.cod_concepto = conceptos.codi_conc_tco")
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
			.append(" AND TRIM(licencias.cod_tiplic) IN (? ,?, ?)")
			.append(" AND licencias_empleados.ind_del = '1'")
			.append(" GROUP BY  licencias.cod_tiplic,tmotivos.codi_moti_tmo, tmotivos.desc_moti_tmo  ,tmotivos.cod_concepto,conceptos.desc_conc_tco");
 

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer SELECT_CONCEPTO_LICENCIAS_CAS = new StringBuffer("")
			.append(" SELECT  ")
			.append(" TRIM(licencias.cod_tiplic) cod_tiplic,")
			.append(" TRIM(tmotivos.codi_moti_tmo) codi_moti_tmo,")
			.append(" sum(licencias_empleados.nume_dias_pem) dias_dscto, ")
			.append(" tmotivos.desc_moti_tmo motivo, ")
			.append(" TRIM(mc.cod_concepto) cod_concepto,")
			.append(" conceptos.desc_conc_tco")
			.append(" FROM licencias")
			.append(" JOIN licencias_empleados ON licencias.cod_licencia = licencias_empleados.cod_licencia")
			.append(" JOIN (SELECT TRIM(DESC_CORTA) AS CODI_MOTI_TMO,")
			.append(" 		TRIM(DESC_ABREVIATURA) AS COD_CONCEPTO")
			.append(" 		FROM T01PARAMETRO")
			.append(" 		WHERE COD_PARAMETRO = '1077'")
			.append(" 		AND COD_TIPO = 'D') MC ON TRIM(MC.CODI_MOTI_TMO) = TRIM(licencias.codi_moti_tmo)")
			.append(" JOIN tmotivos ON licencias.codi_moti_tmo = tmotivos.codi_moti_tmo")
			.append(" JOIN conceptos ON MC.cod_concepto = conceptos.codi_conc_tco")
			.append(" JOIN hist_metas_empleado hm ON hm.tipo_plan_tpl = licencias.tipo_plan_tpl")
			.append(" AND hm.subt_plan_tpl = '1'")
			.append(" AND hm.ano_peri_hme = licencias_empleados.ann_lic_calc")
			.append(" AND hm.nume_peri_hme = licencias_empleados.mes_lic_calc")
			.append(" AND hm.codi_empl_per = licencias.codi_empl_per")
			.append(" WHERE licencias.tipo_plan_tpl = ?")
			.append(" AND licencias.codi_empl_per = ?")
			.append(" AND licencias_empleados.ann_lic_calc = ?")
			.append(" AND licencias_empleados.mes_lic_calc = ?")
			.append(" AND licencias.ind_del = '1'")
			.append(" AND TRIM(licencias.cod_tiplic) in (?, ?, ?)")
			.append(" AND licencias_empleados.ind_del = '1'")
			.append("group by  licencias.cod_tiplic,tmotivos.codi_moti_tmo, tmotivos.desc_moti_tmo  ,mc.cod_concepto,conceptos.desc_conc_tco");
	
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer SELECT_DETALLE_LICENCIA_HABERES = new StringBuffer("")
			.append(" SELECT ")
			.append(" licencias.cod_licencia,") 
			.append(" TRIM(licencias.cod_tiplic) cod_tiplic, ")
			.append(" tmotivos.desc_moti_tmo motivo,   ")
			.append(" licencias.fech_inic_lic,   ")
			.append(" licencias.fech_fina_lic, ")
			.append(" licencias_empleados.nume_dias_pem  dias_dscto,  ")
			.append(" TRIM(tmotivos.cod_concepto) cod_concepto, ")
			.append(" TRIM(tmotivos.codi_moti_tmo) codi_moti_tmo, ")
			.append(" tmotivos.desc_moti_tmo  ")
			.append(" FROM licencias ")
			.append(" JOIN licencias_empleados ON licencias.cod_licencia = licencias_empleados.cod_licencia  ")
			.append(" JOIN tmotivos ON licencias.codi_moti_tmo = tmotivos.codi_moti_tmo  ")
			.append(" JOIN hist_metas_empleado hm ON hm.tipo_plan_tpl = licencias.tipo_plan_tpl  ")
			.append("	AND hm.subt_plan_tpl = '1' ")
			.append(" AND hm.ano_peri_hme = licencias_empleados.ann_lic_calc  ")
			.append(" AND hm.nume_peri_hme = licencias_empleados.mes_lic_calc ")
			.append(" AND hm.codi_empl_per = licencias.codi_empl_per  "	)		 
			.append(" WHERE licencias.tipo_plan_tpl = ?   ")
			.append(" AND licencias.codi_empl_per = ?   ")
			.append(" AND licencias_empleados.ann_lic_calc = ? ")
			.append(" AND licencias_empleados.mes_lic_calc = ?   ")
			.append(" AND licencias.ind_del = '1'   ")
			.append(" AND licencias_empleados.ind_del = '1'  ")
			.append(" AND  TRIM(tmotivos.codi_moti_tmo) =?  ORDER BY licencias.fech_inic_lic DESC");
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
		private static final StringBuffer SELECT_DETALLE_LICENCIA_CAS =  new StringBuffer("")
		.append(" SELECT ")
		.append(" licencias.cod_licencia,") 
		.append(" TRIM(licencias.cod_tiplic) cod_tiplic, ")
		.append(" tmotivos.desc_moti_tmo motivo,   ")
		.append(" licencias.fech_inic_lic,   ")
		.append(" licencias.fech_fina_lic, ")
		.append(" licencias_empleados.nume_dias_pem  dias_dscto,  ")
		.append(" TRIM(tmotivos.cod_concepto) cod_concepto, ")
		.append(" TRIM(tmotivos.codi_moti_tmo) codi_moti_tmo, ")
		.append(" tmotivos.desc_moti_tmo  ")
		.append(" FROM licencias ")
		.append(" JOIN licencias_empleados ON licencias.cod_licencia = licencias_empleados.cod_licencia  ")
		.append(" JOIN tmotivos ON licencias.codi_moti_tmo = tmotivos.codi_moti_tmo  ")
		.append(" JOIN hist_metas_empleado hm ON hm.tipo_plan_tpl = licencias.tipo_plan_tpl  ")
		.append("	AND hm.subt_plan_tpl = '1' ")
		.append(" AND hm.ano_peri_hme = licencias_empleados.ann_lic_calc  ")
		.append(" AND hm.nume_peri_hme = licencias_empleados.mes_lic_calc ")
		.append(" AND hm.codi_empl_per = licencias.codi_empl_per  "	)		 
		.append(" WHERE licencias.tipo_plan_tpl = ?   ")
		.append(" AND licencias.codi_empl_per = ?   ")
		.append(" AND licencias_empleados.ann_lic_calc = ? ")
		.append(" AND licencias_empleados.mes_lic_calc = ?   ")
		.append(" AND licencias.ind_del = '1'   ")
		.append(" AND licencias_empleados.ind_del = '1'  ")
		.append(" AND  TRIM(tmotivos.codi_moti_tmo) =?  ORDER BY licencias.fech_inic_lic DESC ");
	
		//PAS20171U230200001 - solicitud de reintegro  
		private static final String SELECT_LICENCIA_FECHAS = "select fech_inic_lic, fech_fina_lic from licencias where cod_licencia = ?";
	  
	   //PAS20171U230200001 - solicitud de reintegro  
		private static final StringBuffer SELECT_DIAS_SIN_LICENCIA_FIN = new StringBuffer(")) ")          
																				  .append(" select dia ,( select count(*) from licenciames lm where dia between  lm.fech_inic_lic and lm.fech_fina_lic) existe  from diasMes ")  
																				  .append(" ) where existe = 0");
	  
		
		
		private static final StringBuffer SELECT_DIAS_SIN_LICENCIA_INI = new StringBuffer("select dia from ( ")
				  																  .append(" with diasMes(dia) as ")
				  																  .append(" (SELECT to_date(? , 'yyyy/mm/dd') +  LEVEL-1 dia  FROM DUAL ")
				  																  .append(" CONNECT BY LEVEL <=  (to_date(? , 'yyyy/mm/dd') - to_date(? , 'yyyy/mm/dd'))+1), ")     
				  																  .append(" licenciames as ( ")
				  																  .append(" select * from  licencias l ")
				  																  .append(" where ind_del='1' ")
				  																  .append(" and l.codi_empl_per = ? ")
				  																  .append(" and((l.fech_inic_lic >= to_date(? , 'yyyy/mm/dd') and l.fech_fina_lic <= to_date(? , 'yyyy/mm/dd')) ")
																				  .append(" or ( l.fech_inic_lic  >= to_date(? , 'yyyy/mm/dd') and l.fech_inic_lic <= to_date(? , 'yyyy/mm/dd') ) ")
																				  .append(" or ( l.fech_fina_lic  >= to_date(? , 'yyyy/mm/dd') and l.fech_fina_lic <= to_date(? , 'yyyy/mm/dd') ) ")
																				  .append(" or ( l.fech_fina_lic  <= to_date(? , 'yyyy/mm/dd') and l.fech_fina_lic >= to_date(? , 'yyyy/mm/dd') )) ")
																				  .append(" and l.codi_moti_tmo in (");       

		
		private static final StringBuffer FIND_ORIGINAL_POR_LIC_EMP =  new StringBuffer("")
		.append(" SELECT nvl(sum(nume_dias_pem),0) original ")
		.append(" FROM licencias_empleados ")
		.append(" WHERE cod_licencia=?")
		.append(" AND ann_lic_calc=?")
		.append(" AND mes_lic_calc=?")
		.append(" AND  ind_del = '1'  ");  //AGONZALESF -PAS20171U230200028 - solicitud de reintegro  
		

		//AGONZALESF -PAS20191U230200011 - solicitud de reintegro
		private static final StringBuffer GET_SUM_DIA_LICENCIA =  new StringBuffer("") 
		.append("select nvl(sum (nume_dias_pem),0) diasLicencia")
		.append("  from licencias_empleados le")
		.append("  join licencias l on l.cod_licencia = le.cod_licencia")		
		.append("  where le.ann_lic_calc = ?")
		.append("   and le.mes_lic_calc = ?")
		.append("   and le.ind_del <> '1'")
		.append("   and l.ind_del <> '1'")
		.append("   and l.codi_empl_per = ?")
		.append("   and trim (l.codi_moti_tmo) in (select  trim(cod_argumento)  as cod_argumento from t01parametro t where T.COD_PARAMETRO = '1243' and cod_estado = '1' and cod_tipo='D')");
		   
		
	  /**
	   * Método para validar el Datasource a usarse
	   * @param Object datasource
	   */
	  public LicenciasDAO(Object datasource) {
	    if (datasource instanceof DataSource)
	      this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.datasource = getDataSource((String)datasource);
	    else
        throw new DAOException(this, "Datasource no valido");
    }
    
    /**
     * Obtiene las Licencias y Subsidios  
     * @param params
     * @return List listaLicencias
     * @throws DAOException
     */  
    public List findByLicencias(Map params) throws DAOException {
      if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByLicencias()"));
      List listaLicencias = null;    
      StringBuffer strSQL = null;
      strSQL = new StringBuffer(FIND_LICENCIAS.toString());        
      listaLicencias = executeQuery(datasource, strSQL.toString(),
      new Object[]{params.get("anno"), params.get("mes"), params.get("codi_empl_per")});
      if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByLicencias() "));           
      return listaLicencias;
    }
      
	 
	/**
	 * AGONZALESF -PAS20171U230200001 - solicitud de reintegro   
	 * Funcion para encontrar los conceptos para reintegro
	 * @param filtro
	 * @return Lista de conceptos relacionados a licencias (subsidios, Permisos o Licencias)
	 * @throws SQLException
	 */
	public List findConceptosLicencia(Map filtro) throws SQLException { 		 
		List conceptos = new ArrayList();
		log.debug("Ingreso findConceptosLicencia"+ filtro);		
		try {			 
			if (!(filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES) || filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS))) {
				throw new Exception("No es la planilla correcta");
			}
			if (filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
				log.debug("Haberes sql  " + SELECT_CONCEPTO_LICENCIAS_HABERES);
				conceptos =executeQuery(datasource, SELECT_CONCEPTO_LICENCIAS_HABERES.toString(),
					    new Object[]{						
						filtro.get("tipo_plan_tpl"),
						filtro.get("codi_empl_per"),
						filtro.get("anio"),
						filtro.get("mes"),
						filtro.get("cod_tiplic1"),
						filtro.get("cod_tiplic2"),
						filtro.get("cod_tiplic3") 
						
						});			 
			}
			if (filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
				log.debug("CAS sql  " + SELECT_CONCEPTO_LICENCIAS_CAS);
				conceptos =executeQuery(datasource, SELECT_CONCEPTO_LICENCIAS_CAS.toString(),
					    new Object[]{
					filtro.get("tipo_plan_tpl"),
					filtro.get("codi_empl_per"),
					filtro.get("anio"),
					filtro.get("mes"),
					filtro.get("cod_tiplic1"),
					filtro.get("cod_tiplic2"),
					filtro.get("cod_tiplic3") });
			} 	
			log.debug("conceptos: " +conceptos);
		} catch (Exception e) {
			log.error("No se puede obtener conceptos de licencia de base ", e);
			throw new SQLException(e.toString());
		}  
		return conceptos;
	}
	/**
	 * AGONZALESF -PAS20171U230200001 - solicitud de reintegro   
	 * Funcion para encontrar los detalle de licencias para reintegro
	 * @param filtro
	 * @return Licencias detalladas
	 * @throws SQLException
	 */	
	public List findDetalleLicencia(Map filtro) throws SQLException { 
		List detalles = new ArrayList();
		log.debug("Ingreso findDetalleLicencia" + filtro);
		if (filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
			log.debug("Haberes sql  " + SELECT_DETALLE_LICENCIA_HABERES);
			detalles =executeQuery(datasource, SELECT_DETALLE_LICENCIA_HABERES.toString(),
				    new Object[]{
					filtro.get("tipo_plan_tpl"),
					filtro.get("codi_empl_per"),
					filtro.get("anio") , 
					filtro.get("mes"),
					filtro.get("movimiento")});			 
		}
		if (filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
			log.debug("CAS sql  " + SELECT_DETALLE_LICENCIA_CAS);
			detalles =executeQuery(datasource, SELECT_DETALLE_LICENCIA_CAS.toString(),
					  new Object[]{
					filtro.get("tipo_plan_tpl"),
					filtro.get("codi_empl_per"),
					filtro.get("anio") , 
					filtro.get("mes"),
					filtro.get("movimiento")});
		} 	 
	 
		return detalles;
	}
	/**
	 * PAS20171U230200001 - solicitud de reintegro   
	 * Funcion para obtener fecha de inicio , fin de una licencia
	 * @param codigo : codigo de licencia
	 * @return
	 */
	public Map obtenerFechasLicencia(String codigo){
		return executeQueryUniqueResult(datasource, SELECT_LICENCIA_FECHAS, new Object[]{codigo});
	}
	
	
 
	/**
	 * PAS20171U230200001 - solicitud de reintegro  
	 * Obtener dias sin licencia
	 * @param params : filto para la busqueda , fecha de inicio, fin y codigo de empleado
	 * @return lista con fechas donde el colaborador no tiene licencia
	 */
	public List findDiasSinLicencia(Map params){
		StringBuffer strQuery = new StringBuffer(SELECT_DIAS_SIN_LICENCIA_INI.toString());
		
		String[] motivos = Constantes.TMOT_SIN_GOCE.split("-"); 
		
		for (int i = 0; i < motivos.length; i++){
			strQuery.append("'")
					.append(motivos[i])
					.append("'");
			
			if (i < motivos.length - 1) {
				strQuery.append(", ");
			}
		}
		
		strQuery.append(SELECT_DIAS_SIN_LICENCIA_FIN.toString());
		
		return executeQuery(datasource, strQuery.toString(), new Object[]{params.get("fecInicio"), params.get("fecFin"), params.get("fecInicio"), 
																		  params.get("codiEmplPer"), params.get("fecInicio"), params.get("fecFin"), 
																		  params.get("fecInicio"), params.get("fecFin"), params.get("fecInicio"), 
																		  params.get("fecFin"), params.get("fecInicio"), params.get("fecFin")});
	}
	
	
	/**
	 * Metodo para hallar los dias originales de una licencia en un periodo determinado
	 * @param params
	 * @return Lista de devoluciones ya ingresadas
	 */
	public Map findOriginalLicencia(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - findDevueltoPeriodoLicencia()-> " + params); 
		Map original = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_licencia"), 
				params.get("ann_lic_calc"), 					
				params.get("mes_lic_calc")  }; 
		original = executeQueryUniqueResult(datasource, FIND_ORIGINAL_POR_LIC_EMP.toString(), filtro);		
	 
		if (log.isDebugEnabled()) log.debug("FIN - findDevueltoPeriodoLicencia()");
		return original;
	}
	
	
	//AGONZALESF -PAS20191U230200011 - solicitud de reintegro
	/**
	 * Metodo para numero de dias de licencia por periodo 
	 * @param params
	 * @return Lista de devoluciones ya ingresadas
	 */
	public Map getSumDiasLicencia(Map params) {
		if (log.isDebugEnabled()) log.debug(" INICIO - getSumDiasLicencia()-> " + params); 
		Map original = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("codiEmplPer"), 
				params.get("anno"), 					
				params.get("numero")  }; 
		original = executeQueryUniqueResult(datasource, GET_SUM_DIA_LICENCIA.toString(), filtro); 
		if (log.isDebugEnabled()) log.debug("FIN - getSumDiasLicencia()");
		return original;
	}
	 
	
}