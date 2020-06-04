package pe.gob.sunat.administracion.ctrlpatrim.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;
import pe.gob.sunat.rrhh.padron.dao.CentralesDAO;

public class T75DAO extends DAOAbstract{
	
	private DataSource dataSource = null;	
	private DataSource dataSource_telef = null;
	
	private static final String findByCodUorgan = "SELECT t75cod_uorgan, t75cod_local,"
		.concat("nvl(t75n_piso,' ') as t75n_piso, nvl(t75n_anexo,' ') as t75n_anexo FROM t75lunor where t75cod_uorgan = ? order by t75cod_local");
	
	/**
	 * @param datasource Object
	*/
	public T75DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else if (datasource instanceof Map){
	    	dataSource = (DataSource)((Map)datasource).get("dscsa");
			dataSource_telef = (DataSource)((Map)datasource).get("dsctelef");
	    }else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * 
	 * @param datasources HasMap de datasource en este caso dscsa (sa) y dscsp de (sp)
	 */
	
	/*public T75DAO(Map datasources) {		
		dataSource = (DataSource)datasources.get("dscsa");
		dataSource_telef = (DataSource)datasources.get("dsctelef");
	}*/
	
	public BeanMapper beanMapper = new BeanMapper(){
		public Object setear(ResultSet r, Map mapa)throws SQLException{
			MensajeBean msg = new MensajeBean();
			try{
				CentralesDAO dao = new CentralesDAO(dataSource_telef);
				T34DAO t34dao = new T34DAO(dataSource);
				
				Set campos = mapa.keySet();
				
				String codigo_local = mapa.get("t75cod_local") != null ? mapa.get("t75cod_local").toString().trim():"";
				
				if(campos.contains("t75cod_local")){
					if(!codigo_local.equals("")){
						List a = null;
						Map b = null;					
						
						a = dao.findByCodLocal(codigo_local);
						
						b = t34dao.findByCodLocalHash(codigo_local);						
						
						mapa.put("central_telef", a.size()==0?" ":"0"+((String)((HashMap)a.get(0)).get("ddn")).trim()+"-"+((String)((HashMap)a.get(0)).get("numero")).trim() );						
						mapa.put("cod_localdesc", b.size()==0?" ":((String)b.get("t34cod_local")).trim()+"-"+((String)b.get("t34nom_local")).trim());						
					} else {
						mapa.put("central_telef", "");
						mapa.put("cod_localdesc", "");
					}				
				}
				
			}catch (Exception e) {
				log.error("*** ERROR *** : ", e);
		    	msg.setMensajeerror("Ha ocurrido un error al buscar locales por "
		    			.concat(" codigo organizacional : ").concat(e.getMessage()));
		    	msg.setMensajesol("Por favor, intente nuevamente realizar la "
		    			.concat("operacion, de continuar con el problema ")
		    			.concat("comuniquese con el webmaster"));
		    	throw new DAOException (this, msg);
			}
			return mapa;
		}		
	};
	
	
	
	/**
	 * Método findByCodUorgan que obtiene el listado de locales que le pertenece a una unidad organizacional
	 * 
	 * @param String valor
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public List findByCodUorgan(String codUorg) throws DAOException	{
		codUorg = codUorg.trim().replaceAll("'", "'");
		List aLRpta = executeQuery(dataSource, findByCodUorgan , new Object[]{codUorg},beanMapper);
						
		return aLRpta;
	}
}
