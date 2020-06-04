package pe.gob.sunat.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
//JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES
import org.apache.log4j.Logger; 
import java.util.Map;
import pe.gob.sunat.framework.util.date.FechaBean;
//
import pe.gob.sunat.sol.BeanFechaHora;
import pe.gob.sunat.sp.dao.T99DAO;

/**
 * 
 * Clase       : Utiles 
 * Proyecto    : Asistencia 
 * Descripcion : Clase con metodos utiles para las aplicaciones 
 * Autor       : CGARRATT
 * Fecha       : 10-mar-2005 15:35:47
 */
public class Utiles {

	/** Creates a new instance of Utiles */
	private static final Logger log = Logger.getLogger(Utiles.class); //JVILLACORTA - 29/03/2011 - ALERTA DE SOLICITUDES
	public Utiles() {
	}

	/**
	 * Metodo encargado de devolver la fecha actual en formato dd/mm/yyyy
	 * 
	 * @return String
	 */
	public static String obtenerFechaActual() {

		BeanFechaHora bfh = new BeanFechaHora(new Date(System
				.currentTimeMillis()));
		String strFecha = bfh.getFormatDate("dd/MM/yyyy");
		return strFecha;
	}

	/**
	 * Metood encargado de devolver el aï¿½o actual en formato yyyy
	 * 
	 * @return String
	 */
	public static String obtenerAnhoActual() {

		BeanFechaHora bfh = new BeanFechaHora(new Date(System
				.currentTimeMillis()));
		String strFecha = bfh.getFormatDate("yyyy");
		return strFecha;

	}

	/**
	 * Metodo encargado de devolver una fecha en formato yyyymmddhhmmss
	 * 
	 * @param f
	 *            String Fecha origen
	 * @return String
	 */
	public static String toYYYYMMDDHHMMSS(String f) {

		try {

			BeanFechaHora bfh = new BeanFechaHora(f);
			return bfh.getFormatDate("yyyyMMddHHmmss");

		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Metodo encargado de devolver la fecha actual en formato yyyymmddhhmmss
	 * 
	 * @return String
	 */
	public static String toFechaActualSeguida() {

		try {

			BeanFechaHora bfh = new BeanFechaHora(new Date(System
					.currentTimeMillis()));
			return bfh.getFormatDate("yyyyMMddHHmmss");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Metodo encargado de devolver la fecha actual en formato yyyymmddhhmmss
	 * 
	 * @return String
	 */
	public static String toFechaSeguida(String f) {

		try {

			BeanFechaHora bfh = new BeanFechaHora(f);
			return bfh.getFormatDate("yyyyMMdd");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Metodo encargado de devolver la fecha actual en formato yyyymmddhhmmss
	 * 
	 * @return String
	 */
	public static String toHoraActualSeguida() {

		try {

			BeanFechaHora bfh = new BeanFechaHora(new Date(System
					.currentTimeMillis()));
			return bfh.getFormatDate("HHmmss");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Metodo encargado de devolver una fecha en formato YYYYMMDD
	 * 
	 * @param f
	 *            String Fecha origen
	 * @return String
	 */
	public static String formatoYYYYMMDD(String f) {

		try {

			BeanFechaHora bfh = new BeanFechaHora(f);
			return bfh.getFormatDate("yyyyMMdd");
		} catch (Exception e) {

			return "";
		}
	}

	/**
	 * Metodo encargado de devolver la hora actual
	 * 
	 * @return String
	 */
	public static String obtenerHoraActual() {

		BeanFechaHora bfh = new BeanFechaHora(new Date(System
				.currentTimeMillis()));
		return bfh.getFormatDate("HH:mm:ss");
	}

	/**
	 * Metodo encargado de devolver una fecha en formato YYYYMMDD
	 * 
	 * @param ddmmyyyy
	 *            String Fecha en formato DDMMYYYY
	 * @return String
	 */
	public static String toYYYYMMDD(String ddmmyyyy) {

		String cadena = "";
		try {

			BeanFechaHora bfh = new BeanFechaHora(ddmmyyyy, "dd/MM/yyyy");
			cadena = bfh.getFormatDate("yyyy/MM/dd");

		} catch (Exception e) {
			cadena = "";
		}
		return cadena;
	}

	/**
	 * Metodo encargado de convertir un java.sql.Date a String en formato
	 * DD/MM/YYYY
	 * 
	 * @param date
	 *            java.sql.Date Fecha origen en formato Date
	 * @return String
	 */
	public static String dateToString(java.sql.Date date) {
		String str = "";
		if (date != null) {
			BeanFechaHora bfh = new BeanFechaHora(date);
			str = bfh.getFormatDate("dd/MM/yyyy");
		}
		return str;
	}

	/**
	 * Metodo encargado de convertir un java.util.Date a String en formato
	 * DD/MM/YYYY
	 * 
	 * @param date
	 *            java.util.Date Fecha origen en formato Date
	 * @return String
	 */
	public static String dateToString(java.util.Date date) {
		String str = "";
		if (date != null) {
			BeanFechaHora bfh = new BeanFechaHora(new java.sql.Date(date
					.getTime()));
			str = bfh.getFormatDate("dd/MM/yyyy");
		}
		return str;
	}

	/**
	 * Metodo encargado de convertir un java.sql.Timestamp a String en formato
	 * DD/MM/YYYY
	 * 
	 * @param date
	 *            java.sql.Timestamp Fecha origen en formato Timestamp
	 * @return String
	 */
	public static String timeToFecha(java.sql.Timestamp date) {
		String str = "";
		if (date != null) {
			BeanFechaHora bfh = new BeanFechaHora(new java.sql.Date(date
					.getTime()));
			str = bfh.getFormatDate("dd/MM/yyyy");
		}
		return str;
	}

	/**
	 * Metodo encargado de convertir un java.sql.Timestamp a String en formato
	 * HH:mm:ss
	 * 
	 * @param date
	 *            java.sql.Timestamp Fecha origen en formato Timestamp
	 * @return String
	 */
	public static String timeToHora(java.sql.Timestamp date) {
		String str = "";
		if (date != null) {
			BeanFechaHora bfh = new BeanFechaHora(new java.sql.Date(date
					.getTime()));
			str = bfh.getFormatDate("HH:mm:ss");
		}
		return str;
	}

	/**
	 * Metodo encargado de convertir una String a un dato java.sql.Date
	 * 
	 * @param str
	 *            String Fecha origen
	 * @return Date
	 */
	public static java.util.Date stringToDate(String str) {

		java.util.Date date = null;
		try {
			if ((str != null) && (!str.trim().equals(""))) {
				BeanFechaHora bfh = new BeanFechaHora(str);
				date = new java.util.Date(bfh.getSQLDate().getTime());
			}
		} catch (Exception e) {
			
		}
		return date;
	}

	/**
	 * Metodo encargado de convertir una String a un dato java.sql.Timestamp
	 * 
	 * @param str
	 *            String Fecha origen
	 * @return Timestamp
	 */
	public static java.sql.Timestamp stringToTimestamp(String str) {

		java.sql.Timestamp tm = null;
		try {
			if ((str != null) && (!str.trim().equals(""))) {
				BeanFechaHora bfh = new BeanFechaHora(str,
						"dd/MM/yyyy HH:mm:ss");
				tm = new java.sql.Timestamp(bfh.getSQLDate().getTime());
			}
		} catch (Exception e) {
		}
		return tm;
	}

	/**
	 * Metodo encargado de devolver la cantidad de dias de diferencia entre dos
	 * fechas del tipo String en formato dd/mm/yyyy
	 * 
	 * @param fechaInicio
	 *            String
	 * @param fechaFin
	 *            String
	 * @return
	 */
	public static int obtenerDiasDiferencia(String fechaInicio, String fechaFin) {

		java.util.Date fIni = stringToDate(fechaInicio);
		java.util.Date fFin = stringToDate(fechaFin);

		long lFechaIni = fIni.getTime();
		long lFechaFin = fFin.getTime();
		int nDias = 0;

		try {
			long milisDia = 24 * 60 * 60 * 1000;
			Float fDias = Float.valueOf(""
					+ ((lFechaFin - lFechaIni) / milisDia));
			nDias = fDias.intValue();
		} catch (Exception e) {
			nDias = 0;
		}
		return nDias;
	}

	/**
	 * Metodo encargado de devolver la cantidad de horas de diferencia entre dos
	 * fechas del tipo Timestamp
	 * 
	 * @param fechaInicio
	 *            java.sql.Timestamp
	 * @param fechaFin
	 *            java.sql.Timestamp
	 * @return float
	 */
	public static float dameHorasDiferencia(java.sql.Timestamp fechaInicio,
			java.sql.Timestamp fechaFin) {

		long lFechaIni = fechaInicio.getTime();
		long lFechaFin = fechaFin.getTime();
		int nHoras = 0;

		try {
			long milisHora = 60 * 60 * 1000;
			Float fHoras = Float.valueOf(""
					+ ((lFechaFin - lFechaIni) / milisHora));
			nHoras = fHoras.intValue();
		} catch (Exception e) {
			nHoras = 0;
		}
		return nHoras;
	}

	/**
	 * Metodo encargado de devolver la cantidad de meses de diferencia entre dos
	 * fechas del tipo String en formato dd/mm/yyyy
	 * 
	 * @param fechaInicio
	 *            String
	 * @param fechaFin
	 *            String
	 * @return float
	 */
	public static float obtenerMesesDiferencia(String fechaInicio,
			String fechaFin) {

		java.util.Date fIni = stringToDate(fechaInicio);
		java.util.Date fFin = stringToDate(fechaFin);

		long lFechaIni = fIni.getTime();
		long lFechaFin = fFin.getTime();
		int nMeses = 0;

		try {
			long milisMes = 30 * 24 * 60 * 60 * 1000;
			Float fMeses = Float.valueOf(""
					+ ((lFechaFin - lFechaIni) / milisMes));
			nMeses = fMeses.intValue();
		} catch (Exception e) {
			nMeses = 0;
		}
		return nMeses;
	}

	/**
	 * Metodo encargado de devolver la cantidad de aï¿½os de diferencia entre
	 * dos fechas del tipo String en formato dd/mm/yyyy
	 * 
	 * @param fechaInicio
	 *            String
	 * @param fechaFin
	 *            String
	 * @return
	 */
	public static float obtenerAnnosDiferencia(String fechaInicio,
			String fechaFin) {

		java.util.Date fIni = stringToDate(fechaInicio);
		java.util.Date fFin = stringToDate(fechaFin);

		long lFechaIni = fIni.getTime();
		long lFechaFin = fFin.getTime();
		int nAnnos = 0;

		try {
			long milisAnno = 365 * 30 * 24 * 60 * 60 * 1000;
			Float fAnnos = Float.valueOf(""
					+ ((lFechaFin - lFechaIni) / milisAnno));
			nAnnos = fAnnos.intValue();
		} catch (Exception e) {
			nAnnos = 0;
		}
		return nAnnos;
	}

	/**
	 * Metodo encargado de devolver una fecha despues de una cantidad n de dias
	 * 
	 * @param f1
	 *            String
	 * @param dias
	 *            int
	 * @return String
	 */
	public static String dameFechaSiguiente(String f1, int dias) {

		String fSig = "";
		java.util.Date fecha = stringToDate(f1);

		long lFecha = fecha.getTime();

		try {
			long milisDia = 24 * 60 * 60 * 1000;
			long lfecSgte = (lFecha + (milisDia * dias));

			java.util.Date fSiguiente = new java.util.Date(lfecSgte);
			fSig = dateToString(fSiguiente);
		} catch (Exception e) {
		}
		return fSig;
	}

	/**
	 * Metodo encargado de devolver una fecha anterior en una cantidad n de dias
	 * 
	 * @param f1
	 *            String
	 * @param dias
	 *            int
	 * @return String
	 */
	public static String dameFechaAnterior(String f1, int dias) {

		String fAnt = "";
		java.util.Date fecha = stringToDate(f1);

		long lFecha = fecha.getTime();

		try {
			long milisDia = 24 * 60 * 60 * 1000;
			long lfecSgte = (lFecha - (milisDia * dias));

			java.util.Date fSiguiente = new java.util.Date(lfecSgte);
			fAnt = dateToString(fSiguiente);
		} catch (Exception e) {
		}
		return fAnt;
	}

	/**
	 * Metodo encargado de devolver las horas de diferencia entre dos horas
	 * 
	 * @param horaIni
	 *            String
	 * @param horaFin
	 *            String
	 * @return float
	 */
	public static float obtenerHorasDiferencia(String horaIni, String horaFin) {

		float dif = 0;
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			java.util.Date hIni = formatter.parse(horaIni);
			java.util.Date hFin = formatter.parse(horaFin);

			long milisIni = hIni.getTime();
			long milisFin = hFin.getTime();
			Float segundos = Float.valueOf("" + ((milisFin - milisIni) / 1000));
			dif = segundos.floatValue();
			dif /= 60;
			dif /= 60;				
			
		} catch (Exception e) {
			dif = 0;
		}
		return dif;
	}
	
	/**
	 * Te da la diferencia de horas entre 2 fechas. Considera cambio de fecha
	 * @param horaIni
	 * @param horaFin
	 * @return
	 */
	public static float obtenerHorasDifDia(String horaIni, String horaFin) {

		float dif = 0;
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			java.util.Date hIni = formatter.parse(horaIni);
			java.util.Date hFin = formatter.parse(horaFin);

			Float segundos = null;
			long milisIni = hIni.getTime();
			long milisFin = hFin.getTime();
			
			if (hFin.before(hIni)){
				segundos = Float.valueOf("" + ((milisIni - milisFin) / 1000));
			}
			else{
				segundos = Float.valueOf("" + ((milisFin - milisIni) / 1000));
			}

			dif = segundos.floatValue();
			dif /= 60;
			dif /= 60;				
			
		} catch (Exception e) {
			dif = 0;
		}
		return dif;
	}	

	/**
	 * Metodo encargado de devolver los minutos de diferencia entre dos horas
	 * 
	 * @param horaIni
	 *            String
	 * @param horaFin
	 *            String
	 * @return float
	 */
	public static float obtenerMinutosDiferencia(String horaIni, String horaFin) {

		float dif = 0;
		try {

			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			java.util.Date hIni = formatter.parse(horaIni);
			java.util.Date hFin = formatter.parse(horaFin);

			long milisIni = hIni.getTime();
			long milisFin = hFin.getTime();

			Float segundos = Float.valueOf("" + (milisFin - milisIni) / 1000);
			dif = segundos.floatValue();
			dif /= 60;
		} catch (Exception e) {
			dif = 0;
		}
		return dif;
	}

	/**
	 * Metodo que verifica sin una fecha es fin de semana
	 * 
	 * @param fecha
	 *            String
	 * @return boolean
	 */
	public static boolean isWeekEnd(String fecha) {

		boolean finSemana = false;
		java.util.Date f1 = stringToDate(fecha);

		int dow = f1.getDay();

		finSemana = ((dow == 6) || (dow == 0));

		return finSemana;
	}

	/**
	 * Metodo que devuelve un float en formato hh:mm
	 * 
	 * @param f
	 * @return
	 */
	public static String dameFormatoHHMM(float f) {

		String formato = "";

		try {
			int horas = (int) f;
			int minutos = new BigDecimal(""+((f - horas) * 60)).setScale(0,BigDecimal.ROUND_HALF_UP).intValue();
			//int minutos = (int) ((f - horas) * 60);

			String strHora = horas > 9 ? "" + horas : "0" + horas;
			String strMin = minutos > 9 ? "" + minutos : "0" + minutos;

			formato = strHora + ":" + strMin;
		} catch (Exception e) {
			formato = "00:00";
		}

		return formato;
	}

	/**
	 * Metodo que devuelve el aï¿½o de una fecha
	 * 
	 * @param fecha
	 *            String
	 * @return String
	 */
	public static String dameAnho(String fecha) {

		BeanFechaHora bfh = new BeanFechaHora(fecha);
		String strFecha = bfh.getFormatDate("yyyy");
		return strFecha;
	}

	/**
	 * Metodo que verifica si un fecha es de un determinado dia de la semana
	 * 
	 * @param fecha
	 * @param dia
	 * @return
	 */
	public static boolean isDiaSemana(String fecha, int dia) {

		boolean res = false;
		try {
			java.util.Date f1 = stringToDate(fecha);
			res = f1.getDay() == dia;
		} catch (Exception e) {
			
		}
		return res;
	}

	/**
	 * Metodo que devuelve en una cadena el dia de la semana de una determinada
	 * fecha
	 * 
	 * @param fecha
	 *            String
	 * @return String
	 */
	public static String dameDiaSemana(String fecha) {

		String res = "";
		try {
			BeanFechaHora bfh = new BeanFechaHora(fecha);
			res = bfh.getDiasemana();
		} catch (Exception e) {
			
		}
		return res;
	}

	/**
	 * Metodo que devuelve una hora en formato HH:MM:SS
	 * 
	 * @param hora
	 *            String Hora en formato HHMMSS
	 * @return String
	 */
	public static String toHHMMSS(String hora) {

		String cadena = "";
		try {

			if (hora.length() != 6)
				return "";

			String h1 = hora.substring(0, 2);
			String h2 = hora.substring(2, 4);
			String h3 = hora.substring(4, 6);

			int hor = Integer.parseInt(h1.trim());
			if (hor < 10)
				h1 = "0" + hor;
			int min = Integer.parseInt(h2.trim());
			if (min < 10)
				h2 = "0" + min;
			int sec = Integer.parseInt(h3.trim());
			if (sec < 10)
				h3 = "0" + sec;

			if ((hor >= 0) && (hor < 24) && (min >= 0) && (min < 60)
					&& (sec >= 0) && (sec < 60))
				cadena = new String("" + h1 + ":" + h2 + ":" + h3);

		} catch (Exception e) {
			cadena = "";
		}
		return cadena;
	}

	/**
	 * Metodo que verifica si un aï¿½o es vï¿½lido
	 * 
	 * @param anno
	 *            String
	 * @return boolean
	 */
	public static boolean esAnnoValido(String anno) {

		java.sql.Date d = null;
		try {
			BeanFechaHora bfh = new BeanFechaHora(anno, "yyyy");
			d = bfh.getSQLDate();
		} catch (Exception e) {
		}
		return (d != null);
	}

	/**
	 * Metodo que devuelve la fecha exacta de una cantidad de n aï¿½os atras
	 * 
	 * @param f1
	 *            String
	 * @param annos
	 *            int
	 * @return String
	 */
	public static String dameFechaAnhoAnterior(String f1, int annos) {

		String fAnt = "";
		java.util.Date fecha = stringToDate(f1);

		try {

			String dia = fecha.getDate() < 10 ? "0" + fecha.getDate() : ""
					+ fecha.getDate();
			String mes = (fecha.getMonth() + 1) < 10 ? "0"
					+ (fecha.getMonth() + 1) : "" + (fecha.getMonth() + 1);
			int year = fecha.getYear() + 1900 - annos;

			fAnt = "" + dia + "/" + mes + "/" + year;

		} catch (Exception e) {
		}
		return fAnt;
	}

	/**
	 * Metodo que devuelve la diferencia entre dos fechas dependiendo de una
	 * medida
	 * 
	 * @param medida
	 *            String Medida que puede ser minutos, horas, dias, meses o
	 *            aï¿½os
	 * @param fechaIni
	 *            Timestamp
	 * @param fechaFin
	 *            Timestamp
	 * @return float
	 */
	public static float calculaAcumulado(String medida, Timestamp fechaIni,
			Timestamp fechaFin) {

		float total = 0;
		medida = medida != null ? medida.trim() : "";

		try {

			//el calculo se ejecuta en funcion a los minutos
			if (medida.equals(Constantes.MINUTO)) {
				total = obtenerMinutosDiferencia(Utiles.timeToHora(fechaIni),
						Utiles.timeToHora(fechaFin));
			}
			//el calculo se ejecuta en funcion a las horas
			if (medida.equals(Constantes.HORA)) {
				total = dameHorasDiferencia(fechaIni, fechaFin);
			}
			//el calculo se ejecuta en funcion a los dias
			if (medida.equals(Constantes.DIA)) {
				total = obtenerDiasDiferencia(Utiles.timeToFecha(fechaIni),
						Utiles.timeToFecha(fechaFin));
			}
			//el calculo se ejecuta en funcion a los meses
			if (medida.equals(Constantes.MES)) {
				total = obtenerMesesDiferencia(Utiles.timeToFecha(fechaIni),
						Utiles.timeToFecha(fechaFin));
			}
			//el calculo se ejecuta en funcion a los annos
			if (medida.equals(Constantes.ANNO)) {
				total = obtenerAnnosDiferencia(Utiles.timeToFecha(fechaIni),
						Utiles.timeToFecha(fechaFin));
			}
		} catch (Exception e) {
			total = 0;
		}
		return total;

	}

	/**
	 * Metodo que permite insertar una cadena de forma alineada en una cadena de
	 * una longitud determinada
	 * 
	 * @param texto
	 *            String
	 * @param longitud
	 *            int
	 * @param centrar
	 *            boolean
	 * @return String
	 */
	public static String formateaCadena(String texto, int longitud,
			boolean centrar) {

		String cadena = "";
		try {

			if (texto == null)
				texto = "";

			if (texto.length() > longitud) {
				cadena = texto;
			} else {
				if (centrar) {
					int offset = (longitud - texto.length()) / 2;
					for (int i = 0; i < offset; i++)
						cadena += " ";
					cadena += texto;
					offset += texto.length() % 2 == 0 ? 0 : 1;
					for (int i = 0; i < offset; i++)
						cadena += " ";
				} else {
					int offset = longitud - texto.length();
					cadena += texto;
					for (int i = 0; i < offset; i++)
						cadena += " ";
				}
			}

		} catch (Exception e) {
			cadena = texto;
		}
		return cadena;
	}

	/**
	 * Metodo que verifica si un elemento esta en una lista de cadenas
	 * 
	 * @param lista
	 *            ArrayList
	 * @param texto
	 *            String
	 * @return boolean
	 */
	public static boolean estaEnLista(ArrayList lista, String texto) {

		boolean encontro = false;

		if (lista != null) {
			for (int i = 0; i < lista.size() && !encontro; i++) {

				HashMap t = (HashMap) lista.get(i);
				if (t.get("t02cod_pers").equals(texto)) {
					encontro = true;
				}
			}
		}

		return encontro;
	}

	/**
	 * 
	 * @return
	 */
	public static HashMap diasVacPermitidos() {

		HashMap hmPermit = null;
		try {
			hmPermit = new HashMap();
			
			hmPermit.put("1", "2");
			hmPermit.put("2", "2");
			hmPermit.put("3", "2");
			hmPermit.put("4", "2");
			hmPermit.put("5", "2");
			
			hmPermit.put("7", "2");
			hmPermit.put("8", "2");
			hmPermit.put("14", "1");
			hmPermit.put("15", "2");
			hmPermit.put("16", "1");
			hmPermit.put("22", "1");
			hmPermit.put("23", "1");
			hmPermit.put("30", "1");
		} catch (Exception e) {

		}
		return hmPermit;
	}

	/**
	 * Metodo encargado de cargar en un ArrayList todos los registros de un
	 * resulset cargados en HashMaps
	 * 
	 * @param rs
	 *            Resultset. Que contiene los registros a cargar
	 * @param campos
	 *            String. Lista de campos de la estructura del resulset
	 *            separados por el caracter ",".
	 * @return
	 */
	public static ArrayList cargarRegistros(ResultSet rs, String campos) {

		ArrayList res = null;
		try {

			String campo = "";
			String valor = "";
			if (rs != null) {
				res = new ArrayList();

				while (rs.next()) {
					HashMap map = new HashMap();
					StringTokenizer st = new StringTokenizer(campos, ",");

					while (st.hasMoreTokens()) {
						campo = st.nextToken();
						if (rs.getString(campo) != null) {
							valor = rs.getString(campo).trim();
						} else {
							valor = "";
						}
						map.put(campo, valor);
					}

					res.add(map);
				}
			}

			return res;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Metodo encargado de cargar en un HashMap el primer registro encontrado en
	 * el resulset recibido como parï¿½metro.
	 * 
	 * @param rs
	 *            Resultset. Que contiene los registros a cargar
	 * @param campos
	 *            String. Lista de campos de la estructura del resulset
	 *            separados por el caracter ",".
	 * @return
	 */
	public static HashMap cargarMapa(ResultSet rs, String campos) {

		HashMap map = null;
		try {
			String campo = "";
			String valor = "";
			if (rs != null) {
				if (rs.next()) {
					map = new HashMap();
					StringTokenizer st = new StringTokenizer(campos, ",");

					while (st.hasMoreTokens()) {
						campo = st.nextToken();
						if (rs.getString(campo) != null) {
							valor = rs.getString(campo).trim();
						} else {
							valor = "";
						}
						map.put(campo, valor);
					}
				}
			}

			return map;
		} catch (Exception e) {
			
			return null;
		}
	}

	/**
	 * Mï¿½todo encargado de llamar a los mï¿½todos que ordenan una lista por un
	 * campo especï¿½fico.
	 * 
	 * @param lista
	 *            ArrayList. Listado de los registros (HashMaps) a ordenar.
	 * @param key
	 *            String. Nombre del campo por el cual serï¿½ ordenada la lista.
	 * @return ArrayList. Conteniendo la lista ordenada por el campo "key"
	 */
	public static ArrayList ordenarLista(ArrayList lista, String key,
			boolean asc) {
		try {
			Ordenamiento.sortByKey(lista, key, asc);
		} catch (Exception e) {
			return null;
		}
		return lista;
	}
	
	/**
	 * Metodo que devuelve el texto en html para el correo
	 * 
	 * @param codPers
	 * @return @throws
	 *         RemoteException
	 */
	public static String textoCorreoProceso(String dbpool, String nombre, String mensaje, String strURL) {

		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer();
		try {

			head += "<html><head><title>"
					+ Constantes.TITULO_CORREO
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ Constantes.TITULO_CORREO
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table><tr><td><strong>Sr(a)(ita).</strong> <em>")
					.append(nombre).append("</em></td></tr></table><br>")
					.append("<table width='80%'><tr><td>").append(mensaje)
					.append("</td></tr></table>");
//EBV 30/03/2006 Descomentamos link
			if (!strURL.equals("")) {
				body
						.append("<table><tr><td><a href='")
						.append(strURL)
						.append("'><u><em><strong>")
						.append("Si desea ver el detalle de esta informaci&oacute;n pulse aqu&iacute;.</strong></em></u></a></td></tr></table>");
			}
			
			body.append("<br><table><tr><td><strong><em>").append(headerString)
				.append("</em></strong></td></tr></table></body></html>");

			html = head + body.toString();

		} catch (Exception e) {
			return "";
		}
		return html;
	}
	
	/**
	 * 
	 * @param dbpool
	 * @param nombre
	 * @param mensaje
	 * @param strURL
	 * @return
	 */
	public static String textoCorreoDDJJ(HashMap datos) {

		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer();
		try {

			head += "<html><head><title>"
					+ (String)datos.get("subject")
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head><body>";

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");		
			String headerString = bfh.getDiasemana()+", "+date;
			
			body.append(
					"<table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ (String)datos.get("subject")
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table><tr><td><strong>Sr(a)(ita). </strong> <em>")
					.append((String)datos.get("codTrab")).append(" - ")
					.append((String)datos.get("nombre")).append("</em></td></tr></table><br>")
					.append("<table width='80%'><tr><td>").append((String)datos.get("mensage"))
					.append("</td></tr></table>");

			body.append("<br><table width='100%' class='texto'><tr><td width='20%'><strong>Analista : </strong></td>")
			.append("<td width='80%'>").append((String)datos.get("analista")).append("</td></tr></table>");
			
			body.append("<table width='100%' class='texto'><tr><td width='20%'><strong>Acci&oacute;n Realizada : </strong></td>")
			.append("<td width='80%'>").append((String)datos.get("accion")).append("</td></tr></table>");
			
			body.append("<table width='100%' class='texto'><tr><td width='20%'><strong>Fecha : </strong></td>")
			.append("<td width='80%'>").append(bfh.getFormatDate("dd/MM/yyyy hh:mm:ss")).append("</td></tr></table>");

			body.append("<table width='100%' class='texto'><tr><td valign='top' width='20%'><strong>Observaciones : </strong></td>")
					.append("<td width='80%'>").append((String)datos.get("obs")).append("</td></tr></table>");			
			
			body.append("<br><table class='texto'><tr><td><em>").append(headerString)
			.append("</em></td></tr></table></body></html>");

			html = head + body.toString();
			
		} catch (Exception e) {
			return "";
		}
		return html;
	}
	
	/**
	 * Metodo que convierte un flujo de bytes en una cadena 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputToString(InputStream is) throws IOException {
		
		if (is == null) {
			return null;
		}
		
		int size;
		byte buf;
		int count = 0;

		size = is.available();
		byte ary[] = new byte[size];
		buf = (byte)is.read();
		
		while (buf != -1) {
			ary[count] = buf;
			count++;
			buf = (byte)is.read();
		}
		
		return new String(ary);

	}	
	 
	/** JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	 * Metodo que devuelve el texto en html para el correo a Jefes y Directivos por soloicitudes pendientes
	 * 
	 * @param codPers
	 * @return @throws
	 *         RemoteException
	 */
	public static String textoCorreoSolicitudes(Map params, String nombre, String mensaje) {

		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer("");
		log.debug("params:" + params);
		log.debug("fec_envio_notific:" + params.get("fec_notificacion"));
		log.debug("fec_envio_notific:" + params.get("fec_notificacion").getClass());
		log.debug("num_notificacion: " + params.get("num_notificacion"));
		FechaBean fb_fenvio = new FechaBean((Date)params.get("fec_notificacion"));
		String st_fenvio = fb_fenvio.getFormatDate("dd/MM/yyyy");
		log.debug("fb_fenvio: " + fb_fenvio);
		log.debug("st_fenvio: " + st_fenvio);
		
		try {

			head += "<html><head><title>"
					+ Constantes.TITULO_CORREO_DIRECTIVOS
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";
			log.debug("head:" + head);
			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ Constantes.TITULO_CORREO_DIRECTIVOS + "-" + (String)params.get("num_notificacion")
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table>")
					.append("<tr><td class='membrete'><strong>N° de Notificación:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append((String)params.get("num_notificacion"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append(st_fenvio)
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
					.append("<tr><td class='dato'>Notificaci&oacute;n de solicitudes pendientes para directivos</td></tr>")					
					.append("<tr><td><strong>Sr(a)(ita).</strong> <em>")
					.append(nombre).append("</em></td></tr></table><br>")
					.append("<table width='80%'><tr><td>").append(mensaje)
					.append("</td></tr></table>");
            
			/*if (!strURL.equals("")) {
				body
						.append("<table><tr><td><a href='")
						.append(strURL)
						.append("'><u><em><strong>")
						.append(
								"Pulse aqu&iacute; para aprobar o desaprobar las solicitudes.</strong></em></u></a></td></tr></table>");
			}*/
			
			body.append("<table><tr><td>")				
			//.append("<u><em><strong>") //ICAPUNAY 08/08 QUITAR SUBRAYADO Y NEGRITA
			.append("<em>") //ICAPUNAY 08/08 QUITAR SUBRAYADO Y NEGRITA
			//.append("Agradeceremos aprobar o desaprobar seg&uacute;n sea el caso las respectivas solicitudes.</strong></em></u></td></tr></table>"); //ICAPUNAY 08/08 QUITAR SUBRAYADO Y NEGRITA
			//.append("Agradeceremos aprobar o desaprobar seg&uacute;n sea el caso las respectivas solicitudes.</em></td></tr></table>"); //ICAPUNAY 08/08 QUITAR SUBRAYADO Y NEGRITA
			//JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
			.append("Agradeceremos hacer clic en el enlace, a fin de aprobar o desaprobar, seg&uacute;n corresponda, las solicitudes listadas.</em></td></tr></table>");
			//FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
			                                               
			/*body.append("<br><table><tr><td><strong><em>").append(headerString)
				.append("</em></strong></td></tr></table></body></html>");*/
			body.append("<br></body></html>");
			log.debug("body:" + body);
			html = head + body.toString();
			log.debug("html:" + html);
		} catch (Exception e) {
			return "";
		}
		return html;
	}//FIN - JVILLACORTA - 03/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	/**JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	 * Metodo que devuelve el texto en html para el correo a Trabajadores por movimientos de asistencia
	 * 
	 * @param codPers
	 * @return @throws
	 *         RemoteException
	 */
	public static String textoCorreoMovimientoAsistencia(Map params, String nreg, String nombre, String mensaje) { //JVILLACORTA 11/04/2012 Memo N° 00069 - 2012 - 4F3100

		String head = "";
		String html = "";
		String mensajePie = ""; //ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
		StringBuffer body = new StringBuffer();
		log.debug("params:" + params);
		log.debug("fec_envio_notific:" + params.get("fec_notificacion"));
		log.debug("fec_envio_notific:" + params.get("fec_notificacion").getClass());
		log.debug("num_notificacion: " + params.get("num_notificacion"));
		log.debug("fec_notificacion: " + params.get("fec_notificacion"));
		/*FechaBean fb_fenvio = new FechaBean((Date)params.get("fec_notificacion"));
		String st_fenvio = fb_fenvio.getFormatDate("dd/MM/yyyy");
		log.debug("fb_fenvio: " + fb_fenvio);
		log.debug("st_fenvio: " + st_fenvio);*/
		
		try {

			head += "<html><head><title>"
					+ Constantes.TITULO_CORREO_TRABAJADORES
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";
			log.debug("head:" + head);
			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ Constantes.TITULO_CORREO_TRABAJADORES + "-" + (String)params.get("num_notificacion")
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table>")
					//.append("<tr><td class='membrete'><strong>N° de Notificación:</strong></td></tr>")
					.append("<tr><td class='membrete'><strong>Ticket:</strong></td></tr>") //JVILLACORTA 11/04/2012 Memo N° 00069 - 2012 - 4F3100
					.append("<tr><td class='dato'>")
					.append((String)params.get("num_notificacion"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Fecha:</strong></td></tr>")
					.append("<tr><td class='dato'>")
					.append((String)params.get("fec_notificacion"))
					.append("</td></tr>")
					.append("<tr><td class='membrete'><strong>Asunto:</strong></td></tr>")
					.append("<tr><td class='dato'>Alerta de calificaci&oacute;n de marcaciones</td></tr>")					
					.append("<tr><td><strong>Sr(a)(ita).</strong> <em>")
					.append(" ").append(nreg).append(" ").append("-").append(" ").append(nombre).append("</em></td></tr></table><br>") //JVILLACORTA 11/04/2012 Memo N° 00069 - 2012 - 4F3100
					.append("<table width='80%'><tr><td>").append(mensaje)
					.append("</td></tr></table>");
            
			/*if (!strURL.equals("")) {
				body
						.append("<table><tr><td><a href='")
						.append(strURL)
						.append("'><u><em><strong>")
						.append(
								"Pulse aqu&iacute; para regularizar de ser el caso, de lo contrario ser&aacute; sujeto al descuento que corresponda.</strong></em></u></a></td></tr></table>");
			}*/
			
			body.append("<table><tr><td>")				
			.append("<em>")
			.append("Al hacer clic en el enlace de la columna de fecha podr&aacute acceder al registro de papeletas del sistema SIRH Asistencia.</em></td></tr></table>");
			
						
			//ICAPUNAY 23072015 - PAS20155E230300073 - Habilitar Lic. Matrimonio CAS a cuenta vacaciones (SC)
			body.append("<br>");
			mensajePie = "Nota: De haber presentado regularizaciones(licencias, papeletas, solicitudes, etc.), verifique " +
			"si fueron autorizadas o de tener marcaciones registradas no considere el  presente, dado que reprocesaremos su " +
			"información la cual podrá ser comprobada en la siguiente alerta o el autoservicio de asistencia remitido en el cierre de asistencia. Agradecemos su apoyo y comprensión." ;
			body.append("<table width='80%'><tr><td>").append(mensajePie)
			.append("</td></tr></table>");
			//ICAPUNAY 23072015 - PAS20155E230300073
						
			/*body.append("<br><table><tr><td><strong><em>").append(headerString)
				.append("</em></strong></td></tr></table></body></html>");*/
			body.append("<br></body></html>");
			log.debug("body:" + body);
			html = head + body.toString();
			log.debug("html:" + html);

		} catch (Exception e) {
			return "";
		}
		return html;
	}//FIN - JVILLACORTA - 14/11/2011 - MODIFICA ALERTA DE SOLICITUDES
	
	//ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	/**
	 * Metodo que devuelve el texto en html para el correo a enviarse al jefe por solicitudes de labor excepcional (124) y compensacion (125)	
	 * @param codPers
	 * @return @throws
	 *         RemoteException
	 */
	public static String textoCorreoaJefes(String dbpool, String nombreJefe, String mensajeToJefe, String strURL,String nombreColaborador, String mensajeFromColaborador) {

		log.debug("ingreso a textoCorreoaJefes");
		String head = "";
		String html = "";
		StringBuffer body = new StringBuffer();
		try {

			head += "<html><head><title>"
					+ Constantes.TITULO_CORREO
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ Constantes.TITULO_CORREO
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table><tr><td><strong>Sr(a)(ita).</strong> <em>")
					.append(nombreJefe).append("</em></td></tr></table><br>")
					.append("<table width='80%'><tr><td>").append(mensajeToJefe)
					.append("</td></tr></table>")
					.append("<table><tr><td><strong>Sr(a)(ita).</strong> <em>")
					.append(nombreColaborador+ " "+mensajeFromColaborador+".").append("</em></td></tr></table>");
			if (!strURL.equals("")) {
				body.append("<table><tr><td><a href='")
					.append(strURL)
					.append("'><u><em><strong>")
					.append("Si desea ver el detalle de esta informaci&oacute;n pulse aqu&iacute;.</strong></em></u></a></td></tr></table>");
			}			
			body.append("<br><table><tr><td><strong><em>").append(headerString)
				.append("</em></strong></td></tr></table></body></html>");

			html = head + body.toString();

		} catch (Exception e) {
			return "";
		}
		return html;
	}
	
	/**
	  * Metodo que devuelve el texto en html para el correo a enviarse al colaborador por solicitudes de labor excepcional (124)	
	 * @param codPers
	 * @return @throws
	 *         RemoteException
	 */
	public static String textoCorreoProcesoLaborExcepcional(String dbpool, String nombre, String mensaje, String strURL,String estado) {

		log.debug("ingreso a textoCorreoProcesoLaborExcepcional");
		String head = "";
		String html = "";
		String titulo = "";
		StringBuffer body = new StringBuffer();
		try {
			if (estado.equals("aprobada")){
				titulo= Constantes.TITULO_CORREO_LABOR_AUTORIZA;
			}
			if (estado.equals("rechazada")){
				titulo= Constantes.TITULO_CORREO_LABOR_RECHAZO;
			}
			head += "<html><head><title>"
					+ titulo
					+ "</title><style>"
					+ ".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}"
					+ "</style></head>";

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			body.append(
					"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"
							+ titulo
							+ "</strong></center></td></tr></table><br><br>")
					.append("<table><tr><td><strong>Sr(a)(ita).</strong> <em>")
					.append(nombre).append("</em></td></tr></table><br>")
					.append("<table width='80%'><tr><td>").append(mensaje)
					.append("</td></tr></table>");

			if (!strURL.equals("")) {
				body.append("<table><tr><td><a href='")
						.append(strURL)
						.append("'><u><em><strong>")
						.append("Si desea ver el detalle de esta informaci&oacute;n pulse aqu&iacute;.</strong></em></u></a></td></tr></table>");
			}			
			body.append("<br><table><tr><td><strong><em>").append(headerString)
				.append("</em></strong></td></tr></table></body></html>");

			html = head + body.toString();

		} catch (Exception e) {
			return "";
		}
		return html;
	}
	//FIN ICAPUNAY 05/10/2012 AJUSTES AOM 06A4T11 LABOR EXCEPCIONAL
	
	/**
	 * Obtiene la fecha de antiguedad máxima para reportes y otros
	 * @param pool_sp_g Cadena jndi para pool
	 * @return Fecha como maximo de antiguedad
	 * @author wrodriguez
	 * */
	public static String obtenerFechaMinima(String pool_sp_g) throws SQLException{
		if(log.isDebugEnabled()) log.debug("method: obtenerFechaMinima");
		T99DAO codigoDAO = new T99DAO();
		String fechaMinima;
		try {
			fechaMinima = codigoDAO.findParamByCodTabCodigo(pool_sp_g,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.FECHA_INICIO_COMPENSACION);
			log.debug("fechaMinima: "+fechaMinima);
		} catch (Exception e) {
			log.error(e);
			return "";
		}
		return fechaMinima;	
	}
	
	//ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral
	/**
	 * Obtiene la fecha de antiguedad máxima para reportes y otros
	 * @param pool_sp_g String
	 * @return fechaMinima String
	 * @author icapunay
	 * */
	public static String obtenerFechaMinimaClimaLaboral(String pool_sp) throws SQLException{
		if(log.isDebugEnabled()) log.debug("method: obtenerFechaMinimaClimaLaboral");
		T99DAO codigoDAO = new T99DAO();
		String fechaMinima;
		try {
			fechaMinima = codigoDAO.findParamByCodTabCodigo(pool_sp,Constantes.CODTAB_PARAMETROS_ASISTENCIA,Constantes.FECHA_MININA_CLIMA_LABORAL);
			log.debug("fechaMinima: "+fechaMinima);
		} catch (Exception e) {
			log.error(e);
			return "";
		}
		return fechaMinima;	
	}
	//FIN ICAPUNAY - PAS20175E230300069 - Refrigerio Clima Laboral

	
	 // AGONZALESF -PAS20171U230200001 - solicitud de reintegro   
	/**
	 * Construye texto de la notificacion de rechazo de concepto
	 * @param nombre
	 * @param numero
	 * @param conceptos
	 * @return
	 */
	public static String textoCorreoNotificacionRechazoConcepto(String titulo, String nombre ,String numero , List conceptos ) {
		log.debug("ingreso a textoCorreoNotificacionRechazoConcepto");  
		StringBuffer html = new StringBuffer();
		try {
			 
			html.append("<html>")
			.append("<head>")
			.append("<title>").append(titulo).append("</title>")
			.append("<style>")
			.append(".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}")
			.append("table.T2 {color:#333333;border-width: 1px;border-color: #666666;border-collapse: collapse;}")
			.append("table.T2 th {border-width:1px; padding: 1px 5px 1px 5px; border-style: solid; border-color:#666666; background-color:#dedede;}")
			.append("table.T2 td {border-width:1px; padding: 1px 5px 1px 5px; border-style: solid; border-color:#666666; background-color:#ffffff;}")
			.append("</style>")
			.append("</head>");

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			html.append("<body>")			
				.append("<table width='100%' align='center' class='T1'>")
				.append("<tr><td><center><strong>").append( titulo).append("</strong></center></td></tr>")
				.append("</table><br><br>")				
				.append("<table>")
				.append("<tr><td><strong>Sr(a)(ita): </strong> <em>").append(nombre).append("</em></td></tr>")
				.append("<tr><td>Se comunica que su Jefe Inmediato a procedido a rechazar el(los) concepto(s) de la solicitud de reintegro por descuento N&deg;").append(numero).append("</td></tr>")
				.append("</table><br>");			
			 
			html.append("<table width='80%' class='T2'>")
				.append("<tr>")
		 		.append("<th>CONCEPTOS</th>")
		 		.append("<th>MOTIVO</th>")
		 		.append("</tr>");
			for (int i = 0; i < conceptos.size(); i++) {
				Map concepto = (Map)conceptos.get(i);
				html.append("<tr>")
					.append("<td>").append(concepto.get("cod_concepto")).append("-").append(concepto.get("des_concepto")).append("</td>")
			 		.append("<td>").append(concepto.get("motivo")).append("</td>")
			 		.append("</tr>");
			}		
			html.append("</table><br>");
		 		
			html.append("<table>")
			.append("<tr><td>De no estar de acuerdo con esta acci&oacute;n, deber&aacute; volver a generar una nueva solicitud incluyendo este concepto y adjuntando el sustento respectivo .</td></tr>")
			.append("<tr><td>(CC: Jefe Inmediato)</td></tr>")
			.append("<tr><td><strong><em>").append(headerString).append("</em></strong></td></tr>")
			.append("</table>")
			.append("</body>")
			.append("</html>");		 

		} catch (Exception e) {
			return "";
		}
		return html.toString();
	}

	// AGONZALESF -PAS20171U230200001 - solicitud de reintegro  
	/**
	 * Construye Texto de la notificacion de aprobacion de solicitud de reintegro
	 * @param nombre
	 * @param numero
	 * @param conceptos
	 * @param periodo
	 * @return
	 */
	
	public static String textoCorreoNotificacionAprobacionReintegro(String titulo,String nombre ,String numero , List conceptos , String periodo) {
		log.debug("ingreso a textoCorreoNotificacionAprobacionReintegro");  
		StringBuffer html = new StringBuffer();
		try {
			 
			html.append("<html>")
			.append("<head>")
			.append("<title>").append(titulo).append("</title>")
			.append("<style>")
			.append(".T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}")
			.append("table.T2 {color:#333333;border-width: 1px;border-color: #666666;border-collapse: collapse;}")
			.append("table.T2 th {border-width:1px; padding: 1px 5px 1px 5px; border-style: solid; border-color:#666666; background-color:#dedede;}")
			.append("table.T2 td {border-width:1px; padding: 1px 5px 1px 5px; border-style: solid; border-color:#666666; background-color:#ffffff;}")
			.append("</style>")
			.append("</head>");

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			html.append("<body>")			
				.append("<table width='100%' align='center' class='T1'>")
				.append("<tr><td><center><strong>").append( titulo).append("</strong></center></td></tr>")
				.append("</table><br><br>")				
				.append("<table>")
				.append("<tr><td><strong>Sr(a)(ita) :</strong> <em>").append(nombre).append("</em></td></tr>")
				.append("<tr><td>Se comunica que se ha aprobado su solicitud de reintegro por descuento N&deg;").append(numero).append(" del periodo ").append(periodo).append(" seg&uacute;n el siguiente detalle : ").append("</td></tr>")
				.append("<tr><td>Observaci&oacute;n :</td></tr>")
				.append("</table><br>");			
			
			html.append("<table width='80%' class='T2'>")
				.append("<tr>")
		 		.append("<th>CONCEPTO</th>")
		 		.append("<th>MIN. SOL.</th>")
		 		.append("<th>D&Iacute;A SOL.</th>")
		 		.append("<th>APROBADO RRHH</th>")
		 		.append("</tr>");
			for (int i = 0; i < conceptos.size(); i++) {
				Map concepto = (Map)conceptos.get(i);
				String codTipLicencia = Utiles.esNuloesVacio(concepto.get("cod_tiplicencia").toString()) ? "" : concepto.get("cod_tiplicencia").toString(); //<blanco>, L,P,S
				html.append("<tr>")
					.append("<td>").append(concepto.get("cod_concepto")).append("-").append(concepto.get("des_concepto")).append("</td>")
			 		.append("<td style='text-align:right'>").append(codTipLicencia.equals("")?concepto.get("cnt_minsol"):"" ).append("</td>")
			 		.append("<td style='text-align:right'>").append(!codTipLicencia.equals("")?concepto.get("cnt_diasol"):"").append("</td>")
			 		.append("<td style='text-align:right'>").append(codTipLicencia.equals("") ?concepto.get("cnt_minapro"): concepto.get("cnt_diaapro")).append("</td>")
			 		.append("</tr>");
			}		
			html.append("</table><br>");
		 		
			html.append("<table>")			
			.append("<tr><td>(CC: Jefe Inmediato)</td></tr>")
			.append("<tr><td><strong><em>").append(headerString).append("</em></strong></td></tr>")
			.append("</table>")
			.append("</body>")
			.append("</html>");		 

		} catch (Exception e) {
			return "";
		}
		return html.toString();
	}
	
	 // AGONZALESF -PAS20171U230200001 - solicitud de reintegro
	/**
	 * Construye Texto de la notificacion de rechazo de solicitud de reintegro   
	 * @param nombre
	 * @param numero
	 * @param conceptos
	 * @param periodo
	 * @return
	 */
	public static String textoCorreoNotificacionRechazoReintegro(String titulo,String nombre ,String numero , String motivo , String periodo) {
		log.debug("ingreso a textoCorreoNotificacionRechazoReintegro");  
		StringBuffer html = new StringBuffer();
		try {
			 
			html.append("<html>")
			.append("<head>")
			.append("<title>").append(titulo).append("</title>")
			.append("<style>.T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;} </style>")
			.append("</head>");

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			html.append("<body>")			
				.append("<table width='100%' align='center' class='T1'>")
				.append("<tr><td><center><strong>").append( titulo).append("</strong></center></td></tr>")
				.append("</table><br><br>")				
				.append("<table>")
				.append("<tr><td><strong>Sr(a)(ita).</strong> <em>").append(nombre).append("</em></td></tr>")
				.append("<tr><td>Se comunica que se ha rechazado su solicitud de reintegro por descuento N&deg;").append(numero).append(" del periodo ").append(periodo).append(" debido a : ").append("</td></tr>")
				.append("<tr><td>").append(motivo).append("</td></tr>")
				.append("<tr><td>(CC: Jefe Inmediato)</td></tr>")
				.append("<tr><td><strong><em>").append(headerString).append("</em></strong></td></tr>")
				.append("</table>")				
				.append("</body>")
				.append("</html>");		 

		} catch (Exception e) {
			return "";
		}
		return html.toString();
	}
	
	// AGONZALESF -PAS20171U230200001 - solicitud de reintegro   
	/**
	 * Construye Texto de la notificacion de rechazo de solicitud de reintegro
	 * @param nombre
	 * @param numero
	 * @param conceptos
	 * @param periodo
	 * @return
	 */
	public static String textoCorreoNotificacionAlerta(String titulo,String nombre ,String numero , String periodo) {
		log.debug("ingreso a textoCorreoNotificacionAlerta");  
		StringBuffer html = new StringBuffer();
		try {
			 
			html.append("<html>")
			.append("<head>")
			.append("<title>").append(titulo).append("</title>")
			.append("<style>.T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;} </style>")
			.append("</head>");

			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			html.append("<body>")			
				.append("<table width='100%' align='center' class='T1'>")
				.append("<tr><td><center><strong>").append( titulo).append("</strong></center></td></tr>")
				.append("</table><br><br>")				
				.append("<table>")
				.append("<tr><td><strong>Sr(a)(ita).</strong> <em>").append(nombre).append("</em></td></tr>")
				.append("<tr><td>Se comunica que se ha revisado su solicitud de reintegro por descuento N°").append(numero).append(" del periodo ").append(periodo).append(" y se ha determinado que su reintegro ser&aacute; atendido cuando se reincorpore a la instituci&oacute;n ").append("</td></tr>")				
				.append("<tr><td>Agradeciendo su comprensi&oacute;n</td></tr>")
				.append("<tr><td>Divisi&oacute;n de compensaciones</td></tr>")
				.append("<tr><td><strong><em>").append(headerString).append("</em></strong></td></tr>")
				.append("</table>")				
				.append("</body>")
				.append("</html>");		 

		} catch (Exception e) {
			return "";
		}
		return html.toString();
	}
	
	public static String[][] arrayOfString = { 
			{ "Á", "&Aacute;" }, { "É", "&Eacute;" }, { "Í", "&Iacute;" }, { "Ó", "&Oacute;" }, { "Ú", "&Uacute;" }, 
			{ "á", "&aacute;" }, { "é", "&eacute;" }, { "í", "&iacute;" }, { "ó", "&oacute;" }, { "ú", "&uacute;" },
			{ "ñ", "&ntilde;" }, { "Ñ", "&Ntilde;" } };
    
	
	public static String escapeHtml4(String cadena) {
		String target = new String(cadena);
		for (int i = 0; i < arrayOfString.length; i++) {
			target=  target.replaceAll(arrayOfString[i][0], arrayOfString[i][1]);		
		}
		return target.toString();		
	}

	public static String intToString(int num, int digits) { 
	    char[] zeros = new char[digits];
	    Arrays.fill(zeros, '0'); 
	    DecimalFormat df = new DecimalFormat(String.valueOf(zeros)); 
	    return df.format(num);
	}
	 
	
	public static boolean esNuloesVacio(String s){
		if(s!=null){
			return s.trim().equals("");
			
		}else{
			return true;
		}
		
	}
	
	public static String textoCorreoNotificacionSaldoVacacional(String nombresPersonal, String dias, String anno) {
		String html = "";
		try {
			BeanFechaHora bfh = new BeanFechaHora();
			String date = bfh.getFormatDate("dd' de 'MMMM' del 'yyyy");
			String headerString = bfh.getDiasemana()+", "+date;

			html += "<html><head><title>" + Constantes.TITULO_CORREO_SALDO_VACACIONAL + "</title>"
				+ "<style>.T1 {font-style  : normal;text-transform : uppercase;font-size :10pt;color: #FFFFFF;background  : #336699;font-family : Tahoma,Verdana,Arial,Helvetica,sans-serif ;}</style></head>"
				+"<body><table width='100%' align='center' class='T1'><tr><td><center><strong>"+Constantes.TITULO_CORREO_SALDO_VACACIONAL+"</strong></center></td></tr></table><br><br>"
				+"<table><tr><td><strong>Sr(a)(ita).</strong> <em>"+nombresPersonal + "</em></td></tr></table><br>"
				+"<table width='80%'><tr><td>Usted ha generado " + dias + " días de derecho vacacional para el periodo " + anno + ".</td></tr></table>"
				+"<br><table><tr><td><strong><em>"+headerString+"</em></strong></td></tr></table></body></html>";

		} catch (Exception e) {
			log.error("Ha ocurrido un error en textoCorreoNotificacionSaldoVacacional: " + e.getMessage(), e);
		}
		return html;
	}
	
	
	
	public static String[][] matches = { 
		{ "A", "[AÁ]" }, { "E", "[EÉ]" }, { "I", "[IÍ]" }, { "O", "[OÓ]" }, { "U", "[UÚ]" }};
	
	public static String setacento(String cadena) {
		String target = new String(cadena);
		for (int i = 0; i < matches.length; i++) {
			target=  target.replaceAll(matches[i][0], matches[i][1]);		
		}
		return target.toString();		
	}
	
}