package com.luxakara.soft.weatherbot;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.luxakara.soft.enums.TelegramTextStyled;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WeatherController {
	
	@RequestMapping("/weather")
	public String weather(Update update, @RequestBody String cityName) {
		if (cityName != null && !cityName.isBlank()) {
            try {
                String urlString = "https://vi.wttr.in/" + URLEncoder.encode(cityName, StandardCharsets.UTF_8) + "?m?T?tqp0";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlString))
                        .GET()
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String body = response.body();
                String result = body.substring(body.indexOf("<pre>") + 5, body.indexOf("</pre>"));
                log.info(result);
                return wrapByTag(result, TelegramTextStyled.CODE);
            }
            catch (Exception ex) {
                log.error("Error get weather :", ex);
                return "Error get weather :" + ex.getMessage();
            }
        }
        else {
            return "Please type city name !";
        }
	}
	
	private static String wrapByTag(String raw, TelegramTextStyled styled) {
        return styled.getOpenTag() + raw + styled.getCloseTag();
    }
	
	private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
}
