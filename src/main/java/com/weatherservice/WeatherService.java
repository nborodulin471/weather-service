package com.weatherservice;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    private static final String API_KEY = "cda9fb31-d828-440e-a4be-60122dfbe24e"; // апи ключ
    private static final String BASE_URL = "https://api.weather.yandex.ru/v2/forecast";
    private static final double LAT_EXAMPLE = 55.75; // Широта
    private static final double LON_EXAMPLE = 37.62; // Долгота
    private static final int LIMIT_EXAMPLE = 7;

    public static void main(String[] args) {
        // Сделайте GET запрос используя путь: https://api.weather.yandex.ru/v2/forecast.
        // Передайте координаты точки lat и lon, в которой хотите определить погоду,
        // например: https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62.
        var weatherData = getWeatherData(String.format("%s?lat=%s&lon=%s", BASE_URL, LAT_EXAMPLE, LON_EXAMPLE));

        // Выведите на экран все данные (весь ответ от сервиса в формате json) и отдельно температуру (находится в fact {temp}).
        System.out.printf("Все данные: %s%n", (weatherData.toString(2)));
        int temperature = weatherData.getJSONObject("fact").getInt("temp");
        System.out.println("Температура: " + temperature + "°C");

        // Вычислить среднюю температуру за определенный период (передать limit и найти среднее арифметическое температуры).
        var averageTemperatureResponse = getWeatherData(String.format("%s?lat=%s&lon=%s&limit=%s", BASE_URL, LAT_EXAMPLE, LON_EXAMPLE, LIMIT_EXAMPLE));
        var averageTemperature = calculateAverageTemperature(averageTemperatureResponse);
        System.out.println("Средняя температура за " + LIMIT_EXAMPLE + " дней: " + averageTemperature + "°C");
    }

    private static JSONObject getWeatherData(String request) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Yandex-Weather-Key", API_KEY);

            if (connection.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            } else {
                System.out.println("Ошибка: " + connection.getResponseCode() + " - " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(response.toString());
    }

    private static double calculateAverageTemperature(JSONObject jsonResponse) {
        double totalTemperature = 0;
        int count = 0;

        for (int i = 0; i < jsonResponse.getJSONArray("forecasts").length(); i++) {
            int temp = jsonResponse.getJSONArray("forecasts").getJSONObject(i).getJSONObject("parts").getJSONObject("day").getInt("temp_avg");
            totalTemperature += temp;
            count++;
        }

        return count == 0 ? 0 : totalTemperature / count;
    }
}

