package pe.gob.sunat.rrhh.planilla.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * <p>Title: MaestroPersonalDAO </p>
 * <p>Description: Clase para realizar la consulta de Maestro de Personal</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class MaestroPersonalDAO extends DAOAbstract {
  private DataSource datasource; // Variable creada para instanciar el DataSource
  //PAS20165E230300154 lretamozov
  private static final StringBuffer FIND_COD_REGISTRO = new StringBuffer("")
  .append("SELECT CODI_EMPL_PER, CODI_COND_LAB, OTRO_DOCU_PER, REG_LAB_PER, ")
  .append("APE_PAT_PER, APE_MAT_PER, NOM_EMP_PER, TIPO_PLAN_TPL, CODI_DEPE_TDE ")
  .append("FROM MAESTRO_PERSONAL ")
  .append("WHERE NUMERO_REGISTRO_ALTERNO = upper(?)");
  
  //PAS201220C00000148 EMR se actualiza constante, se agrega TRIM a la descripcion de banco y cuenta
  private static final StringBuffer FIND_CABECERA_REPORTE = new StringBuffer(" SELECT ")
  .append(" CODI_EMPL_PER, NUMERO_REGISTRO_ALTERNO, NOMB_CORT_PER, ")  
  .append(" (SELECT DESC_NIVE_NVL FROM NIVELES WHERE CODI_NIVE_NVL = MAESTRO_PERSONAL.CODI_NIVE_TNI) CATEGORIA, ")
  .append(" LIBR_ELEC_PER, FEC_ING_PER, ")
  .append(" (SELECT UNIDAD_ORGANIZACIONAL FROM TDEPENDENCIAS WHERE CODI_DEPE_TDE = MAESTRO_PERSONAL.CODI_DEPE_TDE) CODUUOO, ")
  .append(" (SELECT DESC_DEPE_TDE FROM TDEPENDENCIAS WHERE CODI_DEPE_TDE = MAESTRO_PERSONAL.CODI_DEPE_TDE) UUOO, ")  
  .append(" (SELECT TRIM(NOMB_BANC_BAN) FROM BANCO WHERE CODI_BANC_BAN = MAESTRO_PERSONAL.BANC_SUEL_TBC) BANCO, ")
  .append(" TRIM(SUEL_CTA_PER) SUEL_CTA_PER, TIPO_CUEN_PER, ")
  .append(" (SELECT NOMB_AFP FROM TAFP WHERE  CODI_AFP = MAESTRO_PERSONAL.CODI_AFP) AFP, CODI_AFP_PER ")
  .append("FROM MAESTRO_PERSONAL ")
  .append("WHERE MAESTRO_PERSONAL.CODI_EMPL_PER = ? ");
  
  private static final StringBuffer FIND_CABECERA_REPORTE_HIST = new StringBuffer("")
  .append("SELECT ")
  .append("M.CODI_EMPL_PER, M.NUMERO_REGISTRO_ALTERNO, M.NOMB_CORT_PER, ")
  .append("(SELECT DESC_NIVE_NVL FROM NIVELES ")
  .append("WHERE CODI_NIVE_NVL = H.CODI_NIVE_NVL) CATEGORIA, ")
  .append("M.LIBR_ELEC_PER, M.FEC_ING_PER, ")
  .append("(SELECT UNIDAD_ORGANIZACIONAL FROM TDEPENDENCIAS ")
  .append("WHERE CODI_DEPE_TDE = H.CODI_DEPE_TDE) CODUUOO, ")
  .append("(SELECT DESC_DEPE_TDE FROM TDEPENDENCIAS ")
  .append("WHERE CODI_DEPE_TDE = H.CODI_DEPE_TDE) UUOO, ")
  .append("(SELECT NOMB_BANC_BAN FROM BANCO ")
  .append("WHERE CODI_BANC_BAN = H.CODIGO_BANCO) BANCO, ")
  .append("H.NUMERO_CUENTA_SUELDO AS SUEL_CTA_PER, '1' AS TIPO_CUEN_PER, ")
  .append("(SELECT NOMB_AFP FROM TAFP ")
  .append("WHERE CODI_AFP = H.CODI_AFP) AFP, M.CODI_AFP_PER ")
  .append("FROM MAESTRO_PERSONAL M, HIST_METAS_EMPLEADO H ")
  .append("WHERE M.CODI_EMPL_PER = H.CODI_EMPL_PER AND ")
  .append("M.CODI_EMPL_PER = ? AND ") 
  .append("H.ANO_PERI_HME = ? AND ") 
  .append("H.NUME_PERI_HME = ? "); 
  
  private static final StringBuffer FIND_PERSONAL_CAS_UO =  new StringBuffer("")
  .append("SELECT ")
  .append("MAESTRO_PERSONAL.OTRO_DOCU_PER, MAESTRO_PERSONAL.NUMERO_REGISTRO_ALTERNO, ") 
  .append("MAESTRO_PERSONAL.CODI_EMPL_PER, MAESTRO_PERSONAL.NOMB_CORT_PER, ")
  .append("(SELECT  TCARGOS.DESC_CARG_TCA FROM MAESTRO_PERSONAL, TCARGOS ")
  .append("WHERE ( MAESTRO_PERSONAL.CODI_CARG_TCA = TCARGOS.CODI_CARG_TCA ) ")   
  .append("AND ( MAESTRO_PERSONAL.CODI_EMPL_PER = METAS_EMPLEADO.CODI_EMPL_PER ) ) CARGO, ") 
  .append("METAS_EMPLEADO.FLAG_APROBACION_RECIBO, METAS_EMPLEADO.FECHA_APROBACION_RECIBO, ")
  .append("METAS_EMPLEADO.NUME_SERI_REC, METAS_EMPLEADO.NUME_COMP_REC, ")
  .append("METAS_EMPLEADO.FECH_EMIS_REC, METAS_EMPLEADO.FLAG_SUSPENSION_4TA, ") 
  .append("METAS_EMPLEADO.NUMERO_FORM_SUSPENSION_4TA, METAS_EMPLEADO.FECHA_FORM_SUSPENSION, ") 
  .append("(SELECT SUM (PLANILLA_CALCULADA.VALO_CALC_PCA) ")
  .append("FROM PLANILLA_CALCULADA, TPLANILLA, TPERIODOS, CONCEPTOS ") 
  .append("WHERE ( TPLANILLA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append("( PLANILLA_CALCULADA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ") 
  .append("( PLANILLA_CALCULADA.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) AND ")
  .append("( PLANILLA_CALCULADA.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE  ) AND ")
  .append("( PLANILLA_CALCULADA.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append("( PLANILLA_CALCULADA.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) AND ")
  .append("( PLANILLA_CALCULADA.ANO_PERI_TPE = ? ) AND ") 
  .append("( PLANILLA_CALCULADA.NUME_PERI_TPE = ? ) AND ")
  .append("( PLANILLA_CALCULADA.TIPO_PLAN_TPL = ? ) AND ")
  .append("( CONCEPTOS.TIPO_CONC_TCO = ? )  AND ")  
  .append("PLANILLA_CALCULADA.CODI_EMPL_PER = METAS_EMPLEADO.CODI_EMPL_PER ) BASE, ")  
  .append("(SELECT SUM (DECODE( CONCEPTOS.TIPO_CONC_TCO,'1',1,-1)*PLANILLA_CALCULADA.VALO_CALC_PCA) ")
  .append("FROM PLANILLA_CALCULADA, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append("WHERE ( TPLANILLA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append("( PLANILLA_CALCULADA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append("( PLANILLA_CALCULADA.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) AND ")
  .append("( PLANILLA_CALCULADA.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE  ) AND ")
  .append("( PLANILLA_CALCULADA.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append("( PLANILLA_CALCULADA.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) AND ")
  .append("( PLANILLA_CALCULADA.ANO_PERI_TPE = ? ) AND ") 
  .append("( PLANILLA_CALCULADA.NUME_PERI_TPE = ? ) AND ") 
  .append("( PLANILLA_CALCULADA.TIPO_PLAN_TPL = ? ) AND ") 
  .append("( CONCEPTOS.TIPO_CONC_TCO IN(?, ?) )  AND ") 
  .append("PLANILLA_CALCULADA.CODI_EMPL_PER = METAS_EMPLEADO.CODI_EMPL_PER ) AS MONT_TOTA_REC, ")
  .append(" '1' AS ESTADO ")
  .append("FROM MAESTRO_PERSONAL, TDEPENDENCIAS, METAS_EMPLEADO, TPLANILLA, CONDICION_LABORAL ")
  .append("WHERE MAESTRO_PERSONAL.CODI_DEPE_TDE =  TDEPENDENCIAS. CODI_DEPE_TDE ")
  .append("AND TDEPENDENCIAS.UNIDAD_ORGANIZACIONAL = ? ")
  .append("AND TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ")
  .append("AND TPLANILLA.TIPO_PLAN_TPL = ? ") 
  .append("AND METAS_EMPLEADO.TIPO_PLAN_TPL = TPLANILLA.TIPO_PLAN_TPL ") 
  .append("AND METAS_EMPLEADO.SUBT_PLAN_TPL IN ( SELECT SUBT_PLAN_TPL FROM TPERIODOS ") 
  .append("WHERE TIPO_PLAN_TPL = ? AND ANO_PERI_TPE = ? ") 
  .append("AND NUME_PERI_TPE = ? AND ESTA_PLAN_TPE = ? ) ") 
  .append("AND METAS_EMPLEADO.CODI_EMPL_PER = MAESTRO_PERSONAL.CODI_EMPL_PER ") 
  .append("AND CONDICION_LABORAL.CODI_COND_LAB = ? ") 
  .append("AND ( METAS_EMPLEADO.TIPO_PLAN_TPL  IN  ( SELECT DISTINCT TPLANILLA.TIPO_PLAN_TPL ") 
  .append("FROM TPLANILLA, CONDICION_LABORAL ")
  .append("WHERE ( TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ) ") 
  .append("AND  ( CONDICION_LABORAL.CODI_COND_LAB = ? ) ) ) ")
  .append("UNION ")
  .append("SELECT ")
  .append("MAESTRO_PERSONAL.OTRO_DOCU_PER, MAESTRO_PERSONAL.NUMERO_REGISTRO_ALTERNO, ")
  .append("MAESTRO_PERSONAL.CODI_EMPL_PER, MAESTRO_PERSONAL.NOMB_CORT_PER, ")
  .append("(SELECT  TCARGOS.DESC_CARG_TCA FROM MAESTRO_PERSONAL, TCARGOS ")
  .append("WHERE ( MAESTRO_PERSONAL.CODI_CARG_TCA = TCARGOS.CODI_CARG_TCA ) ")
  .append("AND ( MAESTRO_PERSONAL.CODI_EMPL_PER = HIST_METAS_EMPLEADO.CODI_EMPL_PER ) ) CARGO, ")
  .append("HIST_METAS_EMPLEADO.FLAG_APROBACION_RECIBO, HIST_METAS_EMPLEADO.FECHA_APROBACION_RECIBO, ")
  .append("HIST_METAS_EMPLEADO.NUME_SERI_REC, ")
  .append("HIST_METAS_EMPLEADO.NUME_COMP_REC, HIST_METAS_EMPLEADO.FECH_EMIS_REC, ")
  .append("HIST_METAS_EMPLEADO.FLAG_SUSPENSION_4TA, ")
  .append("HIST_METAS_EMPLEADO.NUMERO_FORM_SUSPENSION_4TA, HIST_METAS_EMPLEADO.FECHA_FORM_SUSPENSION, ")
  .append("(SELECT SUM (PLANILLA_HISTORICAS.VALO_CALC_PHI) ")
  .append("FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append("WHERE ( TPLANILLA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append("( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append("( PLANILLA_HISTORICAS.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) AND ")
  .append("( PLANILLA_HISTORICAS.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE  ) AND ")
  .append("( PLANILLA_HISTORICAS.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append("( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) AND ")
  .append("( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ") 
  .append("( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ") 
  .append("( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = ? ) AND ") 
  .append("( CONCEPTOS.TIPO_CONC_TCO = ? )  AND ") 
  .append("PLANILLA_HISTORICAS.CODI_EMPL_PER = HIST_METAS_EMPLEADO.CODI_EMPL_PER ) BASE, ")
  .append("(SELECT SUM (DECODE( CONCEPTOS.TIPO_CONC_TCO,'1',1,-1)*PLANILLA_HISTORICAS.VALO_CALC_PHI) ")
  .append("FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append("WHERE ( TPLANILLA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append("( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append("( PLANILLA_HISTORICAS.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) AND ")
  .append("( PLANILLA_HISTORICAS.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE  ) AND ")
  .append("( PLANILLA_HISTORICAS.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append("( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) AND ")
  .append("( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ") 
  .append("( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ") 
  .append("( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = ? ) AND ") 
  .append("( CONCEPTOS.TIPO_CONC_TCO IN(?, ?) )  AND ") 
  .append("PLANILLA_HISTORICAS.CODI_EMPL_PER = HIST_METAS_EMPLEADO.CODI_EMPL_PER ) AS MONT_TOTA_REC, ")
  .append("'0' AS ESTADO ")
  .append("FROM MAESTRO_PERSONAL, TDEPENDENCIAS, HIST_METAS_EMPLEADO, TPLANILLA, CONDICION_LABORAL ")
  .append("WHERE MAESTRO_PERSONAL.CODI_DEPE_TDE =  TDEPENDENCIAS. CODI_DEPE_TDE ")
  .append("AND TDEPENDENCIAS.UNIDAD_ORGANIZACIONAL = ? ") 
  .append("AND TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ")
  .append("AND TPLANILLA.TIPO_PLAN_TPL = ? ") 
  .append("AND HIST_METAS_EMPLEADO.ANO_PERI_HME = ? AND HIST_METAS_EMPLEADO.NUME_PERI_HME = ? ") 
  .append("AND HIST_METAS_EMPLEADO.TIPO_PLAN_TPL = TPLANILLA.TIPO_PLAN_TPL ")
  .append("AND HIST_METAS_EMPLEADO.CODI_EMPL_PER = MAESTRO_PERSONAL.CODI_EMPL_PER ")
  .append("AND CONDICION_LABORAL.CODI_COND_LAB = ? ") 
  .append("AND ( HIST_METAS_EMPLEADO.TIPO_PLAN_TPL  IN  ( SELECT DISTINCT TPLANILLA.TIPO_PLAN_TPL ")
  .append("FROM TPLANILLA, CONDICION_LABORAL ")
  .append("WHERE ( TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ) ")
  .append("AND  ( CONDICION_LABORAL.CODI_COND_LAB = ? ) ) ) ") 
  .append("ORDER BY ESTADO DESC, NOMB_CORT_PER ASC "); 
    
  //PAS201220C00000148 EMR se agrega constante para validar que exista el RUC
  private static final StringBuffer FIND_BY_RUC = new StringBuffer("")
  .append(" select siga.pkg_servicios_persona.valida_ruc( ? ) as estado_ruc from dual ");

  
  private static final StringBuffer FIND_CODI_EMPL_PER= new StringBuffer("")
  .append("SELECT CODI_EMPL_PER,ESTA_TRAB_PER, FECH_CESE_PER")
  .append(" FROM MAESTRO_PERSONAL ")
  .append(" WHERE NUMERO_REGISTRO_ALTERNO = upper(?) ");
  
  
  /**
   * M�todo para validar el Datasource a usarse
   * @param Object datasource
   */
  public MaestroPersonalDAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }
  
  /**
   * Obtiene el codigo del empleado por el numero de registro
   * @param params Map
   * @return Map regTrab
   * @throws DAOException
   */
  public Map findByCodReg(Map params) throws DAOException {    
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByCodReg()"));
    Map regTrab = new HashMap();
    regTrab = executeQueryUniqueResult(datasource, FIND_COD_REGISTRO.toString(),
    new Object[]{params.get("codRegistro")});
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByCodReg() ")); 
    return regTrab;
  }
  /**
	 * Obtiene el c�digo del empleado por el n�mero de registro
	 * @param parm String
	 * @return Map
	 * @throws DAOException
	 */
	public Map findByCodReg(String parm) throws DAOException {		
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByCodReg(String) Parametro.:").concat(parm));
		Map regTrab = new HashMap();
		try {
			regTrab = executeQueryUniqueResult(datasource, FIND_COD_REGISTRO.toString(),
					new Object[]{parm});
			log.debug("reg: "+regTrab);
			
		} catch(Exception e) {
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - findByCodRegBoleta() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByCodReg() "));
		return regTrab;
	}
  /**
   * Obtiene los datos de la cabecera para reporte
   * @param params Map
   * @return Map Cabecera
   * @throws DAOException
   */
  public Map findByCabeceraReporte(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByCabeceraReporte()"));
    Map cabecera = new HashMap();    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_CABECERA_REPORTE.toString());
    cabecera = executeQueryUniqueResult(datasource, strSQL.toString(),
    	new Object[]{ params.get("codi_empl_per")});
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByCabeceraReporte() ")); 
    return cabecera;
  }
  
  /**
   * Obtiene la lista de colaboradores CAS para una UUOO 
   * @param Map params
   * @return List lista
   * @throws DAOException
   */
  public List finByPersonalCasUO(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - finByPersonalCasUO()"));
    List lista = new ArrayList();
    if (log.isDebugEnabled()) log.debug("anno: " + params.get("anno"));
    if (log.isDebugEnabled()) log.debug("mes: " + params.get("mes"));
    if (log.isDebugEnabled()) log.debug("tipo_plan_tpl: " + params.get("tipo_plan_tpl"));
    if (log.isDebugEnabled()) log.debug("concepto: " + params.get("concepto"));
    if (log.isDebugEnabled()) log.debug("tipoconcepto: " + params.get("tipoconcepto"));      
    if (log.isDebugEnabled()) log.debug("codi_empl_per: " + params.get("codi_empl_per"));
    if (log.isDebugEnabled()) log.debug("codi_depe: " + params.get("codi_depe"));
    if (log.isDebugEnabled()) log.debug("codi_cond_lab: " + params.get("codi_cond_lab"));
    lista = executeQuery(datasource, FIND_PERSONAL_CAS_UO.toString(), new Object[]{ params.get("anno").toString().trim(), 
    	params.get("mes").toString().trim(), params.get("tipo_plan_tpl").toString().trim(), params.get("concepto").toString().trim(),
    	params.get("anno").toString().trim(), params.get("mes").toString().trim(), params.get("tipo_plan_tpl").toString().trim(), 
    	params.get("concepto").toString().trim(), params.get("tipoconcepto").toString().trim(), params.get("codi_depe").toString().trim(),
    	params.get("tipo_plan_tpl").toString().trim(), params.get("tipo_plan_tpl").toString().trim(), params.get("anno").toString().trim(),
    	params.get("mes").toString().trim(), params.get("esta_plan_tpe").toString().trim(), params.get("codi_cond_lab").toString().trim(),
    	params.get("codi_cond_lab").toString().trim(), params.get("anno").toString().trim(), params.get("mes").toString().trim(),
    	params.get("tipo_plan_tpl").toString().trim(), params.get("concepto").toString().trim(), params.get("anno").toString().trim(),
    	params.get("mes").toString().trim(), params.get("tipo_plan_tpl").toString().trim(), params.get("concepto").toString().trim(),
    	params.get("tipoconcepto").toString().trim(), params.get("codi_depe").toString().trim(), params.get("tipo_plan_tpl").toString().trim(),
    	params.get("anno").toString().trim(), params.get("mes").toString().trim(), params.get("codi_cond_lab").toString().trim(),
    	params.get("codi_cond_lab").toString().trim() });
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - finByPersonalCasUO() "));
    return lista;
  }
  
  /**
   * Obtiene los datos hist�ricos de la cabecera para reporte PDF
   * @param params Map
   * @return Map Cabecera
   * @throws DAOException
   */
  public Map findByCabeceraReporteHistorico(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByCabeceraReporteHistorico()"));
    Map cabecera = new HashMap();    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_CABECERA_REPORTE_HIST.toString());
    cabecera = executeQueryUniqueResult(datasource, strSQL.toString(),
    	new Object[]{ params.get("codi_empl_per"), params.get("anno"), params.get("mes")});
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByCabeceraReporteHistorico() ")); 
    return cabecera;
  }  
  
  //PAS201220C00000148 EMR se agrega m�todo para validar si existe el RUC
  /**
   * Valida el RUC
   * @param params Map
   * @return String estadoRUC
   * @throws DAOException
   */
  public String findByRUC(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByRUC()"));
    Map datosRecibo = new HashMap();
    if (log.isDebugEnabled()) log.debug("txtRUC::" + params.get("txtRUC")); 
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_BY_RUC.toString());    
    datosRecibo = executeQueryUniqueResult(datasource, strSQL.toString(),
       new Object[]{params.get("txtRUC")}); 
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByRUC() ")); 
    if (log.isDebugEnabled()) log.debug("datosRecibo>>RUC:"+datosRecibo); 
    
    return (String)datosRecibo.get("estado_ruc");
  }
  
  //PAS201220C00000148 EMR se agrega m�todo para actualizar RUC
  /**
   * Actualiza RUC para CAS
   * @param Map params
   * @throws DAOException
   */  
  public void updateRUCCAS(Map params) throws DAOException {  
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - updateRUCCAS()"));
    if (log.isDebugEnabled()) log.debug("txtRUC: " + params.get("txtRUC"));
    if (log.isDebugEnabled()) log.debug("codi_empl_per: " + params.get("codi_empl_per"));
    StringBuffer strSql = new StringBuffer("UPDATE MAESTRO_PERSONAL ")
    .append("set OTRO_DOCU_PER = '")
    .append(params.get("txtRUC"))
    .append("' where CODI_EMPL_PER = '") 
    .append(params.get("codi_empl_per"))
    .append("' AND TIPO_PLAN_TPL = '02'");
    executeUpdate(datasource, strSql.toString(), new Object[]{});
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - updateRUCCAS() ")); 
  }

  //AGONZALESF -PAS20191U230200011 - solicitud de reintegro
  /**
   * Busqueda de colaborador por cod siga
   * @param params
   * @return
   */
	public Map findEstadoByCodPers(String codPers) {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findEstadoByCodPers()"));
	    Map regTrab = new HashMap();
	    regTrab = executeQueryUniqueResult(datasource, FIND_CODI_EMPL_PER.toString(),
	    new Object[]{codPers});
	    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findEstadoByCodPers() ")); 
	    return regTrab;
	  }
	
	
}
