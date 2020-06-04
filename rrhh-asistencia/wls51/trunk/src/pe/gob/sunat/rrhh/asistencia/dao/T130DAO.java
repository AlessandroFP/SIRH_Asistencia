package pe.gob.sunat.rrhh.asistencia.dao;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.sol.IncompleteConversationalState;
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

public class T130DAO extends DAOAbstract{

	//private static final Logger log = Logger.getLogger(T130DAO.class);
	private String FIND_FECHAS = "select 	DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) fecha, cnt_tiempo_actual, h_inic " +
	  //JRR - 16/09/2009
    //	  " (select SUM(ROUND((cnt_tiempo_actual / 60),2)) from t130horext h where 	h.cod_pers = ? and  h.cnt_tiempo_actual > 0 and h.est_id = '2') total " +
	  " from 	t130horext h where 	h.cod_pers = ?  and  h.cnt_tiempo_actual > 0 "+
	  //JRR - 26/06/2009
	  " and h.est_id = '2' order by 1 ASC ";
	  
	//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
	private String FIND_FECHAS_SEGUN_FECINI = "select 	DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) fecha, cnt_tiempo_actual, h_inic " +	
	  " from 	t130horext h where 	h.cod_pers = ?  and  h.cnt_tiempo_actual > 0 "+	
	  " and DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2])>=? "+	
	  " and h.est_id = '2' order by 1 ASC ";
	//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009

  //JRR - 16/09/2009
	private String FIND_ACUMULADO = "select SUM(ROUND((cnt_tiempo_actual / 60),2))  as total from t130horext h where 	h.cod_pers = ? and  h.cnt_tiempo_actual > 0 and h.est_id = '2' "; 
	
	//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
	private String FIND_ACUMULADO_SEGUN_FECINI = "select SUM(ROUND((cnt_tiempo_actual / 60),2))  as total from t130horext h where 	h.cod_pers = ? and  h.cnt_tiempo_actual > 0 and h.est_id = '2' and DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2])>=? ";
	
	//FRD 03/07/2009
	private String FIND_FECHAS_LE_NO_AUTORIZADAS = "Select h.f_autor fecha,t.des_turno,h_inic,h_term, " +
	" ROUND((cnt_tiempo_actual / 60),2) horasle " + 
	" From t130horext h, t1270turnoperson p, t45turno t " +
	//" Where h.cod_pers = ? and h.cod_urga = ? and " + 
	" Where h.cod_pers = ? and " +
	" h.cnt_tiempo_actual > 0 and p.est_id = 1 and " + 
	" (h.est_id != '2' or h.est_id is null) and " + 
	//" h.cod_pers = p.cod_pers and h.cod_urga = p.u_organ and " +
	" h.cod_pers = p.cod_pers and " +
	" p.turno = t.cod_turno and " +
	" DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) >= p.fini and " +
	" DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) <= p.ffin and " +
	" DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) >= ? " ;
		
	//DATE("06/01/2009") <=
	//DATE(h.f_autor[4,5]||'/'||h.f_autor[1,2]||'/'||h.f_autor[7,10]) and
	//DATE("06/02/2009") >=
	//DATE(h.f_autor[4,5]||'/'||h.f_autor[1,2]||'/'||h.f_autor[7,10]) and
	//Order by 1 DESC
	
	//PRAC-ASANCHEZ 27/05/2009
	private String FIND_LE_PENDIENTES_COMPENSAR = "Select h.f_autor fecha,t.des_turno,h_inic,h_term, " +
	" cnt_tiempo_actual minutosle, " +
	" t.h_inicio, t.h_fin " +
//	" ROUND((cnt_tiempo_actual / 1440),2) diasle " + 
	" From t130horext h, t1270turnoperson p, t45turno t " +
//	" Where h.cod_pers = ? and h.cod_urga = ? and " +
	" Where h.cod_pers = ? and " +
	" h.cnt_tiempo_actual > 0 and p.est_id = 1 and " + 
	" h.est_id = '2' and " +	
	//" h.cod_pers = p.cod_pers and h.cod_urga = p.u_organ and " +
	" h.cod_pers = p.cod_pers and " +
	" p.turno = t.cod_turno and " +
	" DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) >= p.fini and " +
	" DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) <= p.ffin " ;
	//
	
	//FRD 03/07/2009
	private String UPDATE_ESTADO_LE ="Update t130horext Set est_id = '2' " + 
	" Where cod_pers = ? And f_autor = ? And h_inic = ?";
	
	private String updateLE ="Update t130horext Set cnt_tiempo_actual = ?, fmod = ?, cuser_mod = ? " + 
			"Where cod_pers = ? And f_autor = ? And h_inic = ?";
	
	private String existeLE = "select unique cod_pers, f_autor from t130horext where cod_pers = ? and f_autor = ? ";
	private String obtSaldoFecha = "select sum (cnt_tiempo_actual) cantidad from t130horext "+
					" where cod_pers = ?  and DATE(f_autor[7,10]||'/'||f_autor[4,5]||'/'||f_autor[1,2]) < ? and cnt_tiempo_actual > 0";
//	private String obtLErango = "select sum (cnt_tiempo_acum) cantidad from t130horext "+
//	" where cod_pers = ?  and DATE(f_autor[7,10]||'/'||f_autor[4,5]||'/'||f_autor[1,2]) >= ? and DATE(f_autor[7,10]||'/'||f_autor[4,5]||'/'||f_autor[1,2]) <= ? ";

	//JRR - 02/03/2009
	private String obtLErango = "select sum (cnt_tiempo_actual) cantidad from t130horext "+
	" where cod_pers = ?  and DATE(f_autor[7,10]||'/'||f_autor[4,5]||'/'||f_autor[1,2]) >= ? and DATE(f_autor[7,10]||'/'||f_autor[4,5]||'/'||f_autor[1,2]) <= ? "+
	//JRR - 26/06/2009
	" and est_id = '2' ";

	private DataSource datasource;

	public T130DAO() {
	}

	  /**
	   * 
	   * Este constructor del DAO dicierne como crear el datasource
	   * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	   * mejorar la invocacion del dao.
	   * 
	   * @param datasource Object
	   */
	  public T130DAO(Object datasource) {
	    if (datasource instanceof DataSource)
	      this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.datasource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
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

			con = getConnection(getDataSource((String)dbpool));
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
			//if (seguridad != null) {
			if (seguridad != null && !seguridad.isEmpty()) {				

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} 
				//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
			  else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
				    strSQL.append(" and ((substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "') ");
				    strSQL.append(" or (substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append( uoAO ).append( "'))) ");
			  }
        //else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null || //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
        else if (
        //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
         roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '").append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" and he.cod_pers = p.t02cod_pers ");

			con = getConnection(getDataSource((String)dbpool));
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
	 * M?todo encargado de obtener el registro de autorizaci?n de labor
	 * excepcionalde un trabajador
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPersonal
	 *            N?mero de registro del trabajador.
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

			con = getConnection(getDataSource((String)dbpool));
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
	 * M?todo encargado de verificar si el trabajador posee una autorizaci?n de
	 * labor excepcional para la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            N?mero de registro del trabajador.
	 * @param fecha
	 *            Fecha de b?squeda.
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

			con = getConnection(getDataSource((String)dbpool));

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
	 * M?todo encargado de buscar los registros de autorizaci?n de labor
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

			//strSQL.append("select 	distinct cod_pers " ).append( "from 	t130horext "
			//		).append( "where	h_salid is not null ");
			
			//Agregamos estas lineas para ver quienes tienes turno administrativo 
			//EBV 04/09/2006
			strSQL.append("select distinct cod_pers from t1270turnoperson t1270, t45turno t45 " 
					);
					/*.append(" where t1270.cod_pers not in (select cod_personal from t1933_supervisor "
					).append(" where ind_estado='1' and  cod_mov = 'LE'  ");
							if (criterio.equals("0")) {
								strSQL.append(" and cod_personal = '" ).append( valor.trim().toUpperCase()
										).append( "'  ");
							}
							if (criterio.equals("1")) {
								strSQL.append(" and cod_uorgan = ') " ).append( valor.trim().toUpperCase()
										).append( "'  ");
							}*/
					strSQL.append("   where t1270.turno = t45.cod_turno "
					).append(" and t1270.est_id = '1' "
					).append(" and t45.est_id = '1' "
					).append(" and t45.scontrol_id = '1' "
					).append(" and t45.oper_id != '1' ");
			
					
			//JRR - PARA CRITERIO DIFERENTE A '0' O '1' LO HAR? INSTITUCIONAL DADO QUE NO SE LE AGREGA FILTRO AL QUERY	
					
			if (criterio.equals("0")) {
				strSQL.append(" and t1270.cod_pers = '" ).append( valor.trim().toUpperCase()
						).append( "'");
			}

			if (criterio.equals("1")) {
				strSQL.append(" and t1270.u_organ = '" ).append( valor.trim().toUpperCase()
						).append( "'");
			}
			
//			if (criterio.equals("1")) {
//			strSQL.append(" and cod_urga = '" ).append( valor.trim().toUpperCase()
//					).append( "'");
//		}

			
			//criterios de visibilidad
			/*
			if (seguridad != null) {

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String uoAO = (String) seguridad.get("uoAO");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO 
	        if (log.isDebugEnabled()) log.debug("roles: "+roles); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
	        if (log.isDebugEnabled()) log.debug("uoAO: "+uoAO); //ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO

				if (roles.get(Constantes.ROL_ANALISTA_CENTRAL) != null) {
					strSQL.append(" and 1=1 ");
				} else if (roles.get(Constantes.ROL_ANALISTA_OPERATIVO) != null) {
					//strSQL.append(" and u_organ like '").append( uoSeg.trim().toUpperCase() ).append( "' ");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
          strSQL.append(" and ((u_organ like '").append( uoSeg.trim().toUpperCase() ).append( "') ");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO  
          strSQL.append(" or (u_organ in (select t99siglas from t99codigos where t99cod_tab='471' and t99tip_desc='D' and t99descrip= '").append(uoAO).append( "'))) ");//ICR 28/12/2012 NUEVAS UUOOS ANALISTA OPERATIVO
				} else {
					strSQL.append(" and 1=2 ");
				}
			}
			*/

			strSQL.append(" order by cod_pers ");

			con = getConnection(getDataSource((String)dbpool));
			log.debug(strSQL.toString());
			pre = con.prepareStatement(strSQL.toString());
			rs = pre.executeQuery();
			personal = new ArrayList();

			while (rs.next()) {
				personal.add(rs.getString("cod_pers"));
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
		return personal;
	}
	
	/**
	 * Metodo que se encarga de buscar registros de labor excepcional de un
	 * trabajador.
	 * @throws SQLException
	 */
	public List findByCodPersEstadoMes(String dbpool, String codPers,
			String estado, String fechaIni, String fechaFin) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		List horasExtras = null;

		try {
			String fIni = Utiles.toYYYYMMDD(fechaIni);
			String fFin = Utiles.toYYYYMMDD(fechaFin);
			strSQL.append("select 	distinct he.cod_pers, fecha "
					).append( "from 	t1275marcacion he  "
					).append( "where	he.cod_pers = ?  "
					).append( " and       he.fecha >= date('").append(fIni).append("') "
					).append( " and       he.fecha <= date('").append(fFin).append("') "							
					).append( " and       he.sdel = ? ");

			
			con = getConnection(getDataSource((String)dbpool));
			if (log.isDebugEnabled()) log.debug("findByCodPersEstadoMes - strSQL: " + strSQL.toString());
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			pre.setString(2, estado);
			rs = pre.executeQuery();
			horasExtras = new ArrayList();
			BeanHoraExtra he = null;

			while (rs.next()) {

				he = new BeanHoraExtra();

				he.setCodPers(rs.getString("cod_pers"));
				java.util.Date fecha = rs.getDate("fecha");
				
				he.setFechaAutorizacion(Utiles.dateToString(fecha));

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
	 * Metodo que se encarga de buscar registros de labor excepcional de un
	 * trabajador.
	 * @throws SQLException
	 */
	public ArrayList findfechas(String dbpool, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList horasExtras = null;

		try {
			
			strSQL.append("select 	DATE(h.f_autor[7,10]||''/''||h.f_autor[4,5]||''/''||h.f_autor[1,2]) fecha, cnt_tiempo_actual, h_inic ")
		 	  .append("from 	t130horext h	 ")
		    .append("where 	h.cod_pers = ?  ")
			.append( " and  h.cnt_tiempo_actual > 0 ")
			//JRR - 16/09/2009
			.append(" and h.est_id = '2' ");
			
			strSQL.append(" order by 1 desc ");
			
			con = getConnection(getDataSource((String)dbpool));
			//log.debug(strSQL.toString());
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			
			rs = pre.executeQuery();
			horasExtras = new ArrayList();
			BeanHoraExtra he = null;

			while (rs.next()) {

				he = new BeanHoraExtra();

				java.util.Date fecha = rs.getDate("fecha");
				
				//log.debug("fecha 1 : "+ fecha);
				
				he.setFechaAutorizacion(Utiles.dateToString(fecha));
				he.setAcumulado(rs.getFloat("cnt_tiempo_actual"));
				he.setHoraIni(rs.getString("h_inic"));

				//log.debug("fecha 2 : "+ he.getFechaAutorizacion());
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
	
	/**FRD - 02/07/2009
	 * Metodo que se encarga de buscar registros de labor excepcional de un trabajador para ser autorizados y en su futuro compensarlos.
	 * @param datos 
	 * @throws DAOException
	 */
	public List findLENoAutorizadas(Map datos) throws DAOException{
		List fechasLENoAutorizadas = null;
			
			String codPers = (String)datos.get("trabajador");
			Date fecha_ingreso = (Date)datos.get("fecha_ingreso");
//			String coduorga = (String)datos.get("uuoo");
			String fechaIni = (String)datos.get("fechaIni");
			fechaIni = fechaIni.substring(6,10) + "/" + fechaIni.substring(3,5) + "/" + fechaIni.substring(0,2);  
			String fechaFin = (String)datos.get("fechaFin");
			fechaFin = fechaFin.substring(6,10) + "/" + fechaFin.substring(3,5) + "/" + fechaFin.substring(0,2);
			
			FIND_FECHAS_LE_NO_AUTORIZADAS = FIND_FECHAS_LE_NO_AUTORIZADAS + " and " ; 
			FIND_FECHAS_LE_NO_AUTORIZADAS = FIND_FECHAS_LE_NO_AUTORIZADAS + " DATE('" + fechaIni + "') <= " ;
			FIND_FECHAS_LE_NO_AUTORIZADAS = FIND_FECHAS_LE_NO_AUTORIZADAS + " DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) and ";
			FIND_FECHAS_LE_NO_AUTORIZADAS = FIND_FECHAS_LE_NO_AUTORIZADAS + " DATE('" + fechaFin + "') >= " ;
			FIND_FECHAS_LE_NO_AUTORIZADAS = FIND_FECHAS_LE_NO_AUTORIZADAS + " DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) ";
			//FIND_FECHAS_LE_NO_AUTORIZADAS = FIND_FECHAS_LE_NO_AUTORIZADAS + " Order by 1 DESC ";
			FIND_FECHAS_LE_NO_AUTORIZADAS = FIND_FECHAS_LE_NO_AUTORIZADAS + " Order by DATE(f_autor[7,10]||'/'||f_autor[4,5]||'/'||f_autor[1,2]) DESC, H_INIC ASC ";
			
			fechasLENoAutorizadas = executeQuery(datasource, FIND_FECHAS_LE_NO_AUTORIZADAS, new Object[]{codPers, fecha_ingreso}); //, coduorga});
			return fechasLENoAutorizadas;
	}
	
	/**PRAC-ASANCHEZ 27/08/2009
	 * Metodo que se encarga de buscar registros de labor excepcional para el reporte de LE.
	 * @param datos 
	 * @throws DAOException
	 */
	public List findLEPendientesCompensar(HashMap params) throws DAOException{
		List lePendientesCompensar = null;

			String codPers = (String)params.get("trabajador");
//			String coduorga = (String)params.get("uuoo");
			
			String query = "";
			String fechaIni = (String)params.get("fechaIni");
			fechaIni = fechaIni.substring(6,10) + "/" + fechaIni.substring(3,5) + "/" + fechaIni.substring(0,2);  
			String fechaFin = (String)params.get("fechaFin");
			fechaFin = fechaFin.substring(6,10) + "/" + fechaFin.substring(3,5) + "/" + fechaFin.substring(0,2);
			
			query = FIND_LE_PENDIENTES_COMPENSAR + " and " ; 
			query = query + " DATE('" + fechaIni + "') <= " ;
			query = query + " DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) and ";
			query = query + " DATE('" + fechaFin + "') >= " ;
			query = query + " DATE(h.f_autor[7,10]||'/'||h.f_autor[4,5]||'/'||h.f_autor[1,2]) ";
			query = query + " Order by DATE(f_autor[7,10]||'/'||f_autor[4,5]||'/'||f_autor[1,2]) DESC, H_INIC ASC ";
			
			lePendientesCompensar = executeQuery(datasource, query, new Object[]{codPers}); //, coduorga});
			
			//log.debug("lePendientesCompensar: " + lePendientesCompensar);
			
			return lePendientesCompensar;
	}
	
	
	/**JROJAS4 30/09/2009
	 * Metodo que se encarga de buscar el acumulado de labor excepcional para el reporte de LE.
	 * @param datos 
	 * @throws DAOException
	 */
	public Map findAcumuladoLE(String codPers) throws DAOException{

		Map acumuladoLE = executeQueryUniqueResult(datasource, FIND_ACUMULADO, new Object[]{codPers});
		
		return acumuladoLE;
	}
	
	

  //ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
	 /** Metodo que se encarga de buscar el acumulado de labor excepcional desde una fecha de inicio param?trica para el reporte de LE.
	 * @param datos 
	 * @throws DAOException
	 */
	public Map findAcumuladoLE_Mod(String codPers, String fechaini) throws DAOException{

    FechaBean fecini = new FechaBean(fechaini, "dd/MM/yyyy");
		Map acumuladoLE = executeQueryUniqueResult(datasource, FIND_ACUMULADO_SEGUN_FECINI, new Object[]{codPers,fecini.getSQLDate()});
		
		return acumuladoLE;
	}
	//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
	
	
	/**
	 * Metodo que inserta registro de Labor Excepcional
	 * @throws SQLException
	 */
	public boolean insertLE(String dbpool, 
			String codPers, String f_autor, String h_inic, String codUO, String h_term, //String est_id,
			int acum, int actual,String usuario)
			throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {
			
			strUpd = "Insert into t130horext (" + 
			"cod_pers, f_autor, h_inic, cod_urga, h_term, "+ //est_id,"+
			"cnt_tiempo_acum, cnt_tiempo_actual,fcreacion,cod_user) " + 
            "Values (?,?,?,?,?,?,?,?,?)";

			con = getConnection(getDataSource((String)dbpool));
			pre = con.prepareStatement(strUpd);
			pre.setString(1, codPers );
			pre.setString(2, f_autor);
			pre.setString(3, h_inic);
			pre.setString(4, codUO);
			//pre.setDate(5, new BeanFechaHora(fechaMarcacion).getSQLDate());
			pre.setString(5, h_term);
			//PRAC-ASANCHEZ 26/08/2009
			//pre.setString(6, est_id);
			//
			pre.setInt(6, acum);
			pre.setInt(7, actual);
			FechaBean fecha = new FechaBean();
			pre.setTimestamp(8, fecha.getTimestamp());
			pre.setString(9, usuario);
			
			int res = pre.executeUpdate();

			result = (res > 0);
			

		}

		catch (Exception e) {
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
	 * Metodo que Actualiza registro de Horas Extras
	 * @throws SQLException
	 */
	public boolean updateLE(String dbpool, String codPers, String fecha, String hora,
			float tempnum, String usuario)
			
			throws SQLException {

		String strUpd = "";
		PreparedStatement pre = null;
		Connection con = null;
		boolean result = false;

		try {


			
			strUpd = "Update t130horext Set cnt_tiempo_actual = ?, fmod = ?, cuser_mod = ? " + 
			"Where cod_pers = ? And f_autor = ? And h_inic = ? ";
			
			con = getConnection(getDataSource((String)dbpool));
			pre = con.prepareStatement(strUpd);
			pre.setFloat(1, tempnum);
			FechaBean fecha1 = new FechaBean();
			pre.setTimestamp(2, fecha1.getTimestamp());
			//pre.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			pre.setString(3, usuario);
			pre.setString(4, codPers );
			pre.setString(5, fecha);
			pre.setString(6,hora);
			
			int res = pre.executeUpdate();
			
			result = (res > 0);
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
	
	
	//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009
	/**
	 * Metodo que se encarga de buscar registros de labor excepcional de un
	 * trabajador.
	 * @throws SQLException
	 */
	public List findfechas( String codPers) throws DAOException {

		List horasExtras = new ArrayList();
		List fechasExtras = new ArrayList();			
			horasExtras = executeQuery(datasource, FIND_FECHAS, new Object[]{codPers});		
			
			BeanHoraExtra he = null;
			Map horas = new HashMap();
			//log.debug("horasExtras "+horasExtras);
			if (horasExtras!=null){
				//log.debug("horasExtras "+horasExtras.size());
				for (int i=0; i<horasExtras.size();i++){

					horas = (HashMap)horasExtras.get(i);
					he = new BeanHoraExtra();
					//log.debug("horas "+horas);
					String oo=horas.get("cnt_tiempo_actual").toString().trim();
					Date ss= (Date)horas.get("fecha");
					FechaBean fb = new FechaBean();
					fb.setFecha(ss);
					he.setCodPers(codPers);					
					he.setFechaAutorizacion(fb.getFormatDate("MM/dd/yyyy"));
					he.setFechaCompensacion((String)horas.get("fecha_desc"));
					he.setAcumulado(Float.valueOf(oo).floatValue());
					he.setHoraIni((String)horas.get("h_inic"));
					//JROJAS4 - 16/09/2009
					//he.setDescAcumulado(horas.get("total").toString().trim());
					//log.debug("fecha 2 : "+ he.getFechaAutorizacion());
					fechasExtras.add(he);
				}
				//log.debug("fechasExtras "+fechasExtras.size());
			}


		return fechasExtras;
	}
	//ICAPUNAY-PAS20144EB20000059-Visualizar informacion desde 01/01/2009

	
	/**
	 * Metodo que se encarga de buscar registros de labor excepcional de un
	 * trabajador desde una fecha de inicio param?trica.
	 * @throws SQLException
	 */
	public List findfechas_Mod( String codPers, String fechaini) throws DAOException {

    FechaBean fecini = new FechaBean(fechaini, "dd/MM/yyyy");
		List horasExtras = new ArrayList();
		List fechasExtras = new ArrayList();			
			horasExtras = executeQuery(datasource, FIND_FECHAS_SEGUN_FECINI, new Object[]{codPers,fecini.getSQLDate()});		
			
			BeanHoraExtra he = null;
			Map horas = new HashMap();
	
			if (horasExtras!=null){
		
				for (int i=0; i<horasExtras.size();i++){

					horas = (HashMap)horasExtras.get(i);
					he = new BeanHoraExtra();				
					String oo=horas.get("cnt_tiempo_actual").toString().trim();
					Date ss= (Date)horas.get("fecha");
					FechaBean fb = new FechaBean();
					fb.setFecha(ss);
					he.setCodPers(codPers);					
					he.setFechaAutorizacion(fb.getFormatDate("MM/dd/yyyy"));
					he.setFechaCompensacion((String)horas.get("fecha_desc"));
					he.setAcumulado(Float.valueOf(oo).floatValue());
					he.setHoraIni((String)horas.get("h_inic"));			
					fechasExtras.add(he);
				}		
			}
		return fechasExtras;
	}
	
	/**
	 * Metodo que Actualiza registro de Horas Extras
	 * @throws SQLException
	 */
	public boolean updateLE( String codPers, String fecha, String hora,
			float tempnum, String usuario)
			
			throws DAOException {

		FechaBean fecha1 = new FechaBean();
		
		//String numero = String.valueOf(tempnum);
		
		Integer pp = new Integer(new Float(tempnum).intValue());
		
		//log.debug("fecha le "+fecha);

		int resulta = executeUpdate(datasource, updateLE, new Object[]{pp,fecha1.getTimestamp(), usuario, codPers, fecha, hora});
		//log.debug("pp "+pp);
		boolean result = (resulta> 0);


		return result;
	}
	
	/**FRD - 02/07/2009
	 * Metodo que Actualiza el estado de registro de Horas Extras
	 * @throws DAOException
	 */
	public void updateEstadoLE( String codPers, String fautor, String hinic) throws DAOException {
		executeUpdate(datasource, UPDATE_ESTADO_LE, new Object[]{codPers,fautor,hinic});
	}
	
	/**
	 * M?todo encargado de verificar si el trabajador posee una autorizaci?n de
	 * labor excepcional para la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            N?mero de registro del trabajador.
	 * @param fecha
	 *            Fecha de b?squeda.
	 * @return Boolean. Devuelve "verdadero" si existe un registro de labor
	 *         excepcional para la fecha y trabajador indicados.
	 * @throws SQLException
	 */
	public boolean findByCodPersFAutor( String codPers,
			String fecha) throws DAOException {

		Map rs = new HashMap();
		boolean tiene = false;

			rs = executeQueryUniqueResult(datasource,existeLE, new Object[]{codPers,fecha});
			if (rs!=null)
				{ if (rs.size()>0)
					tiene = true;
				}

 
		return tiene;
	}

	
	/**
	 * M?todo encargado de verificar si el trabajador tiene saldo para la fecha indicada.
	 * 
	 * @param codPers
	 *            N?mero de registro del trabajador.
	 * @param fecha
	 *            Fecha de b?squeda.
	 * @return integer. Devuelve la cantidad de minutos de saldo para la fecha ingresada.
	 * @throws SQLException
	 */
	public Integer findSaldobyFAutor( String codPers,
			String fecha) throws SQLException {

		Map rs = new HashMap();
		Integer cantidad = new Integer(new Float("0").intValue());
		String cadena = "0";
		FechaBean fb = new FechaBean(fecha, "dd/MM/yyyy");
		rs = executeQueryUniqueResult(datasource,obtSaldoFecha, new Object[]{codPers,fb.getSQLDate()});
		if (rs!=null)
		{
			 if (rs.size()>0)
			 {
				 //log.debug("nombre "+rs.get("cantidad_desc").getClass().getName());
				 //log.debug("nombre "+rs.get("cantidad").getClass().getName());
				 cadena = rs.get("cantidad")!=null?rs.get("cantidad").toString().trim():"0";
			 }
			if (cadena==null){
				cadena="0";
			} 
			cantidad = new Integer(new Float(cadena).intValue());
			if (cantidad == null) {
				cantidad = new Integer(new Float("0").intValue());
			}
			 
		}

		return cantidad;

	}
	
	/**
	 * M?todo encargado de verificar si el trabajador tiene saldo para la fecha indicada.
	 * 
	 * @param String codPers
	 *            N?mero de registro del trabajador.
	 * @param String fechaini
	 * 	Fecha de Inicio del rango a buscar
	 * @param String fechafin
	 *  Fecha de Fin del rango a buscar
	 * @return cantidad
	 *  Devuelve la cantidad de minutos de saldo para el rango de fechas ingresada.
	 * @throws SQLException
	 */
	public Integer findSaldobyRango( String codPers,
			String fechaini, String fechafin) throws SQLException {

		Map rs = new HashMap();
		Integer cantidad = new Integer("0");
		String cadena = "0";
		FechaBean fb = new FechaBean(fechaini, "dd/MM/yyyy");
		FechaBean fb1 = new FechaBean(fechafin, "dd/MM/yyyy");
		rs = executeQueryUniqueResult(datasource,obtLErango, new Object[]{codPers,fb.getSQLDate(),fb1.getSQLDate()});
		if (rs!=null)
		{
			 if (rs.size()>0)
			 {
				 //log.debug("nombre "+rs.get("cantidad_desc").getClass().getName());
				 //log.debug("nombre "+rs.get("cantidad").getClass().getName());
				 cadena = rs.get("cantidad")!=null?rs.get("cantidad").toString().trim():"0";
			 }
			cantidad = new Integer(cadena);
		 
		}

		return cantidad;

	}	
	
	/**
	 * Metodo encargado de verificar si el trabajador posee una autorizaci?n de
	 * labor excepcional para la fecha indicada.
	 * 
	 * @param dbpool
	 *            String. Pool de conexiones.
	 * @param codPers
	 *            N?mero de registro del trabajador.
	 * @param fecha
	 *            Fecha de b?squeda.
	 * @return Boolean. Devuelve "verdadero" si existe un registro de labor
	 *         excepcional para la fecha y trabajador indicados.
	 * @throws SQLException
	 */
	public boolean findByCodPersFAutorAcumIgualActual(String dbpool, String codPers,
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
					).append( fecCompara ).append( "')"
					).append(" and cnt_tiempo_acum == cnt_tiempo_actual");

			con = getConnection(getDataSource((String)dbpool));

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
	  * @param datos
	  * @return
	  * @throws DAOException
	  */
	public void deleteLE(String cod_pers, String f_autor){
		try {
			log.debug("cod_pers:"+ cod_pers +"/"+ "f_autor:"+ f_autor);
			String fecCompara = Utiles.toYYYYMMDD(f_autor);
			String deleteLE = "DELETE FROM t130horext WHERE cod_pers = ? " +
					" and DATE(f_autor[7,10]||''/''||f_autor[4,5]||''/''||f_autor[1,2]) = DATE('"+fecCompara+"')";

			executeUpdate(datasource, deleteLE, new Object[]{cod_pers});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la actualizacion. T3336DAO - [deleteDerHabiente]");
		} finally {	
		}
	}

}