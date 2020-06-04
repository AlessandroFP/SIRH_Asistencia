package pe.gob.sunat.rrhh.asistencia.dao;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.date.FechaBean;

public class T3464DAO extends DAOAbstract {
	
	private String QUERY_INSERT = "insert into t3464altalabor(cod_personal,fec_autoriza, cod_jefe, ind_estado, cod_user_crea, fec_crea)  " 
			+ " values ( ? , ? , ? , ? , ? , ? ) ";
	private String QUERY_EXIST = "SELECT cod_personal FROM t3464altalabor where cod_personal = ? and fec_autoriza = ? and ind_estado = ? ";
	private DataSource datasource;
			
	public T3464DAO() {
		super();
	}
	
	  /**
	   * 
	   * Este constructor del DAO dicierne como crear el datasource
	   * dependiendo del tipo de objeto que recibe. Esto nos ayuda a
	   * mejorar la invocacion del dao.
	   * 
	   * @param datasource Object
	   */
	  public T3464DAO(Object datasource) {
	    if (datasource instanceof DataSource)
	      this.datasource = (DataSource)datasource;
	    else if (datasource instanceof String)
	      this.datasource = getDataSource((String)datasource);
	    else
	      throw new DAOException(this, "Datasource no valido");
	  }


	/**
	 * Metodo que se encarga de Insertar las autorizaciones  
	 * de labor Excepcional
	 * @throws SQLException
	 */
	public boolean insertAltaLabor(String cod_pers, 
			String fecha, String jefe, String usuario)
	throws SQLException {
		
	int modifica =0;
			log.debug("QUERY "+QUERY_INSERT);
			FechaBean fecha1 = new FechaBean();
			FechaBean fecha2 = new FechaBean(fecha);
			modifica = executeUpdate(datasource, QUERY_INSERT, new Object[]{cod_pers, fecha2.getSQLDate(), jefe, "1", usuario, fecha1.getTimestamp()});

 
		return true;
	}
	
	/**
	 * Metodo que se encarga de Insertar las autorizaciones  
	 * de labor Excepcional
	 * @throws SQLException
	 */
	public boolean existeAutorizado(String cod_pers, 
			String fecha, Map seguridad)
	throws SQLException {
		
	List modifica =new ArrayList();

			log.debug("QUERY "+QUERY_EXIST);
			FechaBean fecha1 = new FechaBean(fecha);
			log.debug("FEcha "+fecha1.getSQLDate());
			modifica = executeQuery(datasource, QUERY_EXIST, new Object[]{cod_pers,fecha1.getSQLDate(),"1"});
 
			log.debug("modifica "+ modifica.size());
			if (modifica.size() ==0)
				//return true;
				//JRR - 01/07/2009
				return false;
			else
				//return false;
				return true;
	}
}