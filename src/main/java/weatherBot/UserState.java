package weatherBot;

import org.telegram.telegrambots.meta.api.objects.User;

public enum UserState {
    COUNTRIES,
    CITIES,
    WEATHER;

    static UserState next(UserState currentState){
        int ordinal = currentState.ordinal();
        return UserState.values()[ordinal + 1];
    }

}
