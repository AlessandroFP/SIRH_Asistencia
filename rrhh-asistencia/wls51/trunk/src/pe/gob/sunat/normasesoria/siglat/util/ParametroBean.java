package pe.gob.sunat.normasesoria.siglat.util;
    
import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: ParametrosConsultaBean</p> 
 * <p>Descripction: Bean para establecer parámetros de búsqueda</p> 
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: System Database S. A.</p>
 * @author System Database S.A.
 * @version 1.0
 */ 
public class ParametroBean implements Serializable, Comparable {
	private static final long serialVersionUID = 1L;
	private String criterio = null; // criterio de búsqueda
	private String valor = null; // valor de la búsqueda
	private String operador = null; // operador de la búsqueda
	private String tipoDato = null; // tipo de dato
	private final String IGUAL = "=";	
	private final String LIKE = "LIKE";	
	private final String DIFERENTE = "<>";	
	private final String IN = "IN";
	private final String IS = "IS";
	//private final String OR = "OR";
	//private final String AND = "AND";	
	private final String TIPO_NUMERO = "NUMERO";	
	private final String TIPO_TEXTO = "TEXTO";	
	private final String TIPO_FECHAHORA = "FECHAHORA";	
	private final String TIPO_FECHA = "FECHA";	
	private final String TIPO_HORA = "HORA";	
	
	/**
	 * Método para obtener criterio
	 * @return String criterio
	 */
	public String getCriterio() {
		return criterio;
	}

	/**
	 * Método para establecer el criterio
	 * @param criterio String
	 */
	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	/**
	 * Método para obtener el valor
	 * @return String valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * Método para establecer el valor
	 * @param codSectorEmisor String
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * Método para obtener el operador
	 * @return
	 */
	public String getOperador() {
		return operador;
	}

	/**
	 * Método para establecer el operador
	 * @param operador
	 */
	public void setOperador(String operador) {
		this.operador = operador;
	}

	/**
	 * Método para obtener el operador IGUAL QUE
	 * @return
	 */
	public String getIGUAL() {
		return IGUAL;
	}

	/**
	 * Método para obtener el operador LIKE
	 * @return
	 */
	public String getLIKE() {
		return LIKE;
	}
	
	/**
	 * Método para obtener el operador DIFERENTE DE
	 * @return
	 */
	public String getDIFERENTE() {
		return DIFERENTE;
	}

	/**
	 * Método para obtener el tipo de dato del valor
	 * @return
	 */
	public String getTipoDato() {
		return tipoDato;
	}

	/**
	 * Método para establecer el tipo de dato del valor
	 * @param tipoDato
	 */
	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}

	/**
	 * Método para obtener el tipo FECHAHORA
	 * @return
	 */
	public String getTIPO_FECHAHORA() {
		return TIPO_FECHAHORA;
	}

	/**
	 * Método para obtener el tipo NUMERO
	 * @return
	 */
	public String getTIPO_NUMERO() {
		return TIPO_NUMERO;
	}

	/**
	 * Método para obtener el tipo TEXTO
	 * @return
	 */
	public String getTIPO_TEXTO() {
		return TIPO_TEXTO;
	}

	/**
	 * Método para obtener el tipo FECHA
	 * @return
	 */
	public String getTIPO_FECHA() {
		return TIPO_FECHA;
	}

	/**
	 * Método para obtener el tipo HORA
	 * @return
	 */
	public String getTIPO_HORA() {
		return TIPO_HORA;
	}
	
	/**
	 * Método para obtener el operador IN
	 * @return String operador IN
	 */
	public String getIN() {
		return IN;
	}
	
	/**
	 * Método para obtener el operador IS
	 * @return String operador IS
	 */
	public String getIS() {
		return IS;
	}
	
	public boolean equals(Object obj){
		if (!(obj instanceof ParametroBean))
		return false;
		ParametroBean tmp = (ParametroBean)obj;
		return (this.getCriterio().trim()
		.equals(tmp.getCriterio().trim()));
	}
	
	public int compareTo(Object obj) {
		if (!(obj instanceof ParametroBean))
		throw new ClassCastException("Valor invalido");
		ParametroBean tmp = (ParametroBean)obj;
		return (this.getValor()
		.compareTo(tmp.getValor()));
	}
	
	/**
	 * Método para adicionar una clausula WHERE a una sentencia SQL.
	 * @return
	 */
	public StringBuffer addWhere(){
		StringBuffer where = new StringBuffer();
		if (tipoDato == getTIPO_NUMERO()){
			if (getOperador() == getIN()){
				where.append(" (")
				.append(getCriterio())
				.append(" ")
				.append(getOperador())
				.append(" (")
				.append(getValor())
				.append(")) ");
			}else{
			where.append(" (");
			where.append(getCriterio())
			.append(" ")
			.append(getOperador())
			.append(" ")
			.append(getValor())
			.append(") ");
			}
		}
		if (tipoDato == getTIPO_TEXTO() || tipoDato == null){
			if (getOperador() == getLIKE()){
				where.append(" (UPPER(")
				.append(getCriterio())
				.append(") ")
				.append(getOperador())
				.append(" UPPER('%")
				.append(getValor())
				.append("%')) ");
			}else if(getOperador() == getIN()){
				where.append(" (")
				.append(getCriterio())
				.append(" ")
				.append(getOperador())
				.append(" (")
				.append(getValor())
				.append(")) ");
			}else{
				where.append(" (UPPER(")
				.append(getCriterio())
				.append(") ")
				.append(getOperador())
				.append(" UPPER('")
				.append(getValor())
				.append("')) ");
			}
		}
		return where;
	}
	
	public String getSerie(Object[] obj){
		String serie = "";
		if (tipoDato == getTIPO_NUMERO()){
			for(int i = 0 ; i < obj.length; i++){
				if(serie.equals("")){
					serie = (String) obj[i].toString();
				}else{
					serie = serie + ", " + (String) obj[i].toString();
				}
			}
		}
		if (tipoDato == getTIPO_TEXTO() || tipoDato == null){
			for(int i = 0 ; i < obj.length; i++){
				if(serie.equals("")){
					serie = "'" + (String) obj[i].toString() + "'";
				}else{
					serie = serie + ", '" + (String) obj[i].toString() + "'";
				}
			}
		}
		return serie;
	}
	
	public String getSerie(List lista){
		String serie = "";
		if (tipoDato == getTIPO_NUMERO()){
			for(int i = 0 ; i < lista.size(); i++){
				if(serie.equals("")){
					serie = (String) lista.get(i).toString();
				}else{
					serie = serie + ", " + (String) lista.get(i).toString();
				}
			}
		}
		if (tipoDato == getTIPO_TEXTO() || tipoDato == null){
			for(int i = 0 ; i < lista.size(); i++){
				if(serie.equals("")){
					serie = "'" + (String) lista.get(i).toString() + "'";
				}else{
					serie = serie + ", '" + (String) lista.get(i).toString() + "'";
				}
			}
		}
		return serie;
	}
}
