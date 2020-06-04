package pe.gob.sunat.administracion.ctrlpatrim.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;
import pe.gob.sunat.utils.Utilidades;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;
//import pe.gob.sunat.sol.*;

/**
 * Clase       : T30DAO 
 * Descripcion : DAO de la tabla t30movbp
 * Proyecto    : Consultas Sisa 
 * Autor       : JGARCIA9
 * Fecha       : 03-Oct-2006
 */

public class T30DAO extends DAOAbstract{

private DataSource datasource;
	
	/*private String QUERY1_SENTENCE = "SELECT FIRST 500 t30movbp.t30f_document,t30movbp.t30cod_movimi,t30movbp.t30cod_uorgor,t30movbp.t30cod_uorgde,t30movbp.t30cod_locaor,t30movbp.t30n_pisoorig,t30movbp.t30cod_locade,t30movbp.t30n_pisodest," +
			                         "t31dmobp.t31cod_patrim, t31dmobp.t31ind_conser, t33gcpat.t33des_clasin FROM (t30movbp "+
									 "LEFT JOIN t31dmobp ON (t30movbp.t30n_document = t31dmobp.t31n_document) AND (t30movbp.t30cod_movimi = t31dmobp.t31cod_movimi)) "+
									 "LEFT JOIN t33gcpat ON t31dmobp.t31cod_patrim[1,8] = t33gcpat.t33cod_patrim";*/
	
	private String QUERY1_SENTENCE ="SELECT FIRST 500"+
									"(SELECT  P1.T01DES_LARGA FROM T01PARAM P1 WHERE P1.T01_NUMERO='83' AND P1.T01_TIPO = 'D' AND "+
									"P1.T01_ARGUMENTO=T30MOVBP.T30COD_MOVIMI) AS T30COD_MOVIMI_DESC,"+
									"(SELECT  P1.T01DES_LARGA FROM T01PARAM P1 WHERE P1.T01_NUMERO='05' AND P1.T01_TIPO = 'D' AND "+
									"P1.T01_ARGUMENTO=T30MOVBP.T30COD_LOCAOR) AS T30COD_LOCAOR,"+
									"(SELECT  P1.T01DES_LARGA FROM T01PARAM P1 WHERE P1.T01_NUMERO='05' AND P1.T01_TIPO = 'D' AND "+
									"P1.T01_ARGUMENTO=T30MOVBP.T30COD_LOCADE) AS T30COD_LOCADE,"+
									"T30MOVBP.T30F_DOCUMENT,T30MOVBP.T30COD_MOVIMI,T30MOVBP.T30N_DOCUMENT,T30MOVBP.T30COD_UORGOR,T30MOVBP.T30COD_UORGDE,T30MOVBP.T30N_PISOORIG,T30MOVBP.T30N_PISODEST,"+
									"T30MOVBP.T30DES_LNSUOR,T30MOVBP.T30N_ASIGNACI,T30MOVBP.T30N_ORDSALID,"+
									"T31DMOBP.T31COD_PATRIM, T31DMOBP.T31IND_CONSER, T33GCPAT.T33DES_CLASIN "+
									"FROM (T30MOVBP LEFT JOIN T31DMOBP ON (T30MOVBP.T30N_DOCUMENT = T31DMOBP.T31N_DOCUMENT) AND "+ 
									"(T30MOVBP.T30COD_MOVIMI = T31DMOBP.T31COD_MOVIMI)) "+
									"LEFT JOIN T33GCPAT ON T31DMOBP.T31COD_PATRIM[1,8] = T33GCPAT.T33COD_PATRIM "; 
	
	public T30DAO() {
	    super();
	}
	
	/**
	 * @param datasource Object
	*/
	public T30DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.datasource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
	/**
	 * Método joinWithT30T31
	 * @param HashMap p
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public ArrayList joinWithT30T31(HashMap datos)
      throws DAOException { //migrado
		
		Utilidades common = new Utilidades();
		boolean hasParam = false;
		String sWhere = "";
		String sOrder = "";
		List objs = new ArrayList();
		
		//java.sql.Date pt30f_document_i;
		//java.sql.Date pt30f_document_f;
		String pt31cod_patrim="";
		String pt33des_clasin="";
		String pt31cod_person="";
		String ft30f_document_i="";
		String ft30f_document_f="";
		// Build WHERE statement

		//-- Check t30f_document 
		ft30f_document_i=(String)datos.get("fechaIni");
		if (!"".equals(ft30f_document_i)) {
			/*BeanFechaHora fi = new BeanFechaHora() ;
			fi.setFecha( ft30f_document_i);
			pt30f_document_i= fi.getSQLDate();*/
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t30f_document >= ?";
			FechaBean fb = new FechaBean(ft30f_document_i);
			objs.add(fb.getSQLDate());
		}
		//-- Check t30f_document 
		ft30f_document_f=(String)datos.get("fechaFin");
		if (!"".equals(ft30f_document_f)) {
			/*BeanFechaHora ff = new BeanFechaHora() ;
			ff.setFecha( ft30f_document_f);
			pt30f_document_f= ff.getSQLDate();*/
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t30f_document <= ?";
			FechaBean fb = new FechaBean(ft30f_document_f);
			//objs.add(pt30f_document_f);
			objs.add(fb.getSQLDate());
		}
		//-- Check t31cod_patrim 
		pt31cod_patrim = (String)datos.get("codPatrim");
		if (!"".equals(pt31cod_patrim)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t31cod_patrim = ?";
			objs.add(pt31cod_patrim);
		}

		//-- Check t26des_bien 
		pt33des_clasin = (String)datos.get("descBien");
		if (!"".equals(pt33des_clasin)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t33des_clasin like '%" + common.replace(pt33des_clasin, "'", "''") + "%'";
		}
		
		//-- Check t31cod_person
		pt31cod_person = (String)datos.get("nroReg");
		if (!"".equals(pt31cod_person)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t31cod_person = ?";
			objs.add(pt31cod_person);
		}

		if (hasParam) { sWhere = " WHERE (" + sWhere + ")"; }
		// Build ORDER statement
		sOrder = " order by t30f_document Asc";
		//log.debug("T30DAO joinWithT30T31: "+QUERY1_SENTENCE.concat(sWhere).concat(sOrder));
		
		ArrayList aLRpta = new ArrayList();
		try {
			aLRpta = (ArrayList)executeQuery(datasource, QUERY1_SENTENCE.concat(sWhere).concat(sOrder),objs.toArray());
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this,"Error en la consulta. T30DAO - [joinWithT30T31]");
		}
		return aLRpta;
	}
}