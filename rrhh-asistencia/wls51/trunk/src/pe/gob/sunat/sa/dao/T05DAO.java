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
import pe.gob.sunat.sol.dao.DAOAccesoBD;
/**
 * @author WDELGADO
 *
 * DAO de la tabla t05karde
 * 
 */
public class T05DAO extends DAOAbstract {

	private DataSource datasource;
	
	public T05DAO() {
	    super();
	}
	
	/**
	 * @param datasource Object
	*/
	public T05DAO(Object datasource) {
		if (datasource instanceof DataSource)
			this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	    	this.datasource = getDataSource((String)datasource);
	    else
	    	throw new DAOException(this, "Datasource no valido");
	}
 	
  /**
   * Método findByQueryGroup
   * @param HashMap p
   * @param String t05oficina
   * @return ArrayList aLRpta
   * @throws IncompleteConversationalState
   */
	public ArrayList findByQueryGroup(HashMap p, String t05oficina)
     throws IncompleteConversationalState {
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL="";
		String pool = (String)p.get("p1");
		String anno = (String)p.get("p4");

		String pt05anno="";
		String pt05mes="";
		String pt05cod_unipro="";
		String pt05cod_movimi="";

		// Build WHERE statement
		//-- Check t05anno 

		pt05anno = (String)p.get("p4");
		if (pt05anno != null && ! pt05anno.equals("")) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "Year(t05f_operacio) = '" + common.replace(pt05anno, "'", "''") + "' ";
		}

		//-- Check t05oficina

		pt05cod_unipro = t05oficina;
		if (pt05cod_unipro != null && ! pt05cod_unipro.equals("")) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t05cod_unipro = '" + common.replace(pt05cod_unipro, "'", "''") + "' ";
		}
		//-- Check t05cod_movimi

		pt05cod_movimi = (String)p.get("p7");
		if (pt05cod_movimi != null && ! pt05cod_movimi.equals("")) {
			if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t05cod_movimi = '" + common.replace(pt05cod_movimi, "'", "''") + "' ";
		}

		if (hasParam) { sWhere = " WHERE (" + sWhere + ")"; }
		// Build ORDER statement
			sOrder = " order by t05cod_bien Asc ";

        // Build full SQL statement
 
		sSQL = "SELECT t05karde.t05cod_unipro, t05karde.t05cod_bien, t02cbien.t02des_bien, t02cbien.t02med_transf "+
		"FROM t05karde LEFT JOIN t02cbien ON t05karde.t05cod_bien = t02cbien.t02cod_bien ";
		sSQL = sSQL + sWhere;
		sSQL = sSQL + " GROUP BY t05karde.t05cod_unipro, t05karde.t05cod_bien, t02cbien.t02des_bien, t02cbien.t02med_transf ";
		sSQL = sSQL +  sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();
		selectStatement = sSQL;
		log.info("T05DAO findByQueryGroup: "+sSQL);
 		
		try {
			// Cargar datos de la T05
		//aLRpta = (ArrayList)executeQuery(datasource, sSQL.toString());
		//conn = getConnection((DataSource)p.get("p1"));
			conn = getConnection(datasource);
		pstmt = conn.prepareStatement(selectStatement);
		ResultSet rs = pstmt.executeQuery();
		String[] aFields = common.getFieldsName(rs);

		while (rs.next()){
			java.util.Hashtable rsHash = new java.util.Hashtable();
			common.getRecordToHash(rs, rsHash, aFields);
			String fldt05cod_bien = (String) rsHash.get("t05cod_bien");
	
			HashMap h01 = null;
			HashMap h02 = null;
			HashMap h03 = null;
			HashMap h04 = null;
			HashMap h05 = null;
			HashMap h06 = null;
			HashMap h07 = null;
			HashMap h08 = null;
			HashMap h09 = null;
			HashMap h10 = null;
			HashMap h11 = null;
			HashMap h12 = null;
			if (fldt05cod_bien != null && fldt05cod_bien != "" ) 
			{	
			h01 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"1");
			h02 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"2");
			h03 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"3");
			h04 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"4");
			h05 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"5");
			h06 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"6");
			h07 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"7");
			h08 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"8");
			h09 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"9");
			h10 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"10");
			h11 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"11");
			h12 = findByGroupMes(pool,"P", t05oficina, fldt05cod_bien ,anno,"12");
			}
	
			rsHash.put("ene", (String)h01.get("tcantidad"));
			rsHash.put("feb", (String)h02.get("tcantidad"));
			rsHash.put("mar", (String)h03.get("tcantidad"));
			rsHash.put("abr", (String)h04.get("tcantidad"));
			rsHash.put("may", (String)h05.get("tcantidad"));
			rsHash.put("jun", (String)h06.get("tcantidad"));
			rsHash.put("jul", (String)h07.get("tcantidad"));
			rsHash.put("ago", (String)h08.get("tcantidad"));
			rsHash.put("set", (String)h09.get("tcantidad"));
			rsHash.put("oct", (String)h10.get("tcantidad"));
			rsHash.put("nov", (String)h11.get("tcantidad"));
			rsHash.put("dic", (String)h12.get("tcantidad"));
			float t1t = Float.parseFloat((String)h01.get("tcantidad"))+Float.parseFloat((String)h02.get("tcantidad"))+Float.parseFloat((String)h03.get("tcantidad"));
			float t2t = Float.parseFloat((String)h04.get("tcantidad"))+Float.parseFloat((String)h05.get("tcantidad"))+Float.parseFloat((String)h06.get("tcantidad"));
			float t3t = Float.parseFloat((String)h07.get("tcantidad"))+Float.parseFloat((String)h08.get("tcantidad"))+Float.parseFloat((String)h09.get("tcantidad"));
			float t4t = Float.parseFloat((String)h10.get("tcantidad"))+Float.parseFloat((String)h11.get("tcantidad"))+Float.parseFloat((String)h12.get("tcantidad"));
			
			int tt = new Float(t1t+t2t+t3t+t4t).intValue();
			
			String tAnual = ""+tt;
			rsHash.put("tAnual", tAnual);
			aLRpta.add(rsHash);
		}
		} catch (Exception e) {
			throw new IncompleteConversationalState(e.getMessage());
		} finally {
			try {pstmt.close();}
			catch (Exception e) {}

			try	{conn.close();}
			catch (Exception e) {}
		}
		return aLRpta;
	}
	
  /**
   * Método findByGroupMes
   * @param String dbpool_sa
   * @param String t05cod_movimi
   * @param String t05oficina
   * @param String t05cod_bien
   * @param String anno
   * @param String mes
   * @return HashMap hMap
   * @throws IncompleteConversationalState
   */
  public HashMap findByGroupMes(String dbpool_sa, String t05cod_movimi, String t05oficina, String t05cod_bien , String anno, String mes)
		throws IncompleteConversationalState
	{
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL="";


		String pt05cod_movimi="";
		String pt05oficina="";
		String pt05cod_bien="";
		String panno="";
		String pmes="";

		// Build WHERE statement


		//-- Check t05cod_movimi
		pt05cod_movimi = t05cod_movimi;
		if (pt05cod_movimi != null && ! pt05cod_movimi.equals("")) {

		  if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t05cod_movimi = '" + common.replace(pt05cod_movimi, "'", "''") + "' ";
		}

		//-- Check t05oficina
		pt05oficina = t05oficina;
		if (pt05oficina != null && ! pt05oficina.equals("")) {

		  if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t05cod_unipro = '" + common.replace(pt05oficina, "'", "''") + "' ";
		}

		//-- Check t05cod_bien
		pt05cod_bien = t05cod_bien;
		if (pt05cod_bien != null && ! pt05cod_bien.equals("")) {

		  if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "t05cod_bien = '" + common.replace(pt05cod_bien, "'", "''") + "' ";
		}

		//-- Check anno
		panno = anno;
		if (panno != null && ! panno.equals("")) {

		  if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "Year(t05f_operacio) = '" + common.replace(panno, "'", "''") + "' ";
		}

		//-- Check mes
		pmes = mes;
		if (pmes != null && ! pmes.equals("")) {

		  if (! sWhere.equals("")) sWhere += " and ";
			hasParam = true;
			sWhere += "Month(t05f_operacio) = " + common.replace(pmes, "'", "''") + " ";
		}

		if (hasParam) { sWhere = " WHERE (" + sWhere + ")"; }
		// Build ORDER statement
		sOrder = " ";

	  // Build full SQL statement

		sSQL = "SELECT Sum(t05c_movimie1+t05c_movimie2) AS cantidad " +
		"FROM t05karde ";

		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		HashMap hMap = new HashMap();

		selectStatement = sSQL;
  		
		try 
		{
			conn = getConnection(datasource);
			pstmt = conn.prepareStatement(selectStatement);
			ResultSet rs = pstmt.executeQuery();
			String[] aFields = common.getFieldsName(rs);
			  	
			while (rs.next())
			{
				java.util.Hashtable rsHash = new java.util.Hashtable();
				common.getRecordToHash(rs, rsHash, aFields);
				String t = (String) rsHash.get("cantidad");
				String tcantidad = "0";
				if ( t!="" ) 
				{
				float t1 = Float.parseFloat(t);	
				int t2 = new Float(t1).intValue();
				tcantidad = ""+t2;
				}
				hMap.put("tcantidad",tcantidad);
			}
		}
		catch (Exception e) { throw new IncompleteConversationalState(e.getMessage()); }
		finally 
		{
			try {pstmt.close();}
			catch (Exception e) {}

			try	{conn.close();}
			catch (Exception e) {}
		}
		return hMap;
	}


  /**
   * Método findByDocumento
   * @param HashMap p
   * @return ArrayList aLRpta
   * @throws IncompleteConversationalState
   */
  /*No se subio anteriormente al Repositorio creado por Rocio Serrano para el Modulo Web de Precintos*/
  public ArrayList findByDocumento(HashMap p)
    throws IncompleteConversationalState
  {
    Utilidades common = new Utilidades();
    String sWhere = "";
    boolean hasParam = false;
    String sOrder = "";
    String sSQL="";
    String pool = (String)p.get("pool");
    String cod_movimi = (String)p.get("cod_movimi");
    String n_document = (String)p.get("n_document");

//  t05cod_movimi='P' y t05n_document=Nro. de Orden de Salida

  // Build WHERE statement
  //-- Check t05cod_movimi
	  if (cod_movimi != null && ! cod_movimi.equals("")) 
	  {
		  if (! sWhere.equals("")) sWhere += " and ";
		  hasParam = true;
		  sWhere += " t05cod_movimi = '".concat(cod_movimi).concat("' ");
	  }

//-- Check t05n_document
    if (n_document != null && ! n_document.equals("")) {

      if (! sWhere.equals("")) sWhere += " and ";
        hasParam = true;
        sWhere += " t05n_document = '".concat(n_document).concat("' ");
    }

 

  // Build full SQL statement
 
	sSQL = "select t05cod_almace , t05cod_movimi , t05cod_docume , t05n_document ,t05cod_unipro , ".concat(
           " t05cod_camope , t05f_operacio , t05n_itemdocu ,t05cod_bien ,t05c_document ,").concat(
           " t05c_movimie1 from  t05karde   where ").concat(sWhere);

	PreparedStatement pstmt = null;
	String selectStatement = null;
    Connection conn = null;
    ArrayList aLRpta = new ArrayList();
    selectStatement = sSQL;
 		
    try {
		// Cargar datos de la T05
	conn = getConnection(datasource);
	pstmt = conn.prepareStatement(selectStatement);
	ResultSet rs = pstmt.executeQuery();
	String[] aFields = common.getFieldsName(rs);

		while (rs.next())
		{
			java.util.Hashtable rsHash = new java.util.Hashtable();
			common.getRecordToHash(rs, rsHash, aFields);
			aLRpta.add(rsHash);
		}
	}
    catch (Exception e) {
    	e.printStackTrace(); //VCA-22032007
    	throw new IncompleteConversationalState(e.getMessage());}
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