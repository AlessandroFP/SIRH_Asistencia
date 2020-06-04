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
import pe.gob.sunat.sp.asistencia.bean.BeanTipoMovimiento;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1279
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1279Tipo_Mov
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

public class T1279DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1279DAO.class);

	public T1279DAO() {
	}

	/**
	 * Metodo encargado de buscar los datos del movimiento cuyo codigo sea el
	 * indicado por el parametro
	 * @throws SQLException
	 */
	public HashMap findByMov(String dbpool, String mov) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap movimiento = new HashMap();

		try {

			strSQL.append(" select tm.mov, tm.descrip, tm.califica, tm.medida, tm.tipo_id, "
					).append( " tm.sentr_id, tm.srefr_id, tm.sali_id, tm.qvalida, tm.ind_dias "
					).append( " from t1279tipo_mov tm "
					).append( " where tm.est_id = ? and tm.mov = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, mov);
			rs = pre.executeQuery();

			if (rs.next()) {

				movimiento.put("mov", rs.getString("mov") != null ? rs
						.getString("mov").trim() : "");
				movimiento.put("descrip", rs.getString("descrip") != null ? rs
						.getString("descrip").trim() : "");
				movimiento.put("califica",
						rs.getString("califica") != null ? rs.getString(
								"califica").trim() : Constantes.SIN_DESCUENTO);
				movimiento.put("tipo_id", rs.getString("tipo_id") != null ? rs
						.getString("tipo_id").trim() : "");
				movimiento.put("medida", rs.getString("medida") != null ? rs
						.getString("medida").trim() : Constantes.HORA);
				movimiento.put("entrada", rs.getString("sentr_id") != null ? rs
						.getString("sentr_id").trim() : "0");
				movimiento.put("refrigerio",
						rs.getString("srefr_id") != null ? rs.getString(
								"srefr_id").trim() : "0");
				movimiento.put("salida", rs.getString("sali_id") != null ? rs
						.getString("sali_id").trim() : "0");
				movimiento.put("qvalida", rs.getString("qvalida") != null ? rs
						.getString("qvalida") : "0");
				movimiento.put("ind_dias", rs.getString("ind_dias") != null ? rs
						.getString("ind_dias") : "0");

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
		return movimiento;
	}

	/**
	 * Metodo encargado de obtener los datos de los tipos de movimiento que
	 * cumplan con los filtros de estado y tipo indicado. Join con la tabla
	 * T99Codigos.
	 * @throws SQLException
	 */
	public ArrayList joinWithT99T99ByEstadoTipo(String dbpool, String estado,
			String tipo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		try {

			strSQL.append(" select tm.mov, tm.descrip, tm.califica, tm.medida, "
					).append( " tm.est_id, tm.tipo_id, tm.ffini, tm.ffin, "
					).append( " tm.ssoli_id, tm.srrhh_id, tm.sproc_id, tm.sentr_id, tm.srefr_id, tm.sali_id, "
					).append( " tm.dias_antes, tm.dias_despues, tm.dias_acum, tm.oblig_id,  "
					).append( " tm.qvalida, tm.ind_dias, tm.ind_proc, m.t99descrip, t.t99abrev "
					).append( " from t1279tipo_mov tm, t99codigos m, t99codigos t "
					).append( " where m.t99tip_desc = ? " ).append( " and m.t99cod_tab = ? "
					).append( " and t.t99tip_desc = ? " ).append( " and t.t99cod_tab = ? "
					).append( " and tm.est_id = ? ");

			if (!tipo.trim().equals("-1")) {
				strSQL.append( " and tm.tipo_id = ? ");
			}

			strSQL.append( " and m.t99codigo[1,2] = tm.medida "
					).append( " and t.t99codigo[1,2] = tm.tipo_id " ).append( " order by 2 ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.T99DETALLE);
			pre.setString(2, Constantes.CODTAB_MEDIDA);
			pre.setString(3, Constantes.T99DETALLE);
			pre.setString(4, Constantes.CODTAB_TIPOMOV);
			pre.setString(5, estado);
			if (!tipo.trim().equals("-1")) {
				pre.setString(6, tipo.trim());
			}

			rs = pre.executeQuery();
			movimientos = new ArrayList();
			BeanTipoMovimiento movimiento = null;
			while (rs.next()) {

				movimiento = new BeanTipoMovimiento();

				movimiento.setMov(rs.getString("mov"));
				movimiento.setDescrip(rs.getString("descrip").trim());
				movimiento.setCalifica(rs.getString("califica"));
				movimiento.setMedida(rs.getString("medida"));
				movimiento.setFechaIni(rs.getTimestamp("ffini"));
				movimiento.setFechaFin(rs.getTimestamp("ffin"));
				movimiento.setTipoId(rs.getString("tipo_id"));
				movimiento.setEstId(rs.getString("est_id"));
				movimiento.setDescMedida(rs.getString("t99descrip").trim());
				movimiento.setAbrevTipo(rs.getString("t99abrev"));
				movimiento.setSolicitud(rs.getString("ssoli_id"));
				movimiento.setAutRH(rs.getString("srrhh_id"));
				movimiento.setProcAsistencia(rs.getString("sproc_id"));
				movimiento.setAsisEntrada(rs.getString("sentr_id"));
				movimiento.setAsisRefrigerio(rs.getString("srefr_id"));
				movimiento.setAsisSalida(rs.getString("sali_id"));
				movimiento.setDiasAntes(rs.getInt("dias_antes"));
				movimiento.setDiasDespues(rs.getInt("dias_despues"));
				movimiento.setDiasAcum(rs.getInt("dias_acum"));
				movimiento.setObligId(rs.getString("oblig_id"));
				movimiento.setQValida(rs.getInt("qvalida"));
				movimiento.setIndDias(rs.getString("ind_dias")!=null?rs.getString("ind_dias"):"0");
				movimiento.setIndProc(rs.getString("ind_proc")!=null?rs.getString("ind_proc"):"1");

				movimientos.add(movimiento);
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
		return movimientos;
	}

	/**
	 * Metodo encargado de buscar los datos de los tipos de movimiento filtrados
	 * por un tipo, criterio con un valor determinado. Join con la tabla
	 * T99Codigos.
	 * @throws SQLException
	 */
	public ArrayList joinWithT99T99ByCritValTipo(String dbpool,
			String criterio, String valor, String tipo) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		String fecCompara = "";

		try {
			strSQL.append("select tm.mov, tm.descrip, tm.califica, tm.medida, "
					).append( " tm.est_id, tm.tipo_id, tm.ffini, tm.ffin, "
					).append( " tm.ssoli_id, tm.srrhh_id, tm.sproc_id, tm.sentr_id, tm.srefr_id, tm.sali_id , "
					).append( " tm.dias_antes, tm.dias_despues, tm.dias_acum, tm.oblig_id, "
					).append( " tm.qvalida, tm.ind_dias, tm.ind_proc, m.t99descrip, t.t99abrev "
					).append( " from t1279tipo_mov tm, t99codigos m, t99codigos t "
					).append( " where m.t99tip_desc = ? " ).append( " and m.t99cod_tab = ? "
					).append( " and t.t99tip_desc = ? " ).append( " and t.t99cod_tab = ? "
					).append( " and tm.est_id = ? ");

			if (!tipo.trim().equals("-1")) {
				strSQL.append( " and tm.tipo_id = ? ");
			}

			boolean agrega = false;

			if (!valor.trim().equals("")) {
				//busqueda por codigo de movimiento
				if (criterio.equals("0")) {
					strSQL.append( " and tm.mov like '" ).append( valor.trim().toUpperCase()
							).append( "%' ");
					agrega = true;
				}

				//busqueda por descripcion del movimiento
				if (criterio.equals("1")) {
					strSQL.append( " and upper(tm.descrip) like '%"
							).append( valor.trim().toUpperCase() ).append( "%' ");
					agrega = true;
				}

				if (criterio.equals("2") || criterio.equals("3")) {
					fecCompara = Utiles.toYYYYMMDD(valor);
				}

				if (criterio.trim().equals("4")) {
					strSQL.append( " and upper(m.t99descrip) like '%"
							).append( valor.trim().toUpperCase() ).append( "%'");
				}

				if (criterio.trim().equals("5")) {
					strSQL.append( " and upper(tm.califica) = '"
							).append( valor.trim().toUpperCase() ).append( "'");
				}
			}

			strSQL.append( " and t.t99codigo[1,2] = tm.tipo_id "
					).append( " and m.t99codigo[1,2] = tm.medida " ).append( " order by 2 ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.T99DETALLE);
			pre.setString(2, Constantes.CODTAB_MEDIDA);
			pre.setString(3, Constantes.T99DETALLE);
			pre.setString(4, Constantes.CODTAB_TIPOMOV);
			pre.setString(5, Constantes.ACTIVO);
			if (!tipo.trim().equals("-1")) {
				pre.setString(6, tipo.trim());
			}

			rs = pre.executeQuery();
			movimientos = new ArrayList();

			String fechaInicio;
			String fechaFinal;
			BeanTipoMovimiento movimiento = null;
			while (rs.next()) {
				agrega = true;
				if (criterio.equals("2")) {
					fechaInicio = Utiles.toYYYYMMDD(Utiles.timeToFecha(rs
							.getTimestamp("ffini")));

					agrega = (fecCompara.equals("") || (fecCompara
							.compareTo(fechaInicio) <= 0));
				}

				if (criterio.equals("3")) {
					fechaFinal = Utiles.toYYYYMMDD(Utiles.timeToFecha(rs
							.getTimestamp("ffin")));

					agrega = (fecCompara.equals("") || (fecCompara
							.compareTo(fechaFinal) >= 0));
				}

				if (agrega) {
					movimiento = new BeanTipoMovimiento();

					movimiento.setMov(rs.getString("mov"));
					movimiento.setDescrip(rs.getString("descrip").trim());
					movimiento.setCalifica(rs.getString("califica"));
					movimiento.setMedida(rs.getString("medida"));
					movimiento.setFechaIni(rs.getTimestamp("ffini"));
					movimiento.setFechaFin(rs.getTimestamp("ffin"));
					movimiento.setTipoId(rs.getString("tipo_id"));
					movimiento.setEstId(rs.getString("est_id"));
					movimiento.setDescMedida(rs.getString("t99descrip").trim());
					movimiento.setAbrevTipo(rs.getString("t99abrev"));
					movimiento.setSolicitud(rs.getString("ssoli_id"));
					movimiento.setAutRH(rs.getString("srrhh_id"));
					movimiento.setProcAsistencia(rs.getString("sproc_id"));
					movimiento.setAsisEntrada(rs.getString("sentr_id"));
					movimiento.setAsisRefrigerio(rs.getString("srefr_id"));
					movimiento.setAsisSalida(rs.getString("sali_id"));
					movimiento.setDiasAntes(rs.getInt("dias_antes"));
					movimiento.setDiasDespues(rs.getInt("dias_despues"));
					movimiento.setDiasAcum(rs.getInt("dias_acum"));
					movimiento.setObligId(rs.getString("oblig_id"));
					movimiento.setQValida(rs.getInt("qvalida"));
					movimiento.setIndDias(rs.getString("ind_dias")!=null?rs.getString("ind_dias"):"0");
					movimiento.setIndProc(rs.getString("ind_proc")!=null?rs.getString("ind_proc"):"1");

					movimientos.add(movimiento);
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
		return movimientos;
	}

	/**
	 * M�todo encargado de buscar los datos de los tipos de movimiento cuyo
	 * estado corresponda al par�metro recibido.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param estado
	 *            String. Estado de los registros deseados.
	 * @return Arraylist conteniendo los datos de los tipos de movimiento en
	 *         beans de la clase BeanTipoMovimiento.
	 * @throws SQLException
	 */
	public ArrayList findByEstado(String dbpool, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		try {

			strSQL.append(" select tm.mov, tm.descrip, tm.tipo_id "
					).append( " from t1279tipo_mov tm " ).append( " where tm.est_id = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);

			rs = pre.executeQuery();
			movimientos = new ArrayList();
			BeanTipoMovimiento movimiento = null;
			while (rs.next()) {

				movimiento = new BeanTipoMovimiento();

				movimiento.setMov(rs.getString("mov").trim());
				movimiento.setDescrip(rs.getString("descrip").trim());
				movimiento.setTipoId(rs.getString("tipo_id").trim());
				movimiento.setTotal(0);

				movimientos.add(movimiento);
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
		return movimientos;
	}

	/**
	 * M�todo encargado de buscar los movimientos de acuerdo al estado de los
	 * registros.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param estado
	 *            String. Estado de los registros deseados.
	 * @return HashMap conteniendo los datos de los tipos de movimiento en
	 *         HashMaps.
	 * @throws SQLException
	 */
	public HashMap findMapByEstado(String dbpool, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap movimientos = null;

		try {

			strSQL.append(" select tm.mov, tm.descrip, tm.califica, tm.medida, tm.tipo_id, "
					).append( " tm.sentr_id, tm.srefr_id, tm.sali_id "
					).append( " from t1279tipo_mov tm " ).append( " where tm.est_id = ? ");

			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);
			rs = pre.executeQuery();

			movimientos = new HashMap();
			HashMap movimiento = null;
			while (rs.next()) {

				movimiento = new HashMap();

				movimiento.put("mov", rs.getString("mov") != null ? rs
						.getString("mov").trim() : "");
				movimiento.put("descrip", rs.getString("descrip") != null ? rs
						.getString("descrip").trim() : "");
				movimiento.put("califica",
						rs.getString("califica") != null ? rs.getString(
								"califica").trim() : Constantes.SIN_DESCUENTO);
				movimiento.put("tipo_id", rs.getString("tipo_id") != null ? rs
						.getString("tipo_id").trim() : "");
				movimiento.put("medida", rs.getString("medida") != null ? rs
						.getString("medida").trim() : Constantes.HORA);
				movimiento.put("entrada", rs.getString("sentr_id") != null ? rs
						.getString("sentr_id").trim() : "0");
				movimiento.put("refrigerio",
						rs.getString("srefr_id") != null ? rs.getString(
								"srefr_id").trim() : "0");
				movimiento.put("salida", rs.getString("sali_id") != null ? rs
						.getString("sali_id").trim() : "0");
				movimiento.put("total", new Float(0));

				movimientos.put(rs.getString("mov").trim(), movimiento);
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
		return movimientos;
	}

	/**
	 * M�todo encargado de buscar los datos de tipos de movimientos cuyo rango
	 * de vigencia se encuentre entre las fechas indicadas por los par�metros
	 * correspondientes.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param tipo
	 *            String. C�digo de los tipos de movimiento buscados.
	 * @param estado
	 *            String. Estado de los registros.
	 * @param fecha1
	 *            String. Fecha inferior del rango de b�squeda.
	 * @param fecha2
	 *            String. Fecha superior del rango de b�squeda.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTipoMovimiento.
	 * @throws SQLException
	 */
	public ArrayList findByTipoIdEstIdFecIniFecFin(String dbpool, String tipo,
			String estado, String fecha1, String fecha2) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		try {

			strSQL.append("select mov, descrip, ffini, ffin, tipo_id, srefr_id "
					).append( " from t1279Tipo_mov " ).append( " where est_id = ? ");

			if (!tipo.trim().equals("")) {
				strSQL.append( "and tipo_id = ? ");
			}

			strSQL.append( " order by tipo_id, 2 ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);
			if (!tipo.trim().equals("")) {
				pre.setString(2, tipo);
			}

			rs = pre.executeQuery();

			movimientos = new ArrayList();
			boolean agregar = false;

			Timestamp fecIni = null;
			Timestamp fecFin = null;
			Timestamp fIni = null;
			Timestamp fFin = null;

			if (!fecha1.trim().equals("") && fecha2.trim().equals("")) {
				fecha2 = fecha1;
			}

			if (!fecha1.trim().equals("") && !fecha2.trim().equals("")) {
				fecIni = Utiles.stringToTimestamp(fecha1 + " 00:00:00");
				fecFin = Utiles.stringToTimestamp(fecha2 + " 00:00:00");
			}

			BeanTipoMovimiento mvto = null;
			while (rs.next()) {
				if (fecIni != null) {
					if ((rs.getDate("ffini") != null && rs.getDate("ffin") != null)
							&& (!rs.getString("ffini").trim().equals("") && !rs
									.getString("ffin").trim().equals(""))) {

						fIni = new Timestamp(rs.getDate("ffini").getTime());
						fFin = new Timestamp(rs.getDate("ffin").getTime());

						agregar = (!(fecIni.before(fIni) || fecIni.after(fFin)) && !(fecFin
								.before(fIni) || fecFin.after(fFin)));
					} else {
						agregar = true;
					}
				} else {
					agregar = true;
				}

				if (agregar) {

					mvto = new BeanTipoMovimiento();

					mvto.setMov(rs.getString("mov"));
					mvto.setDescrip(rs.getString("descrip"));
					mvto.setAsisRefrigerio(rs.getString("srefr_id"));

					movimientos.add(mvto);
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
		return movimientos;
	}

	/**
	 * M�todo encargado de buscar los datos de los tipos de movimiento filtrados
	 * por su c�digo de tipo y estado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param tipo
	 *            String. C�digo de los tipos de movimiento buscados.
	 * @param estado
	 *            String Estado de los registros deseados.
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findByTipoEstado(String dbpool, String tipo, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		try {

			strSQL.append(" select tm.mov, tm.descrip " ).append( " from t1279tipo_mov tm "
					).append( " where tm.est_id = ? and tm.tipo_id = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);
			pre.setString(2, tipo);

			rs = pre.executeQuery();
			movimientos = new ArrayList();
			HashMap movimiento = null;
			while (rs.next()) {

				movimiento = new HashMap();

				movimiento.put("mov", rs.getString("mov").trim());
				movimiento.put("descrip", rs.getString("descrip").trim());

				movimientos.add(movimiento);
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
		return movimientos;
	}

	/**
	 * Metodo encargado de buscar los tipos de movimiento para los cuales se
	 * puede generar una solicitud.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param estado
	 *            String Estado de los registros deseados.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTipoMovimiento.
	 * @throws SQLException
	 */
	public ArrayList findBySoliId(String dbpool, String estado)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		try {

			strSQL.append(" select tm.mov, tm.descrip, tm.tipo_id "
					).append( " from t1279tipo_mov tm "
					).append( " where tm.est_id = ? and tm.ssoli_id = ? order by tm.tipo_id desc, tm.descrip ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, estado);

			rs = pre.executeQuery();
			movimientos = new ArrayList();
			HashMap movimiento = null;

			while (rs.next()) {

				movimiento = new HashMap();

				movimiento.put("mov", rs.getString("mov").trim());
				movimiento.put("descrip", rs.getString("descrip").trim());

				movimientos.add(movimiento);
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
		return movimientos;
	}

	/**
	 * M�todo encargado de verificar si el movimiento es del tipo indicado en el
	 * par�metro correspondiente
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param mov
	 *            String. C�digo de movimiento evaluado.
	 * @param flag
	 *            String. Tipo de flag a evaluar.
	 * @param incOtros
	 *            Boolean. Indica si se desea incluir los movimientos que no
	 *            tienen flags marcados.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTipoMovimiento.
	 * @throws SQLException
	 */
	public boolean findByFlags(String dbpool, String mov, String flag,
			boolean incOtros) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean res = false;

		try {

			strSQL.append(" select mov " ).append( " from t1279tipo_mov "
					).append( " where mov = ? and est_id = ? ");

			if (flag.trim().equals("0")) {
				//Movimiento de entrada
				strSQL.append( " and (sentr_id = ? ");

				if (incOtros) {
					strSQL.append( " or (sentr_id != ? and srefr_id != ? and sali_id != ?)");
				}

				strSQL.append( ")");
			} else if (flag.trim().equals("1")) {
				//Movimiento de refrigerio
				strSQL.append( " and (srefr_id = ? ");

				if (incOtros) {
					strSQL.append( " or (sentr_id != ? and srefr_id != ? and sali_id != ?)");
				}

				strSQL.append( ")");

			} else if (flag.trim().equals("2")) {
				//Movimiento de salida
				strSQL.append( " and (sali_id = ? ");

				if (incOtros) {
					strSQL.append( " or (sentr_id != ? and srefr_id != ? and sali_id != ?)");
				}

				strSQL.append( ")");

			} else if (flag.trim().equals("3")) {
				//Otros movimientos
				strSQL.append( " and (sentr_id != ? and sali_id != ?)");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, mov);
			pre.setString(2, Constantes.ACTIVO);
			pre.setString(3, Constantes.ACTIVO);
			if (incOtros && !flag.trim().equals("3")) {
				pre.setString(4, Constantes.ACTIVO);
				pre.setString(5, Constantes.ACTIVO);
				pre.setString(6, Constantes.ACTIVO);
			} else {
				if (flag.trim().equals("3")) {
					pre.setString(4, Constantes.ACTIVO);
				}
			}

			rs = pre.executeQuery();

			if (rs.next()) {
				res = true;
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
		return res;
	}

	/**
	 * M�todo encargado de devolver la cantidad de dias antes, despues o
	 * acumulados de un tipo de movimiento
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param mov
	 *            String. C�digo de movimiento evaluado.
	 * @param tipo
	 *            String. Tipo de dato a devolver
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTipoMovimiento.
	 * @throws SQLException
	 */
	public int findNumDiasByTipo(String dbpool, String mov, String tipo)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int num = 0;

		try {

			strSQL.append(" select mov, dias_antes, dias_despues, dias_acum "
					).append( " from t1279tipo_mov " ).append( " where mov = ? and est_id = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, mov);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {

				//dias antes
				if (tipo.equals("1")) {
					num = rs.getInt("dias_antes");
				}
				//dias despues
				if (tipo.equals("2")) {
					num = rs.getInt("dias_despues");
				}
				//dias acum
				if (tipo.equals("3")) {
					num = rs.getInt("dias_acum");
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
	 * M�todo encargado de buscar los datos de tipos de movimientos cuyo rango
	 * de vigencia se encuentre entre las fechas indicadas por los par�metros
	 * correspondientes.
	 * Segun el Perfil
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param tipo
	 *            String. C�digo de los tipos de movimiento buscados.
	 * @param estado
	 *            String. Estado de los registros.
	 * @param fecha1
	 *            String. Fecha inferior del rango de b�squeda.
	 * @param fecha2
	 *            String. Fecha superior del rango de b�squeda.
	 * @param seguridad
	 * 				HashMap Roles.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTipoMovimiento.
	 * @throws SQLException
	 */
	public ArrayList findByTipoIdEstIdFecIniFecFinPerfil(String dbpool, String tipo,
			String estado, String fecha1, String fecha2, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		try {

			strSQL.append("select mov, descrip, ffini, ffin, tipo_id, srefr_id "
			//).append( " from t1279Tipo_mov, t99codigos " ).append( " where est_id = ? ");//ICAPUNAY - FORMATIVAS
			).append( " from t1279Tipo_mov, t99codigos " ).append( " where est_id = ? and mov not in (?,?) ");//ICAPUNAY - FORMATIVAS
			strSQL.append("  and t99tip_desc = 'D' and mov = t99codigo  and t99estado = '1' ");
			//criterios de visibilidad
			if (seguridad != null) {
				
				HashMap roles = (HashMap) seguridad.get("roles");
				log.debug("ROLES "+ roles);

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and t99cod_tab = '514' and t99descrip='C' ");
					
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and t99cod_tab = '517' and t99descrip='O' ");
					
				} else if (roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and t99cod_tab = '516' and t99descrip='J' ");
				} else {
					strSQL.append(" and t99cod_tab = '515' and t99descrip='E' ");
				}
			}

			strSQL.append( " order by tipo_id, 2 ");

			log.debug("SQL "+ strSQL.toString());
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, estado);
			pre.setString(2, Constantes.MOV_PERMISO_SUBVENCIONADO);//ICAPUNAY - FORMATIVAS
			pre.setString(3, Constantes.MOV_PERMISO_NO_SUBVENCIONADO);//ICAPUNAY - FORMATIVAS

			rs = pre.executeQuery();

			movimientos = new ArrayList();
			boolean agregar = false;

			Timestamp fecIni = null;
			Timestamp fecFin = null;
			Timestamp fIni = null;
			Timestamp fFin = null;

			if (!fecha1.trim().equals("") && fecha2.trim().equals("")) {
				fecha2 = fecha1;
			}

			if (!fecha1.trim().equals("") && !fecha2.trim().equals("")) {
				fecIni = Utiles.stringToTimestamp(fecha1 + " 00:00:00");
				fecFin = Utiles.stringToTimestamp(fecha2 + " 00:00:00");
			}

			BeanTipoMovimiento mvto = null;
			while (rs.next()) {
				if (fecIni != null) {
					if ((rs.getDate("ffini") != null && rs.getDate("ffin") != null)
							&& (!rs.getString("ffini").trim().equals("") && !rs
									.getString("ffin").trim().equals(""))) {

						fIni = new Timestamp(rs.getDate("ffini").getTime());
						fFin = new Timestamp(rs.getDate("ffin").getTime());

						agregar = (!(fecIni.before(fIni) || fecIni.after(fFin)) && !(fecFin
								.before(fIni) || fecFin.after(fFin)));
					} else {
						agregar = true;
					}
				} else {
					agregar = true;
				}

				if (agregar) {

					mvto = new BeanTipoMovimiento();

					mvto.setMov(rs.getString("mov"));
					mvto.setDescrip(rs.getString("descrip"));
					mvto.setAsisRefrigerio(rs.getString("srefr_id"));

					movimientos.add(mvto);
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
		return movimientos;
	}
	
	//ICAPUNAY - FORMATIVAS
	/**
	 * M�todo encargado de buscar los datos de tipos de movimientos de asistencia de modalidades formativas cuyo rango
	 * de vigencia se encuentre entre las fechas indicadas por los parametros
	 * correspondientes.
	 * Segun el Perfil
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param tipo
	 *            String. C�digo de los tipos de movimiento buscados.
	 * @param estado
	 *            String. Estado de los registros.
	 * @param fecha1
	 *            String. Fecha inferior del rango de b�squeda.
	 * @param fecha2
	 *            String. Fecha superior del rango de b�squeda.
	 * @param seguridad
	 * 				HashMap Roles.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanTipoMovimiento.
	 * @throws SQLException
	 */
	public ArrayList findByTipoIdEstIdFecIniFecFinFormativasPerfil(String dbpool, String tipo,
			String estado, String fecha1, String fecha2, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList movimientos = null;

		try {

			strSQL.append("select mov, descrip, ffini, ffin, tipo_id, srefr_id "
					).append( " from t1279Tipo_mov, t99codigos " ).append( " where tipo_id=? and est_id=? and mov in (?,?) ");
			strSQL.append("  and t99tip_desc = 'D' and mov = t99codigo  and t99estado = '1' ");
			//criterios de visibilidad
			if (seguridad != null) {				
				HashMap roles = (HashMap) seguridad.get("roles");
				log.debug("ROLES: "+ roles);

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and t99cod_tab = '514' and t99descrip='C' ");
					
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and t99cod_tab = '517' and t99descrip='O' ");
					
				} else if (roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and t99cod_tab = '516' and t99descrip='J' ");
				} 
			}

			strSQL.append( " order by tipo_id, 2 ");

			log.debug("SQL "+ strSQL.toString());
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, tipo);
			pre.setString(2, estado);
			pre.setString(3, Constantes.MOV_PERMISO_SUBVENCIONADO);
			pre.setString(4, Constantes.MOV_PERMISO_NO_SUBVENCIONADO);

			rs = pre.executeQuery();

			movimientos = new ArrayList();
			boolean agregar = false;

			Timestamp fecIni = null;
			Timestamp fecFin = null;
			Timestamp fIni = null;
			Timestamp fFin = null;

			if (!fecha1.trim().equals("") && fecha2.trim().equals("")) {
				fecha2 = fecha1;
			}

			if (!fecha1.trim().equals("") && !fecha2.trim().equals("")) {
				fecIni = Utiles.stringToTimestamp(fecha1 + " 00:00:00");
				fecFin = Utiles.stringToTimestamp(fecha2 + " 00:00:00");
			}

			BeanTipoMovimiento mvto = null;
			while (rs.next()) {
				if (fecIni != null) {
					if ((rs.getDate("ffini") != null && rs.getDate("ffin") != null)
							&& (!rs.getString("ffini").trim().equals("") && !rs
									.getString("ffin").trim().equals(""))) {

						fIni = new Timestamp(rs.getDate("ffini").getTime());
						fFin = new Timestamp(rs.getDate("ffin").getTime());

						agregar = (!(fecIni.before(fIni) || fecIni.after(fFin)) && !(fecFin
								.before(fIni) || fecFin.after(fFin)));
					} else {
						agregar = true;
					}
				} else {
					agregar = true;
				}

				if (agregar) {

					mvto = new BeanTipoMovimiento();

					mvto.setMov(rs.getString("mov"));
					mvto.setDescrip(rs.getString("descrip"));
					mvto.setAsisRefrigerio(rs.getString("srefr_id"));

					movimientos.add(mvto);
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
		return movimientos;
	}
	//FIN ICAPUNAY - FORMATIVAS


}