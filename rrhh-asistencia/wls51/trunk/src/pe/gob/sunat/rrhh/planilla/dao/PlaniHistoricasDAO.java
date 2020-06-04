package pe.gob.sunat.rrhh.planilla.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * <p>Title: PlaniHistoricasDAO </p>
 * <p>Description: Clase para realizar la consulta de Planilla Historicas</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class PlaniHistoricasDAO extends DAOAbstract {
  private DataSource datasource; // Variable creada para instanciar el DataSource
  
    private static final StringBuffer FIND_DETALLE_LIQUIDACION_HISTORICO = new StringBuffer("SELECT ")
  .append(" '1' TIPO, '*' CODIGO, 'I N G R E S O S :' DESC_TIPO, '' INGRESOS, '' DESCUENTOS, '' APORTES ")
  .append(" FROM DUAL ")
  .append(" UNION ALL ( ")
  .append(" SELECT  CONCEPTOS.TIPO_CONC_TCO, CONCEPTOS.CODI_CONC_TCO, '      '||CONCEPTOS.DESC_CONC_TCO, ")
  .append(" TO_CHAR(SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI),'999,999,999.99') COL1, '' COL2, '' COL3 ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" ( CONCEPTOS.TIPO_CONC_TCO = '1' ) AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ")
  .append(" GROUP BY  CONCEPTOS.TIPO_CONC_TCO, CONCEPTOS.CODI_CONC_TCO, CONCEPTOS.DESC_CONC_TCO) ")
  .append(" UNION ALL ( SELECT '2', '*', 'D E S C U E N T O S :', '', '', '' FROM DUAL ) ")
  .append(" UNION ALL ( ")
  .append(" SELECT CONCEPTOS.TIPO_CONC_TCO, CONCEPTOS.CODI_CONC_TCO, '      '||CONCEPTOS.DESC_CONC_TCO, ")
  .append(" '' COL1, TO_CHAR(SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI),'999,999,999.99'), '' COL3 ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" CONCEPTOS.TIPO_CONC_TCO = '2' AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ")
  .append(" GROUP BY   CONCEPTOS.TIPO_CONC_TCO, CONCEPTOS.CODI_CONC_TCO, CONCEPTOS.DESC_CONC_TCO) ")
  .append(" UNION ALL ( SELECT '3', '*', 'A P O R T E S :', '', '', '' FROM DUAL ) ")
  .append(" UNION ALL ( ")
  .append(" SELECT CONCEPTOS.TIPO_CONC_TCO, CONCEPTOS.CODI_CONC_TCO, '      '||CONCEPTOS.DESC_CONC_TCO, ")
  .append(" '' COL1, '' COL2, TO_CHAR(SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI),'999,999,999.99') ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" CONCEPTOS.TIPO_CONC_TCO = '3' AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ")
  .append(" GROUP BY   CONCEPTOS.TIPO_CONC_TCO, CONCEPTOS.CODI_CONC_TCO, CONCEPTOS.DESC_CONC_TCO) ")
  .append(" UNION ALL ( SELECT '4', '*', 'TOTALES', ")
  .append(" ( SELECT TO_CHAR(SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI),'999,999,999.99') ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" (CONCEPTOS.TIPO_CONC_TCO = '1' ) AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ) I, ")
  .append(" ( SELECT TO_CHAR(SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI),'999,999,999.99') ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" ( CONCEPTOS.TIPO_CONC_TCO = '2' ) AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")  //add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ) D, ")
  .append(" ( SELECT TO_CHAR(SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI),'999,999,999.99') ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" (CONCEPTOS.TIPO_CONC_TCO = '3' ) AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND  ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ) A ")
  .append(" FROM DUAL) ")
  .append(" ORDER BY 1,2 ");
  
  private static final StringBuffer FIND_TOTALES_LIQUIDACION_HISTORICO = new StringBuffer(" SELECT 'TOTALES(S/.)', ")  
  .append(" ( SELECT SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI) ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" (CONCEPTOS.TIPO_CONC_TCO = '1' ) AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ") //add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ) TOT_ING, ")
  .append(" ( SELECT SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI) ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" ( CONCEPTOS.TIPO_CONC_TCO = '2' ) AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ) TOT_DESC, ")
  .append(" ( SELECT SUM(PLANILLA_HISTORICAS.VALO_CALC_PHI) ")
  .append(" FROM PLANILLA_HISTORICAS, TPLANILLA, TPERIODOS, CONCEPTOS ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = PLANILLA_HISTORICAS.TIPO_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.SUBT_PLAN_TPL = PLANILLA_HISTORICAS.SUBT_PLAN_TPL ) and ")
  .append(" ( TPERIODOS.ANO_PERI_TPE = PLANILLA_HISTORICAS.ANO_PERI_TPE ) and ")
  .append(" ( TPERIODOS.NUME_PERI_TPE = PLANILLA_HISTORICAS.NUME_PERI_TPE ) and ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( TPERIODOS.TIPO_PLAN_TPL =  TPLANILLA.TIPO_PLAN_TPL ) and ")
  .append(" ( TPLANILLA.CODI_COND_LAB = ? ) AND ")
  .append(" ( TPERIODOS.ESTA_PLAN_TPE = ? ) AND ")
  .append(" (CONCEPTOS.TIPO_CONC_TCO = '3' ) AND ")  
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = '02' ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ")//add
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")//add  
  .append(" ( PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ) ) TOT_APOR ")
  .append(" FROM DUAL ");  
  
  //Detalle de la Boleta - Haberes
  private static final StringBuffer FIND_DETALLE_BOLETA_PLANILLAS_276_728_CAS = new StringBuffer(" SELECT PLANILLA_HISTORICAS.codi_conc_tco as concepto, desc_conc_tco as descripcion, SEG_CIFRADO.DESENCRIPTA(trim(PLANILLA_HISTORICAS.mto_valorbase_cif),seg_cifrado.segcifrado())as monto ")	
	.append(" FROM PLANILLA_HISTORICAS,TPLANILLA,TPERIODOS,CONCEPTOS ")
	.append(" WHERE ( TPLANILLA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) and ")
	.append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) and ")
	.append(" ( PLANILLA_HISTORICAS.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) and ")
	.append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE  ) AND ")
	.append(" ( PLANILLA_HISTORICAS.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
	.append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
	.append(" ( PLANILLA_HISTORICAS.ANO_PERI_TPE = ? ) AND ( PLANILLA_HISTORICAS.NUME_PERI_TPE = ? ) AND ")
	.append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TPL = ?)  AND ")
	.append(" ( PLANILLA_HISTORICAS.Subt_Plan_Tpl =? ) AND ")	
	.append(" ( CONCEPTOS.codi_cicl_cic = ? ) AND PLANILLA_HISTORICAS.CODI_EMPL_PER = ? ")
	.append(" order by PLANILLA_HISTORICAS.codi_conc_tco asc ");

  
  
  private static final StringBuffer FIND_DETALLE_EXTORNO_BOLETA_PLANILLAS_276_728_CAS=new StringBuffer(" SELECT PLANILLA_HISTORICAS.codi_conc_tco as concepto, desc_conc_tco as descripcion, SEG_CIFRADO.DESENCRIPTA(trim(PLANILLA_HISTORICAS.mto_valorbase_cif),seg_cifrado.segcifrado())as monto ")
  .append(" FROM planilla_temporal planilla_historicas,TPLANILLA,TPERIODOS,CONCEPTOS ")
  .append(" WHERE ( TPLANILLA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) and ")
  .append(" ( PLANILLA_HISTORICAS.TIPO_PLAN_TEM = TPERIODOS.TIPO_PLAN_TPL ) and ") 
  .append(" ( PLANILLA_HISTORICAS.SUBT_PLAN_TEM = TPERIODOS.SUBT_PLAN_TPL ) and ")
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TEM = TPERIODOS.ANO_PERI_TPE  ) AND ")
  .append(" ( PLANILLA_HISTORICAS.NUME_PERI_TEM = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append(" ( PLANILLA_HISTORICAS.CODI_CONC_TCO = CONCEPTOS.CODI_CONC_TCO ) and ")
  .append(" ( PLANILLA_HISTORICAS.ANO_PERI_TEM = ? ) ")
  .append(" AND ( PLANILLA_HISTORICAS.NUME_PERI_TEM = ? ) ") 
  .append(" AND ( PLANILLA_HISTORICAS.TIPO_PLAN_TEM = ? ) ")
  .append(" AND( PLANILLA_HISTORICAS.SUBT_PLAN_TEM =? )  ")
  .append(" AND ( CONCEPTOS.codi_cicl_cic = ? ) ")
  .append(" AND PLANILLA_HISTORICAS.Codi_Empl_Tem = ? ")
  .append(" order by PLANILLA_HISTORICAS.Codi_Conc_Tco asc ");
  
  //Conceptos en planila historicos para la Compensacion Tiempo Servicio CTS. WRODRIGUEZRE
  
  private static final StringBuffer FIND_CONCEPTOS_PLANILLA = new StringBuffer("SELECT PH.CODI_CONC_TCO codiCto, ")
  .append("C.DESC_CONC_TCO descCto, ")
  .append("SEG_CIFRADO.DESENCRIPTA( PH.MTO_VALORCALC_CIF, SEG_CIFRADO.SEGCIFRADO() ) AS mtoBase,  ")
  .append("SEG_CIFRADO.DESENCRIPTA( PH.MTO_VALORBASE_CIF, SEG_CIFRADO.SEGCIFRADO() ) AS totalRemuCpt  ")
  .append("FROM PLANILLA_HISTORICAS PH ")
  .append("INNER JOIN CONCEPTOS C ON PH.CODI_CONC_TCO = C.CODI_CONC_TCO ")
  .append("WHERE PH.TIPO_PLAN_TPL= '06' ")
  .append("AND PH.SUBT_PLAN_TPL = ?  ")
  .append("AND PH.ANO_PERI_TPE = ?  ")
  .append("AND PH.NUME_PERI_TPE = ?  ")
  .append("AND PH.CODI_EMPL_PER = ?  ")
  .append("AND PH.CODI_CONC_TCO IN ")
  .append("(SELECT AC.CODI_CONC_TCO ")
  .append("FROM ACUMU_CONCEPTOS AC ")
  .append("WHERE AC.CODI_ACUM_TAC IN ('AC601','AC602') ")
  .append("AND AC.FLAG_USAR_TCA = '1' ) ");
  
  
  /**
   * Método para validar el Datasource a usarse
   * @param Object datasource
   */
  public PlaniHistoricasDAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }
  
  /**
   * Obtiene el fetalle para la boleta CTS
   * @param params Map
   * @return Map detalles de la boleta CTS 
   * @throws DAOException
   */
  public List getDetalleBoletaCTS(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat("getDetalleBoletaCTS"));
//	List<BoletaCTSBean> detalleBoletaCTS = null;    
	List detalleBoletaCTS = new ArrayList();
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_CONCEPTOS_PLANILLA.toString());     
    	
	    detalleBoletaCTS =(ArrayList)executeQuery(datasource, strSQL.toString(),
	    new Object[]{params.get("subt_plan_tpl"), params.get("ano_peri_tpe"), params.get("mes"), params.get("codi_empl_per")});  
    return detalleBoletaCTS;
  }
  
  /**
   * Obtiene el detalle de la liquidación histórico por código de personal para Reporte de Liquidación  
   * @param params Map
   * @return List detalleLiquidacion
   * @throws DAOException
   */  
  public List findByDetalleLiquidacionHistorico(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByDetalleLiquidacionHistorico()"));
    List detalleLiquidacion = null;    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_DETALLE_LIQUIDACION_HISTORICO.toString());        
    detalleLiquidacion = executeQuery(datasource, strSQL.toString(),
      new Object[]{params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per"),
        params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per"),
        params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per"),
        params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per"),
        params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per"),
        params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per")});        
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByDetalleLiquidacionHistorico() ")); 
    return detalleLiquidacion;
  }

  /**
   * Obtiene los montos totales de la liquidaci�n por c�digo de personal
   * @param params Map
   * @return Map totalesLiquidacion 
   * @throws DAOException
   */
  public Map findByTotalesLiquidacionHistorico(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByTotalesLiquidacionHistorico()"));
    Map totalesLiquidacion = new HashMap();    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_TOTALES_LIQUIDACION_HISTORICO.toString());      
    totalesLiquidacion = executeQueryUniqueResult(datasource, strSQL.toString(),
      new Object[]{params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per"),                  
         params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per"),
         params.get("codi_cond_lab"), params.get("esta_plan_tpe"), params.get("anno"), params.get("mes"), params.get("codi_empl_per")});  
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByTotalesLiquidacionHistorico() ")); 
    return totalesLiquidacion;
  }
  
  /**
	 * Obtiene el detalle de los conceptos a pagar por trabajador Boleta-Haberes  
	 * @param params
	 * @return List
	 * @throws DAOException
	 */	
	public List findByDetalleBoleta(Map params,String tipoConcepto) throws DAOException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByDetalleBoleta()"));
		List detalleLiquidacion = null;		
		try {			
		    StringBuffer strSQL = null;
		    strSQL = new StringBuffer(FIND_DETALLE_BOLETA_PLANILLAS_276_728_CAS.toString());		    
		    detalleLiquidacion = executeQuery(datasource, strSQL.toString(),
					new Object[]{params.get("anio"), params.get("mes"),params.get("tipo_plan_tpl"), params.get("subtipoplan"),
		    					 tipoConcepto, params.get("codi_empl_per")});				
		} catch(Exception e) {
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - findByDetalleBoleta() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByDetalleBoleta() "));
		return  detalleLiquidacion;
	}
	
	
	public List findByDetalleBoletaWhitExtorno(Map params,String tipoConcepto) throws DAOException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByDetalleBoletaWhitExtorno()"));
		List detalleLiquidacion = null;		
		try {			
		    StringBuffer strSQL = null;
		    strSQL = new StringBuffer(FIND_DETALLE_EXTORNO_BOLETA_PLANILLAS_276_728_CAS.toString());		    
		    detalleLiquidacion = executeQuery(datasource, strSQL.toString(),
					new Object[]{params.get("anio"), params.get("mes"),params.get("tipo_plan_tpl"), params.get("subtipoplan"),
		    					 tipoConcepto, params.get("codi_empl_per")});				
		} catch(Exception e) {
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - findByDetalleBoletaWhitExtorno() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByDetalleBoletaWhitExtorno() "));
		return  detalleLiquidacion;
	}
  
}
