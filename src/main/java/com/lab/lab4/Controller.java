package com.lab.lab4;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.BlockingQueue;

@org.springframework.stereotype.Controller
public class Controller {
    final HazelcastInstance hazelcastInstance;

    private BlockingQueue<Image> queue;
    private List<String> resolved;

    public Controller(@Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) throws IOException {
        this.hazelcastInstance = hazelcastInstance;
        this.queue = hazelcastInstance.getQueue("tasks");
        this.resolved = hazelcastInstance.getList("resolved");
        hazelcastInstance.<Image>getTopic("taskSend").addMessageListener(message -> {

        });
        if(queue.isEmpty())
        collectKap();

    }


    @GetMapping("/kap")
    public String kap(Model model) {
        Image img = queue.poll();
        model.addAttribute("name", img);

        return "kap";
    }


    public void collectKap() {
        String path = System.getProperty("user.dir") + "/src/main/resources/static/img/";
        File myFolder = new File(path);
        File[] files = myFolder.listFiles();

        for (File file : files) {
            if (!file.isDirectory()) {
                queue.add(new Image("/img/" + file.getName(), 500, 500));
            }
        }

    }

}

class Image implements Serializable {
    public String path;
    public int height;
    public int width;

    public Image(String path, int height, int width) {
        this.path = path;
        this.height = height;
        this.width = width;
    }
}
