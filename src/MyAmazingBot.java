import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
public class MyAmazingBot extends TelegramLongPollingBot {
    static Room[] rooms = {new Room("F101"), new Room("F102"), new Room("F103"), new Room("F104"), new Room("F105")};
    int index;
    String day;
    int numOfDay;
    String time;
    boolean b;
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            Date d = new Date();
            // Set variables
            String messageText = update.getMessage().getText();
            long chat_id = update.getMessage().getChatId();
            SendMessage message = new SendMessage();
            message.setChatId(chat_id);
            if(d.getTime() == 18 && LocalDate.now().getDayOfWeek().toString().equals("FRIDAY")){
                message.setText("Service now isn't available\nRegistration will start on Sunday at 18:00");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < 5; i++) {
                    rooms[i].reset();
                }
                return;
            }
            if(messageText.matches("^((09|1[0-8]):00-1[0-8]:00)$")){
                if(!rooms[index].isBooked(numOfDay,messageText)){
                    time = messageText;
                    message.setText("You can book at " + messageText + " on " + (day.toUpperCase().charAt(0) + day.substring(1).toLowerCase()) + "\nTo book room write /book");
                }
                else{
                    message.setText("Room isn't available at " + messageText + " on " + (day.toUpperCase().charAt(0) + day.substring(1).toLowerCase()) + "\nTry book at another time");
                }
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }
            if(numOfDayOfWeek(messageText.toLowerCase()) <=
                    numOfDayOfWeek(LocalDate.now().getDayOfWeek().toString().toLowerCase())){
                message.setText("You can reserve starting from " + LocalDate.now().getDayOfWeek().toString() + " only");
                if(LocalDateTime.now().getHour() >= 18){
                    message.setText("You can reserve starting from " + LocalDate.now().plusDays(1).getDayOfWeek().toString() + " only");
                }
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }
            switch (messageText.toLowerCase()) {
                case "/start" -> {
                    message.setText("Hello! \nTo see the list of rooms write /list");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                case "/list" -> {
                    String s = "";
                    for (int i = 0; i < rooms.length; i++) {
                        s += rooms[i].roomName + ", ";
                    }
                    s += "\nTo book the room write /try";
                    message.setText(s);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                case "/try" -> {
                    String s = "";
                    for (int i = 0; i < rooms.length; i++) {
                        s += "/" + rooms[i].roomName.toLowerCase() + "\n";
                    }
                    message.setText(s);
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                case "/f101", "/f102", "/f103", "/f104", "/f105" -> {
                    for (int i = 0; i < rooms.length; i++) {
                        if (messageText.substring(1).toUpperCase().equals(rooms[i].getRoomName())) {
                            index = i;
                            break;
                        }
                    }
                    System.out.println(index);
                    message.setText(messageText.toUpperCase().substring(1) + "" +
                            "\nWrite the day of week " +
                            "\n(You can book only on weekdays)");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                case "/book" -> {
                    rooms[index].book(numOfDay, time);
                    message.setText("You successfully booked the " + rooms[index].getRoomName() + " room" +
                            "\nTo see your reservations write /mybooking");
                    b = true;
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                case "monday", "tuesday", "wednesday", "thursday", "friday","saturday","sunday" -> {
                    numOfDay = numOfDayOfWeek(messageText.toLowerCase());
                    if(numOfDay == 5){
                        message.setText("You wrote weekend day \nTry again");
                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        onUpdateReceived(new Update());
                        return;
                    }
                    day = messageText;
                    System.out.println(numOfDay);
                    System.out.println(day);
                    message.setText("Your choose is " + messageText.toUpperCase().charAt(0) + messageText.substring(1).toLowerCase()
                    + "\nWrite timeslot in this format " +
                            "\n\nExample:09:00-11:00 " +
                            "\n(You can book only at 9:00 to 18:00)");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                case "/mybooking" -> {
                    if(b){
                        message.setText("Your reservation on "+ rooms[index].getRoomName() + " on " + day.toUpperCase().charAt(0) + day.substring(1).toLowerCase() + " at " + time + "\nTo cancel your reservation write /cancel");
                    }
                    else{
                        message.setText("You didn't reserved the room\nTo book write /try");
                    }
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                case "/cancel" -> {
                    if(b){
                        rooms[index].cancel(numOfDay, time);
                        message.setText("You canceled your reservation on " + rooms[index].getRoomName() + " on " + day.toUpperCase().charAt(0) + day.substring(1).toLowerCase() + " at " + time);
                    }
                    else {
                        message.setText("You didn't reserved the room\nTo book write /try");
                    }
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
                default -> {
                    message.setText("Your input is incorrect");
                    try {
                        execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    onUpdateReceived(new Update());
                }
            }
        }

        }

    public int numOfDayOfWeek(String day){
        int n;
        switch (day){
            case "monday":
                n = 0;
                break;
            case "tuesday":
                n = 1;
                break;
            case "wednesday":
                n = 2;
                break;
            case "thursday":
                n = 3;
                break;
            case "friday":
                n = 4;
                break;
            default:
                n = 5;
                break;
        }
        return n;
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "MyRoomBookingBot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "5764927302:AAFPGn2nVP_EDKgod8G0P8zN5JlwI2wuc74";
    }
}