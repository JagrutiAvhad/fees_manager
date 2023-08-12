package pros;

// ... (imports)
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@WebServlet(urlPatterns = "/Payment1")
@MultipartConfig
public class PaymentServlet1 extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/hostel";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "JagAvh@2019";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if the studentId and amountPaid parameters are not null or empty
        String studentIdStr = request.getParameter("studentId");
        String amountPaidStr = request.getParameter("amountPaid");

        if (studentIdStr == null || studentIdStr.isEmpty() || amountPaidStr == null || amountPaidStr.isEmpty()) {
            showErrorPage(response, "Invalid parameters. Please provide both studentId and amountPaid.");
            return;
        }

        int studentId;
        double amountPaid;

        try {
            studentId = Integer.parseInt(studentIdStr);
            amountPaid = Double.parseDouble(amountPaidStr);
        } catch (NumberFormatException e) {
            showErrorPage(response, "Invalid parameters. Please provide valid numeric values for studentId and amountPaid.");
            return;
        }

        try {
            // Connect to the database
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

                // Update the fees_paid column in the database
                String updateQuery = "UPDATE hostelfees SET fees_paid = fees_paid + ? WHERE roll_no = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setDouble(1, amountPaid);
                    updateStatement.setInt(2, studentId);
                    int rowsAffected = updateStatement.executeUpdate();

                    // Check if the update was successful
                    if (rowsAffected > 0) {
                        // Fetch the updated student details
                        String selectQuery = "SELECT name, total_fee, fees_paid FROM hostelfees WHERE roll_no = ?";
                        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                            selectStatement.setInt(1, studentId);
                            try (ResultSet resultSet = selectStatement.executeQuery()) {
                                if (resultSet.next()) {
                                    String studentName = resultSet.getString("name");
                                    double totalFee = resultSet.getDouble("total_fee");
                                    double feesPaid = resultSet.getDouble("fees_paid");
                                    double remainingBalance = totalFee - feesPaid;

                                    request.setAttribute("studentName", studentName);
                                    request.setAttribute("totalFee", totalFee);
                                    request.setAttribute("amountPaid", amountPaid);
                                    request.setAttribute("remainingBalance", remainingBalance);

                                    request.getRequestDispatcher("receipt.jsp").forward(request, response);
                                } else {
                                    showErrorPage(response, "Student with ID " + studentId + " not found.");
                                }
                            }
                        }
                    } else {
                        // Update failed
                        showErrorPage(response, "Payment failed. Please try again.");
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
