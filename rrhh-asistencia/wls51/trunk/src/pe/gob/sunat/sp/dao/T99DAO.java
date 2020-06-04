package pe.gob.sunat.sp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.bean.BeanT99;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utilidades;

/**
 * 
 * 
 * Clase : T99DAO Descripcion : Clase para acceder a la t99codigos de sp Autor :
 * CGARRATT Fecha : 15-feb-2005 9:29:14
 */
public class T99DAO extends DAOAccesoBD {

	public T99DAO() {
	}

	/**
	 * Metodo findByBusquedaGen
	 * 
	 * @param HashMap
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByBusquedaGen(HashMap p)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String ptabla = "";
		String pcriterio = "";
		String pvalor = "";
		String pvigente = "";

		// Build WHERE statement

		ptabla = ((String) p.get("tabla")).trim();
		pcriterio = ((String) p.get("criterio")).trim();
		pvalor = ((String) p.get("valor")).trim();
		pvigente = ((String) p.get("vigente")).trim();

		//-- Check tabla
		if (ptabla != null && !ptabla.equals("")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99cod_tab = '" + common.replace(ptabla, "'", "''")
					+ "'";
		}

		//-- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99cod_tab = '" + common.replace(pvalor, "'", "''")
					+ "'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99codigo like '" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99descrip like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("3")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99abrev like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("4")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99siglas like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("5")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99tipo like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}

		//-- Check vigente
		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t99estado = '" + common.replace(pvigente, "'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t99descrip";

		sSQL = "SELECT * from t99codigos ";
		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			conn = getConnection(((String) p.get("dbpool")).trim());
			pstmt = conn.prepareStatement(selectStatement);
			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				java.util.Hashtable rsHash = new java.util.Hashtable();
				common.getRecordToHash(rs, rsHash, aFields);
				aLRpta.add(rsHash);
			}
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage());
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return aLRpta;
	}

	/**
	 * M�todo findByCodParam
	 * 
	 * @param String
	 *            dbpool_sp
	 * @param String
	 *            codTabla
	 * @param String
	 *            codParam
	 * @return HashMap hMap
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByCodParam(String dbpool_sp, String codTabla,
			String codParam) throws IncompleteConversationalState {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(4);

		selectStatement = " SELECT * FROM t99codigos WHERE t99cod_tab = ? AND t99codigo = ?";
		try {
			conn = getConnection(dbpool_sp);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, codTabla);
			pstmt.setString(2, codParam);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				hMap.put("t99cod_tab", rs.getString("t99cod_tab"));
				hMap.put("t99codigo", rs.getString("t99codigo"));
				hMap.put("t99descrip", rs.getString("t99descrip"));
				hMap.put("t99abrev", rs.getString("t99abrev"));
				hMap.put("t99siglas", rs.getString("t99siglas"));
				hMap.put("t99tipo", rs.getString("t99tipo"));
				hMap.put("t99estado", rs.getString("t99estado"));
			} else {
				hMap = null;
			}
			rs.close();
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage());
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return hMap;
	}
	
	//DTARAZONO AJUSTE 3ER ENTREGABLE 28/02/2018
	/**
	 * M�todo findByCodParam
	 * 
	 * @param String
	 *            dbpool_sp
	 * @param String
	 *            codTabla
	 * @param String
	 *            codParam
	 * @return HashMap hMap
	 * @throws IncompleteConversationalState
	 */
	public HashMap diasVentaVacByTipo(String dbpool_sp) throws IncompleteConversationalState {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(4);

		selectStatement = " select t99descrip, t99abrev from t99codigos where t99cod_tab='510' and t99codigo='45' and t99estado='1'";
		try {
			conn = getConnection(dbpool_sp);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				hMap.put("t99descrip", rs.getString("t99descrip"));
				hMap.put("t99abrev", rs.getString("t99abrev"));
				
			} else {
				hMap = null;
			}
			rs.close();
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage());
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return hMap;
	}
	
	/**
	 * Metodo encargado de buscar los detalles de un determinado codigo
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codTab
	 *            String. C�digo de la tabla requerida.
	 * @return ArrayList conteniendo los registros cargados en hashtables
	 * @throws SQLException
	 */

	public ArrayList cargarDiasVac(String dbpool)
			throws SQLException {

		Utilidades common = new Utilidades();
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList categorias = new ArrayList();

		try {

			strSQL = "select t99descrip from t99codigos where t99cod_tab='510' and t99codigo='46'";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			
			rs = pre.executeQuery();

			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				HashMap rsHash = new HashMap();
				common.getRecordToMap(rs, rsHash, aFields);
				categorias.add(rsHash);
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return categorias;
	}
	//FIN DTARAZONA

	/**
	 * Metodo encargado de buscar los detalles de un determinado codigo
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codTab
	 *            String. C�digo de la tabla requerida.
	 * @return ArrayList conteniendo los registros cargados en hashtables
	 * @throws SQLException
	 */

	public ArrayList findDetalleByCodTab(String dbpool, String codTab)
			throws SQLException {

		Utilidades common = new Utilidades();
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList categorias = new ArrayList();

		try {

			strSQL = "select * from t99codigos "
					+ "where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codTab);
			pre.setString(2, "D");
			pre.setString(3, "1");
			rs = pre.executeQuery();

			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				HashMap rsHash = new HashMap();
				common.getRecordToMap(rs, rsHash, aFields);
				categorias.add(rsHash);
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return categorias;
	}

	/**
	 * 
	 * @param datos
	 * @return
	 * @throws SQLException
	 */
	public ArrayList findDetalleByCodTabDS(HashMap datos)
			throws SQLException {

		Utilidades common = new Utilidades();
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList categorias = new ArrayList();

		try {

			strSQL = "select * from t99codigos where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? ";			
			if (datos.get("orderBy")!=null && !((String)datos.get("orderBy")).equals("")){
				strSQL += " order by "+datos.get("orderBy");
			}

			con = getConnection((DataSource)datos.get("dbpool"));
			pre = con.prepareStatement(strSQL);
			pre.setString(1, (String)datos.get("codTab"));
			pre.setString(2, "D");
			pre.setString(3, "1");
			rs = pre.executeQuery();

			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				HashMap rsHash = new HashMap();
				common.getRecordToMap(rs, rsHash, aFields);
				categorias.add(rsHash);
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return categorias;
	}
	
	/**
	 * 
	 * @param datos
	 * @return
	 * @throws SQLException
	 */
	public ArrayList findDetalleByCodTab(HashMap datos)
			throws SQLException {

		Utilidades common = new Utilidades();
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList categorias = new ArrayList();
		try {
			
			strSQL = "select * from t99codigos where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? ";			
			if (datos.get("orderBy")!=null && !((String)datos.get("orderBy")).equals("")){ 
				strSQL += " order by "+datos.get("orderBy");
			}
			
			con = getConnection((String)datos.get("dbpool"));
			pre = con.prepareStatement(strSQL);
			pre.setString(1, (String)datos.get("codTab"));
			pre.setString(2, "D");
			pre.setString(3, "1");
			rs = pre.executeQuery();

			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				HashMap rsHash = new HashMap();
				common.getRecordToMap(rs, rsHash, aFields);
				categorias.add(rsHash);
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return categorias;
	}	
	
	/**
	 * 
	 * @param datos
	 * @return
	 * @throws SQLException
	 */
	public HashMap findByCodTab(HashMap datos)
			throws SQLException {

		Utilidades common = new Utilidades();
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap detalle = new HashMap();

		try {

			strSQL = "select t99codigo, t99descrip from t99codigos "
					+ "where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? ";

			con = getConnection((String)datos.get("dbpool"));
			pre = con.prepareStatement(strSQL);
			pre.setString(1, (String)datos.get("codTab"));
			pre.setString(2, Constantes.T99DETALLE);
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();

			while (rs.next()) {
				detalle.put(rs.getString("t99codigo").trim(),
						rs.getString("t99descrip")!=null?rs.getString("t99descrip").trim():"");
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return detalle;
	}	

	/**
	 * Metodo encargado de buscar la descripcion
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codTab
	 *            String. Codigo de la tabla requerida.
	 * @param codigo
	 *            String. Codigo del registro dentro de la tabla.
	 * @return String. Descripcion contenida en el registro.
	 * @throws SQLException
	 */
	public String findParamByCodTabCodigo(String dbpool, String codTab,
			String codigo) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String parametro = "0";

		try {

			strSQL = "select t99descrip from t99codigos "
					+ "where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? "
					+ " and t99codigo = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codTab);
			pre.setString(2, "D");
			pre.setString(3, "1");
			pre.setString(4, codigo);
			rs = pre.executeQuery();

			if (rs.next()) {
				parametro = rs.getString("t99descrip") != null ? rs.getString(
						"t99descrip").trim() : "";
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return parametro;
	}
	
	/**
	 * Metodo encargado de buscar la descripcion
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codTab
	 *            String. Codigo de la tabla requerida.
	 * @param codigo
	 *            String. Codigo del registro dentro de la tabla.
	 * @return String. Descripcion contenida en el registro.
	 * @throws SQLException
	 */
	public String findParamByCodTabCodigoDS(DataSource dbpool, String codTab,
			String codigo) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String parametro = "0";

		try {

			strSQL = "select t99descrip from t99codigos "
					+ "where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? "
					+ " and t99codigo = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codTab);
			pre.setString(2, "D");
			pre.setString(3, "1");
			pre.setString(4, codigo);
			rs = pre.executeQuery();

			if (rs.next()) {
				parametro = rs.getString("t99descrip") != null ? rs.getString(
						"t99descrip").trim() : "";
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return parametro;
	}	

	/**
	 * M�todo encargado de buscar los registros de aquellos c�digos que
	 * pertenezcan a una tabla determinada, filtrados por un criterio con un
	 * valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @param codTab
	 *            String. C�digo de la tabla requerida.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanT99.
	 * @throws SQLException
	 */

	public ArrayList findByCodTab(String dbpool, String criterio, String valor,
			String codTab) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList categorias = null;

		try {

			strSQL = "select * from t99codigos "
					+ "where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? ";

			//busqueda por codigo
			if (criterio.equals("0")) {
				strSQL += " and t99codigo like '%" + valor.trim().toUpperCase()
						+ "%' ";
			}
			//busqueda por nombre
			if (criterio.equals("1")) {
				strSQL += " and upper(t99descrip) like '%"
						+ valor.trim().toUpperCase() + "%' ";
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codTab);
			pre.setString(2, Constantes.T99DETALLE);
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();
			categorias = new ArrayList();

			while (rs.next()) {

				BeanT99 a = new BeanT99();

				a.setT99codigo(rs.getString(3).trim());
				a.setT99descrip(rs.getString(4).trim());

				categorias.add(a);
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return categorias;
	}

	/**
	 * M�todo encargado de buscar los registros de aquellos c�digos que
	 * pertenezcan a una tabla determinada, filtrados por un criterio con un
	 * valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @param codTab
	 *            String. C�digo de la tabla requerida.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanT99.
	 * @throws SQLException
	 */

	public ArrayList findByCodTab(String dbpool, String criterio, String valor,
			String codTab, String modulo) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList categorias = null;

		try {

			strSQL = "select * from t99codigos "
					+ "where t99cod_tab = ? and t99tip_desc = ? and t99estado = ? ";

			//busqueda por codigo
			if (criterio.equals("0")) {
				strSQL += " and t99codigo like '%" + valor.trim().toUpperCase()
						+ "%' ";
			}
			//busqueda por nombre
			if (criterio.equals("1")) {
				strSQL += " and upper(t99descrip) like '%"
						+ valor.trim().toUpperCase() + "%' ";
			}
			if (modulo != null) {
				strSQL += " and t99_modulo = '" + modulo + "' ";
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codTab);
			pre.setString(2, Constantes.T99DETALLE);
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();
			categorias = new ArrayList();

			while (rs.next()) {

				BeanT99 a = new BeanT99();

				a.setT99codigo(rs.getString(3).trim());
				a.setT99descrip(rs.getString(4).trim());

				categorias.add(a);
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return categorias;
	}

	
	public String getSiguienteCodigo(String dbpool, String codTab)
			throws SQLException {

		String codigo = "";
		String strSQL = "";
		String strUpdate = "";
		PreparedStatement pre = null;
		PreparedStatement preUpd = null;
		Connection con = null;
		ResultSet rs = null;

		try {

			strSQL = "select t99descrip " + "from t99codigos "
					+ "where t99cod_tab = ? and " + "t99codigo = ? ";

			con = getConnection(dbpool);
			//con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, Constantes.CODTAB_CORREL);
			pre.setString(2, codTab);
			rs = pre.executeQuery();

			if (rs.next()) {

				codigo = ""
						+ (Integer.parseInt(rs.getString("t99descrip").trim()) + 1);

				strUpdate = "update t99codigos " + "set t99descrip = ? "
						+ "where t99cod_tab = ? and " + "t99codigo = ? ";

				preUpd = con.prepareStatement(strUpdate);
				preUpd.setString(1, codigo);
				preUpd.setString(2, Constantes.CODTAB_CORREL);
				preUpd.setString(3, codTab);
				int r = preUpd.executeUpdate();

			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (preUpd != null) {
				preUpd.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return codigo;
	}

	/**
	 *  Metodo findByCodParamDS
	 * @param dbpool_sp
	 * @param codTabla
	 * @param codParam
	 * @return
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByCodParamDS(DataSource dbpool_sp, String codTabla,
			String codParam) throws IncompleteConversationalState {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(4);

		selectStatement = " SELECT * FROM t99codigos WHERE t99cod_tab = ? AND t99codigo = ?";
		try {
			conn = getConnection(dbpool_sp);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, codTabla);
			pstmt.setString(2, codParam);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				hMap.put("t99cod_tab", rs.getString("t99cod_tab"));
				hMap.put("t99codigo", rs.getString("t99codigo"));
				hMap.put("t99descrip", rs.getString("t99descrip"));
				hMap.put("t99abrev", rs.getString("t99abrev"));
				hMap.put("t99siglas", rs.getString("t99siglas"));
				hMap.put("t99tipo", rs.getString("t99tipo"));
				hMap.put("t99estado", rs.getString("t99estado"));
			} else {
				hMap = null;
			}
			rs.close();
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage());
		} finally {
			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return hMap;
	}
	
	
	
	/**
	 * Metodo encargado de buscar la descripcion
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codTab
	 *            String. Codigo de la tabla requerida.
	 * @param codigo
	 *            String. Codigo del registro dentro de la tabla.
	 * @return String. Descripcion contenida en el registro.
	 * @throws SQLException
	 */
	public String findParamByCodTabCodigoTodos(String dbpool, String codTab,
			String codigo) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String parametro = "0";

		try {

			strSQL = "select t99descrip from t99codigos "
					+ "where t99cod_tab = ? and t99tip_desc = ?  "
					+ " and t99codigo = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codTab);
			pre.setString(2, "D");
			
			pre.setString(3, codigo);
			rs = pre.executeQuery();

			if (rs.next()) {
				parametro = rs.getString("t99descrip") != null ? rs.getString(
						"t99descrip").trim() : "";
			}

			if (rs != null) {
				rs.close();
			}
			if (pre != null) {
				pre.close();
			}
			if (con != null) {
				con.close();
			}
		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				pre.close();
			} catch (Exception e) {
			}
			try {
				con.close();
			} catch (Exception e) {
			}
		}
		return parametro;
	}
	
	public ArrayList findByCodAbrev(DataSource dbpool, String tab)
			throws SQLException {
		
		ArrayList arrCodAbrev=new ArrayList();
		String qry_respuesta = "SELECT t99codigo,t99abrev FROM t99codigos WHERE t99cod_tab = ? AND t99tip_desc = 'D' ORDER BY t99codigo";
		PreparedStatement pstmt = null;
		Connection conn = null;
		HashMap hm = null;
		try {
			conn = getConnection(dbpool);
			pstmt = conn.prepareStatement(qry_respuesta);
			pstmt.setString(1, tab);
			
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				hm = new HashMap();
				hm.put("codigo", rs.getString(1));
				hm.put("abreviado", rs.getString(2));
				arrCodAbrev.add(hm);
			}

		} catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				pstmt.close();
			} catch (Exception exception1) {
			}
			try {
				conn.close();
			} catch (Exception exception2) {
			}
		}
		return arrCodAbrev;
	}
	
	public ArrayList findByCodDescrip(DataSource dbpool, String tab)
			throws SQLException {
		ArrayList arrCodDescrip = new ArrayList();
		String qry_respuesta = "SELECT t99codigo,t99descrip FROM t99codigos WHERE t99cod_tab = ? AND t99tip_desc = 'D' ORDER BY t99codigo";
		PreparedStatement pstmt = null;
		Connection conn = null;
		HashMap hm = null;
		try {
			conn = getConnection(dbpool);
			pstmt = conn.prepareStatement(qry_respuesta);
			pstmt.setString(1, tab);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				hm = new HashMap();
				hm.put("codigo", rs.getString(1));
				hm.put("descripcion", rs.getString(2));
				arrCodDescrip.add(hm);
			}
		} catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				pstmt.close();
			} catch (Exception exception1) {
			}
			try {
				conn.close();
			} catch (Exception exception2) {
			}
		}
		return arrCodDescrip;
	}

}