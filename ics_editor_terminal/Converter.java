import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;
    

public class Converter {
    private static int[] fieldIndex;

    public static void convert(BufferedReader reader, BufferedWriter writer, int[] fieldIndex) throws Exception{
        Converter.fieldIndex = fieldIndex;
        String calendarTitle = JOptionPane.showInputDialog(null, "Inserire titolo calendario", "titolo", JOptionPane.OK_OPTION);
        String calendarDescription = JOptionPane.showInputDialog(null, "Inserire descrizione calendario", "descrizione", JOptionPane.OK_OPTION);

        // Write initial data to the .ics file
        writer.write("BEGIN:VCALENDAR");
        writer.newLine();
        writer.write("PRODID://LASI//CSVtoGcalendar//1.0");
        writer.newLine();
        writer.write("VERSION:2.0");
        writer.newLine();
        writer.write("CALSCALE:GREGORIAN");
        writer.newLine();
        writer.write("METHOD:PUBLISH");
        writer.newLine();
        writer.write("X-WR-CALNAME:" + calendarTitle);
        writer.newLine();
        writer.write("X-WR-TIMEZONE:Europe/Rome");
        writer.newLine();
        writer.write("X-WR-CALDESC:" + calendarDescription);
        writer.newLine();

        String line;
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(";");
            String[] date = transformDateTime(fields, fieldIndex);
            String[] UIDfield = {date[0], date[1], fields[fieldIndex[ConstantBank.TITLE]]};

            writer.write("BEGIN:VEVENT");
            writer.newLine();
            writer.write("UID:" + getUID(UIDfield));
            writer.newLine();
            writer.write("DTSTART:" + date[0]);
            writer.newLine();
            writer.write("DTEND:" + date[1]);
            writer.newLine();
            writer.write("SUMMARY:" + fields[fieldIndex[ConstantBank.TITLE]]);
            writer.newLine();

            if (fieldIndex[ConstantBank.DESCRIPTION] != -1) {
                writer.write("DESCRIPTION:" + fields[fieldIndex[ConstantBank.DESCRIPTION]]);
                writer.newLine();
            }
            if (fieldIndex[ConstantBank.LOCATION] != -1) {
                writer.write("LOCATION:" + fields[fieldIndex[ConstantBank.LOCATION]]);
                writer.newLine();
            }
             if (fieldIndex[ConstantBank.NOTIFICATION] != -1) {
                //insert code todo here:
            }
            writer.write("END:VEVENT");
            writer.newLine();

            ICS_Creator.numRigheConvertite++;
            
        }

        // Write the closing tag
        writer.write("END:VCALENDAR");

        reader.close();
        writer.close();
    }

    //generate and return UID
    private static String getUID(String[] UIDfield) {
        StringBuilder rawUID = new StringBuilder();

        for (int i = 0; i < UIDfield.length; i++) {
            rawUID.append(UIDfield[i]);
        }

        int hash = rawUID.hashCode();
        hash = Math.abs(hash);

        return Integer.toString(hash);
    }

    //per ottenere il formato corretto per ics
    private static String[] transformDateTime(String[] fields, int[] fieldIndex) {
        String[] dateFormatted = new String[2];
        String[][] rawDate = new String[2][2];
        LocalDateTime[] tmp = new LocalDateTime[2];

        //porto l'ora indietro di 2 ore per risolvere problema di conversione di google
        fields[fieldIndex[ConstantBank.HH_START]] = timeTraveller(fields[fieldIndex[ConstantBank.HH_START]]);
        fields[fieldIndex[ConstantBank.HH_END]] = timeTraveller(fields[fieldIndex[ConstantBank.HH_END]]);

        rawDate[0][0] = fields[fieldIndex[ConstantBank.DD_START]];
        rawDate[0][1] = fields[fieldIndex[ConstantBank.HH_START]];
        rawDate[1][0] = fields[fieldIndex[ConstantBank.DD_END]];
        rawDate[1][1] = fields[fieldIndex[ConstantBank.HH_END]];

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (int i = 0; i < 2; i++) {
            tmp[i] = LocalDateTime.parse(rawDate[i][0] + " " + rawDate[i][1], formatter);
        }

        formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        for (int i = 0; i < 2; i++) {
            dateFormatted[i] = tmp[i].format(formatter) + "Z";
        }

        return dateFormatted;
    }

    //per portare l'ora indietro di 2 ore
    private static String timeTraveller(String hour) {
        char[] hhChar = new char[2];

        hhChar[0] = hour.charAt(0);
        hhChar[1] = hour.charAt(1);

        int hourInteger = Integer.parseInt(new String(hhChar));
        hourInteger -= 2;

        String newHourDigit;

        if (hourInteger<10) newHourDigit = "0" + hourInteger;
        else newHourDigit = Integer.toString(hourInteger);
        hour = hour.replaceFirst(new String(hhChar), newHourDigit);

        return hour;
    }
}
