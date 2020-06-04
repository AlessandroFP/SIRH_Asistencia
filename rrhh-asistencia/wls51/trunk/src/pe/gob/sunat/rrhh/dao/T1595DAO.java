package pe.gob.sunat.rrhh.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.core.pattern.ServiceLocatorException;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.utils.Constantes;

public class T1595DAO extends DAOAbstract {
	protected final Log log = LogFactory.getLog(getClass());
	private DataSource dataSource = null;
	
	private StringBuffer isDelegado = new StringBuffer("select nvl(sestado_activo,0) as estado from t1595delega ")
												.append(" where cod_opcion = ? and cod_personal_deleg= ? and ")
												.append(" finivig < CURRENT and CURRENT < (ffinvig+1)");
	
	//WERR-PAS20155E230300132
	private StringBuffer isDelegado2 = new StringBuffer("select FIRST 1 nvl(sestado_activo,0) as estado from t1595delega ")
												.append(" where (cod_opcion = ? or cod_opcion = ?) and sestado_activo = ? and cod_personal_deleg= ? and ")
												.append(" finivig < CURRENT and CURRENT < (ffinvig+1)");
	
	private StringBuffer updateDelegado = new StringBuffer("update t1595delega set cod_personal_deleg = ?, finivig=?, ffinvig=? ")
	.append(" where cunidad_organ = ? and cod_personal_jefe = ? and cod_opcion = ? and sestado_activo = '1'");
	
	private StringBuffer findByKey = new StringBuffer("select cunidad_organ, cod_opcion, cod_personal_jefe, cod_personal_deleg, finivig, ffinvig ")
	.append(" from t1595delega where cunidad_organ = ? and cod_opcion = ? and cod_personal_jefe = ?");
	
	//JRR
	private StringBuffer ELIMINAR_DELEGACION = new StringBuffer("delete from t1595delega where cunidad_organ = ? ")
	.append(" and cod_opcion = ? and cod_personal_jefe = ? ");
	
	//JRR
	private StringBuffer UPDATE_DELEGACION = new StringBuffer("update t1595delega set cod_personal_deleg = ?, finivig = ?, ffinvig = ?, cuser_mod = ?, fmod = ? ")
	.append(" where cunidad_organ = ? and cod_opcion = ? and cod_personal_jefe = ? ");
	
	//JRR
	private StringBuffer INSERT_DELEGACION = new StringBuffer("insert into t1595delega ")
	.append(" (cunidad_organ, cod_opcion, cod_personal_jefe, cod_personal_deleg, finivig, ffinvig, sestado_activo, cuser_mod, fmod) ")
	.append(" values (?,?,?,?,?,?,?,?,?) ");
	
	//JRR
	private StringBuffer FIND_BY_UO_PROCESO = new StringBuffer("select cunidad_organ, cod_opcion, cod_personal_jefe, cod_personal_deleg, finivig, ffinvig ")
	.append(" from t1595delega where cunidad_organ = ? and cod_opcion = ? ");
	
	//JRR
	private StringBuffer ELIMINAR_DELEG_BY_UO_PROC = new StringBuffer("delete from t1595delega where ")
	.append(" cunidad_organ = ? and cod_opcion = ? ");
	
	
	public T1595DAO(Object dataSource){
		if (dataSource instanceof DataSource){
			this.dataSource=(DataSource)dataSource;
		} else if (dataSource instanceof String){
			try {
				this.dataSource=this.getDataSource((String)dataSource);			
			}catch(ServiceLocatorException e){
				MensajeBean beanM = new MensajeBean();
		        beanM.setError(true);
		        beanM.setMensajeerror("Ha ocurrido un error inesperado en la comunicaci�n al intentar obtener el origen de datos");
		        beanM.setMensajesol("Por favor intente nuevamente ejecutar la opci�n, en caso de continuar con el problema, comuniquese con nuestro webmaster.");				
				throw new DAOException(this, beanM);
			} catch(Exception e){
				throw new DAOException(this, e.getMessage());
			}
		} else {
			MensajeBean beanM = new MensajeBean();
	        beanM.setError(true);
	        beanM.setMensajeerror("Datasource no valido");
	        beanM.setMensajesol("Por favor intente nuevamente ejecutar la opci�n, en caso de continuar con el problema, comuniquese con nuestro webmaster.");
			throw new DAOException(this, beanM);
		}
		
	}
	
	/**
	 * metodo isDelegado: encargado de verificar si un personal a sido delegado para una opcion
	 * @param params(nroRegistro, opcion)
	 * @return
	 * @throws DAOException
	 */
	public boolean isDelegado(Map params){	
		Map mapa = new HashMap();
		boolean flag = false;
		
		String nroRegistro = (params.get("nroRegistro") != null) ? (String)params.get("nroRegistro"):"";
		String opcion = (params.get("opcion") != null) ? (String)params.get("opcion"):"";
		
		nroRegistro = nroRegistro.replaceAll("'", "''");
		opcion = opcion.replaceAll("'", "''");
				
		mapa = executeQueryUniqueResult(dataSource, isDelegado.toString(), new Object[]{opcion, nroRegistro});		
				
		if( mapa != null && !mapa.isEmpty()){
			String estado = (String)mapa.get("estado");
			if(estado.trim().equals("1")){
				flag =true;
			}
		}
		
		return flag;
	}
	
	/**WERR-PAS20155E230300132
	 * metodo isDelegado2: encargado de verificar si un personal a sido delegado para una opcion
	 * @param params(nroRegistro, opcion)
	 * @return
	 * @throws DAOException
	 */
	public boolean isDelegado2(Map params){	
		Map mapa = new HashMap();
		boolean flag = false;
		log.debug("en el DAO de isDelegado2");
		String nroRegistro = (params.get("nroRegistro") != null) ? (String)params.get("nroRegistro"):"";
		String opcion = (params.get("opcion") != null) ? (String)params.get("opcion"):"";		
		nroRegistro = nroRegistro.replaceAll("'", "''");
		opcion = opcion.replaceAll("'", "''");		
		mapa = executeQueryUniqueResult(dataSource, isDelegado2.toString(), new Object[]{opcion,Constantes.DELEGA_TODO, Constantes.ACTIVO, nroRegistro});				
		if( mapa != null && !mapa.isEmpty()){			
				flag =true;			
		}
		return flag;
	}
	
	/**
	 * metodo RegistrarDelegacion: encargado de registrar delegado
	 * @param params(nroRegistro, opcion)
	 * @return
	 * @throws DAOException
	 */
	public void updateDelegado(Map params){	
		
		String cod_uorgan = (params.get("cod_uorgan") != null) ? (String)params.get("cod_uorgan"):"";		
		String nro_registro = (params.get("nro_registro") != null) ? (String)params.get("nro_registro"):"";		
		String nro_registro_jefe = (params.get("nro_registro_jefe") != null) ? (String)params.get("nro_registro_jefe"):"";		
		String cod_opcion = (params.get("cod_opcion") != null) ? (String)params.get("cod_opcion"):"";		
		String fec_desde = (params.get("fec_desde") != null) ? (String)params.get("fec_desde"):"";		
		String fec_hasta = (params.get("fec_hasta") != null) ? (String)params.get("fec_hasta"):"";
				
		cod_uorgan = cod_uorgan.replaceAll("'", "''");
		nro_registro = nro_registro.replaceAll("'", "''");
		nro_registro_jefe = nro_registro_jefe.replaceAll("'", "''");
		cod_opcion = cod_opcion.replaceAll("'", "''");
		fec_desde = fec_desde.replaceAll("'", "''");
		fec_hasta = fec_hasta.replaceAll("'", "''");
		
		FechaBean fecha_desde = new FechaBean(fec_desde);		
		FechaBean fecha_hasta = new FechaBean(fec_hasta);
		
		int nroRegs = executeUpdate(dataSource, updateDelegado.toString(), new Object[]{nro_registro,fecha_desde.getTimestamp(), fecha_hasta.getTimestamp() , cod_uorgan, nro_registro_jefe, cod_opcion});		
		if(log.isDebugEnabled())log.debug("se actualizaron en total "+nroRegs+"registros");
		
	}
	
	/**
	 * metodo findByKey: leer registro por llave primaria
	 * @param params(nroRegistro, opcion)
	 * @return
	 * @throws DAOException
	 */
	public HashMap findByKey(Map params){	
		HashMap registro = new HashMap();		
		String cod_uorgan = (params.get("cod_uorgan") != null) ? (String)params.get("cod_uorgan"):"";
		String opcion = (params.get("opcion") != null) ? (String)params.get("opcion"):"";
		String nro_registro_jefe = (params.get("nro_registro_jefe") != null) ? (String)params.get("nro_registro_jefe"):"";
		
		cod_uorgan = cod_uorgan.replaceAll("'", "''");
		opcion = opcion.replaceAll("'", "''");
		nro_registro_jefe = nro_registro_jefe.replaceAll("'", "''");
		
		registro = (HashMap)executeQueryUniqueResult(dataSource, findByKey.toString(), new Object[]{cod_uorgan, opcion, nro_registro_jefe});
		
		return registro;
	}

	
	//JRR - 06/02/2009
	/**
	 * Metodo que se encarga de eliminar una delegacion
	 * @throws DAOException
	 */
	public void eliminarDelegacion(Map datos) throws DAOException {	
		if (log.isDebugEnabled()) log.debug("eliminarDelegacion - datos: " + datos);	
		
		executeUpdate(dataSource, ELIMINAR_DELEGACION.toString(), 
						new Object[]{datos.get("cunidad_organ"), datos.get("cod_opcion"),
									 datos.get("cod_personal_jefe")
 									 });
	}
	
	/**
	 * Metodo que se encarga de Actualizar una delegacion
	 * @throws DAOException
	 */
	public void updateDelegacion(Map datos) throws DAOException {	
		if (log.isDebugEnabled()) log.debug("updateDelegacion - datos: " + datos);	
		
		executeUpdate(dataSource, UPDATE_DELEGACION.toString(), 
						new Object[]{datos.get("cod_personal_deleg"),
									 datos.get("finivig"),
									 datos.get("ffinvig"),
									 datos.get("cuser_mod"),
									 datos.get("fmod"),
									 datos.get("cunidad_organ"),
									 datos.get("cod_opcion"),
									 datos.get("cod_personal_jefe")
 									 });
	}
	
	/**
	 * Metodo que se encarga de registrar una delegacion
	 * @throws DAOException
	 */	
	public void registrarDelegacion(Map datos) throws DAOException {		
		if (log.isDebugEnabled()) log.debug("registrarDelegacion - datos: " + datos);	
		
		executeUpdate(dataSource, INSERT_DELEGACION.toString(), new Object[]{datos.get("cunidad_organ"), 
									datos.get("cod_opcion"), datos.get("cod_personal_jefe"), datos.get("cod_personal_deleg"), 
									datos.get("finivig"), datos.get("ffinvig"), datos.get("sestado_activo"), 
									datos.get("cuser_mod"), datos.get("fmod")});
	}
	
	/**
	 * Metodo que busca la delegacion de un trabajador por UO y proceso
	 * @throws DAOException
	 */	
	public List findByUOProceso(Map datos) throws DAOException {		
		List lista = null;
		try{
			if (log.isDebugEnabled()) log.debug("findByUOProcEstado - datos: "+datos);
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);
			lista = executeQuery(dataSource, FIND_BY_UO_PROCESO.toString(),
					new Object[]{datos.get("cunidad_organ"), datos.get("cod_opcion")});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
		}
		return lista;
	}
	
	/**
	 * Metodo que se encarga de eliminar una delegacion por UO y Proceso
	 * @throws DAOException
	 */
	public void eliminarDelegacionByUOProceso(Map datos) throws DAOException {	
		if (log.isDebugEnabled()) log.debug("eliminarDelegacionByUOProceso - datos: " + datos);	
		
		executeUpdate(dataSource, ELIMINAR_DELEG_BY_UO_PROC.toString(), 
						new Object[]{datos.get("cunidad_organ"), datos.get("cod_opcion")});
	}
	
}
