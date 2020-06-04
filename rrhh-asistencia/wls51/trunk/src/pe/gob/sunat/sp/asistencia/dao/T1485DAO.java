package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.utils.Constantes;

/**
 * 
 * Clase : T1485DAO Fecha : 28-dic-2004 11:40:02 Proyecto : Asistencia
 * Descripcion :
 * 
 * @author CGARRATT
 *  
 */
public class T1485DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1485DAO.class);

	public T1485DAO() {
	}

	/**
	 * Busca la lista de responsables por proceso
	 * 
	 * @param dbpool
	 * @param proceso
	 * @param uorgan
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findByProcesoUOrgan(String dbpool, String proceso,
			String uorgan) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select  p.t02cod_pers, p.t02nombres, p.t02ap_pate, p.t02ap_mate, "
					).append( "        u.t12des_corta, c.t99descrip "
					).append( "from    t1485seg_uorga s, "
					).append( "        t02perdp p, "
					).append( "        t12uorga u, "
					).append( "        t99codigos c "
					).append( "where   s.u_organ = ? and "
					).append( "        s.operacion = ? and "
					).append( "        c.t99cod_tab = ? and "
					).append( "        s.cod_pers = p.t02cod_pers and "
					).append( "        substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = u.t12cod_uorga and "
					).append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = c.t99codigo 	");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, uorgan.trim().toUpperCase());
			pre.setString(2, proceso);
			pre.setString(3, Constantes.CODTAB_CATEGORIA);

			rs = pre.executeQuery();
			lista = new ArrayList();
			HashMap r = null;

			while (rs.next()) {

				r = new HashMap();

				String texto = rs.getString("t02ap_pate").trim().concat(" ").concat( rs.getString("t02ap_mate").trim() ).concat( ", "
						).concat( rs.getString("t02nombres").trim());

				r.put("proceso", proceso);
				r.put("cod_uorg", uorgan.trim().toUpperCase());
				r.put("cod_pers", rs.getString("t02cod_pers"));
				r.put("nombre", texto);
				r.put("uuoo", rs.getString("t12des_corta"));
				r.put("categoria", rs.getString("t99descrip"));

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

}