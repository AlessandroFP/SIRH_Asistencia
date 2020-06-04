package pe.gob.sunat.rrhh.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.dao.interfaz.BeanMapper;
import pe.gob.sunat.framework.core.pattern.DynaBean;
//import pe.gob.sunat.framework.util.cache.dao.ParamDAO;
import pe.gob.sunat.framework.util.lang.Ordenamiento;



/**
 * 
 * Clase : TelefonoDAO 
 * Autor : RMONTES
 * Fecha : 10/01/2008
 * 
 * Descripcion: Esta clase se creo con la finalidad de obtener los datos de la tabla
 * Telefono de personal.
 */
public class TelefonoDAO extends DAOAbstract {
	
	private DataSource dataSource_telef = null;//datasource a la base de datos telef
	
	private DataSource dataSource_Sp = null;//datasource a la base de datos sp
	
	private final StringBuffer FIND_TELEFONOS = new StringBuffer("SELECT T.COD_PERS, T.TIP_LINEA, T.NUMERO, T.COD_UORGAN, NVL(T.COD_LOCAL,'') AS COD_LOCAL, NVL(T.PISO,'') AS PISO,") 
														 .append(" (SELECT L.T99DESCRIP FROM T99CODIGOS L")
														 .append(" WHERE L.T99COD_TAB = '901' AND L.T99TIP_DESC = 'D'") 
														 .append(" AND T.TIP_LINEA = L.T99CODIGO) AS TIP_LINEA_DESC")
														 .append(" FROM TELEFONOS T")
														 .append(" WHERE T.COD_PERS = ?")
														 .append(" ORDER BY TIP_LINEA");
	private final StringBuffer FIND_TELEFONOS_BY_TIPO = new StringBuffer("SELECT NUMERO FROM TELEFONOS WHERE COD_PERS = ? AND TIP_LINEA = ?");
	private final StringBuffer FIND_BY_KEY = new StringBuffer("SELECT COD_PERS, TIP_LINEA, NUMERO FROM TELEFONOS WHERE COD_PERS = ? AND TIP_LINEA = ? and NUMERO = ?");
	
	private final StringBuffer INSERT_TELEFONO = new StringBuffer("INSERT INTO TELEFONOS (COD_PERS, TIP_LINEA, NUMERO, COD_UORGAN, COD_LOCAL, PISO) VALUES (?,?,?,?,?,?)");	
	
	private final StringBuffer DELETE_TELEFONO = new StringBuffer("DELETE FROM TELEFONOS WHERE COD_PERS = ? AND TIP_LINEA =? AND  NUMERO =? AND  COD_UORGAN =? ");
	
	private static final StringBuffer joinWithT02T12 = new StringBuffer("SELECT t02cod_pers, ")
	.append("t02ap_pate , t02ap_mate , t02nombres , t02cod_uorg, nvl(t02cod_uorgl,'') as t02cod_uorgl, nvl(t12des_uorga,'') as t12des_uorga, nvl(t12des_corta,'') as t12des_corta, ")
	.append("t02cod_cate, nvl(t02cod_catel,'') as t02cod_catel, t02cod_stat, nvl(t12cod_jefat,'') as t12cod_jefat, nvl(t12cod_encar,'') as t12cod_encar ")
	.append("FROM (t02perdp LEFT JOIN t12uorga ON t02cod_uorg = t12cod_uorga) ");
	
	public TelefonoDAO() {}

    /**
	 * @param datasource Object
	 */
	public TelefonoDAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.dataSource_telef = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.dataSource_telef = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	
	/**
	 * constructor TelefonoDAO: para poder usar el bean mapper de la base de datos TELEF con SP
	 * @param datasources Map de datasource en este caso dscSp (sp) y dscTelef de (telef)
	 */
	public TelefonoDAO(Map datasources) {		
		dataSource_Sp = (DataSource)datasources.get("dscSp");
		dataSource_telef = (DataSource)datasources.get("dscTelef");
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
				StringBuffer ListaTelefonos = new StringBuffer("");
				Set campos = mapa.keySet();
				String cod_pers = mapa.get("t02cod_pers") != null ? mapa.get("t02cod_pers").toString().trim():"";
				
				if(campos.contains("t02cod_pers")){
					int tam = 0;					
					if (cod_pers != null && cod_pers.length()>0 ) {
						b = (ArrayList)findTelefonos(cod_pers);						
						tam = b.size(); 
						if(tam == 0){
							ListaTelefonos.append("No Registra");
						}else{	
							ListaTelefonos.append("<select>");
							HashMap aux = null;
							for( int i=0; i<tam;i++ ){
								aux = (HashMap)b.get(i); 
								ListaTelefonos.append("<option>");
								ListaTelefonos.append(aux.get("numero"));
								ListaTelefonos.append("</option>");
							}
							ListaTelefonos.append("</select>");
						}
					}
					mapa.put("t02telefonos_desc", ListaTelefonos.toString().trim());
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
	 * Metodo por el cual se obtienen los telefonos de una determinada persona.
	 * 
	 * @param cod_pers
	 * @return
	 */
	public List findTelefonos(String cod_pers) {
		List res = executeQuery(dataSource_telef, FIND_TELEFONOS.toString(), new Object[]{cod_pers});		
		Ordenamiento.sort(res, "tip_linea");		
		return res;
	}
	
	/**
	 * Metodo por el cual se busca si existe registro 
	 * 
	 * @param cod_pers
	 * @return
	 */
	public Map findTelefonoByKey(Map params) {		
		Map res = executeQueryUniqueResult(dataSource_telef, FIND_BY_KEY.toString(), new Object[]{params.get("cod_pers"), 
													params.get("tip_linea"), params.get("numero").toString().trim()});				
		return res;
	}
	
	/**
	 *  Metodo por el cual se obtienen los telefonos de un tipo registrados para 
	 *  una determinada persona.
	 * 
	 * @param mapa
	 * @return
	 */
	public List findTelefonosByTipo(String cod_pers, String tipo) {
		return executeQuery(dataSource_telef, FIND_TELEFONOS_BY_TIPO.toString(), new Object []{cod_pers, tipo});		
	}
	
	/**
	 *  Metodo por el cual se obtienen los telefonos de un tipo registrados para 
	 *  una determinada persona.
	 * 
	 * @param mapa
	 * @return
	 */
	public void insertTelefono(DynaBean telef) {
		List parametros = new ArrayList();
		
		parametros.add(telef.get("cod_pers"));
		parametros.add(telef.get("tip_linea"));
		parametros.add(telef.get("numero").toString().trim());
		parametros.add(telef.get("cod_uorgan"));
				
		if(telef.isSet("cod_local")){
			parametros.add(telef.get("cod_local"));
		} else {
			parametros.add("");
		}
		
		if(telef.isSet("piso")){
			parametros.add(telef.get("piso"));
		} else {
			parametros.add("");
		}
		
		executeUpdate(dataSource_telef, INSERT_TELEFONO.toString(), parametros.toArray());		
	}	
	
	/**
	 *  Metodo por el cual se obtienen los telefonos de un tipo registrados para 
	 *  una determinada persona.
	 * 
	 * @param mapa
	 * @return
	 */
	public void deleteTelefono(Map telef) {
		executeUpdate(dataSource_telef, DELETE_TELEFONO.toString(), new Object []{telef.get("cod_pers"),telef.get("tip_linea"),telef.get("numero"),telef.get("cod_uorgan")});		
	}
	
	
	
	/**
	 * Metodo para la busqueda de datos de personal,unido a descripcion de la categoria asignada 
	 * y listado de anexos en el bean mapper
	 * @param Map  params
	 * 
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public List joinWithT02T12(Map params) throws DAOException {		
		boolean flag = false;		
		StringBuffer sWhere = new StringBuffer("");
		StringBuffer sSQL = new StringBuffer("");
				
		String nreg = ( params.get("nreg") == null ) ?  "" : params.get("nreg").toString().trim();
		String appat = ( params.get("appat") == null ) ?  "" : params.get("appat").toString();
		String apmat = ( params.get("apmat") == null ) ?  "" : params.get("apmat").toString();
		String nomb = ( params.get("nomb") == null ) ?  "" : params.get("nomb").toString();
		String coduorg = ( params.get("coduorg") == null ) ?  "" : params.get("coduorg").toString().trim();
		String codcate = ( params.get("codcate") == null ) ?  "" : params.get("codcate").toString().trim();
		String estado = ( params.get("estado") == null ) ?  "" : params.get("estado").toString().trim();
		
		nreg = nreg.replaceAll("'", "''");
		appat = appat.replaceAll("'", "''");
		apmat = apmat.replaceAll("'", "''");
		nomb = nomb.replaceAll("'", "''");
		coduorg = coduorg.replaceAll("'", "''");
		codcate = codcate.replaceAll("'", "''");
		estado = estado.replaceAll("'", "''");
				
		//verificando parametro numero de registro
		if (nreg != null && nreg.length() > 0) {			
			flag = true;
			sWhere.append("t02cod_pers = '" + nreg + "'");
		}
		
		//verificando parametro apellido paterno
		if (appat != null && appat.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}					
			sWhere.append("t02ap_pate like '" + appat + "%'");
		}
		
		//verificando parametro apellido materno
		if (apmat != null && apmat.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02ap_mate like '" + apmat + "%'");
		}

		//verificando parametro nombres		
		if (nomb != null && nomb.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02nombres like '%" + nomb + "%'");
		}
		
		//verificando parametro apellido codigo de unidad organizacional
		if (coduorg != null && coduorg.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_uorg = '" + coduorg + "'");
		}
		
		//verificando parametro apellido codigo de categoria
		if (codcate != null && codcate.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_cate = '" + codcate + "'");
		}

		//verificando parametro estado(1)
		if (estado != null && estado.length() > 0) {
			if (flag){
				sWhere.append(" and ");
			} else {
				flag = true;
			}
			sWhere.append("t02cod_stat = '" + estado + "'");
		}
		
		if (flag) {
			sWhere = new StringBuffer(" WHERE (" + sWhere.toString()+")");
		}
		
		sSQL.append(joinWithT02T12).append(sWhere.toString()).append(" order by t02ap_pate, t02ap_mate ");
				
		List listaRpta = executeQuery(dataSource_Sp, sSQL.toString(),beanMapper);		
		if(log.isDebugEnabled()) log.debug("total = "+listaRpta.size());
		
		return listaRpta;
	}
	
	
}
