package pe.gob.sunat.sa.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanSede;
import pe.gob.sunat.utils.Utilidades;

/**
 * @author WDELGADO
 * 
 * DAO de la tabla t34local
 * 
 */
public class T34DAO extends DAOAccesoBD {
	
	private static final Logger log = Logger.getLogger(T34DAO.class);
	
	public T34DAO() {
	}

	/**
	 * M�todo findByCodLocal
	 * 
	 * @param String
	 *            dbpool
	 * @param String
	 *            valor
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByCodLocal(String dbpool, String valor)
			throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL = "";

		String pvalor = "";

		pvalor = valor;
		hasParam = true;
		sWhere += " t34cod_local = '" + common.replace(pvalor, "'", "''") + "'";

		// Build full SQL statement
		if (hasParam) {
			sWhere = " WHERE (" + sWhere + ")";
		}
		sOrder = " order by t34cod_local ";

		sSQL = "SELECT * FROM t34local ";
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
	 * Metodo findByCodLocalHash
	 * 
	 * @param String
	 *            dbpool
	 * @param String
	 *            valor
	 * @return HashMap hMap
	 * @throws IncompleteConversationalState
	 */
	public HashMap findByCodLocalHash(String dbpool, String valor)
			throws IncompleteConversationalState {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		HashMap hMap = new HashMap(3);

		selectStatement = "SELECT * FROM t34local WHERE t34cod_local = ? ";

		try {
			conn = getConnection(dbpool);
			conn.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pstmt = conn.prepareStatement(selectStatement);
			pstmt.setString(1, valor);
			pstmt.setMaxRows(1);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				hMap.put("t34cod_local", rs.getString("t34cod_local"));
				hMap.put("t34nom_local", rs.getString("t34nom_local"));
			} else {
				hMap.put("t34cod_local", "-");
				hMap.put("t34nom_local", "Sin descripci�n");
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

	public ArrayList findByEstId(String dbpool, String estId)
			throws SQLException {
		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList locales = null;
		try {
			strSQL = "select t34cod_local, t34nom_local from t34local   order by t34nom_local";
			
			log.debug("SQL Sedes : "+strSQL);
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			rs = pre.executeQuery();
			locales = new ArrayList();
			BeanSede local;
			for (; rs.next(); locales.add(local)) {
				local = new BeanSede();
				local.setCodLocal(rs.getString("t34cod_local"));
				local.setNomLocal(rs.getString("t34nom_local"));
			}

			if (rs != null)
				rs.close();
			if (pre != null)
				pre.close();
			if (con != null)
				con.close();
		} catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pre != null)
					pre.close();
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}
		return locales;
	}

}