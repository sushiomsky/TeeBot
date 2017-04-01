package org.telegram.updateshandlers;

import org.telegram.BotConfig;
import org.telegram.commands.HelloCommand;
import org.telegram.commands.HelpCommand;
import org.telegram.commands.StartCommand;
import org.telegram.commands.StopCommand;
import org.telegram.database.DatabaseManager;
import org.telegram.services.Emoji;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This handler mainly works with commands to demonstrate the Commands feature of the API
 *
 * @author Timo Schulz (Mit0x2)
 */
public class CommandsHandler extends TelegramLongPollingCommandBot {

    public static final String LOGTAG = "COMMANDSHANDLER";

    /**
     * Constructor.
     */
    public CommandsHandler() {
        register(new HelloCommand());
        register(new StartCommand());
        register(new StopCommand());
  //      register(new BetCommand());
 //       register(new DepositCommand());
//        register(new QuizCommand());

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
            helpCommand.execute(absSender, message.getFrom(), message.getChat(), new String[] {});
        });
    }

    @Override
    public void processNonCommandUpdate(Update update) {

        if (update.hasInlineQuery()) {

        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            DatabaseManager.getInstance().logMsg(message.getFrom().getId(), message.getText());

        /*
            if (!DatabaseManager.getInstance().getUserStateForCommandsBot(message.getFrom().getId())) {
                return;
            }
        */

            //if (message.hasText()) {
                SendMessage echoMessage = new SendMessage();
                echoMessage.setChatId(message.getChatId());

            String command = "fortune";

            Process proc = null;
            try {
                proc = Runtime.getRuntime().exec(command);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Read the output

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line = "";
            StringBuilder lines = new StringBuilder();

            try {
                while((line = reader.readLine()) != null) {
					lines.append(line + "\n");
				}
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //echoMessage.setText(DatabaseManager.getInstance().getRandomMsg());

            echoMessage.setText(lines.toString());

            try {
                    sendMessage(echoMessage);
            } catch (Exception e) {
                    BotLogger.error(LOGTAG, e);
            }
           // }
        }
    }

    @Override
    public String getBotUsername() {
        return BotConfig.COMMANDS_USER;
    }

    @Override
    public String getBotToken() {
        return BotConfig.COMMANDS_TOKEN;
    }
}