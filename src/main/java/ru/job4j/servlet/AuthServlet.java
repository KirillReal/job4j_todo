package ru.job4j.servlet;

import ru.job4j.model.User;
import ru.job4j.store.UserServStore;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, IOException {
        String email = req.getParameter("email");
        req.setCharacterEncoding("UTF-8");
        User user = UserServStore.instOf().findByEmailUser(email);
        if (user == null) {
            req.setAttribute("error", "email not exist");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
        String password = req.getParameter("password");
        assert user != null;
        if (!user.getPassword().equals(password)) {
            req.setAttribute("error", "Неправильный пароль");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
        HttpSession sc = req.getSession();
        sc.setAttribute("user", user);
        resp.sendRedirect(req.getContextPath() + "/index.html");
    }
}
