package pe.gob.sunat.sa.dao; 

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import pe.gob.sunat.utils.Utilidades;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.sol.*;
//import pe.gob.sunat.sol.dao.DAOAccesoBD;
//import pe.gob.sunat.sp.dao.*;
import pe.gob.sunat.rrhh.dao.T12DAO;

/**
 * @author WDELGADO
 *
 * DAO d ela tabla t30movbp
 * 
 */
public class T30DAO extends DAOAbstract{

private DataSource datasource;
	
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
	 * @throws IncompleteConversationalState
	 */
	public ArrayList joinWithT30T31(HashMap p)
    	throws IncompleteConversationalState {
		
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL="";
		String pool1= (String)p.get("p1");
		String pool2= (String)p.get("p2");
		java.sql.Date pt30f_document_i;
		java.sql.Date pt30f_document_f;
		String pt31cod_patrim="";
		String pt33des_clasin="";
		String pt31cod_person="";
		String ft30f_document_i="";
		String ft30f_document_f="";
		// Build WHERE statement

		//-- Check t30f_document 
		ft30f_document_i=(String)p.get("p4");
		if (ft30f_document_i != null && ! ft30f_document_i.equals("")) {
			BeanFechaHora fi = new BeanFechaHora() ;
			fi.setFecha( ft30f_document_i);
			pt30f_document_i= fi.getSQLDate();
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t30f_document >= '" + pt30f_document_i + "' ";
		}
		//-- Check t30f_document 
		ft30f_document_f=(String)p.get("p5");
		if (ft30f_document_f != null && ! ft30f_document_f.equals("")) {
			BeanFechaHora ff = new BeanFechaHora() ;
			ff.setFecha( ft30f_document_f);
			pt30f_document_f= ff.getSQLDate();
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t30f_document <= '" + pt30f_document_f + "' ";
		}
		//-- Check t31cod_patrim 
		pt31cod_patrim = (String)p.get("p6");
		if (pt31cod_patrim != null && ! pt31cod_patrim.equals("")) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t31cod_patrim = '" + common.replace(pt31cod_patrim, "'", "''") + "'";
		}

		//-- Check t26des_bien 
		pt33des_clasin = (String)p.get("p8");
		if (pt33des_clasin != null && ! pt33des_clasin.equals("")) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t33des_clasin like '%" + common.replace(pt33des_clasin, "'", "''") + "%'";
		}
		
		//-- Check t31cod_person
		pt31cod_person = (String)p.get("p7");
		if (pt31cod_person != null && ! pt31cod_person.equals("")) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t31cod_person = '" + common.replace(pt31cod_person, "'", "''") + "'";
		}

		if (hasParam) { sWhere = " WHERE (" + sWhere + ")"; }
		// Build ORDER statement
		sOrder = " order by t30f_document Asc";

		// Build full SQL statement

		//sSQL = "select * from t30movbp ";
  
		sSQL = "SELECT FIRST 500 t30movbp.*, t31dmobp.t31cod_patrim, t31dmobp.t31ind_conser, t33gcpat.t33des_clasin "+
		  "FROM (t30movbp "+
		  "LEFT JOIN t31dmobp ON (t30movbp.t30n_document = t31dmobp.t31n_document) AND (t30movbp.t30cod_movimi = t31dmobp.t31cod_movimi)) "+
		  "LEFT JOIN t33gcpat ON t31dmobp.t31cod_patrim[1,8] = t33gcpat.t33cod_patrim";

		sSQL = sSQL + sWhere + sOrder;
		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();
		selectStatement = sSQL;
 		
    try {

		// Cargar datos de la T12
		//T12DAO daot12 = new T12DAO();
    	T12DAO daot12 = new T12DAO("jdbc/dcsp");
		T01DAO daot01 = new T01DAO("jdbc/dcsa");

	conn = getConnection(datasource);
	pstmt = conn.prepareStatement(selectStatement);
	ResultSet rs = pstmt.executeQuery();
	log.info("T30DAO joinWithT30T31: "+sSQL);
	String[] aFields = common.getFieldsName(rs);

    while (rs.next()){

			String g = " ";
			if (rs.getDate("t30f_document") != null  )
				{BeanFechaHora fh = new BeanFechaHora(rs.getDate("t30f_document"));
				 g = fh.getFormatDate("dd/MM/yyyy");
				}
			java.util.Hashtable rsHash = new java.util.Hashtable();
			common.getRecordToHash(rs, rsHash, aFields);
			String fldt30f_document = (String) rsHash.get("t30f_document");
			String fldt30cod_movimi = (String) rsHash.get("t30cod_movimi");
			String fldt30cod_uorgor = (String) rsHash.get("t30cod_uorgor");
			String fldt30cod_uorgde = (String) rsHash.get("t30cod_uorgde");
			
			String fldt30cod_locaor = (String) rsHash.get("t30cod_locaor");
			String fldt30n_pisoorig = (String) rsHash.get("t30n_pisoorig");
			String fldt30cod_locade = (String) rsHash.get("t30cod_locade");
			String fldt30n_pisodest = (String) rsHash.get("t30n_pisodest");
					
			HashMap a = null;
			if (fldt30cod_movimi != null &&  fldt30cod_movimi.trim() !="") 
			{	a = daot01.findByClave(pool1, "83", fldt30cod_movimi.trim());}
			 
			HashMap b = null;
			//b = daot12.findByCodUorga(pool2,fldt30cod_uorgor.trim());
			b = (HashMap)daot12.findByCodUO(fldt30cod_uorgor.trim());

			HashMap c = null;
			c = daot01.findByClave(pool1, "05", fldt30cod_locaor.trim());
			
			HashMap d = null;
			//d = daot12.findByCodUorga(pool2,fldt30cod_uorgde.trim());
			d = (HashMap)daot12.findByCodUO(fldt30cod_uorgde.trim());
			
			HashMap e = null;
			e = daot01.findByClave(pool1, "05", fldt30cod_locade.trim());

			rsHash.put("t30f_document", g);
			rsHash.put("t30cod_movimi_desc", a==null?" ":((String)a.get("t01des_corta")).trim() );
   			//rsHash.put("t30cod_uorgor_desc", b==null?" ":((String)b.get("t12des_corta")).trim());
			rsHash.put("t30cod_uorgor_desc", b==null?" ":((String)b.get("t12des_uorga")).trim());
			rsHash.put("t30cod_locaor_desc", c==null?" ":"Sede:"+((String)c.get("t01des_corta")).trim());
			rsHash.put("t30n_pisoorig", "Piso:"+fldt30n_pisoorig);
			//rsHash.put("t30cod_uorgde_desc", d==null?" ":((String)d.get("t12des_corta")).trim());
			rsHash.put("t30cod_uorgde_desc", d==null?" ":((String)d.get("t12des_uorga")).trim());
			rsHash.put("t30cod_locade_desc", e==null?" ":"Sede:"+((String)e.get("t01des_corta")).trim());
			rsHash.put("t30n_pisodest", "Piso:"+fldt30n_pisodest);
			aLRpta.add(rsHash);
       }
    }
    catch (Exception e) {throw new IncompleteConversationalState(e.getMessage());}
		finally 
		{
			try {pstmt.close();}
			catch (Exception e) {}

			try	{conn.close();}
			catch (Exception e) {}
		}
		return aLRpta;
	}
}