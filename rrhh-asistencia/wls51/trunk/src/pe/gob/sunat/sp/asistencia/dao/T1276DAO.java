package pe.gob.sunat.sp.asistencia.dao;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.util.date.FechaBean;//ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanPeriodo;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1276DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1276Periodo
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

public class T1276DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1276DAO.class);

	public T1276DAO() {
	}

	/**
	 * Metodo encargado de listar los datos de los periodos filtrandolos por
	 * estado.
	 * @throws SQLException
	 */
	public ArrayList findByEstId(String dbpool, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList periodos = null;

		try {

			strSQL.append("select periodo, finicio, ffin, fcierre, ")
					.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, ")
					.append("fec_ini_mf, fec_fin_mf, fec_cierre_mf, ")
					.append( "est_id from t1276periodo ")
					//PRAC-ASANCHEZ 27/05/2009
					//).append( " where est_id = ? order by 1,2 ");
					.append( "where est_id = ? order by 1 desc ");
					//
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);
			rs = pre.executeQuery();
			periodos = new ArrayList();
			BeanPeriodo periodo = null;

			while (rs.next()) {

				periodo = new BeanPeriodo();

				periodo.setPeriodo(rs.getString("periodo"));
				periodo.setFechaIni(rs.getString("finicio"));
				periodo.setFechaFin(rs.getString("ffin"));
				periodo.setFechaCie(rs.getString("fcierre"));
				periodo.setEstId(rs.getString("est_id"));
				//JRR - 04/03/2010
				periodo.setFechaCieCAS(rs.getString("fec_cierre_cas"));
				periodo.setFechaFinCAS(rs.getString("fec_fin_cas"));
				periodo.setFechaIniCAS(rs.getString("fec_ini_cas"));
				//JRR - 06/06/2011
				periodo.setFechaCieModForm(rs.getString("fec_cierre_mf"));
				periodo.setFechaFinModForm(rs.getString("fec_fin_mf"));
				periodo.setFechaIniModForm(rs.getString("fec_ini_mf"));
				
				periodos.add(periodo);
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
		return periodos;
	}

	/**
	 * Metodo encargado de listar los datos del registro de Periodo cuyo codigo
	 * es el indicado por el parametro.
	 * @throws SQLException
	 */
	public BeanPeriodo findByCodigo(String dbpool, String codigo)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanPeriodo periodo = new BeanPeriodo();

		try {

			strSQL.append("select periodo, finicio, ffin, fcierre, ")
			.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, ")
			.append("fec_ini_mf, fec_fin_mf, fec_cierre_mf, ")
			.append( " est_id from t1276periodo" ).append( " where periodo = ?");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codigo);
			rs = pre.executeQuery();

			if (rs.next()) {

				periodo = new BeanPeriodo();

				periodo.setPeriodo(rs.getString("periodo"));
				periodo.setFechaIni(rs.getString("finicio"));
				periodo.setFechaFin(rs.getString("ffin"));
				periodo.setFechaCie(rs.getString("fcierre"));
				periodo.setEstId(rs.getString("est_id"));
				//JRR - 04/03/2010
				periodo.setFechaCieCAS(rs.getString("fec_cierre_cas"));
				periodo.setFechaFinCAS(rs.getString("fec_fin_cas"));
				periodo.setFechaIniCAS(rs.getString("fec_ini_cas"));
				//JRR - 20/04/2011
				periodo.setFechaCieModForm(rs.getString("fec_cierre_mf"));
				periodo.setFechaFinModForm(rs.getString("fec_fin_mf"));
				periodo.setFechaIniModForm(rs.getString("fec_ini_mf"));

			}

 
		}

		catch (Exception e) {

			e.printStackTrace();
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
		return periodo;
	}
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
	 * Metodo encargado de listar los datos del registro de Periodo cuyo codigo
	 * es el indicado por el parametro y segun estado.
	 * @throws SQLException
	 */
	public BeanPeriodo findByCodigoByEstado(String dbpool, String codigo, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanPeriodo periodo = new BeanPeriodo();

		try {

			strSQL.append("select periodo, finicio, ffin, fcierre, ")
			.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, ")
			.append("fec_ini_mf, fec_fin_mf, fec_cierre_mf, ")
			.append( " est_id from t1276periodo" ).append( " where periodo = ? and est_id=? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codigo);
			pre.setString(2, estado);
			rs = pre.executeQuery();

			if (rs.next()) {

				periodo = new BeanPeriodo();

				periodo.setPeriodo(rs.getString("periodo"));
				periodo.setFechaIni(rs.getString("finicio"));
				periodo.setFechaFin(rs.getString("ffin"));
				periodo.setFechaCie(rs.getString("fcierre"));
				periodo.setEstId(rs.getString("est_id"));				
				periodo.setFechaCieCAS(rs.getString("fec_cierre_cas"));
				periodo.setFechaFinCAS(rs.getString("fec_fin_cas"));
				periodo.setFechaIniCAS(rs.getString("fec_ini_cas"));				
				periodo.setFechaCieModForm(rs.getString("fec_cierre_mf"));
				periodo.setFechaFinModForm(rs.getString("fec_fin_mf"));
				periodo.setFechaIniModForm(rs.getString("fec_ini_mf"));

			}

 
		}

		catch (Exception e) {

			e.printStackTrace();
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
		return periodo;
	}
	//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral

	/**
	 * Metodo encargado de buscar el registro del periodo vigente a la fecha
	 * llamada al metodo.
	 * @throws SQLException
	 */
	public BeanPeriodo findPeriodoActual(String dbpool) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanPeriodo periodo = new BeanPeriodo();

		try {

			String fechaActual = Utiles.toYYYYMMDD(Utiles.obtenerFechaActual());

			strSQL.append("select periodo, finicio, ffin, fcierre, ")
					.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, ")
					.append( " est_id from t1276periodo where est_id = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();

			boolean fin = false;
			while (rs.next() && !fin) {

				//String fIni = Utiles.toYYYYMMDD(rs.getString("finicio"));
				//String fFin = Utiles.toYYYYMMDD(rs.getString("ffin"));
				String fIni = rs.getString("finicio");
				String fFin = rs.getString("ffin");

				if (((fIni.equals("")) || (fIni.compareTo(fechaActual) <= 0))
						&& ((fFin.equals("")) || (fFin.compareTo(fechaActual) >= 0))) {

					periodo.setPeriodo(rs.getString("periodo"));
					periodo.setFechaIni(rs.getString("finicio"));
					periodo.setFechaFin(rs.getString("ffin"));
					periodo.setFechaCie(rs.getString("fcierre"));
					periodo.setEstId(rs.getString("est_id"));
					//JRR - 04/03/2010
					periodo.setFechaCieCAS(rs.getString("fec_cierre_cas"));
					periodo.setFechaFinCAS(rs.getString("fec_fin_cas"));
					periodo.setFechaIniCAS(rs.getString("fec_ini_cas"));
					
					fin = true;
				}
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
		return periodo;
	}

	/**
	 * Metodo encargado de listar los registros de periodos filtrados por un
	 * criterio con un valor determinado.
	 * @throws SQLException
	 */
	public ArrayList findByCritVal(String dbpool, String criterio, String valor)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList periodos = null;

		String fecCompara = "";

		try {
			strSQL.append(" select periodo, finicio, ffin, fcierre, ")
					// JRR - 14/06/2011
					.append("fec_ini_cas, fec_fin_cas, fec_cierre_cas, ")
					.append("fec_ini_mf, fec_fin_mf, fec_cierre_mf, ")
					//
					.append(" est_id from t1276periodo").append(
							" where est_id = ? ");

			boolean agrega = false;

			// busqueda por codigo de periodo
			if (criterio.equals("0")) {
				strSQL.append(" and periodo like '").append(
						valor.trim().toUpperCase()).append("%' ");
				agrega = true;
			}

			if (criterio.equals("1") || criterio.equals("2")
					|| criterio.equals("3") && valor.trim() != "") {
				fecCompara = Utiles.toYYYYMMDD(valor);
			}

			// PRAC-ASANCHEZ 27/05/2009
			// strSQL.append( " order by 1,2,3");
			strSQL.append(" order by 1 desc");
			//

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			periodos = new ArrayList();

			String fechaInicio;
			String fechaFinal;
			// prac-asanchez
			String fechaCierre;
			//			
			BeanPeriodo periodo = null;

			while (rs.next()) {
				agrega = true;
				if (!valor.trim().equals("")) {
					if (criterio.equals("1")) {
						// PRAC-ASANCHEZ 27/05/2009
						// fechaInicio =
						// Utiles.toYYYYMMDD(rs.getString("finicio"));
						fechaInicio = rs.getString("finicio");
						//

						agrega = (fecCompara.equals("") || (fecCompara
								.compareTo(fechaInicio) <= 0));
					}
					if (criterio.equals("2")) {
						// PRAC-ASANCHEZ 27/05/2009
						// fechaFinal = Utiles.toYYYYMMDD(rs.getString("ffin"));
						fechaFinal = rs.getString("ffin");
						//
						agrega = (fecCompara.equals("") || (fecCompara
								.compareTo(fechaFinal) >= 0));
					}

					if (criterio.equals("3")) {
						// PRAC-ASANCHEZ 27/05/2009
						// fechaInicio =
						// Utiles.toYYYYMMDD(rs.getString("fcierre"));
						fechaCierre = Utiles
								.toYYYYMMDD(rs.getString("fcierre"));

						agrega = (fecCompara.equals("") || (fecCompara
								.compareTo(fechaCierre) >= 0));
						//
					}

				}

				if (agrega) {

					periodo = new BeanPeriodo();

					periodo.setPeriodo(rs.getString("periodo"));
					periodo.setFechaIni(rs.getString("finicio"));
					periodo.setFechaFin(rs.getString("ffin"));
					periodo.setFechaCie(rs.getString("fcierre"));
					periodo.setEstId(rs.getString("est_id"));

					// JRR - 14/06/2011
					periodo.setFechaCieCAS(rs.getString("fec_cierre_cas"));
					periodo.setFechaFinCAS(rs.getString("fec_fin_cas"));
					periodo.setFechaIniCAS(rs.getString("fec_ini_cas"));
					// JRR - 14/06/2011
					periodo.setFechaCieModForm(rs.getString("fec_cierre_mf"));
					periodo.setFechaFinModForm(rs.getString("fec_fin_mf"));
					periodo.setFechaIniModForm(rs.getString("fec_ini_mf"));

					periodos.add(periodo);
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
		return periodos;
	}

	/**
	 * Metodo encargado de buscar el periodo vigente para la unidad
	 * organizacional y la fecha indicadas.
	 * @throws RemoteException
	 */

	public String joinWithT1278(String dbpool, String codUO,
			String fechaMarcacion) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fecEval = Utiles.toYYYYMMDD(fechaMarcacion);
		String periodo = ""; //C�digo del periodo

		try {

			strSQL.append("select 	p.periodo "
					).append( " from t1276periodo p, t1278periodo_area pa "
					).append( " where pa.u_organ = ? and "
					).append( " p.est_id = ? and pa.est_id = ? and "
					).append( " (finicio <= '"
					).append( fecEval
					).append( "' and "
					).append( " ffin >= '"
					).append( fecEval ).append( "')" ).append( " and pa.periodo = p.periodo ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codUO.trim());
			pre.setString(2, Constantes.ACTIVO);
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				periodo = rs.getString("periodo");
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
		return periodo;

	}
	
	/**
	 * Metodo encargado de buscar el periodo vigente para la unidad
	 * organizacional y la fecha indicadas.
	 * @throws RemoteException
	 */

	public HashMap findPeriodoCierre(String dbpool, String fecha, String codUO) throws SQLException {

		HashMap periodo = null;
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String fecEval = "";
		log.debug("T1276DAO.findPeriodoCierre... fecha: "+fecha);
		log.debug("T1276DAO.findPeriodoCierre... codUO: "+codUO);
		try {
			
			if (fecha.indexOf("/")>3){
				fecEval = fecha;	
			}else{
				fecEval = Utiles.toYYYYMMDD(fecha);	
			}
			strSQL.append("select  p.periodo, p.fcierre, p.est_id estado_p, pa.u_organ, pa.est_id estado_pa "
					).append( " from t1276periodo p, t1278periodo_area pa "
					).append( " where pa.u_organ = ? and "
					).append( " pa.periodo = p.periodo and "		
					).append( " p.finicio <= '").append(fecEval).append( "' and "
					).append( " p.ffin >= '").append( fecEval ).append( "' "); 
					//).append( " and p.periodo = pa.periodo ");
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codUO.trim());
			rs = pre.executeQuery();

			if (rs.next()) {
				periodo = new HashMap();
				periodo.put("periodo",rs.getString("periodo"));
				periodo.put("fcierre",rs.getString("fcierre"));
				periodo.put("estado_p",rs.getString("estado_p"));
				periodo.put("estado_pa",rs.getString("estado_pa"));
				periodo.put("u_organ",rs.getString("u_organ"));
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
		return periodo;

	}	

	
	 //ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII 
	/**
	 * Metodo encargado de buscar el periodo vigente de un colaborador CAS para la unidad
	 * organizacional y la fecha indicadas.
	 * @param dbpool String
     * @param fecha String
     * @param codUO String
     * @return periodo HashMap
	 * @throws RemoteException
	 */
	public HashMap findPeriodoCierreCAS(String dbpool, String fecha, String codUO) throws SQLException {

		HashMap periodo = null;
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String fecEval = "";
		log.debug("T1276DAO.findPeriodoCierreCAS... fecha: "+fecha);
		log.debug("T1276DAO.findPeriodoCierreCAS... codUO: "+codUO);
		try {
			
			log.debug("fecha.indexOf(/): "+fecha.indexOf("/"));
			if (fecha.indexOf("/")>3){				
				fecEval = fecha;	
				log.debug("fecha.indexOf(/)....fecEval: "+fecEval);
			}else{
				fecEval = Utiles.toYYYYMMDD(fecha);	
				log.debug("fecha.indexOf(/)<=3....fecEval: "+fecEval);
			}
			strSQL.append("select  p.periodo, p.fec_cierre_cas, p.est_id estado_p, pa.u_organ, pa.est_id estado_pa "
					).append( " from t1276periodo p, t1278periodo_area pa "
					).append( " where pa.u_organ = ? and "
					).append( " pa.periodo = p.periodo and "		
					).append( " p.fec_ini_cas <= '").append(fecEval).append( "' and "
					).append( " p.fec_fin_cas >= '").append( fecEval ).append( "' ");			
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codUO.trim());
			rs = pre.executeQuery();

			if (rs.next()) {
				periodo = new HashMap();
				periodo.put("periodo",rs.getString("periodo"));
				periodo.put("fcierre",rs.getString("fec_cierre_cas"));
				periodo.put("estado_p",rs.getString("estado_p"));
				periodo.put("estado_pa",rs.getString("estado_pa"));
				periodo.put("u_organ",rs.getString("u_organ"));
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
		return periodo;

	}	
	
	
	/**
	 * Metodo encargado de buscar el periodo vigente de un colaborador de la Modalidad Formativa para la unidad
	 * organizacional y la fecha indicadas.
	 * @param dbpool String
     * @param fecha String
     * @param codUO String
     * @return periodo HashMap
	 * @throws RemoteException
	 */
	public HashMap findPeriodoCierreFormativa(String dbpool, String fecha, String codUO) throws SQLException {

		HashMap periodo = null;
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		String fecEval = "";
		log.debug("T1276DAO.findPeriodoCierreFormativa... fecha: "+fecha);
		log.debug("T1276DAO.findPeriodoCierreFormativa... codUO: "+codUO);
		try {
			
			if (fecha.indexOf("/")>3){
				fecEval = fecha;	
			}else{
				fecEval = Utiles.toYYYYMMDD(fecha);	
			}
			strSQL.append("select  p.periodo, p.fec_cierre_mf, p.est_id estado_p, pa.u_organ, pa.est_id estado_pa "
					).append( " from t1276periodo p, t1278periodo_area pa "
					).append( " where pa.u_organ = ? and "
					).append( " pa.periodo = p.periodo and "		
					).append( " p.fec_ini_mf <= '").append(fecEval).append( "' and "
					).append( " p.fec_fin_mf >= '").append( fecEval ).append( "' ");				
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codUO.trim());
			rs = pre.executeQuery();

			if (rs.next()) {
				periodo = new HashMap();
				periodo.put("periodo",rs.getString("periodo"));
				periodo.put("fcierre",rs.getString("fec_cierre_mf"));
				periodo.put("estado_p",rs.getString("estado_p"));
				periodo.put("estado_pa",rs.getString("estado_pa"));
				periodo.put("u_organ",rs.getString("u_organ"));
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
		return periodo;

	}	
	//FIN ICAPUNAY 01/07/2011 FORMATIVAS-PARTEII 
	
	/**
	 * Metodo que actualiza el estado de los registros de area por periodo.
	 * @throws SQLException
	 */
	public void UpdateT1278EstIdByPeriodo(String dbpool, String periodo,
			String Estado) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		try {

			strSQL.append("update t1278periodo_area set est_id = ?"
					).append( " where periodo = ?");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Estado);
			pre.setString(2, periodo);
			pre.executeUpdate();

 
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
	}

	/**
	 * Metodo encargado de determinar si existen periodos cuya vigencia se
	 * encuentre dentro del rango de fechas indicado, filtrado opcionalmente por
	 * el codigo del periodo que no se desea considerar.
	 * @throws SQLException
	 */
	public boolean findByFecIniFecFinCodigo(String dbpool, String fecIni,
			String fecFin, String codigo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean existe = false;

		try {

			String fInvIni = Utiles.toYYYYMMDD(fecIni);
			String fInvFin = Utiles.toYYYYMMDD(fecFin);

			strSQL.append("select periodo, finicio, ffin, "
					).append( " est_id from t1276periodo"
					).append( " where est_id = ? and "
					).append( " ((('"
					).append( fInvIni
					).append( "' >= finicio and "
					).append( " '"
					).append( fInvIni
					).append( "' <= ffin ) or "
					).append( " ('"
					).append( fInvFin
					).append( "' >= finicio and "
					).append( " '"
					).append( fInvFin
					).append( "' <= ffin)) or "
					).append( " ((finicio >= '"
					).append( fInvIni
					).append( "' and "
					).append( " finicio <= '"
					).append( fInvFin
					).append( "') or "
					).append( " (ffin >= '"
					).append( fInvIni
					).append( "' and "
					).append( " ffin <= '"
					).append( fInvFin ).append( "')))");

			if (!codigo.trim().equals("")) {
				strSQL.append( " and periodo <> ?");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			if (!codigo.trim().equals("")) {
				pre.setString(2, codigo);
			}
			rs = pre.executeQuery();

			if (rs.next()) {
				existe = true;
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
		return existe;
	}
	
	//JVILLACORTA 16/06/2011 - AOM:MODALIDADES FORMATIVAS
	/**
	 * Metodo encargado de determinar si existen periodos cuya vigencia se
	 * encuentre dentro del rango de fechas indicado, filtrado opcionalmente por
	 * el codigo del periodo que no se desea considerar.
	 * @throws SQLException
	 */
	public boolean findByFecIniFecFinCodigoCAS(String dbpool, String fecIni,
			String fecFin, String codigo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean existe = false;

		try {

			String fInvIni = Utiles.toYYYYMMDD(fecIni);
			String fInvFin = Utiles.toYYYYMMDD(fecFin);

			strSQL.append("select periodo, finicio, ffin, "
					).append( " est_id from t1276periodo"
					).append( " where est_id = ? and "
					).append( " ((('"
					).append( fInvIni
					).append( "' >= fec_ini_cas and "
					).append( " '"
					).append( fInvIni
					).append( "' <= fec_fin_cas ) or "
					).append( " ('"
					).append( fInvFin
					).append( "' >= fec_ini_cas and "
					).append( " '"
					).append( fInvFin
					).append( "' <= fec_fin_cas)) or "
					).append( " ((fec_ini_cas >= '"
					).append( fInvIni
					).append( "' and "
					).append( " fec_ini_cas <= '"
					).append( fInvFin
					).append( "') or "
					).append( " (fec_fin_cas >= '"
					).append( fInvIni
					).append( "' and "
					).append( " fec_fin_cas <= '"
					).append( fInvFin ).append( "')))");

			if (!codigo.trim().equals("")) {
				strSQL.append( " and periodo <> ?");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			if (!codigo.trim().equals("")) {
				pre.setString(2, codigo);
			}
			rs = pre.executeQuery();

			if (rs.next()) {
				existe = true;
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
		return existe;
	}
	
	/**
	 * Metodo encargado de determinar si existen periodos cuya vigencia se
	 * encuentre dentro del rango de fechas indicado, filtrado opcionalmente por
	 * el codigo del periodo que no se desea considerar.
	 * @throws SQLException
	 */
	public boolean findByFecIniFecFinCodigoMF(String dbpool, String fecIni,
			String fecFin, String codigo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean existe = false;

		try {

			String fInvIni = Utiles.toYYYYMMDD(fecIni);
			String fInvFin = Utiles.toYYYYMMDD(fecFin);

			strSQL.append("select periodo, finicio, ffin, "
					).append( " est_id from t1276periodo"
					).append( " where est_id = ? and "
					).append( " ((('"
					).append( fInvIni
					).append( "' >= fec_ini_mf and "
					).append( " '"
					).append( fInvIni
					).append( "' <= fec_fin_mf ) or "
					).append( " ('"
					).append( fInvFin
					).append( "' >= fec_ini_mf and "
					).append( " '"
					).append( fInvFin
					).append( "' <= fec_fin_mf)) or "
					).append( " ((fec_ini_mf >= '"
					).append( fInvIni
					).append( "' and "
					).append( " fec_ini_mf <= '"
					).append( fInvFin
					).append( "') or "
					).append( " (fec_fin_mf >= '"
					).append( fInvIni
					).append( "' and "
					).append( " fec_fin_mf <= '"
					).append( fInvFin ).append( "')))");

			if (!codigo.trim().equals("")) {
				strSQL.append( " and periodo <> ?");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			if (!codigo.trim().equals("")) {
				pre.setString(2, codigo);
			}
			rs = pre.executeQuery();

			if (rs.next()) {
				existe = true;
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
		return existe;
	}
	//FIN - JVILLACORTA 16/06/2011 - AOM:MODALIDADES FORMATIVAS


	/**
	 * Metodo encargado de buscar el periodo vigente para una fecha indicada.
	 * @throws RemoteException
	 */

	public String findByFecha(String dbpool, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fecEval = Utiles.toYYYYMMDD(fecha);
		String periodo = ""; //Codigo del periodo

		try {

			strSQL.append("select 	periodo "
					).append( " from   t1276periodo "
					).append( " where  est_id = ? and "
					).append( " finicio <= '"
					).append( fecEval
					).append( "' and "
					).append( " ffin >= '"
					).append( fecEval ).append( "'");

			
			
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				periodo = rs.getString("periodo");
			}

 
		}

		catch (Exception e) {

			e.printStackTrace();
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
		return periodo;

	}
	
	
	//ICAPUNAY 21/06/2011 FORMATIVAS
	/**
	 * Metodo encargado de buscar el periodo vigente para los colaboradores del régimen CAS para una fecha indicada.
	 * @throws RemoteException
	 */
	public String findByFechaCAS(String dbpool, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fecEval = Utiles.toYYYYMMDD(fecha);
		String periodo = ""; //Codigo del periodo

		try {

			strSQL.append("select 	periodo "
					).append( " from   t1276periodo "
					).append( " where  est_id = ? and "
					).append( " fec_ini_cas <= '"
					).append( fecEval
					).append( "' and "
					).append( " fec_fin_cas >= '"
					).append( fecEval ).append( "'");

			
			
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				periodo = rs.getString("periodo");
			}

 
		}

		catch (Exception e) {

			e.printStackTrace();
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
		return periodo;

	}
	
	/**
	 * Metodo encargado de buscar el periodo vigente para los colaboradores de la modalidad Formativas para una fecha indicada.
	 * @throws RemoteException
	 */
	public String findByFechaModFormativas(String dbpool, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		String fecEval = Utiles.toYYYYMMDD(fecha);
		String periodo = ""; //Codigo del periodo

		try {

			strSQL.append("select 	periodo "
					).append( " from   t1276periodo "
					).append( " where  est_id = ? and "
					).append( " fec_ini_mf <= '"
					).append( fecEval
					).append( "' and "
					).append( " fec_fin_mf >= '"
					).append( fecEval ).append( "'");

			
			
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				periodo = rs.getString("periodo");
			}

 
		}

		catch (Exception e) {

			e.printStackTrace();
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
		return periodo;

	}	
	//FIN ICAPUNAY 21/06/2011 FORMATIVAS
	
	/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	/**
	 * Metodo encargado de listar los datos de los periodos cerrados filtrandolos por regimen/modalidad
	 * @throws SQLException
	 */
	public ArrayList findPeriodosCerradosByRegimen(String dbpool, String tipoRegimen, String fechaInicio)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList periodos = null;

		try {

			strSQL.append("select * from t1276periodo ");					
			
			if (!tipoRegimen.trim().equals("")) {
				
				if (tipoRegimen.trim().equals("0")) {//planilla
					//strSQL.append(" where TO_DATE(fcierre,'%d/%m/%Y') < TO_DATE(?,'%d/%m/%Y') ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
					strSQL.append(" where TO_DATE(fcierre,'%d/%m/%Y') <= TO_DATE(?,'%d/%m/%Y') ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
					strSQL.append(" and TO_DATE(finicio,'%Y/%m/%d') >= TO_DATE(?,'%Y/%m/%d') ");
				}
				if (tipoRegimen.trim().equals("1")) {//cas
					//strSQL.append(" where TO_DATE(fec_cierre_cas,'%d/%m/%Y') < TO_DATE(?,'%d/%m/%Y') ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
					strSQL.append(" where TO_DATE(fec_cierre_cas,'%d/%m/%Y') <= TO_DATE(?,'%d/%m/%Y') ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
					strSQL.append(" and TO_DATE(fec_ini_cas,'%Y/%m/%d') >= TO_DATE(?,'%Y/%m/%d') ");
				}
				if (tipoRegimen.trim().equals("2")) {//formativas
					//strSQL.append(" where TO_DATE(fec_cierre_mf,'%d/%m/%Y') < TO_DATE(?,'%d/%m/%Y') ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
					strSQL.append(" where TO_DATE(fec_cierre_mf,'%d/%m/%Y') <= TO_DATE(?,'%d/%m/%Y') ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
					strSQL.append(" and TO_DATE(fec_ini_mf,'%Y/%m/%d') >= TO_DATE(?,'%Y/%m/%d') ");
				}				
			}
			
			strSQL.append(" order by periodo desc ");
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, new FechaBean().getFormatDate("dd/MM/yyyy"));
			pre.setString(2, fechaInicio);
			rs = pre.executeQuery();
			
			periodos = new ArrayList();
			BeanPeriodo periodo = null;

			while (rs.next()) {

				periodo = new BeanPeriodo();

				periodo.setPeriodo(rs.getString("periodo"));
				periodo.setFechaIni(rs.getString("finicio"));
				periodo.setFechaFin(rs.getString("ffin"));
				periodo.setFechaCie(rs.getString("fcierre"));
				periodo.setEstId(rs.getString("est_id"));				
				periodo.setFechaCieCAS(rs.getString("fec_cierre_cas"));
				periodo.setFechaFinCAS(rs.getString("fec_fin_cas"));
				periodo.setFechaIniCAS(rs.getString("fec_ini_cas"));			
				periodo.setFechaCieModForm(rs.getString("fec_cierre_mf"));
				periodo.setFechaFinModForm(rs.getString("fec_fin_mf"));
				periodo.setFechaIniModForm(rs.getString("fec_ini_mf"));				
				periodos.add(periodo);
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
		return periodos;
	}
	
	/**
	 * Metodo encargado de listar los datos de los periodos cerrados filtrandolos por regimen/modalidad y por anio
	 * @throws SQLException
	 */
	public ArrayList findPeriodosCerradosByRegimenByAnio(String dbpool, String tipoRegimen, String anio)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList periodos = null;

		try {

			strSQL.append("select * from t1276periodo ");					
			
			if (!tipoRegimen.trim().equals("")) {
				
				if (tipoRegimen.trim().equals("0")) {//planilla
					//strSQL.append(" where (TO_DATE(fcierre,'%d/%m/%Y') < TO_DATE(?,'%d/%m/%Y')) ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy					
					strSQL.append(" where (TO_DATE(fcierre,'%d/%m/%Y') <= TO_DATE(?,'%d/%m/%Y')) ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
				}
				if (tipoRegimen.trim().equals("1")) {//cas
					//strSQL.append(" where (TO_DATE(fec_cierre_cas,'%d/%m/%Y') < TO_DATE(?,'%d/%m/%Y')) ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy				
					strSQL.append(" where (TO_DATE(fec_cierre_cas,'%d/%m/%Y') <= TO_DATE(?,'%d/%m/%Y')) ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
				}
				if (tipoRegimen.trim().equals("2")) {//formativas
					//strSQL.append(" where (TO_DATE(fec_cierre_mf,'%d/%m/%Y') < TO_DATE(?,'%d/%m/%Y')) ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy				
					strSQL.append(" where (TO_DATE(fec_cierre_mf,'%d/%m/%Y') <= TO_DATE(?,'%d/%m/%Y')) ");//ICAPUNAY 28/02/2012 visualizar periodos con fecha de cierre <= fecha hoy
				}				
			}			
			strSQL.append(" and (periodo like '").append(anio).append("%') "); //filtra cerrados segun anio
			strSQL.append(" order by periodo asc ");
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, new FechaBean().getFormatDate("dd/MM/yyyy").toString());
			rs = pre.executeQuery();
			
			periodos = new ArrayList();
			BeanPeriodo periodo = null;

			while (rs.next()) {
				periodo = new BeanPeriodo();
				periodo.setPeriodo(rs.getString("periodo"));
				periodo.setFechaIni(rs.getString("finicio"));
				periodo.setFechaFin(rs.getString("ffin"));
				periodo.setFechaCie(rs.getString("fcierre"));
				periodo.setEstId(rs.getString("est_id"));				
				periodo.setFechaCieCAS(rs.getString("fec_cierre_cas"));
				periodo.setFechaFinCAS(rs.getString("fec_fin_cas"));
				periodo.setFechaIniCAS(rs.getString("fec_ini_cas"));			
				periodo.setFechaCieModForm(rs.getString("fec_cierre_mf"));
				periodo.setFechaFinModForm(rs.getString("fec_fin_mf"));
				periodo.setFechaIniModForm(rs.getString("fec_ini_mf"));				
				periodos.add(periodo);
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
		return periodos;
	}	
	/*ICAPUNAY - AOM URGENTE 46U3T10 REPORTES DE GESTION DE ASISTENCIA - 22/08/2011 */
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	/**
	 * Metodo encargado de determinar si un periodo esta cerrado para regimen 276-728
	 * @throws SQLException
	 */
	public boolean isPeriodoCerrado(Map datos) throws SQLException {

		boolean cerrado = false;
		
		try {			
			BeanPeriodo bean = findByCodigo(datos.get("dbpool").toString(), datos.get("periodo").toString());          
            log.debug("Fecha cierre regimen 276-728: "+ bean.getFechaCie());
         
            float dif=-1;
            if (bean.getFechaCie()!=null && bean.getFechaCie().trim()!="") 
            	dif = Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(), bean.getFechaCie());           
            else cerrado = true;
            	
            if (dif < 0) {
            	log.debug("dif < 0");
                cerrado = true;
            }
            log.debug("cerrado: "+ cerrado);
		}
		catch (Exception e) {
			log.error("**** SQL ERROR isPeriodoCerrado()****", e);
			throw new SQLException(e.toString());
		} finally {
			try {
				
			} catch (Exception e) {
				
			}			
		}
		return cerrado;
	}
	
	/**
	 * Metodo encargado de determinar si un periodo esta cerrado para regimen CAS
	 * @throws SQLException
	 */
	public boolean isPeriodoCerradoCAS(Map datos) throws SQLException {

		boolean cerrado = false;
		
		try {			
			BeanPeriodo bean = findByCodigo(datos.get("dbpool").toString(), datos.get("periodo").toString());          
            log.debug("Fecha cierre regimen CAS: "+ bean.getFechaCieCAS());
         
            float dif=-1;
            if (bean.getFechaCieCAS()!=null && bean.getFechaCieCAS().trim()!="") 
            	dif = Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(), bean.getFechaCieCAS());           
            else cerrado = true;
            	
            if (dif < 0) {
            	log.debug("dif < 0");
                cerrado = true;
            }
            log.debug("cerrado: "+ cerrado);
		}
		catch (Exception e) {
			log.error("**** SQL ERROR isPeriodoCerradoCAS()****", e);
			throw new SQLException(e.toString());
		} finally {
			try {
				
			} catch (Exception e) {
				
			}			
		}
		return cerrado;
	}
	
	/**
	 * Metodo encargado de determinar si un periodo esta cerrado para modalidad formativa
	 * @throws SQLException
	 */
	public boolean isPeriodoCerradoModFormativa(Map datos) throws SQLException {

		boolean cerrado = false;
		
		try {			
			BeanPeriodo bean = findByCodigo(datos.get("dbpool").toString(), datos.get("periodo").toString());          
            log.debug("Fecha cierre modalidad formativa: "+ bean.getFechaCieModForm());
         
            float dif=-1;
            if (bean.getFechaCieModForm()!=null && bean.getFechaCieModForm().trim()!="") 
            	dif = Utiles.obtenerDiasDiferencia(Utiles.obtenerFechaActual(), bean.getFechaCieModForm());           
            else cerrado = true;
            	
            if (dif < 0) {
            	log.debug("dif < 0");
                cerrado = true;
            }
            log.debug("cerrado: "+ cerrado);
		}
		catch (Exception e) {
			log.error("**** SQL ERROR isPeriodoCerradoModFormativa()****", e);
			throw new SQLException(e.toString());
		} finally {
			try {
				
			} catch (Exception e) {
				
			}			
		}
		return cerrado;
	}
	//FIN ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados

}