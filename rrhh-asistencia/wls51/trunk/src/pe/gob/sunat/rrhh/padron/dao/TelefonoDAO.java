package pe.gob.sunat.rrhh.padron.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.bean.ParamBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;
import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.utils.Utilidades;

/** 
 * <p> Title: TelefonoDAO</p> 
 * <p> Copyright: Copyright (c) 2007 </p>
 * <p> Company: SUNAT </p>
 * 
 * @author PRAC-JCALLO
 * @version 1.0
 *
 **/

public class TelefonoDAO extends DAOAbstract{
	
	protected final Logger log = Logger.getLogger(getClass());
	private DataSource dataSource = null;
	private DataSource dataSource_Sp = null;
	
	private static final StringBuffer joinWithT02T12 = new StringBuffer("SELECT t02cod_pers, ")
	.append("t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, nvl(t02cod_uorgl,'') as t02cod_uorgl, t12des_uorga, t12des_corta, ")
	.append("t02cod_cate, nvl(t02cod_catel,'') as t02cod_catel, t02cod_stat, nvl(t12cod_jefat,'') as t12cod_jefat, nvl(t12cod_encar,'') as t12cod_encar ")
	.append("FROM (t02perdp LEFT JOIN t12uorga ON t02cod_uorg = t12cod_uorga) ");
	
	private static final String findByPers = "SELECT cod_pers, tip_linea, numero, cod_uorgan,"
		.concat(" cod_local, piso from telefonos where cod_pers = ? order by tip_linea ");
		
	public TelefonoDAO(String jndi) {		
		dataSource = getDataSource(jndi);
	}
	public TelefonoDAO(DataSource ds) {
		dataSource = ds;
	}
	
	/**
	 * constructor TelefonoDAO: para poder usar el bean mapper de la base de datos TELEF con SP
	 * @param datasources Map de datasource en este caso dscSp (sp) y dscTelef de (telef)
	 */
	public TelefonoDAO(Map datasources) {		
		dataSource_Sp = (DataSource)datasources.get("dscSp");
		dataSource = (DataSource)datasources.get("dscTelef");
	}
	
	/**
	 * beanMapper utilizado para agregar descripcion
	 * 
	 * */
	public BeanMapper beanMapper = new BeanMapper(){
		public Object setear(ResultSet r, Map mapa)throws SQLException{
			MensajeBean msg = new MensajeBean();
			try{				
				ArrayList b = null;
				ArrayList a = null;
				StringBuffer ListaTelefonos = new StringBuffer("");
				
				ParamDAO param = new ParamDAO();				
				//TelefonoDAO telefdao = new TelefonoDAO(dataSource_Telef);			
				
				Set campos = mapa.keySet();
				
				String cod_pers = mapa.get("t02cod_pers") != null ? mapa.get("t02cod_pers").toString().trim():"";
				String cod_catel = mapa.get("t02cod_catel") != null ? mapa.get("t02cod_catel").toString().trim():"";
								
				if (campos.contains("t02cod_catel")) {
					log.debug("antes de ejecutar cargarNOcache");
					if (cod_catel.length() > 1 && cod_catel != null && cod_catel !="" ) {
						//en query se selecciona los 3 campos debido a que el framework utiliza los tres campos
						a = (ArrayList)param.cargarNoCache("select t99codigo, t99abrev, t99siglas from t99codigos where t99cod_tab = '001' and t99codigo='"+cod_catel+"'", dataSource, param.LIST);
						log.debug("despues de ejecutar cargarNOcache");
						if(a.size()>0){
							ParamBean paramBean = (ParamBean) a.get(0);
							mapa.put("t02cod_catel_desc", paramBean == null ? " " : cod_catel.trim()
									+ "-" + paramBean.getDescripcion().trim());
						}else {
							mapa.put("t02cod_catel_desc", " ");
						}					
					}else{
						mapa.put("t02cod_catel_desc", " ");
					}					
				}
				
				if(campos.contains("t02cod_pers")){
					int tam = 0;
					log.debug(" telefono DAO findByPers");
					if (cod_pers != null && cod_pers != "" ) {
						b = findByPers(cod_pers);
						log.debug(" FIN telefono DAO findByPers");
						tam = b.size(); 
						if(tam == 0){
							ListaTelefonos.append("No Registra");
						}else{	
							ListaTelefonos.append("<select>");
							for( int i=0; i<tam;i++ ){
								HashMap aux = (HashMap)b.get(i); 
								ListaTelefonos.append("<option>");
								ListaTelefonos.append(aux.get("numero"));
								ListaTelefonos.append("</option>");
							}
							ListaTelefonos.append("</select>");
						}
					}
					mapa.put("t02telefonos_desc", b == null ? "No registra" : ListaTelefonos.toString().trim());
				}
				
			}catch (Exception e) {
				log.error("*** ERROR *** : ", e);
				msg.setMensajeerror("Ha ocurrido un error al buscar".concat(e.getMessage()));
				msg.setMensajesol("Por favor, intente nuevamente realizar la "
		    			.concat("operacion, de continuar con el problema ")
		    			.concat("comuniquese con el webmaster"));
		    	throw new DAOException (this, msg);
			}
			return mapa;
		}		
	}; 	
	
	/**
	 * Metodo para la busqueda de datos de personal,unido a descripcion de la categoria asignada 
	 * 
	 * @param Map  params
	 * 
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public ArrayList joinWithT02T12(Map params)
			throws DAOException {		
		MensajeBean msg = new MensajeBean();
		Utilidades common = new Utilidades();
		
		boolean flag = false;
		StringBuffer sWhere = new StringBuffer("");
		StringBuffer sSQL = new StringBuffer("");
		
		String nreg = "";
		String appat = "";
		String apmat = "";
		String nomb = "";
		String coduorg = "";
		String codcate = "";
		String estado = "";
		
		nreg = ( params.get("nreg") == null ) ?  "" : params.get("nreg").toString().trim(); 
		appat = ( params.get("appat") == null ) ?  "" : params.get("appat").toString();
		apmat = ( params.get("apmat") == null ) ?  "" : params.get("apmat").toString();
		nomb = ( params.get("nomb") == null ) ?  "" : params.get("nomb").toString();
		coduorg = ( params.get("coduorg") == null ) ?  "" : params.get("coduorg").toString().trim();
		codcate = ( params.get("codcate") == null ) ?  "" : params.get("codcate").toString().trim();
		estado = ( params.get("estado") == null ) ?  "" : params.get("estado").toString().trim();
				
		log.debug(" nreg = "+nreg);
		log.debug(" appat = "+appat);
		log.debug(" apmat = "+apmat);
		log.debug(" nomb = "+nomb);
		log.debug(" coduorg = "+coduorg);
		log.debug(" codcate = "+codcate);
		log.debug(" estado = "+estado);		
				
		//verificando parametro numero de registro
		if (nreg != null && nreg.length() > 0) {			
			flag = true;
			sWhere.append("t02cod_pers = '" + common.replace(nreg, "'", "''") + "'");
		}
		
		//verificando parametro apellido paterno
		if (appat != null && appat.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}					
			sWhere.append("t02ap_pate like '" + common.replace(appat, "'", "''") + "%'");
		}
		
		//verificando parametro apellido materno
		if (apmat != null && apmat.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02ap_mate like '" + common.replace(apmat, "'", "''") + "%'");
		}

		//verificando parametro nombres		
		if (nomb != null && nomb.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02nombres like '%" + common.replace(nomb, "'", "''") + "%'");
		}
		
		//verificando parametro apellido codigo de unidad organizacional
		if (coduorg != null && coduorg.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_uorg = '" + common.replace(coduorg, "'", "''") + "'");
		}
		
		//verificando parametro apellido codigo de categoria
		if (codcate != null && codcate.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_cate = '" + common.replace(codcate, "'", "''") + "'");
		}

		//verificando parametro estado(1)
		if (estado != null && estado.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_stat = '" + common.replace(estado, "'", "''") + "'");
		}
		
		if (flag) {
			sWhere = new StringBuffer(" WHERE (" + sWhere.toString()+")");
		}
		
		sSQL.append(joinWithT02T12).append(sWhere.toString()).append(" order by t02ap_pate, t02ap_mate ");
		
		ArrayList listaRpta = new ArrayList();		

		try {		
			log.debug("antes de executwe Query en el TelefonoDAO");
			listaRpta = (ArrayList)executeQuery(dataSource_Sp, sSQL.toString(),beanMapper);		
			log.debug("total = "+listaRpta.size());
		} catch (DAOException e) {
			log.error("*** ERROR *** : ", e);
			msg.setMensajeerror("Ha ocurrido un error al buscar".concat(e.getMessage()));
			msg.setMensajesol("Por favor, intente nuevamente realizar la "
	    			.concat("operacion, de continuar con el problema ")
	    			.concat("comuniquese con el webmaster"));
	    	throw new DAOException (this, msg);
		} finally {
			
		}
		return listaRpta;
	}
	
	/**
	 * metodo findByPers : permite buscar telefonos asociados a un personal
	 * 
	 * @param codPers String
	 * @return {@link ArrayList}
	 * @exception DAOException
	 * 
	 * **/
	public ArrayList findByPers(String codPers) throws DAOException{
		ArrayList lista = null;
		MensajeBean msg = new MensajeBean();
		try {
			Utilidades common = new Utilidades();
			String codRegistro = "";			
			String pValor = codPers;
			
			codRegistro = common.replace(pValor, "'", "''");		
			log.debug("antes del execute query  en el metodo findByPers");
			lista = (ArrayList)executeQuery(dataSource, findByPers, new Object[]{codRegistro});			
		} catch (Exception e) {
			log.error("*** ERROR *** : ", e);
	    	msg.setMensajeerror("Ha ocurrido un error al buscaTelefono"
	    			.concat(" trabajador : ").concat(e.getMessage()));
	    	msg.setMensajesol("Por favor, intente nuevamente realizar la "
	    			.concat("operacion, de continuar con el problema ")
	    			.concat("comuniquese con el webmaster"));
	    	throw new DAOException (this, msg);
		} finally {
			
		}
		return lista;		
	}
}
