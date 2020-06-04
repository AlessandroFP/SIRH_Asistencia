package pe.gob.sunat.sp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.dao.T1271DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.Utilidades;

/**
 * 
 * Clase       : T02DAO   
 * Descripcion : Clase para acceder a la t02perdp
 * Autor       : CGARRATT
 * Fecha       : 15-feb-2005 9:18:25
 */
public class T02DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1271DAO.class);//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
	
	public T02DAO() {
	}

	/**
	 * M�todo joinSession
	 * 
	 * @param String[]
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList joinSession(String[] p)
			throws IncompleteConversationalState {

		Utilidades common = new Utilidades();

		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String plogin = "";
		String ppass = "";
		// Build WHERE statement

		//-- Check login

		plogin = p[4];
		if (plogin != null && !plogin.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_pers = '" + common.replace(plogin, "'", "''")
					+ "'";
		}

		//-- Check password

		ppass = p[5];
		if (ppass != null && !ppass.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02lib_elec = '" + common.replace(ppass, "'", "''")
					+ "'";
		}

		sWhere += " and (((t99tip_desc)='D') AND ((t99cod_tab)='001')) ";

		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		// Build ORDER statement
		sOrder = " ";

		// Build full SQL statement

		sSQL = "SELECT t02cod_pers, t02ap_pate , t02ap_mate , t02nombres , t02lib_elec, t02cod_uorg, t12des_uorga, t12des_corta, t02cod_cate, t02cod_catel, t99descrip, t99abrev, t02cod_stat "
				+ " FROM (t02perdp LEFT JOIN t12uorga ON t02cod_uorg = t12cod_uorga) LEFT JOIN t99codigos ON t02cod_cate = t99codigo ";

		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;
		try {
			T99DAO dao99 = new T99DAO();
			conn = getConnection(p[1]);
			pstmt = conn.prepareStatement(selectStatement);
			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				java.util.Hashtable rsHash = new java.util.Hashtable();
				common.getRecordToHash(rs, rsHash, aFields);
				String catel = (String) rsHash.get("t02cod_catel");
				HashMap a = null;
				if (catel != null && catel != "") {
					a = dao99.findByCodParam(p[1], "001", catel.trim());
				}
				rsHash.put("t02cod_catel_desc", a == null ? " " : catel.trim()
						+ "-" + ((String) a.get("t99abrev")).trim());
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
	 * M�todo joinWithT02T12T99
	 * 
	 * @param HashMap
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList joinWithT02T12T99(HashMap p)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();

		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String preg = "";
		String pappat = "";
		String papmat = "";
		String pnomb = "";
		String puorg = "";
		String pcodcate = "";
		String pestado = "";
		String pcampo = "";
		String porden = "";
		
		// Build WHERE statement

		//-- Check reg

		preg = ((String) p.get("p3")).trim();
		if (preg != null && !preg.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_pers = '" + common.replace(preg, "'", "''") + "'";
		}

		//-- Check appat

		pappat = ((String) p.get("p4")).trim();
		if (pappat != null && !pappat.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_pate like '" + common.replace(pappat, "'", "''")
					+ "%'";
		}

		//-- Check apmat

		papmat = ((String) p.get("p5")).trim();
		if (papmat != null && !papmat.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_mate like '" + common.replace(papmat, "'", "''")
					+ "%'";
		}

		//-- Check nomb

		pnomb = ((String) p.get("p6")).trim();
		if (pnomb != null && !pnomb.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02nombres like '%" + common.replace(pnomb, "'", "''")
					+ "%'";
		}

		//-- Check uorg

		puorg = ((String) p.get("p7")).trim();
		if (puorg != null && !puorg.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_uorg = '" + common.replace(puorg, "'", "''")
					+ "'";
		}
		//-- Check cate

		pcodcate = ((String) p.get("p8")).trim();
		if (pcodcate != null && !pcodcate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_cate = '" + common.replace(pcodcate, "'", "''")
					+ "'";
		}

		//-- Check check

		pestado = ((String) p.get("p12")).trim();
		if (pestado != null && !pestado.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_stat = '" + common.replace(pestado, "'", "''")
					+ "'";
		}

		sWhere += " and (((t99tip_desc)='D') AND ((t99cod_tab)='001')) ";

		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		// Build ORDER statement
		pcampo = (String) p.get("p9");
		porden = (String) p.get("p10");

		if (pcampo != null && !pcampo.equals("")) {
			if (pcampo.equals("1")) {
				sOrder = " order by t02cod_pers " + porden;
			}
			if (pcampo.equals("2")) {
				sOrder = " order by t02ap_pate " + porden + ", t02ap_mate "
						+ porden;
			}
			if (pcampo.equals("3")) {
				sOrder = " order by t02cod_uorg " + porden;
			}
			if (pcampo.equals("4")) {
				sOrder = " order by t02cod_cate " + porden;
			}
		} else {
			sOrder = " order by t02ap_pate, t02ap_mate";
		}
		// Build full SQL statement

		sSQL = "SELECT t02cod_pers, t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, t12des_uorga, t12des_corta, t02cod_cate, t02cod_catel, t99descrip, t99abrev, t02cod_stat "
				+ " FROM (t02perdp LEFT JOIN t12uorga ON t02cod_uorg = t12cod_uorga) LEFT JOIN t99codigos ON t02cod_cate = t99codigo ";

		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			T99DAO dao99 = new T99DAO();
			TelefonoDAO daotelefono = new TelefonoDAO();
			conn = getConnection((String) p.get("p1"));
			pstmt = conn.prepareStatement(selectStatement);
			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				java.util.Hashtable rsHash = new java.util.Hashtable();
				common.getRecordToHash(rs, rsHash, aFields);
				String catel = ((String) rsHash.get("t02cod_catel")).trim();
				String codpers = ((String) rsHash.get("t02cod_pers")).trim();

				HashMap a = null;
				ArrayList b = null;
				String ListaTelefonos = "";

				if (catel.length() > 1) {
					a = dao99.findByCodParam(((String) p.get("p1")).trim(),
							"001", catel.trim());
				}
				if (codpers != null && codpers != "") {
					b = daotelefono.findByPers(((String) p.get("p2")).trim(),
							codpers);
				}
				int n = b.size();
				if (n == 0) {
					ListaTelefonos = "No registra";
				} else {
					ListaTelefonos = "<select>";
					Hashtable ht = null;
					for (int i = 0; i < n; i++) {
						ht = (Hashtable) b.get(i);
						ListaTelefonos = ListaTelefonos + "<option>"
								+ ((String) ht.get("numero")).trim()
								+ "</option>";
					}
					ListaTelefonos = ListaTelefonos + "</select>";
				}

				rsHash.put("t02cod_catel_desc", a == null ? " " : catel.trim()
						+ "-" + ((String) a.get("t99abrev")).trim());
				rsHash.put("t02telefonos_desc", b == null ? "No registra"
						: ListaTelefonos.trim());
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

	public ArrayList joinWithT02T12T99(String dbpool_sp, String orden,
			String dOrden, String reg, String appat, String apmat, String nomb,
			String uorg, String estado, String codcate)
			throws IncompleteConversationalState {

		Utilidades common = new Utilidades();

		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String preg = "";
		String pappat = "";
		String papmat = "";
		String pnomb = "";
		String puorg = "";
		String pcodcate = "";
		String pestado = "";
		String porden = "";

		// Build WHERE statement

		//-- Check reg

		preg = reg;
		if (preg != null && !preg.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_pers = '" + common.replace(preg, "'", "''") + "'";
		}

		//-- Check appat

		pappat = appat;
		if (pappat != null && !pappat.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_pate like '%" + common.replace(pappat, "'", "''")
					+ "%'";
		}

		//-- Check apmat

		papmat = apmat;
		if (papmat != null && !papmat.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_mate like '%" + common.replace(papmat, "'", "''")
					+ "%'";
		}

		//-- Check nomb

		pnomb = nomb;
		if (pnomb != null && !pnomb.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02nombres like '%" + common.replace(pnomb, "'", "''")
					+ "%'";
		}

		//-- Check uorg

		puorg = uorg;
		if (puorg != null && !puorg.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_uorg = '" + common.replace(puorg, "'", "''")
					+ "'";
		}
		//-- Check cate

		pcodcate = codcate;
		if (pcodcate != null && !pcodcate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_cate = '" + common.replace(pcodcate, "'", "''")
					+ "'";
		}

		//-- Check check

		pestado = estado;
		if (pestado != null && !pestado.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_stat = '" + common.replace(pestado, "'", "''")
					+ "'";
		}

		sWhere += " and (((t99tip_desc)='D') AND ((t99cod_tab)='001')) ";

		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		// Build ORDER statement
		porden = orden;
		if (porden != null && !porden.equals("")) {
			if (porden.equals("1")) {
				sOrder = " order by t02cod_pers " + dOrden;
			}
			if (porden.equals("2")) {
				sOrder = " order by t02ap_pate " + dOrden + ", t02ap_mate "
						+ dOrden;
			}
			if (porden.equals("3")) {
				sOrder = " order by t02cod_uorg " + dOrden;
			}
			if (porden.equals("4")) {
				sOrder = " order by t02cod_cate " + dOrden;
			}
		} else {
			sOrder = " order by t02ap_pate, t02ap_mate";
		}
		// Build full SQL statement

		sSQL = "SELECT t02cod_pers, t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, t12des_uorga, t12des_corta, t02cod_cate, t02cod_catel, t99descrip, t99abrev, t02cod_stat "
				+ " FROM (t02perdp LEFT JOIN t12uorga ON t02cod_uorg = t12cod_uorga) LEFT JOIN t99codigos ON t02cod_cate = t99codigo ";

		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			T99DAO dao99 = new T99DAO();
			conn = getConnection(dbpool_sp);
			pstmt = conn.prepareStatement(selectStatement);
			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				java.util.Hashtable rsHash = new java.util.Hashtable();
				common.getRecordToHash(rs, rsHash, aFields);
				String catel = (String) rsHash.get("t02cod_catel");
				HashMap a = null;
				if (catel != null && catel != "") {
					a = dao99.findByCodParam(dbpool_sp, "001", catel.trim());
				}
				rsHash.put("t02cod_catel_desc", a == null ? " " : catel.trim()
						+ "-" + ((String) a.get("t99abrev")).trim());
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
	 * Metodo findByNoReg
	 * 
	 * @param String
	 *            dbpool
	 * @param String
	 *            t02cod_pers
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByNoReg(String dbpool, String t02cod_pers)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sSQL = "";

		String pt02cod_pers = "";

		// Build WHERE statement

		pt02cod_pers = t02cod_pers;
		hasParam = true;
		sWhere += " t02cod_pers = '" + common.replace(pt02cod_pers, "'", "''")
				+ "'";
		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}

		sSQL = "SELECT t02cod_pers, t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, t02cod_uorgl, t02cod_cate, t02cod_catel, t02cod_stat "
				+ " FROM t02perdp ";

		sSQL = sSQL + sWhere;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;

		try {
			T12DAO dao12 = new T12DAO();
			T99DAO dao99 = new T99DAO();

			conn = getConnection(dbpool);
			pstmt = conn.prepareStatement(selectStatement);
			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);

			while (rs.next()) {
				java.util.Hashtable rsHash = new java.util.Hashtable();
				common.getRecordToHash(rs, rsHash, aFields);

				String fldt02cod_uorg = (String) rsHash.get("t02cod_uorg");
				String fldt02cod_uorgl = (String) rsHash.get("t02cod_uorgl");
				String fldt02cod_cate = (String) rsHash.get("t02cod_cate");
				String fldt02cod_catel = (String) rsHash.get("t02cod_catel");
				HashMap a = null;
				HashMap b = null;
				HashMap c = null;
				HashMap d = null;
				if (fldt02cod_uorg != null && fldt02cod_uorg != "") {
					a = dao12.findByCodUorga(dbpool, fldt02cod_uorg.trim());
				}
				if (fldt02cod_uorgl != null && fldt02cod_uorgl != "") {
					b = dao12.findByCodUorga(dbpool, fldt02cod_uorgl.trim());
				}
				if (fldt02cod_cate != null && fldt02cod_cate != "") {
					c = dao99.findByCodParam(dbpool, "001", fldt02cod_cate
							.trim());
				}
				if (fldt02cod_catel != null && fldt02cod_catel != "") {
					d = dao99.findByCodParam(dbpool, "001", fldt02cod_catel
							.trim());
				}
				rsHash.put("t02cod_uorg_desc", a == null ? " " : fldt02cod_uorg
						.trim()
						+ "-" + ((String) a.get("t12des_corta")).trim());
				rsHash.put("t02cod_uorgl_desc", b == null ? " "
						: fldt02cod_uorgl.trim() + "-"
								+ ((String) b.get("t12des_corta")).trim());
				rsHash.put("t02cod_cate_desc", c == null ? " " : fldt02cod_cate
						.trim()
						+ "-" + ((String) c.get("t99abrev")).trim());
				rsHash.put("t02cod_catel_desc", d == null ? " "
						: fldt02cod_catel.trim() + "-"
								+ ((String) d.get("t99abrev")).trim());
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

		//-- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_pers like '" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_pate like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_mate like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("3")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02nombres like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}

		//-- Check vigente

		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_stat = '" + common.replace(pvigente, "'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t02ap_pate, t02ap_mate";

		sSQL = "SELECT * from t02perdp ";
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
	 * Metodo findByCodPers
	 * 
	 * @param String
	 *            dbpool_sp
	 * @param String
	 *            codPers
	 * @return HashMap hMap
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByCodPers(String dbpool_sp, String codPers)
			throws IncompleteConversationalState {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap();

		//selectStatement = " SELECT t02cod_pers, t02ap_pate, t02ap_mate, t02nombres, t02lib_elec , t02cod_uorg, t02cod_uorgl, t02cod_cate, t02cod_catel, t02cod_stat FROM t02perdp WHERE t02cod_pers = ? "; //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		//selectStatement = " SELECT t02cod_pers, t02ap_pate, t02ap_mate, t02nombres, t02lib_elec , t02cod_uorg, t02cod_uorgl, t02cod_cate, t02cod_catel, t02cod_stat, t02cod_rel FROM t02perdp WHERE t02cod_pers = ? "; // se a?o t02cod_rel ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		selectStatement = " SELECT t02cod_pers, t02ap_pate, t02ap_mate, t02nombres, t02lib_elec , t02cod_uorg, t02cod_uorgl, t02cod_cate, t02cod_catel, t02cod_stat, t02cod_rel, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) as t02cod_uuoo FROM t02perdp WHERE t02cod_pers = ? "; // se anade t02cod_uuoo  ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
		try {
			conn = getConnection(dbpool_sp);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, codPers);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				hMap.put("t02cod_pers", rs.getString("t02cod_pers"));
				hMap.put("t02ap_pate", rs.getString("t02ap_pate"));
				hMap.put("t02ap_mate", rs.getString("t02ap_mate"));
				hMap.put("t02nombres", rs.getString("t02nombres"));
				hMap.put("t02lib_elec", rs.getString("t02lib_elec"));
				hMap.put("t02cod_uorg", rs.getString("t02cod_uorg"));
				hMap.put("t02cod_uorgl", rs.getString("t02cod_uorgl"));
				hMap.put("t02cod_cate", rs.getString("t02cod_cate"));
				hMap.put("t02cod_catel", rs.getString("t02cod_catel"));
				hMap.put("t02cod_stat", rs.getString("t02cod_stat"));
				hMap.put("t02cod_rel", rs.getString("t02cod_rel")); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				hMap.put("t02cod_uuoo", rs.getString("t02cod_uuoo")); //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			} else {
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
				hMap.put("t02cod_rel", "-"); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				hMap.put("t02cod_uuoo", "-"); //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
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
	 * Metodo findByBusquedaSeleccion
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

		//-- Check criterio
		if (pcriterio.equals("0")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_pers like '" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("1")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_pate like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("2")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_mate like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}
		if (pcriterio.equals("3")) {
			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02nombres like '%" + common.replace(pvalor, "'", "''")
					+ "%'";
		}

		//-- Check vigente

		if (pvigente != null && !pvigente.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_stat = '" + common.replace(pvigente, "'", "''")
					+ "'";
		}

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t02ap_pate, t02ap_mate";

		sSQL = "SELECT * from t02perdp ";
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
	 * Metodo encargado de buscar los datos de un determinado trabajador.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Registro del trabajador.
	 * @return HashMap conteniendo el registro obtenido.
	 * @throws SQLException
	 */
	public HashMap findEmpleado(String dbpool, String codPers)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap a = new HashMap();

		try {

			strSQL = "select  p.t02cod_pers, " + "        p.t02ap_pate, " + "        p.t02cod_rel, "
					+ "        p.t02ap_mate, " + "        p.t02nombres, "
					+ "        p.t02cod_uorg, " + "        p.t02cod_cate, "
					+ "        p.t02cod_regl, " + "        p.t02f_ingsun, "
					+ "        p.t02f_cese, " + "        p.t02f_nacim, "
					+ "        p.t02direccion, " + "        p.t02urban, "
					+ "        p.t02refer, " + "        p.t02lib_elec, p.t02cod_stat, "
					+ "        uo.t12des_uorga, " + "        uo.t12cod_jefat, "
					+ "        uo.t12cod_encar, " + "        param.t99descrip "
					+ "from    t02perdp p, " + "        t12uorga uo, "
					+ "        t99codigos param "
					+ "where   p.t02cod_pers = ? and  "
					+ " 	   p.t02cod_uorg = uo.t12cod_uorga and "
					+ "        p.t02cod_cate = param.t99codigo and "
					+ "        param.t99cod_tab = '001' and "
					+ "        param.t99tip_desc = 'D' ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codPers.trim().toUpperCase());
			//pre.setString(2, "1");
			rs = pre.executeQuery();

			if (rs.next()) {
				a.put("t02cod_rel", rs.getString("t02cod_rel"));
				a.put("t02cod_pers", rs.getString("t02cod_pers"));
				a.put("t02ap_pate", rs.getString("t02ap_pate").trim());
				a.put("t02ap_mate", rs.getString("t02ap_mate").trim());
				a.put("t02nombres", rs.getString("t02nombres").trim());
				a.put("t02cod_uorg", rs.getString("t02cod_uorg"));
				a.put("t12des_uorga", rs.getString("t12des_uorga").trim());
				a.put("t02cod_cate", rs.getString("t02cod_cate"));
				a.put("t02direccion", rs.getString("t02direccion"));
				a.put("t02urban", rs.getString("t02urban"));
				a.put("t02refer", rs.getString("t02refer"));
				a.put("t02des_cate", rs.getString("t99descrip").trim());
				a.put("t12cod_jefe", rs.getString("t12cod_jefat") != null ? rs
						.getString("t12cod_jefat") : rs
						.getString("t12cod_encar"));
				a.put("t02cod_regl", rs.getString("t02cod_regl"));
				a.put("t02f_ingsun", rs.getTimestamp("t02f_ingsun"));
				a.put("t02f_cese", rs.getTimestamp("t02f_cese"));
				a.put("t02f_nacim", rs.getDate("t02f_nacim"));
				a.put("t02lib_elec", rs.getString("t02lib_elec"));
				a.put("t02cod_stat", rs.getString("t02cod_stat"));

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
		return a;
	}
	//dtarazona
	/**
	 * Metodo encargado de buscar los datos de un determinado trabajador.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Registro del trabajador.
	 * @return HashMap conteniendo el registro obtenido.
	 * @throws SQLException
	 */
	public HashMap findEmpleadoPorRegistro(String dbpool, String codPers)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap a = new HashMap();

		try {

			strSQL = "select  p.t02cod_pers, " + "        p.t02ap_pate, " + "        p.t02cod_rel, "
					+ "        p.t02ap_mate, " + "        p.t02nombres, "
					+ "        p.t02cod_uorg, " + "        p.t02cod_cate, "
					+ "        p.t02cod_regl, " + "        p.t02f_ingsun, "
					+ "        p.t02f_cese, " + "        p.t02f_nacim, "
					+ "        p.t02direccion, " + "        p.t02urban, "
					+ "        p.t02refer, " + "        p.t02lib_elec, p.t02cod_stat "
					+ "from    t02perdp p "
					+ "where   p.t02cod_pers = ? ";
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codPers.trim().toUpperCase());
			//pre.setString(2, "1");
			rs = pre.executeQuery();

			if (rs.next()) {
				a.put("t02cod_rel", rs.getString("t02cod_rel"));
				a.put("t02cod_pers", rs.getString("t02cod_pers"));
				a.put("t02ap_pate", rs.getString("t02ap_pate").trim());
				a.put("t02ap_mate", rs.getString("t02ap_mate").trim());
				a.put("t02nombres", rs.getString("t02nombres").trim());
				a.put("t02cod_uorg", rs.getString("t02cod_uorg"));
				a.put("t02cod_cate", rs.getString("t02cod_cate"));
				a.put("t02direccion", rs.getString("t02direccion"));
				a.put("t02urban", rs.getString("t02urban"));
				a.put("t02refer", rs.getString("t02refer"));
				a.put("t02cod_regl", rs.getString("t02cod_regl"));
				a.put("t02f_ingsun", rs.getTimestamp("t02f_ingsun"));
				a.put("t02f_cese", rs.getTimestamp("t02f_cese"));
				a.put("t02f_nacim", rs.getDate("t02f_nacim"));
				a.put("t02lib_elec", rs.getString("t02lib_elec"));
				a.put("t02cod_stat", rs.getString("t02cod_stat"));
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
		return a;
	}

	//fin dtarazona
	/**
	 * Metodo encargado de buscar los datos de un determinado trabajador.
	 * 
	 * @param dbpool
	 *            DataSource. Pool de conexiones.
	 * @param codPers
	 *            String. Registro del trabajador.
	 * @return HashMap conteniendo el registro obtenido.
	 * @throws SQLException
	 */
	public HashMap findEmpleadoDS(DataSource dbpool, String codPers)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap a = new HashMap();

		try {

			strSQL = "select  p.t02cod_pers, " + "        p.t02ap_pate, "
					+ "        p.t02ap_mate, " + "        p.t02nombres, "
					+ "        p.t02cod_uorg, " + "        p.t02cod_cate, "
					+ "        p.t02cod_regl, " + "        p.t02f_ingsun, "
					+ "        p.t02f_cese, " + "        p.t02f_nacim, "
					+ "        p.t02direccion, " + "        p.t02urban, "
					+ "        p.t02refer, " + "        p.t02lib_elec, p.t02cod_stat, "
					+ "        uo.t12des_uorga, " + "        uo.t12cod_jefat, "
					+ "        uo.t12cod_encar, " + "        param.t99descrip "
					+ "from    t02perdp p, " + "        t12uorga uo, "
					+ "        t99codigos param "
					+ "where   p.t02cod_pers = ? and  "
					+ " 	   p.t02cod_uorg = uo.t12cod_uorga and "
					+ "        p.t02cod_cate = param.t99codigo and "
					+ "        param.t99cod_tab = '001' and "
					+ "        param.t99tip_desc = 'D' ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codPers.trim().toUpperCase());
			//pre.setString(2, "1");
			rs = pre.executeQuery();

			if (rs.next()) {

				a.put("t02cod_pers", rs.getString("t02cod_pers"));
				a.put("t02ap_pate", rs.getString("t02ap_pate").trim());
				a.put("t02ap_mate", rs.getString("t02ap_mate").trim());
				a.put("t02nombres", rs.getString("t02nombres").trim());
				a.put("t02cod_uorg", rs.getString("t02cod_uorg"));
				a.put("t12des_uorga", rs.getString("t12des_uorga").trim());
				a.put("t02cod_cate", rs.getString("t02cod_cate"));
				a.put("t02direccion", rs.getString("t02direccion"));
				a.put("t02urban", rs.getString("t02urban"));
				a.put("t02refer", rs.getString("t02refer"));
				a.put("t02des_cate", rs.getString("t99descrip").trim());
				a.put("t12cod_jefe", rs.getString("t12cod_jefat") != null ? rs
						.getString("t12cod_jefat") : rs
						.getString("t12cod_encar"));
				a.put("t02cod_regl", rs.getString("t02cod_regl"));
				a.put("t02f_ingsun", rs.getTimestamp("t02f_ingsun"));
				a.put("t02f_cese", rs.getTimestamp("t02f_cese"));
				a.put("t02f_nacim", rs.getDate("t02f_nacim"));
				a.put("t02lib_elec", rs.getString("t02lib_elec"));
				a.put("t02cod_stat", rs.getString("t02cod_stat"));

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
		return a;
	}	
	
	
	/**
	 * Metodo encargado de buscar los datos los trabajadores que pertenezcan a
	 * una unidad organizacional.
	 * 
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @return HashMap conteniendo el registro obtenido.
	 * @throws SQLException
	 */
	public ArrayList joinWithT12T99ByNombreCodUO(HashMap params)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;
		try {
			String dbpool = (String) params.get("dbpool");
			String nombre = (String) params.get("nombre");
			String codUO = (String) params.get("codUO");

			strSQL = new StringBuffer("select p.t02cod_pers, p.t02ap_pate, ")
					.append("       p.t02ap_mate, p.t02nombres, ")
					.append("       p.t02cod_uorg, p.t02cod_cate, ")
					.append("       uo.t12des_uorga, param.t99descrip ")
					.append("from   t02perdp p, ")
					.append("       t12uorga uo, ")
					.append("       t99codigos param ")
					.append(
							"where  ( upper(trim(p.t02ap_pate)) || ' ' || upper(trim(p.t02ap_mate))) || ' ' || upper(trim(p.t02nombres)) like '"
									+ nombre.trim().toUpperCase() + "%' and ")
					.append(
							"       p.t02cod_uorg like '"
									+ codUO.trim().toUpperCase() + "%' and ")
					.append("       p.t02cod_uorg = uo.t12cod_uorga and ")
					.append("       p.t02cod_cate = param.t99codigo and ")
					.append("       param.t99cod_tab = '001' and ")
					.append("       param.t99tip_desc = 'D' ")
					.append("order by p.t02nombres, p.t02ap_pate, p.t02ap_mate")
					.toString();

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			rs = pre.executeQuery();

			lista = new ArrayList();

			while (rs.next()) {
				HashMap res = new HashMap();

				res.put("t02cod_pers", rs.getString("t02cod_pers"));
				res.put("t02ap_pate", rs.getString("t02ap_pate").trim());
				res.put("t02ap_mate", rs.getString("t02ap_mate").trim());
				res.put("t02nombres", rs.getString("t02nombres").trim());
				res.put("t02cod_uorg", rs.getString("t02cod_uorg"));
				res.put("t12des_uorga", rs.getString("t12des_uorga").trim());
				res.put("t02cod_cate", rs.getString("t02cod_cate"));
				res.put("t02desc_cate", rs.getString("t99descrip").trim());

				lista.add(res);
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
			//e.printStackTrace();
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
		return lista;
	}

	  /**
	   * Metodo encargado de buscar los trabajadores filtrados
	   * por un criterio con un valor determinado.
	   * Join con las tablas T12UOrga y T99Codigos.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param criterio        String. Criterio de filtro de los registros.
	   * @param valor           String. Valor de filtro para el criterio dado.
	   * @return                ArrayList conteniendo los registros cargados
	   *                        en HashMaps.
	   * @throws SQLException
	   */
	  public ArrayList joinWithT12T99(String dbpool, String criterio, String valor, HashMap seguridad) throws
	      SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    ArrayList personal = null;

	    try {

	      strSQL =
	          "select  p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres," +
	          "        substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, "+
			  "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, " +
	          "        p.t02cod_stat, p.t02f_ingsun, uo.t12des_corta, param.t99descrip, " +
	          "		   (select g.fecha from t1456vacacion_gen g "+             
			  "  		where g.cod_pers = p.t02cod_pers and est_id = ? ) frepuesto "+
	          "from    t02perdp p, " +
	          "        t12uorga uo, " +
	          "        t99codigos param " +
	          "where   p.t02cod_stat = ? ";

	      //busqueda por codigo
	      if (criterio.equals("0")) {
	        strSQL += " and p.t02cod_pers like '" +valor.trim().toUpperCase() + "%' ";
	      }
	      //busqueda por apellido paterno
	      if (criterio.equals("1")) {
	        strSQL += " and p.t02ap_pate like '" + valor.trim().toUpperCase() +"%' ";
	      }
	      //busqueda por uorgan
	      if (criterio.equals("2")) {
	        strSQL += " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" +valor.trim().toUpperCase() + "%' ";
	      }
	      //busqueda por categoria
	      if (criterio.equals("3")) {
	        strSQL += " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) like '" +valor.trim().toUpperCase() + "%' ";
	      }
	      //prac-asanchez
	      if (criterio.equals("4")) {
	    	  	int separacion = valor.indexOf("|");
	    	  	String paterno = valor.substring(0, separacion);
	    	  	String materno = valor.substring(separacion + 1,valor.length());
	    	  	if(!paterno.equals("") && materno.equals("")){
	    	  		strSQL += " and p.t02ap_pate like '" + paterno.trim().toUpperCase() +"%' ";
	    	  	}
	    	  	if(paterno.equals("") && !materno.equals("")){
	    	  		strSQL += " and p.t02ap_mate like '" + materno.trim().toUpperCase() +"%' ";
	    	  	}
	    	  	if(!paterno.equals("") && !materno.equals("")){
	    	  		strSQL += " and p.t02ap_pate like '" + paterno.trim().toUpperCase() +"%' " +
	    	  				"and p.t02ap_mate like '" + materno.trim().toUpperCase() +"%' ";
	    	  	}
	      }
	     
	      //criterios de visibilidad
	      if (seguridad != null) {

	        HashMap roles = (HashMap) seguridad.get("roles");
	        String uoSeg = (String) seguridad.get("uoSeg");
	        String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

	        if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null){
	            strSQL += " and 1=1 ";
	        }//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ) {
	        	    strSQL += " and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" +uoSeg+ "') ";		        	
	        	    strSQL += "   or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" +uoAO+ "'))) ";
		        }
	        else if (
		        	//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
		        	roles.get(Constantes.ROL_SECRETARIA) != null ||
		            roles.get(Constantes.ROL_JEFE) != null
		            //JRR - 22/09/2009
		            || roles.get(Constantes.ROL_ANALISTA_LEXC) != null
		            //
		            ) {
		        	strSQL += " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" +uoSeg+ "' ";
		        }
	        else{
	        	strSQL += " and 1=2 ";
	        }        
	      }

	      strSQL += " and param.t99cod_tab = ? "+
	      			" and param.t99tip_desc = ? "+
					" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga " +
	                " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ";

	      strSQL += "order by p.t02ap_pate";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, Constantes.ACTIVO);
	      pre.setString(2, Constantes.ACTIVO);
	      pre.setString(3, Constantes.CODTAB_CATEGORIA);
	      pre.setString(4, Constantes.T99DETALLE);
	      rs = pre.executeQuery();
	      personal = new ArrayList();

	      while (rs.next()) {

	        HashMap a = new HashMap();

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02ap_pate", rs.getString("t02ap_pate").trim());
	        a.put("t02ap_mate", rs.getString("t02ap_mate").trim());
	        a.put("t02nombres", rs.getString("t02nombres").trim());
	        a.put("t02cod_uorg", rs.getString("cod_uorg"));
	        a.put("t12des_uorga", rs.getString("t12des_corta"));
	        a.put("t02cod_cate", rs.getString("cod_cate"));
	        a.put("t02desc_cate", rs.getString("t99descrip").trim());
	        a.put("t02cod_stat", rs.getString("t02cod_stat"));
	        a.put("t02f_ingsun", Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun")));
	        a.put("f_repuesto", Utiles.timeToFecha(rs.getTimestamp("frepuesto")));
	        a.put("fecha", a.get("f_repuesto")!=null?(String)a.get("t02f_ingsun")+" - "+(String)a.get("f_repuesto"):(String)a.get("t02f_ingsun"));

	        personal.add(a);
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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return personal;
	  }	
	
	  /**
	   * Metodo encargado de buscar los datos de un determinado trabajador.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param codPers         String. Numero de registro del trabajador.
	   * @return                HashMap conteniendo el registro obtenido.
	   * @throws SQLException
	   */
	  public HashMap joinWithT12T99ByCodPers(String dbpool, String codPers, HashMap seguridad) throws SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    HashMap a = new HashMap();
	    
	    //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	    ArrayList aLRpta = new ArrayList();
	    Utilidades common = new Utilidades();
	    String strSQL2 = "";
	    PreparedStatement pre2 = null;
	    Connection con2 = null;
	    ResultSet rs2 = null;
    	
	    HashMap hm = null;
	    String codUoJef = "";	 
	    //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp

	    try {

	      strSQL = "select  p.t02cod_pers, " +
	          "        p.t02ap_pate, " +
	          "        p.t02ap_mate, " +
	          "        p.t02nombres, " +
	          "        substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, "+
			  "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, " +
			//JVV - INI
			  "        p.t02cod_rel, " +
			  //JVV - FIN
	          "        p.t02cod_regl, " +
	          "        p.t02f_ingsun, " +
	          "        p.t02f_nacim, " +
	          "        uo.t12des_uorga, " +
	          "        substr(trim(nvl(uo.t12cod_encar,'')||nvl(uo.t12cod_jefat,'')),1,4) cod_jefe, " +	          
	          "        param.t99descrip " +
	          "from    t02perdp p, " +
	          "        t12uorga uo, " +
	          "        t99codigos param " +
	          "where   p.t02cod_pers = ?  " ;//+
	          //" and       p.t02cod_stat = ? ";

	      //criterios de visibilidad
	      if (seguridad != null) {

	        HashMap roles = (HashMap) seguridad.get("roles");
	        String codPersUsuario = ((String) seguridad.get("codPers")).trim(); //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	        String uoSeg = (String) seguridad.get("uoSeg");
	        String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

	        if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null){
	            strSQL += " and 1=1 ";
	        }
	        //DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        else if (
	            roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ) {
	        	strSQL += " and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" +uoSeg+ "') ";
	        	strSQL += " or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" +uoAO+ "'))) ";
	        	
	        }
	        else if (
		            //roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
		            roles.get(Constantes.ROL_SECRETARIA) != null ||
		            //roles.get(Constantes.ROL_JEFE) != null || //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
		            roles.get(Constantes.ROL_ANALISTA_LEXC) != null
		            
		            ) {
		        		strSQL += " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" +uoSeg+ "' ";
		    }
	        //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
	        else if (roles.get(Constantes.ROL_JEFE) != null){
	        			log.debug("joinWithT12T99ByCodPers(jefe)-uoAO: "+uoAO);
	        			
	        			//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	        			/*uoSeg = findUuooJefe(uoAO)+"%";
	        			log.debug("joinWithT12T99ByCodPers-uoSeg: "+uoSeg); 
	        			strSQL += " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '" +uoSeg+ "' ";*/
	        			if(!codPers.trim().equals(codPersUsuario.trim())){//codPers (solicitante) <> codPersUsuario (jefe)
	        				log.debug("codPers (solicitante) <> codPersUsuario (jefe)");
	        				log.debug("codPers: "+codPers);
	        				log.debug("codPersUsuario: "+codPersUsuario);
		        			strSQL2 = "SELECT * FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)='"+codPersUsuario+"' and t12ind_estad='1'"; 
		        			log.debug("joinWithT12T99ByCodPers(jefe)-strSQL2: "+strSQL2);
		        		    con2 = getConnection(dbpool);
		        			pre2 = con2.prepareStatement(strSQL2);
		        			rs2 = pre2.executeQuery();	        			
		        			String[] aFields = common.getFieldsName(rs2);
	
		        			while (rs2.next()) {
		        				HashMap rsMap = new HashMap();
		        				common.getRecordToMap(rs2, rsMap, aFields);	        				
		        				aLRpta.add(rsMap);
		        			}
		        		  	if (aLRpta!=null & aLRpta.size()>0){    			   		      
			          		      for (int i = 0;i<aLRpta.size();i++){ //0,1,2 (3 unidades)
			          		    	  hm=(HashMap)aLRpta.get(i);
			          		    	  codUoJef= ((String)hm.get("t12cod_uorga")).trim();
			          		    	  if (i==0){//0 (primer registro)
			          		    		strSQL += " and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef) + "%' "; 
			          		    	  }else{
			          		    		strSQL += " or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef)+ "%' "; 
			          		    	  }    		    	  
			          		    	  if (i==aLRpta.size()-1){//2 (ultimo registro)
			          		    		strSQL += ")";
			          		    	  }
			          		      }
		          		  	}
		        		  	log.debug("joinWithT12T99ByCodPers(jefe)-strSQL: "+strSQL);
	        			} 	
	          		  	//FIN  ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	        }
	        //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
	        else{
	        	strSQL += " and 1=2 ";	
	        }               
	      }

	      strSQL += " and param.t99cod_tab = ? "+
					" and param.t99tip_desc = ? "+
					" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga " +
					" and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ";
	         

	      con = getConnection(dbpool);
	      log.debug("joinWithT12T99ByCodPers(jefe)-strSQL final: "+strSQL);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, codPers.toUpperCase());
	      //pre.setString(2, Constantes.ACTIVO); EBV 03/05/2006 Se busca todos los Trab.
	      pre.setString(2, Constantes.CODTAB_CATEGORIA);
	      pre.setString(3, Constantes.T99DETALLE);
	      	      	      
	      rs = pre.executeQuery();

	      if (rs.next()) {

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02ap_pate", rs.getString("t02ap_pate").trim());
	        a.put("t02ap_mate", rs.getString("t02ap_mate").trim());
	        a.put("t02nombres", rs.getString("t02nombres").trim());
	        a.put("t02cod_uorg", rs.getString("cod_uorg"));
	        a.put("t12des_uorga", rs.getString("t12des_uorga").trim());
	        a.put("t02cod_cate", rs.getString("cod_cate"));
	        a.put("t02desc_cate", rs.getString("t99descrip").trim());
	        a.put("t12cod_jefe", rs.getString("cod_jefe"));
	      //JVV - INI
	        a.put("t02cod_rel", rs.getString("t02cod_rel"));
	        //JVV - FIN
	        a.put("t02cod_regl", rs.getString("t02cod_regl"));
	        a.put("t02f_ingsun", Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun")));
	        a.put("t02f_nacim", Utiles.dateToString(rs.getDate("t02f_nacim")));

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
	      
	      //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	      if (rs2 != null) {
		     rs2.close();
		  }
		  if (pre2 != null) {
		     pre2.close();
		  }
		  if (con2 != null) {
		     con2.close();
		  }
		  //ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	  
		  
	    }

	    catch (Exception e) {
	    	//e.printStackTrace();
	    	throw new SQLException(e.toString());
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return a;
	  }	
	  
	  
	/**
	 * M�todo joinWithT02T12T99
	 * 
	 * @param HashMap
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList busquedaAyudas(HashMap p)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String pt02cod_pers = "";
		String pt02ap_pate = "";
		String pt02ap_mate = "";
		String pt02nombres = "";
		String pt02cod_uorg = "";
		String pt02cod_cate = "";
		String pt02cod_stat = "";

		// Build WHERE statement

		//-- Check reg

		pt02cod_pers = ((String) p.get("t02cod_pers")).trim();
		if (pt02cod_pers != null && !pt02cod_pers.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_pers = '"
					+ common.replace(pt02cod_pers, "'", "''") + "'";
		}

		//-- Check appat

		pt02ap_pate = ((String) p.get("t02ap_pate")).trim();
		if (pt02ap_pate != null && !pt02ap_pate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_pate like '"
					+ common.replace(pt02ap_pate, "'", "''") + "%'";
		}

		//-- Check apmat

		pt02ap_mate = ((String) p.get("t02ap_mate")).trim();
		if (pt02ap_mate != null && !pt02ap_mate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_mate like '"
					+ common.replace(pt02ap_mate, "'", "''") + "%'";
		}

		//-- Check nomb

		pt02nombres = ((String) p.get("t02nombres")).trim();
		if (pt02nombres != null && !pt02nombres.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02nombres like '%"
					+ common.replace(pt02nombres, "'", "''") + "%'";
		}

		//-- Check uorg

		pt02cod_uorg = ((String) p.get("t02cod_uorg")).trim();
		if (pt02cod_uorg != null && !pt02cod_uorg.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_uorg = '"
					+ common.replace(pt02cod_uorg, "'", "''") + "'";
		}
		//-- Check cate

		pt02cod_cate = ((String) p.get("t02cod_cate")).trim();
		if (pt02cod_cate != null && !pt02cod_cate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_cate = '"
					+ common.replace(pt02cod_cate, "'", "''") + "'";
		}

		//-- Check check

		pt02cod_stat = ((String) p.get("t02cod_stat")).trim();
		if (pt02cod_stat != null && !pt02cod_stat.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_stat = '"
					+ common.replace(pt02cod_stat, "'", "''") + "'";
		}

		sWhere += " and (((t99tip_desc)='D') AND ((t99cod_tab)='001')) ";

		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}

		sOrder = " order by t02ap_pate, t02ap_mate";

		// Build full SQL statement

		sSQL = "SELECT t02cod_pers, t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, t12des_uorga, t12des_corta, t02cod_cate, t02cod_catel, t99descrip, t99abrev, t02cod_stat "
				+ " FROM (t02perdp LEFT JOIN t12uorga ON t02cod_uorg = t12cod_uorga) LEFT JOIN t99codigos ON t02cod_cate = t99codigo ";

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
	 * M�todo joinWithT02T12T99
	 * 
	 * @param HashMap
	 *            p
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList busquedaAyudas(Map p)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String pt02cod_pers = "";
		String pt02ap_pate = "";
		String pt02ap_mate = "";
		String pt02nombres = "";
		String pt02cod_uorg = "";
		String pt02cod_cate = "";
		String pt02cod_stat = "";

		// Build WHERE statement

		//-- Check reg

		pt02cod_pers = ((String) p.get("t02cod_pers")).trim();
		if (pt02cod_pers != null && !pt02cod_pers.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_pers = '"
					+ common.replace(pt02cod_pers, "'", "''") + "'";
		}

		//-- Check appat

		pt02ap_pate = ((String) p.get("t02ap_pate")).trim();
		if (pt02ap_pate != null && !pt02ap_pate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_pate like '"
					+ common.replace(pt02ap_pate, "'", "''") + "%'";
		}

		//-- Check apmat

		pt02ap_mate = ((String) p.get("t02ap_mate")).trim();
		if (pt02ap_mate != null && !pt02ap_mate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02ap_mate like '"
					+ common.replace(pt02ap_mate, "'", "''") + "%'";
		}

		//-- Check nomb

		pt02nombres = ((String) p.get("t02nombres")).trim();
		if (pt02nombres != null && !pt02nombres.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02nombres like '%"
					+ common.replace(pt02nombres, "'", "''") + "%'";
		}

		//-- Check uorg

		pt02cod_uorg = ((String) p.get("t02cod_uorg")).trim();
		if (pt02cod_uorg != null && !pt02cod_uorg.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_uorg = '"
					+ common.replace(pt02cod_uorg, "'", "''") + "'";
		}
		//-- Check cate

		pt02cod_cate = ((String) p.get("t02cod_cate")).trim();
		if (pt02cod_cate != null && !pt02cod_cate.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_cate = '"
					+ common.replace(pt02cod_cate, "'", "''") + "'";
		}

		//-- Check check

		pt02cod_stat = ((String) p.get("t02cod_stat")).trim();
		if (pt02cod_stat != null && !pt02cod_stat.equals("")) {

			if (!sWhere.equals(""))
				sWhere += " and ";
			hasParam = true;
			sWhere += "t02cod_stat = '"
					+ common.replace(pt02cod_stat, "'", "''") + "'";
		}

		sWhere += " and (((t99tip_desc)='D') AND ((t99cod_tab)='001')) ";

		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}

		sOrder = " order by t02ap_pate, t02ap_mate";

		// Build full SQL statement

		sSQL = "SELECT t02cod_pers, t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, t12des_uorga, t12des_corta, t02cod_cate, t02cod_catel, t99descrip, t99abrev, t02cod_stat "
				+ " FROM (t02perdp LEFT JOIN t12uorga ON t02cod_uorg = t12cod_uorga) LEFT JOIN t99codigos ON t02cod_cate = t99codigo ";

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
	 * 
	 * @param dbpool
	 * @param codPers
	 * @param strwhere
	 *            Registros de usuarios
	 * @return Retorna la lista de apellidos y nombres de los registros de los
	 *         usuarios
	 * @throws SQLException
	 */

	public ArrayList joinWithfindEmpleado(String dbpool, Vector codPers,
			String strwhere) throws SQLException {
		ArrayList arrUsuarios = new ArrayList();
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		
		try {
			StringBuffer strSelect = new StringBuffer("Select ");
			strSelect
					.append(" p.t02cod_pers,  p.t02ap_pate,  p.t02ap_mate , p.t02nombres, p.t02cod_uorg, p.t02cod_cate, ");
			strSelect
					.append(" p.t02cod_regl,p.t02f_ingsun,  p.t02f_nacim,  uo.t12des_uorga, uo.t12cod_jefat, uo.t12cod_encar, param.t99descrip ");
			strSelect.append(" FROM ");
			strSelect.append(" t02perdp p,  t12uorga uo, t99codigos param ");
			strSelect.append(" WHERE  ");
			strSelect.append(strwhere);
			strSelect.append(" AND  p.t02cod_uorg = uo.t12cod_uorga ");
			strSelect.append(" AND  p.t02cod_cate = param.t99codigo ")
					 .append(" AND  param.t99cod_tab = '001' ")
					 .append(" AND  param.t99tip_desc = 'D' ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSelect.toString());

			for (int i = 0; i < codPers.size(); i++) {
				String codreg = (String) codPers.get(i);
				pre.setString(i + 1, codreg.toUpperCase());
				//ultimo = i;
			}

			rs = pre.executeQuery();

			while (rs.next()) {
				HashMap hm = new HashMap();

				hm.put("t02cod_pers", rs.getString("t02cod_pers"));
				hm.put("t02ap_pate", rs.getString("t02ap_pate").trim());
				hm.put("t02ap_mate", rs.getString("t02ap_mate").trim());
				hm.put("t02nombres", rs.getString("t02nombres").trim());
				hm.put("t02cod_uorg", rs.getString("t02cod_uorg"));
				hm.put("t12des_uorga", rs.getString("t12des_uorga").trim());
				hm.put("t02cod_cate", rs.getString("t02cod_cate"));
				hm.put("t02des_cate", rs.getString("t99descrip").trim());
				hm.put("t02cod_regl", rs.getString("t02cod_regl"));
				hm.put("t02f_ingsun", rs.getTimestamp("t02f_ingsun"));
				hm.put("t02f_nacim", rs.getDate("t02f_nacim"));
				arrUsuarios.add(hm);
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

		return arrUsuarios;
	}

	  /**
	   * M�todo encargado de buscar los datos de un trabajador siempre y cuando
	   * su fecha de ingreso sea menor a la indicada por el par�metro respectivo.
	   * Join con las tablas T12UOrga y T99Codigos.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param codPers         String. N�mero de registro del trabajador.
	   * @param fIng            String. Fecha de ingreso m�xima permitida para la b�squeda.
	   * @return                HashMap conteniendo el registro obtenido.
	   * @throws SQLException
	   */
	  public HashMap joinWithT12T99ByCodPersFIng(String dbpool, String codPers, String fIng) throws
	      SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    HashMap a = new HashMap();

	    try {

	      strSQL =
	          "select  p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres," +
	          "        p.t02cod_uorg, p.t02cod_cate, p.t02cod_stat," +
	          "        p.t02f_ingsun, p.t02f_nacim, p.t02lib_elec," +
	          "        uo.t12des_uorga, param.t99descrip " +
	          "from    t02perdp p, " +
	          "        t12uorga uo, " +
	          "        t99codigos param " +
	          "where   p.t02cod_pers = ? and  " +
	          "        p.t02cod_stat = ? and " +
	          "        p.t02f_ingsun <= ? and " +
	          "        p.t02cod_uorg = uo.t12cod_uorga and " +
	          "	       p.t02cod_cate = param.t99codigo and " +
	          "        param.t99cod_tab = '001' and " +
	          "        param.t99tip_desc = 'D' ";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, codPers.trim().toUpperCase());
	      pre.setString(2, Constantes.ACTIVO);
	      pre.setTimestamp(3, Utiles.stringToTimestamp(fIng + " 00:00:00"));
	      rs = pre.executeQuery();

	      if (rs.next()) {

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02ap_pate", rs.getString("t02ap_pate").trim());
	        a.put("t02ap_mate", rs.getString("t02ap_mate").trim());
	        a.put("t02nombres", rs.getString("t02nombres").trim());
	        a.put("t02cod_uorg", rs.getString("t02cod_uorg"));
	        a.put("t12des_uorga", rs.getString("t12des_uorga").trim());
	        a.put("t02cod_cate", rs.getString("t02cod_cate"));
	        a.put("t02desc_cate", rs.getString("t99descrip").trim());
	        a.put("t02cod_stat", rs.getString("t02cod_stat"));
	        a.put("t02f_ingsun", Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun")));
	        a.put("t02f_nacim", Utiles.timeToFecha(rs.getTimestamp("t02f_nacim")));
	        a.put("t02lib_elec", rs.getString("t02lib_elec"));

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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return a;
	  }

	  /**
	   * Metodo que se encarga de la busqueda de los registros de los
	   * trabajadores que pertenecen a una determinada unidad organizacional.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param uo              String. Codigo de la unidad organizacional.
	   * @return                ArrayList conteniendo los registros cargados
	   *                        en HashMaps.
	   * @throws SQLException
	   */
	  public ArrayList findByUO(String dbpool, String uo, HashMap seguridad) throws SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    ArrayList personal = null;

	    try {

	      strSQL = "select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga from t02perdp " +
	          " where substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '"+uo.trim().toUpperCase()+"%' and " +
	          " t02cod_stat = ? ";

	      //criterios de visibilidad
	      if (seguridad != null) {

	        HashMap roles = (HashMap) seguridad.get("roles");
	        String codPers = (String) seguridad.get("codPers");
	        String uoSeg = (String) seguridad.get("uoSeg");
	        String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

	      //DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
	          strSQL += " and ((substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '" +uoSeg.toUpperCase()+ "') ";
	          strSQL += " or (substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" +uoAO.toUpperCase()+ "'))) ";
	        }

	        
	        if (roles.get(Constantes.ROL_ANALISTA_LEXC) != null ) {
		          strSQL += " and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '" +uoSeg.toUpperCase()+ "' ";
		        }
	      //DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (
	            roles.get(Constantes.ROL_JEFE) != null
	            ) {
	          strSQL += " and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '" +uoSeg.toUpperCase()+ "' ";
	          strSQL += " and t02cod_pers != '" +codPers.toUpperCase()+ "' ";
	        }
	      }
	      strSQL += " order by t02cod_pers";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, Constantes.ACTIVO);
	      rs = pre.executeQuery();
	      personal = new ArrayList();

	      while (rs.next()) {

	        HashMap a = new HashMap();

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02cod_uorg", rs.getString("cod_uorga"));

	        personal.add(a);
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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return personal;
	  }

	  /**
	   * Metodo que se encarga de la busqueda de los registros de los
	   * trabajadores que pertenecen a una determinada unidad organizacional y
	   * cuya fecha de ingreso sea menor a la indicada por el parametro
	   * respectivo.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param uo              String. Codigo de la unidad organizacional.
	   * @param fIng            String. Fecha de ingreso maxima.
	   * @return                ArrayList conteniendo los registros cargados
	   *                        en HashMaps.
	   * @throws SQLException
	   */
	  public ArrayList findByUOFIng(String dbpool, String uo, String fIng) throws SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    ArrayList personal = null;

	    try {

	      strSQL = "select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga, t02f_ingsun, t02cod_stat" +
	          "from   t02perdp " +
	          "where  t02cod_uorg = ? and " +
	          "       t02f_ingsun <= ? and " +
	          "       t02cod_stat = ? order by t02cod_pers";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, uo.trim().toUpperCase());
	      pre.setTimestamp(2, Utiles.stringToTimestamp(fIng + " 00:00:00"));
	      pre.setString(3, Constantes.ACTIVO);
	      rs = pre.executeQuery();
	      personal = new ArrayList();

	      while (rs.next()) {

	        HashMap a = new HashMap();

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02cod_uorg", rs.getString("cod_uorga"));

	        personal.add(a);

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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return personal;
	  }

	  /**
	   * Metodo encargado de buscar los registros de los trabajadores
	   * por su categoria.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param categoria       String. Codigo de categoria.
	   * @return                ArrayList conteniendo los registros cargados
	   *                        en HashMaps.
	   * @throws SQLException
	   */
	  public ArrayList findByCategoria(String dbpool, String categoria, HashMap seguridad) throws SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    ArrayList personal = null;

	    try {

	      strSQL = " select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga, substr(trim(nvl(t02cod_catel,'')||nvl(t02cod_cate,'')),1,2) cod_cate, t02cod_stat" +
	          " from   t02perdp " +
	          " where  substr(trim(nvl(t02cod_catel,'')||nvl(t02cod_cate,'')),1,2) = ? and " +
	          "        t02cod_stat = ? ";

	      //criterios de visibilidad
	      if (seguridad != null) {

	        HashMap roles = (HashMap) seguridad.get("roles");
	        String uoSeg = (String) seguridad.get("uoSeg");
	        String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

	        if ( roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
	          strSQL += " and ((substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '" +uoSeg+ "') ";
	          strSQL += " or (substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '" +uoAO+ "'))) ";
	        }
	        
	        if (roles.get(Constantes.ROL_ANALISTA_LEXC) != null) {
		          strSQL += " and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '" +uoSeg+ "' ";
		        }
	      }

	      strSQL += " order by t02cod_pers";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, categoria.trim().toUpperCase());
	      pre.setString(2, Constantes.ACTIVO);
	      rs = pre.executeQuery();
	      personal = new ArrayList();

	      while (rs.next()) {

	        HashMap a = new HashMap();

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02cod_uorg", rs.getString("cod_uorga"));

	        personal.add(a);

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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return personal;
	  }

	  /**
	   * Metodo que se encarga de buscar los datos de los trabajadores
	   * activos
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param estado          String. Corresponde al estado por el cual se
	   *                        desea filtrar los registros.
	   * @return                ArrayList conteniendo los registros cargados
	   *                        en HashMaps.
	   * @throws SQLException
	   */
	  public ArrayList findAll(String dbpool, String estado) throws SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    ArrayList personal = null;

	    try {

	      strSQL = "select t02cod_pers, t02cod_uorg, t02cod_stat" +
	          "from   t02perdp " +
	          "where t02cod_stat = ? order by t02cod_pers";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, Constantes.ACTIVO);
	      rs = pre.executeQuery();
	      personal = new ArrayList();

	      while (rs.next()) {

	        HashMap a = new HashMap();

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02cod_uorg", rs.getString("t02cod_uorg"));

	        personal.add(a);

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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return personal;
	  }

	  /**
	   * Metodo encargado de buscar los registros de los trabajadores cuyo
	   * estado sea igual al indicado por el parametro y cuya fecha de ingreso
	   * sea menor a la fecha indicada.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param estado          String. Corresponde al estado por el cual se
	   *                        desea filtrar los registros.
	   * @param fIng            String. Fecha de ingreso maxima permitida
	   *                        para la busqueda.
	   * @return                ArrayList conteniendo los registros cargados
	   *                        en HashMaps.
	   * @throws SQLException
	   */
	  public ArrayList findAllByFIng(String dbpool, String estado, String fIng) throws
	      SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    ArrayList personal = null;

	    try {

	      strSQL =
	          "select t02cod_pers, t02cod_uorg, t02f_ingsun, t02cod_stat  from t02perdp " +
	          "where t02f_ingsun <= ? and t02cod_stat = ? order by t02cod_pers";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setTimestamp(1, Utiles.stringToTimestamp(fIng + " 00:00:00"));
	      pre.setString(2, Constantes.ACTIVO);
	      rs = pre.executeQuery();
	      personal = new ArrayList();

	      while (rs.next()) {

	        HashMap a = new HashMap();

	        a.put("t02cod_pers", rs.getString("t02cod_pers"));
	        a.put("t02cod_uorg", rs.getString("t02cod_uorg"));

	        personal.add(a);

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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return personal;
	  }

	  /**
	   * M�todo que se encarga de buscar los datos del jefe de un trabajador
	   * determinado.
	   *
	   * @param dbpool          String. Pool de conexiones.
	   * @param codPers         String. N�mero de registro del trabajador.
	   * @return                HashMap conteniendo el registro obtenido.
	   * @throws SQLException
	   */
	  public HashMap findByCodPersWithJefe(String dbpool, String codPers, HashMap seguridad) throws SQLException {

	    HashMap t = null;

	    try {
	      T12DAO uoDAO = new T12DAO();
	      t = joinWithT12T99ByCodPers(dbpool,codPers,seguridad);
	      if (t.get("t02cod_pers")!=null && !((String)t.get("t02cod_pers")).equals("")) {

	        if (((String) t.get("t12cod_jefe")).equals(codPers)) {
	          HashMap jefe = uoDAO.findJefeByTrabajador(dbpool,
	                                          ( (String) t.get("t02cod_uorg")).trim(),
	                                          ( (String) t.get("t02cod_pers")).trim());
	          t.put("t12cod_jefe",
	                jefe.get("t12cod_jefat") != null ?
	                (String) jefe.get("t12cod_jefat") :
	                (String) jefe.get("t12cod_encar"));
	          t.put("t12cod_jefe_desc", (String) jefe.get("t12cod_jefat_desc"));
	          t.put("t12cod_cate_desc", (String) jefe.get("t12cod_cate_desc"));
	          t.put("t12cod_uorg_desc", (String) jefe.get("t12cod_uorg_desc"));
	          t.put("t12cod_uorg_jefe", (String) jefe.get("t12cod_uorg_jefe"));
	        }
	        else {
	          HashMap jefe = joinWithT12T99ByCodPers( dbpool, (String) t.get("t12cod_jefe"),seguridad);
	          t.put("t12cod_jefe", (String) jefe.get("t02cod_pers"));
	          t.put("t12cod_jefe_desc",
	                (String) jefe.get("t02cod_pers") + " - " +
	                (String) jefe.get("t02ap_pate") + " " +
	                (String) jefe.get("t02ap_mate") + ", " +
	                (String) jefe.get("t02nombres")
	                );
	          t.put("t12cod_cate_desc", (String) jefe.get("t02desc_cate"));
	          t.put("t12cod_uorg_desc", (String) jefe.get("t12des_uorga"));
	          t.put("t12cod_uorg_jefe", (String) jefe.get("t02cod_uorg"));
	        }
	      }

	    }
	    catch (Exception e) {
	    	//e.printStackTrace();
	    	throw new SQLException(e.toString());
	    }
	    return t;
	  }

	  /**
	   * Metodo encargado de obtener el correo del trabajador
	   * @param dbpool
	   * @param codPers
	   * @return
	   * @throws SQLException
	   */
	  public String findNombreCompletoByCodPers(String dbpool, String codPers) throws
	      SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    String nombre = "";

	    try {

	      strSQL = "select t02ap_pate, t02ap_mate, t02nombres "+
	          "from t02perdp where t02cod_pers = ? ";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, codPers.toUpperCase());
	      rs = pre.executeQuery();

	      if (rs.next()) {
	        nombre =  rs.getString("t02ap_pate").trim()+" "+rs.getString("t02ap_mate").trim()+", "+rs.getString("t02nombres").trim();
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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return nombre;
	  }	

	  /**
	   * Metodo encargado de obtener el correo del trabajador
	   * @param dbpool
	   * @param codPers
	   * @return
	   * @throws SQLException
	   */
	  public String findNombreCompletoByCodPersDS(DataSource dbpool, String codPers) throws
	      SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    String nombre = "";

	    try {

	      strSQL = "select t02ap_pate, t02ap_mate, t02nombres "+
	          "from t02perdp where t02cod_pers = ? ";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, codPers.toUpperCase());
	      rs = pre.executeQuery();

	      if (rs.next()) {
	        nombre =  rs.getString("t02ap_pate").trim()+" "+rs.getString("t02ap_mate").trim()+", "+rs.getString("t02nombres").trim();
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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return nombre;
	  }	  
	  
	  /**
	   * Metodo encargado de verificar si un trabajador pertenece a una UUOO
	   * @param dbpool
	   * @param codPers
	   * @return
	   * @throws SQLException
	   */
	  public boolean findCodPersInUUOO(String dbpool, String codPers, String uuoo) throws
	      SQLException {

	    String strSQL = "";
	    PreparedStatement pre = null;
	    Connection con = null;
	    ResultSet rs = null;
	    boolean existe = false;

	    try {

	      strSQL =  " select  t02cod_pers from t02perdp "+
		  			" where   t02cod_pers = ? and "+
					" substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = ? ";

	      con = getConnection(dbpool);
	      pre = con.prepareStatement(strSQL);
	      pre.setString(1, codPers.toUpperCase());
	      pre.setString(2, uuoo.toUpperCase());
	      rs = pre.executeQuery();

	      if (rs.next()) existe = true;

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
	    }
	    finally {
	      try {
	        rs.close();
	      }
	      catch (Exception e) {}
	      try {
	        pre.close();
	      }
	      catch (Exception e) {}
	      try {
	        con.close();
	      }
	      catch (Exception e) {}
	    }
	    return existe;
	  }	  

	  /**
		 * Metodo encargado de buscar los datos los trabajadores que pertenezcan a
		 * una unidad organizacional, filtrandolos por apellidos y/o nombres
		 * 
		 * 
		 * @param dbpool
		 *            String. Pool de conexiones.
		 * @return HashMap conteniendo el registro obtenido.
		 * @throws SQLException
		 */
		public ArrayList findByNombreCodUO(HashMap params)
				throws SQLException {

			String strSQL = "";
			PreparedStatement pre = null;
			Connection con = null;
			ResultSet rs = null;
			ArrayList lista = null;
			try {
				String dbpool = (String) params.get("dbpool");
				String nombre = (String) params.get("nombre");
				String apell = (String) params.get("apell");
				String codUO = (String) params.get("codUO");

				strSQL = new StringBuffer("select p.t02cod_pers, p.t02ap_pate, ")
						.append("       p.t02ap_mate, p.t02nombres, ")
						.append("       p.t02cod_uorg, p.t02cod_cate, ")
						.append("       uo.t12des_uorga, param.t99descrip ")
						.append("from   t02perdp p, ")
						.append("       t12uorga uo, ")
						.append("       t99codigos param ")
						.append("where  ( upper(trim(p.t02ap_pate)) || ' ' || upper(trim(p.t02ap_mate))) like '")
						.append(apell.trim().toString()) .append("%' and ")
						.append("       upper(trim(p.t02nombres)) like '")
						.append(nombre.trim().toUpperCase() + "%' and ")
						.append("       p.t02cod_uorg like '")
						.append(codUO.trim().toUpperCase() + "%' and ")
						.append("       p.t02cod_uorg = uo.t12cod_uorga and ")
						.append("       param.t99cod_tab = '001' and ")
						.append("       param.t99tip_desc = 'D' and ")
						.append("       p.t02cod_cate = param.t99codigo ")
						.append("order by p.t02nombres, p.t02ap_pate, p.t02ap_mate")
						.toString();

				con = getConnection(dbpool);
				pre = con.prepareStatement(strSQL);
				rs = pre.executeQuery();

				lista = new ArrayList();

				while (rs.next()) {
					HashMap res = new HashMap();

					res.put("t02cod_pers", rs.getString("t02cod_pers"));
					res.put("t02ap_pate", rs.getString("t02ap_pate").trim());
					res.put("t02ap_mate", rs.getString("t02ap_mate").trim());
					res.put("t02nombres", rs.getString("t02nombres").trim());
					res.put("t02cod_uorg", rs.getString("t02cod_uorg"));
					res.put("t12des_uorga", rs.getString("t12des_uorga").trim());
					res.put("t02cod_cate", rs.getString("t02cod_cate"));
					res.put("t02desc_cate", rs.getString("t99descrip").trim());

					lista.add(res);
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
				//e.printStackTrace();
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
			return lista;
		}

		/**
		 * Metodo encargado de buscar los datos de un determinado trabajador.
		 * 
		 * @param dbpool
		 *            String. Pool de conexiones.
		 * @param codPers
		 *            String. Registro del trabajador.
		 * @return HashMap conteniendo el registro obtenido.
		 * @throws SQLException
		 */
		public HashMap findEmpleadoLicEnf(String dbpool, String codPers)
				throws SQLException {

			String strSQL = "";
			PreparedStatement pre = null;
			Connection con = null;
			ResultSet rs = null;
			HashMap a = new HashMap();

			try {

				strSQL = "select  p.t02cod_pers, " + "        p.t02ap_pate, "
						+ "        p.t02ap_mate, " + "        p.t02nombres, "
						+ "        substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorga  , " + "        p.t02cod_cate, "
						+ "        p.t02cod_regl, " + "        p.t02f_ingsun, "
						+ "        p.t02f_cese, " + "        p.t02f_nacim, "
						+ "        p.t02direccion, " + "        p.t02urban, "
						+ "        p.t02refer, " + "        p.t02lib_elec, p.t02cod_stat, "
						+ "        param.t99descrip "
						+ "from    t02perdp p, "
						+ "        t99codigos param "
						+ "where   p.t02cod_pers = ? and  "
						+ "        param.t99cod_tab = '001' and "
						+ "        param.t99tip_desc = 'D' and "
						+ "        p.t02cod_cate = param.t99codigo ";

				con = getConnection(dbpool);
				pre = con.prepareStatement(strSQL);
				pre.setString(1, codPers.trim().toUpperCase());
				//pre.setString(2, "1");
				rs = pre.executeQuery();
				T12DAO dao = new T12DAO();
				HashMap jefe = new HashMap();
				if (rs.next()) {

					a.put("t02cod_pers", rs.getString("t02cod_pers"));
					a.put("t02ap_pate", rs.getString("t02ap_pate").trim());
					a.put("t02ap_mate", rs.getString("t02ap_mate").trim());
					a.put("t02nombres", rs.getString("t02nombres").trim());
					a.put("t02cod_uorg", rs.getString("cod_uorga"));
					jefe = dao.findJefeByJefatura(dbpool, rs.getString("cod_uorga").trim());
					a.put("t12des_uorga", (String)jefe.get("t12cod_uorg_desc"));
					a.put("t02cod_cate", rs.getString("t02cod_cate"));
					a.put("t02direccion", rs.getString("t02direccion"));
					a.put("t02urban", rs.getString("t02urban"));
					a.put("t02refer", rs.getString("t02refer"));
					a.put("t02des_cate", rs.getString("t99descrip").trim());
					a.put("t12cod_jefe", (String)jefe.get("t12cod_jefat") != null ? (String)jefe.get("t12cod_jefat") : (String)jefe.get("t12cod_encar"));
					a.put("t02cod_regl", rs.getString("t02cod_regl"));
					a.put("t02f_ingsun", rs.getTimestamp("t02f_ingsun"));
					a.put("t02f_cese", rs.getTimestamp("t02f_cese"));
					a.put("t02f_nacim", rs.getDate("t02f_nacim"));
					a.put("t02lib_elec", rs.getString("t02lib_elec"));
					a.put("t02cod_stat", rs.getString("t02cod_stat"));

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
			return a;
		}

		
		//ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro
		/**
		   * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero		 
		   * @param String unidad
		   * @return String uoJefe
		   * @throws SQLException
		   */
		  public String findUuooJefe(String unidad) throws SQLException {
	    
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
		    	log.error("**** SQL ERROR **** "+ e.toString());
				throw new SQLException(e.toString());
		    }
		    finally {
		    }
		    log.debug("findUuooJefe-uoJefe(final): "+uoJefe);
			return uoJefe; 
		  }	  
		  //ICAPUNAY-PAS20144EB20000075-ajustes visibilidad solicitudes y autorizaciones/nueva, reporte vacaciones programadas y programacion de vacaciones/registro

	  
}