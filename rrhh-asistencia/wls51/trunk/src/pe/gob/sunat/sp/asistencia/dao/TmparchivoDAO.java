//AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
package pe.gob.sunat.sp.asistencia.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.utils.Constantes;

/** 
 * Title : T8151DAO 
 * Description : Clase para el manejo de la tabla temporal (detalle  de archivo) tmparchivo 
 * estos datos se insertaran permanentemente en t8151archivodet 
 * Company: Sunat
 * 
 * @author agonzalesf
 * @version 1.0
 */

public class TmparchivoDAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(TmparchivoDAO.class);
	private static final int PARAM_IN_SIZE = 15;

	private DataSource datasource;
	private static final String INSERT_SENTENCE = "INSERT INTO tmparchivo(num_arcdet,num_archivo,num_seqdoc,arc_adjunto,fec_carga,cod_tiparc,des_archivo,nom_archivo,ind_del) "
			+ "VALUES (?,?,?,?,sysdate,?,?,?,?)";

	private static final String UPDATE_STATE_SENTENCE = "UPDATE tmparchivo SET ind_del = ? WHERE  num_archivo =?";

	private static final String SELECT_FILE_SENTENCE = "SELECT nom_archivo,arc_adjunto FROM tmparchivo WHERE num_archivo =? AND num_arcdet =?";
		
	private static final String SELECT_FILE_SENTENCE_1 = "SELECT num_archivo,num_arcdet,num_seqdoc,nom_archivo ,cod_tiparc,des_archivo FROM tmparchivo WHERE num_archivo =?  ";
	
	private static final String SELECT_FILE_SENTENCE_2 = "SELECT num_archivo,num_arcdet,num_seqdoc,arc_adjunto,fec_carga,cod_tiparc,nom_archivo,des_archivo ,ind_del FROM tmparchivo WHERE num_archivo =?  ";
	
	private static final String DELETE_SENTENCE = "DELETE FROM tmparchivo WHERE num_archivo =? ";

	private static final String SELECT_MAX_DETAIL_BY_FILE = "SELECT NVL(MAX(num_arcdet),0) as maximo FROM tmparchivo WHERE num_archivo =?";



	public TmparchivoDAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}


	/**
	 * Funcion para encontrar archivo por num_archivo y num_arcdet
	 * @param numArchivo
	 * @param numArcdet
	 * @return Mapa con nombre de archivo y archivo
	 * @throws SQLException
	 */
	public Map findArchivoById(Integer numArchivo, Integer numArcdet) throws SQLException {
		Map map = new HashMap();
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		InputStream archivo = null;
		String nombre = null;
		try {
			log.debug("Ingreso findArchivoById");
			con = datasource.getConnection();
			pre = con.prepareStatement(SELECT_FILE_SENTENCE.toString());
			pre.setInt(1, numArchivo.intValue());
			pre.setInt(2, numArcdet.intValue());
			log.debug("antes rs");
			rs = pre.executeQuery();
			log.debug("Ingreso rs");
			log.debug(rs);
			if (rs.next()) {
				Blob b = rs.getBlob("arc_adjunto");
				nombre = rs.getString("nom_archivo");
				archivo = (b != null ? b.getBinaryStream() : null);
			}
			map.put("arc_adjunto", archivo);
			map.put("nom_archivo", nombre);

		} catch (Exception e) {
			log.error("No se puede obtener archivo de base ", e);
			throw new SQLException(e.toString());
		} finally {
			log.debug("Cierre de conexion");
			List exceptions = new ArrayList();
			try {
				con.close();
			} catch (SQLException ex) {
				exceptions.add(ex);
			}
			try {
				pre.close();
			} catch (SQLException ex) {
				exceptions.add(ex);
			}
			try {
				rs.close();
			} catch (SQLException ex) {
				exceptions.add(ex);
			}
			if (exceptions.size() != 0) {
				throw new DAOException();
			}
		}
		return map;
	}

	/**
	 * 
	 * @param numArchivo
	 * @param numSeqDoc
	 * @return
	 * @throws SQLException
	 */
	public List findArchivos(Map mapa) throws SQLException {

		log.debug("Insertar en detalle " + mapa);
		String numArchivo = mapa.get("num_archivo").toString();
		StringBuffer finalSql = new StringBuffer().append(SELECT_FILE_SENTENCE_1.toString());

		if (mapa.containsKey("num_arcdet")) {
			finalSql.append(" AND  num_arcdet = " + mapa.get("num_arcdet"));
		}
		if (mapa.containsKey("num_seqdoc")) {
			finalSql.append(" AND  num_seqdoc=" + mapa.get("num_seqdoc"));
		}
		if (mapa.containsKey("ind_dels")) {
			List ind_dels = (List) mapa.get("ind_dels");
			if (ind_dels.size() > 0) {
				finalSql.append(" AND  ind_del in('S'");
				for (int i = 0; i < ind_dels.size(); i++) {
					finalSql.append(",'" + ind_dels.get(i) + "'");
				}
				finalSql.append(")");
			}
		}
		log.debug("finalSql --->" + finalSql.toString());
		List lista = null;
		lista = executeQuery(datasource, finalSql.toString(), new Object[] { numArchivo });
		return lista;
	}

	/**
	 * Funcion que devuelve el siguiente numero de detalle
	 * @param numArchivo
	 * @return Siguiente numero 
	 * @throws SQLException
	 */
	public int findNext(Integer numArchivo) throws SQLException {
		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		int b = 0;

		try {
			log.debug("Ingreso findNext ->" + numArchivo);
			con = datasource.getConnection();
			pre = con.prepareStatement(SELECT_MAX_DETAIL_BY_FILE.toString());
			pre.setInt(1, numArchivo.intValue());
			rs = pre.executeQuery();			
			if (rs.next()) {
				b = rs.getInt("maximo"); 
			}
		} catch (Exception e) {
			log.error("No se puede obtener secuenciador ", e);
			throw new SQLException(e.toString());
		} finally {
			log.debug("Cierre de conexion");
			List exceptions = new ArrayList();
			try {
				con.close();
			} catch (SQLException ex) {
				log.debug("Cierre de CON");
				exceptions.add(ex);
			}
			try {
				pre.close();
				
			} catch (SQLException ex) {
				log.debug("Cierre de PRE");
				exceptions.add(ex);
			}
			try {
				rs.close();
			} catch (SQLException ex) {
				log.debug("Cierre de RS");
				exceptions.add(ex);
			}
			if (exceptions.size() != 0) {
				log.debug("Cierre de conexion");
				throw new DAOException();
			}
		}
		return b + 1;
	}

	/**
	 * Funcion que inserta nuevo archivo temporal 
	 * @param datos
	 * @return mayor a cero si exito 
	 */
	public int insertArchivo(Map datos) {
		int control = -1;
		Connection conexion = null;
		PreparedStatement statement = null;
		try {
		
			if (log.isDebugEnabled())  log.debug("Item -----> " + datos);
			conexion = datasource.getConnection(); 
			statement = conexion.prepareStatement(INSERT_SENTENCE.toString());
			statement.setInt(1, Integer.parseInt( datos.get("num_arcdet").toString()));
			statement.setInt(2, Integer.parseInt( datos.get("num_archivo").toString()));
			statement.setInt(3, Integer.parseInt( datos.get("num_seqdoc").toString()));
			statement.setBinaryStream(4, (InputStream) datos.get("arc_adjunto"), ((InputStream) datos.get("arc_adjunto")).available());
			statement.setString(5, (String) datos.get("cod_tiparc"));
			statement.setString(6, (String) datos.get("des_archivo"));
			statement.setString(7, (String) datos.get("nom_archivo"));
			statement.setInt(8, Integer.parseInt(datos.get("ind_del").toString()));
			control = statement.executeUpdate();

		} catch (Exception e) {
			log.error("Error al insertar detalle ", e);
		} finally {
			log.debug("Cierre de conexion");
			List exceptions = new ArrayList();
			try {
				conexion.close();
			} catch (SQLException ex) {
				exceptions.add(ex);
			}
			try {
				statement.close();
			} catch (SQLException ex) {
				exceptions.add(ex);
			}
			if (exceptions.size() != 0) {
				throw new DAOException();
			}
		}
		return control;
	}

	/**
	 * Eliminar logicamente los archivos (setear ind_del=EST_ARC_TEMP_PARA_BORRAR)
	 * @param numArchivo
	 * @param numArcDet
	 * @return
	 */
	public int updateState(Map mapa,String estado) {
		
		log.debug("Insertar en detalle " + mapa +"--->" + estado);
		String numArchivo =mapa.get("num_archivo").toString();  
		StringBuffer finalSql = new StringBuffer().append(UPDATE_STATE_SENTENCE.toString());
		
		if(mapa.containsKey("num_arcdet")){
			finalSql.append(" AND  num_arcdet = "+mapa.get("num_arcdet") );
		}
		if(mapa.containsKey("num_seqdoc")){
			finalSql.append(" AND  num_seqdoc="+mapa.get("num_seqdoc") );
		}
		if(mapa.containsKey("ind_del")){
			finalSql.append(" AND  ind_del="+mapa.get("ind_del") );
		}
		log.debug("finalSql --->" + finalSql.toString()); 
		
		executeUpdate(
				datasource,
				finalSql.toString(),
				new Object[] { estado,numArchivo });
			 
		return 1;
		
	}

	/**
	 * Borra archivos de la tabla temporal
	 * @param mapa
	 * @return
	 */
	public int deleteArchivos(Map mapa) throws SQLException {
		log.debug("deleteArchivos " + mapa +"--->" +   mapa);
		String numArchivo =mapa.get("num_archivo").toString();  
		StringBuffer finalSql = new StringBuffer().append(DELETE_SENTENCE.toString());
		
		if(mapa.containsKey("num_arcdet")){
			finalSql.append(" AND  num_arcdet = "+mapa.get("num_arcdet") );
		}
		if(mapa.containsKey("num_seqdoc")){
			finalSql.append(" AND  num_seqdoc="+mapa.get("num_seqdoc") );
		}
		 
		log.debug("finalSql --->" + finalSql.toString()); 
		
		executeUpdate(
				datasource,
				finalSql.toString(),
				new Object[] { numArchivo });
			 
		return 1;
		 
	}

	/**
	 * Funcion para obtener el numero de archivos validos por numero de archivo
	 * @param numArchivo
	 * @return
	 */
	public List findArchivosByNumArchivo(Map mapa) {
		  if (log.isDebugEnabled()) log.debug(this.toString().concat(" INICIO - findArchivosByNumArchivo()"));
		  List lista  = null;    
	      StringBuffer strSQL = null;
	      String numArchivo =mapa.get("num_archivo").toString(); 
	      strSQL = new StringBuffer(SELECT_FILE_SENTENCE_2.toString());
	      if(mapa.containsKey("ind_del")){
	    	  strSQL.append(" AND  ind_del="+mapa.get("ind_del") );
			}
	        
	      lista = executeQuery(datasource, strSQL.toString(),
	      new Object[]{numArchivo});
	      if (log.isDebugEnabled()) log.debug(this.toString().concat(" FIN - findArchivosByNumArchivo() "));           
	      return lista ;
	}

}
