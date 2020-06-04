package pe.gob.sunat.rrhh.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;
import pe.gob.sunat.framework.util.date.FechaBean;

/**
 * 
 * Clase : T09DAO 
 * Autor : RMONTES
 * Fecha : 07/11/2007
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * T09FAMIL de personal.
 */
public class T09DAO extends DAOAbstract {
	private DataSource dataSource = null;
	
	private final StringBuffer insertByRtps = new StringBuffer("insert into t09famil (t09cod_pers, t09correl, t09en_sunat ,t09cod_persf, t09cod_fami, t09ap_pfami, t09ap_mfami, t09nomb_fa, t09f_nacfa ");
	private final StringBuffer updateByRtps = new StringBuffer("update t09famil set t09en_sunat = ? ,t09cod_persf = ?, t09f_nacfa = ? ");
	private final StringBuffer findCorrelForRtps = new StringBuffer("select nvl(max(t09correl),0)+1 as sig_correl from t09famil where t09cod_pers = ? and t09cod_fami=? ");
	private final StringBuffer findByRtps = new StringBuffer("select t09cod_pers, t09cod_fami, t09correl from t09famil where t09cod_pers = ? and t09cod_fami = ? and ")
												.append(" t09ap_pfami = ? and t09ap_mfami = ? and t09nomb_fa = ?");
	
	/**
	 * Clase estandar de mapeo de data adicional.
	 */
	public BeanMapper beanMapper = new BeanMapper(){
		public Object setear(ResultSet r, Map mapa)throws DAOException{
			
			try{
				Set campos = mapa.keySet();
				
				if (campos.contains("t09f_nacfa")){
					if (mapa.get("t09f_nacfa")!=null){
						mapa.put("t09edad", "" + FechaBean.getDiferencia(new FechaBean().getCalendar(), (new FechaBean((Date) mapa.get("t09f_nacfa"))).getCalendar(), Calendar.YEAR));
	                } else{
	                	mapa.put("t09edad", "--");
	                }
				}
				
				return mapa;
			} catch (Exception e) {
				log.error("*** SQL Error ****", e);
				throw new DAOException(this, e.getMessage());
			}
		}		
	};
	
	private static final StringBuffer FIND_BY_CODPERS = new StringBuffer("select  f.t09cod_pers, f.t09cod_fami, f.t09ap_pfami, ")
																 .append("f.t09ap_mfami, f.t09nomb_fa, f.t09f_nacfa, ")
																 .append("(select t.t01des_larga from t01param t ")
																 .append("where t.t01_numero = '709' and t.t01_tipo = 'D' and ") 
																 .append("f.t09cod_fami = t.t01_argumento) as t09cod_fami_desc ")
																 .append("from    t09famil f ")
																 .append("where   t09cod_pers = ? and ") 
																 .append("t09cod_fami != '14'");
																 
	/**
	 * @param datasource Object
	 */
	public T09DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * Metodo que devuelve los datos de un registro especifico de la tabla T09FAMIL
	 * filtrado por el numero de registro del trabajador.
	 * 
	 * @param codpers
	 * @return
	 */
	public List findByCodPers(String codpers){
		return executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{codpers}, beanMapper);
	}
	
	/**
	 * Metodo que se encarga de obtener correlativo de la tabla 
	 *  
	 * @param hm		Map. datos de del personal()
	 * @return          void
	 * @throws DAOException
	 * */
	public Map findCorrelForRtps(Map hm) throws DAOException {
		Map res = new HashMap();
		
		res = executeQueryUniqueResult(dataSource, findCorrelForRtps.toString(), new Object[]{hm.get("cod_personal"), "0"+hm.get("ind_vinculo")});
		
		return res;
	}
	
	
	/**
	 * Metodo que se encarga de insertar datos del personal 
	 *  
	 * @param hm		Map. datos de del personal()
	 * @return          void
	 * @throws DAOException
	 * */
	public void insertByRtps(Map hm) throws DAOException {
		
		String ind_vinculo = (hm.get("ind_vinculo") != null ) ? (String)hm.get("ind_vinculo"):"";
		String cod_personal1 = (hm.get("cod_personal1") != null ) ? hm.get("cod_personal1").toString():" ";
		
		String en_sunat = ( cod_personal1.trim().length()>0 ) ? "S":"N";
		
		FechaBean fecha_nac= new FechaBean((String)hm.get("fec_nacimiento"));
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
		
		if(ind_vinculo.equalsIgnoreCase("01")) {
			insertByRtps.append(", t09f_matri, t09f_graba, t09cod_user ) values (?,?,?,?,?,?,?,?,?,?,?,?)");
			FechaBean fecha_matri= new FechaBean((String)hm.get("fec_matrimonio"));
			
			executeUpdate(dataSource, insertByRtps.toString(), new Object[]{hm.get("cod_personal"), hm.get("t09correl"), en_sunat, cod_personal1, ind_vinculo, hm.get("ape_pat_derecho"), 
				hm.get("ape_mat_derecho"), hm.get("nom_derecho"), fecha_nac.getTimestamp(), fecha_matri.getTimestamp(), fecha_reg.getTimestamp(), hm.get("nomb_usuario")} );	
			
		} else {
			insertByRtps.append(" , t09f_graba, t09cod_user ) values (?,?,?,?,?,?,?,?,?,?,?)");
			
			executeUpdate(dataSource, insertByRtps.toString(), new Object[]{hm.get("cod_personal"), hm.get("t09correl"), en_sunat, cod_personal1, ind_vinculo, hm.get("ape_pat_derecho"), 
				hm.get("ape_mat_derecho"), hm.get("nom_derecho"), fecha_nac.getTimestamp(), fecha_reg.getTimestamp(), hm.get("nomb_usuario")} );	
			
		}
			
	}
	
	/**
	 * Metodo que se encarga de actualizar datos del personal 
	 *  
	 * @param hm		Map 
	 * @param reg		Map , datos de registro a actualizar
	 * @return          void
	 * @throws DAOException
	 * */
	public void updateByRtps(Map hm, Map reg) throws DAOException {
		
		String ind_vinculo = (hm.get("ind_vinculo") != null ) ? (String)hm.get("ind_vinculo"):"";
		String cod_personal1 = (hm.get("cod_personal1") != null ) ? hm.get("cod_personal1").toString():" ";
		
		String en_sunat = ( cod_personal1.trim().length()>0 ) ? "S":"N";
		
		FechaBean fecha_nac= new FechaBean((String)hm.get("fec_nacimiento"));
		FechaBean fecha_reg= (FechaBean)hm.get("fecha_reg");
		
		if(ind_vinculo.equalsIgnoreCase("01")) {
			updateByRtps.append(", t09f_matri = ?, t09f_graba = ?, t09cod_user = ? where t09cod_pers = ? and t09cod_fami = ? and t09correl = ?");
			FechaBean fecha_matri= new FechaBean((String)hm.get("fec_matrimonio"));
			
			executeUpdate(dataSource, updateByRtps.toString(), new Object[]{en_sunat, cod_personal1, fecha_nac.getTimestamp(), 
				fecha_matri.getTimestamp(),fecha_reg.getTimestamp(), hm.get("nomb_usuario"), reg.get("t09cod_pers"), reg.get("t09cod_fami"), reg.get("t09correl")} );	
			
		} else {
			updateByRtps.append(" , t09f_graba = ?, t09cod_user = ? where t09cod_pers = ? and t09cod_fami = ? and t09correl = ?");
			
			executeUpdate(dataSource, updateByRtps.toString(), new Object[]{en_sunat, cod_personal1, fecha_nac.getTimestamp(), 
				fecha_reg.getTimestamp(), hm.get("nomb_usuario"), reg.get("t09cod_pers"), reg.get("t09cod_fami"), reg.get("t09correl")} );	
			
		}
			
	}
	
	/**
	 * Metodo de Busqueda de registro si ya existe.
	 *  
	 * @param hm Map
	 * @return void
	 * @throws DAOException
	 * */
	public Map findByRtps(Map hm) throws DAOException {
		Map res = new HashMap();		
		res = executeQueryUniqueResult(dataSource, findByRtps.toString(), new Object[]{hm.get("cod_personal"), hm.get("ind_vinculo"), hm.get("ape_pat_derecho"), hm.get("ape_mat_derecho"), hm.get("nom_derecho")});		
		return res;
	}
}
