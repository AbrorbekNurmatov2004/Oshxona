package oshxona.oshxona.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import oshxona.oshxona.model.Food;
import oshxona.oshxona.validator.FoodValidator;

@Component
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final TelegramService service;
    private final ButtonMaker buttonMaker;
    private final FoodValidator foodValidator;

    public void handle(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String chatId = callbackQuery.getMessage().getChatId().toString();

        if (data.startsWith("food:")) {
            String replace = data.replace("food:", "");
            Food food = foodValidator.getFoodCode(replace);
            buttonMaker.sendFood(chatId, food);
        } else if (data.startsWith("plus:")) {
            String code = data.replace("plus:", "");
            service.calculation(chatId, code, 1);
            Food food = foodValidator.getFoodCode(code);
            buttonMaker.editFood(chatId, messageId, food);
        } else if (data.startsWith("minus:")) {
            String code = data.replace("minus:", "");
            int count = service.getFoodCount(chatId, code);
            if (count > 0) {
                service.calculation(chatId, code, -1);
            }
            Food food = foodValidator.getFoodCode(code);
            buttonMaker.editFood(chatId, messageId, food);
        } else if (data.startsWith("addCart:")) {
            String code = data.replace("addCart:", "");
            int count = service.getFoodCount(chatId, code);
            service.foodAddToCart(chatId, count, messageId);
        } else if (data.startsWith("clear:")) {
            service.editCart(chatId, messageId);
        } else if (data.startsWith("true:")) {
            service.orderConfirmed(chatId, messageId);
        } else if (data.startsWith("comment:")) {
            String code = data.replace("comment:", "");
            service.setActiveCommentSession(chatId, code);
        }
    }
}