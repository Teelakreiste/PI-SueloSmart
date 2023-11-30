/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import Entity.DataTable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author osmel
 */
public class DataTableTest {
    
    public DataTableTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testConstructorAndGetters() {
        Object[][] testData = {{1, "Alice"}, {2, "Bob"}};
        Object[] testColumnNames = {"ID", "Name"};

        DataTable dataTable = new DataTable(testData, testColumnNames);

        assertArrayEquals(testData, dataTable.getData());
        assertArrayEquals(testColumnNames, dataTable.getColumnNames());
    }
}
