package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1454DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1454Asistencia_d
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

public class T1454DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1454DAO.class);

	private static final String SELECT_MOVIMIENTO_SUSTENTO = "SELECT t.mov, m.descrip,m.tipo_id, t.fecha  ,t.cod_pers  FROM   T1454ASISTENCIA_D  t, t1279tipo_mov m "
			 + " WHERE t.cod_pers = ? " 
			 + " AND t.esta_id = 1"
			 + " AND t.mov = m.mov"
			 + " AND m.tipo_id IN ('02','03','05')"
	 		 + " AND t.fecha BETWEEN TO_DATE(? , '%d/%m/%Y') AND TO_DATE(?,'%d/%m/%Y')";
			
	public T1454DAO() {
	}

	/**
	 * Metodo encargado de eliminar los registros del detalle de asistencia
	 * diaria correspondientes a un trabajador para un periodod determinado.
	 * @throws SQLException
	 */
	public void deleteByCodPersPeriodo(String dbpool, String codPers,
			String periodo) throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strUpd = "delete from t1454asistencia_d  where cod_pers = ? and periodo = ? ";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.executeUpdate();

	 

		} catch (Exception e) {
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

	}

	/**
	 * Metodo encargado de insertar un registro de asistencia diaria.
	 * @throws SQLException
	 */
	public void insertByCodPersPeriodoMov(String dbpool, String codPers,
			String periodo, String fecha, String uo, String mov, float total,
			String usuario) throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;

		try {

			strUpd = "insert into t1454asistencia_d(cod_pers,periodo,mov,fecha,total,esta_id,fcreacion,cuser_crea) values(?,?,?,?,?,?,?,?)";
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setString(3, mov);
			pre.setDate(4, new java.sql.Date(Utiles.stringToDate(fecha)
					.getTime()));
			pre.setFloat(5, total);
			pre.setString(6, Constantes.ACTIVO);
			pre.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			pre.setString(8, usuario);
			pre.executeUpdate();
 
		} catch (Exception e) {

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

	}
	
	/**
	 * Metodo encargado de insertar un registro de asistencia diaria.
	 * @throws SQLException
	 */
	public boolean updateTardanzaEfectiva(String dbpool, String codPers,
			String periodo, String fecha, String mov, float minutosExtra) throws SQLException {

		String strSQL = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean hayMinTardEfect = false;
		try {

			strSQL = 	" update t1454asistencia_d " +
						" set 	 total = ? " +
						" where  cod_pers = ? and periodo = ? and mov = ? and fecha = ? and total > 0 ";
			
			log.debug("updateTardanzaEfectiva : "+strSQL);
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL);
			pre.setFloat(1, minutosExtra);
			pre.setString(2, codPers);
			pre.setString(3, periodo);
			pre.setString(4, mov);
			pre.setDate(5, new java.sql.Date(Utiles.stringToDate(fecha).getTime()));
			int res = pre.executeUpdate();
			if (res>0){ 
				hayMinTardEfect = true;
			}
 
		} catch (Exception e) {
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
		return hayMinTardEfect;
	}	

	/**
	 * Metodo encargado de buscar los registros de asistencia diaria de un
	 * trabajador para una fecha y periodos especificos. Join con la tabla
	 * T1279Tipo_Mov
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFechaPeriodo(String dbpool, String codPers,
			String fecha, String periodo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;
		String fec = "";
		try {
			//String fec = Utiles.toYYYYMMDD(fecha);
			
			if (fecha.indexOf("/")>3){ 
			   fec = fecha;	
			}else {
			   fec = Utiles.toYYYYMMDD(fecha);
			}
			
			strSQL.append("select d.mov, m.medida, d.total "
					).append( " from  t1454asistencia_d d, "
					).append( "       t1279tipo_mov m " 
					).append( " where d.cod_pers = ? and "					
					).append( "       d.periodo = ? and " 
					).append( "		  m.califica = ? and "
					).append( "       d.fecha = DATE('").append( fec ).append( "') and "
					).append( "       d.mov = m.mov ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setString(3, Constantes.DESCUENTO);
			rs = pre.executeQuery();
			detalle = new ArrayList();
			BeanTipoMovimiento det = null;

			while (rs.next()) {

				det = new BeanTipoMovimiento();

				det.setMov(rs.getString("mov"));
				det.setMedida(rs.getString("medida"));
				det.setTotal(rs.getFloat("total"));

				detalle.add(det);
			}

 
		}

		catch (Exception e) {
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
		return detalle;
	}
	
	/**
	 * Metodo encargado de buscar los registros de asistencia diaria de un
	 * trabajador para una fecha y periodos especificos. Join con la tabla
	 * T1279Tipo_Mov
	 * @throws SQLException
	 */
	public float findDescuentoDiarioMovimiento(String dbpool, String codPers,
			String fecha, String mov) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		float total = 0;
		try {
			
			String fec = Utiles.toYYYYMMDD(fecha);
			
			strSQL.append("select sum(d.total) total "
					).append( " from  t1454asistencia_d d " 
					).append( " where d.cod_pers = ? and "					
					).append( "       d.mov = ? and " 
					).append( "       d.fecha = DATE('").append(fec).append( "') ");					

			log.debug("strSQL : "+strSQL);
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, mov);
			rs = pre.executeQuery();

			if (rs.next()) total = rs.getFloat("total");
 
		}

		catch (Exception e) {
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
		return total;
	}	

	/**
	 * Metodo encargado de obtener la cantidad de dias laborados por un
	 * trabajador en un rango de fechas determinado. Join con la tabla
	 * T1276Periodo.
	 * @throws SQLException
	 */
	public int findDiasLaborablesByFiniFin(String dbpool, String codPers,
			String fechaIni, String fechaFin) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int dias = 0;

		try {

			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);

			strSQL.append("select 	count(distinct a.fecha) dias "
					).append( "from 	t1454asistencia_d a, "
					).append( "        t1276periodo p "
					).append( "where   a.cod_pers = ? and "
					).append( "        DATE(p.finicio[7,10]||''/''||p.finicio[4,5]||''/''||p.finicio[1,2]) <= DATE('"
					).append( fIni
					).append( "') and "
					).append( "        DATE(ffin[7,10]||''/''||ffin[4,5]||''/''||ffin[1,2]) >= DATE('"
					).append( fFin ).append( "') and " ).append( "        a.periodo = p.periodo and "
					).append( "        a.mov != ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.MOV_INASISTENCIA);
			rs = pre.executeQuery();

			if (rs.next()) {
				dias = rs.getInt("dias");
			}

 
		}

		catch (Exception e) {
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
		return dias;
	}
        //PAS20171U230200001 - solicitud de reintegro  
	/**
	 * Obtener fechas con movimientos
	 * @param dbpool
	 * @param filtro
	 * @return
	 * @throws SQLException
	 */
	public List findByFechasConMovimientosParaValidarReintegro(DataSource ds, Map filtro) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		List detalle = new ArrayList();
		String fec = "";
		try {

			strSQL.append("SELECT t.mov, m.descrip, t.fecha  ,t.cod_pers FROM   T1454ASISTENCIA_D  t, t1279tipo_mov m")
					.append(" WHERE t.cod_pers = ?")
					.append(" AND TO_CHAR(t.fecha , '%Y') =?")
					.append(" AND TO_CHAR(t.fecha , '%m') =?")
					.append(" AND t.esta_id = 1")
					.append(" AND t.mov = m.mov")
					//.append(" AND m.califica = 'N'") // PAS20181U230200067 - solicitud de reintegro , se incluye los movimientos no descontables 
					.append(" AND m.tipo_id IN ('02','03','05')");
			con = getConnection(ds);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, (String) filtro.get("cod_pers"));
			pre.setString(2, (String) filtro.get("anioConsulta"));
			pre.setString(3, (String) filtro.get("mesConsulta"));
			log.debug(strSQL);
			log.debug(filtro);
			rs = pre.executeQuery();
			while (rs.next()) {
				Date date = rs.getDate("fecha");
				detalle.add(date);
			}
			log.debug(detalle);
		}

		catch (Exception e) {
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
		return detalle;
	}

    //PAS20181U230200067 - solicitud de reintegro planilla adicionales adicionar sustento  
	/**
	 * Obtener movimientos para sustentar reintegro en un determinado rango de fecha  
	 * @param dbpool
	 * @param filtro
	 * @return
	 * @throws SQLException
	 */
	

	public List obtenerMovimientosSustento(DataSource ds, Map filtro) throws SQLException { 
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		List detalle = new ArrayList(); 
		try { 
			con = getConnection(ds);
			pre = con.prepareStatement(SELECT_MOVIMIENTO_SUSTENTO);
			pre.setString(1, (String) filtro.get("cod_pers"));
			pre.setString(2, (String) filtro.get("ffinicioLic"));
			pre.setString(3, (String) filtro.get("ffinLic"));
			log.debug(SELECT_MOVIMIENTO_SUSTENTO);
			log.debug(filtro);
			rs = pre.executeQuery();
			while (rs.next()) {
				Map movimiento = new HashMap();
				movimiento.put("mov", rs.getString("mov"));
				movimiento.put("descrip", rs.getString("descrip"));
				movimiento.put("tipo_id", rs.getString("tipo_id"));
				movimiento.put("fecha", rs.getString("fecha"));
				movimiento.put("cod_pers", rs.getString("cod_pers"));
				detalle.add(movimiento);
			}
			log.debug(detalle);
		}

		catch (Exception e) {
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
		return detalle;
	}
	

         //PAS20171U230200001 - solicitud de reintegro  
	/**
	 * Metodo encargado de buscar los registros de asistencia diaria de un
	 * trabajador para un grupo de fechas especificos.
	 * ArrayList
	 * @throws SQLException
	 */
	public ArrayList findByCodPersFechas(String dbpool, String codPers,
										 java.sql.Date[] fechas) throws SQLException {

		StringBuffer strSQL = null;
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;
		
		try {
			
			strSQL = new StringBuffer("select fecha, mov from t1454asistencia_d where cod_pers = ? and mov != '00' and fecha in (");

			con = getConnection(dbpool);
			for (int i = 0; i < fechas.length; i++){
				strSQL.append("?");
				
				if (i < fechas.length - 1){
					strSQL.append(", ");
				}
			}
			
			strSQL.append(")");
			log.debug(strSQL);
			
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			for (int i = 0; i < fechas.length; i++){
				pre.setDate(i+2, fechas[i]);
				log.debug(fechas[i]);
			}
			rs = pre.executeQuery();
			detalle = new ArrayList();
			
			while (rs.next()) {
				HashMap det = new HashMap();

				det.put("fecha", rs.getObject("fecha"));
				det.put("mov", rs.getObject("mov"));

				detalle.add(det);
			}

 
		}

		catch (Exception e) {
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
		return detalle;
	}	

        //PAS20171U230200001 - solicitud de reintegro   
	/**
	 * Metodo encargado de buscar los registros de asistencia diaria de un
	 * trabajador para un rango de fechas especifico.
	 * ArrayList
	 * @throws SQLException
	 */
	public ArrayList findByCodPersRangoFechas(String dbpool, String codPers,
										 	  java.sql.Date fecIni, java.sql.Date fecFin) throws SQLException {

		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;
		
		try {
			
			String strSQL = "select fecha, mov from t1454asistencia_d where cod_pers = ? and mov != '00' and fecha >= ? and fecha <= ?";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setDate(2, fecIni);
			pre.setDate(3, fecFin);
				
			rs = pre.executeQuery();
			detalle = new ArrayList();
			
			while (rs.next()) {
				HashMap det = new HashMap();

				det.put("fecha", rs.getObject("fecha"));
				det.put("mov", rs.getObject("mov"));

				detalle.add(det);
			}

 
		}

		catch (Exception e) {
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
		return detalle;
	}

}