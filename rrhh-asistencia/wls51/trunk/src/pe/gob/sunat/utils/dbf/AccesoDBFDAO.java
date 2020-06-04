package pe.gob.sunat.utils.dbf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.svcon.jdbf.DBFReader;
import com.svcon.jdbf.JDBFException;
import com.svcon.jdbf.JDBField;

public class AccesoDBFDAO
{

    public AccesoDBFDAO()
    {
    }

    public static ArrayList leerDBF(InputStream file)
        throws Exception
    {
        ArrayList datos = null;
        try
        {
            datos = new ArrayList();
            DBFReader dbf = new DBFReader(file);
            do
            {
                if(!dbf.hasNextRecord())
                    break;
                Object data[] = dbf.nextRecord();
                if(data != null)
                {
                    HashMap valores = new HashMap(data.length);
                    for(int i = 0; i < data.length; i++)
                    {
                        JDBField campo = dbf.getField(i);
                        Object valor = data[i];
                        valores.put(campo.getName(), valor);
                    }

                    datos.add(valores);
                }
            } while(true);
            dbf.close();
        }
        catch(JDBFException e)
        {
            throw new Exception("Error al leer el archivo DBF : " + e.getMessage());
        }
        catch(Exception e)
        {
            throw new Exception("Error al leer el archivo DBF : " + e.toString());
        }
        return datos;
    }

    public static ArrayList leerDBF(String ruta)
        throws Exception
    {
        ArrayList datos = null;
        try
        {
            datos = new ArrayList();
            DBFReader dbf = new DBFReader(ruta);
            do
            {
                if(!dbf.hasNextRecord())
                    break;
                Object data[] = dbf.nextRecord();
                if(data != null)
                {
                    HashMap valores = new HashMap(data.length);
                    for(int i = 0; i < data.length; i++)
                    {
                        JDBField campo = dbf.getField(i);
                        Object valor = data[i];
                        valores.put(campo.getName(), valor);
                    }

                    datos.add(valores);
                }
            } while(true);
            dbf.close();
        }
        catch(JDBFException e)
        {
            throw new Exception("Error al leer el archivo DBF : " + e.getMessage());
        }
        catch(Exception e)
        {
            throw new Exception("Error al leer el archivo DBF : " + e.toString());
        }
        return datos;
    }
}
