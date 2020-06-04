package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;

/** 
 * 
 * Clase       : T1270DAO 
 * Descripcion : Clase encargada de administrar los datos de la tabla t1270turnoperson 
 * Proyecto    : ASISTENCIA 
 * Autor       : JROJAS4
 * Fecha       : 23-ENE-2009 
 * 
 * */

public class T1270DAO extends DAOAbstract {	
	
	private DataSource datasource;	
	
	//JRR
	private final StringBuffer INSERT_TURNO_PERSONA = new StringBuffer("INSERT INTO t1270turnoperson "
			).append("(cod_pers, u_organ, turno, fini, ffin, est_id, sonom_id, fcreacion, cuser_crea) " // fmod, cuser_mod) "
			).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
	
	private final StringBuffer INSERT_TURNO_PERSONA_SUST = new StringBuffer("INSERT INTO t1270turnoperson "
			).append("(cod_pers, u_organ, turno, fini, ffin,observ, est_id, sonom_id, fcreacion, cuser_crea) " // fmod, cuser_mod) "
			).append("VALUES (?, ?, ?, ?,?, ?, ?, ?, ?, ?) ");
	//private final StringBuffer INSERT_TURNO_PERSONA = new StringBuffer("INSERT INTO t1270turnoperson "
			//).append("(cod_pers, u_organ, turno, fini, ffin, est_id, sonom_id, fcreacion, cuser_crea) " // fmod, cuser_mod) "
			//).append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
	//JRR
	private final StringBuffer FIND_TURNO_PERSONA_BY_PK = new StringBuffer("SELECT "
			).append("cod_pers, u_organ, turno, fini, ffin, est_id, sonom_id "
			).append("FROM 	t1270turnoperson "
			).append("WHERE cod_pers = ? "
			).append("AND   turno = ? "					
			).append("AND   fini = ? ");
	
	//JRR
	private final StringBuffer UPDATE_TURNO_PERSONA = new StringBuffer("UPDATE t1270turnoperson "
			).append("SET ffin = ?,"
			).append("    u_organ = ?, "
			).append("    est_id = ?, "
			).append("    sonom_id = ?, "
			).append("    fmod = ?, "					
			).append("    cuser_mod = ? "					
			).append("WHERE cod_pers = ? "
			).append("AND   turno = ? "					
			).append("AND   fini = ? ");
	private final StringBuffer UPDATE_TURNO_PERSONA_MOD_SUST = new StringBuffer("UPDATE t1270turnoperson "
			).append("SET ffin = ?, observ=?,"
			).append("    u_organ = ?, "
			).append("    est_id = ?, "
			).append("    sonom_id = ?, "
			).append("    fmod = ?, "					
			).append("    cuser_mod = ? "					
			).append("WHERE cod_pers = ? "
			).append("AND   turno = ? "					
			).append("AND   fini = ? ");
	
	/*private final StringBuffer UPDATE_TURNO_PERSONA = new StringBuffer("UPDATE t1270turnoperson "
			).append("SET ffin = ?, "
			).append("    u_organ = ?, "
			).append("    est_id = ?, "
			).append("    sonom_id = ?, "
			).append("    fmod = ?, "					
			).append("    cuser_mod = ? "					
			).append("WHERE cod_pers = ? "
			).append("AND   turno = ? "					
			).append("AND   fini = ? ");*/
	
	//JRR
	private final StringBuffer DELETE_TURNO_PERSONA = new StringBuffer("DELETE FROM t1270turnoperson "
			).append("WHERE cod_pers = ? AND turno = ? AND fini = ? ");
	
	//MTM
	private final StringBuffer FIND_TURNO_PERSONA = new StringBuffer(" SELECT FIRST 1 DISTINCT t45.h_inicio, t45.h_fin "
			).append("FROM t45turno t45, t1270turnoperson t1270 "
			).append("WHERE t1270.cod_pers = ? AND t1270.turno = t45.cod_turno "
			).append("AND (t1270.fini <= ? AND ? <=t1270.ffin) "
			).append("AND t1270.est_id = '1' AND t45.est_id = '1' "
			//).append("AND t45.scontrol_id = '1' AND t45.oper_id != '1' "
			).append("AND t45.scontrol_id = '1' "		
			).append("AND ((? <= t45.h_inicio AND t45.h_inicio >= ?) OR "
			).append("(? >= t45.h_fin AND t45.h_fin<= ? )) ");
	
	
	//MTM
	private final StringBuffer FIND_TURNO_PERSONA_PRIMERDIA = new StringBuffer(" SELECT FIRST 1 DISTINCT t45.h_inicio, t45.h_fin "
			).append("FROM t45turno t45, t1270turnoperson t1270 "
			).append("WHERE t1270.cod_pers = ? AND t1270.turno = t45.cod_turno "
			).append("AND (t1270.fini <= ? AND ? <=t1270.ffin) "
			).append("AND t1270.est_id = '1' AND t45.est_id = '1' "
			).append("AND t45.scontrol_id = '1' "		
			).append("AND (('00:00' < ? AND ? <= t45.h_inicio ) AND "
			).append("('00:00' < ? AND ? <= t45.h_inicio )) ");
	
	private final StringBuffer FIND_TURNO_PERSONA_SEGUNDODIA = new StringBuffer(" SELECT FIRST 1 DISTINCT t45.h_inicio, t45.h_fin "
	).append("FROM t45turno t45, t1270turnoperson t1270 "
	).append("WHERE t1270.cod_pers = ? AND t1270.turno = t45.cod_turno "
	).append("AND (t1270.fini <= ? AND ? <=t1270.ffin) "
	).append("AND t1270.est_id = '1' AND t45.est_id = '1' "
	).append("AND t45.scontrol_id = '1' "		
	).append("AND ((t45.h_fin <= ? AND ? < '24:00' ) AND "
	).append("(t45.h_fin <= ? AND ? < '24:00' )) ");
	
	
	//ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno
	private final StringBuffer FIND_COLABORADORES_BYTURNOS_BYFECHACREAMODIF = new StringBuffer("select distinct p.t02cod_pers,p.t02ap_pate, p.t02ap_mate,p.t02nombres "
	).append("from t1270turnoperson tp, t02perdp p "
	).append("where tp.cod_pers=p.t02cod_pers and "
	).append("p.t02cod_stat=? and "		
	).append("(date(tp.fcreacion)=? or date(tp.fmod)=?) and "
	).append("tp.turno in (select t99codigo from t99codigos where t99cod_tab=? and t99tip_desc=? and t99estado=?) "
	).append("order by 1 asc ");
	
	
	private final StringBuffer FIND_TURNOS_BYCODPERS_BYFECHACREAMODIF = new StringBuffer("select tp.cod_pers,tp.turno,t.des_turno,tp.fini, tp.ffin, t.des_turno, t.h_inicio, t.h_fin,t.oper_id,t.scontrol_id "
	).append("from t1270turnoperson tp, t45turno t "
	).append("where tp.cod_pers=? and "
	).append("tp.turno=t.cod_turno and "	
	).append("(date(tp.fcreacion)=? or date(tp.fmod)=?) and "
	).append("tp.turno in (select t99codigo from t99codigos where t99cod_tab=? and t99tip_desc=? and t99estado=?) "
	).append("order by tp.fini asc ");
	//
	

	/**
	* 
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.
	* 
	* @param datasource Object
	*/
	public T1270DAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
		
	/**
	 * Metodo que se encarga de registrar el turno de un trabajador
	 * @throws DAOException
	 */	
	public void registrarTurnoPersona(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("registrarTurnoPersona - datos1: " + datos);	
		if(datos.get("modificar")!=null){
			executeUpdate(datasource, INSERT_TURNO_PERSONA_SUST.toString(), new Object[]{datos.get("cod_pers"), 
				datos.get("u_organ"), datos.get("turno"), datos.get("fini"), 
				datos.get("ffin"),datos.get("obsSustento"), datos.get("est_id"), datos.get("sonom_id"), 
				datos.get("fcreacion"), datos.get("cuser_crea")});
		}else{
			executeUpdate(datasource, INSERT_TURNO_PERSONA.toString(), new Object[]{datos.get("cod_pers"), 
				datos.get("u_organ"), datos.get("turno"), datos.get("fini"), 
				datos.get("ffin"), datos.get("est_id"), datos.get("sonom_id"), 
				datos.get("fcreacion"), datos.get("cuser_crea")});
		}
		
	}
	
	/**
	 * Metodo que busca el turno de un trabajador
	 * @throws DAOException
	 */	
	public Map findTurnoPersonaByPK(Map datos) throws DAOException {		
		Map mapa = null;
		try{
			if (log.isDebugEnabled()) log.debug("findTurnoPersonaByPK - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			mapa = executeQueryUniqueResult(datasource, FIND_TURNO_PERSONA_BY_PK.toString(),
					new Object[]{datos.get("cod_pers"), datos.get("turno"), datos.get("fini")});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return mapa;
	}
	
	/**
	 * Metodo que se encarga de Actualizar el turno de un trabajador
	 * @throws DAOException
	 */
	public void actualizarTurnoPersona(Map datos) throws DAOException {	
		if (log.isDebugEnabled()) log.debug("actualizarTurnoPersona - datos1: " + datos);	
		
		if(datos.get("modificar")!=null){
			executeUpdate(datasource, UPDATE_TURNO_PERSONA_MOD_SUST.toString(), 
					new Object[]{datos.get("ffin"),datos.get("obsSustento"),
								 datos.get("u_organ"),
								 datos.get("est_id"),
								 datos.get("sonom_id"),
								 datos.get("fmod"),
								 datos.get("cuser_mod"),
								 datos.get("cod_pers"),
								 datos.get("turno"),
									 datos.get("fini")
									 });
		}else{
			executeUpdate(datasource, UPDATE_TURNO_PERSONA.toString(), 
					new Object[]{datos.get("ffin"),
								 datos.get("u_organ"),
								 datos.get("est_id"),
								 datos.get("sonom_id"),
								 datos.get("fmod"),
								 datos.get("cuser_mod"),
								 datos.get("cod_pers"),
								 datos.get("turno"),
									 datos.get("fini")
									 });
		}		
	}
	
	/**
	  * Metodo que elimina el turno de un trabajador
	  * @param datos
	  * @return
	  * @throws DAOException
	  */
	public void eliminarTurnoPersona(Map params) throws DAOException {
		if (log.isDebugEnabled()) log.debug("eliminarTurnoPersona - params: " + params);
		executeUpdate(datasource, DELETE_TURNO_PERSONA.toString(), 
				new Object[]{params.get("cod_pers"), params.get("turno"), params.get("fini")});
	}
	
	/**
	 * Metodo que busca el turno de un trabajador
	 * @throws DAOException
	 */	
	public Map findTurnoPersonaExcepcional(Map datos) throws DAOException {		
		Map mapa = null;
		try{
				mapa = executeQueryUniqueResult(datasource, FIND_TURNO_PERSONA.toString(),
						new Object[]{datos.get("cod_pers"), datos.get("fechaMarca2"), datos.get("fechaMarca2")
						, datos.get("HoraIni"), datos.get("HoraFin"), datos.get("HoraIni"), datos.get("HoraFin")});		
			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return mapa;
	}
	
	
	/* ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno */	
	/**
	 * Metodo encargado de obtener la lista de colaboradores activos por turnos creados o modificados en una fecha 
	 * @return List
	 * @throws DAOException
	 */	
	public List findColaboradoresByTurnosByFechaCreaModif(Map datos) throws DAOException {	
		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1270DAO findColaboradoresByTurnosByFechaCreaModif");
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, FIND_COLABORADORES_BYTURNOS_BYFECHACREAMODIF.toString(), new Object[]{datos.get("estado"), datos.get("fecCreaModif"),datos.get("fecCreaModif"),datos.get("cod_tab"),datos.get("tip_desc"),datos.get("estado2")});
			
		} catch (Exception e) {
			log.error("*** SQL Error en findColaboradoresByTurnosByFechaCreaModif****",e);
		}
		return lista;
	}
	
	/**
	 * Metodo encargado de obtener la lista de turnos por colaborador y por tipos de turnos creados o modificados en una fecha 
	 * @return List
	 * @throws DAOException
	 */	
	public List findTurnosByCodPersByFechaCreaModif(Map datos) throws DAOException {	
		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1270DAO findTurnosByCodPersByFechaCreaModif");
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(datasource, FIND_TURNOS_BYCODPERS_BYFECHACREAMODIF.toString(), new Object[]{datos.get("codPers"), datos.get("fecCreaModif"),datos.get("fecCreaModif"),datos.get("cod_tab"),datos.get("tip_desc"),datos.get("estado2")});
			
		} catch (Exception e) {
			log.error("*** SQL Error en findTurnosByCodPersByFechaCreaModif****",e);
		}
		return lista;
	}
	/* FIN ICAPUNAY - PAS20165E230300096 - Alerta de Cambio de turno */	
	
}
