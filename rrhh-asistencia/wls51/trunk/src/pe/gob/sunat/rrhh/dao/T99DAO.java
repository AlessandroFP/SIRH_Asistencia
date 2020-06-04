package pe.gob.sunat.rrhh.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.cache.interfaz.Param;
import pe.gob.sunat.framework.util.date.FechaBean;
//import pe.gob.sunat.sol.IncompleteConversationalState;

/**
 * @author CGARRATT
 * 
 * Clase que accede a la tabla t99codigos, basada en la estructura definida en
 * la base de datos SP.
 * 
 * @since dao-1.0
 * 
 */
public class T99DAO extends DAOAbstract implements Param{

	private static final Logger log = Logger.getLogger(T99DAO.class);
	
	  private String QUERY1_SENTENCE = " SELECT T99COD_TAB, T99TIP_DESC, T99CODIGO, T99DESCRIP, T99SIGLAS FROM T99CODIGOS WHERE T99COD_TAB = ? AND T99TIP_DESC = 'D' ";
      
	  private String QUERY2_SENTENCE = " INSERT INTO T99CODIGOS (T99COD_TAB, T99TIP_DESC, T99CODIGO, T99DESCRIP, T99SIGLAS, T99ESTADO, T99COD_USER, T99_FACTUAL) " +
	  								   " VALUES(?,?,?,?,?,?,?,?) ";
	  
	  private String QUERY5_SENTENCE = " UPDATE T99CODIGOS SET T99DESCRIP= ? ,T99SIGLAS = ? , T99TIPO = ?  " +
      " WHERE T99COD_TAB = ? AND T99CODIGO = ? ";
	  
	  //JRR - 08/04/2009
	  private final StringBuffer FIND_PARAM_BY_CODTAB_CODIGO = new StringBuffer("SELECT "
		).append("t99cod_tab, t99tip_desc, t99codigo, t99descrip, t99abrev, t99siglas, t99tipo, t99estado "
		).append("FROM 	t99codigos "
		).append("WHERE t99cod_tab = ? and t99tip_desc = ? and t99estado = ? and t99codigo = ? ");

	  
	  //FRD - 20/04/2009
	   private final StringBuffer QUERY_VACACIONES_VENCIDAS = new StringBuffer("Select anno, saldo, t99descrip, t99siglas "
	   ).append("From t99codigos, t1281vacaciones_c v, t02perdp p " 
	   ).append("Where v.cod_pers = ? " 
	   ).append("And saldo > 0 " 
	   ).append("And t99cod_tab = '510' " 
	   ).append("And v.dias > 0 " 
	   ).append("And t99codigo in ('07', '11', '12', '13', " 
	   ).append("'14', '16', '17', '18', '19', " 
	   ).append("'20', '21', '22', '23', '24', "
	   ).append("'25', '26', '27', '28') " 
	   ).append("And t99abrev = anno " 
	   ).append("And v.cod_pers = p.t02cod_pers "); 

	   
	  //FRD - 20/04/2009
	  private final StringBuffer QUERY_VAC_VEN_FPAGO = 
	  new StringBuffer("Select cod_clasif1, cod_clasif2 "
	  ).append("From t99codigos "
	  ).append("Where "
	  ).append("t99abrev = ? "
	  ).append("And t99cod_tab = '510' "
	  ).append("And t99tip_desc = 'D' "
	  ).append("And t99codigo in ('07', '11', '12', '13', " 
	  ).append("'14', '16', '17', '18', '19', " 
	  ).append("'20', '21', '22', '23', '24', "
	  ).append("'25', '26', '27', '28') "); 
	  
	//JVV - INI 01/09/2010
	  private final StringBuffer QUERY_TIPO_SOL_CAS = 
	  new StringBuffer("SELECT * "
	  ).append("FROM t99codigos "
	  ).append("WHERE "
	  ).append("t99abrev = 'S' "
	  ).append("AND t99cod_tab = '518' "
	  ).append("AND t99codigo = ? ");
	  
	  private final StringBuffer QUERY_TIPO_LIC_CAS = 
		  new StringBuffer("SELECT * "
		  ).append("FROM t99codigos "
		  ).append("WHERE "
		  ).append("t99abrev = 'L' "
		  ).append("AND t99cod_tab = '518' "
		  ).append("AND t99codigo = ? ");
	//JVV - FIN
		  
	  /* JRR - 06/04/2011 - RECUPERACION DE FUENTES - Decompilacion */
	  private final String BUSC_PARAM_BY_CRITERIO = " SELECT T99COD_TAB, T99TIP_DESC, T99CODIGO, T99ABREV, T99DESCRIP, T99SIGLAS "
		  .concat("FROM T99CODIGOS WHERE T99COD_TAB = ? AND T99TIP_DESC = 'D' AND T99ESTADO = '1' ");
	  
	  private final String QUERY1_A_SENTENCE = " SELECT T99COD_TAB, T99TIP_DESC, T99CODIGO,  T99DESCRIP, T99SIGLAS,T99TIPO, T99ABREV FROM T99CODIGOS ";
	  
	  private StringBuffer QUERY_UPDATE_PARAMETROS = new StringBuffer("UPDATE")
	  	.append(" T99CODIGOS SET T99ESTADO = ? , T99COD_USER = ?, T99_FACTUAL = ? ");

	  /*   */
  
	  private DataSource datasource;
	  
	  public T99DAO() {
			super();
		  }
		
		  /**
		   * 
		   * Este constructor del DAO dicierne como crear el datasource
		   * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
		   * mejorar la invocacion del dao.
		   * 
		   * @param datasource Object
		   */
		  
		  public T99DAO(Object datasource) {
		    if (datasource instanceof DataSource)
		      this.datasource = (DataSource)datasource;
		    else if (datasource instanceof String)
		      this.datasource = getDataSource((String)datasource);
		    else
		      throw new DAOException(this, "Datasource no valido");
		  }
		  
	/**
	 * 
	 * @param v String[]
	 * @param pool String
	 * @return TreeMap
	 */
	public TreeMap mbuscar(String[] v, String pool) {
		return mbuscar(v, getDataSource(pool));
	}

	/**
	 * 
	 * @param v String[]
	 * @param ds DataSource
	 * @return TreeMap
	 */
	public TreeMap mbuscar(String[] v, DataSource ds) {
		TreeMap r = new TreeMap();
		Connection con = null;
		try {
			con = getConnection(ds);
			ResultSet rs = null;
			StringBuffer sb_cond = new StringBuffer("('");
			for (int i = 0; i < v.length; i++) {
				sb_cond.append(v[i].trim()).append("'");
				if ((i + 1) < v.length) {
					sb_cond.append(",'");
				}
			}
			sb_cond.append(")");
			sb_cond
					.insert(0,
							"SELECT t99codigo, t99descrip, t99abrev, t99siglas FROM t99codigos WHERE t99cod_tab IN ");
			sb_cond.append(" AND t99tip_desc = 'D' ORDER BY 1");

			PreparedStatement pstmt = con.prepareStatement(sb_cond.toString());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				ParamBean param = new ParamBean(rs.getString(1).trim(), 
												rs.getString(2).trim(),
												rs.getString(3)==null?"":rs.getString(3).trim(),
												rs.getString(4)==null?"":rs.getString(4).trim());
				r.put(param.getCodigo().trim(), param);
			}
			if(log.isDebugEnabled()) log.debug("Elementos cargados..:" + r.size());
			rs.close();
		} catch (SQLException e) {
			throw new DAOException(this, e.getMessage()
					+ e.getLocalizedMessage() + e.getErrorCode());
		} catch (Exception e) {
			throw new DAOException(this, e.getMessage()
					+ e.getLocalizedMessage());
		} finally {
			close(con);
		}
		return r;
	}

	/**
	 * 
	 * @param qry String
	 * @param pool String
	 * @return TreeMap
	 */
	public TreeMap mbuscar(String qry, String pool) {
		return mbuscar(qry, getDataSource(pool));
	}

	/**
	 * 
	 * @param qry String
	 * @param ds DataSource
	 * @return TreeMap
	 */
	public TreeMap mbuscar(String qry, DataSource ds) {
		TreeMap r = new TreeMap();
		Connection con = null;
		try {
			con = getConnection(ds);
			PreparedStatement pstmt = con.prepareStatement(qry);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ParamBean param = new ParamBean(rs.getString(1).trim(), rs.getString(2), rs.getString(3), "");
				r.put(param.getCodigo().trim(), param);
			}
			if(log.isDebugEnabled()) log.debug("Elementos cargados..:" + r.size());
			rs.close();
		} catch (SQLException e) {
			throw new DAOException(this, e);
		} catch (Exception e) {
			throw new DAOException(this, e);
		} finally {
			close(con);
		}
		return r;
	}

	/**
	 * 
	 * @param v String[]
	 * @param pool String
	 * @param valor String
	 * @return ParamBean
	 */
	public ParamBean buscar(String[] v, String pool, String valor) {
		return buscar(v, getDataSource(pool), valor);
	}

	/**
	 * 
	 * @param v String[]
	 * @param ds DataSource
	 * @param valor String
	 * @return ParamBean
	 */
	public ParamBean buscar(String[] v, DataSource ds, String valor) {
		ParamBean r = new ParamBean();
		Connection con = null;
		try {
			con = getConnection(ds);
			ResultSet rs = null;
			StringBuffer sb_cond = new StringBuffer("('");
			for (int i = 0; i < v.length; i++) {
				sb_cond.append(v[i].trim()).append("'");
				if ((i + 1) < v.length) {
					sb_cond.append(",'");
				}
			}
			sb_cond.append(")");

			sb_cond
					.insert(0,
							"SELECT t99codigo, t99descrip, t99abrev, t99siglas  FROM t99codigos WHERE t99cod_tab IN ");
			sb_cond.append(" AND t99tip_desc = 'D' AND t99codigo = ? ORDER BY 1");
			PreparedStatement pstmt = con.prepareStatement(sb_cond.toString());
			pstmt.setString(1, valor);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				r = new ParamBean(rs.getString(1).trim(), 
						rs.getString(2).trim(),
						rs.getString(3)==null?"":rs.getString(3).trim(),
						rs.getString(4)==null?"":rs.getString(4).trim());				
			}
			rs.close();
		} catch (SQLException e) {
			throw new DAOException(this, e);
		} catch (Exception e) {
			throw new DAOException(this, e);
		} finally {
			close(con);
		}
		return r;
	}

	/**
	 * Metodo que se encarga de buscar los parametros 
	 * de un codigo de tabla en especifico
	 * @throws SQLException
	 */
	public List buscarParametro(Map params)
	throws DAOException {
		
		List lista = new ArrayList();		
		
		lista = (ArrayList) executeQuery(datasource, QUERY1_SENTENCE,new Object[]{(String) params.get("t99cod_tab")});
		
		return lista;
	}
	
	/**
	 * Metodo que se encarga de insertar parametros
	 * @throws SQLException
	 */
	
	public void insertarParametro(Map params)
	throws DAOException {
		
		
		FechaBean fecAct = new FechaBean();
		
		Object[] objs = new Object[] { (String) params.get("t99cod_tab"),
									   "D",
									   (String) params.get("t99codigo"),
									   (String) params.get("t99descrip"),
									   (String) params.get("t99siglas"),
									   "1",
									   (String) params.get("t99cod_user"),
									   fecAct.getTimestamp()
									 };
			
		executeUpdate(datasource, QUERY2_SENTENCE, objs);
		
	}
	
	/**
	 * Metodo que se encarga de actualizar parametros
	 * @throws SQLException
	 */
	
	public void actualizarParametro(Map params)
	throws DAOException {
		
		Object[] objs = new Object[] { (String) params.get("t99descrip"),
									   (String) params.get("t99siglas"),
									   (String) params.get("t99tipo"),
									   (String) params.get("t99cod_tab"),
									   (String) params.get("t99codigo")
									 };
			
		executeUpdate(datasource,QUERY5_SENTENCE, objs);
		
	}
	
	//JRR
	/**
	 * Metodo que busca los campos de un parametro t99
	 * Mapa datos contiene t99cod_tab t99tip_desc t99estado t99codigo
	 * @throws DAOException
	 */	
	public Map findParamByCodTabCodigo(Map datos) throws DAOException {		
		Map mapa = null;
		try{
			if (log.isDebugEnabled()) log.debug("T99DAO findParamByCodTabCodigo - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			mapa = executeQueryUniqueResult(datasource, FIND_PARAM_BY_CODTAB_CODIGO.toString(),
			new Object[]{datos.get("t99cod_tab"), datos.get("t99tip_desc"), datos.get("t99estado"), datos.get("t99codigo")});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return mapa;
	}

	
	//FRD
	 /**
	  * Metodo que busca las fechas que puede ingresar un trabajador para registrar goze vacaciones 
	  * vencidas y mostrar saldos respectivos 
	  * Como parametro solo se ingresa el Registro dle Trabajador
	  * @throws DAOException
	  */ 
	 public List findT99T1281T02Codper(String codper) throws DAOException {  
	  List vacvenList = null;
	  try{
	   if (log.isDebugEnabled()) log.debug("T99DAO findT99T1281T02Codper - codper: "+codper);
	   setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
	   vacvenList = executeQuery(datasource, QUERY_VACACIONES_VENCIDAS.toString(), new Object[]{codper});

	  } catch (Exception e) {
	   log.error("*** SQL Error ****",e);
	  }
	  return vacvenList;
	 }
	 

	 /* JRR - PROGRAMACION 17/05/2010 */ 
		//FRD 21/04/2009
		/**
		 * Metodo que busca Mes (Año 2009) de Pago Vacaciones Vencidas
		 * @throws DAOException
		 */	
		public Map findMesAbonoVacaVencidas(String codper) throws DAOException {		
			Map mapa = null;
			try{
				if (log.isDebugEnabled()) log.debug("T99DAO findMesAbonoVacaVencidas - codper: "+codper);
				setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
				mapa = executeQueryUniqueResult(datasource, QUERY_VAC_VEN_FPAGO.toString(),
				new Object[]{codper});
			} catch (Exception e) {
				log.error("*** SQL Error ****",e);
			}
			return mapa;
		}
		
		//JVV - INI 01/09/2010
		/**
		   * Metodo encargado de buscar los datos de un determinado Tipo de Solicitud.
		   * @throws DAOException
		   */
		public Map findTipoSolicitudCAS(String tipo) throws DAOException {
			Map hMap = null;			
			try {
				if (log.isDebugEnabled()) log.debug("T99DAO findTipoSolicitudCAS - tipo: "+tipo);
				setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
				hMap = executeQueryUniqueResult(datasource, QUERY_TIPO_SOL_CAS.toString(),
				new Object[]{tipo});				
			} catch (Exception e) {
				log.error("*** SQL Error ****",e);				
			} 				
			return hMap;
		}
		
		/**
		   * Metodo encargado de buscar los datos de un determinado Tipo de Licencia.
		   * @throws DAOException
		   */
		public Map findTipoLicenciaCAS(String tipo) throws DAOException {
			Map hMap = null;			
			try {
				if (log.isDebugEnabled()) log.debug("T99DAO findTipoSolicitudCAS - tipo: "+tipo);
				setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);				
				hMap = executeQueryUniqueResult(datasource, QUERY_TIPO_LIC_CAS.toString(),
				new Object[]{tipo});				
			} catch (Exception e) {
				log.error("*** SQL Error ****",e);				
			} 				
			return hMap;
		}
		//JVV - FIN
		
		/* JRR - 06/04/2011 - RECUPERACION DE FUENTES - Decompilacion */		
		
		public List buscarParamByCriterio(Map params) throws DAOException {
			List lista = new ArrayList();
			StringBuffer strSQL = new StringBuffer(BUSC_PARAM_BY_CRITERIO);
			List filtros = new ArrayList();
			filtros.add(params.get("t99cod_tab"));
			if(params.get("t99codigo") != null && !"".equals(params.get("t99codigo").toString().trim()))
			{
				filtros.add(params.get("t99codigo"));
				strSQL.append(" AND T99CODIGO = ?");
			}
			if(params.get("t99abrev") != null && !"".equals(params.get("t99abrev").toString().trim()))
			{
				filtros.add((String)params.get("t99abrev") + "%");
				strSQL.append(" AND T99ABREV LIKE ?");
			}
			if(params.get("t99siglas") != null && !"".equals(params.get("t99siglas").toString().trim()))
			{
				filtros.add(params.get("t99siglas"));
				strSQL.append(" AND T99SIGLAS = ?");
			}
			lista = (ArrayList)executeQuery(datasource, strSQL.toString(), filtros.toArray());
			return lista;
		}
		
		public List findByCriterio(Map params) throws DAOException, Exception {
	        List lista = new ArrayList();
	        String sWhere = "";
	        boolean hasParam = false;
	        String sSQL = "";
	        String pt99cod_tab = "";
	        pt99cod_tab = (String)params.get("t99cod_tab");
	        if(pt99cod_tab != null && !pt99cod_tab.equals("-1"))
	        {
	            if(!sWhere.equals(""))
	                sWhere = sWhere + " AND ";
	            hasParam = true;
	            sWhere = sWhere + "T99COD_TAB = '" + pt99cod_tab.replaceAll("'", "''") + "'";
	        }
	        if(!sWhere.equals(""))
	            sWhere = sWhere + " AND ";
	        hasParam = true;
	        sWhere = sWhere + "T99TIP_DESC = '" + "D".replaceAll("'", "''") + "'";
	        if(params.get("t99codigo") != null && !"".equals(params.get("t99codigo").toString()))
	        {
	            if(!sWhere.equals(""))
	                sWhere = sWhere + " AND ";
	            hasParam = true;
	            sWhere = sWhere + "T99CODIGO = '" + params.get("t99codigo").toString().replaceAll("'", "''") + "'";
	        }
	        if(params.get("t99descrip") != null && !"".equals(params.get("t99descrip").toString()))
	        {
	            if(!sWhere.equals(""))
	                sWhere = sWhere + " AND ";
	            hasParam = true;
	            sWhere = sWhere + "upper(T99DESCRIP) like '" + params.get("t99descrip").toString().replaceAll("'", "''") + "%'";
	        }
	        if(hasParam)
	            sWhere = " WHERE " + sWhere;
	        sSQL = QUERY1_A_SENTENCE + sWhere;
	        setIsolationLevel(TX_READ_UNCOMMITTED);
	        lista = (ArrayList)executeQuery(datasource, sSQL);
	        setIsolationLevel(-1);
	        return lista;
	    }
		
		
		public void eliminarParametros(Map params)
		throws DAOException
		{
			String sSQL = this.QUERY_UPDATE_PARAMETROS.toString();
			sSQL = 
				sSQL.concat(" WHERE T99COD_TAB = ? AND T99TIP_DESC = ? AND T99CODIGO = ? ");
			Object[] objs = 
			{ 
					"0", 
					(String)params.get("cod_usu_aud"), 
					new FechaBean().getSQLDate(), 
					(String)params.get("t99cod_tab"), 
					(String)params.get("t99tip_desc"), 
					(String)params.get("t99codigo") };
			executeUpdate(this.datasource, sSQL.toString(), objs);
		}
		
		/*         */		
		
		
}
