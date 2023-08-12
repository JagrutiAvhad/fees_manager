<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Student Fees Payment</title>
</head>
<body>
    <h1>Student Fees Payment</h1>
   <form action="PaymentServlet" method="post">


        <label for="studentId">Roll No:</label>
        <input type="text" id="studentId" name="studentId" required>
        
        <label for="amountPaid">Amount Paid:</label>
        <input type="text" id="amountPaid" name="amountPaid" required>
        
        <input type="submit" value="Proceed to Payment">
    </form>
</body>
</html>

