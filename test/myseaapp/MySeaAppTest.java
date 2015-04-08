/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package myseaapp;

import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fgourdea
 */
public class MySeaAppTest {
    
    public MySeaAppTest() {
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


    /**
     * Test of parseDocument method, of class MySeaApp.
     */
    @Test
    public void testParseDocument() {
        System.out.println("parseDocument");
        String fileContent = "blabla<video>C://video</video> blablabla<photo>D://photo</photo>";
        MySeaApp instance = new MySeaApp();
        instance.parseDocument(fileContent);
        assertEquals("blabla", instance.textes.get(0));
        assertEquals("C://video",instance.nomPhotoOuVideos.get(0));
        assertEquals("<video>", instance.typeTags.get(0));
        assertEquals(" blablabla", instance.textes.get(1));
        assertEquals("D://photo",instance.nomPhotoOuVideos.get(1));
        assertEquals("<photo>", instance.typeTags.get(1));
        assertEquals(2, instance.textes.size());
        assertEquals(2, instance.nomPhotoOuVideos.size());
        assertEquals(2, instance.typeTags.size());
    }

}
