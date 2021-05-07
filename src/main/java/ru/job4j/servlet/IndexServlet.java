package ru.job4j.servlet;

import org.json.JSONArray;
import org.json.JSONObject;
import ru.job4j.model.Category;
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
            JSONArray arCat = new JSONArray();
            for (Category category :  task.getCategories()) {
                arCat.put(category.getName());
            }
            json.put("categories", arCat);
            json.put("userName", task.getUser().getName());
            ar.put(json);
        }
        writer.println(ar);
        writer.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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
            Item item = new Item(description, false, user);
            System.out.println(req.getParameter("description"));
            String[] array = req.getParameterValues("categories[]");
            for (String s : array) {
                item.addCat(HiberStore.instOf().findByIdCategory(Integer.parseInt(s)));
            }
            item = HiberStore.instOf().create(item);
            PrintWriter writer = new PrintWriter(resp.getOutputStream(),
                    true, StandardCharsets.UTF_8);
            JSONObject json = new JSONObject();
            json.put("idTask", item.getId());
            json.put("description", item.getDescription());
            json.put("created", item.getCreated());
            json.put("userName", item.getUser().getName());
            JSONArray arCat = new JSONArray();
            for (Category category :  item.getCategories()) {
                arCat.put(category.getName());
            }
            json.put("categories", arCat);
            json.put("done", item.isDone());
            writer.println(json);
            writer.flush();
        }
    }
}
