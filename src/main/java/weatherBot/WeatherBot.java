package weatherBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.HashSet;

import static weatherBot.service.ApiService.getWeatherInfoFromCityName;
import static weatherBot.service.ApiService.getWeatherInfoFromLocation;
import static weatherBot.service.KeyboardService.*;

public class WeatherBot extends TelegramLongPollingBot implements WeatherInterface {

    HashSet<String> members = new HashSet<>();

    HashMap<String, Integer> currentCountryPage = new HashMap<>();
    HashMap<String, Integer> currentCityPage = new HashMap<>();
    HashMap<String, UserState> currentState = new HashMap<>();
    HashMap<String, String> userCountryName = new HashMap<>();

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String messageFromBot;
            if (members.add(chatId)) {
                System.out.println(members.size() + " -> " + message.getFrom().getFirstName() + " | @" + message.getFrom().getUserName());
            }
            if (message.hasLocation()) {
                messageFromBot = getWeatherInfoFromLocation(message.getLocation());
                try {
                    execute(new SendMessage(chatId, messageFromBot));
                } catch (TelegramApiException e) {
                    System.out.println("location ni execute qilishda xatolik");
                    e.printStackTrace();
                }
            } else if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    currentCountryPage.put(chatId, 0);
                    currentState.put(chatId, UserState.COUNTRIES);
                    messageFromBot = CHOOSE_COUNTRY;
                    execute(getCountriesInlinePages().get(0), chatId, messageFromBot);
                } else {
                    String cityName = text.toLowerCase();
                    messageFromBot = getWeatherInfoFromCityName(cityName);
                    try {
                        execute(new SendMessage(chatId, messageFromBot));
                    } catch (TelegramApiException e) {
                        System.out.println("city name jo'natganda weather olishda execute da error");
                        e.printStackTrace();
                    }
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            String callData = callbackQuery.getData();
            String chatId = message.getChatId().toString();
            InlineKeyboardMarkup inlineKeyboardMarkup = null;
            String messageFromBot = CHOOSE_COUNTRY;

            switch (currentState.get(chatId)) {
                case COUNTRIES -> {
                    int index = currentCountryPage.get(chatId);
                    switch (callData) {
                        case NEXT_BUTTON -> {
                            inlineKeyboardMarkup = getCountriesInlinePageByIndex(index + 1);
                            currentCountryPage.replace(chatId, index + 1);
                        }
                        case PREVIOUS_BUTTON -> {
                            inlineKeyboardMarkup = getCountriesInlinePageByIndex(index - 1);
                            currentCountryPage.replace(chatId, index - 1);
                        }
                        default -> {
                            inlineKeyboardMarkup = getCitiesInlinePagesByCountryName(callData).get(0);
                            userCountryName.put(chatId, callData);
                            currentState.replace(chatId, UserState.CITIES);
                            currentCityPage.put(chatId, 0);
                            messageFromBot = CHOOSE_CITY + " of " + userCountryName.get(chatId);
                        }
                    }
                }
                case CITIES -> {
                    int index = currentCityPage.get(chatId);
                    messageFromBot = CHOOSE_CITY + " of " + userCountryName.get(chatId);
                    switch (callData) {
                        case NEXT_BUTTON -> {
                            inlineKeyboardMarkup = getCitiesInlinePageByIndex(userCountryName.get(chatId), index + 1);
                            currentCityPage.replace(chatId, index + 1);
                        }
                        case PREVIOUS_BUTTON -> {
                            inlineKeyboardMarkup = getCitiesInlinePageByIndex(userCountryName.get(chatId), index - 1);
                            currentCityPage.replace(chatId, index - 1);
                        }
                        case BACK_BUTTON -> {
                            currentState.put(chatId, UserState.COUNTRIES);
                            inlineKeyboardMarkup = getCountriesInlinePageByIndex(currentCountryPage.get(chatId));
                            messageFromBot = CHOOSE_COUNTRY;
                        }
                        default -> {
                            inlineKeyboardMarkup = getWeatherInline();
                            messageFromBot = getWeatherInfoFromCityName(callData);
                            currentState.replace(chatId, UserState.WEATHER);
                        }
                    }
                }
                case WEATHER -> {
                    if (BACK_BUTTON.equals(callData)) {
                        inlineKeyboardMarkup = getCitiesInlinePageByIndex(userCountryName.get(chatId), currentCityPage.get(chatId));
                        currentState.replace(chatId, UserState.CITIES);
                        messageFromBot = CHOOSE_CITY + " of " + userCountryName.get(chatId);
                    }
                }
            }

            try {
                execute(editMenu(inlineKeyboardMarkup, message, messageFromBot));
            } catch (TelegramApiException e) {
                System.out.println("edit inline keyboard error");
            }
        }
    }

    private EditMessageText editMenu(InlineKeyboardMarkup i, Message message, String messageFromBot) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText(messageFromBot);
        editMessageText.setReplyMarkup(i);
        editMessageText.setMessageId(message.getMessageId()); // asosiy
        editMessageText.setChatId(message.getChatId().toString());
        return editMessageText;
    }

    void execute(InlineKeyboardMarkup i, String chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);
        sendMessage.setReplyMarkup(i);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("execute da error");
            e.printStackTrace();
        }
    }
}
