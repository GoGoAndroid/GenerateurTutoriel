/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GenerateurTutorielTest;

import GenerateurTutoriel.GenerateurTutoriel;
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
public class GenerateurTutorielTest {

    public GenerateurTutorielTest() {
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

        String[] fileContents = new String[]{
            "blabla<video>C://video</video> blublu<photo>D://photo</photo>",
            "blibli<photo>C://photo</photo> bloblo<video>D://video</video>",
            "",
            "blibli<photo>C://photo</photo> blablablabla",
            "bla"
        };

        String[][] textes = new String[][]{
            new String[]{"blabla", " blublu"},
            new String[]{"blibli", " bloblo"},
            new String[]{},
            new String[]{"blibli", " blablablabla"},
            new String[]{"bla"}
        };

        String[][] nomPhotoOuVideos = new String[][]{
            new String[]{"C://video", "D://photo"},
            new String[]{"C://photo", "D://video"},
            new String[]{},
            new String[]{"C://photo", ""},
            new String[]{""}
        };

        String[][] typeTags = new String[][]{
            new String[]{"<video>", "<photo>"},
            new String[]{"<photo>", "<video>"},
            new String[]{},
            new String[]{"<photo>", ""},
            new String[]{""}
        };

        String[][] resteDocuments = new String[][]{
            new String[]{" blublu<photo>D://photo</photo>", ""},
            new String[]{" bloblo<video>D://video</video>", ""},
            new String[]{},
            new String[]{" blablablabla", ""},
            new String[]{""}
        };

        int i = -1;
        for (String fileContent : fileContents) {
            i++;
            GenerateurTutoriel instance = new GenerateurTutoriel();
            instance.parseDocument(fileContent);

            for (int j = 0; j < textes[i].length; j++) {
                System.out.println("i : " + i);
                System.out.println("j : " + j);
                assertEquals(textes[i][j], instance.textes.get(j));
                System.out.println(textes[i][j]);
                assertEquals(nomPhotoOuVideos[i][j], instance.nomPhotoOuVideos.get(j));
                System.out.println(nomPhotoOuVideos[i][j]);
                assertEquals(typeTags[i][j], instance.typeTags.get(j));
                System.out.println(typeTags[i][j]);
                assertEquals(resteDocuments[i][j], instance.resteDocuments.get(j));
                System.out.println(resteDocuments[i][j]);
            }
        }
    }
    
    @Test
    public void testSendPost() throws Exception {
        System.out.println("sendPost");
        GenerateurTutoriel instance = new GenerateurTutoriel();
        String expResult = "ok";
        String result = instance.sendPost("blablabla","C://video","<video>");
        assertEquals(expResult, result);
    }    
}
