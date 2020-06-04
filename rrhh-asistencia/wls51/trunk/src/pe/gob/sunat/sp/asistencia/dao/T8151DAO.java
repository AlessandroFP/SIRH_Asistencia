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

/** 
 * Title : T8151DAO 
 * Description : Clase para el manejo de la tabla t8151archivo (detalle  de archivo) 
 * Company: Sunat
 * 
 * @author agonzalesf
 * @version 1.0
 */

public class T8151DAO extends DAOAbstract {
	private static final Logger log = Logger.getLogger(T8151DAO.class);
	private static final int PARAM_IN_SIZE = 15;

	private DataSource datasource;
	private static final String SELECT_FILE_SENTENCE = "SELECT nom_archivo,arc_adjunto FROM t8151archivodet WHERE num_archivo =? AND num_arcdet =?";
	private static final String SELECT_FILE_SENTENCE_1 = "SELECT num_archivo,num_arcdet,num_seqdoc,nom_archivo ,cod_tiparc,des_archivo FROM t8151archivodet WHERE num_archivo =? AND num_seqdoc =? and ind_del=0";

 	 
	private static final String INSERT_SENTENCE = "INSERT INTO t8151archivodet(num_archivo,num_arcdet,num_seqdoc,arc_adjunto,fec_carga,"
			+ "cod_tiparc,nom_archivo,des_archivo,cod_usucrea,fec_creacion,"
			+ "ind_del) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,sysdate,0)";
	
	public T8151DAO() {
	}

	public T8151DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource) datasource;
		else if (datasource instanceof String)
			this.datasource = getDataSource((String) datasource);
		else
			throw new DAOException(this, "Datasource no valido");
	}

 
	/**
	 * Método que se encarga de buscar  los archivos filtrados por  num_archivo y num_arcdet 
	 * @param numArchivo
	 * @param numArcdet
	 * @return Mapa con nombre de archivo y  Stream con datos del archivo
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
			try {
				rs.close();
			} catch (Exception ex) {
				 
			}
			try {
				pre.close();
			} catch (Exception ex) {
			 
			}
			
			try {
				con.close();
			} catch (Exception ex) {
				 
			}
			 
		}
		return map;
	}

	/**
	 * Método que se encarga de buscar  los archivos filtrados por  num_archivo y num_seqdoc
	 * @param numArchivo
	 * @param numSeqDoc
	 * @return Lista con datos de archivos encontrados
	 * @throws SQLException
	 */
	public List findArchivosByNumArchivoNumSeqDoc(Integer numArchivo, Integer numSeqDoc) throws SQLException {

		PreparedStatement pre = null;
		Connection con = null;
		ResultSet rs = null;
		InputStream archivo = null;
		List archivos = new ArrayList();
		try {
			log.debug("Ingreso findArchivosByNumArchivoNumSeqDoc");
			con = datasource.getConnection();
			pre = con.prepareStatement(SELECT_FILE_SENTENCE_1.toString());
			pre.setInt(1, numArchivo.intValue());
			pre.setInt(2, numSeqDoc.intValue()); 
			rs = pre.executeQuery();  
			while (rs.next()) {
				Map map = new HashMap();
				//num_archivo,num_arcdet,num_seqdoc,nom_archivo,cod_tiparc,des_archivo  
				map.put("num_archivo",new Integer(rs.getInt(1)));
				map.put("num_arcdet", new Integer(rs.getInt(2)));
				map.put("num_seqdoc", new Integer(rs.getInt(3)));
				map.put("nom_archivo", rs.getString(4));
				map.put("cod_tiparc", rs.getString(5));
				map.put("des_archivo", rs.getString(6));
				archivos.add(map);
			}
		} catch (Exception e) {
			log.error("No se puede obtener archivo de base ", e);
			throw new SQLException(e.toString());
		} finally {
			log.debug("Cierre de conexion");
		 
			try {
				rs.close();
			} catch (Exception ex) {
			 
			}
			try {
				pre.close();
			} catch (Exception ex) {
				 
			}
			
			try {
				con.close();
			} catch (Exception ex) {
				 
			}
			 
		}
		return archivos;
	}

	 
	/**
	 * Metodo que se encarga de insertar archivos 
	 * @param mapa : Mapa con  numero de archivo 
	 * @return 1
	 */
	public int insert(Map mapa) throws SQLException {
		log.debug("insert ->"+mapa);
		int control = -1;
		Connection conexion = null;
		PreparedStatement statement = null;
		try {
		
			if (log.isDebugEnabled())  log.debug("Item -----> " + mapa);
			conexion = datasource.getConnection(); 
			statement = conexion.prepareStatement(INSERT_SENTENCE.toString());
			statement.setInt(1, ((Integer) mapa.get("num_archivo")).intValue());
			statement.setInt(2, ((Integer) mapa.get("num_arcdet")).intValue()); 
			statement.setInt(3, ((Integer) mapa.get("num_seqdoc")).intValue());
			File archivo =  (File) mapa.get("arc_adjunto");
			InputStream targetStream = new FileInputStream(archivo);
			statement.setBinaryStream(4, targetStream, targetStream.available());
			statement.setTimestamp(5, (java.sql.Timestamp) mapa.get("fec_carga"));
			statement.setString(6, (String) mapa.get("cod_tiparc"));			
			statement.setString(7, (String) mapa.get("nom_archivo"));			
			statement.setString(8, (String) mapa.get("des_archivo"));
			statement.setString(9, (String) mapa.get("cod_usucrea"));		
			control = statement.executeUpdate();

		} catch (Exception e) {
			log.error("Error al insertar detalle ", e);
		} finally {
			log.debug("Cierre de conexion");
			try {
				statement.close();
			} catch (Exception ex) {
				 
			}
			try {
				conexion.close();
			} catch (Exception ex) {
				 
			}
			
			 
		}
		return control; 
	}

 

}
