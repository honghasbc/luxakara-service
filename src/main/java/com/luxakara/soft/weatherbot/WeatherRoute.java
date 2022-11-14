package com.luxakara.soft.weatherbot;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@Component
public class WeatherRoute extends TelegramLongPollingBot {

	private final String botToken;
    private final String botUsername;
    private final WeatherService weatherService;

    WeatherRoute(@Value("${luxakara.bot.token}") String botToken, @Value("${luxakara.bot.username}") String botUsername, WeatherService weatherService) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.weatherService = weatherService;
    }

    @Override
    public void onUpdateReceived(Update update) {
    	Message message = update.getMessage();

		Long chatId = message.getChatId();
		String text = message.getText();

		log.info("Chat id:" + chatId);
		log.info("Text : " + text);

		int indexOf = text.indexOf(" ");

		if (indexOf > -1) {

			SendMessage sendMessage = new SendMessage();
			
			if (text.startsWith("/weather")) {
				String city = text.substring(indexOf);
				sendMessage.setChatId(chatId.toString());
				sendMessage.setReplyToMessageId(message.getMessageId());
				log.info("Text city : {}" + city);
				sendMessage.setText(weatherService.weather(city));
				
			} else {
				sendMessage.setChatId(chatId.toString());
				sendMessage.setReplyToMessageId(message.getMessageId());
				sendMessage.setText("Hello sir, can I help you ?");
			}
			try {
                execute(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
		}else {
			SendMessage sendMessage = new SendMessage();
			sendMessage.setChatId(chatId.toString());
			sendMessage.setReplyToMessageId(message.getMessageId());
			sendMessage.setText("Hello sir, can I help you ?");
		    try {
		        execute(sendMessage);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
    }

    @PostConstruct
    public void start() {
       log.info("{} started Successfully", getBotUsername());
    }


}