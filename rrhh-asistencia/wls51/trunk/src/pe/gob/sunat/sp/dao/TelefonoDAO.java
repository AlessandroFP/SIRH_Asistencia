package pe.gob.sunat.sp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import pe.gob.sunat.sol.IncompleteConversationalState;
import pe.gob.sunat.sol.dao.DAOAccesoBD;
import pe.gob.sunat.utils.Utilidades;

public class TelefonoDAO extends DAOAccesoBD {
  public TelefonoDAO() {}

	/**
	 * M�todo findByPers
	 * @param String dbpool
	 * @param String codReg
	 * @return ArrayList aLRpta
	 * @throws IncompleteConversationalState
	 */
	public ArrayList findByPers(String dbpool, String codReg)
	throws IncompleteConversationalState
	{
		Utilidades common = new Utilidades();
		String sWhere = "";
		boolean hasParam = false;
		String sOrder = "";
		String sSQL="";

		String pvalor="";

		// Build WHERE statement

		//-- Check valor
		pvalor = codReg;

			hasParam = true;
			sWhere += " cod_pers = '" + common.replace(pvalor, "'", "''") + "'";


		// Build full SQL statement
		if (hasParam) { sWhere = " WHERE (" + sWhere + ")"; }
		sOrder = " order by tip_linea ";

		sSQL = "SELECT cod_pers, tip_linea, numero, cod_uorgan, cod_local, piso from telefonos ";
		sSQL = sSQL + sWhere + sOrder;

		PreparedStatement pstmt = null;
		String selectStatement = null;
		Connection conn = null;
		ArrayList aLRpta = new ArrayList();

		selectStatement = sSQL;
  		
		try 
		{
			conn = getConnection(dbpool);
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
    catch (Exception e) {throw new IncompleteConversationalState(e.getMessage());}
		finally 
		{
			try {pstmt.close();}
			catch (Exception e) {}

			try {conn.close();}
			catch (Exception e) {}
		}
		return aLRpta;
	}
}
