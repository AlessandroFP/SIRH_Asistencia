package pe.gob.sunat.sp.asistencia.ejb;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

import pe.gob.sunat.ejb.EJBEstandarLocalCMP;

/**
 * @ejb.bean name="T130CMP" cmp-version="2.x" description="Entidad de la tabla
 *           t130horext"
 *           local-jndi-name="ejb/rrhh/cmp/sp/asistencia/t130cmp" type="CMP"
 *           view-type="local" schema="T130Schema"
 * 
 * @ejb.home local-class="pe.gob.sunat.sp.asistencia.ejb.T130CMPHome"
 *           local-extends="javax.ejb.EJBLocalHome"
 * 
 * @ejb.interface local-class="pe.gob.sunat.sp.asistencia.ejb.T130CMPLocal"
 *                local-extends="javax.ejb.EJBLocalObject"
 * 
 * @ejb.transaction type="Required"
 * @ejb.transaction-type type="Container"
 * 
 * @ejb.persistence table-name="t130horext"
 * @ejb.pk class="pe.gob.sunat.sp.asistencia.ejb.T130CMPPK"
 *         extends="java.lang.Object" implements="java.io.Serializable"
 * 
 * @jboss.container-configuration name="Standard CMP 2.x EntityBean"
 * @jboss.persistence create-table="false" remove-table="false"
 *                    table-name="t130horext"
 * @jboss.version="3.2"
 * 
 * @weblogic.persistence create-table="false" remove-table="false"
 *                       table-name="t130horext"
 * @weblogic.cache max-beans-in-cache="300"
 * @weblogic.pool max-beans-in-free-pool="300" initial-beans-in-free-pool="0"
 * @weblogic.delay-database-insert-until ejbCreate
 * 
 * @weblogic.enable-call-by-reference True
 * @weblogic.clustering home-is-clusterable="True" stateless-bean-is-clusterable="True"
 * @weblogic.transaction-descriptor trans-timeout-seconds = "300"

 * @author CGARRATT
 */
abstract public class T130CMP extends EJBEstandarLocalCMP implements EntityBean {
	
	EntityContext entityContext;

	/**
	 * @ejb.create-method view-type="local"
	 * @param codPers
	 * @param fAutor
	 * @param hInic
	 * @param uo
	 * @param hFin
	 * @param hSalida
	 * @param obs
	 * @param codJefe
	 * @param estado
	 * @param fCreacion
	 * @param usuario
	 * @return
	 * @throws CreateException
	 */
	public T130CMPPK ejbCreate(String codPers, String fAutor, String hInic,
			String uo, String hFin, String hSalida, String obs, String codJefe,
			String estado, java.sql.Timestamp fCreacion, String usuario)
			throws CreateException {
		setCodPers(codPers);
		setFAutor(fAutor);
		setHInic(hInic);
		setCodUrga(uo);
		setHTerm(hFin);
		setHSalid(hSalida);
		setObserva(obs);
		setCodJefe(codJefe);
		setEstId(estado);
		setFcreacion(fCreacion);
		setCodUser(usuario);
		return null;
	}

	/**
	 * @ejb.create-method view-type="local"
	 * @param codPers
	 * @param fAutor
	 * @param hInic
	 * @return
	 * @throws CreateException
	 */
	public T130CMPPK ejbCreate(java.lang.String codPers,
			java.lang.String fAutor, java.lang.String hInic)
			throws CreateException {
		setCodPers(codPers);
		setFAutor(fAutor);
		setHInic(hInic);
		return null;
	}

	public void ejbPostCreate(String codPers, String fAutor, String hInic,
			String uo, String hFin, String hSalida, String obs, String codJefe,
			String estado, java.sql.Timestamp fCreacion, String usuario)
			throws CreateException {
		/** @todo Complete this method */
	}

	public void ejbPostCreate(java.lang.String codPers,
			java.lang.String fAutor, java.lang.String hInic)
			throws CreateException {
		/** @todo Complete this method */
	}

	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_pers" jdbc-type="VARCHAR"
	 */
	public abstract void setCodPers(java.lang.String codPers);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_autor" jdbc-type="VARCHAR"
	 */
	public abstract void setFAutor(java.lang.String fAutor);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_inic" jdbc-type="VARCHAR"
	 */
	public abstract void setHInic(java.lang.String hInic);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_urga" jdbc-type="VARCHAR"
	 */
	public abstract void setCodUrga(java.lang.String codUrga);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_term" jdbc-type="VARCHAR"
	 */
	public abstract void setHTerm(java.lang.String hTerm);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="observa" jdbc-type="VARCHAR"
	 */
	public abstract void setObserva(java.lang.String observa);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_jefe" jdbc-type="VARCHAR"
	 */
	public abstract void setCodJefe(java.lang.String codJefe);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_efect" jdbc-type="VARCHAR"
	 */
	public abstract void setFEfect(java.lang.String fEfect);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_salid" jdbc-type="VARCHAR"
	 */
	public abstract void setHSalid(java.lang.String hSalid);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="id_confir" jdbc-type="VARCHAR"
	 */
	public abstract void setIdConfir(java.lang.Short idConfir);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract void setEstId(java.lang.String estId);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract void setFcreacion(java.sql.Timestamp fcreacion);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_user" jdbc-type="VARCHAR"
	 */
	public abstract void setCodUser(java.lang.String codUser);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fmod" jdbc-type="TIMESTAMP"
	 */
	public abstract void setFmod(java.sql.Timestamp fmod);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser_mod" jdbc-type="VARCHAR"
	 */
	public abstract void setCuserMod(java.lang.String cuserMod);
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_pers" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getCodPers();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_autor" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getFAutor();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_inic" jdbc-type="VARCHAR"
	 * @ejb.pk-field
	 */
	public abstract java.lang.String getHInic();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_urga" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCodUrga();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_term" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getHTerm();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="observa" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getObserva();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_jefe" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCodJefe();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="f_efect" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getFEfect();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="h_salid" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getHSalid();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="id_confir" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.Short getIdConfir();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="est_id" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getEstId();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fcreacion" jdbc-type="TIMESTAMP"
	 */
	public abstract java.sql.Timestamp getFcreacion();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cod_user" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCodUser();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="fmod" jdbc-type="TIMESTAMP"
	 */
	public abstract java.sql.Timestamp getFmod();
	/**
	 * @ejb.interface-method view-type="local"
	 * @ejb.persistence column-name="cuser_mod" jdbc-type="VARCHAR"
	 */
	public abstract java.lang.String getCuserMod();

}