package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
//import pe.gob.sunat.utils.Constantes;
//import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.utils.Utiles;

/** 
 * 
 * Clase       : T1282DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1282vacaciones_d 
 * Proyecto    : ASISTENCIA 
 * Autor       : PRAC-JCALLO
 * Fecha       : 12-MAR-2007 
 * 
 * */

public class T1282DAO extends DAOAbstract {
	
	private DataSource dataSource = null;
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	private final StringBuffer findAllColumnsByKey = new StringBuffer("SELECT cod_pers, periodo, nvl(licencia,'') as licencia, ffinicio, nvl(anno_vac,'') as anno_vac, ffin, ")
	.append(" dias, nvl(anno,'') as anno, nvl(u_organ,'') as u_organ, nvl(anno_ref,'') as anno_ref, nvl(area_ref,'') AS area_ref, ")
	.append(" numero_ref, nvl(observ,'') as observ, nvl(est_id,'') as est_id, fcreacion, nvl(cuser_crea,'') as cuser_crea, fmod, nvl(cuser_mod,'') as cuser_mod ")
	.append(" FROM t1282vacaciones_d WHERE cod_pers = ? and periodo = ? and licencia = ? and ffinicio = ?");
	
	/* JROJASR - Modificado por Fusión - 06/08/2010 */
	private final StringBuffer INSERTAR_VACACIONES_DETALLE =  new StringBuffer("INSERT INTO t1282vacaciones_d (cod_pers, periodo, licencia, ffinicio, anno_vac, ffin, ")
	.append(" dias, anno, u_organ, anno_ref, area_ref, numero_ref, observ, est_id, fcreacion, cuser_crea, ind_conv) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
	
	private final StringBuffer deleteByPrimaryKey = new StringBuffer("DELETE from t1282vacaciones_d WHERE cod_pers = ? and periodo = ? and licencia = ? and ffinicio = ?");
	//dtarazona editado 16/04/2018 para solucionar el bug
	private final StringBuffer updateByPrimaryKeyAndDias = new StringBuffer("UPDATE t1282vacaciones_d SET est_id='0', fmod=current, cuser_mod=? WHERE cod_pers = ? and licencia = ? and ffinicio = ? and dias=?");
	//fin  dtarazona
	
	private final StringBuffer joinT02perdp = new StringBuffer("SELECT  v.cod_pers, v.periodo, v.ffinicio, v.ffin, v.anno_vac, v.dias, v.u_organ, ")
	.append(" v.observ, v.periodo, v.licencia, v.fcreacion, v.cuser_crea, (select descrip from t1279tipo_mov where tipo_id = '02' and mov = v.licencia) as tipo, ")
	.append(" v.est_id, v.anno_ref, v.area_ref, v.numero_ref, v.cuser_mod from  t1282vacaciones_d v, t02perdp p where v.cod_pers = ? ");
	
	private final StringBuffer actualizaProgVentaFinal = new StringBuffer("UPDATE t1282vacaciones_d ")
	.append(" SET est_id = ?, anno_ref = ?, area_ref = ?, numero_ref = ?, fmod = ?, cuser_mod = ? ")
	.append(" where  cod_pers = ? and anno_vac = ? and licencia = ? and est_id = ? and numero_ref = ? ");	
	
	private final StringBuffer actualizaProg1 = new StringBuffer("UPDATE t1282vacaciones_d ")
	.append(" SET est_id = ?, anno_ref = ?, area_ref = ?, numero_ref = ?, fmod = ?, cuser_mod = ? ")
	.append(" where  cod_pers = ? and ffinicio = ? 	and anno_vac = ? and licencia = ? and est_id = ? and dias=? ");
	
	private final StringBuffer actualizaProg = new StringBuffer("UPDATE t1282vacaciones_d ")
	.append(" SET est_id = ?, anno_ref = ?, area_ref = ?, numero_ref = ?, fmod = ?, cuser_mod = ? ")
	.append(" where  cod_pers = ? and ffinicio = ? 	and anno_vac = ? and licencia = ? and est_id = ?");

	//JRR - 03/12/2008 - Cierre Asistencia
	private final StringBuffer findDiasCompensacionVacacional = new StringBuffer("SELECT cod_pers, sum(dias) as total, periodo ")
	.append(" FROM t1282vacaciones_d ")
	.append(" WHERE licencia = '46' AND PERIODO[5,6] = MONTH(TODAY) ")
	.append(" AND PERIODO[1,4] = YEAR(TODAY) ")
	.append(" AND numero_ref > 0 ")
	.append(" GROUP BY cod_pers, periodo");
	
	//JRR - 21/04/2009 - Vacaciones Vencidas
	private final StringBuffer findProgVacVencidas = new StringBuffer("SELECT cod_pers ")
	.append(" FROM t1282vacaciones_d ")
	.append(" WHERE cod_pers = ? and anno_vac = ? and licencia = '49' ")
	.append(" and observ like 'ART 23 DL 713%' and est_id = '5' ");

	//JRR - 23/04/2009 - Fecha pertenece a Vacaciones Vencidas
	private final StringBuffer findByCodPersFIniFFinVacVencidas = new StringBuffer("SELECT cod_pers ")
	.append(" FROM t1282vacaciones_d ")
	.append(" WHERE cod_pers = ? AND licencia = ? ")
	//.append(" AND ffin is not null AND ffinicio != ffin ")
	//JRR - 23/04/2009
	.append(" AND ffin is not null AND observ not like 'Imdemnizacion por Vacacion%' ")
	.append(" AND ffinicio <= ? and ffin >= ? ");

	
	/* FUSION PROGRAMACION - JROJAS4 */
	private final StringBuffer FINDBYCODPERS = new StringBuffer("SELECT  ")
	.append("v.cod_pers, v.periodo, v.ffinicio, v.ffin, v.anno_vac, v.dias,")
	.append("v.est_id, v.anno_ref, v.area_ref, v.numero_ref, '1' as bd, '1' as visible, v.ind_conv, v.observ as sustento ")
	.append("from  t1282vacaciones_d v where v.cod_pers = ? ")
	.append("and v.anno_vac=? and v.licencia=? and v.est_id<>?");

	private final StringBuffer DELETEBYPRIMARYKEYSINPERIODO = new StringBuffer("DELETE FROM t1282vacaciones_d WHERE cod_pers = ? and licencia = ? and ffinicio = ?");
	
	private final StringBuffer DELETEBYLICENCIABYNUMEROREFBYINDMATRI = new StringBuffer("DELETE FROM t1282vacaciones_d WHERE cod_pers = ? and ((licencia= ? and est_id=?) or (licencia=? and est_id=?)) and anno_ref=?  and numero_ref=? and ind_matri='1' ");//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	
	private final StringBuffer UPDATEESTADOBYTIPO49BYNUMEROREFBYINDMATRI = new StringBuffer("UPDATE t1282vacaciones_d SET est_id=?,cuser_mod=?,fmod=?,anno_ref=null,area_ref=null,numero_ref=null,ind_matri=null WHERE cod_pers = ? and licencia=? and est_id=? and anno_ref=?  and numero_ref=? and ind_matri='1' ");//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta
	
	private final StringBuffer UPDATEINDMATRIBYTIPO49BYNUMEROREFBYINDMATRI = new StringBuffer("UPDATE t1282vacaciones_d SET cuser_mod=?,fmod=?,ind_matri=? WHERE cod_pers = ? and licencia=? and est_id=? and anno_ref=?  and numero_ref=? and ind_matri='1' ");//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta
	
	private final StringBuffer UPDATEINDMATRIBYTIPO49BYNUMEROREFBYINDMATRI_LICENCIA = new StringBuffer("UPDATE t1282vacaciones_d SET cuser_mod=?,fmod=?,ind_matri=? WHERE cod_pers = ? and licencia=? and est_id=? and anno_ref is null  and numero_ref is null and ind_matri='1' ");//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta
	
	private final StringBuffer FINDVACFISBYCODPERS = new StringBuffer("SELECT ")
	.append("P.t02cod_pers,V.licencia,S.u_organ, V.dias,")
	.append("S.anno,S.numero, S.asunto,S.observ,S.fecha,")
	.append("P.t02ap_pate, P.t02ap_mate, P.t02nombres,V.ffinicio,")
	.append("V.ffin,'1' as bd,'1' as visible ")
	.append("FROM T1282VACACIONES_D V, T02PERDP P, T1277SOLICITUD S ")
	.append("WHERE V.cod_pers = P.t02cod_pers AND V.cod_pers=S.cod_pers ")
	.append("AND V.ffinicio=S.ffinicio and V.licencia=S.licencia " )
	.append("AND V.anno_vac=S.anno_vac AND V.numero_ref=S.numero ")
	.append("AND V.cod_pers=? AND V.est_id=? ")
	.append("AND V.anno_vac=? AND V.licencia=? AND S.est_id=? ");
	
	private final StringBuffer SELECTBYPRIMARYKEYSINPERIODO = new StringBuffer("SELECT COUNT(*) as ccantidad FROM t1282vacaciones_d WHERE cod_pers = ? and licencia = ? and ffinicio = ?");
	
	private final StringBuffer FINDBY_CODPERS_NUMEROREF_LICENCIA_EST_ID =new 
	StringBuffer("select cod_pers,periodo,ffinicio,ffin,dias " )
	.append("from t1282vacaciones_d ") 
	.append("where cod_pers=? and licencia=? and " )
	.append("est_id=? and anno_vac=? and numero_ref=?");
	/*            */
	
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	private final StringBuffer FINDBY_CODPERS_ANNONUMEROREF_LICENCIA_ESTID_INDMATRI =new 
	StringBuffer("select first 1 cod_pers,anno_vac,ffinicio,ffin,dias,licencia " )
	.append("from t1282vacaciones_d ") 
	.append("where cod_pers=? and licencia=? and " )
	.append("est_id=? and anno_ref=? and numero_ref=? and ind_matri='1' ");
	
	
	private final StringBuffer FINDBY_CODPERS_ANNOVAC_LICENCIA_ESTID =new 
	StringBuffer("select cod_pers,anno_vac,ffinicio,ffin,dias,licencia " )
	.append("from t1282vacaciones_d ") 
	.append("where cod_pers=? and licencia=? and " )
	.append("est_id=? and anno_vac=? and ind_matri is null ");
	//FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 	
	
	/* JRR - PROGRAMACION - 18/05/2010 */	
	private final StringBuffer QUERY3_SENTENCE = new StringBuffer(" Select sum(dias) as sumvacefeccomp ")
	  .append(" from  t1282vacaciones_d where cod_pers = ? and anno_vac = ? ")
	  .append(" and (licencia = ? or licencia = ?) and est_id = ? ");

	/* JRR - PROGRAMACION - 04/06/2010 */	
	private final StringBuffer QUERY4_SENTENCE = new StringBuffer(" Select sum(dias) as sumvactotven ")
	  .append(" from  t1282vacaciones_d where cod_pers = ? and anno_vac = ? ")
	  .append(" and licencia = ? and est_id = ? ");
	/*           */

	/* JRR - AUTORIZACION BATCH - 08/03/2011 */	
	private final StringBuffer FINDBYVACACIONES = new StringBuffer("SELECT ")
	.append(" cod_pers FROM t1282vacaciones_d WHERE licencia = ? ")
	.append(" AND ? Between ffinicio and ffin  ");
	
	private final StringBuffer FINDBYFINVACACIONES = new StringBuffer("SELECT")
	.append(" cod_pers FROM t1282vacaciones_d WHERE licencia = ?")
	.append(" AND ffin BETWEEN ? AND ? ");
	/*          */
	
	/* ICAPUNAY - TRABAJADORES CON VACACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	
	private final StringBuffer FIND_VACACIONES_PROGRAMADAS_ACEPTADAS = new StringBuffer(" select v.* from t1282vacaciones_d v, t02perdp p ")
	  .append(" where v.licencia=? and v.est_id=? ")
	  //.append(" and (DATE(v.ffinicio)-TODAY)=? ")
	  .append(" and ((DATE(v.ffinicio)-TODAY)=? or (DATE(v.ffinicio)-TODAY)=? ")
	  .append(" or (DATE(v.ffinicio)-TODAY)=? or (DATE(v.ffinicio)-TODAY)=? or (DATE(v.ffinicio)-TODAY)=?) ")
	  .append(" and v.cod_pers=p.t02cod_pers ")
	  .append(" and p.t02cod_stat=? ")
	  .append(" and p.t02cod_rel not in (?) ") 
	  .append(" order by substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) asc, p.t02cod_pers asc ");
	/* FIN ICAPUNAY - TRABAJADORES CON VACACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */
		
	/* ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
	private final StringBuffer FINDBY_VACACIONESPROGRAMADAS = new StringBuffer("SELECT ")
	.append(" cod_pers FROM t1282vacaciones_d , t02perdp ")
	.append(" WHERE cod_pers=t02cod_pers AND t02cod_stat=? ")
	.append(" AND licencia=? AND est_id=? ")
	.append(" AND ? BETWEEN ffinicio AND ffin ");	
	
	/* FIN ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
	
	private String findProgramacionesByParams = 
		"SELECT "+ 
		"    vac.cod_pers, "+
		"    vac.licencia, "+
		"    vac.ffinicio, "+
		"	 vac.ffin, "+
		"	 vac.dias, " +
		"    vac.anno_vac, "+
		"    vac.anno_ref, "+
		"    vac.area_ref, "+
		"    vac.numero_ref, "+
		"    vac.est_id estado_id, " +
		"	 trim(nvl(pe.t02ap_pate,''))||' '||trim(nvl(pe.t02ap_mate,''))||', '||trim(nvl(pe.t02nombres,''))  as trabajador, " +
		"	 vac.fcreacion, "+
		"	 vac.fmod, "+
		"	 vac.cuser_crea, "+
		"	 vac.cuser_mod, "+
		"	 vac.observ, "+
		"	 vac.periodo "+
		"FROM  "+
		"    t1282vacaciones_d vac ,  "+
		"    t02perdp pe "+
		"WHERE  "+
		"    vac.cod_pers = pe.t02cod_pers  "+
		"    AND pe.t02cod_stat = ? "+
		"    AND vac.licencia = ?  "+
		"    AND vac.est_id = ?  ";
		//"    AND vac.cod_pers = ? ";
	
	
	private String updateProgramacionByMatrimonio =	
		"UPDATE "+ 
		"    t1282vacaciones_d "+
		"SET "+
		"    est_id = ?, "+
		"    anno_ref = ?, "+
		"    area_ref = ?, "+
		"    numero_ref = ?, "+
		"    fmod = ?, "+
		"    cuser_mod = ?, "+
		"    ind_matri = ? "+
		"WHERE "+
		"    cod_pers = ? "+
		"    AND ffinicio = ? "+
		"    AND anno_vac = ? "+
		"    AND licencia = ? "+
		"    AND est_id = ? ";
	
	private String insertVacacionesDetByMatrimonio = 
		"INSERT INTO t1282vacaciones_d ( "+
		"	cod_pers,   periodo,    licencia,   ffinicio, "+
		"	anno_vac,   ffin,       dias,       anno, "+
		"	u_organ,    anno_ref,   area_ref,   numero_ref, "+
		"	observ,     est_id,     fcreacion,  cuser_crea, "+ 
		"	ind_conv,   ind_matri) "+ 
		"VALUES "+ 
		"	(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "; 
	
	private String deleteByPK = 
		"DELETE FROM "+
		"    t1282vacaciones_d "+
		"WHERE  "+
		"    cod_pers = ? "+
		"    AND periodo = ? "+
		"    AND licencia = ? "+
		"    AND ffinicio = ? ";
	
	
	  //AGONZALESF -PAS20171U230200001 - solicitud de reintegro   Vacaciones para ver documentos aprobados en reintegro
	  //AGONZALESF- PAS20191U230200011 - solicitud de reintegro 
	  private static final StringBuffer FIND_DOC_APROB_VACACIONES = new StringBuffer("")
	  .append(" select m.descrip as tipo,")
	  .append(" date(v.ffinicio) as fecha_inicio,")
	  .append(" date(v.ffin) as fecha_fin,")
	  .append(" v.dias as dias,") 
	  .append(" v.observ as motivo")
	  	.append(" from t1282vacaciones_d v,t1279tipo_mov m") 
	  	.append(" where v.licencia=m.mov")
	  	.append(" and v.est_id='1'")
	  .append(" and v.cod_pers=?") 
	  	.append(" and v.licencia  in (SELECT  t99descrip from t99codigos   WHERE t99cod_tab='R07' and t99tip_desc ='D' and t99tipo='VACAC' and t99estado ='1' )")
		.append(" and")
		.append(" (")
		.append(" v.ffinicio between  ?  and  ?")
		.append(" or v.ffin between ? and ?")
		.append(" or  ? between v.ffinicio and v.ffin")
		.append(" or  ? between v.ffinicio and v.ffin")
		.append(")"); 
	  //
	  
	  //ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
		private final StringBuffer FIND_VACACIONES_PROGRAMADAS_BYANIOMES = new StringBuffer("select d.cod_pers as registro,(trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as apenom, ")
		  //.append("p.t02f_ingsun as fechaingreso,(u.t12cod_uorga||' - '||u.t12des_corta) as desuo, ")
		  .append("p.t02f_ingsun as fechaingreso,(u.t12cod_uorga||' - '||u.t12des_corta) as desuo,u.t12cod_uorga as coduo, ")
		  .append("d.anno_vac as periodo, date(d.ffinicio) as fechainicio,date(d.ffin) as fechafin ")
		  .append("from t1282vacaciones_d d, t02perdp p, t12uorga u ")
		  .append("where d.cod_pers=p.t02cod_pers and p.t02cod_stat=? ")
		  .append("and ((YEAR(d.ffinicio)=? and MONTH(d.ffinicio)=?) or (YEAR(d.ffin)=? and MONTH(d.ffin)=?)) ")
		  .append("and d.licencia=? and d.est_id=? ")
		  .append("and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=u.t12cod_uorga ")
		  .append("and u.t12cod_uorga like ? ")
		  .append("and p.t02cod_rel not in (?) ")
		  .append("order by u.t12cod_uorga asc, d.cod_pers asc, d.ffinicio asc ");
		
		private final StringBuffer FIND_COLABORADORES_VACACIONES_PROGRAMADAS_BYANIOMES = 
		new StringBuffer("select distinct d.cod_pers ")
		  .append("from t1282vacaciones_d d, t02perdp p, t12uorga u ")
		  .append("where d.cod_pers=p.t02cod_pers and p.t02cod_stat=? ")
		  .append("and ((YEAR(d.ffinicio)=? and MONTH(d.ffinicio)=?) or (YEAR(d.ffin)=? and MONTH(d.ffin)=?)) ")
		  .append("and d.licencia=? and d.est_id=? ")
		  .append("and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=u.t12cod_uorga ")
		  .append("and u.t12cod_uorga like ? ")
		  .append("order by d.cod_pers asc ");
		
		private final StringBuffer FIND_COLAB_VACACIONES_PROGRAMADAS_BYANIOMESUNIDAD =
		new StringBuffer("select distinct d.cod_pers ")
		  .append("from t1282vacaciones_d d, t02perdp p, t12uorga u ")
		  .append("where d.cod_pers=p.t02cod_pers and p.t02cod_stat=? ")
		  .append("and ((YEAR(d.ffinicio)=? and MONTH(d.ffinicio)=?) or (YEAR(d.ffin)=? and MONTH(d.ffin)=?)) ")
		  .append("and d.licencia=? and d.est_id=? ")
		  .append("and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=u.t12cod_uorga ")
		  .append("and u.t12cod_uorga=? ")
		  .append("order by d.cod_pers asc ");
		
		  private static final StringBuffer FINDPERSFALTAPROGVAC = new 
		  StringBuffer("SELECT substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) as coduo,'' as anno_vac,P.t02cod_pers,'' as licencia, '' as est_id ")
			.append("FROM  t02perdp P ")
			.append("WHERE P.t02cod_stat=? ")
			.append("and P.t02f_ingsun<=? ")
			.append("and substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) matches ? ")
			.append("and not exists (select de.cod_pers from t1282vacaciones_d de where P.t02cod_pers= de.cod_pers) ")	
		    .append("UNION ")
		    .append("SELECT substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) as coduo,V.anno_vac,P.t02cod_pers,V.licencia, V.est_id ")
		    .append("FROM  t02perdp P, t1282vacaciones_d V ")
		    .append("where p.t02cod_pers=V.cod_pers ")
		    .append("and P.t02cod_stat=? ")
		    .append("and P.t02f_ingsun<=? ")
		    .append("and substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) matches ? ")
		    .append("and V.licencia=? and V.est_id=? and CAST(V.anno_vac AS INT)>=YEAR(CURRENT)-1 ") 
		    .append("order by coduo,t02cod_pers,anno_vac ");	
		  
		  
		  private static final StringBuffer FINDVACACIONES_BYCODPERSANIO = new 
		  StringBuffer("select * from t1282vacaciones_d ")
			.append("where cod_pers=? and anno_vac=? ")
			.append("and licencia=? and est_id<>? ");		
	   //FIN  
		  
		//  private static final StringBuffer FINDSUMAVENTAVAC= new StringBuffer("select sum(v.dias) as diasventa from t1282vacaciones_d v ")//,t1277solicitud s 
		//  .append(" where v.cod_pers=? and v.anno_vac=? and (v.licencia='49' and v.est_id='4') ");
		 
		   private static final StringBuffer FINDTOTALFRACCIONADO= new StringBuffer("select SUM(v.DIAS) as sumfraccionados from t1282vacaciones_d v ")//,t1277solicitud s 
			  .append(" where v.cod_pers=? and v.anno_vac=? and v.dias<7 and v.est_id=? ");
		   
		   //
		   private static final StringBuffer FINDTOTALVENTASENSOL= new StringBuffer("select SUM(v.dias) as diasventasol from t1282vacaciones_d v, t1277solicitud s ")//,t1277solicitud s 
			  .append(" where v.cod_pers=? and v.anno_vac=? and v.licencia='49' and v.est_id='5' and s.numero=v.numero_ref and s.licencia='46'");
		   
		   private static final StringBuffer FINDTOTALREPROGENSOL= new StringBuffer("select SUM(v.DIAS) as sumfracsol from t1282vacaciones_d v inner join t1277solicitud s on s.numero=v.numero_ref and v.est_id='5' and  s.licencia='54' ")//,t1277solicitud s 
			  .append(" where v.cod_pers=? AND v.anno_vac=?");
	/**
	 *@param datasource Object
	 *
	 * */
	public T1282DAO(Object datasource) {
		if (datasource instanceof DataSource)
	      this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.dataSource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	  * Metodo que obtiene los datos a partir de la llave 
	  * @param datos (String cod_pers, String periodo, String licencia , TimeStamp ffinicio)
	  * @return
	  * @throws DAOException
	  */
	public Map findAllColumnsByKey(Map datos) throws DAOException {
		
		Map soli = executeQueryUniqueResult(dataSource, findAllColumnsByKey.toString(), new Object[]{ 
					datos.get("cod_pers"), datos.get("periodo"), datos.get("licencia"),datos.get("ffinicio")} );
		
		return soli;
	}
	//dtarazona
	/**
	 * Metodo encargado de insertar un registro en la tabla t1274licen_med
	 * @param params ( cod_pers, periodo, licencia, ffinicio, anno_vac, ffin, dias, anno, u_organ, anno_ref, area_ref, numero_ref, observ, est_id, fcreacion, cuser_crea)
	 * @throws DAOException
	 */
	public boolean updateVacacionExistente(Map params) throws DAOException {
		String sql = "update t1282vacaciones_d set ";
		
		if(params.get("editAnnoVac")!=null && !params.get("editAnnoVac").toString().trim().equals("")){
			sql=sql+" anno_vac='"+params.get("editAnnoVac").toString().trim()+"', ";
		}
				
			sql=sql+ " ffin=?, dias=?,est_id='1',cuser_mod=? where cod_pers=? and periodo=? and licencia=? and ffinicio=?";	
		log.debug("Consulta 1282:"+sql);	
		int modificado;
		
		List lstParams = new ArrayList();
		
		lstParams.add(params.get("ffin"));
		lstParams.add(params.get("dias"));
		lstParams.add(params.get("cuser_mod"));
		lstParams.add(params.get("cod_pers"));
		lstParams.add(params.get("periodo"));
		lstParams.add(params.get("licencia"));
		lstParams.add(params.get("ffinicio"));		
		
		modificado = executeUpdate(dataSource, sql, lstParams.toArray());
		
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de insertar un registro en la tabla t1274licen_med
	 * @param params ( cod_pers, periodo, licencia, ffinicio, anno_vac, ffin, dias, anno, u_organ, anno_ref, area_ref, numero_ref, observ, est_id, fcreacion, cuser_crea)
	 * @throws DAOException
	 */
	public boolean insertarVacacionesDetalle(Map params) throws DAOException {
		String indMatrimonio = (String)params.get("ind_matri");
		boolean isMatrimonio = indMatrimonio!=null && indMatrimonio.equals(constantes.leePropiedad("ACTIVO"));
		int modificado;
		
		List lstParams = new ArrayList();
		lstParams.add(params.get("cod_pers"));
		lstParams.add(params.get("periodo"));
		lstParams.add(params.get("licencia"));
		lstParams.add(params.get("ffinicio"));
		lstParams.add(params.get("anno_vac"));
		lstParams.add(params.get("ffin"));
		lstParams.add(params.get("dias"));
		lstParams.add(params.get("anno"));
		lstParams.add(params.get("u_organ"));
		lstParams.add(params.get("anno_ref"));
		lstParams.add(params.get("area_ref"));
		lstParams.add(params.get("numero_ref"));
		lstParams.add(params.get("sustento"));
		lstParams.add(params.get("est_id"));
		lstParams.add(params.get("fcreacion"));
		lstParams.add(params.get("cuser_crea"));
		lstParams.add(params.get("ind_conv"));
		log.debug("sustentoF:"+params.toString());
		log.debug("Convenio Inidice:"+params.get("ind_conv"));
		if(isMatrimonio)
			lstParams.add(indMatrimonio);
		
		if(isMatrimonio)
			modificado = executeUpdate(dataSource, insertVacacionesDetByMatrimonio, lstParams.toArray());
		else
			modificado = executeUpdate(dataSource, INSERTAR_VACACIONES_DETALLE.toString(), lstParams.toArray());
		
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro
	 * @param params contiene (Map columns, String anno, Short numero, String u_organ, String cod_pers, String num_seguim)
	 * @throws DAOException
	 */
	public boolean updateCustomColumns(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1282vacaciones_d ");
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
		
		strSQL.append(" WHERE cod_pers = ? and periodo = ? and licencia = ? and ffinicio = ?");
		if(log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString()); 
		//los datos de la llave primaria
		listaVal.add(params.get("cod_pers"));
		listaVal.add(params.get("periodo"));
		listaVal.add(params.get("licencia"));
		listaVal.add(params.get("ffinicio"));
		
		int modificado = executeUpdate(dataSource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}
	
	/**
	 * Metodo encargado de verificar si un trabajador tiene un registro de
	 * vacacion efectiva o especial cuyas fechas sean abarcadas por los
	 * parametros indicados.
	 * @param params (cod_pers, fechaIni, fechaFin)
	 * @throws DAOException
	 */
	public boolean findByCodPersFIniFFin(Map params) throws DAOException {

		StringBuffer strSQL = new StringBuffer("");
		boolean tiene = false;
		
		String fechaIni = (params.get("fechaIni") != null)? params.get("fechaIni").toString():"";
		String fechaFin = (params.get("fechaFin") != null)? params.get("fechaFin").toString():"";
		log.debug("fechaIni:" +fechaIni+"|");
		log.debug("fechaFin:" +fechaFin+"|");

		FechaBean fecha1 = new FechaBean(fechaIni);
		String fecCompara1 = fecha1.getFormatDate("yyyy/MM/dd");
//		String fecCompara1 = Utiles.toYYYYMMDD(fechaIni);
		log.debug("fecCompara1:" +fecCompara1+"|");
		
		FechaBean fecha2 = null;
		String fecCompara2 = "";
		//if (fechaFin.trim() != "") {
		if (!fechaFin.trim().equals("")) {
			fecha2 = new FechaBean(fechaFin);
			//fecCompara2 = Utiles.toYYYYMMDD(fechaFin);
			fecCompara2 = fecha2.getFormatDate("yyyy/MM/dd");
			log.debug("fecCompara2:" +fecCompara2+"|");
		}

		strSQL.append("select cod_pers from t1282Vacaciones_d "
				).append( " where cod_pers = ? and (licencia = ? or licencia = ?) ");

		if (!fechaFin.equals("")) {

			if (!fechaIni.equals(fechaFin)) {
				strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2
						).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2
						).append( "') and ffinicio <= DATE('" ).append( fecCompara2
						).append( "'))) ");
			} else {
				
				strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
			}

		} else {
			
			strSQL.append(" and (ffinicio <=DATE('" ).append( fecCompara1
					).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
		}

		List listaResult = executeQuery(dataSource, strSQL.toString(), new Object[]{params.get("cod_pers"), constantes.leePropiedad("VACACION"), constantes.leePropiedad("VACACION_ESPECIAL")});
		if(listaResult !=null && !listaResult.isEmpty()&& listaResult.size()>0){
			tiene = true;
		}
 
		return tiene;
	}
	
	/**
	 * Metodo encargado de verificar si un trabajador tiene un registro de
	 * vacacion efectiva o especial cuyas fechas sean abarcadas por los
	 * parametros indicados.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha1
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fecha2
	 *            String. Fecha superior del rango de busqueda.
	 * @return Booelan. Devuelve "verdadero" si existe por lo menos un registro
	 *         que cumpla con las condiciones.
	 *@param String cod_pers, String fecha1, String fecha2, prog(si, no), String fechaIni         
	 * @throws DAOException
	 */
	public boolean findByCodPersFIniFFinProg(Map params) throws DAOException {

		StringBuffer strSQL = new StringBuffer("");		
		boolean tiene = false;
		List valores = new ArrayList();
		
		String fecha1 = (params.get("fecha1") != null ) ? params.get("fecha1").toString():"";
		String fecha2 = (params.get("fecha2") != null ) ? params.get("fecha2").toString():"";
		String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():"";
		String prog = (params.get("prog") != null ) ? params.get("prog").toString():"no";

		FechaBean fb_fecha1 = new FechaBean(fecha1);
		String fecCompara1 = fb_fecha1.getFormatDate("yyyy/MM/dd");
		
		//String fecCompara1 = Utiles.toYYYYMMDD(fecha1);
		FechaBean fb_fecha2 = null;
		String fecCompara2 = "";
		
		FechaBean fb_fecIni = null;
		String fecIni = "";
		
		if (fecha2.trim() != "") {
			//fecCompara2 = Utiles.toYYYYMMDD(fecha2);
			fb_fecha2 = new FechaBean(fecha2);
			fecCompara2 = fb_fecha2.getFormatDate("yyyy/MM/dd");
		}
		if (fechaIni.trim() != "") {
			//fecIni = Utiles.toYYYYMMDD(fechaIni);
			fb_fecIni = new FechaBean(fechaIni);
			fecIni = fb_fecIni.getFormatDate("yyyy/MM/dd");
		}

		strSQL.append("select cod_pers from t1282Vacaciones_d "
				).append( " where cod_pers = ? and (licencia = ? or licencia = ? ");

		if (prog.equalsIgnoreCase("si")) {
			strSQL.append(" or ( (licencia = ? and (est_id = ? or est_id = ?)) ");
			strSQL.append(" 	  or (licencia = ? and est_id = ?) ) ");
		}

		strSQL.append(" ) ");

		if (!fecha2.equals("")) {

			if (!fecha1.equals(fecha2)) {
				strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2
						).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2
						).append( "') and ffinicio <= DATE('" ).append( fecCompara2
						).append( "'))) ");
			} else {
				strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
			}

		} else {
			strSQL.append(" and (ffinicio <=DATE('" ).append( fecCompara1
					).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
		}

		if (!fechaIni.equals("")) {
			strSQL.append(" and ffinicio != DATE('" ).append( fecIni ).append( "') ");
		}
		
		
		
		valores.add(params.get("cod_pers"));
		valores.add(constantes.leePropiedad("VACACION"));
		valores.add(constantes.leePropiedad("VACACION_ESPECIAL"));
		if (prog.equals("si")) {
			valores.add(constantes.leePropiedad("VACACION_PROGRAMADA"));
			valores.add(constantes.leePropiedad("PROG_ACEPTADA"));
			valores.add(constantes.leePropiedad("PROG_PROGRAMADA"));
			valores.add(constantes.leePropiedad("REPROGRAMACION_VACACION"));
			valores.add(constantes.leePropiedad("PROG_ACEPTADA"));
		}

		List resultado = executeQuery(dataSource, strSQL.toString(), valores.toArray());
		
		if(resultado != null && !resultado.isEmpty() && resultado.size()>0) {
			tiene = true;
		}
		
		return tiene;
	}
	
	/**
	 * Metodo encargado de verificar si un trabajador tiene un registro de
	 * vacacion efectiva, especial o programada cuyas fechas sean abarcadas por los
	 * parametros indicados.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param fecha1
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fecha2
	 *            String. Fecha superior del rango de busqueda.
	 * @return Booelan. Devuelve "verdadero" si existe por lo menos un registro
	 *         que cumpla con las condiciones.
	 *@param String cod_pers, String fecha1, String fecha2, prog(si, no), String fechaIni         
	 * @throws DAOException
	 */
	public boolean findByCodPersFIniFFinProgAdelanto(Map params) throws DAOException {

		StringBuffer strSQL = new StringBuffer("");		
		boolean tiene = false;
		List valores = new ArrayList();
		
		String fecha1 = (params.get("fecha1") != null ) ? params.get("fecha1").toString():"";
		String fecha2 = (params.get("fecha2") != null ) ? params.get("fecha2").toString():"";
		String fechaIni = (params.get("fechaIni") != null ) ? params.get("fechaIni").toString():"";
		String prog = (params.get("prog") != null ) ? params.get("prog").toString():"no";

		FechaBean fb_fecha1 = new FechaBean(fecha1);
		String fecCompara1 = fb_fecha1.getFormatDate("yyyy/MM/dd");
		
		FechaBean fb_fecha2 = null;
		String fecCompara2 = "";
		
		FechaBean fb_fecIni = null;
		String fecIni = "";
		
		if (fecha2.trim() != "") {
			fb_fecha2 = new FechaBean(fecha2);
			fecCompara2 = fb_fecha2.getFormatDate("yyyy/MM/dd");
		}
		if (fechaIni.trim() != "") {
			fb_fecIni = new FechaBean(fechaIni);
			fecIni = fb_fecIni.getFormatDate("yyyy/MM/dd");
		}

		strSQL.append("select cod_pers from t1282Vacaciones_d "
				).append( " where cod_pers = ? and (licencia = ? or licencia = ? ");

		if (prog.equalsIgnoreCase("si")) {//
			if(params.get("aprobar") != null  && params.get("aprobar").toString().equals("0"))
			strSQL.append(" or (licencia = ? and ((est_id = ? and anno_vac <> ? ) or est_id=? )) ");
			//strSQL.append(" or (licencia = ? and ((est_id = ? and anno_vac <> ? ) or (est_id=? and (anno_vac<>? or (anno_vac=? and ffinicio<>DATE('").append(fecCompara1).append("') and dias<>?)) ) )) ");
			else
				//strSQL.append(" or (licencia = ? and (est_id = ? or (est_id=? and (anno_vac<>? or (anno_vac=? and ffinicio<>DATE('").append(fecCompara1).append("') and dias<>?))))) ");
			    strSQL.append(" or (licencia = ? and (est_id = ? or (est_id=? and anno_vac<>?))) ");
		}

		strSQL.append(" ) ");

		if (!fecha2.equals("")) {

			if (!fecha1.equals(fecha2)) {
				strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2
						).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2
						).append( "') and ffinicio <= DATE('" ).append( fecCompara2
						).append( "'))) ");
			} else {
				strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
			}

		} else {
			strSQL.append(" and (ffinicio <=DATE('" ).append( fecCompara1
					).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
		}

		if (!fechaIni.equals("")) {
			strSQL.append(" and ffinicio != DATE('" ).append( fecIni ).append( "') ");
		}
		
		valores.add(params.get("cod_pers"));
		valores.add(constantes.leePropiedad("VACACION"));
		valores.add(constantes.leePropiedad("VACACION_ESPECIAL"));
		if (prog.equals("si")) {
			valores.add(constantes.leePropiedad("VACACION_PROGRAMADA"));
			valores.add(constantes.leePropiedad("PROG_ACEPTADA"));
			if(params.get("aprobar") != null  && params.get("aprobar").toString().equals("0")){
				valores.add(params.get("annoVac"));
				valores.add(constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
			}else{
				valores.add(constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD"));
				valores.add(params.get("annoVac"));
			}
		}
		log.debug(""+strSQL);
		List resultado = executeQuery(dataSource, strSQL.toString(), valores.toArray());
		
		if(resultado != null && !resultado.isEmpty() && resultado.size()>0) {
			tiene = true;
		}
		
		return tiene;
	}
	
	/**
	 * metodo : findByCodPersLicenciaAnhoVac
	 * @param params String cod_pers, String licencia, String anho 
	 * @throws DAOException
	 * */
	public int findByCodPersLicenciaAnhoVac(Map params) throws DAOException {
		int acumulado = 0;
		
		StringBuffer strSQL = new StringBuffer(" select sum(dias) as acumulado from  t1282vacaciones_d where cod_pers = ? and ")
		      .append( "      anno_vac = '" ).append( params.get("anho").toString().trim() ).append( "' ");
		
		String licencia = (params.get("licencia") != null) ? params.get("licencia").toString():"";
		
		if (licencia.equals("46")){
			strSQL.append(" and licencia = '").append( licencia ).append( "' ");	
		}else{
		   if (licencia.equals(constantes.leePropiedad("VACACION_PROGRAMADA"))) {
			 strSQL.append(" and ((licencia = '"
					).append( constantes.leePropiedad("VACACION_PROGRAMADA") ).append( "' ");
			 strSQL.append(" 		and (est_id != '" ).append( constantes.leePropiedad("PROG_EFECTUADA")
					).append( "' and est_id != '" ).append( constantes.leePropiedad("PROG_RECHAZADA")
					).append( "')) ");
			 strSQL.append(" or (licencia = '"
					).append( constantes.leePropiedad("REPROGRAMACION_VACACION") ).append( "' ");
			 strSQL.append(" 	and est_id = '" ).append( constantes.leePropiedad("PROG_ACEPTADA")
					).append( "')) ");

		   } else {
			strSQL.append(" and licencia = '" ).append( licencia.trim() ).append( "' ");
			strSQL.append(" and (est_id != '" ).append( constantes.leePropiedad("PROG_EFECTUADA")
					).append( "' and est_id != '" ).append( constantes.leePropiedad("PROG_RECHAZADA")
					).append( "') ");
		   }
		}
		
		Map resultado = executeQueryUniqueResult(dataSource, strSQL.toString(), new Object[]{params.get("cod_pers")}); 
		
		if (resultado != null && !resultado.isEmpty()) {
			acumulado = Integer.parseInt(resultado.get("acumulado")!=null?resultado.get("acumulado").toString():"0");
		}
	
		return acumulado;
	}
	
	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1282vacacion_d
	 * @param params (String cod_pers,String periodo, String  licencia, TimeStamp ffinicio)
	 * @throws DAOException
	 */
	public boolean deleteByPrimaryKey(Map params) throws DAOException {
		
		int modificado = executeUpdate(dataSource, deleteByPrimaryKey.toString(), new Object[]{params.get("cod_pers"), 
			params.get("periodo"), params.get("licencia") ,params.get("ffinicio")});
		
		return (modificado>0);
	}
	
	//dtarazona editado 16/04/2018 para solucionar el bug
	/**
	 * Metodo encargado de editar un registro de la tabla t1282vacacion_d
	 * @param params (String cod_pers,String periodo, String  licencia, TimeStamp ffinicio)
	 * @throws DAOException
	 */
	public boolean updateByPrimaryKeyAndDias(Map params) throws DAOException {
		
		int modificado = executeUpdate(dataSource, updateByPrimaryKeyAndDias.toString(), new Object[]{params.get("cuser_mod"),params.get("cod_pers"), params.get("licencia") ,params.get("ffinicio"),params.get("dias")});
		
		return (modificado>0);
	}
	//fin dtarazona
	
	/**
	 * Metodo encargado de obtener los dias de los detalles de vacaciones
	 * filtrados por registro de trabajador, ano y tipo de vacacion
	 * 
	 * @param params (String cod_pers, String anno_vac, String licencia, String fechaIni)
	 *
	 * @return List listaResul
	 * @throws DAOException
	 */
	public List findByCodPersAnnoVacLicencia(Map params)
			throws DAOException {

		StringBuffer strSQL = new StringBuffer("SELECT dias from t1282vacaciones_d where cod_pers = ? and anno_vac = ? and licencia = ? ");	
		
		List listaResul = null;
		List valores = new ArrayList();		
		String fechaIni = (params.get("fechaIni") != null) ? params.get("fechaIni").toString():"";
		
		valores.add(params.get("cod_pers"));
		valores.add(params.get("anno_vac"));
		valores.add(params.get("licencia"));
		
		if (!fechaIni.equals("")) {
			strSQL.append(" and ffinicio != ? ");
			valores.add(new FechaBean(fechaIni).getTimestamp());
		}
		strSQL.append(" order by dias");
		listaResul = executeQuery(dataSource, strSQL.toString(), valores.toArray());
		
		return listaResul;
	}
	
	//DTARAZONA 3ER ENTREGABLE 
	/**
	 * Metodo encargado de obtener el numero de dias de vacaciones VENDIDOS
	 * filtrados por registro de trabajador, ano y tipo de vacacion
	 * 
	 * @param params (String cod_pers, String anno_vac, String licencia, String fechaIni)
	 *
	 * @return List listaResul
	 * @throws DAOException
	 */
	public HashMap diasVendidosByCodPersAnno(String dbpool,String codPers,String anno)
			throws DAOException {

		StringBuffer strSQL = new StringBuffer("select sum(dias) as dias_vendidos from t1282vacaciones_d where licencia='49' and est_id='4' and cod_pers=? and anno_vac=? ");	
		if(log.isDebugEnabled()) log.debug(anno+" Dato11:"+codPers);
		Map resultado = null;
		try {         
		 resultado = executeQueryUniqueResult(dataSource, strSQL.toString(), new Object[]{anno,codPers});
		 //if(resultado!=null && resultado.get("dias_vendidos").toString().trim()!="")
		 //if(log.isDebugEnabled()) log.debug("Dato11:"+resultado.get("dias_vendidos").toString());
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion (diasVendidosByCodPersAnno):"+e.getMessage());
		}
		return (HashMap)resultado;
	}
	
	/**
	 * Metodo encargado de buscar la lista de vacaciones efectivas y especiales
	 * por gozar a la fecha indicada.
	 *
	 * @param mapa Map que contiene los parametros de busqueda ("fechaFin", "cod_pers" y "dias").
	 * @return List conteniendo los registros cargados en HashMaps.
	 * @throws DAOException
	 */
	public List findVacProgByEst(String pool,String codPers, String anno) throws DAOException {

		StringBuffer strSQL = new StringBuffer("Select s.anno_vac,s.dias,to_char(s.ffinicio, '%d/%m/%Y') as ffinicio from t1282vacaciones_d s where cod_pers=? and licencia='49' and est_id='5' and numero_ref=?");		
		List lista = null;
		List valores = new ArrayList();	
		
		valores.add(codPers);
		valores.add(anno);
		//valores.add(constantes.leePropiedad("VACACION_ESPECIAL"));	
		lista = executeQuery(dataSource, strSQL.toString(), valores.toArray());
		
		return lista;
	}
	public List cargarAnnosVac(String pool,String codPers) throws DAOException {

		StringBuffer strSQL = new StringBuffer("SELECT distinct vd.anno_vac from   t1282vacaciones_d vd where  vd.cod_pers = ?  and vd.licencia = '49' and vd.est_id='1' order by vd.anno_vac asc");		
		List lista = null;
		List valores = new ArrayList();			
		valores.add(codPers);		
		//valores.add(constantes.leePropiedad("VACACION_ESPECIAL"));	
		lista = executeQuery(dataSource, strSQL.toString(), valores.toArray());
		
		return lista;
	}
	
	//FIN DTARAZONA
	
	/**
	 * Metodo encargado de buscar los registros de detalle de vacaciones
	 * filtrados por estado, registro del trabajador, tipo de vacacion y ano.
	 * Join con la tabla T1279Tipo_Mov.
	 * 	
	 * @param params (String codPers, String estado, String licencia,String anho, HashMap seguridad)
	 * @return List vacaciones
	 * 
	 * @throws DAOException
	 */
	public List joinWithT02AndTipoMov(Map params) throws DAOException {

		StringBuffer strSQL = new StringBuffer(joinT02perdp.toString());		
		List vacaciones = null;
		List valores = new ArrayList();
		
		HashMap seguridad = (params.get("seguridad") != null ) ? (HashMap)params.get("seguridad"): new HashMap();
		String estado = (params.get("estado") != null ) ? (String)params.get("estado"): "";
		String licencia = (params.get("licencia") != null ) ? (String)params.get("licencia"): "";
		String anno_vac = (params.get("anno_vac") != null ) ? (String)params.get("anno_vac"): "";
		String cod_pers = (params.get("cod_pers") != null ) ? (String)params.get("cod_pers"): "";
		//primer parametro
		valores.add(cod_pers);
		
		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty()) {
			HashMap roles = (HashMap) seguridad.get("roles");
			String uoSeg = (String) seguridad.get("uoSeg");
			String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
				strSQL.append(" and 1=1 ");
			}
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
				strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "') ");
				strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");				
			}
			//else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			else if (
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
					roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "' ");
			} else {
				strSQL.append(" and 1=2 ");
			}
		}

		if (!estado.trim().equals("")) {
			strSQL.append(" and v.est_id = ?");
			valores.add(estado);
		}

		if (!licencia.equals("")) {
			if (licencia.trim().equals(constantes.leePropiedad("VACACION_PROGRAMADA"))) {

				strSQL.append(" and ((v.licencia='"
						).append( constantes.leePropiedad("VACACION_PROGRAMADA") ).append( "' ");
				strSQL.append("      and v.est_id != '"
						).append( constantes.leePropiedad("PROG_RECHAZADA") ).append( "' ");
				//PROGRAMACION - 17/05/2010
				strSQL.append("      and v.est_id != '"
				).append( constantes.leePropiedad("PROG_PROGRAMADA") ).append( "' ");
				//
				strSQL.append("		 and v.est_id != '"
						).append( constantes.leePropiedad("PROG_EFECTUADA") ).append( "' ) ");
				strSQL.append(" or (v.licencia='"
						).append( constantes.leePropiedad("REPROGRAMACION_VACACION") ).append( "' ");
				strSQL.append("      and v.est_id = '"
						).append( constantes.leePropiedad("PROG_ACEPTADA") ).append( "')) ");
			} else {
				strSQL.append(" and v.licencia='" ).append( licencia.trim() ).append( "' ");
			}

		} else {
			strSQL.append(" and (v.licencia='" ).append( constantes.leePropiedad("VACACION")
					).append( "' or v.licencia='" ).append( constantes.leePropiedad("VACACION_ESPECIAL")
					//prac-asanchez 03/06/2009 - Hacer visible los goces vacacionales del tipo 63
					).append( "' or (v.licencia='" ).append( constantes.leePropiedad("VACACION_INDEMNIZADA")
					//PRAC-ASANCHEZ 16/06/2009
					//).append( "' and v.ffinicio <> v.ffin))" );
					).append( "' and v.ffin is not null ")
					//JRR - 02/07/2009
					.append(" AND v.observ not like 'Imdemnizacion por Vacacion%'))" );
					//
		}

		if (!anno_vac.trim().equals("")) {
			strSQL.append(" and v.anno_vac = '" ).append( anno_vac ).append( "' ");
		}
		
		if(params.get("from2010")!=null)					//jquispecoi 04/2014
		strSQL.append(" and v.anno_vac >= '2010' "); 		
		
		strSQL.append( " and v.cod_pers = p.t02cod_pers ");
		strSQL.append(" order by v.anno_vac, v.ffinicio ");

		if (log.isDebugEnabled()) log.debug(strSQL);
		
		vacaciones = executeQuery(dataSource, strSQL.toString(), valores.toArray());
				
		return vacaciones;
	}
	
	/**
	 * Metodo encargado de buscar la lista de vacaciones efectivas y especiales
	 * por gozar a la fecha indicada.
	 *
	 * @param mapa Map que contiene los parametros de busqueda ("fechaFin", "cod_pers" y "dias").
	 * @return List conteniendo los registros cargados en HashMaps.
	 * @throws DAOException
	 */
	public List findByCodPersFechaFinDiasPend(Map mapa) throws DAOException {

		StringBuffer strSQL = new StringBuffer("SELECT  anno_vac, ffinicio, ffin, dias from	t1282vacaciones_d where cod_pers = ? and (licencia = ? or licencia = ?)");		
		List lista = null;
		List valores = new ArrayList();
		String cod_pers = mapa.get("cod_pers") != null ? mapa.get("cod_pers").toString() : "";
		String fechaFin = ( mapa.get("fechaFin") != null ) ? mapa.get("fechaFin").toString() : "";
		
		int dias = mapa.get("dias") != null ? Integer.parseInt(mapa.get("dias").toString()) : 0;
		//Orden: 1 Ascendente y 0 descendente
		String orden = mapa.get("orden") != null ? mapa.get("orden").toString(): constantes.leePropiedad("ACTIVO");
		
		valores.add(cod_pers);
		valores.add(constantes.leePropiedad("VACACION"));
		valores.add(constantes.leePropiedad("VACACION_ESPECIAL"));
	
		if (!fechaFin.equals("")) {
			strSQL.append(" and ffin > ? ");
			valores.add(new FechaBean(fechaFin).getSQLDate());
		}

		if (dias > 0) {
			strSQL.append(" and dias >= " ).append( dias);
		}

		strSQL.append(" order by ffinicio");

		if (orden.trim().equals("0")) {
			strSQL.append(" desc");
		}
		
		lista = executeQuery(dataSource, strSQL.toString(), valores.toArray());
		
		return lista;
	}
	
	/**
	 * Metodo encargado de buscar la lista de vacaciones efectivas y especiales
	 * ya gozadas hasta la fecha indicada.
	 * 
	 * @param mapa Map que contiene los parametros de busqueda ("fechaFin", "codPers" y "dias").
	 * @return List conteniendo los registros cargados en HashMaps.
	 * @throws DAOException
	 */
	public List findByCodPersFechaFinDiasGoz(Map mapa)throws DAOException {

		StringBuffer strSQL = new StringBuffer("SELECT  anno_vac, ffinicio, ffin, dias from	t1282vacaciones_d where cod_pers = ? and (licencia = ? or licencia = ?)");		
		List lista = null;

		String fechaFin = mapa.get("fechaFin") != null ? mapa.get("fechaFin").toString() : "";
		String cod_pers = mapa.get("cod_pers") != null ? mapa.get("cod_pers").toString() : "";
		int dias = mapa.get("dias") != null ? Integer.parseInt(mapa.get("dias").toString()) : 0;
		//Orden: 1 Ascendente y 0 descendente
		String orden = mapa.get("orden") != null ? mapa.get("orden").toString(): "1";
		
		List valores = new ArrayList();
		
		valores.add(cod_pers);
		valores.add(constantes.leePropiedad("VACACION"));
		valores.add(constantes.leePropiedad("VACACION_ESPECIAL"));

		if (!fechaFin.equals("")) {
			strSQL.append(" and ffin <= ? ");
			valores.add(new FechaBean(fechaFin).getSQLDate());
		}

		if (dias > 0) {
			strSQL.append(" and dias >= " ).append( dias);
		}
		
		if(mapa.get("from2010")!=null)						//jquispecoi 04/2014
			strSQL.append(" and anno_vac >= '2010' "); 		//
		
		strSQL.append(" order by ffinicio");

		if (orden.trim().equals("0")) {
			strSQL.append(" desc");
		}
		
		lista = executeQuery(dataSource, strSQL.toString(), valores.toArray());

		return lista;
	}
	
	/**
	 *
	 * @param params Map (String codPers, String anho, String estado)
	 * @return List
	 * @throws DAOException
	 */
	public List findVacacionesPorFirmar(Map params) throws DAOException {
		StringBuffer strSQL = new StringBuffer("SELECT * from t1282vacaciones_d where cod_pers = ? and est_id = ?");
		List vacaciones = null;
		
		String anno = (params.get("anno") != null ) ? params.get("anno").toString():"";
		String estado = (params.get("estado") != null ) ? params.get("estado").toString():"";
		
		if (!anno.equals("")) {
			strSQL.append(" and anno = '" ).append( anno ).append( "'");
		}

		strSQL.append(" and (licencia = ? or licencia = ?) order by ffinicio");

		vacaciones = executeQuery(dataSource, strSQL.toString(), new Object[]{params.get("cod_pers"), estado, constantes.leePropiedad("VACACION"), constantes.leePropiedad("VACACION_ESPECIAL")});
	
		return vacaciones;
	}
	
	/**
	 * 
	 * @param params Map (String codPers, String anho, String estado, String licencia)
	 * @return List vacaciones
	 * @throws DAOException
	 * */
	public List findByCodPersAnnoVacEstIdLicencia(Map params) throws DAOException {
		StringBuffer strSQL = new StringBuffer("SELECT cod_pers, ffinicio, ffin, anno_vac, dias, observ, periodo, licencia, est_id, anno ")
									.append(" from   t1282vacaciones_d where  cod_pers = ? and est_id = ?");
		
		List vacaciones = null;
		String anno_vac = (params.get("anno_vac") != null) ? params.get("anno_vac").toString():"";
		String licencia = (params.get("licencia") != null) ? params.get("licencia").toString():"";
		String dias = (params.get("dias") != null) ? params.get("dias").toString():"";
		String ffinicio = (params.get("ffinicio") != null) ? params.get("ffinicio").toString():"";
		
		if (!anno_vac.equals("")) {
			strSQL.append(" and anno_vac = '" ).append( anno_vac.trim() ).append( "'");
		}

		if (!licencia.equals("")) {
			strSQL.append(" and licencia = '" ).append( licencia.trim() ).append( "'");
		}
		if (!dias.equals("")) {
			strSQL.append(" and dias = '" ).append( dias.trim() ).append( "'");
		}
		if (!ffinicio.equals("")) {
			FechaBean fecha1 = new FechaBean(ffinicio);
			strSQL.append(" and date(ffinicio) = '" ).append( fecha1.getSQLDate()).append( "' ");
		}

		strSQL.append(" order by ffinicio");

		vacaciones = executeQuery(dataSource, strSQL.toString(), new Object[]{params.get("cod_pers"), params.get("est_id")});
					
		return vacaciones;
	}
	
	/**
	 * Metodo encargado de buscar la lista de vacaciones efectivas y especiales
	 * por gozar a la fecha indicada.
	 * 	 
	 * @param mapa Map que contiene los parametros de busqueda ("fechaFin", "cod_pers" y "dias").
	 * @return List conteniendo los registros cargados en HashMaps.
	 * @throws DAOException
	 */
	public List findPendientes(Map mapa) throws DAOException {

		StringBuffer strSQL = new StringBuffer("SELECT vd.cod_pers, vd.periodo , vd.anno_vac, TO_CHAR(vd.ffinicio, '%d/%m/%Y') as ffinicio, vd.ffin, vd.dias, ")
		.append(" (select descrip from t1279tipo_mov where mov = vd.licencia) as descrip, ")
		.append(" vd.observ from   t1282vacaciones_d vd where  vd.cod_pers = ? ");
		
		List lista = null;

		String cod_pers = mapa.get("cod_pers") != null ? (String) mapa.get("cod_pers") : "";
		String orden = mapa.get("orden") != null ? (String) mapa.get("orden"): "1";
		String licencia = mapa.get("licencia") != null ? (String) mapa.get("licencia") : "";
		
		FechaBean fec_actual = new FechaBean();
		String fechaFin = fec_actual.getFormatDate("yyyy/MM/dd");
		//String fechaFin = Utiles.obtenerFechaActual();

		if (licencia.trim().equals("")) {
			//strSQL.append(" and (vd.licencia = '" ).append( constantes.leePropiedad("VACACION")
			//		).append( "' or vd.licencia = '" ).append( constantes.leePropiedad("VACACION_ESPECIAL")
			//		).append( "' )");
			strSQL.append(" and vd.licencia = '" ).append( "49").append( "' and vd.est_id = '" ).append( "1").append( "' and vd.anno_vac=? ");
		} else {

			if (licencia.trim().equals(constantes.leePropiedad("VACACION_PROGRAMADA"))) {
				strSQL.append(" and (vd.licencia = '"
						).append( constantes.leePropiedad("VACACION_PROGRAMADA") ).append( "' ");
				strSQL.append(" or vd.licencia = '"
						).append( constantes.leePropiedad("REPROGRAMACION_VACACION") ).append( "' ) ");
				strSQL.append(" and vd.est_id = '" ).append( constantes.leePropiedad("PROG_ACEPTADA")
						).append( "' ");
			} else if (licencia.trim().equals(
					constantes.leePropiedad("REPROGRAMACION_VACACION"))) {
				strSQL.append(" and vd.licencia = '"
						).append( constantes.leePropiedad("VACACION_PROGRAMADA") ).append( "' ");
				strSQL.append(" and vd.est_id = '" ).append( constantes.leePropiedad("PROG_ACEPTADA")
						).append( "' ");
			} else {
				strSQL.append(" and vd.licencia = '" ).append( licencia.trim() ).append( "' )");
			}
		}
		String annoVac = mapa.get("annoVac") != null ? (String) mapa.get("annoVac") : "";
		if(log.isDebugEnabled()) log.debug("AnnoVac:"+annoVac);
		if (!fechaFin.equals("")) {
			//strSQL.append(" and vd.ffin > DATE('" ).append( fechaFin			
//			strSQL.append(" and vd.ffin > DATE('" ).append( Utiles.toYYYYMMDD(fechaFin)
					//).append( "')");
		}

		strSQL.append(" order by vd.ffinicio ");
		
		if (orden.trim().equals("0")) {
			strSQL.append(" desc");
		}
			
		lista = executeQuery(dataSource, strSQL.toString(), new Object[]{cod_pers,annoVac});
		if(log.isDebugEnabled()) log.debug("ConsultaVac:"+strSQL);
		return lista;
	}
	
	/**
	 * Metodo encargado de buscar la lista de vacaciones registradas de un
	 * trabajador o de los trabajadores de una unidad organizacional para un año
	 * y tipo de licencias especificos.
	 * 
	 * @param mapa Map que contiene los parametros de busqueda ("criterio", "valor", "anho", "licencia" y "codUsr").
	 * @return List conteniendo los registros cargados en HashMaps.
	 * @throws DAOException
	 */
	public List joinWithT02ByCritValAnhoLicencia(Map mapa) throws DAOException {

		StringBuffer strSQL = new StringBuffer("SELECT  v.cod_pers, v.periodo, v.licencia, v.ffinicio, v.ffin, v.dias, ")
		.append(" v.est_id, v.observ, p.t02ap_pate, p.t02ap_mate, p.t02nombres from	t1282vacaciones_d v, t02perdp p ")
		.append(" where v.licencia = ? ");
		
		List lista = null;

		String valor = mapa.get("valor") != null ? (String) mapa.get("valor"): "";
		String codUO = mapa.get("codUO") != null ? (String) mapa.get("codUO"): "";
		String fechaIni = mapa.get("fechaIni") != null ? (String) mapa.get("fechaIni") : "";
		String fechaFin = mapa.get("fechaFin") != null ? (String) mapa.get("fechaFin") : "";
		String licencia = mapa.get("licencia") != null ? (String) mapa.get("licencia") : "";		
		String codUsr = (String) mapa.get("codUsr");
		Map seguridad = (HashMap) mapa.get("seguridad");

		FechaBean fb_fechaIni = new FechaBean(fechaIni);
		String fecCompara1 = fb_fechaIni.getFormatDate("yyyy/MM/dd");
//		String fecCompara1 = Utiles.toYYYYMMDD(fechaIni);
		
		FechaBean fb_fechaFin = null;
		String fecCompara2 = "";
		
		if (fechaFin.trim() != "") {
//			fecCompara2 = Utiles.toYYYYMMDD(fechaFin);
			fb_fechaFin = new FechaBean(fechaFin);
			fecCompara2 = fb_fechaFin.getFormatDate("yyyy/MM/dd");
		}

		if (!valor.trim().equals("")) {
			strSQL.append(" and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()
					).append( "'");
		}
		strSQL.append(" and v.cod_pers != ? ");

		if (!fechaFin.equals("")) {

			if (!fechaIni.equals(fechaFin)) {

				strSQL.append(" and ((v.ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and v.ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (v.ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and v.ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (v.ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and v.ffin <= DATE('" ).append( fecCompara2
						).append( "') and v.ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
				strSQL.append(" or (v.ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and v.ffin >= DATE('" ).append( fecCompara2
						).append( "') and v.ffinicio <= DATE('" ).append( fecCompara2
						).append( "'))) ");
			} else {
				strSQL.append(" and (v.ffinicio <= DATE('" ).append( fecCompara1
						).append( "'))  and (v.ffin >= DATE('" ).append( fecCompara2
						).append( "')) ");
			}

		} else {
			if (!fechaIni.equals("")) {
				strSQL.append(" and (v.ffinicio <=DATE('" ).append( fecCompara1
						).append( "'))  and (v.ffin >= DATE('" ).append( fecCompara1
						).append( "')) ");
			}
		}

		//criterios de visibilidad
		if (seguridad != null) {

			Map roles = (HashMap) seguridad.get("roles");

			if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '"
						).append( codUO.trim().toUpperCase() ).append( "' ");
			} else {
				strSQL.append(" and 1 = 2 )");
			}
		}

		strSQL.append(" and  p.t02cod_pers = v.cod_pers "
				).append( " order by v.ffinicio, p.t02ap_pate, p.t02ap_mate, p.t02nombres ");

		lista = executeQuery(dataSource, strSQL.toString(), new Object[]{licencia, codUsr});

		return lista;
	}
	
	/**
	 * Metodo encargado de realizar la busqueda del numero de registro del
	 * personal que se no encuentre de vacaciones en la fecha indicada.
	 * 
	 * @param params (String fecha,HashMap seguridad)	 	 
	 *           
	 * @return ArrayList conteniendo los numeros de registro de los trabajadores
	 *         con vacacion.
	 * @throws DAOException
	 */

	public List joinWithT02ByFecha(Map params) throws DAOException {
		
		String fecha = (params.get("fecha") != null) ? params.get("fecha").toString():"";

		StringBuffer strSQL = new StringBuffer("SELECT 	t02cod_pers from t02perdp "
					).append( "where  	t02cod_stat = ? and  "
					).append( "        t02cod_pers not in "
					).append( "        ( "
					).append( "                select 	cod_pers from t1282Vacaciones_d  "
					).append( "                where 	(licencia = ? or licencia = ?) and  "
					).append( "                (ffinicio <= DATE('" ).append( fecha
					).append( "')) and  " ).append( "                (ffin >= DATE('" ).append( fecha
					).append( "')) " ).append( "        )  ");
		
		List personal = null;
				
		Map seguridad = (params.get("seguridad") != null) ? (HashMap)params.get("seguridad"):new HashMap();

		FechaBean fb_fecha = new FechaBean(fecha);
		fecha = fb_fecha.getFormatDate("yyyy/MM/dd");
		//fecha = Utiles.toYYYYMMDD(fecha);

		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty()) {

			Map roles = (HashMap) seguridad.get("roles");
			String uoSeg = (String) seguridad.get("uoSeg");
			String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
				strSQL.append(" and 1=1 ");
			} else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
				//strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "' "); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
				strSQL.append(" and ((substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "') "); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
				strSQL.append(" or (substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append( uoAO ).append( "'))) "); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			} else {
				strSQL.append(" and 1=2 ");
			}
		}		
		strSQL.append("order By t02cod_pers ");

		personal = executeQuery(dataSource, strSQL.toString(), new Object[]{constantes.leePropiedad("ACTIVO"), constantes.leePropiedad("VACACION"), constantes.leePropiedad("VACACION_ESPECIAL")});
			
		return personal;
	}
			
	/**
	 * Metodo encargado de actualizar Programacion de Vacaciones
	 * @param params contiene (Map columns, String anno, Short numero, String u_organ, String cod_pers, String num_seguim)
	 * @throws DAOException
	 */
	public boolean actualizaProgramacion(Map params) throws DAOException {
		
		if (log.isDebugEnabled()) log.debug("T2182DAO actualizaProgramacion - params: " + params);		
		int modificado = 0;
		String indMatrimonio = (String)params.get("ind_matri");
		boolean isMatrimonio = false;
		if(indMatrimonio!=null){
			indMatrimonio = indMatrimonio.equals(constantes.leePropiedad("INACTIVO"))?null:indMatrimonio; //0->null
			isMatrimonio = true;
		}
		
		List listaVal = new ArrayList();
		listaVal.add(params.get("estado_new"));
//		if (params.get("apruebaSolVenta")!=null && params.get("apruebaSolVenta").equals("1")) {
			listaVal.add(params.get("anno_ref"));
			listaVal.add(params.get("area_ref"));
			listaVal.add(params.get("numero_ref"));
//		}
		listaVal.add(params.get("fmod"));
		listaVal.add(params.get("cuser_mod"));
		
		if(isMatrimonio)
			listaVal.add(indMatrimonio);
		
		listaVal.add(params.get("cod_pers"));
		if (params.get("apruebaSolVenta")==null || !params.get("apruebaSolVenta").equals("1"))
			listaVal.add(params.get("ffinicio"));
		listaVal.add(params.get("anno_vac"));
		listaVal.add(params.get("licencia"));
		listaVal.add(params.get("estado_id"));
		//if(params.get("dias")!=null)
		//listaVal.add(params.get("dias"));
		log.debug("ListaParam:"+listaVal);
		if (params.get("apruebaSolVenta")!=null && params.get("apruebaSolVenta").equals("1"))
			listaVal.add(params.get("numero_ref"));
		
		if (params.get("apruebaSolVenta")!=null && params.get("apruebaSolVenta").equals("1")) {
			modificado = executeUpdate(dataSource, actualizaProgVentaFinal.toString(), listaVal.toArray());
		}else if(isMatrimonio){
			modificado = executeUpdate(dataSource, updateProgramacionByMatrimonio, listaVal.toArray());
		} else {
			modificado = executeUpdate(dataSource, actualizaProg.toString(), listaVal.toArray());
		}
		
		return (modificado>0);
	}
	
	
	/**
	 * Metodo encargado de acumular el resumen mensual por Compensacion Vacacional 
	 * @param params (String periodo, String mov)
	 * @throws DAOException
	 */
	public List findDiasCompensacionVacacional() throws DAOException {
		
		List resultado = executeQuery(dataSource, findDiasCompensacionVacacional.toString(), 
				new Object[]{});
		
		return resultado;
	}

	/**
	 * Metodo encargado de verificar si un trabajador tiene un registro de
	 * programacion en registro o en solicitud cuyas fechas sean abarcadas por los
	 * parametros indicados.
	 * @param params (cod_pers, fechaIni, fechaFin)
	 * @throws DAOException
	 */
	public boolean findByCodPersCruceProg(Map params) throws DAOException {

		StringBuffer strSQL = new StringBuffer("");
		boolean tiene = false;
		
		String fechaIni = (params.get("fechaIni") != null)? params.get("fechaIni").toString():"";
		String fechaFin = (params.get("fechaFin") != null)? params.get("fechaFin").toString():"";

		FechaBean fecha1 = new FechaBean(fechaIni);
		String fecCompara1 = fecha1.getFormatDate("yyyy/MM/dd");
		
		FechaBean fecha2 = null;
		String fecCompara2 = "";

		if (!fechaFin.trim().equals("")) {
			fecha2 = new FechaBean(fechaFin);
			fecCompara2 = fecha2.getFormatDate("yyyy/MM/dd");
		}

		strSQL.append("select cod_pers from t1282Vacaciones_d "
				).append( " where cod_pers = ? and licencia = ?  ");

		if (!fechaFin.equals("")) {

			if (!fechaIni.equals(fechaFin)) {
				strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2
						).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2
						).append( "') and ffinicio <= DATE('" ).append( fecCompara2
						).append( "'))) ");
			} else {
				
				strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
			}

		} else {
			
			strSQL.append(" and (ffinicio <=DATE('" ).append( fecCompara1
					).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
		}

		strSQL.append(" and est_id in (?,?) ");
		List listaResult = executeQuery(dataSource, strSQL.toString(), new Object[]{params.get("cod_pers"), constantes.leePropiedad("VACACION_PROGRAMADA"), constantes.leePropiedad("PROG_ACEPTADA"), constantes.leePropiedad("PROGRAMACION_EN_SOLICITUD")});
		if(listaResult !=null && !listaResult.isEmpty()&& listaResult.size()>0){
			tiene = true;
		}
 
		return tiene;
	}
	
	
	/**
	 * Metodo encargado de validar la existencia de programacion vencida 
	 * @param params (Map mapa)
	 * @throws DAOException
	 */
	public List findProgVacVencidas(Map mapa) throws DAOException {
		List resultado = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1282DAO findProgVacVencidas - mapa: "+mapa);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			resultado = executeQuery(dataSource, findProgVacVencidas.toString(), 
					new Object[]{mapa.get("userOrig"), mapa.get("annoVac")});

		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}		
		return resultado;
	}
	
	//JRR - 23/04/2009
	/**
	 * Metodo que valida si en una fecha indicada existe una vacacion vencida efectuada 
	 * Mapa datos contiene cod_pers fechaIni
	 * @throws DAOException
	 */	
	public List findByCodPersFIniFFinVacVencidas(Map datos) throws DAOException {		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1282DAO findByCodPersFIniFFinVacVencidas - datos: "+datos);
			FechaBean fecha = new FechaBean(datos.get("fechaIni").toString());
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(dataSource, findByCodPersFIniFFinVacVencidas.toString(), new Object[]{
						datos.get("cod_pers"), constantes.leePropiedad("VACACION_INDEMNIZADA"),
						fecha.getSQLDate(), fecha.getSQLDate()});
			
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return lista;
	}
	
	
	/* FUSION PROGRAMACION - JROJAS4 */
	
	/**
	  * Metodo que obtiene la lista de las vacaciones programadas para un periodo 
	  * @param datos (String cod_pers, String periodo, String licencia , TimeStamp ffinicio)
	  * @return
	  * @throws DAOException
	  */
	public List obtenerProgramadas(Map params) throws DAOException {
		log.debug("cod_pers:"+params.get("cod_pers"));
		log.debug("anno_vac:"+params.get("anno_vac"));
		log.debug("licencia:"+params.get("licencia"));
		
		List lista = executeQuery(dataSource, FINDBYCODPERS.toString(), new Object[]{ 
			params.get("cod_pers"), params.get("anno_vac"), params.get("licencia"),"0"});
		
		return lista;
	}
	
	
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro sin periodo
	 * @param params contiene (Map columns, String anno, Short numero, String u_organ,  String num_seguim)
	 * @throws DAOException
	 */
	public boolean updateCustomColumnsSinPeriodo(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1282vacaciones_d ");
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
		
		strSQL.append(" WHERE cod_pers = ?  and licencia = ? and ffinicio = ? and est_id!='0' ");
		if(log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString()); 
		//los datos de la llave primaria
		listaVal.add(params.get("cod_pers"));
		listaVal.add(params.get("licencia"));
		listaVal.add(params.get("ffinicio"));
		
		int modificado = executeUpdate(dataSource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}

	
	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1282vacacion_d
	 * Sin el periodo
	 * @param params (String cod_pers, String  licencia, TimeStamp ffinicio)
	 * @throws DAOException
	 */
	public boolean deleteByPrimaryKeySinPeriodo(Map params) throws DAOException {
		
		int modificado = executeUpdate(dataSource, DELETEBYPRIMARYKEYSINPERIODO.toString(), new Object[]{params.get("cod_pers"), 
			 params.get("licencia") ,params.get("ffinicio")});
		
		return (modificado>0);
	}
	
	
	  /**
	 * Metodo encargado de traer una lista con las Vacaciones fisicas del personal ingresado como parametro
	 * @param params (String cod_pers, String periodo)
	 * @throws DAOException
	 */
	public List traerVacFis(Map dBean) throws DAOException {

		if (log.isDebugEnabled()) {
			log.debug("VALORES DEL DBEAN PARA EL QUERY traerVacFis");
			log.debug(dBean.get("txtregistro").toString());
			log.debug(dBean.get("est_id").toString());
			log.debug(dBean.get("cod_periodo").toString());
			log.debug(dBean.get("licencia").toString());
			log.debug(dBean.get("est_id").toString());
			log.debug("==================================================");										  
		}


		Object [] obj = new Object[]{dBean.get("txtregistro").toString(), dBean.get("est_id").toString(),
				dBean.get("cod_periodo").toString(),dBean.get("licencia").toString(),dBean.get("est_id").toString()};
		List listaResul = executeQuery(dataSource, FINDVACFISBYCODPERS.toString(),obj);
		return listaResul;
	}
	
	/**
	 * metodo : selectByPrimaryKeySinPeriodo
	 * 
	 * @param params
	 *          
	 * @throws DAOException
	 */
	public int selectByPrimaryKeySinPeriodo(Map params) throws DAOException {
		int cantidad = 0;		
		
		Map resultado = executeQueryUniqueResult(dataSource, SELECTBYPRIMARYKEYSINPERIODO.toString(), 
				new Object[] {params.get("cod_pers"), 
			 params.get("licencia") ,params.get("ffinicio")});
		
		if (resultado != null && !resultado.isEmpty()) {
			cantidad = Integer.parseInt(resultado.get("ccantidad").toString());
		}
		
		return cantidad;
	}
	
	/**
	 * Metodo encargado de traer una programamacion , tomando en cuenta el numero de referencia de la solicitud
	 * @param params (String cod_pers, String periodo)	
	 * @throws DAOException
	 */
	public Map findByCodPersNumeroRefLicenciaEstid(Map params) throws DAOException {
		
		Map resultado = null;	
	 	try{	
		    resultado = executeQueryUniqueResult(dataSource, FINDBY_CODPERS_NUMEROREF_LICENCIA_EST_ID.toString()
				, new Object[] {	params.get("cod_pers"),	params.get("licencia"),params.get("est_id"), 
			params.get("anno_vac"), params.get("numero_ref")});
		    
	 	} catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta T1282DAO. [findByCodPersNumeroRefLicenciaEstid]");
		}
	 	return resultado;
	}


	/*               */
	
/* JRR - PROGRAMACION - 18/05/2010 */	
	/**
	 * metodo : findByCodPersAnnoVacEfecComp
	 * 
	 * @param saldo
	 *          String cod_pers, String anno
	 * @throws DAOException
	 */
	public int findByCodPersAnnoVacEfecComp(Map saldo) throws DAOException {
		int sumVacEfecComp = 0;		
		Map resultado = executeQueryUniqueResult(dataSource, QUERY3_SENTENCE .toString(), 
				new Object[] {	saldo.get("cod_pers"), saldo.get("anno"),constantes.leePropiedad("VACACION"),
			constantes.leePropiedad("VACACION_VENTA"),constantes.leePropiedad("PROGRAMACION_ACEPTADA")});
		
		if (resultado != null && !resultado.isEmpty()) {
			sumVacEfecComp = Integer.parseInt(resultado.get("sumvacefeccomp") != null ? resultado.get(
							"sumvacefeccomp").toString() : constantes.leePropiedad("VALOR_POR_DEFECTO_CERO"));
		}
		return sumVacEfecComp;
	}

	
/* JRR - PROGRAMACION - 04/06/2010 */	
	/**
	 * metodo : findVentasTotXanio
	 * 
	 * @param saldo
	 *          String cod_pers, String anno
	 * @throws DAOException
	 */
	public int findVentasTotXanio(Map saldo) throws DAOException {
		int sumvactotven = 0;		
		Map resultado = executeQueryUniqueResult(dataSource, QUERY4_SENTENCE.toString(), 
				new Object[] {	saldo.get("cod_pers"), saldo.get("anno"),
			constantes.leePropiedad("VACACION_VENTA"),constantes.leePropiedad("PROGRAMACION_ACEPTADA")});
		
		if (resultado != null && !resultado.isEmpty()) {
			sumvactotven = Integer.parseInt(resultado.get("sumvactotven") != null ? resultado.get(
							"sumvactotven").toString() : constantes.leePropiedad("VALOR_POR_DEFECTO_CERO"));
		}
		
		return sumvactotven;
	}	
	
	/**
	 * metodo : findVentasTotXanioEnSolicitud
	 * 
	 * @param saldo
	 *          String cod_pers, String anno
	 * @throws DAOException
	 */
	public int findVentasTotXanioEnSolicitud(Map saldo) throws DAOException {
		int sumvactotven = 0;		
		Map resultado = executeQueryUniqueResult(dataSource, FINDTOTALVENTASENSOL.toString(), 
				new Object[] {	saldo.get("cod_pers"), saldo.get("annoVac")});
		
		if (resultado != null && !resultado.isEmpty()) {
			sumvactotven = Integer.parseInt(resultado.get("diasventasol") != null ? resultado.get(
							"diasventasol").toString() : constantes.leePropiedad("VALOR_POR_DEFECTO_CERO"));
		}
		
		return sumvactotven;
	}
	
	/**
	 * metodo : FINDTOTALFRACCIONADO
	 * 
	 * @param saldo
	 *          String cod_pers, String anno
	 * @throws DAOException
	 */
	public int findTotalFraccionadoPorAnio(Map saldo) throws DAOException {
		int sumvactotven = 0;		
		Map resultado = executeQueryUniqueResult(dataSource, FINDTOTALFRACCIONADO.toString(), 
				new Object[] {	saldo.get("cod_pers"), saldo.get("annoVac"),constantes.leePropiedad("ACTIVO")});
		
		if (resultado != null && !resultado.isEmpty()) {
			sumvactotven = Integer.parseInt(resultado.get("sumfraccionados") != null ? resultado.get(
							"sumfraccionados").toString() : constantes.leePropiedad("VALOR_POR_DEFECTO_CERO"));
		}
		return sumvactotven;
	}	
	/**
	 * metodo : FINDTOTALREPROGENSOL
	 * 
	 * @param saldo
	 *          String cod_pers, String anno
	 * @throws DAOException
	 */
	public int findTotalReprogEnSolicitud(Map saldo) throws DAOException {
		int sumvactotven = 0;		
		Map resultado = executeQueryUniqueResult(dataSource, FINDTOTALREPROGENSOL.toString(), 
				new Object[] {	saldo.get("cod_pers"), saldo.get("annoVac")});
		
		if (resultado != null && !resultado.isEmpty()) {
			sumvactotven = Integer.parseInt(resultado.get("sumfracsol") != null ? resultado.get(
							"sumfracsol").toString() : constantes.leePropiedad("VALOR_POR_DEFECTO_CERO"));
		}
		return sumvactotven;
	}	
	
	
/*     */	
	
	
//FUSION DE CLASE CON METODOS COMSA	
/* JRR - AUTORIZACION BATCH - 08/03/2011 */
    /**
	 * 
	 * Metodo que se encarga de listas de personas en Vacaciones
	 * 
	 * @param HashMap
	 *            datos
	 * @return List
	 * @throws DAOException
	 */
	public List obtenerVacaciones(Map datos) throws DAOException {
		List lista = null;
		try {
			String fecha = datos.get("fechaProceso").toString();
			Object obj[] = { datos.get("licencia"), Timestamp.valueOf(fecha) };
			lista = executeQuery(dataSource, FINDBYVACACIONES.toString(), obj);
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lista;
	}
	
	/**
	 * 
	 * Metodo que se encarga de listas de personas fin vacaciones
	 * 
	 * @param HashMap
	 *            datos
	 * @return List
	 * @throws DAOException
	 */
	public List obtenerfinVacaciones(Map datos) throws DAOException {
		List lista = null;
		try {
			Object obj[] = { datos.get("licencia"), datos.get("fechaFin"),datos.get("fechaIni") };
			lista = executeQuery(dataSource, FINDBYFINVACACIONES.toString(),obj);
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lista;
	}	
/*    */
	
	/* ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	
	/**
	 * Metodo encargado de traer una lista de las vacaciones programadas aceptadas o activas pendientes de goce por los trabajadores 
	 * @return List
	 * @throws DAOException
	 */	
	public List findVacacionesProgramadasActivas() throws DAOException {	
		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1282DAO findVacacionesProgramadasActivas");			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(dataSource, FIND_VACACIONES_PROGRAMADAS_ACEPTADAS.toString(), new Object[]{
				//constantes.leePropiedad("VACACION_PROGRAMADA"),constantes.leePropiedad("PROGRAMACION_ACEPTADA"), constantes.leePropiedad("DIAS_PREVIOS_INICIO_VACACIONES"),constantes.leePropiedad("ACTIVO")});
				constantes.leePropiedad("VACACION_PROGRAMADA"),constantes.leePropiedad("PROGRAMACION_ACEPTADA"), constantes.leePropiedad("DIAS_PREVIOS_INICIO_VACACIONES5"),constantes.leePropiedad("DIAS_PREVIOS_INICIO_VACACIONES4"),constantes.leePropiedad("DIAS_PREVIOS_INICIO_VACACIONES3"),constantes.leePropiedad("DIAS_PREVIOS_INICIO_VACACIONES2"),constantes.leePropiedad("DIAS_PREVIOS_INICIO_VACACIONES1"),constantes.leePropiedad("ACTIVO"),constantes.leePropiedad("CODREL_MOD_FORMATIVA")});
			
		} catch (Exception e) {
			log.error("*** SQL Error en findVacacionesProgramadasActivas****",e);
		}
		return lista;
	}
	/* FIN ICAPUNAY - TRABAJADORES CON VACIONES PROGRAMADAS ACEPTADAS O ACTIVAS - 28/02/2011 */	

	/* ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
	/**
	 * 
	 * Metodo que se encarga de listar las personas en Vacaciones Programadas Activas	
	 * cuyo rango de fechas de vacaciones incluya la fecha(fechaProceso) pasada como par�metro
	 * @param datos HashMap	       
	 * @return List
	 * @throws DAOException
	 */
	public List obtenerVacacionesProgramadas(Map datos) throws DAOException {
		List lista = null;
		try {
			String fechaProceso = datos.get("fechaProceso").toString();
			Object obj[] = { datos.get("estadoTrabajador"),datos.get("licencia"),datos.get("estadoLicencia"),Timestamp.valueOf(fechaProceso) };
			lista = executeQuery(dataSource, FINDBY_VACACIONESPROGRAMADAS.toString(), obj);
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lista;
	}	
	
	/* FIN ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
	
		/* EBV 11/06/2012*/
	/**
	 * Metodo encargado de verificar si un trabajador tiene un registro de
	 * vacacion efectiva o especial cuyas fechas sean abarcadas por los
	 * parametros indicados.
	 * @param params (cod_pers, fechaIni, fechaFin)
	 * @throws DAOException
	 */
	public boolean findByCodPersFIniFFin49(Map params) throws DAOException {

		StringBuffer strSQL = new StringBuffer("");
		boolean tiene = false;
		
		String fechaIni = (params.get("fechaIni") != null)? params.get("fechaIni").toString():"";
		String fechaFin = (params.get("fechaFin") != null)? params.get("fechaFin").toString():"";
		log.debug("fechaIni:" +fechaIni+"|");
		log.debug("fechaFin:" +fechaFin+"|");

		FechaBean fecha1 = new FechaBean(fechaIni);
		String fecCompara1 = fecha1.getFormatDate("yyyy/MM/dd");
//		String fecCompara1 = Utiles.toYYYYMMDD(fechaIni);
		log.debug("fecCompara1:" +fecCompara1+"|");
		
		FechaBean fecha2 = null;
		String fecCompara2 = "";
		//if (fechaFin.trim() != "") {
		if (!fechaFin.trim().equals("")) {
			fecha2 = new FechaBean(fechaFin);
			//fecCompara2 = Utiles.toYYYYMMDD(fechaFin);
			fecCompara2 = fecha2.getFormatDate("yyyy/MM/dd");
			log.debug("fecCompara2:" +fecCompara2+"|");
		}

		strSQL.append("select cod_pers from t1282Vacaciones_d "
				).append( " where cod_pers = ? and (licencia = ? ) ")
				.append(" and est_id='1' ");

		if (!fechaFin.equals("")) {

			if (!fechaIni.equals(fechaFin)) {
				strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
				strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
						).append( "') and ffin <= DATE('" ).append( fecCompara2
						).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
				strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
						).append( "') and ffin >= DATE('" ).append( fecCompara2
						).append( "') and ffinicio <= DATE('" ).append( fecCompara2
						).append( "'))) ");
			} else {
				
				strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
			}

		} else {
			
			strSQL.append(" and (ffinicio <=DATE('" ).append( fecCompara1
					).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
		}

		List listaResult = executeQuery(dataSource, strSQL.toString(), new Object[]{params.get("cod_pers"), constantes.leePropiedad("VACACION_PROGRAMADA")});
		if(listaResult !=null && !listaResult.isEmpty()&& listaResult.size()>0){
			tiene = true;
		}
 
		return tiene;
	}
	/* EBV 11/06/2012*/
	
	//dtarazona
	public List findPrimaryKey(Map datos) throws DAOException {
		List lstVacacionesProgramadas = null;
		try {
			String licencia = (String)datos.get("licencia");
			String cod_pers = (String)datos.get("cod_pers");
			String periodo = (String)datos.get("periodo");
			Date ffinicio = (Date)datos.get("ffinicio");			
			log.debug("ffinicio:"+ffinicio);
			String sql="select ffinicio,dias,numero_ref,est_id from t1282vacaciones_d where cod_pers=? and periodo=? and licencia=? and ffinicio=?";
			List lstParams = new ArrayList();
			lstParams.add(cod_pers);
			lstParams.add(periodo);
			lstParams.add(licencia);			
			lstParams.add(ffinicio);
			if(log.isDebugEnabled()) log.debug("SQL:"+sql);
			lstVacacionesProgramadas = executeQuery(dataSource, sql, lstParams.toArray());
		} catch (Exception e) {
			log.error("Erro:"+e.getMessage());
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lstVacacionesProgramadas;
	}	
	public List findPrimaryKeyAndId(Map datos) throws DAOException {
		List lstVacacionesProgramadas = null;
		try {			
			String cod_pers = (String)datos.get("cod_pers");
			String anno_vac=(String)datos.get("annoVac");
			log.debug("annoVacFPK:"+anno_vac);
			String sql="select to_char(ffinicio,'%d/%m/%Y') as ffinicio,dias,numero_ref,est_id from t1282vacaciones_d where cod_pers=? and anno_vac=? and licencia='49' and est_id='4'";
			List lstParams = new ArrayList();
			lstParams.add(cod_pers);
			lstParams.add(anno_vac);			
			if(log.isDebugEnabled()) log.debug("lstParams:"+lstParams);
			if(log.isDebugEnabled()) log.debug("SQL:"+sql);
			lstVacacionesProgramadas = executeQuery(dataSource, sql, lstParams.toArray());
		} catch (Exception e) {
			log.error("Erro:"+e.getMessage());
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lstVacacionesProgramadas;
	}	
	//fin dtarazona
	
	/**
	 * 
	 * Metodo que se encarga de listar las personas en Vacaciones Programadas Activas	
	 * para un determinado colaborador. Tambi鮠es posible diferencias si este  perte
	 * nece al tipo matrimonio a trav鳠del campo ind_matri. Se agrega descripcion pa
	 * para mostrar en tablas de reportes.
	 * @param datos Par᭥tros de entrada (c?o de personal, licencia, estado prog, 
	 * estado pers)	       
	 * @return List
	 * @throws DAOException
	 */
	public List findVacacionesProgramadasByParams(Map datos) throws DAOException {
		List lstVacacionesProgramadas = null;
		try {
			String ind_matri = (String)datos.get("ind_matri");
			String cod_pers = (String)datos.get("cod_pers");
			String numero = (String)datos.get("numero");
			String anno_vac = (String)datos.get("anno_vac");
			Date ffinicio = (Date)datos.get("ffinicio");
			Date ffin = (Date)datos.get("ffin");
			
			String activo = constantes.leePropiedad("ACTIVO");
			List lstParams = new ArrayList();
			lstParams.add(activo);
			//1. Datos obligatorios
			lstParams.add(datos.get("licencia"));
			lstParams.add(datos.get("est_id"));
			
			//2. Para indicador activo o inactivo
			if (ind_matri!=null && ind_matri.equals(activo)){
				lstParams.add(ind_matri);
				findProgramacionesByParams += " AND ind_matri = ? ";
			}
			
			//3. B?squedas din᭩cas
			if (cod_pers!=null && !cod_pers.equals("")){
				lstParams.add(cod_pers);
				findProgramacionesByParams += " AND cod_pers = ? ";
			}else if (ffinicio!=null){
				lstParams.add(ffinicio);
				findProgramacionesByParams += " AND ffinicio = ? ";
			}else if (ffin!=null){
				lstParams.add(ffin);
				findProgramacionesByParams += " AND ffin = ? ";
			}else if (anno_vac!=null && !ind_matri.equals("")){
				lstParams.add(anno_vac);
				findProgramacionesByParams += " AND anno_vac = ? ";
			}else if(numero!=null && numero.equals("0")){
				//filtrar todo
			}
				
			
			lstVacacionesProgramadas = executeQuery(dataSource, findProgramacionesByParams, lstParams.toArray());
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lstVacacionesProgramadas;
	}	
	
	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1282vacacion_d
	 * adicionalmente se a? el indicador 
	 * @param params Llave compuesta de la tabla
	 * @return Si el resultado tuvo 鸩to
	 * @throws DAOException
	 */
	public boolean deleteByPKAndMore(Map params) throws DAOException {
		int modificado;
		try {
			String strInd_matri = (String)params.get("ind_matri");
			String activo = constantes.leePropiedad("ACTIVO");
			List lstParams = new ArrayList();
			lstParams.add(params.get("cod_pers"));
			lstParams.add(params.get("periodo"));
			lstParams.add(params.get("licencia"));
			lstParams.add(params.get("ffinicio"));
			String deleteByPK_ = deleteByPK;
			if (strInd_matri!=null && strInd_matri.equals(activo)){
				lstParams.add(strInd_matri);
				deleteByPK_ += " AND ind_matri = ? ";
			}
			modificado = executeUpdate(dataSource, deleteByPK_, lstParams.toArray());
		}catch(Exception e){
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return (modificado>0);
	}

	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	/**
	 * Metodo encargado de traer un tipo de vacacion , tomando en cuenta el anio y numero de referencia de la solicitud
	 * @param params (String cod_pers, String licencia, String est_id, String anno_ref, String numero_ref)
	 * @throws DAOException
	 */
	public Map findByCodPersAnnoNumeroRefLicenciaEstidIndMatri(Map params) throws DAOException {
		Map resultado = null;
		try {         
		 resultado = executeQueryUniqueResult(dataSource, FINDBY_CODPERS_ANNONUMEROREF_LICENCIA_ESTID_INDMATRI.toString()
				, new Object[] {	params.get("cod_pers"),	params.get("licencia"),params.get("est_id"), 
			params.get("anno_ref"), params.get("numero_ref")});
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion (findByCodPersAnnoNumeroRefLicenciaEstid)");
		}
		return resultado;
	}
	
	
	/**
	 * Metodo encargado de traer lista de vacaciones no relacionadas a licencia de matrimonio (ind_matri is null), segun anio vacacional, tipo y estado de la vacacion
	 * @param params (String cod_pers, String licencia, String est_id, String anno)
	 * @throws DAOException
	 */
	public List findVacacionesNoGeneradasPorLicenciaMatrimonio(Map params) throws DAOException {
		List resultado = null;
		try {         
		 resultado = executeQuery(dataSource, FINDBY_CODPERS_ANNOVAC_LICENCIA_ESTID.toString()
				, new Object[] {	params.get("cod_pers"),	params.get("licencia"),params.get("estado"), 
			params.get("anno")});
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion (findVacacionesNoGeneradasPorLicenciaMatrimonio)");
		}
		return resultado;
	}
	
	/**
	 * Metodo encargado de eliminar(fisico) vacaciones de la tabla t1282vacacion_d registradas por licencia de matrimonio (ind_matri=1)
	 * segun movimiento vacacion, anio y nro de referencia de la solicitud
	 * @param params (String cod_pers, String  licencia, TimeStamp ffinicio)
	 * @throws DAOException
	 */
	public boolean deleteVacacionesByLicByNroRefSolByMatrimonio(Map params) throws DAOException {
		int eliminado = 0;
		try { 
			eliminado = executeUpdate(dataSource, DELETEBYLICENCIABYNUMEROREFBYINDMATRI.toString(), new Object[]{params.get("cod_pers"), 
			 params.get("licencia") ,params.get("est_id1"),params.get("licencia2"),params.get("est_id2"),params.get("anno_ref"), params.get("numero_ref")});
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo eliminar las vacaciones (deleteByPrimaryKeySinPeriodo)");
		}
		return (eliminado>0);
	}
	
	
	/**
	 * Metodo encargado de activar vacaciones programadas de la tabla t1282vacacion_d desactivadas por licencia de matrimonio (ind_matri=1)
	 * segun anio y nro de referencia de la solicitud
	 * @param params (String cod_pers, String  licencia, TimeStamp ffinicio)
	 * @throws DAOException
	 */
	public boolean updateEstadoTipo49ByNroRefSolByMatrimonio(Map params) throws DAOException {
		int modificados = 0;
		try { 
			modificados = executeUpdate(dataSource, UPDATEESTADOBYTIPO49BYNUMEROREFBYINDMATRI.toString(), new Object[]{params.get("estado1"),params.get("cuser_mod"),params.get("fmod"),params.get("cod_pers"), 
			 params.get("licencia") ,params.get("estado2"), params.get("anno_ref"), params.get("numero_ref")});
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo eliminar las vacaciones (updateEstadoTipo49ByNroRefSolByMatrimonio)");
		}
		return (modificados>0);
	}
	
	/**
	 * Metodo encargado de desactivar el indicador de matrimonio, para siguientes
	 * procesos ya sean por RRHH o por autoservicio.
	 * segun anio y nro de referencia de la solicitud
	 * @param params (String cod_pers, String  licencia, TimeStamp ffinicio)
	 * @throws DAOException
	 */
	public boolean updateIndmatriTipo49ByNroRefSolByMatrimonio(Map params) throws DAOException {
		int modificados = 0;
		try { 
			if(params.get("anno_ref")==null && params.get("numero_ref")==null){
				modificados = executeUpdate(dataSource, UPDATEINDMATRIBYTIPO49BYNUMEROREFBYINDMATRI_LICENCIA.toString(), new Object[]{params.get("cuser_mod"),params.get("fmod"),params.get("ind_matri"),params.get("cod_pers"), 
				params.get("licencia") ,params.get("est_id")});
			}else{
				modificados = executeUpdate(dataSource, UPDATEINDMATRIBYTIPO49BYNUMEROREFBYINDMATRI.toString(), new Object[]{params.get("cuser_mod"),params.get("fmod"),params.get("ind_matri"),params.get("cod_pers"), 
				params.get("licencia") ,params.get("est_id"), params.get("anno_ref"), params.get("numero_ref")});
			}
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo actualizar las vacaciones (updateIndmatriTipo49ByNroRefSolByMatrimonio)");
		}
		return (modificados>0);
	}
	
	//FIN ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	
	/**
	 * AGONZALESF - PAS20171U230200001 - solicitud de reintegro 
	 * Funcion para obtener papeletas para interface de documentos aprobados en reintegros 
	 * @param params
	 * @return
	 * @throws DAOException
	 */
	public List findDocAprobadoVacaciones(Map datos) throws DAOException {
		List lista = new ArrayList(); 
		log.debug("Ingreso findDocAprobadoVacaciones ->" + datos );
		
		java.sql.Date inicio =  new BeanFechaHora(datos.get("ffinicio").toString()).getSQLDate();
                //PAS20171U230200033 - solicitud de reintegro   Correccion del campo ffin
		java.sql.Date  fin =  new BeanFechaHora(datos.get("ffin").toString()).getSQLDate();
	  
		try {
			Object obj[] = { datos.get("cod_pers"), inicio, fin, inicio, fin, inicio, fin };
			lista = executeQuery(dataSource, FIND_DOC_APROB_VACACIONES.toString(), obj);
			log.debug("findDocAprobadoVacaciones ->" + obj );
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lista;
	}
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	/**
	 * Metodo encargado de traer las vacaciones programadas activas de colaboradores regimen 276-728 y 1057 que inicien en un anio y mes
	 * @return List
	 * @throws DAOException
	 */	
	public List findVacacionesProgramadasByAnioMes(String anio, String mes, String unidad, String noregimen) throws DAOException {	
		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1282DAO findVacacionesProgramadasByAnioMes");
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(dataSource, FIND_VACACIONES_PROGRAMADAS_BYANIOMES.toString(), new Object[]{constantes.leePropiedad("ACTIVO"),anio, mes,anio,mes,
				constantes.leePropiedad("VACACION_PROGRAMADA"),constantes.leePropiedad("PROGRAMACION_ACEPTADA"),unidad, noregimen});
			
		} catch (Exception e) {
			log.error("*** SQL Error en findVacacionesProgramadasByAnioMes****",e);
		}
		return lista;
	}
	
	/**
	 * Metodo encargado de obtener los colaboradores con  vacaciones programadas activas que inicien en un a? mes y segun unidad especifica
	 * @return List
	 * @throws DAOException
	 */	
	public List findColabConVacacProgramadasByAnioMesUnidad(String anio, String mes, String unidad) throws DAOException {	
		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1282DAO findColabConVacacProgramadasByAnioMesUnidad");
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(dataSource, FIND_COLAB_VACACIONES_PROGRAMADAS_BYANIOMESUNIDAD.toString(), new Object[]{constantes.leePropiedad("ACTIVO"),anio, mes,anio,mes,
				constantes.leePropiedad("VACACION_PROGRAMADA"),constantes.leePropiedad("PROGRAMACION_ACEPTADA"),unidad});
			
		} catch (Exception e) {
			log.error("*** SQL Error en findColabConVacacProgramadasByAnioMesUnidad****",e);
		}
		return lista;
	}
	
	/**
	 * Metodo encargado de obtener los colaboradores con  vacaciones programadas activas que inicien en un a? mes
	 * @return List
	 * @throws DAOException
	 */	
	public List findColabConVacacProgramadasByAnioMes(String anio, String mes, String unidad) throws DAOException {	
		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("T1282DAO findColabConVacacProgramadasByAnioMes");
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(dataSource, FIND_COLABORADORES_VACACIONES_PROGRAMADAS_BYANIOMES.toString(), new Object[]{constantes.leePropiedad("ACTIVO"),anio, mes,anio,mes,
				constantes.leePropiedad("VACACION_PROGRAMADA"),constantes.leePropiedad("PROGRAMACION_ACEPTADA"),unidad});
			
		} catch (Exception e) {
			log.error("*** SQL Error en findColabConVacacProgramadasByAnioMes****",e);
		}
		return lista;
	}
	
	/**
	 * Metodo encargado de traer lista de vacaciones (programadas=1, en solicitud=5, efectiva=2, vendida=4) por colaborador y anio vacacional
	 * @param codPers String
	 * @param anio String
	 * @throws DAOException
	 */
	public List findVacacionesByCodPersAnio(String codPers, String anio) throws DAOException {
		List vacaciones = null;
		try {         
			vacaciones = executeQuery(dataSource, FINDVACACIONES_BYCODPERSANIO.toString()
				, new Object[] {codPers,anio,constantes.leePropiedad("VACACION_PROGRAMADA"),constantes.leePropiedad("INACTIVO")});
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion (findVacacionesByCodPersAnio)");
		}
		return vacaciones;
	}
	
	/** Metodo para listar las personas que faltan programar sus vacaciones sin repetir tipo de vacacion por anio
	 * 
	 * @param registro
	 * @return Map
	 * @throws DAOException
	 */
	public List listarFaltantesProgramVacaciones(Map datos) throws DAOException {
		List lista = null;
		try {
			if (log.isDebugEnabled()) log.debug("T1282DAO listarFaltantesProgramVacaciones");
			if (log.isDebugEnabled()) log.debug("fechaIngreso date: "+new FechaBean((String)datos.get("fechaIngreso")).getSQLDate());
			if (log.isDebugEnabled()) log.debug("unidad matches: "+(String)datos.get("unidad")+"*");
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			//lista = executeQuery(dataSource, FINDPERSFALTAPROGVAC.toString(), new Object[]{constantes.leePropiedad("ACTIVO"),new FechaBean((String)datos.get("fechaIngreso")).getSQLDate(),(String)datos.get("unidad")+"%"});
			lista = executeQuery(dataSource, FINDPERSFALTAPROGVAC.toString(), new Object[]{constantes.leePropiedad("ACTIVO"),new FechaBean((String)datos.get("fechaIngreso")).getSQLDate(),(String)datos.get("unidad")+"*",constantes.leePropiedad("ACTIVO"),new FechaBean((String)datos.get("fechaIngreso")).getSQLDate(),(String)datos.get("unidad")+"*",(String)datos.get("tipo"),(String)datos.get("estado")});

		} catch (Exception e) {
			throw new DAOException(this,
			"No se pudo cargar la informacion de personal-metodo listarFaltantesProgramVacaciones()");
		}
		return lista;
	}
	//FIN ICAPUNAY 
	
	}
