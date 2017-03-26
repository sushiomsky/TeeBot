package org.telegram.updateshandlers;

import org.telegram.BotConfig;
import org.telegram.commands.BetCommand;
import org.telegram.commands.DepositCommand;
import org.telegram.commands.HelpCommand;
import org.telegram.database.DatabaseManager;
import org.telegram.services.Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * Created by Dennis Suchomsky on 23.03.17.
 */
public class DoubleOrNothingHandler extends TelegramLongPollingCommandBot {


        public static final String LOGTAG = "DOUBLEORNOTHINGHANDLER";

        /**
         * Constructor.
         */
        public DoubleOrNothingHandler() {
            register(new BetCommand());
            register(new DepositCommand());
            HelpCommand helpCommand = new HelpCommand(this);
            register(helpCommand);

            registerDefaultAction((absSender, message) -> {
                SendMessage commandUnknownMessage = new SendMessage();
                commandUnknownMessage.setChatId(message.getChatId());
                commandUnknownMessage.setText("The command '" + message.getText() + "' is not known by this bot. Here comes some help " + Emoji.AMBULANCE);
                try {
                    absSender.sendMessage(commandUnknownMessage);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
                helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[]{});
            });
        }

        @Override
        public void processNonCommandUpdate(Update update) {

            if (update.hasMessage()) {
                Message message = update.getMessage();


                if (!DatabaseManager.getInstance().getUserStateForCommandsBot(message.getFrom().getId())) {
                    return;
                }

                if (message.hasText()) {
                    SendMessage echoMessage = new SendMessage();
                    echoMessage.setChatId(message.getChatId());
                    echoMessage.setText("Hey heres your message:\n" + message.getText());

                    try {
                        sendMessage(echoMessage);
                    } catch (TelegramApiException e) {
                        BotLogger.error(LOGTAG, e);
                    }
                }
            }
        }

        @Override
        public String getBotUsername() {
            return BotConfig.DOUBLEORNOTHING_USER;
        }

        @Override
        public String getBotToken() {
            return BotConfig.DOUBLEORNOTHING_TOKEN;
        }
}