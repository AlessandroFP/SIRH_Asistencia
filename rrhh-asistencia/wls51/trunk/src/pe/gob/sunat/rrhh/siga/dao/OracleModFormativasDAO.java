package pe.gob.sunat.rrhh.siga.dao;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;


/**
 * 
 * Clase       : OracleModFormativasDAO 
 * Proyecto    : Asistencia 
 * Descripcion : Clase encargada de administrar las consultas a la base de datos sig de oracle para modalidades formativas
 * Autor       : ICAPUNAY
 * Fecha       : 23-junio-2011 17:00:00
 */
public class OracleModFormativasDAO extends DAOAbstract 
{
	private DataSource datasource;
	
	private final StringBuffer REGISTRAR_SALDOVACACIONAL_FORMATIVAS = new  StringBuffer("SELECT SIGA01.PER_PLANILLA_PROCESO_CALC_MF.leer_vacciones_registro");
		
	/**
	* Este constructor del DAO dicierne como crear el datasource
	* dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	* mejorar la invocacion del dao.	
	* @param datasource Object
	*/
	public OracleModFormativasDAO(Object datasource) {
		if (datasource instanceof DataSource)
		  this.datasource = (DataSource)datasource;
		else if (datasource instanceof String)
		  this.datasource = getDataSource((String)datasource);
		else
		  throw new DAOException(this, "Datasource no valido");
	}
	
	
	/**
	 * Metodo encargado de obtener un valor de retorno, de  si a un colaborador de la modalidad formativa se le generará saldo vacacional para el 
	 * rango de fechas ingresadas como parametro. Devuelve valor 1 (si se genera saldo) o 0 (no se genera saldo).	
	 * @param String fechaInicial
	 * @param String fechaFinal
	 * @param String cod_persFormativo
	 * @return String creaSaldo
	 * @throws DAOException
	 */	
	public String registrarSaldoVacacionalModFormativas(String fechaInicial,String fechaFinal,String cod_persFormativo) throws DAOException {
		
		Map m_creaSaldo= new HashMap();
		String creaSaldo=null;
		if (log.isDebugEnabled()) log.debug("OracleModFormativasDAO-registrarSaldoVacacionalModFormativas-cod_persFormativo: "+cod_persFormativo);
		if (log.isDebugEnabled()) log.debug("fechaInicial: "+fechaInicial);
		if (log.isDebugEnabled()) log.debug("fechaFinal: "+fechaFinal);		
		try{
			
			m_creaSaldo = executeQueryUniqueResult(datasource, REGISTRAR_SALDOVACACIONAL_FORMATIVAS.append("(TO_DATE('").append(fechaInicial).
			append("','YYYYMMDD'),TO_DATE('").append(fechaFinal).append("','YYYYMMDD'),'").append(cod_persFormativo).append("') ").append("AS RESULTADO ").append("FROM DUAL").toString());
			if(m_creaSaldo!=null && !m_creaSaldo.isEmpty()){
				if (log.isDebugEnabled()) log.debug("Ingreso aqui");
				if (log.isDebugEnabled()) log.debug("m_creaSaldo.get(resultado): "+m_creaSaldo.get("resultado"));
				creaSaldo=(String)m_creaSaldo.get("resultado");
				if (log.isDebugEnabled()) log.debug("creaSaldo: "+creaSaldo);
			}			
		}catch (Exception e) {
			log.error("*** Oracle Error ****", e);
			throw new DAOException(this, "Error en consulta Oracle. [REGISTRAR_SALDOVACACIONAL_FORMATIVAS]");
		}		
		return creaSaldo;
	}
	
	
	
}
