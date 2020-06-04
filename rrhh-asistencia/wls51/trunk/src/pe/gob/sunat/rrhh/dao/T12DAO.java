package pe.gob.sunat.rrhh.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.normasesoria.siglat.util.Constantes;
import pe.gob.sunat.sol.BeanFechaHora;

/**
 * 
 * Clase : T12DAO
 * Autor : CGARRATT
 * Fecha : 21/11/2005
 */
public class T12DAO extends DAOAbstract {
	
	protected final Log log = LogFactory.getLog(getClass());
	private DataSource dataSource = null;
	
	//JRR - 21/06/2010 - FUSION PROGRAMACION	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	public static final String CRITERIO_COD_UUOO = "0";
	public static final String CRITERIO_DESC_UUOO = "1";
	//
	
	private static final String findByCodUO = "SELECT t12des_uorga, t12cod_nivel FROM t12uorga WHERE t12cod_uorga = ?";
	private static final StringBuffer findByCodUorga = new StringBuffer("select t12cod_uorga, t12des_uorga, ")
			.append("t12des_corta, t12f_vigenci, t12f_baja, t12cod_nivel, t12cod_categ, t12cod_subpr, t12ind_aplic, ")
			.append("t12cod_anter, t12ind_estad, t12cod_jefat, t12cod_encar, t12cod_repor, t12tipo, t12f_graba, ")
			.append("t12cod_user, t12ind_lima , cod_dpto from t12uorga WHERE t12cod_uorga = ? ");
	private static final String findByJefatura = "SELECT t12cod_jefat, t12cod_encar, t12cod_repor FROM t12uorga WHERE t12cod_uorga = ? ";	
	private static final String findByBusquedaGen = "SELECT t12cod_uorga, t12des_uorga, t12des_corta, t12ind_estad from t12uorga ";

	/*Para Sistema SIGLAT*/
	private static final String FIND_BY_UUOO = "SELECT t12cod_uorga, t12des_uorga, t12des_corta, t12ind_estad, t12ind_aplic from t12uorga ";
	/**********************/
	private static final StringBuffer FIND_BY_CODDESC = new StringBuffer(" select t12cod_uorga, t12des_uorga, ")
																 .append(" t12des_corta from t12uorga ")
																 .append(" where t12ind_estad = ? ");
	private static final StringBuffer FIND_UO_JEFE = new StringBuffer("select substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4) as jefefinal, t12cod_uorga,t12des_uorga,t12des_corta, t12cod_jefat, t12cod_encar  ")
			.append(" from t12uorga where t12ind_estad=?  order by 2 asc");
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	private static final StringBuffer FIND_BY_ESTADO = new StringBuffer(" select t12cod_uorga, t12des_uorga, ")
	 .append(" t12des_corta from t12uorga ")
	 .append(" where t12ind_estad = ? order by 1 asc");
	//
	
	private final StringBuffer findByCodSeguridad = new StringBuffer("SELECT t12cod_uorga, nvl(t12des_corta,''), t12des_uorga FROM t12uorga where t12ind_estad = ? ");
	
	
	/* COMSA */
	private StringBuffer QUERY20_SENTENCE = new StringBuffer(" SELECT T12COD_UORGA, T12DES_CORTA,'0' AS NUM_PL_FIJAS ")
	.append(" FROM T12UORGA ")
	.append(" WHERE (T12IND_ESTAD = 1) AND LENGTH(T12COD_UORGA) = 6 AND T12COD_UORGA[3,6] = '0000' ");

	private StringBuffer QUERY30_SENTENCE = new StringBuffer(" SELECT T12COD_UORGA, T12DES_CORTA,'0' AS NUM_PL_FIJAS ")
	.append(" FROM T12UORGA ")
	.append(" WHERE ( T12IND_ESTAD = 1 ) AND  T12COD_UORGA[1,2] = ? ")
	.append(" AND LENGTH(T12COD_UORGA)= 6  ");
	/*    */ 
	
	/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	private StringBuffer FIND_UO_BY_CODUO = new StringBuffer("SELECT t12cod_uorga, t12des_corta, t12cod_nivel ")
	.append("FROM t12uorga ")
	.append("WHERE ")
	.append("t12cod_uorga = ? ");	
	
	private StringBuffer FIND_INTENDENCIAS = new StringBuffer("select t12cod_uorga, t12des_corta, t12cod_nivel ")
	.append("from t12uorga ")
	.append("where t12cod_nivel='1' ")
	.append("and length(t12cod_uorga) = 6 and t12cod_uorga[3,6] = '0000' ")
	.append("order by t12cod_uorga asc ");

	private StringBuffer FIND_UUOO_BY_INTENDENCIA = new StringBuffer("select t12cod_uorga, t12des_corta, t12cod_nivel ")
	.append("from t12uorga ")
	//.append("where t12ind_estad='1' ") //ICAPUNAY 17/02/2012 MOSTRAR LAS UOS INACTIVAS Y ACTIVAS POR SER REPORTES HISTORICOS	
	//.append("and  T12COD_UORGA[1,2]= ? ") //ICAPUNAY 17/02/2012 MOSTRAR LAS UOS INACTIVAS Y ACTIVAS POR SER REPORTES HISTORICOS
	.append("where T12COD_UORGA[1,2]= ? ") //ICAPUNAY 17/02/2012 MOSTRAR LAS UOS INACTIVAS Y ACTIVAS POR SER REPORTES HISTORICOS
	.append("and length(t12cod_uorga) = 6 ")
	//.append("and t12f_baja is null ") //ICAPUNAY 17/02/2012 MOSTRAR LAS UOS INACTIVAS Y ACTIVAS POR SER REPORTES HISTORICOS
	.append("order by t12cod_uorga asc ");
	
	private StringBuffer FIND_INTENDENCIA_BY_CODUO = new StringBuffer("select t12cod_uorga, t12des_corta, t12cod_nivel ")
	.append("from t12uorga ")
	.append("where t12cod_nivel='1' ")
	.append("and length(t12cod_uorga) = 6 and t12cod_uorga[3,6] = '0000' ")
	.append("and t12cod_uorga = ? ");
	/*FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	private StringBuffer FIND_UUOOS_BY_JEFEDELEGADO = new StringBuffer("SELECT t12cod_uorga as coduo, t12des_corta as desuo, t12cod_nivel as nivel, substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4) as codjefe ")
	//.append("FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? and t12ind_estad='1' and t12cod_nivel in ('0','1','2','3','4') ")
	.append("FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? and t12ind_estad='1' and t12cod_nivel in ('0','1','2','3','4','5') ")
	.append("UNION ")
	.append("select d.cunidad_organ as coduo,u.t12des_corta as desuo, u.t12cod_nivel as nivel, substr(trim(nvl(u.t12cod_encar,'')||nvl(u.t12cod_jefat,'')),1,4) as codjefe ")
	.append("from t1595delega d, t12uorga u ")
	//.append("where d.cunidad_organ=u.t12cod_uorga and u.t12ind_estad='1' and u.t12cod_nivel in ('0','1','2','3','4') ")
	.append("where d.cunidad_organ=u.t12cod_uorga and u.t12ind_estad='1' and u.t12cod_nivel in ('0','1','2','3','4','5') ")
	.append("and d.cod_personal_deleg=? ")
	.append("and DATE(?) between d.finivig and d.ffinvig and d.sestado_activo = '1' ")	
	.append("order by nivel asc, coduo asc ");
	
	
	private StringBuffer FIND_UUOOS_FROMNIVEL_BY_JEFEDELEGADO = new StringBuffer("SELECT t12cod_uorga as coduo, t12des_corta as desuo, t12cod_nivel as nivel, substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4) as codjefe ")
	.append("FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? and t12ind_estad='1' and t12cod_nivel in (?) ")
	.append("and t12cod_repor not in (select u.t12cod_uorga from t12uorga u where u.t12ind_estad='1' and u.t12cod_nivel=?) ")
	.append("UNION ")
	.append("select d.cunidad_organ as coduo,u.t12des_corta as desuo, u.t12cod_nivel as nivel, substr(trim(nvl(u.t12cod_encar,'')||nvl(u.t12cod_jefat,'')),1,4) as codjefe ")
	.append("from t1595delega d, t12uorga u ")
	.append("where d.cunidad_organ=u.t12cod_uorga and u.t12ind_estad='1' and u.t12cod_nivel in (?) ")
	.append("and u.t12cod_repor not in (select t12cod_uorga from t12uorga where t12ind_estad='1' and t12cod_nivel=?) ")	
	.append("and d.cod_personal_deleg=? ")
	.append("and DATE(?) between d.finivig and d.ffinvig and d.sestado_activo = '1' ")
	.append("order by nivel asc, coduo asc ");		
	
	private StringBuffer FIND_UUOOS_BY_UO = new StringBuffer("SELECT t12cod_uorga as coduo, t12des_corta as desuo, t12cod_nivel as nivel, substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4) as codjefe ")
	.append("FROM t12uorga where t12cod_uorga like ? and t12cod_uorga<>? and t12ind_estad='1' and t12cod_nivel=? ")
	.append("order by coduo asc ");
	//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
	//ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
	private StringBuffer FIND_UUOOS_FROMNIVEL_BY_REPORTE_JEFE = new StringBuffer("SELECT t12cod_uorga as coduo, t12des_corta as desuo, t12cod_nivel as nivel, substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4) as codjefe ")
	.append("FROM t12uorga where t12ind_estad='1' and t12cod_nivel in (?) ")
	.append("and t12cod_repor in (SELECT t12cod_uorga FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? and t12ind_estad='1') ")
	.append("UNION ")
	.append("SELECT t12cod_uorga as coduo, t12des_corta as desuo, t12cod_nivel as nivel, substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4) as codjefe ")
	.append("FROM t12uorga where t12ind_estad='1' and t12cod_nivel in (?) ")
	.append("and t12cod_repor in (SELECT distinct cunidad_organ FROM t1595delega where cod_personal_deleg=? and sestado_activo='1' and (DATE(?) between finivig and ffinvig)) ")
	.append("order by nivel asc, coduo asc ");
	//FIN ICAPUNAY	
	
	/**
	 * @param datasource Object
	 */
	public T12DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}

//JRR - 21/06/2010 - FUSION PROGRAMACION	
	/**
	 * Metodo encargado de buscar las Intendencias filtradas por un
	 * criterio con un valor determinado.
	 * 
	 * @param datos Map.
	 * @return List
	 * @throws DAOException
	 */
	public List findIntByCodDesc(Map datos) 
	throws DAOException, Exception{
		/**constantes de roles manejados**/
		String ruta_archivo = "/pe/gob/sunat/rrhh/roles.properties";
		Propiedades propiedadesRoles = new Propiedades(this.getClass(),ruta_archivo);
		List dependencias = null;
		String criterio = (String)datos.get("criterio");
		String valor = (String)datos.get("valor");
		Map seguridad = (HashMap)datos.get("seguridad");
		
		StringBuffer strSQL = new StringBuffer("");
		strSQL.append(FIND_BY_CODDESC.toString());
		
		if (CRITERIO_COD_UUOO.equals(criterio)) {
			strSQL.append(" and t12cod_uorga like '").append(valor.trim().toUpperCase())
			.append("%' and t12cod_uorga like '__0000'");
		}
		if (CRITERIO_DESC_UUOO.equals(criterio)) {
			strSQL.append(" and t12des_uorga like '%").append(valor.trim().toUpperCase())
			.append("%' and t12cod_uorga  like '__0000'");
		}
		
		// criterios de visibilidad
		if (seguridad != null) {
			
			HashMap roles = (HashMap) seguridad.get("roles");
			String uoSeg = (String) seguridad.get("uoSeg");
			String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
		    if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
		    if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
			
			if (roles.get(propiedadesRoles.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
				strSQL.append(" and 1=1 ");
			} 
			//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
		    else if (roles.get(propiedadesRoles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null){	
		      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
		      	strSQL.append( " and ((t12cod_uorga like '").append(uoSeg ).append("') ");
		      	strSQL.append( " or (t12cod_uorga in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO ).append("'))) ");		        	
		    }
			//else if (roles.get(propiedadesRoles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
			else if (
			//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
					roles.get(propiedadesRoles.leePropiedad("ROL_JEFE")) != null) {
				strSQL.append( " and t12cod_uorga like '").append(uoSeg ).append("' ");
			} 
		}
		
		if ("2".equals(criterio) || "3".equals(criterio)) {
			strSQL.append(" order by t12cod_uorga");
		} else {
			strSQL.append(" order by t12des_uorga");
		}
		
		if(log.isDebugEnabled()){log.debug("strSQL en T12DAO-->" + strSQL.toString());}
		
		dependencias = executeQuery(dataSource, strSQL.toString(), new Object[] {constantes.leePropiedad("ACTIVO")});
		
		return dependencias;
	}
//	
	
	/**
	 * Metodo findByCodUO: buscar datos de la unidad por codigo de la unidad organizacional
	 * 
	 * @param String coduo
	 * @return Map unidad
	 * @throws DAOException
	 */
    public Map findByCodUO(String coduo) throws DAOException {
            
		Map unidad = new HashMap();
		try {
			unidad = executeQueryUniqueResult(dataSource,findByCodUO, new Object[] {coduo});
			if (unidad!=null && !unidad.isEmpty()) {				
				unidad.put("t12cod_nivel",unidad.get("t12cod_nivel") != null ? unidad.get("t12cod_nivel") : "0");				
			}
    	}
    	catch(Exception e){
            throw new DAOException(this,"No se pudo cargar la informaciÃƒÂ³n de la UUOO");
    	}   
    	return unidad;
    }
    
    
	/**
	 * Metodo findByBusquedaGen
	 * 
	 * @param Map p
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public List findByBusquedaGen(Map params) throws DAOException {
		
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		StringBuffer sSQL = null;

		String pcriterio = (params.get("criterio") != null ) ? params.get("criterio").toString().trim():""; 
		String pvalor = (params.get("valor") != null ) ? params.get("valor").toString().trim():"";
		String pvigente = (params.get("vigente") != null ) ? params.get("vigente").toString().trim():"";
		
		// -- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12cod_uorga like '" + pvalor.replaceAll("'", "''")
			        + "%'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_uorga like '" + pvalor.toUpperCase().replaceAll( "'", "''") + "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_corta like '%"
					+ pvalor.toUpperCase().replaceAll("'", "''") + "%'";
		}

		// -- Check vigente
		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_estad = '" + pvigente.replaceAll("'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";

		sSQL = new StringBuffer(findByBusquedaGen).append(sWhere).append(sOrder);

		List rpta = executeQuery(dataSource, sSQL.toString());
		 
		return rpta;
	}
			
	/**
	 * Metodo encargado de buscar las unidades organizacionales filtradas por un
	 * criterio con un valor determinado.
	 * 
	 * @param params	Map.	Contiene los parametros de la busqueda (
	 * 							criterio: criterio de busqueda, 
	 * 							valor: filtro de la busqueda por t12cod_uorga(0) t12des_uorga(1)
	 * 							orden: campo por el cual ordenar, t12des_uorga(0) t12cod_uorga(1)
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws DAOException
	 */
	public List findByCodDesc(Map params) throws DAOException {

		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String orden = (String) params.get("orden");
		
		List objs = new ArrayList(); 
		
		StringBuffer strSQL = null;
		strSQL = new StringBuffer(FIND_BY_CODDESC.toString());
	
		if (criterio.equals("0")) {
			strSQL.append(" and t12cod_uorga like '").append(valor.trim().toUpperCase()).append("%' ");
		}
		if (criterio.equals("1")) {
			strSQL.append(" and t12des_uorga like '%").append(valor.trim().toUpperCase()).append("%' ");
		}
		
		if (orden == null || orden.trim().equals("0")) {
			strSQL.append(" order by t12des_uorga");
		} else if (orden.trim().equals("1")) {
			strSQL.append(" order by t12cod_uorga");
		}
	
		objs.add("1");			
		
		List unidades = executeQuery(dataSource, strSQL.toString(), objs.toArray());
		
		return unidades;
	}
	
	//dtarazona
	/**
	 * Metodo encargado de buscar las unidades organizacionales activas conjuntamente con su jefe oencargado
	 * .
	 * 
	 * @param params	Map.	Contiene los parametros de la busqueda (
	 * 							criterio: criterio de busqueda, 
	 * 							valor: filtro de la busqueda por t12cod_uorga(0) t12des_uorga(1)
	 * 							orden: campo por el cual ordenar, t12des_uorga(0) t12cod_uorga(1)
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws DAOException
	 */
	public List buscarUODirectivos() throws DAOException {

		List objs = new ArrayList(); 
		log.debug("Entrando a buscarUODirectivos");
		StringBuffer strSQL = null;
		strSQL = new StringBuffer(FIND_UO_JEFE.toString());
		
		objs.add("1");			
		
		List unidades = executeQuery(dataSource, strSQL.toString(), objs.toArray());
		log.debug("Devolver:"+unidades);
		return unidades;
	}
	
	
	/**
	 * Metodo findByCodUorga: busqueda de unidad organizacional por codigo
	 *
	 * @param String codUor
	 * 
	 * @return HashMap hMap
	 * 
	 * @throws DAOException
	 */
	public Map findByCodUorga(String codUor) throws DAOException {
		
		Map hMap = new HashMap();
		List lista = null;		
		
		lista = executeQuery(dataSource, findByCodUorga.toString(), new Object[]{codUor});			
		if (lista.isEmpty() || lista == null) {
			hMap.put("t12cod_uorga", "-");
			hMap.put("t12des_uorga", "Sin descripcion");
			hMap.put("t12des_corta", "S/Codigo");
			hMap.put("t12ind_estad", "-");
			hMap.put("t12cod_jefat", "-");
			hMap.put("t12cod_encar", "-");
		} else {
			hMap = (Map) lista.get(0);
		}			
		
		return hMap;
	}
	
	/**
	 * Metodo findByJefe
	 *	
	 * @param String	cod_uorga
	 * @param String	codReg
	 * @return HashMap hm
	 * @throws DAOException
	 */
	public Map findByJefe(String cod_uorga, String codReg) throws DAOException {
		
		Map hj = findByJefatura(cod_uorga);		
		Map hm = new HashMap();
		
		if ( hj.get("t12cod_jefat").toString().equals(codReg) 
				|| hj.get("t12cod_encar").toString().equals(codReg) ) {
			
			String nUnidad = hj.get("t12cod_repor").toString();			
			if (nUnidad == null || nUnidad.trim().length() < 1) {
				nUnidad = "100000";
				hm = findByJefatura(nUnidad);
			} else {
				hm = this.findByJefe(nUnidad, codReg);
			}
			//hm = findByJefatura(nUnidad);
		} else {
			hm = hj;
		}		
		return hm;
	}
	
	/**
	 * Metodo findByJefatura
	 *
	 * @param String	t12cod_uorga
	 * @return HashMap a
	 * @throws DAOException
	 */
	public Map findByJefatura(String t12cod_uorga) throws DAOException {
		
		Map hjefa = null;
		Map hencar = null;
		String jefat = "";		
		String encar = "";
		String repor = "";
		Map hmapa = new HashMap();

		T02DAO dao = new T02DAO(dataSource);		
		Map map= executeQueryUniqueResult(dataSource, findByJefatura, new Object[]{t12cod_uorga});		
		if(map != null && !map.isEmpty()){
			jefat = (map.get("t12cod_jefat") != null) ? map.get("t12cod_jefat").toString():" ";		
			encar = (map.get("t12cod_encar") != null) ? map.get("t12cod_encar").toString():" ";
			repor = (map.get("t12cod_repor") != null) ? map.get("t12cod_repor").toString():" ";
		}
		/* falta cuando sea la unidad 100000*/
		if (jefat != null && jefat.trim().length()>0) {			
			hjefa = dao.findByCodPers(jefat);
		}			
		if (encar != null && encar.trim().length()>0) {			
			hencar = dao.findByCodPers(encar);
		}		
		//asignando los codigos
		hmapa.put("t12cod_jefat", jefat == null ? " " : jefat);					
		hmapa.put("t12cod_encar", encar == null ? " " : encar);			
		hmapa.put("t12cod_repor", repor == null ? " " : repor);			
		hmapa.put("t12cod_jefat_desc", hjefa == null ? " " : jefat.trim() + "-"
				+ ((String) hjefa.get("t02ap_pate")).trim() + " "
				+ ((String) hjefa.get("t02ap_mate")).trim() + ", "
				+ ((String) hjefa.get("t02nombres")).trim());			
		hmapa.put("t12cod_encar_desc", hencar == null ? " " : encar.trim()
				+ "-"
				+ ((String) hencar.get("t02ap_pate")).trim()
				+ " "
				+ ((String) hencar.get("t02ap_mate")).trim()
				+ ", "
				+ ((String) hencar.get("t02nombres")).trim()
				+ "(Encargado)");
		
		return hmapa;
	}
	
	/**
	 * metodo findByCodSeguridad: metodo para buscar todas las uorganizacionales visibles
	 * de acuerdo al perfil de seguridad de visibilidad de unidades organizacionales 
	 * 
	 * @param Map params
	 * @return List
	 * */
	
	public List findByCodSeguridad(Map params) throws DAOException{
		List lista = null;
		List args = new ArrayList();
		/**constantes de roles manejados**/
		String ruta_archivo = "/pe/gob/sunat/rrhh/roles.properties";
		Propiedades propiedadesRoles = new Propiedades(this.getClass(),ruta_archivo);
		
		StringBuffer sql = new StringBuffer(findByCodSeguridad.toString());
		String criterio = (params.get("criterio") != null) ? params.get("criterio").toString(): "";
		String valor = (params.get("valor") != null) ? params.get("valor").toString(): "";
		String estado = (params.get("estado") != null) ? params.get("estado").toString(): "";
		HashMap seguridad = (HashMap)params.get("seguridad");
		args.add(estado);//estado activo o inactivo de la unidad
		
		//JRR - 19/03/2009
		boolean esAdminCentral = false;
		
		if (criterio.equals("1")) {//por codigo de unidad organizacional
			sql.append(" and t12cod_uorga = '").append(valor.trim().toUpperCase()).append("' ");
		}
		if (criterio.equals("2")) {//por descriocion larga
			sql.append(" and t12des_uorga like '%").append(valor.trim().toUpperCase()).append("%' ");
		}
		if (criterio.equals("3")) {//por codigo de nivel
			sql.append(" and t12cod_nivel in (").append(valor).append(") ");
		}
		if (criterio.equals("4")) {
			String cod_nivel = (String) seguridad.get("cod_nivel");
			sql.append(" and t12cod_uorga like '")
			.append(valor.substring(0, Integer.parseInt(cod_nivel) + 1)).append("%' ");
		}

		// criterios de visibilidad
		if (seguridad != null) {

			HashMap roles = (HashMap) seguridad.get("roles");
			String uoSeg = (String) seguridad.get("uoSeg");
			String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
		    if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
		    if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

			if (roles.get(propiedadesRoles.leePropiedad("ROL_ANALISTA_CENTRAL")) != null) {
				sql.append(" and 1=1 ");
				//JRR
				esAdminCentral = true;
			}
			//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
		    else if (roles.get(propiedadesRoles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null){	
		      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
		      	sql.append( " and ((t12cod_uorga like '").append(uoSeg ).append("') "); //uoSeg viene concatenado con % al final
		      	sql.append( " or (t12cod_uorga in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO ).append("'))) ");		        	
		    }
			//else if (roles.get(propiedadesRoles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
			else if (
			//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
					roles.get(propiedadesRoles.leePropiedad("ROL_JEFE")) != null) {
				sql.append(" and t12cod_uorga like '").append(uoSeg).append("' ");//uoSeg viene concatenado con % al final
			} else {
				sql.append(" and 1=2 ");
			}
		}

		if (criterio.equals("3") || criterio.equals("4") || criterio.equals("5")) {
			sql.append(" order by t12cod_uorga");
		} else {
			sql.append(" order by t12des_uorga");
		}
		
		if(log.isDebugEnabled()) log.debug("sql:"+sql.toString());
		
		//JRR
		if ( (!criterio.equals("5")) || ((criterio.equals("5") && esAdminCentral)) ) {
			lista = executeQuery(dataSource, sql.toString(), args.toArray());
		}			
		
		return lista;
	}
	

	/****************** METODOS COMSA ******************************/	
	
	/**
	 * Metodo que se encarga de obtener la lista de INTENDENCIAS 
	 * de un rango de fechas
	 * @throws SQLException
	 */
	public List obtenListaIntendencias()
	throws DAOException {

		List lista = (ArrayList) executeQuery(dataSource, QUERY20_SENTENCE.toString());

		return lista;
	}
	

	  
	/**
	 * Metodo que se encarga de obtener la lista de UUOO x INTENDENCIA 
	 * de un rango de fechas
	 * @throws SQLException
	 */
	public List obtenListaUUOO(String t12cod_uorga)
	throws DAOException {

		Object[] objs = new Object[] {t12cod_uorga.substring(0,2)};

		List lista = (ArrayList) executeQuery(dataSource, QUERY30_SENTENCE.toString(), objs);

		return lista;
	}
	
	/***************************   ***********************************/
	
	
	/* FUSION PROGRAMACION - JROJAS4 */
	
	/**
	 * Metodo findByBusquedaInt
	 * 
	 * @param Map p
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public List findByBusquedaInt(Map params) throws DAOException {
		
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		StringBuffer sSQL = null;
		
		String pcriterio = (params.get("criterio") != null ) ? params.get("criterio").toString().trim():""; 
		String pvalor = (params.get("valor") != null ) ? params.get("valor").toString().trim():"";
		String pvigente = (params.get("vigente") != null ) ? params.get("vigente").toString().trim():"";
		
		// -- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12cod_uorga like '" + pvalor.replaceAll("'", "''")
			+ "%' and t12cod_uorga like '__0000'";
			
			//+ "%' and substr(t12cod_uorga,3,4) = '0000'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_uorga like '" + pvalor.toUpperCase().replaceAll( "'", "''") + 
			"%' and t12cod_uorga like '__0000'";
			//"%' and substr(t12cod_uorga,3,4) = '0000'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_corta like '%"
				+ pvalor.toUpperCase().replaceAll("'", "''") + 
				"%' and t12cod_uorga like '__0000'";
			//"%' and substr(t12cod_uorga,3,4) = '0000'";
		}
		
		// -- Check vigente
		if (pvigente != null && !pvigente.equals("")) {
			
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_estad = '" + pvigente.replaceAll("'", "''")
			+ "'";
		}
		
		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";
		
		sSQL = new StringBuffer(findByBusquedaGen).append(sWhere).append(sOrder);
		
		List rpta = executeQuery(dataSource, sSQL.toString());
		
		return rpta;
	}
	

	/*             */
	
/***************************   ***********************************/
	/*Para Sistema SIGLAT*/
	/**
	 * Metodo findByUUOO
	 * 
	 * @param Map p
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public List findByUUOO(Map params) throws DAOException {
		
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		StringBuffer sSQL = null;

		String pcriterio = (params.get("criterio") != null ) ? params.get("criterio").toString().trim():""; 
		String pvalor = (params.get("valor") != null ) ? params.get("valor").toString().trim():"";
		String pvigente = (params.get("vigente") != null ) ? params.get("vigente").toString().trim():"";
		String paplic = (params.get("aplic") != null ) ? params.get("aplic").toString().trim():"";
		// -- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "UPPER(t12cod_uorga) like UPPER('" + pvalor.replaceAll("'", "''")
			        + "%')";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "UPPER(t12des_uorga) like UPPER('" + pvalor.replaceAll( "'", "''") + "%')";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "UPPER(t12des_corta) like UPPER('%"
					+ pvalor.replaceAll("'", "''") + "%')";
		}

		// -- Check vigente
		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_estad = '" + pvigente.replaceAll("'", "''")
					+ "'";
		}

		// -- Check aplic
		if (paplic != null && !paplic.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_aplic = '" + paplic.replaceAll("'", "''")
					+ "'";
		}
		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";

		sSQL = new StringBuffer(FIND_BY_UUOO).append(sWhere).append(sOrder);

		List rpta = executeQuery(dataSource, sSQL.toString());
		 
		return rpta;
	}			
	/*Para Sistema SIGLAT*/
	/**
	 * M?do findByComboUUOO
	 * @return
	 */
	public List findByComboUUOO() {
		
		StringBuffer strSQL = new StringBuffer();
		List lista = null;			
			strSQL.append(FIND_BY_UUOO);
			strSQL.append("WHERE t12ind_estad = '")
			.append(Constantes.UUOO_ESTADO_ACTIVO).append("' ");
			strSQL.append("AND t12ind_aplic = '")
			.append(Constantes.UUOO_ESTADO_APLIC).append("' ");
			strSQL.append("ORDER BY t12cod_uorga ");			
		lista = executeQuery(dataSource, strSQL.toString());			
		return lista;
	}	
	
	/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	/**
	 * Metodo que se encarga de buscar una unidad organica activa o inactiva
	 * @param cod_unidad String 
	 * @return unidad List
	 * @throws DAOException
	 */
	public List findUObyCodUo(String cod_unidad) throws DAOException {
		
		List unidad = null;

		unidad = (ArrayList) executeQuery(dataSource,FIND_UO_BY_CODUO.toString(),new Object[]{cod_unidad});

		return unidad;
	}
	
	/**
	 * Metodo que se encarga de obtener la lista de INTENDENCIAS activas e inactivas de sunat	
	 * @throws DAOException
	 */
	public List findIntendencias() throws DAOException {
		
		List intendencias = null;

		intendencias = (ArrayList) executeQuery(dataSource, FIND_INTENDENCIAS.toString());

		return intendencias;
	}
	  
	/**
	 * Metodo que se encarga de obtener la lista de sub unidades organicas x INTENDENCIA incluyendo la misma intendencia	
	 * @throws DAOException
	 */
	public List findUUOOsByIntendencia(String cod_intendencia) throws DAOException {
		
		List uuoos = null;

		Object[] objs = new Object[] {cod_intendencia.substring(0,2)};

		uuoos = (ArrayList) executeQuery(dataSource, FIND_UUOO_BY_INTENDENCIA.toString(), objs);

		return uuoos;
	}	
	
	/**
	  * Metodo que devuelve una unidad organica buscada como intendencia	
	  * @param cod_unidad String 
	  * @return intendencia Map 
	  * @throws DAOException
	  */
	public Map findIntendenciaByCodUO(String cod_unidad) throws DAOException {
		
		Map intendencia= null;
		if (log.isDebugEnabled()) log.debug("T12DAO - findIntendenciaByCodUO");
		if (log.isDebugEnabled()) log.debug("cod_unidad: "+cod_unidad);
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			intendencia = executeQueryUniqueResult(dataSource, FIND_INTENDENCIA_BY_CODUO.toString(),new Object[]{cod_unidad});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [FIND_INTENDENCIA_BY_CODUO]");
		}		
		return intendencia;
	}
	/*FIN ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
	 * Metodo que se encarga de obtener la lista de unidades organicas donde una persona es jefe y/o delegado	
	 * @throws DAOException
	 */
	public ArrayList findUUOOsByJefeDelegado(String registro) throws DAOException {
		
		ArrayList uuoos = null;		
		try{
			Object[] objs = new Object[] {registro,registro,new BeanFechaHora().getSQLDate()};
	
			uuoos = (ArrayList) executeQuery(dataSource, FIND_UUOOS_BY_JEFEDELEGADO.toString(), objs);
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findUUOOsByJefeDelegado]");
		}	
		return uuoos;
	}
	
	
	/**
	 * Metodo que se encarga de obtener la lista de unidades organicas (de determinado nivel que no dependan de otro nivel) donde una persona es jefe y/o delegado	
	 * @throws DAOException
	 */
	public ArrayList findUUOOsFromNivelByJefeDelegado(String registro, String nivel1, String nivel2) throws DAOException {
		
		ArrayList uuoos = null;		
		try{
			Object[] objs = new Object[] {registro,nivel1,nivel2,nivel1,nivel2,registro,new BeanFechaHora().getSQLDate()};			
	
			uuoos = (ArrayList) executeQuery(dataSource, FIND_UUOOS_FROMNIVEL_BY_JEFEDELEGADO.toString(), objs);
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findUUOOsFromNivelByJefeDelegado]");
		}	
		return uuoos;
	}
	
	/**
	 * Metodo que se encarga de obtener la lista de unidades de cierto nivel dependientes de una unidad	
	 * @throws DAOException
	 */
	public ArrayList findUUOOsByUO(String unidadSinCeros, String unidad, String nivel) throws DAOException {
		
		ArrayList uuoos = null;		
		try{
			Object[] objs = new Object[] {unidadSinCeros+"%",unidad,nivel};
	
			uuoos = (ArrayList) executeQuery(dataSource, FIND_UUOOS_BY_UO.toString(), objs);
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findUUOOsByUO]");
		}	
		return uuoos;
	}
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
	//ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
	/**
	 * Metodo que se encarga de obtener la lista de unidades por nivel que reportan a un jefe	
	 * @throws DAOException
	 */
	public ArrayList findUUOOsFromNivelByReporta(String nivel,String jefe) throws DAOException {
		
		ArrayList uuoos = null;		
		try{
			Object[] objs = new Object[] {nivel,jefe,nivel,jefe, new BeanFechaHora().getSQLDate()};
	
			uuoos = (ArrayList) executeQuery(dataSource, FIND_UUOOS_FROMNIVEL_BY_REPORTE_JEFE.toString(), objs);
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findUUOOsFromNivelByReporta]");
		}	
		return uuoos;
	}
	//ICAPUNAY - PAS20171U230300040 - Refrigerio Clima Laboral
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	/**
	 * Metodo encargado de buscar las unidades organizacionales filtradas por estado
	 * 
	 * @param estado String
	 * @return unidades List 
	 * @throws DAOException
	 */
	public List findByEstado(String estado) throws DAOException {
		
		List objs = new ArrayList(); 
		
		StringBuffer strSQL = null;
		strSQL = new StringBuffer(FIND_BY_ESTADO.toString());
		
		objs.add(estado);			
		
		List unidades = executeQuery(dataSource, strSQL.toString(), objs.toArray());
		
		return unidades;
	}
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	
}