package pe.gob.sunat.rrhh.planilla.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/**
 * <p>Title: MetasEmpleadoDAO </p>
 * <p>Description: Clase para realizar la consulta de Metas Empleado</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class MetasEmpleadoDAO extends DAOAbstract {
  private DataSource datasource; // Variable creada para instanciar el DataSource
  
  private static final StringBuffer FIND_RECIBO_CAS = new StringBuffer(" SELECT ")
  .append(" NUME_SERI_REC, NUME_COMP_REC, FECH_EMIS_REC, MONT_TOTA_REC, FLAG_SUSPENSION_4TA, NUMERO_FORM_SUSPENSION_4TA, ")    
  .append(" FECHA_FORM_SUSPENSION, ANNO_EJEC_EJE, CODI_EMPL_PER, SUBT_PLAN_TPL, TIPO_PLAN_TPL ")        
  .append(" FROM METAS_EMPLEADO ")
  .append(" WHERE CODI_EMPL_PER = ? ")
  .append(" AND TIPO_PLAN_TPL = '02' " );
  
  //PAS201220C00000148 EMR se agrega constante para consultar datos de suspensión
  private static final StringBuffer FIND_SUSPENSION_CAS = new StringBuffer(" SELECT ")
  .append(" FLAG_SUSPENSION_4TA, NUMERO_FORM_SUSPENSION_4TA, ")    
  .append(" FECHA_FORM_SUSPENSION, CODI_EMPL_PER, SUBT_PLAN_TPL, TIPO_PLAN_TPL, TO_CHAR(SYSDATE,'YYYY') ANNO_ACTUAL ")        
  .append(" FROM METAS_EMPLEADO ")
  .append(" WHERE CODI_EMPL_PER = ? ")
  .append(" AND TIPO_PLAN_TPL = '02' " )
  .append(" AND SUBT_PLAN_TPL = ? " );
  
  //PAS20171U230200001 
  private static final String FIND_BY_TPLANILLA_REGEMP = "SELECT codi_empl_per FROM metas_empleado WHERE tipo_plan_tpl = ? AND subt_plan_tpl = '1' and num_registro = ?"; 
  
  
  //PAS20181U230200067  
  private static final String FIND_BY_TPLANILLA_REINTEGRO = " "
		 + " select ano_peri_tpe ,nume_peri_tpe, tipo_plan_tpl ,subt_plan_tpl,desc_subt_stp ,codi_empl_per from ( "
		 + " SELECT tp.ano_peri_tpe, tp.nume_peri_tpe, tp.tipo_plan_tpl , tp.subt_plan_tpl , st.desc_subt_stp ,  me.codi_empl_per  ,"
         + " ROW_NUMBER() OVER (order by tp.ano_peri_tpe desc ,  tp.nume_peri_tpe desc , tp.subt_plan_tpl asc) rn"
         + " FROM tperiodos tp, subtplanilla st , metas_empleado me "
         + " WHERE st.tipo_plan_tpl = tp.tipo_plan_tpl"
         + " and st.subt_plan_stp = tp.subt_plan_tpl"
         + " and me.tipo_plan_tpl = tp.tipo_plan_tpl"
         + " and me.subt_plan_tpl = tp.subt_plan_tpl  "         
         + " and tp.esta_plan_tpe = '1'"
         + " and tp.tipo_plan_tpl = ?"
         + " and me.codi_empl_per = ?"
         + " and tp.ano_peri_tpe ||  tp.nume_peri_tpe > ?"
         + " order by tp.ano_peri_tpe desc ,  tp.nume_peri_tpe desc , tp.subt_plan_tpl asc) where rn =1";

  /**
   * Método para validar el Datasource a usarse
   * @param Object datasource
   */
  public MetasEmpleadoDAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }
  
  /**
   * Obtiene los datos del Recibo por Honorarios y del Formulario de Suspensión
   * @param params Map
   * @return Map datosRecibo
   * @throws DAOException
   */
  public Map findByRecibo(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByRecibo()"));
    Map datosRecibo = new HashMap();    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_RECIBO_CAS.toString());    
    datosRecibo = executeQueryUniqueResult(datasource, strSQL.toString(),
       new Object[]{params.get("codi_empl_per"), params.get("subt_plan_tpl")});
    //PAS201220C00000148 EMR se agrega parametro para subtipo de planilla
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByRecibo() ")); 
    return datosRecibo;
  }
  
  /**
     * Actualiza los datos del Recibo por Honorarios CAS
     * @param Map params      
     * @throws DAOException
     */
  public void updateRecibo(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - updateRecibo()"));
    if (log.isDebugEnabled()) log.debug("parametros a actualizar: ");
    if (log.isDebugEnabled()) log.debug("txtSerieR: " + params.get("txtSerieR"));
    if (log.isDebugEnabled()) log.debug("txtNumeroR: " + params.get("txtNumeroR"));    
    if (log.isDebugEnabled()) log.debug("fechaR: " + params.get("fechaR"));
    if (log.isDebugEnabled()) log.debug("sindconformidad: " + params.get("sindconformidad"));
    if (log.isDebugEnabled()) log.debug("txtNumeroF: " + params.get("txtNumeroF"));
    if (log.isDebugEnabled()) log.debug("fechaF: " + params.get("fechaF"));
    if (log.isDebugEnabled()) log.debug("txtNeto: " + params.get("txtNeto"));
    if (log.isDebugEnabled()) log.debug("datos generales: ");    
    if (log.isDebugEnabled()) log.debug("codi_empl_per: " + params.get("codi_empl_per"));    
    if (log.isDebugEnabled()) log.debug("subt_plan_tpl: " + params.get("subt_plan_tpl"));
    if (log.isDebugEnabled()) log.debug("tipo_plan_tpl: " + params.get("tipo_plan_tpl"));
    if (log.isDebugEnabled()) log.debug("anno: " + params.get("anno"));
    String indconformidad = (String)params.get("sindconformidad");    
    String numeroF = "";    
    numeroF = (null != params.get("txtNumeroF"))?(String)params.get("txtNumeroF"):"";
    if(indconformidad.equals("1")){
      StringBuffer strSql = new StringBuffer("UPDATE METAS_EMPLEADO ")
      .append(" set METAS_EMPLEADO.NUME_SERI_REC = LPAD(TRIM('")
      .append((String)params.get("txtSerieR"))
      .append("'),4,'0'), METAS_EMPLEADO.NUME_COMP_REC = LPAD(TRIM('")
      .append((String)params.get("txtNumeroR"))
      .append("'),8,'0'), METAS_EMPLEADO.FECH_EMIS_REC = TO_DATE('")
      .append((String)params.get("fechaR"))
      .append("','dd/mm/yyyy') , METAS_EMPLEADO.FLAG_SUSPENSION_4TA = '")
      .append((String)params.get("sindconformidad"))
      .append("', METAS_EMPLEADO.NUMERO_FORM_SUSPENSION_4TA = '")
      .append(numeroF)
      .append("', METAS_EMPLEADO.FECHA_FORM_SUSPENSION = TO_DATE('")
      .append((String)params.get("fechaF"))
      .append("','dd/mm/yyyy') where METAS_EMPLEADO.CODI_EMPL_PER = '")
      .append((String)params.get("codi_empl_per"))
      .append("' and METAS_EMPLEADO.TIPO_PLAN_TPL = '")
      .append((String)params.get("tipo_plan_tpl"))
      .append("' ");
      if (log.isDebugEnabled()) log.debug("STRSQL:"+ strSql.toString()); 
      executeUpdate(datasource, strSql.toString(), new Object[]{});
    } else {
      StringBuffer strSql = new StringBuffer("UPDATE METAS_EMPLEADO ")
  	  .append(" set METAS_EMPLEADO.NUME_SERI_REC = LPAD(TRIM('")
  	  .append((String)params.get("txtSerieR"))
  	  .append("'),4,'0'), METAS_EMPLEADO.NUME_COMP_REC = LPAD(TRIM('")
  	  .append((String)params.get("txtNumeroR"))
	  .append("'),8,'0'), METAS_EMPLEADO.FECH_EMIS_REC = TO_DATE('")
	  .append((String)params.get("fechaR"))
  	  .append("','dd/mm/yyyy') , METAS_EMPLEADO.FLAG_SUSPENSION_4TA = '")
  	  .append((String)params.get("sindconformidad"))
  	  .append("', METAS_EMPLEADO.NUMERO_FORM_SUSPENSION_4TA = '")
  	  .append(numeroF)
  	  .append("', METAS_EMPLEADO.FECHA_FORM_SUSPENSION = NULL ")
  	  .append("where METAS_EMPLEADO.CODI_EMPL_PER = '")
  	  .append((String)params.get("codi_empl_per"))
  	  .append("' and METAS_EMPLEADO.TIPO_PLAN_TPL = '")
  	  .append(((String)params.get("tipo_plan_tpl")).trim())
  	  .append("' ");
      if (log.isDebugEnabled()) log.debug("STRSQL:"+ strSql.toString()); 
      executeUpdate(datasource, strSql.toString(), new Object[]{});
    } 
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - updateRecibo() ")); 
  }
  
  /**
   * Actualiza las conformidades de los colaboradores CAS para una UUOO 
   * @param Map hm
   * @throws DAOException
   */  
  public void updateConformidadCAS(Map params) throws DAOException {  
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - updateConformidadCAS()"));
    if (log.isDebugEnabled()) log.debug("ind_conformidad: " + params.get("ind_conformidad"));
    if (log.isDebugEnabled()) log.debug("codi_empl_per: " + params.get("codi_empl_per"));
    if (log.isDebugEnabled()) log.debug("tipo_plan_tpl: " + params.get("tipo_plan_tpl"));
    try {
    String indconformidad = (String)params.get("ind_conformidad");
    if(!"".equals(indconformidad)){
      StringBuffer strSql = new StringBuffer("UPDATE METAS_EMPLEADO ")
      .append("set METAS_EMPLEADO.FLAG_APROBACION_RECIBO = '")
      .append(params.get("ind_conformidad"))
      .append("', METAS_EMPLEADO.FECHA_APROBACION_RECIBO = SYSDATE ")    
      .append("where METAS_EMPLEADO.CODI_EMPL_PER = '") 
      .append(params.get("codi_empl_per"))
      .append("' and METAS_EMPLEADO.TIPO_PLAN_TPL = '")
      .append(((String)params.get("tipo_plan_tpl")).trim())
      .append("' ");
      executeUpdate(datasource, strSql.toString(), new Object[]{});
    }
    } catch (DAOException e) {
		MensajeBean mbean = new MensajeBean();
		mbean.setMensajeerror("Ocurrió un error al Autorizar Documentos.");
		mbean.setMensajesol("Consulte con el Administrador del Sistema.");
		StringBuffer msg = new StringBuffer();
		msg.append(" ERROR - autorizaDocumento() ")
		   .append(e.toString());
		log.error(this.toString().concat(msg.toString()));
		throw new DAOException(this, e);
	} finally {			
	}
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - updateConformidadCAS() ")); 
  }
  
  //PAS201220C00000148 EMR se agrega método para consultar los datos de la suspensión
  /**
   * Obtiene los datos de la suspension
   * @param params Map
   * @return Map datosRecibo
   * @throws DAOException
   */
  public Map findBySuspension(Map params) throws DAOException {
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findBySuspension()"));
    Map datosRecibo = new HashMap();    
    StringBuffer strSQL = null;
    strSQL = new StringBuffer(FIND_SUSPENSION_CAS.toString());    
    datosRecibo = executeQueryUniqueResult(datasource, strSQL.toString(),
       new Object[]{params.get("codi_empl_per"), params.get("subt_plan_tpl")});
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findBySuspension() ")); 
    return datosRecibo;
  }
  
  //PAS201220C00000148 EMR se agrega método para actualizar los datos de la suspensión
  /**
   * Actualiza la suspensión 
   * @param Map params
   * @throws DAOException
   */  
  public void updateSuspension(Map params) throws DAOException {  
	  
	if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO DAO - updateSuspension()"));
    if (log.isDebugEnabled()) log.debug("sindconformidad: " + params.get("sindconformidad"));
    if (log.isDebugEnabled()) log.debug("codi_empl_per: " + params.get("codi_empl_per"));
    if (log.isDebugEnabled()) log.debug("tipo_plan_tpl: " + params.get("tipo_plan_tpl"));
    if (log.isDebugEnabled()) log.debug("subt_plan_tpl: " + params.get("subt_plan_tpl"));
    if (log.isDebugEnabled()) log.debug("svalidasuspension: " + params.get("svalidasuspension"));
    
    try {
    	    
	String indconformidad = (String)params.get("sindconformidad");    
    
    if(indconformidad.equals("1")){
    	StringBuffer strSql = new StringBuffer(" UPDATE METAS_EMPLEADO set FLAG_SUSPENSION_4TA = '"+(String)params.get("sindconformidad")+"', NUMERO_FORM_SUSPENSION_4TA = '"+(String)params.get("txtNumeroF")+"'," +
    											" FECHA_FORM_SUSPENSION = TO_DATE('"+ (String)params.get("fechaF")+"','dd/mm/yyyy'), IND_VALIDACION = '"+(String)params.get("svalidasuspension")+"' " +
    											" where CODI_EMPL_PER ='"+(String)params.get("codi_empl_per")+"' and TIPO_PLAN_TPL = '02' and SUBT_PLAN_TPL ='"+(String)params.get("subt_plan_tpl")+"' ");	
      if (log.isDebugEnabled()) log.debug("STRSQL: "+ strSql.toString()); 
      
      executeUpdate(datasource, strSql.toString(), new Object[]{});
      
    } else {    	
    	StringBuffer strSql = new StringBuffer(" UPDATE METAS_EMPLEADO set FLAG_SUSPENSION_4TA = '"+(String)params.get("sindconformidad")+"', NUMERO_FORM_SUSPENSION_4TA = '"+(String)params.get("txtNumeroF")+"'," +
				" FECHA_FORM_SUSPENSION = NULL, IND_VALIDACION = '"+(String)params.get("svalidasuspension")+"' " +
				" where CODI_EMPL_PER ='"+(String)params.get("codi_empl_per")+"' and TIPO_PLAN_TPL = '02' and SUBT_PLAN_TPL ='"+(String)params.get("subt_plan_tpl")+"' ");	

    		if (log.isDebugEnabled()) log.debug("STRSQL:"+ strSql.toString()); 

    		executeUpdate(datasource, strSql.toString(), new Object[]{});

  	 } 
    } catch (DAOException e) {
		MensajeBean mbean = new MensajeBean();
		mbean.setMensajeerror("Ocurrió un error al Autorizar Documentos.");
		mbean.setMensajesol("Consulte con el Administrador del Sistema.");
		StringBuffer msg = new StringBuffer();
		msg.append(" ERROR - autorizaDocumento() ")
		   .append(e.toString());
		log.error(this.toString().concat(msg.toString()));
		throw new DAOException(this, e);
	} finally {			
	}
    if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN DAO - updateSuspension() ")); 
  } 
  
  //PAS20171U230200001 - solicitud de reintegro   -RMONTES
  /**
   * Metodo para obtener registro de persona inscrita en una planilla determinada  
   * @param tPlanilla : tipo de  planilla
   * @param numRegistro : numero de registro
   * @return codigo de empleado
   * @throws DAOException
   */
  public Map findByTPlanillaRegEmp(String tPlanilla, String numRegistro) throws DAOException {
	return executeQueryUniqueResult(datasource, FIND_BY_TPLANILLA_REGEMP, new Object[]{tPlanilla, numRegistro});
  }
  
  
  //PAS20181U230200067 - solicitud de reintegro, planillas adicionales 
  /**
   * Metodo para obtener planillas en la que persona inscrita   
   * @param tPlanilla : tipo de  planilla
   * @param string 
   * @param annoOrig 
   * @param numRegistro : numero de registro
   * @return codigo de empleado
   * @throws DAOException
   */
  public Map findByTPlanillaReintegro(String tPlanilla, String codiEmplPer, String periodoOrig) throws DAOException {
	return executeQueryUniqueResult(datasource, FIND_BY_TPLANILLA_REINTEGRO, new Object[]{tPlanilla, codiEmplPer,periodoOrig});
  }
}
