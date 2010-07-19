/*
Copyright 2009-2010 Igor Polevoy 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/


package activejdbc;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Timestamp;
import java.text.*;

/**
 * @author Igor Polevoy
 */
public class Converter {

    /**
     * Returns string representation of database object
     * @param value
     * @return
     */
    public static String toString(Object value) {
        if(value == null) {
            return null;
        } else if(value instanceof Clob) {
            return clobToString((Clob) value);
        } else {
            return value.toString();
        }
    }

    /*
     * Converts clob to string
     */
    private static String clobToString(Clob clob) {
        try {
            Reader r = clob.getCharacterStream();
            StringWriter sw = new StringWriter();
            copyStream(r, sw);
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Copying stream
     */
    private static void copyStream(Reader r, Writer w) throws IOException {
        char buffer[] = new char[4096];
        for (int n = 0; -1 != (n = r.read(buffer));) {
            w.write(buffer, 0, n);
        }
    }


    /**
     * Returns true if the value is any numeric type and has a value of 1, or
     * if string type has a value of 'y', 't', 'true' or 'yes'. Otherwise, return false.
     *
     * @param value
     * @return true if the value is any numeric type and has a value of 1, or
     * if string type has a value of 'y', 't', 'true' or 'yes'. Otherwise, return false.
     */
    public static Boolean toBoolean(Object value){
        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof BigDecimal) {
            return value.equals(BigDecimal.ONE);
        } else if (value instanceof Long) {
            return value.equals(1L);
        } else if (value instanceof Integer) {
            return value.equals(1);
        } else if (value instanceof Character) {
            return value.equals('y') || value.equals('Y')
                    || value.equals('t') || value.equals('T');

        }else return value.toString().equalsIgnoreCase("yes")
                || value.toString().equalsIgnoreCase("true") 
                || value.toString().equalsIgnoreCase("y")
                || value.toString().equalsIgnoreCase("t")
                || Boolean.parseBoolean(value.toString());
    }


    public static java.sql.Date toSqlDate(Object value){
        if (value == null) {
            return null;
        } else if (value instanceof java.sql.Date) {
            return (java.sql.Date) value;
        } else if (value instanceof Timestamp) {
            return new java.sql.Date(((Timestamp) value).getTime());
        } else if (value instanceof java.util.Date) {
            return new java.sql.Date(((java.util.Date) value).getTime());

        } else {
            try {
                DateFormat df = new SimpleDateFormat();
                java.util.Date d = df.parse(value.toString());
                return new java.sql.Date(d.getTime());
            } catch (ParseException e) {
                throw new ConversionException("failed to convert: '" + value + "' to java.sql.Date", e);
            }
        }
    }

    public static Double toDouble(Object value){
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            NumberFormat nf = new DecimalFormat();
            try {
                return nf.parse(value.toString()).doubleValue();
            } catch (ParseException e) {
                throw new ConversionException("failed to convert: '" + value + "' to Double", e);
            }
        }
    }


    /**
     *   If the value is instance of java.sql.Timestamp, returns it, else tries to convert the
     * value to Timestamp using {@link Timestamp#valueOf(String)}.
     * This method might trow <code>IllegalArgumentException</code> if fails at conversion.
     *
     *
     * @see {@link Timestamp#valueOf(String)}
     * @param value value to convert.
     * @return instance of Timestamp.
     */
    public static Timestamp toTimestamp(Object value){

       if (value == null) {
            return null;
        } else if (value instanceof Timestamp) {
            return (Timestamp) value;
        } else if (value instanceof java.sql.Date) {
            return new Timestamp(((java.sql.Date)value).getTime());
        } else if (value instanceof java.util.Date) {
           return new Timestamp(((java.util.Date)value).getTime());
        } else {
            try {
                return Timestamp.valueOf(value.toString());
            } catch (IllegalArgumentException  e) {
                throw new ConversionException("failed to convert: '" + value + "' to Timestamp", e);
            }
        }
    }

    public static Float toFloat(Object value){
        if (value == null) {
            return null;        
        }else if (value instanceof Number) {
            return  ((Number)value).floatValue();
        } else {
            NumberFormat nf = new DecimalFormat();
            try {
                return nf.parse(value.toString()).floatValue();
            } catch (ParseException e) {
                throw new ConversionException("failed to convert: '" + value + "' to Float", e);
            }
        }
    }


    public static Long toLong(Object value){
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return  ((Number)value).longValue();
        } else {
            NumberFormat nf = new DecimalFormat();
            try {
                return nf.parse(value.toString()).longValue();
            } catch (ParseException e) {
                throw new ConversionException("failed to convert: '" + value + "' to Long", e);
            }
        }
    }


    public static Integer toInteger(Object value){
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return  ((Number)value).intValue();
        } else {
            NumberFormat nf = new DecimalFormat();
            try {
                return nf.parse(value.toString()).intValue();
            } catch (ParseException e) {
                throw new ConversionException("failed to convert: '" + value + "' to Integer", e);
            }
        }
    }

    public static BigDecimal toBigDecimal(Object value){
        if (value == null) {
            return null;
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }else {
            return new BigDecimal(value.toString());
        }
    }
}


