//package pe.gob.sunat.sp.asistencia.dao;
package pe.gob.sunat.rrhh.asistencia.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
//import pe.gob.sunat.sp.asistencia.bean.BeanAsistencia;
import pe.gob.sunat.sp.asistencia.bean.BeanProceso;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1933DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1933_supervisor
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author ebenavid
 * @version 1.0
 */

public class T1933DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1933DAO.class);

	public T1933DAO() {
	}

	/**
	 * Meotodo que se encarga de buscar los registros de asistencia de un
	 * determinado trabajador, para una fecha y periodos especificos.
	 * 
	 * @throws SQLException
	 */
	public ArrayList findSupervisores(String dbpool, String codPers, String mov,
			 String uorg) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {

			strSQL.append("select cod_personal, fec_inicio, fec_fin, t02ap_pate, t02ap_mate, t02nombres "
					).append( " from  t1933_supervisor, t02perdp "
					).append( " where cod_uorgan = ? and cod_mov = ? "
					).append( " and fec_inicio <= ? and fec_fin >= ? "
					).append(" and ind_estado = ?  and cod_personal = t02cod_pers ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, uorg);
			pre.setString(2, mov);
			pre.setDate(3, new BeanFechaHora(Utiles.obtenerFechaActual()).getSQLDate());
			pre.setDate(4, new BeanFechaHora(Utiles.obtenerFechaActual()).getSQLDate());
			pre.setString(5, Constantes.ACTIVO);
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanProceso det = null;

			while (rs.next()) {

				det = new BeanProceso();

				det.setCodigo(rs.getString("cod_personal"));
				det.setFechaIni(new BeanFechaHora(rs.getDate("fec_inicio")).getFormatDate("dd/MM/yyyy"));
				
				det.setFechaFin(new BeanFechaHora(rs.getDate("fec_fin")).getFormatDate("dd/MM/yyyy"));
				String texto = rs.getString("t02ap_pate").trim() + " "
				+ rs.getString("t02ap_mate").trim() + ", "
				+ rs.getString("t02nombres").trim();

				det.setValor(texto);
				detalle.add(det);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {

			}
			try {
				if (pre != null)
					pre.close();
			} catch (Exception e) {

			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {

			}
		}
		return detalle;
	}

	/**
	 * Meotodo que se encarga de buscar los registros de asistencia de un
	 * determinado trabajador, para una fecha y periodos especificos.
	 * 
	 * @throws SQLException
	 */
	public boolean esSupervisor(String dbpool, String codPers, String mov,
			 String uorg) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean detalle = false;

		try {

			strSQL.append("select cod_personal, fec_inicio, fec_fin, t02ap_pate, t02ap_mate, t02nombres "
					).append( " from  t1933_supervisor, t02perdp "
					).append( " where cod_uorgan = ? and cod_mov = ? "
					).append( " and fec_inicio <= ? and fec_fin >= ? "
					).append(" and ind_estado = ?  and cod_personal = t02cod_pers "
					).append(" and cod_personal = '").append(codPers.trim()).append("' ");
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, uorg);
			pre.setString(2, mov);
			pre.setDate(3, new BeanFechaHora(Utiles.obtenerFechaActual()).getSQLDate());
			pre.setDate(4, new BeanFechaHora(Utiles.obtenerFechaActual()).getSQLDate());
			pre.setString(5, Constantes.ACTIVO);
			rs = pre.executeQuery();
			while (rs.next()) {
				return true;
			}
		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {

			}
			try {
				if (pre != null)
					pre.close();
			} catch (Exception e) {

			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {

			}
		}
		return detalle;
	}
	
	/**
	 * Busca la lista de Supervisores segun criterios
	 * 
	 * @param datos
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findBySupervCriterios(HashMap datos) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {


			strSQL.append("select   s.cod_uorgan, s.cod_mov, s.cod_personal, s.fec_inicio, s.fec_fin, s.ind_estado,  "
					).append( " 	   s.cod_user_mod, s.fec_mod, c.t99descrip, u.t12des_corta, p.t02ap_pate, p.t02ap_mate, p.t02nombres  "
					).append( "from    t1933_supervisor s, "
					).append( "        t02perdp p, "
					).append( "        t12uorga u, "
					).append( "        t99codigos c " ).append( "where   ");

			if (datos.get("codUO") != null && !datos.get("codUO").equals("")) {
				strSQL.append(" s.cod_uorgan = '" ).append( (String) datos.get("codUO")
						).append( "' and ");
			}
			if (datos.get("cod_mov") != null
					&& !datos.get("cod_mov").equals("")) {
				strSQL.append(" s.cod_mov = '"
						).append( (String) datos.get("cod_mov") ).append( "' and ");
			}

			if (datos.get("cod_pers_ori") != null
					&& !datos.get("cod_pers_ori").equals("")) {
				strSQL.append(" s.cod_personal = '"
						).append( (String) datos.get("cod_pers_ori") ).append( "' and ");
			}
			if (datos.get("ind_estado") != null
					&& !datos.get("ind_estado").equals("")) {
				strSQL.append(" s.ind_estado = '"
						).append( (String) datos.get("ind_estado") ).append( "' and ");
			}
		
			strSQL.append( " c.t99cod_tab = ? and "
					).append( " c.t99tip_desc = ? and "
					).append( " s.cod_personal = p.t02cod_pers and "
					).append( " c.t99codigo = s.cod_mov and "
					).append( " s.cod_uorgan = u.t12cod_uorga "
					).append( "order by s.cod_uorgan, s.ind_estado, s.cod_personal ");

			con = getConnection((String) datos.get("dbpool"));
			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(1, (String) datos.get("cod_mov"));
			pre.setString(1, Constantes.CODTAB_SUPERVISOR);
			pre.setString(2, Constantes.T99DETALLE);

			rs = pre.executeQuery();
			lista = new ArrayList();
			HashMap r = null;

			while (rs.next()) {

				r = new HashMap();

				r.put("cod_uorgan", rs.getString("cod_uorgan"));
				r.put("desc_uorg", rs.getString("t12des_corta"));
				r.put("cod_mov", rs.getString("cod_mov"));
				r.put("desc_mov", rs.getString("t99descrip"));
				//r.put("fec_inicio", rs.getDate("fec_inicio"));
				r.put("fec_inicio",new BeanFechaHora(rs.getDate("fec_inicio")).getFormatDate("dd/MM/yyyy"));
				r.put("fec_fin",new BeanFechaHora(rs.getDate("fec_fin")).getFormatDate("dd/MM/yyyy"));
				//r.put("fec_fin", rs.getDate("fec_fin"));
				r.put("ind_estado", rs.getString("ind_estado"));
				r.put("cuser", rs.getString("cod_user_mod"));
				r.put("fgraba", new BeanFechaHora(rs.getDate("fec_mod")).getFormatDate("dd/MM/yy HH:mm:ss"));

				r.put("cod_personal", rs.getString("cod_personal"));
				String texto = rs.getString("t02ap_pate").trim() + " "
				+ rs.getString("t02ap_mate").trim() + ", "
				+ rs.getString("t02nombres").trim();

				r.put("nom_supervisor", texto);

				if (rs.getString("ind_estado").equals(Constantes.ACTIVO)) {
					r.put("desc_estado", "ACTIVO");
				}else{
					r.put("desc_estado", "INACTIVO");
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
	
	/**
	 * Metodo que se encarga de eliminar el supervisor.
	 * 
	 * @throws SQLException
	 */
	public void borrarSupervisor(String dbpool, String codPers, String mov,
			 String uorg) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		
		try {

			strSQL.append("delete from  t1933_supervisor "
					).append( " where cod_uorgan = ? and cod_mov = ? "
					).append( " and cod_personal = ? ");
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, uorg);
			pre.setString(2, mov);
			pre.setString(3, codPers);
			pre.executeUpdate();

		}

		catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
			throw new SQLException(e.toString());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {

			}
			try {
				if (pre != null)
					pre.close();
			} catch (Exception e) {

			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {

			}
		}
		
	}
	
	/**
	 * Metodo que inserta registro de supervisor
	 * @throws SQLException
	 */
	public boolean insertRegistroSupervisor(String dbpool, 
			String codMov,String UO,String superv,String fechaInicio,
			String fechaFin, String usuario)
			throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {
			


			strUpd = "Insert into t1933_supervisor (" + 
			"cod_uorgan, cod_mov, cod_personal, fec_inicio,fec_fin, "+
			"ind_estado, cod_user_mod, fec_mod ) " + 
            "Values (?,?,?,?,?,?,?,?)";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			pre.setString(1, UO );
			pre.setString(2, codMov);
			pre.setString(3, superv);
			pre.setDate(4, new BeanFechaHora(fechaInicio).getSQLDate());
			pre.setDate(5, new BeanFechaHora(fechaFin).getSQLDate());
			pre.setString(6, "1");
			pre.setString(7, usuario);
			pre.setTimestamp(8, new java.sql.Timestamp(System.currentTimeMillis()));
			int res = pre.executeUpdate();

			result = (res > 0);
			
 

		}

		catch (Exception e) {
			//log.error("**** SQL ERROR **** Se ha encontrado una doble marcación para REGISTRO:" + codPers + ", FECHA:"+ fechaMarcacion + " "+ e.toString());
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
	 * Metodo que modifica el registro de supervisor
	 * @throws SQLException
	 */
	public boolean modificaRegistroSupervisor(String dbpool, HashMap superv,
			String fechaInicio,
			String fechaFin, String estado, String usuario)
			throws SQLException {

		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd.append("update t1933_supervisor " ).append( "set fec_inicio = ? , "
					).append( "    fec_fin = ? , ind_estado = ? , cod_user_mod = ? , fec_mod = ?  "
					).append( " where cod_uorgan = ? and cod_mov = ? "
					).append( "      and cod_personal = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			pre.setDate(1, new BeanFechaHora(fechaInicio).getSQLDate());
			pre.setDate(2, new BeanFechaHora(fechaFin).getSQLDate());
			pre.setString(3, estado);
			pre.setString(4, usuario);
			pre.setTimestamp(5, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(6, (String) superv.get("cod_uorgan"));
			pre.setString(7, (String) superv.get("cod_mov"));
			pre.setString(8, (String) superv.get("cod_personal"));
			int res = pre.executeUpdate();

			result = (res > 0);

		}


		catch (Exception e) {
			//log.error("**** SQL ERROR **** Se ha encontrado una doble marcación para REGISTRO:" + codPers + ", FECHA:"+ fechaMarcacion + " "+ e.toString());
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