package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.dao.T02DAO;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.Utilidades;

/**
 * 
 * Clase : T1480DAO Fecha : 28-dic-2004 11:40:02 Proyecto : Asistencia
 * Descripcion :
 * 
 * @author CGARRATT
 *  
 */
public class T1480DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1480DAO.class);

	public T1480DAO() {
	}

	/**
	 * Busca la lista de aprobadores por solicitud
	 * 
	 * @param datos
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAprobadores(HashMap datos) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			T02DAO daoP = new T02DAO();

			strSQL.append("select   s.mov, s.accion_id, s.u_organ, s.cod_personal_ori, s.cod_personal_des, s.cinstancia_aprob,  "
					).append( " 	   s.cuser, s.fgraba, c.t99descrip, u.t12des_corta "
					).append( "from    t1480sol_flujo s, "
					).append( "        t1279tipo_mov m, "
					).append( "        t12uorga u, "
					).append( "        t99codigos c " ).append( "where   ");

			if (datos.get("codUO") != null && !datos.get("codUO").equals("")) {
				strSQL.append(" s.u_organ = '" ).append( (String) datos.get("codUO")
						).append( "' and ");
			}
			if (datos.get("cod_pers_ori") != null
					&& !datos.get("cod_pers_ori").equals("")) {
				strSQL.append(" s.cod_personal_ori = '"
						).append( (String) datos.get("cod_pers_ori") ).append( "' and ");
			}
			if (datos.get("cod_pers_des") != null
					&& !datos.get("cod_pers_des").equals("")) {
				strSQL.append(" s.cod_personal_des = '"
						).append( (String) datos.get("cod_pers_des") ).append( "' and ");
			}
			if (datos.get("instancia") != null
					&& !datos.get("instancia").equals("")) {
				strSQL.append(" s.cinstancia_aprob = '"
						).append( (String) datos.get("instancia") ).append( "' and ");
			}

			strSQL.append(" s.mov = ? and "
					).append( " c.t99cod_tab = ? and "
					).append( " c.t99tip_desc = ? and "
					).append( " c.t99codigo = s.cinstancia_aprob and "
					).append( " s.mov = m.mov and "
					).append( " s.u_organ = u.t12cod_uorga "
					).append( "order by s.u_organ, s.cinstancia_aprob, s.cod_personal_ori ");

			con = getConnection((String) datos.get("dbpool"));
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, (String) datos.get("mov"));
			pre.setString(2, Constantes.CODTAB_INSTANCIA);
			pre.setString(3, Constantes.T99DETALLE);

			log.debug("FindAPROBADORES : "+ strSQL.toString());
			
			rs = pre.executeQuery();
			lista = new ArrayList();
			HashMap r = null;

			while (rs.next()) {

				r = new HashMap();

				r.put("cod_uorg", rs.getString("u_organ"));
				r.put("accion_id", rs.getString("accion_id"));
				r.put("mov", rs.getString("mov"));
				r.put("desc_uorg", rs.getString("t12des_corta"));
				r.put("instancia", rs.getString("cinstancia_aprob"));
				r.put("cuser", rs.getString("cuser"));
				r.put("fgraba", new BeanFechaHora(rs.getDate("fgraba")).getFormatDate("dd/MM/yy HH:mm:ss"));
				r.put("desc_instancia", rs.getString("t99descrip"));

				String cod_pers_ori = rs.getString("cod_personal_ori");
				String cod_pers_des = rs.getString("cod_personal_des");

				r.put("cod_pers_ori", cod_pers_ori);
				r.put("nom_aprob_ori", daoP.findNombreCompletoByCodPers(
						(String) datos.get("dbpool"), cod_pers_ori));

				if (cod_pers_des != null && !cod_pers_des.trim().equals("")) {

					r.put("cod_pers_des", cod_pers_des);
					r.put("nom_aprob_des", daoP.findNombreCompletoByCodPers(
							(String) datos.get("dbpool"), cod_pers_des));
				}
				lista.add(r);
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
	
	
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	/**
	 * Metodo encargado de listar las instancias de aprobación por tipo de solicitud y unidad
	 * 
	 * @param datos
	 * @return instancias ArrayList
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findInstanciasBySolByUO(HashMap datos)
			throws IncompleteConversationalState {

		PreparedStatement pstmt = null;
		String strSQL = "";
		Connection conn = null;		
		ArrayList instancias = new ArrayList();
		Utilidades common = new Utilidades();
		try {

			strSQL = "select * from t1480sol_flujo where u_organ=? and mov=? "
					+ "order by cinstancia_aprob asc ";

			conn = getConnection((String) datos.get("dbpool"));
			conn.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			
			pstmt = conn.prepareStatement(strSQL);
			pstmt.setString(1, (String) datos.get("u_organ")); 
			pstmt.setString(2, (String) datos.get("mov")); 

			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);
			
             
			while (rs.next()) {
				HashMap rsMap = new HashMap();
				common.getRecordToMap(rs, rsMap, aFields);	        				
				instancias.add(rsMap);
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
		return instancias;
	}
	
	
	/** 05/05/2016
	 * Busca la lista de aprobadores por dos instancias y otros parametros enviados
	 * 
	 * @param datos
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAprobadoresByInstancias(HashMap datos) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			T02DAO daoP = new T02DAO();

			strSQL.append("select   s.mov, s.accion_id, s.u_organ, s.cod_personal_ori, s.cod_personal_des, s.cinstancia_aprob,  "
					).append( " 	   s.cuser, s.fgraba, c.t99descrip, u.t12des_corta "
					).append( "from    t1480sol_flujo s, "
					).append( "        t1279tipo_mov m, "
					).append( "        t12uorga u, "
					).append( "        t99codigos c " ).append( "where   ");

			if (datos.get("codUO") != null && !datos.get("codUO").equals("")) {
				strSQL.append(" s.u_organ = '" ).append( (String) datos.get("codUO")
						).append( "' and ");
			}
			if (datos.get("cod_pers_ori") != null
					&& !datos.get("cod_pers_ori").equals("")) {
				strSQL.append(" s.cod_personal_ori = '"
						).append( (String) datos.get("cod_pers_ori") ).append( "' and ");
			}
			if (datos.get("cod_pers_des") != null
					&& !datos.get("cod_pers_des").equals("")) {
				strSQL.append(" s.cod_personal_des = '"
						).append( (String) datos.get("cod_pers_des") ).append( "' and ");
			}
			if ((datos.get("instancia1") != null && !datos.get("instancia1").equals("")) && (datos.get("instancia2") != null && !datos.get("instancia2").equals(""))) {
				strSQL.append(" (s.cinstancia_aprob = '").append( (String) datos.get("instancia1") ).append( "' or s.cinstancia_aprob = '").append( (String) datos.get("instancia2") ).append( "') and ");
			}

			strSQL.append(" s.mov = ? and "
					).append( " c.t99cod_tab = ? and "
					).append( " c.t99tip_desc = ? and "
					).append( " c.t99codigo = s.cinstancia_aprob and "
					).append( " s.mov = m.mov and "
					).append( " s.u_organ = u.t12cod_uorga "
					).append( "order by s.u_organ, s.cinstancia_aprob, s.cod_personal_ori ");

			con = getConnection((String) datos.get("dbpool"));
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, (String) datos.get("mov"));
			pre.setString(2, Constantes.CODTAB_INSTANCIA);
			pre.setString(3, Constantes.T99DETALLE);

			log.debug("findAprobadoresByInstancias : "+ strSQL.toString());
			
			rs = pre.executeQuery();
			lista = new ArrayList();
			HashMap r = null;

			while (rs.next()) {

				r = new HashMap();

				r.put("cod_uorg", rs.getString("u_organ"));
				r.put("accion_id", rs.getString("accion_id"));
				r.put("mov", rs.getString("mov"));
				r.put("desc_uorg", rs.getString("t12des_corta"));
				r.put("instancia", rs.getString("cinstancia_aprob"));
				r.put("cuser", rs.getString("cuser"));
				r.put("fgraba", new BeanFechaHora(rs.getDate("fgraba")).getFormatDate("dd/MM/yy HH:mm:ss"));
				r.put("desc_instancia", rs.getString("t99descrip"));

				String cod_pers_ori = rs.getString("cod_personal_ori");
				String cod_pers_des = rs.getString("cod_personal_des");

				r.put("cod_pers_ori", cod_pers_ori);
				r.put("nom_aprob_ori", daoP.findNombreCompletoByCodPers(
						(String) datos.get("dbpool"), cod_pers_ori));

				if (cod_pers_des != null && !cod_pers_des.trim().equals("")) {

					r.put("cod_pers_des", cod_pers_des);
					r.put("nom_aprob_des", daoP.findNombreCompletoByCodPers(
							(String) datos.get("dbpool"), cod_pers_des));
				}
				lista.add(r);
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
	//ICAPUNAY - PAS20165E230300005 - delegacion sol./Reg Aut/Reg Comp
	
	/**
	 * Metodo que actualiza el aprobador del flujo
	 * @throws SQLException
	 */
	public boolean updateAprobadorFlujoOri(String dbpool, String numMov, String usuario, String nuevoAprob, 
			String viejoAprob, String uo)throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("Update t1480sol_flujo Set cod_personal_ori = ?, fgraba = ?, cuser = ?" +
					" Where mov = ? and cod_personal_ori = ? ");

			if (uo.trim().length() > 0) {
				strUpd.append(" and u_organ = '").append(uo.toUpperCase()).append("' ");
			}
			log.debug(" strUpd " + strUpd.toString());
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			
			pre.setString(1, nuevoAprob);
			pre.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(3, usuario);
			pre.setString(4, numMov);
			pre.setString(5, viejoAprob);
			
			
			int res = pre.executeUpdate();

			result = true;
 
            
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}

		return result;
	}

	
	/**
	 * Metodo que actualiza el aprobador del flujo
	 * @throws SQLException
	 */
	public boolean updateAprobadorFlujoDes(String dbpool, String numMov, String usuario, String nuevoAprob, 
			String viejoAprob, String uo)throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("Update t1480sol_flujo Set cod_personal_des = ?, fgraba = ?, cuser = ? "+ 
            " Where mov = ? and cod_personal_des = ? ");

			log.debug("uo "+uo);
			if (uo.trim().length() > 0) {
				strUpd.append(" and u_organ = '").append(uo.toUpperCase()).append("' ");
			}
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			
			pre.setString(1, nuevoAprob);
			pre.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(3, usuario);
			pre.setString(4, numMov);
			pre.setString(5, viejoAprob);
			
			
			int res = pre.executeUpdate();

			result = true;
 
            
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}

		return result;
	}
	
	/**
	 * Metodo que inserta un nuevo flujo de aprobadores (tupla)
	 * @throws SQLException
	 * @author jmaravi
	 * @since  14/04/2014
	 */
	public boolean insertAprobadorFlujo(String dbpool, HashMap datos )throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("insert into t1480sol_flujo ");
			strUpd.append("( mov, accion_id, u_organ, cod_personal_ori, cod_personal_des, cinstancia_aprob, fgraba, cuser ) ");
			strUpd.append("values ( ?, ?, ?, ?, ?, ?, current, ? )" );

			//log.debug("uo "+uo);

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			
			pre.setString(1, (String)datos.get("sNumMov"));
			pre.setString(2, (String)datos.get("cAccion"));
			pre.setString(3, (String)datos.get("sUniOrgan"));
			pre.setString(4, (String)datos.get("sCodPersOri"));
			pre.setString(5, (String)datos.get("sCodPersDes"));
			pre.setString(6, (String)datos.get("cInstancia"));
			pre.setString(7, (String)datos.get("sUsuario"));
			
			int res = pre.executeUpdate();

			result = res>0; 
            
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}

		return result;
	}	

	
	/**
	 * Metodo que elimina un flujo de aprobadores (tupla), mediante su primary key.
	 * @throws SQLException
	 * @author jmaravi
	 * @since  14/04/2014
	 */
	public boolean deleteAprobadorFlujo(String dbpool, HashMap hmDatos)throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("delete from t1480sol_flujo ");
			strUpd.append("where mov = ? and accion_id = ? and u_organ = ? and cod_personal_ori = ? ");
			

			//log.debug("uo "+uo);
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			
			pre.setString(1, (String)hmDatos.get("sNumMov"));
			pre.setString(2, (String)hmDatos.get("cAccion"));
			pre.setString(3, (String)hmDatos.get("sUniOrgan"));
			pre.setString(4, (String)hmDatos.get("sCodPersOri"));			
			
			int res = pre.executeUpdate();
			log.info("Eliminaci�n del flujo de aprobador:"+hmDatos);

			result = res>0;             
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}

		return result;
	}	
	
	
	/**
	 * Metodo que elimina un flujo de aprobadores para la Unidad Organizacional y tipo
	 * de movimiento de los par�metros, mediante su primary key.
	 * @throws SQLException
	 * @author jmaravi
	 * @since  14/04/2014
	 */
	public boolean deleteAprobadorFlujo4UniOrgYMov(String dbpool, HashMap hmDatos)throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("delete from t1480sol_flujo ");
			strUpd.append("where mov = ? and accion_id = ? and u_organ = ? ");
			

			//log.debug("uo "+uo);
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			
			pre.setString(1, (String)hmDatos.get("sNumMov"));
			pre.setString(2, (String)hmDatos.get("cAccion"));
			pre.setString(3, (String)hmDatos.get("sUniOrgan"));
			
			int res = pre.executeUpdate();
			log.info("Eliminaci�n de los flujos de aprobadores para la unidad y tipo_mov:"+hmDatos);

			result = res>0;             
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				pre.close();
			} catch (Exception e) {

			}
			try {
				con.close();
			} catch (Exception e) {

			}
		}

		return result;
	}	
	
}