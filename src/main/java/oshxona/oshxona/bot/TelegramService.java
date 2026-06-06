package oshxona.oshxona.bot;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import oshxona.oshxona.dto.order.CartItem;
import oshxona.oshxona.model.Food;
import oshxona.oshxona.model.Order;
import oshxona.oshxona.model.enums.OrderStatus;
import oshxona.oshxona.service.ClientService;
import oshxona.oshxona.service.OrderService;
import oshxona.oshxona.utils.BotUtils;
import oshxona.oshxona.validator.FoodValidator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class TelegramService {

    private final ClientService clientService;
    private final OrderService orderService;
    private final OshxonaBot oshxonaBot;
    private final ButtonMaker buttonMaker;
    private final FoodValidator foodValidator;

    public TelegramService(ClientService clientService, OrderService orderService, @Lazy OshxonaBot oshxonaBot, ButtonMaker buttonMaker, FoodValidator foodValidator) {
        this.clientService = clientService;
        this.orderService = orderService;
        this.oshxonaBot = oshxonaBot;
        this.buttonMaker = buttonMaker;
        this.foodValidator = foodValidator;
    }

    public int getFoodCount(String chatId, String foodCode) {
        return orderService.getFoodCount(chatId, foodCode);
    }

    public void cart(String chatId) {
        Map<String, CartItem> cart = orderService.getCart(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (cart == null || cart.isEmpty()) {
            sendMessage.setText(BotUtils.CARD_IS_EMPTY);
            oshxonaBot.sendMessage(sendMessage);
            return;
        }

        StringBuilder builder = new StringBuilder("<b>🛒 Mening savatim</b>\n\n");
        double totalPrice = 0;

        for (Map.Entry<String, CartItem> entry : cart.entrySet()) {
            String foodCode = entry.getKey();
            CartItem cartItem = entry.getValue();

            Food food = foodValidator.getFoodCode(foodCode);

            double price = food.getPrice() * cartItem.getCount();
            totalPrice += price;
            builder.append("🛍 Taom: ").append(food.getName()).append("\n")
                    .append("🔢 Soni: ").append(cartItem.getCount()).append(" ta").append("\n")
                    .append("🚗 Yetkazib berish bepul: 0 so'm").append("\n");

            if (cartItem.getComment() != null && !cartItem.getComment().isEmpty()) {
                builder.append("✍️ Izoh: <i>").append(cartItem.getComment()).append("</i>\n");
            }
            builder.append("────────────────\n");
        }
        builder.append("Umumiy: ").append(totalPrice).append(" so'm");

        sendMessage.setParseMode("HTML");
        sendMessage.setText(builder.toString());
        sendMessage.setReplyMarkup(buttonMaker.confirmAndClear());
        oshxonaBot.sendMessage(sendMessage);
    }

    public void calculation(String chatId, String foodCode, int number) {
        orderService.calculation(chatId, foodCode, number);
    }

    public void orders(String chatId) {
        List<Order> orders = orderService.getOrders(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (orders == null || orders.isEmpty()) {
            sendMessage.setText(BotUtils.NO_ORDERS);
            oshxonaBot.sendMessage(sendMessage);
            return;
        }

        StringBuilder builder = new StringBuilder("<b>🧾 Sizning oxirgi buyurtmalaringiz:</b>\n\n");
        double total = 0;

        for (Order order : orders) {
            double price = order.getFood().getPrice() * order.getTotalAmount();
            total += price;

            builder.append("<b>Kvitansiya:</b> <code>").append(order.getReceipt()).append("</code>\n")
                    .append("<b>Holati:</b> ( ").append(OrderStatus.statusText(order.getOrderStatus())).append(" )\n")
                    .append("🌮 ").append(order.getTotalAmount()).append(" ta ")
                    .append(order.getFood().getName()).append(" — ")
                    .append(order.getFood().getPrice()).append(" so'm\n")
                    .append("─────────────────────────\n\n");
        }
        builder.append("─────────────────────────\n")
                .append("🛍 Jami summa: ").append(total).append(" so'm\n");

        sendMessage.setParseMode("HTML");
        sendMessage.setText(builder.toString());
        oshxonaBot.sendMessage(sendMessage);
    }

    public void sendWelcome(Message message) {
        String chatId = message.getChatId().toString();
        clientService.registerByChatId(chatId, message.getFrom().getFirstName());

        String welcome = BotUtils.MESSAGE_FOR_WELCOME.formatted(message.getFrom().getFirstName());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(welcome);
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(buttonMaker.mainMenu());
        oshxonaBot.sendMessage(sendMessage);
    }

    public void foodMenu(Message message, SendMessage sendMessage) {
        String chatId = message.getChatId().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String sana = LocalDate.now().format(formatter);
        sendMessage.setChatId(chatId);
        sendMessage.setText(BotUtils.TODAY_DATE + sana);
        sendMessage.setReplyMarkup(buttonMaker.foodMenu());
        oshxonaBot.sendMessage(sendMessage);
    }

    public void foodAddToCart(String chatId, int count, Integer messageId) {
        SendMessage sendMessage = new SendMessage();
        if (count > 0) {
            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(messageId);
            oshxonaBot.deleteMessage(deleteMessage);
            sendMessage.setChatId(chatId);
            sendMessage.setText(BotUtils.SAVE_TO_CART);
            oshxonaBot.sendMessage(sendMessage);
        } else {
            sendMessage.setChatId(chatId);
            sendMessage.setText(BotUtils.CHOOSE_AMOUNT);
            oshxonaBot.sendMessage(sendMessage);
        }
    }

    public void editCart(String chatId, Integer messageId) {
        orderService.clearCart(chatId);
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText(BotUtils.CLEANED);
        oshxonaBot.editMessageText(edit);
    }

    public void orderConfirmed(String chatId, Integer messageId) {
        orderService.saveOrder(chatId);
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText(BotUtils.CONFIRM);
        oshxonaBot.editMessageText(edit);
        SendMessage contactMessage = new SendMessage();
        contactMessage.setChatId(chatId);
        contactMessage.setText(BotUtils.SHARE_CONTACT);
        contactMessage.setReplyMarkup(buttonMaker.shareContact());
        oshxonaBot.sendMessage(contactMessage);
    }

    public void saveUserContact(String chatId, String phoneNumber) {
        clientService.savePhoneNumber(chatId, phoneNumber);
        SendMessage locationMessage = new SendMessage();
        locationMessage.setChatId(chatId);
        locationMessage.setText(BotUtils.SEND_LOCATION);
        locationMessage.setReplyMarkup(buttonMaker.shareLocation());
        oshxonaBot.sendMessage(locationMessage);
    }

    public void saveUserLocation(String chatId, double latitude, double longitude) {
        clientService.saveLocation(chatId, latitude, longitude);
        SendMessage mainMenuMsg = new SendMessage();
        mainMenuMsg.setChatId(chatId);
        mainMenuMsg.setText("📍 Manzil qabul qilindi!");
        mainMenuMsg.setReplyMarkup(buttonMaker.mainMenu());
        oshxonaBot.sendMessage(mainMenuMsg);
        this.orders(chatId);
    }

    public void deleteMessage(Integer messageId, String chatId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(chatId);
        oshxonaBot.deleteMessage(deleteMessage);
    }

    public void backCart(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(BotUtils.MAIN_MENU);
        sendMessage.setReplyMarkup(buttonMaker.mainMenu());
        oshxonaBot.sendMessage(sendMessage);
    }

    public void setActiveCommentSession(String chatId, String code) {
        orderService.setActiveCommentSession(chatId, code);
        SendMessage msg = new SendMessage(chatId, BotUtils.COMMENT_ADD);
        oshxonaBot.sendMessage(msg);
    }

    public void addFoodComment(String chatId, String foodCode, String comment) {
        orderService.addCommentToCart(chatId, foodCode, comment);
    }

    public void removeActiveComment(String chatId) {
        orderService.removeActiveCommentSession(chatId);
    }
}
