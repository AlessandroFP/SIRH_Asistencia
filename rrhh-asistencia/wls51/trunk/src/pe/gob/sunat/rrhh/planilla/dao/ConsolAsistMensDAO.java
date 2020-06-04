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
 * <p>Title: ConsolAsistMensDAO </p>
 * <p>Description: Clase para realizar la consulta de Consolidado Asistencia Mensual</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class ConsolAsistMensDAO extends DAOAbstract {
  private DataSource datasource; // Variable creada para instanciar el DataSource

  private static final StringBuffer FIND_ASISTENCIA_MENSUAL = new StringBuffer("SELECT ")
  .append(" T.TIPO_MOVI_PER, S.DESC_TIPO_TPR, T.CANT_MINU_PER, ")
  .append(" T.CNT_MINU_REGU, T.OBS_ASISTENCIA ")
  .append(" FROM CONSOLIDADO_ASISTENCIA_MENSUAL T, TIPO_SALIDAS S ")
  .append(" WHERE T.PERI_ANUA_PER = ? ")
  .append(" AND T.PERI_MENS_PER = ? ")
  .append(" AND T.CODI_EMPL_PER = ? ")
  .append(" AND T.TIPO_MOVI_PER = S.CODI_MOVI_TPR ")
  .append(" AND S.DSCT_PAGO_TPR = ? ");
  
  
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer FIND_CONCEPTO_ASISTENCIA_HABERES = new StringBuffer("SELECT c.codi_conc_tco AS concepto,")
			.append(" c.desc_conc_tco AS desc_concepto,")
			.append(" SUM(NVL(cant_minu_per, 0) + NVL(cnt_minu_regu, 0)) minutos_dscto,")
			.append(" tipo_movi_per AS tipo_movimiento,")
			.append(" desc_tipo_tpr AS desc_movimiento")
			.append(" FROM consolidado_asistencia_mensual asi, tipo_salidas sal, conceptos c")
			.append(" WHERE asi.codi_empl_per = ?")
			.append(" AND asi.tipo_plan_tpl = ?")
			.append(" AND asi.peri_anua_per = ?")
			.append(" AND asi.peri_mens_per = ?")
			.append(" AND asi.tipo_movi_per = sal.codi_movi_tpr")
			.append(" AND sal.cod_concepto = c.codi_conc_tco")
			.append(" GROUP BY c.codi_conc_tco,")
			.append(" c.desc_conc_tco,   ")
			.append(" tipo_movi_per,")
			.append(" desc_tipo_tpr");

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	private static final StringBuffer FIND_CONCEPTO_ASISTENCIA_CAS = new StringBuffer("SELECT c.codi_conc_tco AS concepto,")
			.append(" c.desc_conc_tco AS desc_concepto,")
			.append(" SUM(NVL(cant_minu_per, 0) + NVL(cnt_minu_regu, 0)) minutos_dscto,")
			.append(" tipo_movi_per AS tipo_movimiento,")
			.append(" desc_tipo_tpr AS desc_movimiento")
			.append(" FROM consolidado_asistencia_mensual asi, tipo_salidas sal, conceptos c")
			.append(" WHERE asi.codi_empl_per = ?")
			.append(" AND asi.tipo_plan_tpl = ?")
			.append(" AND asi.peri_anua_per = ?")
			.append(" AND asi.peri_mens_per = ?")
			.append(" AND asi.tipo_movi_per = sal.codi_movi_tpr")
			.append(" AND sal.cod_concepto_cas = c.codi_conc_tco")
			.append(" GROUP BY c.codi_conc_tco, c.desc_conc_tco, tipo_movi_per, desc_tipo_tpr");
	
  /**
   * Método para validar el Datasource a usarse
   * @param Object datasource
   */
  public ConsolAsistMensDAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }
  
  /**
   * Obtiene el detalle de asisencias para descuentos por código de personal 
   * @param params
   * @return List listaAsistencia
   * @throws DAOException
   */  
  public List findByAsistenciaMensual(Map params) throws DAOException {
    if (log.isDebugEnabled()) log.debug(this.toString()
    		.concat(" INICIO - findByAsistenciaMensual()"));
    List listaAsistencia = null;    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_ASISTENCIA_MENSUAL.toString());        
    listaAsistencia = executeQuery(datasource, strSQL.toString(),
    new Object[]{params.get("anno"), params.get("mes"), params.get("codi_empl_per"),"S"});
    if (log.isDebugEnabled()) log.debug(this.toString()
    		.concat(" FIN - findByAsistenciaMensual() "));  
    return listaAsistencia;
  }
  
	
	/**
	 * AGONZALESF -PAS20171U230200001 - solicitud de reintegro  	
	 * Funcion para encontrar los conceptos para reintegro  
	 * @param filtro
	 * @return
	 * @throws SQLException
	 */
	public List findConceptosAsistencia(Map filtro) throws SQLException {
		List conceptos = new ArrayList();
		log.debug("Ingreso findConceptoAsistencia");
		log.debug("Datos del filtroIngreso  " + filtro);
		try {
			if (!(filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES) || filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS))) {
				throw new Exception("No es la planilla correcta");
			}
			if (filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
				log.debug("Seleccionar conceptos de planilla - haberes");
				log.debug(FIND_CONCEPTO_ASISTENCIA_HABERES);
				conceptos = executeQuery(datasource, FIND_CONCEPTO_ASISTENCIA_HABERES.toString(), new Object[] {//2017-01  pero pedimos 2016-12
						filtro.get("codi_empl_per"), filtro.get("tipo_plan_tpl"), filtro.get("anioConsulta"), filtro.get("mesConsulta") });
			}
			if (filtro.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
				log.debug("Seleccionar conceptos de planilla - cas"); //2017-01  
				log.debug(FIND_CONCEPTO_ASISTENCIA_CAS);
				conceptos = executeQuery(datasource, FIND_CONCEPTO_ASISTENCIA_CAS.toString(),
						new Object[] { filtro.get("codi_empl_per"), filtro.get("tipo_plan_tpl"), filtro.get("anio"), filtro.get("mes") });
			}

		} catch (Exception e) {
			log.error("No se puede obtener conceptos de asistencia de base ", e);
			throw new SQLException(e.toString());
		}
		return conceptos;
	}
}
