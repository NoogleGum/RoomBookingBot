
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

public class RoomBookingBot extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "5764927302:AAFPGn2nVP_EDKgod8G0P8zN5JlwI2wuc74";
    private static final String BOT_USERNAME = "MyRoomBookingBot";

    private static Map<String, String> roomBookings;
    private static Map<Long, String> userNames;

    public RoomBookingBot() {
        roomBookings = new HashMap<>();
        userNames = new HashMap<>();
    }

        public static void main(String args[]) {
            try {
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new RoomBookingBot());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                sendMessage(chatId, "Welcome to the Room Booking Bot! Please enter your name:");
            } else if (!userNames.containsKey(chatId)) {
                String name = messageText;
                userNames.put(chatId, name);
                sendMessage(chatId, "Thank you, " + name + "! Please enter the room number you would like to book (e.g. F101):");
            } else if (!roomBookings.containsKey(chatId)) {
                String roomNumber = messageText;
                if (roomNumber.matches("F[1-5][0-9][1-5]")) {
                    roomBookings.put(String.valueOf(chatId), roomNumber);
                    sendMessage(chatId, "The room " + roomNumber + " has been booked. You can remove your booking by sending /remove.");
                } else {
                    sendMessage(chatId, "Invalid room number. Please enter a valid room number (e.g. F101):");
                }
            } else if (messageText.equals("/remove")) {
                roomBookings.remove(chatId);
                sendMessage(chatId, "Your booking has been removed.");
            }
        }
    }

    private void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(messageText);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }
}
