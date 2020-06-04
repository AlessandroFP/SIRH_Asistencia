package pe.gob.sunat.servicio.registro.dao;

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

/**
 * DAO de la tabla Spr de la base de datos recauda
 * 
 * @author JVALDEZ
 */
public class SprDAO extends DAOAbstract {
	private DataSource datasource;
	// sentencias SQL.
	private String QUERY1_SENTENCE = "SELECT " + 
          "spr_numruc,spr_correl,spr_ubigeo,spr_nomvia,spr_numer1," +
          "spr_inter1,spr_nomzon,spr_refer1,spr_nombre,spr_tipest," +
          "spr_licenc,spr_tipvia,spr_tipzon,spr_fecact " +
          "FROM spr WHERE ";
  
	private String QUERY2_SENTENCE = "SELECT " + 
          "spr_numruc,spr_correl,spr_ubigeo,spr_nomvia,spr_numer1," +
          "spr_inter1,spr_nomzon,spr_refer1,spr_nombre,spr_tipest," +
          "spr_licenc,spr_tipvia,spr_tipzon ,spr_fecact, " +
          "num_kilom,num_manza,num_depar,num_lote " +
          "FROM spr, outer t1150datspr, ddp " +
          "WHERE ddp_numruc = ? and spr_numruc=num_ruc and spr_correl = num_correl " +
          "and spr_numruc=ddp_numruc and ddp_estado!='20' " +
          "ORDER BY spr_correl";

	private String QUERY3_SENTENCE = "SELECT " + 
          "spr_numruc,spr_correl,spr_ubigeo,spr_nomvia,spr_numer1," +
          "spr_inter1,spr_nomzon,spr_refer1,spr_nombre,spr_tipest," +
          "spr_licenc,spr_tipvia,spr_tipzon ,spr_fecact, " +
          "num_kilom,num_manza,num_depar,num_lote " +
          "FROM spr, outer t1150datspr, ddp " +
          "WHERE ddp_numruc = ? and spr_correl IN ? and spr_numruc=num_ruc and spr_correl = num_correl " +
          "and spr_numruc=ddp_numruc and ddp_estado!='20' " +
          "ORDER BY spr_correl";
  
	private String QUERY4_SENTENCE = "SELECT " + 
			"spr_correl,ind_conleg,num_kilom,num_manza,num_depar,num_lote " +
			"FROM spr, outer t1150datspr " +
			"WHERE spr_numruc = ? and spr_numruc=num_ruc and spr_correl = num_correl " +
			"ORDER BY spr_correl";
  
	// los valores entre corchetes no pueden ser modificados, ya que estos son tomados por 
	// los parsers para reemplazar por los nombres de los campos o condiciones.
	private String INSERT_SENTENCE = "INSERT INTO spr (spr_numruc,spr_correl,spr_ubigeo,spr_nomvia,spr_numer1,spr_inter1,spr_nomzon,spr_refer1,spr_nombre,spr_tipest,spr_licenc,spr_tipvia,spr_tipzon,spr_indmaq,spr_userna,spr_fecact) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private String UPDATE_SENTENCE = "UPDATE spr SET ([CAMPOS]) = ([VALORES]) WHERE 1 = 1 [CONDICION]";
	private String DELETE_SENTENCE = "DELETE FROM spr WHERE 1 = 1 [CONDICION]";
	// definicion de todos los atributos de la tabla.
	private final static String[] PROPIEDADES = new String[]{ "spr_numruc",
															  "spr_correl",
															  "spr_ubigeo",
															  "spr_nomvia",
															  "spr_numer1",
															  "spr_inter1",
															  "spr_nomzon",
															  "spr_refer1",
															  "spr_nombre",
															  "spr_tipest",
															  "spr_licenc",
															  "spr_tipvia",
															  "spr_tipzon",
															  "spr_indmaq",
															  "spr_userna",
															  "spr_fecact"
    };

	/**
	 * 
	 */
	public SprDAO() {
		super();
		// TODO Auto-generated constructor stub
	}
  
	/**
	 * Este constructor del DAO dicierne como crear el datasource
	 * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	 * mejorar la invocacion del dao.
	 * 
	 * @param datasource Object
	 */
	public SprDAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String)datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}
  
	// operaciones de grabacion
	/**
	 * Crea un nuevo establecimiento.
	 *  
	 * @param params Map
	 */
	public void insert(Map params) {
		MensajeBean msg = new MensajeBean();
    
		try {
			Object[] o = new Object[] {	params.get("spr_numruc"),
										params.get("spr_correl"),
										params.get("spr_ubigeo"),
										params.get("spr_nomvia"),
										params.get("spr_numer1"),
										params.get("spr_inter1"),
										params.get("spr_nomzon"),
										params.get("spr_refer1"),
										params.get("spr_nombre"),
										params.get("spr_tipest"),
										params.get("spr_licenc"),
										params.get("spr_tipvia"),
										params.get("spr_tipzon"),
										params.get("spr_indmaq"),
										params.get("spr_userna"),
										params.get("spr_fecact") };

			insert(datasource, INSERT_SENTENCE, o);
		}
		catch (DAOException e) {
			msg.setMensajeerror("Ha ocurrido un error al intentar crear un registro en la SPR : ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
			throw new DAOException (this, msg);
		}
	}

	/**
	* Los nombres de los parametros contenidos en params deben de corresponder a los nombres de los campos de la tabla.
	* Los nombres que vienen en condicion seran tomados para construir la condicion de actualizacion.
	* 
	* @param params Map
   	* @param condicion Map
   	*/
	public void update(Map params, Map condicion) {
		MensajeBean msg = new MensajeBean();
		
		try {
			update(datasource, UPDATE_SENTENCE, PROPIEDADES, params, condicion);
		}
		catch (DAOException e) {
			msg.setMensajeerror("Ha ocurrido un error al intentar actualizar la informacion de la SPR : ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
			throw new DAOException (this, msg);
		}
	}

  /**
   * Los nombres que vienen en condicion seran tomados para construir la condicion de eliminacion.
   * 
   * @param condicion Map
   */
	public void delete(Map condicion) {
		MensajeBean msg = new MensajeBean();
		
		try {
			delete(datasource, DELETE_SENTENCE, PROPIEDADES, condicion);
		}
		catch (DAOException e) {
			msg.setMensajeerror("Ha ocurrido un error al intentar eliminar la informacion de la SPR : ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
			throw new DAOException (this, msg);
		}
	}

  /**
   * <p>Permite realizar busquedas sobre la tabla SPR.
   * <p>Dentro del Map condicion deben de venir los atributos que seran considerados en el 
   * WHERE de busqueda. Los claves deben de corresponder a los nombres de los campos de la
   * tabla SPR.
   * <p>Para crear condiciones con IN, se debe enviar como datos un arreglo de objetos 
   * es decir un Object[]. Este puede contener String o Date, de acuerdo al tipo de dato
   * del campo.
   * <ul>Si parametrizar es true se cargan las siguientes claves adicionales.
   * <li>tipvia_desc, contiene la descripcion segun parametro 058, utiliza spr_tipvia
   * <li>tipzon_desc, contiene la descripcion segun parametro 059, utiliza spr_tipzon
   * <li>dpto_desc, contiene la descripcion segun parametro 030, utiliza spr_ubigeo
   * <li>prov_desc, contiene la descripcion segun parametro 031, utiliza spr_ubigeo
   * <li>dist_desc, contiene la descripcion segun parametro 001, utiliza spr_ubigeo
   * <li>tipest_desc, contiene la descripcion segun parametro 063, utiliza spr_tipest
   * </ul>
   * <p>Considerar que para que parametrizar funcione adecuadamente se deben de haber
   * cargado previamente los parametros con el ParamDAO.
   * 
   * @param condicion Map
   * @param orderBy String[] contiene los nombres de los campos para generar el ordenamiento
   *                         del resultado
   * @param parametrizar boolean indica si se desea o no cargar las descripciones segun la
   *                             tabla de parametros que corresponda.
   * 
   * @return List
   */
	protected final List buscar(Map condicion, String[] orderBy, boolean parametrizar) {
		List rpta = new ArrayList();
		List l = new ArrayList();
		String camposKey = "";
		SQLParser parser = new SQLParser(PROPIEDADES);
		camposKey = parser.parser(condicion, l);
		String ordenamiento = null;
    
		if (orderBy != null) {
			ordenamiento = " ORDER BY ";
			
			for (int i=0;i<orderBy.length;i++) {
				ordenamiento = ordenamiento.concat( orderBy[i]);
				
				if (i+1<orderBy.length)
					ordenamiento = ordenamiento.concat( ",");
			}
		}
    
		Object[] o = l.toArray();
		rpta = executeQuery( datasource, QUERY1_SENTENCE.concat(camposKey.substring(5)).concat( ordenamiento!=null?ordenamiento:"" ), o);
    
		if (parametrizar) {
		  /*
		   * <li>tipvia_desc, contiene la descripcion segun parametro 058, utiliza spr_tipvia
		   * <li>tipzon_desc, contiene la descripcion segun parametro 059, utiliza spr_tipzon
		   * <li>dpto_desc, contiene la descripcion segun parametro 030, utiliza spr_ubigeo
		   * <li>prov_desc, contiene la descripcion segun parametro 031, utiliza spr_ubigeo
		   * <li>dist_desc, contiene la descripcion segun parametro 001, utiliza spr_ubigeo
		   * <li>tipest_desc, contiene la descripcion segun parametro 063, utiliza spr_tipest
		   */
			rpta = parametrizar(rpta, false);
		}
    
		return rpta;
	}
	
  /**
   * Agrega las descripciones de los codigos de acuerdo al parametro que le corresponda.
   * 
   * @param rpta List
   * @param es_cdp 
   * @return List
   */
	private List parametrizar(List rpta, boolean es_cdp) {
		ParamDAO paramDAO = new ParamDAO();
		ParamBean o = null;
		
		for (int i=0;i<rpta.size();i++) {
			((Map)rpta.get(i)).put("tipvia_desc",!"-".equals( ((Map)rpta.get(i)).get("spr_tipvia") ) ? ((o=paramDAO.buscar("recaudaprm058", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("spr_tipvia") ) )!=null?o.getDescripcion().substring(0,4):"-") : "-" );
			((Map)rpta.get(i)).put("tipzon_desc",!"-".equals( ((Map)rpta.get(i)).get("spr_tipzon") ) ? ((o=paramDAO.buscar("recaudaprm059", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("spr_tipzon") ) )!=null?o.getDescripcion().substring(0,4):"-") : "-" );
			((Map)rpta.get(i)).put("dpto_desc",!"-".equals( ((Map)rpta.get(i)).get("spr_ubigeo") ) ? ((o=paramDAO.buscar("recaudaprm030", ParamDAO.TIPO1, ( (String) ((Map)rpta.get(i)).get("spr_ubigeo") ).substring(0,2) ) )!=null?o.getDescripcion().trim():"-") : "-" );
			((Map)rpta.get(i)).put("prov_desc",!"-".equals( ((Map)rpta.get(i)).get("spr_ubigeo") ) ? ((o=paramDAO.buscar("recaudaprm031", ParamDAO.TIPO1, ( (String) ((Map)rpta.get(i)).get("spr_ubigeo") ).substring(0,4) ) )!=null?o.getDescripcion().trim():"-") : "-" );
			((Map)rpta.get(i)).put("dist_desc",!"-".equals( ((Map)rpta.get(i)).get("spr_ubigeo") ) ? ((o=paramDAO.buscar("recaudaprm001", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("spr_ubigeo") ) )!=null?o.getDescripcion().substring(0,31).trim():"-") : "-" );
			((Map)rpta.get(i)).put("tipest_desc",!"-".equals( ((Map)rpta.get(i)).get("spr_tipest") ) ? ((o=paramDAO.buscar("recaudaprm063", ParamDAO.TIPO1, (String) ((Map)rpta.get(i)).get("spr_tipest") ) )!=null?o.getDescripcion():"-") : "-" );
			((Map)rpta.get(i)).put("direccion_desc", genDireccion( (Map)rpta.get(i), es_cdp ) );
		}
    
		return rpta;
	}
	
  /**
   * Genera la direccion completa del establecimiento.
   * @param sprRow Map
   * @param es_cdp 
   * @return String
   */
	private String genDireccion(Map sprRow, boolean es_cdp) {
		StringBuffer sb_dir = new StringBuffer("");
     
		if (!"-".equals(sprRow.get("tipvia_desc")) && !"----".equals(sprRow.get("tipvia_desc"))) {
			sb_dir.append(sprRow.get("tipvia_desc")).append(" ");
		}
		
		if (!"-".equals( ((String)sprRow.get("spr_nomvia")).trim()) ) {
			sb_dir.append(sprRow.get("spr_nomvia")).append(" ");
		}
     
		if (!"-".equals( ((String)sprRow.get("spr_numer1")).trim()) ) {
			sb_dir.append("NRO. ").append(sprRow.get("spr_numer1")).append(" ");
		}
     
		if (sprRow.get("num_kilom") != null && !"-".equals( ((String)sprRow.get("num_kilom")).trim()) ) {
			sb_dir.append("KM. ").append(sprRow.get("num_kilom")).append(" ");
		}
     
		if (sprRow.get("num_manza") != null && !"-".equals( ((String)sprRow.get("num_manza")).trim()) ) {
			sb_dir.append("MZA. ").append(sprRow.get("num_manza")).append(" ");
		}
     
		if (!"-".equals( ((String)sprRow.get("spr_inter1")).trim()) ) {
			sb_dir.append("INT. ").append(sprRow.get("spr_inter1")).append(" ");
		}
     
		if (sprRow.get("num_depar") != null && !"-".equals( ((String)sprRow.get("num_depar")).trim()) ) {
			sb_dir.append("DPTO. ").append(sprRow.get("num_depar")).append(" ");
		}
     
		if (sprRow.get("num_lote") != null && !"-".equals( ((String)sprRow.get("num_lote")).trim()) ) {
			sb_dir.append("LOTE. ").append(sprRow.get("num_lote")).append(" ");
		}
     
		if (!"-".equals(sprRow.get("tipzon_desc")) && !"----".equals(sprRow.get("tipzon_desc"))) {
			sb_dir.append(sprRow.get("tipzon_desc")).append(" ");
		}
      
		if (!"-".equals( ((String)sprRow.get("spr_nomzon")).trim()) ) {
			sb_dir.append(sprRow.get("spr_nomzon")).append(" ");
		}    
     
		if (!es_cdp) {
			if(!"-".equals( ((String)sprRow.get("spr_refer1")).trim()) ) {
				sb_dir.append("(").append(sprRow.get("spr_refer1")).append(") ");
			}
		}
		
		if (!"-".equals( ((String)sprRow.get("spr_ubigeo")).trim()) ) {
			sb_dir.append(sprRow.get("dpto_desc")).append(" ").append(sprRow.get("prov_desc")).append(" ").append(sprRow.get("dist_desc"));
		}
		
		return sb_dir.toString();
	}
	
  /**
   * <p>Obtiene la relacion de los establecimientos anexos.
   * <p>Para esto se enlaza con la tabla t1150datspr y la ddp.
   *  
   * @param ruc String
   * @return List
   */
	public List getEstablecimientosAnexos(String ruc) {
		List rpta = executeQuery( datasource, QUERY2_SENTENCE, new Object[]{ruc});
		
		if (rpta != null)
			rpta = parametrizar(rpta, false);
		return rpta;
	}
	
  /**
   * <p>Obtiene la relacion de los establecimientos anexos.
   * <p>Para esto se enlaza con la tabla t1150datspr y la ddp.
   *  
   * @param ruc String
   * @return List
   */
	public List getEstablecimientosAnexos(String ruc, Short[] correl) {
		return getEstablecimientosAnexos(ruc, correl, false);
	}
  
  /**
   * <p>Obtiene la relacion de los establecimientos anexos, adicionando la direccion completa.
   * <p>Para esto se enlaza con la tabla t1150datspr y la ddp.
   *  
   * @param ruc String
   * @return List
   */
	public List getEstablecimientosAnexos(String ruc, Short[] correl, boolean es_cdp) {
		log.debug("getEstablecimientosAnexos " + correl.length);
		List rpta = executeQuery( datasource, QUERY3_SENTENCE, new Object[]{ruc, correl});
		
		if (rpta != null)
			rpta = parametrizar(rpta, es_cdp);
		
		return rpta;
	}
	
  /**
   * Retorna todas las filas que tengan el ruc.
   * 
   * @param ruc String
   * @return List retorna una lista de mapas en caso de existir registros, o una lista vacia de no encontrar informacion. 
   */
	public List findByRUC(String ruc) {
		Map p = new HashMap();
		p.put("spr_numruc", ruc);
		List rpta = buscar(p);
		return rpta;
	}
	
  /**
   * Retorna todas las filas con el ruc, y mayores o iguales al valor de correl.
   * 
   * @param ruc String
   * @param correl int
   * @return List retorna una lista de mapas en caso de existir registros,
   *              o una lista vacia de no encontrar informacion.
   */
	public List findByRUC(String ruc, int correl) {
		Map p = new HashMap();
		p.put("spr_numruc", ruc);
		p.put(">=spr_correl", new Integer(correl));
		List rpta = buscar(p);
		return rpta;
	}
	
  /**
   * Retorna todas las filas con el ruc y con la lista en correl.
   * 
   * @param ruc String
   * @param correl Short[]
   * @return List retorna una lista de mapas en caso de existir registros, o una lista vacia de no encontrar informacion.
   */
	public List findByRUC(String ruc, Short[] correl) {
		Map p = new HashMap();
		p.put("spr_numruc", ruc);
		p.put("spr_correl", correl);
		List rpta = buscar(p);
		return rpta;
	}
	
  /**
   * Busca por la clave primaria, es decir por ruc y correl.
   * 
   * @param ruc String
   * @param correl int
   * @return Un mapa de encontrar un registro o null de no existir.
   */
	public Map findByPK(String ruc, int correl) {
		Map p = new HashMap();
		p.put("spr_numruc", ruc);
		p.put("spr_correl", new Integer(correl));
		List rpta = buscar(p);
		return (rpta!=null && rpta.size()>0? (Map) rpta.get(0): null);
	}
  
  /**
   * <p>Obtiene la relacion de los establecimientos anexos.
   * <p>Para esto se enlaza con la tabla t1150datspr y la ddp.
   *  
   * @param ruc String
   * @return List
   */
	public List getEstAnexosT1150(String ruc) {
		log.debug("getEstAnexosT1150 " + ruc);
		List rpta = executeQuery( datasource, QUERY4_SENTENCE, new Object[]{ruc});
		return rpta;
	}  
}