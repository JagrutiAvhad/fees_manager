package pros;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns="/Payment")
public class PaymentServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hostel";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "JagAvh@2019";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rollNumber = request.getParameter("rollNumber");

        try {
            // Connect to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                // Fetch student details from the database using the roll number
                String query = "SELECT Eid, name, total_fee, fees_paid FROM hostelfees WHERE roll_no = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, rollNumber);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        // If the student is found, proceed to payment
                        if (resultSet.next()) {
                            int studentId = resultSet.getInt("Eid");
                            String studentName = resultSet.getString("name");
                            double totalFee = resultSet.getDouble("total_fee");
                            double feesPaid = resultSet.getDouble("fees_paid");

                            request.setAttribute("studentId", studentId);
                            request.setAttribute("studentName", studentName);
                            request.setAttribute("totalFee", totalFee);
                            request.setAttribute("feesPaid", feesPaid);

                            request.getRequestDispatcher("/payment.jsp").forward(request, response);
                        } else {
                            // Student not found
                            response.setContentType("text/html");
                            PrintWriter out = response.getWriter();
                            out.println("<h1>Student with Roll Number " + rollNumber + " not found.</h1>");
                            out.close();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle SQL exceptions as needed
                showErrorPage(response, "An error occurred while processing the request.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            // Handle ClassNotFoundException as needed
            showErrorPage(response, "An error occurred while processing the request.");
        }
    }

    private void showErrorPage(HttpServletResponse response, String errorMessage) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<h1>Error: " + errorMessage + "</h1>");
        out.close();
    }
}
