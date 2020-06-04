package pe.gob.sunat.rrhh.dao;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;
import pe.gob.sunat.framework.util.date.FechaBean;

/**
 * 
 * Clase : T03DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T03PERDS de personal.
 */
public class T03DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private final StringBuffer insertByRtps = new StringBuffer("insert into t03perds (t03cod_pers, t03grp_sang, t03sexo, t03cod_ubin, t03est_civ, t03f_graba, t03cod_user) values (?,?,?,?,?,?,?)");
	private final StringBuffer updateByRtps = new StringBuffer("update t03perds set t03grp_sang = ?, t03sexo = ?, t03cod_ubin = ?, t03est_civ = ?, t03f_graba = ?, t03cod_user = ? where t03cod_pers = ?");
	private final StringBuffer findByRegistro = new StringBuffer("select p.t03cod_pers, p.t03grp_sang, ")
																.append("(select g.t99descrip from t99codigos g ")
																.append("where g.t99cod_tab = '453' and g.t99tip_desc = 'D' and ")
																.append("g.t99codigo = p.t03grp_sang) as t03grp_sang_desc, ")
														 .append("p.t03sexo, p.t03cod_ubin, p.t03est_civ from t03perds p where p.t03cod_pers = ?");
	
	/**
	 * Clase estandar de mapeo de data adicional.
	 */
	public BeanMapper beanMapper = new BeanMapper(){
		public Object setear(ResultSet r, Map mapa)throws DAOException{
			
			try{
				Set campos = mapa.keySet();
				
				mapa.put("t03sexo_desc", "");
				if (campos.contains("t03sexo")){
					if (mapa.get("t03sexo")!=null){
						if (((String) mapa.get("t03sexo")).equals("M")){
							mapa.put("t03sexo_desc", "Masculino");
						} else if (((String) mapa.get("t03sexo")).equals("F")){
							mapa.put("t03sexo_desc", "Femenino");
						}
					}
				}
				
				mapa.put("t03tratamiento", "Señor");
				if (campos.contains("t03sexo") && campos.contains("t03est_civ")){
					if (mapa.get("t03sexo")!=null){
						if (((String) mapa.get("t03sexo")).equals("F")){
							if (mapa.get("t03est_civ")!=null){
								if(((String) mapa.get("t03est_civ")).equals("S")){
									mapa.put("t03tratamiento", "Señorita");
					            } else {
					            	mapa.put("t03tratamiento", "Señora");
					            }
							} else {
								mapa.put("t03tratamiento", "Señorita");
							}
						}
					}
				}
				
				return mapa;
			} catch (Exception e) {
				log.error("*** SQL Error ****", e);
				throw new DAOException(this, e.getMessage());
			}
		}		
	};
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select p.t03cod_pers, p.t03grp_sang, p.t03sexo, p.t03cod_ubin, ") 
																 .append("p.t03est_civ, p.t03pamf, p.t03f_graba, p.t03cod_user, ")
																 .append("(select g.t99descrip from t99codigos g ")
																 .append("where g.t99cod_tab = '453' and g.t99tip_desc = 'D' and ")
																 .append("g.t99codigo = p.t03grp_sang) as t03grp_sang_desc, ")
																 .append("(select u.t01des_larga from t01param u ")
																 .append("where u.t01_numero = '700' and u.t01_tipo = 'D' and ")
																 .append("p.t03cod_ubin = u.t01_argumento) as t03cod_ubin_desc, ")
																 .append("(select e.t01des_larga from t01param e ")
																 .append("where e.t01_numero = '702' and e.t01_tipo = 'D' and ")
																 .append("p.t03est_civ = e.t01_argumento) as t03est_civ_desc ")
																 .append("from t03perds p where p.t03cod_pers = ?");
	
	/**
	 * @param datasource Object
	 */
	public T03DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T03PERDS
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public Map findByCodPers(String codpers){
		return (HashMap) executeQueryUniqueResult(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers}, beanMapper);
	}
	
	/**
	 * Metodo que se encarga de buscar registro por codigo de personal
	 *  
	 * @param cod_pers	String. nro de registro del trabajador sunat
	 * @return          Map
	 * @throws DAOException
	 * */
	public Map findByRegistro(String cod_pers) throws DAOException {
		
		Map res = (HashMap)executeQueryUniqueResult(dataSource, findByRegistro.toString(), new Object[]{cod_pers} );
		
		return res;		
	}

	/**
	 * Metodo que se encarga de actualizar algunos datos del personal 
	 *  
	 * @param hm		Map. datos de del personal(cod_grupo_sang,ind_sexo_desc,cod_ubigeo_dom,ind_estado_civil, nomb_usuario, cod_personal ...)
	 * @return          void
	 * @throws DAOException
	 * */
	public void updateByRtps(Map hm) throws DAOException {
		
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
				
		executeUpdate(dataSource, updateByRtps.toString(), new Object[]{hm.get("cod_grupo_sang"), hm.get("ind_sexo_desc"), 
				 hm.get("cod_ubigeo_dom"), hm.get("ind_estado_civil"), fecha_reg.getTimestamp(), hm.get("nomb_usuario"), hm.get("cod_personal")} );
	}
	
	/**
	 * Metodo que se encarga de insertar datos del personal 
	 *  
	 * @param hm		HashMap. datos de del personal(cod_grupo_sang,ind_sexo_desc,cod_ubigeo_dom,ind_estado_civil, cod_personal ...)
	 * @return          void
	 * @throws DAOException
	 * */
	public void insertByRtps(Map hm) throws DAOException {
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
		executeUpdate(dataSource, insertByRtps.toString(), new Object[]{hm.get("cod_personal"), hm.get("cod_grupo_sang"), hm.get("ind_sexo_desc"), 
				 hm.get("cod_ubigeo_dom"), hm.get("ind_estado_civil"), fecha_reg.getTimestamp(), hm.get("nomb_usuario")} );	
	}
}

