package pe.gob.sunat.administracion.ctrlpatrim.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;
import pe.gob.sunat.framework.util.io.dao.ExcelDAO;

/**
 * 
 * Clase       : T26DAO 
 * Descripcion : 
 * Proyecto    : Sigesa 
 * Autor       : RMONTES
 * Fecha       : 25-jul-2006 10:58:00
 */
public class T26DAO extends DAOAbstract{ 

	private DataSource dataSource = null;
	
	public static final StringBuffer FIND_BY_CODPERS = new StringBuffer("SELECT B.T26COD_PATRIM, B.T26DES_BIEN,  T26COD_MARBIE,  T26COD_MODBIE, ")
																		   .append(" MA.T01DES_LARGA AS T26DESC_MARBIE, MO.T01DES_LARGA AS T26DESC_MODBIE, ")
																		   .append(" B.T26COD_LOCAL, L.T34NOM_LOCAL, B.T26N_PISOLOCA ")
																		   .append(" FROM T26BPASI B, OUTER T01PARAM MA, OUTER T01PARAM MO, OUTER T34LOCAL L ")
																		   .append(" WHERE T26COD_PERSON = ? AND ")
																		   .append(" MA.T01_NUMERO = '44' AND MA.T01_TIPO = 'D' AND B.T26COD_MARBIE = MA.T01_ARGUMENTO AND ")
																		   .append(" MO.T01_NUMERO = '45' AND MO.T01_TIPO = 'D' AND B.T26COD_MARBIE = MO.T01_ARGUMENTO AND ")
																		   .append(" B.T26COD_LOCAL = L.T34COD_LOCAL ");
	
	public static final StringBuffer FIND_BY_CODPATRIM = new StringBuffer("SELECT B.T26COD_PATRIM, B.T26DES_BIEN,  T26COD_MARBIE,  T26COD_MODBIE, ")
															      .append(" MA.T01DES_LARGA AS T26DESC_MARBIE, MO.T01DES_LARGA AS T26DESC_MODBIE, ")
															      .append(" B.T26COD_LOCAL, L.T34NOM_LOCAL, B.T26N_PISOLOCA, B.T26N_SERIBIEN ")
															      .append(" FROM T26BPASI B, OUTER T01PARAM MA, OUTER T01PARAM MO, OUTER T34LOCAL L ")
																  .append(" WHERE B.T26COD_PATRIM = ? AND ")
																  .append(" MA.T01_NUMERO = '44' AND MA.T01_TIPO = 'D' AND B.T26COD_MARBIE = MA.T01_ARGUMENTO AND ")
																  .append(" MO.T01_NUMERO = '45' AND MO.T01_TIPO = 'D' AND B.T26COD_MARBIE = MO.T01_ARGUMENTO AND ")
																  .append(" B.T26COD_LOCAL = L.T34COD_LOCAL ");
	
	/*public static final StringBuffer FIND_BY_CODPATRIM2 = new StringBuffer ("SELECT t26cod_marbie,t26cod_modbie,t26cod_colbie,t26cod_matbie,t26cod_uorgan,")
																    .append("t26cod_person,t26ind_situac,t26cod_local,t26ind_conser,t26ind_proced,t26n_pisoloca,t26med_bien,")
																    .append("t26f_vencgara,t26des_bien,t26cod_patrim,t26n_ultinven,t26n_seribien,t26f_ingalmac,t26f_ultasign")
																    .append(" FROM t26bpasi WHERE t26cod_patrim = ? OR t26cod_ante = ?");*/
	
	public static final StringBuffer FIND_BY_CODPATRIM3 = new StringBuffer ("SELECT ") 
																	.append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='44' AND P1.T01_TIPO = 'D' AND ") 
																	.append("P1.T01_ARGUMENTO=B.T26COD_MARBIE) AS T26COD_MARBIE_DESC, ")
																	.append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='45' AND P1.T01_TIPO = 'D' AND ")
																	.append("P1.T01_ARGUMENTO=B.T26COD_MODBIE) AS T26COD_MODBIE_DESC, ")
																	.append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='46' AND P1.T01_TIPO = 'D' AND ") 
																	.append("P1.T01_ARGUMENTO=B.T26COD_COLBIE) AS T26COD_COLBIE_DESC, ")
																	.append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='47' AND P1.T01_TIPO = 'D' AND ") 
																	.append("P1.T01_ARGUMENTO=B.T26COD_MATBIE) AS T26COD_MATBIE_DESC,B.T26COD_UORGAN,B.T26COD_PERSON, ")
																	.append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='81' AND P1.T01_TIPO = 'D' AND ") 
																	.append("P1.T01_ARGUMENTO=B.T26IND_SITUAC) AS T26IND_SITUAC_DESC,B.T26COD_LOCAL, ")
																	.append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='27' AND P1.T01_TIPO = 'D' AND ") 
																	.append("P1.T01_ARGUMENTO=B.T26IND_CONSER) AS T26IND_CONSER_DESC, ")
																	.append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='82' AND P1.T01_TIPO = 'D' AND ") 
																	.append("P1.T01_ARGUMENTO=B.T26IND_PROCED) AS T26IND_PROCED_DESC,B.T26N_PISOLOCA,B.T26MED_BIEN, ")
																	.append("B.T26F_VENCGARA,B.T26DES_BIEN,B.T26COD_PATRIM,B.T26N_ULTINVEN,B.T26N_SERIBIEN,B.T26F_INGALMAC,B.T26F_ULTASIGN ")
																	.append("FROM T26BPASI B ")
																	.append("WHERE B.T26COD_PATRIM = ? OR B.T26COD_ANTE = ? ");

	private String QUERY1_SENTENCE = "SELECT FIRST 500 t26cod_patrim,t26des_bien,t26ind_conser,t26cod_local,t26f_document,t26f_ultasign FROM t26bpasi ";
	
	//private String QUERY2_SENTENCE = "SELECT t26cod_patrim,t26cod_ante,t26des_bien,t26cod_person,t26cod_uorgan FROM t26bpasi ";
	
	public static final StringBuffer QUERY2_SENTENCE = new StringBuffer("SELECT ") 
																 .append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='44' AND P1.T01_TIPO = 'D' AND ") 
																 .append("P1.T01_ARGUMENTO=B.T26COD_MARBIE) AS T26COD_MARBIE_DESC, ")
																 .append("(SELECT  P1.T01DES_CORTA FROM T01PARAM P1 WHERE P1.T01_NUMERO='45' AND P1.T01_TIPO = 'D' AND ")
																 .append("P1.T01_ARGUMENTO=B.T26COD_MODBIE) AS T26COD_MODBIE_DESC, ")
																 .append("B.t26cod_patrim,B.t26cod_ante,B.t26des_bien,B.t26cod_person,B.t26cod_uorgan ")
																 .append("FROM T26BPASI B");
	public T26DAO() {
	}
    public T26DAO(String jndi) {
		dataSource = getDataSource(jndi);
	}
    public T26DAO(DataSource ds) {
		dataSource = ds;
	}
	/**
	  * Metodo que obtiene la lista de Bienes de un determinado usuario  
	  * @param cod_pers
	  * @return
	  * @throws DAOException
	  */
    public List findByCodPers(String cod_pers)
	throws DAOException {

    	ArrayList res = null;
    	try {
    		res = (ArrayList) executeQuery(dataSource, FIND_BY_CODPERS.toString(), new Object[]{cod_pers});				
    	} catch (Exception e) {
    		log.error("*** SQL Error ****",e);
    		throw new DAOException(this, "Error en la consulta. T26DAO - [findByCodPers]");
    	}  
    	return res;    
   	}
    
    /**
	  * Metodo que obtiene los datos de un bien patrimonial espifico
	  * @param cod_pers
	  * @return
	  * @throws DAOException
	  */
    public Map findByCodPatrim(String cod_patrim)
	throws DAOException {

	   	Map res = null;
	   	try {
	   		res = (HashMap) executeQueryUniqueResult(dataSource, FIND_BY_CODPATRIM.toString(), new Object[]{cod_patrim});				
	   	} catch (Exception e) {
	   		log.error("*** SQL Error ****",e);
	   		throw new DAOException(this, "Error en la consulta. T26DAO - [findByCodPatrim]");
	   	}  
	   	return res;    
  	}
   
   //de aqui para adelante son metodos que se agregan
   
    /**
 	  * Método findByQuery obtiene la lista de bienes asignados 
 	  * @param HashMap p
 	  * @param String cuenta
 	  * @return ArrayList aLRpta
 	  * @throws DAOException
 	  */
	public ArrayList findByQuery(HashMap datos) // migrado
       throws DAOException {

        String cuenta = (String)datos.get("cuenta");
		
		boolean hasParam = false;
		String sWhere = "";
		String sOrder = "";
		String sSQL="";
		List objs = new ArrayList();
		
		HashMap res = new HashMap();
		
		//Date pt26f_priasign_i;
		//Date pt26f_priasign_f;
		String ft26f_priasign_i="";
		String ft26f_priasign_f="";
		String pt26cod_patrim="";
		String pt26cod_person="";
		
		
		// Build WHERE statement
	
		//-- Check t26f_priasign 
		ft26f_priasign_i=(String)datos.get("fechaIni");
		if (!"".equals(ft26f_priasign_i)) {
			FechaBean fb = new FechaBean(ft26f_priasign_i);
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26f_priasign >= ? ";
			objs.add(fb.getSQLDate());
		}
		//-- Check t26f_priasign 
		ft26f_priasign_f=(String)datos.get("fechaFin");
		if (!"".equals(ft26f_priasign_f)) {
			FechaBean fb = new FechaBean(ft26f_priasign_f);
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26f_priasign <= ? ";
			objs.add(fb.getSQLDate());
		}
		//-- Check t26cod_patrim 
		pt26cod_patrim = (String)datos.get("codPatrim");
		if (!"".equals(pt26cod_patrim)){
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26cod_patrim = ? ";
			objs.add(pt26cod_patrim);
		}
		//-- Check t26cod_person 
		pt26cod_person = (String)datos.get("nroReg");
		if (!"".equals(pt26cod_person)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26cod_person = ? ";
			objs.add(pt26cod_person);
		}
		
		if (hasParam) { sWhere = " WHERE ( T26IND_SITUAC = 'A' and  " + sWhere + ")"; }
		// Build ORDER statement
		sOrder = " order by t26f_priasign Asc";

		if (cuenta != null && cuenta.equals("1")){
			sSQL = "SELECT count(*) contador FROM t26bpasi ";
			sSQL = sSQL + sWhere;
		}else{
			sSQL = "SELECT * FROM t26bpasi ";
			sSQL = sSQL + sWhere + sOrder;
		}
		
		
		log.debug("T26DAO findByQuery : "+sSQL);
		ArrayList aLRpta = new ArrayList();
		try {
			if (cuenta != null && cuenta.equals("1")){
				res = (HashMap) executeQueryUniqueResult(dataSource, sSQL.toString(), objs.toArray());
				aLRpta.add(res);
			} else {
				aLRpta = (ArrayList)executeQuery(dataSource, sSQL.toString(),objs.toArray());
			}
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
	   		throw new DAOException(this, "Error en la consulta. T26DAO - [findByQuery]");
		}
		return aLRpta;
	}
	
	/**
	 * Metodo findByQueryDisp
	 * @param HashMap p
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public ArrayList findByQueryDisp(HashMap datos) 
	  throws DAOException { //Migrado
        
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		//String sSQL="";
		List objs = new ArrayList();

		String pt26ind_situac="";
		//String pt26cod_patrim="";
		String pt26des_bien="";
		String pt26ind_conser="";
		String pt26cod_local="";

		// Build WHERE statement
	
		//-- Check t26cod_patrim 
		/*pt26cod_patrim = (String)p.get("p6");
		if (pt26cod_patrim != null && ! pt26cod_patrim.equals("")) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26cod_patrim = '" + common.replace(pt26cod_patrim, "'", "''") + "'";
		}*/

		//-- Check t26ind_situac 
		pt26ind_situac = (String)datos.get("indSituacion");
		if (!"".equals(pt26ind_situac)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26ind_situac = ? ";
			objs.add(pt26ind_situac);
		}

		//-- Check t26des_bien 
		pt26des_bien = (String)datos.get("descBien");
		if (!"".equals(pt26des_bien)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			//sWhere += "t26des_bien like '%" + common.replace(pt26des_bien, "'", "''") + "%'";
			sWhere += "t26des_bien like '%" + pt26des_bien.replaceAll("'", "''") + "%'";
			//sWhere += "t26des_bien like '%?%'";
			//objs.add(pt26des_bien);
		}
		
		//-- Check t26ind_conser 
		pt26ind_conser = (String)datos.get("estado");
		if (!"".equals(pt26ind_conser)) {
			if (! sWhere.equals("")) sWhere += " and ";
				hasParam = true;
			sWhere += "t26ind_conser = ?";
			objs.add(pt26ind_conser);
		}
		
		//-- Check t26cod_local 
		pt26cod_local = (String)datos.get("codLocal");
		if (!"".equals(pt26cod_local)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26cod_local = ?";
			objs.add(pt26cod_local);
		}

		if (hasParam) { sWhere = " WHERE (" + sWhere + ")"; }
		
		// Build ORDER statement
		sOrder = " order by t26des_bien Asc";
		
		log.debug("T26DA0 findByQueryDisp "+QUERY1_SENTENCE.concat(sWhere).concat(sOrder));
		
		ArrayList aLRpta = new ArrayList();
		
		try {
			aLRpta = (ArrayList)executeQuery(dataSource, QUERY1_SENTENCE.concat(sWhere).concat(sOrder),objs.toArray());
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
	   		throw new DAOException(this, "Error en la consulta. T26DAO - [findByQueryDisp]");
		}
		return aLRpta;
	}

	/**
	 * Metodo findByQueryBPAsig
	 * @param HashMap p
	 * @return ArrayList aLRpta
	 * @throws DAOException
	 */
	public ArrayList findByQueryBPAsig(HashMap datos)
	  throws DAOException {//migrado
        
		boolean hasParam = false;
		String sWhere = "";
		String sOrder = "";
		
		List objs = new ArrayList();

		String pt26cod_patrim="";
		String pt26cod_person="";
		String pt26cod_uorgan="";

		//-- Check t26cod_patrim 
		pt26cod_patrim = (String)datos.get("codPatrim");
		if (!"".equals(pt26cod_patrim)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "(t26cod_patrim = ? or ";
			sWhere += "t26cod_ante = ? )";
			objs.add(pt26cod_patrim);
			objs.add(pt26cod_patrim);
		}

		//-- Check t26ind_situac 
		pt26cod_person = (String)datos.get("nroReg");
		if (!"".equals(pt26cod_person)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26cod_person = ?";
			objs.add(pt26cod_person);
		}

		//-- Check t26des_bien 
		pt26cod_uorgan = (String)datos.get("codUO");
		if (!"".equals(pt26cod_uorgan)) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t26cod_uorgan = ?";
			objs.add(pt26cod_uorgan);
		}

		if (hasParam) { sWhere = " WHERE ( T26IND_SITUAC = 'A' and " + sWhere + ")"; }
		
		sOrder = " order by t26des_bien Asc";
		//log.debug("T26DAO findByQueryBPAsig: "+QUERY2_SENTENCE_.toString().concat(sWhere).concat(sOrder));

		ArrayList aLRpta = new ArrayList();
   
		try {
			aLRpta = (ArrayList)executeQuery(dataSource, QUERY2_SENTENCE.toString().concat(sWhere).concat(sOrder),objs.toArray());
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
	   		throw new DAOException(this, "Error en la consulta. T26DAO - [findByQueryBPAsig]");
		}
		return aLRpta;
	}

	/**
	 * Metodo fichabien
	 * @param HashMap p
	 * @return HashMap hMap
	 * @throws DAOException
	 */
	public HashMap findFicha(HashMap p) 
	  throws DAOException {//migrado
		HashMap res = new HashMap();
		String dato = (String)p.get("codPatrim");
		try {
			res = (HashMap) executeQueryUniqueResult(dataSource, FIND_BY_CODPATRIM3.toString(), new Object[]{dato,dato});
		} catch (Exception e) {
			log.error("*** SQL Error ****",e);
			throw new DAOException(this, "Error en la consulta. T26DAO - [findFicha]");
		}
		return res;
	}
	
	/**
	 * Guarda registros en formato Excel
	 * @param destFileName String
	 * 
	 * @return boolean
	 */
	public boolean listAllToXLS(String destFileName,List datos) 
	  throws Exception {
		boolean bOk = true;
	    List titulos = new ArrayList();
	    List campos = new ArrayList();
	    List tipos = new ArrayList();
	    
	    titulos.add("Cod. Patr.");
	    titulos.add("Cod. Patr. Ant.");
	    titulos.add("Descripción del Bien");
	    titulos.add("Marca");
	    titulos.add("Modelo");
	    titulos.add("Asignado A");
	    titulos.add("Nombre Empleado");
	    titulos.add("Cod. Unid. Org.");
	    titulos.add("Nombre Unid. Org.");
	    campos.add("t26cod_patrim");
	    campos.add("t26cod_ante");
	    campos.add("t26des_bien");
	    campos.add("t26cod_marbie_des");
	    campos.add("t26cod_modbie_des");
	    campos.add("t26cod_person");
	    campos.add("t26cod_person_des");
	    campos.add("t26cod_uorgan");
	    campos.add("t26cod_uorgan_des");
	    
	    for (int i=0;i<9;i++)
	    	tipos.add("1");
	    
	    ExcelDAO excelDAO = new ExcelDAO();
	    
	    bOk = excelDAO.generaArchivo(titulos, campos, tipos, datos, destFileName,"Bienes Patrimoniales Asignados", 2, 2);
	    
	    return bOk;
	}
	  
	/**
	 * Guarda todos los registros en formato Excel
	 * @param destFileName String
	 * 
	 * @return boolean
	 */
	public boolean listAllToXLS2(String destFileName,List datos) 
	  throws Exception {
		boolean bOk = true;
	    List titulos = new ArrayList();
	    List campos = new ArrayList();
	    List tipos = new ArrayList();
	    
	    titulos.add("Cod. Patr.");
	    titulos.add("Descripción del Bien");
	    titulos.add("Fecha Asig.");
	    titulos.add("N° O/S");
	    titulos.add("Reg. Empleado");
	    titulos.add("Nom. Empleado");
	    campos.add("t26cod_patrim");
	    campos.add("t26des_bien");
	    campos.add("t26f_priasign");
	    campos.add("t26n_ordesali");
	    campos.add("t26cod_person");
	    campos.add("t26cod_person_desc");
	    
	    for (int i=0;i<9;i++)
	    	tipos.add("1");
	    
	    ExcelDAO excelDAO = new ExcelDAO();
	    
	    bOk = excelDAO.generaArchivo(titulos, campos, tipos, datos, destFileName,"Bienes Patrimoniales Asignados", 2, 2);
	    
	    return bOk;
	}
}
