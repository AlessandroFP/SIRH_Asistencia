package pe.gob.sunat.rrhh.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;
import pe.gob.sunat.framework.core.pattern.DynaBean;
//import pe.gob.sunat.utils.Constantes;
//import pe.gob.sunat.utils.Utilidades;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.lang.Ordenamiento;
import pe.gob.sunat.framework.util.dao.SQLParser;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.utils.Constantes;

/**
 * 
 * Clase : T02DAO 
 * Autor : CGARRATT 
 * Fecha : 21/11/2005
 * 
 * Descripcion: Esta clase se creo con la finalidad de que se generen todos los
 * metodos y funciones necesarias para poder hacer mantenimiento a la tabla
 * t02perdp de personal.
 */
public class T02DAO extends DAOAbstract {

  protected final Log log = LogFactory.getLog(getClass());

  private DataSource dataSource = null;
  
  private DataSource dataSource_telef = null;

  private static final StringBuffer FINDBYREGISTRO = new StringBuffer("SELECT substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) ")
  .append(" cod_uorg, t02tip_pers, t02cod_ante, t02ap_pate, t02ap_mate, t02nombres, substr(trim(nvl(t02cod_catel,'')||nvl(t02cod_cate,'')),1,2) cod_cate, ")
  .append(" t02ind_aduana, t02lib_elec, ")
  //JRR - 28/04/2011
  .append(" t02cod_rel ")
  .append(" FROM t02perdp WHERE t02cod_pers = ? ");

  private static final StringBuffer findNivelTrabajador =new StringBuffer("SELECT t99tipo, t99descrip FROM t02perdp, t99codigos WHERE t02cod_pers = ? ")
  .append(" and t99cod_tab = '001' and t99tip_desc = 'D' and t99codigo = substr(trim(nvl(t02cod_catel,'')||nvl(t02cod_cate,'')),1,2) ");

  private static final String QRY_T02BY_CODPER = "SELECT t02cod_pers, t02ap_pate, t02ap_mate, t02nombres, t02cod_uorgl, t02cod_uorg, t02cod_cate, t02cod_catel FROM t02perdp ";
  
  /*System DataBase 10/07/2009(SIGLAT). Se agreg� query para b�squeda de personas y UUOO. */
  private static final StringBuffer QRY2_T02BY_CODPER = new StringBuffer()
  .append("SELECT A.t02cod_pers, A.t02ap_pate, A.t02ap_mate, A.t02nombres, ")
  .append("NVL(A.t02cod_uorgl, A.t02cod_uorg) AS t02cod_uorg, ")
  .append("B.t12des_uorga, A.t02cod_cate, A.t02cod_catel ")
  .append("FROM t02perdp A ")
  .append("LEFT OUTER JOIN t12uorga B ")
  .append("ON t02cod_uorg = B.t12cod_uorga ");  

  private static final String findNombreCompleto = "select t02ap_pate, t02ap_mate, t02nombres from t02perdp where t02cod_pers = ? ";

  private static final String findByApellidosNombres= "SELECT t02cod_pers, t02ap_pate, t02ap_mate, t02nombres, t02cod_uorg, t02cod_uorgl FROM t02perdp where t02cod_stat = '1' ";

  /*** agregado 20/04/2007 prac-jcallo **/
  /*** se agrego campo t02cod_ante 18/06/2008 amancill **/
  private static final StringBuffer joinWithT12T99ByCodPers = new StringBuffer("select  p.t02cod_pers, ")
  .append(" p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
  .append(" substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
  .append(" substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
  .append(" p.t02cod_rel, p.t02cod_regl, p.t02f_ingsun, p.t02f_nacim, uo.t12des_uorga, ")
  .append(" substr(trim(nvl(uo.t12cod_encar,'')||nvl(uo.t12cod_jefat,'')),1,4) cod_jefe, ")
  .append(" param.t99descrip, t02cod_stat, t02lib_elec, p.t02cod_stat, p.t02lib_elec, p.t02cod_ante ")
  .append(" from t02perdp p, t12uorga uo, t99codigos param ")
  .append(" where   p.t02cod_pers = ?  ");	
  /** fin agregado 20/04/2007 prac-jcallo **/

  private static final StringBuffer findByCodUorg = new StringBuffer("select t02cod_pers, t02cod_rel, t02ap_pate, ")
  .append("t02ap_mate, t02nombres, t02cod_uorg, t02cod_uorgl, t02cod_cate, t02cod_catel, t02cod_carg, t02lib_elec ")
  .append("from t02perdp ");

  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
  private static final StringBuffer findUOsByJefe = new StringBuffer("SELECT * ")
  .append("FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)=? ")
  .append("and t12ind_estad=? "); 
  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
  
  
  private static final StringBuffer findByCodPers = new StringBuffer("SELECT t02cod_pers, t02ap_pate, t02ap_mate, ")
  .append("t02nombres, t02lib_elec , t02cod_uorg, t02cod_uorgl,")
  .append("t02cod_cate, t02cod_catel, t02cod_stat ")
  .append("FROM t02perdp WHERE t02cod_pers = ? ");


  private static final StringBuffer busquedaAyudas = new StringBuffer("SELECT A.T02COD_PERS, A.T02AP_PATE , A.T02AP_MATE , A.T02NOMBRES , NVL(T02COD_UORGL, T02COD_UORG) AS T02COD_UORG,") 
  .append("(SELECT B.T12DES_UORGA FROM T12UORGA B WHERE NVL(A.T02COD_UORGL, A.T02COD_UORG) = B.T12COD_UORGA) AS T12DES_UORGA, ")
  .append("A.T02COD_CATE, A.T02COD_CATEL, ")
  .append("(SELECT C.T99DESCRIP FROM T99CODIGOS C WHERE C.T99COD_TAB = '001' AND C.T99TIP_DESC = 'D' AND A.T02COD_CATE = C.T99CODIGO) AS T99DESCRIP,") 
  .append("A.T02COD_STAT, A.T02COD_ANTE ")
  .append("FROM T02PERDP A ");

  private static final StringBuffer findByApPaternoAndCodPersonalWithLike = new StringBuffer("SELECT t02cod_pers, t02ap_pate, t02ap_mate, t02nombres FROM t02perdp where t02cod_stat = '1'");  //GRG 11/10/2007
   
  private static final StringBuffer JOIN_WITH_T12T99DETA = new StringBuffer("SELECT t02cod_pers, t02ap_pate, t02ap_mate, t02nombres, t02lib_elec, ") 
																	.append("t02cod_uorg, t02cod_uorgl, t02cod_cate, t02cod_catel, t02cod_stat, ")
																	.append("t02cod_carg, t02lib_elec, t02f_nacim, t02f_ingsun, t02f_cese, ")
																	.append("t02direccion, t02urban, t02refer, ")
																	//JRR - 28/10/2009
																	.append("t02cod_ante, ")
																	//
																	.append("(select uo.t12des_uorga from t12uorga uo ")
																	.append("where uo.t12cod_uorga = p.t02cod_uorg) as t02cod_uorg_desc, ")
																	.append("(select uol.t12des_uorga from t12uorga uol ")
																	.append("where uol.t12cod_uorga = p.t02cod_uorgl) as t02cod_uorgl_desc, ")
																	.append("(select c.t99descrip from t99codigos c ")
																	.append("where c.t99cod_tab = '001' and c.t99tip_desc = 'D' and ")
																	.append("p.t02cod_cate = c.t99codigo) as t02cod_cate_desc, ")
																	.append("(select cl.t99descrip from t99codigos cl ")
																	.append("where cl.t99cod_tab = '001' and cl.t99tip_desc = 'D' and ")
																	.append("p.t02cod_catel = cl.t99codigo) as t02cod_catel_desc, ")
																	.append("(select ca.t99descrip from t99codigos ca ")
																	.append("where ca.t99cod_tab = '022' and ca.t99tip_desc = 'D' and ")
																	.append("p.t02cod_carg = ca.t99codigo) as t02cod_carg_desc, ")
																	.append("(select u.t01des_larga from t01param u ")
																	.append("where u.t01_numero = '700' and u.t01_tipo = 'D' and ")
																	.append("p.t02cod_ubip = u.t01_argumento) as t02cod_ubip_desc ") 
																	.append(", t02cod_rel ") //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
																	.append("FROM t02perdp p WHERE t02cod_pers = ?");
	  

  /*** agregado 08/11/2007 EBV **/
  private static final StringBuffer joinWithT12T99 = new StringBuffer("select  p.t02cod_pers, ")
  .append(" p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
  .append(" substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
  .append(" substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
  .append(" p.t02cod_regl, p.t02f_ingsun, p.t02f_nacim, uo.t12des_uorga, ")
  .append(" substr(trim(nvl(uo.t12cod_encar,'')||nvl(uo.t12cod_jefat,'')),1,4) cod_jefe, ")
  .append(" param.t99descrip, t02cod_stat  from t02perdp p, t12uorga uo, t99codigos param ")
  .append(" where   p.t02cod_pers = ?  and p.t02cod_stat = ? ");	
  /** fin agregado 08/11/2007 EBV **/
  
  /** agregado 15/11/2007 prac-jcallo ***/
  private final StringBuffer updateByRtps = new StringBuffer("update t02perdp set t02ap_pate = ?, ")
  .append("t02ap_mate = ?, t02nombres = ? , t02f_nacim = ?, t02cod_ubip = ?, t02urban = ?, t02direccion = ?, t02refer = ?");
	
  private final StringBuffer insertByRtps = new StringBuffer("insert into t02perdp (t02cod_pers ,t02ap_pate,")
  .append("t02ap_mate, t02nombres , t02f_nacim , t02cod_ubip, t02urban , t02direccion , t02refer");
		
  /** fin agregado **/


  private static final StringBuffer FINDBYREGISTROACTIVO = new StringBuffer("SELECT substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) ")
  .append(" cod_uorg, t02tip_pers, t02cod_ante, t02ap_pate, t02ap_mate, t02nombres, substr(trim(nvl(t02cod_catel,'')||nvl(t02cod_cate,'')),1,2) cod_cate, ")
  .append(" t02ind_aduana, t02lib_elec FROM t02perdp WHERE t02cod_pers = ? and t02cod_stat='1' ");
  
  private final StringBuffer findIngresoPersonal = new StringBuffer(" SELECT t02cod_pers from t02perdp ")
  .append(" where to_char(t02f_ingsun,'%d/%m') = ? and t02cod_stat='1' ")
  .append(" UNION ")
  .append(" SELECT cod_pers as t02cod_pers FROM t1456vacacion_gen ")
  .append(" where to_char(fecha,'%d/%m') = ? and est_id = '1'");
  
  
  /* COMSA */
  private StringBuffer QUERY1_SENTENCE = new StringBuffer(" select t02cod_pers from t02perdp t where t.t02lib_elec =? " );
  
  private StringBuffer COLAB_BY_UO = new StringBuffer(" select p.t02cod_pers, trim(t02ap_pate)||' '||trim(t02ap_mate)||', '||trim(t02nombres) as nombres, substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) as uuoo,t02cod_rel,to_char(t02f_ingsun,'%d/%m/%Y') as t02f_ingsun,t02cod_regl from t02perdp p where substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=? and t02cod_stat='1'" );
  
  private static final StringBuffer FIND_FOR_PADRON = new StringBuffer("SELECT t02cod_pers, t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, ") 
  															 .append("t02cod_uorgl, t02cod_cate, t02cod_catel, t02cod_stat, uo.t12cod_jefat, uo.t12cod_encar ,uo.t12des_corta ") 
  															 .append("from t02perdp p, t12uorga uo ")
  															 .append("where uo.t12cod_uorga = p.t02cod_uorg ");  

  /* FUSION PROGRAMACION - JROJAS4*/  
  
  private static final StringBuffer FINDPERSCONSFALTAPROGVAC = new 
	/* StringBuffer("SELECT V.anno_vac,P.t02cod_pers, P.t02cod_uorg,")
	.append("P.t02ap_pate,P.t02ap_mate,P.t02nombres,P.t02cod_cate,")
	.append("P.t02f_ingsun,P.t02f_cese, T.t12des_corta,")
	.append("V.licencia, V.est_id FROM (t02perdp P " )
	.append("left join t12uorga T on T.t12cod_uorga=P.t02cod_uorg ) ") 
	.append("left outer join t1282vacaciones_d V on P.t02cod_pers=V.cod_pers ")
	.append("WHERE P.t02cod_stat=?  ");*/
  //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
   StringBuffer("SELECT P.t02cod_pers, substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) as t02cod_uorg, ")
	.append("P.t02ap_pate,P.t02ap_mate,P.t02nombres,P.t02cod_cate, ")
	.append("P.t02f_ingsun,P.t02f_cese,T.t12cod_uorga, T.t12des_corta ")
	.append("FROM t02perdp P,t12uorga T  " )
	.append("WHERE substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6)=T.t12cod_uorga ") 
	.append("and P.t02cod_stat=? ") 
	.append("and P.t02f_ingsun<=? ")
	.append("and P.t02cod_rel not in (?) ");
  //FIN
  

  private final static String[] PROPIEDADES = new String[]{"v.anno_vac","v.est_id"};
	
  private static final StringBuffer LISTARVACPROG = new StringBuffer("select ")
	.append(" p.t02cod_pers, p.t02ap_pate, p.t02f_ingsun, ")
	.append("p.t02ap_mate, p.t02nombres, v.anno_vac as periodo, ")
	.append("v.ffinicio as desde, v.ffin as hasta, v.dias as dias, v.observ,")
	.append(" m.descrip, v.licencia, v.u_organ, " )
	.append(" uo.t12des_uorga, uo.t12des_corta, p.t02cod_cate, v.est_id ")
	.append(" from	t1282vacaciones_d v, t1279tipo_mov m, ")
	.append(" t02perdp p,t12uorga uo ")
	.append(" where m.mov = v.licencia  and " )
	//.append(" ( (v.licencia = '49' and v.observ <> 'ART 23 DL 713')  or v.licencia = '64' ) ")
	.append(" ( v.licencia = '49' or v.licencia = '64' ) ")	
	//.append(" and p.t02cod_uorg=uo.t12cod_uorga " ) //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
	.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=uo.t12cod_uorga " ) //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro	
	.append( " and v.cod_pers = p.t02cod_pers  and v.est_id!='0' and p.t02cod_stat = '1' and p.t02cod_rel<>'10' " );
  
  /*          */  
  
  private StringBuffer QUERY2_SENTENCE = new StringBuffer(" SELECT " )
  .append("T02COD_PERS, T02AP_PATE, T02AP_MATE, T02NOMBRES, T02F_INGSUN, SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) t02cod_uorg, T02COD_REL " )  
  //ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambien para todos los regimenes pero solo por el criterio REGISTRO)
  //.append("FROM T02PERDP " )
  //.append("WHERE T02COD_STAT = '1' " );
  .append("FROM T02PERDP " );
  //FIN ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambien para todos los regimenes pero solo por el criterio REGISTRO)
  
  //FIN
  
  
  //ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION ASISTENCIA
  private StringBuffer QUERY_CALIFICACION = new StringBuffer(" SELECT " )
  .append(" T02COD_PERS, T02AP_PATE, T02AP_MATE, T02NOMBRES, T02F_INGSUN, SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) t02cod_uorg, T02COD_REL " )  
  .append(" FROM T02PERDP " );
  //FIN ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION ASISTENCIA

  
  /* JRR - AUTORIZACION BATCH - 08/03/2011 */  
  private final StringBuffer FINDBYCESADOS = new StringBuffer("SELECT ")
	.append(" t02cod_pers from t02perdp where t02f_cese BETWEEN ? and ? ");	 
  /*          */  
  
  
  /* JRR - 06/04/2011 - RECUPERACION DE FUENTES - Decompilados */
  private StringBuffer QUERY3_SENTENCE = new StringBuffer("SELECT ")
  	.append("P.t02cod_pers,P.t02ap_pate,P.t02ap_mate,P.t02nombres,P.t02f_nacim,")
  	.append("P.t02direccion,P.t02urban,P.t02cod_ubip,P.t02refer,P.t02cod_uorg,")
  	.append("P.t02cod_uorgl,P.t02cod_cate,P.t02cod_catel,")
  	.append("PE.t03sexo,PE.t03est_civ,T.cod_nacionalidad,T.cod_sede, ")
  	.append("EX.cnt_anhos_act as cnt_anhos,EX.cnt_mes_act as cnt_mes,EX.cnt_dias_act as cnt_dias ")
  	.append("FROM T02PERDP P ")
  	.append("LEFT JOIN T03PERDS PE ON P.t02cod_pers=PE.t03cod_pers ")
  	.append("LEFT JOIN T3335RTPSTRABAJADO T ON P.t02cod_pers=T.cod_personal ")
  	.append("AND T.IND_ESTADO='A' AND T.IND_DEL='0' ")
  	.append("LEFT JOIN T3376EXT_PADRON EX ON P.t02cod_pers=EX.cod_personal ")
  	.append("AND EX.IND_DEL='1' ")
  	.append("WHERE P.t02cod_pers=? ");
  
  Propiedades constantes = new Propiedades(getClass(), "/pe/gob/sunat/rrhh/rrhh-bienestar.properties");
  
  private static final String[] PROPIEDADES_2 = { "t02cod_pers", "t02ap_pate", "t02ap_mate", "t02nombres", "t02cod_uorg" };
  
  private StringBuffer QUERY4_SENTENCE = new StringBuffer(" SELECT ")
  	.append(" t.t02ap_pate, ")
  	.append(" t.t02ap_mate, ")
  	.append(" t.t02nombres, ")
  	.append(" r.cod_sede ")
  	.append(" from t02perdp t ")
  	.append(" left join t3335rtpstrabajado r ")
  	.append(" on t.t02cod_pers = r.cod_personal ")
  	.append(" and r.ind_estado = ? ")
  	.append(" and r.ind_del= ? ")
  	.append(" where t.t02cod_pers = ? ");
  
  private StringBuffer QUERY2A_SENTENCE = new StringBuffer(" SELECT t02cod_pers,t02ap_pate, ")
  	.append("t02ap_mate,t02nombres,t02direccion,t02cod_ubip,t02cod_uorg,t02cod_uorgl, ")
  	.append("t02cod_catel,t02cod_cate FROM t02perdp A WHERE ");
  
  /*         */ 
  
  /*ICAPUNAY - PASE PAS20112A550001380 Modificacion de alertas de solicitudes y movimientos de asistencia - 06/03/2012 */
  private StringBuffer QUERY_T02PERDP = new StringBuffer(" SELECT " )
  .append("T02COD_PERS cod_pers, T02AP_PATE, T02AP_MATE, T02NOMBRES, T02F_INGSUN, SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) t02cod_uorg, t02cod_rel " )
  .append("FROM T02PERDP " ); 
  /*FIN ICAPUNAY - PASE PAS20112A550001380 Modificacion de alertas de solicitudes y movimientos de asistencia - 06/03/2012 */
  
  // PAS20171U230200001 - solicitud de reintegro  
  private final StringBuffer FIND_BY_PK_CESADO = new StringBuffer("SELECT ")
	.append(" t02cod_pers from t02perdp where t02cod_pers = ? and t02f_cese < ?  ");	 
  
  /**
   * Dentro de este archivo de propiedades se han definido los roles del sistema.
   */
  private static final String ROLES_PROPERTIES_FILENAME = "/pe/gob/sunat/rrhh/roles.properties";
  private static Propiedades propiedades_roles = new Propiedades(
      T02DAO.class, ROLES_PROPERTIES_FILENAME);

  
  /**
	 * beanMapper utilizado para cargar el listado de telefonos de un usuario
	 * 
	 * */
	public BeanMapper beanMapperTelf = new BeanMapper(){
		public Object setear(ResultSet r, Map mapa)throws SQLException{
			MensajeBean msg = new MensajeBean();
			try{				
				Set campos = mapa.keySet();
				String cod_pers = mapa.get("t02cod_pers") != null ? mapa.get("t02cod_pers").toString().trim():"";
				TelefonoDAO telefDao = new TelefonoDAO(dataSource_telef);
				if(campos.contains("t02cod_pers")){
					if (cod_pers != null && cod_pers.length()>0 ) {
						mapa.put("lst_telefonos", telefDao.findTelefonos(cod_pers));
					}
				}
				
			}catch (Exception e) {
				log.error("*** ERROR *** : ", e);
				msg.setMensajeerror("Ha ocurrido un error al buscar".concat(e.getMessage()));
				msg.setMensajesol("Por favor, intente nuevamente realizar la "
		    			.concat("operacion, de continuar con el problema ")
		    			.concat("comuniquese con el webmaster"));
		    	throw new DAOException (this, msg);
			}
			return mapa;
		}		
	};
	
	
  /**
   * @param datasource Object
   */
  public T02DAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.dataSource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.dataSource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }

  public T02DAO() {
    // TODO Auto-generated constructor stub
  }

  public T02DAO(String jndi){
      dataSource = null;
      dataSource = getDataSource(jndi);
  }  
  
  public T02DAO(Map sources){
      dataSource = (DataSource) sources.get("datasource");
      
      if (sources.get("datasource_telef")!=null){
    	  dataSource_telef = (DataSource) sources.get("datasource_telef");
      }
  }  
  
  /**
   * 
   * @param registro
   * @return Map
   * @throws DAOException
   */
  public Map findByRegistro(String registro) throws DAOException {

    Map mapa = new HashMap();
    try {
      mapa = executeQueryUniqueResult(dataSource, FINDBYREGISTRO.toString(), new Object[] { registro });
      if(log.isDebugEnabled())log.debug("codigo registro = "+registro);
      if (mapa != null && !mapa.isEmpty()) {
        mapa.put("t02cod_pers", registro);
      }
    } catch (Exception e) {
      throw new DAOException(this,
      "No se pudo cargar la informacion de personal");
    }
    return mapa;
  }

  /**
   * 
   * @param registro
   * @return Map
   * @throws DAOException
   */
  public Map findNivelTrabajador(String registro) throws DAOException {

    Map mapa = new HashMap();
    try {

      mapa = executeQueryUniqueResult(dataSource, findNivelTrabajador.toString(), new Object[] { registro });
      if (mapa != null && !mapa.isEmpty()) {
        mapa.put("desc_cate", mapa.get("t99descrip") != null ? mapa.get("t99descrip") : "-");
        mapa.put("nivel_func", mapa.get("t99tipo") != null ? mapa.get("t99tipo") : "");
      }

    } catch (Exception e) {
      throw new DAOException(this, "No se pudo cargar la información del nivel del trabajador");
    }
    return mapa;
  }

  /**
   * 
   * @param params
   *            DynaDTO
   * @return Map
   */
  public Map findByCodPersona(Map params){
    Map r = new HashMap();
    MensajeBean msg = new MensajeBean();
    try {
      String condicion = "";
      List objs = new ArrayList();
      String codPersona = (String) params.get("cod_persona");
      if (codPersona != null && codPersona.length() != 0) {
        condicion += " WHERE t02cod_pers = ?";
        objs.add(codPersona);
      }

      if(log.isDebugEnabled())log.debug(QRY_T02BY_CODPER.concat(condicion) + " " + objs.size());
      for (int i = 0; i < objs.size(); i++)
    	  if(log.isDebugEnabled())log.debug(objs.get(i));

      r = executeQueryUniqueResult(dataSource, QRY_T02BY_CODPER
          .concat(condicion), objs.toArray());

    } catch (DAOException e) {
      msg.setMensajeerror("Ha ocurrido un error al acceder a los datos de Persona Natural : ".concat(e.getMessage()));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException(this, msg);
    }
    return r;
  }

  /**
   * 
   * @param params
   *            DynaDTO
   * @return Map
   */
  public Map findByUUOOCodPersona(Map params){
    Map r = new HashMap();
    MensajeBean msg = new MensajeBean();
    StringBuffer strSQL = new StringBuffer();
    try {
      String condicion = "";
      List objs = new ArrayList();
      String codPersona = (String) params.get("cod_persona");
      if (codPersona != null && codPersona.length() != 0) {
        condicion += " WHERE t02cod_pers = ?";
        objs.add(codPersona);
      }
      strSQL.append(QRY2_T02BY_CODPER);
      strSQL.append(condicion);
      
      if(log.isDebugEnabled())log.debug(strSQL.toString() + " " + objs.size());
      for (int i = 0; i < objs.size(); i++)
    	  if(log.isDebugEnabled())log.debug(objs.get(i));

      r = executeQueryUniqueResult(dataSource, strSQL.toString(), objs.toArray());

    } catch (DAOException e) {
      msg.setMensajeerror("Ha ocurrido un error al acceder a los datos de Persona Natural : ".concat(e.getMessage()));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException(this, msg);
    }
    return r;
  }
  
  public String findNombreCompleto(String codPers){
    StringBuffer nombre = new StringBuffer("");
    Map datos = executeQueryUniqueResult(dataSource,findNombreCompleto,new Object[]{codPers});

    if (datos!=null){
      nombre.append(((String)datos.get("t02ap_pate")).trim()).append(" ");
      nombre.append(((String)datos.get("t02ap_mate")).trim()).append(", ");
      nombre.append(((String)datos.get("t02nombres")).trim()).append(" ");
    }   		
    return nombre.toString();
  }

  /**
   *
   * Este metodo retorna un listado de las Personas contenidos
   * en objetos Map, segun los parametros enviados.
   * 
   * claves que pueden venir en params:
   * <ul>
   * <li>t02ap_pate
   * <li>t02ap_mate
   * <li>t02nombres
   * </ul>
   * 
   * @param params DynaBean
   * 
   * @return List
   *    Contenido: Una Lista con Map's conteniendo los siguientes valores:
   *       t02cod_pers, t02ap_pate, t02ap_mate, t02nombres, t02cod_uorg, t02cod_uorgl
   * @throws DAOException
   */
  public List findByApellidosNombres( DynaBean params) throws DAOException {
    List r = new ArrayList();
    MensajeBean msg = new MensajeBean();

    try {
      String condicion = "";
      List objs = new ArrayList();
      if(params.isSet("t02ap_pate"))
      {
        condicion = condicion + " AND t02ap_pate = ? ";
        objs.add(params.getString("t02ap_pate"));
      }
      if(params.isSet("t02ap_mate"))
      {
        condicion = condicion + " AND t02ap_mate = ? ";
        objs.add(params.getString("t02ap_mate"));
      }
      if(params.isSet("t02nombres"))
      {
        condicion = condicion + " AND t02nombres = ? ";
        objs.add(params.getString("t02nombres"));
      }


      if(log.isDebugEnabled())log.debug(findByApellidosNombres.concat( condicion ) + " " + objs.size());
      for (int i=0;i<objs.size();i++)
    	  if(log.isDebugEnabled())log.debug(objs.get(i));

      r = executeQuery( dataSource, findByApellidosNombres.concat( condicion ), objs.toArray());
    } catch (DAOException e){
      msg.setMensajeerror("Ha ocurrido un error al acceder a los datos de Ejemplo : ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);
    }
    return r;
  }

  /*** agregado 20/04/2007 prac-jcallo **/	
  /**
   * Metodo encargado de buscar los datos de un determinado trabajador.
   * 
   * @param codPers       String. Numero de registro del trabajador.
   * @param seguridad     Map. Numero de registro del trabajador.
   * 
   * @return HashMap 		conteniendo el registro obtenido.
   * 
   * @throws DAOException
   */   
  public HashMap joinWithT12T99ByCodPers(String codPers, Map seguridad) 
  throws DAOException {

    StringBuffer strSQL = null;
    strSQL = new StringBuffer(joinWithT12T99ByCodPers.toString());
    List res= new ArrayList();
    HashMap a = new HashMap();
    List objs = new ArrayList();

    //criterios de visibilidad
    if (seguridad != null) {
      HashMap roles = (HashMap) seguridad.get("roles");
      String uoSeg = (String) seguridad.get("uoSeg");
      String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
      if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
      if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

      if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_CENTRAL")) != null){
        strSQL = strSQL.append(" and 1=1 ");
      }
      //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
      else if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null){	
      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
      	strSQL = strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append("') ");
      	strSQL = strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append("'))) ");		        	
      }
      //else if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
      else if (
      //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
          roles.get(propiedades_roles.leePropiedad("ROL_SECRETARIA")) != null ||
          roles.get(propiedades_roles.leePropiedad("ROL_JEFE")) != null
          //JRR - 22/09/2009
          || roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_LEXC")) != null
          //
      ) {
        strSQL = strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append("' ");
      } else {
        strSQL = strSQL.append(" and 1=2 ");	
      }               
    }

    strSQL = strSQL.append(" and param.t99cod_tab = ? ")
    .append(" and param.t99tip_desc = ? ")
    .append(" and substr(trim(nvl(p.t02cod_uorgl,'')")
    .append("||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
    .append(" and substr(trim(nvl(p.t02cod_catel,'')")
    .append("||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

    objs.add(codPers.toUpperCase());
    objs.add("001");
    objs.add("D");

    if(log.isDebugEnabled())log.debug("query t02 "+ strSQL.toString());
    if(log.isDebugEnabled())log.debug("query objs "+ objs.toArray());
    res=executeQuery(dataSource, strSQL.toString(), objs.toArray());
    if (res.size()>0){
      a=(HashMap)res.get(0);
    }
    if(log.isDebugEnabled())log.debug("a "+ a);
    return a;
  }

  /**
   * Metodo encargado de buscar los datos los trabajadores que pertenezcan a
   * una unidad organizacional.
   * 
   * @param params		Map. Numero de registro del trabajador.	 * 
   * @return ArrayList 	conteniendo el registro obtenido.	 * 
   * @throws DAOException
   */
  public ArrayList joinWithT12T99ByNombreCodUO(Map params)
  throws DAOException {

    MensajeBean msg = new MensajeBean();
    ArrayList lista = new ArrayList();		
    StringBuffer sSQL = null;
    try {		
      String nombre = (String) params.get("nombre");
      String codUO = (String) params.get("codUO");

      sSQL = new StringBuffer("select p.t02cod_pers, p.t02ap_pate, p.t02ap_mate,")
      .append(" p.t02nombres, p.t02cod_uorg, p.t02cod_cate, ")
      .append("uo.t12des_uorga, param.t99descrip ")
      .append(" from   t02perdp p, t12uorga uo, t99codigos param  ")
      .append(" where  ( upper(trim(p.t02nombres))||' '||upper(trim(p.t02ap_pate))")
      .append("||' '||upper(trim(p.t02ap_mate)) like '")
      .append(nombre.trim().toUpperCase())
      .append("%')");

      /**verificar si es que tiene codigo de de organizacion**/

      if(codUO !=null && codUO!="" && codUO.trim().length()>0){				
        sSQL = sSQL.append(" and p.t02cod_uorg like '%")
        .append(codUO.trim().toUpperCase())
        .append("%' ");
      }

      sSQL = sSQL.append(" and p.t02cod_uorg = uo.t12cod_uorga ")
      .append(" and param.t99cod_tab='001' ")
      .append(" and param.t99tip_desc='D'")
      .append(" and p.t02cod_cate = param.t99codigo ")
      .append(" order by p.t02nombres, p.t02ap_pate, p.t02ap_mate");
      if(log.isDebugEnabled())log.debug("querry : "+sSQL);
      lista = (ArrayList) executeQuery(dataSource, sSQL.toString());

    } catch (DAOException e) {
      log.error("*** ERROR *** : ", e);
      msg.setMensajeerror("Ha ocurrido un error al acceder a los datos de "
          .concat("trabajador con unidad organizacional : ").concat(e.getMessage()));
      msg.setMensajesol("Por favor, intente nuevamente realizar la "
          .concat("operacion, de continuar con el problema ")
          .concat("comuniquese con el webmaster"));
      throw new DAOException (this, msg);
    } finally {

    }
    return lista;
  }

  /**
   * Metodo busqueda de datos personals por codigo de registro
   *
   * @param String codPers
   * 
   * @return HashMap hMap
   * 
   * @throws DAOException
   */
  public HashMap findByCodPers(String codPers)
  throws DAOException {

    MensajeBean msg = new MensajeBean();
    HashMap hMap = new HashMap();
    List lista = null;
    List objs = new ArrayList();

    try {		
      objs.add(codPers);

      lista = executeQuery(dataSource, findByCodPers.toString(), objs.toArray()); 

      if (lista.isEmpty() || lista == null) {
        hMap.put("t02cod_pers", "-");
        hMap.put("t02ap_pate", "S/descripcion");
        hMap.put("t02ap_mate", "-");
        hMap.put("t02nombres", "-");
        hMap.put("t02lib_elec", "-");
        hMap.put("t02cod_uorg", "-");
        hMap.put("t02cod_uorgl", "-");
        hMap.put("t02cod_cate", "-");
        hMap.put("t02cod_catel", "-");
        hMap.put("t02cod_stat", "-");
      } else {
        hMap = (HashMap) lista.get(0);
      }

    } catch (DAOException e) {
      log.error("*** ERROR *** : ", e);
      msg.setMensajeerror("Ha ocurrido un error al acceder a los datos de "
          .concat("trabajador con unidad organizacional : ").concat(e.getMessage()));
      msg.setMensajesol("Por favor, intente nuevamente realizar la "
          .concat("operacion, de continuar con el problema ")
          .concat("comuniquese con el webmaster"));
      throw new DAOException (this, msg);
    } finally {

    }
    return hMap;
  }

  /**
   * Metodo que se encarga de la busqueda de los registros de los
   * trabajadores que pertenecen a una determinada unidad organizacional.
   * 
   * @param params	Map. parametros (codUorg) y (estado)
   * @return          ArrayList conteniendo los registros cargados
   *                  en HashMaps.
   * @throws DAOException
   * */
  public ArrayList findByCodUorg(Map params) throws DAOException {

    ArrayList listaPersonal = new ArrayList();
    //Utilidades common = new Utilidades();
    MensajeBean msg = new MensajeBean();

    String codUorg = "";
    String estado = "";
    String sWhere = "";
    String sSQL = "";
    boolean flag = false;

    try {	      
      codUorg = (params.get("codUorg") == null )? "" :params.get("codUorg").toString().trim();
      estado = (params.get("estado") == null )? "" :params.get("estado").toString().trim();

      if(codUorg !=null && codUorg.length() > 0){
        sWhere = sWhere.concat(" t02cod_uorg = '" + codUorg.replaceAll("'", "''") +"'");
        flag = true;
      }	    	
      if(estado !=null && estado.length() > 0){
        if(flag) {
          sWhere = sWhere.concat(" and ");
        }
        sWhere = sWhere.concat(" t02cod_stat = '"+estado.replaceAll("'", "''")+"'");
        flag = true;
      }
      if(flag){
        sWhere = " where "+ sWhere;
      }

      sSQL = findByCodUorg.toString() + sWhere + " order by t02cod_pers ";
      if(log.isDebugEnabled())log.debug(" SQL => " + sSQL);
      listaPersonal = (ArrayList) executeQuery(dataSource, sSQL);
    } catch (DAOException e) {
      log.error("*** ERROR *** : ", e);
      msg.setMensajeerror("Ha ocurrido un error al buscar".concat(e.getMessage()));
      msg.setMensajesol("Por favor, intente nuevamente realizar la "
          .concat("operacion, de continuar con el problema ")
          .concat("comuniquese con el webmaster"));
      throw new DAOException (this, msg);
    } finally {

    }
    return listaPersonal;
  }
  /*** fin 03/05/2007 prac-jcallo ***/


  /**
   * Mï¿½todo busqueda por nombres y codigo de unidad organizacional
   * 
   * @param HashMap
   *            p
   * @return ArrayList aLRpta
   * @throws IncompleteConversationalState
   */
  public List busquedaAyudas(Map p){
    //Utilidades common = new Utilidades();
    StringBuffer sWhere = null;
    boolean hasParam = false;
    StringBuffer sSQL = null;

    String pt02cod_pers = "";
    String pt02ap_pate = "";
    String pt02ap_mate = "";
    String pt02nombres = "";
    String pt02cod_uorg = "";
    String pt02cod_cate = "";
    String pt02cod_stat = "";

    // Build WHERE statement

    //-- Check reg

    sWhere = new StringBuffer("");

    pt02cod_pers = p.get("t02cod_pers")!=null?((String) p.get("t02cod_pers")).trim():"";
    if (!pt02cod_pers.equals("")) {

      if (!sWhere.toString().equals("")){
        sWhere.append(" and ");
      }
      hasParam = true;
      sWhere.append("a.t02cod_pers = '")
      .append(pt02cod_pers.replaceAll( "'", "''")).append("'");
    }

    //-- Check appat

    pt02ap_pate =  p.get("t02ap_pate")!=null?((String) p.get("t02ap_pate")).trim():"";
    if (!pt02ap_pate.equals("")) {

      if (hasParam){
        sWhere.append(" and ");
      }
      hasParam = true;
      sWhere.append("a.t02ap_pate like '")
      .append(pt02ap_pate.replaceAll("'", "''")).append("%'");
    }

    //-- Check apmat

    pt02ap_mate = p.get("t02ap_mate")!=null?((String) p.get("t02ap_mate")).trim():"";
    if (!pt02ap_mate.equals("")) {

      if (hasParam){
        sWhere.append(" and ");
      }
      hasParam = true;
      sWhere.append("a.t02ap_mate like '")
      .append(pt02ap_mate.replaceAll("'", "''")).append("%'");
    }

    //-- Check nomb

    pt02nombres = p.get("t02nombres")!=null?((String) p.get("t02nombres")).trim():"";
    if (!pt02nombres.equals("")) {

      if (hasParam){
        sWhere.append(" and ");
      }
      hasParam = true;
      sWhere.append("a.t02nombres like '%")
      .append(pt02nombres.replaceAll("'", "''")).append("%'");
    }

    //-- Check uorg

    pt02cod_uorg = p.get("t02cod_uorg")!=null?((String) p.get("t02cod_uorg")).trim():"";
    if (!pt02cod_uorg.equals("")) {

      if (hasParam){
        sWhere.append(" and ");
      }
      hasParam = true;
      sWhere.append("a.t02cod_uorg = '")
      .append(pt02cod_uorg.replaceAll( "'", "''")).append("'");
    }
    //-- Check cate

    pt02cod_cate = p.get("t02cod_cate")!=null?((String) p.get("t02cod_cate")).trim():"";
    if (!pt02cod_cate.equals("")) {

      if (hasParam){
        sWhere.append(" and ");
      }
      hasParam = true;
      sWhere.append("a.t02cod_cate = '")
      .append(pt02cod_cate.replaceAll("'", "''")).append("'");
    }

    //-- Check check

    pt02cod_stat = p.get("t02cod_stat")!=null?((String) p.get("t02cod_stat")).trim():"";
    if (!pt02cod_stat.equals("")) {

      if (hasParam){
        sWhere.append(" and ");
      }
      hasParam = true;
      sWhere.append("a.t02cod_stat = '")
      .append(pt02cod_stat.replaceAll("'", "''")).append("'");
    }

    // Build full SQL statement

    sSQL = new StringBuffer(busquedaAyudas.toString());

    if (hasParam){
      sSQL.append(" WHERE ").append(sWhere.toString());
    }

    sSQL.append(" order by a.t02ap_pate, a.t02ap_mate");

    return executeQuery(dataSource, sSQL.toString());

  }
  
  /*** GRG 11/10/2007 ***/
  /**
  *
  * Este metodo retorna un listado de las Personas contenidos
  * en objetos Map, segun los parametros enviados.
  * Realiza las bÃºsquedas utilizando "like"
  * 
  * claves que pueden venir en params:
  * <ul>
  * <li>t02ap_pate
  * <li>t02cod_pers
  * </ul>
  * 
  * @param params DynaBean
  * 
  * @return List
  *    Contenido: Una Lista con Map's conteniendo los siguientes valores:
  *       t02cod_pers, t02ap_pate, t02ap_mate, t02nombres
  * @throws DAOException
  */
  public List findByApPaternoAndCodPersonalWithLike(DynaBean params) throws DAOException {
     List r = new ArrayList();
     MensajeBean msg = new MensajeBean();
     try { 
         String condicion = "";
         List objs = new ArrayList();
         if(params.isSet("t02ap_pate"))
         {
             condicion = condicion + " AND t02ap_pate LIKE ? ";
             objs.add(params.getString("t02ap_pate"));
         } 
         if(params.isSet("t02cod_pers"))
         {
             condicion = condicion + " AND t02cod_pers LIKE ? ";
             objs.add(params.getString("t02cod_pers"));
         }

         if(log.isDebugEnabled())log.debug(findByApPaternoAndCodPersonalWithLike.append( condicion ) + " " + objs.size());
         for (int i=0;i<objs.size();i++)
        	 if(log.isDebugEnabled())log.debug(objs.get(i));
         r = executeQuery( dataSource, findByApPaternoAndCodPersonalWithLike.append( condicion ).toString(), objs.toArray());
     } catch (DAOException e){
         log.error("*** ERROR *** : ", e);
         msg.setMensajeerror("Ha ocurrido un error al acceder a los datos de la tabla t02perdp en el metodo findByApPaternoAndCodPersonalWithLike : ".concat( e.getMessage() ));
         msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
         throw new DAOException (this, msg);
       }
     return r;
  }

  /*** agregado 08/11/2007 EBV **/	
  /**
   * Metodo encargado de buscar los datos de un determinado trabajador (Unicamente Activo).
   * 
   * @param codPers       String. Numero de registro del trabajador.
   * @param seguridad     Map. Numero de registro del trabajador.
   * 
   * @return HashMap 		conteniendo el registro obtenido.
   * 
   * @throws DAOException
   */   
  public HashMap joinWithT12T99(String codPers, Map seguridad) 
  throws DAOException {

    StringBuffer strSQL = null;
    strSQL = new StringBuffer(joinWithT12T99.toString());
    List res= new ArrayList();
    HashMap a = new HashMap();
    List objs = new ArrayList();
    if(log.isDebugEnabled()) log.debug("joinWithT12T99 ingreso");

    //criterios de visibilidad
    if (seguridad != null) {
      HashMap roles = (HashMap) seguridad.get("roles");
      String uoSeg = (String) seguridad.get("uoSeg");
      String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
      if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
      if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

      if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_CENTRAL")) != null){
        strSQL = strSQL.append(" and 1=1 ");
      }
      //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
      else if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null){	
      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
      	strSQL = strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append("') ");
      	strSQL = strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append("'))) ");		        	
      }
      //else if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
      else if (
      //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
          roles.get(propiedades_roles.leePropiedad("ROL_SECRETARIA")) != null ||
          roles.get(propiedades_roles.leePropiedad("ROL_JEFE")) != null
          //JRR - 22/09/2009
          || roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_LEXC")) != null
          //
      ) {
        strSQL = strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append("' ");
      } else {
        strSQL = strSQL.append(" and 1=2 ");	
      }               
    }

    strSQL = strSQL.append(" and param.t99cod_tab = ? ")
    .append(" and param.t99tip_desc = ? ")
    .append(" and substr(trim(nvl(p.t02cod_uorgl,'')")
    .append("||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
    .append(" and substr(trim(nvl(p.t02cod_catel,'')")
    .append("||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

    objs.add(codPers.toUpperCase());
    objs.add("1");
    objs.add("001");
    objs.add("D");

    if(log.isDebugEnabled()) log.debug("query t02 "+ strSQL.toString());
    if(log.isDebugEnabled()) log.debug("query objs "+ objs.toArray());
    res=executeQuery(dataSource, strSQL.toString(), objs.toArray());
    if (res.size()>0){
      a=(HashMap)res.get(0);
    }
    if(log.isDebugEnabled()) log.debug("a "+ a);
    return a;
  }
  
  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
  /**
   * Metodo encargado de buscar los datos de un determinado trabajador activo (Si es perfil SIRH JEFE busca al trabajador dentro de la unidades donde el perfil sea jefe).
   * 
   * @param codPers       Numero de registro del trabajador.
   * @param seguridad     Datos del usuario logueado.
   * 
   * @return HashMap 	  Datos del registro obtenido.
   * 
   * @throws DAOException
   */   
  public HashMap joinWithT12T99_ByAllUUOOsJefe(String codPers, Map seguridad) 
  throws DAOException {

    StringBuffer strSQL = null;
    strSQL = new StringBuffer(joinWithT12T99.toString());
    List res= new ArrayList();
    List resUOsJefe= new ArrayList();
    HashMap a = new HashMap();
    HashMap uoMap = new HashMap();
    String codUoJef = "";
    List objs = new ArrayList();
    List objsJefe = new ArrayList();

    //criterios de visibilidad
    if (seguridad != null) {
      HashMap roles = (HashMap) seguridad.get("roles");
      String codPersUsuario = ((String) seguridad.get("codPers")).trim();
      String uoSeg = (String) seguridad.get("uoSeg");
      String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
      if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
      if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

      if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_CENTRAL")) != null){
        strSQL = strSQL.append(" and 1=1 ");
      }
      //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
      else if (roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_OPERATIVO")) != null){	
      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
      	strSQL = strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append("') ");
      	strSQL = strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append("'))) ");		        	
      }      
      else if (
      //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
          roles.get(propiedades_roles.leePropiedad("ROL_SECRETARIA")) != null ||
          roles.get(propiedades_roles.leePropiedad("ROL_JEFE")) != null         
          || roles.get(propiedades_roles.leePropiedad("ROL_ANALISTA_LEXC")) != null
         
      ) {
    	  //si es jefe se obtiene todas las unidades donde es encargado o jefe (fnontene,2153,uos: 5E2000,5E1100,5E3100)
    	  if (roles.get(propiedades_roles.leePropiedad("ROL_JEFE")) != null){ 
    		  objsJefe.add(codPersUsuario.toUpperCase());
    		  objsJefe.add("1");    		  
    		  resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
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
    		  
    	  }
    	  //fin si es jefe
    	  else{
    		  strSQL = strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')").append("||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append("' ");
    	  }
        
      } else {
        strSQL = strSQL.append(" and 1=2 ");	
      }               
    }

    strSQL = strSQL.append(" and param.t99cod_tab = ? ")
    .append(" and param.t99tip_desc = ? ")
    .append(" and substr(trim(nvl(p.t02cod_uorgl,'')")
    .append("||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
    .append(" and substr(trim(nvl(p.t02cod_catel,'')")
    .append("||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

    objs.add(codPers.toUpperCase());
    objs.add("1");
    objs.add("001");
    objs.add("D");

    if(log.isDebugEnabled()) log.debug("query t02 "+ strSQL.toString());
    if(log.isDebugEnabled()) log.debug("query objs "+ objs.toArray());
    res=executeQuery(dataSource, strSQL.toString(), objs.toArray());
    if (res.size()>0){
      a=(HashMap)res.get(0);
    }
    if(log.isDebugEnabled()) log.debug("a "+ a);
    return a;
  }
  //FIN ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp

  /**
   * Obtiene los datos del usuario asï¿½ como sus unidades, cargos y categorï¿½as actuales y lï¿½gicas.
   * 
   * @param codpers		Numero de registro del usuario.
   * @return
   */
  public Map joinWithT12T01Deta(String codpers){
	  return executeQueryUniqueResult(dataSource, JOIN_WITH_T12T99DETA.toString(), new Object[]{codpers});
  }
  
  /** agregado 15/11/2007 prac-jcallo ***/	
  /**
   * 
   * Metodo que se encarga de actualizar algunos datos del personal 
   *  
   * @param hm		HashMap. datos de del personal(ape_pat,ape_mat,nom_personal,etc ...)
   * @return          void
   * @throws DAOException
   * */
	public void updateByRtps(Map hm) throws DAOException {
		
		if(log.isDebugEnabled())  log.debug("===detalle del rtps a registrar y aprobar===");
		if(log.isDebugEnabled())  log.debug(" rtps : "+hm);
		if(log.isDebugEnabled())  log.debug("== ==");
		
		FechaBean fechaNac = new FechaBean((String)hm.get("fec_nacimiento"));
		
		String cod_docum = (hm.get("cod_docum") != null ) ? (String)hm.get("cod_docum"):"";
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
		
		/************************/
		
		String codigo_ubigeo; 
		String nombre_zona;
		String nombre_via;
		String numero_via;
		String numero_interior;
		String referencia;
		
		if (hm.get("cod_via_dest")!= null &&
				hm.get("nom_via_dest")!= null && !hm.get("nom_via_dest").toString().equals("") &&
					hm.get("num_via_dest")!= null && !hm.get("num_via_dest").toString().equals("")){
			
			codigo_ubigeo = (hm.get("cod_ubigeo_dest")!=null?hm.get("cod_ubigeo_dest").toString():"");
			nombre_zona = (hm.get("nom_zona_dest")!=null?hm.get("nom_zona_dest").toString():"");
			nombre_via = hm.get("nom_via_dest").toString();
			numero_via = hm.get("num_via_dest").toString();
			numero_interior = (hm.get("num_interior_dest")!=null?hm.get("num_interior_dest").toString():"");
			referencia = (hm.get("des_referencia_des")!=null?hm.get("des_referencia_des").toString():"");
		} else {
			
			codigo_ubigeo = (hm.get("cod_ubigeo_dom")!=null?hm.get("cod_ubigeo_dom").toString():"");
			nombre_zona = (hm.get("nom_zona")!=null?hm.get("nom_zona").toString():"");
			nombre_via = (hm.get("nom_via")!=null?hm.get("nom_via").toString():"");
			numero_via = (hm.get("num_via")!=null?hm.get("num_via").toString():"");
			numero_interior = (hm.get("num_interior")!=null?hm.get("num_interior").toString():"");
			referencia = (hm.get("des_referencia")!=null?hm.get("des_referencia").toString():"");
		}
		
		/**************************/
		
		if(cod_docum.equalsIgnoreCase("01")){
			updateByRtps.append(", t02lib_elec = ? , t02f_graba = ?, t02cod_user = ? where t02cod_pers = ? ");
			
			executeUpdate(dataSource, updateByRtps.toString(), new Object[]{hm.get("ape_pat"), hm.get("ape_mat"), 
				 hm.get("nom_personal"), fechaNac.getTimestamp(), codigo_ubigeo, nombre_zona, 
				 (nombre_via+" "+numero_via+" "+numero_interior), referencia,
				 hm.get("num_docum"), fecha_reg.getTimestamp(), hm.get("nomb_usuario"), hm.get("cod_personal")} );
		}else{
			updateByRtps.append(", t02f_graba = ?, t02cod_user = ? where t02cod_pers = ? ");
			executeUpdate(dataSource, updateByRtps.toString(), new Object[]{hm.get("ape_pat"), hm.get("ape_mat"), 
				 hm.get("nom_personal"), fechaNac.getTimestamp(), codigo_ubigeo, nombre_zona, 
				 (nombre_via+" "+numero_via+" "+numero_interior), referencia,
				 fecha_reg.getTimestamp(), hm.get("nomb_usuario"),hm.get("cod_personal")} );
		}
		
	}
	
	/**
	 * Metodo que se encarga de actualizar algunos datos del personal 
	 *  
	 * @param hm		HashMap. datos de del personal(ape_pat,ape_mat,nom_personal,etc ...)
	 * @return          void
	 * @throws DAOException
	 * */
	public void insertByRtps(Map hm) throws DAOException {
		
		String cod_docum = (hm.get("cod_docum") != null ) ? (String)hm.get("cod_docum"):"";
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
		
		/************************/
		
		String codigo_ubigeo; 
		String nombre_zona;
		String nombre_via;
		String numero_via;
		String numero_interior;
		String referencia;
		
		if (hm.get("cod_via_dest")!= null &&
				hm.get("nom_via_dest")!= null && !hm.get("nom_via_dest").toString().equals("") &&
					hm.get("num_via_dest")!= null && !hm.get("num_via_dest").toString().equals("")){
			
			codigo_ubigeo = (hm.get("cod_ubigeo_dest")!=null?hm.get("cod_ubigeo_dest").toString():"");
			nombre_zona = (hm.get("nom_zona_dest")!=null?hm.get("nom_zona_dest").toString():"");
			nombre_via = hm.get("nom_via_dest").toString();
			numero_via = hm.get("num_via_dest").toString();
			numero_interior = (hm.get("num_interior_dest")!=null?hm.get("num_interior_dest").toString():"");
			referencia = (hm.get("des_referencia_des")!=null?hm.get("des_referencia_des").toString():"");
		} else {
			
			codigo_ubigeo = (hm.get("cod_ubigeo_dom")!=null?hm.get("cod_ubigeo_dom").toString():"");
			nombre_zona = (hm.get("nom_zona")!=null?hm.get("nom_zona").toString():"");
			nombre_via = (hm.get("nom_via")!=null?hm.get("nom_via").toString():"");
			numero_via = (hm.get("num_via")!=null?hm.get("num_via").toString():"");
			numero_interior = (hm.get("num_interior")!=null?hm.get("num_interior").toString():"");
			referencia = (hm.get("des_referencia")!=null?hm.get("des_referencia").toString():"");
		}
		
		/**************************/
		
		if(cod_docum.equalsIgnoreCase("01")){
			insertByRtps.append(", t02lib_elec, t02f_graba, t02cod_user  ) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			
			executeUpdate(dataSource, insertByRtps.toString(), new Object[]{hm.get("cod_personal"),hm.get("ape_pat"), hm.get("ape_mat"), 
				 	hm.get("nom_personal"), hm.get("fec_nacimiento"), codigo_ubigeo, nombre_zona, 
				 	(nombre_via+" "+numero_via+" "+numero_interior), referencia,
				 	hm.get("num_docum"), fecha_reg.getTimestamp(), hm.get("nomb_usuario") } );
		}else{
			insertByRtps.append(", t02f_graba, t02cod_user ) values (?,?,?,?,?,?,?,?,?,?,?)");
			executeUpdate(dataSource, insertByRtps.toString(), new Object[]{hm.get("cod_personal"),hm.get("ape_pat"), hm.get("ape_mat"), 
				 	hm.get("nom_personal"), hm.get("fec_nacimiento"), codigo_ubigeo, nombre_zona, 
				 	(nombre_via+" "+numero_via+" "+numero_interior), referencia,
				 	fecha_reg.getTimestamp(), hm.get("nomb_usuario") } );
		}
		
	}
		
	/** fin agregado 15/11/2007 prac-jcallo ***/


	  /**
	   * 
	   * @param registro
	   * @return Map
	   * @throws DAOException
	   */
	  public Map findByRegistroActivo(String registro) throws DAOException {
 
	    Map mapa = new HashMap();
	    try {
	      mapa = executeQueryUniqueResult(dataSource, FINDBYREGISTROACTIVO.toString(), new Object[] { registro });
	      if(log.isDebugEnabled())log.debug("codigo registro = "+registro);
	      if (mapa != null && !mapa.isEmpty()) {
	        mapa.put("t02cod_pers", registro);
	      }
	    } catch (Exception e) {
	      throw new DAOException(this,
	      "No se pudo cargar la informacion de personal");
	    }
	    return mapa;
	  }
	  
	  
    /**
	  * 
	  * Metodo que se encarga de listas todos los numeros de registro de las personas que
	  * INGRESARON O REINGRESARON A SUNAT en una determina fecha ( dia y mes) 
	  *  
	  * @param String diames formato (dd/MM).
	  * @return List
	  * @throws DAOException
	  * */
	public List findIngresoPersonal(String diames) throws DAOException {
		return executeQuery(dataSource, findIngresoPersonal.toString(), new Object[]{diames,diames});
	}	
	
	
	
	/****************** METODO COMSA ******************************/
	/**
	 * Metodo encargado de validar si el beneficiario ha trabajado antes en SUNAT.
	 * 
	 * 
	 * 
	 * @return List 		conteniendo el registro obtenido.
	 * 
	 * @throws DAOException
	 * @throws Exception
	 */   
	
	public List validarTrabajador(Map params)
	throws DAOException,Exception {
		List lista = null;
		String num_doc =  (String) params.get("num_doc");
		Object[] objs = new Object[] { num_doc};
		setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
		lista = executeQuery(dataSource, QUERY1_SENTENCE.toString(), objs);
		setIsolationLevel(-1);
		if (log.isDebugEnabled()) log.debug("lista trab"+ lista.size());
		return lista;
	}
	
	/****************** METODO  ******************************/
	/**
	 * Metodo encargado de obtener todos los trabajadores de una UO.
	 * 
	 * 
	 * 
	 * @return List 		conteniendo el registro obtenido.
	 * 
	 * @throws DAOException
	 * @throws Exception
	 */   
	
	public List findColaboradorByUO(String uo)
	throws DAOException,Exception {
		List lista = null;
		Object[] objs = new Object[] {uo};
		setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
		lista = executeQuery(dataSource, COLAB_BY_UO.toString(), objs);
		setIsolationLevel(-1);
		if (log.isDebugEnabled()) log.debug("lista trab"+ lista.size());
		return lista;
	}
	
	/**
	  * 
	  * Metodo que se encarga de listas todos los datos de las personas para mostrarlas 
	  * en las busquedas del padron
	  *  
	  * @param String diames formato (dd/MM).
	  * @return List
	  * @throws DAOException
	  * */
	public List findForPadron(Map params ) throws DAOException {
		boolean flag = false;		
		StringBuffer sWhere = new StringBuffer("");
		StringBuffer sSQL = new StringBuffer("");
				
		String nreg = ( params.get("nreg") == null ) ?  "" : params.get("nreg").toString().trim();
		String appat = ( params.get("appat") == null ) ?  "" : params.get("appat").toString();
		String apmat = ( params.get("apmat") == null ) ?  "" : params.get("apmat").toString();
		String nomb = ( params.get("nomb") == null ) ?  "" : params.get("nomb").toString();
		String coduorg = ( params.get("coduorg") == null ) ?  "" : params.get("coduorg").toString().trim();
		String codcate = ( params.get("codcate") == null ) ?  "" : params.get("codcate").toString().trim();
		String estado = ( params.get("estado") == null ) ?  "" : params.get("estado").toString().trim();
		
		nreg = nreg.replaceAll("'", "''");
		appat = appat.replaceAll("'", "''");
		apmat = apmat.replaceAll("'", "''");
		nomb = nomb.replaceAll("'", "''");
		coduorg = coduorg.replaceAll("'", "''");
		codcate = codcate.replaceAll("'", "''");
		estado = estado.replaceAll("'", "''");
				
		//verificando parametro numero de registro
		if (nreg != null && nreg.length() > 0) {			
			flag = true;
			sWhere.append("t02cod_pers = '" + nreg + "'");
		}
		
		//verificando parametro apellido paterno
		if (appat != null && appat.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}					
			sWhere.append("t02ap_pate like '" + appat + "%'");
		}
		
		//verificando parametro apellido materno
		if (apmat != null && apmat.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02ap_mate like '" + apmat + "%'");
		}

		//verificando parametro nombres		
		if (nomb != null && nomb.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02nombres like '" + nomb + "%'");
		}
		
		//verificando parametro apellido codigo de unidad organizacional
		if (coduorg != null && coduorg.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_uorg = '" + coduorg + "'");
		}
		
		//verificando parametro apellido codigo de categoria
		if (codcate != null && codcate.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_cate = '" + codcate + "'");
		}

		//verificando parametro estado(1)
		if (estado != null && estado.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_stat = '" + estado + "'");
		}
		
		if (flag) {
			sWhere = new StringBuffer(" and (" + sWhere.toString()+")");
		}
		
		sSQL.append(FIND_FOR_PADRON.toString()).append(sWhere.toString());
		
		List res = null;
		if (params.get("cargar_telef")!=null && "1".equals((String) params.get("cargar_telef"))){
			res = executeQuery(dataSource, sSQL.toString(), beanMapperTelf);
		} else {
			res = executeQuery(dataSource, sSQL.toString());
		}
			
		if (res!= null && res.size()>0){ Ordenamiento.sort(res, new String[]{"t02ap_pate", "t02ap_mate"});}
		if(log.isDebugEnabled()) log.debug("total = "+res!= null?(""+res.size()):"0");
		
		return res;
	}	
	
	//metodo joinWithT12T01Deta1 solo es usado por metodo obtenerUsuProgramacion (ProgramacionFacade)
	/* FUSION PROGRAMACION - JROJAS4 */
	/*
	 * Obtiene los datos del usuario asi como sus unidades, cargos y categorias actuales y logicas.
	 * 
	 * @param codpers		Numero de registro del usuario.
	 * @return
	 */
	public Map joinWithT12T01Deta1(Map datos){
		StringBuffer strSQL = null;
		List objsJefe = new ArrayList(); List resUOsJefe= new ArrayList(); HashMap uoMap = new HashMap(); String codUoJef = ""; // ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp/Prog Vacaciones		
	  	strSQL = new StringBuffer(JOIN_WITH_T12T99DETA.toString());
		if(log.isDebugEnabled()) log.debug("DATOS "+datos);
		
		//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
		HashMap roles = (HashMap)datos.get("roles");
		if(log.isDebugEnabled()) log.debug("roles: "+roles);
		if  (("usuario_AnalisCentral".equals(datos.get("perfil_usuario").toString())) || (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null) ){
			if(log.isDebugEnabled()) log.debug("perfil o rol analista central");
			return executeQueryUniqueResult(dataSource, strSQL.toString(), new Object[]{datos.get("txtregistro").toString()});
		}//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
		else if  (( "usuario_AnalisOperativo".equals(datos.get("perfil_usuario").toString())) ){
			
			strSQL.append(" and t02cod_uorg like '" ).append( datos.get("uoSeg").toString().trim().toUpperCase()).append( "' ");
			strSQL.append(" UNION ").append(JOIN_WITH_T12T99DETA.toString());
			strSQL.append(" and t02cod_uorgl like '" ).append( datos.get("uoSeg").toString().trim().toUpperCase()).append( "' ");
			return executeQueryUniqueResult(dataSource, strSQL.toString(), new Object[]{datos.get("txtregistro").toString(),datos.get("txtregistro").toString()});
			
		} else if ("usuario_Jefe".equals(datos.get("perfil_usuario").toString())){				
					
			//datos.put("uoSeg", findUuooJefe(datos.get("uuoo_usuario")!=null?datos.get("uuoo_usuario").toString().trim():"")+"%");  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp/Prog Vacaciones
			//if(log.isDebugEnabled()) log.debug("nueva visibilidad jefe(uoSeg): "+datos.get("uoSeg").toString());  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp/Prog Vacaciones

			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp/Prog Vacaciones
			//se obtiene todas las unidades donde es encargado o jefe (fnontene,2153,uos: 5E2000,5E1100,5E3100)
			objsJefe.add(datos.get("usuario").toString().trim().toUpperCase());
			objsJefe.add("1");    		  
			resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
			if(log.isDebugEnabled()) log.debug("resUOsJefe(joinWithT12T01Deta1):"+ resUOsJefe);
			if (resUOsJefe.size()>0){    			   		      
				for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
					uoMap=(HashMap)resUOsJefe.get(i);
					codUoJef= uoMap.get("t12cod_uorga").toString().trim();
					if (i==0){//0 (primer registro)
						strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
					}else{
						strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
					}    		    	  
					if (i==resUOsJefe.size()-1){//2 (ultimo registro)
						strSQL.append(")");
					}
				}
			}	
			return executeQueryUniqueResult(dataSource, strSQL.toString(), new Object[]{datos.get("txtregistro").toString()});
			// ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp/Prog Vacaciones			
		}
		else
		{
			if(log.isDebugEnabled()) log.debug("perfil que no es analista central, jefe o analista operativo");
			return executeQueryUniqueResult(dataSource, strSQL.toString(), new Object[]{datos.get("txtregistro").toString()});
		}
	}
	
	
	//ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
	/** Metodo para listar las personas que faltan programar sus vacaciones
	 * 
	 * @param registro
	 * @return Map
	 * @throws DAOException
	 */
	public List listarFaltantesProgVac(Map datos) throws DAOException {
		List lista = null;
		List resUOsJefe= new ArrayList();	  
	    HashMap uoMap = new HashMap();
	    String codUoJef = "";	  
	    List objsJefe = new ArrayList();
	    
		try {
			String indSub = ((String) datos.get("indSub"));
			String codPersUsuario = ((String) datos.get("codPers")).trim();
			StringBuffer sWhere = new StringBuffer("");		
			String sOrderBy=" order by T.t12des_corta,P.t02cod_pers ";
			StringBuffer sSQL = new StringBuffer(""); 			
				
			if ("4".equals(datos.get("cod_criterio").toString())) {//registro
				sWhere.append(" AND P.t02cod_pers= '").append(((String)datos.get("txtbusqueda")).trim()).append("' ");
			} else if ("2".equals(datos.get("cod_criterio").toString()) && indSub.equals(Constantes.ACTIVO)) { //unidad y mostrar subunidades
				sWhere.append(" AND T.t12cod_uorga like '").append(findUuooJefe(((String)datos.get("txtbusqueda")).trim())).append("%' ");
			} else if ("2".equals(datos.get("cod_criterio").toString()) && indSub.equals(Constantes.INACTIVO)) { //solo de unidad
				sWhere.append(" AND T.t12cod_uorga= '").append(((String)datos.get("txtbusqueda")).trim()).append("' ");
			} else if ("3".equals(datos.get("cod_criterio").toString())) { //intendencia
				sWhere.append(" AND substr(P.t02cod_uorg,1,2)= '").append(((String)datos.get("txtbusqueda")).trim().substring(0,2)).append("' ");
			}			
				
			if ("usuario_Jefe".equals(datos.get("perfil_usuario").toString())) { //jefe
				sWhere.append(" and t02cod_uorg like '" ).append( datos.get("uoSeg").toString().trim().toUpperCase()).append( "' ");
				
				objsJefe.add(codPersUsuario.toUpperCase());
	    		  objsJefe.add("1");    		  
	    		  resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
	    		  if(log.isDebugEnabled()) log.debug("resUOsJefe:"+ resUOsJefe);
	    		  if (resUOsJefe.size()>0){    			   		      
	    		      for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
	    		    	  uoMap=(HashMap)resUOsJefe.get(i);
	    		    	  codUoJef= uoMap.get("t12cod_uorga").toString().trim();
	    		    	  if (i==0){//0 (primer registro)
	    		    		  sWhere.append(" and (substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
	    		    	  }else{
	    		    		  sWhere.append(" or substr(trim(nvl(P.t02cod_uorgl,'')||nvl(P.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
	    		    	  }    		    	  
	    		    	  if (i==resUOsJefe.size()-1){//2 (ultimo registro)
	    		    		  sWhere.append(") ");
	    		    	  }
	    		      }
	    		  }	    		  
			} else if ( "usuario_AnalisOperativo".equals(datos.get("perfil_usuario").toString())) {	//analista operativo		
				sWhere.append(" and ((substr(trim(nvl(P.t02cod_uorgl,'')").append("||nvl(P.t02cod_uorg,'')),1,6) like '").append(datos.get("uoSeg").toString()).append("') ");
		      	sWhere.append(" or (substr(trim(nvl(P.t02cod_uorgl,'')").append("||nvl(P.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(datos.get("uoAO").toString()).append("'))) ");
			} else { //analista central
				sWhere.append(" and 1=1 " );
			}

			sSQL.append(FINDPERSCONSFALTAPROGVAC.toString()).append(sWhere.toString()).append(sOrderBy);
			//lista = executeQuery(dataSource, sSQL.toString(), new Object[] { "1"});
			lista = executeQuery(dataSource, sSQL.toString(), new Object[] {datos.get("estado").toString(),new FechaBean((String)datos.get("fechaIngreso")).getSQLDate(),datos.get("noregimen").toString()});

		} catch (Exception e) {
			throw new DAOException(this,
			"No se pudo cargar la informacion de personal");
		}
		return lista;
	}
	//FIN
	
		
	/**
	 * Mï¿½todo para listar vacaciones por Trabajador 
	 * ademï¿½s se considera un criterio de Seguridad
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	public List listarVacProgramadas(Map datos)
	throws DAOException, Exception{
		if(log.isDebugEnabled())log.debug("T02DAO datos-->" + datos);
		String periodoIni = (String)datos.get("periodoIni");
		String periodoFin = (String)datos.get("periodoFin");
		String estado = (String)datos.get("cmbEstado");
		String criterio = (String)datos.get("cmbCriterio");
		String valor = (String)datos.get("txtValor");
		String visLike = (String)datos.get("uoSeg");
		String codPersUsuario = ((String) datos.get("codPers")).trim(); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
    	HashMap roles = (HashMap) datos.get("roles");//ICAPUNAY-MEMO 32-4F3100-2013    	
    	log.debug("T02DAO-listarVacProgramadas-roles: "+roles);//ICAPUNAY-MEMO 32-4F3100-2013
    	
    	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
    	
    	List resUOsJefe= new ArrayList();      
        HashMap uoMap = new HashMap();
        String codUoJef = "";     
        List objsJefe = new ArrayList();
        
    	//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
    	/*if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null || roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
    		//visLike = visLike; visLike o uoSeg no cambia se mantiene del servlet
    	}else if (roles.get(Constantes.ROL_JEFE) != null){
    		log.debug("uuoo_usuario: "+(String) datos.get("uuoo_usuario")); 
    		visLike = findUuooJefe(((String) datos.get("uuoo_usuario"))).trim()+"%";
			log.debug("visLike: "+visLike);     	
    	}*/   		
    	//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
    	//ICAPUNAY - PAS20165E230300132
	
		if(log.isDebugEnabled())log.debug("estado-->" + estado);
		if(log.isDebugEnabled())log.debug("criterio-->" + criterio);
		if(log.isDebugEnabled())log.debug("valor-->" + valor);
		if(log.isDebugEnabled())log.debug("visLike-->" + visLike);//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
		
		Map p = new HashMap();

		if (!"".equals(periodoIni)) {
			p.put("#v.anno_vac", new Object[]{periodoIni, periodoFin});
		}		
		if (!"".equals(estado) && !estado.equals("-1")) {
			p.put("v.est_id", estado);			
		}
		
		List l = new ArrayList();
		String camposKey = "";
		
		SQLParser parser = new SQLParser(PROPIEDADES);
		camposKey = parser.parser(p, l);
		
		if(log.isDebugEnabled())log.debug("camposKey-->" + camposKey);
		StringBuffer sSQL = new StringBuffer(""); 
		sSQL.append(LISTARVACPROG.toString());
		if(log.isDebugEnabled())log.debug("LISTARVACPROG--> " + LISTARVACPROG.toString());
		
		/**FRD se comento esto 17/06/2009
		if ("0".equals(criterio)) {
			if(log.isDebugEnabled())log.debug("entro criterio cero-->");
			sSQL.append( " AND v.cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
		}
		*/
		if ("0".equals(criterio)) {
			if(log.isDebugEnabled())log.debug("entro criterio registro-->");
			
			Map hmRegUserCons = this.findByRegistro(valor.trim().toUpperCase());
			String uoUserCons = String.valueOf(hmRegUserCons.get("cod_uorg")); 
			
			sSQL.append( " AND v.cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			//sSQL.append( " and '").append(uoUserCons.trim().toUpperCase()).append("' = v.u_organ ");
			//sSQL.append( " and '").append(uoUserCons.trim().toUpperCase()).append("' = p.t02cod_uorg ");ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
			sSQL.append( " and '").append(uoUserCons.trim().toUpperCase()).append("' = substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ");//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
			//sSQL.append( " and '").append(uoUserCons.trim().toUpperCase()).append("' like '"); ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//sSQL.append( visLike).append("' "); ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		}
		
		
		if ("1".equals(criterio)) {
			if(log.isDebugEnabled())log.debug("entro criterio unidad-->");
			//sSQL.append( " and '").append(valor.trim().toUpperCase() ).append("' = v.u_organ ");
			//sSQL.append( " and '").append(valor.trim().toUpperCase() ).append("' = p.t02cod_uorg ");ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
			sSQL.append( " and '").append(valor.trim().toUpperCase() ).append("' = substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ");//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
			//sSQL.append( " and '").append(valor.trim().toUpperCase() ).append("' like '"); ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			//sSQL.append( visLike).append("' "); ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		}

		if ("4".equals(criterio)) {
			if(log.isDebugEnabled())log.debug("entro criterio intendencia-->");
			
			//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
			String intendencia = "--'";
			intendencia=valor.trim().substring(0,2).toUpperCase().concat("%");
			if(log.isDebugEnabled())log.debug("intendencia: "+intendencia);
			
			
			//sSQL.append( " and p.t02cod_uorg ").append("like '").append(intendencia).append("'");ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
			sSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ").append("like '").append(intendencia).append("'");//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
			
		} //se agrega linea ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados	
		
		//DTARAZONA - SE AGREGA LA OPCION INSTITUCIONAL - 30/01/2018
		
		
		
		//FIN DTARAZONA
		
		
	    	if (roles.get(Constantes.ROL_ANALISTA_CENTRAL)!=null){
	    		if(log.isDebugEnabled())log.debug("es analista central-->");	    		
	    		sSQL.append( " and 1=1 ");				
	    	}else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO)!=null){
	    		if(log.isDebugEnabled())log.debug("es analista operativo-->");	    	
	    		sSQL.append( " and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ").append("like '").append(visLike).append("' ");
				sSQL.append( " or (p.t02cod_uorg in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(((String) datos.get("uuoo_usuario")).trim()).append("'))) ");
	    	}
	    	else if (roles.get(Constantes.ROL_JEFE) != null){
	    		if(log.isDebugEnabled())log.debug("es jefe -->");
	    		
	    		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	    		/*//sSQL.append( " and p.t02cod_uorg ").append("like '");ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
	    		sSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ").append("like '");//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
				sSQL.append( visLike).append("' ");*/
	    		
	    		objsJefe.add(codPersUsuario.toUpperCase());
	    		objsJefe.add("1");    		  
	    		resUOsJefe=executeQuery(dataSource, findUOsByJefe.toString(), objsJefe.toArray());
	    		if(log.isDebugEnabled()) log.debug("resUOsJefeee:"+ resUOsJefe);
	    		if (resUOsJefe.size()>0){    			   		      
	    			for (int i = 0;i<resUOsJefe.size();i++){ //0,1,2 (3 unidades)
	    				uoMap=(HashMap)resUOsJefe.get(i);
	    				codUoJef= uoMap.get("t12cod_uorga").toString().trim();
	    				if (i==0){//0 (primer registro)
	    					sSQL = sSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
	    				}else{
	    					sSQL = sSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(findUuooJefe(codUoJef)).append("%' "); 
	    				}    		    	  
	    				if (i==resUOsJefe.size()-1){//2 (ultimo registro)
	    					sSQL = sSQL.append(") ");
	    				}
	    			}
	    		}
				//ICAPUNAY - PAS20165E230300132
	    	}
	    	else{
	    		if(log.isDebugEnabled())log.debug("no es analista central, ni operativo, ni jefe-->");	    		
	    		//sSQL.append( " and p.t02cod_uorg ").append("like '");ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
	    		sSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ").append("like '");//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
				sSQL.append( visLike).append("' ");
				/*sSQL.append( " and '").append(valor.trim().toUpperCase() ).append("' = p.t02cod_uorg ");
				sSQL.append( " and '").append(valor.trim().toUpperCase() ).append("' like '");
				sSQL.append( visLike).append("' ");*/
	    	}
	    	//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
						
		//} //se comenta linea ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	    	
		/**FRD se comento esto 17/06/2009		
		if ("4".equals(criterio)) {
			if(log.isDebugEnabled())log.debug("entro criterio cuatro-->");
			String intendencia = "--'";
			if (valor.trim().length()==6 && "0000".equals(valor.trim().substring(2,6))){
				intendencia=valor.trim().substring(0,2).concat("%'");
			}
			sSQL.append( " and v.u_organ like '").append(intendencia);
		}
		*/
		 
		Object[] o = l.toArray();
		if(log.isDebugEnabled())log.debug("o-->" + o);
		if(log.isDebugEnabled())log.debug("aaLISTARVACPROG-->" + sSQL.toString());
//		setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
		
		List lista = (ArrayList) executeQuery(dataSource, sSQL.toString()
					//.concat(camposKey).concat(" order by p.t02cod_pers"), o); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				   .concat(camposKey).concat(" order by p.t02cod_pers asc,periodo asc, desde asc "), o); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
//		setIsolationLevel(-1);
		if(log.isDebugEnabled())log.debug("antes de lista");
		if(log.isDebugEnabled())log.debug("lista-->" + lista);
		return lista;
	}
	
	
	/*      */

	
	//ASANCHEZZ - 20100412
	
	/**
	 * Metodo que se encarga de buscar el personal
	 * @param params Map
	 * @return List 
	 * @throws DAOException
	 */
	
	public List buscarPersonal(Map params)  throws DAOException {    

		StringBuffer sSQL = new StringBuffer(QUERY2_SENTENCE.toString()); 
		List lista = null;
		//igual al metodo joinWithT02Nested de la T1275DAO (sp)  linea 844
		try{

			String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
			String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";
			String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
			Map seguridad = (HashMap)params.get("seguridad");
			
			/*ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n para todos los regimenes pero solo por el criterio REGISTRO)
			if (criterio.equals("0")){
				sSQL.append(" AND T02COD_PERS = '").append(valor.trim().toUpperCase()).append( "' ");
			} else if(criterio.equals("1")){
				sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) = '" ).append( valor.trim().toUpperCase()).append( "' ");
			//} else if(criterio.equals("2")){
			//}else if(criterio.equals("2") && !valor.equals("")){//Intendencia		
			}else if(criterio.equals("4") && !valor.equals("")){//Intendencia - //JVILLACORTA 20/06/2011 - AOM:MODALIDADES FORMATIVAS
				String intendencia = valor.substring(0,2);
				sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
			}//SI ES INSTITUCIONAL, ENTONCES NO ES NECESARIO NINGUN FILTRO DE COD_UORG
			*///FIN ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n para todos los regimenes pero solo por el criterio REGISTRO)
			
			//ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n para todos los regimenes pero solo por el criterio REGISTRO)
			if (criterio.equals("0")){
				sSQL.append(" WHERE T02COD_STAT in ('1','0') ");
				sSQL.append(" AND T02COD_PERS = '").append(valor.trim().toUpperCase()).append( "' ");
			} else if(criterio.equals("1")){
				sSQL.append(" WHERE T02COD_STAT = '1' ");
				sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) = '" ).append( valor.trim().toUpperCase()).append( "' ");
			//} else if(criterio.equals("2")){
			//}else if(criterio.equals("2") && !valor.equals("")){//Intendencia		
			}else if(criterio.equals("4") && !valor.equals("")){//Intendencia - //JVILLACORTA 20/06/2011 - AOM:MODALIDADES FORMATIVAS
				String intendencia = valor.substring(0,2);
				sSQL.append(" WHERE T02COD_STAT = '1' ");
				sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
			}else{
				sSQL.append(" WHERE T02COD_STAT = '1' ");//SI ES INSTITUCIONAL, ENTONCES NO ES NECESARIO NINGUN FILTRO DE COD_UORG SOLO EL FILTRO DE ACTIVOS
			}
			//FIN ICAPUNAY 01/08/2011 a solicitud de Carmen Lama (ver INACTIVOS tambiÃ©n para todos los regimenes pero solo por el criterio REGISTRO)

			if (regimen.equals("0")) {//276 -728
				//sSQL.append(" AND (T02COD_REL = '01' OR T02COD_REL = '02') ");
				//JRR - 10/05/2011
				sSQL.append(" AND T02COD_REL NOT IN ('09','10') ");
			}else if(regimen.equals("1")){//1057
				sSQL.append(" AND T02COD_REL = '09' ");
			}//JRR - 20/04/2011
			 else if(regimen.equals("2")){//Modalidad Formativa
				sSQL.append(" AND T02COD_REL = '10' ");
			}
			//JRR - Los t02cod_rel que no se filtran en la condicion vendrï¿½n de todas formas ('03','04',etc)
			
			//sSQL.append(" AND T02COD_STAT = '1' "); //ya incluido en la variable QUERY2_SENTENCE

			//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {

				Map roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
			    if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
			    if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					sSQL.append(" AND 1=1 ");
				} 
				//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
			    else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){	
			      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
			      	sSQL.append(" AND ((SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "') ");
			      	sSQL.append(" OR (SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) IN (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");		        	
			    }
				//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
				else if (
				//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
						roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "' ");
				} else {
					sSQL.append(" AND 1=2 ");
				}
			}
					
			sSQL.append(" ORDER BY T02COD_PERS ");
			
			setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
			lista = (ArrayList) executeQuery(dataSource, sSQL.toString());
			setIsolationLevel(-1);

		}catch (Exception e) {
			log.error("*** SQL Error ****",e);
			MensajeBean msg = new MensajeBean();
			msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
			throw new DAOException (this, msg);     
		} finally {       
	    }    
	    return lista; 
	}	
	
	//FIN	
	

//FUSION DE CLASE CON METODOS COMSA	
/* JRR - AUTORIZACION BATCH - 08/03/2011 */
	/**
	  * 
	  * Metodo que se encarga de listas de personas cesadas
	  *    
	  * @param HashMap datos
	  * @return List
	  * @throws DAOException
	  * */
	public List obtenerCesados (Map datos) throws DAOException {
	  List lista = null;
	  try {
	  	Object obj[] = {	  		
	  		datos.get("fechaFin"),	
	  		datos.get("fechaIni")
	  	};  	
	    lista = executeQuery(dataSource, FINDBYCESADOS.toString(), obj );	    
	  } catch (Exception e) {
	    throw new DAOException(this,"No se pudo cargar la informacion");
	  }
	  return lista;
	} 
/*		*/	
	
	
	/* JRR - 06/04/2011 - RECUPERACION DE FUENTES - Decompilados */
	public Map detallarDatEmp(Map params)
        throws DAOException, Exception
    {
        Map datEmp = null;
        Object objs[] = {
            (String)params.get("num_doc_afec")
        };
        setIsolationLevel(TX_READ_UNCOMMITTED);
        datEmp = executeQueryUniqueResult(dataSource, QUERY3_SENTENCE.toString(), objs);
        setIsolationLevel(-1);
        return datEmp;
    }

	//07/04/2011
	public List buscarInfoTrab(Map params)
	    throws DAOException
	  {
	    List lista = new ArrayList();
	    Map parametros = new LinkedHashMap();
	    try {
	      if ((params.get("num_docide_pnat") != null) && 
	        (!"".equals(params.get("num_docide_pnat").toString()))) {
	        parametros.put("t02cod_pers", params.get("num_docide_pnat"));
	      } else {
	        if ((params.get("des_apepat_pnat") != null) && (!"".equals(params.get("des_apepat_pnat").toString()))) {
	          parametros.put("t02ap_pate", params.get("des_apepat_pnat"));
	        }
	        if ((params.get("des_apemat_pnat") != null) && (!"".equals(params.get("des_apemat_pnat").toString()))) {
	          parametros.put("t02ap_mate", params.get("des_apemat_pnat"));
	        }
	        if ((params.get("des_nombre_pnat") != null) && (!"".equals(params.get("des_nombre_pnat").toString()))) {
	          parametros.put("t02nombres", params.get("des_nombre_pnat"));
	        }
	        if ((params.get("cod_uuoo") != null) && (!"".equals(params.get("cod_uuoo").toString()))) {
	          parametros.put("t02cod_uorg", params.get("cod_uuoo"));
	        }
	      }
	      List l = new ArrayList();
	      String camposKey = "";
	      SQLParser parser = new SQLParser(PROPIEDADES_2);
	      camposKey = parser.parser(parametros, l);
	      Object[] obj = l.toArray();
	      String query = QUERY2A_SENTENCE.toString().concat(camposKey.substring(4));
	      lista = executeQuery(dataSource, query, obj, new BeanMapper() {
	        public Object setear(ResultSet rs, Map data) throws SQLException {
	          if (log.isDebugEnabled())log.debug("Seteando a DynaBean");
	          if ((data.get("t02cod_catel") == null) || ("".equals(data.get("t02cod_catel").toString())))
	            data.put("t02cod_cate", data.get("t02cod_cate").toString());
	          else {
	            data.put("t02cod_catel", data.get("t02cod_catel").toString());
	          }
	          if ((data.get("flagPers") != null) && (constantes.leePropiedad("FLAG_PERS").equals(data.get("flagPers"))) && 
	            (dataSource != null) && (dataSource_telef != null)) {
	            TelefonoDAO telefonoDAO = new TelefonoDAO(dataSource_telef);
	            List listTelefonos = telefonoDAO.findTelefonos(data.get("num_docide_pnat").toString());
	            data.put("listTelefonos", listTelefonos);
	          }

	          String cod_uorga = "";
	          ParamDAO paramDao = new ParamDAO();
	          if (data.get("t02cod_uorgl") != null) {
	            data.put("t12cod_uorga", data.get("t02cod_uorgl"));
	            cod_uorga = data.get("t02cod_uorgl").toString().trim();
	          } else if (data.get("t02cod_uorg") != null) {
	            data.put("t12cod_uorga", data.get("t02cod_uorg"));
	            cod_uorga = data.get("t02cod_uorg").toString().trim();
	          } else {
	            data.put("t12cod_uorga", "");
	          }
	          String des_uorga = paramDao.buscar("spprmA01t12", 0, cod_uorga) != null ? 
	            paramDao.buscar("spprmA01t12", 0, cod_uorga).getDescripcion() : "";
	          data.put("t12des_corta", des_uorga);
	          data.put("t12des_uorga", des_uorga);
	          return new DynaBean(data);
	        } } );
	    } catch (Exception e) {
	      throw new DAOException(this, "No se pudo realizar la consulta de personas en la tabla t02perdp");
	    }
	    return lista;
	  }
	
	
	//07/04/2011
	public Map findPersLocal(String codigo)
	    throws DAOException, Exception
	  {
	    Map datEmp = null;
	    Object[] objs = { 
	      this.constantes.leePropiedad("IND_ESTADO"), 
	      this.constantes.leePropiedad("VALOR_POR_DEFECTO_CERO"), 
	      codigo };

	    datEmp = executeQueryUniqueResult(this.dataSource, this.QUERY4_SENTENCE.toString(), objs);
	    return datEmp;
	}	
	
	/*Metodo Recuperado del ear de produccion:llamado desde la clase PerModForFacade.java */
	public List findByBusquedaGen(Map p)
    throws DAOException, Exception
  {
    String sWhere = "";
    boolean hasParam = false;
    String sOrder = "";
    String sSQL = "";

    String pcriterio = "";
    String pvalor = "";
    String pvigente = "";

    pcriterio = ((String)p.get("criterio")).trim();
    pvalor = ((String)p.get("valor")).trim();
    pvigente = ((String)p.get("vigente")).trim();

    if (pcriterio.equals("0")) {
      if (!sWhere.equals(""))
        sWhere = sWhere + " and ";
      hasParam = true;
      sWhere = sWhere + "t02cod_pers like '" + pvalor.replaceAll("'", "''") + 
        "%'";
    }
    if (pcriterio.equals("1")) {
      if (!sWhere.equals(""))
        sWhere = sWhere + " and ";
      hasParam = true;
      sWhere = sWhere + "t02ap_pate like '%" + 
        pvalor.replaceAll("'", "''") + "%'";
    }
    if (pcriterio.equals("2")) {
      if (!sWhere.equals(""))
        sWhere = sWhere + " and ";
      hasParam = true;
      sWhere = sWhere + "t02nombres like '%" + 
        pvalor.replaceAll("'", "''") + "%'";
    }

    if (hasParam) {
      sWhere = " WHERE (" + sWhere + ")";
    }
    sOrder = " order by t02cod_pers";

    sSQL = "select t02cod_pers,( trim(t02ap_pate) || '  ' || trim(t02ap_mate) || '  ' || trim(t02nombres) )as t02des_repr,t02nombres from t02perdp";
    sSQL = sSQL + sWhere + sOrder;

    ArrayList aLRpta = new ArrayList();
    setIsolationLevel(TX_READ_UNCOMMITTED);
    try {
      aLRpta = (ArrayList)executeQuery(this.dataSource, sSQL.toString());
    } catch (Exception e) {
      throw new DAOException(this, "No se pudo realizar la consulta de uo en la tabla t02perdp");
    }
    setIsolationLevel(-1);
    return aLRpta;
  }
	/*Metodo Recuperado del ear de produccion:llamado desde la clase PerModForFacade.java */
	  public int findNroTotTrabSunat(Map params)
	    throws DAOException, Exception
	  {
	    int nroTrab = 0;
	    Object[] objs = { params.get("t02cod_stat") };
	    setIsolationLevel(TX_READ_UNCOMMITTED);
	    Map mapaNroTrab = executeQueryUniqueResult(this.dataSource, this.QUERY2_SENTENCE.toString(), objs);
	    setIsolationLevel(-1);
	    if (this.log.isDebugEnabled()) this.log.debug("mapaNroTrab " + mapaNroTrab);
	    if (mapaNroTrab != null) {
	      nroTrab = mapaNroTrab.get("cont_trab") != null ? Integer.parseInt(mapaNroTrab.get("cont_trab").toString()) : 0;
	    }
	    return nroTrab;
	  }

   /*        */	
   
	  /*ICAPUNAY - PASE PAS20112A550001380 Modificacion de alertas de solicitudes y movimientos de asistencia - 06/03/2012 */	
	  /**
	   * Metodo que se encarga de buscar el personal activo en la t02perdp por criterio, valor, regimen y seguridad
	   * @param params Map
	   * @return List 
	   * @throws DAOException
	   */

	  public List buscarPersonalActivo_fromT02(Map params)  throws DAOException {    

		  StringBuffer sSQL = new StringBuffer(QUERY_T02PERDP.toString()); 
		  List lista = null;

		  try{
			  String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
			  String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";
			  String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
			  Map seguridad = (HashMap)params.get("seguridad");

			  if (criterio.equals("0")){ //Registro (1551)
				  sSQL.append(" WHERE T02COD_STAT = '1' ");
				  sSQL.append(" AND T02COD_PERS = '").append(valor.trim().toUpperCase()).append( "' ");
			  } else if(criterio.equals("1")){//UO (2A5500)
				  sSQL.append(" WHERE T02COD_STAT = '1' ");
				  sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) = '" ).append( valor.trim().toUpperCase()).append( "' ");
			  }else if(criterio.equals("4") && !valor.equals("")){//Intendencia (2A0000)
				  String intendencia = valor.substring(0,2);
				  sSQL.append(" WHERE T02COD_STAT = '1' ");
				  sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
			  }else{ //Institucional (sunat)
				  sSQL.append(" WHERE T02COD_STAT = '1' ");
			  }		


			  if (regimen.equals("0")) { // Regimen 276 - 728				
				  sSQL.append(" AND T02COD_REL NOT IN ('09','10') ");
			  }else if(regimen.equals("1")){//Regimen 1057
				  sSQL.append(" AND T02COD_REL = '09' ");
			  }else if(regimen.equals("2")){//Modalidad Formativa
				  sSQL.append(" AND T02COD_REL = '10' ");
			  }


			  //criterios de visibilidad
			  if (seguridad != null && seguridad.size()>0) {
				  Map roles = (HashMap) seguridad.get("roles");
				  String uoSeg = (String) seguridad.get("uoSeg");
				  String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
			      if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
			      if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

				  if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {//Visualiza a todos
					  sSQL.append(" AND 1=1 ");
				  }
				  //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
			      else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){	
			      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
			      	sSQL.append(" AND ((SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "') ");
			      	sSQL.append(" OR (SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) IN (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");		        	
			      }
				  //else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null  || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
				  else if (
				  //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO		  
						  roles.get(Constantes.ROL_SECRETARIA) != null
						  || roles.get(Constantes.ROL_JEFE) != null) {
					  sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "' ");
				  } else { //No visualiza ninguno
					  sSQL.append(" AND 1=2 ");
				  }
			  }

			  sSQL.append(" ORDER BY T02COD_PERS ");

			  setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
			  lista = (ArrayList) executeQuery(dataSource, sSQL.toString());
			  setIsolationLevel(-1);

		  }catch (Exception e) {
			  log.error("*** SQL Error ****",e);
			  MensajeBean msg = new MensajeBean();
			  msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
			  msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
			  throw new DAOException (this, msg);     
		  } finally {       
		  }    
		  return lista; 
	  }	
	  /*FIN ICAPUNAY - PASE PAS20112A550001380 Modificacion de alertas de solicitudes y movimientos de asistencia - 06/03/2012 */
	  
	  
	//ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
	  /**
		 * Metodo que se encarga de buscar el personal
		 * @param params Map
		 * @return List 
		 * @throws DAOException
		 */		
		public List buscarPersonal_labor(Map params)  throws DAOException {    

			StringBuffer sSQL = new StringBuffer(QUERY2_SENTENCE.toString()); 
			List lista = null;		
			try{

				String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
				String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";
				String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
				Map seguridad = (HashMap)params.get("seguridad");
				
				if (criterio.equals("0")){
					sSQL.append(" WHERE T02COD_STAT in ('1','0') ");
					sSQL.append(" AND T02COD_PERS = '").append(valor.trim().toUpperCase()).append( "' ");
				} else if(criterio.equals("1")){
					sSQL.append(" WHERE T02COD_STAT = '1' ");
					sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) = '" ).append( valor.trim().toUpperCase()).append( "' ");					
				}else if(criterio.equals("4") && !valor.equals("")){
					String intendencia = valor.substring(0,2);
					sSQL.append(" WHERE T02COD_STAT = '1' ");
					sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
				}else{
					sSQL.append(" WHERE T02COD_STAT = '1' ");
				}
				
				if (regimen.equals("0")) {//276 -728					
					sSQL.append(" AND T02COD_REL NOT IN ('09','10') ");
				}else if(regimen.equals("1")){//1057
					sSQL.append(" AND T02COD_REL = '09' ");
				}else if(regimen.equals("2")){//Modalidad Formativa
					sSQL.append(" AND T02COD_REL = '10' ");
				}else if (regimen.equals("")){//por automatico (planilla y cas)
					sSQL.append(" AND T02COD_REL NOT IN ('10') ");
				}
	
				//criterios de visibilidad
				if (seguridad != null && seguridad.size()>0) {

					Map roles = (HashMap) seguridad.get("roles");
					String uoSeg = (String) seguridad.get("uoSeg");
					String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
				    if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
				    if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

					if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
						sSQL.append(" AND 1=1 ");
					}
					//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
				    else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){	
				      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
				      	sSQL.append(" AND ((SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "') ");
				      	sSQL.append(" OR (SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) IN (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");		        	
				    }
					//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
					else if (
					//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
							roles.get(Constantes.ROL_SECRETARIA) != null
							|| roles.get(Constantes.ROL_JEFE) != null) {
						sSQL.append(" AND ((SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "') ");
					} else {
						sSQL.append(" AND 1=2 ");
					}
				}						
				sSQL.append(" ORDER BY T02COD_PERS ");
				
				setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
				lista = (ArrayList) executeQuery(dataSource, sSQL.toString());
				setIsolationLevel(-1);

			}catch (Exception e) {
				log.error("*** SQL Error ****",e);
				MensajeBean msg = new MensajeBean();
				msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
				msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
				throw new DAOException (this, msg);     
			} finally {       
		    }    
		    return lista; 
		}	
		//FIN ICAPUNAY 09/04/2012 AOM 06A4T11 LABOR EXCEPCIONAL Y COMPENSACIONES-PAS20124E550000064
		
		//ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION DE ASISTENCIA
		/**
		 * Metodo que se encarga de buscar el personal para la calificacion de asistencia
		 * @param params Map
		 * @return List 
		 * @throws DAOException
		 */		
		public List buscarPersonal_Calificacion(Map params)  throws DAOException {    

			StringBuffer sSQL = new StringBuffer(QUERY_CALIFICACION.toString()); 
			List lista = null;
		
			try{
				String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
				String valor = (params.get("valor") != null ) ? params.get("valor").toString():"";
				String regimen = (params.get("regimen") != null ) ? params.get("regimen").toString():"";
				Map seguridad = (HashMap)params.get("seguridad");
				
				if (criterio.equals("0")){//registro
					sSQL.append(" WHERE T02COD_STAT = '1' ");
					sSQL.append(" AND T02COD_PERS = '").append(valor.trim().toUpperCase()).append( "' ");
				} else if(criterio.equals("1")){//uuoo
					sSQL.append(" WHERE T02COD_STAT = '1' ");
					sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) = '" ).append( valor.trim().toUpperCase()).append( "' ");					
				}else if(criterio.equals("2") && !valor.equals("")){//intendencia
					String intendencia = valor.substring(0,2);
					sSQL.append(" WHERE T02COD_STAT = '1' ");
					sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
				}else{//institucional
					sSQL.append(" WHERE T02COD_STAT = '1' ");
				}			

				if (regimen.equals("0")) {//276 -728					
					sSQL.append(" AND T02COD_REL NOT IN ('09','10') ");
				}else if(regimen.equals("1")){//1057
					sSQL.append(" AND T02COD_REL = '09' ");
				}else if(regimen.equals("2")){//Modalidad Formativa
					sSQL.append(" AND T02COD_REL = '10' ");
				}
				
				//criterios de visibilidad
				if (seguridad != null && seguridad.size()>0) {

					Map roles = (HashMap) seguridad.get("roles");
					String uoSeg = (String) seguridad.get("uoSeg");
					String uoAO = (String) seguridad.get("uoAO");//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
				    if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
				    if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO

					if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
						sSQL.append(" AND 1=1 ");
					} 
					//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO 
				    else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){	
				      	if (log.isDebugEnabled()) log.debug("es ROL_ANALISTA_OPERATIVO");
				      	sSQL.append(" AND ((SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "') ");
				      	sSQL.append(" OR (SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) IN (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");		        	
				    }
					//else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
					else if (
					//ICR 02/01/2013 NUEVAS UUOOS ANALISTA OPERATIVO
							roles.get(Constantes.ROL_SECRETARIA) != null
							|| roles.get(Constantes.ROL_JEFE) != null) {
						sSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL,'')||NVL(T02COD_UORG,'')),1,6) LIKE '").append(uoSeg).append( "' ");
					} else {
						sSQL.append(" AND 1=2 ");
					}
				}
						
				sSQL.append(" ORDER BY T02COD_PERS ");
				
				setIsolationLevel(T02DAO.TX_READ_UNCOMMITTED);
				lista = (ArrayList) executeQuery(dataSource, sSQL.toString());
				setIsolationLevel(-1);

			}catch (Exception e) {
				log.error("*** SQL Error ****",e);
				MensajeBean msg = new MensajeBean();
				msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
				msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
				throw new DAOException (this, msg);     
			} finally {       
		    }    
		    return lista; 
		}	
		//FIN ICAPUNAY 13/06/2012 PASE 2012-381 AJUSTES A CALIFICACION ASISTENCIA
		
		//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
		/**
		   * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero		 
		   * @param String unidad
		   * @return String uoJefe
		   * @throws SQLException
		   */
		  public String findUuooJefe(String unidad) throws DAOException {
	    
		    String uoJefe = "";	
		    try {

		    	log.debug("findUuooJefe-unidad: "+unidad); 
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
	        	log.debug("findUuooJefe-uoJefe: "+uoJefe);
		    }
		    catch (Exception e) {
		    	log.error("*** SQL Error ****",e);
				MensajeBean msg = new MensajeBean();
				msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
				msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
		    }
		    finally {
		    }
		    log.debug("findUuooJefe-uoJefe(final): "+uoJefe);
			return uoJefe; 
		  }	  
		  //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro

	
		  /**
		   *  PAS20171U230200001 - solicitud de reintegro  
		   *  Funcion para determinar si un colaborador es cesado		   *  		   *  
		   * @param filtro
		   * @return
		   * @throws DAOException
		   */
		  public boolean esCesado(Map filtro) throws DAOException {
			  Map pers = executeQueryUniqueResult(dataSource, FIND_BY_PK_CESADO.toString(), new Object[]{filtro.get("codPers"), filtro.get("fecCompara")});
			  
			  return (pers != null && pers.get("t02cod_pers")!=null);
		  }
}