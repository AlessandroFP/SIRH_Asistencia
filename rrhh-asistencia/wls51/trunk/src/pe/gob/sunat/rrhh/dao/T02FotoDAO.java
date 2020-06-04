package pe.gob.sunat.rrhh.dao;


/**
 * <p> Title : T02FotoDAO</p>
 * <p>Description : Realiza operaciones sobra la tabla T02FOTOS </p>
 * <p>Copyright   : Copyright (c) 2007</p>
 * <p>Company     : COMSA </p>
 * @author J.Callo 
 * @version 1.0 
 */


import java.util.Map;
import pe.gob.sunat.framework.core.bean.MensajeBean;
import pe.gob.sunat.framework.core.dao.DAOAbstract;
import pe.gob.sunat.framework.core.dao.DAOException;
import javax.sql.DataSource;




public class T02FotoDAO extends DAOAbstract{
 
 
   /** Datasource del DAO */
   private DataSource datasource;
   /** Query para consulta la Foto de la BD */
   private static String qry_foto = "SELECT " 
         .concat("t02foto FROM t02fotos WHERE t02cod_pers = ?");
   private static final String INSERT_SENTENCE = "insert " 
         .concat("into T02FOTOS values (?,?,?,?)");
   private static final String SELECT_SENTENCE_T02COD_PERS = " SELECT " 
     .concat(" T02COD_PERS FROM T02FOTOS WHERE T02COD_PERS=?");
   private static final String UPDATE_SENTENCE = " UPDATE " 
         .concat("T02FOTOS SET  T02FOTO = ? , T02F_GRABA = ?,T02COD_USER = ?  " )
         .concat("WHERE  T02COD_PERS= ? ");
     private String DELETE_SENTENCE = " DELETE FROM T02FOTOS  WHERE T02COD_PERS = ? ";
   
   
 
 public T02FotoDAO(String jndi) {  
   datasource = getDataSource(jndi);  
 }
 
 /**
   * Carga el datasource en el DAO
   * @param datasource DataSource
   */
 public T02FotoDAO(DataSource datasource){
   this.datasource = datasource;
 }
  
   
 /**
  * Extrae la foto de un trabajador como un archivo .tmp de la tabla t02fotos hacia la carpeta /data0/
  * @param cod_pers String             Nro de Registro
  * @return Map que contiene el nombre del archivo temporal
  * @exception DAOException 
  */
 public Map cargar(String cod_pers) throws DAOException {

   MensajeBean msg = new MensajeBean();
   Map mapa = null;
   try {
      mapa = executeQueryUniqueResult(datasource, qry_foto, new Object[]{cod_pers});
   } catch (Exception e) {
      msg.setMensajeerror("Ha ocurrido un error al cargar la foto del"
         .concat(" trabajador : ").concat(e.getMessage()));
      msg.setMensajesol("Por favor, intente nuevamente realizar la "
         .concat("operacion, de continuar con el problema ")
         .concat("comuniquese con el webmaster"));
      throw new DAOException (this, msg);
     }
     return mapa;
 }
   

 /**
  * Metodo encargado de elminar la foto
  * @param params Map
  * @throws DAOException
  */
 public void eliminaFoto (Map params) throws DAOException {
   Object[] o = new Object[] { params.get("cod_registro")}; 
   executeUpdate(datasource, DELETE_SENTENCE, o);
  
 }
 
 /**
  * Metodo encargargado de actualizar la foto
  * @param params Map
  * @throws DAOException
  */
 public void actualizaFoto (Map params) throws DAOException {
   Object[] o = new Object[] {
               params.get("arc_foto"),params.get("fec_graba"), 
               params.get("usuario"),params.get("per_codigo"), 
   }; 
   executeUpdate(datasource, UPDATE_SENTENCE, o);

  }
 /**
  * Metodo encargado de registrar una nueva foto
  * @param params Map
  * @throws DAOException
  */
 public void registraFoto (Map params) throws DAOException { 
	 
	 MensajeBean msg = new MensajeBean(); 
	 try {
		 //EBV 25/05/2009 Estandares
//		 if(buscarSiExisteFoto(params)){
//			 actualizaFoto(params);
//		 } else
//		 {
			 Object[] o = new Object[] { 
					 params.get("per_codigo"), params.get("arc_foto"), 
					 params.get("fec_graba"), params.get("usuario") 
			 }; 
			 insert(datasource, INSERT_SENTENCE, o); 
//		 }
	 } catch (DAOException e) { 
		 msg.setMensajeerror("Ha ocurrido un error al intentar grabar " 
				 .concat("la informacion : ").concat( e.getMessage() )); 
		 msg.setMensajesol("Por favor, intente nuevamente realizar la operacion," 
				 .concat(" de continuar con el problema comuniquese con el webmaster"));
		 throw new DAOException (this, msg); 
	 }   
 } 
 
 /**
  * Metodo encargado de verificar la existencia de una foto segun el codigo de persona
  * @param params Map 
  * @return boolean
  * @throws DAOException
  */
 public boolean buscarSiExisteFoto(Map params)throws DAOException {
	 boolean existeFoto=false;
	 try{ 
		 Map dato = executeQueryUniqueResult(datasource, SELECT_SENTENCE_T02COD_PERS.toString(),new Object[]{(String)params.get("per_codigo")});
		 if (dato!=null && dato.get("t02cod_pers")!=null) {
			 existeFoto=true;
		 } else {
			 existeFoto=false;
		 }
		 return existeFoto;
	 }
	 catch (Exception e) {
		 if (log.isDebugEnabled()) {
			 log.debug("Se produjo una excepcion");
		 }
	 }
	 return existeFoto;
 }

}
