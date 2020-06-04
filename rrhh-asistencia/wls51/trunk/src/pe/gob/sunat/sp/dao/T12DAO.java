package pe.gob.sunat.sp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.bean.BeanT12;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utilidades;

/**
 * 
 * Clase : T12DAO Descripcion : Clase de acceso a la t12uorga de sp Autor :
 * CGARRATT Fecha : 15-feb-2005 9:26:38
 */
public class T12DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T12DAO.class);
	
	public T12DAO() {
	}

	/**
	 * Metodo findByJefe
	 * 
	 * @param String
	 *            pool_sp
	 * @param String
	 *            cod_uorga
	 * @param String
	 *            codReg
	 * @return HashMap hm
	 * @throws IncompleteConversationalState
	 * 
	 */
	public HashMap findByJefe(String pool_sp, String cod_uorga, String codReg)
			throws IncompleteConversationalState {
		HashMap hj = findByJefatura(pool_sp, cod_uorga);
		log.debug("HJ "+ hj);
		HashMap hm = new HashMap();
		log.debug("codReg "+ codReg);
		if (((String) hj.get("t12cod_jefat")).equals(codReg)
				|| ((String) hj.get("t12cod_encar")).equals(codReg)) {
			
			String nUnidad = findByNewUnidadJefe(cod_uorga);
			if (nUnidad.equals("")) {
				nUnidad = "100000";
			}
			log.debug("entre nueva unidad "+ nUnidad);
			hm = findByJefatura(pool_sp, nUnidad);
		} else {
			hm = findByJefatura(pool_sp, cod_uorga);
			log.debug("HM "+ hm);
		}
		log.debug("HM1 "+ hm);
		return hm;
	}

	/**
	 * Metodo findByJefatura
	 * 
	 * @param String
	 *            dbpool
	 * @param String
	 *            t12cod_uorga
	 * @return HashMap a
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByJefatura(String dbpool, String t12cod_uorga)
			throws IncompleteConversationalState {
		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		selectStatement = "SELECT t12cod_jefat, t12cod_encar FROM t12uorga WHERE t12cod_uorga = ? ";

		T02DAO dao = new T02DAO();
		try {
			// Obtener Conexion a BD
			conn = getConnection(dbpool);
			// Buscar en la BD
			pstmt = conn.prepareStatement(selectStatement);

			pstmt.setString(1, t12cod_uorga.trim());
			ResultSet rs = pstmt.executeQuery();
			HashMap a = new HashMap();
			if (rs.next()) {
				HashMap d = null;
				HashMap e = null;
				String jefat = rs.getString("t12cod_jefat");
				String encar = rs.getString("t12cod_encar");
				if (jefat != null && jefat != "") {
					d = dao.findByCodPers(dbpool, jefat);
				}
				if (encar != null && encar != "") {
					e = dao.findByCodPers(dbpool, encar);
				}
				a.put("t12cod_jefat", jefat == null ? " " : jefat);
				a.put("t12cod_encar", encar == null ? " " : encar);
				a.put("t12cod_jefat_desc", d == null ? " " : jefat.trim() + "-"
						+ ((String) d.get("t02ap_pate")).trim() + " "
						+ ((String) d.get("t02ap_mate")).trim() + ", "
						+ ((String) d.get("t02nombres")).trim());
				a
						.put("t12cod_encar_desc", e == null ? " " : encar
								.trim()
								+ "-"
								+ ((String) e.get("t02ap_pate")).trim()
								+ " "
								+ ((String) e.get("t02ap_mate")).trim()
								+ ", "
								+ ((String) e.get("t02nombres")).trim()
								+ "(Encargado)");
			}
			rs.close();
			return a;
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
	}

	/**
	 * Mï¿½todo findByNewUnidadJefe
	 * 
	 * @param string
	 *            codUorgan
	 * @return String newUorg
	 * @throws IncompleteConversationalState
	 */
	private String findByNewUnidadJefe(String codUorgan)
			throws IncompleteConversationalState {
		int longitud = codUorgan.length();
		String newUorg = null;
		for (int i = longitud; i >= 1; i--) {
			if (!codUorgan.trim().substring(i - 1, i).equals("0")) {
				newUorg = codUorgan.trim().substring(0, i - 1);
				if (newUorg.trim().length() == 5) {
					newUorg = newUorg.trim() + "0";
					break;
				} else {
					if (newUorg.trim().length() == 4) {
						newUorg = newUorg.trim() + "00";
						break;
					} else {
						if (newUorg.trim().length() == 3) {
							newUorg = newUorg.trim() + "000";
							break;
						} else {
							if (newUorg.trim().length() == 2) {
								newUorg = newUorg.trim() + "0000";
								break;
							} else {
								if (newUorg.trim().length() == 1) {
									newUorg = newUorg.trim() + "00000";
									break;
								}
							}
						}
					}
				}
			}
		}
		return newUorg;
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

		String pcriterio = "";
		String pvalor = "";
		String pvigente = "";

		// Build WHERE statement

		pcriterio = ((String) p.get("criterio")).trim();
		pvalor = ((String) p.get("valor")).trim();
		pvigente = ((String) p.get("vigente")).trim();

		// -- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12cod_uorga like '" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_uorga like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_corta like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}

		// -- Check vigente
		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_estad = '" + common.replace(pvigente, "'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";

		sSQL = "SELECT * from t12uorga ";
		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			conn = getConnection((String) p.get("dbpool"));
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
	 * Metodo findByBusquedaGen
	 * 
	 * @param HashMap
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByBusquedaGen(Map p)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String pcriterio = "";
		String pvalor = "";
		String pvigente = "";

		// Build WHERE statement

		pcriterio = ((String) p.get("criterio")).trim();
		pvalor = ((String) p.get("valor")).trim();
		pvigente = ((String) p.get("vigente")).trim();

		// -- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12cod_uorga like '" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_uorga like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_corta like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}

		// -- Check vigente
		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_estad = '" + common.replace(pvigente, "'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";

		sSQL = "SELECT * from t12uorga ";
		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			conn = getConnection((String) p.get("dbpool"));
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
	 * Mï¿½todo findByBusquedaGen
	 * 
	 * @param HashMap
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByBusquedaGenDS(HashMap p)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String pcriterio = "";
		String pvalor = "";
		String pvigente = "";

		// Build WHERE statement

		pcriterio = ((String) p.get("criterio")).trim();
		pvalor = ((String) p.get("valor")).trim();
		pvigente = ((String) p.get("vigente")).trim();

		// -- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12cod_uorga like '" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_uorga like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_corta like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}

		// -- Check vigente
		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_estad = '" + common.replace(pvigente, "'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";

		sSQL = "SELECT * from t12uorga ";
		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			conn = getConnection((DataSource) p.get("dbpool"));
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
	 * Mï¿½todo findByCodUor
	 * 
	 * @param String
	 *            dbpool
	 * @param String
	 *            codUorg
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByCodUor(String dbpool, String codUorg)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String pvalor = "";

		// Build WHERE statement

		// -- Check valor
		pvalor = codUorg;

		hasParam = true;
		sWhere += " t12cod_uorga = '" + common.replace(pvalor, "'", "''") + "'";

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";

		sSQL = "SELECT * from t12uorga ";
		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			conn = getConnection(dbpool);
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
	 * Mï¿½todo findByCodUorga
	 * 
	 * @param String
	 *            dbpool_sp
	 * @param String
	 *            codUor
	 * @return HashMap hMap
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByCodUorga(String dbpool_sp, String codUor)
			throws IncompleteConversationalState {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(7);

		selectStatement = " SELECT * FROM t12uorga WHERE t12cod_uorga = ? ";
		try {
			conn = getConnection(dbpool_sp);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, codUor);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				hMap.put("t12cod_uorga", rs.getString("t12cod_uorga"));
				hMap.put("t12des_uorga", rs.getString("t12des_uorga"));
				hMap.put("t12des_corta", rs.getString("t12des_corta"));
				hMap.put("t12ind_estad", rs.getString("t12ind_estad"));
				hMap.put("t12cod_jefat", rs.getString("t12cod_jefat"));
				hMap.put("t12cod_encar", rs.getString("t12cod_encar"));
				hMap.put("t12cod_repor", rs.getString("t12cod_repor"));//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
			} else {
				hMap.put("t12cod_uorga", "-");
				hMap.put("t12des_uorga", "Sin descripcion");
				hMap.put("t12des_corta", "S/Codigo");
				hMap.put("t12ind_estad", "-");
				hMap.put("t12cod_jefat", "-");
				hMap.put("t12cod_encar", "-");
				hMap.put("t12cod_repor", "-");//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
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
	 * Recibe una conexion establecida y busca la unidad organizacional.
	 * 
	 * @param conn
	 *            Connection
	 * @param codUor
	 *            String
	 * @return Map
	 * @throws IncompleteConversationalState
	 */
	public Map findByCodUorga(Connection conn, String codUor)
			throws IncompleteConversationalState {
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(7);

		selectStatement = " SELECT * FROM t12uorga WHERE t12cod_uorga = ? ";
		try {
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, codUor);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				hMap.put("t12cod_uorga", rs.getString("t12cod_uorga"));
				hMap.put("t12des_uorga", rs.getString("t12des_uorga").trim());
				hMap.put("t12des_corta", rs.getString("t12des_corta").trim());
				hMap.put("t12ind_estad", rs.getString("t12ind_estad"));
				hMap.put("t12cod_jefat", rs.getString("t12cod_jefat"));
				hMap.put("t12cod_encar", rs.getString("t12cod_encar"));
				hMap.put("t12cod_nivel",
						rs.getString("t12cod_nivel") != null ? rs
								.getString("t12cod_nivel") : "0");
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
		}
		return hMap;
	}

	/**
	 * Mï¿½todo findByBusquedaSeleccion
	 * 
	 * @param String[]
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByBusquedaSeleccion(String[] p)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String pcriterio = "";
		String pvalor = "";
		String pvigente = "";

		// Build WHERE statement

		pcriterio = p[4];
		pvalor = p[5];
		pvigente = p[6];

		// -- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12cod_uorga like '" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_uorga like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12des_corta like '%"
					+ common.replace(pvalor, "'", "''") + "%'";
		}

		// -- Check vigente
		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t12ind_estad = '" + common.replace(pvigente, "'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t12cod_uorga, t12des_uorga ";

		sSQL = "SELECT * from t12uorga ";
		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			conn = getConnection(p[1]);
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
	 * Metodo encargado de buscar las unidades organizacionales filtradas por un
	 * criterio con un valor determinado.
	 * 
	 * @param params
	 *            HashMap. Contiene los parametros de la busqueda (dbpool:
	 *            cadena de conexion, criterio: criterio de busqueda, valor:
	 *            filtro de la busqueda.
	 * @return ArrayList conteniendo los registros cargados en HashMaps.
	 * @throws SQLException
	 */
	public ArrayList findByCodDesc(HashMap params) throws SQLException {

		String dbpool = (String) params.get("dbpool");
		String criterio = (String) params.get("criterio");
		String valor = (String) params.get("valor");
		String orden = (String) params.get("orden");

		StringBuffer strSQL = null;
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList unidades = null;

		try {

			strSQL = new StringBuffer(
					"select t12cod_uorga, t12des_uorga, t12des_corta ").append(
					"from  t12uorga ").append("where t12ind_estad = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and t12cod_uorga like '").append(
						valor.trim().toUpperCase()).append("%' ");
			}
			if (criterio.equals("1")) {
				strSQL.append(" and t12des_uorga like '%").append(
						valor.trim().toUpperCase()).append("%' ");
			}

			if (orden == null || orden.trim().equals("0")) {
				strSQL.append(" order by t12des_uorga");
			} else if (orden.trim().equals("1")) {
				strSQL.append(" order by t12cod_uorga");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, "1");
			rs = pre.executeQuery();
			unidades = new ArrayList();

			while (rs.next()) {
				HashMap map = new HashMap();

				map.put("t12cod_uorga", rs.getString("t12cod_uorga"));
				map.put("t12des_uorga",
						rs.getString("t12des_uorga") == null ? "" : rs
								.getString("t12des_uorga"));
				map.put("t12des_corta",
						rs.getString("t12des_corta") == null ? "" : rs
								.getString("t12des_corta"));

				unidades.add(map);
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
		} catch (Exception e) {
			log.debug("Error "+e);
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
		return unidades;
	}

	/**
	 * Metodo encargado de buscar los datos de la unidad organizacional cuyo
	 * codigo coincida con el parametro indicado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codigo
	 *            String. Codigo de la unidad organizacional.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanT12.
	 * @throws SQLException
	 */
	public BeanT12 findByCodigo(String dbpool, String codigo)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanT12 a = null;

		try {

			strSQL = "select * from t12uorga where t12cod_uorga = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codigo.toUpperCase());
			rs = pre.executeQuery();

			if (rs.next()) {

				a = new BeanT12();

				a.setT12cod_uorgan(rs.getString(1));
				a.setT12descr_uorg(rs.getString(2).trim());
				a.setT12descr_corta(rs.getString(3).trim());
				a.setT12f_vigenci(rs.getDate(4));
				a.setT12f_baja(rs.getDate(5));
				a.setT12cod_nivel(rs.getString(6));
				a.setT12cod_categ(rs.getString(7));
				a.setT12cod_subpr(rs.getString(8));
				a.setT12ind_aplic(rs.getString(9));
				a.setT12cod_anter(rs.getString(10));
				a.setT12ind_estad(rs.getString(11));
				a.setT12cod_jefat(rs.getString(12));
				a.setT12cod_encar(rs.getString(13));
				a.setT12cod_repor(rs.getString(14));
				a.setT12tipo(rs.getString(15));
				a.setT12f_graba(rs.getDate(16));
				a.setT12cod_user(rs.getString(17));

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
			log.debug("Error "+e);
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
		return a;
	}

	/**
	 * Metodo encargado de buscar los registros de periodo por area que para un
	 * determinado periodo, filtrados por un criterio con un valor determinado.
	 * Join con la tabla T1278Periodo_Area.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param periodo
	 *            Codigo del periodo.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanT12.
	 * @throws SQLException
	 */
	public ArrayList joinWithT1278(String dbpool, String periodo,
			String criterio, String valor) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList unidades = null;

		try {

			strSQL = "select 	pa.u_organ, u.t12des_uorga, pa.est_id "
					+ "from 	t12uorga u, " + "        t1278periodo_area pa "
					+ "where 	pa.periodo = ? ";

			// busqueda por codigo
			if (criterio.equals("0")) {
				strSQL += " and u.t12cod_uorga like '"
						+ valor.trim().toUpperCase() + "%' ";
			}
			// busqueda por nombre
			if (criterio.equals("1")) {
				strSQL += " and u.t12des_uorga like '%"
						+ valor.trim().toUpperCase() + "%' ";
			}

			strSQL += " and pa.u_organ = u.t12cod_uorga ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, periodo);
			rs = pre.executeQuery();
			unidades = new ArrayList();

			while (rs.next()) {

				BeanT12 a = new BeanT12();

				a.setT12cod_uorgan(rs.getString(1));
				a.setT12descr_uorg(rs.getString(2).trim());
				a.setT12ind_estad(rs.getString(3));

				unidades.add(a);
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
		return unidades;
	}
	
	public ArrayList joinWithT1278SinPeriodo(String dbpool, String periodo, String activo) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList unidades = null;

		try {			
			/*strSQL = "select u.t12cod_uorga, u.t12des_uorga, u.t12ind_estad from t12uorga u where u.t12cod_uorga not in(select pa.u_organ "
					+ "from t12uorga u1,t1278periodo_area pa where pa.periodo = ? and pa.u_organ = u1.t12cod_uorga) and "
					+ "t12ind_estad = ? and t12f_vigenci>=(select finicio from t1276periodo where periodo=?) "
					+ "and t12f_vigenci<=(select ffin from t1276periodo where periodo = ?)";*/
			
			strSQL = "select u.t12cod_uorga, u.t12des_uorga, u.t12ind_estad from t12uorga u where u.t12cod_uorga not in(select pa.u_organ "
					+ "from t12uorga u1,t1278periodo_area pa where pa.periodo = ? and pa.u_organ = u1.t12cod_uorga) and "
					+ "t12ind_estad = ? and t12f_vigenci<=(select ffin from t1276periodo where periodo = ?)";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, periodo);
			pre.setString(2, activo);
			pre.setString(3, periodo);

			rs = pre.executeQuery();
			unidades = new ArrayList();

			while (rs.next()) {

				BeanT12 a = new BeanT12();

				a.setT12cod_uorgan(rs.getString(1));
				a.setT12descr_uorg(rs.getString(2).trim());
				a.setT12ind_estad(rs.getString(3));

				unidades.add(a);
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
		return unidades;
	}

	/**
	 * Metodo encargado de buscar las unidades organizacionales filtradas por un
	 * criterio con un valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanT12.
	 * @throws SQLException
	 */
	public ArrayList findByCodDesc(String dbpool, String criterio,
			String valor, HashMap seguridad) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList unidades = null;

		try {

			strSQL = "select * from t12uorga where t12ind_estad = ? ";

			if (criterio.equals("0")) {
				strSQL += " and t12cod_uorga like '"
						+ valor.trim().toUpperCase() + "%' ";
			}
			if (criterio.equals("1")) {
				strSQL += " and t12des_uorga like '%"
						+ valor.trim().toUpperCase() + "%' ";
			}
			if (criterio.equals("2")) {
				strSQL += " and t12cod_nivel in (" + valor + ") ";
			}
			if (criterio.equals("3")) {

				String cod_nivel = (String) seguridad.get("cod_nivel");
				strSQL += " and t12cod_uorga like '"
						+ valor.substring(0, Integer.parseInt(cod_nivel) + 1)
						+ "%' ";
			}

			// criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL += " and 1=1 ";
				}//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
				else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){
					strSQL += " and (t12cod_uorga like '" + uoSeg + "' ";
					strSQL += " or t12cod_uorga in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" +uoAO+ "')) ";
				}//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
				else if (
						//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			            //JRR - 22/09/2009
			            roles.get(Constantes.ROL_ANALISTA_LEXC) != null
			            //
						|| roles.get(Constantes.ROL_JEFE) != null) {
					strSQL += " and t12cod_uorga like '" + uoSeg + "' ";
				} else {
					strSQL += " and 1=2 ";
				}
			}

			if (criterio.equals("2") || criterio.equals("3")) {
				strSQL += " order by t12cod_uorga";
			} else {
				strSQL += " order by t12des_uorga";
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			unidades = new ArrayList();

			while (rs.next()) {

				BeanT12 a = new BeanT12();

				a.setT12cod_uorgan(rs.getString("t12cod_uorga"));
				if (criterio.equals("2")) {
					a.setT12descr_uorg(rs.getString("t12des_corta").trim());
				} else {
					a.setT12descr_uorg(rs.getString("t12des_uorga").trim());
				}

				unidades.add(a);
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
		return unidades;
	}

	public HashMap findJefeByTrabajador(String pool_sp, String cod_uorga,
			String codReg) throws SQLException {

		HashMap hm = new HashMap();
		try {

			HashMap hj = findByJefatura(pool_sp, cod_uorga);
			log.debug("HJ "+ hj);
			log.debug("codReg "+ codReg);
			if (((String) hj.get("t12cod_jefat")).equals(codReg)
					|| ((String) hj.get("t12cod_encar")).equals(codReg)) {
				String nUnidad = findByNewUnidadJefe(cod_uorga);
				if (nUnidad.equals("")) {
					nUnidad = "100000";
				}
				log.debug("entre nueva unidad "+ nUnidad);
				hm = findJefeByJefatura(pool_sp, nUnidad);
				log.debug("HM nueva unidad  "+ hm);
			} else {
				hm = findJefeByJefatura(pool_sp, cod_uorga);
				log.debug("HM "+ hm);
			}
		} catch (Exception e) {
			log.debug("Error"+e);
		}
		log.debug("HM1 "+ hm);
		return hm;
	}

	public String findJefeByUO(String dbpool, String t12cod_uorga) throws SQLException {

		PreparedStatement pstmt = null;
		String selectStatement = null;
		ResultSet rs = null;
		Connection conn = null;		
		String jefe = "";
		
		try {

			selectStatement = "SELECT substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4) cod_jefe FROM t12uorga WHERE t12cod_uorga = ? ";
			conn = getConnection(dbpool);
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, t12cod_uorga.trim());
			rs = pstmt.executeQuery();
			if (rs.next()) jefe = rs.getString("cod_jefe");
			
		} catch (Exception e) {
			log.debug("Error "+e);
			throw new SQLException(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}

			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
		return jefe;
	}

	/**
	 * 
	 * @param dbpool
	 * @param t12cod_uorga
	 * @return
	 * @throws SQLException
	 */
	public HashMap findJefeByJefatura(String dbpool, String t12cod_uorga)
			throws SQLException {

		PreparedStatement pstmt = null;
		String selectStatement = null;
		ResultSet rs = null;
		Connection conn = null;
		selectStatement = "SELECT t12cod_jefat, t12cod_encar FROM t12uorga WHERE t12cod_uorga = ? ";

		T02DAO dao = new T02DAO();
		try {

			conn = getConnection(dbpool);
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, t12cod_uorga.trim());
			rs = pstmt.executeQuery();
			HashMap a = new HashMap();
			if (rs.next()) {
				if(log.isDebugEnabled()) log.debug("metodo findJefeByJefatura(rs.next)");//ICR 04/01/2013 logs para nullpointer rs.getString("t12cod_jefat") y rs.getString("t12cod_encar")
				HashMap d = null;
				HashMap e = null;
				String jefat = rs.getString("t12cod_jefat");
				String encar = rs.getString("t12cod_encar");
				if(log.isDebugEnabled()) log.debug("findJefeByJefatura(jefat): "+jefat);//ICR 04/01/2013 logs para nullpointer rs.getString("t12cod_jefat") y rs.getString("t12cod_encar")
				if(log.isDebugEnabled()) log.debug("findJefeByJefatura(encar): "+encar);//ICR 04/01/2013 logs para nullpointer rs.getString("t12cod_jefat") y rs.getString("t12cod_encar")
				if (jefat != null && jefat != "") {
					d = dao.joinWithT12T99ByCodPers(dbpool, jefat, null);
				}
				if (encar != null && encar != "") {
					e = dao.joinWithT12T99ByCodPers(dbpool, encar, null);
				}
				
				//JRR - 01/03/2011 - Validacion para destinatario de solicitudes
				a.put("t12cod_jefe_final", (encar != null && !encar.trim().equals(""))  ? encar : jefat);
				//a.put("t12cod_jefe_final", encar == null ? jefat : encar);
				a.put("t12cod_jefat", jefat == null ? " " : jefat);
				a.put("t12cod_encar", encar == null ? " " : encar);
				a.put("t12cod_jefat_desc", (d == null || d.isEmpty()) ? " "
						: jefat.trim() + " - "
								+ ((String) d.get("t02ap_pate")).trim() + " "
								+ ((String) d.get("t02ap_mate")).trim() + ", "
								+ ((String) d.get("t02nombres")).trim());
				a.put("t12cod_encar_desc", (e == null || e.isEmpty()) ? " "
						: encar.trim() + "-"
								+ ((String) e.get("t02ap_pate")).trim() + " "
								+ ((String) e.get("t02ap_mate")).trim() + ", "
								+ ((String) e.get("t02nombres")).trim()
								+ "(Encargado)");
				a.put("t12cod_cate_desc", (d == null || d.isEmpty()) ? (String) e.get("t02desc_cate")
						: (String) d.get("t02desc_cate"));
				a.put("t12cod_uorg_desc", (d == null || d.isEmpty()) ? (String) e.get("t12des_uorga")
						: (String) d.get("t12des_uorga"));
				a.put("t12cod_uorg_jefe", (d == null || d.isEmpty()) ? (String) e.get("t02cod_uorg")
						: (String) d.get("t02cod_uorg"));
				

			}

			pstmt.close();
			rs.close();
			conn.close();

			return a;
		} catch (Exception e) {
			log.debug("Error "+e);
			throw new SQLException(e.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}

			try {
				pstmt.close();
			} catch (Exception e) {
			}

			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return
	 * @throws SQLException
	 */
	public String findUOByVisibilidad(String dbpool, String codigo)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String codUO = "";

		try {

			strSQL = "select 	u.t12cod_nivel " + "from    t12uorga u	 "
					+ "where   u.t12ind_estad = ? and "
					+ "        u.t12cod_uorga = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, codigo);
			rs = pre.executeQuery();

			if (rs.next()) {

				int nivel = rs.getInt("t12cod_nivel");
				codUO = nivel == 0 ? "%" : codigo.substring(0, nivel + 1) + "%";
				codUO = codUO.toUpperCase();
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
		return codUO;
	}

	/**
	 * Metodo encargado de cargar el delegado de una UUOO
	 * 
	 * @param datos
	 * @return
	 * @throws IncompleteConversationalState
	 */
	public HashMap findDelegado(HashMap datos)
			throws IncompleteConversationalState {
		if(log.isDebugEnabled()) log.debug("method findDelegado");
		
		PreparedStatement pstmt = null;
		String strSQL = "";
		Connection conn = null;
		T02DAO dao = new T02DAO();
		HashMap delegado = null;
		try {

			strSQL = "select  d.cunidad_organ, d.cod_opcion, d.cod_personal_jefe, d.cod_personal_deleg "
					+ "from    t1595delega d "
					+ "where   d.cunidad_organ = ? "
					+ "        and DATE(?) between d.finivig and d.ffinvig "
					+ "        and d.sestado_activo = ? "
					+ "        and (d.cod_opcion = ? or d.cod_opcion = ?) order by d.cod_opcion ";

			conn = getConnection((String) datos.get("dbpool"));
			conn.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			
			pstmt = conn.prepareStatement(strSQL);
			
			pstmt.setString(1, (String) datos.get("codUO"));
			pstmt.setDate(2, new BeanFechaHora().getSQLDate());
			pstmt.setString(3, Constantes.ACTIVO);
			pstmt.setString(4, Constantes.DELEGA_TODO);
			pstmt.setString(5, (String) datos.get("codOpcion"));

			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				String cod_delega = rs.getString("cod_personal_deleg");

				if (cod_delega != null && !cod_delega.equals("")) {

					delegado = dao.joinWithT12T99ByCodPers((String) datos
							.get("dbpool"), cod_delega, null);
					delegado.put("cod_opcion", rs.getString("cod_opcion"));
					delegado.put("cod_jefe", rs.getString("cod_personal_jefe"));
					delegado.put("cod_delega", cod_delega);//ICAPUNAY/DTARAZONA - PAS20181U230300016 - Mejoras vacaciones2
				}

			}

			rs.close();

		} catch (Exception e) {
			log.error("Ocurrió un error en findDelegado: " + e.getMessage(), e);
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
		
		return delegado;
	}
	
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	/**
	 * Metodo encargado de listar unidades con delegacion segun registro de delegado
	 * 
	 * @param datos
	 * @return uuoosDelegadas ArrayList
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findDelegadoInUUOOs(HashMap datos)
			throws IncompleteConversationalState {

		PreparedStatement pstmt = null;
		String strSQL = "";
		Connection conn = null;		
		ArrayList uuoosDelegadas = new ArrayList();
		Utilidades common = new Utilidades();
		try {

			strSQL = "select  d.cunidad_organ, d.cod_opcion, d.cod_personal_jefe, d.cod_personal_deleg "
					+ "from    t1595delega d "
					+ "where   d.cod_personal_deleg=? "
					+ "        and DATE(?) between d.finivig and d.ffinvig "
					+ "        and d.sestado_activo = ? "
					+ "        and (d.cod_opcion = ? or d.cod_opcion = ?) order by d.cunidad_organ asc, d.cod_opcion asc ";

			conn = getConnection((String) datos.get("dbpool"));
			conn.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			
			pstmt = conn.prepareStatement(strSQL);
			pstmt.setString(1, (String) datos.get("codPers")); //registro delegado
			pstmt.setDate(2, new BeanFechaHora().getSQLDate());
			pstmt.setString(3, Constantes.ACTIVO);
			pstmt.setString(4, Constantes.DELEGA_TODO);
			pstmt.setString(5, (String) datos.get("codOpcion"));

			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);
			
             
			while (rs.next()) {
				HashMap rsMap = new HashMap();
				common.getRecordToMap(rs, rsMap, aFields);	        				
				uuoosDelegadas.add(rsMap);
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
		return uuoosDelegadas;
	}
	
	/**
	 * Metodo encargado de listar unidades con jefe o encargado segun registro de jefe/encargado
	 * 
	 * @param datos
	 * @return uuoos  ArrayList
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findJefeEncargadoInUUOOs(HashMap datos)
			throws IncompleteConversationalState {

		log.debug("ingreso a findJefeEncargadoInUUOOs");	
		PreparedStatement pstmt = null;
		String strSQL = "";
		Connection conn = null;		
		ArrayList uuoos = new ArrayList();
		Utilidades common = new Utilidades();
		try {

			strSQL = "select  u.t12cod_uorga, substr(trim(nvl(u.t12cod_encar,'')||nvl(u.t12cod_jefat,'')),1,4) as cod_jefe, u.t12cod_encar, u.t12cod_jefat "
					+ "from    t12uorga u "
					+ "where   u.t12ind_estad=? "
					+ "and (u.t12cod_encar = ? or u.t12cod_jefat = ?) order by u.t12cod_uorga asc ";

			conn = getConnection((String) datos.get("dbpool"));
			conn.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			
			pstmt = conn.prepareStatement(strSQL);
			pstmt.setString(1, Constantes.ACTIVO);
			pstmt.setString(2, (String) datos.get("codPers")); //registro jefe o encargado
			pstmt.setString(3, (String) datos.get("codPers")); //registro jefe o encargado
			
			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);
			             
			while (rs.next()) {
				HashMap rsMap = new HashMap();
				common.getRecordToMap(rs, rsMap, aFields);	        				
				uuoos.add(rsMap);
			}
			rs.close();
			log.debug("T12DAO.findJefeEncargadoInUUOOs(uuoos): "+uuoos);

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
		return uuoos;
	}
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp

	/**
	 * 
	 * @param datos
	 * @return
	 * @throws SQLException
	 */
	public HashMap findDelegados(HashMap datos) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap delegados = new HashMap();

		try {

			strSQL = "select d.cod_opcion, d.cod_personal_deleg, d.finivig, d.ffinvig "
					+ "from    t1595delega d "
					+ "where   d.cunidad_organ = ? "
//JRR-06/02/2009	+ "        and d.cod_personal_jefe = ? "
					+ "        and d.sestado_activo = ? ";

			con = getConnection((String) datos.get("dbpool"));
			pre = con.prepareStatement(strSQL);
			pre.setString(1, (String) datos.get("codUO"));
/*			pre.setString(2, (String) datos.get("codJefe"));
			pre.setString(3, Constantes.ACTIVO);*/
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();

			HashMap info = null;
			while (rs.next()) {

				info = new HashMap();

				info.put("cod_deleg", rs.getString("cod_personal_deleg"));
				info.put("fini", rs.getTimestamp("finivig"));
				info.put("ffin", rs.getTimestamp("ffinvig"));

				delegados.put(rs.getString("cod_opcion"), info);
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
		return delegados;
	}
	
	/**
	 * Metodo encargado de buscar las unidades organizacionales filtradas por un
	 * criterio con un valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanT12.
	 * @throws SQLException
	 */
	public ArrayList findUOsinAprob(String dbpool, String criterio,
			String valor, HashMap seguridad) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList unidades = null;

		try {

			strSQL = "select * from t12uorga where  t12cod_uorga not in ";
			strSQL += " (select u_organ from t1480sol_flujo where mov = ? ) ";
			strSQL += " and  t12ind_estad = ? ";

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

		        if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null){
		            strSQL += " and 1=1 ";
		        }
		        else if (
		            roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null 
		            ) {
		        	strSQL += " and (t12cod_uorga like '" +uoSeg+ "' ";
		        	strSQL += " or t12cod_uorga in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" +uoAO+ "')) ";
		        }
		        else if (
			            //roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
			            roles.get(Constantes.ROL_JEFE) != null
			            ) {
			        	strSQL += " and t12cod_uorga like '" +uoSeg+ "' ";
			        }
		        else{
		        	strSQL += " and 1=2 ";	
		        } 
			}

			
			strSQL += " order by t12cod_uorga";
			

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, criterio);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();
			unidades = new ArrayList();

			while (rs.next()) {

				BeanT12 a = new BeanT12();

				a.setT12cod_uorgan(rs.getString("t12cod_uorga"));
				a.setT12descr_uorg(rs.getString("t12des_uorga").trim());

				unidades.add(a);
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
		return unidades;
	}


}