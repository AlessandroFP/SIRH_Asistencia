package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.rrhh.asistencia.dao.T1271DAO;
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanReporte;
import pe.gob.sunat.sp.asistencia.bean.BeanTurnoTrabajo;
import pe.gob.sunat.sp.asistencia.bean.BeanVacacion;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;
import pe.gob.sunat.utils.Utilidades;
import pe.gob.sunat.sp.asistencia.dao.T1270DAO; //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
import pe.gob.sunat.sp.dao.T02DAO; //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
import pe.gob.sunat.sp.asistencia.dao.T1276DAO; //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados

/**
 * <p>
 * Title: ReporteDAO
 * </p>
 * Description: Clase encargada de administrar las consultas para la realizacion
 * de reportes
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

public class ReporteDAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(ReporteDAO.class);
	//JVV - ini
	Propiedades constantes = new Propiedades(getClass(),"/constantes.properties");
	//JVV - fin
	public ReporteDAO() {
	}

	/**
	 * Metodo encargado de buscar los datos de uno o varios trabajadores
	 * filtrados por un criterio con un valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanReporte.
	 * @throws SQLException
	 */
	public ArrayList findPersonalByCriterioValor(String dbpool,
			String criterio, String valor) throws SQLException {

		
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {

			strSQL.append("select t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorg, t02ap_pate, ");
			strSQL.append("       t02ap_mate, t02nombres, t02f_ingsun ");
			strSQL.append(" from  t02perdp " + " where t02cod_stat = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and t02cod_pers = '").append(valor.trim().toUpperCase()).append("'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = '")
				.append(valor.trim().toUpperCase()).append("'");
			}

			strSQL.append(" order by t02ap_pate ");

			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = rs.getString("t02cod_pers").trim() + " - ";
				texto += rs.getString("t02ap_pate").trim() + " "
						+ rs.getString("t02ap_mate").trim() + ", "
						+ rs.getString("t02nombres").trim();
				texto += " ("
						+ Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))
						+ ")";

				a.setCodigo(rs.getString("t02cod_pers"));
				a.setUnidad(rs.getString("cod_uorg"));
				a.setNombre(texto);

				personal.add(a);
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
		return personal;
	}

	/**
	 * Metodo encargado de buscar los datos de uno o varios trabajadores
	 * filtrados por un criterio con un valor determinado, ordenados por numero
	 * de registro del trabajador.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanReporte.
	 * @throws SQLException
	 */
	public ArrayList findPersonalByCriterioValorOrderByCodPers(String dbpool,
			String criterio, String valor) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {

			strSQL.append("select  t02cod_pers, substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) cod_uorg, t02ap_pate, ")
				  .append("        t02ap_mate, t02nombres, t02f_ingsun ")
				  .append(" from  t02perdp " + " where t02cod_stat = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and t02cod_pers = '").append(valor.trim().toUpperCase()).append("'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = '")
				.append(valor.trim().toUpperCase()).append("'");
			}

			strSQL.append(" order by t02cod_pers");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = rs.getString("t02cod_pers") + " - ";
				texto += rs.getString("t02ap_pate").trim() + " "
						+ rs.getString("t02ap_mate").trim() + ", "
						+ rs.getString("t02nombres").trim();
				texto += " ("
						+ Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))
						+ ")";
				a.setCodigo(rs.getString("t02cod_pers"));
				a.setUnidad(rs.getString("cod_uorg"));
				a.setNombre(texto);

				personal.add(a);
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
		return personal;
	}

	/**
	 * Metodo encargado de determinar la cantidad de trabajadores filtrados por
	 * un criterio con un valor determinado
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanReporte.
	 * @throws SQLException
	 */
	public int findTotalPersonalByCriterioValor(String dbpool, String criterio,
			String valor) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int total = 0;

		try {

			strSQL.append("select count(t02cod_pers) as total from t02perdp ").append("where t02cod_stat = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and t02cod_pers = '" + valor.trim().toUpperCase()).append("'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = '")
						.append(valor.trim().toUpperCase()).append("'");
			}

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				total = rs.getInt("total");
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
		return total;
	}

	/**
	 * Metodo encargado de buscar los datos de las licencias de un determinado
	 * tipo registradas para un rango de fechas especifico. Join de las tablas
	 * T1273Licencia y T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param tipo
	 *            String. Tipo de las licencias buscadas.
	 * @param fechaIni
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fechaFin
	 *            String. Fecha superior del rango de busqueda.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanReporte.
	 * @throws SQLException
	 */
	public ArrayList findLicenciasByTipoCodPers(String dbpool, String tipo,
			String fechaIni, String fechaFin, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	l.numero, l.anno, l.ffin, l.ffinicio, m.descrip, m.tipo_id, l.qdias, l.observ ")
					.append("from	t1273licencia l, ")
					.append("        t1279tipo_mov m ")
					.append("where 	l.cod_pers = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and l.ffinicio >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and l.ffin <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("')");
			}
			if (!tipo.equals("-1")) {
				strSQL.append(" and m.mov = '").append(tipo.trim()).append("'");
			}

			strSQL.append(" and l.licencia = m.mov ");
			strSQL.append(" order by l.numero desc");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			String txtObs = null;
			StringTokenizer st = new StringTokenizer("");
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setAnno(rs.getString("anno"));
				detalle.setFechaFin(rs.getTimestamp("ffin"));
				detalle.setFechaInicio(rs.getTimestamp("ffinicio"));
				detalle.setNombre(rs.getString("descrip"));
				detalle.setCantidad(new Float(rs.getFloat("qdias")).intValue());
				txtObs = rs.getString("observ");

				if (txtObs !=null)
				{
					
					st = new StringTokenizer(txtObs, "&");
					log.debug("st "+st.toString());
					log.debug("st "+txtObs.indexOf("&"));
					if (txtObs.indexOf("&")==0){
						detalle.setDescripcion("");
					}
					else
					{
						if (txtObs.indexOf("&")== -1){
							detalle.setDescripcion(txtObs);
						} else {
							detalle.setDescripcion(st.nextToken());
						}
					}
					log.debug("stdes "+detalle.getDescripcion());
				}
				else {
					detalle.setDescripcion("");
				}
				
				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * Metodo encargado de buscar los datos de las licencias de un determinado
	 * tipo generadas para un rango de fechas y filtradas por un criterio con un
	 * valor determinado. Join de las tablas T1273Licencia y T02Perdp.
	 * 
	 * @throws SQLException
	 */
	public ArrayList findPersonalByLicencia(String dbpool, String tipo,
			String fechaIni, String fechaFin, String criterio, String valor)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select   l.licencia, l.cod_pers, l.anno, l.ffinicio, ")
				  .append("        	l.ffin, p.t02ap_pate, ")
				  .append( "        	p.t02ap_mate, p.t02nombres,substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg,substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate,uo.t12des_uorga, t12des_corta, p.t02f_ingsun,  l.qdias, l.observ  ")
				  .append( "from		t1273licencia l, " + "        t02perdp p,t12uorga uo ")
				  .append( "where 	l.licencia = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and p.t02cod_pers = '").append(valor.trim().toUpperCase()).append("'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase()).append("'");
			}

			if (!fechaIni.equals("")) {
				strSQL.append(" and l.ffinicio >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and l.ffin <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("')");
			}

			strSQL.append(" and l.cod_pers = p.t02cod_pers and uo.t12cod_uorga=substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ");
			strSQL.append(" order by l.cod_pers ");
			log.debug("Consulta:"+strSQL);
			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, tipo);
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			String txtObs = null;
			StringTokenizer st = new StringTokenizer("");
			while (rs.next()) {

				detalle = new BeanReporte();

				String texto = rs.getString("cod_pers") + " - ";
				texto += rs.getString("t02ap_pate").trim() + " "
						+ rs.getString("t02ap_mate").trim() + ", "
						+ rs.getString("t02nombres").trim();
				texto += " ("
						+ Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))
						+ ")";

				detalle.setCodigo(rs.getString("cod_pers"));
				detalle.setNombre(texto);
				detalle.setUnidad(rs.getString("cod_uorg")+" - "+rs.getString("t12des_uorga"));
				detalle.setAnno(rs.getString("anno"));
				detalle.setFechaFin(rs.getTimestamp("ffin"));
				detalle.setFechaInicio(rs.getTimestamp("ffinicio"));

				detalle.setCantidad(new Float(rs.getFloat("qdias")).intValue());
				txtObs = rs.getString("observ");
				if (txtObs !=null)
				{
					
					st = new StringTokenizer(txtObs, "&");
					log.debug("st "+st.toString());
					log.debug("st "+txtObs.indexOf("&"));
					if (txtObs.indexOf("&")==0){
						detalle.setDescripcion("");
					}
					else
					{
						if (txtObs.indexOf("&")== -1){
							detalle.setDescripcion(txtObs);
						} else {
							detalle.setDescripcion(st.nextToken());
						}
					}
					log.debug("stdes "+detalle.getDescripcion());
				}
				else {
					detalle.setDescripcion("");
				}
				
				lista.add(detalle);

			}
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * Metodo encargado de buscar la cantidad de licencias de un determinado
	 * tipo registradas para un trabajador en un rango de fechas especifico.
	 * Join de las tablas T1273Licencia y T1279Tipo_Mov
	 * @throws SQLException
	 */
	public ArrayList findAcumuladoLicenciasByTipoCodPers(String dbpool,
			String tipo, String fechaIni, String fechaFin, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	m.descrip, count(l.numero) as cantidad, sum(l.qdias) as dias ")
					.append("from	t1273licencia l, ")
					.append("        t1279tipo_mov m ")
					.append("where 	l.cod_pers = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and l.ffinicio >= DATE('")
				.append(Utiles.toYYYYMMDD(fechaIni))
				.append("')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and l.ffin <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("')");
			}
			if (!tipo.equals("-1")) {
				strSQL.append(" and m.mov = '").append(tipo.trim()).append("'");
			}

			strSQL.append(" and l.licencia = m.mov ");
			strSQL.append(" group by m.descrip order by m.descrip");

			con = getConnection(dbpool);
			
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setNombre(rs.getString("descrip").trim());
				detalle.setCantidad(rs.getInt("cantidad"));
				detalle.setDescripcion("" + rs.getInt("dias"));

				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * Metodo encargado de buscar la cantidad de licencias de un determinado
	 * tipo registradas para un trabajador en un rango de fechas especifico.
	 * Join de las tablas T1273Licencia y T02Perdp
	 * @throws SQLException
	 */
	public ArrayList findAcumuladoPersonalByLicencia(String dbpool,
			String tipo, String fechaIni, String fechaFin, String criterio,
			String valor) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select count(l.numero) as cantidad, sum(l.qdias) as dias, p.t02cod_pers, p.t02ap_pate, ")
			.append("       p.t02ap_mate, p.t02nombres,substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) as cod_uorg,substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) as cod_cate,uo.t12des_uorga,t12des_corta, p.t02f_ingsun ")
			.append("from	t1273licencia l, ")
			.append("        t02perdp p,t12uorga uo ")
			.append("where 	l.licencia = ? ");

			if (criterio.equals("0")) {
				strSQL.append(" and p.t02cod_pers = '").append(valor.trim().toUpperCase()).append("'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase()).append("'");
			}

			if (!fechaIni.equals("")) {
				strSQL.append(" and l.ffinicio >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and l.ffin <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("')");
			}

			strSQL.append(" and l.cod_pers = p.t02cod_pers and uo.t12cod_uorga=substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) ");
			strSQL.append(" group by p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres,cod_uorg,cod_cate,t12des_uorga,t12des_corta,p.t02f_ingsun ");
			strSQL.append(" order by p.t02ap_pate, p.t02ap_mate ");
			log.debug("Cons:"+strSQL);
			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, tipo);
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {

				detalle = new BeanReporte();

				String texto = rs.getString("t02cod_pers") + " - ";
				texto += rs.getString("t02ap_pate").trim() + " "
						+ rs.getString("t02ap_mate").trim() + ", "
						+ rs.getString("t02nombres").trim();
				texto += " ("
						+ Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))
						+ ")";
				detalle.setCodigo(rs.getString("t02cod_pers"));
				detalle.setNombre(texto);
				detalle.setUnidad(rs.getString("cod_uorg")+" - "+rs.getString("t12des_uorga"));
				detalle.setCantidad(rs.getInt("cantidad"));
				detalle.setDescripcion("" + rs.getInt("dias"));

				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * Metodo encargado de listar las marcaciones realizadas por un trabajador
	 * dentro de un rango de fechas y filtradas por hora de marcacion Join de
	 * las tablas T1275Marcacion y T1280Tipo_Reloj.
	 * @throws SQLException
	 */
	public ArrayList findMarcaciones(String dbpool, String fechaIni,
			String fechaFin, String horaMarca, String reloj, String codPers) 
			throws SQLException {
		
		log.debug("Reporte Marcaciones-ingreso a findMarcaciones");
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		T1270DAO turnoDAO = new T1270DAO();	
		T1276DAO periodoDAO = new T1276DAO();	
		T02DAO persDAO = new T02DAO();	
		BeanTurnoTrabajo turno1 = null;
		BeanTurnoTrabajo turno2 = null;
		BeanTurnoTrabajo turno3 = null;
		boolean visualizar = true;
		String s_fecha = "";
		String s_fechaAnt = "";
		HashMap hmPers =null;
		String cod_rel="-";
		String periodo = "";
		Map params = new HashMap();
		boolean bPeriodoCerrado = false;
		//FIN 

		try {

			strSQL.append("select 	m.fecha, m.hora, r.descrip, m.reloj ")
			        .append("from   t1275marcacion m, ")
                    .append("       t1280tipo_reloj r ")
                    .append("where	m.cod_pers = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and m.fecha >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("') ");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and m.fecha <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("') ");
			}

			strSQL.append("       and m.sdel = ? ");
            strSQL.append("       and (m.hora >= '").append(horaMarca.trim()).append("' or ");
            strSQL.append("            m.hora = ?) ");
			strSQL.append(" 	  and m.reloj = r.reloj ");
			// JVV - 02/10/2010
			if (!reloj.trim().equals("-1")) {
				strSQL.append(" 	  and m.reloj = '").append(reloj.trim()).append("'");
			}	
			
			strSQL.append(" order by 1,2 ");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			pre.setString(3, Constantes.SALIDA);
			rs = pre.executeQuery();

					
			lista = new ArrayList();
			BeanReporte detalle = null;
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			hmPers = persDAO.findByCodPers(dbpool, codPers.trim().toUpperCase());
			cod_rel=hmPers!=null?hmPers.get("t02cod_rel").toString():"-";						
			params.put("dbpool", dbpool);			
			//FIN
			
			while (rs.next()) {
				
				//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				s_fecha = new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy");
				log.debug("s_fecha: "+s_fecha);
				visualizar = true;
				turno1 = null;
				turno2 = null;
				turno3 = null;
				
				if(!cod_rel.equals("-")) {
					if(cod_rel.equals("09")) {//regimen 1057 (CAS)
						periodo = periodoDAO.findByFechaCAS(dbpool, s_fecha);	
						log.debug("periodo CAS: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoCAS(params);
						log.debug("bPeriodoCerrado CAS: "+bPeriodoCerrado);
					} else if(cod_rel.equals("10")) {//Modalidades Formativas 
						periodo = periodoDAO.findByFechaModFormativas(dbpool, s_fecha);	
						log.debug("periodo formativas: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoModFormativa(params);
						log.debug("bPeriodoCerrado Formativa: "+bPeriodoCerrado);												
					} else { //regimen 276-728
						periodo = periodoDAO.findByFecha(dbpool, s_fecha);
						log.debug("periodo 276-728: "+periodo);
						params.put("periodo",periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerrado(params);
						log.debug("bPeriodoCerrado 276-728: "+bPeriodoCerrado);	
					}
				}
			
				turno1 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(dbpool,codPers.trim(),s_fecha);		    
				if (turno1!=null && bPeriodoCerrado){
					visualizar = false;	//no se debe visualizar informacion de s_fecha
					log.debug("NO visualizar informacion de fecha: "+s_fecha);
				}else{
					turno2 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fecha);
					if (turno2!=null && bPeriodoCerrado){
						visualizar = false;	//no se debe visualizar informacion de s_fecha
						log.debug("NO visualizar informacion1 de fecha: "+s_fecha);
					}else{
						s_fechaAnt = Utiles.dameFechaAnterior(s_fecha, 1);
						log.debug("s_fechaAnt: "+s_fechaAnt);
						turno3 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fechaAnt);
						if (turno3!=null && bPeriodoCerrado){
							visualizar = false; //no se debe visualizar informacion de s_fecha
							log.debug("NO visualizar informacion2 de fecha: "+s_fecha);
						}						
					}							    
				}
								
				if (visualizar==true){
					log.debug("SI visualizar informacion de fecha: "+s_fecha);					
				//FIN ICAPUNAY	
			
					detalle = new BeanReporte();
	
					//detalle.setFecha(new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy")); //ICAPUNAY - PAS20165E230300132 - MSNAAF071
					detalle.setFecha(s_fecha); //ICAPUNAY - PAS20165E230300132 - MSNAAF071
					String dia = Utiles.dameDiaSemana(detalle.getFecha());
					detalle.setNombre(detalle.getFecha() + " " + dia);
					detalle.setHora(rs.getString("hora"));
					detalle.setDescripcion(rs.getString("descrip").trim());
					log.debug("setFecha: "+new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));
					log.debug("setNombre:"+ detalle.getFecha() + " " + dia);
					log.debug("setHora:"+ rs.getString("hora"));
					log.debug("setDescripcion:"+ rs.getString("descrip").trim());
					log.debug("detalle: "+detalle);
					lista.add(detalle);
				}//add esta linea //ICAPUNAY - PAS20165E230300132 - MSNAAF071		
			}
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findMarcacionesImpares(String dbpool, String fechaIni,
			String fechaFin, String codPers) throws SQLException {

		log.debug("Reporte Marcaciones Impares-ingreso a findMarcacionesImpares");
		StringBuffer strSQL = new StringBuffer("");
		StringBuffer strSQL1 = new StringBuffer("");
		PreparedStatement pre = null;
		PreparedStatement pre1 = null;
		Connection con = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ArrayList lista = null;
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		T1270DAO turnoDAO = new T1270DAO();	
		T1276DAO periodoDAO = new T1276DAO();	
		T02DAO persDAO = new T02DAO();	
		BeanTurnoTrabajo turno1 = null;
		BeanTurnoTrabajo turno2 = null;
		BeanTurnoTrabajo turno3 = null;
		boolean visualizar = true;
		String s_fecha = "";
		String s_fechaAnt = "";
		HashMap hmPers =null;
		String cod_rel="-";
		String periodo = "";
		Map params = new HashMap();
		boolean bPeriodoCerrado = false;
		//FIN 

		try {

			strSQL.append("select m.fecha, count(m.hora) as num_marcas ")
					.append("from t1275marcacion m, t02perdp p ")
							.append("where m.cod_pers = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and m.fecha >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("') ");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and m.fecha <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("') ");
			}

			strSQL.append(" and m.sdel = ? and p.t02cod_stat = ? and m.cod_pers = p.t02cod_pers ");
			strSQL.append(" group by 1 order by 1 ");
			
			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());			
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			pre.setString(3, Constantes.ACTIVO);
			rs = pre.executeQuery();
			
			lista = new ArrayList();			
			BeanReporte detalle = null;
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			hmPers = persDAO.findByCodPers(dbpool, codPers.trim().toUpperCase());
			cod_rel=hmPers!=null?hmPers.get("t02cod_rel").toString():"-";						
			params.put("dbpool", dbpool);			
			//FIN
			
			while (rs.next()) {
				
				//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				s_fecha = new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy");
				log.debug("s_fecha: "+s_fecha);
				visualizar = true;
				turno1 = null;
				turno2 = null;
				turno3 = null;
				
				if(!cod_rel.equals("-")) {
					if(cod_rel.equals("09")) {//regimen 1057 (CAS)
						periodo = periodoDAO.findByFechaCAS(dbpool, s_fecha);	
						log.debug("periodo CAS: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoCAS(params);
						log.debug("bPeriodoCerrado CAS: "+bPeriodoCerrado);
					} else if(cod_rel.equals("10")) {//Modalidades Formativas 
						periodo = periodoDAO.findByFechaModFormativas(dbpool, s_fecha);	
						log.debug("periodo formativas: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoModFormativa(params);
						log.debug("bPeriodoCerrado Formativa: "+bPeriodoCerrado);												
					} else { //regimen 276-728
						periodo = periodoDAO.findByFecha(dbpool, s_fecha);
						log.debug("periodo 276-728: "+periodo);
						params.put("periodo",periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerrado(params);
						log.debug("bPeriodoCerrado 276-728: "+bPeriodoCerrado);	
					}

				}
			
				turno1 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(dbpool,codPers.trim(),s_fecha);		    
				if (turno1!=null && bPeriodoCerrado){
					visualizar = false;	//no se debe visualizar informacion de s_fecha
					log.debug("NO visualizar informacion de fecha: "+s_fecha);
				}else{
					turno2 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fecha);
					if (turno2!=null && bPeriodoCerrado){
						visualizar = false;	//no se debe visualizar informacion de s_fecha
						log.debug("NO visualizar informacion1 de fecha: "+s_fecha);
					}else{
						s_fechaAnt = Utiles.dameFechaAnterior(s_fecha, 1);
						log.debug("s_fechaAnt: "+s_fechaAnt);
						turno3 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fechaAnt);
						if (turno3!=null && bPeriodoCerrado){
							visualizar = false; //no se debe visualizar informacion de s_fecha
							log.debug("NO visualizar informacion2 de fecha: "+s_fecha);
						}						
					}							    
				}
								
				if (visualizar==true){
					log.debug("SI visualizar informacion de fecha: "+s_fecha);					
				//FIN ICAPUNAY	

					strSQL1 = new StringBuffer("");
					Date fecMarca = rs.getDate("fecha");
					int numMarcas = rs.getInt("num_marcas");
	
					//solo ingresan los q poseen marcaciones impares
					if (numMarcas % 2 == 1) {
	
						strSQL1.append(" select  m.fecha, m.hora, r.descrip, m.reloj ")
								.append(" from   t1275marcacion m, ")
								.append("        t1280tipo_reloj r ")
								.append("where   m.cod_pers = ? and ")
								.append("        m.fecha = ? and ")
								.append("        m.sdel = ? and ")
								.append("        m.reloj = r.reloj ");
	
						log.debug(strSQL1.toString());
						
						pre1 = con.prepareStatement(strSQL1.toString());
						pre1.setString(1, codPers);
						pre1.setDate(2, fecMarca);
						pre1.setString(3, Constantes.ACTIVO);
						rs1 = pre1.executeQuery();
	
						while (rs1.next()) {
	
							detalle = new BeanReporte();
	
							detalle.setFecha(new BeanFechaHora(rs1.getDate("fecha")).getFormatDate("dd/MM/yyyy")); 							
							String dia = Utiles.dameDiaSemana(detalle.getFecha());
							detalle.setNombre(detalle.getFecha() + " " + dia);
							detalle.setHora(rs1.getString("hora"));
							detalle.setDescripcion(rs1.getString("descrip"));
	
							lista.add(detalle);
						}
	
						if (rs1 != null) {
							rs1.close();
						}
						if (pre1 != null) {
							pre1.close();
						}
	
					}//fin if numMarcas
				}//add esta linea //ICAPUNAY - PAS20165E230300132 - MSNAAF071				

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
		return lista;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findCalificaciones(String dbpool, String fechaIni,
			String fechaFin, String codPers, String criterio, String valor)
			throws SQLException {

		log.debug("Reporte Calificaciones-ingreso a findCalificaciones");
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		T1270DAO turnoDAO = new T1270DAO();	
		T1276DAO periodoDAO = new T1276DAO();	
		T02DAO persDAO = new T02DAO();	
		BeanTurnoTrabajo turno1 = null;
		BeanTurnoTrabajo turno2 = null;
		BeanTurnoTrabajo turno3 = null;
		boolean visualizar = true;
		String s_fecha = "";
		String s_fechaAnt = "";
		HashMap hmPers =null;
		String cod_rel="-";
		String periodo = "";
		Map params = new HashMap();
		boolean bPeriodoCerrado = false;
		//FIN 

		try {

			BeanFechaHora bfh = new BeanFechaHora();
			String mesActual = bfh.getMes();
			String anhoActual = bfh.getAnho();
			bfh.getCalendar().roll(Calendar.MONTH, -2);
			String mes = bfh.getMes();
			String anho = anhoActual;
			if (mesActual.compareTo(mes) < 0)
				anho = "" + (Integer.parseInt(anhoActual) - 1);
			String fecha = anho + "/" + mes + "/01";

			strSQL.append("select  a.fing, a.hing, a.hsal, m.descrip, a.fcreacion ")
				  .append("from    t1271asistencia a, ")
				  .append("        t1279tipo_mov m ")
				  .append("where   a.cod_pers = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and a.fing >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("') ");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and a.fing <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("') ");
			}
			if (criterio.equals("2")) {
				strSQL.append(" and a.fing >= DATE('").append(fecha).append("')");
				//se agrego esta linea para la validacion de la consulta de mis marcaciones 04/01/2006
				strSQL.append(" and a.mov in (?,?,?,?,?,?) ");
			}
			if (valor!= null) {
				if (!valor.equals("")){
					strSQL.append(" and a.mov = '").append(valor.trim()).append("' ");
				}
			}
			strSQL.append(" and a.mov = m.mov ");

			if (criterio.equals("2")) {
				strSQL.append(" order by 1 desc, a.hing, a.fcreacion ");
			} else {
				strSQL.append(" order by 1, a.hing, a.fcreacion ");
			}

			log.debug("SQL : "+strSQL.toString());
			
			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();


			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
					
			if (criterio.equals("2")) {
				//se agrego esta linea para la validacion de la consulta de mis marcaciones 04/01/2006
				pre.setString(2,Constantes.MOV_TARDANZA_CON_DESCUENTO);
				pre.setString(3,Constantes.MOV_TARDANZA_SIN_DESCUENTO);
				pre.setString(4,Constantes.MOV_SALIDA_NO_AUTORIZADA);
				pre.setString(5,Constantes.MOV_OMISION_MARCA);
				pre.setString(6,Constantes.MOV_INASISTENCIA);
				pre.setString(7,Constantes.MOV_LABOR_FUERA_OFICINA);
			}
			
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			hmPers = persDAO.findByCodPers(dbpool, codPers.trim().toUpperCase());
			cod_rel=hmPers!=null?hmPers.get("t02cod_rel").toString():"-";						
			params.put("dbpool", dbpool);			
			//FIN 
			
			while (rs.next()) {
				
				//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				s_fecha = new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy");
				log.debug("s_fecha: "+s_fecha);
				visualizar = true;
				turno1 = null;
				turno2 = null;
				turno3 = null;
				
				if(!cod_rel.equals("-")) {
					if(cod_rel.equals("09")) {//regimen 1057 (CAS)
						periodo = periodoDAO.findByFechaCAS(dbpool, s_fecha);	
						log.debug("periodo CAS: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoCAS(params);
						log.debug("bPeriodoCerrado CAS: "+bPeriodoCerrado);
					} else if(cod_rel.equals("10")) {//Modalidades Formativas 
						periodo = periodoDAO.findByFechaModFormativas(dbpool, s_fecha);	
						log.debug("periodo formativas: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoModFormativa(params);
						log.debug("bPeriodoCerrado Formativa: "+bPeriodoCerrado);												
					} else { //regimen 276-728
						periodo = periodoDAO.findByFecha(dbpool, s_fecha);
						log.debug("periodo 276-728: "+periodo);
						params.put("periodo",periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerrado(params);
						log.debug("bPeriodoCerrado 276-728: "+bPeriodoCerrado);	
					}
				}
			
				turno1 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(dbpool,codPers.trim(),s_fecha);		    
				if (turno1!=null && bPeriodoCerrado){
					visualizar = false;	//no se debe visualizar informacion de s_fecha
					log.debug("NO visualizar informacion de fecha: "+s_fecha);
				}else{
					turno2 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fecha);
					if (turno2!=null && bPeriodoCerrado){
						visualizar = false;	//no se debe visualizar informacion de s_fecha
						log.debug("NO visualizar informacion1 de fecha: "+s_fecha);
					}else{
						s_fechaAnt = Utiles.dameFechaAnterior(s_fecha, 1);
						log.debug("s_fechaAnt: "+s_fechaAnt);
						turno3 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,codPers.trim(),s_fechaAnt);
						if (turno3!=null && bPeriodoCerrado){
							visualizar = false; //no se debe visualizar informacion de s_fecha
							log.debug("NO visualizar informacion2 de fecha: "+s_fecha);
						}						
					}							    
				}
								
				if (visualizar==true){
					log.debug("SI visualizar informacion de fecha: "+s_fecha);					
				//FIN ICAPUNAY	
					detalle = new BeanReporte();

					//detalle.setFecha(new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy")); //ICAPUNAY - PAS20165E230300132 - MSNAAF071
					detalle.setFecha(s_fecha); //ICAPUNAY - PAS20165E230300132 - MSNAAF071

					String dia = Utiles.dameDiaSemana(detalle.getFecha());
					detalle.setNombre(detalle.getFecha() + " " + dia);

					String hIng = rs.getString("hing");
					String hSal = rs.getString("hsal");

					if (hSal != null && !hSal.trim().equals("")) {
						detalle.setHora(hIng + " - " + hSal);
					} else {
						detalle.setHora(hIng);
					}
					detalle.setDescripcion(rs.getString("descrip"));
					log.debug("detalle: "+detalle);

					lista.add(detalle);
				}//add esta linea //ICAPUNAY - PAS20165E230300132 - MSNAAF071				
			} 

		} catch (Exception e) {

			
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
		return lista;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findTurnosTrabajo(String dbpool, String fechaIni,
			String fechaFin, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;
		
		if(log.isDebugEnabled())log.debug("dbpool:" + dbpool);
		if(log.isDebugEnabled())log.debug("fechaIni:" + fechaIni);
		if(log.isDebugEnabled())log.debug("fechaFin:" + fechaFin);
		if(log.isDebugEnabled())log.debug("codPers:" + codPers);		
		
		try {

			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);

			strSQL.append("select 	tp.fini, tp.ffin, ")
					.append("        t.h_inicio, t.h_fin, " + "        t.des_turno  ")
					.append("from 	t1270turnoperson tp, " + "        t45turno t ")
					.append("where	tp.cod_pers = ? and " + "        tp.est_id = ? ");

			if (!fechaIni.equals("") && !fechaFin.equals("")) {

				strSQL.append(" and ( ");
				strSQL.append(" ( tp.fini = DATE('").append(fIni).append("') ) or ");
				strSQL.append(" ( tp.fini = DATE('").append(fFin).append("') ) or ");
				strSQL.append(" ( tp.ffin = DATE('").append(fIni).append("') ) or ");
				strSQL.append(" ( tp.ffin = DATE('").append(fFin).append("') ) or ");
				strSQL.append(" ( (DATE('").append(fFin).append("') > tp.fini) and ( DATE('").append(fFin).append("') < tp.ffin ) ) or ");
				strSQL.append(" ( (DATE('").append(fIni).append("') > tp.fini) and ( DATE('").append(fIni).append("') < tp.ffin ) ) or ");
				strSQL.append(" ( (DATE('").append(fIni).append("') > tp.fini) and ( DATE('").append(fFin).append("') < tp.ffin ) ) or ");
				strSQL.append(" ( (DATE('").append(fIni).append("') < tp.fini) and ( DATE('").append(fFin).append("') > tp.ffin ) ) ");
				strSQL.append(" ) ");

			} else {
				strSQL.append(" and (1 = 2) ");
			}

			strSQL.append(" and tp.turno = t.cod_turno ");
			strSQL.append(" order by tp.fini ");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			if(log.isDebugEnabled())log.debug("strSQL findTurnosTrabajo:" + strSQL);
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setFechaInicio(rs.getTimestamp("fini"));
				detalle.setFechaFin(rs.getTimestamp("ffin"));

				String hIni = rs.getString("h_inicio");
				String hFin = rs.getString("h_fin");

				if (hFin != null && !hFin.equals("")) {
					detalle.setHora(hIni + " - " + hFin);
				} else {
					detalle.setHora(hIni);
				}

				detalle.setDescripcion(rs.getString("des_turno"));

				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAutorizacionesHoraExtra(String dbpool,
			String fechaIni, String fechaFin, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	DATE(h.f_autor[7,10]||''/''||h.f_autor[4,5]||''/''||h.f_autor[1,2]),h.f_autor,h.h_inic,h.h_term,h.h_salid,h.observa,h.cnt_tiempo_acum,h.cnt_tiempo_actual ")
			   	  .append("from 	t130horext h	 ")
                  .append("where 	h.cod_pers = ?  ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and (DATE(h.f_autor[7,10]||''/''||h.f_autor[4,5]||''/''||h.f_autor[1,2]) >= DATE('")
						.append(Utiles.toYYYYMMDD(fechaIni)).append("'))");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and (DATE(h.f_autor[7,10]||''/''||h.f_autor[4,5]||''/''||h.f_autor[1,2]) <= DATE('")
						.append(Utiles.toYYYYMMDD(fechaFin)).append("'))");
			}
			strSQL.append(" order by 1 ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			HashMap he = null;
			while (rs.next()) {

				detalle = new BeanReporte();
				he = new HashMap();
				detalle.setFecha(rs.getString("f_autor"));
				String dia = Utiles.dameDiaSemana(detalle.getFecha());
				detalle.setNombre(detalle.getFecha() + " " + dia);
				detalle.setHora(rs.getString("h_inic"));
				detalle.setHora1(rs.getString("h_term"));
				detalle.setHora2(rs.getString("h_salid"));
				detalle.setDescripcion(rs.getString("observa"));
				he.put("MinAcum",rs.getString("cnt_tiempo_acum"));
				he.put("MinSal",rs.getString("cnt_tiempo_actual"));
				detalle.setMap(he);

				lista.add(detalle);
			}
 
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findCompensaciones(String dbpool, String fechaIni,
			String fechaFin, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select  numero, anno, ffinicio, ffin, qdias, observ ")
			.append("from	t1273licencia " + "where 	cod_pers = ? and ")
			.append("	licencia = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and ffinicio >= DATE('")
						.append(Utiles.toYYYYMMDD(fechaIni)).append("')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and ffin <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("')");
			}

			strSQL.append(" order by numero, anno desc");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.LICENCIA_COMPENSACION);
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setAnno(rs.getString("anno"));
				detalle.setFechaInicio(rs.getTimestamp("ffinicio"));
				detalle.setFechaFin(rs.getTimestamp("ffin"));
				
			
				detalle.setCantidad(Math.round(rs.getFloat("qdias")));

				float numHoras = Utiles.obtenerHorasDiferencia(Utiles
						.timeToHora(detalle.getFechaInicio()), Utiles
						.timeToHora(detalle.getFechaFin()));

				detalle.setHora("" + Utiles.dameFormatoHHMM(numHoras));

				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findCompensacionesPendientes(String dbpool, String codPers)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList acumulados = null;

		try {

			strSQL.append("select 	ha.t_acum ").append("from 	t132horacu ha ")
					.append( "where	ha.est_id = ? and ")
					.append( "        ha.cod_pers = ? and ")
					.append( "        ha.t_acum >= 0 ");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, codPers);
			rs = pre.executeQuery();
			acumulados = new ArrayList();

			BeanReporte ha = null;
			while (rs.next()) {

				ha = new BeanReporte();

				String desc = Utiles.dameFormatoHHMM(rs.getFloat("t_acum"));
				ha.setDescripcion(desc);

				acumulados.add(ha);

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
		return acumulados;
	}

	/**
	 * 
	 * @param uo
	 * @return @throws
	 *         SQLException
	 */
	public float findTotalDescuentoMovimiento(String dbpool, String periodo,
			String codPers, String mov, String tipo, boolean descuento)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		float total = 0;

		try {

			strSQL.append("select 	sum(a.total) as total ")
					.append( "from 	t1272asistencia_r a, ")
					.append( "        t1279tipo_mov m " ).append( "where	a.periodo = ? and ")
					.append( "        a.cod_pers = ? and ")
					.append( "        m.califica = ? and " ).append( "        a.est_id = ? ");

			if (!tipo.equals("")) {
				strSQL .append( " and m.tipo_id = '" ).append( tipo ).append( "' ");
			}

			if (!mov.equals("")) {
				strSQL .append( " and a.mov = '" ).append( mov ).append( "' ");
			}

			strSQL .append( " and a.mov = m.mov ");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, periodo);
			pre.setString(2, codPers);
			pre.setString(3, descuento ? Constantes.DESCUENTO
					: Constantes.SIN_DESCUENTO);
			pre.setString(4, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				total = rs.getFloat("total");
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
		return total;
	}
	
	/**
	 * Suma solamente los excesos por dia para el resumen mensual
	 * @param dbpool
	 * @param periodo
	 * @param codPers
	 * @param mov
	 * @param tipo
	 * @param descuento
	 * @return
	 * @throws SQLException
	 */
	public float findTotalExcesosTardanzaDiaria(String dbpool, String periodo, String codPers, String mov)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		float total = 0;

		try {

			//aqui solo descontamos los excesos 
			strSQL.append("select 	  sum(a.total-10) as total ")
					.append( "from    t1272asistencia_r a, ")
					.append( "        t1279tipo_mov m " )
					.append( "where	  a.periodo = ? and ")
					.append( "        a.cod_pers = ? and ")
					.append( "        m.mov = ? and " )
					.append( "        a.est_id = ? and ")	
					.append( " 		  a.mov = m.mov ");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, periodo);
			pre.setString(2, codPers);
			pre.setString(3, mov);
			pre.setString(4, Constantes.ACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {
				total = rs.getFloat("total");
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
		return total;
	}	
	
	/**
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT02T12T99findPersonalTotalUOPeriodoByCriterioValor(
			String dbpool, String criterio, String valor, HashMap seguridad, String periodo)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {
				
			strSQL.append("select p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres,p.t02f_ingsun, ")
			.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg ")
					.append( " from t02perdp p ");
					
			if (criterio.equals("1")) {
				valor = valor.toUpperCase();
				
				strSQL.append( " Where substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = ? ")
	            	  .append( " and p.t02cod_stat = ? ");
				
			} else if (criterio.equals("3")) {
				strSQL.append( " Where p.t02cod_stat = ? ");
				
			}
			
			
			//.append( "        Select unique cod_pers ")
		            //.append( "        from t1272asistencia_r ")
		            //.append( "        Where periodo = ? and u_organ = ? )  " );
		       
			//strSQL.append("select   p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
		       //	.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
		       //	.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
		       //	.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")
		       //	.append( "from    t02perdp p, ")
		       //	.append( "        t12uorga uo, ")
		       //	.append( "        t99codigos param ")
		       //	.append( "where   ");
			
			//if (criterio.equals("0")) {
		       //	strSQL.append( "  p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
		       //}

		       //if (criterio.equals("1")) {
		       //strSQL.append( "  substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
		       //}

			//strSQL .append( " and param.t99cod_tab = ? ")
			//	.append( " and param.t99tip_desc = ? ")
			//		.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
			//		.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ")
			//       .append( " and p.t02cod_pers in (Select unique cod_pers from t1272asistencia_r Where periodo = ? and u_organ = ?) ");

			//criterios de visibilidad
			if (seguridad != null && seguridad.size()>0) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						   roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}
					
			strSQL.append(" order by t02cod_pers ");
            
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(1, Constantes.ACTIVO);
			//pre.setString(1, Constantes.CODTAB_CATEGORIA);
			//pre.setString(2, Constantes.T99DETALLE);
			//pre.setString(1, periodo);

			if (criterio.equals("1")) {
				pre.setString(1, valor);
				pre.setString(2, Constantes.ACTIVO);
				
			} else if (criterio.equals("3")) {
				pre.setString(1, Constantes.ACTIVO);
				
			}
			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();
				                      
				String texto = rs.getString("t02cod_pers").trim().concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim());
								//.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")");
								//.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());
				a.setCodigo(rs.getString("t02cod_pers"));
				//a.setUnidad(rs.getString("cod_uorg") .concat( " - ".concat( rs.getString("t12des_uorga"))));
				a.setUnidad(rs.getString("cod_uorg"));
				a.setNombre(texto.trim());
				personal.add(a);
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
		return personal;
	}	

	
	
	/**
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT02T12T99findPersonalTotalByCriterioValor(
			String dbpool, String criterio, String valor, HashMap seguridad, String periodo)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {
		
			strSQL.append("select   p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ")
					.append( "where   ");
			
			if (criterio.equals("0")) {
				strSQL.append( "  p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}

			if (criterio.equals("1")) {
				strSQL.append( "  substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			}

			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ")
					.append( " and p.t02cod_stat = ? ");
		//	        .append( " and p.t02cod_pers in (Select unique cod_pers from t1454asistencia_d Where periodo = ? ) ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}
					
			strSQL.append(" order by t02cod_pers ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(1, Constantes.ACTIVO);
			pre.setString(1, Constantes.CODTAB_CATEGORIA);
			pre.setString(2, Constantes.T99DETALLE);
			pre.setString(3, Constantes.ACTIVO);
			//pre.setString(3, periodo);			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = rs.getString("t02cod_pers").trim().concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim());
								//.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")");
								//.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());
				a.setCodigo(rs.getString("t02cod_pers"));
				a.setUnidad(rs.getString("cod_uorg"));// .concat( " - ".concat( rs.getString("t12des_uorga"))));
				a.setNombre(texto.trim());
				personal.add(a);
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
		return personal;
	}	
	
	/**
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT02T12T99findPersonalByCriterioValor(
			String dbpool, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	    ArrayList aLRpta = new ArrayList();
	    Utilidades common = new Utilidades();
	    String strSQL2 = "";
	    PreparedStatement pre2 = null;
	    Connection con2 = null;
	    ResultSet rs2 = null;
    	
	    HashMap hm = null;
	    String codUoJef = "";	 
	    //ICAPUNAY - PAS20165E230300132
		log.debug("joinWithT02T12T99findPersonalByCriterioValor ingreso");

		try {

			strSQL.append("select   p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					//.append( "        t99codigos param "); ICAPUNAY 06/09/2012  ATENDER SAU20124F310000200 (Demora Consulta reporte papeletas)
					.append( " outer  t99codigos param "); //ICAPUNAY 06/09/2012  ATENDER SAU20124F310000200 (Demora Consulta reporte papeletas)
					//.append( "where   p.t02cod_stat = ? ");// JVILLACORTA 24/08/2011
			
			if (criterio.equals("0")) {
				strSQL.append( "where   p.t02cod_stat in (?,?) "); // JVILLACORTA 24/08/2011 (ver INACTIVOS tambien para todos los regimenes pero solo por el criterio REGISTRO)
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}

			if (criterio.equals("1")) {
				strSQL.append( "where   p.t02cod_stat = ? ");// JVILLACORTA 24/08/2011
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			}
			
			//ICAPUNAY 05/01/2012 - SE CORRIGIO A SOLICITUD DEL USUARIO DCABALLERO POR ERROR EN REPORTE DE MARCACIONES IMPARES POR EL CRITERIO INSTITUCIONAL
			if (criterio.equals("3") || criterio.equals("6")) {// CRITERIO 6: INSTITUCIONAL
				strSQL.append( "where   p.t02cod_stat = ? ");				
			}
			//FIN ICAPUNAY 05/01/2012
			
			//DTARAZONA 26/01/2018 - SE HA AGREGADO EL CRITERIO INTENDENCIA
			if (criterio.equals("5")) {
				String intendencia = valor.substring(0,2);
				if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);
				strSQL.append( "where   p.t02cod_stat = ? ");
				strSQL.append(" AND substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%' ");		
			}
			// FIN DTARAZONA

			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null && !seguridad.isEmpty()) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String codPersUsuario = ((String) seguridad.get("codPers")).trim(); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					strSQL.append(" or   (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null
						//JRR - 22/09/2009
						|| roles.get(Constantes.ROL_ANALISTA_LEXC) != null)
						{
					
					//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
					if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
						strSQL2 = "SELECT * FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)='"+codPersUsuario+"' and t12ind_estad='1'"; 
	        			log.debug("joinWithT02T12T99findPersonalByCriterioValor(jefe)-strSQL2: "+strSQL2);
	        		    con2 = getConnection(dbpool);
	        			pre2 = con2.prepareStatement(strSQL2);
	        			rs2 = pre2.executeQuery();	        			
	        			String[] aFields = common.getFieldsName(rs2);

	        			while (rs2.next()) {
	        				HashMap rsMap = new HashMap();
	        				common.getRecordToMap(rs2, rsMap, aFields);	        				
	        				aLRpta.add(rsMap);
	        			}
	        		  	if (aLRpta!=null & aLRpta.size()>0){    			   		      
		          		      for (int i = 0;i<aLRpta.size();i++){ //0,1,2 (3 unidades)
		          		    	  hm=(HashMap)aLRpta.get(i);
		          		    	  codUoJef= ((String)hm.get("t12cod_uorga")).trim();
		          		    	  if (i==0){//0 (primer registro)
		          		    		strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef) + "%' "); 
		          		    	  }else{
		          		    		strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef)+ "%' "); 
		          		    	  }    		    	  
		          		    	  if (i==aLRpta.size()-1){//2 (ultimo registro)
		          		    		strSQL.append(") " );
		          		    	  }
		          		      }
	          		  	}
	        		  	log.debug("joinWithT02T12T99findPersonalByCriterioValor(jefe)-strSQL: "+strSQL);
					}
					else{//secretaria o analista lexc
						strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					}
					//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					//FIN ICAPUNAY - PAS20165E230300132
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by t02cod_pers ");
			log.debug("joinWithT02T12T99findPersonalByCriterioValor-strSQL final: "+strSQL);

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			/*pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, Constantes.CODTAB_CATEGORIA);
			pre.setString(3, Constantes.T99DETALLE);*/
			// JVILLACORTA 24/08/2011 (ver INACTIVOS tambien para todos los regimenes pero solo por el criterio REGISTRO)
			if (criterio.equals("0")) {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.INACTIVO);
				pre.setString(3, Constantes.CODTAB_CATEGORIA);
				pre.setString(4, Constantes.T99DETALLE);
			}
			
			if (criterio.equals("1")) {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.CODTAB_CATEGORIA);
				pre.setString(3, Constantes.T99DETALLE);
			}
			
			//ICAPUNAY 05/01/2012 - SE CORRIGIO A SOLICITUD DEL USUARIO DCABALLERO POR ERROR EN REPORTE DE MARCACIONES IMPARES POR EL CRITERIO INSTITUCIONAL
			if (criterio.equals("3") || criterio.equals("6")) {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.CODTAB_CATEGORIA);
				pre.setString(3, Constantes.T99DETALLE);				
			}
			//FIN ICAPUNAY 05/01/2012
			
			//DTARAZONA 26/01/2018
			if (criterio.equals("5")) {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.CODTAB_CATEGORIA);
				pre.setString(3, Constantes.T99DETALLE);				
			}
			
			// FIN - JVILLACORTA 24/08/2011 (ver INACTIVOS tambien para todos los regimenes pero solo por el criterio REGISTRO)
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = (rs.getString("t02cod_pers")!=null?rs.getString("t02cod_pers"):"").trim().concat( " - ").concat((rs.getString("t02ap_pate")!=null?rs.getString("t02ap_pate"):"").trim()).concat( " ")
								.concat((rs.getString("t02ap_mate")!=null?rs.getString("t02ap_mate"):"").trim()).concat( ", ").concat((rs.getString("t02nombres")!=null?rs.getString("t02nombres"):"").trim())
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun")!=null?rs.getTimestamp("t02f_ingsun"): new FechaBean().getTimestamp())).concat( ")")
								//.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim()); //Diego
								.concat("  -  " ).concat(rs.getString("t99descrip")!=null?rs.getString("t99descrip").trim():"" ).concat( " / ").concat(rs.getString("t12des_corta")!=null?rs.getString("t12des_corta").trim():"");
				a.setCodigo(rs.getString("t02cod_pers")!=null?rs.getString("t02cod_pers"):"");
				//a.setUnidad(rs.getString("cod_uorg") .concat( " - ".concat( rs.getString("t12des_uorga")))); //Diego
				a.setUnidad((rs.getString("cod_uorg")!=null?rs.getString("cod_uorg"):"").concat( " - ".concat(rs.getString("t12des_uorga")!=null?rs.getString("t12des_uorga"):"")));
				a.setNombre(texto.trim());
				personal.add(a);
				log.debug("joinWithT02T12T99findPersonalByCriterioValor-a: "+a);
				 
			}
			log.debug("joinWithT02T12T99findPersonalByCriterioValor-personal: "+personal);
		
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
		return personal;
	}
	
	//JVILLACORTA 16/05/2011 - AOM:MODALIDADES FORMATIVAS
	/**
	 * @param criterio
	 * @param valor
	 * @return @throws SQLException
	 */	
	public ArrayList joinWithT02T12T99findPersonalByCriterioVal(
			String dbpool, String regimen, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;
		
		if(log.isDebugEnabled())log.debug("dbpool:" + dbpool);
		if(log.isDebugEnabled())log.debug("regimen:" + regimen);
		if(log.isDebugEnabled())log.debug("criterio:" + criterio);
		if(log.isDebugEnabled())log.debug("valor:" + valor);
		if(log.isDebugEnabled())log.debug("seguridad:" + seguridad);
		log.debug("joinWithT02T12T99findPersonalByCriterioVal ingreso");
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	    ArrayList aLRpta = new ArrayList();
	    Utilidades common = new Utilidades();
	    String strSQL2 = "";
	    PreparedStatement pre2 = null;
	    Connection con2 = null;
	    ResultSet rs2 = null;
    	
	    HashMap hm = null;
	    String codUoJef = "";	 
	    //ICAPUNAY - PAS20165E230300132

		try {
			//JVV - se agrego t02cod_rel
			strSQL.append("select   p.t02cod_rel, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ")
					.append( "where   p.t02cod_stat = ? ");
			
			if (criterio.equals("0")) {
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}
			
			if (criterio.equals("0") && regimen.equals("es276")) {				
				strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)//in (01,02)//strSQL.append( " and p.t02cod_rel in (?,?) ")			
			} else if (criterio.equals("0") && regimen.equals("es1057")) {
				strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
			  }
			  else if (criterio.equals("0") && regimen.equals("esModFormativa")) {
				strSQL.append( "and   p.t02cod_rel = ? "); //= '10' 
			  }					
			
			if (criterio.equals("1") && regimen.equals("es276")) {
				strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			} else if (criterio.equals("1") && regimen.equals("es1057")) {
						strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
						strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
					}
		      else if (criterio.equals("1") && regimen.equals("esModFormativa")) {
						strSQL.append( "and   p.t02cod_rel = ? "); //= '10'
						strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			        }
			
			//JVV-ini
			if (criterio.equals("4")) {	//Intendencia	
				String intendencia = valor.substring(0,2);
				if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%' ");
				if (regimen.equals("es276")) {
					strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)
				}
				if (regimen.equals("es1057")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
				}
				if (regimen.equals("esModFormativa")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '10'
				}
			}
			
			if (criterio.equals("3")) {	//Intstituc					
				if (regimen.equals("es276")) {
					strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)
				}
				if (regimen.equals("es1057")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
				}
				if (regimen.equals("esModFormativa")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '10'
				}
			}
			//JVV-fin
			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPersUsuario = ((String) seguridad.get("codPers")).trim(); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null)
						{
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					strSQL.append(" or   (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null
						//JRR - 22/09/2009
						|| roles.get(Constantes.ROL_ANALISTA_LEXC) != null)
						{
					
					//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
					if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
						strSQL2 = "SELECT * FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)='"+codPersUsuario+"' and t12ind_estad='1'"; 
	        			log.debug("joinWithT02T12T99findPersonalByCriterioVal(jefe)-strSQL2: "+strSQL2);
	        		    con2 = getConnection(dbpool);
	        			pre2 = con2.prepareStatement(strSQL2);
	        			rs2 = pre2.executeQuery();	        			
	        			String[] aFields = common.getFieldsName(rs2);

	        			while (rs2.next()) {
	        				HashMap rsMap = new HashMap();
	        				common.getRecordToMap(rs2, rsMap, aFields);	        				
	        				aLRpta.add(rsMap);
	        			}
	        		  	if (aLRpta!=null & aLRpta.size()>0){    			   		      
		          		      for (int i = 0;i<aLRpta.size();i++){ //0,1,2 (3 unidades)
		          		    	  hm=(HashMap)aLRpta.get(i);
		          		    	  codUoJef= ((String)hm.get("t12cod_uorga")).trim();
		          		    	  if (i==0){//0 (primer registro)
		          		    		strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef) + "%' "); 
		          		    	  }else{
		          		    		strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef)+ "%' "); 
		          		    	  }    		    	  
		          		    	  if (i==aLRpta.size()-1){//2 (ultimo registro)
		          		    		strSQL.append(") " );
		          		    	  }
		          		      }
	          		  	}
	        		  	log.debug("joinWithT02T12T99findPersonalByCriterioVal(jefe)-strSQL: "+strSQL);
					}
					else{//secretaria o analista lexc
						strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					}
					//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					//FIN ICAPUNAY - PAS20165E230300132						
				
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by cod_uorg,t02cod_pers ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			if(log.isDebugEnabled())log.debug("strSQL findPersonalByCriterioValor:" + strSQL);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			//JVV-ini			
			if (criterio.equals("0") && regimen.equals("es276")) {
				pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
				pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
				pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(5, constantes.leePropiedad("T99DETALLE"));
			} else if (criterio.equals("0") && regimen.equals("es1057")) {
						pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
						pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
						pre.setString(4, constantes.leePropiedad("T99DETALLE"));
					}
			  else if (criterio.equals("0") && regimen.equals("esModFormativa")) {
				pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
				pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(4, constantes.leePropiedad("T99DETALLE"));
			}
			
			
			if (criterio.equals("1") && regimen.equals("es276")) {				
				pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
				pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
				pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(5, constantes.leePropiedad("T99DETALLE"));
			} else if (criterio.equals("1") && regimen.equals("es1057")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				   }
			  else if (criterio.equals("1") && regimen.equals("esModFormativa")) {
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
			       }
			
			//JVV-fin
			
			if (criterio.equals("4") || criterio.equals("3")) {
				if (regimen.equals("es276")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(5, constantes.leePropiedad("T99DETALLE"));
				}
				if (regimen.equals("es1057")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				if (regimen.equals("esModFormativa")) {
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
			}			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = rs.getString("t02cod_rel").trim().concat( " - ").concat(rs.getString("t02cod_pers").trim()).concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim())
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")")
								.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());
				/*/JVV-ini
				if(rs.getString("t02cod_rel").trim().equals("01")){
					texto.concat("D.L.276");
				} else {
					if (rs.getString("t02cod_rel").trim().equals("02")){
						texto.concat("D.L.728");
					} else {				
					texto.concat("D.L.1057");
					}
				}
				//JVV-fin*/
				a.setCodigo(rs.getString("t02cod_pers"));
				a.setUnidad(rs.getString("cod_uorg") .concat( " - ".concat( rs.getString("t12des_uorga"))));
				a.setNombre(texto.trim());
				personal.add(a);
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
		return personal;
	}
	//FIN - JVILLACORTA 16/05/2011 - AOM:MODALIDADES FORMATIVAS

	/**
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers
	 * @param numDias
	 * @param incluyePend
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findVacacionesGozadas(String dbpool, String fechaIni,
			String fechaFin, String codPers, int numDias, boolean incluyePend,
			boolean incluyeProg) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select  v.anno_vac, v.ffinicio, v.ffin, v.dias, v.observ, m.descrip, v.licencia ")
					.append( "from	t1282vacaciones_d v, t1279tipo_mov m ")
					.append( "where m.mov = v.licencia and	v.cod_pers = ? and (v.licencia = ? or v.licencia = ? or v.licencia = ? ")
					//PRAC-ASANCHEZ 17/06/2009
					//prac-asanchez 03/06/2009
					//.append( "or (v.licencia= ? and v.ffinicio <> v.ffin) ");
					//
					.append( "or (v.licencia= ? and v.ffin is not null ")
					//JRR - 02/07/2009
					.append(" AND v.observ not like 'Imdemnizacion por Vacacion%') ");
					//
			
			if (incluyeProg) {
				strSQL.append( " or ( (v.licencia = '")
						.append( Constantes.VACACION_PROGRAMADA)
						.append( "' or v.licencia = '")
						.append( Constantes.REPROGRAMACION_VACACION ).append( "') ")
						.append( "      and v.est_id = '" ).append( Constantes.PROG_ACEPTADA)
						.append( "') ) ");
			} else {
				strSQL.append(" ) ");
			}

			if (!fechaIni.equals("")) {
				strSQL.append(" and (v.ffinicio >= DATE('")
						.append( Utiles.toYYYYMMDD(fechaIni) ).append( "')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and v.ffin <= DATE('" ).append( Utiles.toYYYYMMDD(fechaFin))
						.append( "'))");
			}

			if (numDias >= 0) {
				strSQL.append(" and v.dias >= ").append(""+numDias);
			}

			if (!incluyePend) {
				strSQL.append(" and v.ffin <= DATE('")
						.append( Utiles.toYYYYMMDD(Utiles.obtenerFechaActual()) ).append( "')");

				strSQL.append(" order by v.anno_vac, v.ffinicio");
			} else {
				strSQL.append(" order by v.ffinicio");
			}

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION);
			pre.setString(3, Constantes.VACACION_ESPECIAL);
			pre.setString(4, Constantes.VACACION_VENTA);
			//prac-asanchez 03/06/2009
			pre.setString(5, Constantes.VACACION_INDEMNIZADA);
			//
			rs = pre.executeQuery();
						
			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setAnno(rs.getString("anno_vac"));
				detalle.setFechaInicio(rs.getTimestamp("ffinicio"));
				detalle.setFechaFin(rs.getTimestamp("ffin"));
				detalle.setCantidad(rs.getInt("dias"));
				detalle.setDescripcion(rs.getString("observ")!=null ? rs.getString("observ").trim():"");
				detalle.setNombre(rs.getString("descrip"));
				detalle.setCodigo(rs.getString("licencia").trim());

				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}
	//dtarazona 3er entregable
	/**
	 * 
	 * @param dbpool
	 * @param anhoIni
	 * @param anhoFin
	 * @param criterio
	 * @param valor
	 * @param numDias
	 * @param seguridad
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findEstadisticoAnualVacaciones(String dbpool,
			String anhoIni, String anhoFin, String criterio, String valor,HashMap seguridad) throws SQLException {
		if(log.isDebugEnabled()) log.debug("method findEstadisticoAnualVacaciones");
		
		StringBuffer strSQL = new StringBuffer("");
		StringBuffer strSQL1 = new StringBuffer("");
		StringBuffer strSQL2 = new StringBuffer("");
		StringBuffer strSQL3 = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			boolean tieneWhere = false;

			strSQL1.append("select p.t02cod_uorg,u.t12des_corta, c.anno,c.saldo, (select count(*) from t1277solicitud sol, t1455sol_seg seg ")
					.append( " where sol.anno = seg.anno and sol.numero = seg.numero and sol.licencia = '46'and seg.estado_id = '2' ")
					.append( " and seg.accion_id = '3' and sol.cod_pers = c.cod_pers and sol.anno_vac = c.anno ) as sol_rechazadas, ")
					.append( " (case when DATE(c.anno||'/'||month(p.t02f_ingsun)||'/'||day(p.t02f_ingsun)) > current::date ")
					.append( " then DATE(c.anno||'/'||month(p.t02f_ingsun)||'/'||day(p.t02f_ingsun)) - current::date ")
					.append( " else 0 end ) as dias_trunc, nvl(sum(case when b.licencia='46' then b.dias else 0 end),0) as dias_venta, ")
					.append( " nvl(sum(case when b.licencia='07' then b.dias else 0 end),0) as dias_goce ")
					.append( " from t1281vacaciones_c c inner join t02perdp p on c.cod_pers = p.t02cod_pers inner join t12uorga u on p.t02cod_uorg = u.t12cod_uorga ")
					.append( " left join t1282vacaciones_d  b on c.cod_pers = b.cod_pers and c.anno = b.anno_vac and b.licencia in ('46','07') and b.est_id='1' where ");
			
			strSQL2.append("select ")
					.append(" p.t02cod_uorg, ")
					.append(" u.t12des_corta, ")
					.append(" c.anno, ")
					.append(" ( ")
					.append(" select count(*) from ")
					.append(" t1277solicitud sol, ")
					.append(" t1455sol_seg seg ")
					.append("where ")
					.append(" sol.anno = seg.anno ")
					.append(" and sol.numero = seg.numero ")
					.append(" and sol.licencia = '46' ")
					.append(" and seg.estado_id = '2' ")
					.append(" and seg.accion_id = '3' ")
					.append(" and sol.cod_pers = c.cod_pers ")
					.append(" and sol.anno_vac = c.anno ")
					.append(" ) as sol_venta_rechazadas, ")
					.append(" ( ")
					.append(" select count(*) from ")
					.append(" t1277solicitud sol, ")
					.append(" t1455sol_seg seg ")
					.append(" where ")
					.append(" sol.anno = seg.anno ")
					.append(" and sol.numero = seg.numero ")
					.append(" and sol.licencia = '07' ")
					.append(" and seg.estado_id = '2' ")
					.append(" and seg.accion_id = '3' ")
					.append(" and sol.cod_pers = c.cod_pers ")
					.append(" and sol.anno_vac = c.anno ")
					.append(" ) as sol_goce_rechazadas,")
					.append(" ( ")
					.append(" case when DATE(c.anno||'/'||month(p.t02f_ingsun)||'/'||day(p.t02f_ingsun)) > current::date ")
					.append(" then DATE(c.anno||'/'||month(p.t02f_ingsun)||'/'||day(p.t02f_ingsun)) - current::date ")
					.append(" else 0 end ")
					.append(" ) as dias_trunc, ")	
					.append(" sum(c.saldo) as saldo_total, ")
					.append(" ( ")
					.append(" case when year(current) = c.anno ")
					.append(" then (select count(t02cod_pers) from t02perdp where t02cod_stat='1' and t02cod_uorg=p.t02cod_uorg)- ")
					.append("    (select count(distinct cod_pers) from t1282vacaciones_d, t02perdp where cod_pers=t02cod_pers and licencia='49' and est_id='1' and t02cod_uorg=p.t02cod_uorg and anno_vac=c.anno) ")
					.append(" else 0 end")
					.append(" ) as trab_no_programa, ")
					.append(" nvl(sum(case when b.licencia='46' then b.dias else 0 end),0) as dias_venta, ")
					.append(" nvl(sum(case when b.licencia='07' then b.dias else 0 end),0) as dias_goce ")
					.append("from ")
					.append(" t02perdp p")
					.append(" inner join t1281vacaciones_c c on c.cod_pers = p.t02cod_pers ")
					.append(" inner join t12uorga u on p.t02cod_uorg = u.t12cod_uorga ")
					.append(" left join t1282vacaciones_d  b on c.cod_pers = b.cod_pers and c.anno = b.anno_vac and b.licencia in ('46','07') and b.est_id='1' ")
					.append("where ");
			
			strSQL3.append("select ")
					.append("    (")
					.append("    select count(sol.numero) from ")
					.append("        t1277solicitud sol, ")
					.append("        t1455sol_seg seg")
					.append("    where ")
					.append("        sol.anno = seg.anno")
					.append("        and sol.numero = seg.numero")
					.append("        and sol.licencia = '46'")
					.append("        and seg.estado_id = '2'")
					.append("        and seg.accion_id = '3'")
					.append("        and sol.u_organ = a.t02cod_uorg")
					.append("        and sol.anno_vac = a.anno")
					.append("    ) as sol_venta_rechazadas, ")
					.append("    (")
					.append("    select count(sol.numero) from ")
					.append("        t1277solicitud sol, ")
					.append("        t1455sol_seg seg")
					.append("    where ")
					.append("        sol.anno = seg.anno")
					.append("        and sol.numero = seg.numero")
					.append("        and sol.licencia = '07'")
					.append("        and seg.estado_id = '2'")
					.append("        and seg.accion_id = '3'")
					.append("        and sol.u_organ = a.t02cod_uorg")
					.append("        and sol.anno_vac = a.anno")
					.append("    ) as sol_goce_rechazadas,")
					.append("    a.t02cod_uorg,")
					.append("    a.t12des_corta,")
					.append("    a.anno,")
					.append("    a.dias_trunc,")
					.append("    a.trab_no_programa,")
					.append("    a.saldo_total,")
					.append("    a.dias_venta,")
					.append("    a.dias_goce ")
					.append("from ")
					.append("    (")
					.append("    select ")
					.append("        p.t02cod_uorg,")
					.append("        u.t12des_corta,")
					.append("        c.anno,")
					.append("        nvl(sum(")
					.append("            case when DATE(c.anno||'/'||month(p.t02f_ingsun)||'/'||day(p.t02f_ingsun)) > current::date ")
					.append("            then DATE(c.anno||'/'||month(p.t02f_ingsun)||'/'||day(p.t02f_ingsun)) - current::date ")
					.append("            else 0 end ")
					.append("        ),0) as dias_trunc,")
					.append("( ")
					.append("case when year(current) = c.anno ")
					.append("then (select count(t02cod_pers) from t02perdp where t02cod_stat='1' and t02cod_uorg=p.t02cod_uorg)- ")
					.append("    (select count(distinct cod_pers) from t1282vacaciones_d, t02perdp where cod_pers=t02cod_pers and licencia='49' and est_id='1' and t02cod_uorg=p.t02cod_uorg and anno_vac=c.anno) ")
					.append("else 0 end ")
					.append( ") as trab_no_programa, ")
					.append("        sum(c.saldo) as saldo_total,")
					.append("        nvl(sum(case when b.licencia='46' then b.dias else 0 end),0) as dias_venta, ")
					.append("        nvl(sum(case when b.licencia='07' then b.dias else 0 end),0) as dias_goce ")
					.append("    from ")
					.append("        t02perdp p ")
					.append("        inner join t1281vacaciones_c c on c.cod_pers = p.t02cod_pers ")
					.append("        inner join t12uorga u on p.t02cod_uorg = u.t12cod_uorga ")
					.append("        left join t1282vacaciones_d  b on c.cod_pers = b.cod_pers and c.anno = b.anno_vac and b.licencia in ('46','07') and b.est_id='1' ")
					.append("    where ");
					/*'        p.t02cod_stat = '1''+
					'        and p.t02cod_uorg like '1U23%' '+
					'        and c.anno between 2015 and 2018'+
					'    group by '+
					'        p.t02cod_uorg,'+
					'        c.anno, '+
					'        u.t12des_corta '+
					'    ) a order by a.t02cod_uorg';*/

			
					//.append( " where c.cod_pers = '1019' and p.t02cod_stat = '1' and c.anno<='2013' ");
			T02DAO persDAO = new T02DAO();
			HashMap persona=persDAO.findByCodPers(dbpool,(String) seguridad.get("codPers"));
			String uoJefe=(String)persona.get("t02cod_uuoo");
			while(uoJefe.endsWith("0"))
				uoJefe = uoJefe.substring(0, uoJefe.length()-1);
			if(log.isDebugEnabled()) log.debug("UOJEFE:"+uoJefe);
			//criterios de visibilidad
			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL1 .append( "  1=1 ");
					strSQL3 .append( "  1=1 ");
					tieneWhere = true;
				} /*else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL .append( "  ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "') ");
					
					      strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					tieneWhere = true;
				}*/ else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 //roles.get(Constantes.ROL_SECRETARIA) != null	||
						roles.get(Constantes.ROL_JEFE) != null) {
					//strSQL1 .append( "  substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "' ");	
					//strSQL3 .append( "  substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "' ");
					strSQL1 .append( "  p.t02cod_uorg like '").append( uoJefe ).append( "%' ");	
					strSQL3 .append( "  p.t02cod_uorg like '").append( uoJefe ).append( "%' ");
				} else {
					strSQL1 .append( " 1=2 ");	strSQL3 .append( " 1=2 ");				
				}
			}

			if (criterio.trim().equals("0")) {				
				strSQL.append(strSQL1);				
				strSQL .append( " and c.cod_pers = '" ).append( valor.trim().toUpperCase() ).append( "' and p.t02cod_stat = '1'");
			} else if (criterio.trim().equals("1")) {
				while(valor.endsWith("0"))
					valor = valor.substring(0, valor.length()-1);
				
				strSQL.append(strSQL3);
				strSQL .append( " and p.t02cod_stat = '1' and p.t02cod_uorg like '").append( valor.trim().toUpperCase() ).append( "%'");	
			}

			if (!anhoIni.trim().equals("")) {
				strSQL .append( " and (c.anno >= '" ).append( anhoIni ).append( "'");

				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and c.anno <= '" ).append( anhoFin ).append( "'");
				}

				strSQL .append( ")");
			} else {
				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and c.anno <= '" ).append( anhoFin ).append( "'");
				}
			}

			if (criterio.trim().equals("0")) {				
				strSQL .append( " group by c.anno,c.saldo,sol_rechazadas,dias_trunc,u.t12des_corta,p.t02cod_uorg order by c.anno desc");
			} else if (criterio.trim().equals("1")) {				
				/*strSQL.append( " group by ")
					  .append( "  p.t02cod_uorg, ")
					  .append( "  c.anno, ")
					  .append( "  sol_venta_rechazadas, ")
					  .append( "  sol_goce_rechazadas, ")
					  .append( "  dias_trunc, ")
					  .append( "  p.t02cod_uorg, u.t12des_corta ")
					  .append( " order by")
					  .append( "  p.t02cod_uorg desc, ")
					  .append( "  c.anno desc ");*/
				strSQL.append("    group by ")
				.append("        p.t02cod_uorg,")
				.append("        c.anno, ")
				.append("        u.t12des_corta ")
				.append("    ) a order by a.t02cod_uorg, a.anno desc");
			}
			
			if(log.isDebugEnabled()) log.debug("ConsultaEA:"+ strSQL);
			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			//pre.setInt(1, numDias);
			rs = pre.executeQuery();

			lista =new ArrayList();
			
			while (rs.next()) {
				if (criterio.trim().equals("0")) {
					Map m=new HashMap();
					m.put("dias_venta", rs.getString("dias_venta"));
					m.put("dias_trunc", rs.getString("dias_trunc"));
					m.put("dias_goce", rs.getString("dias_goce"));
					m.put("des_corta", rs.getString("t12des_corta"));
					m.put("anno", rs.getString("anno"));
					m.put("saldo", rs.getString("saldo"));
					m.put("sol_rechazadas", rs.getString("sol_rechazadas"));
					m.put("uuoo", rs.getString("t02cod_uorg"));
					lista.add(m);
				}else if (criterio.trim().equals("1")){
					Map m=new HashMap();
					m.put("dias_venta", rs.getString("dias_venta"));
					m.put("dias_trunc", rs.getString("dias_trunc"));
					m.put("dias_goce", rs.getString("dias_goce"));
					m.put("des_corta", rs.getString("t12des_corta"));
					m.put("anno", rs.getString("anno"));
					m.put("saldo", rs.getString("saldo_total"));
					m.put("sol_venta_rechazadas", rs.getString("sol_venta_rechazadas"));
					m.put("uuoo", rs.getString("t02cod_uorg"));
					m.put("sol_goce_rechazadas", rs.getString("sol_goce_rechazadas"));
					m.put("trab_no_programa", rs.getString("trab_no_programa"));
					lista.add(m);
				}
			}
			
		} catch (Exception e) {
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
		return lista;
	}
	
	/**
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers
	 * @param numDias
	 * @param incluyePend
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findPersonalNoGeneroSaldoVacacional(String dbpool,
			String fechaIni, String fechaFin, String criterio, String valor,HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {
			boolean tieneWhere = false;
			strSQL.append("select num_saldovac,cod_personal,num_anno,fec_saldovac,num_dias_labor,num_dias_descont,num_dias_exclic,fec_creacion,to_char(p.t02f_ingsun,'%d/%m/%Y') as t02f_ingsun,(trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as nombres,p.t02cod_uorg,u.t12des_corta ")
					.append( "from t9437saldovac s, t02perdp p,t12uorga u ");					
			
			//criterios de visibilidad
			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL .append( " where 1=1 ");
					tieneWhere = true;
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL .append( " where ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					tieneWhere = true;
				} else if (roles.get(Constantes.ROL_SECRETARIA) != null	|| roles.get(Constantes.ROL_JEFE) != null) {
					strSQL .append( " where substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "' ");
					tieneWhere = true;
				} else {
					strSQL .append( " where 1=2 ");
					tieneWhere = true;
				}
			}
			
			if(criterio.trim().equals("0") && !valor.trim().equals("")){
				strSQL.append(" and cod_personal='").append( valor.trim().toUpperCase() ).append( "'");
			}
			
			strSQL.append(" and p.t02cod_pers=s.cod_personal and u.t12cod_uorga=p.t02cod_uorg ");
			
			if (!fechaIni.equals("")) {
				strSQL.append(" and (fec_saldovac >= DATE('")
						.append( Utiles.toYYYYMMDD(fechaIni) ).append( "')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and fec_saldovac <= DATE('" ).append( Utiles.toYYYYMMDD(fechaFin))
						.append( "'))");
			}
			strSQL.append(" and ind_genera='0' order by p.t02cod_uorg,cod_personal");
			log.debug("Consulta:"+strSQL.toString());
			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(2, Constantes.VACACION);
			rs = pre.executeQuery();
						
			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) {
				detalle = new HashMap();
				detalle.put("num_saldovac", rs.getString("num_saldovac"));
				detalle.put("codUO",rs.getString("t02cod_uorg"));
				detalle.put("descUO",rs.getString("t12des_corta"));
				detalle.put("codPers",rs.getString("cod_personal"));
				detalle.put("nombres",rs.getString("nombres"));
				detalle.put("fecIngreso",rs.getString("t02f_ingsun"));
				detalle.put("annoVac",rs.getString("num_anno"));
				detalle.put("diasLabor",rs.getString("num_dias_labor").trim());
				detalle.put("diasDescont",rs.getString("num_dias_descont").trim());
				detalle.put("excesoEnf",rs.getString("num_dias_exclic").trim());
				detalle.put("fecGenera",rs.getString("fec_creacion").trim());

				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}
	/**
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers
	 * @param numDias
	 * @param incluyePend
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findDetalleSaldoVacacional(String dbpool,
			String numero) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {
			strSQL.append("select num_saldovacdet,num_saldovac,ind_saldovacdet,to_char(fec_saldovacdet,'%d/%m/%Y') as fechas ")
					.append( " from t9446saldovacdet where ");					
			
			strSQL.append(" num_saldovac='").append( numero.trim()).append( "'");
			
			strSQL.append(" order by ind_saldovacdet,fec_saldovacdet asc");
			log.debug("Consulta:"+strSQL.toString());
			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(2, Constantes.VACACION);
			rs = pre.executeQuery();
						
			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) {
				detalle = new HashMap();
				detalle.put("num_saldovac_d", rs.getString("num_saldovacdet"));
				detalle.put("num_saldovac",rs.getString("num_saldovac"));
				detalle.put("ind_saldovac_d",rs.getString("ind_saldovacdet"));
				detalle.put("dia",rs.getString("fechas"));

				lista.add(detalle);
			}
		} catch (Exception e) {
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
		return lista;
	}
	//dtarazona
	/**
	 * 
	 * @param dbpool
	 * @param anhoIni
	 * @param anhoFin
	 * @param criterio
	 * @param valor
	 * @param numDias
	 * @param seguridad
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAnhosVacacionesPendientes(String dbpool,
			String anhoIni, String anhoFin, String criterio, String valor,
			int numDias, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			boolean tieneWhere = false;

			strSQL.append("select distinct(anno) as anio ")
					.append( " from  t1281vacaciones_c ")
					.append( " where saldo > 0 and cod_pers in ")
					.append( "     (select v.cod_pers ")
					.append( "      from t1281vacaciones_c v, ")
					.append( "           t02perdp p ");

			//criterios de visibilidad
			if (seguridad != null) {
				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL .append( " where 1=1 ");
					tieneWhere = true;
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL .append( " where ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "') ");
					
					      strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					tieneWhere = true;
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					strSQL .append( " where substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "' ");
					tieneWhere = true;
				} else {
					strSQL .append( " where 1=2 ");
					tieneWhere = true;
				}
			}

			if (criterio.trim().equals("0")) {
				if (tieneWhere) {
					strSQL .append( " and ");
				} else {
					strSQL .append( " where ");
					tieneWhere = true;
				}

				strSQL .append( " v.cod_pers = '" ).append( valor.trim().toUpperCase() ).append( "'");
			} else if (criterio.trim().equals("1")) {
				if (tieneWhere) {
					strSQL .append( " and ");
				} else {
					strSQL .append( " where ");
					tieneWhere = true;
				}

				strSQL .append( " substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '")
						.append( valor.trim().toUpperCase() ).append( "'");
			}else if (criterio.trim().equals("5")) { //DTARAZONA 26/01/2018 - CONDICION AGREGADA PARA EL CASO DE INTENDENCIA
				if (tieneWhere) {
					strSQL .append( " and ");
				} else {
					strSQL .append( " where ");
					tieneWhere = true;
				}
				String intendencia = valor.substring(0,2);
				strSQL .append( " substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")			
				.append( intendencia.trim().toUpperCase() ).append( "%' ");
			}

			if (tieneWhere) {
				strSQL .append( " and ");
			} else {
				strSQL .append( " where ");
				tieneWhere = true;
			}

			strSQL .append( " p.t02cod_pers = v.cod_pers ");

			strSQL .append( " group by cod_pers having sum(saldo)> ?)");

			if (!anhoIni.trim().equals("")) {
				strSQL .append( " and (anno >= '" ).append( anhoIni ).append( "'");

				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and anno <= '" ).append( anhoFin ).append( "'");
				}

				strSQL .append( ")");
			} else {
				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and anno <= '" ).append( anhoFin ).append( "'");
				}
			}

			strSQL .append( " order by anno");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setInt(1, numDias);
			rs = pre.executeQuery();

			lista = new ArrayList();
			while (rs.next()) {
				lista.add(rs.getString("anio"));
			}
 
		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * 
	 * @param dbpool
	 * @param regimen
	 * @param anhoIni
	 * @param anhoFin
	 * @param criterio
	 * @param valor
	 * @param mayorA
	 * @param seguridad
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT02T12T99findPersVacaPendientes(String dbpool,
			String regimen, String anhoIni, String anhoFin, String criterio, String valor,//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
			int mayorA, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		PreparedStatement prePerson = null;
		Connection con = null;
		ResultSet rs = null;
		ResultSet rsPerson = null;
		ArrayList personal = null;

		try {
			strSQL.append("select cod_pers, sum(saldo) as saldo ")
					.append( " from t1281vacaciones_c")
					.append( " group by cod_pers having sum(saldo)> ? and ")
					.append( "          cod_pers in (select distinct(c.cod_pers) as codPers ")
					.append( "                       from   t1281vacaciones_c c, ")
					.append( "                              t02perdp p")
					.append( "                       where  p.t02cod_stat = ? and c.saldo > 0 ");

			if (!anhoIni.trim().equals("")) {
				strSQL .append( " and (anno >= '" ).append( anhoIni ).append( "'");

				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and anno <= '" ).append( anhoFin ).append( "'");
				}
				strSQL .append( ")");
			
			} else {
				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and anno <= '" ).append( anhoFin ).append( "'");
				}
			}

			if (criterio.equals("0")) {
				//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
				if (log.isDebugEnabled())log.debug("entro criterio Registro-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL .append( " and p.t02cod_rel not in (09,10) " );
				} else if (regimen.equals("09")){
					strSQL .append( " and p.t02cod_rel = '09' " );
				} else if (regimen.equals("10")){
					strSQL .append( " and p.t02cod_rel = '10' " );
				}
				//FIN - JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
				strSQL .append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase())
						.append( "'");
			}

			if (criterio.equals("1")) {
				//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
				if (log.isDebugEnabled())log.debug("entro criterio UUOO-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL .append( " and p.t02cod_rel not in (09,10) " );
				} else if (regimen.equals("09")){
					strSQL .append( " and p.t02cod_rel = '09' " );
				} else if (regimen.equals("10")){
					strSQL .append( " and p.t02cod_rel = '10' " );
				}
				//FIN - JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
				strSQL .append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '")
						.append( valor.trim().toUpperCase() ).append( "'");
			}
			
			//JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS
			if (criterio.equals("5")) {
				if(log.isDebugEnabled())log.debug("entro criterio INTEND-->");
				String intendencia = valor.substring(0,2);
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append(intendencia.trim().toUpperCase()).append("%'")
					.append( " and p.t02cod_rel not in (09,10) ");
				} else if (regimen.equals("09")){
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append(intendencia.trim().toUpperCase()).append("%'")
					.append( " and p.t02cod_rel = '09' " );
				} else if (regimen.equals("10")){
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append(intendencia.trim().toUpperCase()).append("%'")
					.append( " and p.t02cod_rel = '10' " );
				}
			}
			
			if (criterio.equals("3")) {
				if(log.isDebugEnabled())log.debug("entro criterio INSTITUC-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL.append( " and p.t02cod_rel not in (09,10) ");
				} else if (regimen.equals("09")){
					strSQL .append( " and p.t02cod_rel = '09' " );
				} else if (regimen.equals("10")){
					strSQL .append( " and p.t02cod_rel = '10' " );
				}
			}
			//FIN - JVILLACORTA 28/06/2011 - AOM:MODALIDADES FORMATIVAS

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL .append( " and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL .append( " and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "') ");
					
					    strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_JEFE) != null) {
					strSQL .append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '")
							.append( uoSeg ).append( "' ");
				} else {
					strSQL .append( " and 1 = 2 ");
				}
			}

			strSQL .append( " and p.t02cod_pers = c.cod_pers ");
			strSQL .append( " ) ");
			strSQL .append( " order by cod_pers ");
			
			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			log.debug("strSQL.toString():" + strSQL.toString());
			pre.setInt(1, mayorA);
			pre.setString(2, Constantes.ACTIVO);
			rs = pre.executeQuery();

			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				strSQL = new StringBuffer("");
				a = new BeanReporte();

				strSQL.append("select  p.T02ap_pate, p.T02ap_mate, p.T02nombres,")
						.append( "        p.T02f_ingsun, uo.t12des_corta, param.t99descrip, ")
						//EBV 28/11/2008
						.append( " (select g.fecha from t1456vacacion_gen g " )
					    .append( "  where g.cod_pers = p.t02cod_pers and est_id = '1' ) frepuesto ,")
					    //EBV 28/11/2008
						.append( "        substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, uo.t12des_uorga ")
						.append( "from    t02perdp p, ")
						.append( "        t12uorga uo, ")
						.append( "        t99codigos param ")
						.append( "where   p.t02cod_pers = ? and  ")
						.append( "        substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga and ")
						.append( "	       substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo")
						.append( "  and param.t99cod_tab = ? ");

				prePerson = con.prepareStatement(strSQL.toString());
				prePerson.setString(1, rs.getString("cod_pers"));
				prePerson.setString(2, Constantes.CODTAB_CATEGORIA);
				rsPerson = prePerson.executeQuery();
				log.debug("rsPerson:" + rsPerson);
				log.debug("cod_pers:" + rs.getString("cod_pers"));				
				a.setCodigo(rs.getString("cod_pers"));
				rsPerson.next();
				log.debug("t02ap_pate:" + rsPerson.getString("t02ap_pate"));
				log.debug("t02ap_mate:" + rsPerson.getString("t02ap_mate"));
				log.debug("t02nombres:" + rsPerson.getString("t02nombres"));
				String texto = rsPerson.getString("t02ap_pate").trim() .concat( " "
						.concat( rsPerson.getString("t02ap_mate").trim() .concat( ", "
						.concat( rsPerson.getString("t02nombres").trim()))));
				a.setNombre(texto);
				if ((rsPerson.getTimestamp("frepuesto")!=null) && (rsPerson.getTimestamp("frepuesto").toString()!="")) 
				{
					a.setFecha(Utiles.timeToFecha(rsPerson.getTimestamp("frepuesto")));
				}
				else
				{
					a.setFecha(Utiles.timeToFecha(rsPerson.getTimestamp("t02f_ingsun")));
				}
				
				a.setUnidad(rsPerson.getString("t12des_corta").trim());
				a.setCategoria(rsPerson.getString("cod_uorg").trim() .concat( " - ".concat( rsPerson.getString("t12des_uorga").trim())));

				personal.add(a);

				if (rsPerson != null) {
					rsPerson.close();
				}

				if (prePerson != null) {
					prePerson.close();
				}

			}
 


		} catch (Exception e) {
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
		return personal;
	}

	/**
	 * 
	 * @param dbpool
	 * @param codPers
	 * @param saldoFavor
	 * @param anhoIni
	 * @param anhoFin
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAnhosPendByCodPersSaldoAnno(String dbpool,
			String codPers, boolean saldoFavor, String anhoIni, String anhoFin)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {
			strSQL.append("select cod_pers, anno, dias, saldo")
					.append( " from t1281vacaciones_c where cod_pers = ? ");

			if (saldoFavor) {
				strSQL .append( " and saldo > 0");
			}

			if (!anhoIni.trim().equals("")) {
				strSQL .append( " and (anno >= " ).append( anhoIni.trim());

				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and anno <= " ).append( anhoFin.trim() ).append( ")");
				} else {
					strSQL .append( ")");
				}
			} else {
				if (!anhoFin.trim().equals("")) {
					strSQL .append( " and anno <= " ).append( anhoFin.trim());
				}
			}

			strSQL .append( " order by anno");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

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
	 * @param fechaIni
	 * @param mayorA
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findVacPendByCodPersAnhoDias(String dbpool,
			String codPers, String fechaIni, int mayorA) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select cod_pers, ffinicio, ffin, dias")
					.append( " from t1282vacaciones_d where cod_pers = ?")
					.append( " and (licencia = ? or licencia = ?)")
					.append( " and ffinicio >= DATE('" ).append( Utiles.toYYYYMMDD(fechaIni))
					.append( "')" ).append( " and dias >= ?");

			strSQL .append( " order by ffinicio");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION);
			pre.setString(3, Constantes.VACACION_ESPECIAL);
			pre.setInt(4, mayorA);

			rs = pre.executeQuery();
			vacaciones = new ArrayList();

			BeanReporte bRepo = null;
			while (rs.next()) {

				bRepo = new BeanReporte();

				bRepo.setCodigo(rs.getString("cod_pers"));
				bRepo.setFechaInicio(rs.getTimestamp("ffinicio"));
				bRepo.setFechaFin(rs.getTimestamp("ffin"));
				bRepo.setCantidad(rs.getInt("dias"));

				vacaciones.add(bRepo);
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
	 * @param fechaIni
	 * @param fechaFin
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findCompVacByCodPersFFiniFFin(String dbpool,
			String codPers, String fechaIni, String fechaFin)
			throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select cod_pers, anno_vac, ffinicio, ffin, dias, observ")
					.append( " from t1282vacaciones_d" ).append( " where cod_pers = ?")
					.append( " and licencia = ? ");

			if (!fechaIni.trim().equals("")) {
				strSQL .append( " and (ffinicio >= DATE('")
						.append( Utiles.toYYYYMMDD(fechaIni)) .append( "')");

				if (!fechaFin.trim().equals("")) {
					strSQL .append( " and ffinicio <= DATE('")
							.append( Utiles.toYYYYMMDD(fechaFin) ).append( "'))");
				}
			} else {
				if (!fechaFin.trim().equals("")) {
					strSQL .append( " and ffinicio <= DATE('")
							.append( Utiles.toYYYYMMDD(fechaFin) ).append( "')");
				}
			}

			strSQL .append( " order by anno_vac, ffinicio");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION_VENTA);

			rs = pre.executeQuery();
			vacaciones = new ArrayList();

			BeanReporte bRepo = null;
			while (rs.next()) {

				bRepo = new BeanReporte();

				bRepo.setCodigo(rs.getString("cod_pers"));
				bRepo.setAnno(rs.getString("anno_vac"));
				bRepo.setFechaInicio(rs.getTimestamp("ffinicio"));
				bRepo.setFechaFin(rs.getTimestamp("ffin"));
				bRepo.setCantidad(rs.getInt("dias"));
				bRepo.setDescripcion(rs.getString("observ").trim());

				vacaciones.add(bRepo);
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
	 * @param codUOrg
	 * @param fechaIni
	 * @param fechaFin
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAcumVacByCodUOrgFFiniFFin(String dbpool,
			String codUOrg, String fechaIni, String fechaFin)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList vacaciones = null;

		try {

			strSQL.append("select v.licencia, sum(v.dias) as dias")
					.append( " from t02perdp p, ")
					.append( " t1282vacaciones_d v ")
					.append( " where substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = ? and")
					.append( "       v.licencia != '" ).append( Constantes.VACACION_SUSPENDIDA)
					.append( "' and " ).append( "       (v.ffinicio >= DATE('")
					.append( Utiles.toYYYYMMDD(fechaIni) ).append( "')")
					.append( "        and v.ffin <= DATE('")
					.append( Utiles.toYYYYMMDD(fechaFin) ).append( "')) and ")
					.append( "       v.cod_pers = p.t02cod_pers ")
					.append( " group by v.licencia");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codUOrg);

			rs = pre.executeQuery();
			vacaciones = new ArrayList();

			BeanReporte bRepo = null;
			while (rs.next()) {

				bRepo = new BeanReporte();

				bRepo.setCodigo(rs.getString("licencia"));
				bRepo.setCantidad(rs.getInt("dias"));

				vacaciones.add(bRepo);
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
	 * @param codPers
	 * @param fecha
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findInasistenciasFecha(String dbpool, String codUO,
			String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;
		String fec = "";
		try {

			System.out.print("ANTES DEL FOPRMATEO fecha " + fecha);
			
			fec = Utiles.toYYYYMMDD(fecha);

			System.out.print("DESPUES DEL FORMATEO fec" + fec);
			
			strSQL.append("select p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "        c.t99descrip categoria ")
					.append( "from    t1454asistencia_d d, ")
					.append( "        t02perdp p, ")
					.append( "        t99codigos c ")
					.append( "where   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = ? ")
					.append( "        and d.fecha = DATE('").append( fec).append( "')  ")
					.append( "        and d.mov = ?  ")
					.append( "        and c.t99tip_desc = ?  ")
					.append( "        and c.t99cod_tab = ?  ")
					.append( "        and d.cod_pers = p.t02cod_pers ")
					.append( "        and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = c.t99codigo");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codUO);
			pre.setString(2, Constantes.MOV_INASISTENCIA);
			pre.setString(3, Constantes.T99DETALLE);
			pre.setString(4, Constantes.CODTAB_CATEGORIA);
			rs = pre.executeQuery();
			detalle = new ArrayList();

			BeanReporte det = null;
			while (rs.next()) {

				det = new BeanReporte();

				String texto = rs.getString("t02ap_pate").trim() .concat( " "
						.concat( rs.getString("t02ap_mate").trim() .concat( ", "
						.concat( rs.getString("t02nombres").trim()))));

				det.setCodigo(rs.getString("t02cod_pers").trim());
				det.setNombre(texto);
				det.setUnidad(rs.getString("categoria").trim());
				det.setHora("1");

				detalle.add(det);
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
		return detalle;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findInasistenciasPeriodoNumDias(String dbpool,
			String codUO, String periodo, String numDias) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;
		try {
		
			strSQL.append("select  p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "        c.t99descrip categoria, ")
					.append( "        sum(r.total) total ")
					.append( "from    t1272asistencia_r r, ")
					.append( "        t02perdp p, ")
					.append( "        t99codigos c ")
					.append( "where   r.u_organ = ? ")
					//.append(		 "substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = ? ")
					.append( "        and r.mov = ?  ")
					.append( "        and r.periodo = ? ")
					.append( "        and total >= ? ")
					.append( "        and c.t99tip_desc = ?  ")
					.append( "        and c.t99cod_tab = ?  ")
					.append( "        and r.cod_pers = p.t02cod_pers ")
					.append( "        and p.t02cod_uorg = ? ") 
					.append( "        and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = c.t99codigo ")
					.append( "group by 1, 2, 3, 4, 5 ");

			int num = 0;

			try {
				num = Integer.parseInt(numDias);
			} catch (Exception e) {

			}

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codUO);
			pre.setString(2, Constantes.MOV_INASISTENCIA);
			pre.setString(3, periodo);
			pre.setInt(4, num);
			pre.setString(5, Constantes.T99DETALLE);
			pre.setString(6, Constantes.CODTAB_CATEGORIA);
			pre.setString(7, codUO);
			rs = pre.executeQuery();
			detalle = new ArrayList();

			BeanReporte det = null;
			while (rs.next()) {

				det = new BeanReporte();

				String texto = rs.getString("t02ap_pate").trim() .concat( " ")
						.concat( rs.getString("t02ap_mate").trim() ).concat( ", ")
						.concat( rs.getString("t02nombres").trim());

				det.setCodigo(rs.getString("t02cod_pers").trim());
				det.setNombre(texto);
				det.setUnidad(rs.getString("categoria").trim());
				det.setHora(rs.getString("total"));

				detalle.add(det);
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
		return detalle;
	}

	/**
	 * 
	 * @param dbpool
	 * @param periodo
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT1444T1279T99ByCodPersPeriodo(String dbpool,
			String periodo, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList detalle = null;

		try {
			strSQL.append("select d.cod_pers, d.periodo, d.total, d.period_reg,")
					.append( "       t.descrip, c.t99abrev, d.observ")
					.append( " from  t1444devol d, ")
					.append( "       t1279tipo_mov t, ")
					.append( "       t99codigos c")
					.append( " where c.t99codigo[1, 2] = t.medida and c.t99cod_tab = ? and t99tip_desc = ? and ")
					.append( "       d.cod_pers = ? and ")
					.append( "       d.period_reg = ? and " ).append( "       t.mov = d.mov ")
					.append( " order by t.descrip");

			con = getConnection(dbpool);
//			con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.CODTAB_MEDIDA);
			pre.setString(2, Constantes.T99DETALLE);
			pre.setString(3, codPers.toUpperCase());
			pre.setString(4, periodo);
			rs = pre.executeQuery();
			detalle = new ArrayList();

			BeanReporte bRepo = null;
			while (rs.next()) {

				bRepo = new BeanReporte();

				bRepo.setCodigo(rs.getString("cod_pers"));
				bRepo.setAnno(rs.getString("period_reg"));
				bRepo.setHora(rs.getString("periodo"));
				bRepo.setNombre(rs.getString("descrip").trim());
				bRepo.setUnidad(rs.getString("total") .concat( " "
						.concat( rs.getString("t99abrev"))));
				bRepo.setDescripcion(rs.getString("observ").trim());

				detalle.add(bRepo);
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
		return detalle;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findSinTurno(String dbpool, String fechaIni,
			String fechaFin, String codUO) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ");
			strSQL .append( "         c.t99descrip categoria  ");
			strSQL .append( "from 	t02perdp p, t99codigos c ");
			strSQL .append( "where 	substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '")
					.append( codUO.trim() ).append( "' and p.t02cod_stat = ? and ");
			strSQL .append( "         c.t99cod_tab = ? and c.t99tip_desc = ? and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = c.t99codigo and p.t02cod_pers not in ( ");
			strSQL .append( "select 	tp.cod_pers from t1270turnoperson tp ");
			strSQL .append( "where	tp.cod_pers = p.t02cod_pers and tp.est_id = ? ");

			if (!fechaIni.equals("") && !fechaFin.equals("")) {

				BeanFechaHora fIni = new BeanFechaHora(fechaIni, "dd/MM/yyyy");
				BeanFechaHora fFin = new BeanFechaHora(fechaFin, "dd/MM/yyyy");

				strSQL .append( " and ( ");
				strSQL .append( " ( tp.fini = DATE('"+fIni.getSQLDate()+"') ) or ");
				strSQL .append( " ( tp.fini = DATE('"+fFin.getSQLDate()+"') ) or ");
				strSQL .append( " ( tp.ffin = DATE('"+fIni.getSQLDate()+"') ) or ");
				strSQL .append( " ( tp.ffin = DATE('"+fFin.getSQLDate()+"') ) or ");
				strSQL .append( " ( (DATE('"+fFin.getSQLDate()+"') > tp.fini) and ( DATE('"+fFin.getSQLDate()+"') < tp.ffin ) ) or ");
				strSQL .append( " ( (DATE('"+fIni.getSQLDate()+"') > tp.fini) and ( DATE('"+fIni.getSQLDate()+"') < tp.ffin ) ) or ");
				strSQL .append( " ( (DATE('"+fIni.getSQLDate()+"') > tp.fini) and ( DATE('"+fFin.getSQLDate()+"') < tp.ffin ) ) or ");
				strSQL .append( " ( (DATE('"+fIni.getSQLDate()+"') < tp.fini) and ( DATE('"+fFin.getSQLDate()+"') > tp.ffin ) ) ");
				strSQL .append( " ) ");

			} else {
				strSQL .append( " and (1 = 2) ");
			}

			strSQL .append( " ) order by p.t02cod_pers");

			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			

			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, Constantes.CODTAB_CATEGORIA);
			pre.setString(3, Constantes.T99DETALLE);
			pre.setString(4, Constantes.ACTIVO);
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) {

				detalle = new HashMap();

				String texto = rs.getString("t02ap_pate").trim() .concat( " "
						.concat( rs.getString("t02ap_mate").trim() .concat( ", "
						.concat( rs.getString("t02nombres").trim()))));

				detalle.put("codpers", rs.getString("t02cod_pers"));
				detalle.put("nombre", texto);
				detalle.put("categoria", rs.getString("categoria"));

				lista.add(detalle);
			}

		} catch (Exception e) {
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
		return lista;
	}

	//PRAC-ASANCHEZ 18/06/2009
	/**
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findPersonalSinTurno(String dbpool, String fechaIni,
			String fechaFin, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ");
			strSQL .append( "         c.t99descrip categoria  ");
			strSQL .append( "from 	t02perdp p, t99codigos c ");
			strSQL .append( "where 	p.t02cod_pers = '")
					.append( codPers.trim() ).append( "' and p.t02cod_stat = ? and ");
			strSQL .append( "         c.t99cod_tab = ? and c.t99tip_desc = ? and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = c.t99codigo and p.t02cod_pers not in ( ");
			strSQL .append( "select 	tp.cod_pers from t1270turnoperson tp ");
			strSQL .append( "where	tp.cod_pers = p.t02cod_pers and tp.est_id = ? ");

			if (!fechaIni.equals("") && !fechaFin.equals("")) {

				BeanFechaHora fIni = new BeanFechaHora(fechaIni, "dd/MM/yyyy");
				BeanFechaHora fFin = new BeanFechaHora(fechaFin, "dd/MM/yyyy");

				strSQL .append( " and ( ");
				strSQL .append( " ( tp.fini = DATE('"+fIni.getSQLDate()+"') ) or ");
				strSQL .append( " ( tp.fini = DATE('"+fFin.getSQLDate()+"') ) or ");
				strSQL .append( " ( tp.ffin = DATE('"+fIni.getSQLDate()+"') ) or ");
				strSQL .append( " ( tp.ffin = DATE('"+fFin.getSQLDate()+"') ) or ");
				strSQL .append( " ( (DATE('"+fFin.getSQLDate()+"') > tp.fini) and ( DATE('"+fFin.getSQLDate()+"') < tp.ffin ) ) or ");
				strSQL .append( " ( (DATE('"+fIni.getSQLDate()+"') > tp.fini) and ( DATE('"+fIni.getSQLDate()+"') < tp.ffin ) ) or ");
				strSQL .append( " ( (DATE('"+fIni.getSQLDate()+"') > tp.fini) and ( DATE('"+fFin.getSQLDate()+"') < tp.ffin ) ) or ");
				strSQL .append( " ( (DATE('"+fIni.getSQLDate()+"') < tp.fini) and ( DATE('"+fFin.getSQLDate()+"') > tp.ffin ) ) ");
				strSQL .append( " ) ");

			} else {
				strSQL .append( " and (1 = 2) ");
			}

			strSQL .append( " ) ");

			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			

			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, Constantes.CODTAB_CATEGORIA);
			pre.setString(3, Constantes.T99DETALLE);
			pre.setString(4, Constantes.ACTIVO);
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) {

				detalle = new HashMap();

				String texto = rs.getString("t02ap_pate").trim() .concat( " "
						.concat( rs.getString("t02ap_mate").trim() .concat( ", "
						.concat( rs.getString("t02nombres").trim()))));

				detalle.put("codpers", rs.getString("t02cod_pers"));
				detalle.put("nombre", texto);
				detalle.put("categoria", rs.getString("categoria"));

				lista.add(detalle);
			}
			if(log.isDebugEnabled()) log.debug("Consulta2:"+strSQL);
		} catch (Exception e) {
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
		return lista;
	}
	//
	//dtarazona modificacion del reporte de personal sin turno
	/**
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findTurnosActivosPorCodigo(String dbpool, String codPers,String fechaIni,String fechaFin) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {
			strSQL.append("SELECT u_organ,turno,to_char(fini,'%d/%m/%Y') as fini,to_char(ffin,'%d/%m/%Y') ffin FROM t1270turnoperson WHERE cod_pers=? ");
			
			
			if (!fechaIni.equals("") && !fechaFin.equals("")) {

				BeanFechaHora fIni = new BeanFechaHora(fechaIni, "dd/MM/yyyy");
				BeanFechaHora fFin = new BeanFechaHora(fechaFin, "dd/MM/yyyy");

				strSQL .append( " AND ffin > DATE('"+fIni.getSQLDate()+"') AND fini < DATE('"+fFin.getSQLDate()+"') ORDER BY date(ffin) asc");
			} else {
				strSQL .append( " and (1 = 2) ");
			}

			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());

			pre.setString(1, codPers);
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) 
			{
				detalle = new HashMap();
				detalle.put("uuoo", rs.getString("u_organ"));
				detalle.put("codTurno", rs.getString("turno"));
				detalle.put("fini", rs.getString("fini"));
				detalle.put("ffin", rs.getString("ffin"));
				lista.add(detalle);
			}
			if(log.isDebugEnabled()) log.debug("Consulta2:"+strSQL);
		} catch (Exception e) {
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
		return lista;
	}
	//fin dtarazona
	/**
	 * Metodo encargado de listar las marcaciones realizadas por un trabajador
	 * dentro de un rango de fechas y filtradas por hora de marcacion Join de
	 * las tablas T1275Marcacion y T1280Tipo_Reloj.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param fechaIni
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fechaFin
	 *            String. Fecha superior del rango de busqueda.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @param horaMarca
	 *            String. Hora de marca.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanReporte.
	 * @throws SQLException
	 */
	public ArrayList findMarcacionesPersonal(String dbpool, String fechaIni,
			String fechaFin, String codPers, String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			BeanFechaHora bfh = new BeanFechaHora();
			//String mesActual = bfh.getMes();
			//String anhoActual = bfh.getAnho();
			bfh.getCalendar().roll(Calendar.MONTH, -2);
			//String mes = bfh.getMes();
			//String anho = anhoActual;
			//if (mesActual.compareTo(mes) < 0) anho = ""+(Integer.parseInt(anhoActual) - 1);
			//String fecha = anho +"/"+mes+"/01";
			//String fecha = "2006" +"/"+"05"+"/06";
			//DIVA-2006-57746 30/05/2006 - EBV
			//String fecha = "2006" +"/"+"06"+"/01";
			strSQL.append(" select 	m.fecha, m.hora, r.descrip, m.reloj ")
				  .append(" from  	t1275marcacion m, ")
				  .append("         t1280tipo_reloj r ")
				  .append(" where	m.cod_pers = ?  ");

			if (!fechaIni.equals("")) {
				strSQL .append( " and m.fecha >= DATE('").append( Utiles.toYYYYMMDD(fechaIni) ).append( "') ");
			}
			if (!fechaFin.equals("")) {
				strSQL .append( " and m.fecha <= DATE('").append( Utiles.toYYYYMMDD(fechaFin) ).append( "') ");
			}
			strSQL .append( " and m.fecha >= DATE(?)");
			strSQL .append( " and m.sdel = ? and m.reloj = r.reloj ");
			strSQL .append( " order by 1 desc, 2 ");

			log.debug("QUERY : "+strSQL.toString());
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, fecha.trim());
			pre.setString(3, Constantes.ACTIVO);

			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setFecha(new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));
				String dia = Utiles.dameDiaSemana(detalle.getFecha());
				detalle.setNombre(detalle.getFecha()+" "+dia);
				detalle.setHora(rs.getString("hora"));
				detalle.setDescripcion(rs.getString("descrip").trim());

				lista.add(detalle);
			}


		} catch (Exception e) {
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
		return lista;
	}

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findPapeletas(HashMap datos) throws SQLException {

		log.debug("Reporte Papeletas-ingreso a findPapeletas(datos): "+datos);
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
		T1270DAO turnoDAO = new T1270DAO();	
		T1276DAO periodoDAO = new T1276DAO();	
		T02DAO persDAO = new T02DAO();	
		BeanTurnoTrabajo turno1 = null;
		BeanTurnoTrabajo turno2 = null;
		BeanTurnoTrabajo turno3 = null;
		boolean visualizar = true;
		String s_fecha = "";
		String s_fechaAnt = "";
		HashMap hmPers =null;
		String cod_rel="-";
		String periodo = "";
		Map params = new HashMap();
		boolean bPeriodoCerrado = false;
		//FIN 

		try {

			String dbpool = (String) datos.get("dbpool");
			String fechaIni = (String) datos.get("fechaIni");
			String fechaFin = (String) datos.get("fechaFin");
			String estado = (String) datos.get("estado");
			String papeleta = (String) datos.get("papeleta");
			String cod_pers = (String) datos.get("cod_pers");

			strSQL.append("select     a.fing, a.hing, a.hsal, ")
					.append( "        m.descrip, a.fcreacion, c.t99descrip, a.fmod, a.jefe_autor ")
					.append( "from    t1271asistencia a, ")
					.append( "        t1279tipo_mov m, ")
					//.append( "        t99codigos c ") ICAPUNAY 06/09/2012  ATENDER SAU20124F310000200 (Demora Consulta reporte papeletas)
					.append( " outer   t99codigos c ") //ICAPUNAY 06/09/2012  ATENDER SAU20124F310000200 (Demora Consulta reporte papeletas)
					.append( "where   a.cod_pers = ? ");

			if (!papeleta.equals("-1")) {
				strSQL .append( " and a.mov = '" ).append( papeleta ).append( "' ");
			}
			if (!fechaIni.equals("")) {
				strSQL .append( " and a.fing >= DATE('").append( Utiles.toYYYYMMDD(fechaIni) ).append( "') ");
			}
			if (!fechaFin.equals("")) {
				strSQL .append( " and a.fing <= DATE('").append( Utiles.toYYYYMMDD(fechaFin) ).append( "') ");
			}
			if (!estado.equals("-1")) {
				strSQL .append( " and a.estado_id = '" ).append( estado ).append( "' ");
			}
			//ICAPUNAY 06/09/2012  ATENDER SAU20124F310000200 (Demora Consulta reporte papeletas)
			else{ //todas las papeletas
				strSQL .append( " and a.estado_id <> '" ).append(Constantes.ASISTENCIA_CALIFICADA ).append( "' ");
			}
			//FIN ICAPUNAY 06/09/2012  ATENDER SAU20124F310000200 (Demora Consulta reporte papeletas)

			strSQL .append( " and m.tipo_id = ? and c.t99cod_tab = '507' ");
			strSQL .append( " and c.t99tip_desc='D' "); //ICAPUNAY 06/09/2012  ATENDER SAU20124F310000200 (Demora Consulta reporte papeletas)
			strSQL .append( " and a.mov = m.mov ");
			strSQL .append( " and a.estado_id = c.t99codigo ");
			strSQL .append( " order by 1, a.hing, a.fcreacion ");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());			
			pre.setString(1, cod_pers.trim().toUpperCase());
			pre.setString(2, Constantes.TIPO_MOV_PAPELETA);			
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			
			//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			hmPers = persDAO.findByCodPers(dbpool, cod_pers.trim().toUpperCase());
			cod_rel=hmPers!=null?hmPers.get("t02cod_rel").toString():"-";
						
			params.put("dbpool", dbpool);			
			//FIN 
			
			while (rs.next()) {
				//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				s_fecha = new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy");
				log.debug("s_fecha: "+s_fecha);
				visualizar = true;
				turno1 = null;
				turno2 = null;
				turno3 = null;
				
				if(!cod_rel.equals("-")) {
					if(cod_rel.equals("09")) {//regimen 1057 (CAS)
						periodo = periodoDAO.findByFechaCAS(dbpool, s_fecha);	
						log.debug("periodo CAS: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoCAS(params);
						log.debug("bPeriodoCerrado CAS: "+bPeriodoCerrado);
					} else if(cod_rel.equals("10")) {//Modalidades Formativas 
						periodo = periodoDAO.findByFechaModFormativas(dbpool, s_fecha);	
						log.debug("periodo formativas: "+periodo);
						params.put("periodo", periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerradoModFormativa(params);
						log.debug("bPeriodoCerrado Formativa: "+bPeriodoCerrado);												
					} else { //regimen 276-728
						periodo = periodoDAO.findByFecha(dbpool, s_fecha);
						log.debug("periodo 276-728: "+periodo);
						params.put("periodo",periodo);
						bPeriodoCerrado = periodoDAO.isPeriodoCerrado(params);
						log.debug("bPeriodoCerrado 276-728: "+bPeriodoCerrado);	
					}
				}
				//FIN ICAPUNAY - PAS20165E230300132

				detalle = new HashMap();

				String hIng = rs.getString("hing");
				String hSal = rs.getString("hsal");

				if (hSal != null && !hSal.trim().equals("")) {
					detalle.put("hora", hIng+" - "+hSal);
				} else {
					detalle.put("hora", hIng);
				}

				//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				turno1 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm1dia(dbpool,cod_pers.trim(),s_fecha);		    
				if (turno1!=null && bPeriodoCerrado){
					visualizar = false;	//no se debe visualizar informacion de s_fecha
					log.debug("NO visualizar informacion de fecha: "+s_fecha);
				}else{
					turno2 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,cod_pers.trim(),s_fecha);
					if (turno2!=null && bPeriodoCerrado){
						visualizar = false;	//no se debe visualizar informacion de s_fecha
						log.debug("NO visualizar informacion1 de fecha: "+s_fecha);
					}else{
						s_fechaAnt = Utiles.dameFechaAnterior(s_fecha, 1);
						log.debug("s_fechaAnt: "+s_fechaAnt);
						turno3 = turnoDAO.joinWithT45ByCodPersFecha_NoControlado_ActInact_OperAdm2dias(dbpool,cod_pers.trim(),s_fechaAnt);
						if (turno3!=null && bPeriodoCerrado){
							visualizar = false; //no se debe visualizar informacion de s_fecha
							log.debug("NO visualizar informacion2 de fecha: "+s_fecha);
						}						
					}							    
				}			
				if (visualizar==true){
					log.debug("SI visualizar informacion de fecha: "+s_fecha);
				//FIN ICAPUNAY	
					//detalle.put("fecha", new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy")); //ICAPUNAY - PAS20165E230300132 - MSNAAF071
					detalle.put("fecha", s_fecha); //ICAPUNAY - PAS20165E230300132 - MSNAAF071
					detalle.put("papeleta", rs.getString("descrip"));
					detalle.put("estado", rs.getString("t99descrip"));
					detalle.put("fmod", new BeanFechaHora(rs.getDate("fmod")).getFormatDate("dd/MM/yyyy"));
					detalle.put("jefe_autor", rs.getString("jefe_autor"));
					log.debug("detalle: "+detalle);
					lista.add(detalle);
				}//add esta linea //ICAPUNAY - PAS20165E230300132 - MSNAAF071				
			}

		} catch (Exception e) {			
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
		return lista;
	}

	/**
	 * 
	 * @param dbpool
	 * @param periodo
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public HashMap findResumenMensual(String dbpool, String periodo,
			String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap detalle = new HashMap();

		try {
			strSQL.append("select  r.mov, r.total, m.medida ")
					.append( "from    t1272asistencia_r r, ")
					.append( "        t1279tipo_mov m ")
					.append( "where   r.cod_pers = ? and ")
					.append( "        r.periodo = ? and ")
					.append( "        r.est_id = ? and ")
					.append( "        m.califica = ? and " ).append( "        r.mov = m.mov ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setString(3, Constantes.ACTIVO);
			pre.setString(4, Constantes.DESCUENTO);
			rs = pre.executeQuery();

			while (rs.next()) {

				String mov = rs.getString("mov");
				String valor = rs.getString("total");
				String medida = rs.getString("medida");

				float total = Float.parseFloat(valor);

				if (medida.equals(Constantes.HORA))
					total = total * 60;
				if (medida.equals(Constantes.DIA))
					total = total * 60 * 8;
				if (medida.equals(Constantes.MES))
					total = total * 60 * 8 * 20;
				if (medida.equals(Constantes.ANNO))
					total = total * 60 * 8 * 20 * 240;

				detalle.put(mov, new Float(total));

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
		return detalle;
	}

	/**
	 * 
	 * @param dbpool
	 * @param periodo
	 * @param codPers
	 * @return @throws
	 *         SQLException
	 */
	public HashMap findDevoluciones(String dbpool, String periodo,
			String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		HashMap detalle = new HashMap();

		try {
			strSQL.append("select  d.mov, d.total, m.medida ")
					.append( "from    t1444devol d, " ).append( "        t1279tipo_mov m ")
					.append( "where   d.cod_pers = ? and ")
					.append( "        d.periodo = ? and ")
					.append( "        m.califica = ? and " ).append( "        d.mov = m.mov ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, periodo);
			pre.setString(3, Constantes.DESCUENTO);
			rs = pre.executeQuery();

			while (rs.next()) {

				String mov = rs.getString("mov");
				String valor = rs.getString("total");
				String medida = rs.getString("medida");

				float total = Float.parseFloat(valor);

				if (medida.equals(Constantes.HORA))
					total = total * 60;
				if (medida.equals(Constantes.DIA))
					total = total * 60 * 8;
				if (medida.equals(Constantes.MES))
					total = total * 60 * 8 * 20;
				if (medida.equals(Constantes.ANNO))
					total = total * 60 * 8 * 20 * 240;

				detalle.put(mov, new Float(total));

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
		return detalle;
	}
	
//JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS
	
	/** findPersonalByCriterioValorPersonalRegimen
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */	
	public ArrayList joinWithT02T12T99findPersonalByCriterioValorPersonalRegimen(
			String dbpool, String regimen, String criterio, String valor, HashMap seguridad)
			throws SQLException {
		if(log.isDebugEnabled())log.debug("dbpool:" + dbpool);
		if(log.isDebugEnabled())log.debug("regimen:" + regimen);
		if(log.isDebugEnabled())log.debug("criterio:" + criterio);
		if(log.isDebugEnabled())log.debug("valor:" + valor);
		if(log.isDebugEnabled())log.debug("seguridad:" + seguridad);
		
		log.debug("joinWithT02T12T99findPersonalByCriterioValorPersonalRegimen ingreso");
		
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	    ArrayList aLRpta = new ArrayList();
	    Utilidades common = new Utilidades();
	    String strSQL2 = "";
	    PreparedStatement pre2 = null;
	    Connection con2 = null;
	    ResultSet rs2 = null;
    	
	    HashMap hm = null;
	    String codUoJef = "";	 
	    //ICAPUNAY - PAS20165E230300132
	    
		try {
			//JVV - se agrego t02cod_rel
			strSQL.append("select   p.t02cod_rel, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip, p.t02lib_elec ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ");
					//.append( "where   p.t02cod_stat = ? ");// JVILLACORTA 24/08/2011
			
			/*if (criterio.equals("0")) {
				strSQL.append( "where   p.t02cod_stat in (?,?) "); // JVILLACORTA 24/08/2011 (ver INACTIVOS tambiÃƒÂ©n para todos los regimenes pero solo por el criterio REGISTRO)
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}*/
			
			//JVV-ini
			if (criterio.equals("0")) {
				strSQL.append( "where   p.t02cod_stat in (?,?) "); // JVILLACORTA 24/08/2011 (ver INACTIVOS tambiÃƒÂ©n para todos los regimenes pero solo por el criterio REGISTRO)
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
				if (regimen.equals("es276")) {
					//JVV - 23/05/2011
					strSQL.append( "and p.t02cod_rel not in (?,?) ");	//in (09,10)
				} else {
					if (regimen.equals("es1057")) {
						strSQL.append( "and p.t02cod_rel = ? "); //= '09'
					} else {
						strSQL.append( "and p.t02cod_rel = ? "); //= '10'
					}					
				}//								
			} 		

			if (criterio.equals("1")) {
				strSQL.append( "where   p.t02cod_stat = ? "); // JVILLACORTA 24/08/2011
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
				if (regimen.equals("es276")) {
					//JVV - 23/05/2011
					strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)			
				} else {					
					if (regimen.equals("es1057")) {
						strSQL.append( "and p.t02cod_rel = ? "); //= '09'
					} else {
						strSQL.append( "and p.t02cod_rel = ? "); //= '10'
					}										
				}//
			}
			
			if (criterio.equals("4")) {	//Intendencia	
				String intendencia = valor.substring(0,2); // 2A de 2A0000				
				strSQL.append( "where   p.t02cod_stat = ? "); // JVILLACORTA 24/08/2011
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%'");				
				if (regimen.equals("es276")) {				
					strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)			
				} else {
					if (regimen.equals("es1057")) {
						strSQL.append( "and p.t02cod_rel = ? "); //= '09'
					} else {
						strSQL.append( "and p.t02cod_rel = ? "); //= '10'
					}										
				}
			}
			
			// JVV - 03/10/2010 - BUSQUEDA INSTITUCIONAL	
			if (criterio.equals("3")) {				
				strSQL.append( "where   p.t02cod_stat = ? "); // JVILLACORTA 24/08/2011
				if (regimen.equals("es276")) {				
					strSQL.append( "and p.t02cod_rel not in (?,?) ");	//in (09,10)			
				} else {
					if (regimen.equals("es1057")) {
						strSQL.append( "and p.t02cod_rel = ? "); //= '09'
					} else {
						strSQL.append( "and p.t02cod_rel = ? "); //= '10'
					}										
				}
			}
			
			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null && !seguridad.isEmpty()) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPersUsuario = ((String) seguridad.get("codPers")).trim(); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					  strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					
					//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
					if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
						strSQL2 = "SELECT * FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)='"+codPersUsuario+"' and t12ind_estad='1'"; 
	        			log.debug("joinWithT02T12T99findPersonalByCriterioValor(jefe)-strSQL2: "+strSQL2);
	        		    con2 = getConnection(dbpool);
	        			pre2 = con2.prepareStatement(strSQL2);
	        			rs2 = pre2.executeQuery();	        			
	        			String[] aFields = common.getFieldsName(rs2);

	        			while (rs2.next()) {
	        				HashMap rsMap = new HashMap();
	        				common.getRecordToMap(rs2, rsMap, aFields);	        				
	        				aLRpta.add(rsMap);
	        			}
	        		  	if (aLRpta!=null & aLRpta.size()>0){    			   		      
		          		      for (int i = 0;i<aLRpta.size();i++){ //0,1,2 (3 unidades)
		          		    	  hm=(HashMap)aLRpta.get(i);
		          		    	  codUoJef= ((String)hm.get("t12cod_uorga")).trim();
		          		    	  if (i==0){//0 (primer registro)
		          		    		strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef) + "%' "); 
		          		    	  }else{
		          		    		strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef)+ "%' "); 
		          		    	  }    		    	  
		          		    	  if (i==aLRpta.size()-1){//2 (ultimo registro)
		          		    		strSQL.append(") " );
		          		    	  }
		          		      }
	          		  	}
	        		  	log.debug("joinWithT02T12T99findPersonalByCriterioValorPersonalRegimen(jefe)-strSQL: "+strSQL);
					}
					else{//secretaria
						strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					}
					//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					//FIN ICAPUNAY - PAS20165E230300132					
					
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by t02cod_pers ");
			log.debug("joinWithT02T12T99findPersonalByCriterioValorPersonalRegimen-strSQL final: "+strSQL);

			con = getConnection(dbpool);			
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			//pre.setString(1, Constantes.ACTIVO);
			
			if (criterio.equals("0")) {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.INACTIVO);
				if (regimen.equals("es276")) {
					pre.setString(3, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(4, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(5, constantes.leePropiedad("CODTAB_CATEGORIA"));//"001"
					pre.setString(6, constantes.leePropiedad("T99DETALLE"));//"D"
				} else {
					if (regimen.equals("es1057")) {
						pre.setString(3, constantes.leePropiedad("CODREL_REG1057")); //"09"
						pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
						pre.setString(5, constantes.leePropiedad("T99DETALLE"));
					} else {
						pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
						pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
						pre.setString(5, constantes.leePropiedad("T99DETALLE"));					
					}					
			    }								
			}
			
			if ((criterio.equals("1")) || (criterio.equals("4"))  || (criterio.equals("3"))) {
				pre.setString(1, Constantes.ACTIVO);
				if (regimen.equals("es276")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));//"001"
					pre.setString(5, constantes.leePropiedad("T99DETALLE"));//"D"
				} else {
					if (regimen.equals("es1057")) {
						pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
						pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
						pre.setString(4, constantes.leePropiedad("T99DETALLE"));
					} else {
						pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
						pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
						pre.setString(4, constantes.leePropiedad("T99DETALLE"));					
					}					
			    }								
			}
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			HashMap mapa = new HashMap();
			String dni = null;
			while (rs.next()) {

				a = new BeanReporte();
				dni = rs.getString("t02lib_elec");
				if (dni != null && !dni.trim().equals("")) {
					
				} else {
					dni = "-";
				}
				String texto = rs.getString("t02cod_rel").trim().concat( " - ").concat(rs.getString("t02cod_pers").trim()).concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim())
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")")
								.concat(" ( DNI : ").concat( dni ).concat( " )")
								.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());
				
				a.setCodigo(rs.getString("t02cod_pers"));
				a.setUnidad(rs.getString("cod_uorg") .concat( " - ".concat( rs.getString("t12des_uorga"))));
				a.setNombre(texto.trim());
				mapa.put("dni", rs.getString("t02lib_elec"));
				a.setMap(mapa);
				personal.add(a);
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
		return personal;
	}
	//FIN - JVILLACORTA 20/05/2011 - AOM:MODALIDADES FORMATIVAS

	/** findPersonalByCriterioValorPersonal
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT02T12T99findPersonalByCriterioValorPersonal(
			String dbpool, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {

			strSQL.append("select   p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip, p.t02lib_elec ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ")
					.append( "where   p.t02cod_stat = ? ");
			
			if (criterio.equals("0")) {
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}

			if (criterio.equals("1")) {
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			}

			//PRAC-ASANCHEZ 04/08/2009 - BUSQUEDA POR INTENDENCIA
			if (criterio.equals("4")) {		
				String intendencia = valor.substring(0,2);
				//PROBAR
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%' ");
				//strSQL.append(" and ((p.t02cod_uorgl is null and p.t02cod_uorg LIKE '").append( intendencia.trim().toUpperCase() ).append( "%') ")
				//.append(" or t02cod_uorgl LIKE '").append( intendencia.trim().toUpperCase() ).append( "%') ");
			}
			//
			
			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by t02cod_pers ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			pre.setString(2, Constantes.CODTAB_CATEGORIA);
			pre.setString(3, Constantes.T99DETALLE);
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			HashMap mapa = new HashMap();
			String dni = null;
			while (rs.next()) {

				a = new BeanReporte();
				dni = rs.getString("t02lib_elec");
				if (dni != null && !dni.trim().equals("")) {
					
				} else {
					dni = "-";
				}
				String texto = rs.getString("t02cod_pers").trim().concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim())
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")")
								.concat(" ( DNI : ").concat( dni ).concat( " )")
								.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());
				a.setCodigo(rs.getString("t02cod_pers"));
				a.setUnidad(rs.getString("cod_uorg") .concat( " - ".concat( rs.getString("t12des_uorga"))));
				a.setNombre(texto.trim());
				mapa.put("dni", rs.getString("t02lib_elec"));
				a.setMap(mapa);
				personal.add(a);
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
		return personal;
	}

	/**
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT02T12T99findPersonalByCriterioValorInact(
			String dbpool, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	    ArrayList aLRpta = new ArrayList();
	    Utilidades common = new Utilidades();
	    String strSQL2 = "";
	    PreparedStatement pre2 = null;
	    Connection con2 = null;
	    ResultSet rs2 = null;
    	
	    HashMap hm = null;
	    String codUoJef = "";	 
	    //ICAPUNAY - PAS20165E230300132
		log.debug("joinWithT02T12T99findPersonalByCriterioValorInact ingreso");

		try {

			strSQL.append("select   p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ");
			if (criterio.equals("0")) {
				strSQL.append( "where   p.t02cod_stat in (?,?) ");
			} else {
				strSQL.append( "where   p.t02cod_stat = ? ");
			}
			
			if (criterio.equals("0")) {
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}

			if (criterio.equals("1")) {
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			}
			//DTARAZONA 19/02/2018 - SE HA AGREGADO EL CRITERIO INTENDENCIA
			if (criterio.equals("4")) {
				if (log.isDebugEnabled())log.debug("codigo intendencia:" + valor);
				
				String intendencia = valor.substring(0,2);
				if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);
			
				strSQL.append(" AND substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%' ");		
			}
			// FIN DTARAZONA

			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null && !seguridad.isEmpty()) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String codPersUsuario = ((String) seguridad.get("codPers")).trim(); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					  strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");

					
				}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					
					//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
					if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
						strSQL2 = "SELECT * FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)='"+codPersUsuario+"' and t12ind_estad='1'"; 
	        			log.debug("joinWithT02T12T99findPersonalByCriterioValorInact(jefe)-strSQL2: "+strSQL2);
	        		    con2 = getConnection(dbpool);
	        			pre2 = con2.prepareStatement(strSQL2);
	        			rs2 = pre2.executeQuery();	        			
	        			String[] aFields = common.getFieldsName(rs2);

	        			while (rs2.next()) {
	        				HashMap rsMap = new HashMap();
	        				common.getRecordToMap(rs2, rsMap, aFields);	        				
	        				aLRpta.add(rsMap);
	        			}
	        		  	if (aLRpta!=null & aLRpta.size()>0){    			   		      
		          		      for (int i = 0;i<aLRpta.size();i++){ //0,1,2 (3 unidades)
		          		    	  hm=(HashMap)aLRpta.get(i);
		          		    	  codUoJef= ((String)hm.get("t12cod_uorga")).trim();
		          		    	  if (i==0){//0 (primer registro)
		          		    		strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef) + "%' "); 
		          		    	  }else{
		          		    		strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef)+ "%' "); 
		          		    	  }    		    	  
		          		    	  if (i==aLRpta.size()-1){//2 (ultimo registro)
		          		    		strSQL.append(") " );
		          		    	  }
		          		      }
	          		  	}
	        		  	log.debug("joinWithT02T12T99findPersonalByCriterioValorInact(jefe)-strSQL: "+strSQL);
					}
					else{//secretaria
						strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					}
					//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					//FIN ICAPUNAY - PAS20165E230300132
				
				} else {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ") ;
				}
			}

			strSQL.append(" order by t02cod_pers ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			if (criterio.equals("0")) {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.INACTIVO);
				pre.setString(3, Constantes.CODTAB_CATEGORIA);
				pre.setString(4, Constantes.T99DETALLE);
			} else {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.CODTAB_CATEGORIA);
				pre.setString(3, Constantes.T99DETALLE);
				
			}
			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = rs.getString("t02cod_pers").trim().concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim())
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")")
								.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());
				a.setCodigo(rs.getString("t02cod_pers"));				
				a.setUnidad(rs.getString("cod_uorg") .concat( " - ".concat( rs.getString("t12des_uorga"))));
				a.setNombre(texto.trim());
				personal.add(a);
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
		return personal;
	}
	
	/**
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	//JVV-ini --se agrego regimen
	public ArrayList joinWithT02T12T99findPersonalByCriterioValorInactivo(
			String dbpool, String regimen, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {
			//JVV - se agrego t02cod_rel
			strSQL.append("select   p.t02cod_rel, p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ")
					.append( "where   p.t02cod_stat = ?"); //solo activos			
			
			if (criterio.equals("0")) {
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}
			//JVV-ini
			if (criterio.equals("0") && regimen.equals("es276")) {				
				strSQL.append( "and   p.t02cod_rel in (?,?) ");	//in (01,02)			
			} else {
				if (criterio.equals("0") && regimen.equals("es1057")) {
				strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
				}
			}
			//JVV-fin
			if (criterio.equals("1")) {
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			}
			//JVV-ini			
			// BUSQUEDA POR INTENDENCIA
			if (criterio.equals("5")) {		
				String intendencia = valor.substring(0,2);				
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%'");				
				if (regimen.equals("es276")) {				
					strSQL.append( "and   p.t02cod_rel in (?,?) ");	//in (01,02)			
				} else {					
					strSQL.append( "and   p.t02cod_rel = ? "); //= '09'					
				}
			}
			
			// BUSQUEDA INSTITUCIONAL	
			if (criterio.equals("4")) {		
				/*String intendencia = "--'";
				if (valor.trim().length()==6 && "0000".equals(valor.trim().substring(2,6))){
					intendencia=valor.trim().substring(0,2).concat("%'");
				}
				strSQL.append( " and p.t02cod_uorg like '").append(intendencia);*/
				if (regimen.equals("es276")) {				
					strSQL.append( "and   p.t02cod_rel in (?,?) ");	//in (01,02)			
				} else {					
					strSQL.append( "and   p.t02cod_rel = ? "); //= '09'					
				}
			}			
			//JVV-fin
			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					  strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
				} else {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ") ;
				}
			}

			strSQL.append(" order by t02cod_pers ");

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());			
			//JVV-ini
			pre.setString(1, Constantes.ACTIVO);
			if (criterio.equals("0") && regimen.equals("es276")) {			
				pre.setString(2, constantes.leePropiedad("CODREL_REG276")); //"01"
				pre.setString(3, constantes.leePropiedad("CODREL_REG728")); //"02"
				pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(5, constantes.leePropiedad("T99DETALLE"));
			} else {
				if (criterio.equals("0") && regimen.equals("es1057")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
					}
			}
			
			if (criterio.equals("1")) {				
				pre.setString(2, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(3, constantes.leePropiedad("T99DETALLE"));
			}
			
			if (criterio.equals("5") && regimen.equals("es276")) {			
				pre.setString(2, constantes.leePropiedad("CODREL_REG276")); //"01"
				pre.setString(3, constantes.leePropiedad("CODREL_REG728")); //"02"
				pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(5, constantes.leePropiedad("T99DETALLE"));
			} else {
				if (criterio.equals("5") && regimen.equals("es1057")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
					}
			}
			
			if (criterio.equals("4") && regimen.equals("es276")) {			
				pre.setString(2, constantes.leePropiedad("CODREL_REG276")); //"01"
				pre.setString(3, constantes.leePropiedad("CODREL_REG728")); //"02"
				pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(5, constantes.leePropiedad("T99DETALLE"));
			} else {
				if (criterio.equals("4") && regimen.equals("es1057")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
					}
			}
			//JVV-fin			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = rs.getString("t02cod_rel").trim().concat( " - ").concat(rs.getString("t02cod_pers").trim()).concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim())
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")")
								.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());
				/*JVV-ini
				if(rs.getString("t02cod_rel").trim().equals("01")){
					texto.concat("D.L.276");
				} else {
					if (rs.getString("t02cod_rel").trim().equals("02")){
						texto.concat("D.L.728");
					} else {				
					texto.concat("D.L.1057");
					}
				}
				//JVV-fin*/
				a.setCodigo(rs.getString("t02cod_pers"));
				a.setUnidad(rs.getString("cod_uorg") .concat( " - ".concat( rs.getString("t12des_uorga"))));
				a.setNombre(texto.trim());
				personal.add(a);
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
		return personal;
	}
	
	/**
	 * Metodo encargado de buscar los datos de las licencias Medicas de un determinado
	 * tipo registradas para un rango de fechas especifico. Join de las tablas
	 * T1273Licencia y T1279Tipo_Mov.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param tipo
	 *            String. Tipo de las licencias buscadas.
	 * @param fechaIni
	 *            String. Fecha inferior del rango de busqueda.
	 * @param fechaFin
	 *            String. Fecha superior del rango de busqueda.
	 * @param codPers
	 *            String. Numero de registro del trabajador.
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanReporte.
	 * @throws SQLException
	 */
	public ArrayList findLicenciasMedByTipoCodPers(String dbpool, String tipo,
			String fechaIni, String fechaFin, String codPers, int numDias)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	l.numero, l.anno, l.ffin, l.ffinicio, m.descrip, m.tipo_id, l.qdias, l.observ ")
					.append("from	t1273licencia l, ")
					.append("        t1279tipo_mov m ")
					.append("where 	l.cod_pers = ? ");

			if (!fechaIni.equals("")) {
				strSQL.append(" and l.ffinicio >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and l.ffin <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("')");
			}
			if (!tipo.equals("-1")) {
				strSQL.append(" and m.mov = ? ");
			} else {
				strSQL.append(" and m.mov  in ( ?,?,?,?,?,? ) ");
			}
				

			strSQL.append(" and l.licencia = m.mov ");
			strSQL.append(" order by m.descrip, l.numero desc");

			log.debug(strSQL.toString());
			log.debug("tipo :" + tipo);
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			if (!tipo.equals("-1")) {
				pre.setString(2, tipo.trim());
			} else
			{
				pre.setString(2, Constantes.LICENCIA_ENFERMEDAD);
				pre.setString(3, Constantes.LICENCIA_PRENATAL);
				pre.setString(4, Constantes.LICENCIA_POSTNATAL);
				pre.setString(5, Constantes.LICENCIA_PARTO_MULTIPLE);
				pre.setString(6, Constantes.LICENCIA_GRAVIDEZ);
				pre.setString(7, Constantes.LICENCIA_GRAVIDEZ_MULTIPLE);
			}
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			int acumDias = 0;
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setAnno(rs.getString("anno"));
				detalle.setFechaFin(rs.getTimestamp("ffin"));
				detalle.setFechaInicio(rs.getTimestamp("ffinicio"));
				detalle.setNombre(rs.getString("descrip"));
				detalle.setCantidad(rs.getInt("qdias"));
				detalle.setDescripcion(rs.getString("observ"));
				acumDias += detalle.getCantidad();
				lista.add(detalle);
			}
			if (acumDias <= numDias) {
				lista = null;				;
			}
 

		} catch (Exception e) {
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
		return lista;
	}
	
	/**
	 * Papeletas de Labor Excepcional
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findPapeletasLE(String dbpool, String fechaIni,
			String fechaFin, String codPers) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select     a.fing, a.hing, a.hsal, ")
					.append( "        m.descrip, a.fcreacion, c.t99descrip, a.jefe_autor ")
					.append( "from    t1271asistencia a, ")
					.append( "        t1279tipo_mov m, ")
					.append( "        t99codigos c ")
					.append( "where   a.cod_pers = ? ")
					.append( " and a.mov =  ? ");
					
			
			if (!fechaIni.equals("")) {
				strSQL .append( " and a.fing >= DATE('").append( Utiles.toYYYYMMDD(fechaIni) ).append( "') ");
			}
			if (!fechaFin.equals("")) {
				strSQL .append( " and a.fing <= DATE('").append( Utiles.toYYYYMMDD(fechaFin) ).append( "') ");
			}
			
			strSQL .append( " and a.estado_id IN ('" )
				.append( Constantes.PAPELETA_ACEPTADA ).append( "', '")
				.append( Constantes.PAPELETA_PROCESADA ).append( "') ");
			

			strSQL .append( " and m.tipo_id = ? and c.t99cod_tab = '507' ");
			strSQL .append( " and a.mov = m.mov ");
			strSQL .append( " and a.estado_id = c.t99codigo ");
			strSQL .append( " order by 1, a.hing, a.fcreacion ");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			pre.setString(2, Constantes.MOV_PAPELETA_COMPENSACION);
			pre.setString(3, Constantes.TIPO_MOV_PAPELETA);
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) {

				detalle = new HashMap();

				String hIng = rs.getString("hing");
				String hSal = rs.getString("hsal");

				if (hSal != null && !hSal.trim().equals("")) {
					detalle.put("hora", hIng+" - "+hSal);
				} else {
					detalle.put("hora", hIng);
				}

				detalle.put("fecha", new BeanFechaHora(rs.getDate("fing")).getFormatDate("dd/MM/yyyy"));
				detalle.put("papeleta", rs.getString("descrip"));
				detalle.put("estado", rs.getString("t99descrip"));
				detalle.put("jefe_autor", rs.getString("jefe_autor"));

				lista.add(detalle);
			}

		} catch (Exception e) {

			
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
		return lista;
	}

	
	//PRAC-ASANCHEZ 31/07/2009

	/**
	 * 
	 * @param codPers
	 * @param fecha1
	 * @param fecha2
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findDetalleDiario(String dbpool, String fechaIni,
			String fechaFin, String codPers, String criterio, String valor)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			FechaBean fec_actual = new FechaBean();
			strSQL.append("select  a.cod_pers, a.fecha, m.descrip, a.total, a.fcreacion, m.tipo_id, m.mov ")
				  .append("from    t1454asistencia_d a, ")
				  .append("        t1279tipo_mov m, ")
				  .append("        t02perdp p ")
				  .append("where   a.cod_pers = ? and a.esta_id = ? and a.cod_pers = p.t02cod_pers ")
				  .append("and (p.t02f_cese >= ? or p.t02f_cese is null) ")
				  .append("and p.t02f_fallec is null ")
				  .append("and p.t02cod_stat = ? ");
				
			if (!fechaIni.equals("")) {
				strSQL.append(" and a.fecha >= DATE('").append(Utiles.toYYYYMMDD(fechaIni)).append("') ");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and a.fecha <= DATE('").append(Utiles.toYYYYMMDD(fechaFin)).append("') ");
			}
			if (valor!= null) {
				if (!valor.equals("")){
					strSQL.append(" and a.mov = '").append(valor.trim()).append("' ");
				}
			}
			strSQL.append(" and a.mov = m.mov ");
			strSQL.append(" order by a.fecha, a.fcreacion");
			
			if (log.isDebugEnabled()) log.debug("SQL : "+strSQL.toString());
			
			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();


			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.ACTIVO);
			pre.setString(3, fec_actual.getSQLDate().toString());
			pre.setString(4, Constantes.ACTIVO);
			
			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanReporte detalle = null;
			List registrosConHora = null;
			Map mapa = null;
			String dia = "";
			String hIng = "";
			String hSal = "";
			T1271DAO asisDAO = new T1271DAO(dbpool);
			Map params = null;	
			BeanReporte detalle1 = null;
		
			while (rs.next()) {
				
				
				detalle = new BeanReporte();
				detalle.setFecha(new BeanFechaHora(rs.getDate("fecha")).getFormatDate("dd/MM/yyyy"));
				dia = Utiles.dameDiaSemana(detalle.getFecha());
				detalle.setNombre(detalle.getFecha() + " " + dia);
				detalle.setDescripcion(rs.getString("descrip"));
				detalle.setUnidad(rs.getString("total"));
				
				
				params = new HashMap();
				params.put("cod_pers", codPers);
				params.put("mov", rs.getString("mov"));
				params.put("fecha", rs.getDate("fecha").toString());				
				log.debug("MAP PRMS:"+params);
				
				//registroConHora = this.findByCodPersMovFecha(dbpool, codPers, rs.getString("mov"), 
					//		rs.getDate("fecha").toString());
		
				registrosConHora =  asisDAO.findByCodPersMovFecha(params);
				
				if(registrosConHora.size() > 0){
					for(int i = 0; i < registrosConHora.size(); i++){
						//no cambiar de posicion la generacion del nuevo objeto BeanReporte.
						detalle1 = new BeanReporte();
						detalle1.setFecha(detalle.getFecha());
						detalle1.setNombre(detalle.getNombre());
						detalle1.setDescripcion(detalle.getDescripcion());
						detalle1.setUnidad(detalle.getUnidad());
						
						log.debug("registrosConHora " + registrosConHora);
						mapa = (HashMap)registrosConHora.get(i);
						hIng = (String)mapa.get("hing");
						hSal = (String)mapa.get("hsal");
						if (hSal != null && !hSal.trim().equals("")) {
							detalle.setHora(hIng + " - " + hSal);
						} else {
							detalle.setHora(hIng);
						}
						
						detalle1.setHora(detalle.getHora());
						
						lista.add(detalle1);
						log.debug("detalle.getNombre(): " + detalle.getNombre().toString());
						log.debug("detalle.getHora(): " + detalle.getHora().toString());
						log.debug("detalle.getDescripcion(): " + detalle.getDescripcion().toString());
					}
				}else{
					detalle.setHora("");
					lista.add(detalle);
				}
				
			}
			log.debug("listafinal: " + lista);

		} catch (Exception e) {

			
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
		return lista;
	}
	//
	
/* ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */
	
	/**
	 * Metodo encargado de devolver las notificaciones de vacaciones a trabajadores realizadas segun criterios de busqueda
	 * @param dbpool String
	 * @param fechaNotific String
	 * @param fechaIniGoce String
	 * @param solicitud String
	 * @param codPers String
	 * @return lista List
	 * @throws SQLException
	 */
	public List findNotificacionesDeVacacionesEnviadas(String dbpool, String fechaNotific, String fechaNotificFin,
			String fechaIniGoce,String fechaFinGoce,String solicitud, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		List lista = null;
		String texto=null;
		String tieneFR="left";

		try {
			
			if (log.isDebugEnabled()) log.debug("Ingreso a findNotificacionesDeVacacionesEnviadas-ReporteDAO");
			
			/*strSQL.append( " select p.t02cod_pers, p.t02cod_uorg, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ");
			strSQL.append( " DATE(n.fec_ini_vacacion) as fec_ini_vacacion,  ");
			strSQL.append( " n.ind_regis_solicit as ind_regis_solicit  ");
			strSQL.append( " from t4502NotificaVac n, t02perdp p  ");
			strSQL.append( " where n.cod_pers=p.t02cod_pers ");
			strSQL.append( " and p.t02cod_pers = '").append(codPers.trim()).append("' ");
			strSQL.append( " and p.t02cod_stat = ? ");*/
			
			if(solicitud.equals("1")) tieneFR="inner";
			if(log.isDebugEnabled()) log.debug("TieneFR:"+tieneFR);
			
			strSQL.append( " select p.t02cod_pers, substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) as t02cod_uorg, u.t12des_corta, ");
			strSQL.append( " p.t02ap_pate, p.t02ap_mate, p.t02nombres, date(n.fec_envio_notific) as fec_envio_notific,  ");
			strSQL.append( " DATE(n.fec_ini_vacacion) as fec_ini_vacacion,  ");
			strSQL.append( " (CASE WHEN d.est_id='1' THEN 'Programada' ELSE CASE WHEN d.est_id='5' THEN 'En Solicitud' ELSE CASE WHEN d.est_id='0' THEN 'Eliminada' ELSE CASE WHEN d.est_id='2' THEN 'Gozada' ELSE CASE WHEN d.est_id='4' THEN 'Vendida' END END END END END) as estado,    ");
			strSQL.append( " date(s.fecha) as fecharegistro "); 
			strSQL.append( " from t4502NotificaVac n inner join t02perdp p on  "); 
			strSQL.append( " n.cod_pers=p.t02cod_pers and p.t02cod_rel<>'10'  ");
			strSQL.append( " and p.t02cod_pers = '").append(codPers.trim()).append("' ");
			strSQL.append( " and p.t02cod_stat = ? ");
			strSQL.append( " inner join t12uorga u on ");
			strSQL.append( " substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6)=u.t12cod_uorga ");
			strSQL.append( " inner join t1282vacaciones_d d on ");
			strSQL.append( " p.t02cod_pers=d.cod_pers ");
			strSQL.append( " and date(d.ffinicio )=date(n.fec_ini_vacacion) ");
			strSQL.append( " and d.licencia='49' ");
			strSQL.append( " and DATE(n.fec_envio_notific) between ? and ? ");
			strSQL.append(tieneFR).append( " join t1277solicitud s on ");//DTARAZONA 2DO ENTREGABLE
			strSQL.append( " p.t02cod_pers=s.cod_pers ");
			strSQL.append( " and date(s.ffinicio)=date(n.fec_ini_vacacion) ");
								
			if ((!fechaIniGoce.equals("") && fechaIniGoce!=null) && (!fechaFinGoce.equals("") && fechaFinGoce!=null) ) {
				strSQL.append( " and DATE(n.fec_ini_vacacion) between ? and ? ");
			} else {
				strSQL.append( " and 1=1 ");
			}							
		
			strSQL.append(" order by n.fec_envio_notific asc ");
			
			con = getConnection(dbpool);
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			if (log.isDebugEnabled()) log.debug("strSQL.toString(): "+strSQL.toString());	
			pre = con.prepareStatement(strSQL.toString());
			
			pre.setString(1, Constantes.ACTIVO);
			FechaBean b_fNotif = new FechaBean(fechaNotific,"dd/MM/yyyy");					
			FechaBean b_ffinNotif = new FechaBean(fechaNotificFin,"dd/MM/yyyy");					
			pre.setString(2, b_fNotif.getFormatDate("yyyy-MM-dd"));
			pre.setString(3, b_ffinNotif.getFormatDate("yyyy-MM-dd"));
						
			if ((!fechaIniGoce.equals("") && fechaIniGoce!=null) && (!fechaFinGoce.equals("") && fechaFinGoce!=null) ) {									
					FechaBean b_fIniGoce = new FechaBean(fechaIniGoce,"dd/MM/yyyy");					
					FechaBean b_fFinGoce = new FechaBean(fechaFinGoce,"dd/MM/yyyy");
					pre.setString(4, b_fIniGoce.getFormatDate("yyyy-MM-dd"));
					pre.setString(5, b_fFinGoce.getFormatDate("yyyy-MM-dd"));							
			}			
									
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			
			while (rs.next()) {

				detalle = new HashMap();

				if (log.isDebugEnabled()) log.debug("entro a rs-next");	
				texto = (rs.getString("t02ap_pate")!=null?rs.getString("t02ap_pate").trim():"").concat( " "
						.concat((rs.getString("t02ap_mate")!=null?rs.getString("t02ap_mate").trim():"") .concat( ", "
						.concat(rs.getString("t02nombres")!=null?rs.getString("t02nombres").trim():""))));
				
				detalle.put("uuoo",rs.getString("t02cod_uorg")!=null?rs.getString("t02cod_uorg"):"");
				detalle.put("desuo",rs.getString("t12des_corta")!=null?rs.getString("t12des_corta"):"");
				detalle.put("codpers",rs.getString("t02cod_pers")!=null? rs.getString("t02cod_pers"):"");
				detalle.put("estado",rs.getString("estado")!=null? rs.getString("estado"):"");
				detalle.put("nombre", texto);
				detalle.put("fecnotif",rs.getString("fec_envio_notific")!=null? new FechaBean(rs.getString("fec_envio_notific"),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy"):"");
				detalle.put("fecinigoce",rs.getString("fec_ini_vacacion")!=null? new FechaBean(rs.getString("fec_ini_vacacion"),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy"):"");
				detalle.put("fecharegistro",rs.getString("fecharegistro")!=null? new FechaBean(rs.getString("fecharegistro"),"yyyy-MM-dd").getFormatDate("dd/MM/yyyy"):"");
				//detalle.put("regsolicitud", rs.getString("ind_regis_solicit").equals("0")?"No":"Si");

				if (log.isDebugEnabled()) log.debug("detalle: "+detalle);	
				lista.add(detalle);
			}

		} catch (Exception e) {
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
		
		if (log.isDebugEnabled()) log.debug("lista ReporteDao: "+lista);	
		return lista;
	}
	
	
	/**
	 * findPersonalByCriterioValor_NotificacionesVacaciones
	 * @param dbpool String
	 * @param criterio String
	 * @param valor String
	 * @param seguridad HashMap
	 * @return personal ArrayList
	 * @throws SQLException
	 */
	public ArrayList joinWithT02T12T99findPersonalByCriterioValor_NotificacionesVacaciones(
			String dbpool, String regimen, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;
		
		    ArrayList aLRpta = new ArrayList();
		    Utilidades common = new Utilidades();
		    String strSQL2 = "";
		    PreparedStatement pre2 = null;
		    Connection con2 = null;
		    ResultSet rs2 = null;
	    	
		    HashMap hm = null;
		    String codUoJef = "";	

		try {

			strSQL.append("select   p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ")
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ")
					.append( "where   p.t02cod_stat = ? ");
			
			if (criterio.equals("0")) {
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}
			
			if (criterio.equals("0") && regimen.equals("01")) {				
				strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)//in (01,02)//strSQL.append( " and p.t02cod_rel in (?,?) ")			
			} else if (criterio.equals("0") && regimen.equals("09")) {
				strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
			} else if (criterio.equals("0") && regimen.equals("10")) {
				strSQL.append( "and   p.t02cod_rel = ? "); //= '10' 
			}
			
			if (criterio.equals("1") && regimen.equals("01")) {
				strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			} else if (criterio.equals("1") && regimen.equals("09")) {
						strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
						strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
		    } else if (criterio.equals("1") && regimen.equals("10")) {
						strSQL.append( "and   p.t02cod_rel = ? "); //= '10'
						strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			}
			
			if (criterio.equals("5")) {	//Intendencia	
				String intendencia = valor.substring(0,2);
				if (log.isDebugEnabled())log.debug("codigo intendencia:" + intendencia);
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
				.append( intendencia.trim().toUpperCase() ).append( "%' ");
				if (regimen.equals("01")) {
					strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)
				}
				if (regimen.equals("09")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
				}
				if (regimen.equals("10")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '10'
				}
			}
			
			if (criterio.equals("4")) {	//Intstituc					
				if (regimen.equals("01")) {
					strSQL.append( "and   p.t02cod_rel not in (?,?) ");	//in (09,10)
				}
				if (regimen.equals("09")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '09'
				}
				if (regimen.equals("10")) {
					strSQL.append( "and   p.t02cod_rel = ? "); //= '10'
				}
			}

			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String codPersUsuario = ((String) seguridad.get("codPers")).trim(); //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null){
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					  strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
				} else if (roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null						
						|| roles.get(Constantes.ROL_ANALISTA_LEXC) != null)
						{
					
					if (roles.get(Constantes.ROL_JEFE) != null) {
						strSQL2 = "SELECT * FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)='"+codPersUsuario+"' and t12ind_estad='1'"; 
	        			log.debug("joinWithT02T12T99findPersonalByCriterioValor_NotificacionesVacaciones(jefe)-strSQL2: "+strSQL2);
	        		    con2 = getConnection(dbpool);
	        			pre2 = con2.prepareStatement(strSQL2);
	        			rs2 = pre2.executeQuery();	        			
	        			String[] aFields = common.getFieldsName(rs2);

	        			while (rs2.next()) {
	        				HashMap rsMap = new HashMap();
	        				common.getRecordToMap(rs2, rsMap, aFields);	        				
	        				aLRpta.add(rsMap);
	        			}
	        		  	if (aLRpta!=null & aLRpta.size()>0){    			   		      
		          		      for (int i = 0;i<aLRpta.size();i++){ //0,1,2 (3 unidades)
		          		    	  hm=(HashMap)aLRpta.get(i);
		          		    	  codUoJef= ((String)hm.get("t12cod_uorga")).trim();
		          		    	  if (i==0){//0 (primer registro)
		          		    		strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef) + "%' "); 
		          		    	  }else{
		          		    		strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef)+ "%' "); 
		          		    	  }    		    	  
		          		    	  if (i==aLRpta.size()-1){//2 (ultimo registro)
		          		    		strSQL.append(") " );
		          		    	  }
		          		      }
	          		  	}	        		  	
					}else {					
						strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					}
				} else {
					strSQL.append(" and 1=2 ");
				}
			}
			//strSQL.append(" order by t02cod_pers "); //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			strSQL.append(" order by cod_uorg asc, t02cod_pers asc "); //ICAPUNAY - PAS20181U230300016 - Mejoras vacaciones2
			if (log.isDebugEnabled()) log.debug("joinWithT02T12T99findPersonalByCriterioValor_NotificacionesVacaciones(strSQL.toString()): "+strSQL.toString());	

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			//pre.setString(2, Constantes.CODTAB_CATEGORIA);
			//pre.setString(3, Constantes.T99DETALLE);
			if (criterio.equals("0") && regimen.equals("01")) {
				pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
				pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
				pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(5, constantes.leePropiedad("T99DETALLE"));
			} else if (criterio.equals("0") && regimen.equals("09")) {
						pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
						pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
						pre.setString(4, constantes.leePropiedad("T99DETALLE"));
			} else if (criterio.equals("0") && regimen.equals("10")) {
				pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
				pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(4, constantes.leePropiedad("T99DETALLE"));
			}
						
			if (criterio.equals("1") && regimen.equals("01")) {				
				pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
				pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
				pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
				pre.setString(5, constantes.leePropiedad("T99DETALLE"));
			} else if (criterio.equals("1") && regimen.equals("09")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
		    } else if (criterio.equals("1") && regimen.equals("10")) {
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
			}									
			if (criterio.equals("5") || criterio.equals("4")) {
				if (regimen.equals("01")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(5, constantes.leePropiedad("T99DETALLE"));
				}
				if (regimen.equals("09")) {
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057")); //"09"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				if (regimen.equals("10")) {
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA")); //"10"
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
			}		
			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {

				a = new BeanReporte();

				String texto = (rs.getString("t02cod_pers")!=null?rs.getString("t02cod_pers"):"").trim().concat( " - ").concat(rs.getString("t02ap_pate")!=null?rs.getString("t02ap_pate").trim():"").concat( " ")
								.concat(rs.getString("t02ap_mate")!=null?rs.getString("t02ap_mate").trim():"").concat( ", ").concat(rs.getString("t02nombres")!=null?rs.getString("t02nombres").trim():"")
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun")!=null?rs.getTimestamp("t02f_ingsun"): new FechaBean().getTimestamp())).concat( ")")
								.concat("  -  " ).concat(rs.getString("t99descrip")!=null?rs.getString("t99descrip").trim():"").concat( " / ").concat(rs.getString("t12des_corta")!=null?rs.getString("t12des_corta").trim():"");
				a.setCodigo(rs.getString("t02cod_pers")!=null?rs.getString("t02cod_pers"):"");
				a.setUnidad((rs.getString("cod_uorg")!=null?rs.getString("cod_uorg"):"").concat( " - ".concat(rs.getString("t12des_uorga")!=null?rs.getString("t12des_uorga"):"")));
				a.setNombre(texto.trim());
				personal.add(a);
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
		return personal;
	}	
	/* FIN ICAPUNAY - AOM URGENTE 45U3T10 NOTIFICACIONES DE ALERTAS DE VACACIONES PARA TRABAJADORES - 17/04/2011 */

	//JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS
	/**
	 * 
	 * @param criterio
	 * @param valor
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList joinWithT02T12T99findPersonalByCriterioValorInactMF(
			String dbpool, String regimen, String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;
		
		//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	    ArrayList aLRpta = new ArrayList();
	    Utilidades common = new Utilidades();
	    String strSQL2 = "";
	    PreparedStatement pre2 = null;
	    Connection con2 = null;
	    ResultSet rs2 = null;
    	
	    HashMap hm = null;
	    String codUoJef = "";	 
	    //ICAPUNAY - PAS20165E230300132
		log.debug("joinWithT02T12T99findPersonalByCriterioValorInactMF ingreso");

		try {

			strSQL.append("select   p.t02cod_pers, p.t02ap_pate, p.t02ap_mate, p.t02nombres, ")
					.append( "		   substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorg, ")
					.append( "        substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, ")
					//.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip ") //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
					.append( "        p.t02f_ingsun, uo.t12des_uorga, t12des_corta, param.t99descrip,p.t02cod_rel ") //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
					.append( "from    t02perdp p, ")
					.append( "        t12uorga uo, ")
					.append( "        t99codigos param ");
			/*if (criterio.equals("0")) {
				strSQL.append( "where   p.t02cod_stat in (?,?) ");
			} else {
				strSQL.append( "where   p.t02cod_stat = ? ");
			}			
			if (criterio.equals("0")) {
				strSQL.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'");
			}*/			
			if (criterio.equals("0")) {
				if (log.isDebugEnabled())log.debug("entro criterio Registro-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL.append( "where   p.t02cod_stat in (?,?) ")
					.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'")
					.append( " and p.t02cod_rel not in (?,?) "); //not in (09,10)
				} else if (regimen.equals("09")){
					strSQL.append( "where   p.t02cod_stat in (?) ")
					.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'")
					.append( "and   p.t02cod_rel = ? "); //= '09'
				} else if (regimen.equals("10")){
					strSQL.append( "where   p.t02cod_stat in (?) ")
					.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'")
					.append( "and   p.t02cod_rel = ? "); //= '10'
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){ //solo regimenes 276-728 planilla y 1057 cas (No formativas)
					strSQL.append( "where   p.t02cod_stat in (?) ")
					.append( " and p.t02cod_pers = '" ).append( valor.trim().toUpperCase()).append( "'")
					.append( "and   p.t02cod_rel not in (?) "); // '10'
				}
				//FIN
			}		

			if (criterio.equals("1")) {
				if (log.isDebugEnabled())log.debug("entro criterio UUOO-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL .append( "where   p.t02cod_stat = ? ")
					.append( "and   p.t02cod_rel not in (?,?) "); //not in (09,10)
				} else if (regimen.equals("09")){
					strSQL .append( "where   p.t02cod_stat = ? ")
					.append( "and   p.t02cod_rel = ? "); //= '09'
				} else if (regimen.equals("10")){
					strSQL .append( "where   p.t02cod_stat in (?) ")
					.append( "and   p.t02cod_rel = ? "); //= '10'
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){ //solo regimenes 276-728 planilla y 1057 cas (No formativas)
					strSQL .append( "where   p.t02cod_stat in (?) ")
					.append( "and   p.t02cod_rel not in (?) "); // '10'
				}
				//FIN 
				strSQL.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = '").append(valor.trim().toUpperCase() ).append("' ");
			}
			
			if (criterio.equals("5")) {
				String intendencia = valor.substring(0,2);
				if (log.isDebugEnabled())log.debug("entro criterio INTENDENCIA-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL .append( " where   p.t02cod_stat = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append(intendencia.trim().toUpperCase()).append("%'")
					.append( " and   p.t02cod_rel not in (?,?) "); //not in (09,10)
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					strSQL .append( "where   p.t02cod_stat = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append(intendencia.trim().toUpperCase()).append("%'")				
					.append( " and p.t02cod_rel = ? "); //= '09'
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					strSQL .append( "where   p.t02cod_stat = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append(intendencia.trim().toUpperCase()).append("%'")				
					.append( " and p.t02cod_rel = ? "); //= '10'
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){ //solo regimenes 276-728 planilla y 1057 cas (No formativas)
					strSQL .append( "where   p.t02cod_stat = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) LIKE '")
					.append(intendencia.trim().toUpperCase()).append("%'")				
					.append( " and p.t02cod_rel not in (?) "); // '10'
				}
				//FIN 
			} 
			
			if (criterio.equals("3")) {
				if(log.isDebugEnabled())log.debug("entro criterio INSTITUC-->");
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					strSQL .append( "where   p.t02cod_stat = ? ")				    
				  	.append( "and   p.t02cod_rel not in (?,?) "); //not in (09,10)
				} else if (constantes.leePropiedad("CODREL_REG1057").equals(regimen)){
					strSQL .append( "where   p.t02cod_stat = ? ")				   
					.append( "and   p.t02cod_rel = ? "); //= '09'
				} else if (constantes.leePropiedad("CODREL_MOD_FORMATIVA").equals(regimen)){
					strSQL .append( "where   p.t02cod_stat = ? ")				    
					.append( "and   p.t02cod_rel = ? "); //= '10'
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){ //solo regimenes 276-728 planilla y 1057 cas (No formativas)
					strSQL .append( "where   p.t02cod_stat = ? ")				    
					.append( "and   p.t02cod_rel not in (?) "); // '10'
				}
				//FIN 
			}	

			strSQL .append( " and param.t99cod_tab = ? ")
					.append( " and param.t99tip_desc = ? ")
					.append( " and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) = uo.t12cod_uorga ")
					.append( " and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = param.t99codigo ");

			//criterios de visibilidad
			if (seguridad != null && !seguridad.isEmpty()) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String codPersUsuario = ((String) seguridad.get("codPers")).trim(); //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "') ");
					
					  strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append("  (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
					.append(uoAO).append( "'))) ");
					
				}  else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_SECRETARIA) != null
						|| roles.get(Constantes.ROL_JEFE) != null) {
					
					//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
					if (roles.get(constantes.leePropiedad("ROL_JEFE")) != null) {
						strSQL2 = "SELECT * FROM t12uorga where substr(trim(nvl(t12cod_encar,'')||nvl(t12cod_jefat,'')),1,4)='"+codPersUsuario+"' and t12ind_estad='1'"; 
	        			log.debug("joinWithT02T12T99findPersonalByCriterioValorInactMF(jefe)-strSQL2: "+strSQL2);
	        		    con2 = getConnection(dbpool);
	        			pre2 = con2.prepareStatement(strSQL2);
	        			rs2 = pre2.executeQuery();	        			
	        			String[] aFields = common.getFieldsName(rs2);

	        			while (rs2.next()) {
	        				HashMap rsMap = new HashMap();
	        				common.getRecordToMap(rs2, rsMap, aFields);	        				
	        				aLRpta.add(rsMap);
	        			}
	        		  	if (aLRpta!=null & aLRpta.size()>0){    			   		      
		          		      for (int i = 0;i<aLRpta.size();i++){ //0,1,2 (3 unidades)
		          		    	  hm=(HashMap)aLRpta.get(i);
		          		    	  codUoJef= ((String)hm.get("t12cod_uorga")).trim();
		          		    	  if (i==0){//0 (primer registro)
		          		    		strSQL.append(" and (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef) + "%' "); 
		          		    	  }else{
		          		    		strSQL.append(" or substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"+ findUuooJefe(codUoJef)+ "%' "); 
		          		    	  }    		    	  
		          		    	  if (i==aLRpta.size()-1){//2 (ultimo registro)
		          		    		strSQL.append(") " );
		          		    	  }
		          		      }
	          		  	}
	        		  	log.debug("joinWithT02T12T99findPersonalByCriterioValorInactMF(jefe)-strSQL: "+strSQL);
					}
					else{//secretaria
						strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					}
					//strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ");
					//FIN ICAPUNAY - PAS20165E230300132

				} else {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append(uoSeg).append( "' ") ;
				}
			}

			//strSQL.append(" order by t02cod_pers "); //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
			strSQL.append(" order by cod_uorg asc, t02cod_pers asc "); //ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
			log.debug("strSQL final: "+strSQL);

			con = getConnection(dbpool);
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			/*if (criterio.equals("0")) {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.INACTIVO);
				pre.setString(3, Constantes.CODTAB_CATEGORIA);
				pre.setString(4, Constantes.T99DETALLE);
			} else {
				pre.setString(1, Constantes.ACTIVO);
				pre.setString(2, Constantes.CODTAB_CATEGORIA);
				pre.setString(3, Constantes.T99DETALLE);
				
			}*/
			if (criterio.equals("0")) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("INACTIVO"));
					pre.setString(3, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(4, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));	
					pre.setString(5, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(6, constantes.leePropiedad("T99DETALLE"));								
				} else if (regimen.equals("09")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				} else if (regimen.equals("10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//FIN
			}				
			
			if (criterio.equals("1")) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					pre.setString(1, constantes.leePropiedad("ACTIVO"));				
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));	
					pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(5, constantes.leePropiedad("T99DETALLE"));									
				} else if (regimen.equals("09")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				} else if (regimen.equals("10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//FIN
			}
			
			if (criterio.equals("5")) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					pre.setString(1, constantes.leePropiedad("ACTIVO"));				
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));	
					pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(5, constantes.leePropiedad("T99DETALLE"));											
				} else if (regimen.equals("09")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				} else if (regimen.equals("10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//FIN
			}
			
			if (criterio.equals("3")) {
				if (constantes.leePropiedad("CODREL_REG276").equals(regimen)) {
					pre.setString(1, constantes.leePropiedad("ACTIVO"));				
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(3, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));	
					pre.setString(4, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(5, constantes.leePropiedad("T99DETALLE"));								
				} else if (regimen.equals("09")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_REG1057"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				} else if (regimen.equals("10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				else if (regimen.equals("No10")){
					pre.setString(1, constantes.leePropiedad("ACTIVO"));
					pre.setString(2, constantes.leePropiedad("CODREL_MOD_FORMATIVA"));
					pre.setString(3, constantes.leePropiedad("CODTAB_CATEGORIA"));
					pre.setString(4, constantes.leePropiedad("T99DETALLE"));
				}
				//FIN
			}
			
			rs = pre.executeQuery();
			personal = new ArrayList();

			BeanReporte a = null;
			while (rs.next()) {
				a = new BeanReporte();
				//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
                String cod_rel = rs.getString("t02cod_rel")!=null?rs.getString("t02cod_rel").trim():""; 
                String cod_uorg = rs.getString("cod_uorg")!=null?rs.getString("cod_uorg").trim():""; 
                String t12des_uorga = rs.getString("t12des_uorga")!=null?rs.getString("t12des_uorga").trim():""; 
                String desRegimen = cod_rel.equals("09")?"DL 1057":cod_rel.equals("10")?"Formativa":"DL 276-728"; 
                log.debug("desRegimen2: "+desRegimen);                
				/*String texto = rs.getString("t02cod_pers").trim().concat( " - ").concat(rs.getString("t02ap_pate").trim()).concat( " ")
								.concat(rs.getString("t02ap_mate").trim()).concat( ", ").concat( rs.getString("t02nombres").trim())
								.concat(" (").concat( Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun"))).concat( ")")
								.concat("  -  " ).concat( rs.getString("t99descrip").trim() ).concat( " / ").concat( rs.getString("t12des_corta").trim());*/				
				String codPers = rs.getString("t02cod_pers")!=null?rs.getString("t02cod_pers").trim():"";
				String apPate = rs.getString("t02ap_pate")!=null?rs.getString("t02ap_pate").trim():"";
				String apMate = rs.getString("t02ap_mate")!=null?rs.getString("t02ap_mate").trim():"";
				String nombres = rs.getString("t02nombres")!=null?rs.getString("t02nombres").trim():"";
				String fecIngreso = rs.getTimestamp("t02f_ingsun")!=null?Utiles.timeToFecha(rs.getTimestamp("t02f_ingsun")):"";
				String descripcion = rs.getString("t99descrip")!=null?rs.getString("t99descrip").trim():"";
				String desUnidad = rs.getString("t12des_corta")!=null?rs.getString("t12des_corta").trim():"";				
				String texto = codPers + " - " + apPate + " " + apMate + ", " + nombres + 
								" (" + fecIngreso + ") " +
								" - " + descripcion + " / " + desUnidad;				
				
				//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral	
				a.setCodigo(codPers);
				a.setUnidad(cod_uorg.concat( " - ".concat(t12des_uorga)));
				a.setNombre(texto.trim());
				a.setCategoria(desRegimen);//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				a.setDescripcion(apPate.concat(" ").concat(apMate).concat(", ").concat(nombres));//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
				personal.add(a);
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
		return personal;
	}
	//FIN - JVILLACORTA 01/07/2011 - AOM:MODALIDADES FORMATIVAS

	/**
	 * Permite obtener los movimientos, flujo y aprobadores por unidad organizacional, indicando además,
	 * cuantas solicitudes pendientes de atención tiene cada aprobador.
	 * @param datos HashMap con los parámetros necesarios para ejecutar el método.
	 * @return ArrayList 
	 * @throws SQLException
	 * @author jmaravi
	 * @since 14/03/2014
	 */
	public ArrayList findMovimientosFlujosAprobadores(String dbpool, HashMap datos) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		String sFiltraTipMov="";
		if(datos.get("filtra_tipo_mov")!=null && !datos.get("filtra_tipo_mov").equals("")){
			sFiltraTipMov=" and trim(tm.mov) = '" + datos.get("filtra_tipo_mov").toString() + "' ";
		}
		
		try {
			strSQL.append("	select 					")
			.append("		det.*,				")
			.append("		(	select  count(1) 			")
			.append("			from    			")
			.append("				t1277solicitud s		")
			.append("				join	t1455sol_seg ss	")
			.append("					on  s.cod_pers = ss.cod_pers  	")
			.append("					and s.anno = ss.anno  	")
			.append("					and s.numero = ss.numero  	")
			.append("					and s.seguim_act = ss.num_seguim 	")
			.append("					and ss.estado_id <> '2'	")
			.append("					and ss.cuser_dest = det.aprob_ini	")
			.append("					and s.u_organ = det.u_organ	")
			.append("					and s.licencia = det.mov_cod	")
			.append("		) as aprob_ini_pend,				")
			.append("		(	select  count(1) 			")
			.append("			from    			")
			.append("				t1277solicitud s		")
			.append("				join	t1455sol_seg ss	")
			.append("					on  s.cod_pers = ss.cod_pers  	")
			.append("					and s.anno = ss.anno  	")
			.append("					and s.numero = ss.numero  	")
			.append("					and s.seguim_act = ss.num_seguim 	")
			.append("					and ss.estado_id <> '2'	")
			.append("					and ss.cuser_dest = det.aprob_int	")
			.append("					and s.u_organ = det.u_organ	")
			.append("					and s.licencia = det.mov_cod	")
			.append("		) as aprob_int_pend,				")
			.append("		(	select  count(1) 			")
			.append("			from    			")
			.append("				t1277solicitud s		")
			.append("				join	t1455sol_seg ss	")
			.append("					on  s.cod_pers = ss.cod_pers  	")
			.append("					and s.anno = ss.anno  	")
			.append("					and s.numero = ss.numero  	")
			.append("					and s.seguim_act = ss.num_seguim 	")
			.append("					and ss.estado_id <> '2'	")
			.append("					and ss.cuser_dest = det.aprob_fin	")
			.append("					and s.u_organ = det.u_organ	")
			.append("					and s.licencia = det.mov_cod	")
			.append("		) as aprob_fin_pend 				")
			.append("	from					")
			.append("		(				")
			.append("		select 				")
			.append("			trim(f.u_organ) as u_organ,			")
			.append("			trim(t.t99abrev) as mov_grupo_cod ,			")
			.append("			trim(t.t99descrip)  as mov_grupo_des,			")
			.append("			trim(tm.mov) as mov_cod, 			")
			.append("			trim(tm.descrip) as mov_des,			")
			.append("			trim(m.t99descrip) as mov_medida,			")
			.append("			max(case when f.cinstancia_aprob in ('1','4') then f.cod_personal_ori else '' end) as aprob_ini,			")
			.append("			max(case when f.cinstancia_aprob ='2' then f.cod_personal_ori else '' end) as aprob_int,			")
			.append("			max(case when f.cinstancia_aprob ='3' then f.cod_personal_ori else '' end) as aprob_fin,			")
			.append("			max(nvl(f.cinstancia_aprob,'')) as aprob_tipo			")
			.append("		from 				")
			.append("			t1279tipo_mov tm			")
			.append("			join			")
			.append("			t99codigos m			")
			.append("				on tm.medida = m.t99codigo[1,2]		")
			.append("				and tm.est_id = 1		")
			.append("			join			")
			.append("			t99codigos t 			")
			.append("				on t.t99codigo[1,2] = tm.tipo_id		")
			.append("			left join			")
			.append("			t1480sol_flujo f			")
			.append("				on  f.mov = tm.mov		")
			.append("				and f.u_organ = ?		")
			.append("			join 			")
			.append("			t99codigos h	 		")
			.append("				on  h.t99cod_tab = 'M02'	 	")
			.append("				and h.t99tip_desc = 'D' 		")
			.append("				and h.t99codigo = tm.mov 		")
			.append("		where 				")
			.append("			m.t99tip_desc = 'D'			")
			.append("			and m.t99cod_tab = '503'			")
			.append("			and t.t99tip_desc = 'D'			")
			.append("			and t.t99cod_tab = '502'			")
			.append( sFiltraTipMov )
			.append("		group by				")
			.append("			f.u_organ,			")
			.append("			tm.mov, 			")
			.append("			tm.descrip,			")
			.append("			m.t99descrip, 			")
			.append("			t.t99abrev,			")
			.append("			t.t99descrip			")
			.append("		) as det				")
			.append("						")
			.append("	order by 					")
			.append("		mov_des				");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, (String)datos.get("uniOrgan"));
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) {
				detalle = new HashMap();
				
				detalle.put("u_organ", datos.get("uniOrgan"));
				detalle.put("mov_grupo_cod", rs.getString("mov_grupo_cod"));
				detalle.put("mov_grupo_des", rs.getString("mov_grupo_des"));
				detalle.put("mov_cod", rs.getString("mov_cod"));
				detalle.put("mov_des", rs.getString("mov_des"));
				detalle.put("mov_medida", rs.getString("mov_medida"));
				detalle.put("aprob_ini", rs.getString("aprob_ini"));
				detalle.put("aprob_int", rs.getString("aprob_int"));
				detalle.put("aprob_fin", rs.getString("aprob_fin"));
				detalle.put("aprob_tipo", rs.getString("aprob_tipo"));
				detalle.put("aprob_ini_pend", rs.getString("aprob_ini_pend"));
				detalle.put("aprob_int_pend", rs.getString("aprob_int_pend"));
				detalle.put("aprob_fin_pend", rs.getString("aprob_fin_pend"));
	
				//detalle.put("", rs.getString(""));
				lista.add(detalle);
			}

		} catch (Exception e) {			
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
		return lista;
	}
			
	/**
	 * Permite obtener la lista de las solicitudes que tiene pendiente un aprobador específico.
	 * @param datos HashMap con los parámetros necesarios para ejecutar el método.
	 * @return ArrayList 
	 * @throws SQLException
	 * @author jmaravi
	 * @since 10/04/2014
	 */
	public ArrayList obtenerPendientesxAprobador(String dbpool, HashMap datos) throws SQLException {
		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {
			strSQL.append("")
			.append("	select   			")
			.append("		s.cod_pers, 		")
			.append("		trim(p.t02ap_pate) || ' ' || trim(p.t02ap_mate) || ', ' || trim(p.t02nombres) as emisor,		")
			.append("		trim(m.descrip) as tipo_mov, 		")
			.append("		s.anno, 		")
			.append("		s.numero,  		")
			.append("		s.u_organ as u_organ, 		")
			.append("		s.fecha as fechaSol, 		")
			.append("		trim(s.asunto) as asunto,		")
			.append("		ss.fecha_recep as ultDeriv, 		")
			.append("		ss.estado_id, 		")
			.append("		trim(pp.t02ap_pate) || ' ' || trim(pp.t02ap_mate) || ', ' || trim(pp.t02nombres) as anterior_resp,		")
			.append("		ss.cuser_orig, 		")
			.append("		ss.cuser_dest,		")
			.append("		ss.u_organ as uo_orig, 	")
			.append("		s.est_id, 		")
			.append("		p.t02cod_rel		")
			.append("	from    			")
			.append("		t1277solicitud s		")
			.append("		join		")
			.append("		t1455sol_seg ss		")
			.append("			on  s.cod_pers = ss.cod_pers  	")
			.append("			and s.anno = ss.anno  	")
			.append("			and s.numero = ss.numero  	")
			.append("			and s.seguim_act = ss.num_seguim 	")
			.append("		join		")
			.append("		t02perdp p		")
			.append("			on s.cod_pers = p.t02cod_pers	")
			.append("		join		")
			.append("		t1279tipo_mov m 		")
			.append("			on s.licencia = m.mov	")
			.append("		join		")
			.append("		t02perdp pp		")
			.append("			on pp.t02cod_pers = ss.cuser_orig	")
			.append("	where   			")
			.append("		ss.estado_id <> '2'		")
			.append("		and		")
			.append("		s.u_organ = ?		")
			.append("		and		")
			.append("		ss.cuser_dest = ?		")
			.append("		and		")
			.append("		s.licencia = ?		");			

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, (String)datos.get("uniOrgan"));
			pre.setString(2, (String)datos.get("cod_aprob"));			
			pre.setString(3, (String)datos.get("tipo_mov"));			
			rs = pre.executeQuery();

			lista = new ArrayList();
			HashMap detalle = null;
			while (rs.next()) {
				detalle = new HashMap();
				detalle.put("cod_pers", rs.getString("cod_pers"));
				detalle.put("emisor", rs.getString("emisor"));
				detalle.put("tipo_mov", rs.getString("tipo_mov"));
				detalle.put("anno", rs.getString("anno"));
				detalle.put("numero", new Integer(rs.getInt("numero")) );
				detalle.put("u_organ", rs.getString("u_organ"));
				detalle.put("fechaSol", rs.getTimestamp("fechaSol"));
				detalle.put("asunto", rs.getString("asunto"));
				detalle.put("ultDeriv", rs.getTimestamp("ultDeriv"));
				detalle.put("estado_id", rs.getString("estado_id"));
				detalle.put("anterior_resp", rs.getString("anterior_resp"));
				detalle.put("cuser_orig", rs.getString("cuser_orig"));
				detalle.put("cuser_dest", rs.getString("cuser_dest"));
				detalle.put("uo_orig", rs.getString("uo_orig"));
				detalle.put("est_id", rs.getString("est_id"));
				detalle.put("t02cod_rel", rs.getString("t02cod_rel"));
	
				//detalle.put("", rs.getString(""));
				lista.add(detalle);
			}

		} catch (Exception e) {			
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
		return lista;
	}
	
	
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	/**
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers
	 * @param numDias
	 * @param incluyePend
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findVacacionesGozadasMatrimonio(String dbpool, String fechaIni,
			String fechaFin, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select  v.anno_vac, v.ffinicio, v.ffin, v.dias, v.observ, m.descrip, v.licencia ")
					.append( "from	t1282vacaciones_d v, t1279tipo_mov m ")
					.append( "where m.mov = v.licencia and	v.cod_pers = ? and v.licencia = ? and ind_matri='1' ");
					
			if (!fechaIni.equals("")) {
				strSQL.append(" and (v.ffinicio >= DATE('")
						.append( Utiles.toYYYYMMDD(fechaIni) ).append( "')");
			}
			if (!fechaFin.equals("")) {
				strSQL.append(" and v.ffin <= DATE('" ).append( Utiles.toYYYYMMDD(fechaFin))
						.append( "'))");
			}

			strSQL.append(" order by v.ffinicio");


			con = getConnection(dbpool);

			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, Constantes.VACACION);

			rs = pre.executeQuery();
						
			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {

				detalle = new BeanReporte();

				detalle.setAnno(rs.getString("anno_vac"));
				detalle.setFechaInicio(rs.getTimestamp("ffinicio"));
				detalle.setFechaFin(rs.getTimestamp("ffin"));
				detalle.setCantidad(rs.getInt("dias"));
				detalle.setDescripcion(rs.getString("observ")!=null ? rs.getString("observ").trim():"");
				detalle.setNombre(rs.getString("descrip"));
				detalle.setCodigo(rs.getString("licencia").trim());

				lista.add(detalle);
			}
 

		} catch (Exception e) {
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
		return lista;
	}
	//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta 
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral 
	/**
	 * 
	 * @param dbpool
	 * @param fechaIni
	 * @param fechaFin
	 * @param codPers	
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAutorizacionesClimaLaboral(String dbpool, String fechaIni,String fechaFin, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;
		log.debug("ingreso findAutorizacionesClimaLaboral");

		try {

			strSQL.append("select u.t12cod_uorga as cod_uorga, u.t12des_uorga as des_uorga,u.t12des_corta as des_corta, ")
			.append("(CASE WHEN p1.t02cod_rel='10' THEN 'Formativas' ELSE CASE WHEN p1.t02cod_rel='09' THEN 'DL 1057' ELSE 'DL 276-728' END END) as regimen,p1.t02cod_pers as regcol, ")
			.append("(trim(p1.t02ap_pate)||' '||trim(p1.t02ap_mate)||' '||trim(p1.t02nombres)) as colaborador, a.cod_aut as regaut, (trim(p.t02ap_pate)||' '||trim(p.t02ap_mate)||' '||trim(p.t02nombres)) as autorizador, ")
			//.append("p1.t02cod_pers as colaborador, p.t02cod_pers as autorizador, ")
			//.append("a.fec_aut, clima.total as minClima, exceso.total as minExceso ")
			.append("a.fec_aut, ")
			.append("(select total from t1454asistencia_d where cod_pers=? and mov=? and (fecha BETWEEN to_date(?,'%d/%m/%Y') and to_date(?,'%d/%m/%Y') and fecha=a.fec_aut)) as minclima, ")
			.append("(select total from t1454asistencia_d where cod_pers=? and mov=? and (fecha BETWEEN to_date(?,'%d/%m/%Y') and to_date(?,'%d/%m/%Y') and fecha=a.fec_aut)) as minexceso ")
			//.append("from	t8167autorizaclima a  inner join t02perdp p1 on a.cod_pers = p1.t02cod_pers and (a.fec_aut BETWEEN to_DATE('").append(fechaIni).append("','%d/%m/%Y') and to_DATE('").append(fechaFin).append("','%d/%m/%Y')) ").append("and a.cod_pers=? ")
			.append("from	t8167autorizaclima a inner join t02perdp p1 on a.cod_pers = p1.t02cod_pers and (a.fec_aut BETWEEN to_date(?,'%d/%m/%Y') and to_date(?,'%d/%m/%Y')) ").append("and a.cod_pers=? ")
			.append("left join t02perdp p on a.cod_aut = p.t02cod_pers ")
			//.append("left join (select d1.cod_pers,d1.fecha,d1.total from t1454asistencia_d d1 where d1.cod_pers=? and d1.mov=? and (d1.fecha BETWEEN to_DATE('").append(fechaIni).append("','%d/%m/%Y') and to_DATE('").append(fechaFin).append("','%d/%m/%Y'))) as clima on a.cod_pers=clima.cod_pers and a.fec_aut=clima.fecha ")
			//.append("left join (select d2.cod_pers,d2.fecha,d2.total from t1454asistencia_d d2 where d2.cod_pers=? and d2.mov=? and (d2.fecha BETWEEN to_DATE('").append(fechaIni).append("','%d/%m/%Y') and to_DATE('").append(fechaFin).append("','%d/%m/%Y'))) as exceso on a.cod_pers=exceso.cod_pers and a.fec_aut=exceso.fecha ")
			.append("left join t12uorga u on substr(trim(nvl(p1.t02cod_uorgl,'')||nvl(p1.t02cod_uorg,'')),1,6) = u.t12cod_uorga ")
			.append("order by a.fec_aut asc ");
			
			log.debug("strSQL final2: "+strSQL);
			log.debug("codPers.trim(): "+codPers.trim());
			log.debug("fechaIni: "+fechaIni);
			log.debug("fechaFin: "+fechaFin);
			con = getConnection(dbpool);

			//con.prepareCall("SET ISOLATION TO DIRTY READ").execute();

			pre = con.prepareStatement(strSQL.toString()); //5 parametros
			pre.setString(1, codPers.trim());
			pre.setString(2, Constantes.MOV_REFRI_CLIMALABORAL);//136
			pre.setString(3, fechaIni.trim());
			pre.setString(4, fechaFin.trim());
			pre.setString(5, codPers.trim());
			pre.setString(6, Constantes.MOV_EXCESO_REFRIGERIO);//09			
			pre.setString(7, fechaIni.trim());
			pre.setString(8, fechaFin.trim());
			pre.setString(9, fechaIni.trim());
			pre.setString(10, fechaFin.trim());
			pre.setString(11, codPers.trim());			
			
			rs = pre.executeQuery();
						
			lista = new ArrayList();
			BeanReporte detalle = null;
			while (rs.next()) {
				log.debug("rs.getString(regimen)2: " + rs.getString("regimen"));
				detalle = new BeanReporte();
				detalle.setCodigoUnidad(rs.getString("cod_uorga"));
				detalle.setUnidad(rs.getString("des_uorga"));
				detalle.setUnidadCorta(rs.getString("des_corta"));
				detalle.setCategoria(rs.getString("regimen"));
				detalle.setCodigo(rs.getString("regcol"));
				detalle.setNombre(rs.getString("colaborador"));
				detalle.setCodigoJefe(rs.getString("regaut"));
				detalle.setDescripcion(rs.getString("autorizador"));
				detalle.setFecha(new FechaBean(rs.getDate("fec_aut")).getFormatDate("dd/MM/yyyy"));				
				detalle.setCantidad(rs.getInt("minclima"));	
				detalle.setCantidad2(rs.getInt("minexceso"));
				
				lista.add(detalle);
			} 

		} catch (Exception e) {
			log.error("**** SQL ERROR findAutorizacionesClimaLaboral() ****", e);
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
		return lista;
	}
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	
	//ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
	/**
	   * Metodo encargado de obtener el codigo de unidad organica restando los ceros empezando desde el final hasta el caracter que no sea cero		 
	   * @param String unidad
	   * @return String uoJefe
	   * @throws SQLException
	   */
	  public String findUuooJefe(String unidad) throws DAOException {
    
	    String uoJefe = "";	
	    try {

	    	log.debug("findUuooJefe-unidadd: "+unidad); 
			uoJefe= unidad!=null? unidad.trim(): "";
        	if (!"".equals(uoJefe)){
        		log.debug("entro if");
        		int nroCar = uoJefe.length();
            	log.debug("nroCar: "+nroCar);
            	char v= '9';  //solo 1 caracter almacena              	
            	for (int p = nroCar-1; p >= 0; p--) {
            		log.debug("entro for");
            		log.debug("uoJefe: "+uoJefe);
            		log.debug("v: "+v);
            		log.debug("p: "+p);
            		v= uoJefe.charAt(p);
            		if (p!=0){
            			log.debug("entro p!=0");
                		if ('0'==v){
                			uoJefe=uoJefe.substring(0, p);
                			log.debug("uoJefe2: "+uoJefe);
                		}else{                			
                			break;
                		} 
            		}	
            	}            		
        	}
        	log.debug("findUuooJefe-uoJefee: "+uoJefe);
	    }
	    catch (Exception e) {
	    	log.error("*** SQL Error ****",e);
			MensajeBean msg = new MensajeBean();
			msg.setMensajeerror("Ha ocurrido un error inesperado: ".concat( e.getMessage() ));
			msg.setMensajesol("Por favor, intente nuevamente realizar la operacion, de continuar con el problema comuniquese con el webmaster");
	    }
	    finally {
	    }
	    log.debug("findUuooJefe-uoJefee(final): "+uoJefe);
		return uoJefe; 
	  }	  
	  //ICAPUNAY - PAS20165E230300132 - MSNAAF071 Visualizacion SIRH Asistencia/Turnos No Controlados
			
}