package raf.sk.projekat1.specification;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import raf.sk.projekat1.model.*;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public abstract class ScheduleService {
    protected Schedule schedule;
    public ScheduleService(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * This method is used to load Schedule from JSON file
     * @param filepath This is the path to JSON file
     */
    public abstract void loadJSON(String filepath) throws IOException;
    /**
     * This method is used to load Schedule from CSV file
     * @param filepath This is the path to CSV file
     */
    public abstract void loadCSV(String filepath) throws IOException;
    /**
     * This method is used to load list of Places from CSV file
     * @param filepath This is the path to CSV file
     */
    public void loadPlacesCSV(String filepath) throws IOException {
        Reader in = new FileReader(filepath);
        CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        CSVParser parser = new CSVParser(in, format);
        List<CSVRecord> records = parser.getRecords();

        Set<String> headers = records.iterator().next().toMap().keySet();
        List<String> stringsList = new ArrayList<>(headers);

        for(CSVRecord record : records){
            Places place = new Places();

            for(int i = 0; i < headers.size(); i++){
                if(i == 0){
                    place.setName(record.get(i));
                }else{
                    place.getAdditional().put(stringsList.get(i),record.get(i));
                }
            }
            schedule.getPlaces().add(place);
        }
    }

    /**
     * This method is used to add single Appointment to Schedule
     * @param when This is the path to CSV file
     * @param place This is the path to CSV file
     * @param time This is the path to CSV file
     * @param additional This is the path to CSV file
     * @return boolean
     */
    public boolean addAppointment(String when, String place, String time, Map<String, String> additional) {
        LocalDate date = LocalDate.parse(when, DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat()));
        if(date.isBefore(getSchedule().getStartDate()) || date.isAfter(getSchedule().getEndDate())
                || (getSchedule().getNonWorkingDates().contains(date) && getSchedule().getNonWorkingDaysOfTheWeek().contains(getSchedule().getInfo().getDayFormat().get(date.getDayOfWeek().getValue()-1))))
            return false;

        String[] split = time.split("-");
        LocalTime startTime = LocalTime.parse(split[0]);
        LocalTime endTime = LocalTime.parse(split[1]);
        if(startTime.isBefore(getSchedule().getStartTime()) || endTime.isAfter(getSchedule().getEndTime()))
            return false;

        for(Appointment a : getSchedule().getAppointments()){
            if(overlappingAppointments(a, startTime, endTime, date, place)){
                return false;
            }
        }

        Appointment newAppointment = new Appointment(startTime, endTime, date, date, getSchedule().getInfo().getDayFormat().get(date.getDayOfWeek().getValue()-1), additional);

        for(Places p : getSchedule().getPlaces()){
            if(p.getName().equals(place)){
                newAppointment.setPlace(p);
            }
        }
        getSchedule().getAppointments().add(newAppointment);
        sortAppointmentList();

        return true;
    }
    public abstract boolean addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional);

    public abstract boolean removeAppointment(String when, String place, String time);
    public abstract boolean removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat);

    public abstract Appointment find(String when, String place, String time);

    public abstract boolean updateAppointment(Appointment appointment, String when);
    public abstract boolean updateAppointment(Appointment appointment, Places place);
    public abstract boolean updateAppointment(Appointment appointment, String startTime, String endTime);
    public abstract boolean updateAppointment(Appointment appointment, Map<String, String> additional);
    public abstract boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime);
    public abstract boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime, Places place);

    public List<Appointment> search(){
        return new ArrayList<>(getSchedule().getAppointments());
    }
    public List<Appointment> search(Places place){
        List<Appointment> results = new ArrayList<>();

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName())) {
                results.add(a);
            }
        }
        return results;
    }
    public List<Appointment> search(Map<String, String> additional){
        List<Appointment> results = new ArrayList<>();

        for(Appointment a : getSchedule().getAppointments()){
            int flag = additional.size();
            for(Map.Entry<String,String> entry : additional.entrySet()){
                if(a.getAdditional().containsValue(entry.getValue())){
                    flag--;
                }
            }
            if(flag == 0){
                results.add(a);
            }
        }
        return results;
    }
    public abstract List<Appointment> search(String startDate, String endDate);
    public abstract List<Appointment> search(String startDate, String endDate, Map<String, String> additional);
    public abstract List<Appointment> search(String startDate, String endDate, Places place);
    public abstract List<Appointment> search(String startDate, String endDate, Places place, Map<String, String> additional);
    public abstract List<Appointment> search(String day, String startDate, String endDate, Places place);
    public abstract List<Appointment> search(String day, String startDate, String endDate, Map<String, String> additional);
    public abstract List<Appointment> search(String day, String startDate, String endDate, Places place, Map<String, String> additional);

    public abstract List<String> check(String startDate, String endDate);
    public abstract List<String> check(String startDate, String endDate, Map<String, String> additional);
    public abstract List<String> check(String startDate, String endDate, String day);
    public abstract List<String> check(String startDate, String endDate, String day, Map<String, String> additional);
    public abstract List<String> check(String startDate, String endDate, Places place);
    public abstract List<String> check(String startDate, String endDate, String day, Places place);
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate);
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional);
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate, Places place);
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate);
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional);
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Places place);

    public abstract List<String> printAppointments(List<Appointment> appointments);
    public abstract boolean overlappingAppointments(Appointment a, LocalTime sTime, LocalTime eTime, LocalDate date, String place);
    public abstract void sortAppointmentList();

    public void exportCSV(String filepath, List<Appointment> appointments) throws IOException{
            FileWriter fileWriter = new FileWriter(filepath);
            CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

            csvPrinter.printRecord(getSchedule().getInfo().getHeaders());

            for (Appointment appointment : appointments) {
                List<String> results = new ArrayList<>();
                for(int i = 0; i < getSchedule().getInfo().getHeaders().size(); i++){
                    if(appointment.getAdditional().containsKey(getSchedule().getInfo().getHeaders().get(i))){
                        results.add(appointment.getAdditional().get(getSchedule().getInfo().getHeaders().get(i)));
                    }
                    else{
                        if(i == getSchedule().getInfo().getPlace() || getSchedule().getInfo().getHeaders().get(i).equals("Place")){
                            results.add(appointment.getPlace().getName());
                        }
                        else if(i == getSchedule().getInfo().getTime() || getSchedule().getInfo().getHeaders().get(i).equals("Time")){
                            results.add(appointment.getStartTime() + "-" + appointment.getEndTime());
                        }
                        else if(i == getSchedule().getInfo().getStartDate() || getSchedule().getInfo().getHeaders().get(i).equals("Start Date")){
                            results.add(appointment.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        }
                        else if(i == getSchedule().getInfo().getDay() || getSchedule().getInfo().getHeaders().get(i).equals("Day")){
                            results.add(appointment.getDay());
                        }
                        else if(i == getSchedule().getInfo().getEndDate() || getSchedule().getInfo().getHeaders().get(i).equals("End Date")){
                            results.add(appointment.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())));
                        }
                    }
                }
                csvPrinter.printRecord(results);
            }

            csvPrinter.close();
            fileWriter.close();
    }
    public void exportPDF(String filepath, List<Appointment> appointments) throws IOException, DocumentException{
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filepath));

        document.open();
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD);
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);

        PdfPTable table = new PdfPTable(getSchedule().getInfo().getHeaders().size());

        for (int j = 0; j < getSchedule().getInfo().getHeaders().size(); j++) {
            Phrase phrase = new Phrase(getSchedule().getInfo().getHeaders().get(j), headerFont);
            PdfPCell cell = new PdfPCell(phrase);
            cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
            table.addCell(cell);
        }

        for(Appointment a : appointments){
            for(int i = 0; i < getSchedule().getInfo().getHeaders().size(); i++){
                if(a.getAdditional().containsKey(getSchedule().getInfo().getHeaders().get(i))){
                    table.addCell(new Phrase(a.getAdditional().get(getSchedule().getInfo().getHeaders().get(i)), bodyFont));
                }
                else{
                    if(i == getSchedule().getInfo().getPlace() || getSchedule().getInfo().getHeaders().get(i).equals("Place")){
                        table.addCell(new Phrase(a.getPlace().getName(), bodyFont));
                    }
                    else if(i == getSchedule().getInfo().getTime() || getSchedule().getInfo().getHeaders().get(i).equals("Time")){
                        table.addCell(new Phrase(a.getStartTime() + "-" + a.getEndTime(), bodyFont));
                    }
                    else if(i == getSchedule().getInfo().getStartDate() || getSchedule().getInfo().getHeaders().get(i).equals("Start Date")){
                        table.addCell(new Phrase(a.getStartDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), bodyFont));
                    }
                    else if(i == getSchedule().getInfo().getDay() || getSchedule().getInfo().getHeaders().get(i).equals("Day")){
                        table.addCell(new Phrase(a.getDay(), bodyFont));
                    }
                    else if(i == getSchedule().getInfo().getEndDate() || getSchedule().getInfo().getHeaders().get(i).equals("End Date")){
                        table.addCell(new Phrase(a.getEndDate().format(DateTimeFormatter.ofPattern(getSchedule().getInfo().getDateFormat())), bodyFont));
                    }
                }
            }
        }

        table.setWidthPercentage(100);

        document.add(table);
        document.close();
    }

}

