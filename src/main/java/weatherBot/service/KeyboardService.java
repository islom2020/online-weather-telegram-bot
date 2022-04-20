package weatherBot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import weatherBot.WeatherInterface;

import java.util.ArrayList;
import java.util.List;

import static weatherBot.service.ApiService.getCitiesByCountryName;
import static weatherBot.service.ApiService.getCountryListInString;

public class KeyboardService implements WeatherInterface {

    public static InlineKeyboardMarkup getWeatherInline() {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        list.add(getBackButtonRow());
        inlineKeyboardMarkup.setKeyboard(list);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getCitiesInlinePageByIndex(String countryName, int index) {
        return getCitiesInlinePagesByCountryName(countryName).get(index);
    }

    public static InlineKeyboardMarkup getCountriesInlinePageByIndex(int index) {
        return getCountriesInlinePages().get(index);
    }

    public static List<InlineKeyboardMarkup> getCountriesInlinePages() {
        return getInlinePagesByList(getCountryListInString(), false);
    }

    public static List<InlineKeyboardMarkup> getCitiesInlinePagesByCountryName(String countryName) {
        return getInlinePagesByList(getCitiesByCountryName(countryName), true);
    }

    private static List<InlineKeyboardMarkup> getInlinePagesByList(List<String> list, boolean isCities) {

        List<InlineKeyboardMarkup> pages = new ArrayList<>();
        List<List<InlineKeyboardButton>> page = new ArrayList<>();
        List<InlineKeyboardButton> listRow = new ArrayList<>();

        int index = 1;
        int size = list.size();
        for (String data : list) {

            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(data);
            inlineKeyboardButton.setCallbackData(data);
            listRow.add(inlineKeyboardButton);

            if (index == size) {
                listRow = new ArrayList<>();
                if (pages.size() != 0){
                    page.add(getPreviousButtonRow());
                }
                if (isCities) {
                    page.add(getBackButtonRow());
                }
                pages.add(new InlineKeyboardMarkup(page));
            } else if (listRow.size() == 3) {
                page.add(listRow);
                listRow = new ArrayList<>();
                if (page.size() == 10) {
                    if (pages.size() == 0) {
                        page.add(getNextButtonRow());
                    } else {
                        page.add(getPreviousNextButtonRow());
                    }
                    if (isCities) {
                        page.add(getBackButtonRow());
                    }
                    pages.add(new InlineKeyboardMarkup(page));
                    page = new ArrayList<>();
                }
            }
            index++;
        }
        return pages;
    }

    private static List<InlineKeyboardButton> getPreviousButtonRow() {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(PREVIOUS_BUTTON);
        inlineKeyboardButton.setCallbackData(PREVIOUS_BUTTON);
        return List.of(inlineKeyboardButton);
    }

    private static List<InlineKeyboardButton> getNextButtonRow() {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(NEXT_BUTTON);
        inlineKeyboardButton.setCallbackData(NEXT_BUTTON);
        return List.of(inlineKeyboardButton);
    }

    private static List<InlineKeyboardButton> getPreviousNextButtonRow() {
        InlineKeyboardButton inlineKeyboardButtonPrevious = new InlineKeyboardButton(PREVIOUS_BUTTON);
        inlineKeyboardButtonPrevious.setCallbackData(PREVIOUS_BUTTON);
        InlineKeyboardButton inlineKeyboardButtonNext = new InlineKeyboardButton(NEXT_BUTTON);
        inlineKeyboardButtonNext.setCallbackData(NEXT_BUTTON);
        return List.of(inlineKeyboardButtonPrevious, inlineKeyboardButtonNext);
    }

    private static List<InlineKeyboardButton> getBackButtonRow() {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(BACK_BUTTON);
        inlineKeyboardButton.setCallbackData(BACK_BUTTON);
        return List.of(inlineKeyboardButton);
    }
}
