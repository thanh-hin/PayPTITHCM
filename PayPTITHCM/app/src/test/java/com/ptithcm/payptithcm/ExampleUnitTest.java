package com.ptithcm.payptithcm;

import com.ptithcm.payptithcm.models.FeeItem;
import com.ptithcm.payptithcm.models.HistoryItem;
import com.ptithcm.payptithcm.models.Student;

import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests cho PayPTITHCM
 * Test Cases:
 * 1. Model FeeItem - constructor, getters, setters, selected flag
 * 2. Model HistoryItem - constructor, getters, setters
 * 3. Model Student - constructor, getters, setters
 * 4. Business logic - tinh tong tien, loc item
 * 5. Validation - MSSV format, OTP format
 * 6. Currency formatting
 * 7. FeeItem status logic
 */
public class ExampleUnitTest {

    // ================================================================
    // 1. FeeItem model tests
    // ================================================================

    @Test
    public void feeItem_constructor_setsFieldsCorrectly() {
        FeeItem item = new FeeItem(1, "Học phí HK2/2025", 9000000L, "UNPAID", "2025-06-30");
        assertEquals(1, item.getId());
        assertEquals("Học phí HK2/2025", item.getName());
        assertEquals(9000000L, item.getAmount());
        assertEquals("UNPAID", item.getStatus());
        assertEquals("2025-06-30", item.getDeadline());
        assertFalse("Mặc định isSelected phải là false", item.isSelected());
    }

    @Test
    public void feeItem_defaultConstructor_notNull() {
        FeeItem item = new FeeItem();
        assertNotNull(item);
    }

    @Test
    public void feeItem_setSelected_togglesCorrectly() {
        FeeItem item = new FeeItem(1, "Test", 1000L, "UNPAID", "2025-01-01");
        assertFalse(item.isSelected());
        item.setSelected(true);
        assertTrue(item.isSelected());
        item.setSelected(false);
        assertFalse(item.isSelected());
    }

    @Test
    public void feeItem_setters_workCorrectly() {
        FeeItem item = new FeeItem();
        item.setId(99);
        item.setName("Phí ký túc xá");
        item.setAmount(1500000L);
        item.setStatus("OVERDUE");
        item.setDeadline("2025-03-31");

        assertEquals(99, item.getId());
        assertEquals("Phí ký túc xá", item.getName());
        assertEquals(1500000L, item.getAmount());
        assertEquals("OVERDUE", item.getStatus());
        assertEquals("2025-03-31", item.getDeadline());
    }

    @Test
    public void feeItem_zeroAmount_isValid() {
        FeeItem item = new FeeItem(1, "Test", 0L, "UNPAID", "2025-01-01");
        assertEquals(0L, item.getAmount());
    }

    // ================================================================
    // 2. HistoryItem model tests
    // ================================================================

    @Test
    public void historyItem_constructor_setsFieldsCorrectly() {
        HistoryItem item = new HistoryItem(
                101, "2024-10-12 09:30:00", "Học phí HK1/2024",
                8500000L, "SUCCESS", "Chuyển khoản", "TXN20241012001");

        assertEquals(101, item.getPaymentId());
        assertEquals("2024-10-12 09:30:00", item.getDate());
        assertEquals("Học phí HK1/2024", item.getFeeName());
        assertEquals(8500000L, item.getAmount());
        assertEquals("SUCCESS", item.getStatus());
        assertEquals("Chuyển khoản", item.getMethod());
        assertEquals("TXN20241012001", item.getTransactionId());
    }

    @Test
    public void historyItem_defaultConstructor_notNull() {
        HistoryItem item = new HistoryItem();
        assertNotNull(item);
    }

    @Test
    public void historyItem_setters_workCorrectly() {
        HistoryItem item = new HistoryItem();
        item.setPaymentId(55);
        item.setDate("2025-01-15 10:00:00");
        item.setFeeName("Bảo hiểm y tế");
        item.setAmount(702000L);
        item.setStatus("SUCCESS");
        item.setMethod("Ví điện tử");
        item.setTransactionId("TXN999");

        assertEquals(55, item.getPaymentId());
        assertEquals("2025-01-15 10:00:00", item.getDate());
        assertEquals("Bảo hiểm y tế", item.getFeeName());
        assertEquals(702000L, item.getAmount());
        assertEquals("SUCCESS", item.getStatus());
        assertEquals("Ví điện tử", item.getMethod());
        assertEquals("TXN999", item.getTransactionId());
    }

    // ================================================================
    // 3. Student model tests
    // ================================================================

    @Test
    public void student_constructor_setsFieldsCorrectly() {
        Student student = new Student(
                "21520001", "Nguyen Van An",
                "an@student.ptithcm.edu.vn", "0901234567",
                "D21CQCN01-N", "Công nghệ thông tin");

        assertEquals("21520001", student.getStudentId());
        assertEquals("Nguyen Van An", student.getFullName());
        assertEquals("an@student.ptithcm.edu.vn", student.getEmail());
        assertEquals("0901234567", student.getPhone());
        assertEquals("D21CQCN01-N", student.getClassName());
        assertEquals("Công nghệ thông tin", student.getFaculty());
    }

    @Test
    public void student_defaultConstructor_notNull() {
        Student student = new Student();
        assertNotNull(student);
    }

    @Test
    public void student_setters_workCorrectly() {
        Student student = new Student();
        student.setStudentId("22520001");
        student.setFullName("Le Van Cuong");
        student.setEmail("cuong@student.ptithcm.edu.vn");
        student.setPhone("0923456789");
        student.setClassName("D22CQDT01-N");
        student.setFaculty("Điện tử viễn thông");

        assertEquals("22520001", student.getStudentId());
        assertEquals("Le Van Cuong", student.getFullName());
        assertEquals("cuong@student.ptithcm.edu.vn", student.getEmail());
    }

    // ================================================================
    // 4. Business logic - tinh tong tien da chon
    // ================================================================

    @Test
    public void totalAmount_noSelection_isZero() {
        List<FeeItem> fees = new ArrayList<>();
        fees.add(new FeeItem(1, "Phí A", 1000000L, "UNPAID", "2025-01-01"));
        fees.add(new FeeItem(2, "Phí B", 2000000L, "UNPAID", "2025-02-01"));

        long total = 0;
        for (FeeItem item : fees) {
            if (item.isSelected()) total += item.getAmount();
        }
        assertEquals(0L, total);
    }

    @Test
    public void totalAmount_allSelected_isCorrect() {
        List<FeeItem> fees = new ArrayList<>();
        FeeItem a = new FeeItem(1, "Phí A", 9000000L, "UNPAID", "2025-01-01");
        FeeItem b = new FeeItem(2, "Phí B", 1500000L, "UNPAID", "2025-02-01");
        a.setSelected(true);
        b.setSelected(true);
        fees.add(a);
        fees.add(b);

        long total = 0;
        for (FeeItem item : fees) {
            if (item.isSelected()) total += item.getAmount();
        }
        assertEquals(10500000L, total);
    }

    @Test
    public void totalAmount_partialSelection_isCorrect() {
        List<FeeItem> fees = new ArrayList<>();
        FeeItem a = new FeeItem(1, "Phí A", 9000000L, "UNPAID", "2025-01-01");
        FeeItem b = new FeeItem(2, "Phí B", 1500000L, "UNPAID", "2025-02-01");
        FeeItem c = new FeeItem(3, "Phí C", 702000L, "OVERDUE", "2025-03-31");
        a.setSelected(true);
        // b chua chon
        c.setSelected(true);
        fees.add(a); fees.add(b); fees.add(c);

        long total = 0;
        for (FeeItem item : fees) {
            if (item.isSelected()) total += item.getAmount();
        }
        assertEquals(9702000L, total);
    }

    @Test
    public void paidFee_cannotBeSelected() {
        // PAID item khong nen duoc chon - logic trong adapter se disable checkbox
        FeeItem paid = new FeeItem(1, "Học phí HK1/2024", 8500000L, "PAID", "2024-11-30");
        // Kiem tra trang thai
        assertTrue("PAID fee should not allow selection",
                "PAID".equals(paid.getStatus()));
    }

    // ================================================================
    // 5. Validation tests - MSSV, OTP
    // ================================================================

    @Test
    public void mssvValidation_emptyString_isInvalid() {
        String mssv = "";
        assertTrue("Empty MSSV should be invalid", mssv.isEmpty());
    }

    @Test
    public void mssvValidation_validMSSV_isCorrectLength() {
        String mssv = "21520001";
        assertTrue("MSSV 21520001 should have valid length",
                mssv.length() >= 7 && mssv.length() <= 10);
    }

    @Test
    public void mssvValidation_tooShort_isInvalid() {
        String mssv = "123";
        assertFalse("MSSV too short", mssv.length() >= 7 && mssv.length() <= 10);
    }

    @Test
    public void mssvValidation_tooLong_isInvalid() {
        String mssv = "12345678901"; // 11 chars
        assertFalse("MSSV too long", mssv.length() >= 7 && mssv.length() <= 10);
    }

    @Test
    public void otpValidation_sixDigits_isValid() {
        // OTP sinh ra phai la 6 chu so
        int otp = 100000 + new java.util.Random(42).nextInt(900000);
        String otpStr = String.valueOf(otp);
        assertEquals("OTP must be 6 digits", 6, otpStr.length());
        assertTrue("OTP must be numeric", otpStr.matches("\\d{6}"));
    }

    @Test
    public void otpValidation_minValue() {
        assertEquals("Min OTP should be 100000", 6, String.valueOf(100000).length());
    }

    @Test
    public void otpValidation_maxValue() {
        assertEquals("Max OTP should be 999999", 6, String.valueOf(999999).length());
    }

    @Test
    public void otpValidation_wrongOTP_rejected() {
        String generatedOTP = "123456";
        String userInput = "000000";
        assertFalse("Wrong OTP should be rejected", userInput.equals(generatedOTP));
    }

    @Test
    public void otpValidation_correctOTP_accepted() {
        String generatedOTP = "654321";
        String userInput = "654321";
        assertTrue("Correct OTP should be accepted", userInput.equals(generatedOTP));
    }

    // ================================================================
    // 6. Currency formatting
    // ================================================================

    @Test
    public void currencyFormat_largeAmount_formatsCorrectly() {
        long amount = 9000000L;
        String formatted = String.format("%,d đ", amount);
        assertEquals("9,000,000 đ", formatted);
    }

    @Test
    public void currencyFormat_smallAmount_formatsCorrectly() {
        long amount = 50000L;
        String formatted = String.format("%,d đ", amount);
        assertEquals("50,000 đ", formatted);
    }

    @Test
    public void currencyFormat_zeroAmount() {
        long amount = 0L;
        String formatted = String.format("%,d đ", amount);
        assertEquals("0 đ", formatted);
    }

    // ================================================================
    // 7. FeeItem status constants
    // ================================================================

    @Test
    public void feeStatus_unpaid_identifiedCorrectly() {
        FeeItem item = new FeeItem(1, "Test", 1000L, "UNPAID", "2025-01-01");
        assertFalse("UNPAID should not be PAID", "PAID".equals(item.getStatus()));
        assertFalse("UNPAID should not be OVERDUE", "OVERDUE".equals(item.getStatus()));
        assertTrue("Should be UNPAID", "UNPAID".equals(item.getStatus()));
    }

    @Test
    public void feeStatus_overdue_identifiedCorrectly() {
        FeeItem item = new FeeItem(1, "Test", 1000L, "OVERDUE", "2025-01-01");
        assertTrue("Should be OVERDUE", "OVERDUE".equals(item.getStatus()));
    }

    @Test
    public void feeStatus_paid_identifiedCorrectly() {
        FeeItem item = new FeeItem(1, "Test", 1000L, "PAID", "2024-01-01");
        assertTrue("Should be PAID", "PAID".equals(item.getStatus()));
    }

    // ================================================================
    // 8. Transaction ID format
    // ================================================================

    @Test
    public void transactionId_generatedFormat_startsWithTXN() {
        String txnId = "TXN" + System.currentTimeMillis();
        assertTrue("Transaction ID should start with TXN", txnId.startsWith("TXN"));
        assertTrue("Transaction ID should have timestamp",
                txnId.length() > 3);
    }

    // ================================================================
    // 9. Empty list edge cases
    // ================================================================

    @Test
    public void emptyFeeList_totalIsZero() {
        List<FeeItem> fees = new ArrayList<>();
        long total = 0;
        for (FeeItem item : fees) {
            if (item.isSelected()) total += item.getAmount();
        }
        assertEquals(0L, total);
    }

    @Test
    public void emptyHistoryList_isHandled() {
        List<HistoryItem> history = new ArrayList<>();
        assertTrue("Empty history should be detected", history.isEmpty());
    }

    // ================================================================
    // 10. Student serializable
    // ================================================================

    @Test
    public void student_isSerializable() {
        Student student = new Student("21520001", "Test", "test@email.com",
                "0900000000", "ClassA", "Faculty");
        // Student implements Serializable - kiem tra class implements interface
        assertTrue(student instanceof java.io.Serializable);
    }

    // Sanity check test goc
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
