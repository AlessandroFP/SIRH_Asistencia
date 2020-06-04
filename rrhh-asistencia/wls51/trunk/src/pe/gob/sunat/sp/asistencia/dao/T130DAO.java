package pe.gob.sunat.sp.asistencia.dao;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T130DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * T130HorExt
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

public class T130DAO extends DAOAccesoBD {

	private static final Logger log = Logger.getLogger(T130DAO.class);

	public T130DAO() {
	}

	/**
	 * Metodo que se encarga de buscar registros de labor excepcional de un
	 * trabajador.
	 * @throws SQLException
	 */
	public ArrayList findByCodPersEstado(String dbpool, String codPers,
			String estado) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList horasExtras = null;

		try {

			strSQL.append("select 	he.cod_pers, cod_jefe, cod_urga, f_autor, h_inic,"
					).append( "        h_term, observa, f_efect, h_salid, est_id "
					).append( "from 	t130horext he  "
					).append( "where	he.cod_pers = ? and "
					).append( "        he.est_id = ? and "
					).append( "        he.h_salid is not null");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, estado);
			rs = pre.executeQuery();
			horasExtras = new ArrayList();
			BeanHoraExtra he = null;

			while (rs.next()) {

				he = new BeanHoraExtra();

				he.setCodPers(rs.getString("cod_pers"));
				he.setCodJefe(rs.getString("cod_jefe"));
				he.setCodUO(rs.getString("cod_urga"));
				he.setFechaAutorizacion(rs.getString("f_autor"));
				he.setHoraIni(rs.getString("h_inic"));
				he.setHoraFin(rs.getString("h_term"));
				he.setObservacion(rs.getString("observa").trim());
				he.setFechaEfectiva(rs.getString("f_efect"));
				he.setHoraSalida(rs.getString("h_salid"));

				horasExtras.add(he);

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
		return horasExtras;
	}

	/**
	 * Metodo encargado de buscaro registros de labor excepcional filtrados por
	 * un criterio con un valor determinado, de los subordinados a un trabajador
	 * especifico. Join con T02Perdp
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @param codJefe
	 *            String. Numero de registro del trabajador(jefe).
	 * @return ArrayList conteniendo los registros cargados en beans de la clase
	 *         BeanHoraExtra.
	 * @throws IncompleteConversationalState
	 * @throws RemoteException
	 */
	public ArrayList joinWithT02(String dbpool, String criterio, String valor,
			String codJefe, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList horasExtras = null;

		try {

			strSQL.append("select 	he.cod_pers, he.cod_jefe, he.cod_urga, "
					).append( "        he.h_inic, he.h_term, he.observa, he.h_salid, "
					).append( "        he.f_autor, p.t02ap_pate, p.t02ap_mate, "
					).append( "        p.t02nombres " ).append( "from 	t130horext he,  "
					).append( "        t02perdp p " ).append( "where	he.cod_pers != ? and "
					).append( "        he.est_id = ? ");

			//busqueda por registro
			if (criterio.equals("0")) {
				strSQL.append(" and he.cod_pers like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			//busqueda por trabajador
			if (criterio.equals("1")) {
				strSQL.append(" and p.t02ap_pate like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			//busqueda por fecha de autorizacion
			if (criterio.equals("2")) {
				String fecCompara = Utiles.toYYYYMMDD(valor.trim());
				strSQL.append(" and DATE(f_autor[7,10]||''/''||f_autor[4,5]||''/''||f_autor[1,2]) = DATE('"
						).append( fecCompara ).append( "') ");
			}
			//busqueda por observacion
			if (criterio.equals("3")) {
				strSQL.append(" and upper(he.observa) like '%"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}

			//criterios de visibilidad
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO


				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "') ");
					
					strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ")
					.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip = '"							
					).append( uoAO ).append( "'))) ");
					
				} else if (//roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null	|| 
						roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" and he.cod_pers = p.t02cod_pers ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codJefe);
			pre.setString(2, Constantes.INACTIVO);
			rs = pre.executeQuery();
			horasExtras = new ArrayList();
			BeanHoraExtra he = null;

			while (rs.next()) {

				boolean agrega = true;
				String fechaAut = rs.getString("f_autor");
				String fA = Utiles.toYYYYMMDD(fechaAut);

				//busqueda por fecha
				if (criterio.equals("2")) {
					String fComp = Utiles.toYYYYMMDD(valor.trim());
					agrega = (fComp.equals("") || (fComp.compareTo(fA) <= 0));
				}

				if (agrega) {

					he = new BeanHoraExtra();

					he.setCodPers(rs.getString("cod_pers"));
					he.setCodJefe(rs.getString("cod_jefe"));
					he.setCodUO(rs.getString("cod_urga"));
					he.setTrabajador(rs.getString("t02ap_pate").concat( " "
							).concat( rs.getString("t02ap_mate") ).concat( ", "
							).concat( rs.getString("t02nombres")));
					he.setFechaAutorizacion(fechaAut);
					he.setHoraIni(rs.getString("h_inic"));
					he.setHoraFin(rs.getString("h_term"));
					he.setObservacion(rs.getString("observa").trim());
					he.setHoraSalida(rs.getString("h_salid"));

					horasExtras.add(he);
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
		return horasExtras;
	}

	/**
	 * M�todo encargado de obtener el registro de autorizaci�n de labor
	 * excepcionalde un trabajador
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPersonal
	 *            N�mero de registro del trabajador.
	 * @return Bean de la clase BeanHoraExtra.
	 * @throws SQLException
	 */
	public BeanHoraExtra joinWithT02T02(String dbpool, String codPersonal)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		BeanHoraExtra salida = null;

		try {

			strSQL.append("select 	he.cod_pers, he.cod_jefe, he.cod_urga, "
					).append( "        he.h_inic, he.h_term, he.h_salid, "
					).append( "        he.f_autor, he.observa, "
					).append( "        p1.t02ap_pate as patTrab, "
					).append( "        p1.t02ap_mate as matTrab, "
					).append( "        p1.t02nombres as nomTrab, "
					).append( "        p2.t02ap_pate as patJefe, "
					).append( "        p2.t02ap_mate as matJefe, "
					).append( "        p2.t02nombres as nomJefe "
					).append( "from 	t130horext he,  " ).append( "        t02perdp p1, "
					).append( "        t02perdp p2 " ).append( "where	he.cod_pers = ? and "
					).append( "        he.f_autor = ? and "
					).append( "        he.est_id = ? and "
					).append( "	he.cod_pers = p1.t02cod_pers and"
					).append( "	he.cod_jefe = p2.t02cod_pers");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPersonal);
			pre.setString(2, Utiles.obtenerFechaActual());
			pre.setString(3, Constantes.INACTIVO);
			rs = pre.executeQuery();

			if (rs.next()) {

				salida = new BeanHoraExtra();

				salida.setCodPers(rs.getString("cod_pers"));
				salida.setCodJefe(rs.getString("cod_jefe"));
				salida.setCodUO(rs.getString("cod_urga"));
				salida.setTrabajador(rs.getString("patTrab").concat( " "
						).concat( rs.getString("matTrab") ).concat( ", "
						).concat( rs.getString("nomTrab")));
				salida.setFechaAutorizacion(rs.getString("f_autor"));
				salida.setHoraIni(rs.getString("h_inic"));
				salida.setHoraFin(rs.getString("h_term"));
				salida.setHoraSalida(rs.getString("h_salid"));
				salida.setObservacion(rs.getString("observa").trim());
				salida.setJefe(rs.getString("patJefe").trim().concat( " "
						).concat( rs.getString("matJefe").trim() ).concat( ", "
						).concat( rs.getString("nomJefe").trim()));

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
		return salida;
	}

	/**
	 * M�todo encargado de verificar si el trabajador posee una autorizaci�n de
	 * labor excepcional para la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            N�mero de registro del trabajador.
	 * @param fecha
	 *            Fecha de b�squeda.
	 * @return Boolean. Devuelve "verdadero" si existe un registro de labor
	 *         excepcional para la fecha y trabajador indicados.
	 * @throws SQLException
	 */
	public boolean findByCodPersFAutor(String dbpool, String codPers,
			String fecha) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		boolean tiene = false;

		try {
			String fecCompara = Utiles.toYYYYMMDD(fecha);

			strSQL.append("select cod_pers, f_autor from t130horext "
					).append( " where cod_pers = ? "
					).append( " and DATE(f_autor[7,10]||''/''||f_autor[4,5]||''/''||f_autor[1,2]) = DATE('"
					).append( fecCompara ).append( "')");

			con = getConnection(dbpool);

			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);

			rs = pre.executeQuery();
			if (rs.next())
				tiene = true;

 
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
		return tiene;
	}

	/**
	 * M�todo encargado de buscar los registros de autorizaci�n de labor
	 * excepcional filtrados por un criterio con un valor determinado.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param criterio
	 *            String. Criterio de filtro de los registros.
	 * @param valor
	 *            String. Valor de filtro para el criterio dado.
	 * @return @throws
	 *         SQLException
	 */
	public ArrayList findAutorizadosByCriterioValor(String dbpool,
			String criterio, String valor, HashMap seguridad)
			throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList personal = null;

		try {

			strSQL.append("select 	distinct cod_pers " ).append( "from 	t130horext "
					).append( "where	h_salid is not null ");

			if (criterio.equals("0")) {
				strSQL.append(" and cod_pers = '" ).append( valor.trim().toUpperCase()
						).append( "'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and cod_urga = '" ).append( valor.trim().toUpperCase()
						).append( "'");
			}

			//criterios de visibilidad
			if (seguridad != null && !seguridad.isEmpty()) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//DEC 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					strSQL.append(" and ((cod_urga like '"
							).append( uoSeg.trim().toUpperCase() ).append( "') ");
					
					strSQL.append(" or (cod_urga in ")
					.append(" (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip = '"							
					).append( uoAO.trim().toUpperCase() ).append( "'))) ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" order by cod_pers ");

			con = getConnection(dbpool);
			pre = con.prepareStatement(strSQL.toString());
			rs = pre.executeQuery();
			personal = new ArrayList();

			while (rs.next()) {
				personal.add(rs.getString("cod_pers"));
			}
			
			log.debug(strSQL.toString());//ICAPUNAY 20/03/2013 - Memo 00023_2013_4F3100

 
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
		return personal;
	}

}