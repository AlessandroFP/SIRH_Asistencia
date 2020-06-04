package pe.gob.sunat.rrhh.asistencia.dao;

import java.util.Map;

import javax.sql.DataSource;

import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import pe.gob.sunat.framework.util.Propiedades;
import pe.gob.sunat.framework.util.date.FechaBean;
 
/**
 * <p>Title: DAO que se encarga de modificar los registros de la tabla T1962autoriza</p>
 * <p>Description : Accede a la tabla T1962autoriza</p>
 * <p>Proyecto    : rrhh-autorizaciones</p>
 * <p>Clase       : T1962DAO</p>
 * <p>Fecha       : 25 de Junio 2008</p>
 * <p>Copyright   : Copyright (c) 2000-2008</p>
 * <p>Company     : SUNAT</p>
 * @author SOFIA CHAVEZ (COMSA)
 * @version 1.0
*/

public class T1962DAO extends DAOAbstract{
	
	private DataSource datasource;	
	Propiedades constantes = new Propiedades(getClass(), "/constantes.properties");
	
	
		
	private StringBuffer QUERY_SENTENCE1 = new StringBuffer("UPDATE ")
	.append(" t1962autoriza SET cod_tipo = ? , ind_estado = ? , ")
	.append(" cod_usumodif = ? , fec_modif = ?  where cod_personal = ? ");	
	
	private StringBuffer QUERY_SENTENCE2 = new StringBuffer("UPDATE ")
	.append(" t1962autoriza SET cod_tipo = ? , ind_estado = ? , ")
	.append(" cod_usumodif = ? , fec_modif = ?  where fec_crea >= ? ")
	.append(" and fec_crea <= ? ");	
	
	
	
	
	/**
   * 
   * Este es el constructor del DAO que se utiliza para crear el datasource
   * dependiendo del tipo de objeto que recibe. 
   *  
   * @param datasource Object
   *
  */  
    
  public T1962DAO(Object datasource) {		
    if (datasource instanceof DataSource) {
      this.datasource = (DataSource)datasource;
  	} else if (datasource instanceof String){
      this.datasource = getDataSource((String)datasource);
    } else {
      throw new DAOException(this, "Datasource no valido");
    }
  }
  
  /**
   * 
   * Modifica registros de la tabla T1962autoriza del personal que tengan vacaciones 
   * efectivas a la fecha en que se corre el proceso.
   * @param codigo String
   * @throws DAOException
   */  
  public void bloquearXVacaciones(Map codigo) throws DAOException {  	
  	Object objs[] = { "V",constantes.leePropiedad("INACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }
    
  /**
   * 
   * Modifica registros de la tabla T1962autoriza del personal que tengan una 
   * licencia = 008 a la fecha en que se corre el proceso.
   * @param codigo String
   * @throws DAOException
   */
  
  public void bloquearXSuspensiones(Map codigo) throws DAOException {  	
  	Object objs[] = {"S",constantes.leePropiedad("INACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }
  
  /**
   * 
   * Modifica registro de la tabla T1962autoriza del personal que tengan una 
   * licencia = 008 dentro de los 3 dias previos a la fecha en que se
   * corre el proceso.
   * @param codigo String
   * @throws DAOException
   */  
  public void desbloquearFinSuspensiones(Map codigo) throws DAOException {
  	Object objs[] = {"M",constantes.leePropiedad("ACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }

  /**
   * 
   * Modifica registro de la tabla T1962autoriza del personal que hayan dejado de
   * laborar en la institución dentro de los 3 dias previos a la fecha en
   * que se corre el proceso.
	 * @param codigo String
	 * @throws DAOException
	 */
  
  public void bloquearCesados(Map codigo) throws DAOException { 
  	Object objs[] = {"F",constantes.leePropiedad("INACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("t02cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }
  
 
  /**
   * 
   * Modifica registro de la tabla T1962autoriza del personal hayan tenido vacaciones
   * efectivas dentro de los 3 dias previos a la fecha en que corre el proceso.
   * @param codigo
   * @throws DAOException
   */
  public void desbloquearFinVacaciones(Map codigo) throws DAOException {  	
  	Object objs[] = {"M",constantes.leePropiedad("ACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }
 
  /**
   * 
   * Modifica registro de la tabla T1962autoriza del personal que tengan una licencia 010
   * a la fecha en que se corre el proceso.
   * @param codigo String
	 * @throws DAOException
	 */ 
  
  public void bloquearExoneraciones(Map codigo) throws DAOException {  	
  	Object objs[] = {"E",constantes.leePropiedad("INACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }
 
  /**
   * 
   * Modifica registro de la tabla T1962autoriza del personal que hayan tenido vacaciones 
   * efectivas dentro de los 3 dias previos a la fecha en que se corre el proceso.
   * @param codigo String
   * @throws DAOException
   */
  
  public void desbloquearFinExoneraciones(Map codigo) throws DAOException {  	
  	Object objs[] = {"M",constantes.leePropiedad("ACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }
    
  /**
   * 
   * Modifica registro de la tabla T1962autoriza cuya fecha de creación este dentro de 
   * los 3 dias previos a la fecha en que se corre el proceso.
   * @param codigo String
   * @throws DAOException
   */ 
  
  public void desbloquearNuevos(Map codigo) throws DAOException {  	
  	Object objs[] = {"N",constantes.leePropiedad("ACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("fechaFin"),codigo.get("fechaIni")};
  	executeUpdate(datasource,QUERY_SENTENCE2.toString(), objs); 	
  }
  
  /* ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
  /**
   * 
   * Modifica registros de la tabla T1962autoriza del personal que TENGA vacaciones 
   * programadas activas a la fecha actual en que se corre el proceso.
   * @param codigo Map
   * @throws DAOException
   */  
  public void bloquearXVacacionesProgramadas(Map codigo) throws DAOException {  	
  	Object objs[] = { "P",constantes.leePropiedad("INACTIVO"),codigo.get("cod_usumodif").toString(),
  			new FechaBean().getTimestamp(),codigo.get("cod_pers").toString()};
  	executeUpdate(datasource,QUERY_SENTENCE1.toString(), objs); 	
  }  
  /* FIN ICAPUNAY - AOM 45U3T10: ALERTA DE VACACIONES PARA TRABAJADORES (BLOQUEO DE LECTURA DE FOTOCHECK) - 07/04/2011 */
}
