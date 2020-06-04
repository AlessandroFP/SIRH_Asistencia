/*
 * Created on 25-abr-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package pe.gob.sunat.servicio.registro.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.dao.SQLParser;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.core.pattern.DynaBean;
/**
 * DAO de la tabla ddp de la base de datos recauda
 * 
 * @author JVALDEZ
 *
 */
public class DdpDAO extends DAOAbstract {
  private DataSource datasource;
  
  private static final String findByNroRUC = "SELECT ddp_lllttt , ddp_nombre , ddp_numruc , ".concat("ddp_numreg , ddp_tpoemp , ddp_tamano , ddp_mclase, ").concat("ddp_ciiu   , ddp_ubigeo , ddp_nomvia , ddp_numer1 , ").concat("ddp_inter1 , ddp_nomzon , ddp_refer1 , ddp_flag22 , ").concat("ddp_estado , ddp_tipvia , ddp_tipzon , ddp_identi, ddp_fecalt ").concat(" FROM ddp ").concat(" WHERE ddp_numruc = ? AND   ddp_estado <> '20' ");

  
  private static final String QUERY1_SENTENCE = "SELECT ddp_lllttt,ddp_nombre,ddp_numruc,"
    .concat("ddp_numreg,ddp_tpoemp,ddp_tamano,ddp_mclase,")	
    .concat("ddp_ciiu,ddp_ubigeo,ddp_nomvia,ddp_numer1,")
    .concat("ddp_inter1,ddp_nomzon,ddp_refer1,ddp_flag22,")
    .concat("ddp_estado,ddp_tipvia,ddp_tipzon,ddp_identi,ddp_fecalt, ddp_fecbaj, ddp_fecact ")
    .concat("FROM ddp WHERE ");
  
  private static final String QUERY3_SENTENCE = "SELECT ddp_lllttt,ddp_nombre,ddp_numruc,"
    .concat("ddp_numreg,ddp_tpoemp,ddp_tamano,ddp_mclase,")
    .concat("ddp_ciiu,ddp_ubigeo,ddp_nomvia,ddp_numer1,")
    .concat("ddp_inter1,ddp_nomzon,ddp_refer1,ddp_flag22,")
    .concat("ddp_estado,ddp_tipvia,ddp_tipzon,ddp_identi,ddp_fecalt,")
    .concat("ddp_fecact,ddp_fecbaj,")
    .concat("num_kilom,num_manza,num_depar,num_lote ")
    .concat("FROM ddp, outer t1144datcon WHERE ddp_numruc = ? AND ddp_numruc = num_ruc ");
  
  private static final String QUERY2_SENTENCE = "SELECT count(*) row_count FROM ddp WHERE ";
  
  // los valores entre corchetes no pueden ser modificados, ya que estos son tomados por 
  // los parsers para reemplazar por los nombres de los campos o condiciones.
  private String INSERT_SENTENCE = "INSERT INTO ddp (ddp_lllttt,ddp_secuen,ddp_nombre,ddp_numruc,ddp_numreg,ddp_tpoemp,ddp_tamano,ddp_identi,ddp_ciiu,ddp_ubigeo,ddp_nomvia,ddp_numer1,ddp_inter1,ddp_nomzon,ddp_refer1,ddp_flag22,ddp_estado,ddp_fecalt,ddp_fecbaj,ddp_tipvia,ddp_tipzon,ddp_doble,ddp_mclase,ddp_reacti,ddp_userna,ddp_fecact) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private String UPDATE_SENTENCE = "UPDATE ddp SET ([CAMPOS]) = ([VALORES]) WHERE 1 = 1 [CONDICION]";
  private static final StringBuffer UPDATE_SENTENCE2 = new StringBuffer("UPDATE ddp SET [COL_EXPR] WHERE 1 = 1 [CONDICION]");
  private String DELETE_SENTENCE = "DELETE FROM ddp WHERE 1 = 1 [CONDICION]";
  
  private final static String[] PROPIEDADES = new String[]{
    "ddp_lllttt","ddp_secuen","ddp_nombre","ddp_numruc","ddp_numreg","ddp_tpoemp","ddp_tamano",
    "ddp_identi","ddp_ciiu","ddp_ubigeo","ddp_nomvia","ddp_numer1","ddp_inter1","ddp_nomzon",
    "ddp_refer1","ddp_flag22","ddp_estado","ddp_fecalt","ddp_fecbaj","ddp_tipvia","ddp_tipzon",
    "ddp_doble","ddp_mclase","ddp_reacti","ddp_userna","ddp_fecact"
  };
  
  /**Consulta de los Contribuyentes por RUC */
	//private static final String QUERY4_SENTENCE = "SELECT ddp_numruc , ddp_numreg , ddp_estado "		
  	private static final String QUERY4_SENTENCE = "SELECT ddp_numruc , ddp_numreg , ddp_estado, ddp_flag22, ddp_nombre "
  		+ " FROM ddp "
		+ " WHERE ddp_numruc = ?";
  	
  	
  	//rortizq 26set011
  	private static final String QUERY_SENTENCE_SANCION = "SELECT ddp_numruc , ddp_numreg , ddp_estado, ddp_ubigeo "		
  		+ " FROM ddp "
		+ " WHERE ddp_numruc = ?";	
		
  	
     //Querys para  RUC de  oficio 
    private static final String QRY_DDS_T1144= "select ddp_numruc,ddp_nombre,dds_fecnac, ddp_ubigeo, ddp_nomvia, ".concat( 
	" ddp_numer1, ddp_inter1,ddp_nomzon, ddp_refer1, ddp_tipvia, ddp_tipzon, dds_telef1, ").concat(
	" num_kilom, num_manza, num_depar, num_lote,num_telef1, cod_correo1,ddp_tpoemp,ddp_numreg,").concat(
	" ddp_estado,ddp_tamano, ddp_flag22, ddp_ciiu, ddp_identi, dds_inicio, num_telef2, num_telef3, num_telef4, num_fax").concat(
	" from ddp, dds, outer t1144datcon ").concat(
	" where ddp_numruc=dds_numruc ").concat(
	" and ddp_numruc=? ").concat(
	" and ddp_numruc=num_ruc ");
	
    private static final String QRY5_DDP_T1144= "select ddp_numruc,ddp_nombre, ddp_ubigeo, ddp_nomvia, ".concat( 
	" ddp_numer1, ddp_inter1,ddp_nomzon, ddp_refer1, ddp_tipvia, ddp_tipzon, ").concat(
	" num_kilom, num_manza, num_depar, num_lote,num_telef1, cod_correo1,ddp_tpoemp,ddp_numreg,").concat(
	//MLL " ddp_tpoemp,ddp_tamano,ddp_flag22,ddp_estado ,ddp_ciiu").concat(
	" ddp_tpoemp,ddp_tamano,ddp_flag22,ddp_estado ,ddp_ciiu, ddp_identi ").concat(
	" from ddp,  outer t1144datcon ").concat(
	" where ddp_numruc=? "). concat(	
	" and ddp_numruc=num_ruc ");
	   
	
	//Querys  para la busqueda de ruc de oficio
		
    private static final String QRY_OFICIO_FECHA_NUMREG =" select unique ddp_numruc, ddp_nombre,ddp_fecalt,ddp_tpoemp, dds_docide, "
		  .concat( "dds_nrodoc, ddp_ubigeo,ddp_tipzon,ddp_nomzon,ddp_tipvia, ")
		  .concat( " ddp_nomvia,ddp_numer1,num_kilom,num_manza, num_lote,num_depar, ")
		  .concat(" ddp_inter1, ddp_refer1, tip_alta_nidi, tip_alta_oficio,ddp_numreg, ")
		  .concat(" num_resalt, fec_notalt")
		  .concat(" from  ddp, dds, t1609rucofi ofi, outer t1144datcon con where ")
		  .concat(" ddp_numruc=dds_numruc and ddp_numruc=con.num_ruc and ")
		  .concat(" ofi.num_ruc=ddp_numruc and" )
		  //.concat(" ind_condicion= ? and" )
		  .concat("  ddp_fecalt>=? and")
		  .concat("  ddp_fecalt<=? ")
		  .concat("  and ddp_numreg= ?  ");   
	
    private static final String QRY_OFICIO_FECHA_NUMREG_TIPOFICIO ="select unique ddp_numruc, ddp_nombre,ddp_fecalt,ddp_tpoemp, dds_docide, "
		  .concat( "dds_nrodoc, ddp_ubigeo,ddp_tipzon,ddp_nomzon,ddp_tipvia, ")
		  .concat( " ddp_nomvia,ddp_numer1,num_kilom,num_manza, num_lote,num_depar, ")
		  .concat(" ddp_inter1, ddp_refer1, tip_alta_nidi, tip_alta_oficio,ddp_numreg, ")
		  .concat(" num_resalt, fec_notalt")
		  .concat(" from  ddp, dds, t1609rucofi ofi, outer t1144datcon con where ")
		  .concat(" ddp_numruc=dds_numruc and ddp_numruc=con.num_ruc and ")
		  .concat(" ofi.num_ruc=ddp_numruc and" )
		  //.concat(" ind_condicion= ? and" )
		  .concat("  ddp_fecalt>=? and")
		  .concat("  ddp_fecalt<=? ")
		  .concat("  and ddp_numreg= ?  and ")		  
	      .concat("  tip_alta_oficio = ?  ");
	
	//25/05/2008 H.A.A: querys para el modulo SIGEDA, para la consulta del RUC 
    private static StringBuffer Query_RUC_withLike1 = new StringBuffer("select ddp_numruc,ddp_nombre,ddp_numreg from ddp");	
    private static StringBuffer Query_RUC_withLike2 = new StringBuffer("select CAST(count(*) as Integer) as contador from ddp");
	
    //19jul2010
    private StringBuffer QUERY_RAZONSOCIAL = new StringBuffer(" SELECT " )
	.append(" DDP_NOMBRE FROM DDP WHERE DDP_NUMRUC = ? ");
    
    /*MLL Sincronizacion*/
    private StringBuffer QUERY0_SENTENCE = new StringBuffer("SELECT ddp_lllttt,ddp_nombre,ddp_numruc,")
    .append("ddp_numreg,ddp_tpoemp,ddp_tamano,ddp_mclase,")
    .append("ddp_ciiu,ddp_ubigeo,ddp_nomvia,ddp_numer1,")
    .append("ddp_inter1,ddp_nomzon,ddp_refer1,ddp_flag22,")
    .append("ddp_estado,ddp_tipvia,ddp_tipzon,ddp_identi,ddp_fecalt, ddp_fecbaj, ddp_fecact ")
    .append("FROM ddp WHERE ddp_numruc = ?");

    /*MLL*/
    
  /**
   * 
   */
  public DdpDAO() {
    super();
    // TODO Auto-generated constructor stub
  }
  /**
   * 
   * Este constructor del DAO dicierne como crear el datasource
   * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
   * mejorar la invocacion del dao.
   * 
   * @param datasource Object
   */
  public DdpDAO(Object datasource) {
    if (datasource instanceof DataSource)
      this.datasource = (DataSource)datasource;
    else if (datasource instanceof String)
      this.datasource = getDataSource((String)datasource);
    else
      throw new DAOException(this, "Datasource no valido");
  }
  // operaciones de grabacion
  /**
   * Crea un nuevo contribuyente.
   *  
   * @param params Map
   */
  public void insert(Map params){
    MensajeBean msg = new MensajeBean();
    
    try {
      
      Object[] o = new Object[] {  
          params.get("ddp_lllttt"), params.get("ddp_secuen"), params.get("ddp_nombre"), params.get("ddp_numruc"), params.get("ddp_numreg"), params.get("ddp_tpoemp"), params.get("ddp_tamano"),
          params.get("ddp_identi"), params.get("ddp_ciiu"), params.get("ddp_ubigeo"), params.get("ddp_nomvia"), params.get("ddp_numer1"), params.get("ddp_inter1"), params.get("ddp_nomzon"),
          params.get("ddp_refer1"), params.get("ddp_flag22"), params.get("ddp_estado"), params.get("ddp_fecalt"), params.get("ddp_fecbaj"), params.get("ddp_tipvia"), params.get("ddp_tipzon"),
          params.get("ddp_doble"), params.get("ddp_mclase"), params.get("ddp_reacti"), params.get("ddp_userna"), params.get("ddp_fecact")
      };
      
      insert(datasource, INSERT_SENTENCE, o);
      
    } catch (DAOException e){
      msg.setMensajeerror("Ha ocurrido un error al intentar crear un registro en la DDP : ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);
    }
    
  }
  
  /**
   * Los nombres de los parametros contenidos en params deben de corresponder
   * a los nombres de los campos de la tabla.
   * Los nombres que vienen en condicion seran tomados para construir la 
   * condicion de actualizacion.
   * 
   * @param params Map
   * @param condicion Map
   */
  public void update(Map params, Map condicion){
    MensajeBean msg = new MensajeBean();
    try {
    	update(datasource, UPDATE_SENTENCE, PROPIEDADES, params, condicion);
      
    } catch (DAOException e){
      msg.setMensajeerror("Ha ocurrido un error al intentar actualizar la informacion de la DDP : ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);
    }
    
  }
  
  /**
   * Los nombres de los parametros contenidos en params deben de corresponder
   * a los nombres de los campos de la tabla.
   * Los nombres que vienen en condicion seran tomados para construir la 
   * condicion de actualizacion.
   * 
   * @param params Map
   * @param condicion Map
   */
  public void update2(Map params, Map condicion){   
    	update2(datasource, UPDATE_SENTENCE2.toString(), PROPIEDADES, params, condicion);
  }
  /**
   * Los nombres que vienen en condicion seran tomados para construir la 
   * condicion de eliminacion.
   * 
   * @param condicion Map
   */
  public void delete(Map condicion){
    MensajeBean msg = new MensajeBean();
    try {
      
      delete(datasource, DELETE_SENTENCE, PROPIEDADES, condicion);
      
    } catch (DAOException e){
      msg.setMensajeerror("Ha ocurrido un error al intentar eliminar la informacion de la DDP : ".concat( e.getMessage() ));
      msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
      throw new DAOException (this, msg);
    }
    
  }
  
  // operaciones de busquedas
  
  /**
   * <p>Permite realizar busquedas sobre la tabla ddp.
   * <p>Dentro del Map condicion deben de venir los atributos que seran considerados en el 
   * WHERE de busqueda. Los claves deben de corresponder a los nombres de los campos de la
   * tabla ddp.
   * <p>Para crear condiciones con IN, se debe enviar como datos un arreglo de objetos 
   * es decir un Object[]. Este puede contener String o Date, de acuerdo al tipo de dato
   * del campo.
   * 
   * @param condicion Map
   * @return List
   */
  protected final List buscar(Map condicion, String[] orderBy, boolean parametrizar){
    List rpta = new ArrayList();
    
    List l = new ArrayList();
    String camposKey = "";
    
    SQLParser parser = new SQLParser(PROPIEDADES);
    camposKey = parser.parser(condicion, l);
    
    String ordenamiento = null;
    if (orderBy != null){
      ordenamiento = " ORDER BY ";
      for (int i=0;i<orderBy.length;i++){
        ordenamiento = ordenamiento.concat( orderBy[i]);
        if (i+1<orderBy.length)
          ordenamiento = ordenamiento.concat( ",");
      }
    }
    Object[] o = l.toArray();
    rpta = executeQuery( datasource, QUERY1_SENTENCE.concat(camposKey.substring(5)).concat( ordenamiento!=null?ordenamiento:"" ), o);
    if (parametrizar){
      /*
       * 
       */
      boolean es_cdp = false;
      if ( condicion.containsKey("flag_cdp")){
        es_cdp = ( (Boolean) condicion.get("flag_cdp") ).booleanValue();
      }
      
      rpta = parametrizar(rpta, es_cdp,true);
    }
    
    return rpta;
  }
  /**
   * Agrega las descripciones de los codigos de acuerdo al parametro que le corresponda.
   * 
   * @param rpta List
   * @param es_cdp boolean
   * @return List
   */
  private List parametrizar(List rpta, boolean es_cdp,boolean ubigeo){
    ParamDAO paramDAO = new ParamDAO();
    ParamBean o = null;
    for (int i=0;i<rpta.size();i++){
      
      log.debug("buscando en recaudaprm003");
      ((Map)rpta.get(i)).put("ddp_numreg_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_numreg")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm003", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_numreg") ))!=null?o.getDescripcion().substring(11,35).trim():"-") : "-" );
      log.debug("buscando en recaudaprm006");
      ((Map)rpta.get(i)).put("ddp_tpoemp_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_tpoemp")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm006", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_tpoemp") ))!=null?o.getDescripcion().substring(0,35).trim():"-") : "-" );
      log.debug("buscando en recaudaprm009");
      ((Map)rpta.get(i)).put("ddp_tamano_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_tamano")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm009", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_tamano") ))!=null?o.getDescripcion().substring(0,20).trim():"-") : "-" );
      log.debug("buscando en recaudaprm026");
      ((Map)rpta.get(i)).put("ddp_flag22_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_flag22")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm026", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_flag22") ))!=null?o.getDescripcion().substring(0,35).trim():"-") : "-" );
      log.debug("buscando en recaudaprm178");
      ((Map)rpta.get(i)).put("ddp_estado_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_estado")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm178", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_estado") ))!=null?o.getDescripcion().substring(0,25).trim():"-") : "-" );
      log.debug("buscando en recaudaprm058");
      ((Map)rpta.get(i)).put("ddp_tipvia_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_tipvia")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm058", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_tipvia") ))!=null?o.getDescripcion().substring(0,4).trim():"-") : "-" );
      log.debug("buscando en recaudaprm059");
      ((Map)rpta.get(i)).put("ddp_tipzon_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_tipzon")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm059", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_tipzon") ))!=null?o.getDescripcion().substring(0,4).trim():"-") : "-" );
      log.debug("buscando en recaudaprm002");
      ((Map)rpta.get(i)).put("ddp_ciiu_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_ciiu")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm002", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_ciiu") ))!=null?o.getDescripcion().substring(0,40).trim():"-") : "-" );
      log.debug("buscando en recaudaprm030");
      ((Map)rpta.get(i)).put("depa_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_ubigeo")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm030", ParamDAO.TIPO1, ( (String) ((Map)rpta.get(i)).get("ddp_ubigeo") ).substring(0,2) ))!=null?o.getDescripcion().trim():"-") : "-" );
      log.debug("buscando en recaudaprm031");
      ((Map)rpta.get(i)).put("prov_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_ubigeo")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm031", ParamDAO.TIPO1, ( (String) ((Map)rpta.get(i)).get("ddp_ubigeo")).substring(0,4) ))!=null?o.getDescripcion().trim():"-") : "-" );
      log.debug("buscando en recaudaprm001");
      ((Map)rpta.get(i)).put("dist_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_ubigeo")).trim() ) ?
              ((o=paramDAO.buscar("recaudaprm001", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("ddp_ubigeo") ))!=null?o.getDescripcion().substring(0,31).trim():"-") : "-" );
      ((Map)rpta.get(i)).put("direccion_desc", genDireccion( (Map)rpta.get(i), es_cdp, ubigeo ) );
      ((Map)rpta.get(i)).put("identi_desc", 
          !"-".equals( ((String)((Map)rpta.get(i)).get("ddp_identi")).trim() ) ?
              (((String)((Map)rpta.get(i)).get("ddp_identi")).trim().equals("01")?"PERSONA NATURAL":(((String)((Map)rpta.get(i)).get("ddp_identi")).trim().equals("02")?"PERSONA JURIDICA":"-")) : "-" );      
    }
    return rpta;
  }
  /**
   * Retorna la cantidad de elementos que cumplen con la condicion.
   * 
   * @param condicion Map
   * @return int
   */
  public int count(Map condicion){
    
    List l = new ArrayList();
    String camposKey = "";
    
    SQLParser parser = new SQLParser(PROPIEDADES);
    camposKey = parser.parser(condicion, l);
    
    Object[] o = l.toArray();
    int cantidad = 0;
    Map rpta = executeQueryUniqueResult( datasource, QUERY2_SENTENCE.concat(camposKey.substring(5)), o);
    if (rpta != null){
      cantidad = ((BigDecimal)rpta.get("row_count")).intValue();
    }
    
    return cantidad;
  }
  
  /**
   * 
   * @param ruc String
   * @return Map
   */
  public Map findByPK(String ruc){
    return findByPK(ruc, false);
  }
  /**
   * 
   * @param ruc String
   * @param parametrizar boolean
   * @return Map
   */
  public Map findByPK(String ruc, boolean parametrizar){
    return findByPK(ruc, parametrizar, false); 
  }
  /**
   * 
   * @param ruc String
   * @param parametrizar boolean
   * @param cdp boolean Parametro para poder generar la direccion sin la referencia
   * @return Map
   */
  public Map findByPK(String ruc, boolean parametrizar, boolean cdp){
    Map p = new HashMap();
    p.put("ddp_numruc", ruc);
    
    p.put("flag_cdp", new Boolean(cdp));
    
    List r = buscar(p, parametrizar);
    if (r.isEmpty() || r==null || r.size()==0){
      return null;
    }
    return (Map)r.get(0); 
  }
  
  /**
   * 
   * @param ruc String[]
   * @return List
   */
  public List listaRucs(String ruc[]){
    return listaRucs(ruc, false);
  }
  /**
   * 
   * @param ruc String[]
   * @return List
   */
  public List listaRucs(String ruc[], boolean parametrizar){
    Map p = new HashMap();
    p.put("ddp_numruc", ruc);
    List r = buscar(p, parametrizar);
    
    return r;
  }
  /**
   * Genera la direccion completa del establecimiento.
   *    
   * @param row Map
   * @param es_cdp 
   * @return String
   */
  private String genDireccion(Map row, boolean es_cdp,boolean ubigeo){
    StringBuffer sb_dir = new StringBuffer("");
    log.debug("ddp->row " + row);
    if(!"-".equals(row.get("ddp_tipvia_desc")) && !"----".equals(row.get("ddp_tipvia_desc"))){
      sb_dir.append(row.get("ddp_tipvia_desc")).append(" ");
    }
    if(!"-".equals( ((String)row.get("ddp_nomvia")).trim()) ){
      sb_dir.append( ((String)row.get("ddp_nomvia")).trim() ).append(" ");
    }
    if(!"-".equals( ((String)row.get("ddp_numer1")).trim()) ){
      sb_dir.append("NRO. ").append(((String)row.get("ddp_numer1")).trim()).append(" ");
    }
    if(row.get("num_kilom") != null && !"-".equals( ((String)row.get("num_kilom")).trim()) ){
      sb_dir.append("KM. ").append(((String)row.get("num_kilom")).trim()).append(" ");
    }
    if(row.get("num_manza") != null && !"-".equals( ((String)row.get("num_manza")).trim()) ){
      sb_dir.append("MZA. ").append(((String)row.get("num_manza")).trim()).append(" ");
    }
    if(!"-".equals( ((String)row.get("ddp_inter1")).trim()) ){
      sb_dir.append("INT. ").append(((String)row.get("ddp_inter1")).trim()).append(" ");
    }
    if(row.get("num_depar") != null && !"-".equals( ((String)row.get("num_depar")).trim()) ){
      sb_dir.append("DPTO. ").append(((String)row.get("num_depar")).trim()).append(" ");
    }
    if(row.get("num_lote") != null && !"-".equals( ((String)row.get("num_lote")).trim()) ){
      sb_dir.append("LOTE. ").append(((String)row.get("num_lote")).trim()).append(" ");
    }
    if(!"-".equals(row.get("ddp_tipzon_desc")) && !"----".equals(row.get("ddp_tipzon_desc"))){
      sb_dir.append(row.get("ddp_tipzon_desc")).append(" ");
    }
    if(!"-".equals( ((String)row.get("ddp_nomzon")).trim()) ){
      sb_dir.append(((String)row.get("ddp_nomzon")).trim()).append(" ");
    }
    if ( !es_cdp) {
      if(!"-".equals( ((String)row.get("ddp_refer1")).trim()) ){
        sb_dir.append("(").append(((String)row.get("ddp_refer1")).trim()).append(") ");
      }
    }
    if(ubigeo){
    	if(!"-".equals( ((String)row.get("ddp_ubigeo")).trim()) ){
    		sb_dir.append(row.get("depa_desc")).append(" - ").append(row.get("prov_desc")).append(" - ").append(row.get("dist_desc"));
    	}
    }
    return sb_dir.toString();
  }
  
  /**
   * Retorna la informacion del ruc parametrizada,
   * la direccion del contribuyente es obtenida de las tablas ddp y t1144datcon
   * @param ruc
   * @return Map
   * @author jleon8
   */
  public Map findByPKDirecc(String ruc,boolean ubigeo) {
    return findByPKDirecc(ruc, true,ubigeo);
  }
  /**
   * Retorna la informacion del ruc parametrizada,
   * la direccion del contribuyente es obtenida de las tablas ddp y t1144datcon
   * @param ruc
   * @return Map
   * @author jleon8
   */
  public Map findByPKDirecc(String ruc, boolean parametrizar,boolean ubigeo) {
    return findByPKDirecc(ruc, parametrizar, true,ubigeo);
  }
  /**
   * Retorna la informacion del ruc parametrizada,
   * la direccion del contribuyente es obtenida de las tablas ddp y t1144datcon
   * @param ruc
   * @return Map
   * @author jleon8
   */
  public Map findByPKDirecc(String ruc, boolean parametrizar, boolean es_cdp,boolean ubigeo) {
    
    List rpta = super.executeQuery(datasource, QUERY3_SENTENCE, new String[] { ruc });
    
    if (rpta.isEmpty()) {
      return null;
    }
    if (parametrizar)
      rpta = parametrizar(rpta, es_cdp,ubigeo);
    
    return (Map) rpta.get(0);
  }
  
  /**
   * Este metodo es utilizado por el webservice de ruc, en este caso se retorna un 
   * javabean estatico en lugar de un objeto Map, para simplificar el modelo del 
   * webservice.
   *  
   * @param ruc String
   * @return DdpBean
   */  
  public Map findByRuc(String ruc){
    Map p = new HashMap();
    p.put("ddp_numruc", ruc);
    p.put("!ddp_estado", "20");
    List r = buscar(p, true);
    if (r.isEmpty()){
      return null;
    }
    return (HashMap)r.get(0);
  }
  
  /**
   * Este metodo es utilizado por el webservice de ruc, en este caso se retorna una cadena con 
   * el domicilio legal 
   *  
   * @param ruc String
   * @return String
   */    
  public String getDomicilioLegal(String nroRuc){
    List r = executeQuery( datasource, QUERY3_SENTENCE.concat(" AND ddp_estado!='20' "), new Object[]{nroRuc});
    r = parametrizar(r, false,true);
    if (r==null || r.isEmpty()){
      return null;
    }
    return (String) ((HashMap)r.get(0)).get("direccion_desc"); 	  
  }
  public List listaRucsOrden(String rucs[], String orden[], boolean parametrizar) {
      Map condicion = new HashMap();
      condicion.put("ddp_numruc", rucs);
      List r = buscar(condicion, orden, parametrizar);
      return r;
  }
  
  /**
	 * Busqueda por RUC
	 * @param nroRuc
	 * @return  Mapa con el registro encontrado
	 * @author JESQUIV2
	 * @since 03-feb-2006
	 */
	public Map findByNroRUCall(String nroRuc){
		return executeQueryUniqueResult(datasource,QUERY4_SENTENCE, new Object[] {nroRuc});
	}	
	
		  /**
			  * M?do que realiza un join entre las tablas dds y t1144 para obtener la informaci?  * del contribuyente
			  * @param ruc String
			  * @return Map
			  * @author Narista
			  * @since  02/11/2006
			  */
			 public Map joinWithDdsT1144( String ruc) {
				 Object[] o = new Object[] { ruc };
				 List lista = new ArrayList();
				 lista = executeQuery(datasource,QRY_DDS_T1144,o );		 
			     Map a = new HashMap();
			     if (lista.size()>0){
			    	 lista = parametrizar(lista, false, true);
			    	 a = (Map)lista.get(0);   
			    	 
			     }else {
			    	 a=null;
			     }
			     return a; 	   
			 }
	 /**
	  * Metodo que realiza un join entre la tabla t1144 para obtener la informacion del contribuyente
	  * @param ruc String
	  * @return Map
	  * @author Narista
	  * @since 02/11/2006
	  */
	 public Map joinWithT1144( String ruc) {
		 Object[] o = new Object[] { ruc };
		 List lista = new ArrayList();
		 lista = executeQuery(datasource,QRY5_DDP_T1144,o );		 
	     Map a = new HashMap();
	     if (lista.size()>0){
	    	 lista = parametrizar(lista, false, true);
	    	 a = (Map)lista.get(0);   
	    	 
	     }else {
	    	 a=null;
	     }
	     return a; 	   
	 }
	 
	 /**
		 * Busca los rucs inscritos de oficio dentro de un rango de fechas determinada
		 * motivo y dependencia.
		 * @param map
		 * @return List
		 * @throws SQLException 
		 * @author Narista
	     * @since 04/01/2007 
		 */
		public List findByRucOficioFecha (Map parametros) throws SQLException {
			log.debug("findByRucOficioFecha"+parametros); 
		  List resp=new ArrayList();
		  FechaBean fh=new FechaBean();
		  FechaBean fh2=new FechaBean();
		  Object[] o = null;
		  String  query = this.QRY_OFICIO_FECHA_NUMREG;
		  log.debug("query"+query); 
		  fh.setFecha(parametros.get("fecini").toString());
		  fh2.setFecha(parametros.get("fecfin").toString().concat(" 23:59:59") ,"dd/MM/yyyy HH:mm:ss");		  
		  o = new Object[] { fh.getSQLDate(),fh2.getSQLDate(), parametros.get("numreg").toString()};
          
          if (!parametros.get("motivo").toString().equals("-")){
        	  query= this.QRY_OFICIO_FECHA_NUMREG_TIPOFICIO;  
        	  o = new Object[] { fh.getSQLDate(),fh2.getSQLDate(), parametros.get("numreg").toString(), parametros.get("motivo").toString()};
          }
          log.debug("query"+query);
		  resp = executeQuery(datasource,query+" order by ddp_fecalt desc",o );			    
		     /*if (resp.size()>0){
		    	 resp = parametrizar(resp);		    	 
		     }*/
		     return resp;			  
		}
		
		 /*** Henry Agapito ***/
		  /**
		  * Este metodo retorna un listado de los ruc contenidos
		  * en objetos Map, segun los parametros enviados.
		  * Realiza las búsquedas utilizando "like"
		  * claves que pueden venir en params:
		  * <ul>
		  * <li>ddp_numruc
		  * <li>ddp_nombre
		  * </ul>
		  * @param params DynaBean
		  * @return List
		  * Contenido: Una Lista con Map's conteniendo los siguientes valores:
		  *            ddp_numruc, ddp_nombre
		  * @throws DAOException
		  */	
		  public List findByNumRucandNombreWithLike(DynaBean params) throws DAOException {
			   List r = new ArrayList();
			   StringBuffer queryactual1 = new StringBuffer("select ddp_numruc,ddp_nombre,ddp_numreg from ddp");
			   StringBuffer queryactual2 = new StringBuffer("select CAST(count(*) as Integer) as contador from ddp");
			     MensajeBean msg = new MensajeBean();
			     try {
			         String condicion = "";
			         List objs = new ArrayList();
			         if(params.isSet("ddp_numruc") && params.isSet("ddp_nombre")){
		        		 condicion = condicion + " where ddp_numruc LIKE ?";
		        		 objs.add(params.getString("ddp_numruc"));
		        		 condicion = condicion + " AND ddp_nombre LIKE ? ";
		        		 objs.add(params.getString("ddp_nombre")); 
		        	 }
		        	 else{
		        		 if(params.isSet("ddp_numruc"))
		        		 {
		        			 condicion = condicion + " where ddp_numruc LIKE ? ";
		        			 objs.add(params.getString("ddp_numruc"));
		        		 } 
		        		 if(params.isSet("ddp_nombre"))
		        		 {
		        			 condicion = condicion + " where ddp_nombre LIKE ? ";
		        			 objs.add(params.getString("ddp_nombre"));
		        		 }
		        	 }
			         if(params.isSet("count") && params.getString("count").equals("si")){
			        	  log.debug(Query_RUC_withLike2 +" "+ condicion  + " " + objs.size());
					      log.debug("objs = "+objs.size());
					      log.debug("objs = "+objs);
					      
					      r = executeQuery( datasource, Query_RUC_withLike2.append(condicion).toString(), objs.toArray());
					      condicion="";
					      Query_RUC_withLike2 = queryactual2;
			         }
			         else{
			        	  log.debug(Query_RUC_withLike1 +" "+ condicion  + " " + objs.size());
					      log.debug("objs = "+objs.size());
					      log.debug("objs = "+objs);
					      
					      r = executeQuery( datasource, Query_RUC_withLike1.append(condicion).toString(), objs.toArray());
					      condicion="";
					      Query_RUC_withLike1 = queryactual1;
			         }
			      
			     } catch (DAOException e){
			         log.error("*** ERROR *** : ", e);
			         msg.setMensajeerror("Ha ocurrido un error al acceder a los datos de la tabla ddp en el metodo findByNumRucandNombreWithLike : ".concat( e.getMessage() ));
			         msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
			         throw new DAOException (this, msg);
			       }
			     return r;
		  }
		  
		  /*** Henry Agapito ***/
		  /**
		  * Este metodo retorna un listado de los ruc contenidos
		  * en objetos Map, segun los parametros enviados.
		  * Realiza las búsquedas utilizando "like"
		  * claves que pueden venir en params:
		  * <ul>
		  * <li>ddp_numruc
		  * <li>ddp_nombre
		  * </ul>
		  * @param params DynaBean
		  * @return List
		  * Contenido: Una Lista con Map's conteniendo los siguientes valores:
		  *            ddp_numruc, ddp_nombre
		  * @throws DAOException
		  */	
		  public List findByRucandFlag22(java.util.Map p) throws DAOException {
			  List r = new ArrayList();
			  String sql = "";
		      //String condicion="";
			  List objs = new ArrayList();
		      sql =  "SELECT replace(SUBSTR(ddp_nombre,1,50),'\"','') ddp_nombre, DECODE(ddp_flag22,'12','NO HABIDO',' ') ddp_flag22 FROM ddp where ddp_numruc = ?";
		      objs.add(p.get("ddp_numruc"));
			  
     		 r = executeQuery( datasource, sql, objs.toArray());
    
			  return r;
		  }
		  
		  /**
			 * Metodo que se encarga de buscar la razon social de un contribuyente
			 * @param ruc String numero de ruc 
			 * @return String
			 * @throws DAOException
			 */
			
			public String obtenerRazonSocial(String ruc)
				throws DAOException {
				Map mapa = null;
				Object[] objs = new Object[] { ruc };
				mapa = executeQueryUniqueResult(datasource, QUERY_RAZONSOCIAL.toString(), objs);	
			    return (mapa!=null && mapa.get("ddp_nombre")!=null)?mapa.get("ddp_nombre").toString():"";
		    }
			
		    public Map findByNroRUC(String nroRuc)
		    {
		        return executeQueryUniqueResult(datasource, findByNroRUC, new Object[] {
		            nroRuc
		        });
		    }
		    
	/*MLL Sincronizacion*/

		public Map findRucByPK(String ruc) {
		      Map a = new HashMap();
		      a = executeQueryUniqueResult(this.datasource, QUERY0_SENTENCE.toString(), new Object[] { ruc });
		      return a;
		}
		    
	
		/**
		 * Busqueda por RUC
		 * @param nroRuc
		 * @return  Mapa con el registro encontrado
		 * @author rortizq
		 * @since 26set2011
		 */
		public Map findByNroRUCall_Sancion(String nroRuc){
			return executeQueryUniqueResult(datasource,QUERY_SENTENCE_SANCION, new Object[] {nroRuc});
		}
}
