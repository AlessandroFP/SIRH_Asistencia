package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;

/**
 * <p>
 * Title: T1455DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1455Sol_Seg
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Sunat
 * </p>
 * 
 * @author cgarratt
 * @version 1.0
 */

public class T1455DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1455DAO.class);

	public T1455DAO() {
	}

	/**
	 * Metodo encargado de buscar el codigo del siguiente seguimiento de una
	 * solicitud.
	 * @throws SQLException
	 */
	public String findSigSeg(String dbpool, String anno, Short numero,
			String codPers) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String num = "0";

		try {

			strSQL = "select max(num_seguim)+1 total from t1455sol_seg where cod_pers = ? and anno = ? and numero = ?";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codPers);
			pre.setString(2, anno);
			pre.setShort(3, numero.shortValue());

			rs = pre.executeQuery();
			if (rs.next()) {
				num = rs.getString("total");
				if (num == null || num.trim().equals("")) {
					num = "0";
				}
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
		return num;
	}
	

	/**
	 * Metodo que inserta registro de Seguimiento
	 * @throws SQLException
	 */
	public boolean insertRegistroSeguimiento(String dbpool,String anno,Short numero,String uOrgan,
	String codPers,	String numSeguimiento,String accion_iniciar,String estado_seguimiento, 
	String userOrig,String userDest,String txtObsSeg,String usuario) throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd = "Insert into t1455sol_seg (" +
			"anno,numero,u_organ,cod_pers,num_seguim,fecha_recep,fecha_deriv,accion_id," +
			"estado_id,cuser_orig,cuser_dest,tobserv,fcreacion,cuser_crea,fmod,cuser_mod) "+
			" Values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			
			pre.setString(1, anno);
			pre.setShort(2, numero.shortValue());
			pre.setString(3, uOrgan);
			pre.setString(4, codPers);
			pre.setString(5, numSeguimiento);
			pre.setTimestamp(6, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(7, null);
			pre.setString(8, accion_iniciar);
			pre.setString(9, estado_seguimiento);
			pre.setString(10,userOrig);
			pre.setString(11, userDest);
			pre.setString(12, txtObsSeg);
			pre.setTimestamp(13,new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(14,usuario );
			pre.setString(15,null );
			pre.setString(16, null);
			
			
			int res = pre.executeUpdate();

			result = true;

 
            
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
	public boolean updateAprobadorSeg(String dbpool, String numMov, String usuario, String nuevoAprob, 
			String viejoAprob, String anno, Short numero, String uorgan, String cod_pers )throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd = "Update t1455sol_seg Set cuser_dest = ?, fmod = ?, cuser_mod = ? "+ 
            " Where anno = ? and numero = ? and cod_pers = ?  "+
            " and cuser_dest = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			
			pre.setString(1, nuevoAprob);
			pre.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(3, usuario);
			pre.setString(4, anno);
			pre.setShort(5, numero.shortValue());
			
			pre.setString(6, cod_pers);
			pre.setString(7, viejoAprob);
			
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
	 * Metodo que transfiere las solicitudes pendientes de aprobación de de un aprobador a otro. 
	 * @throws SQLException
	 * @author jmaravi
	 * @since 15/04/2014
	 */
	public int transferirSegPendientes(String dbpool, String uorgan, String numMov, 
				String viejoAprob, String nuevoAprob, String usuario )throws SQLException 
	{
		StringBuffer strUpd = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		int result = 0;

		try {

			strUpd.append("	update t1455sol_seg ss				");
			strUpd.append("	set ss.cuser_dest = ?, fmod = current, cuser_mod = ?			");
			strUpd.append("	where 				");
			strUpd.append("		exists( select 1			");
			strUpd.append("			   from t1277solicitud s		");
			strUpd.append("			   where s.cod_pers = ss.cod_pers  		");
			strUpd.append("				and s.anno = ss.anno  	");
			strUpd.append("				and s.numero = ss.numero  	");
			strUpd.append("				and s.seguim_act = ss.num_seguim 	");
			strUpd.append("				and ss.estado_id <> '2'	");
			strUpd.append("				and s.u_organ = ?	");
			strUpd.append("				and ss.cuser_dest = ?	");			
			strUpd.append("				and s.licencia = ?	");
			strUpd.append("		);			");


			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd.toString());
			
			pre.setString(1, nuevoAprob);
			pre.setString(2, usuario);
			pre.setString(3, uorgan);
			pre.setString(4, viejoAprob);			
			pre.setString(5, numMov);
		
			result = pre.executeUpdate();            
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