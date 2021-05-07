package ru.job4j.servlet;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.job4j.model.Item;
import ru.job4j.model.User;
import ru.job4j.store.HiberStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IndexServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        String description = req.getParameter("description");
        PrintWriter writer = new PrintWriter(resp.getOutputStream(), true, StandardCharsets.UTF_8);
        JSONArray ar = new JSONArray();
        List<Item> tasks = (List<Item>) HiberStore.instOf().findAll();
        for (Item task :  tasks) {
            JSONObject json = new JSONObject();
            json.put("idTask", task.getId());
            json.put("description", task.getDescription());
            json.put("created", task.getCreated());
            json.put("done", task.isDone());
            json.put("userName", task.getUser().getName());
            ar.put(json);
        }
        writer.println(ar);
        writer.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println(req.getParameter("idTask"));
        if (req.getParameter("idTask") != null)  {
            int id = Integer.parseInt(req.getParameter("idTask"));
            Item item = HiberStore.instOf().findById(id);
            item.setFinished(true);
            HiberStore.instOf().update(id, item);
        } else {
            resp.setContentType("json");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");
            String description = req.getParameter("description");
            User user = (User) req.getSession().getAttribute("user");
            System.out.println(user.getName());
            Item item = HiberStore.instOf().create(
                    new Item(description, false, user));
            PrintWriter writer = new PrintWriter(resp.getOutputStream(),
                    true, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject();
            json.put("idTask", item.getId());
            json.put("description", item.getDescription());
            json.put("created", item.getCreated());
            json.put("userName", item.getUser().getName());
            json.put("done", item.isDone());
            writer.println(json);
            writer.flush();
        }
    }
}
