package pe.gob.sunat.rrhh.asistencia.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

import pe.gob.sunat.sp.asistencia.bean.BeanHoraExtra;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * <p>
 * Title: T132DAO
 * </p>
 * <p>
 * Description: Clase encargada de administrar las consultas a la tabla
 * t132HorAcu
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

public class T132DAO extends DAOAbstract {

	//private static final Logger log = Logger.getLogger(T132DAO.class);
	private String MODIFICAR_ACUM ="Update t132horacu Set t_acum = ?, fmod = ?, cuser_mod = ? " + 
	"Where cod_pers = ? ";
	private String OBTENER_SALDO = "select t_acum from 	t132horacu  Where cod_pers  = ? ";
	
	private DataSource datasource;
	
	public T132DAO() {
	}

	  /**
	   * 
	   * Este constructor del DAO dicierne como crear el datasource
	   * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	   * mejorar la invocacion del dao.
	   * 
	   * @param datasource Object
	   */
	  public T132DAO(Object datasource) {
	    if (datasource instanceof DataSource)
	      this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.datasource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	  }
	  
	/**
	 * Metodo encargado de listar los registros de horas acumuladas de los
	 * trabajadores de una determinada unidad organizacional, filtrados por un
	 * criterio con un valor determinado.
	 * @throws SQLException
	 */
	public ArrayList joinWithT02T99(String dbpool, String criterio,
			String valor, String codUO, HashMap seguridad) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		ArrayList acumulados = null;

		try {

			strSQL.append("select 	ha.cod_pers, ha.t_acum, ha.est_id, p.t02cod_pers, "
					+ "        substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) cod_uorga, substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) cod_cate, p.t02ap_pate, "
					+ "        p.t02ap_mate, p.t02nombres, c.t99descrip "
					+ "from 	t132horacu ha,  "
					+ "        t02perdp p, "
					+ "        t99codigos c " 
					+ "where	ha.est_id = ? ");

			//busqueda por registro
			if (criterio.equals("0")) {
				strSQL.append(" and ha.cod_pers like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			//busqueda por trabajador
			if (criterio.equals("1")) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}
			//busqueda por categoria
			if (criterio.equals("2")) {
				strSQL.append(" and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) like '"
						).append( valor.trim().toUpperCase() ).append( "%' ");
			}

			//criterios de visibilidad
			//if (seguridad != null) {
			if (seguridad != null && !seguridad.isEmpty()) {				

				HashMap roles = (HashMap) seguridad.get("roles");
				String uoSeg = (String) seguridad.get("uoSeg");
				String codPers = (String) seguridad.get("codPers");

				if (roles.get(Constantes.ROL_JEFE) != null) {
					strSQL.append(" and p.t02cod_pers != '" ).append( codPers ).append( "' ");
					strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) like '"
							).append( uoSeg ).append( "' ");
				} else {
					strSQL.append(" and 1=2 ");
				}
			}

			strSQL.append(" and substr(trim(nvl(p.t02cod_catel,'')||nvl(p.t02cod_cate,'')),1,2) = c.t99codigo and "
					).append( "	ha.cod_pers = p.t02cod_pers ");

			con = getConnection(getDataSource((String)dbpool));
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, Constantes.ACTIVO);
			//pre.setInt(2,Constantes.HORAS_JORNADA);
			rs = pre.executeQuery();
			acumulados = new ArrayList();
			BeanHoraExtra ha = null;

			while (rs.next()) {

				ha = new BeanHoraExtra();

				ha.setCodPers(rs.getString("cod_pers"));
				ha.setCodCate(rs.getString("cod_cate"));
				ha.setDesCate(rs.getString("t99descrip").trim());
				ha.setTrabajador(rs.getString("t02ap_pate").trim().concat( " "
						).concat( rs.getString("t02ap_mate").trim() ).concat( ", "
						).concat( rs.getString("t02nombres").trim()));
				ha.setAcumulado(rs.getFloat("t_acum"));
				String desc = Utiles.dameFormatoHHMM(ha.getAcumulado());
				ha.setDescAcumulado(desc);

				acumulados.add(ha);

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
		return acumulados;
	}

	/**
	 * Metodo encargado de obtener el saldo de las horas Acumuladas.
	 * @throws SQLException
	 */
	public float obtenerSaldo(String dbpool, String codPers) throws SQLException {

		StringBuffer strSQL = new StringBuffer("");
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		float acumulados = 0;

		try {

			strSQL.append("select ha.t_acum "
					+ "from 	t132horacu ha  "
					+ "where	ha.cod_pers  = ? ");

			con = getConnection(getDataSource((String)dbpool));
			pre = con.prepareStatement(strSQL.toString());
			pre.setString(1, codPers);
			rs = pre.executeQuery();

			while (rs.next()) {

				acumulados = rs.getFloat("t_acum");

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
		return acumulados;
	}
	
	
	/**
	 * Metodo encargado de obtener el saldo de las horas Acumuladas.
	 * @throws SQLException
	 */
	public float obtenerSaldo( String codPers) throws DAOException {
		//log.debug("llegue obtener saldo");
		Map mapa =  new HashMap();
		mapa = executeQueryUniqueResult(datasource, OBTENER_SALDO,new Object[]{codPers});
		//log.debug("llegue mapa ");
		float acumulados = 0;
		//log.debug("mapa "+mapa);
		if (mapa!=null && mapa.size()>0) {
			//log.debug("nombre clase "+mapa.get("t_acum").getClass().getName());
			 //acumulados = ((Double) mapa.get("t_acum")).floatValue();
			 acumulados =  Float.parseFloat(mapa.get("t_acum").toString());
		}
		//log.debug("acumulados "+ acumulados);
		return acumulados;
	}
	
	/**
	 * Metodo que se encarga de registrar la licencia
	 * de un trabajador
	 * @throws DAOException
	 */
	public boolean modificarAcum(Map datos)
	throws DAOException {

		
		FechaBean fecha1 = new FechaBean();
		int modifica = 0;


			

			modifica = executeUpdate(datasource, MODIFICAR_ACUM, new Object[]{(String)datos.get("saldo"), fecha1.getTimestamp(),(String) datos.get("usuario"),(String) datos.get("trabajador")});

			boolean result = (modifica> 0);

		return result;
	}

}