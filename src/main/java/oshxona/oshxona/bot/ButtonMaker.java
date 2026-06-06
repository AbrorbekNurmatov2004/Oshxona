package oshxona.oshxona.bot;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import oshxona.oshxona.model.FileEntity;
import oshxona.oshxona.model.Food;
import oshxona.oshxona.repository.FileRepository;
import oshxona.oshxona.repository.FoodRepository;
import oshxona.oshxona.service.OrderService;
import oshxona.oshxona.utils.ErrorConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static oshxona.oshxona.utils.BotUtils.*;

@Component
public class ButtonMaker {

    private final FoodRepository repository;
    private final OshxonaBot oshxonaBot;
    private final OrderService service;
    private final FileRepository fileRepository;

    public ButtonMaker(FoodRepository repository, @Lazy OshxonaBot oshxonaBot, OrderService service, FileRepository fileRepository) {
        this.repository = repository;
        this.oshxonaBot = oshxonaBot;
        this.service = service;
        this.fileRepository = fileRepository;
    }

    public ReplyKeyboard mainMenu() {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(MENU);
        row1.add(CART);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(ORDER);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        keyboardRows.add(row1);
        keyboardRows.add(row2);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    public InlineKeyboardMarkup foodMenu() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<Food> foods = repository.findAllByActiveTrue();

        for (Food food : foods) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(food.getName());
            button.setCallbackData("food:" + food.getCode());

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            rows.add(row);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup confirmAndClear() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(CLEAN_CART);
        button1.setCallbackData("clear:");

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(CONFIRM_ORDER);
        button2.setCallbackData("true:");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(button1);
        row.add(button2);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public void sendFood(String chatId, Food food) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);

        FileEntity currentFileEntity = null;

        try {
            String image = food.getImage();
            File imageFile = null;

            if (image != null && image.contains("/profile/download/")) {
                String id = image.substring(image.lastIndexOf("/") + 1);
                Optional<FileEntity> entity = fileRepository.findById(id);

                if (entity.isPresent()) {
                    currentFileEntity = entity.get();

                    if (currentFileEntity.getTelegramFileId() != null) {
                        sendPhoto.setPhoto(new InputFile(currentFileEntity.getTelegramFileId()));
                    } else {
                        String diskPath = currentFileEntity.getPath();
                        imageFile = new File(diskPath);
                    }
                }
            }

            if (sendPhoto.getPhoto() == null) {
                if (imageFile != null && imageFile.exists()) {
                    sendPhoto.setPhoto(new InputFile(imageFile));
                } else {
                    sendPhoto.setPhoto(new InputFile(NO_PICTURE));
                }
            }

        } catch (Exception e) {
            System.out.println(ErrorConstants.s_NOT_FOUND.formatted("Photo") + e.getMessage());
            sendPhoto.setPhoto(new InputFile(NO_PICTURE));
        }

        sendPhoto.setCaption("🍔 <b>" + food.getName() + "</b>\n\n\n" +
                "📄 " + food.getDescription() + "\n\n\n" +
                "💰 " + food.getPrice() + " so'm");
        sendPhoto.setParseMode("HTML");
        sendPhoto.setReplyMarkup(quantityAndCart(food, chatId));

        Message executedMessage = oshxonaBot.sendPhoto(sendPhoto);

        if (executedMessage != null && executedMessage.hasPhoto() && currentFileEntity != null && currentFileEntity.getTelegramFileId() == null) {
            int photoSizeCount = executedMessage.getPhoto().size();
            String telegramFileId = executedMessage.getPhoto().get(photoSizeCount - 1).getFileId();

            currentFileEntity.setTelegramFileId(telegramFileId);
            fileRepository.save(currentFileEntity);
        }
    }

    public InlineKeyboardMarkup quantityAndCart(Food food, String chatId) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        int count = service.getFoodCount(chatId, food.getCode());

        InlineKeyboardButton minusBtn = new InlineKeyboardButton();
        minusBtn.setText("➖");
        minusBtn.setCallbackData("minus:" + food.getCode());

        InlineKeyboardButton countBtn = new InlineKeyboardButton();
        countBtn.setText(String.valueOf(count));
        countBtn.setCallbackData("count:");

        InlineKeyboardButton plusBtn = new InlineKeyboardButton();
        plusBtn.setText("➕");
        plusBtn.setCallbackData("plus:" + food.getCode());

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText(COMMENT);
        button1.setCallbackData("comment:" + food.getCode());

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText(ADD_CART);
        button2.setCallbackData("addCart:" + food.getCode());

        row.add(minusBtn);
        row.add(countBtn);
        row.add(plusBtn);
        row1.add(button1);
        row2.add(button2);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row);
        rows.add(row1);
        rows.add(row2);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    public void editFood(String chatId, Integer messageId, Food food) {
        EditMessageCaption editCaption = new EditMessageCaption();
        editCaption.setChatId(chatId);
        editCaption.setMessageId(messageId);

        editCaption.setCaption("🍔 <b>" + food.getName() + "</b>\n\n\n" +
                "📄 " + food.getDescription() + "\n\n\n" +
                "💰 " + food.getPrice() + " so'm"
        );
        editCaption.setParseMode("HTML");
        editCaption.setReplyMarkup(quantityAndCart(food, chatId));
        oshxonaBot.editCaption(editCaption);
    }

    public ReplyKeyboard shareContact() {
        KeyboardButton backButton = new KeyboardButton();
        backButton.setText(BACK);

        KeyboardButton phoneButton = new KeyboardButton();
        phoneButton.setText(SEND_PHONE_NUMBER);
        phoneButton.setRequestContact(true);

        KeyboardRow row = new KeyboardRow();
        row.add(backButton);
        row.add(phoneButton);

        List<KeyboardRow> list = new ArrayList<>();
        list.add(row);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setOneTimeKeyboard(false);
        markup.setResizeKeyboard(true);
        markup.setKeyboard(list);
        return markup;
    }

    public ReplyKeyboard shareLocation() {
        KeyboardButton backButton = new KeyboardButton();
        backButton.setText(BACK);

        KeyboardButton locationButton = new KeyboardButton();
        locationButton.setText(LOCATION);
        locationButton.setRequestLocation(true);

        KeyboardRow row = new KeyboardRow();
        row.add(backButton);
        row.add(locationButton);

        List<KeyboardRow> list = new ArrayList<>();
        list.add(row);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setOneTimeKeyboard(false);
        markup.setResizeKeyboard(true);
        markup.setKeyboard(list);
        return markup;
    }
}
