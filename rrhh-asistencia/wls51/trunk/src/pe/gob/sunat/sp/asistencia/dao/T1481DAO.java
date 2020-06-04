package pe.gob.sunat.sp.asistencia.dao;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.dao.DAOAccesoBD;

/**
 * 
 * Clase : T1481DAO Fecha : 14-dic-2005 11:40:02 Proyecto : Asistencia
 * Descripcion :
 * 
 * @author CGARRATT
 *  
 */
public class T1481DAO extends DAOAccesoBD {

	private static final Logger logger = Logger.getLogger(T1481DAO.class);

	public T1481DAO() {
	}

	/**
	 * Metodo encargo de registrar el log de un proceso en BD
	 * 
	 * @param mapa
	 * @param usuario
	 * @return
	 */
	public boolean registraBatchLogBD(String dbpool, HashMap mapa, String usuario) {

		String strUpd = "";
		PreparedStatement pre = null;
		java.sql.Connection con = null;
		boolean result = true;
		try {

			String idProceso = (String) mapa.get("messageID");
			Timestamp fInicio = (Timestamp) mapa.get("fInicio");
			String codPers = (String) mapa.get("codPers");
			//InputStream in = (InputStream) mapa.get("archivo");

			strUpd = "update t1481lote " + "set finicio = ?, "
					+ "  estado_id = ? "
					+ "where cod_proceso = ? and cod_pers = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);

			pre.setTimestamp(1, fInicio);
			
			pre.setString(2, "2");
			pre.setString(3, idProceso);
			pre.setString(4, codPers);
			int res = pre.executeUpdate();

			if (res > 0)
				result = true;
		}
		catch (Exception e) {
			result = false;
			logger.debug("Error : " + e.toString());
		} finally {
			try {
              if (pre != null){
			    pre.close();
			  }
			} catch (Exception e) {}
			try {
			  if (con != null){
			    con.close();
			  }
			} catch (Exception e) {}
		}
		return result;
	}	
	
	/**
	 * Metodo encargo de registrar el log de un proceso en BD
	 * 
	 * @param mapa
	 * @param usuario
	 * @return
	 */
	public boolean registraLogBD(String dbpool, HashMap mapa, String usuario) {

		String strUpd = "";
		PreparedStatement pre = null;
		java.sql.Connection con = null;
		boolean result = true;
		try {

			String idProceso = (String) mapa.get("messageID");
			Timestamp fFin = (Timestamp) mapa.get("fFin");
			String codPers = (String) mapa.get("codPers");
			InputStream in = (InputStream) mapa.get("archivo");

			strUpd = "update t1481lote " + "set ffin = ?, "
					+ "    archivo = ?, " + "    estado_id = ? "
					+ "where cod_proceso = ? and cod_pers = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);

			pre.setTimestamp(1, fFin);
			pre.setBinaryStream(2, in, in.available());
			pre.setString(3, "1");
			pre.setString(4, idProceso);
			pre.setString(5, codPers);
			int res = pre.executeUpdate();

			if (res > 0)
				result = true;
		}
		catch (Exception e) {
			result = false;
			logger.debug("Error : " + e.toString());
		} finally {
			try {
              if (pre != null){
			    pre.close();
			  }
			} catch (Exception e) {}
			try {
			  if (con != null){
			    con.close();
			  }
			} catch (Exception e) {}
		}
		return result;
	}	
	
	/**
	 * Metodo encargado de listar los logs registrados por un determinado
	 * usuario
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findLogsByCodPers(String dbpool, String codPers,
			String tipo) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		java.sql.Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;
		
		BeanFechaHora bfh = new BeanFechaHora();									//jquispecoi 05/2014
		String anno;
		String mes;
		if(Integer.parseInt(bfh.getMes())==1){
			anno = String.valueOf(Integer.parseInt(bfh.getAnho())-1);
			mes="12";
		}else{
			anno=bfh.getAnho();
			mes= String.valueOf(Integer.parseInt(bfh.getMes())-1);
		}																			//

		try {

			strSQL = "select cod_proceso, finicio, ffin, tobservacion, estado_id "
				+ "from t1481lote where cod_pers = ? and ctipo_proceso = ? "
				+ "and finicio >= to_date('01/"+mes.trim()+"/"+anno.trim()+" 00:00:00','%d/%m/%Y %H:%M:%S') "    //jquispe 01/2015
				+ "order by finicio desc ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, codPers);
			pre.setString(2, tipo);
			rs = pre.executeQuery();
			lista = new ArrayList();

			while (rs.next()) {

				HashMap log = new HashMap();

				log.put("id_proceso", rs.getString("cod_proceso"));
				log.put("fecha_inicio", rs.getTimestamp("finicio"));
				log.put("fecha_fin", rs.getTimestamp("ffin"));
				log.put("observacion", rs.getString("tobservacion"));
				log.put("estado", rs.getString("estado_id"));

				lista.add(log);
			}

		}

		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
	          if (rs != null){
			    rs.close();
			  }
			} catch (Exception e) {}
			try {
              if (pre != null){
				    pre.close();
			  }
			} catch (Exception e) {}
			try {
			  if (con != null){
			    con.close();
			  }
			} catch (Exception e) {}
		}
		return lista;
	}

	
	/**
	 * Metodo encargado de listar los logs registrados por un determinado
	 * usuario
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public InputStream findArchivoById(String dbpool, String id)
			throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		java.sql.Connection con = null;
		ResultSet rs = null;
		InputStream archivo = null;

		try {

			strSQL = "select archivo from t1481lote where cod_proceso = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setString(1, id);
			rs = pre.executeQuery();

			if (rs.next()) {
				Blob b = rs.getBlob("archivo");
				archivo = b != null ? b.getBinaryStream() : null;
			}
		}
		catch (Exception e) {
			throw new SQLException(e.toString());
		} finally {
			try {
	          if (rs != null){
			    rs.close();
			  }
			} catch (Exception e) {}
			try {
              if (pre != null){
			    pre.close();
			  }
			} catch (Exception e) {}
			try {
			  if (con != null){
			    con.close();
			  }
			} catch (Exception e) {}
		}
		return archivo;
	}
	
	/**
	 * Metodo encargo de registrar el log de un proceso en BD
	 * 
	 * @param mapa
	 * @param usuario
	 * @return
	 */
	public boolean registraMensajeLogBD(String dbpool, HashMap mapa,
			String usuario) {
		//log.debug("inicio de registraMensajeLogBD");
		String strUpd = "";
		PreparedStatement pre = null;
		java.sql.Connection con = null;
		boolean result = false;
		try {
			//log.debug("mapa: " + mapa);el log no deja insertar, y sale un error al ejecutar el proceso
			String idProceso = (String) mapa.get("messageID");
			String codPers = (String) mapa.get("codPers");
			String obs = (String) mapa.get("observacion");
			String tipo = (String) mapa.get("tipoProceso");
			Timestamp fInicio = (Timestamp) mapa.get("fInicio");

			strUpd = "insert into t1481lote(cod_proceso,finicio,cod_pers,tobservacion,estado_id,ctipo_proceso,fcreacion,cuser_crea) "
					+ "values (?,?,?,?,?,?,?,?)";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);

			pre.setString(1, idProceso);
			pre.setTimestamp(2, fInicio);
			pre.setString(3, codPers);
			pre.setString(4, obs);
			pre.setString(5, "0");
			pre.setString(6, tipo);
			pre.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			pre.setString(8, usuario);
			int res = pre.executeUpdate();

			if (res > 0)
				result = true;

		}

		catch (Exception e) {
			result = false;
			logger.debug("Error : " + e.toString());
		} finally {
			try {
              if (pre != null){
			    pre.close();
			  }
			} catch (Exception e) {}
			try {
			  if (con != null){
			    con.close();
			  }
			} catch (Exception e) {}		}
		return result;
	}
	
}