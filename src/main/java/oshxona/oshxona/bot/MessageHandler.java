package oshxona.oshxona.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import oshxona.oshxona.service.OrderService;
import oshxona.oshxona.utils.BotUtils;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final TelegramService service;
    private final OrderService orderService;

    public void handle(Message message) {
        String text = message.getText();
        String chatId = message.getChatId().toString();

        if (message.hasContact()) {
            String phoneNumber = message.getContact().getPhoneNumber();
            service.saveUserContact(chatId, phoneNumber);
            return;
        } else if (message.hasLocation()) {
            double latitude = message.getLocation().getLatitude();
            double longitude = message.getLocation().getLongitude();
            service.saveUserLocation(chatId, latitude, longitude);
            return;
        }

        String activeFood = orderService.getActiveCommentFoodCode(chatId);
        if (activeFood != null) {
            service.addFoodComment(chatId, activeFood, text);
            service.removeActiveComment(chatId);
            service.cart(chatId);
            return;
        }

        switch (text) {
            case "/start" -> service.sendWelcome(message);
            case BotUtils.MENU -> service.foodMenu(message, new SendMessage());
            case BotUtils.CART -> service.cart(chatId);
            case BotUtils.ORDER -> service.orders(chatId);
            case BotUtils.BACK -> service.backCart(chatId);
            default -> {
                service.deleteMessage(message.getMessageId(), chatId);
            }
        }
    }
}
