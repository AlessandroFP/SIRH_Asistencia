package pe.gob.sunat.sp.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanLicencia;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T1273DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T1273Licencia
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

public class T1273DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T1273DAO.class);

	public T1273DAO() {
	}

	/**
	 * Metodo que se encarga de determinar si un trabajador tiene registradas
	 * licencias dentro del rango de fechas indicados, filtrados opcionalmente
	 * por el numero de la licencia que no se desea considerar.
	 * @throws SQLException
	 */
	public boolean findByCodPersFIniFFin(String dbpool, String codPers,
			String fecha1, String fecha2, String numero) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean tiene = false;

		try {
			log.debug("fecha1 "+ fecha1);
			String fecCompara1 = Utiles.toYYYYMMDD(fecha1);
			log.debug("feccompara1 "+ fecCompara1);
			String fecCompara2 = "";
			log.debug("fecha2 "+ fecha2);
			if (!fecha2.trim().equals("")) {
				fecCompara2 = Utiles.toYYYYMMDD(fecha2);
			}
			log.debug("feccompara2 "+ fecCompara2);
			strSQL.append("select cod_pers from t1273Licencia "
					).append( " where cod_pers = ? ");

			if (!fecha2.trim().equals("")) {

				if (!fecha1.equals(fecha2)) {
					strSQL.append(" and ((ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and ffin <= DATE('" ).append( fecCompara2 ).append( "')) ");
					strSQL.append(" or (ffinicio <= DATE('" ).append( fecCompara1
							).append( "') and ffin <= DATE('" ).append( fecCompara2
							).append( "') and ffin >= DATE('" ).append( fecCompara1 ).append( "') ) ");
					strSQL.append(" or (ffinicio >= DATE('" ).append( fecCompara1
							).append( "') and ffin >= DATE('" ).append( fecCompara2
							).append( "') and ffinicio <= DATE('" ).append( fecCompara2
							).append( "'))) ");
				} else {
					strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
							).append( "'))  and (ffin >= DATE('" ).append( fecCompara2 ).append( "')) ");
				}
			} else {
				strSQL.append(" and (ffinicio <= DATE('" ).append( fecCompara1
						).append( "'))  and (ffin >= DATE('" ).append( fecCompara1 ).append( "')) ");
			}

			if (!numero.equals("")) {
				strSQL.append(" and numero != '" ).append( numero ).append( "'");
			}

			log.debug("query "+ strSQL.toString());
			log.debug("pool " + dbpool);
			con = getConnection(dbpool);
			log.debug("pase pool " );
			//con.prepareCall("SET LOCK MODE TO WAIT 10").execute();
			con.prepareCall("SET ISOLATION TO DIRTY READ").execute();
			pre = con.prepareStatement(strSQL.toString());
			log.debug("pase pre " );
			pre.setString(1, codPers);
			log.debug("pase set " );
			rs = pre.executeQuery();
			log.debug("ejcuto query " );
			if (rs.next()) {
				tiene = true;
			}
			log.debug("hice next " );
 
		} catch (Exception e) {

			e.printStackTrace();
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return tiene;
	}

	/**
	 * Metodo que se encarga de listar las licencias de un trabajador
	 * registradas para una determinada fecha.
	 * @throws SQLException
	 */
	public BeanLicencia findByCodPersFecha(String dbpool, String codPers,
			String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanLicencia licencia = null;

		try {
			String fecCompara1 = Utiles.toYYYYMMDD(fecha);

			strSQL.append("select cod_pers, anno, ffin, ffinicio, licencia, qdias "
					).append( " from  t1273Licencia " ).append( " where cod_pers = ? and "
					).append( "       ffinicio <= DATE('" ).append( fecCompara1 ).append( "') and "
					).append( "       ffin >=DATE('" ).append( fecCompara1 ).append( "') ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);

			rs = pre.executeQuery();
			if (rs.next()) {

				licencia = new BeanLicencia();

				licencia.setCodPers(rs.getString("cod_pers"));
				licencia.setAnno(rs.getString("anno"));
				licencia.setFechaFin(rs.getTimestamp("ffin"));
				licencia.setFechaIni(rs.getTimestamp("ffinicio"));
				licencia.setLicencia(rs.getString("licencia"));
				licencia.setDiasAcumulados(rs.getFloat("qdias"));

			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return licencia;
	}

	/**
	 * Metodo que se encarga de determinar si un trabajador tiene registradas
	 * licencias en una fecha determindad, filtrados opcionalmente por tipo de
	 * licencia a buscar y por el numero de la licencia que no se desea
	 * considerar.
	 * @return @throws	 *         SQLException
	 */
	public boolean findByCodPersTipoFIniFFin(String dbpool, String codPers,
			String tipo, String fecha1, String numero) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean tiene = false;

		try {
			String fecCompara1 = Utiles.toYYYYMMDD(fecha1);

			strSQL.append("select cod_pers from t1273Licencia "
					).append( " where cod_pers = ? " ).append( " and DATE(ffinicio) <= DATE('"
					).append( fecCompara1 ).append( "') " ).append( " and DATE(ffin) >= DATE('"
					).append( fecCompara1 ).append( "') ");

			if (!tipo.equals("")) {
				strSQL.append(" and licencia = '" ).append( tipo ).append( "'");
			}

			if (!numero.equals("")) {
				strSQL.append(" and numero != '" ).append( numero ).append( "'");
			}

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);

			rs = pre.executeQuery();
			if (rs.next())
				tiene = true;

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
		return tiene;
	}

	/**
	 * Metodo que se encarga de calcular la cantidad de dias acumulados por un
	 * personal para un ano y tipo de licencia determinados.
	 * @throws SQLException
	 */

	public float findDiasAcumulados(String dbpool, String codPers, String anno,
			String licencia) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		float total = 0;

		try {

			log.debug("codPers :"+codPers+".");
			log.debug("anno :"+anno+".");
			log.debug("licencia :"+licencia+".");
			
			strSQL.append("select sum(t.qdias) total from t1273licencia t "
					).append( "where cod_pers = ? and anno = ? and licencia = ?");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers.trim().toUpperCase());
			pre.setString(2, anno.trim().toUpperCase());
			pre.setString(3, licencia.trim().toUpperCase());

			rs = pre.executeQuery();
			if (rs.next()) {
				total = rs.getFloat("total");
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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
	 * Metodo encargado de buscar los registros de licencia de un tipo filtrados
	 * por un criterio con un valor determinado. Join con las tablas T02Perdp y
	 * T1279Tipo_Mov
	 * @throws SQLException
	 */
	public ArrayList joinWithT02T1279(String dbpool, String tipo,
			String criterio, String valor, HashMap seguridad)
			throws SQLException {

		boolean tieneWhere = false;

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList lista = null;

		try {

			strSQL.append("select 	l.cod_pers, l.licencia, l.numero, l.anno,"
					).append( "        l.ffinicio, l.ffin, l.observ, l.anno_ref,"
					).append( "        l.numero_ref, l.area_ref, l.periodo,"
					).append( "        l.cuser_crea, l.cuser_mod, l.fcreacion, l.fmod, "
					).append( "        p.t02ap_pate, p.t02ap_mate, p.t02nombres, "
					).append( "        m.descrip, m.tipo_id, m.dias_acum, m.ind_proc, l.qdias "
					).append( "from	t1273licencia l, " ).append( "        t02perdp p, "
					).append( "        t1279tipo_mov m ");

			if (criterio.equals("0")) {
				strSQL.append(" where l.numero = '" ).append( valor.trim() ).append( "'");
				tieneWhere = true;
			}
			if (criterio.equals("1")) {
				strSQL.append(" where l.anno = '" ).append( valor.trim() ).append( "'");
				tieneWhere = true;
			}
			if (criterio.equals("2")) {
				strSQL.append(" where p.t02ap_pate like '%"
						).append( valor.trim().toUpperCase() ).append( "%'");
				tieneWhere = true;
			}
			if (criterio.equals("3")) {
				strSQL.append(" where l.ffinicio <= DATE('"
						).append( Utiles.toYYYYMMDD(valor.trim()) ).append( "')");
				tieneWhere = true;
			}
			if (criterio.equals("4")) {
				strSQL.append(" where l.ffin <= DATE('"
						).append( Utiles.toYYYYMMDD(valor.trim()) ).append( "')");
				tieneWhere = true;
			}
			if (criterio.equals("5")) {
				strSQL.append(" where l.cod_pers = '"
						).append( valor.trim().toUpperCase() ).append( "'");
				tieneWhere = true;
			}

			if (!tipo.equals("-1")) {
				if (tieneWhere) {
					strSQL.append(" and ");
				} else {
					strSQL.append(" where ");
					tieneWhere = true;
				}

				strSQL.append(" m.mov = '" ).append( tipo.trim() ).append( "'");
			}

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (tieneWhere) {
					strSQL.append(" and ");
				} else {
					strSQL.append(" where ");
					tieneWhere = true;
				}
				
				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					
					  strSQL.append(" ((substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '"
						).append( uoSeg.toUpperCase() ).append( "') ");
				
				strSQL.append(" or (substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) in ")
				.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '")
				.append( uoAO.toUpperCase() ).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null ||
						 roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) like '"
							).append( uoSeg.toUpperCase() ).append( "' ");
				} else {
					strSQL.append(" 1=2 ");
				}
			}

			//Joins
			if (tieneWhere) {
				strSQL.append(" and ");
			} else {
				strSQL.append(" where ");
				tieneWhere = true;
			}
			strSQL.append(" l.cod_pers = p.t02cod_pers and l.licencia = m.mov ");
			strSQL.append(" order by l.ffinicio desc");
			
			//strSQL.append(" order by l.numero desc");

			log.debug(strSQL.toString());
			
			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());

			rs = pre.executeQuery();

			lista = new ArrayList();
			BeanLicencia licencia = null;
			while (rs.next()) {

				licencia = new BeanLicencia();

				licencia.setCodPers(rs.getString("cod_pers"));
				licencia.setDescripcion(rs.getString("descrip"));
				licencia.setAnno(rs.getString("anno"));
				licencia.setFechaFin(rs.getTimestamp("ffin"));
				licencia.setFechaIni(rs.getTimestamp("ffinicio"));
				licencia.setLicencia(rs.getString("licencia"));
				licencia.setNumero(rs.getString("numero"));
				licencia.setObservacion(rs.getString("observ") != null ? rs
						.getString("observ").trim() : "");
				licencia.setTipo(rs.getString("tipo_id"));
				licencia.setTrabajador(rs.getString("t02ap_pate").trim().concat( " "
						).concat( rs.getString("t02ap_mate").trim() ).concat( ", "
						).concat( rs.getString("t02nombres").trim()));
				licencia.setAnoRef(rs.getString("anno_ref"));
				licencia.setNumeroRef(rs.getString("numero_ref"));
				licencia.setAreaRef(rs.getString("area_ref"));
				licencia.setPeriodo(rs.getString("periodo"));
				licencia.setMaxDiasAcum(rs.getInt("dias_acum"));
				licencia.setMasiva(rs.getString("ind_proc")!=null?(rs.getString("ind_proc").equals("1")):true);

				if (rs.getDate("fmod")!=null){
					licencia.setUsuario(rs.getString("cuser_mod"));
					licencia.setFgraba(new BeanFechaHora(rs.getDate("fmod")).getFormatDate("dd/MM/yyyy HH:mm:ss"));
				}
				else{
					licencia.setUsuario(rs.getString("cuser_crea"));
					licencia.setFgraba(new BeanFechaHora(rs.getDate("fcreacion")).getFormatDate("dd/MM/yyyy HH:mm:ss"));					
				}
				
				//Agregamos los dias de Licencia
				licencia.setDiasLicencia(String.valueOf(rs.getInt("qdias")));
				
				lista.add(licencia);
			}

 
		} catch (Exception e) {

			e.printStackTrace();
			log.error("**** SQL ERROR **** "+ e.toString());
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
	 * Metodo encargado de buscar los datos de una licencia de tipo medica. Join
	 * con la tabla T1274Licen_Med.
	 * @throws SQLException
	 */
	public void joinWithT1274(String dbpool, BeanLicencia licencia)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;

		try {

			strSQL.append("select 	c_certific, cod_cmp, fecha, enfermedad "
					).append( "from 	t1274licen_med " ).append( "where 	cod_pers = ? and "
					).append( "	periodo = ? and " ).append( "        numero = ? and "
					).append( "        ffinicio = ? ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, licencia.getCodPers());
			pre.setString(2, licencia.getPeriodo());
			pre.setString(3, licencia.getNumero());
			pre.setTimestamp(4, licencia.getFechaIni());
			rs = pre.executeQuery();

			if (rs.next()) {

				licencia.setCertificado(rs.getString("c_certific"));
				licencia.setNroCMP(rs.getString("cod_cmp"));
				licencia.setFechaCita(rs.getTimestamp("fecha"));
				licencia.setTipoEnfermedad(rs.getString("enfermedad"));
			}

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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

	public int findCantidadByTipo(HashMap datos) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int total = 0;

		try {

			strSQL.append("select count(*) total from t1273licencia t "
					).append( "where licencia = ? and cod_pers = ? ");

			con = getConnection((String) datos.get("dbpool"));
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, (String) datos.get("tipoMov"));
			pre.setString(2, (String) datos.get("userOrig"));

			rs = pre.executeQuery();
			if (rs.next()) {
				total = rs.getInt("total");
			} 

 
		} catch (Exception e) {
			log.error("**** SQL ERROR **** "+ e.toString());
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

}