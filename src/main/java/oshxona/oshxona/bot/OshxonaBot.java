package oshxona.oshxona.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import oshxona.oshxona.config.YmlData;

@Slf4j
@Component
public class OshxonaBot extends TelegramLongPollingBot {

    private final YmlData ymlData;
    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    public OshxonaBot(YmlData ymlData, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler) {
        super(ymlData.getToken());
        this.ymlData = ymlData;
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()) {
            messageHandler.handle(update.getMessage());
        } else if (update.hasCallbackQuery()){
            callbackQueryHandler.handle(update.getCallbackQuery());
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            Integer messageId = execute(sendMessage).getMessageId();
            log.info("Sending message id {} to message id {} " , messageId, messageId);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public Message sendPhoto(SendPhoto sendPhoto) {
        try {
            return execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void editCaption(EditMessageCaption editMessageCaption) {
        try {
            execute(editMessageCaption);
            log.info("Xabar caption-i muvaffaqiyatli o'zgartirildi. ChatId: {}, MessageId: {}",
                    editMessageCaption.getChatId(), editMessageCaption.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Xabar caption-ini o'zgartirishda xatolik: {}", e.getMessage());
        }
    }

    public void deleteMessage(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
            log.info("Xabar muvaffaqiyatli o'chirildi. ChatId: {}, MessageId: {}",
                    deleteMessage.getChatId(), deleteMessage.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Xabarni o'chirishda xatolik: {}", e.getMessage());
        }
    }

    public void editMessageText(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
            log.info("Xabar matni muvaffaqiyatli tahrirlandi. ChatId: {}, MessageId: {}",
                    editMessageText.getChatId(), editMessageText.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Xabar matnini tahrirlashda xatolik: {}", e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return ymlData.getUsername();
    }
}
