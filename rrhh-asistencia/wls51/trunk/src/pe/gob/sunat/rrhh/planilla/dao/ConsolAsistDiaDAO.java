package pe.gob.sunat.rrhh.planilla.dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>Title: ConsolAsistDiaDAO </p>
 * <p>Description: Clase para realizar la consulta de Consolidado Asistencia Diario</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: SUNAT</p>
 * @author EMOSTACERO
 * @version 1.0 
 */
public class ConsolAsistDiaDAO extends DAOAbstract {
	  private DataSource datasource; // Variable creada para instanciar el DataSource

	  private static final StringBuffer FIND_DETALLE_ASISTENCIA_DIARIA = new StringBuffer("SELECT ")
	  .append(" FECH_ASIST_CON, MOVI_DIAR_CON, MINU_EXTR_CON ")
	  .append(" FROM CONSOLIDADO_ASISTENCIA_DIARIO ")
	  .append(" WHERE CODI_EMPL_PER = ? ")
	  .append(" AND FECH_ASIST_CON BETWEEN TO_DATE( ? ,'dd/mm/yyyy') ") 
	  .append(" AND TO_DATE( ? ,'dd/mm/yyyy') ")
	  .append(" AND MOVI_DIAR_CON = ? ");
	  
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro    
	private static final StringBuffer FIND_DETALLE_ASISTENCIA = new StringBuffer(" SELECT  ")
			.append(" cd.movi_diar_con as movimiento,")
			.append(" cd.fech_asist_con as fecha,")
			.append(" cd.minu_extr_con as minutos")
			.append(" FROM consolidado_asistencia_diario cd ")
			.append(" WHERE cd.tipo_plan_tpl = ?")
			.append(" AND cd.codi_empl_per = ?")
			.append(" AND TO_CHAR(cd.fech_asist_con, 'yyyy') = ?")
			.append(" AND TO_CHAR(cd.fech_asist_con, 'mm') = ?")
			.append(" AND cd.movi_diar_con like ?")
			.append(" ORDER BY cd.fech_asist_con desc");

	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro    
		private static final StringBuffer FIND_DETALLE_ASISTENCIA_DIARIO = new StringBuffer(" SELECT  ")				
				.append(" nvl(cd.minu_extr_con,0) as original")
				.append(" FROM consolidado_asistencia_diario cd ")
				.append(" WHERE cd.tipo_plan_tpl = ?")
				.append(" AND cd.codi_empl_per = ?")
				.append(" AND  cd.fech_asist_con  = ?")				
				.append(" AND cd.movi_diar_con like ?");
	  
	  /**
	   * Método para validar el Datasource a usarse
	   * @param Object datasource
	   */
	  public ConsolAsistDiaDAO(Object datasource) {
	    if (datasource instanceof DataSource)
	      this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.datasource = getDataSource((String)datasource);
	    else
        throw new DAOException(this, "Datasource no valido");
    }
    
    /**
     * Obtiene el detalle de asisencias para descuentos 
     * @param params
     * @return List listaDetalleAsistencia
     * @throws DAOException
     */  
    public List findByDetalleAsistenciaDiaria(Map params) throws DAOException {
      if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findByDetalleAsistenciaDiaria()"));
      List listaDetalleAsistencia = null;    
      StringBuffer strSQL = null;
      strSQL = new StringBuffer(FIND_DETALLE_ASISTENCIA_DIARIA.toString());        
      listaDetalleAsistencia = executeQuery(datasource, strSQL.toString(),
      new Object[]{params.get("codi_empl_per"), params.get("fech_inic_tpe"), 
            params.get("fech_fina_tpe"),params.get("tipo_movi_per")});
      if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findByDetalleAsistenciaDiaria() "));           
      return listaDetalleAsistencia;
    }
    
    
    
    
    /**
     * AGONZALESF -PAS20171U230200001 - solicitud de reintegro   
     * Obtiene el detalle de asistencia por movimiento
     * @param params
     * @return List listaDetalleAsistencia
     * @throws DAOException
     */  
    public List findDetalleAsistenciaDiaria(Map params) throws DAOException {
    	if (log.isDebugEnabled()) log.debug("INICIO findDetalleAsistenciaDiariaJoinDevolucion ->params " +params);
    	List listaDetalleAsistencia = null;    
    	 
    
    	if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_HABERES)) {
    		log.debug("Seleccionar detalles de planilla - haberes");
    		log.debug(FIND_DETALLE_ASISTENCIA);	 
    		listaDetalleAsistencia = executeQuery(datasource, FIND_DETALLE_ASISTENCIA.toString(),
        			new Object[]{
    			params.get("tipo_plan_tpl"),
    			params.get("codi_empl_per"),
    			params.get("anioConsulta"),
    			params.get("mesConsulta"), 
        		params.get("movimiento")  
        		});
    	}
    	
    	if (params.get("tipo_plan_tpl").equals(Constantes.PLANILLA_CAS)) {
    		log.debug("Seleccionar detalles de planilla - cas");
    		log.debug(FIND_DETALLE_ASISTENCIA);	 
    		listaDetalleAsistencia = executeQuery(datasource, FIND_DETALLE_ASISTENCIA.toString(),
        			new Object[]{   
        		params.get("tipo_plan_tpl"),
        		params.get("codi_empl_per"),
        		params.get("anioConsulta"),
    			params.get("mesConsulta"), 
        		params.get("movimiento")  
        		});

    	} 
    	if (log.isDebugEnabled()) log.debug("FIN  findDetalleAsistenciaDiariaJoinDevolucion");           
    	return listaDetalleAsistencia;
    }
    
    
    /**
     * AGONZALESF -PAS20171U230200001 - solicitud de reintegro   
     * Obtiene el detalle de asistencia por movimiento
     * @param params
     * @return List listaDetalleAsistencia
     * @throws DAOException
     */  
    public Map findDetalleOriginal(Map params) throws DAOException {
    	if (log.isDebugEnabled()) log.debug(" INICIO - findDetalleOriginal()-> " + params); 
		Map devolucion = null;
		Object[] filtro = null;
		filtro = new Object[] { 
				params.get("cod_tip_planilla"), 
				params.get("cod_empl_per"), 					
				params.get("fec_asis_origen"),
				params.get("cod_mov_origen")  }; 
		devolucion = executeQueryUniqueResult(datasource, FIND_DETALLE_ASISTENCIA_DIARIO.toString(), filtro);		
    
		if (log.isDebugEnabled()) log.debug("FIN - findDetalleOriginal()");
		return devolucion;
    }
}