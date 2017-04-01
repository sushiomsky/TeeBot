package org.telegram.updateshandlers;

import org.telegram.BotConfig;
import org.telegram.commands.FortuneCommand;
import org.telegram.commands.HelpCommand;
import org.telegram.commands.StartCommand;
import org.telegram.commands.StopCommand;
import org.telegram.database.DatabaseManager;
import org.telegram.services.CustomTimerTask;
import org.telegram.services.Emoji;
import org.telegram.services.TimerExecutor;
import org.telegram.shell.UnixCommand;
import org.telegram.structure.FortuneAlert;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.logging.BotLogger;

import java.util.List;

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
		super();
		startAlertTimers();

        register(new FortuneCommand());
        register(new StartCommand());
        register(new StopCommand());
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
        if (update.hasMessage()) {
            Message message = update.getMessage();
            DatabaseManager.getInstance().logMsg(message.getFrom().getId(), message.getText());
        }

    }

	private void startAlertTimers() {
		TimerExecutor.getInstance().startExecutionEveryDayAt(new CustomTimerTask("First day alert", -1) {
			@Override
			public void execute() {
				sendAlerts();
			}
		}, 19, 30, 0);
	}

	private void sendAlerts() {
		List<FortuneAlert> allAlerts = DatabaseManager.getInstance().getAllFortuneAlerts();
		for (FortuneAlert fortuneAlert : allAlerts) {
			synchronized (Thread.currentThread()) {
				try {
					Thread.currentThread().wait(35);
				} catch (InterruptedException e) {
					BotLogger.severe(LOGTAG, e);
				}
			}
			SendMessage sendMessage = new SendMessage();
			sendMessage.enableMarkdown(true);
			sendMessage.setChatId(String.valueOf(fortuneAlert.getUserId()));
			UnixCommand fortune = new UnixCommand("fortune");
			sendMessage.setText(fortune.getOutput());
			try {
				sendMessage(sendMessage);
			} catch (TelegramApiRequestException e) {
				BotLogger.warn(LOGTAG, e);
				if (e.getApiResponse().contains("Can't access the chat") || e.getApiResponse().contains("Bot was blocked by the user")) {
					//	DatabaseManager.getInstance().deleteAlertsForUser(weatherAlert.getUserId());
					BotLogger.error(LOGTAG, e);
				}
			} catch (Exception e) {
				BotLogger.severe(LOGTAG, e);
			}
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