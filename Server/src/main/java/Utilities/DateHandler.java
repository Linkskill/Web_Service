package Utilities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Classe responsável por converter as datas o padrão \"ano-mês-dia\" para o padrão
 * \"dia-mês-ano\".
 */
public class DateHandler {
    /**
     * Converte data em Json para objeto LocalDate.
     * @param dateString String em Json representando uma data
     * @return LocalDate representando esta data, ou null se
     * a string informada não estiver no formato correto
     */
    public static LocalDate jsonStringToDate(String dateString) {
        if(dateString == null || dateString.equals(""))
            return null;
        
        try {
            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-M-d"));
            return date;
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Converte LocalDate para data em Json.
     * @param date Objeto com a data
     * @return String em Json representando esta data, ou null
     * se a data informada for null
     */
    public static String dateToJsonString(LocalDate date) {
        if(date == null)
            return null;
        
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-M-d"));
        
        return formattedDate;
    }
    
    /**
     * Transforma uma String em uma data do padrão "\dia-mês-ano"\ do tipo LocalDate.
     * @param dateString contém a data a ser convertida
     * @return data do tipo LocalDate
     */
    public static LocalDate stringToDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("d-M-yyyy"));
    }

    /**
     * Transforma uma data do tipo LocalDate em uma String com formatação "\dia-mês-ano"\.
     * @param date data do tipo LocalDate no padrão (ano/mês/dia)
     * @return date String na formatação d-M-yyyy
     */
    public static String dateToString(LocalDate date) {
        if(date == null)
            return null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-M-yyyy");

        return date.format(formatter);
    }
}