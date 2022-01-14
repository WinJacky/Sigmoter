package main.java.util;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import main.java.dataType.EnhancedTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author feisher
 * @version 1.0
 * @date 2022/1/14 16:31
 */
public class ParseTest {
    private static Logger logger = LoggerFactory.getLogger(ParseTest.class);

    private static EnhancedTestCase tc;
    public static String folder;

    public ParseTest(String folder) {
        ParseTest.folder = folder;
    }

    public EnhancedTestCase parseAndSerialize(String pathToTestCase) {
        CompilationUnit cu = null;

        try {
            cu = JavaParser.parse(new File(pathToTestCase));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        new MethodVisitor().visit(cu, pathToTestCase);
        UtilsParser.serializeTestCase(tc, pathToTestCase, folder);

        return tc;
    }


    private static class MethodVisitor extends VoidVisitorAdapter<Object> {
        @Override
        public void visit(MethodDeclaration m, Object arg) {
            //TODO
            super.visit(m, arg);
        }
    }

}
