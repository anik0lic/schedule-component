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
    /**
     * This method is used to add single Appointment to Schedule
     * @param startDate This is the path to CSV file
     * @param endDate This is the path to CSV file
     * @param time This is the path to CSV file
     * @param place This is the path to CSV file
     * @param repeat This is the path to CSV file
     * @param additional This is the path to CSV file
     * @return boolean
     */
    public abstract boolean addAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat, Map<String, String> additional);

    /**
     * This method is used to add single Appointment to Schedule
     * @param when This is the path to CSV file
     * @param place This is the path to CSV file
     * @param time This is the path to CSV file
     * @return boolean
     */
    public abstract boolean removeAppointment(String when, String place, String time);
    /**
     * This method is used to add single Appointment to Schedule
     * @param startDate This is the path to CSV file
     * @param endDate This is the path to CSV file
     * @param time This is the path to CSV file
     * @param place This is the path to CSV file
     * @param repeat This is the path to CSV file
     * @return boolean
     */
    public abstract boolean removeAppointment(String startDate, String endDate, String time, String place, AppointmentRepeat repeat);

    /**
     * This method is used to add single Appointment to Schedule
     * @param when This is the path to CSV file
     * @param place This is the path to CSV file
     * @param time This is the path to CSV file
     * @return Appointment
     */
    public abstract Appointment find(String when, String place, String time);

    /**
     * This method is used to add single Appointment to Schedule
     * @param appointment This is the path to CSV file
     * @param when This is the path to CSV file
     * @return boolean
     */
    public abstract boolean updateAppointment(Appointment appointment, String when);
    /**
     * This method is used to add single Appointment to Schedule
     * @param appointment This is the path to CSV file
     * @param place This is the path to CSV file
     * @return boolean
     */
    public abstract boolean updateAppointment(Appointment appointment, Places place);
    /**
     * This method is used to add single Appointment to Schedule
     * @param appointment This is the path to CSV file
     * @param startTime This is the path to CSV file
     * @param endTime This is the path to CSV file
     * @return boolean
     */
    public abstract boolean updateAppointment(Appointment appointment, String startTime, String endTime);
    /**
     * This method is used to add single Appointment to Schedule
     * @param appointment This is the path to CSV file
     * @param additional This is the path to CSV file
     * @return boolean
     */
    public abstract boolean updateAppointment(Appointment appointment, Map<String, String> additional);
    /**
     * This method is used to add single Appointment to Schedule
     * @param appointment This is the path to CSV file
     * @param when This is the path to CSV file
     * @param startTime This is the path to CSV file
     * @param endTime This is the path to CSV file
     * @return boolean
     */
    public abstract boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime);
    /**
     * This method is used to add single Appointment to Schedule
     * @param appointment This is the path to CSV file
     * @param when This is the path to CSV file
     * @param startTime This is the path to CSV file
     * @param endTime This is the path to CSV file
     * @param place This is the path to CSV file
     * @return boolean
     */
    public abstract boolean updateAppointment(Appointment appointment, String when, String startTime, String endTime, Places place);

    /**
     * This method is used to search for all the appointments in the schedule
     * @return List of Appointments
     */
    public List<Appointment> search(){
        return new ArrayList<>(getSchedule().getAppointments());
    }
    /**
     * This method is used to search for appointments in the schedule
     * @param place This is the place name of the appointment
     * @return List of Appointments
     */
    public List<Appointment> search(Places place){
        List<Appointment> results = new ArrayList<>();

        for(Appointment a : getSchedule().getAppointments()){
            if(a.getPlace().getName().equals(place.getName())) {
                results.add(a);
            }
        }
        return results;
    }
    /**
     * This method is used to search for appointments in the schedule
     * @param additional This is the map of the additional information of the appointment(the key is the header and the value is the information)
     * @return List of Appointments
     */
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
    /**
     * This method is used to search for appointments in the schedule
     * @param startDate This is the start date of the appointment
     * @param endDate This is the end date of the appointment
     * @return List of Appointments
     */
    public abstract List<Appointment> search(String startDate, String endDate);
    /**
     * This method is used to search for appointments in the schedule
     * @param startDate This is the start date of the appointment
     * @param endDate This is the end date of the appointment
     * @param additional This is the map of the additional information of the appointment(the key is the header and the value is the information)
     * @return List of Appointments
     */
    public abstract List<Appointment> search(String startDate, String endDate, Map<String, String> additional);
    /**
     * This method is used to search for appointments in the schedule
     * @param startDate This is the start date of the appointment
     * @param endDate This is the end date of the appointment
     * @param place This is the place name of the appointment
     * @return List of Appointments
     */
    public abstract List<Appointment> search(String startDate, String endDate, Places place);
    /**
     * This method is used to search for appointments in the schedule
     * @param startDate This is the start date of the appointment
     * @param endDate This is the end date of the appointment
     * @param place This is the place name of the appointment
     * @param additional This is the map of the additional information of the appointment(the key is the header and the value is the information)
     * @return List of Appointments
     */
    public abstract List<Appointment> search(String startDate, String endDate, Places place, Map<String, String> additional);
    /**
     * This method is used to search for appointments in the schedule
     * @param day This is the day of the appointment
     * @param startDate This is the start date of the appointment
     * @param endDate This is the end date of the appointment
     * @param place This is the place name of the appointment
     * @return List of Appointments
     */
    public abstract List<Appointment> search(String day, String startDate, String endDate, Places place);
    /**
     * This method is used to search for appointments in the schedule
     * @param day This is the day of the appointment
     * @param startDate This is the start date of the appointment
     * @param endDate This is the end date of the appointment
     * @param additional This is the map of the additional information of the appointment(the key is the header and the value is the information)
     * @return List of Appointments
     */
    public abstract List<Appointment> search(String day, String startDate, String endDate, Map<String, String> additional);
    /**
     * This method is used to search for appointments in the schedule
     * @param day This is the day of the appointment
     * @param startDate This is the start date of the appointment
     * @param endDate This is the end date of the appointment
     * @param place This is the place name of the appointment
     * @param additional This is the map of the additional information of the appointment(the key is the header and the value is the information)
     * @return List of Appointments
     */
    public abstract List<Appointment> search(String day, String startDate, String endDate, Places place, Map<String, String> additional);

    /**
     * This method is used to check for free appointments
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @return List of Strings
     */
    public abstract List<String> check(String startDate, String endDate);
    /**
     * This method is used to check for free appointments
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param additional This is the map of the addition information about the appointment(the key is the header and the value is the information)
     * @return List of Strings
     */
    public abstract List<String> check(String startDate, String endDate, Map<String, String> additional);
    /**
     * This method is used to check for free appointments
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param day This is the day of the week of the appointment
     * @return List of Strings
     */
    public abstract List<String> check(String startDate, String endDate, String day);
    /**
     * This method is used to check for free appointments
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param day This is the day of the week of the appointment
     * @param additional This is the map of the addition information about the appointment(the key is the header and the value is the information)
     * @return List of Strings
     */
    public abstract List<String> check(String startDate, String endDate, String day, Map<String, String> additional);
    /**
     * This method is used to check for free appointments
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param place This is the place name of the appointment
     * @return List of Strings
     */
    public abstract List<String> check(String startDate, String endDate, Places place);
    /**
     * This method is used to check for free appointments
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param day This is the day of the week of the appointment
     * @param place This is the place name of the appointment
     * @return List of Strings
     */
    public abstract List<String> check(String startDate, String endDate, String day, Places place);
    /**
     * This method is used to check for free appointments
     * @param startTime This is the start time of the appointment
     * @param endTime This is the end time of the appointment
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @return List of Strings
     */
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate);
    /**
     * This method is used to check for free appointments
     * @param startTime This is the start time of the appointment
     * @param endTime This is the end time of the appointment
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param additional This is the map of the addition information about the appointment(the key is the header and the value is the information)
     * @return List of Strings
     */
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate, Map<String, String> additional);
    /**
     * This method is used to check for free appointments
     * @param startTime This is the start time of the appointment
     * @param endTime This is the end time of the appointment
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param place This is the place name of the appointment
     * @return List of Strings
     */
    public abstract List<String> check(String startTime, String endTime, String startDate, String endDate, Places place);
    /**
     * This method is used to check for free appointments
     * @param startTime This is the start time of the appointment
     * @param endTime This is the end time of the appointment
     * @param day This is the day of the week of the appointment
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @return List of Strings
     */
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate);
    /**
     * This method is used to check for free appointments
     * @param startTime This is the start time of the appointment
     * @param endTime This is the end time of the appointment
     * @param day This is the day of the week of the appointment
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param additional This is the map of the addition information about the appointment(the key is the header and the value is the information)
     * @return List of Strings
     */
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Map<String, String> additional);
    /**
     * This method is used to check for free appointments
     * @param startTime This is the start time of the appointment
     * @param endTime This is the end time of the appointment
     * @param day This is the day of the week of the appointment
     * @param startDate This is the start date from where you check
     * @param endDate This is the end date to where you check
     * @param place This is the place name of the appointment
     * @return List of Strings
     */
    public abstract List<String> check(String startTime, String endTime, String day, String startDate, String endDate, Places place);

    /**
     * This method is used to return the list of Appointments in a String format ready to be printed or added to the table
     * @param appointments This is List of Appointments
     * @return List of Strings
     */
    public abstract List<String> printAppointments(List<Appointment> appointments);
    /**
     * This method is used to check if an appointment is overlapping in the appointment list
     * @param appointment This is the appointment that will be checked
     * @param sTime This is the start time of the appointment
     * @param eTime This is the end time of the appointment
     * @param date This is the date of the appointment ( the format in the second implementation is "startDate-endDate" e.g. "01/10/2023-10/10/2023", but in the first implementation it's just the date e.g. "01/10/2023")
     * @param place This is the place name of the appointment
     * @return boolean
     */
    public abstract boolean overlappingAppointments(Appointment appointment, LocalTime sTime, LocalTime eTime, LocalDate date, String place);
    /**
     * This method is used to sort the appointment list
     */
    public abstract void sortAppointmentList();

    /**
     * This method is used to export the table into a CSV file
     * @param filepath This is the path to CSV file
     * @param appointments This is the list of Appointments that will be exported
     */
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
    /**
     * This method is used to export the table into a PDF type file
     * @param filepath This is the path to PDF file
     * @param appointments This is the list of Appointments we will export
     */
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

