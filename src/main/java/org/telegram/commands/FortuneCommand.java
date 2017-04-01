package org.telegram.commands;

import org.telegram.shell.UnixCommand;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * This commands starts the conversation with the bot
 *
 * @author Timo Schulz (Mit0x2)
 */
public class FortuneCommand extends BotCommand {

    public static final String LOGTAG = "FORTUNECOMMAND";

    public FortuneCommand() {
        super("fortune", "fortune - print a random, hopefully interesting, adage");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
		UnixCommand fortune = new UnixCommand("fortune");
		SendMessage answer = new SendMessage();

		answer.setChatId(chat.getId().toString());
        answer.setText(fortune.getOutput());

        try {
            absSender.sendMessage(answer);
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
}