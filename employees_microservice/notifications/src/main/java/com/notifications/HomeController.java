package com.notifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    KafkaConsumer consumer;

    @RequestMapping("/")
    public String home(Model m) {

        List<String> messages = consumer.getList();

        if (messages.isEmpty()) {
            return "no-messages";
        }

        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder().append("Sent notification to emails: \n");
        StringBuilder names = new StringBuilder().append("That new employees were hired: \n");

        try {
            URL url = new URL("http://localhost:8080/employees_war_exploded/employees/emails");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader input = new BufferedReader(
                                    new InputStreamReader(connection.
                                                    getInputStream()));
            String line = "";
            StringBuffer content = new StringBuffer();

            while (null != (line= input.readLine())) {
                content.append(line);
            }

            ObjectNode[] emailList = objectMapper.readValue(content.toString(), ObjectNode[].class);

            for (ObjectNode node : emailList) {
                if (node.has("email")) {
                    result.append(node.get("email"));
                    result.append(System.lineSeparator());
                }
            }

            for (String name : messages) {
                names.append(name);
                names.append(System.lineSeparator());
            }
            messages.clear();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }

        m.addAttribute("emailList", result.toString());
        m.addAttribute("names", names.toString());

        return "home";
    }
}
