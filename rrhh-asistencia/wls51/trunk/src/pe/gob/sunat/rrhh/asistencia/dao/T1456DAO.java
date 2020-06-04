package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.utils.Constantes;
import pe.gob.sunat.utils.Utiles;

/**
 * 
 * Clase : T1456DAO 
 * Autor : JROJASR 
 * Fecha : 09/05/2011
 * 
 * Descripcion: Esta clase se creo con la finalidad de administrar las consultas a la tabla t1456vacacion_gen.
 */

public class T1456DAO extends DAOAbstract {
	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");

	private final StringBuffer FIND_BY_FING_FREPUESTO = new StringBuffer("select p.t02cod_pers, substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) t02cod_uorg, "
		).append( " p.t02f_ingsun f_ingsun, p.t02cod_ante, p.t02cod_rel, "
		).append( " (select g.fecha from t1456vacacion_gen g "
		).append( "  where g.cod_pers = p.t02cod_pers and est_id = ? ) f_repuesto "
		).append( "from 	t02perdp p "
		).append( "where 	(p.t02f_ingsun <= ? "
		).append( "         or ( select fecha "
		).append( "              from t1456vacacion_gen ");
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	private final StringBuffer FINDCOLABORADOR_BY_FING_FREPUESTO = new StringBuffer("select p.t02cod_pers, substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) t02cod_uorg, "
	).append( "p.t02f_ingsun f_ingsun, p.t02cod_ante, p.t02cod_rel, "
	).append( "(select g.fecha from t1456vacacion_gen g "
	).append( "where g.cod_pers = p.t02cod_pers and est_id = ? ) f_repuesto "
	).append( "from 	t02perdp p "
	).append( "where  p.t02cod_stat=? "
	).append( "and p.t02cod_pers=? ");	
	//

	
	private DataSource datasource;	
	
	/**
	 * 
	 * Constructor del DAO para crear el datasource
	 * dependiendo del tipo de objeto que recibe.
	 * 
	 * @param datasource Object
	 *  
	 * */
	public T1456DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.datasource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}


	/**
	 * Metodo que se encarga de la busqueda de los datos de los trabajadores
	 * cuya fecha de ingreso o reingreso a la Sunat sea menor a la indicada por
	 * el parametro respectivo, y que no tengan generado aun un registro en la
	 * tabla T1281Vacaciones_c para el anno indicado por el parametro
	 * correspondiente y filtrados por un criterio con un valor determinado.
	 * 
	 * @throws DAOException
	 */
	public List findByFIngFRepuesto(Map params) throws DAOException {

		List detalle = null;
		StringBuffer strSQL = new StringBuffer(FIND_BY_FING_FREPUESTO.toString());		
		String criterio = (params.get("criterio") != null ) ? params.get("criterio").toString():"";
		Map seguridad = (params.get("seguridad") != null) ? (HashMap)params.get("seguridad"):new HashMap();
		String valor =  (params.get("valor") != null ) ? params.get("valor").toString():"";
		
		//String fIni = (params.get("fIni") != null ) ? params.get("fIni").toString():"";  
		String fIni = Utiles.toYYYYMMDD(params.get("fIni").toString());
		String anno = (params.get("anno") != null ) ? params.get("anno").toString():"";
		
		
		strSQL.append( " where cod_pers = p.t02cod_pers and est_id = ? ) <= DATE('")
			  .append( fIni ).append( "')) ")
			  .append( " and p.t02cod_stat = ? ");
		
		if (params.get("regimen")!=null && params.get("regimen").equals("2")) {
			strSQL.append(" and t02cod_rel = '09' ");
		} else if (params.get("regimen")!=null && params.get("regimen").equals("1")){
			strSQL.append(" and t02cod_rel not in ('09','10') ");
		} else if (params.get("regimen")!=null && params.get("regimen").equals("3")){
			strSQL.append(" and t02cod_rel = '10' ");
		}
		//JRR - Cuando no se filtra por regimen, vienen todos
		
		if (criterio.equals("0")) {
			strSQL.append(" and t02cod_pers = '" ).append( valor.trim().toUpperCase()
			).append( "'");
		}

		if (criterio.equals("1")) {
			strSQL.append(" and substr(trim(nvl(t02cod_uorgl,'')||nvl(t02cod_uorg,'')),1,6) = '"
			).append( valor.trim().toUpperCase() ).append( "'");
		}

		if (criterio.equals("2") && !valor.equals("")){//Intendencia
			String intendencia = valor.substring(0,2);
			strSQL.append(" AND SUBSTR(TRIM(NVL(T02COD_UORGL, '')||NVL(T02COD_UORG, '')), 1, 6) LIKE '" ).append( intendencia.trim().toUpperCase()).append( "%' ");
		}//SI ES INSTITUCIONAL("3") NO ES NECESARIO NINGUN FILTRO DE COD_UORG
		
		strSQL.append("  and p.t02cod_pers not in ( "
		).append( "                select cod_pers  "
		).append( "                from t1281vacaciones_c  "
		).append( "                where cod_pers > '0' and anno = '" ).append( anno
		).append( "' and dias > 0) ");

		//criterios de visibilidad
		if (seguridad != null && !seguridad.isEmpty()) {				

			String codigo = (String) seguridad.get("codPers");

			strSQL.append(" and substr(trim(nvl(p.t02cod_uorgl,'')||nvl(p.t02cod_uorg,'')),1,6) in ");
			strSQL.append(" (select u_organ from t1485seg_uorga "
					).append( " where cod_pers = '" ).append( codigo.toUpperCase()
					).append( "' and operacion = '" ).append( Constantes.PROCESO_VACACIONES
					).append( "') ");
		}

		strSQL.append(" order by p.t02cod_pers");

		
		detalle = executeQuery(datasource, strSQL.toString(), new Object[]{
				constantes.leePropiedad("ACTIVO"),
				Utiles.stringToTimestamp(params.get("fIni").toString()+ " 00:00:00"),
				constantes.leePropiedad("ACTIVO"),
				constantes.leePropiedad("ACTIVO")});
		
		return detalle;
	}
	
	//ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	/**
	  * Metodo que busca la fecha de ingreso y fecha de repuesto de un colaborador	
	  * @param codPers String	
	  * @return colab Map
	  * @throws DAOException
	  */
	public Map findTrabajadorByFingFrepuesto(String codPers) throws DAOException {
				
		Map colab = null;
		if (log.isDebugEnabled()) log.debug("T1456DAO - findTrabajadorByFingFrepuesto - codPers: " + codPers);		
		try{
			
			setIsolationLevel(DAOAbstract.TX_READ_UNCOMMITTED);			
			colab = executeQueryUniqueResult(datasource, FINDCOLABORADOR_BY_FING_FREPUESTO.toString(), new Object[]{constantes.leePropiedad("ACTIVO"),constantes.leePropiedad("ACTIVO"),codPers});
			
		}catch (Exception e) {
			log.error("*** SQL Error ****", e);
			throw new DAOException(this, "Error en consulta. [findTrabajadorByFingFrepuesto]");
		}	
		return colab;
	}
	//FIN ICAPUNAY - PAS20171U230300074 - Mejoras vacaciones
	
}
