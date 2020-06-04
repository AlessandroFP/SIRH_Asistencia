//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.dao;

 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * Title : T8153DAO 
 * Description : Clase para el manejo de tabla  t8154soldetalle 
 * @author agonzalesf
 * @version 1.0
 */

public class T8154DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T8154DAO.class);

	private static final String INSERT_DETALLE = "INSERT INTO t8154soldetalle"
	  + " (num_seqrein,  " 
	  + " num_seqcon, "  
	  + "  cod_licencia, "  
	  + "  fec_asisorig,  "
	  + " cnt_mindsctorig,  "
	  + "  cnt_diadsctorig,   "
	  + "  cnt_diasol,"
	  + "  cnt_minsol," 
	  + "  cod_tipdev, "  
	  + "  des_tipdev, "  
	  + "  cod_origmov, "    
	  + "  ann_licencia, "
	  + "  mes_licencia, " 
	  + "  cod_usucrea, " 
	  + "  fec_creacion,  "
	  + "  ind_del)   " 
	  + "   values  (?,?,?,?,?,?,?,?,?,?"
	  + ",?,?,?,?,?,?)" ;
	
	private static final String SELECT_DETALLES_REINTEGRO = "SELECT" 
			+ " deta.num_seqrein,"
			+ " deta.cod_licencia,"
			+ " conc.cod_tiplicencia,"	
			+ " conc.num_seqcon,"	
			+ " rein.cod_planorig," 
			+ " rein.cod_splanorig," 
			+ " rein.ann_solplan," 
			+ " rein.mes_solplan,"  
			+ " rein.cod_plandev," 
			+ " rein.cod_splandev," 
			+ " rein.ann_plandev,"
			+ " rein.mes_plandev," 
			+ " rein.cod_pers,"
			+ " deta.fec_asisorig," 
			+ " deta.cod_origmov,"
			+ " conc.cod_concepto,"
			+ " deta.cod_tipdev,"
			+ " NVL(deta.cnt_mindev, 0) cnt_mindev,"
			+ " NVL(deta.cnt_diadev, 0) cnt_diadev," 
			+ " deta.des_observa, "  
			+ " deta.ann_licencia, "  
			+ " deta.mes_licencia "  
			+ " FROM  t8154soldetalle as deta"
			+ " INNER JOIN  t8153solconcepto as conc ON conc.num_seqrein =  deta.num_seqrein  and conc.num_seqcon   = deta.num_seqcon"
			+ " INNER JOIN  t8155solreintegro as rein ON conc.num_seqrein =  rein.num_seqrein  "
			+ " INNER JOIN t1277solicitud as sol ON rein.ann_solicitud =  sol.anno and rein.num_solicitud = sol.numero "
			+ " INNER JOIN t1455sol_seg seg ON seg.anno like sol.anno and seg.numero = sol.numero and  seguim_act = num_seguim"
			+ " WHERE rein.num_seqrein = ? AND  conc.cod_estado = 'A'";



//AGONZALESF -PAS20171U230200020
//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	private static final String FIND_SOLICITUDES_EN_PROCESO_ASISTENCIA = "SELECT deta.fec_asisorig, sum(conc.cnt_minsol) as minEnProceso "
	+" FROM  t8154soldetalle as deta"
	+" INNER JOIN  t8153solconcepto as conc ON conc.num_seqrein =  deta.num_seqrein  and conc.num_seqcon   = deta.num_seqcon"
	+" INNER JOIN  t8155solreintegro as rein ON conc.num_seqrein =  rein.num_seqrein  "
	+" INNER JOIN  t1277solicitud as sol ON rein.ann_solicitud =  sol.anno and rein.num_solicitud = sol.numero and rein.cod_uuoo = sol.u_organ and rein.cod_pers = sol.cod_pers and rein.cod_mov = sol.licencia and rein.fec_solicitud = sol.ffinicio" 
	+" INNER JOIN  t1455sol_seg seg ON seg.anno like sol.anno and seg.numero = sol.numero and  seguim_act = num_seguim"
	+" where rein.cod_pers = ?"
	+" and rein.cod_mov =? "
	+" and rein.ann_solplan = ?"
	+" and rein.mes_solplan = ?"
	+ " AND rein.cod_planorig = ?"		 
	+ " AND rein.cod_splanorig = ?"	
	+" and  seg.estado_id  = 1 "
	+" and conc.cod_estado = 'A'"
	+" and conc.cod_movimiento =?" 
	+" group by  deta.fec_asisorig  ";
	
	//AGONZALESF -PAS20171U230200020
//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	private static final String FIND_SOLICITUDES_EN_PROCESO_LICENCIA = "SELECT   deta.cod_licencia ,   sum(conc.cnt_diasol) as diaEnProceso"
			+" FROM  t8154soldetalle as deta"
			+" INNER JOIN  t8153solconcepto as conc ON conc.num_seqrein =  deta.num_seqrein  and conc.num_seqcon   = deta.num_seqcon"
			+" INNER JOIN  t8155solreintegro as rein ON conc.num_seqrein =  rein.num_seqrein  "
			+" INNER JOIN  t1277solicitud as sol ON rein.ann_solicitud =  sol.anno and rein.num_solicitud = sol.numero and rein.cod_uuoo = sol.u_organ and rein.cod_pers = sol.cod_pers and rein.cod_mov = sol.licencia and rein.fec_solicitud = sol.ffinicio" 
			+" INNER JOIN  t1455sol_seg seg ON seg.anno like sol.anno and seg.numero = sol.numero and  seguim_act = num_seguim"
			+" where rein.cod_pers = ?"
			+" and rein.cod_mov =? "
			+" and rein.ann_solplan = ?"
			+" and rein.mes_solplan = ?"
			+ " AND rein.cod_planorig = ?"		 
			+ " AND rein.cod_splanorig = ?"	
			+" and  seg.estado_id  = 1 "
			+" and conc.cod_estado = 'A'"
			+" and conc.cod_movimiento =?" 
			+" group by deta.cod_licencia";

	
 
																		  
	//AGONZALESF -PAS20171U230200033 -Afinado para traer datos de dias/min devueltos si existen
	private static final StringBuffer FIND_DETSOLICITUDES_BY_PK = new StringBuffer(
			"select cod_licencia, ")
			.append("fec_asisorig,  ")
			.append("cnt_diadsctorig, ")
			.append("cnt_mindsctorig, ")
			.append("cnt_diadev, ")
			.append("cnt_mindev, ")
			.append("CASE   WHEN cnt_diadev is null THEN  cnt_diasol    ELSE cnt_diadev   	END as cnt_diasol, ")
			.append("CASE   WHEN cnt_mindev is null THEN  cnt_minsol    ELSE cnt_mindev   	END as cnt_minsol, ")
			.append("CASE   WHEN cnt_diadev is null THEN  cnt_diadsctorig - cnt_diasol    ELSE cnt_diadsctorig - cnt_diadev  	END as cnt_diasaldo, ")
			.append("CASE   WHEN cnt_mindev is null THEN  cnt_mindsctorig - cnt_minsol   ELSE cnt_mindsctorig - cnt_mindev 	END as cnt_minsaldo, ")
			.append("cod_tipdev, ")
			.append("des_tipdev, ")
			.append("cod_licencia, ")
			.append("cod_origmov, ")
			.append("des_observa ") 
			
																		   .append("from t8154soldetalle ")
																		   .append("where num_seqrein = ? and num_seqcon = ? "); 
	
	private static final String UPDATE_DEVOL_INI = "UPDATE t8154soldetalle SET COD_USUMODIF = ?, FEC_MODIF = ? ";
	private static final String UPDATE_DEVOL_FIN = " WHERE num_seqrein = ? AND num_seqcon = ? and cod_licencia = ? and fec_asisorig = ?";

	private DataSource datasource;

	public T8154DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

	/**
	 * Inserta el detalle de concepto de solicitud de reintegro
	 * @param datos : datos de detalle  
	 * @return true si es correcto
	 * 
	 
	  
	 */
	public boolean insertDetalleSolReintegro(Map datos) {
		log.debug("Insertar en detalle " + datos);
		executeUpdate(
				datasource,
				INSERT_DETALLE,
				new Object[] { datos.get("num_seqrein"), datos.get("num_seqcon"), datos.get("cod_licencia"), datos.get("fec_asisorig"),
						datos.get("cnt_mindsctorig"), datos.get("cnt_diadsctorig"), datos.get("cnt_diasol"), datos.get("cnt_minsol"), datos.get("cod_tipdev"),
						datos.get("des_tipdev"), datos.get("cod_origmov"), datos.get("ann_licencia"), datos.get("mes_licencia"), datos.get("cod_usucrea"),
						datos.get("fec_creacion"), datos.get("ind_del") });
		return true;
	}

	/**
	 * Funcion para actualizar la suma de dias/minutos aprobados por rrhh detalle por detalle
	 * @param mapa
	  */
	public void updateDevolucionDetalles(HashMap mapa) {
		
		log.debug("updateDevolucionDetalles " + mapa);
		
		StringBuffer strQuery = new StringBuffer(UPDATE_DEVOL_INI);
		List parametros = new ArrayList();

		parametros.add(mapa.get("cod_usumodif"));
		parametros.add(mapa.get("fec_modif"));

		if (mapa.get("cnt_diadev") != null && new Integer(mapa.get("cnt_diadev").toString()).intValue() > 0) {
			strQuery.append(", cnt_diadev = ? ");
			parametros.add(mapa.get("cnt_diadev"));
		} else {
			strQuery.append(", cnt_mindev = ? ");
			parametros.add(mapa.get("cnt_mindev"));
		}

		if (mapa.get("cod_tipdev") != null) {
			strQuery.append(", cod_tipdev = ?");
			parametros.add(mapa.get("cod_tipdev"));
		}
		if (mapa.get("des_tipdev") != null) {
			strQuery.append(", des_tipdev = ?");
			parametros.add(mapa.get("des_tipdev"));
		}

		if (mapa.get("des_observa") != null) {
			strQuery.append(", des_observa = ?");
			parametros.add(mapa.get("des_observa"));
		}

		parametros.add(mapa.get("num_seqrein"));
		parametros.add(mapa.get("num_seqcon"));
		parametros.add(mapa.get("cod_licencia"));
		parametros.add(mapa.get("fec_asisorig"));

		strQuery.append(UPDATE_DEVOL_FIN);

		executeUpdate(datasource, strQuery.toString(), parametros.toArray());
	}

	/** 
	 * Funcion encontrar solicitudes de reintegro en proceso asistencia
	 * @param params datos de busqueda
	 * @return Lista de detalles en proceso
	 
	 */
        //AGONZALESF -PAS20171U230200020
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	public List findSolReinDetalleEnProcesoAsistencia(Map params) {
		if (log.isDebugEnabled())
			log.debug(" INICIO - findSolReinConceptoEnProceso() ->" + params);
		List lista = null;
		lista = executeQuery(datasource, FIND_SOLICITUDES_EN_PROCESO_ASISTENCIA, new Object[] { params.get("cod_pers"), params.get("tipo"), //301,302		    	
				params.get("anio"), params.get("mes"), params.get("tipoPlanilla"),
				params.get("subtipoPlanilla"),params.get("movimiento") });
		if (log.isDebugEnabled())
			log.debug(" FIN - findSolReinConceptoEnProceso() ");
		return lista;
	}
	 
	/** 
	 * Funcion encontrar solicitudes de reintegro en proceso  licencia
	 * @param params datos de busqueda
	 * @return Lista de detalles en proceso	 
	 */
        //AGONZALESF -PAS20171U230200020
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	public List findSolReinDetalleEnProcesoLicencia(Map params) {
		if (log.isDebugEnabled())
			log.debug(" INICIO - findSolReinConceptoEnProceso() ->" + params);
		List lista = null;
		lista = executeQuery(datasource, FIND_SOLICITUDES_EN_PROCESO_LICENCIA, new Object[] { params.get("cod_pers"), params.get("tipo"), //301,302		    	
				params.get("anio"), params.get("mes"),params.get("tipoPlanilla"),
				params.get("subtipoPlanilla"), params.get("movimiento") });
		if (log.isDebugEnabled())
			log.debug(" FIN - findSolReinConceptoEnProceso() ");
		return lista;
	}

	/**
	 * Obtiene detalle de concepto de solicitud por concepto
	 * @param params datos para la busqueda 
	 * @return Lista de detalle 
	 */
	public List findDetSolicitudesByPK(Map params) {
		if (log.isDebugEnabled())
			log.debug(this.toString().concat(" INICIO - findDetSolicitudesByPK()"));
		List lista = null;
		List lstParams = new ArrayList();
		lstParams.add(params.get("num_seqrein"));
		lstParams.add(params.get("num_seqcon"));

		StringBuffer strQuery = FIND_DETSOLICITUDES_BY_PK;
		if (params.containsKey("fec_asisorig")) {
			strQuery.append(" and fec_asisorig = ?");
			lstParams.add(params.get("fec_asisorig"));
		}

		lista = executeQuery(datasource, strQuery.toString(), lstParams.toArray());
		if (log.isDebugEnabled())
			log.debug(this.toString().concat(" FIN - findDetSolicitudesByPK() "));
		return lista;
	}

	/**
	 * Funcion para encontrar los detalles de solicitud aprobada
	 * @param solicitud
	 * @return Lista de detalles 
	 */
	public List findSolicitudDetallesByNumSeqRein(String numSeqRein) {
		List list = new ArrayList();
		log.debug("Ingreso findSolicitudDetallesByNumSeqRein ->" + numSeqRein);
		Object obj[] = { numSeqRein };
		list = executeQuery(datasource, SELECT_DETALLES_REINTEGRO.toString(), obj);
		log.debug("  findSolicitudDetallesByNumSeqRein ->" + list);
		return list;
	}

}
