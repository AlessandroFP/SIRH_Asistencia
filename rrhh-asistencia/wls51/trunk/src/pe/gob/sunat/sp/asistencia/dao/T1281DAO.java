package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanVacacion;
import pe.gob.sunat.sp.dao.T99DAO;
import pe.gob.sunat.utils.Constantes;

/**
 * <p>
 * Title: T1281DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1270Vacaciones_c
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

public class T1281DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1281DAO.class);

	public T1281DAO() {
	}

	/**
	 * Metodo encargado de insertar un registro de cabecera de vacaciones en la
	 * base de datos.
	 * @throws SQLException
	 */
	public boolean insertByCodPersAnno(String dbpool, String codPers,
			String anno, int diasVaca, String usuario) throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {

			strUpd = "insert into t1281vacaciones_c(cod_pers,anno,dias,saldo,fcreacion,cuser_crea) values (?,?,?,?,?,?)";

			con = getConnection(dbpool);
			pre = con.prepareStatement(strUpd);
			pre.setString(1, codPers);
			pre.setString(2, anno);
			pre.setInt(3, diasVaca);
			pre.setInt(4, diasVaca);
			pre.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pre.setString(6, usuario);
			int res = pre.executeUpdate();

			if (res > 0)
				result = true;
 

		}

		catch (Exception e) {

			result = false;
			log.error("**** SQL ERROR ****", e);
			throw new SQLException(e.toString());
		} finally {
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
		return result;
	}

	/**
	 * Metodo que se encarga de buscar los registros de las cabeceras de
	 * vacaciones con saldos superiores al requerido para la venta, para un
	 * trabajador especifico.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersSaldoVenta(String dbpool, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			T99DAO codigoDAO = new T99DAO();
			String diasVenta = codigoDAO.findParamByCodTabCodigo(dbpool,
					Constantes.CODTAB_PARAMETROS_ASISTENCIA,
					Constantes.DIAS_VENTA_VACACIONES);

			strSQL.append("select 	c.cod_pers, c.anno, c.dias, c.saldo "
					).append( "from 	t1281vacaciones_c c " ).append( "where 	c.cod_pers = ?  "
					).append( "        and c.saldo >= ? "
					).append( "        and c.anno not in ( "
					).append( "                select  anno_vac "
					).append( "                from	t1282vacaciones_d "
					).append( "                where   cod_pers = c.cod_pers and "
					).append( "                        licencia = ? ) "
					).append( "order by c.anno ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, diasVenta);
			pre.setString(3, Constantes.VACACION_VENTA);
			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;
			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setAnno(rs.getString("anno"));
				vac.setDias(rs.getInt("dias"));
				vac.setSaldo(rs.getInt("saldo"));
				//vac.setSaldoTemp(rs.getInt("saldo_temp"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return vacaciones;
	}

	/**
	 * Metodo que se encarga de buscar los registros de las cabeceras de
	 * vacaciones con saldos superiores al requerido para la venta, para un
	 * trabajador y ano especificos.
	 */
	public boolean findSaldoVacacional(String dbpool, String codPers,
			String anno, String dias) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean tiene = false;

		try {

			T99DAO codigoDAO = new T99DAO();
			
			//Este trae al constante de 15 ... ya no deberia de ser...
			//debe ser el que se ingresa por el JSP
			//String diasVenta = codigoDAO.findParamByCodTabCodigo(dbpool,
			//		Constantes.CODTAB_PARAMETROS_ASISTENCIA,
			//		Constantes.DIAS_VENTA_VACACIONES);
			String diasVenta = dias;
			
			strSQL.append("select c.cod_pers from t1281vacaciones_c c "
					).append( "where   c.cod_pers = ? "
					).append( "and c.saldo >= ? and c.anno = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, diasVenta);
			pre.setString(3, anno);
			rs = pre.executeQuery();

			if (rs.next()) {
				tiene = true;
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return tiene;
	}
	
	/**
	 * Metodo que se encarga de buscar si un usuario posee una venta 
	 * vacacional para un determinado anho
	 */
	public boolean findVentaVacacional(String dbpool, String codPers,
			String anno) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean tiene = false;

		try {

			strSQL.append(" select * from t1282vacaciones_d where cod_pers = ? and anno_vac = ? and licencia = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, anno);
			pre.setString(3, Constantes.VACACION_VENTA);
			rs = pre.executeQuery();
			
			if (rs.next()) {
				tiene = true;
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return tiene;
	}	

	/**
	 * M�todo encargado de buscar los registros de la cabecera de las vacaciones
	 * que cumplan con los par�metros recibidos.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. N�mero de registro del trabajador.
	 * @param saldoFavor
	 *            Boolean. Que indica si se desea obtener s�lo los registros con
	 *            saldo a favor.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersSaldo(String dbpool, String codPers,
			boolean saldoFavor) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select cod_pers, anno, dias, saldo"
					).append( " from t1281vacaciones_c where cod_pers = ? ");

			if (saldoFavor) {
				strSQL.append(" and saldo > 0");
			}

			strSQL.append(" order by anno");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setAnno(rs.getString("anno"));
				vac.setDias(rs.getInt("dias"));
				vac.setSaldo(rs.getInt("saldo"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return vacaciones;
	}

	/**
	 * Metodo encargado de buscar los registros de cabecera de vacaciones para un
	 * trabajador y ano especifico, opcionalmente filtrados por su saldo a
	 * favor.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param saldoFavor
	 *            Boolean. Que indica si se desea obtener solo los registros con
	 *            saldo a favor.
	 * @param anho
	 *            Ano de busqueda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * 
	 * @throws SQLException
	 */
	public ArrayList findByCodPersSaldoAnno(String dbpool, String codPers,
			boolean saldoFavor, String anho, HashMap seguridad)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {
			String from2010=null;									//jquispecoi 04/2014
			if(seguridad!=null){
				from2010=(String)seguridad.get("from2010");			
				if (seguridad.get("wasNull")!=null)
					seguridad=null;
			}														//
			
			strSQL.append("select cod_pers, anno, dias, saldo"
					).append( " from  t1281vacaciones_c v, t02perdp p "
					).append( " where v.cod_pers = ? and v.dias > 0 " + (from2010==null?"":" and v.anno >= '2010' "));		//jquispecoi 04/2014

			//criterios de visibilidad
			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ").append("(select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append( uoAO ).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			if (saldoFavor) {
				strSQL.append(" and saldo > 0");
			}

			if (!anho.trim().equals("")) {
				strSQL.append(" and anno = ?");
			}

			strSQL.append(" and v.cod_pers = p.t02cod_pers order by anno desc");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			if (!anho.trim().equals("")) {
				pre.setString(2, anho.trim());
			}
			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setAnno(rs.getString("anno"));
				vac.setDias(rs.getInt("dias"));
				vac.setSaldo(rs.getInt("saldo"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return vacaciones;
	}
	
	/**
	 * Metodo encargado de buscar los registros de cabecera de vacaciones para un
	 * trabajador y ano especifico, opcionalmente filtrados por su saldo a
	 * favor.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param saldoFavor
	 *            Boolean. Que indica si se desea obtener solo los registros con
	 *            saldo a favor.
	 * @param anho
	 *            Ano de busqueda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * 
	 * @throws SQLException
	 */
	public ArrayList findByCodPersSaldoAnnoDias(String dbpool, String codPers,
			boolean saldoFavor,boolean diasMayorCero, String anho, HashMap seguridad)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {
			String from2010=null;									//jquispecoi 04/2014
			if(seguridad!=null){
				from2010=(String)seguridad.get("from2010");			
				if (seguridad.get("wasNull")!=null)
					seguridad=null;
			}														//
			
			strSQL.append("select cod_pers, anno, dias, saldo"
					).append( " from  t1281vacaciones_c v, t02perdp p "
					).append( " where v.cod_pers = ? ");
			
			if(diasMayorCero)
				strSQL.append(" and v.dias > 0 ");
			
			strSQL.append((from2010==null?"":" and v.anno >= '2010' "));		//jquispecoi 04/2014

			//criterios de visibilidad
			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ").append("(select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append( uoAO ).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			if (saldoFavor) {
				strSQL.append(" and saldo > 0");
			}

			if (!anho.trim().equals("")) {
				strSQL.append(" and anno = ?");
			}

			strSQL.append(" and v.cod_pers = p.t02cod_pers order by anno desc");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			if (!anho.trim().equals("")) {
				pre.setString(2, anho.trim());
			}
			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setAnno(rs.getString("anno"));
				vac.setDias(rs.getInt("dias"));
				vac.setSaldo(rs.getInt("saldo"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return vacaciones;
	}

	/**
	 * M�todo encargado de buscar los registros de la cabecera de las vacaciones
	 * que cumplan con los par�metros recibidos.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            String. N�mero de registro del trabajador.
	 * @param saldoFavor
	 *            Boolean. Que indica si se desea obtener s�lo los registros con
	 *            saldo a favor.
	 * @param anhoIni
	 *            A�o inferior de b�squeda.
	 * @param anhoFin
	 *            A�o superior de b�squeda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanVacacion.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersSaldoAnnoIniAnnoFin(String dbpool,
			String codPers, boolean saldoFavor, String anhoIni, String anhoFin)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select cod_pers, anno, dias, saldo"
					).append( " from t1281vacaciones_c where cod_pers = ? ");

			if (saldoFavor) {
				strSQL.append(" and saldo > 0");
			}

			if (!anhoIni.trim().equals("")) {
				strSQL.append(" and anno >= " ).append( anhoIni.trim());
			}

			if (!anhoFin.trim().equals("")) {
				strSQL.append(" and anno <= " ).append( anhoFin.trim());
			}

			strSQL.append(" order by anno");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			BeanVacacion vac = null;

			while (rs.next()) {

				vac = new BeanVacacion();

				vac.setCodPers(rs.getString("cod_pers"));
				vac.setAnno(rs.getString("anno"));
				vac.setDias(rs.getInt("dias"));
				vac.setSaldo(rs.getInt("saldo"));

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return vacaciones;
	}

	/**
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public int findByCodPers(String dbpool, String codPers) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int saldo = 0;

		try {

			strSQL.append("select sum(saldo) as acum_saldo"
					).append( " from t1281vacaciones_c where cod_pers = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			if (rs.next()) {
				saldo = rs.getInt("acum_saldo");
			}
 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return saldo;
	}
	
	/**
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public int findPrimerSaldoByCodPers(String dbpool, String codPers) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int saldo = 0;

		try {

			strSQL.append("select anno, saldo from t1281vacaciones_c where cod_pers = ? and saldo > 0 order by anno desc");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			if (rs.next()) {
				saldo = rs.getInt("saldo");
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return saldo;
	}	

	public int findByCodPersAnno(String dbpool, String codPers)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int max = 0;

		try {

			strSQL.append("select max(anno) as maximo from t1281vacaciones_c where cod_pers = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			if (rs.next()) {
				max = rs.getInt("maximo");
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return max;
	}

	public int findSaldoByAnho(String dbpool, String codPers, String anho)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int saldo = 0;

		try {

			strSQL.append("select saldo from t1281vacaciones_c where cod_pers = ? and anno = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, anho);
			rs = pre.executeQuery();

			if (rs.next()) {
				saldo = rs.getInt("saldo");
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return saldo;
	}

	/**
	 * Metodo encargado de cargar la lista de cabeceras de vacaciones
	 * 
	 * @param dbpool
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findSaldos(String dbpool, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select v.cod_pers, v.anno, v.saldo, p.t02ap_pate, p.t02ap_mate, p.t02nombres "
					).append( " from  t1281vacaciones_c v, t02perdp p "
					).append( " where v.cod_pers = ? and v.dias > 0 and v.cod_pers = p.t02cod_pers order by v.anno desc ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			rs = pre.executeQuery();
			vacaciones = new ArrayList();
			HashMap vac = null;

			while (rs.next()) {

				vac = new HashMap();

				String texto = rs.getString("t02ap_pate").trim().concat( " "
						).concat( rs.getString("t02ap_mate").trim() ).concat( ", "
						).concat( rs.getString("t02nombres").trim());

				vac.put("cod_pers", rs.getString("cod_pers"));
				vac.put("anno", rs.getString("anno"));
				vac.put("saldo", rs.getString("saldo"));
				vac.put("nombre", texto);

				vacaciones.add(vac);
			}

 
		}

		catch (Exception e) {
			log.error("**** SQL ERROR ****", e);
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
		return vacaciones;
	}

}