package be.guldentops.geert.lox.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAbstractSyntaxTree {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(1);
        }

        String outputDir = args[0];
        defineAbstractSyntaxTree(outputDir, "Expression", Arrays.asList(
                "Binary   : Expression left, Token operator, Expression right",
                "Grouping : Expression expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expression right"
        ));
    }

    private static void defineAbstractSyntaxTree(String outputDir,
                                                 String baseClassName,
                                                 List<String> types) {
        String path = outputDir + "/" + baseClassName + ".java";

        try (PrintWriter writer = new PrintWriter(path, "UTF-8")) {
            writeBaseClassHeader(baseClassName, writer);
            writeBaseClassBody(baseClassName, types, writer);
            writeBaseClassFooter(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeBaseClassHeader(String baseClassName, PrintWriter writer) {
        writer.println("package be.guldentops.geert.lox.grammar;");
        writer.println("");
        writer.println("import java.util.List;");
        writer.println("import be.guldentops.geert.lox.lexer.Token;");
        writer.println("");
        writer.printf("public abstract class %s {", baseClassName);
        writer.println();
    }

    private static void writeBaseClassBody(String baseClassName, List<String> types, PrintWriter writer) {
        writeAbstractAcceptMethod(writer);
        writeVisitorInterface(writer, baseClassName, types);
        writeInnerClasses(baseClassName, types, writer);
    }

    private static void writeAbstractAcceptMethod(PrintWriter writer) {
        writer.println("");
        writer.println("  abstract <R> R accept(Visitor<R> visitor);");
    }

    private static void writeVisitorInterface(PrintWriter writer, String baseClassName, List<String> types) {
        writer.println();
        writer.println(" public interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.printf("    R visit%s%s (%s  %s);", typeName, baseClassName, typeName, baseClassName.toLowerCase());
            writer.println();
        }

        writer.println("  }");
    }

    private static void writeInnerClasses(String baseClassName, List<String> types, PrintWriter writer) {
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseClassName, className, fields);
        }
    }

    private static void defineType(PrintWriter writer,
                                   String baseClassName,
                                   String className,
                                   String fieldList) {
        String[] fields = fieldList.split(", ");

        writeClassHeader(writer, baseClassName, className);
        writeFields(writer, fields);
        writeConstructor(writer, className, fieldList, fields);
        writeAcceptMethodImplementation(writer, baseClassName, className);
        writeClassFooter(writer);
    }

    private static void writeClassHeader(PrintWriter writer, String baseClassName, String className) {
        writer.println();
        writer.printf("public static class %s extends %s {", className, baseClassName);
        writer.println();
    }

    private static void writeFields(PrintWriter writer, String[] fieldsArray) {
        writer.println();

        for (String field : fieldsArray) {
            writer.printf("    public final %s;", field);
            writer.println();
        }
    }

    private static void writeConstructor(PrintWriter writer,
                                         String className,
                                         String fieldList,
                                         String[] fields) {
        writer.println();
        writer.printf("    public %s(%s) {", className, fieldList);
        writer.println();

        for (String field : fields) {
            String name = field.split(" ")[1];
            writer.printf("      this.%s = %s;", name, name);
            writer.println();
        }

        writer.println("    }");
    }

    private static void writeAcceptMethodImplementation(PrintWriter writer, String baseClassName, String className) {
        writer.println();
        writer.println("    <R> R accept(Visitor<R> visitor) {");
        writer.printf("      return visitor.visit%s%s(this);", className, baseClassName);
        writer.println();
        writer.println("    }");
    }

    private static void writeClassFooter(PrintWriter writer) {
        writer.println("  }");
    }

    private static void writeBaseClassFooter(PrintWriter writer) {
        writer.println("}");
    }
}