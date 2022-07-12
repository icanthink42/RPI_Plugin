package icanthink.rpiplugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class MathQuestionGenerator {

    static String generate_quadratic(int terms) {
        Random rand = new Random();
        String quadratic = "";
        for (int i = 0; i < terms + 1; i++) {
            quadratic += "(x^" + (rand.nextInt(8) + 1) + ") + ";
        }
        quadratic = quadratic.substring(0, quadratic.length() - 3);
        return quadratic;
    }
    static String generate_math_problem() {
        Random rand = new Random();
        int type = rand.nextInt(3);
        if (type == 0) {
            return generate_quadratic_derivative();
        } else if (type == 1) {
            return generate_quadratic_integral();
        } else {
            return generate_dot_product();
        }
    }
    static String generate_quadratic_integral() {
        Random rand = new Random();
        String question = "Find the definite integral of ";
        question += generate_quadratic(rand.nextInt(2));
        question += " from " + rand.nextInt(5) + " to " + rand.nextInt(5);
        return question;
    }
    static String generate_quadratic_derivative() {
        Random rand = new Random();
        String question = "Find f'(" + (rand.nextInt(10) - 5) + ") if f(x)=";
        question += generate_quadratic(rand.nextInt(2));
        return question;
    }

    static String generate_dot_product() {
        Random rand = new Random();
        String question = "Whats the dot product of <" + (rand.nextInt(60) - 30) + ", " + (rand.nextInt(60) - 30) + "> and <" + (rand.nextInt(60) - 30) + ", " + (rand.nextInt(60) - 30) + ">";
        return question;
    }

    static String solve_equation(String question, boolean shorten) {
        try {
            question = URLEncoder.encode(question, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            Bukkit.getLogger().warning("Failed to url encode question!");
        }
        String json_string = RpiPlugin.get("https://api.wolframalpha.com/v2/query?input=" + question + "&format=plaintext&output=JSON&appid=" + RpiPlugin.wolf_key);
        JsonElement jsonElement = JsonParser.parseString(json_string);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray pods = jsonObject.get("queryresult").getAsJsonObject().get("pods").getAsJsonArray();
        JsonObject pod = new JsonObject();
        for (int i = 0; i < pods.size(); i++) {
            if (pods.get(i).getAsJsonObject().get("primary") != null && pods.get(i).getAsJsonObject().get("primary").toString().equals("true")) {
                pod = pods.get(i).getAsJsonObject();
            }
        }
        String str_answer = pod.get("subpods").getAsJsonArray().get(0).getAsJsonObject().get("plaintext").toString();
        str_answer = str_answer.replace("\"", "");
        if (shorten) {
            str_answer = str_answer.replace(" ", "");
            str_answer = str_answer.replace("x", "*");
            str_answer = str_answer.replace("−", "-");
            if (str_answer.contains("≈")) {
                str_answer = str_answer.substring(str_answer.lastIndexOf("≈") + 1);
            }
            if (str_answer.contains("=")) {
                str_answer = str_answer.substring(str_answer.lastIndexOf("=") + 1);
            }
        }
        return str_answer;
    }
}
