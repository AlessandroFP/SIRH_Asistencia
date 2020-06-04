package pe.gob.sunat.rrhh.dao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;

/**
 * 
 * Clase : T47DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T47PERID de personal.
 */
public class T47DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	/**
	 * Clase estandar de mapeo de data adicional.
	 */
	public BeanMapper beanMapper = new BeanMapper(){
		public Object setear(ResultSet r, Map mapa)throws DAOException{
			
			try{
				Set campos = mapa.keySet();
				
				if (campos.contains("t47habla")){
					if (mapa.get("t47habla")!=null){
						mapa.put("t47habla_desc", getDescNivel((String) mapa.get("t47habla")));
					}
				}
				
				if (campos.contains("t47lee")){
					if (mapa.get("t47lee")!=null){
						mapa.put("t47lee_desc", getDescNivel((String) mapa.get("t47lee")));
					}
				}
				
				if (campos.contains("t47escribe")){
					if (mapa.get("t47escribe")!=null){
						mapa.put("t47escribe_desc", getDescNivel((String) mapa.get("t47escribe")));
					}
				}
				
				return mapa;
			} catch (Exception e) {
				log.error("*** SQL Error ****", e);
				throw new DAOException(this, e.getMessage());
			}
		}
		
		/**
		 * Metodo encargado de devolver la descripcion de un nivel de idiomas
		 * @param tipo
		 * @return
		 */
		private String getDescNivel(String tipo){
			try{
			    if (tipo.equals("-"))
			      return "-";
			    else if (tipo.equalsIgnoreCase("i"))
			      return "INTERMEDIO";
			    else if (tipo.equalsIgnoreCase("b"))
			      return "BASICO";
			    else if (tipo.equalsIgnoreCase("a"))
			      return "AVANZADO";
			    else
			      return "-";
			}catch(Exception e){
				return "-";
			}
		}
	};
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select i.t47cod_idio, i.t47habla, i.t47lee, i.t47escribe, ")
																 .append("(select t.t99descrip from t99codigos t ")
																 .append("where t.t99cod_tab = '013' and t.t99tip_desc = 'D' and ")
																 .append("i.t47cod_idio = t.t99codigo) as t47cod_idio_desc ")
																 .append("from t47perid i where t47cod_pers = ?");
	
	/**
	 * @param datasource Object
	 */
	public T47DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de los registro de la tabla T47PERID
	 * filtrados por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers}, beanMapper);
	}
}
