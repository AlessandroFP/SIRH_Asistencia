package pe.gob.sunat.rrhh.planilla.dao;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: TPeriodosDAO </p>
 * <p>Description: Clase para realizar la consulta de Periodos</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class TPeriodosDAO extends DAOAbstract {
  private DataSource datasource; // Variable creada para instanciar el DataSource


  private static final StringBuffer FIND_PERIODOS_ASISTENCIA =  new StringBuffer("")
  .append(" SELECT DISTINCT TPERIODOS.ANO_PERI_TPE, TPERIODOS.NUME_PERI_TPE,  ")
  .append(" TPERIODOS.SUBT_PLAN_TPL, TPERIODOS.FECHA_PLAZO_MAXIMO, TPERIODOS.ESTA_PLAN_TPE   ")
  .append(" FROM TPERIODOS ")
  .append(" JOIN T01PARAMETRO ON (T01PARAMETRO.COD_PARAMETRO = '1002'  ")
  .append(" AND T01PARAMETRO.COD_MODULO = ?  ")
  .append(" AND T01PARAMETRO.COD_TIPO = 'D'  ")
  .append(" AND T01PARAMETRO.COD_ESTADO = '1'   ")
  .append(" AND TPERIODOS.ANO_PERI_TPE || TPERIODOS.NUME_PERI_TPE = T01PARAMETRO.COD_ARGUMENTO) ")
  .append(" WHERE TPERIODOS.SUBT_PLAN_TPL = ?  ")
  .append(" AND TPERIODOS.TIPO_PLAN_TPL = ? ") //'02'
  .append(" ORDER BY TPERIODOS.ANO_PERI_TPE DESC, TPERIODOS.NUME_PERI_TPE DESC "); 
  
  private static final StringBuffer FIND_PERIODOS_LIQUIDACION = new StringBuffer("")
  .append(" SELECT DISTINCT TPERIODOS.ANO_PERI_TPE, ")
  .append(" TPERIODOS.NUME_PERI_TPE ")
  .append(" FROM TPERIODOS, TPLANILLA, CONDICION_LABORAL, PLANILLA_CALCULADA ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = TPLANILLA.TIPO_PLAN_TPL ) AND ")
  .append(" ( TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ) AND ")
  .append(" ( CONDICION_LABORAL.CODI_COND_LAB = ? ) AND  ")
  .append(" TPERIODOS.ESTA_PLAN_TPE = ? AND ")
  .append(" (TPERIODOS.ano_peri_tpe||TPERIODOS.nume_peri_tpe < ?) AND ")
  .append(" ( PLANILLA_CALCULADA.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE ) AND ")
  .append(" ( PLANILLA_CALCULADA.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append(" ( PLANILLA_CALCULADA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append(" ( PLANILLA_CALCULADA.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) ")      
  .append(" ORDER BY TPERIODOS.ANO_PERI_TPE DESC, TPERIODOS.NUME_PERI_TPE DESC ");  
  
  private static final StringBuffer FIND_PERIODOS_CONFORMIDAD =  new StringBuffer("")
  .append(" SELECT DISTINCT TPERIODOS.ANO_PERI_TPE, ")
  .append(" TPERIODOS.NUME_PERI_TPE, '1' as SUBT_PLAN_TPL, max( FECHA_PLAZO_MAXIMO) as FECHA_PLAZO_MAXIMO, ESTA_PLAN_TPE ")
  .append(" FROM TPERIODOS, TPLANILLA, CONDICION_LABORAL, PLANILLA_CALCULADA WHERE ( TPERIODOS.TIPO_PLAN_TPL = TPLANILLA.TIPO_PLAN_TPL ) AND ")
  .append(" ( TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ) AND ( CONDICION_LABORAL.CODI_COND_LAB = ? ) AND  ")
  .append(" ESTA_PLAN_TPE = ? AND ")
  .append(" ( PLANILLA_CALCULADA.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE ) AND ")
  .append(" ( PLANILLA_CALCULADA.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append(" ( PLANILLA_CALCULADA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append(" ( PLANILLA_CALCULADA.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) ")
  .append(" group by  TPERIODOS.ANO_PERI_TPE, TPERIODOS.NUME_PERI_TPE, ESTA_PLAN_TPE")
  .append(" ORDER BY TPERIODOS.ANO_PERI_TPE DESC, TPERIODOS.NUME_PERI_TPE DESC ");
  
  private static final StringBuffer FIND_PERIODOS_RECIBO =  new StringBuffer("SELECT DISTINCT TPERIODOS.ANO_PERI_TPE, ")
  .append(" TPERIODOS.NUME_PERI_TPE, TPERIODOS.SUBT_PLAN_TPL, FECHA_PLAZO_MAXIMO, ESTA_PLAN_TPE ")
  .append(" FROM TPERIODOS, TPLANILLA, CONDICION_LABORAL, PLANILLA_CALCULADA ")
  .append(" WHERE ( TPERIODOS.TIPO_PLAN_TPL = TPLANILLA.TIPO_PLAN_TPL ) AND ")
  .append(" ( TPLANILLA.CODI_COND_LAB = CONDICION_LABORAL.CODI_COND_LAB ) AND ( CONDICION_LABORAL.CODI_COND_LAB = ? ) AND  ")
  .append(" ESTA_PLAN_TPE = ? AND ")
  .append(" ( PLANILLA_CALCULADA.ANO_PERI_TPE = TPERIODOS.ANO_PERI_TPE ) AND ")
  .append(" ( PLANILLA_CALCULADA.NUME_PERI_TPE = TPERIODOS.NUME_PERI_TPE ) AND ")
  .append(" ( PLANILLA_CALCULADA.TIPO_PLAN_TPL = TPERIODOS.TIPO_PLAN_TPL ) AND ")
  .append(" ( PLANILLA_CALCULADA.SUBT_PLAN_TPL = TPERIODOS.SUBT_PLAN_TPL ) AND ")
  .append(" ( PLANILLA_CALCULADA.CODI_EMPL_PER = ? ) ")    
  .append(" ORDER BY TPERIODOS.ANO_PERI_TPE DESC, TPERIODOS.NUME_PERI_TPE DESC ");
  
  private static final StringBuffer FIND_FECHAS_PERIODO =  new StringBuffer("")
  .append("SELECT FECH_INIC_TPE, FECH_FINA_TPE, FECHA_PAGO, COD_ARC_FIRMA ")
  .append("FROM TPERIODOS ")
  .append("WHERE ANO_PERI_TPE = ? ")
  .append("AND NUME_PERI_TPE = ? ")
  .append("AND SUBT_PLAN_TPL = ? ")
  .append("AND TIPO_PLAN_TPL = ? ");
//  .append("and FECHA_PAGO is not null ");
  
  //dtarazona
  private static final StringBuffer FIND_CONTRATOS =  new StringBuffer("")
  .append("select  cp.CODIGO_CONVENIO,to_char(CP.FECHA_INICIO,'dd/mm/YYYY') as fechaini, to_char(CP.FECHA_FIN,'dd/mm/YYYY') as fechafin, CP.FECHA_REAL_TERMINO, CP.EMPLEADO, CP.COD_NIVE_NVL, CP.COD_ACCION, CP.CANT_HORAS_SEMANALES, ")
  .append(" CP.COD_REGIMEN, CP.COD_JORNADA,CP.DEPENDENCIA,p.NUMERO_REGISTRO_ALTERNO,ap.TIPO_ACCION,cp.ESTADO ")
  .append(" from convenios_personal cp, MAESTRO_PERSONAL p , ACCIONES_PERSONAL ap ")
  .append(" where ")
  .append(" p.NUMERO_REGISTRO_ALTERNO=? AND p.CODI_EMPL_PER=cp.EMPLEADO  and ap.ACCION_ID=cp.COD_ACCION and ap.TIPO_ACCION in ('0001','0002','0003') ")
  .append(" and ((cp.FECHA_INICIO>=? and cp.FECHA_INICIO<=?) OR (cp.FECHA_FIN>=? and cp.FECHA_FIN<=?)OR(CP.FECHA_INICIO<? and cp.FECHA_FIN>?) OR (CP.FECHA_INICIO>=? and cp.FECHA_FIN<=?)) ")
  .append(" order by cp.FECHA_INICIO ASC ");
  
  //Búsqueda de periodos de Boleta-SIGA 
  private static final StringBuffer FIND_PERIODOS_BOLETA=new StringBuffer(" select (p.ANO_PERI_TPE || p.nume_peri_tpe || p.subt_plan_tpl) as codigo, ")
  .append(" (trim(p.DESC_PERI_TPE)||'  '||p.ANO_PERI_TPE||' '|| s.desc_subt_stp) as descripcion ")
  .append(" from hist_metas_empleado hme, tperiodos p, subtplanilla s ")
  .append("  where hme.tipo_plan_tpl = p.tipo_plan_tpl ")
  .append("  and hme.subt_plan_tpl = p.subt_plan_tpl ")
  .append("  and hme.ano_peri_hme = p.ano_peri_tpe ")
  .append("  and hme.nume_peri_hme = p.nume_peri_tpe ")
  .append("  and p.tipo_plan_tpl = s.tipo_plan_tpl ")
  .append("  and p.subt_plan_tpl = s.subt_plan_stp ")
  .append("  and hme.tipo_plan_tpl=? ")
  .append("  and hme.codi_empl_per=?  ")
  .append("  and p.esta_plan_tpe='2' ")
  .append("  and p.ind_visualizacion='1' ")  
   .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) >= ? ")
   .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) < ? ")
  .append("  order by  p.ano_peri_tpe, p.nume_peri_tpe, p.subt_plan_tpl asc  ");
  
  //Busqueda de periodos de Boleta-SIGA CAS
  private static final StringBuffer FIND_PERIODOS_CAS=new StringBuffer(" select (p.ANO_PERI_TPE || p.nume_peri_tpe || p.subt_plan_tpl) as codigo, ")
  .append(" (trim(p.DESC_PERI_TPE)||'  '||p.ANO_PERI_TPE||' '|| s.desc_subt_stp) as descripcion ")
  .append(" from hist_metas_empleado hme, tperiodos p, subtplanilla s ")
  .append("  where hme.tipo_plan_tpl = p.tipo_plan_tpl ")
  .append("  and hme.subt_plan_tpl = p.subt_plan_tpl ")
  .append("  and hme.ano_peri_hme = p.ano_peri_tpe ")
  .append("  and hme.nume_peri_hme = p.nume_peri_tpe ")
  .append("  and p.tipo_plan_tpl = s.tipo_plan_tpl ")
  .append("  and p.subt_plan_tpl = s.subt_plan_stp ")
  .append("  and hme.tipo_plan_tpl=? ")
  .append("  and hme.codi_empl_per=?  ")
  .append("  and p.esta_plan_tpe='2' ")
  .append("  and p.ind_visualizacion='1' ")  
  .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) >= ? ")
  .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) < ? ")
   .append("  order by  p.ano_peri_tpe, p.nume_peri_tpe, p.subt_plan_tpl asc  ");
  
  //PAS201220C00000148 EMR se agrega constante para consultar los periodos disponibles para actualizar datos de suspensi�n
  //Búsqueda de periodos de suspension de cuarta 
  private static final StringBuffer FIND_PERIODOS_SUSPENSION =  new StringBuffer("")
  .append(" SELECT P.ANO_PERI_TPE, P.NUME_PERI_TPE, P.SUBT_PLAN_TPL, P.FECHA_PLAZO_MAXIMO, P.ESTA_PLAN_TPE, ")
  .append(" (TRIM(P.DESC_PERI_TPE)||' '||P.ANO_PERI_TPE||' '|| TRIM(S.DESC_SUBT_STP)) AS DESCRIPCION ")
  .append(" FROM METAS_EMPLEADO ME, TPERIODOS P, SUBTPLANILLA S  ")
  .append(" WHERE ME.TIPO_PLAN_TPL = P.TIPO_PLAN_TPL  ")
  .append(" AND ME.SUBT_PLAN_TPL = P.SUBT_PLAN_TPL  ")
  .append(" AND P.TIPO_PLAN_TPL = S.TIPO_PLAN_TPL  ")
  .append(" AND P.SUBT_PLAN_TPL = S.SUBT_PLAN_STP  ")
  .append(" AND ME.TIPO_PLAN_TPL = '02' ")
  .append(" AND ME.CODI_EMPL_PER = ? ")
  .append(" AND P.ESTA_PLAN_TPE = '1' ")
  .append(" AND NVL(P.IND_VISUALIZACION,'0') = '1'")
  .append(" ORDER BY  P.ANO_PERI_TPE DESC, P.NUME_PERI_TPE DESC, P.SUBT_PLAN_TPL ASC ");
  
  
  //Búsqueda de periodos de Boleta-SIGA 2_VERSION  avargascasq 20150720
  private static final StringBuffer FIND_PERIODOS_BOLETA_2=new StringBuffer(" select (p.ANO_PERI_TPE || p.nume_peri_tpe || p.subt_plan_tpl|| p.ind_asistencia || NVL(p.cod_arc_firma,'0')) as codigo, ")
  .append(" (trim(tp.DESC_TIPO_TPL)||' - '|| TRIM(S.DESC_SUBT_STP)) as descripcion, ")
  .append(" p.cod_arc_firma as cod_arc_firma,  ")
  .append(" trim(s.desc_subt_stp) as subtipo,  ")
  .append(" trim(hme.ind_consulta) as indconsulta ")
  .append(" from hist_metas_empleado hme, tperiodos p, subtplanilla s, TPLANILLA tp  ")
  .append("  where hme.tipo_plan_tpl = p.tipo_plan_tpl ")
  .append("  and hme.subt_plan_tpl = p.subt_plan_tpl ")
  .append("  and hme.ano_peri_hme = p.ano_peri_tpe ")
  .append("  and hme.nume_peri_hme = p.nume_peri_tpe ")
  .append("  and p.tipo_plan_tpl = s.tipo_plan_tpl ")
  .append("  and p.tipo_plan_tpl = tp.tipo_plan_tpl ")
  .append("  and p.subt_plan_tpl = s.subt_plan_stp ")
  .append("  and hme.tipo_plan_tpl=? ")
  .append("  and hme.codi_empl_per=?  ")
  .append("  and p.esta_plan_tpe='2' ")
  .append("  and p.ind_visualizacion='1' ")  
   .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) >= ? ")
   .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) >= ? ")
  .append("  order by  p.ano_peri_tpe, p.nume_peri_tpe, p.subt_plan_tpl asc  ");
  
  //Busqueda de periodos de Boleta-SIGA CAS  2_VERSION  avargascasq 20150720
  private static final StringBuffer FIND_PERIODOS_CAS_2=new StringBuffer(" select (p.ANO_PERI_TPE || p.nume_peri_tpe || p.subt_plan_tpl|| p.ind_asistencia || NVL(p.cod_arc_firma,'0')) as codigo, ")
  .append(" (trim(tp.DESC_TIPO_TPL)||' - '|| TRIM(S.DESC_SUBT_STP)) as descripcion , ")
  .append(" p.cod_arc_firma as cod_arc_firma,  ")
  .append(" trim(s.desc_subt_stp) as subtipo, ")
  .append(" trim(hme.ind_consulta) as indconsulta ")
  .append(" from hist_metas_empleado hme, tperiodos p, subtplanilla s, TPLANILLA tp  ")
  .append("  where hme.tipo_plan_tpl = p.tipo_plan_tpl ")
  .append("  and hme.subt_plan_tpl = p.subt_plan_tpl ")
  .append("  and hme.ano_peri_hme = p.ano_peri_tpe ")
  .append("  and hme.nume_peri_hme = p.nume_peri_tpe ")
  .append("  and p.tipo_plan_tpl = s.tipo_plan_tpl ")
  .append("  and p.tipo_plan_tpl = tp.tipo_plan_tpl ")
  .append("  and p.subt_plan_tpl = s.subt_plan_stp ")
  .append("  and hme.tipo_plan_tpl=? ")
  .append("  and hme.codi_empl_per=?  ")
  .append("  and p.esta_plan_tpe='2' ")
  .append("  and p.ind_visualizacion='1' ")  
  .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) >= ? ")
  .append("  and (p.ano_peri_tpe||p.nume_peri_tpe) >= ? ")
   .append("  order by  p.ano_peri_tpe, p.nume_peri_tpe, p.subt_plan_tpl asc  ");
  
  //Búsqueda de periodos de BoletaCTS-SIGA wrodriguezre 20180708
  private static final StringBuffer FIND_PERIODOS_BOLETA_CTS=new StringBuffer(" select (p.ANO_PERI_TPE || p.nume_peri_tpe || p.subt_plan_tpl|| p.ind_asistencia || NVL(p.cod_arc_firma,'0')) as codigo, ")
  .append(" (trim(tp.DESC_TIPO_TPL)||' - '|| TRIM(S.DESC_SUBT_STP)) as descripcion, ")
  .append(" p.cod_arc_firma as cod_arc_firma,  ")
  .append(" trim(s.desc_subt_stp) as subtipo,  ")
  .append(" trim(hme.ind_consulta) as indconsulta, ")
  .append(" trim(HME.NUME_PERI_HME) as numeperihme ")
  .append(" from hist_metas_empleado hme, tperiodos p, subtplanilla s, TPLANILLA tp  ")
  .append("  where hme.tipo_plan_tpl = p.tipo_plan_tpl ")
  .append("  and hme.subt_plan_tpl = p.subt_plan_tpl ")
  .append("  and hme.ano_peri_hme = p.ano_peri_tpe ")
  .append("  and hme.nume_peri_hme = p.nume_peri_tpe ")
  .append("  and p.tipo_plan_tpl = s.tipo_plan_tpl ")
  .append("  and p.tipo_plan_tpl = tp.tipo_plan_tpl ")
  .append("  and p.subt_plan_tpl = s.subt_plan_stp ")
  .append("  and hme.tipo_plan_tpl='06' ")
  .append("  and hme.codi_empl_per=?  ")
  .append("  and p.esta_plan_tpe='2' ")
  .append("  and p.ind_visualizacion='1' ")  
  .append(" and p.ano_peri_tpe||p.nume_peri_tpe >=?  ")
  .append("  order by  p.ano_peri_tpe, p.nume_peri_tpe, p.subt_plan_tpl asc  ");
  
  //PAS20171U230200001 - solicitud de reintegro    
  private static final StringBuffer FIND_BY_TPLANILLA = new StringBuffer("SELECT tp.tipo_plan_tpl, tp.subt_plan_tpl, st.desc_subt_stp,  tp.ano_peri_tpe, tp.nume_peri_tpe, ")
  																 .append("       tp.fech_inic_tpe, tp.fech_fina_tpe ") 
  															     .append("FROM tperiodos tp, subtplanilla st ") 
  															     .append("WHERE st.tipo_plan_tpl = tp.tipo_plan_tpl and st.subt_plan_stp = tp.subt_plan_tpl and ") 
  		  														 .append("tp.tipo_plan_tpl = ? AND tp.subt_plan_tpl = '1' AND tp.esta_plan_tpe = '1' ");
  
  //PAS20171U230200033 - solicitud de reintegro , agrega ind_bloqueo a query 
  private static final StringBuffer FIND_EST_TPERIODO =  new StringBuffer("select t.esta_plan_tpe, t.ind_bloqueo from tperiodos t where tipo_plan_tpl = ?  and subt_plan_tpl = ? and ano_peri_tpe = ?  and nume_peri_tpe =?");
  
  
 //PAS20181U230200067 - solicitud de reintegro, planillas adicionales  
  private static final StringBuffer FIND_BY_DESCRIPCION_PLANILLA = new StringBuffer("")
  .append(" select p.tipo_plan_tpl , p.subt_plan_tpl , p.ano_peri_tpe , p.nume_peri_tpe , s.desc_subt_stp from tperiodos p")
  .append(" left join tplanilla t on t.tipo_plan_tpl = p.tipo_plan_tpl ")
  .append(" left join subtplanilla s on s.tipo_plan_tpl =p.tipo_plan_tpl and s.subt_plan_stp =p.subt_plan_tpl")
  .append(" where p.tipo_plan_tpl = ?")
  .append(" and p.subt_plan_tpl = ?")
  .append(" and p.ano_peri_tpe =?")
  .append(" and p.nume_peri_tpe =?");

  
  /**
   * Método para validar el Datasource a usarse
   * @param Object datasource
   */
  public TPeriodosDAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }
  
  /**
   * Obtiene los periodos para la consulta de asistencia cas
   * @param params Map
   * @return List lista
   * @throws DAOException
   */  
  public List findByPeriodoAsistencia(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByPeriodoAsistencia()"));
    List lista = null;
    lista = executeQuery(datasource, FIND_PERIODOS_ASISTENCIA.toString(),
    new Object[]{params.get("cod_modulo"), params.get("subt_plan_tpl"), params.get("tipo_plan_tpl") });                
    if (log.isDebugEnabled()) log.debug("cod_modulo: " + params.get("cod_modulo"));        
    if (log.isDebugEnabled()) log.debug("subt_plan_tpl: " + params.get("subt_plan_tpl")); 
    if (log.isDebugEnabled()) log.debug("tipo_plan_tpl: " + params.get("tipo_plan_tpl"));
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodoAsistencia() "));
    return lista;
  }
  
  /**
   * Obtiene los periodos para la consulta de Liquidación CAS
   * @param params Map
   * @return List lista
   * @throws DAOException
   */  
  public List findByPeriodoLiquidacionCAS(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByPeriodoLiquidacionCAS()"));
    List lista = null;
    lista = executeQuery(datasource, FIND_PERIODOS_LIQUIDACION.toString(),    		
    		new Object[]{ params.get("codi_cond_lab"), params.get("esta_plan_tpe"),params.get("mesCorte")});
    if (log.isDebugEnabled()) log.debug("codi_cond_lab: " + params.get("codi_cond_lab"));
    if (log.isDebugEnabled()) log.debug("esta_plan_tpe: " + params.get("esta_plan_tpe"));   
    if (log.isDebugEnabled()) log.debug("mesCorte: " + params.get("mesCorte"));   
    
      
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodoLiquidacionCAS() "));
    return lista;
  }
  
  /**
   * Obtiene los periodos para conformidad de servicio
   * @param params Map
   * @return List listaPeriodos
   * @throws DAOException
   */  
  public List findByPeriodoConformidadCAS(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByPeriodoConformidadCAS()"));
    List listaPeriodo = null;
    listaPeriodo = executeQuery(datasource, FIND_PERIODOS_CONFORMIDAD.toString(),
      new Object[]{ params.get("codi_cond_lab"), params.get("esta_plan_tpe")});          
    if (log.isDebugEnabled()) log.debug("codi_cond_lab: " + params.get("codi_cond_lab"));
    if (log.isDebugEnabled()) log.debug("esta_plan_tpe: " + params.get("esta_plan_tpe"));      
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodoConformidadCAS() "));
    return listaPeriodo;
  }
  
  /**
   * Obtiene los periodos para registro de recibo CAS
   * @param params Map
   * @return lista List
   * @throws DAOException
   */
  public List findByPeriodoReciboCAS(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByPeriodoReciboCAS()"));
    List lista = null;
    lista = executeQuery(datasource, FIND_PERIODOS_RECIBO.toString(),
      new Object[]{ params.get("codi_cond_lab"), params.get("esta_plan_tpe"),params.get("subt_plan_tpl"), params.get("codi_empl_per")});
    if (log.isDebugEnabled()) log.debug("codi_cond_lab: " + params.get("codi_cond_lab"));
    if (log.isDebugEnabled()) log.debug("esta_plan_tpe: " + params.get("esta_plan_tpe"));
    if (log.isDebugEnabled()) log.debug("codi_empl_per: " + params.get("codi_empl_per"));
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodoReciboCAS() "));
    return lista;
  }
  
  /**
   * Obtiene las fechas del periodo para listar asistencia y descuentos 
   * @param params Map
   * @return Map fechasPeriodo
   * @throws DAOException
   */
  public Map findByFechasPeriodo(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByFechasPeriodo()"));
    Map fechasPeriodo = null;    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_FECHAS_PERIODO.toString());      
    fechasPeriodo = executeQueryUniqueResult(datasource, strSQL.toString(),
      new Object[]{params.get("anno"), params.get("mes"), params.get("subt_plan_tpl"), params.get("tipo_plan_tpl")});
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByFechasPeriodo() "));
    return fechasPeriodo;
  }
  
  /**
   * Obtiene todos los contratos dentro del rango de fechas 
   * @param params Map
   * @return Map fechasPeriodo
   * @throws DAOException
   */
  public List findContratosByFechas(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findContratosByFechaIniFechaFin()"));
    List contrato = null;    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_CONTRATOS.toString()); 
    log.debug("ConsultaCont:"+strSQL);
    contrato = executeQuery(datasource, strSQL.toString(),new Object[]{params.get("codPers"),params.get("fechaIni"),params.get("fechaFin"),params.get("fechaIni"),params.get("fechaFin"),params.get("fechaIni"),params.get("fechaFin"),params.get("fechaIni"),params.get("fechaFin")});
    return contrato;
  }
  
  /**
	 * Obtiene los periodos para las Boletas de Haberes
	 * @return lista List
	 * @throws DAOException
	 */
	public List findByPeriodosBoleta(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByPeriodosBoleta()"));
		List lista = null;
		try {
			String tipo_plan=(String)params.get("tipo_plan_tpl");

			if(tipo_plan.equals("01")){
				lista=executeQuery(datasource, FIND_PERIODOS_BOLETA.toString(),new Object[]{params.get("tipo_plan_tpl"), params.get("codi_empl_per"),params.get("mesCorte"),params.get("mesCorteInicioBoletaDigital")});
			
			}else if(tipo_plan.equals("02")){
				lista=executeQuery(datasource, FIND_PERIODOS_CAS.toString(),new Object[]{params.get("tipo_plan_tpl"), params.get("codi_empl_per"),params.get("mesCorte"),params.get("mesCorteInicioBoletaDigital")});
		   }
				
		}catch(Exception e){
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - findByPeriodosBoleta() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodosBoleta() "));
		return lista;
	}

  //PAS201220C00000148 EMR se agrega metodo para consultar los periodos disponibles para actualizar suspensi�n
  /**
   * Obtiene los periodos para registro de suspensión de renta de cuarta para CAS // 2_version avargascasq 20150720
   * @param params Map
   * @return lista List
   * @throws DAOException
   */
  public List findByPeriodoSuspensionCAS(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByPeriodoSuspensionCAS()"));
    List lista = null;
    lista = executeQuery(datasource, FIND_PERIODOS_SUSPENSION.toString(),
      new Object[]{params.get("codi_empl_per")});
    if (log.isDebugEnabled()) log.debug("codi_empl_per: " + params.get("codi_empl_per"));
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodoSuspensionCAS() "));
    return lista;
  }
  
  /**
	 * Obtiene los periodos para las Boletas de Haberes // 2_version avargascasq 20150720
	 * @return lista List
	 * @throws DAOException
	 */
	public List findByPeriodosBoleta2(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByPeriodosBoleta()"));
		List lista = null;
		try {
			String tipo_plan=(String)params.get("tipo_plan_tpl");

			if(tipo_plan.equals("01")){
				lista=executeQuery(datasource, FIND_PERIODOS_BOLETA_2.toString(),new Object[]{params.get("tipo_plan_tpl"), params.get("codi_empl_per"),params.get("mesCorte"),params.get("mesCorteInicioBoletaDigital")});
			
			}else if(tipo_plan.equals("02")){
				lista=executeQuery(datasource, FIND_PERIODOS_CAS_2.toString(),new Object[]{params.get("tipo_plan_tpl"), params.get("codi_empl_per"),params.get("mesCorte"),params.get("mesCorteInicioBoletaDigital")});
		   }
				
		}catch(Exception e){
			StringBuffer msg = new StringBuffer()
			.append(" ERROR - findByPeriodosBoleta() : ").append(e.toString());
			if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
			throw new DAOException(this, msg.toString());
		}
		if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodosBoleta() "));
		return lista;
	}

	  /**
		 * Obtiene los periodos para las Boletas CTS wrodriguezre 20160708
		 * @return lista List
		 * @throws DAOException
		 */
		public List findByPeriodosBoletaCTS(Map params) throws DAOException {
			if (log.isDebugEnabled()) log.debug("findByPeriodosBoletaCTS");
			List lista = null;
			try {
				//Es boleta CTS. No se necesita tipo plan (Ya esta en la query)
//					lista=executeQuery(datasource, FIND_PERIODOS_BOLETA_CTS.toString(),new Object[]{params.get("tipo_plan_tpl"), params.get("codi_empl_per"),params.get("mesCorte"),params.get("mesCorteInicioBoletaDigital")});
				lista=executeQuery(datasource, FIND_PERIODOS_BOLETA_CTS.toString(),new Object[]{params.get("codi_empl_per"),params.get("anioMesCorte")});		
				log.debug("la lista de los periodos en findByPeridosBoletasCTS[]"+lista);
			}catch(Exception e){
				StringBuffer msg = new StringBuffer()
				.append(" ERROR - findByPeriodosBoletaCTS : ").append(e.toString());
				if (log.isDebugEnabled()) log.debug(this.toString() + msg.toString());
				throw new DAOException(this, msg.toString());
			}
			if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByPeriodosBoletaCTS "));
			return lista;
		}

  
	//PAS20171U230200001 - solicitud de reintegro    
	/**
	 * Metodo que obtiene periodo activo por tipo de planilla 
	 * @param tPlanilla : tipo de planilla
	 * @return Mapa con datos de periodo
	 */
	public Map findByTPlanilla(String tPlanilla){
		return executeQueryUniqueResult(datasource, FIND_BY_TPLANILLA.toString(), new String[]{tPlanilla});
	}
  
	//PAS20171U230200001 - solicitud de reintegro    
	/**
	 * Metodo que obtiene estado  de periodo de la planilla 
	 * @param params : mapa de datos de planilla
	 * @return Mapa estado de planilla
	 */
	public Map findEstadoByTPeriodo(Map  params){
		return executeQueryUniqueResult(datasource, FIND_EST_TPERIODO.toString(), new Object[]{params.get("tipo_plan_tpl"), params.get("subt_plan_tpl"),params.get("ano_peri_tpe"),params.get("nume_peri_tpe")});
	}
	
	 //PAS20181U230200067 - solicitud de reintegro, planillas adicionales   
	/**
	 * Metodo que obtiene estado  de periodo de la planilla 
	 * @param params : mapa de datos de planilla
	 * @return Mapa estado de planilla
	 */
	public Map findDescripcionByTPeriodo(Map  params){
		return executeQueryUniqueResult(datasource, FIND_BY_DESCRIPCION_PLANILLA.toString(), new Object[]{params.get("tipo_plan_tpl"), params.get("subt_plan_tpl"),params.get("ano_peri_tpe"),params.get("nume_peri_tpe")});
	}
}