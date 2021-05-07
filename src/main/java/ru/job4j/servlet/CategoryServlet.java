package ru.job4j.servlet;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.job4j.model.Category;
import ru.job4j.store.HiberStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CategoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Category> categories = HiberStore.instOf().findAllCategory();
        resp.setContentType("json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = new PrintWriter(resp.getOutputStream(), true, StandardCharsets.UTF_8);
        JSONArray ar = new JSONArray();
        for (Category category :  categories) {
            JSONObject json = new JSONObject();
            json.put("idCat", category.getId());
            json.put("nameCat", category.getName());
            ar.put(json);
        }
        writer.println(ar);
    }
}
