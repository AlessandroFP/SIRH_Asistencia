package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sol.BeanFechaHora;
//import pe.gob.sunat.utils.Constantes;
//import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.Utiles;

/** 
 * 
 * Clase       : T1273DAO 
 * Descripcion : clase encargada de administrar los datos de la tabla t1273licencia 
 * Proyecto    : ASISTENCIA 
 * Autor       : PRAC-JCALLO
 * Fecha       : 11-MAR-2007 
 * 
 * */

public class T1273DAO extends DAOAbstract {
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	//private static final Logger log = Logger.getLogger(T1273DAO.class);
	private final StringBuffer QUERY1_SENTENCE = new StringBuffer("UPDATE t1273licencia set anno= ?, u_organ = ?, ffin = ?, qdias = ?, anno_ref = ?, numero_ref = ?, area_ref = ?, observ = ?, fmod= ?, cuser_mod=? ")
	.append(" where periodo = ? and numero = ? and ffinicio = ? and cod_pers = ? and licencia = ? ");
	private final StringBuffer INSERTAR_LICENCIA =new StringBuffer("INSERT into t1273licencia( periodo, anno, numero, u_organ, ffinicio, ffin, cod_pers, qdias, licencia, anno_ref, numero_ref, area_ref, observ, fcreacion, cuser_crea)")
	.append(" values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
	private final StringBuffer EXISTE_LICENCIA = new StringBuffer("SELECT cod_pers from t1273Licencia where cod_pers = ?  AND ffinicio <= ? and ffin >= ? ");
	
	private final StringBuffer findByCodPersFecha = new StringBuffer("SELECT cod_pers, anno, ffin, ffinicio, licencia, qdias from  t1273Licencia " )
	.append( " where cod_pers = ? and ffinicio <= ? and ffin >=? ");
	
	private final StringBuffer findDiasAcumulados = new StringBuffer("SELECT sum(t.qdias) total from t1273licencia t where cod_pers = ? and anno = ? and licencia = ?");
	
	private final StringBuffer joinWithT02T1279 = new StringBuffer("SELECT 	l.cod_pers, l.licencia, l.numero, l.anno, l.ffinicio, l.ffin, l.observ, l.anno_ref,")
	.append(" l.numero_ref, l.area_ref, l.periodo, l.cuser_crea, l.cuser_mod, l.fcreacion, l.fmod, p.t02ap_pate||' '||p.t02ap_mate||', '||p.t02nombres as trabajador, m.descrip, m.tipo_id, ")
	.append(" m.dias_acum, m.ind_proc, l.qdias from	t1273licencia l, t02perdp p, t1279tipo_mov m ");
	
        //PAS20181U230200023 
	private final StringBuffer joinWithT1274 = new StringBuffer("SELECT c_certific, cod_cmp, fecha, enfermedad ,num_archivo,cod_cie10 from 	t1274licen_med where 	cod_pers = ? and ")
	.append(" periodo = ? and numero = ? and ffinicio = ? ");
	
	private final StringBuffer findCantidadByTipo = new StringBuffer("SELECT count(*) total from t1273licencia t where licencia = ? and cod_pers = ? ");
	
	//JRR
	private final StringBuffer findSupLimiteLicenciasSinGoce = new StringBuffer("SELECT a.cod_pers, a.qdias, a.ffinicio, a.ffin ")
	.append(" FROM   t1273licencia a, t1279tipo_mov m ")
	.append(" WHERE ? between a.ffinicio and a.ffin ")
	.append(" and m.califica = 'S' ")
	.append(" and m.tipo_id = '03' ")
	.append(" and a.licencia != '008' ")
	.append(" and a.licencia = m.mov ")
	.append(" and a.qdias >= 30 ")
	.append(" UNION ")
	.append(" SELECT a.cod_pers, a.qdias, a.ffinicio, a.ffin ")        
	.append(" FROM   t1273licencia a, t1279tipo_mov m ")
	.append(" WHERE ? between a.ffinicio and a.ffin ")
	.append(" and m.califica = 'S' ")
	.append(" and m.tipo_id = '03' ")
	.append(" and a.licencia != '008' ")
	.append(" and a.licencia = m.mov ")
	.append(" and a.qdias >= 30 ");
	
	//prac-jcallo
	private final StringBuffer findAllColumnsByKey = new StringBuffer("select periodo, nvl(anno,'') as anno, numero, nvl(u_organ,'') as u_organ, ffinicio, ffin, cod_pers, qdias, licencia, anno_ref, numero_ref, area_ref, observ, fcreacion, cuser_crea, fmod, nvl(cuser_mod,'') as cuser_mod ")
	.append(" from t1273licencia where periodo = ? and numero = ? and ffinicio = ? and cod_pers = ? and licencia = ?");
	
	private final StringBuffer findBySolRef = new StringBuffer(
	"select lic.periodo, lic.anno, lic.numero, lic.cod_pers, lic.licencia, lic.ffinicio, lic.ffin, lic.qdias, mov.descrip "+ 
	"from t1273licencia lic, t1279tipo_mov mov "+
	"where lic.anno_ref=? and lic.numero_ref=? and lic.cod_pers=? and lic.licencia=? and mov.mov=lic.licencia ");
	
	private final StringBuffer deleteByPrimaryKey = new StringBuffer("DELETE from t1273licencia where periodo = ? and numero = ? and ffinicio = ? and cod_pers = ? and licencia = ?");
	private final StringBuffer deleteByReference = new StringBuffer("DELETE from t1273licencia where numero_ref = ? and anno_ref= ? and cod_pers = ? and licencia = ?");
	
	/* JRR - AUTORIZACION BATCH - 08/03/2011 */		
	private final StringBuffer QUERY1_SENTENCE1 = new StringBuffer("SELECT ")
	.append(" cod_pers FROM t1273licencia WHERE licencia = ? ")
	.append(" AND ffinicio<= ? and ffin>= ? ");
	
	private final StringBuffer QUERY1_SENTENCE2 = new StringBuffer("SELECT ")
	.append(" cod_pers FROM t1273licencia WHERE licencia = ? ")
	.append(" AND ffin >= ? AND ffin<= ? ");  
	/*          */
	
	
	//AGONZALESF - PAS20171U230200001 - solicitud de reintegro   Licencias para ver documentos aprobados en reintegro	
        // PAS20171U230200033 ajustr nombre de variable
	// PAS20191U230200011 -solicitud de reintegro
	private final StringBuffer  FIND_DOC_APROB_LICENCIA =new StringBuffer("select m.descrip AS tipo,")
	.append(" DATE(l.ffinicio) AS fecha_inicio,")
	.append(" DATE(l.ffin) AS fecha_fin,")
	.append(" l.qdias AS dias, ")
	.append(" l.observ AS motivo ")
	.append(" FROM t1273licencia l,t1279tipo_mov m")
	.append(" WHERE l.licencia=m.mov")
	.append(" AND l.cod_pers=?")
	.append(" AND l.licencia   in (SELECT  t99descrip from t99codigos   WHERE t99cod_tab='R07' and t99tip_desc ='D' and t99tipo='LICEN' and t99estado ='1'  )")
	.append(" AND ") 
	.append(" ( ")
	.append("  	l.ffinicio between ? and  ? ")
	.append("   or l.ffin between  ? and  ? ")
	.append(" 	or  ?  between l.ffinicio and l.ffin")
	.append(" 	or  ?  between l.ffinicio and l.ffin")
	.append(" )");
	
	//AGONZALESF -PAS20171U230200001 - solicitud de reintegro   Subsidios para ver documentos aprobados en reintegro	
        // PAS20171U230200033 ajustr nombre de variable
	private final StringBuffer  FIND_DOC_APROB_LICENCIA_SUBSIDIOS =new StringBuffer("select m.descrip AS tipo,")
	.append(" DATE(l.ffinicio) AS fecha_inicio,")
	.append(" DATE(l.ffin) AS fecha_fin,")
	.append(" l.qdias AS dias, ")
	.append(" l.observ AS motivo ")
	.append(" FROM t1273licencia l,t1279tipo_mov m")	
	.append(" WHERE l.licencia=m.mov")	
	.append(" AND l.cod_pers=?")
	.append(" AND l.licencia  in (SELECT  t99descrip from t99codigos   WHERE t99cod_tab='R07' and t99tip_desc ='D' and t99tipo='SUBSI' and t99estado ='1'  )")		
	.append(" AND ((date(l.ffinicio) BETWEEN ? and ?) or (date(l.ffin) BETWEEN ? and ?))");
	
	//AGONZALESF - PAS20171U230200001 - solicitud de reintegro verificar si existe licencia en sirh 
	private final StringBuffer EXISTE_LICENCIA2 =new StringBuffer("SELECT anno , numero, licencia , cod_pers, ffinicio , ffin  from t1273Licencia where cod_pers = ?  AND (ffinicio = ? or ffin = ?)");
	
	
	private DataSource datasource;
		
	  /**
	   * 
	   * Este constructor del DAO dicierne como crear el datasource
	   * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	   * mejorar la invocacion del dao.
	   * 
	   * @param datasource Object
	   */
	  public T1273DAO(Object datasource) {
	    if (datasource instanceof DataSource)
	      this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.datasource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	  }


	/**
	 * Metodo que se encarga de actualizar la licencia
	 * de un trabajador
	 * @param params String periodo, String anho,
			Short numero, String uo, java.sql.Timestamp ffinicio,
			java.sql.Timestamp ffin, String codPers, float dias,
			String licencia, String ano_ref, String numero_ref,
			String area_ref, String observ, java.sql.Timestamp fcrea,
			String usuario
	 * @throws DAOException
	 */
	public boolean modificarLicencia(Map params) throws DAOException {

		//String qdias = (params.get("dias") != null ) ? params.get("dias").toString():"";
		int modifica = 0;
		log.debug("QUERY "+QUERY1_SENTENCE);
		modifica = executeUpdate(datasource, QUERY1_SENTENCE.toString(), new Object[]{params.get("anho"), params.get("uo"), params.get("ffin"),
			params.get("dias"), params.get("ano_ref"),  params.get("numero_ref"), params.get("area_ref"), params.get("observ"), params.get("fcrea"),
			params.get("usuario"), params.get("periodo"), params.get("numero"), params.get("ffinicio"), params.get("cod_pers"), params.get("licencia")});//new ArrayList(); 
		return (modifica>0) ? true:false;
	}
	
	/**
	 * Metodo que se encarga de registrar la licencia
	 * de un trabajador
	 * @throws DAOException
	 */
	public boolean registrarLicenciaCompensacion(Map datos)	throws DAOException {

		
		FechaBean fecha1 = new FechaBean();
		FechaBean fecha2 = new FechaBean((String)datos.get("fecha"));
		int modifica = 0;
		log.debug("QUERY "+INSERTAR_LICENCIA);
		
		modifica = executeUpdate(datasource, INSERTAR_LICENCIA.toString(), new Object[]{fecha1.getFormatDate("yyyyMM"), fecha1.getAnho(),
			//"0",
			//JRR - 26/06/2009
			datos.get("numero"),
			(String)datos.get("uuoo"), fecha2.getSQLDate() , fecha2.getSQLDate(), (String)datos.get("trabajador"), "1", "31", "", "0", "", "&fechacomp =", fecha1.getTimestamp(),
			(String) datos.get("usuario")});			
			//PRAC-ASANCHEZ 26/08/2009
			//(String) datos.get("loginUsuario")});
		
		return (modifica>0);
	}
	
	/**
	 * Metodo que se encarga de determinar si un trabajador tiene registradas
	 * licencias en una fecha determindad, filtrados opcionalmente por tipo de
	 * licencia a buscar y por el numero de la licencia que no se desea
	 * considerar.
	 * @param params Map String codPers, String tipo, String fecha1, String numero
	 * @return @throws	 DAOException
	 */
	public boolean findByCodPersTipoFIniFFin(Map params) throws DAOException {

		StringBuffer strSQL = new StringBuffer(EXISTE_LICENCIA.toString());
		List lista = new ArrayList();
		boolean tiene = false;
		
		FechaBean fb = new FechaBean(params.get("fecha1").toString());
		
		if (!params.get("tipo").toString().equals("")) {
			strSQL.append(" and licencia = '" ).append( params.get("tipo").toString() ).append( "'");
		}

		if (!params.get("numero").toString().equals("")) {
			strSQL.append(" and numero != '" ).append( params.get("numero").toString() ).append( "'");
		}

		lista = executeQuery(datasource, strSQL.toString().trim(), new Object[]{params.get("cod_pers"),fb.getSQLDate(),fb.getSQLDate()});
		
		log.debug("LISTA: "+lista);
		if (lista!=null && lista.size()>0)
			tiene = true;
		return tiene;
	}
	
	//migrado desde sp.asistencia PRAC-JCALLO
	
	/**
	 * Metodo que se encarga de determinar si un trabajador tiene registradas
	 * licencias dentro del rango de fechas indicados, filtrados opcionalmente
	 * por el numero de la licencia que no se desea considerar.
	 * @param params Map String codPers, String fecha1, String fecha2, String numero
	 * @throws DAOException
	 */
	public boolean findByCodPersFIniFFin(Map params) throws DAOException {

		log.debug("params "+ params);
		
		//24072008
		String hmfecha1 = (params.get("fecha1") != null)? params.get("fecha1").toString():"";
		String hmfecha2 = (params.get("fecha2") != null)? params.get("fecha2").toString():"";

		FechaBean fecha1 = new FechaBean(hmfecha1);
		//FechaBean fecha1 = new FechaBean(params.get("fecha1").toString());
		String fecCompara1 = fecha1.getFormatDate("yyyy/MM/dd");
		log.debug("feccompara1:"+ fecCompara1);
//		String fecCompara1 = Utiles.toYYYYMMDD(params.get("fecha1").toString());			
		String fecCompara2 = "";
		FechaBean fecha2 = null;
		StringBuffer strSQL= new StringBuffer();
		boolean tiene = false;
		//if (!params.get("fecha2").toString().trim().equals("")) {
		if (!hmfecha2.trim().equals("")) {		
			//fecCompara2 = Utiles.toYYYYMMDD(params.get("fecha2").toString());
			fecha2 = new FechaBean(hmfecha2);
			//fecha2 = new FechaBean(params.get("fecha2").toString());
			fecCompara2 = fecha2.getFormatDate("yyyy/MM/dd");
			log.debug("feccompara2:"+ fecCompara2);
		}
		
		strSQL.append("SELECT cod_pers from t1273Licencia where cod_pers = ? ");

		if (!params.get("fecha2").toString().trim().equals("")) {

			if (!params.get("fecha1").toString().equals(params.get("fecha2").toString())) {
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
			strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
					).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
		}

		if (!params.get("numero").toString().equals("")) {
			strSQL.append(" and numero != '" ).append( params.get("numero").toString() ).append( "'");
		}
		
		List resultado = executeQuery(datasource, strSQL.toString(), new Object[]{params.get("cod_pers")});
		
		if(resultado != null && !resultado.isEmpty() &&  resultado.size()>0) tiene = true;

		return tiene;
	}

	/**
	 * Metodo que se encarga de listar las licencias de un trabajador
	 * registradas para una determinada fecha.
	 * @param params String cod_pers, String fecha
	 * @throws DAOException
	 */
	public List findByCodPersFecha(Map params) throws DAOException {
			
		log.debug("FECHA:" + params.get("fecha")+"|");
		List licencia = null; 
		FechaBean fech = new FechaBean(params.get("fecha").toString());		
		licencia = executeQuery(datasource, findByCodPersFecha.toString(), new Object[]{params.get("cod_pers"), fech.getSQLDate(), fech.getSQLDate()});
		
		return licencia;
	}

	/**
	 * Metodo que se encarga de calcular la cantidad de dias acumulados por un
	 * personal para un ano y tipo de licencia determinados.
	 * @param params Map String cod_pers, String anno, String licencia
	 * @throws DAOException
	 */

	public float findDiasAcumulados(Map params) throws DAOException {

		Map resultado = executeQueryUniqueResult(datasource, findDiasAcumulados.toString(), new Object[]{params.get("cod_pers"), params.get("anno"), params.get("licencia")});
		
		Float total = resultado.get("total")!=null?new Float(resultado.get("total").toString()):new Float("0");
		return total.floatValue();
	}

	/**
	 * Metodo encargado de buscar los registros de licencia de un tipo filtrados
	 * por un criterio con un valor determinado. Join con las tablas T02Perdp y
	 * T1279Tipo_Mov
	 * @param parmas String tipo, String criterio, String valor, HashMap seguridad
	 * @throws DAOException
	 */
	public List joinWithT02T1279(Map params) throws DAOException {
		StringBuffer strSQL = new StringBuffer(joinWithT02T1279.toString());		
		boolean tieneWhere = false;
		List lista = null;
		String criterio = (params.get("criterio") != null) ? params.get("criterio").toString():"";
		String valor = (params.get("valor") != null) ? params.get("valor").toString().trim():"";
		String tipo = (params.get("tipo") != null) ? params.get("tipo").toString():"";
		Map seguridad = (params.get("seguridad") != null) ? (HashMap)params.get("seguridad"):new HashMap();
		
		//FechaBean fb_valor = new FechaBean(valor);
		
		if (criterio.equals("0")) {
			strSQL.append(" where l.numero = '" ).append( valor.trim() ).append( "'");
			tieneWhere = true;
		}
		if (criterio.equals("1")) {
			strSQL.append(" where l.anno = '" ).append( valor.trim() ).append( "'");
			tieneWhere = true;
		}
		if (criterio.equals("2")) {
			strSQL.append(" where p.t02ap_pate like '%"
					).append( valor.trim().toUpperCase() ).append( "%'");
			tieneWhere = true;
		}
		if (criterio.equals("3")) {
			FechaBean fb_valor = new FechaBean(valor);
			strSQL.append(" where l.ffinicio = DATE('"
//					).append( Utiles.toYYYYMMDD(valor.trim()) ).append( "')");
					).append( fb_valor.getFormatDate("yyyy/MM/dd") ).append( "')");					
			tieneWhere = true;
		}
		if (criterio.equals("4")) {
			FechaBean fb_valor = new FechaBean(valor);
			strSQL.append(" where l.ffin = DATE('"
//					).append( Utiles.toYYYYMMDD(valor.trim()) ).append( "')");
					).append( fb_valor.getFormatDate("yyyy/MM/dd") ).append( "')");			
			tieneWhere = true;
		}
		if (criterio.equals("5")) {
			strSQL.append(" where l.cod_pers = '"
					).append( valor.trim().toUpperCase() ).append( "'");
			tieneWhere = true;
		}

		if (!tipo.equals("-1")) {
			if (tieneWhere) {
				strSQL.append(" and ");
			} else {
				strSQL.append(" where ");
				tieneWhere = true;
			}

			strSQL.append(" m.mov = '" ).append( tipo.trim() ).append( "'");
		}

		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty() ) {

			Map roles = (HashMap) seguridad.get("roles");
			String uoSeg = (String) seguridad.get("uoSeg");
			String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

			if (tieneWhere) {
				strSQL.append(" and ");
			} else {
				strSQL.append(" where ");
				tieneWhere = true;
			}
			
			if (roles.get(constantes.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
				strSQL.append(" 1=1 ");
			}
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null) {
				strSQL.append(" ((substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '").append( uoSeg.toUpperCase() ).append( "') ");
				strSQL.append(" or (substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append( uoAO ).append( "'))) ");				
			}
			//else if (roles.get(constantes.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
			else if (
			//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
					roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
				strSQL.append(" substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '").append( uoSeg.toUpperCase() ).append( "' ");
			} else {
				strSQL.append(" 1=2 ");
			}
		}

		//Joins
		if (tieneWhere) {
			strSQL.append(" and ");
		} else {
			strSQL.append(" where ");
			tieneWhere = true;
		}
		strSQL.append(" l.cod_pers = p.t02cod_pers and l.licencia = m.mov and m.tipo_id='03' ");
		
		//strSQL.append(" order by l.numero desc");
		
		lista = executeQuery(datasource, strSQL.toString());
		
		return lista;
	}

	/**
	 * Metodo encargado de buscar los datos de una licencia de tipo medica. Join
	 * con la tabla T1274Licen_Med.
	 * @throws DAOException
	 */
	public Map joinWithT1274(Map params) throws DAOException {
		
		Map licencia = executeQueryUniqueResult(datasource, joinWithT1274.toString(), new Object[]{params.get("cod_pers"), 
			params.get("periodo"), params.get("numero"),  params.get("fechaIni")});
		
		return licencia;
	}
	
	/**
	 * Metodo encargado de calcular la cantidad de licencia por tipo
	 * @param datos
	 * @throws DAOException
	 */
	public int findCantidadByTipo(Map datos) throws DAOException {
		
		int total = 0;
		
		Map resultado = executeQueryUniqueResult(datasource, findCantidadByTipo.toString(), new Object[]{datos.get("tipoMov"), datos.get("userOrig")});

		total = Integer.parseInt(resultado.get("total").toString());
		
		return total;
	}
	
	/**
	 * Metodo que se encarga de registrar la licencia de un trabajador
	 * @param params 
	 * @throws DAOException
	 */
	public boolean registrarLicenciaGeneral(Map datos) throws DAOException {
		
		//"INSERT into t1273licencia( periodo, anno, numero, u_organ, ffinicio, ffin, cod_pers, qdias, licencia, anno_ref, numero_ref, area_ref, observ, fcreacion, cuser_crea)")
		int modifica = executeUpdate(datasource, INSERTAR_LICENCIA.toString(), new Object[]{datos.get("periodo"), datos.get("anno"), datos.get("numero"), datos.get("u_organ"),
			datos.get("ffinicio"), datos.get("ffin"), datos.get("cod_pers"), datos.get("qdias"), datos.get("licencia"), datos.get("anno_ref"), datos.get("numero_ref"), 
			datos.get("area_ref"), datos.get("observ"), datos.get("fcreacion"),datos.get("cuser_crea")});

 
		return (modifica>0);
	}
	
	//prac-jcallo
	/**
	 * Metodo encargado de buscar los datos de una licencia mediante los los siguientes campos
	 * @param params String periodo, Short numero, timestamp ffinicio, String cod_pers, String licencia 
	 * @throws DAOException
	 */
	public Map findAllColumnsByKey(Map params) throws DAOException {
				
		Map licencia = executeQueryUniqueResult(datasource, findAllColumnsByKey.toString(), new Object[]{params.get("periodo"), 
			params.get("numero"), params.get("ffinicio") , params.get("cod_pers"),  params.get("licencia")});
		
		return licencia;
	}
	
	/**
	 * Metodo encargado de buscar los datos de una licencia mediante solicitud de referecia: jquispecoi 03/2014
	 * @param params  
	 * @throws DAOException
	 */
	public Map findBySolRef(Map params) throws DAOException {
		
		Map licencia = executeQueryUniqueResult(datasource, findBySolRef.toString(), new Object[]{params.get("anno_ref"), 
			params.get("numero_ref"), params.get("cod_pers") , params.get("licencia")});
		
		return licencia;
	}
	
	
	//PRAC-JCALLO
	/**
	 * Metodo encargado de actualizar solo las campos especificados de un registro
	 * @param params contiene (Map columns, String periodo, Short numero, Timestamp ffinicio, String cod_pers, String licencia)
	 * @throws DAOException
	 */
	public boolean updateCustomColumns(Map params) throws DAOException {
		
		StringBuffer strSQL = new StringBuffer("UPDATE t1273licencia ");
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
		
		strSQL.append(" WHERE periodo = ? and numero = ? and ffinicio = ? and cod_pers = ? and licencia = ? ");
		if(log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString()); 
		//los datos de la llave primaria
		listaVal.add(params.get("periodo"));
		listaVal.add(params.get("numero"));
		listaVal.add(params.get("ffinicio"));
		listaVal.add(params.get("cod_pers"));
		listaVal.add(params.get("licencia"));
		
		int modificado = executeUpdate(datasource, strSQL.toString(), listaVal.toArray());
		
		return (modificado>0);
	}
	
	/**
	 * Metodo que actualiza Fechas de la solicitud Solicitud: jquispecoi: 03/2014
	 * @throws SQLException
	 */
	public boolean updateRegistroLicenciaFechas(String dbpool, String ffinicio, String ffin, String cuser_mod, 
			String anno, int numero, String cod_pers, String licencia)throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {
			strUpd = "update t1273licencia set ffinicio=?, ffin=?, fmod=?, cuser_mod=? "+
				     "where anno_ref=? and numero_ref=? and cod_pers=? and licencia=? ";
			con = getConnection(datasource);
			pre = con.prepareStatement(strUpd);
			
			pre.setTimestamp(1, Utiles.stringToTimestamp(ffinicio + " 00:00:00"));
			pre.setTimestamp(2, Utiles.stringToTimestamp(ffin + " 00:00:00"));
			pre.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(4, cuser_mod);
			pre.setString(5, anno);
			pre.setInt(6, numero);
			pre.setString(7, cod_pers);
			pre.setString(8, licencia);
			
			int res = pre.executeUpdate();
			result = true;
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {}
			try {
				con.close();
			} catch (Exception e) {}
		}
		return result;
	}
	
	
	
	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1273licencia
	 * @param params (String periodo, Short numero, Timestamp ffinicio, String cod_pers, String licencia)
	 * @throws DAOException
	 */
	public boolean deleteByPrimaryKey(Map params) throws DAOException {
		
		int modificado = executeUpdate(datasource, deleteByPrimaryKey.toString(), new Object[]{params.get("periodo"), 
			params.get("numero"), params.get("ffinicio") , params.get("cod_pers"),  params.get("licencia")});
		
		return (modificado>0);
	}

	/**
	 * Metodo encargado de eliminar(fisico) un registro de la tabla t1273licencia
	 * @param params (String periodo, Short numero, Timestamp ffinicio, String cod_pers, String licencia)
	 * @throws DAOException
	 */
	public boolean deleteByReference(Map params) throws DAOException {

		int modificado = executeUpdate(datasource, deleteByReference.toString(), new Object[]{params.get("numero_ref"), 
			params.get("anno_ref"), params.get("cod_pers") , params.get("licencia")});
		
		return (modificado>0);
	}
	
	//JRR
	/**
	 * Metodo que se encarga de listar las licencias sin goce de un trabajador
	 * que exceden el limite al realizar el envío a Planillas.
	 * @param params String cod_pers, String fecha
	 * @throws DAOException
	 */
	public List findSupLimiteLicenciasSinGoce(Map params) throws DAOException {
		
		if(log.isDebugEnabled()) log.debug("T1273DAO - findSupLimiteLicenciasSinGoce - params: "+params);
		List licencias = new ArrayList();
		FechaBean fechaActual = new FechaBean();
		String cod_anho = params.get("periodo").toString().substring(0,4);
		String cod_mes = params.get("periodo").toString().substring(4,6);
		String fechaMesPeriodo = "01/" + cod_mes + "/" + cod_anho;
		String fechaMesActual = "01/" + fechaActual.getMes() + "/" + fechaActual.getAnho();
		Timestamp tsfechaMesPeriodo = Utiles.stringToTimestamp(fechaMesPeriodo + " 00:00:00");
		Timestamp tsfechaMesActual = Utiles.stringToTimestamp(fechaMesActual + " 00:00:00");
		
		licencias = executeQuery(datasource, findSupLimiteLicenciasSinGoce.toString(),
				new Object[]{tsfechaMesPeriodo, tsfechaMesActual});
		
		return licencias;
	}
	
	
//FUSION DE CLASE CON METODOS COMSA	
/* JRR - AUTORIZACION BATCH - 08/03/2011 */
	/**
	  * 
	  * Metodo que se encarga de listas de personas Exoneradas
	  *    
	  * @param HashMap datos
	  * @return List
	  * @throws DAOException
	  * */
	public List obtenerCodigosBloqueado (Map datos) throws DAOException {
	  List lista = null;
	  try {  	
	  	String fecha = datos.get("fechaProceso").toString();
	  	Object obj[] = {datos.get("licencia"),Timestamp.valueOf(fecha),Timestamp.valueOf(fecha)};  	
		    lista = executeQuery(datasource, QUERY1_SENTENCE1.toString(), obj );    
	  } catch (Exception e) {
	    throw new DAOException(this,"No se pudo cargar la informacion");
	  }
	  return lista;
	}  
	/**
	  * 
	  * Metodo que se encarga de listas de personas Exoneradas
	  *    
	  * @param HashMap datos
	  * @return List
	  * @throws DAOException
	  * */
	public List obtenerCodigosDesbloquea (Map datos) throws DAOException {
	  List lista = null;
	  try {  	
	  	Object obj[] = {	  		
	  		datos.get("licencia"),
	  		datos.get("fechaFin"),	
	  		datos.get("fechaIni")
	  	};  	
	    lista = executeQuery(datasource, QUERY1_SENTENCE2.toString(), obj );	    
	  } catch (Exception e) {
	    throw new DAOException(this,"No se pudo cargar la informacion");
	  }
	  return lista;
	}	 
	
	
	/**
	 * AGONZALESF - PAS20171U230200001 - solicitud de reintegro  
	 * Funcion para  obtener licencias para interface de documentos aprobados en reintegro
	 * @param datos
	 * @return
	 * @throws DAOException
	 */
	public List findDocAprobadoLicencias(Map datos) throws DAOException {
		List lista = new ArrayList();
		java.sql.Date inicio =  new BeanFechaHora(datos.get("ffinicio").toString()).getSQLDate();
                //PAS20171U230200033 - solicitud de reintegro   Correccion del campo ffin
		java.sql.Date  fin =  new BeanFechaHora(datos.get("ffin").toString()).getSQLDate();
		
		try {
			Object obj[] = { datos.get("cod_pers"), inicio,fin, inicio,fin, inicio,fin};
			lista = executeQuery(datasource, FIND_DOC_APROB_LICENCIA.toString(), obj);
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lista;
	}
	
	/**
	 * AGONZALESF - PAS20171U230200001 - solicitud de reintegro  
	 * Funcion para  obtener licencias para interface de documentos aprobados en reintegro	
	 * @param datos
	 * @return
	 * @throws DAOException
	 */
	public List findDocAprobadoLicenciasSubsidios(Map datos) throws DAOException {
		List lista = new ArrayList();
		java.sql.Date inicio =  new BeanFechaHora(datos.get("ffinicio").toString()).getSQLDate();
                //PAS20171U230200033 - solicitud de reintegro   Correccion del campo ffin
		java.sql.Date  fin =  new BeanFechaHora(datos.get("ffin").toString()).getSQLDate();
		
		try {
			Object obj[] = { datos.get("cod_pers"), inicio,fin, inicio,fin};
			lista = executeQuery(datasource, FIND_DOC_APROB_LICENCIA_SUBSIDIOS.toString(), obj);
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lista;
	}
	
	/**
	 * AGONZALESF - PAS20171U230200001 - solicitud de reintegro  
	 * Funcion para verificar existencia de licencia
	 * @param datos
	 * @return
	 * @throws DAOException
	 */
	public List findLicenciaExistentes(Map datos) throws DAOException {
		List lista = new ArrayList();
		java.sql.Date inicio =  new BeanFechaHora(datos.get("ffinicioLic").toString()).getSQLDate();
		java.sql.Date  fin =  new BeanFechaHora(datos.get("ffinLic").toString()).getSQLDate();
		
		try {
			Object obj[] = { datos.get("cod_pers"), inicio,fin };
			lista = executeQuery(datasource, EXISTE_LICENCIA2.toString(), obj);
		} catch (Exception e) {
			throw new DAOException(this, "No se pudo cargar la informacion");
		}
		return lista;
	}
	
		
}
