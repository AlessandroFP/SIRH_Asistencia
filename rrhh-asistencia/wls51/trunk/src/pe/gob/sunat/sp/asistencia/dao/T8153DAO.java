//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

/** 
 * Title : T8153DAO 
 * Description : Clase para el manejo de tabla  t8153solconcepto 
 * @author agonzalesf
 * @version 1.0
 */

public class T8153DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T8153DAO.class);

	private DataSource datasource;

	private static final String SELECT_CONCEPTOS = "SELECT "
			+ " c.num_seqrein,"
			+ " c.num_seqcon,"
			+ " nvl(c.cod_tiplicencia,'') as cod_tiplicencia ,"
			+ " c.cod_concepto,"
			+ " c.des_concepto,"
			+ " c.cod_movimiento,"
			+ " c.des_movimiento,"			
			+ " c.cnt_mindscto,"
			+ " c.cnt_diadscto, "
			+ " c.cnt_minsol, "
			+ " c.cnt_diasol,"
			+ " nvl(c.cnt_minapro,0) cnt_minapro,"
			+ " nvl(c.cnt_diaapro,0) cnt_diaapro,"
			+ " c.des_motivo,"
			+ " c.cod_estado, "
			+ " c.cod_tipobs, " 
			+ " c.ind_del,  " 
			+ " c.cod_estado  " 
			+ " FROM t8153solconcepto c, t8155solreintegro s, outer t8152archivo a "
			+ " WHERE c.num_seqrein = ? and "
			+ "       s.num_seqrein = c.num_seqrein and "
			+ "       a.num_archivo = s.num_archivo and c.cod_estado = 'A'";

	
	private static final String FIND_CONCEPTOS_EXCLUIDOS = "SELECT nvl(c.cod_tiplicencia,'') cod_tiplicencia, c.des_concepto ,c.cod_concepto,  c.des_movimiento , nvl(m.t99descrip,'') motivo"
			+" FROM t8153solconcepto c , outer (select * from t99codigos where t99cod_tab='R04' and t99tip_desc='D' and t99estado='1' ) m"
			+" WHERE c.num_seqrein = ? and  c.cod_estado = 'I' and m.t99codigo =c.cod_tipobs";

	private static final String FIND_CONCEPTOS_APROBADOS = "SELECT nvl(c.cod_tiplicencia,'') cod_tiplicencia, c.des_concepto ,c.cod_concepto, c.des_movimiento , "
			+ " nvl(c.cnt_minapro,0) cnt_minapro, nvl(c.cnt_diaapro,0) cnt_diaapro,"	
			+ " c.cnt_minsol, "
			+ " c.cnt_diasol "
			+" FROM t8153solconcepto c "
			+" WHERE c.num_seqrein = ? and  c.cod_estado = 'A'";
	
	private static final String INSERT_CONCEPTO = "INSERT INTO t8153solconcepto"
		+ "  (num_seqrein,  "
		+ "   num_seqcon,   "
	   + " cod_tiplicencia,"   
	   + " cod_concepto, "  
	   + " des_concepto, "  
	   + " cod_movimiento,"   
	   + " des_movimiento, "  
	   + " des_motivo , "  
	   + " cnt_mindscto," 
	   + " cnt_diadscto,"   
	   + " cnt_minsol,"    
	   + " cnt_diasol, "   
	   + " cnt_minapro, "   
	   + " cnt_diaapro, "  
	   + " cod_estado, "   
	   + " cod_usucrea," 
	   + " fec_creacion,"  
	   + " ind_del)  " 
	   + " VALUES "
	   + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	  
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	private static final String FIND_SOLICITUDES_EN_PROCESO = "SELECT  conc.cod_movimiento as movimiento ,conc.cod_tiplicencia, sum(conc.cnt_minsol) minEnProceso,sum(conc.cnt_diasol) as  diaEnProceso"
		 + " FROM  t8153solconcepto as conc "
		 + " INNER JOIN  t8155solreintegro as rein ON conc.num_seqrein =  rein.num_seqrein  "
		 + " INNER JOIN t1277solicitud as sol ON rein.ann_solicitud =  sol.anno and rein.num_solicitud = sol.numero and rein.cod_uuoo = sol.u_organ and rein.cod_pers = sol.cod_pers and rein.cod_mov = sol.licencia and rein.fec_solicitud = sol.ffinicio"
		 + " INNER JOIN t1455sol_seg seg ON seg.anno like sol.anno and seg.numero = sol.numero and  seguim_act = num_seguim"
		 + " WHERE rein.cod_pers = ?"
		 + " AND rein.cod_mov =? "
		 + " AND rein.ann_solplan = ?"
		 + " AND rein.mes_solplan = ?"		 
		 + " AND rein.cod_planorig = ?"		 
		 + " AND rein.cod_splanorig = ?"		 
		 + " AND  seg.estado_id  =1 "
		 + " AND conc.cod_estado = 'A'"
		 + " GROUP BY conc.cod_movimiento , conc.cod_tiplicencia";
	
	
	private static final String UPDATE_ESTADO = "UPDATE t8153solconcepto SET IND_DEL = ?, COD_ESTADO = ?, COD_TIPOBS = ?, COD_USUMODIF = ?, FEC_MODIF = ?  WHERE num_seqrein = ? AND num_seqcon = ?";
	
	private static final String UPDATE_CNT_APROB_INI = "UPDATE t8153solconcepto SET COD_USUMODIF = ?, FEC_MODIF = ? ";
	private static final String UPDATE_CNT_APROB_FIN = " WHERE num_seqrein = ? AND num_seqcon = ?";
	
	public T8153DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

	/**
	 * Obtener conceptos por numero de solicitud de reintegro
	 * @param numSeqrein : Numero de solicitud de reintegro
	 * @return Lista de conceptos (registros de la tabla t8153solconcepto)
	 * @throws SQLException
	 */
	public List findConceptos(String numSeqrein) throws SQLException {
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		InputStream archivo = null;
		List conceptos = new ArrayList();
		try {
			log.debug("Ingreso findConceptos");
			con = datasource.getConnection();
			pre = con.prepareStatement(SELECT_CONCEPTOS.toString());
			pre.setString(1, numSeqrein);
			rs = pre.executeQuery();
			while (rs.next()) {
				Map map = new HashMap();
				map.put("num_seqrein", rs.getString(1));
				map.put("num_seqcon", rs.getString(2));
				map.put("cod_tiplicencia", rs.getString(3));
				map.put("cod_concepto", rs.getString(4));
				map.put("des_concepto", rs.getString(5));
				map.put("cod_movimiento", rs.getString(6));
				map.put("des_movimiento", rs.getString(7)); 
				map.put("cnt_minsol", rs.getString(10));
				map.put("cnt_diasol", rs.getString(11));
				map.put("cnt_minapro", rs.getString(12));
				map.put("cnt_diaapro", rs.getString(13)); 
				map.put("des_motivo", rs.getString(14));  
				map.put("ind_del", rs.getString(17));
                map.put("cod_estado", rs.getString(18));  //codigo de activo/inactivo
				map.put("num_seqdoc", rs.getString(2)); //numero de archivo detalle es el numero de documento 
				conceptos.add(map);
			}
		} catch (Exception e) {
			log.error("No se puede obtener conceptos de base ", e);
			throw new SQLException(e.toString());
		} finally {
			log.debug("Cierre de conexion");
			try {
				rs.close();
			} catch (Exception ex) {
				 
			}
			try {
				pre.close();
			} catch (Exception ex) { 
			}
			
			try {
				con.close();
			} catch (Exception ex) { 
			}
		}
		return conceptos;
	}

	/**
	 * Insertar concepto de solicitud de reintegro
	 * @param datos Datos a insertar 
	 */
	public boolean insertConceptoSolReintegro(Map datos) {
		executeUpdate(datasource, INSERT_CONCEPTO, new Object[]{
				datos.get("num_seqrein"),
				datos.get("num_seqcon"), 
				datos.get("cod_tiplicencia"), 
				datos.get("cod_concepto"),
				datos.get("des_concepto"),
				datos.get("cod_movimiento"),
				datos.get("des_movimiento"),
				datos.get("des_motivo"),
				datos.get("cnt_mindscto"),
				datos.get("cnt_diadscto"),
				datos.get("cnt_minsol"), 
				datos.get("cnt_diasol"), 
				datos.get("cnt_minapro"), 
				datos.get("cnt_diaapro"), 
				datos.get("cod_estado"),   
				datos.get("cod_usucrea"),				
				datos.get("fec_creacion"),
				datos.get("ind_del")});
		return true;
		
	}

 
	/**
	 * Cambiar el estado de concepto  
	 * @param solicitud datos de la solicitud 
	 * @param estado
	 * @return true si no existe error
	 */

	public boolean cambiarEstado(HashMap solicitud, String estado) {
		executeUpdate(datasource, UPDATE_ESTADO, new Object[]{
				solicitud.get("ind_del"),
				estado,
				solicitud.get("cod_tipobs"),
				solicitud.get("cod_usumodif"),
				new FechaBean().getTimestamp(),
				solicitud.get("num_seqrein"),
				solicitud.get("num_seqcon"),
				
				});
		return true;
	}

	/**
	 * Obtener conceptos excluidos de una solicitud
	 * @param reintegro num_seqrein -> numero de solicitud de reintegro 
	 * @return lista de conceptos excluidos 
	 */
	public List findConceptoExcluidosBySolicitud(Map reintegro) {
		if (log.isDebugEnabled())
			log.debug(" INICIO - findConceptoExcluidosBySolicitud() ->" + reintegro);
		List lista = null;
		lista = executeQuery(datasource, FIND_CONCEPTOS_EXCLUIDOS, new Object[] { reintegro.get("num_seqrein")});
		if (log.isDebugEnabled())
			log.debug(" FIN - findConceptoExcluidosBySolicitud() ");
		return lista;
	}
	
	/**
	 * Obtener conceptos aprobados de una solicitud
	 * @param reintegro num_seqrein -> numero de solicitud de reintegro  
	 * @return lista de conceptos aprobados	   
	 */
	public List findConceptoAprobadosBySolicitud(Map reintegro) {
		if (log.isDebugEnabled())
			log.debug(" INICIO - findConceptoAprobadosBySolicitud() ->" + reintegro);
		List lista = null;
		lista = executeQuery(datasource, FIND_CONCEPTOS_APROBADOS, new Object[] { reintegro.get("num_seqrein")});
		if (log.isDebugEnabled())
			log.debug(" FIN - findConceptoAprobadosBySolicitud() ");
		return lista;
	}

	/**
	 * Funcion para actualizar la suma de dias/minutos aprobados por rrhh
	 * @param mapa datos para actualizar 
	 */
	public void updateDevolucionConcepto(Map mapa) {
		StringBuffer strQuery = new StringBuffer(UPDATE_CNT_APROB_INI);
		
		if (mapa.get("cnt_diaapro")!=null && new Integer(mapa.get("cnt_diaapro").toString()).intValue() > 0){
			strQuery.append(", cnt_diaapro = ? ");
		} else {
			strQuery.append(", cnt_minapro = ? ");
		}
		strQuery.append(UPDATE_CNT_APROB_FIN);
		
		executeUpdate(datasource, strQuery.toString(), new Object[]{
				mapa.get("cod_usumodif"),
				mapa.get("fec_modif"),
				mapa.get("cnt_diaapro")!=null && new Integer(mapa.get("cnt_diaapro").toString()).intValue() > 0 ? mapa.get("cnt_diaapro") : mapa.get("cnt_minapro"),
				mapa.get("num_seqrein"),
				mapa.get("num_seqcon"),
				});
	}
 
	//AGONZALESF -PAS20181U230200067 - solicitud de reintegro, planillas adicionales
	/** 
	 * Funcion para obtener la solicitudes en proceso
	 * @param params datos de busqueda 
	 * @return lista de conceptos en proceso
	 * 
	 */
	public List findSolReinConceptoEnProceso(Map params) {
		if (log.isDebugEnabled())
			log.debug(" INICIO - findSolReinConceptoEnProceso() ->" + params);
		List lista = null;
		lista = executeQuery(datasource, FIND_SOLICITUDES_EN_PROCESO, new Object[] { params.get("cod_pers"), params.get("tipo"), //301,302		    	
				params.get("anio"), //2017 01
				params.get("mes"),
				params.get("tipoPlanilla"),
				params.get("subtipoPlanilla")});
		if (log.isDebugEnabled())
			log.debug(" FIN - findSolReinConceptoEnProceso() ");
		return lista;
	}

}
