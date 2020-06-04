package pe.gob.sunat.rrhh.utils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Title: ConvertidorCSVaLIST
 * Description: Convierte un Archivo CSV a LIST - Valida si un String es fecha valida
 * Copyright: Copyright (c) 2011
 * Company: Sunat
 * @author icapunay
 * @version 1.0 
 */

public class ConvertidorCSVaLIST {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Metodo encargado de convertir un file (archivo csv) a List
	 * @param file File
	 * @return List	
	 */
	public List convertir(File file) {
		String aLine;
		Vector columnNames = new Vector();		
		Map map1 = new HashMap();
		Map map2 = new HashMap();
		List lista = new ArrayList();
		int cont;
		StringTokenizer st1;
		StringTokenizer st2;
		try {			
			FileInputStream File = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(File,"ISO-8859-1"));
			// extrae nombres de columnas
			st1 = new StringTokenizer(br.readLine(), ",");
			
			while (st1.hasMoreTokens()) {					
				columnNames.addElement(st1.nextToken());				
			}
			//llenando nombre de columnas en el map			
			for(int i=0;i<columnNames.size();i++) {
				map1.put(new Integer(i),columnNames.get(i).toString().trim());					
			}
			lista.add(map1);
			// extrae datos
			while ((aLine = br.readLine()) != null) {			
				map2 = new HashMap();
				int contador=0;
				while (contador<5) {
					int posicion=0;
					  if(contador<4){
						  posicion=aLine.indexOf(",");
						  map2.put(columnNames.get(contador).toString().trim(),aLine.substring(0,posicion));
						  aLine = aLine.substring(posicion+1);
					  }else{
						  map2.put(columnNames.get(contador).toString().trim(), aLine!=""?aLine:"");
					  }
				      contador++; 
				}							
				lista.add(map2);
			}
			br.close();
			log.debug("ListaImportante:"+lista.toString());
			/*
			 *st1 = new StringTokenizer(br.readLine(), ",");
			while (st1.hasMoreTokens()) {
				columnNames.addElement(st1.nextToken());
			}
			//llenando nombre de columnas en el map			
			for(int i=0;i<columnNames.size();i++) {
				map1.put(new Integer(i),columnNames.get(i).toString().trim());					
			}
			lista.add(map1);
			// extrae datos
			
			while ((aLine = br.readLine()) != null) {
				st2 = new StringTokenizer(aLine, ",");				
				map2 = new HashMap();
				cont = 0;
				while (st2.hasMoreTokens()) {
					map2.put(columnNames.get(cont++).toString().trim(), st2.nextToken());					
				}
				lista.add(map2);
			}
			br.close();
			 * */
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return lista;		
	}
	
	/**
	 * Metodo encargado de validar un String es una Fecha Valida
	 * @param fechax String
	 * @return boolean	
	 */
	public boolean isFechaValida(String fechax) {  
		try {				
     		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());      
			formatoFecha.setLenient(false);   //permite que se habilite la validacion de fechas correctas en dias, meses y año   
			formatoFecha.parse(fechax);  
		} catch (ParseException e){   
			log.error(e.getMessage());
			return false;  
		}  
		return true;		
	}	
}
