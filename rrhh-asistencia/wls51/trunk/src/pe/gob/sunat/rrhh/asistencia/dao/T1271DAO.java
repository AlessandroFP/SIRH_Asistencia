package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import pe.gob.sunat.utils.Constantes;
//import pe.gob.sunat.utils.Utiles;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sol.BeanFechaHora;

/**
 * 
 * Clase : T1271DAO 
 * Autor : PRAC-JCALLO 
 * Fecha : 07/03/2008
 * 
 * Descripcion: Esta clase se creo con la finalidad de que se generen todos los
 * metodos y funciones necesarias para poder hacer mantenimiento a la tabla
 * t1271asistencia.
 */

public class T1271DAO extends DAOAbstract {
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	private final StringBuffer QUERY1_SENTENCE = new StringBuffer("DELETE from t1271asistencia where cod_pers = ? and periodo = ? and fing = ? and hing = ? ");
	private final StringBuffer QUERY2_ANULA = new StringBuffer("DELETE from t1271asistencia where cod_pers = ? and fing = ? and hing = ? ");
	
	//PAS20155E230300022 papeletas generadas (estado 1) se generen y procesen con estado inicial 0
	private final StringBuffer findByCodPersFechaAndEstado = new StringBuffer("SELECT mov, fing, fsal, hing, hsal, estado_id, periodo from  t1271asistencia where cod_pers = ? and fing = ? and estado_id = ? ");	
	
	private final StringBuffer findByCodPersFechaPeriodo = new StringBuffer("SELECT mov, fing, fsal, hing, hsal, estado_id, periodo from  t1271asistencia where cod_pers = ? and fing = ? order by hing asc ");	// add "order by hing asc"  ICR 18052015 - PAS20155E230000154 - ajustes a salida no autorizada y exceso de refrigerio en generacion y proceso asistencia
	//private final StringBuffer findByCodPersFechaHingHsal = new StringBuffer("SELECT first 1 mov, fing, fsal, hing, hsal, estado_id, periodo from  t1271asistencia where cod_pers = ? and fing = ? and hing = ? and hsal = ? and estado_id in (?,?,?) ");	//ICR 10062015 - PAS20155E230300022 - ajustes a generacion y proceso asistencia //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	private final StringBuffer findByCodPersFechaHingHsal = new StringBuffer("SELECT first 1 mov, fing, fsal, hing, hsal, estado_id, periodo from  t1271asistencia where cod_pers = ? and fing = ? and hing = ? and hsal = ? and estado_id in (?,?,?,?) ");	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	private final StringBuffer findPersonalByFecha = new StringBuffer("SELECT distinct cod_pers, u_organ from t1271asistencia where fing = ? ");
	private final StringBuffer findPersonalAsistencia = new StringBuffer("SELECT t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) ")
	.append(" cod_uorga, t02cod_rel from t02perdp where t02cod_stat = ? ");
	
	private final StringBuffer findByCodPersPeriodo = new StringBuffer("SELECT t.cod_pers, t.mov, t.fing, t.hing, t.hsal, t.autor_id, t.estado_id ")
	.append(" from t1271asistencia t where t.cod_pers = ? and t.periodo = ? order 	by t.fing ");
	
	//dtarazona
	private final StringBuffer findByCodPersTurno = new StringBuffer("select t.h_inicio,t.h_fin,t.dias_int,t.oper_id,h_inirefr,t.h_finref,t.min_refr,tp.turno ")
	.append(" from t1270turnoperson tp,t45turno t ")
	.append(" where t.cod_turno=tp.turno and cod_pers=? and fini<=? and ffin>=? ");
	
	private final StringBuffer updateByCodPersFIniHIni = new StringBuffer("UPDATE t1271asistencia set mov = ? where cod_pers = ? and periodo = ? and fing = ? and hing = ? ");
	private final StringBuffer updatePapeletaByFecha = new StringBuffer("UPDATE t1271asistencia set mov = ?, estado_id = ? where cod_pers = ? and periodo = ? and fing = ? and hing = ? ");
	
	private final StringBuffer deleteByCodPersPeriodo = new StringBuffer("DELETE from t1271asistencia where cod_pers = ? and periodo = ? and fing = ? ");
	private final StringBuffer findByCodPersFecha = new StringBuffer("SELECT a.cod_pers, a.periodo, a.u_organ, a.mov, a.fing, a.hing, ")
	.append( " a.fsal, a.hsal, a.autor_id, a.jefe_autor, a.fecha_autor, a.estado_id, m.descrip, a.obs_papeleta ")
	.append( " from  t1271asistencia a, t1279tipo_mov m ")
	.append( " where a.cod_pers = ? and a.fing = ? and a.mov = m.mov ");
	
	//ICR 18/01/2013 PAS20134EB20000017 Ajustes a visibilidad de calificaciones indistinto de la uuoo para opcion Asistencia/Calificar Asistencia
	private final StringBuffer findByCodPersFechaMod = new StringBuffer("SELECT a.cod_pers, a.periodo, a.u_organ, a.mov, a.fing, a.hing, ")
	.append( " a.fsal, a.hsal, a.autor_id, a.jefe_autor, a.fecha_autor, a.estado_id, m.descrip, a.obs_papeleta ")
	.append( " from  t1271asistencia a, t1279tipo_mov m, t02perdp p ")
	.append( " where a.cod_pers = ? and a.fing = ? and a.mov = m.mov ")
	.append( " and a.cod_pers = p.t02cod_pers ");
	//FIN ICR 18/01/2013 PAS20134EB20000017 Ajustes a visibilidad de calificaciones indistinto de la uuoo para opcion Asistencia/Calificar Asistencia
	
	//ASANCHEZZ 20100412
	//COMENTANDO ESTO
	/*
	private final StringBuffer findByFIngFFinNull = new StringBuffer("SELECT cod_pers, u_organ, fing from t1271asistencia where (fsal IS NULL and hsal IS NULL) and fing >= ? and fing <= ? ");
	*/
	private final StringBuffer findByFIngFFinNull = new StringBuffer("SELECT cod_pers, u_organ, fing from t1271asistencia a, t02perdp p where a.cod_pers = p.t02cod_pers and (a.fsal IS NULL and a.hsal IS NULL) and a.fing >= ? and a.fing <= ? ");
	//FIN
	
	private final StringBuffer deleteByCodPersFecha = new StringBuffer("DELETE from t1271asistencia where cod_pers = ? and fing = ?");
	private final StringBuffer findByCodPersFFinNull = new StringBuffer("SELECT cod_pers from t1271asistencia where  cod_pers = ? and fing = ? and (fsal IS NULL and hsal IS NULL) ");
	private final StringBuffer joinWithT02findByCodPersUOrgFecha = new StringBuffer("SELECT a.cod_pers,(select t02ap_pate||' '||t02ap_mate||', '||t02nombres from t02perdp where t02cod_pers=a.cod_pers) as trabajador, a.periodo, a.u_organ, a.mov, a.fing, a.hing, a.fsal, a.hsal, a.autor_id,")
	.append( " a.jefe_autor, a.fecha_autor, a.estado_id, a.obs_papeleta from  t1271asistencia a  where a.cod_pers != ? and a.periodo > '000000' and fing >= ? and a.hing >= '00:00:00' and ")
	.append( " fsal <= ? and estado_id IS NOT NULL ");
	
	private final StringBuffer findPapeletas = new StringBuffer("SELECT a.cod_pers,(select t02ap_pate||' '||t02ap_mate||', '||t02nombres from t02perdp where t02cod_pers=a.cod_pers) as trabajador, a.periodo, a.u_organ, a.mov, a.fing, a.hing, a.fsal, a.hsal, a.autor_id,")
	//.append(" a.jefe_autor, a.fecha_autor, a.estado_id, a.obs_papeleta from  t1271asistencia a, t02perdp p where a.cod_pers = p.t02cod_pers and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like ? and ") (se quito like xq no debe filtrar x uo) ICAPUNAY-PAS20145E230000034 ajustes a consulta de "turnos administrativos" y "autorizar papeletas"
	.append(" a.jefe_autor, a.fecha_autor, a.estado_id, a.obs_papeleta from  t1271asistencia a, t02perdp p where a.cod_pers = p.t02cod_pers and ") //ICAPUNAY-PAS20145E230000034 ajustes a consulta de "turnos administrativos" y "autorizar papeletas"
	.append(" a.periodo > '000000' and a.estado_id = ? and a.cod_pers != ? and a.hing >= '00:00:00' and   a.jefe_autor = ? ")
	.append(" order by a.fing, a.cod_pers");			//jquispe: 06/08/2013
	
	private final StringBuffer findPapeletasSubordinados = new StringBuffer("SELECT a.cod_pers,(select t02ap_pate||' '||t02ap_mate||', '||t02nombres from t02perdp where t02cod_pers=a.cod_pers) as trabajador,  a.periodo, a.u_organ, a.mov, a.fing, a.hing, a.fsal, a.hsal, ")
	.append(" a.autor_id, a.jefe_autor, a.fecha_autor, a.estado_id, a.obs_papeleta from  t1271asistencia a, t02perdp p where a.cod_pers != ? and a.periodo > '000000' and fing >= ? and a.hing >= '00:00:00' and fsal <= ? and estado_id IS NOT NULL and ")
	.append(" a.cod_pers = p.t02cod_pers ");			//jquispe: 06/08/2013 
	
	private final StringBuffer findMarcacionCalificada = new StringBuffer("SELECT cod_pers from t1271asistencia where cod_pers = ? and ((fing = ? and hing = ? ) or (fsal = ? and hsal = ? )) and estado_id != ? ");
	private final StringBuffer insertRegistroAsistencia = new StringBuffer("INSERT into t1271asistencia (cod_pers, periodo, u_organ, mov, fing, hing, fsal, hsal, reloj_ing, ")
	.append(" reloj_sal, autor_id, jefe_autor, estado_id,fcreacion,cuser_crea) Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	
	private final StringBuffer findRegistroAsistencia = new StringBuffer("select cod_pers, periodo, mov, fing, hing, fsal, hsal,estado_id from t1271asistencia where cod_pers = ? And periodo = ? And fing = ? And hing = ? And estado_id = ?");//PAS20155E230300022 papeletas generadas (estado 1) se generen y procesen con estado inicial 0
	private final StringBuffer updateRegistroAsistencia = new StringBuffer("UPDATE t1271asistencia Set mov = ?, fsal = ?, hsal = ?, fmod = ?, cuser_mod = ? where cod_pers = ? And periodo = ? And fing = ? And hing = ? And estado_id = ?");
	private final StringBuffer updateRegistroPapeleta = new StringBuffer("UPDATE t1271asistencia Set mov = ?, autor_id = ?, jefe_autor = ?, estado_id = ?, obs_papeleta = ?, fmod = ?, cuser_mod = ? where cod_pers = ? And periodo = ? And fing = ? And hing = ? ");
	private final StringBuffer findByKeyAsistencia = new StringBuffer("SELECT cod_pers, periodo, u_organ, mov, fing, hing, fsal, hsal, reloj_ing, reloj_sal, autor_id, jefe_autor, fecha_autor,")
            .append(" estado_id, fcreacion, cuser_crea, fmod, cuser_mod, obs_papeleta from t1271asistencia where cod_pers = ? And periodo = ? and u_organ= ? and fing = ? And hing = ? ");
	
	private final StringBuffer updateEliminarPapeleta = new StringBuffer("UPDATE t1271asistencia Set mov = ?, autor_id = ?, jefe_autor = ?, fecha_autor = null , estado_id = ?, obs_papeleta = ?, fmod = ?, cuser_mod = ? where cod_pers = ? And periodo = ? And fing = ? And hing = ? ");
	private final StringBuffer updateAsistenciaFechaAutor = new StringBuffer("UPDATE t1271asistencia Set mov = ?, autor_id = ?, jefe_autor = ?, fecha_autor = ? , estado_id = ?, obs_papeleta = ?, fmod = ?, cuser_mod = ? where cod_pers = ? And periodo = ? And fing = ? And hing = ? ");

	//JRR
	private final StringBuffer findJefeAutorPapeletasPendientesByPeriodo = new StringBuffer("SELECT unique jefe_autor ")
    .append(" from t1271asistencia where periodo = ? and estado_id = ? and jefe_autor is not null ");

	//JRR
	private final StringBuffer findTrabPapeletasPendientesByPeriodoAndJefe = new StringBuffer("SELECT cod_pers, fmod ")
    .append(" from t1271asistencia where periodo = ? and estado_id = ? and jefe_autor = ? ");

	//PRAC-ASANCHEZ 18/08/2009
	private final StringBuffer FIND_BY_COD_PERS_MOV_FECHA = new StringBuffer("SELECT t.cod_pers, t.mov, t.fing, t.hing, t.hsal, t.autor_id, t.estado_id ")
	.append(" from t1271asistencia t where t.cod_pers = ? and t.mov = ? and t.fing = ? order by t.hing ");
	
	//JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES
	private final StringBuffer findTrabMovimientoAsistencia = new StringBuffer(" SELECT unique a.cod_pers ")
		.append(" FROM t1271Asistencia a, t1279Tipo_Mov t ")
		.append(" WHERE a.mov = t.mov and ")		
		.append(" a.mov in ('98','01','00','10','12','09') and ")		
		//.append(" a.fing in (?,?) and ")
		.append(" a.estado_id = ? ")		
		.append(" and a.cod_pers is not null and ")
		.append(" a.fing between ? and ?");
	
	private final StringBuffer findMovimientoAsistenciaByTrab = new StringBuffer(" SELECT a.cod_pers, a.periodo, a.mov, a.fing, a.hing, a.hsal, t.descrip ")
		.append(" FROM t1271Asistencia a, t1279Tipo_Mov t ")
		.append(" WHERE a.mov = t.mov and ")		
		.append(" a.mov in ('98','01','00','10','12','09') and ")
		.append(" a.cod_pers = ? and ")		
		.append(" a.estado_id = ? and ")
		.append(" a.fing between ? and ? ")
	    //.append(" order by a.fing "); //ICAPUNAY 09/08/2011 DEBE ORDENARSE POR "TIPO MOV." Y "FECHA" ASCENDENTE
		.append(" order by t.descrip asc,a.fing asc "); //ICAPUNAY 09/08/2011 DEBE ORDENARSE POR "TIPO MOV." Y "FECHA" ASCENDENTE
	//FIN - JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	  private static final StringBuffer findUOsByJefe = new StringBuffer("SELECT * ")
	  .append("FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? ")
	  .append("and t12ind_estad=? "); 
	  //ICAPUNAY - PAS20165E230300132
	
	  
	  //AGONZALESF - PAS20171U230200001  solicitud de reintegro  Papeletas para ver documentos aprobados en reintegro
	  //AGONZALESF - PAS20191U230200011 - solicitud de reintegro
	  private static final StringBuffer FIND_DOC_APROB_PAPELETA = new StringBuffer("SELECT a.fing as fecha,")
	  .append(" a.hing AS marcacionentrada,")
	  .append(" a.hsal AS marcacionsalida,")
	  .append(" m.descrip AS papeleta,")
	  .append(" a.obs_papeleta AS motivo")
	  .append(" FROM t1271asistencia a,t1279tipo_mov m ")
	  .append(" WHERE a.mov=m.mov ")
	  .append(" AND a.cod_pers=?") 
	  .append(" AND a.estado_id<>'0' ") 
	  .append(" AND a.fing between ? AND ? ")
	  .append(" AND a.mov in (SELECT  t99descrip from t99codigos   WHERE t99cod_tab='R07' and t99tip_desc ='D' and t99tipo='PAPEL' and t99estado ='1' )")
	  .append(" ORDER BY a.periodo desc ");

	
	private DataSource datasource;	
	
	/**
	 * 
	 * Este constructor del DAO dicierne como crear el datasource
	 * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	 * mejorar la invocacion del dao.
	 * 
	 * @param datasource Object
	 *  
	 * */
	public T1271DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.datasource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}


	/**
	 * 
	 * Metodo que se encarga de eliminar calificaciones 
	 * de un rango de fechas
	 * 
	 * @throws DAOException 
	 */
	public boolean deleteAsistencia(String cod_pers, String periodo,
			String fing, String hing) throws DAOException {
		int modifica =0;
		modifica = executeUpdate(datasource, QUERY1_SENTENCE.toString(), new Object[]{cod_pers, periodo,  new FechaBean(fing).getSQLDate(), hing}); //Utiles.stringToDate(fing)		
		return (modifica>0) ? true:false;
	}
	
	/**
	 * Metodo que se encarga de eliminar calificaciones 
	 * de un rango de fechas por Sol. Anulacion
	 * 
	 * @throws DAOException
	 */
	public boolean deleteAsistenciaAnulacion(String cod_pers, 
			String fing, String hing) throws DAOException {
		int modifica =0;		
		modifica = executeUpdate(datasource, QUERY2_ANULA.toString(), new Object[]{cod_pers,  new FechaBean(fing).getSQLDate(), hing}); //Utiles.stringToDate(fing) 
		return (modifica>0) ? true:false;
	}
	
	//PRAC-JCALLO 04/03/2008 migracion de sp.asistencia.dao a rrhh.asistencia.dao 
	/**
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * determinado trabajador, para una fecha y periodos especificos.
	 * 
	 * @throws DAOException
	 */
	public List findByCodPersFechaPeriodo(Map params) throws DAOException {
		List detalle = null;		
		detalle = executeQuery(datasource, findByCodPersFechaPeriodo.toString(), new Object[]{params.get("cod_pers"), new FechaBean((String)params.get("fing")).getSQLDate()});		
		return detalle;
	}
	
	//PAS20155E230300022 papeletas generadas (estado 1) se generen y procesen con estado inicial 0
	/**
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * determinado trabajador, para una fecha y estado especifico.
	 * 
	 * @throws DAOException
	 */
	public List findByCodPersFechaAndEstado(Map params) throws DAOException {
		List result = null;		
		result = executeQuery(datasource, findByCodPersFechaAndEstado.toString(), new Object[]{params.get("cod_pers"), new FechaBean((String)params.get("fing")).getSQLDate(),params.get("estado_id")});		
		return result;
	}
	
	// ICR 10062015 - PAS20155E230300022 - ajustes a generacion y proceso asistencia
	/**
	 * Metodo que se encarga de buscar registro de asistencia de un
	 * determinado trabajador, para una fecha, estado, hora de ingreso y salida especifica.
	 * 
	 * @throws DAOException
	 */
	//public Map findByCodPersFechaHingHsal(String codPers, String  fechaIng, String horaIng, String horaSal, String estado1, String estado2, String estado3) throws DAOException { //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	public Map findByCodPersFechaHingHsal(String codPers, String  fechaIng, String horaIng, String horaSal, String estado1, String estado2, String estado3,String estado4) throws DAOException { //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
		Map calificacion= null;
		if (log.isDebugEnabled()) log.debug("T1271DAO - findByCodPersFechaHingHsal");
		if (log.isDebugEnabled()) log.debug("T1271DAO - findByCodPersFechaHingHsal-codPers: "+codPers+" fechaIng: "+fechaIng+" horaIng: "+horaIng + " horaSal: "+horaSal + " estado1: "+estado1 + " estado2: "+estado2+ " estado3: "+estado3+ " estado4: "+estado4);
		try{		
			//calificacion = executeQueryUniqueResult(datasource, findByCodPersFechaHingHsal.toString(), new Object[]{codPers,new FechaBean((String)fechaIng).getSQLDate(),horaIng,horaSal,estado1,estado2,estado3}); //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral		
			calificacion = executeQueryUniqueResult(datasource, findByCodPersFechaHingHsal.toString(), new Object[]{codPers,new FechaBean((String)fechaIng).getSQLDate(),horaIng,horaSal,estado1,estado2,estado3,estado4}); //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [T1271DAO - findByCodPersFechaHingHsal]");
		}	
		
		return calificacion;
	}
	// ICR 10062015 - PAS20155E230300022 - ajustes a generacion y proceso asistencia

	/**
	 * Metodo que se encarga de buscar los codigos de los trabajadores y sus
	 * respectivas areas organizacionales a partir del registro de asistencia de
	 * una determinada fecha.
	 * 
	 * @throws DAOException
	 */
	public List findPersonalByFecha(Map params) throws DAOException {	
		List detalle = null;//lista resultado		
		StringBuffer strSQL = new StringBuffer(findPersonalByFecha.toString());
		
		//criterios de visibilidad
		Map seguridad = (HashMap)params.get("seguridad");
		if (seguridad != null  && !seguridad.isEmpty()) {
			String codPers = (String) seguridad.get("codPers");

			strSQL.append(" and u_organ in ");
			strSQL.append(" (select u_organ from t1485seg_uorga "
					).append( " where cod_pers = '" ).append( codPers
					).append( "' and operacion = '"
//					).append( Constantes.PROCESO_CALIFICACION ).append( "') ");
					).append( constantes.leePropiedad("PROCESO_CALIFICACION") ).append( "') ");
			
		}
		strSQL.append(" order by cod_pers");
		
		detalle = executeQuery(datasource, strSQL.toString(), new Object[]{new FechaBean(params.get("fecha").toString()).getSQLDate()});

		return detalle;
	}

	/**
	 * Metodo que se encarga de buscar los codigos de los trabajadores y sus
	 * respectivas areas organizacionales a partir del registro de asistencia de
	 * un periodo y filtrados por un criterio con un valor determinados.
	 * 
	 * @throws DAOException
	 */
	public List findPersonalAsistencia(Map params) throws DAOException {

		List detalle = null;
		StringBuffer strSQL = new StringBuffer(findPersonalAsistencia.toString());		
		String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
		Map seguridad = (params.get("seguridad") != null) ? (HashMap)params.get("seguridad"):new HashMap();
		String valor =  (params.get("valor") != null ) ? params.get("valor").toString():"";
		
		//JRR - 22/04/2010 - Cambio para CAS
		//JRR - 27/04/2011 - Modificación por Formativas
		if (params.get("regimen")!=null && params.get("regimen").equals("2")) {
			strSQL.append(" and t02cod_rel = '09' ");
		} else if (params.get("regimen")!=null && params.get("regimen").equals("1")){
			//strSQL.append(" and (t02cod_rel = '01' or t02cod_rel = '02') ");
			strSQL.append(" and t02cod_rel not in ('09','10') ");
		} else if (params.get("regimen")!=null && params.get("regimen").equals("3")){
			strSQL.append(" and t02cod_rel = '10' ");
		}
		//JRR - Cuando no se filtra por regimen, vienen todos
		
		if (criterio.equals("0")) {
			strSQL.append(" and t02cod_pers = '" ).append( valor.trim().toUpperCase()
			).append( "'");
		}

		if (criterio.equals("1")) {
			strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = '"
			).append( valor.trim().toUpperCase() ).append( "'");
		}

		//JRR - 27/04/2011
		if (criterio.equals("2") && !valor.equals("")){//Intendencia
			String intendencia = valor.substring(0,2);
			strSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
		}//SI ES INSTITUCIONAL("3") NO ES NECESARIO NINGUN FILTRO DE COD_UORG
		
		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty()) {
			String codPers = (String) seguridad.get("codPers");
			strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in ");
			strSQL.append(" (select u_organ from t1485seg_uorga "
					).append( " where cod_pers = '" ).append( codPers
//					).append( "' and operacion = '" ).append( Constantes.PROCESO_ASISTENCIA
					).append( "' and operacion = '" ).append( constantes.leePropiedad("PROCESO_ASISTENCIA")							
					).append( "') ");
		}
		
		strSQL.append(" order by t02cod_pers");
		
		//detalle = executeQuery(datasource, strSQL.toString(), new Object[]{Constantes.ACTIVO});
		detalle = executeQuery(datasource, strSQL.toString(), new Object[]{constantes.leePropiedad("ACTIVO")});
		
		return detalle;
	}

	/**
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * trabajador en un periodo determinado.
	 * @throws DAOException
	 */
	public List findByCodPersPeriodo(Map params) throws DAOException {
		List detalle = null;		
		
		String cod_pers = (params.get("cod_pers") != null) ? params.get("cod_pers").toString():"";
		String periodo = (params.get("periodo") != null) ? params.get("periodo").toString():"";
		
		detalle = executeQuery(datasource, findByCodPersPeriodo.toString(), new Object[]{cod_pers, periodo});
		
		return detalle;
	}

	//datarazona
	/**
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * trabajador en un periodo determinado.
	 * @throws DAOException
	 */
	public List buscarDetalleTurnoPersona(Map params) throws DAOException {
		List detalle = null;	
		log.debug("Entrando a buscarDetalleTurnoPersona:"+params.toString());
		String cod_pers = (params.get("codPers") != null) ? params.get("codPers").toString():"";
		
		detalle = executeQuery(datasource, findByCodPersTurno.toString(), new Object[]{cod_pers,new FechaBean((String)params.get("fecha")).getSQLDate(), new FechaBean((String)params.get("fecha")).getSQLDate()});
		log.debug("FechaFiltro:"+new FechaBean((String)params.get("fecha")).getSQLDate());
		return detalle;
	}

	/**
	 * Metodo que realiza la modifica el codigo de movimiento de los registros
	 * de asistencia de un trabajador, para un periodo, fecha y hora
	 * determinados.
	 * @throws DAOException
	 */
	public boolean updateByCodPersFIniHIni(Map params) throws DAOException {
		
		int res = executeUpdate(datasource, updateByCodPersFIniHIni.toString(), new Object[]{params.get("mov"), 
				params.get("cod_pers"), params.get("periodo"), new FechaBean((String)params.get("fechaIni")).getSQLDate(),
				params.get("horaIni")});

		return (res>0) ? true:false;
	}

	/**
	 * Metodo que se encarga de modificar el codigo de movimiento y el estado de
	 * los registros de asistencia de un trabajador, para un periodo, fecha y
	 * hora determinados.
	 * @param params Map(String cod_pers,String periodo, String fechaIni, String horaIni, String mov, String estado)
	 * @throws DAOException
	 */
	public boolean updatePapeletaByFecha(Map params) throws DAOException {

		int res = executeUpdate(datasource, updatePapeletaByFecha.toString(), new Object[]{params.get("mov"), params.get("estado"), 
				params.get("cod_pers"), params.get("periodo"), new FechaBean((String)params.get("fechaIni")).getSQLDate(),
				params.get("horaIni")});
		
		return ( res>0 ) ? true:false;
	}

	/**
	 * Metodo que se encarga de eliminar los registros de asistencia de un
	 * trabajador para un periodo y fecha determinados.
	 * @param params Map (String cod_pers, String periodo, String fechaIni)
	 * @throws DAOException
	 */
	public void deleteByCodPersPeriodo(Map params) throws DAOException {

		executeUpdate(datasource, deleteByCodPersPeriodo.toString(), new Object[]{params.get("cod_pers"), params.get("periodo"),
				 new FechaBean((String)params.get("fechaIni")).getSQLDate()});
		
	}

	/**
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * trabajador y fechas determinados.
	 * @param params Map (String cod_pers, String fecha, Map seguridad)
	 * @throws DAOException
	 */
	public List findByCodPersFecha(Map params) throws DAOException {

		List listaResult = new ArrayList();
		
		//StringBuffer strSQL = new StringBuffer(findByCodPersFecha.toString());//ICR 18/01/2013 PAS20134EB20000017 Ajustes a visibilidad de calificaciones indistinto de la uuoo para opcion Asistencia/Calificar Asistencia
		StringBuffer strSQL = new StringBuffer(findByCodPersFechaMod.toString());//ICR 18/01/2013 PAS20134EB20000017 Ajustes a visibilidad de calificaciones indistinto de la uuoo para opcion Asistencia/Calificar Asistencia
		
		Map seguridad = (params.get("seguridad") != null ) ? (HashMap)params.get("seguridad"):new HashMap();
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		List objsJefe = new ArrayList();
		List resUOsJefe= new ArrayList();
		HashMap uoMap = new HashMap();
	    String codUoJef = "";
		String codPersUsuario = seguridad!=null && !seguridad.isEmpty()? ((String) seguridad.get("codPers")).trim():"";
		//ICAPUNAY - PAS20165E230300132 
		
		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty()) {
			Map roles = (HashMap) seguridad.get("roles");
			String uoSeg = (String) seguridad.get("uoSeg");
			String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

//			if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
			if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {				
				strSQL.append(" and 1=1 ");
//			} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null
//					|| roles.get(Constantes.ROL_JEFE) != null) {
			} 
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
				//ICR 18/01/2013 PAS20134EB20000017 Ajustes a visibilidad de calificaciones indistinto de la uuoo para opcion Asistencia/Calificar Asistencia
				//strSQL.append(" and ((a.u_organ like '" ).append( uoSeg ).append( "') ");
				//strSQL.append(" or (a.u_organ in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
				strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" ).append( uoSeg ).append( "') ");
				strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
				//FIN ICR 18/01/2013 PAS20134EB20000017 Ajustes a visibilidad de calificaciones indistinto de la uuoo para opcion Asistencia/Calificar Asistencia
			}
			//else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			else if (
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
					roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
				
				//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				//si es jefe, debe visualizar informacion de colaboradores de todas las unidades donde es encargado o jefe (incluyendo subunidades)
				objsJefe.add(codPersUsuario.toUpperCase());
				objsJefe.add("1");    		  
				resUOsJefe=executeQuery(datasource, findUOsByJefe.toString(), objsJefe.toArray());
				if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
				if (resUOsJefe.size()>0){    			   		      
					for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
						uoMap=(HashMap)resUOsJefe.get(i);
						codUoJef= uoMap.get("t12cod_uorga").toString().trim();
						if (i==0){//0 (primer registro)
							strSQL = strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
						}else{
							strSQL = strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
						}    		    	  
						if (i==resUOsJefe.size()-1){//2 (ultimo registro)
							strSQL = strSQL.append(") ");
						}
					}
				}
				//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" ).append( uoSeg ).append( "' ");//ICR 18/01/2013 PAS20134EB20000017 Ajustes a visibilidad de calificaciones indistinto de la uuoo para opcion Asistencia/Calificar Asistencia
				//ICAPUNAY - PAS20165E230300132				
				
			} else {
				strSQL.append(" and 1=2 ");
			}
		}
		
		strSQL.append(" order by a.hing");
		if(log.isDebugEnabled()) log.debug("strSQL final: " + strSQL);
		
		listaResult = executeQuery(datasource, strSQL.toString(), new Object[]{params.get("cod_pers"), new FechaBean((String)params.get("fecha")).getSQLDate()});
		
		return listaResult;
	}

	/**
	 * Metodo que se encarga de buscar los registros de asistencia que se
	 * encuentren dentro de un determinado rango de fechas.
	 * @param params Map (String fechaIni,String fechaFin, String criterio, String valor)
	 * @throws DAOException
	 */
	public List findByFIngFFinNull(Map params) throws DAOException {

		List listaResult = new ArrayList();
		try{
			StringBuffer strSQL = new StringBuffer(findByFIngFFinNull.toString());
			String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
			String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";

			//ASANCHEZZ 20100409
			//COMENTANDO ESTO
			/*
			if (criterio.equals("0")) {
				strSQL.append(" and cod_pers = '" ).append( valor.trim().toUpperCase()).append( "' ");
			}
			if (criterio.equals("1")) {
				strSQL.append(" and u_organ = '" ).append( valor.trim().toUpperCase()).append( "' ");
			}			
			 */
			
			if(log.isDebugEnabled())log.debug("T1271DAO - findByFIngFFinNull - params: " + params);
			
			String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
	
			if (criterio.equals("0")) {//Registro
				strSQL.append(" and a.cod_pers = '" ).append( valor.trim().toUpperCase()).append( "' ");
			}else if(criterio.equals("1")){//UUOO
				strSQL.append(" and a.u_organ = '" ).append( valor.trim().toUpperCase()).append( "' ");
			}else if(criterio.equals("2") && !valor.equals("")){//Intendencia
				String intendencia = valor.substring(0,2);
				strSQL.append(" and a.u_organ LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
			}//si criterio = 3, es decir institucional, entonces no se aplica filtro
			
			if (regimen.equals("0")) {//276 -728
				strSQL.append(" and p.t02cod_rel not in ('09','10') ");//ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION DE ASISTENCIA
			}else if(regimen.equals("1")){//1057
				strSQL.append(" and p.t02cod_rel = '09' ");
			}else if(regimen.equals("2")){//Modalidad Formativa //ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION DE ASISTENCIA
				strSQL.append(" and p.t02cod_rel = '10' ");
			}
			//FIN ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION DE ASISTENCIA
			
			if(log.isDebugEnabled())log.debug("T1271DAO - findByFIngFFinNull - strSQL: " + strSQL);
			
			//FIN

			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			listaResult = executeQuery(datasource, strSQL.toString(), new Object[]{new FechaBean((String)params.get("fechaIni")).getSQLDate(),
				new FechaBean((String)params.get("fechaFin")).getSQLDate()});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return listaResult;
	}

	/**
	 * Meotodo que se encarga de eliminar los registros asistencia de un
	 * trabajador para una fecha especifica.
	 * @param params Map(String codPers, String fecha)
	 * @throws DAOException
	 */
	public void deleteByCodPersFecha(Map params) throws DAOException {
		
		executeUpdate(datasource, deleteByCodPersFecha.toString(), new Object[]{params.get("cod_pers"), 
			new FechaBean((String)params.get("fecha")).getSQLDate()});

	}

	/**
	 * Metodo que se encarga de determinar si existen registros de asistencia
	 * para un trabajador en una fecha determinada.
	 * @param params Map(String cod_pers,String fecha)
	 * @throws DAOException
	 */
	public boolean findByCodPersFFinNull(Map params) throws DAOException {

		List resultado = new ArrayList();
		boolean resp = false;
		
		resultado = executeQuery(datasource, findByCodPersFFinNull.toString(), new Object[]{params.get("cod_pers"), 
			new FechaBean((String)params.get("fecha")).getSQLDate()});
		
		resp = (resultado != null && !resultado.isEmpty()) ? ( (resultado.size() > 0) ? true:false ): false;
		
		return resp;
	}

	/**
	 * Metodo que se encarga de buscar los datos del registro de asistencia y de
	 * personal filtrados por registro de personal, unidad organizacional y
	 * rango fechas de asistencia. Join con la tabla T02Perdp
	 * @param params Map(String codPers, String codUOrg, String fechaIni, String fechaFin, Map seguridad)
	 * @throws DAOException
	 */
	public List joinWithT02findByCodPersUOrgFecha(Map params) throws DAOException {
		
		Map seguridad = (params.get("seguridad") != null ) ? (HashMap)params.get("seguridad"): new HashMap();
		StringBuffer strSQL = new StringBuffer(joinWithT02findByCodPersUOrgFecha.toString());
		
		String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():"";
		String fechaFin = (params.get("fechaFin") != null ) ? params.get("fechaFin").toString():"";
		
		FechaBean fIni = null;
		FechaBean fFin = null;
		
		List listaResult = new ArrayList();
		
		//criterios de visibilidad
		if (seguridad != null) {

			Map roles = (HashMap) seguridad.get("roles");
			String uoSeg = (String) seguridad.get("uoSeg");
			String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
				strSQL.append(" and 1=1 ");
			} 
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
				strSQL.append(" and ((a.u_organ like '" ).append( uoSeg ).append( "') ");
				strSQL.append(" or (a.u_organ in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");
			}
			//else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			else if (
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
					roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
				strSQL.append(" and a.u_organ like '" ).append( uoSeg ).append( "' ");
			} else {
				strSQL.append(" and 1=2 ");
			}
		}

		strSQL.append(" order by fing, hing");
		
		if (fechaIni.trim().equals("")) {
			fIni = new FechaBean();
		} else {
			fIni = new FechaBean(fechaIni);
		}

		if (fechaFin.trim().equals("")) {
			fFin = fIni;
		} else {
			fFin = new FechaBean(fechaFin);
		}
		
		listaResult = executeQuery(datasource, strSQL.toString(), new Object[]{params.get("cod_pers"), fIni.getSQLDate(), fFin.getSQLDate()});
		
		return listaResult;
	}
	
	/**
	 * Metodo que se encarga de buscar los datos del registro de asistencia y de
	 * personal filtrados por registro de personal, unidad organizacional y
	 * rango fechas de asistencia. Join con la tabla T02Perdp
	 * @param params Map(String codPers, Map seguridad)
	 * @throws DAOException
	 */
	public List findPapeletas( Map params )throws DAOException {
		
		List listaResult = new ArrayList();
		Map seguridad = (params.get("seguridad") != null ) ? (HashMap)params.get("seguridad"): new HashMap();
		String uoSeg = (seguridad.get("uoSeg") != null ) ? seguridad.get("uoSeg").toString():"";
		String cod_pers = (params.get("cod_pers") != null ) ? params.get("cod_pers").toString():"";
		
		//listaResult = executeQuery(datasource, findPapeletas.toString(), new Object[]{uoSeg, constantes.leePropiedad("PAPELETA_REGISTRADA"), cod_pers, cod_pers}); (se quita uoseg porque puede tener mas de 1 unidad y que no esta en su jerarquia) ICAPUNAY-PAS20145E230000034 ajustes a consulta de "turnos administrativos" y "autorizar papeletas"
		listaResult = executeQuery(datasource, findPapeletas.toString(), new Object[]{constantes.leePropiedad("PAPELETA_REGISTRADA"), cod_pers, cod_pers}); //ICAPUNAY-PAS20145E230000034 ajustes a consulta de "turnos administrativos" y "autorizar papeletas"

		
		return listaResult;
	}	

	/**
	 * Metodo que se encarga de buscar los datos del registro de asistencia y de
	 * personal filtrados por registro de personal, unidad organizacional y
	 * rango fechas de asistencia. Join con la tabla T02Perdp
	 * @param params Map(String codPers, String fechaIni, String fechaFin, Map seguridad)
	 * @throws DAOException
	 */
	public List findPapeletasSubordinados(Map params) throws DAOException {

		List listaResult = new ArrayList();
		Map seguridad = (params.get("seguridad") != null ) ? (HashMap)params.get("seguridad"): new HashMap();
		String cod_pers = (params.get("cod_pers") != null ) ? params.get("cod_pers").toString():"";
		StringBuffer strSQL = new StringBuffer(findPapeletasSubordinados.toString());

		String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():"";
		String fechaFin = (params.get("fechaFin") != null ) ? params.get("fechaFin").toString():"";
		
		FechaBean fIni = null;
		FechaBean fFin = null;

		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty()) {

			Map roles = (HashMap) seguridad.get("roles");
			String cmbEstado = (String) seguridad.get("cmbEstado");
			String cmbCriterio = (String) seguridad.get("cmbCriterio");
			String txtValor = (String) seguridad.get("txtValor");
			String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {					
				//strSQL.append(" and a.u_organ like '" ).append( uoSeg ).append( "' ");
				strSQL.append(" and a.jefe_autor = '" ).append(cod_pers.toUpperCase()).append( "' ");
				
				if (!cmbEstado.equals("-1")){
					strSQL.append(" and a.estado_id = '" ).append( cmbEstado ).append( "' ");
				}					
				if (cmbCriterio.equals("0") && !txtValor.trim().equals("")){
					strSQL.append(" and a.cod_pers like '" ).append( txtValor.toUpperCase().trim() ).append( "%' ");
				}
				else if (cmbCriterio.equals("1") && !txtValor.trim().equals("")){				//jquispe: 07/08/2013
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" ).append( txtValor.toUpperCase().trim() ).append( "%' ");
				}
			} else {
				strSQL.append(" and 1=2 ");
			}
		}
		
		strSQL.append(" order by fing, hing");

		if (fechaIni.trim().equals("")) {
			fIni = new FechaBean();
		} else {
			fIni = new FechaBean(fechaIni);
		}

		if (fechaFin.trim().equals("")) {
			fFin = fIni;
		} else {
			fFin = new FechaBean(fechaFin);
		}
		
		listaResult = executeQuery(datasource, strSQL.toString(), new Object[]{cod_pers, fIni.getSQLDate(), fFin.getSQLDate()});
		
		return listaResult;
	}	
	
	/**
	 * 
	 * @param params Map(String codPers,String fecha, String hora)
	 * @throws DAOException
	 */
	public boolean findMarcacionCalificada(Map params) throws DAOException {
		
		boolean res = false;
		List resultado = new ArrayList();	
		
		resultado = executeQuery(datasource, findMarcacionCalificada.toString(), new Object[]{params.get("cod_pers"), 
			new FechaBean((String)params.get("fecha")).getSQLDate(), params.get("hora"),
			new FechaBean((String)params.get("fecha")).getSQLDate(), params.get("hora"),
			constantes.leePropiedad("PRE_CALIFICACION_ASIS")});

		res = (resultado != null && !resultado.isEmpty()) ? ( (resultado.size() > 0) ? true:false ): false;
		return res;
	}
	
	
	/**
	 * Metodo que inserta registro de Asistencia
	 * @param params Map (String codPers,String periodo,String codUO,String fechaMarcacion,String horaMarcacion, String fechaFinMov,String horaFinMov,String calificacion,String tMov,String relojIni,String relojFin,String usuario)
	 * @throws DAOException
	 */
	public boolean insertRegistroAsistencia(Map params) throws DAOException {
		int result;
		
		result = executeUpdate(datasource, insertRegistroAsistencia.toString(), new Object[]{params.get("cod_pers"), params.get("periodo"), params.get("cod_uorgan"),
					params.get("mov"), new FechaBean((String)params.get("fechaMarcacion")).getSQLDate(), params.get("horaMarcacion"), 
					new FechaBean((String)params.get("fechaFinMov")).getSQLDate(), params.get("horaFinMov"), params.get("relojIni"), params.get("relojFin"),
					"", "", params.get("calificacion"), new FechaBean().getTimestamp(), params.get("usuario")});
		
		return (result>0);
	}
	
	
	
	
	/**
	 * Metodo que Actualiza registro de Asistencia
	 * @param params Map(String tMov,String fechaFinMov,String horaFinMov,String usuario,String codPers,String periodo,String fechaMarcacion,String horaMarcacion,String calificacion)
	 * @throws DAOException
	 */
	public boolean updateRegistroAsistencia(Map params) throws DAOException {
		
		int result = 0;
		
		result = executeUpdate(datasource, updateRegistroAsistencia.toString(), new Object[]{params.get("mov"), 
			new FechaBean((String)params.get("fechaFinMov")).getSQLDate(), params.get("horaFinMov"), new FechaBean().getTimestamp(), params.get("usuario"),
			params.get("cod_pers"), params.get("periodo"), new FechaBean((String)params.get("fechaMarcacion")).getSQLDate(), params.get("horaMarcacion"), params.get("calificacion")});


		return (result>0);
	}
	
	//PAS20155E230300022 papeletas generadas (estado 1) se generen y procesen con estado inicial 0
	/**
	 * Metodo que lista registros de Asistencia
	 * @param params Map(String tMov,String fechaFinMov,String horaFinMov,String usuario,String codPers,String periodo,String fechaMarcacion,String horaMarcacion,String calificacion)
	 * @throws DAOException
	 */
	public List findRegistroAsistencia(Map params) throws DAOException {
		List result = null;
		result = executeQuery(datasource, findRegistroAsistencia.toString(), new Object[]{params.get("cod_pers"), params.get("periodo"), new FechaBean((String)params.get("fechaMarcacion")).getSQLDate(), params.get("horaMarcacion"), params.get("calificacion")});
		return result;
	}
	
	/**
	 * Metodo que Actualiza registro de Asistencia, para registrar o modificar una papeleta
	 * @param params Map(String tMov,String fechaFinMov,String horaFinMov,String usuario,String codPers,String periodo,String fechaMarcacion,String horaMarcacion,String calificacion)
	 * @throws DAOException
	 */
	public boolean updateRegistroPapeleta(Map params) throws DAOException {
		
		int result = 0;
		log.error("***ingreso a updateRegistroPapeleta(Map params): "+params);//ICR 18052015
		try {//ICR 18052015
			//mov = ?, autor_id = ?, jefe_autor = ?, estado_id = ?, obs_papeleta = ?, fmod = ?, cuser_mod = ? where cod_pers = ? And periodo = ? And fing = ? And hing = ? 
			result = executeUpdate(datasource, updateRegistroPapeleta.toString(), new Object[]{params.get("mov"), params.get("autor_id"), params.get("jefe_autor"), params.get("estado_id"),
				 params.get("obs_papeleta"), params.get("fmod"), params.get("cuser_mod"), params.get("cod_pers"), params.get("periodo"), params.get("fing"), params.get("hing")});

		} catch (Exception e) {//ICR 18052015
			log.error("*** SQL Error updateRegistroPapeleta(Map params)****",e);
		}//ICR 18052015	
		return (result>0);
	}
		
	/**
	 * Metodo que se encarga de buscar los datos del registro de asistencia y de
	 * personal filtrados por registro de personal, unidad organizacional 
	 * fecha de ingreso y hora de ingreso de asistencia.
	 * @param params Map(String codPers, String periodo, String u_organ,String fIng, String hIng)
	 * @throws DAOException
	 */
	public Map findByKeyAsistencia(Map params) throws DAOException {
		
		Map resultado = executeQueryUniqueResult(datasource, findByKeyAsistencia.toString(), new Object[]{params.get("cod_pers"), params.get("periodo"), params.get("u_organ"),  
			new FechaBean((String)params.get("fIng")).getSQLDate(), params.get("hIng")});
		
		return resultado;
	}	
	
	/**
	 * Metodo que Actualiza registro de Asistencia, para eliminar un papeleta
	 * @param params Map(String tMov,String fechaFinMov,String horaFinMov,String usuario,String codPers,String periodo,String fechaMarcacion,String horaMarcacion,String calificacion)
	 * @throws DAOException
	 */
	public boolean updateEliminarPapeleta(Map params) throws DAOException {
		
		int result = 0;
		//mov = ?, autor_id = ?, jefe_autor = ?, estado_id = ?, obs_papeleta = ?, fmod = ?, cuser_mod = ? where cod_pers = ? And periodo = ? And fing = ? And hing = ? 
		result = executeUpdate(datasource, updateEliminarPapeleta.toString(), new Object[]{params.get("mov"), params.get("autor_id"), params.get("jefe_autor"), params.get("estado_id"),
			 params.get("obs_papeleta"), params.get("fmod"), params.get("cuser_mod"), params.get("cod_pers"), params.get("periodo"), new FechaBean((String)params.get("fing")).getSQLDate(), params.get("hing")});


		return (result>0);
	}
	//
	
	/**
	 * Metodo que Actualiza registro de Asistencia
	 * @param params Map(String tMov,String fechaFinMov,String horaFinMov,String usuario,String codPers,String periodo,String fechaMarcacion,String horaMarcacion,String calificacion)
	 * @throws DAOException
	 */
	public boolean updateAsistenciaFechaAutor(Map params) throws DAOException {
		
		int result = 0;
		//mov = ?, autor_id = ?, jefe_autor = ?, estado_id = ?, obs_papeleta = ?, fmod = ?, cuser_mod = ? where cod_pers = ? And periodo = ? And fing = ? And hing = ? 
		result = executeUpdate(datasource, updateAsistenciaFechaAutor.toString(), new Object[]{params.get("mov"), params.get("autor_id"), params.get("jefe_autor"), params.get("fecha_autor"), params.get("estado_id"),
			 params.get("obs_papeleta"), params.get("fmod"), params.get("cuser_mod"), params.get("cod_pers"), params.get("periodo"), params.get("fing"), params.get("hing")});


		return (result>0);
	}
	
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro
	 * @param params contiene (Map columns, String anno, Short numero, String u_organ, String cod_pers, String num_seguim)
	 * @throws DAOException
	 */
	public boolean updateCustomColumns(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1271asistencia ");
		List listaVal = new ArrayList();
		Map columns = (params.get("columns") != null) ? (HashMap)params.get("columns"): new HashMap();
		
		if(columns != null && !columns.isEmpty()) {
			Iterator it = columns.entrySet().iterator();
			boolean first = true;//para ver si es el primer campo de la sentencia SQL
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				if (first) {
					strSQL.append(" set "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
					first = false;
				} else {
					strSQL.append(", "+e.getKey()+"= ? ");
					listaVal.add(e.getValue());
				}				
			}
		}
		
		strSQL.append(" WHERE cod_pers = ? And periodo = ? And fing = ? And hing = ? ");
		if(log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString()); 
		//los datos de la llave primaria
		listaVal.add(params.get("cod_pers"));
		listaVal.add(params.get("periodo"));
		listaVal.add(params.get("fing"));
		listaVal.add(params.get("hing"));
		
		int modificado = executeUpdate(datasource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}
	

	//JRR
	/**
	 * Metodo encargado de buscar los jefes que tienen papeletas pendientes 
	 * @param params Map(String periodo)
	 * @throws DAOException
	 */
	public List findJefeAutorPapeletasPendientesByPeriodo(Map params) throws DAOException {
		
		List resultado = new ArrayList();	
		
		resultado = executeQuery(datasource, findJefeAutorPapeletasPendientesByPeriodo.toString(),
				new Object[]{params.get("periodo"), constantes.leePropiedad("PAPELETA_REGISTRADA")});
		
		return resultado;
	}
	
	/**
	 * Metodo encargado de buscar las papeletas pendientes por jefe 
	 * @param params Map(String periodo,String jefe_autor)
	 * @throws DAOException
	 */
	public List findTrabPapeletasPendientesByPeriodoAndJefe(Map params, Map jefe) throws DAOException {
		
		List resultado = new ArrayList();	
		
		resultado = executeQuery(datasource, findTrabPapeletasPendientesByPeriodoAndJefe.toString(),
				new Object[]{params.get("periodo"), constantes.leePropiedad("PAPELETA_REGISTRADA"),
							jefe.get("jefe_autor").toString().trim()});
		
		return resultado;
	}
	
	
	//PRAC-ASANCHEZ - 18/08/2009 
	/**
	 * Metodo que se encarga de buscar los registros de asistencia de un
	 * trabajador en una fecha determinada y para un movimiento determinado.
	 * @throws DAOException
	 */
	public List findByCodPersMovFecha(Map params) throws DAOException {
		List detalle = null;		
		
		String cod_pers = (params.get("cod_pers") != null) ? params.get("cod_pers").toString():"";
		String mov = (params.get("mov") != null) ? params.get("mov").toString():"";
		String fecha = (params.get("fecha") != null) ? params.get("fecha").toString():"";
	
		detalle = executeQuery(datasource, FIND_BY_COD_PERS_MOV_FECHA.toString(), new Object[]{cod_pers, mov, fecha});
		
		return detalle;
	}
	//

	//JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES
	/**
	 * Metodo que se encarga de buscar el codigo unico de los trabajadores que tienen 
	 * movimientos de asistencia inconsistentes
	 * @param Map datos
	 * @return List
	 * @throws DAOException
	 */
	public List findTrabMovimientoAsistencia(Map hm) throws DAOException {
		List resultado = new ArrayList();		
		
		if (log.isDebugEnabled()) log.debug("hoy: " + hm.get("hoy"));
		if (log.isDebugEnabled()) log.debug("fanterior: " + hm.get("fanterior"));		
		Date fhoy = new Date();
		Date fante = new Date();
		fhoy = new FechaBean((String)hm.get("hoy")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("fhoy: " + fhoy);
		fante = new FechaBean((String)hm.get("fanterior")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("fante: " + fante);
		
		resultado = executeQuery(datasource, findTrabMovimientoAsistencia.toString(),
				new Object[]{constantes.leePropiedad("ESTADO_PRECALIFICADO"), fante, fhoy});
		
		return resultado;
	}//FIN - JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES
		
	//JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES	
	/**
	 * Metodo que se encarga de buscar los movimientos de asistencia inconsistentes por trabajador 
	 * @param params Map(String periodo, String cuser_dest)
	 * @throws DAOException
	 */
	public List findMovimientoAsistenciaByTrab(Map hm) throws DAOException {
		
		List resultado = new ArrayList();
		
		if (log.isDebugEnabled()) log.debug("hoy: " + hm.get("hoy"));
		if (log.isDebugEnabled()) log.debug("fanterior: " + hm.get("fanterior"));		
		Date fhoy = new Date();
		Date fante = new Date();
		fhoy = new FechaBean((String)hm.get("hoy")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("fhoy: " + fhoy);
		fante = new FechaBean((String)hm.get("fanterior")).getSQLDate();
		if (log.isDebugEnabled()) log.debug("fante: " + fante);
	
		resultado = executeQuery(datasource, findMovimientoAsistenciaByTrab.toString(),
				new Object[]{hm.get("cod_pers").toString().trim(),
							constantes.leePropiedad("ESTADO_MOV_ASISTENCIA"), fante, fhoy});				
		
		return resultado;
	}//FIN - JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	/**
	   * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero		 
	   * @param String unidad
	   * @return String uoJefe
	   * @throws SQLException
	   */
	  public String findUuooJefe(String unidad) throws DAOException {
    
	    String uoJefe = "";	
	    try {

	    	log.debug("findUuooJefe-unidadd: "+unidad); 
			uoJefe= unidad!=null? unidad.trim(): "";
        	if (!"".equals(uoJefe)){
        		log.debug("entro if");
        		int nroCar = uoJefe.length();
            	log.debug("nroCar: "+nroCar);
            	char v= '9';  //solo 1 caracter almacena              	
            	for (int p = nroCar-1; p >= 0; p--) {
            		log.debug("entro for");
            		log.debug("uoJefe: "+uoJefe);
            		log.debug("v: "+v);
            		log.debug("p: "+p);
            		v= uoJefe.charAt(p);
            		if (p!=0){
            			log.debug("entro p!=0");
                		if ('0'==v){
                			uoJefe=uoJefe.substring(0, p);
                			log.debug("uoJefe2: "+uoJefe);
                		}else{                			
                			break;
                		} 
            		}	
            	}            		
        	}
        	log.debug("findUuooJefe-uoJefee: "+uoJefe);
	    }
	    catch (Exception e) {
	    	log.error("*** SQL Error ****",e);
			MensajeBean msg = new MensajeBean();
			msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
	    }
	    finally {
	    }
	    log.debug("findUuooJefe-uoJefee(final): "+uoJefe);
		return uoJefe; 
	  }	  
	  //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	
	  
	/**
	 * PAS20171U230200001 - solicitud de reintegro  
	 * Funcion para obtener papeletas para interface de documentos aprobados en reintegros
	 * @param params
	 * @return
	 * @throws DAOException
	 */
	public List findDocAprobadoPapeletas(Map params) throws DAOException {
		List detalle = null;
		java.sql.Date ffinicio = new BeanFechaHora(params.get("ffinicio").toString()).getSQLDate();
		java.sql.Date ffin = new BeanFechaHora(params.get("ffin").toString()).getSQLDate();
		Object obj[] = { (String)params.get("cod_pers"),  ffinicio  ,ffin};  //
		detalle = executeQuery(datasource, FIND_DOC_APROB_PAPELETA.toString(), obj);
		return detalle;
	}

}
